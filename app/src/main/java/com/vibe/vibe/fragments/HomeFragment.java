package com.vibe.vibe.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.DiscoverAdapter;
import com.vibe.vibe.adapters.RandomPlaylistsAdapter;
import com.vibe.vibe.adapters.RecentlySongsAdapter;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Topic;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.ArtistModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.TopicModel;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.utils.GreetingUtil;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private ScrollView fragmentHome;
    private ConstraintLayout constraintLayout;
    private TextView tvHello, tvName, tvAlbum, textViewSubtitle, tvDiscover, tvRecentSongs, tvRandomPlaylists;
    private ImageView ivNotification, ivSettings, ivAlbum, ivLike, ivPlay;
    private ShapeableImageView ivProfileArtist;
    private RecyclerView rvDiscover, rvRecentSongs, rvRandomPlaylists;
    private DiscoverAdapter discoverAdapter;
    private RecentlySongsAdapter recentlySongsAdapter;
    private RandomPlaylistsAdapter randomPlaylistsAdapter;
    private final AlbumModel albumModel = new AlbumModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();
    ArrayList<Album> albums = new ArrayList<>();
    ArrayList<Album> album = new ArrayList<>();
    private ArtistModel artistModel = ArtistModel.getInstance();
    private final TopicModel topicModel = new TopicModel();
    private ProgressBar progressBar;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        fragmentHome = new ScrollView(getContext());
        constraintLayout = new ConstraintLayout(getContext());
        fragmentHome.addView(constraintLayout);
        RecyclerView.LayoutManager layoutManagerDiscover = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvDiscover.setLayoutManager(layoutManagerDiscover);
        discoverAdapter = new DiscoverAdapter(getContext());
        rvDiscover.setAdapter(discoverAdapter);
        RecyclerView.LayoutManager layoutManagerRecentSongs = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvRandomPlaylists.setLayoutManager(layoutManagerRecentSongs);
        randomPlaylistsAdapter = new RandomPlaylistsAdapter(getContext());
        rvRandomPlaylists.setAdapter(randomPlaylistsAdapter);
        progressBar.setVisibility(View.VISIBLE);
        tvHello.setText(GreetingUtil.getGreeting());
        getAllAlbums();
        getAllPlaylists();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view) {
        fragmentHome = view.findViewById(R.id.fragmentHome);
        constraintLayout = view.findViewById(R.id.fragmentHomeLayout);
        tvHello = view.findViewById(R.id.tvHello);
        tvName = view.findViewById(R.id.tvName);
        tvDiscover = view.findViewById(R.id.tvDiscover);
        tvRecentSongs = view.findViewById(R.id.tvRecentSongs);
        tvRandomPlaylists = view.findViewById(R.id.tvRandomPlaylists);
        ivNotification = view.findViewById(R.id.ivNotification);
        ivSettings = view.findViewById(R.id.ivSettings);
        ivAlbum = view.findViewById(R.id.ivAlbum);
        ivLike = view.findViewById(R.id.ivLike);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivProfileArtist = view.findViewById(R.id.ivProfileArtist);
        rvDiscover = view.findViewById(R.id.rvDiscover);
        rvRecentSongs = view.findViewById(R.id.rvRecentSongs);
        rvRandomPlaylists = view.findViewById(R.id.rvRandomPlaylists);
        tvAlbum = view.findViewById(R.id.tvAlbum);
        textViewSubtitle = view.findViewById(R.id.textViewSubtitle);
        progressBar = view.findViewById(R.id.progressBarHome);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fragmentHome.startAnimation(fadeInAnimation);
    }


    private void getAllAlbums() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                albumModel.getAlbums(new AlbumModel.AlbumModelCallbacks() {
                    @Override
                    public void onCallback(ArrayList<Album> albumModels) {
                        discoverAdapter.setAlbums(albumModels);
                        albums.addAll(albumModels);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }, 500);
        if (!albums.isEmpty()) {
            discoverAdapter.setAlbums(albums);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getAllPlaylists() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                playlistModel.getPlaylists(new PlaylistModel.playListCallbacks() {
//                    @Override
//                    public void onCallbackPlaylist(ArrayList<Object> playlists) {
//
//                    }
//
//                    @Override
//                    public void onCallbackAlbum(ArrayList<Album> albums) {
//                        Log.e(TAG, "onCallbackAlbum: " + albums.toString());
//                        randomPlaylistsAdapter.setPlaylists(albums);
//                        album.addAll(albums);
//                        progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onCallbackError(String error) {
//                        Log.e(TAG, "onCallbackError: " + error);
//                    }
//                });
//            }
                topicModel.getAllTopics(new TopicModel.TopicModelListener() {
                    @Override
                    public void onTopicsChanged(ArrayList<Topic> topics) {
                        Log.e(TAG, "onTopicsChanged: " + topics.toString());
                        randomPlaylistsAdapter.setPlaylists(topics.get(0).getAlbums());
                        album.addAll(topics.get(0).getAlbums());
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTopicsChangedError(String error) {
                        Log.e(TAG, "onTopicsChangedError: " + error);
                    }
                });
            }
        }, 500);
        if (!album.isEmpty()) {
            randomPlaylistsAdapter.setPlaylists(album);
            progressBar.setVisibility(View.GONE);
        }
    }
}