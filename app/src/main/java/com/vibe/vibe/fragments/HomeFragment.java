package com.vibe.vibe.fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.DiscoverAdapter;
import com.vibe.vibe.adapters.RandomPlaylistsAdapter;
import com.vibe.vibe.adapters.RecentlySongsAdapter;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.ArtistModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;

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
    private TextView tvHello, tvName, tvAlbum, textViewSubtitle;
    private ImageView ivNotification, ivSettings, ivAlbum, ivLike, ivPlay;
    private ShapeableImageView ivProfileArtist;
    private RecyclerView rvDiscover, rvRecentSongs, rvRandomPlaylists;
    private DiscoverAdapter discoverAdapter;
    private RecentlySongsAdapter recentlySongsAdapter;
    private RandomPlaylistsAdapter randomPlaylistsAdapter;
    private final AlbumModel albumModel = new AlbumModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();
    private  ArtistModel artistModel = ArtistModel.getInstance();

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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        fragmentHome = new ScrollView(getContext());
        constraintLayout = new ConstraintLayout(getContext());
        fragmentHome.addView(constraintLayout);

        RecyclerView.LayoutManager layoutManagerDiscover = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvDiscover.setLayoutManager(layoutManagerDiscover);
        discoverAdapter = new DiscoverAdapter(getContext());
        rvDiscover.setAdapter(discoverAdapter);
        getAllAlbums();
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
    }


    private void getAllAlbums() {
        albumModel.getAlbums(new AlbumModel.AlbumModelCallbacks() {
            @Override
            public void onCallback(ArrayList<Album> albumModels) {
                discoverAdapter.setAlbums(albumModels);
            }
        });
    }
}