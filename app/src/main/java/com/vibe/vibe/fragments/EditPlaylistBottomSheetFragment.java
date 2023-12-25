package com.vibe.vibe.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPlaylistBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPlaylistBottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "EditPlaylistBottomSheetFragment";
    private TextView tvCancel, tvSave, tvChangeImage;
    private ImageView imgPlaylistBottomSheet;
    private EditText edtPlaylistName, edtAddDescription;
    private MaterialButton btnAddDescription;
    private Album album;
    private final UserModel userModel = new UserModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private String uid;
    private String playlistName, playlistDescription;
    private boolean isShow = false;
    private String uri;

    private static final int REQUEST_CODE = 123;

    public EditPlaylistBottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditPlaylistBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditPlaylistBottomSheetFragment newInstance(String param1, String param2) {
        EditPlaylistBottomSheetFragment fragment = new EditPlaylistBottomSheetFragment();
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
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_playlist_bottom_sheet, container, false);
        init(view);
        handleClick(view);
        return view;
    }

    private void init(View view) {
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);
        tvChangeImage = view.findViewById(R.id.tvChangeImage);
        imgPlaylistBottomSheet = view.findViewById(R.id.imgPlaylistBottomSheet);
        edtPlaylistName = view.findViewById(R.id.edtPlaylistName);
        edtAddDescription = view.findViewById(R.id.edtAddDescription);
        btnAddDescription = view.findViewById(R.id.btnAddDescription);
        edtPlaylistName.setText(album.getName());
        edtAddDescription.setText(album.getDescription());
        if (album.getImage().equals("")) {
            Glide
                    .with(getActivity())
                    .load(R.drawable.default_playlist)
                    .into(imgPlaylistBottomSheet);
        } else {
            Glide
                    .with(getActivity())
                    .load(album.getImage())
                    .into(imgPlaylistBottomSheet);
        }
    }

    private void handleClick(View view) {
        tvChangeImage.setOnClickListener(v -> {
            openFileChooser();
        });
        btnAddDescription.setOnClickListener(v -> {
            isShow = !isShow;
            if (isShow) {
                edtAddDescription.setVisibility(View.VISIBLE);
                btnAddDescription.setVisibility(View.GONE);
            } else {
                edtAddDescription.setVisibility(View.GONE);
            }
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
        tvSave.setOnClickListener(v -> {
            prepareData();
            HashMap<String, Object> data = new HashMap<>();
            data.put("id", album.getId());
            data.put("name", album.getName());
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", playlistName);
            updatedData.put("description", playlistDescription);
            if (uri != null) {
                updatedData.put("image", uri);
            } else {
                updatedData.put("image", album.getImage());
            }


            userModel.getConfiguration(uid, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
                @Override
                public void onCompleted(ArrayList<Map<String, Object>> config) {
                    if (config == null) {
                        config = new ArrayList<>();
                    }
                    if (config.size() == 0) {
                        config.add(data);
                    } else {
                        for (int i = 0; i < config.size(); i++) {
                            String songId = (String) config.get(i).get("id");
                            if (songId.equals(album.getId())) {
                                config.remove(i);
                                break;
                            }
                        }
                        config.add(data);
                    }
                    userModel.addConfiguration(uid, Schema.PRIVATE_PLAYLISTS, config, new UserModel.OnAddConfigurationListener() {
                        @Override
                        public void onAddConfigurationSuccess() {
                            playlistModel.updatePlaylistForUser(uid, album.getId(), updatedData, new PlaylistModel.OnUpdatePlaylistListener() {
                                @Override
                                public void onUpdatePlaylistSuccess() {
                                    Log.d(TAG, "onUpdatePlaylistSuccess: ");
                                    PlaylistFragment playlistFragment = new PlaylistFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("album", album);
                                    bundle.putString("playlist", "This is a playlist");
                                    bundle.putString("SFP", album.getId());
                                    playlistFragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, playlistFragment).addToBackStack(null).commit();
                                    dismiss();

                                }

                                @Override
                                public void onUpdatePlaylistFailed() {
                                    Log.d(TAG, "onUpdatePlaylistFailed: ");
                                    dismiss();
                                }
                            });
                        }

                        @Override
                        public void onAddConfigurationFailure(String error) {
                            Log.d(TAG, "onAddConfigurationFailure: " + error);
                            dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.d(TAG, "onFailure: " + error);
                    dismiss();
                }
            });
        });
    }

    private void prepareData() {
        playlistName = edtPlaylistName.getText().toString();
        if (playlistName.isEmpty()) {
            edtPlaylistName.setError("Playlist name is required");
            return;
        }
        if (isShow) {
            playlistDescription = edtAddDescription.getText().toString();
        } else {
            playlistDescription = album.getDescription();
        }
    }

    private void openFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == getActivity().RESULT_OK && data != null) {
            Log.e(TAG, "onActivityResult2: " + data.getData().toString());
            Uri imageUri = data.getData();
            Log.e(TAG, "onActivityResult: " + imageUri.toString());
            uri = imageUri.toString();
            Glide
                    .with(getActivity())
                    .load(uri)
                    .into(imgPlaylistBottomSheet);
        }
    }


}