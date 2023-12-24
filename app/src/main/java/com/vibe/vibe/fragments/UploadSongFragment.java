package com.vibe.vibe.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.UploadTask;
import com.vibe.vibe.R;
import com.vibe.vibe.adapters.UploadAdapter;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.models.SongModel;
import com.vibe.vibe.models.UserModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadSongFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UploadSongFragment";
    private MaterialButton btnUploadSong;
    private LinearLayout llEmptyUpload, llRecentUploaded;
    private RelativeLayout rlUploadSong;
    private TextView tvFileName, tvFileSize;
    private ProgressBar pbUpload;
    private RecyclerView rvRecentUploads;
    private File currentFile;
    private String fileName;
    private long fileSize;
    private long fileDuration;
    private String fileImage;
    private String filePath;
    private boolean isSave;
    private String ArtistName;
    private final UserModel userModel = new UserModel();
    private final SongModel songModel = new SongModel();
    private String uid;
    private UploadAdapter uploadAdapter;
    private static final int REQUEST_CODE_UPLOAD_SONG = 123;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadSongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadSongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadSongFragment newInstance(String param1, String param2) {
        UploadSongFragment fragment = new UploadSongFragment();
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
            currentFile = bundle.getSerializable("file") != null ? (File) bundle.getSerializable("file") : null;
            fileName = bundle.getString(com.vibe.vibe.constants.File.FILE_NAME);
            fileSize = bundle.getLong(com.vibe.vibe.constants.File.FILE_SIZE);
            fileDuration = bundle.getLong(com.vibe.vibe.constants.File.FILE_DURATION);
            fileImage = bundle.getString(com.vibe.vibe.constants.File.FILE_IMAGE);
            filePath = bundle.getString(com.vibe.vibe.constants.File.FILE_PATH);
            isSave = bundle.getBoolean(com.vibe.vibe.constants.File.FILE_SAVE, false);
            ArtistName = bundle.getString("aristName");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            trimCache(getContext());
            Log.e(TAG, "onDestroy: clear cache done");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_song, container, false);
        init(view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRecentUploads.setLayoutManager(layoutManager);
        rvRecentUploads.setNestedScrollingEnabled(true);
        uploadAdapter = new UploadAdapter(getContext());
        rvRecentUploads.setAdapter(uploadAdapter);
        handleClick();

        if (isSave) {
            rlUploadSong.setVisibility(View.VISIBLE);
            llEmptyUpload.setVisibility(View.GONE);

            displaySaveFile();
        } else {
            rlUploadSong.setVisibility(View.GONE);
        }
        return view;
    }

    private void init(View view) {
        btnUploadSong = view.findViewById(R.id.btnUploadSong);
        llEmptyUpload = view.findViewById(R.id.emptyUploaded);
        llRecentUploaded = view.findViewById(R.id.recentUploaded);
        rlUploadSong = view.findViewById(R.id.rl_audio_file);
        tvFileName = view.findViewById(R.id.file_name);
        tvFileSize = view.findViewById(R.id.file_size);
        pbUpload = view.findViewById(R.id.progress_upload);
        rvRecentUploads = view.findViewById(R.id.rvRecentUploaded);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Application.SHARED_PREFERENCES_USER, getContext().MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, null);
    }
    private void displaySaveFile() {
        llEmptyUpload.setVisibility(View.GONE);
        llRecentUploaded.setVisibility(View.VISIBLE);

        rlUploadSong.setVisibility(View.VISIBLE);
        initUploadLayout();
    }
    private void initUploadLayout() {
        String fileName = currentFile.getName();
        String songName = fileName.substring(0, fileName.lastIndexOf("."));
        tvFileName.setText(songName);
        tvFileSize.setText(fileSize == 0 ? "0 Kb" : FileUtils.byteCountToDisplaySize(fileSize));

//        upload task
        String id = UUID.randomUUID().toString();
        songModel.uploadSong(id, Uri.parse(filePath), new SongModel.OnSongUploadListener() {

            @Override
            public void onSongUploadSuccess() {
                Snackbar.make(getView(), "Upload success", Snackbar.LENGTH_SHORT).show();
                Song song = new Song(id, songName, uid, fileImage, filePath, (int) fileDuration, LocalTime.now().toString(), ArtistName);
                updateConfigForUser(song);
                rlUploadSong.setVisibility(View.GONE);
            }

            @Override
            public void onSongUploadProgress(UploadTask.TaskSnapshot taskSnapshot) {
                pbUpload.setProgress((int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
            }

            @Override
            public void onSongUploadFailed() {
                Snackbar.make(getView(), "Upload failed", Snackbar.LENGTH_SHORT).show();
                rlUploadSong.setVisibility(View.GONE);
            }
        });
    }

    private void updateConfigForUser(Song song) {
        userModel.getConfiguration(uid, Schema.SONGS_UPLOADED, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }

                HashMap<String, Object> map = new HashMap<>();
                Log.e(TAG, "onCompleted: song: " + song.toString());
                map.put("id", song.getId());
                map.put("name", song.getName());
                map.put("duration", fileDuration);
                map.put("artistId", song.getArtistId());
                map.put("imageResource", song.getImageResource());
                map.put("releaseDate", song.getReleaseDate());
                map.put("resource", song.getResource());
                map.put("artistName", song.getArtistName());
                Log.e(TAG, "onCompleted: map: " + map.toString());

                if (config.size() == 0) {
                    config.add(map);
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        Map<String, Object> item = config.get(i);
                        if (item.get("id").equals(song.getId())) {
                            config.set(i, map);
                            break;
                        }

                        if (i == config.size() - 1) {
                            config.add(map);
                        }
                    }
                }
                userModel.addConfiguration(uid, Schema.SONGS_UPLOADED, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        Log.e(TAG, "onCompleted: config upload song successful");
                        updateUI();
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.e(TAG, "onCompleted: config upload song failure");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "onFailure: " + error);
            }
        });
    }

    private void updateUI() {
        userModel.getConfiguration(uid, Schema.SONGS_UPLOADED, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null || config.size() == 0) {
                    llEmptyUpload.setVisibility(View.VISIBLE);
                    llRecentUploaded.setVisibility(View.GONE);
                    return;
                }

                llEmptyUpload.setVisibility(View.GONE);
                llRecentUploaded.setVisibility(View.VISIBLE);

                ArrayList<Song> songs = new ArrayList<>();

                for (Map<String, Object> song : config) {
                    String id = (String) song.get("id");
                    String name = (String) song.get("name");
                    String artistId = (String) song.get("artistId");
                    String releaseDate = (String) song.get("releaseDate");
                    String artistName = (String) song.get("artistName");
                    String image = (String) song.get("imageResource");
                    String resource = (String) song.get("resource");
                    int duration = (int) (long) song.get("duration");

                    Song songItem = new Song(id, name, artistId, image, resource, duration, releaseDate, artistName);
                    songs.add(songItem);
                }
                uploadAdapter.setSongs(songs);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "onFailure: " + error);
            }
        });
    }

    private void handleClick() {
        btnUploadSong.setOnClickListener(v -> {
            if (Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE_UPLOAD_SONG);
            } else {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_UPLOAD_SONG && resultCode == getActivity().RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult: " + data.getData().getPath());
            Uri uri = data.getData();
            getActivity().grantUriPermission(getActivity().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            getActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);
            Log.d(TAG, "onActivityResult: " + uri);
            filePath = uri.toString();
            handleCopyFileToTemp(uri);
            sendData();
        }
    }

    private void sendData() {
        Bundle bundle = new Bundle();
        bundle.putString(com.vibe.vibe.constants.File.FILE_PATH, filePath);
        bundle.putSerializable(com.vibe.vibe.constants.File.FILE, currentFile);
        bundle.putString(com.vibe.vibe.constants.File.FILE_NAME, fileName);
        bundle.putLong(com.vibe.vibe.constants.File.FILE_SIZE, fileSize);
        bundle.putLong(com.vibe.vibe.constants.File.FILE_DURATION, fileDuration);
        bundle.putString(com.vibe.vibe.constants.File.FILE_PATH, filePath);
        EditSongFragment editSongFragment = new EditSongFragment();
        editSongFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, editSongFragment).commit();
    }

    private void handleCopyFileToTemp(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r", null);
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            getFileDisplayName(uri);
            getDuration(uri);
            File tempFile = File.createTempFile(fileName == null ? "temp" : fileName, ".mp3", getActivity().getCacheDir());
            Log.d(TAG, "handleCopyFileToTemp: " + tempFile.getAbsolutePath());
            InputStream inputStream = new FileInputStream(fileDescriptor);
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            parcelFileDescriptor.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            currentFile = tempFile;
            fileSize = currentFile.length();
            Log.d(TAG, "handleCopyFileToTemp: " + fileSize);
        } catch (Exception e) {
            Log.d(TAG, "handleCopyFileToTemp: " + e.getMessage());
        }
    }

    private void getFileDisplayName(Uri uri) {
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);

                    fileName = cursor.getString(displayNameIndex);
                    Log.d(TAG, "getFileDisplayName: " + fileName);
                }
            } finally {
                cursor.close();
            }
        }
    }
    private void getDuration(Uri uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getActivity(), uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.e(TAG, "getDuration: time duration: " + time );
            fileDuration = Long.parseLong(time) / 1000;
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
                Log.e(TAG, "trimCache: clear cache done");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}