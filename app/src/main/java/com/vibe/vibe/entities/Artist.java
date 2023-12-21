package com.vibe.vibe.entities;


import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String bio;
    private String thumbnail;
    private String createdDate;
    private String playlistId;

    public Artist() {

    }

    public Artist(String id, String name, String bio, String thumbnail, String createdDate, String playlistId) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.thumbnail = thumbnail;
        this.createdDate = createdDate;
        this.playlistId = playlistId;
    }

    public Artist(String id, String name, String thumbnail, String playlistId) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.playlistId = playlistId;
    }

    public Artist(String id, String name, String thumbnail, String bio, String createdDate) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.bio = bio;
        this.createdDate = createdDate;
    }

    // getters & setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", playlistId='" + playlistId + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("artistId", id);
        result.put("bio", bio);
        result.put("name", name);
        result.put("thumbnail", thumbnail);
        result.put("playlistId", playlistId);
        result.put("createdDate", createdDate);

        return result;
    }
}
