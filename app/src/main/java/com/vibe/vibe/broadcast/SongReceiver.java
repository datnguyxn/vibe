package com.vibe.vibe.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.services.SongService;

import java.util.ArrayList;

public class SongReceiver extends BroadcastReceiver {
    private static final String TAG = SongReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(Application.ACTION_TYPE, 0);
        if (action != 0) {
            Intent serviceIntent = new Intent(context, SongService.class);
            serviceIntent.putExtra(Application.ACTION_TYPE, action);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Song song = (Song) bundle.getSerializable(Application.CURRENT_SONG);
                if (song != null) {
                    Log.e(TAG, "onReceive: receive current song : " + song.toString());
                    serviceIntent.putExtra(Application.CURRENT_SONG, song);
                }
                ArrayList<Song> songs = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
                if (songs != null) {
                    Log.e(TAG, "onReceive: receive songs: " + songs.toString());
                    serviceIntent.putExtra(Application.SONGS_ARG, songs);
                }
                int songIndex = bundle.getInt(Application.SONG_INDEX, 0);
                Log.e(TAG, "onReceive: receive song index: " + songIndex );
                serviceIntent.putExtra(Application.SONG_INDEX, songIndex);
            }
            Log.e(TAG, "onReceive: action received " + action);
//            send action to song service
            context.startService(serviceIntent);
        }
    }
}
