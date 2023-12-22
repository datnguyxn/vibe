package com.vibe.vibe.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.vibe.vibe.MainActivity;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.DiscoverAdapter;
import com.vibe.vibe.adapters.RandomPlaylistsAdapter;
import com.vibe.vibe.adapters.RecentlySongsAdapter;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.entities.Topic;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.ArtistModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.TopicModel;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.services.SongService;
import com.vibe.vibe.utils.GreetingUtil;
import com.vibe.vibe.utils.RandomValue;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private TextView tvHello, tvName, tvDetail, tvAlbum, textViewSubtitle, tvDiscover, tvRecentSongs, tvRandomPlaylists;
    private ImageView ivSettings, ivAlbum, ivPlay;
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
    ArrayList<Song> songs = new ArrayList<>();
    private ArtistModel artistModel = ArtistModel.getInstance();
    private final TopicModel topicModel = new TopicModel();
    private ProgressBar progressBar;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Song song;
    private String uid;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private BroadcastReceiver onReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaying = bundle.getBoolean(Application.IS_PLAYING);
                isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);

                int action = bundle.getInt(Application.ACTION_TYPE);
                Log.e(TAG, "onReceiveHomeFragment: receiver " + action + " " + isPlaying + " isShuffle: " + isShuffle + " isRepeat: " + isRepeat);
                handleActionReceiveForHome(action);
            }
        }
    };

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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(onReceiver, SongService.getIntentFilter());
        fragmentHome = new ScrollView(getContext());
        constraintLayout = new ConstraintLayout(getContext());
        fragmentHome.addView(constraintLayout);

        RecyclerView.LayoutManager layoutManagerDiscover = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvDiscover.setLayoutManager(layoutManagerDiscover);
        discoverAdapter = new DiscoverAdapter(getContext());
        rvDiscover.setAdapter(discoverAdapter);

        RecyclerView.LayoutManager layoutRandomPlaylist = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvRandomPlaylists.setLayoutManager(layoutRandomPlaylist);
        randomPlaylistsAdapter = new RandomPlaylistsAdapter(getContext());
        rvRandomPlaylists.setAdapter(randomPlaylistsAdapter);

        RecyclerView.LayoutManager  layoutManagerRecentlySongs = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvRecentSongs.setLayoutManager(layoutManagerRecentlySongs);
        recentlySongsAdapter = new RecentlySongsAdapter(getContext());
        rvRecentSongs.setAdapter(recentlySongsAdapter);

        progressBar.setVisibility(View.VISIBLE);
        tvHello.setVisibility(View.GONE);
        tvName.setVisibility(View.GONE);
        tvDiscover.setVisibility(View.GONE);
        tvRecentSongs.setVisibility(View.GONE);
        tvRandomPlaylists.setVisibility(View.GONE);
        ivSettings.setVisibility(View.GONE);
        ivAlbum.setVisibility(View.GONE);
        ivPlay.setVisibility(View.GONE);
        ivProfileArtist.setVisibility(View.GONE);
        tvAlbum.setVisibility(View.GONE);
        textViewSubtitle.setVisibility(View.GONE);
        tvDetail.setVisibility(View.GONE);
        tvHello.setText(GreetingUtil.getGreeting());
        getAllAlbums();
        getAllPlaylists();
        getAllRecentSongs();
        handlePlayMusicClick();
        handleOnClickSongOfRecently();
        handleClickRandomAlbum();
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
        tvDetail = view.findViewById(R.id.tvDetail);
        tvRecentSongs = view.findViewById(R.id.tvRecentSongs);
        tvRandomPlaylists = view.findViewById(R.id.tvRandomPlaylists);
        ivSettings = view.findViewById(R.id.ivSettings);
        ivAlbum = view.findViewById(R.id.ivAlbum);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivProfileArtist = view.findViewById(R.id.ivProfileArtist);
        tvAlbum = view.findViewById(R.id.tvAlbum);
        textViewSubtitle = view.findViewById(R.id.textViewSubtitle);
        rvDiscover = view.findViewById(R.id.rvDiscover);
        rvRecentSongs = view.findViewById(R.id.rvRecentSongs);
        rvRandomPlaylists = view.findViewById(R.id.rvRandomPlaylists);
        progressBar = view.findViewById(R.id.progressBarHome);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Application.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
        ivSettings.setOnClickListener(v -> {
            SettingFragment settingsFragment = new SettingFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, settingsFragment).addToBackStack("HomeFragment").commit();
        });
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
                        getAndRandomOneSongFromAlbum(albumModels);
