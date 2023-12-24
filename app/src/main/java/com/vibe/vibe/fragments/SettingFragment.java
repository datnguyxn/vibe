package com.vibe.vibe.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.models.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements OptionEditProfileBottomSheetFragment.BottomSheetListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "SettingFragment";
    private FirebaseAuth mAuth;
    private ImageView imgBackToHome;
    private ShapeableImageView ivAvatarProfile;
    private TextView tvProfileName, tvProfileInfo, tvEditProfile, tvUploadSongs;
    private final UserModel userModel = new UserModel();
    private String id;
    private String avatar;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View convertView = inflater.inflate(R.layout.fragment_setting, container, false);
        init(convertView);
        setProfileInfo();
        imgBackToHome.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        // TODO: Edit profile
        tvEditProfile.setOnClickListener(v -> {
            OptionEditProfileBottomSheetFragment optionEditProfileBottomSheetFragment = new OptionEditProfileBottomSheetFragment();
            optionEditProfileBottomSheetFragment.setBottomSheetListener(this);
            Bundle bundle = new Bundle();
            bundle.putString("avatar", avatar);
            bundle.putString("username", tvProfileName.getText().toString());
            optionEditProfileBottomSheetFragment.setArguments(bundle);
            optionEditProfileBottomSheetFragment.show(getChildFragmentManager(), optionEditProfileBottomSheetFragment.getTag());
        });
        return convertView;
    }

    private void init(View convertView) {
        mAuth = FirebaseAuth.getInstance();
        imgBackToHome = convertView.findViewById(R.id.imgBackToHome);
        ivAvatarProfile = convertView.findViewById(R.id.ivAvatarProfile);
        tvProfileName = convertView.findViewById(R.id.tvProfileName);
        tvProfileInfo = convertView.findViewById(R.id.tvProfileInfo);
        tvEditProfile = convertView.findViewById(R.id.tvEditProfile);
        tvUploadSongs = convertView.findViewById(R.id.tvUploadSong);
        tvUploadSongs.setOnClickListener(v -> {
            UploadSongFragment uploadSongFragment = new UploadSongFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, uploadSongFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setProfileInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Application.SHARED_PREFERENCES_USER, getActivity().MODE_PRIVATE);
        id = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
        userModel.getUser(id, new UserModel.onGetUserListener() {

            @Override
            public void onGetUserSuccess(User user) {
                Glide
                        .with(getActivity())
                        .load(user.getAvatar())
                        .into(ivAvatarProfile);
                tvProfileName.setText(user.getUsername());
                tvProfileInfo.setText(user.getPhoneNumber() + " ・ " + user.getEmail());
                avatar = user.getAvatar();
            }

            @Override
            public void onGetUserFailure(String error) {
                Log.e(TAG, "onGetUserFailure: " + error);
            }
        });
    }

    @Override
    public void onDataReceived(String data) {
        Log.e(TAG, "onDataReceived: " + data);
        Glide
                .with(getActivity())
                .load(data)
                .into(ivAvatarProfile);
        userModel.updateAvatar(id, data);
        avatar = data;
    }

    @Override
    public void onUsernameReceived(String username) {
        tvProfileName.setText(username);
        userModel.updateUsername(id, username);
    }
}