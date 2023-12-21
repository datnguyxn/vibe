package com.vibe.vibe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.entities.Topic;
import com.vibe.vibe.utils.RandomValue;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class TopicModel extends Model {
    private static final String TOPIC_COLLECTION = "topics";
    private static final String TAG = "TopicModel";
    public TopicModel() {
        super();
    }
    public interface TopicModelListener {
        void onTopicsChanged(ArrayList<Topic> topics);
        void onTopicsChangedError(String error);
    }

    public void getAllTopics(TopicModelListener listener) {
        database.child(TOPIC_COLLECTION).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Topic> topics = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Topic topic = new Topic();
                    JSONObject jsonObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                    topic.setId(dataSnapshot.getKey());
                    topic.setTitle(jsonObject.optString("title"));
                    JSONObject data = jsonObject.optJSONObject("data");
                    ArrayList<Album> albums = new ArrayList<>();
                    ArrayList<Song> songs = new ArrayList<>();
                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject playlistObject = data.optJSONObject(key);
                        Album album = new Album();
                        album.setId(key);
                        album.setName(playlistObject.optString("title"));
                        album.setDescription(playlistObject.optString("sortDescription"));
                        album.setImage(playlistObject.optString("thumbnail"));
                        int releaseAt = playlistObject.optInt("releaseAt");
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        album.setCreatedDate(dateFormat.format(releaseAt));
                        album.setSongs(songs);
                        albums.add(album);
                    }
                    topic.setAlbums(albums);
                    topics.add(topic);
                }
                listener.onTopicsChanged(topics);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
                listener.onTopicsChangedError(error.getMessage());
            }
        });
    }
}