//                        progressBar.setVisibility(View.GONE);
                        updateUI();
                    }
                });
            }
        }, 500);
        if (!albums.isEmpty()) {
            discoverAdapter.setAlbums(albums);
//            progressBar.setVisibility(View.GONE);
            updateUI();
        }
    }

    private void getAllPlaylists() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//            }
                topicModel.getAllTopics(new TopicModel.TopicModelListener() {
                    @Override
                    public void onTopicsChanged(ArrayList<Topic> topics) {
                        Log.e(TAG, "onTopicsChanged: " + topics.toString());
                        randomPlaylistsAdapter.setPlaylists(topics.get(0).getAlbums());
                        album.addAll(topics.get(0).getAlbums());
//                        progressBar.setVisibility(View.GONE);
                        updateUI();
//                        fragmentHome.setVisibility(View.VISIBLE);
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
//            progressBar.setVisibility(View.GONE);
            updateUI();
//            fragmentHome.setVisibility(View.VISIBLE);
        }
    }

    private void getAllRecentSongs() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playlistModel.getRecentlyPlaylistOfUser(uid, uid, new PlaylistModel.onGetRecentlyPlaylistListener() {
                    @Override
                    public void onGetRecentlyPlaylistSuccess(Album album) {
                        songs = album.getSongs();
                        Log.e(TAG, "onGetRecentlyPlaylistSuccess: " + songs.toString());
                        recentlySongsAdapter.setSongs(songs);
                        updateUI();
                    }

                    @Override
                    public void onGetRecentlyPlaylistFailure(String error) {
                        Log.e(TAG, "onGetRecentlyPlaylistFailure: " + error);
                    }
                });
            }
        }, 500);
        if (!songs.isEmpty()) {
            recentlySongsAdapter.setSongs(songs);
            updateUI();
        }
    }

    private void handleClickRandomAlbum() {
        randomPlaylistsAdapter.setOnItemClickListener(new RandomPlaylistsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album playlist) {
                Bundle bundle = new Bundle();
                albumModel.getAlbum(playlist.getId(), new AlbumModel.onGetAlbumListener() {
                    @Override
                    public void onAlbumFound(Album album) {
                        bundle.putSerializable("album", album);
                        PlaylistFragment playlistFragment = new PlaylistFragment();
                        playlistFragment.setArguments(bundle);
//                playlistFragment.setAlbum(playlist);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, playlistFragment).addToBackStack(null).commit();
                    }

                    @Override
                    public void onAlbumNotExist() {
                        Log.e(TAG, "onAlbumNotExist: ");
                        Snackbar.make(getView(), "Album not exist", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getAndRandomOneSongFromAlbum(ArrayList<Album> albums) {
        ArrayList<Album> randomValues = RandomValue.getRandomValues(albums, 1);
        Album album = randomValues.get(0);
        song = RandomValue.getRandomValuesSong(album.getSongs(), 1);
        Glide.with(getContext()).load(song.getImageResource()).into(ivProfileArtist);
        tvName.setText(song.getName());
        Glide.with(getContext()).load(album.getImage()).into(ivAlbum);
        ivAlbum.setOnClickListener(v -> {
            PlaylistFragment playlistFragment = new PlaylistFragment();
            playlistFragment.setAlbum(album);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, playlistFragment).addToBackStack(null).commit();
        });
        tvAlbum.setText(album.getName());
        textViewSubtitle.setText(album.getDescription());
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        tvHello.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.VISIBLE);
        tvDiscover.setVisibility(View.VISIBLE);
        tvRecentSongs.setVisibility(View.VISIBLE);
        tvRandomPlaylists.setVisibility(View.VISIBLE);
        ivSettings.setVisibility(View.VISIBLE);
        ivAlbum.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);
        ivProfileArtist.setVisibility(View.VISIBLE);
        tvAlbum.setVisibility(View.VISIBLE);
        textViewSubtitle.setVisibility(View.VISIBLE);
        tvDetail.setVisibility(View.VISIBLE);
    }

    private void handlePlayMusicClick() {
        ivPlay.setOnClickListener(v -> {
            isPlaying = true;
            Log.e(TAG, "handlePlayMusicClick: play music is clicked");
            Intent intent = new Intent(getContext(), SongService.class);
            Bundle bundle = new Bundle();
            Log.e(TAG, "handlePlayMusicClick: " + song.toString());
            ArrayList<Song> songs = new ArrayList<>();
            songs.add(song);
            bundle.putSerializable(Application.SONGS_ARG, songs);
            if (isPlaying) {
                intent.putExtra(Application.ACTION_TYPE, Action.ACTION_PLAY);
            } else {
                intent.putExtra(Application.ACTION_TYPE, Action.ACTION_PAUSE);
            }

            intent.putExtra(Application.SONG_INDEX, 0);
            intent.putExtra(Application.IS_PLAYING, isPlaying);
            intent.putExtras(bundle);
            getContext().startService(intent);
        });

    }

    private void handleActionReceiveForHome(int action) {
        if (action == Action.ACTION_PLAY) {
            Glide.with(getContext()).load(R.drawable.pause).into(ivPlay);
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
        } else if (action == Action.ACTION_PAUSE) {
            Glide.with(getContext()).load(R.drawable.property_1_play).into(ivPlay);
        }
    }

    private void handleOnClickSongOfRecently() {
        recentlySongsAdapter.setOnItemClickListener(new RecentlySongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                Log.e(TAG, "onItemClick: " + song.toString());
                Intent intent = new Intent(getContext(), SongService.class);
                intent.putExtra(Application.ACTION_TYPE, Action.ACTION_PLAY);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Application.SONGS_ARG, songs);
                bundle.putSerializable(Application.CURRENT_SONG, song);
                intent.putExtra(Application.SONG_INDEX, position);
                intent.putExtra(Application.IS_PLAYING, isPlaying);
                intent.putExtra(Application.IS_SHUFFLE, isShuffle);
                intent.putExtra(Application.IS_REPEAT, isRepeat);
                intent.putExtras(bundle);
                getContext().startService(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onReceiver);
    }
}