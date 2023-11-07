package com.vibe.vibe.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String id;
    private String name;
    private String description;
    private String image;
    private String createdDate;
    private ArrayList<Artist> artists;
    private ArrayList<String> artistIds;
    private ArrayList<Song> songs;

    //getters & setters
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

     public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

     public ArrayList<String> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(ArrayList<String> artistIds) {
        this.artistIds = artistIds;
    }

     public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", artists=" + artists +
                ", artistIds=" + artistIds +
                ", songs=" + songs +
                '}';
    }
}
