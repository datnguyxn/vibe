package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Artist;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistModel extends Model{
    private static final String TAG = "ArtistModel";
    private static ArtistModel instance = null;
    public interface SearchArtistListener {
        void onBeginSearch();
        void onSearchComplete(List<Object> artists);
        void onArtistAdded(Object artist);
        void onSearchError(String error);
    }
    private ArtistModel() {
        super();
    }

    public static ArtistModel getInstance() {
        if (instance == null) {
            instance = new ArtistModel();
        }
        return instance;
    }

    public void search(String key, SearchArtistListener listener) {
        ArrayList<Object> artists = new ArrayList<>();
        DatabaseReference artistRef = database.child(Schema.ARTISTS);

        /*
        * artists: {
        *   artistId: {
        *      name: "artist name",
        *     thumbnail: "thumbnail url",
        *    playlistId: "playlist id"
        *  },
        * artistId: {
        *     name: "artist name",
        *    thumbnail: "thumbnail url",
        *   playlistId: "playlist id"
        * }
        * }
        * */

        // search by artist name
        listener.onBeginSearch();

        artistRef.orderByChild("name").startAt(key).endAt(key + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artists.add(artist);
                    listener.onArtistAdded(artist);
                }
                listener.onSearchComplete(artists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onSearchError(databaseError.getMessage());
            }
        });
    }
}
