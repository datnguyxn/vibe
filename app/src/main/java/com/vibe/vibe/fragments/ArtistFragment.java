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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.ArtistAdapter;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Artist;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ArtistFragment";
    private ImageView artist_thumb, imgBackFromArtistToHome, ivLikeStatus;
    private TextView artistName, tvArtistAlbumsName;
    private RecyclerView rvArtistAlbums;
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();
    private ArtistAdapter artistAdapter;
    private Artist artist;
    private boolean isFavorite = false;
    private String uid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArtistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistFragment newInstance(String param1, String param2) {
        ArtistFragment fragment = new ArtistFragment();
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
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        init(view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvArtistAlbums.setLayoutManager(layoutManager);
        artistAdapter = new ArtistAdapter(getActivity());
        rvArtistAlbums.setAdapter(artistAdapter);
        Log.d(TAG, "setArtist: " + artist.toString());
        Log.d(TAG, "setArtist: " + artistName.toString());
        artistName.setText(artist.getName());
        Glide.with(getActivity()).load(artist.getThumbnail()).into(artist_thumb);
        getPlaylistOfArtist();
        handleClick();
        checkArtistAddedToFavorite();
        return view;
    }

    private void init(View view) {
        artist_thumb = (ImageView) view.findViewById(R.id.artist_thumb);
        artistName = (TextView) view.findViewById(R.id.artistNameForArtistFragment);
        rvArtistAlbums = (RecyclerView) view.findViewById(R.id.rvArtistAlbums);
        imgBackFromArtistToHome = (ImageView) view.findViewById(R.id.imgBackFromArtistToHome);
        ivLikeStatus = (ImageView) view.findViewById(R.id.ivLikeArtist);
        tvArtistAlbumsName = (TextView) view.findViewById(R.id.tvArtistAlbumsName);
        imgBackFromArtistToHome.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Application.SHARED_PREFERENCES_USER, getContext().MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
    }

    public void setArtist(Artist newArtist) {
        this.artist = newArtist;
    }

    private void getPlaylistOfArtist() {
        if (artist.getPlaylistId() != null) {
            playlistModel.getPlaylistOfArtist(artist.getPlaylistId(), new PlaylistModel.onGetPublicPlaylistListener() {
                @Override
                public void onGetPublicPlaylistSuccess(Album album) {
                    Log.e(TAG, "onGetPublicPlaylistSuccess: " + album.toString());
                    ArrayList<Album> albums = new ArrayList<>();
                    if (album.getName() == null) {
                        album.setName(album.getSongs().get(0).getName());
                        album.setImage(album.getSongs().get(0).getImageResource());
                    }
                    albums.add(album);
                    artistAdapter.setAlbums(albums);
                }

                @Override
                public void onGetPublicPlaylistFailed() {
                    Log.e(TAG, "onGetPublicPlaylistFailed: ");
                }
            });
        } else {
            tvArtistAlbumsName.setText("No albums");
        }
    }

    private void handleClick() {
        artistAdapter.setOnItemClickListener(new ArtistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album album, int position) {
                PlaylistFragment playlistFragment = new PlaylistFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("album", album);
                bundle.putString("playlist", "");
                playlistFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, playlistFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        ivLikeStatus.setOnClickListener(v -> {
            if (isFavorite) {
                ivLikeStatus.setImageResource(R.drawable.like);
                isFavorite = false;
                removeArtistFromFavorite(artist);
            } else {
                ivLikeStatus.setImageResource(R.drawable.unlike);
                isFavorite = true;
                addArtistToFavorite(artist);
            }
        });
    }

    private void addArtistToFavorite(Artist artist) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("artistId", artist.getId());
        data.put("artistName", artist.getName());
        data.put("image", artist.getThumbnail());
        data.put("playlistId", artist.getPlaylistId());
        userModel.getConfiguration(uid, Schema.FAVORITE_ARTISTS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }

                if (config.size() == 0) {
                    config.add(data);
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        String artistId = (String) config.get(i).get("artistId");
                        if (artistId.equals(artist.getId())) {
                            config.remove(i);
                            break;
                        }
                    }
                    config.add(data);
                }
                userModel.addConfiguration(uid, Schema.FAVORITE_ARTISTS, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        Log.e(TAG, "onAddConfigurationSuccess: config artist add " + artist.getName() + " update completed");
                        Snackbar.make(getView(), "Added Artist to favorite successfully", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.e(TAG, "onAddConfigurationFailure: error" + error);
                        Snackbar.make(getView(), "Added Artist to favorite failure", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "onFailure: error" + error);
                Snackbar.make(getView(), "Added Artist to favorite failure", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void removeArtistFromFavorite(Artist artist) {
        userModel.getConfiguration(uid, Schema.FAVORITE_ARTISTS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null || config.size() == 0) {
                    return;
                }

                for (int i = 0; i < config.size(); i++) {
                    String artistId = (String) config.get(i).get("artistId");
                    if (artistId.equals(artist.getId())) {
                        config.remove(i);
                        break;
                    }
                }
                userModel.addConfiguration(uid, Schema.FAVORITE_ARTISTS, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        Log.e(TAG, "onAddConfigurationSuccess: config artist remove " + artist.getName() + " update completed");
                        Snackbar.make(getView(), "Removed Artist from favorite successfully", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.e(TAG, "onAddConfigurationFailure: error" + error);
                        Snackbar.make(getView(), "Removed Artist from favorite failure", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "onFailure: error" + error);
                Snackbar.make(getView(), "Removed Artist from favorite failure", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void checkArtistAddedToFavorite() {
        userModel.getConfiguration(uid, Schema.FAVORITE_ARTISTS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    isFavorite = false;
                    ivLikeStatus.setImageResource(R.drawable.like);
                    return;
                } else {
                    for (Map<String, Object> map : config) {
                        if (Objects.equals(map.get("artistId"), artist.getId())) {
                            isFavorite = true;
                            ivLikeStatus.setImageResource(R.drawable.unlike);
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
}