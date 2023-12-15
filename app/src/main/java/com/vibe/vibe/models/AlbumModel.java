package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AlbumModel extends Model {
    private static final String TAG = AlbumModel.class.getSimpleName();
    private final String ALBUM_COLLECTION = "albums";

    private interface AlbumModelCallback {
        void onCallback(AlbumModel albumModel);
    }

    public interface AlbumSearchListener {
        void onAlbumBeginSearch();
        void onAlbumSearchResult(ArrayList<Object> albums);
        void onSongSearchResult(ArrayList<Object> songs);
        void onAlbumSearchError(String error);
    }

    public interface AlbumModelCallbacks {
        void onCallback(ArrayList<Album> albumModels);
    }

    public AlbumModel() {
        super();
    }

    public void getAlbums(AlbumModelCallbacks albumModelCallbacks) {
        database.child(ALBUM_COLLECTION).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Album> albums = new ArrayList<>();
                ArrayList<String> artistContributor = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Album album = new Album();
                    JSONObject jsonObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                    album.setId(dataSnapshot.getKey());
                    album.setName(jsonObject.optString("title"));
                    album.setDescription(jsonObject.optString("sortDescription"));
                    album.setImage(jsonObject.optString("thumbnail"));
                    int releaseAt = jsonObject.optInt("releaseAt");
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    album.setCreatedDate(dateFormat.format(releaseAt));

                    ArrayList<Song> songs = new ArrayList<>();
                    JSONArray albumData = jsonObject.optJSONArray("albumData");
                    if (albumData != null) {
                        for (int i = 0; i < albumData.length(); i++) {
                            JSONObject songObject = albumData.optJSONObject(i);
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
                            if (genresArray != null) {
                                for (int j = 0; j < genresArray.length(); j++) {
                                    String genre = genresArray.optString(j);
                                    genres.add(genre);
                                }
                            }

                            Song song = new Song(id, name, artistIds, artistNames, thumbnail, url, releaseDateStr, artistName, duration, genres);
                            songs.add(song);
                        }
                    }
                    if (songs.size() > 0) {
                        album.setSongs(songs);
                        album.setArtistIds(artistContributor);
                        Log.d(TAG, "onDataChange1: " + album.getArtistIds());
                    }  else {
                        Log.d(TAG, "onDataChange2: " + album.getName());
                    }
                    Log.d(TAG, "onDataChange3: " + album.getName());
                    albums.add(album);
                    if (albums.size() > 10) {
                        break;
                    }
                }
                albumModelCallbacks.onCallback(albums);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error " + error.getMessage());
                albumModelCallbacks.onCallback(null);
            }
        });
    }

    public void search(String key, AlbumSearchListener listener) {

    }
}
