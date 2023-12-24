package com.vibe.vibe.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.vibe.vibe.adapters.ArtistAdapter;
import com.vibe.vibe.adapters.ArtistFavoriteAdapter;
import com.vibe.vibe.adapters.PlaylistAdapter;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Artist;
import com.vibe.vibe.entities.Playlist;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.R;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.ArtistModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "LibraryFragment";
    private ImageView addPlaylistForUser;
    private ShapeableImageView ivAvatarProfileInLibrary;
    private RecyclerView rcvPlaylist, rcvArtist;
    private TextView tvUrLibrary;
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final AlbumModel albumModel = new AlbumModel();
    private final UserModel userModel = new UserModel();
    private final ArtistModel artistModel = new ArtistModel();
    private PlaylistAdapter playlistAdapter;
    private ArtistFavoriteAdapter artistAdapter;
    private ProgressBar progressBarLibrary;
    private String uid;
    private String username;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
        View convertView = inflater.inflate(R.layout.fragment_library, container, false);
        init(convertView);
        rcvPlaylist.setVisibility(View.GONE);
        tvUrLibrary.setVisibility(View.GONE);
        rcvArtist.setVisibility(View.GONE);
        addPlaylistForUser.setVisibility(View.GONE);
        ivAvatarProfileInLibrary.setVisibility(View.GONE);
        progressBarLibrary.setVisibility(View.VISIBLE);
        initUiForUser();
        initPlaylist();
        initArtist();
        handleAddPlaylist();
        handleClickPlaylist();
        return convertView;
    }

    private void init(View convertView) {
        rcvPlaylist = convertView.findViewById(R.id.rcvPlaylist);
        tvUrLibrary = convertView.findViewById(R.id.tvUrLibrary);
        rcvArtist = convertView.findViewById(R.id.rcvArtist);
        ivAvatarProfileInLibrary = convertView.findViewById(R.id.ivAvatarProfileInLibrary);
        progressBarLibrary = convertView.findViewById(R.id.progressBarLibrary);
        addPlaylistForUser = convertView.findViewById(R.id.addPlaylistForUser);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Application.SHARED_PREFERENCES_USER, getContext().MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
        ivAvatarProfileInLibrary.setOnClickListener(v -> {
            SettingFragment settingFragment = new SettingFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, settingFragment).addToBackStack(null).commit();
        });
    }

    private void initUiForUser() {
        userModel.getUser(uid, new UserModel.onGetUserListener() {
            @Override
            public void onGetUserSuccess(User user) {
                Glide.with(getActivity()).load(user.getAvatar()).into(ivAvatarProfileInLibrary);
                username = user.getUsername();
                initUI();
            }

            @Override
            public void onGetUserFailure(String error) {
                Log.d(TAG, "onGetUserFailure: " + error);
                Snackbar.make(getView(), "Error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void initPlaylist() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvPlaylist.setLayoutManager(layoutManager);
        playlistAdapter = new PlaylistAdapter(getContext());
        rcvPlaylist.setAdapter(playlistAdapter);
        getLibrary();
    }

    private void getLibrary() {
        playlistModel.getPrivatePlaylist(uid, new PlaylistModel.OnGetPlaylistListener() {
            @Override
            public void onGetPlaylist(ArrayList<Playlist> playlists) {
                Log.d(TAG, "onGetPlaylist: " + playlists.toString());
                playlistAdapter.setPlaylistsForAdapter(playlists, username);
                initUI();
            }

            @Override
            public void onGetPlaylistFailed() {
                Log.d(TAG, "onGetPlaylistFailed: ");
            }
        });
    }

    private void handleAddPlaylist() {
        addPlaylistForUser.setOnClickListener(v -> {
            AddPlaylistForUserBottomSheetFragment addPlaylistForUserBottomSheetFragment = new AddPlaylistForUserBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Application.SHARED_PREFERENCES_UUID, uid);
            addPlaylistForUserBottomSheetFragment.setArguments(bundle);
            addPlaylistForUserBottomSheetFragment.show(getFragmentManager(), addPlaylistForUserBottomSheetFragment.getTag());
        });
    }

    private void handleClickPlaylist() {
        playlistAdapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Playlist playlist, int position) {
                playlistModel.getPlaylistOfUser(uid, playlist.getId(), new PlaylistModel.onGetAllPlaylistOfUserListener() {
                    @Override
                    public void onGetAllPlaylistSuccess(Album album) {
                        Log.d(TAG, "onGetAllPlaylistSuccess: " + album.toString());
                        PlaylistFragment playlistFragment = new PlaylistFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("album", album);
                        bundle.putString("playlist", "This is a playlist");
                        bundle.putString("SFP", album.getId());
                        playlistFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, playlistFragment).addToBackStack(null).commit();
                    }

                    @Override
                    public void onGetAllPlaylistFailed() {

                    }
                });
            }
        });
        artistAdapter.setOnItemClickListener(new ArtistFavoriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Artist artist, int position) {
                ArtistFragment artistFragment = new ArtistFragment();
                artistFragment.setArtist(artist);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, artistFragment).addToBackStack(null).commit();
            }
        });
    }
    
    private void initArtist() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvArtist.setLayoutManager(layoutManager);
        artistAdapter = new ArtistFavoriteAdapter(getContext());
        rcvArtist.setAdapter(artistAdapter);
        getArtist();
    }

    private void getArtist() {
        userModel.getAristOfUser(uid, new UserModel.OnGetArtistFavoriteListener() {
            @Override
            public void onCompleted(ArrayList<Artist> artists) {
                Log.d(TAG, "onCompleted: " + artists.toString());
                artistAdapter.setArtists(artists);
                initUI();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void initUI() {
        rcvPlaylist.setVisibility(View.VISIBLE);
        tvUrLibrary.setVisibility(View.VISIBLE);
        addPlaylistForUser.setVisibility(View.VISIBLE);
        rcvArtist.setVisibility(View.VISIBLE);
        ivAvatarProfileInLibrary.setVisibility(View.VISIBLE);
        progressBarLibrary.setVisibility(View.GONE);
    }
}