package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
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
import java.util.Iterator;
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

    public interface onRemoveSongFromPlaylistListener {
        void onRemoveSongFromPlaylistSuccess();

        void onRemoveSongFromPlaylistFailure(String error);
    }

    public interface onGetRecentlyPlaylistListener {
        void onGetRecentlyPlaylistSuccess(Album album);

        void onGetRecentlyPlaylistFailure(String error);
    }

    public interface onGetPublicPlaylistListener {
        void onGetPublicPlaylistSuccess(Album album);

        void onGetPublicPlaylistFailed();
    }

    public interface onGetAllPlaylistOfUserListener {
        void onGetAllPlaylistSuccess(Album album);

        void onGetAllPlaylistFailed();
    }

    public interface OnGetPlaylistListener {
        void onGetPlaylist(ArrayList<Playlist> playlists);

        void onGetPlaylistFailed();
    }

    public interface OnAddPlaylistListener {
        void onAddPlaylistSuccess();

        void onAddPlaylistFailed();
    }

    public interface OnRemovePlaylistListener {
        void onRemovePlaylistSuccess();

        void onRemovePlaylistFailed();
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

    public void addSongToPrivatePlaylistFavorite(String uid, String playlistId, Song song, onPlaylistAddListener listener) {
        Map<String, Object> songMap = song.toMap();
        Query query = database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).child(Schema.FAVORITE_SONGS);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        JSONObject jsonObject = new JSONObject((Map) dataSnapshot.getValue());
                        String id = jsonObject.optString("id");
                        Log.e(TAG, "onDataChange: add song to favorite id:  " + id);
                        if (id.equals(song.getId())) {
                            listener.onPlaylistAddFailure();
                            return;
                        }
                    }
                    database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).child(Schema.FAVORITE_SONGS).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "onComplete: add song to favorite successfully");
                            listener.onPlaylistAddSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: add song to favorite failed" + e.getMessage());
                            listener.onPlaylistAddFailure();
                        }
                    });
                } else {
                    List<Map<String, Object>> songList = new ArrayList<>();
                    songList.add(songMap);
                    database.child(Schema.PLAYLISTS).child(uid).child(playlistId).child(Schema.FAVORITE_SONGS).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onPlaylistAddSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: add song to favorite failed" + e.getMessage());
                            listener.onPlaylistAddFailure();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: add song to favorite failed" + error.getMessage());
                listener.onPlaylistAddFailure();
            }
        });
    }

    public void addSongToRecentlyPlaylistOfUser(String uid, String playlistId, Song song, onPlaylistAddListener listener) {
        Map<String, Object> songMap = song.toMap();
        Query query = database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).child(Schema.RECENTLY_PLAYED);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        JSONObject jsonObject = new JSONObject((Map) dataSnapshot.getValue());
                        String id = jsonObject.optString("id");
                        Log.e(TAG, "onDataChange: add song to favorite id:  " + id);
                        if (id.equals(song.getId())) {
                            listener.onPlaylistAddFailure();
                            return;
                        }
                    }
                    database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).child(Schema.RECENTLY_PLAYED).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "onComplete: add song to favorite successfully");
                            listener.onPlaylistAddSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: add song to favorite failed" + e.getMessage());
                            listener.onPlaylistAddFailure();
                        }
                    });
                } else {
                    List<Map<String, Object>> songList = new ArrayList<>();
                    songList.add(songMap);
                    database.child(Schema.PLAYLISTS).child(uid).child(playlistId).child(Schema.RECENTLY_PLAYED).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onPlaylistAddSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: add song to favorite failed" + e.getMessage());
                            listener.onPlaylistAddFailure();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: add song to favorite failed" + error.getMessage());
                listener.onPlaylistAddFailure();
            }
        });
    }

    public void removeSongToPrivatePlaylistFavorite(String uid, String playlistId, Song song, onRemoveSongFromPlaylistListener listener) {
        Query query = database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).child(Schema.FAVORITE_SONGS);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object songs = snapshot.getValue();
                    Log.e(TAG, "onDataChange: songs: " + snapshot.getValue());
                    if (songs instanceof Map) {
                        Map<String, Object> songMap = (Map<String, Object>) songs;
                        for (Map.Entry<String, Object> entry : songMap.entrySet()) {
                            Map<String, Object> songMapChild = (Map<String, Object>) entry.getValue();
                            String id = (String) songMapChild.get("id");
                            Log.e(TAG, "onDataChange: id :" + song.getId() + " id want to remove: " + id);

                            if (id.equals(song.getId())) {
                                database.child(Schema.PLAYLISTS).child(uid).child(playlistId).child(Schema.FAVORITE_SONGS).child(entry.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "onComplete: " + entry.getKey() + " removed");
                                        listener.onRemoveSongFromPlaylistSuccess();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listener.onRemoveSongFromPlaylistFailure(e.getMessage());
                                    }
                                });
                                return;
                            }
                        }
                    }

                } else {
                    listener.onRemoveSongFromPlaylistFailure("Playlist not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onRemoveSongFromPlaylistFailure(error.getMessage());
            }
        });
    }

    public void getRecentlyPlaylistOfUser(String uid, String playlistId, onGetRecentlyPlaylistListener listener) {
        Query query = database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).child(Schema.RECENTLY_PLAYED);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object value = snapshot.getValue();
                    if (value instanceof Map) {
                        Album album = new Album();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        ArrayList<String> artistContributor = new ArrayList<>();
                        ArrayList<Song> songs = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject((Map) value);
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            JSONObject songObject = jsonObject.optJSONObject(key);
                            String songId = songObject.optString("id");
                            String songName = songObject.optString("name");

                            int releaseDate = songObject.optInt("releaseDate");
                            String releaseDateStr = dateFormat.format(releaseDate);

                            String artistName = songObject.optString("artistName");

                            JSONArray artistsIdArray = songObject.optJSONArray("artistsId");

                            int duration = songObject.optInt("duration");

                            String thumbnail = songObject.optString("imageResource");

                            ArrayList<String> artistIds = new ArrayList<>();
                            ArrayList<String> artistNames = new ArrayList<>();
                            if (artistsIdArray != null) {
                                for (int j = 0; j < artistsIdArray.length(); j++) {
                                    String artistId = artistsIdArray.optString(j);
                                    artistIds.add(artistId);
                                }
                            }

                            ArrayList<String> genres = new ArrayList<>();
                            JSONArray genresArray = songObject.optJSONArray("genres");
                            if (genresArray != null) {

                                for (int j = 0; j < genresArray.length(); j++) {
                                    String genre = genresArray.optString(j);
                                    genres.add(genre);
                                }
                            }

                            Song song = new Song(songId, songName, artistIds, artistNames, thumbnail, "Unknown", releaseDateStr, artistName, duration, genres);
                            songs.add(song);
                        }
                        if (songs.size() > 0) {
                            album.setSongs(songs);
                            album.setArtistIds(artistContributor);
                        }
                        album.setName("Recently Played");
                        album.setId(playlistId);
                        listener.onGetRecentlyPlaylistSuccess(album);
                    } else {
                        listener.onGetRecentlyPlaylistFailure("Playlist not exist");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetRecentlyPlaylistFailure(error.getMessage());
            }
        });
    }

    public void getPlaylistOfArtist(String playlistId, onGetPublicPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(playlistId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object playlists = snapshot.getValue();
                    Album album = new Album();
                    if (playlists instanceof List) {

                        JSONArray jsonArray = new JSONArray((List) playlists);
                        ArrayList<Song> songs = new ArrayList<>();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        ArrayList<String> artistContributor = new ArrayList<>();

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject songObject = jsonArray.optJSONObject(i);
                                String id = songObject.optString("id");
                                String name = songObject.optString("title");

                                int releaseDate = songObject.optInt("releaseDate");
                                String releaseDateStr = dateFormat.format(releaseDate);

                                String artistName = songObject.optString("artistName");

                                JSONArray artistsIdArray = songObject.optJSONArray("artistIds");

                                int duration = songObject.optInt("duration");

                                String thumbnail = songObject.optString("thumbnail");

                                String url = songObject.optString("url");
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
                                JSONArray genresArray = songObject.optJSONArray("genreIds");
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
                                album.setId(playlistId);
                                album.setSongs(songs);
                                album.setArtistIds(artistContributor);
                                listener.onGetPublicPlaylistSuccess(album);
                                return;
                            }
                        }
                    }
                } else {
                    listener.onGetPublicPlaylistFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetPublicPlaylistFailed();
            }
        });
    }

    public void getPlaylistOfUser(String uid, String playlistId, onGetAllPlaylistOfUserListener listener) {
        Query query = database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Album album = new Album();
                    Object value = snapshot.getValue();
                    if (value instanceof Map) {
                        JSONObject playlist = new JSONObject((Map) value);
                        String id = playlist.optString("id");
                        String name = playlist.optString("name");
                        String image = playlist.optString("image");
                        String description = playlist.optString("description");
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        JSONObject favoriteSongs = playlist.optJSONObject("songs");
                        ArrayList<String> artistContributor = new ArrayList<>();

                        if (favoriteSongs != null) {
                            ArrayList<Song> songs = new ArrayList<>();
                            Iterator<String> iterator = favoriteSongs.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                JSONObject songObject = favoriteSongs.optJSONObject(key);
                                String songId = songObject.optString("id");
                                String songName = songObject.optString("name");

                                int releaseDate = songObject.optInt("releaseDate");
                                String releaseDateStr = dateFormat.format(releaseDate);

                                String artistName = songObject.optString("artistName");

                                JSONArray artistsIdArray = songObject.optJSONArray("artistsId");

                                int duration = songObject.optInt("duration");

                                String thumbnail = songObject.optString("imageResource");

                                ArrayList<String> artistIds = new ArrayList<>();
                                ArrayList<String> artistNames = new ArrayList<>();

                                if (artistsIdArray != null) {
                                    for (int j = 0; j < artistsIdArray.length(); j++) {
                                        String artistId = artistsIdArray.optString(j);
                                        artistIds.add(artistId);
                                    }
                                }

                                ArrayList<String> genres = new ArrayList<>();
                                JSONArray genresArray = songObject.optJSONArray("genres");
                                if (genresArray != null) {

                                    for (int j = 0; j < genresArray.length(); j++) {
                                        String genre = genresArray.optString(j);
                                        genres.add(genre);
                                    }
                                }

                                Song song = new Song(songId, songName, artistIds, artistNames, thumbnail, "Unknown", releaseDateStr, artistName, duration, genres);
                                songs.add(song);
                            }
                            if (songs.size() > 0) {
                                album.setSongs(songs);
                                album.setArtistIds(artistContributor);
                            }
                        }
                        album.setName(name);
                        album.setId(id);
                        album.setImage(image);
                        album.setDescription(description);
                        listener.onGetAllPlaylistSuccess(album);
                    }
                } else {
                    listener.onGetAllPlaylistFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPrivatePlaylist(String userId, OnGetPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object playlists = snapshot.getValue();
                    if (playlists instanceof Map) {
                        Map<String, Object> playlistMap = (Map<String, Object>) playlists;
                        ArrayList<Playlist> playlistArrayList = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : playlistMap.entrySet()) {
                            Playlist playlist = new Playlist();
                            String id = entry.getKey();
                            JSONObject jsonObject = new JSONObject((Map) entry.getValue());

                            if (jsonObject != null) {
                                String name = jsonObject.optString("name");
                                String image = jsonObject.optString("image");
                                String description = jsonObject.optString("description");

                                JSONObject songs = jsonObject.optJSONObject("songs");
                                if (songs != null) {
                                    ArrayList<Song> songArrayList = new ArrayList<>();
                                    Iterator<String> iterator = songs.keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        JSONObject songObject = songs.optJSONObject(key);
                                        String songId = songObject.optString("id");
                                        String songName = songObject.optString("name");
                                        String songImage = songObject.optString("imageResource");
                                        Song song = new Song();
                                        song.setId(songId);
                                        song.setName(songName);
                                        song.setImageResource(songImage);
                                        songArrayList.add(song);
                                    }
                                    playlist.setSongs(songArrayList);
                                }

                                playlist.setId(id);
                                playlist.setPlaylistName(name);
                                playlist.setImage(image);
                                playlist.setDescription(description);
                            }

                            if (playlist != null) {
                                playlistArrayList.add(playlist);
                            }
                        }
                        listener.onGetPlaylist(playlistArrayList);
                    }
                } else {
                    listener.onGetPlaylistFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetPlaylistFailed();
            }
        });
    }

    public void addPrivatePlaylistForUser(String uid, String playlistId, Map<String, Object> values, OnAddPlaylistListener listener) {
        database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).setValue(values)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onAddPlaylistSuccess();
                    } else {
                        listener.onAddPlaylistFailed();
                    }
                })
                .addOnFailureListener(e -> listener.onAddPlaylistFailed());
    }

    public void removePlaylistForUser(String uid, String playlistId, OnRemovePlaylistListener listener) {
        database.child(PLAYLIST_COLLECTION).child(uid).child(playlistId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onRemovePlaylistSuccess();
                    } else {
                        listener.onRemovePlaylistFailed();
                    }
                })
                .addOnFailureListener(e -> listener.onRemovePlaylistFailed());
    }
}
