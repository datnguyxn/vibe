package com.vibe.vibe.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.vibe.vibe.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OptionEditProfileBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionEditProfileBottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE = 123;
    private String uri;
    private String username;
    private static final String TAG = "OptionEditProfileBottomSheetFragment";

    public interface BottomSheetListener {
        void onDataReceived(String data);
        void onUsernameReceived(String username);
    }

    public BottomSheetListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ShapeableImageView ivAvatarProfile;
    private TextInputEditText etUserName;
    private TextView tvSave;
    public OptionEditProfileBottomSheetFragment() {
        // Required empty public constructor
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        mListener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OptionEditProfileBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptionEditProfileBottomSheetFragment newInstance(String param1, String param2) {
        OptionEditProfileBottomSheetFragment fragment = new OptionEditProfileBottomSheetFragment();
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
        View convertView = inflater.inflate(R.layout.fragment_option_edit_profile_bottom_sheet, container, false);

        ivAvatarProfile = convertView.findViewById(R.id.ivAvatarProfile);
        etUserName = convertView.findViewById(R.id.etUserName);
        tvSave = convertView.findViewById(R.id.tvSave);

        savedInstanceState = getArguments();
        Log.e(TAG, "onCreateView: " + savedInstanceState.getString("avatar"));
        etUserName.setText(savedInstanceState.getString("username"));
        Glide.with(getContext()).load(savedInstanceState.getString("avatar")).into(ivAvatarProfile);
        ivAvatarProfile.setOnClickListener(v -> openFileChooser());
        tvSave.setOnClickListener(v -> {
            etUserName.getText().toString().toLowerCase();
            username = etUserName.getText().toString();
            sendUsernameToParent(username);
        });
        return convertView;
    }

    private void openFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
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
            sendData();
        }
    }


    private void sendDataToParent(String data) {
        if (mListener != null) {
            mListener.onDataReceived(data);
            dismiss();
        }
    }

    private void sendUsernameToParent(String username) {
        if (mListener != null) {
            mListener.onUsernameReceived(username);
            dismiss();
        }
    }

    // Trigger this method when you want to send data
    private void sendData() {
        sendDataToParent(uri);
    }
}