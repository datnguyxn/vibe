package com.vibe.vibe.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.SongModel;
import com.vibe.vibe.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreOptionBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreOptionBottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = MoreOptionBottomSheetFragment.class.getSimpleName();
    private ImageView ivMorePicture;
    private String[] playlistIds;
    private String[] playlistNames;
    private TextView tvMoreSong, tvMoreArtist, tvLikeArtist, tvAddToPlaylist, tvDownload, tvShare, tvClose, tvRemoveFromPlaylist;
    private final UserModel userModel = new UserModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final SongModel songModel = new SongModel();
    private String uid;
    private Song song;
    private String playlistId;
    private boolean isLiked = false;

    public MoreOptionBottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreOptionBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreOptionBottomSheetFragment newInstance(String param1, String param2) {
        MoreOptionBottomSheetFragment fragment = new MoreOptionBottomSheetFragment();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_option_bottom_sheet, container, false);
        init(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            song = (Song) bundle.getSerializable(Application.CURRENT_SONG);
            Log.d(TAG, "onCreateView: " + song.toString());
            checkSongAddedToFavorite();
            playlistId = bundle.getString("SFP");
            if (playlistId.equals("")) {
                tvRemoveFromPlaylist.setVisibility(View.GONE);
            }
            assert song != null;
            Glide.with(requireContext()).load(song.getImageResource()).into(ivMorePicture);
            tvMoreSong.setText(song.getName());
            tvMoreArtist.setText(song.getArtistName());
            if (isLiked) {
                tvLikeArtist.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unlike, 0, 0, 0);
            } else {
                tvLikeArtist.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
            }
        }
        handleClick();
        return view;
    }

    private void handleClick() {
        tvAddToPlaylist.setOnClickListener(v -> {
            userModel.getConfiguration(uid, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
                @Override
                public void onCompleted(ArrayList<Map<String, Object>> config) {
                    if (config == null || config.size() == 0) {
                        Toast.makeText(getActivity(), "You don't have any playlist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    playlistIds = new String[config.size()];
                    playlistNames = new String[config.size()];
                    for (int i = 0; i < config.size(); i++) {
                        String id = (String) config.get(i).get("id");
                        String name = (String) config.get(i).get("name");
                        playlistIds[i] = id;
                        playlistNames[i] = name;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Choose playlist");
                    builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String playlistId = playlistIds[i];
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("songId", song.getId());
                            data.put("songName", song.getName());
                            userModel.getConfiguration(uid, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
                                @Override
                                public void onCompleted(ArrayList<Map<String, Object>> config) {
                                    Log.d(TAG, "onCompleted: " + config.toString());
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
                                    userModel.addConfiguration(uid, Schema.FAVORITE_SONGS, config, new UserModel.OnAddConfigurationListener() {
                                        @Override
                                        public void onAddConfigurationSuccess() {
                                            Log.d(TAG, "onAddConfigurationSuccess: ");
                                            playlistModel.addSongToPrivatePlaylistFavorite(uid, playlistId, song, new PlaylistModel.onPlaylistAddListener() {
                                                @Override
                                                public void onPlaylistAddSuccess() {
                                                    Log.d(TAG, "onPlaylistAddSuccess: ");
                                                    Snackbar.make(getView(), "Add " + song.getName() + " to " + playlistNames[i] + " successfully", Snackbar.LENGTH_SHORT).show();
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
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


                @Override
                public void onFailure(String error) {
                    Log.d(TAG, "onFailure: " + error);
                }
            });
        });
        tvDownload.setOnClickListener(v -> {
            if (Environment.isExternalStorageManager()) {
                downloadSong();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                getActivity().startActivityForResult(intent, 1000);
            }
        });
        tvRemoveFromPlaylist.setOnClickListener(v -> {
            userModel.getConfiguration(uid, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
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
                    userModel.addConfiguration(uid, Schema.FAVORITE_SONGS, config, new UserModel.OnAddConfigurationListener() {
                        @Override
                        public void onAddConfigurationSuccess() {
                            playlistModel.removeSongToPrivatePlaylistFavorite(uid, playlistId, song, new PlaylistModel.onRemoveSongFromPlaylistListener() {
                                @Override
                                public void onRemoveSongFromPlaylistSuccess() {
                                    Snackbar.make(getView(), "Remove " + song.getName() + " from " + playlistId + " successfully", Snackbar.LENGTH_SHORT).show();
                                    Log.d(TAG, "onRemoveSongFromPlaylistSuccess: ");
                                }

                                @Override
                                public void onRemoveSongFromPlaylistFailure(String error) {
                                    Snackbar.make(getView(), "Remove " + song.getName() + " from " + playlistId + " failed", Snackbar.LENGTH_SHORT).show();
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
        });
    }

    private void downloadSong() {
        if (getActivity() != null) {
            songModel.downloadSong(song.getId(), song.getName(), new SongModel.OnSongDownloadListener() {
                @Override
                public void onSongDownloadSuccess() {
                    Snackbar.make(getView(), "Download " + song.getName() + " successfully", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onSongDownloadFailed() {
                    Toast.makeText(getActivity(), "Download failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        tvShare.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_TEXT, song.getLink());
//            startActivity(Intent.createChooser(intent, "Share"));
        });
    }

    private void checkSongAddedToFavorite() {
        userModel.getConfiguration(uid, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    isLiked = false;
                    tvLikeArtist.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                    return;
                } else {
                    for (Map<String, Object> map : config) {
                        if (Objects.equals(map.get(Schema.SONG_ID), song.getId())) {
                            isLiked = true;
                            tvLikeArtist.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unlike, 0, 0, 0);
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


    private void init(View view) {
        ivMorePicture = view.findViewById(R.id.ivMorePicture);
        tvMoreSong = view.findViewById(R.id.tvMoreSong);
        tvMoreArtist = view.findViewById(R.id.tvMoreArtist);
        tvLikeArtist = view.findViewById(R.id.tvLikeArtist);
        tvAddToPlaylist = view.findViewById(R.id.tvAddToPlaylist);
        tvRemoveFromPlaylist = view.findViewById(R.id.tvRemoveFromPlaylist);
        tvDownload = view.findViewById(R.id.tvDownload);
        tvShare = view.findViewById(R.id.tvShare);
        tvClose = view.findViewById(R.id.tvClose);
        tvClose.setOnClickListener(v -> dismiss());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Application.SHARED_PREFERENCES_USER, requireContext().MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
    }
}