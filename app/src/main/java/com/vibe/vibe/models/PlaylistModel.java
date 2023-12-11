package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistModel extends Model {
    private static final String TAG = "PlaylistModel";

    public interface onPlaylistAddListener {
        void onPlaylistAddSuccess();
        void onPlaylistAddFailure();
    }

    public PlaylistModel() {
        super();
    }

    public void addPlaylist(String id, String playlistName, String description, String image, String userId, String createDate, onPlaylistAddListener listener) {
        Playlist playlist = new Playlist(id, playlistName, description, image, userId, createDate);
        Map<String, Object> playlistMap = playlist.toMap();
        playlistMap.put(Application.SONGS_ARG, new ArrayList<>());
        database.child(Schema.PLAYLISTS).child(userId).child(id).setValue(playlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.e(TAG, "onComplete: add playlist successfully");
                    listener.onPlaylistAddSuccess();
                }
                else {
                    Log.e(TAG, "onComplete: add playlist failed");
                    listener.onPlaylistAddFailure();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: " + e.getMessage());
                listener.onPlaylistAddFailure();
            }
        });
    }
}
