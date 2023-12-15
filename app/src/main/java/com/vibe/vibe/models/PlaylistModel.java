package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Playlist;
import com.vibe.vibe.entities.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistModel extends Model {
    private static final String TAG = PlaylistModel.class.getSimpleName();

    public interface onPlaylistAddListener {
        void onPlaylistAddSuccess();

        void onPlaylistAddFailure();
    }

    public interface playListCallbacks {
        void onCallback(ArrayList<Playlist> playlists);
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
                if (task.isSuccessful()) {
                    Log.e(TAG, "onComplete: add playlist successfully");
                    listener.onPlaylistAddSuccess();
                } else {
                    Log.e(TAG, "onComplete: add playlist failed");
                    listener.onPlaylistAddFailure();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
                listener.onPlaylistAddFailure();
            }
        });
    }

//    public void getPlaylists(playListCallbacks playListCallbacks) {
//        database.child(Schema.PLAYLISTS).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<Playlist> playlists = new ArrayList<>();
//                Object playlist = snapshot.getValue();
//                Album album = new Album();
//                if (playlists instanceof List) {
//                    JSONArray jsonArray = new JSONArray((List) playlists);
//                    ArrayList<Song> songs = new ArrayList<>();
//                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                    ArrayList<String> artistContributor = new ArrayList<>();
//                    if (jsonArray != null) {
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject songObject = jsonArray.optJSONObject(i);
//                            String id = songObject.optString("id");
//                            String name = songObject.optString("title");
//
//                            int releaseDate = songObject.optInt("releaseDate");
//                            String releaseDateStr = dateFormat.format(releaseDate);
//
//                            String artistName = songObject.optString("artistName");
//
//                            JSONArray artistsIdArray = songObject.optJSONArray("artistIds");
//
//                            int duration = songObject.optInt("duration");
//
//                            String thumbnail = songObject.optString("thumbnail");
//
//                            String url = songObject.optString("url");
//                            ArrayList<String> artistIds = new ArrayList<>();
//                            ArrayList<String> artistNames = new ArrayList<>();
//
//                            if (artistsIdArray != null) {
//                                for (int j = 0; j < artistsIdArray.length(); j++) {
//                                    JSONObject artistId = artistsIdArray.optJSONObject(j);
//                                    String artistIdStr = artistId.optString("id");
//                                    String artistNameStr = artistId.optString("name");
//                                    artistNames.add(artistNameStr);
//                                    artistIds.add(artistIdStr);
//                                    artistContributor.add(artistIdStr);
//                                }
//                            }
//
//                            ArrayList<String> genres = new ArrayList<>();
//                            JSONArray genresArray = songObject.optJSONArray("genreIds");
//                            for (int j = 0; j < genresArray.length(); j++) {
//                                String genre = genresArray.optString(j);
//                                genres.add(genre);
//                            }
//                            if (url.isEmpty()) {
//                                url = "Unknown";
//                            }
//
//                            Song song = new Song(id, name, artistIds, artistNames, thumbnail, url, releaseDateStr, artistName, duration, genres);
//                            songs.add(song);
//                        }
//                        if (songs.size() > 0) {
////                            album.setId(playlistId);
//                            album.setSongs(songs);
//                            album.setArtistIds(artistContributor);
//                            playListCallbacks.onCallback(album);
//                            return;
//                        }
//                    }
//                }
//            } else {
//                playListCallbacks.onCallback(null);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                listener.onGetPublicPlaylistFailed();
//            }
//        });
//    }
}
