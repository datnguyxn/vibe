package com.vibe.vibe.entities;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Song implements Serializable {
    private String id;
    private String name;
    private String artistId;
    private ArrayList<String> artistsId;
    private String imageResource;
    private String resource;
    private String releaseDate;
    private String artistName;
    private int duration;
    private ArrayList<String> artistsName;
    private ArrayList<String> genres;

    public Song() {
    }
    public Song(String id, String name, String artistId, String imageResource, String resource, String releaseDate) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.imageResource = imageResource;
        this.resource = resource;
        this.releaseDate = releaseDate;
    }

    public Song(String id, String name, ArrayList<String> artistsId,ArrayList<String> artistsName, String imageResource, String resource, String releaseDate, String artistName, int duration, ArrayList<String> genres) {
        this.id = id;
        this.name = name;
        this.artistsId = artistsId;
        this.artistsName = artistsName;
        this.imageResource = imageResource;
        this.resource = resource;
        this.releaseDate = releaseDate;
        this.artistName = artistName;
        this.duration = duration;
        this.genres = genres;
    }

    public Song(String id, String name, String artistId, String imageResource, String resource, int duration, String releaseData) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.imageResource = imageResource;
        this.resource = resource;
        this.duration = duration;
        this.releaseDate = releaseData;
    }

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

     public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

     public ArrayList<String> getArtistsId() {
        return artistsId;
    }

    public void setArtistsId(ArrayList<String> artistsId) {
        this.artistsId = artistsId;
    }

     public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

     public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

     public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

     public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

     public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

     public ArrayList<String> getArtistsName() {
        return artistsName;
    }

    public void setArtistsName(ArrayList<String> artistsName) {
        this.artistsName = artistsName;
    }

     public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", this.id);
        result.put("name", this.name);
        result.put("artistId", this.artistId);
        result.put("imageResource", this.imageResource);
        if (this.artistsName != null) {
            result.put("artistName", this.artistsName);
        }
        if (this.artistsId != null) {
            result.put("artistsId", this.artistsId);
        }
        result.put("artistName", artistName);
        result.put("duration", duration);
        result.put("genres", genres);
        result.put("releaseDate", releaseDate);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return id.equals(song.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistId='" + artistId + '\'' +
                ", imageResource='" + imageResource + '\'' +
                ", resource='" + resource + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
