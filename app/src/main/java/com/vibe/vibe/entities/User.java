package com.vibe.vibe.entities;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uuid;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private Uri avatar;
    private String playlistId;

    //constructors
    public User() {

    }

    public User(String uuid, String username, String password, String phoneNumber) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
    public User(String uuid, String username, String phoneNumber, String password, String playlistId) {
        this.uuid = uuid;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.playlistId = playlistId;
    }
    public User(String uuid, String username, String email, Uri avatar) {
        this(uuid, username, email, avatar.toString());
        this.avatar = avatar;
    }

    public User(String uuid, String username, String email, Uri avatar, String playlistId) {
        this(uuid, username, email, avatar.toString());
        this.avatar = avatar;
        this.playlistId = playlistId;
    }

    public User(HashMap<String, Object> userMap) {
        this.uuid = (String) userMap.get("uuid");
        this.username = (String) userMap.get("username");
        this.phoneNumber = (String) userMap.get("phoneNumber");
        this.email = (String) userMap.get("email");
        this.password = (String) userMap.get("password");
        this.playlistId = (String) userMap.get("playlistId");
        this.avatar = Uri.parse((String) userMap.get("avatar"));
    }
    //getters & setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

     public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

     public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

     public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

     public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

     public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
    }

     public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        if (avatar == null) {
            result.put("uuid", uuid);
            result.put("username", username);
            result.put("phoneNumber", phoneNumber);
            result.put("password", password);
            result.put("playlistId", playlistId);
            result.put("email", "");
            result.put("avatar", "");
        } else {
            result.put("uuid", uuid);
            result.put("username", username);
            result.put("phoneNumber", "");
            result.put("password", password);
            result.put("playlistId", playlistId);
            result.put("email", email);
            result.put("avatar", avatar.toString());
        }

        return result;
    }
    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", avatar=" + avatar.toString() +
                ", playlistId='" + playlistId + '\'' +
                '}';
    }
}
