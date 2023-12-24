package com.vibe.vibe.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.PlaylistSongAdapter;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.SongModel;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.services.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = SearchFragment.class.getSimpleName();
    private ImageView blur_image, playlist_image, imgBackPlaylistToHome;
    private TextView playlist_name, playlist_size;
    private RecyclerView rvplaylist_songs;
    private PlaylistSongAdapter playlistSongAdapter;
    private ProgressBar progressBarAlbum;
    private ImageButton ibDownload, share, moreOptions;
    private ImageView play_playlist;
    private ArrayList<Song> songs;
    private Album album;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<Song> songPlaylist;
    private Song currentSong;
    private int index = 0;
    private boolean isPlaylist = false;
    private boolean isSongPrivatePlaylist = false;
    private String uid;
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();
    private final SongModel songModel = new SongModel();
    private final AlbumModel albumModel = new AlbumModel();

    private BroadcastReceiver onReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                currentSong = (Song) bundle.getSerializable(Application.CURRENT_SONG);
                songPlaylist = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
                isPlaying = bundle.getBoolean(Application.IS_PLAYING);
                isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);

                int action = bundle.getInt(Application.ACTION_TYPE);
                Log.e(TAG, "onReceive: receiver " + action + " " + isPlaying + " isShuffle: " + isShuffle + " isRepeat: " + isRepeat);
                handleActionReceive(action);
            }
        }
    };


    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        updateStatusUI();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistFragment newInstance(String param1, String param2) {
        PlaylistFragment fragment = new PlaylistFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            album = (Album) getArguments().getSerializable("album");
            songs = album.getSongs();
            if (getArguments().getString("playlist").equals("") || album.getName().equals("Liked Songs")) {
                isPlaylist = false;
            } else {
                isPlaylist = true;
            }
            if (getArguments().getString("SFP").equals("")) {
                isSongPrivatePlaylist = false;
            } else {
                isSongPrivatePlaylist = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        init(view);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onReceiver, SongService.getIntentFilter());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvplaylist_songs.setLayoutManager(layoutManager);
        playlistSongAdapter = new PlaylistSongAdapter(getContext(), album.getId(), isSongPrivatePlaylist);
        rvplaylist_songs.setAdapter(playlistSongAdapter);
        playlistSongAdapter.setSongs(songs);
        playlist_name.setText(album.getName());
        playlist_size.setText(songs.size() + " songs");
        if (album.getName().equals("Liked Songs")) {
            share.setVisibility(View.GONE);
        }
        if (!isPlaylist) {
            moreOptions.setVisibility(View.GONE);
        } else {
            moreOptions.setVisibility(View.VISIBLE);
            moreOptions.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(getContext(), moreOptions);
                popupMenu.inflate(R.menu.playlist_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.editPlaylist) {
                        EditPlaylistBottomSheetFragment editPlaylistBottomSheetFragment = new EditPlaylistBottomSheetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("album", album);
                        bundle.putString("uid", uid);
                        editPlaylistBottomSheetFragment.setArguments(bundle);
                        editPlaylistBottomSheetFragment.show(getFragmentManager(), editPlaylistBottomSheetFragment.getTag());
                        return true;
                    } else if (menuItem.getItemId() == R.id.deletePlaylist) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Delete playlist");
                        builder.setMessage("Are you sure you want to delete this playlist?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                                    userModel.getConfiguration(uid, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
                                        @Override
                                        public void onCompleted(ArrayList<Map<String, Object>> config) {
                                            if (config == null) {
                                                config = new ArrayList<>();
                                            }

                                            if (config.size() == 0) {
                                                return;
                                            } else {
                                                for (int i = 0; i < config.size(); i++) {
                                                    if (config.get(i).get("id").equals(album.getId())) {
                                                        config.remove(i);
                                                        break;
                                                    }
                                                }
                                            }
                                           userModel.addConfiguration(uid, Schema.PRIVATE_PLAYLISTS, config, new UserModel.OnAddConfigurationListener() {
                                               @Override
                                               public void onAddConfigurationSuccess() {
                                                   playlistModel.removePlaylistForUser(uid, album.getId(), new PlaylistModel.OnRemovePlaylistListener() {
                                                       @Override
                                                       public void onRemovePlaylistSuccess() {
                                                           Snackbar.make(getView(), "Delete playlist successfully", Snackbar.LENGTH_SHORT).show();
                                                           LibraryFragment libraryFragment = new LibraryFragment();
                                                           getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, libraryFragment).commit();
                                                       }

                                                       @Override
                                                       public void onRemovePlaylistFailed() {
                                                           Snackbar.make(getView(), "Delete playlist failed", Snackbar.LENGTH_SHORT).show();
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
                                })
                                .setNegativeButton("No", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();
                        return true;
                    } else {
                        return false;
                    }
                });
                popupMenu.show();
            });
        }
        if (album.getImage().equals("")) {
            Glide.with(getContext()).load(R.drawable.default_playlist).into(playlist_image);
            Glide.with(getContext()).load(R.drawable.default_playlist).into(blur_image);
        } else {
            Glide.with(getContext()).load(album.getImage()).into(playlist_image);
            Glide.with(getContext()).load(album.getImage()).into(blur_image);
        }
        if (album.getName().equals("Liked Songs")) {
            Glide.with(getContext()).load(R.drawable.like_song).into(playlist_image);
            Glide.with(getContext()).load(R.drawable.like_song).into(blur_image);
        }
        handleClickSong();
        handleButtonBehavior();
        return view;
    }

    private void init(View view) {
        blur_image = view.findViewById(R.id.blur_image);
        playlist_image = view.findViewById(R.id.playlist_image);
        playlist_name = view.findViewById(R.id.playlistName);
        playlist_size = view.findViewById(R.id.playlist_size);
        rvplaylist_songs = view.findViewById(R.id.rvplaylist_songs);
        imgBackPlaylistToHome = view.findViewById(R.id.imgBackPlaylistToHome);
        play_playlist = view.findViewById(R.id.play_playlist);
        ibDownload = view.findViewById(R.id.ibDownload);
        share = view.findViewById(R.id.share);
        moreOptions = view.findViewById(R.id.moreOptions);
        progressBarAlbum = view.findViewById(R.id.progressBarAlbum);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Application.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, null);
        imgBackPlaylistToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }

        });
    }

    public void setAlbum(Album album1) {
        this.album = album1;
        if (album.getSongs().isEmpty()) {
            albumModel.getAlbum(album.getId(), new AlbumModel.onGetAlbumListener() {
                @Override
                public void onAlbumFound(Album album2) {
                    Log.e(TAG, "onAlbumFound: " + album2.toString());
                    album = album2;
                    songs = album2.getSongs();
                }

                @Override
                public void onAlbumNotExist() {
                    Log.e(TAG, "onAlbumNotExist: ");
                }
            });
        } else {
            this.album = album1;
            Log.e(TAG, "setAlbum11: " + album.getSongs().toString());
            songs = album.getSongs();
        }
    }

    private void handleClickSong() {
        playlistSongAdapter.setOnItemClickListener(new PlaylistSongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("songId", song.getId());
                data.put("songName", song.getName());
                userModel.getConfiguration(uid, Schema.RECENTLY_PLAYED, new UserModel.onGetConfigListener() {
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
                        userModel.addConfiguration(uid, Schema.RECENTLY_PLAYED, config, new UserModel.OnAddConfigurationListener() {
                            @Override
                            public void onAddConfigurationSuccess() {
                                playlistModel.addSongToRecentlyPlaylistOfUser(uid, uid, song, new PlaylistModel.onPlaylistAddListener() {
                                    @Override
                                    public void onPlaylistAddSuccess() {
                                        Log.d(TAG, "onPlaylistAddSuccess: ");
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
                Log.e(TAG, "onItemClick: " + song.toString());
                sendActionToService(Action.ACTION_PLAY, position);
            }
        });
    }

    private void handleActionReceive(int action) {
        switch (action) {
            case Action.ACTION_START:
            case Action.ACTION_PLAY_ALBUM:
            case Action.ACTION_PLAY:
            case Action.ACTION_RESUME:
            case Action.ACTION_PAUSE:
            case Action.ACTION_CLOSE:
                updateStatusUI();
                break;
        }
    }

    private void updateStatusUI() {
        if (isPlaying) {
            play_playlist.setImageResource(R.drawable.pause);
        } else {
            play_playlist.setImageResource(R.drawable.property_1_play);
        }
    }

    private void sendActionToService(int action, int position) {
        Intent intent = new Intent(getContext(), SongService.class);
        intent.putExtra(Application.ACTION_TYPE, action);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Application.SONGS_ARG, album.getSongs());
        intent.putExtra(Application.SONG_INDEX, position);
        intent.putExtra(Application.IS_PLAYING, isPlaying);
        intent.putExtra(Application.IS_SHUFFLE, isShuffle);
        intent.putExtra(Application.IS_REPEAT, isRepeat);
        intent.putExtras(bundle);
        getContext().startService(intent);
    }

    private void handleButtonBehavior() {
        play_playlist.setOnClickListener(v -> {
            isPlaying = true;
            Intent intent = new Intent(getContext(), SongService.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Application.SONGS_ARG, album.getSongs());
            if (isPlaying) {
                intent.putExtra(Application.ACTION_TYPE, Action.ACTION_PLAY_ALBUM);
            } else {
                intent.putExtra(Application.ACTION_TYPE, Action.ACTION_PAUSE);
            }
            intent.putExtra(Application.SONG_INDEX, 0);
            intent.putExtra(Application.IS_PLAYING, isPlaying);
            intent.putExtras(bundle);
            getContext().startService(intent);
            updateStatusUI();
        });

        ibDownload.setOnClickListener(v -> {
            Log.e(TAG, "handleButtonBehavior: download");
            for (Song song : album.getSongs()) {
                Log.e(TAG, "handleButtonBehavior: " + song.toString());
                songModel.downloadSong(song.getId(), song.getName(), new SongModel.OnSongDownloadListener() {
                    @Override
                    public void onSongDownloadSuccess() {
                        Log.e(TAG, "onSongDownloadSuccess: " + song.getName());
                        Snackbar.make(getView(), "Download " + song.getName() + " successfully", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSongDownloadFailed() {
                        Log.e(TAG, "onSongDownloadFailed: ");
                    }
                });
            }
        });
        share.setOnClickListener(v -> {
            GenerateQRCodeBottomSheetFragment generateQRCodeBottomSheetFragment = new GenerateQRCodeBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putString("isSong", "");
            bundle.putSerializable("album", album);
            if (!isPlaylist) {
                bundle.putString("isAlbum", "isAlbum");
                bundle.putString("isPlaylist", "");
            } else {
                bundle.putString("isAlbum", "");
                bundle.putString("isPlaylist", "isPlaylist");
            }
            generateQRCodeBottomSheetFragment.setArguments(bundle);
            generateQRCodeBottomSheetFragment.show(getFragmentManager(), generateQRCodeBottomSheetFragment.getTag());
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(onReceiver);
    }
}