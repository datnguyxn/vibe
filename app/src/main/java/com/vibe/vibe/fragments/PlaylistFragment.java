package com.vibe.vibe.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.PlaylistSongAdapter;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;

import java.util.ArrayList;

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
    private ImageButton play_playlist, ibDownload, share, moreOptions;
    private ArrayList<Song> songs;
    private Album album;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<Song> songPlaylist;
    private Song currentSong;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        init(view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvplaylist_songs.setLayoutManager(layoutManager);
        playlistSongAdapter = new PlaylistSongAdapter(getContext());
        rvplaylist_songs.setAdapter(playlistSongAdapter);
        playlistSongAdapter.setSongs(songs);
        playlist_name.setText(album.getName());
        playlist_size.setText(album.getSongs().size() + " songs");
        Glide.with(getContext()).load(album.getImage()).into(playlist_image);
        Glide.with(getContext()).load(album.getImage()).into(blur_image);
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
        ibDownload  = view.findViewById(R.id.ibDownload);
        share = view.findViewById(R.id.share);
        moreOptions = view.findViewById(R.id.moreOptions);
        imgBackPlaylistToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
            }

        });
    }

    public void setAlbum(Album album) {
        this.album = album;
        Log.e(TAG, "setAlbum: " + album.toString());
        songs = album.getSongs();
        Log.e(TAG, "setAlbum: " + songs.toString());
    }

    private void handleActionReceive(int action) {
        switch (action) {

        }
    }
}