package com.vibe.vibe.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class Playlist {
    private String id;
    private String playlistName;
    private String description;
    private String image;
    private String userId;
    private String createdDate;
    private ArrayList<Song> songs;

    // constructors
    public Playlist() {
    }

    public Playlist(String id, String playlistName, String description, String image, String userId, String createdDate) {
        this.id = id;
        this.playlistName = playlistName;
        this.description = description;
        this.image = image;
        this.userId = userId;
        this.createdDate = createdDate;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", playlistName);
        result.put("description", description);
        result.put("image", image);
        result.put("userId", userId);
        result.put("createdDate", createdDate);
        return result;
    }
    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", playlistName='" + playlistName + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", userId='" + userId + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }

}

