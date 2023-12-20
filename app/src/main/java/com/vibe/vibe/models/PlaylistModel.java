package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
    private final static String PLAYLIST_COLLECTION = "playlists";

    public interface onPlaylistAddListener {
        void onPlaylistAddSuccess();

        void onPlaylistAddFailure();
    }

    public interface playListCallbacks {
        void onCallbackPlaylist(ArrayList<Object> playlists);

        void onCallbackAlbum(ArrayList<Album> albums);

        void onCallbackError(String error);
    }

    public PlaylistModel() {
        super();
    }

    public void addPlaylist(String id, String playlistName, String description, String image, String userId, String createDate, onPlaylistAddListener listener) {
        Playlist playlist = new Playlist(id, playlistName, description, image, userId, createDate);
        Map<String, Object> playlistMap = playlist.toMap();
        playlistMap.put(Application.SONGS_ARG, new ArrayList<>());
        database.child(PLAYLIST_COLLECTION).child(userId).child(id).setValue(playlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void getPlaylists(playListCallbacks playListCallbacks) {
        DatabaseReference playlistRef = database.child(PLAYLIST_COLLECTION);
        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Album> albums = new ArrayList<>();
                ArrayList<Song> songs = new ArrayList<>();
                ArrayList<String> artistContributor = new ArrayList<>();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    Album album = new Album();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Object playlists = childSnapshot.getValue();
                        if (playlists instanceof Map) {
                            Map<String, Object> playlistMap = (Map<String, Object>) playlists;
                            JSONObject jsonObject = new JSONObject(playlistMap);
                            String id = jsonObject.optString("id");
                            String name = jsonObject.optString("title");

                            int releaseDate = jsonObject.optInt("releaseDate");
                            String releaseDateStr = dateFormat.format(releaseDate);

                            String artistName = jsonObject.optString("artistName");

                            JSONArray artistsIdArray = jsonObject.optJSONArray("artistIds");

                            int duration = jsonObject.optInt("duration");

                            String thumbnail = jsonObject.optString("thumbnail");

                            String url = jsonObject.optString("url");
                            ArrayList<String> artistIds = new ArrayList<>();
                            ArrayList<String> artistNames = new ArrayList<>();

                            if (artistsIdArray != null) {
                                for (int j = 0; j < artistsIdArray.length(); j++) {
                                    JSONObject artistId = artistsIdArray.optJSONObject(j);
                                    String artistIdStr = artistId.optString("id");
                                    String artistNameStr = artistId.optString("name");
                                    artistNames.add(artistNameStr);
                                    artistIds.add(artistIdStr);
                                    artistContributor.add(artistIdStr);
                                }
                            }

                            ArrayList<String> genres = new ArrayList<>();
                            JSONArray genresArray = jsonObject.optJSONArray("genreIds");
                            if (genresArray == null) {
                                break;
                            }
                            for (int j = 0; j < genresArray.length(); j++) {
                                String genre = genresArray.optString(j);
                                genres.add(genre);
                            }
                            if (url.isEmpty()) {
                                url = "Unknown";
                            }

                            Song song = new Song(id, name, artistIds, artistNames, thumbnail, url, releaseDateStr, artistName, duration, genres);
                            songs.add(song);
                        }
                        if (songs.size() > 0) {
                            album.setId(key);
                            album.setImage(songs.get(0).getImageResource());
                            album.setName(songs.get(0).getArtistName());
                            album.setSongs(songs);
                            album.setArtistIds(artistContributor);
                        }
                        Log.e(TAG, "onDataChange: " + album.toString());
                    }
                    albums.add(album);
                    if (albums.size() > 10) {
                        break;
                    }
                }

                playListCallbacks.onCallbackAlbum(albums);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                playListCallbacks.onCallbackError(error.getMessage());
            }
        });
    }
}
