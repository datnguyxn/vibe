package com.vibe.vibe.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.services.SongService;
import com.vibe.vibe.utils.MainActivityListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = PlayerFragment.class.getSimpleName();
    private MainActivityListener mainActivityListener;
    private ImageView hide, more, ivSongImage, ivLike, ivShuffle, ivPrevious, ivPlay, ivNext, ivRepeat;
    private TextView tvSongName, tvArtistName, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private Song song;
    private ArrayList<Song> songs;
    private boolean isPlaying = false;
    private boolean isLike = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private int totalTime = 0;
    private int stopAfterMinutes = 0;
    private int index = 0;
    private int seekTo = 0;
    private String id;
    private final UserModel userModel = new UserModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final Handler handler = new Handler();

    private BroadcastReceiver onReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int action = bundle.getInt(Application.ACTION_TYPE);
                isPlaying = bundle.getBoolean(Application.IS_PLAYING);
                song = (Song) bundle.getSerializable(Application.CURRENT_SONG);
                index = bundle.getInt(Application.SONG_INDEX);
                seekTo = bundle.getInt(Application.SEEK_BAR_PROGRESS, 0);
                Log.w(TAG, "onReceive4565: " + seekTo);
                isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);
                songs = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
                stopAfterMinutes = bundle.getInt(Application.STOP_AFTER_MINUTES, 0);
                Log.e(TAG, "onReciver: " + stopAfterMinutes);
                Log.e(TAG, "onReceive: handle receive: " + action + " isPlaying: " + isPlaying + "; isShuffle: " + isShuffle + "; isRepeat: " + isRepeat + "; seekTo: " + seekTo);
                handleActionReceive(action);
            }
        }
    };


    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            int action = getArguments().getInt(Application.ACTION_TYPE);
            isPlaying = bundle.getBoolean(Application.IS_PLAYING);
            song = (Song) bundle.getSerializable(Application.CURRENT_SONG);
            index = bundle.getInt(Application.SONG_INDEX);
            seekTo = bundle.getInt(Application.SEEK_BAR_PROGRESS, 0);
            Log.w(TAG, "onReceive4566: " + seekTo);
            isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
            isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);
            songs = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
            Log.e(TAG, "onReceive: handle receive: " + action + " isPlaying: " + isPlaying + "; isShuffle: " + isShuffle + "; isRepeat: " + isRepeat + "; seekTo: " + seekTo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        init(view);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onReceiver, SongService.getIntentFilter());
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcastToService(Action.ACTION_PLAY_BACK);
                getActivity().onBackPressed();
                showNavigationViews();
            }
        });

        more.setOnClickListener(v -> {
            Log.d(TAG, "onClick: more");
            MoreOptionBottomSheetFragment moreOptionBottomSheetFragment = new MoreOptionBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Application.CURRENT_SONG, song);
            bundle.putBoolean("Like", isLike);
            bundle.putString("SFP", "");
            bundle.putSerializable(Application.SONGS_ARG, songs);
            bundle.putSerializable(Application.CURRENT_SONG, song);
            bundle.putInt(Application.SONG_INDEX, index);
            bundle.putInt(Application.SEEK_BAR_PROGRESS, seekTo);
            bundle.putBoolean(Application.IN_NOW_PLAYING, true);
            bundle.putBoolean(Application.IS_PLAYING, isPlaying);
            bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
            bundle.putBoolean(Application.IS_REPEAT, isRepeat);
            moreOptionBottomSheetFragment.setArguments(bundle);
            moreOptionBottomSheetFragment.show(getChildFragmentManager(), moreOptionBottomSheetFragment.getTag());
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            song = (Song) bundle.getSerializable(Application.CURRENT_SONG);
            songs = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
            isPlaying = bundle.getBoolean(Application.IS_PLAYING, false);
            isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
            isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);
            index = bundle.getInt(Application.SONG_INDEX, 0);
            if (songs != null) {
                Log.e(TAG, "onCreateView: songs: " + songs.size());
            }
        }
        initUIForPlayer();
        updateStatusUIForPlayer();
        checkSongAddedToFavorite();
        handleActionsForPlayer();
        handleSeekbarChange();
        updateCurrentDuration();
        return view;
    }

    private void handleActionsForPlayer() {
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    sendBroadcastToService(Action.ACTION_PAUSE);
                } else {
                    sendBroadcastToService(Action.ACTION_RESUME);
                }
            }
        });
        ivLike.setOnClickListener(v -> {
            if (isLike) {
                isLike = false;
                ivLike.setImageResource(R.drawable.like);
                removeSongFromFavorite();
            } else {
                isLike = true;
                ivLike.setImageResource(R.drawable.unlike);
                addSongFromFavorite();
            }
        });
        ivShuffle.setOnClickListener(v -> {
            isShuffle = !isShuffle;
            updateStatusUIForPlayer();
            sendBroadcastToService(Action.ACTION_SHUFFLE);
        });
        ivPrevious.setOnClickListener(v -> {
            sendBroadcastToService(Action.ACTION_PREVIOUS);
        });
        ivNext.setOnClickListener(v -> {
            sendBroadcastToService(Action.ACTION_NEXT);
        });
        ivRepeat.setOnClickListener(v -> {
            isRepeat = !isRepeat;
            updateStatusUIForPlayer();
            sendBroadcastToService(Action.ACTION_REPEAT);
        });
    }

    private void addSongFromFavorite() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("songId", song.getId());
        data.put("songName", song.getName());
        userModel.getConfiguration(id, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }
                if (config.size() == 0) {
                    config.add(data);
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        String songId = (String) config.get(i).get("songId");
                        if (songId.equals(song.getId())) {
                            config.remove(i);
                            break;
                        }
                    }
                    config.add(data);
                }
                userModel.addConfiguration(id, Schema.FAVORITE_SONGS, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        playlistModel.addSongToPrivatePlaylistFavorite(id, id, song, new PlaylistModel.onPlaylistAddListener() {
                            @Override
                            public void onPlaylistAddSuccess() {
                                Log.e(TAG, "onPlaylistAddSuccess: ");
                                sendBroadcastToService(Action.ACTION_SONG_LIKED);
                            }

                            @Override
                            public void onPlaylistAddFailure() {
                                Log.d(TAG, "onPlaylistAddFailure: ");
                            }
                        });
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.d(TAG, "onAddConfigurationFailure: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }

    private void removeSongFromFavorite() {
        userModel.getConfiguration(id, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }

                if (config.size() == 0) {
                    return;
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        if (config.get(i).get("songId").equals(song.getId())) {
                            config.remove(i);
                            break;
                        }
                    }
                }
                userModel.addConfiguration(id, Schema.FAVORITE_SONGS, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        playlistModel.removeSongToPrivatePlaylistFavorite(id, id, song, new PlaylistModel.onRemoveSongFromPlaylistListener() {
                            @Override
                            public void onRemoveSongFromPlaylistSuccess() {
                                Log.e(TAG, "onRemoveSongFromPlaylistSuccess: ");
                                sendBroadcastToService(Action.ACTION_SONG_LIKED);
                            }

                            @Override
                            public void onRemoveSongFromPlaylistFailure(String error) {
                                Log.d(TAG, "onRemoveSongFromPlaylistFailure: " + error);
                            }
                        });
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.d(TAG, "onAddConfigurationFailure: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityListener) {
            mainActivityListener = (MainActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainActivityListener");
        }
    }


    private void showNavigationViews() {
        if (mainActivityListener != null) {
            mainActivityListener.showNavigationViews();
        }
    }

    public void hideNavigationViews() {
        if (mainActivityListener != null) {
            mainActivityListener.hideNavigationViews();
        }
    }

    private void init(View view) {
        hide = view.findViewById(R.id.hide);
        more = view.findViewById(R.id.more);
        ivSongImage = view.findViewById(R.id.ivSongImage);
        ivLike = view.findViewById(R.id.ivLike);
        ivShuffle = view.findViewById(R.id.ivShuffle);
        ivPrevious = view.findViewById(R.id.ivPrevious);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivNext = view.findViewById(R.id.ivNext);
        ivRepeat = view.findViewById(R.id.ivRepeat);
        tvSongName = view.findViewById(R.id.tvSongName);
        tvArtistName = view.findViewById(R.id.tvArtistName);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        seekBar = view.findViewById(R.id.seekBar);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Application.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        id = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
    }

    private void handleSeekbarChange() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo = seekBar.getProgress();
                sendBroadcastToService(Action.ACTION_SEEK_TO);
            }
        });
    }

    private void handleActionReceive(int action) {
        switch (action) {
            case Action.ACTION_START:
            case Action.ACTION_PLAY: {
                updateStatusUIForPlayer();
                updateSeekbarUI();
                updateCurrentDuration();
                break;
            }
            case Action.ACTION_RESUME:
            case Action.ACTION_SEEK_TO: {
                updateStatusUIForPlayer();
                updateSeekbarUI();
                removeUpdateCurrentDuration();
                updateCurrentDuration();
                break;
            }
            case Action.ACTION_PAUSE: {
                updateStatusUIForPlayer();
                updateSeekbarUI();
                break;
            }
            case Action.ACTION_NEXT:
            case Action.ACTION_PREVIOUS: {
                isLike = false;
                updateSeekbarUI();
                updateStatusUIForPlayer();
                initUIForPlayer();
                removeUpdateCurrentDuration();
                break;
            }
            case Action.ACTION_PLAY_BACK:
                isLike = false;
                Log.e(TAG, "updateSeekbarUI: " + seekTo);
                updateSeekbarUI();
                updateStatusUIForPlayer();
                initUIForPlayer();
                break;
            case Action.ACTION_SHUFFLE: {
                Log.e(TAG, "handleActionReceive: shuffle: " + isShuffle);
                updateStatusUIForPlayer();
                break;
            }
            case Action.ACTION_REPEAT: {
                Log.e(TAG, "handleActionReceive: repeat: " + isRepeat);
                updateStatusUIForPlayer();
                break;
            }
        }
    }

    private void initUIForPlayer() {
        Log.e(TAG, "initUIForPlayer: " + song.getName());
        Glide.with(requireContext()).load(song.getImageResource()).into(ivSongImage);
        tvSongName.setText(song.getName());
        tvArtistName.setText(song.getArtistName());
        tvTotalTime.setText(durationToString(song.getDuration()));
        seekBar.setProgress(0);
        seekBar.setMax(song.getDuration());

        if (isPlaying) {
            ivPlay.setImageResource(R.drawable.pause);
        } else {
            ivPlay.setImageResource(R.drawable.property_1_play);
        }
    }

    private Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                seekTo = seekTo + 1;
                seekBar.setProgress(seekTo);
                tvCurrentTime.setText(durationToString(seekTo));
                if (seekTo == song.getDuration()) {
                    if (isRepeat) {
                        seekTo = 0;
                        sendBroadcastToService(Action.ACTION_SEEK_TO);
                    } else {
                        sendBroadcastToService(Action.ACTION_NEXT);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }
    };


    private void updateCurrentDuration() {
        handler.postDelayed(updateSeekbar, 1000);
    }

    private void removeUpdateCurrentDuration() {
        handler.removeCallbacks(updateSeekbar);
    }

    private String durationToString(int duration) {
        int minutes = duration / 60;
        int seconds = duration % 60;
        if (seconds < 10)
            return minutes + ":0" + seconds;
        else if (seconds == 0)
            return minutes + ":00";
        else if (seconds > 10)
            return minutes + ":" + seconds;
        else if (seconds == 60)
            return (minutes + 1) + ":00";
        else
            return minutes + ":" + seconds;
    }

    private void sendBroadcastToService(int action) {
        Intent intent = new Intent(getActivity(), SongService.class);
        intent.putExtra(Application.ACTION_TYPE, action);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Application.SONGS_ARG, songs);
        bundle.putSerializable(Application.CURRENT_SONG, song);
        bundle.putInt(Application.SONG_INDEX, index);
        bundle.putInt(Application.SEEK_BAR_PROGRESS, seekTo);
        bundle.putBoolean(Application.IN_NOW_PLAYING, true);
        bundle.putBoolean(Application.IS_PLAYING, isPlaying);
        bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
        bundle.putBoolean(Application.IS_REPEAT, isRepeat);
        intent.putExtras(bundle);
        getActivity().startService(intent);
    }

    private void updateSeekbarUI() {
        seekBar.setProgress(seekTo);
        tvCurrentTime.setText(durationToString(seekTo));
    }

    private void updateStatusUIForPlayer() {
        if (isPlaying) {
            ivPlay.setImageResource(R.drawable.pause);
        } else {
            ivPlay.setImageResource(R.drawable.property_1_play);
        }

        checkSongAddedToFavorite();
        if (isLike) {
            ivLike.setImageResource(R.drawable.unlike);
        } else {
            ivLike.setImageResource(R.drawable.like);
        }

        if (isShuffle) {
            ivShuffle.setImageResource(R.drawable.shuffle_on);
        } else {
            ivShuffle.setImageResource(R.drawable.shuffle_off);
        }

        if (isRepeat) {
            ivRepeat.setImageResource(R.drawable.replay_all);
        } else {
            ivRepeat.setImageResource(R.drawable.repeat_off);
        }

        seekBar.setMax(song.getDuration());
    }

    private void checkSongAddedToFavorite() {
        userModel.getConfiguration(id, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    isLike = false;
                    ivLike.setImageResource(R.drawable.like);
                    return;
                } else {
                    for (Map<String, Object> map : config) {
                        if (Objects.equals(map.get(Schema.SONG_ID), song.getId())) {
                            isLike = true;
                            ivLike.setImageResource(R.drawable.unlike);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(onReceiver);
    }
}