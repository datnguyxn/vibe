package com.vibe.vibe.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.vibe.vibe.MainApplication;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.SongModel;

import java.util.ArrayList;
import java.util.Random;

public class SongService extends Service {
    private final static String TAG = SongService.class.getSimpleName();
    private Song song;
    private ArrayList<Song> songs;
    private int index;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isNowPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private int seekTo = 0;
    private SongModel songModel = new SongModel();

    public SongService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: " + song.getName());
            }
        });
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            songs = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
            Log.e(TAG, "onStartCommand: handle song service: " + songs.get(0).toString());
            if (songs != null) {
                index = bundle.getInt(Application.SONG_INDEX);
                isNowPlaying = bundle.getBoolean(Application.IN_NOW_PLAYING);
                isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);
                seekTo = bundle.getInt(Application.SEEK_BAR_PROGRESS, 0);
                song = songs.get(index);
                Log.e(TAG, "onStartCommand: handle song index:  " + index + " " + song.toString() + "; isShuffle: " + isShuffle + " ; isRepeat: " + isRepeat + "; seek to: " + seekTo);
            }
        }

        int action = intent.getIntExtra("action", 0);
        if (action != 0) {
            Log.e(TAG, "onStartCommand: handle action: " + action);
            handleAction(action);
        }
        return START_STICKY;
    }

    private void handleAction(int action) {
        switch (action) {
            case Action.ACTION_RESUME:
                resume();
                break;
            case Action.ACTION_PLAY:
            case Action.ACTION_PLAY_ALBUM:
                play();
                break;
            case Action.ACTION_PAUSE:
                pause();
                break;
            case Action.ACTION_NEXT:
                next();
                break;
            case Action.ACTION_PREVIOUS:
                previous();
                break;
            case Action.ACTION_PLAY_BACK:
                playBack();
                break;
            case Action.ACTION_SHUFFLE:
                shuffle();
                break;
            case Action.ACTION_REPEAT:
                repeat();
                break;
            case Action.ACTION_SONG_LIKED:
                songLiked();
                break;
            case Action.ACTION_SEEK_TO:
                seekTo();
                break;
            case Action.ACTION_CLOSE:
                stopSelf();
                sendBroadcastToActivity(Action.ACTION_CLOSE);
                break;
        }
    }

    private void seekTo() {
        if (mediaPlayer != null) {
            Log.e(TAG, "seekTo: " + seekTo);
            int length = mediaPlayer.getCurrentPosition();
            long seekTo = this.seekTo * 1000L;
            Log.e(TAG, "seekTo: " + length);
            mediaPlayer.seekTo(seekTo, MediaPlayer.SEEK_CLOSEST);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    Log.e(TAG, "onSeekComplete: seek to : " + mediaPlayer.getCurrentPosition());
                    mediaPlayer.start();
                    isPlaying = true;
                    sendBroadcastToActivity(Action.ACTION_SEEK_TO);
                }
            });
        }
    }

    private void songLiked() {
        sendBroadcastToActivity(Action.ACTION_SONG_LIKED);
    }

    private void playBack() {
        sendBroadcastToActivity(Action.ACTION_PLAY_BACK);
    }

    private void shuffle() {
        sendBroadcastToActivity(Action.ACTION_SHUFFLE);
    }

    private void repeat() {
        sendBroadcastToActivity(Action.ACTION_REPEAT);
    }

    private void previous() {
        if (index > 0) {
            index--;
        } else {
            index = songs.size() - 1;
        }
        song = songs.get(index);
        play();
        sendBroadcastToActivity(Action.ACTION_PREVIOUS);
    }

    private void next() {
        seekTo = 0;
        if (isShuffle) {
            index = new Random().nextInt(songs.size());
        }

        if (!isRepeat) {
            if (index < songs.size() - 1) {
                index++;
            } else {
                index = 0;
            }
        }

        song = songs.get(index);
        Log.e(TAG, "next: handle next method: " + index + " " + songs.size() + " " + songs.get(index).toString());
        play();
        sendBroadcastToActivity(Action.ACTION_NEXT);
    }

    private void resume() {
        if (mediaPlayer != null) {
            Log.e(TAG, "resume: " + mediaPlayer.getCurrentPosition());
            int length = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
            isPlaying = true;
            sendNotification();
            sendBroadcastToActivity(Action.ACTION_RESUME);
        } else {
            Log.e(TAG, "resume: media player null");
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            sendBroadcastToActivity(Action.ACTION_PAUSE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: SongService destroyed");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void play() {
        try {
            mediaPlayer.reset();
            if (!song.getResource().isEmpty()) {
                songModel.find(song.getId(), new SongModel.OnSongFindListener() {
                    @Override
                    public void onSongFind(Uri songUri) {
                        try {
                            mediaPlayer.setDataSource(SongService.this, songUri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            isPlaying = true;
                            sendNotification();
                            sendBroadcastToActivity(Action.ACTION_PLAY);
                        } catch (Exception e) {
                            Log.e(TAG, "onSongFind: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onSongNotExist() {
                        Log.e(TAG, "onSongNotExist: ");
                        Toast.makeText(SongService.this, "Song not exist", Toast.LENGTH_SHORT).show();
//                        next();LENGTH_SHORT
                    }
                });
            } else {
                Toast.makeText(this, "Song not exist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "play: " + e.getMessage());
        }
    }

    private void sendBroadcastToActivity(int action) {
        Intent intent = new Intent();
        intent.setAction(Application.ACTION_TO_ACTIVITY);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Application.CURRENT_SONG, song);
        bundle.putSerializable(Application.SONGS_ARG, songs);
        bundle.putBoolean(Application.IS_PLAYING, isPlaying);
        bundle.putInt(Application.SONG_INDEX, index);
        bundle.putInt(Application.SEEK_BAR_PROGRESS, mediaPlayer.getCurrentPosition() / 1000);
        bundle.putBoolean(Application.IN_NOW_PLAYING, isNowPlaying);
        bundle.putInt(Application.ACTION_TYPE, action);
        bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
        bundle.putBoolean(Application.IS_REPEAT, isRepeat);

        intent.putExtras(bundle);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendNotification() {
        Intent intent = new Intent();
        Log.e(TAG, "sendNotification: " + song.toString() + index + isPlaying);
        intent.putExtra(Application.SONG_INDEX, index);
        intent.putExtra(Application.SONGS_ARG, songs);
        intent.putExtra(Application.IN_NOW_PLAYING, true);
        intent.putExtra(Application.IS_SHUFFLE, isShuffle);
        intent.putExtra(Application.IS_REPEAT, isRepeat);
        intent.putExtra(Application.SEEK_BAR_PROGRESS, seekTo);
        intent.putExtra(Application.CURRENT_SONG, song);
        intent.putExtra(Application.IS_PLAYING, isPlaying);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, TAG);
        mediaSessionCompat.setActive(true);
        MediaMetadata.Builder mediaMetadata = new MediaMetadata.Builder();
        mediaMetadata.putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.getDuration());
        mediaMetadata.putString(MediaMetadata.METADATA_KEY_TITLE, song.getName());
        mediaMetadata.putString(MediaMetadata.METADATA_KEY_ARTIST, song.getArtistName());
        Glide.with(this)
                .asBitmap()
                .load(song.getImageResource())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Log.e(TAG, "onResourceReady: " + resource);
                        Log.e(TAG, "onResourceReady: " + transition);
                        mediaMetadata.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.e(TAG, "onLoadCleared: " + placeholder);
                    }
                });
        mediaSessionCompat.setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata.build()));
        mediaSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
        mediaSessionCompat.setFlags(0);

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, MainApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.vibe_logo)
                .setSound(null)
                .setSubText("Vibe")
                .setContentIntent(pendingIntent)
                .setContentText(song.getName())
                .setContentTitle(song.getArtistName())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                );
        Glide.with(this)
                .asBitmap()
                .load(song.getImageResource())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notificationCompat.setLargeIcon(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.e(TAG, "onLoadCleared: " + placeholder);
                    }
                });
        if (isPlaying) {
            notificationCompat
                    .addAction(R.drawable.previous, "Previous", getPendingIntent(this, Action.ACTION_PREVIOUS))
                    .addAction(R.drawable.pause_no_color, "Pause", getPendingIntent(this, Action.ACTION_PAUSE))
                    .addAction(R.drawable.next, "Next", getPendingIntent(this, Action.ACTION_NEXT));
        } else {
            notificationCompat
                    .addAction(R.drawable.previous, "Previous", getPendingIntent(this, Action.ACTION_PREVIOUS))
                    .addAction(R.drawable.play_no_color, "Play", getPendingIntent(this, Action.ACTION_PLAY))
                    .addAction(R.drawable.next, "Next", getPendingIntent(this, Action.ACTION_NEXT));
        }
        notificationCompat.addAction(R.drawable.baseline_clear_24, "Close", getPendingIntent(this, Action.ACTION_CLOSE));

        Notification notification = notificationCompat.build();

//        startForeground(1, notification);
        startForeground(1, notification);
    }

        public static IntentFilter getIntentFilter () {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Application.ACTION_TO_ACTIVITY);
            return intentFilter;
        }

        private PendingIntent getPendingIntent (Context context,int action){
            Intent intent = new Intent(this, SongService.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Application.SONGS_ARG, songs);
            bundle.putInt(Application.SONG_INDEX, index);
            intent.putExtra("action", action);
            bundle.putSerializable(Application.CURRENT_SONG, song);
            intent.putExtras(bundle);
            return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
    }
