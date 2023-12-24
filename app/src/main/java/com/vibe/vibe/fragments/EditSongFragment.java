package com.vibe.vibe.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vibe.vibe.R;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditSongFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = EditSongFragment.class.getSimpleName();
    private ImageView ivSongUpload;
    private TextView tvSongUpload, tvSongSize;
    private TextInputEditText etSongName, etArtistName;
    private MaterialButton btnEditSong;
    private File file, newFile;
    private String fileName;
    private long fileSize;
    private long fileDuration;
    private String fileImage;
    private String filePath;
    private static final int REQUEST_CODE_GET_IMAGE = 2;
    private String nameSong, nameArtist;
    public EditSongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditSongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditSongFragment newInstance(String param1, String param2) {
        EditSongFragment fragment = new EditSongFragment();
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
            Bundle bundle = getArguments();
            file = bundle.getSerializable(com.vibe.vibe.constants.File.FILE) != null ? (File) bundle.getSerializable(com.vibe.vibe.constants.File.FILE) : null;
            fileName = bundle.getString(com.vibe.vibe.constants.File.FILE_NAME);
            fileSize = bundle.getLong(com.vibe.vibe.constants.File.FILE_SIZE);
            filePath = bundle.getString(com.vibe.vibe.constants.File.FILE_PATH);
            fileDuration = bundle.getLong(com.vibe.vibe.constants.File.FILE_DURATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_song, container, false);
        init(view);
        handleClick();
        return view;
    }

    private void init(View view) {
        ivSongUpload = view.findViewById(R.id.ivSongUpload);
        tvSongUpload = view.findViewById(R.id.tvSongUpload);
        tvSongSize = view.findViewById(R.id.tvSongSize);
        etSongName = view.findViewById(R.id.etSongName);
        etArtistName = view.findViewById(R.id.etArtistName);
        btnEditSong = view.findViewById(R.id.btnEditSong);
        etSongName.setText(fileName == null ? "" : fileName.substring(0, fileName.lastIndexOf(".")));
        String displaySize = FileUtils.byteCountToDisplaySize(fileSize);
        tvSongSize.setText(fileSize == 0 ? "0 Kb" : displaySize);
        Log.e(TAG, "initUI: duration: " + fileDuration );
    }
    private void prepareData() {
        nameSong = etSongName.getText().toString();
        nameArtist = etArtistName.getText().toString();
        if (nameSong.isEmpty()) {
            etSongName.setError("Please enter song name");
            return;
        }
        if (nameArtist.isEmpty()) {
            etArtistName.setError("Please enter artist name");
            return;
        }
        if (nameSong.equals(fileName)) {
            etSongName.setError("Please enter new song name");
            return;
        }
        newFile = new File(file.getParentFile(), nameSong + ".mp3");
        if (file.renameTo(newFile)) {
            Log.e(TAG, "saveFile: " + newFile.getAbsolutePath());
        }
    }
    private void handleClick() {
        tvSongUpload.setOnClickListener(v -> {
            openFileChooser();
        });
        btnEditSong.setOnClickListener(v -> {
            prepareData();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Do you want to save this song?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                UploadSongFragment uploadSongFragment = new UploadSongFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(com.vibe.vibe.constants.File.FILE, newFile);
                bundle.putString(com.vibe.vibe.constants.File.FILE_NAME, newFile.getName());
                bundle.putLong(com.vibe.vibe.constants.File.FILE_SIZE, newFile.length());
                bundle.putLong(com.vibe.vibe.constants.File.FILE_DURATION, fileDuration);
                bundle.putString("aristName", nameArtist);
                if (fileImage == null) {
                    fileImage = "";
                }
                bundle.putString(com.vibe.vibe.constants.File.FILE_IMAGE, fileImage);
                bundle.putString(com.vibe.vibe.constants.File.FILE_PATH, filePath);
                bundle.putBoolean(com.vibe.vibe.constants.File.FILE_SAVE, true);
                uploadSongFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, uploadSongFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }
    private void openFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GET_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GET_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Log.e(TAG, "onActivityResult2: " + data.getData().toString());
            Uri imageUri = data.getData();
            Log.e(TAG, "onActivityResult: " + imageUri.toString());
            ivSongUpload.setImageURI(imageUri);
            fileImage = imageUri.toString();
        }
    }
}