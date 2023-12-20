package com.vibe.vibe.models;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SongModel extends Model {
    private static final String TAG = SongModel.class.getSimpleName();

    public interface OnSongFindListener {
        void onSongFind(Uri songUri);

        void onSongNotExist();
    }

    public interface OnSongDownloadListener {
        void onSongDownloadSuccess();

        void onSongDownloadFailed();
    }

    public interface OnSongUploadListener {
        void onSongUploadSuccess();

        void onSongUploadFailed();
    }

    public SongModel() {
        super();
    }

    public void find(String id, OnSongFindListener listener) {
        Log.d(TAG, "find: " + id + ".mp3");
        storageRef.child(id + ".mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri.toString());
                listener.onSongFind(uri);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "find: ", e);
            listener.onSongNotExist();
        });
    }

    public void downloadSong(String id, String name, OnSongDownloadListener listener) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name + ".mp3");
            storageRef.child(id + ".mp3").getFile(file).addOnSuccessListener(taskSnapshot -> {
                listener.onSongDownloadSuccess();
            }).addOnFailureListener(e -> {
                listener.onSongDownloadFailed();
            });
        } catch (Exception e) {
            Log.e(TAG, "downloadSong: ", e);
            listener.onSongDownloadFailed();
        }
    }

    public void uploadSong(String id, Uri fileSong, OnSongUploadListener listener) {
        StorageReference songRef = storageRef.child(id + ".mp3");
        UploadTask uploadTask = songRef.putFile(fileSong);

        uploadTask.addOnProgressListener(taskSnapshot -> {
                    Log.d(TAG, "uploadSong: " + taskSnapshot.getBytesTransferred() + " / " + taskSnapshot.getTotalByteCount());
                    listener.onSongUploadSuccess();
                })
                .addOnPausedListener(taskSnapshot -> {
                    Log.d(TAG, "uploadSong: upload paused");
                    listener.onSongUploadFailed();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "uploadSong: ", e);
                    listener.onSongUploadFailed();
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "uploadSong: upload success");
                    listener.onSongUploadSuccess();
                });
    }
}
