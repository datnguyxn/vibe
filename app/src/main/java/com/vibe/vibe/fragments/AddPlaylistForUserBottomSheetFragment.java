package com.vibe.vibe.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPlaylistForUserBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPlaylistForUserBottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "AddPlaylistForUserBottomSheetFragment";
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();
    private ImageView ivAddPlaylistImage;
    private TextInputEditText etPlaylistName;
    private MaterialButton btnCreatePlaylist;
    private String uid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPlaylistForUserBottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPlaylistForUserBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPlaylistForUserBottomSheetFragment newInstance(String param1, String param2) {
        AddPlaylistForUserBottomSheetFragment fragment = new AddPlaylistForUserBottomSheetFragment();
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
        Bundle bundle = getArguments();
        uid = bundle.getString(Application.SHARED_PREFERENCES_UUID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_playlist_for_user_bottom_sheet, container, false);
        init(view);
        handleCreatePlaylist();
        return view;
    }

    private void handleCreatePlaylist() {
        btnCreatePlaylist.setOnClickListener(v -> {
            String playlistName = etPlaylistName.getText().toString();
            HashMap<String, Object> data = new HashMap<>();
            String playlistId = UUID.randomUUID().toString();
            data.put("id", playlistId);
            data.put("name", playlistName);
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
                            String id = (String) config.get(i).get("id");
                            if (id.equals(playlistId)) {
                                config.remove(i);
                                break;
                            }
                        }
                        config.add(data);
                    }
                    userModel.addConfiguration(uid, Schema.PRIVATE_PLAYLISTS, config, new UserModel.OnAddConfigurationListener() {

                        @Override
                        public void onAddConfigurationSuccess() {
                            data.put("image", "");
                            data.put("description", "");
                            data.put("userId", uid);
                            data.put("createdDate", LocalTime.now().toString());
                            playlistModel.addPrivatePlaylistForUser(uid, playlistId, data, new PlaylistModel.OnAddPlaylistListener() {
                                @Override
                                public void onAddPlaylistSuccess() {
                                    Log.d(TAG, "onAddPlaylistSuccess: ");
                                    LibraryFragment libraryFragment = new LibraryFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, libraryFragment).addToBackStack(null).commit();
                                    dismiss();
                                }

                                @Override
                                public void onAddPlaylistFailed() {
                                    Log.e(TAG, "onAddPlaylistFailed: ");
                                    dismiss();
                                }
                            });
                        }

                        @Override
                        public void onAddConfigurationFailure(String error) {
                            Log.e(TAG, "onAddConfigurationFailure: " + error);
                            dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "onFailure: " + error);
                    dismiss();
                }
            });
        });
    }

    private void init(View view) {
        ivAddPlaylistImage = view.findViewById(R.id.ivAddPlaylistImage);
        etPlaylistName = view.findViewById(R.id.etPlaylistName);
        btnCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);

    }
}