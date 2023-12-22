package com.vibe.vibe.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.SearchAdapter;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Artist;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.ArtistModel;
import com.vibe.vibe.services.SongService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SearchView searchView;
    private RecyclerView rvSearchResults;
    private ArtistModel artistModel = ArtistModel.getInstance();
    private final AlbumModel albumModel = new AlbumModel();
    private SearchAdapter searchAdapter;
    private ProgressBar progressBar;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.edtSearchLayout);
        rvSearchResults = view.findViewById(R.id.rcSearchResults);
        progressBar = view.findViewById(R.id.progressBar);
        searchAdapter = new SearchAdapter(getContext());
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvSearchResults.setAdapter(searchAdapter);
        initView();
        handleClick();
        return view;
    }

    private void initView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.clearResults();

//                Snackbar.make(getView(), query, Snackbar.LENGTH_SHORT).show();
//                search(query);
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // delay time to search after user stop typing 1s

                return true;
            }
        });
    }

    private void search(String query) {
        new Handler().postDelayed(() -> {
            artistModel.search(query, new ArtistModel.SearchArtistListener() {
                @Override
                public void onBeginSearch() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSearchComplete(ArrayList<Object> artists) {
                    if (artists != null) {
                        Log.e(TAG, "onSearchComplete: " + artists.size());
                        searchAdapter.addResults(artists);
                        searchAdapter.orderByType();
                    } else {
                        Log.e(TAG, "onSearchComplete: null");
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onArtistAdded(Object artist) {
                    Log.e(TAG, "onArtistAdded: " + artist.toString());
                }

                @Override
                public void onSearchError(String error) {
                    Snackbar.make(getView(), error, Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }, 500);

        new Handler().postDelayed(() -> {
            albumModel.search(query, new AlbumModel.AlbumSearchListener() {
                @Override
                public void onAlbumBeginSearch() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAlbumSearchComplete(ArrayList<Object> albums) {
                    if (albums != null) {
                        Log.e(TAG, "onAlbumSearchComplete: " + albums.size());
                        searchAdapter.addResults(albums);
                        searchAdapter.orderByType();
                    } else {
                        Log.e(TAG, "onAlbumSearchComplete: null");
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onSongSearchComplete(ArrayList<Object> songs) {
                    if (songs != null) {
                        Log.e(TAG, "onSongSearchComplete: " + songs.size());
                        searchAdapter.addResults(songs);
                        searchAdapter.orderByType();
                    } else {
                        Log.e(TAG, "onSongSearchComplete: null");
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onAlbumSearchError(String error) {
                    Snackbar.make(getView(), error, Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }, 500);
    }

    private void handleClick() {
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                Log.d(TAG, "onItemClick: " + song.toString());
                Intent intent = new Intent(getContext(), SongService.class);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                ArrayList<Song> songs = new ArrayList<>();
                songs.add(song);
                bundle.putSerializable(Application.SONGS_ARG, songs);
//        bundle.putBoolean(Application.IS_PLAYING, true);
                bundle.putSerializable(Application.CURRENT_SONG, song);

                intent.putExtra(Application.ACTION_TYPE, Action.ACTION_PLAY);
                intent.putExtras(bundle);
                getActivity().startService(intent);

                PlayerFragment playerFragment = new PlayerFragment();
                playerFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frameLayout, playerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onItemClick(Album album, int position) {
                Log.d(TAG, "onItemClick: " + album.toString());

                albumModel.getAlbum(album.getId(), new AlbumModel.onGetAlbumListener() {
                    @Override
                    public void onAlbumFound(Album album) {
                        Log.e(TAG, "onAlbumFound: " + album.toString());
                        PlaylistFragment playlistFragment = new PlaylistFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("album", album);
                        bundle.putString("playlist", "");
                        playlistFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, playlistFragment)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onAlbumNotExist() {
                        Snackbar.make(getView(), "Album not exist", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onItemClick(Artist artist, int position) {
                Log.d(TAG, "onItemClick: " + artist.toString());
                ArtistFragment artistFragment = new ArtistFragment();
                artistFragment.setArtist(artist);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, artistFragment).addToBackStack(null).commit();

            }
        });
    }
}