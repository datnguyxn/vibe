package com.vibe.vibe.entities;


import java.io.Serializable;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String bio;
    private String image;
    private String createdDate;
    private String playlistId;

    public Artist() {

    }

    public Artist(String id, String name, String bio, String image, String createdDate, String playlistId) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.createdDate = createdDate;
        this.playlistId = playlistId;
    }

    public Artist(String id, String name, String image, String playlistId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.playlistId = playlistId;
    }

    public Artist(String id, String name, String image, String bio, String createdDate) {
        this.id = id;
        this.name = name;
        this.image = image;
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
                ", image='" + image + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", playlistId='" + playlistId + '\'' +
                '}';
    }
}
