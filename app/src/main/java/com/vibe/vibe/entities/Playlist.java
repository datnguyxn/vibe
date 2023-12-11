package com.vibe.vibe.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Playlist {
    private String id;
    private String playlistName;
    private String description;
    private String image;
    private String userId;
    private String createdDate;
    private ArrayList<Song> songs;

    public Playlist() {

    }

    public Playlist(String id, String playlistName, String description, String image, String userId, String createdDate, ArrayList<Song> songs) {
        this.id = id;
        this.playlistName = playlistName;
        this.description = description;
        this.image = image;
        this.userId = userId;
        this.createdDate = createdDate;
        this.songs = songs;
    }

    public Playlist(String id, String playlistName, String description, String image, String userId, String createDate) {
        this.id = id;
        this.playlistName = playlistName;
        this.description = description;
        this.image = image;
        this.userId = userId;
        this.createdDate = createDate;
    }

    //getters & setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

     public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

     public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

     public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

     public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

     public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    //getters & setters

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

