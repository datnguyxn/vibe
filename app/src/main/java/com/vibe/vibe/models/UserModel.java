package com.vibe.vibe.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.utils.HashPassword;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserModel extends Model {
    private static final String TAG = "UserModel";
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final String USERS_COLLECTION = "users";

    public interface onGetUserListener {
        void onGetUserSuccess(User user);
        void onGetUserFailure(String error);
    }
    public interface UserCallBacks {
        void onCallback(User user);
    }

    public interface isExistListener {
        void onExist(User user);

        void onNotFound();
    }

    public interface onAddConfigListener {
        void onCompleted();

        void onFailure();
    }

    public interface onGetConfigListener {
        void onCompleted(List<Map<String, Object>> config);

        void onFailure();
    }

    public interface LoginCallBacks {
        void onCompleted(User user);

        void onFailure();
    }

    public interface OnAddConfigurationListener {
        void onAddConfigurationSuccess();
        void onAddConfigurationFailure(String error);
    }

    public UserModel() {
        super();
    }

    public UserModel(DatabaseReference database) {
        super(database);
    }

    public void loginWithPhoneNumber(String phone, String password, LoginCallBacks callBacks) {
        Query query = database.child(USERS_COLLECTION);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isExist = false;
                User user = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        try {
                            if (user.getPhoneNumber().equals(phone) && HashPassword.verifyPassword(password, user.getPassword())) {
                                isExist = true;
                                callBacks.onCompleted(user);
                                break;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e(TAG, "onDataChange: user is null");
                    }
                }
                if (!isExist) {
                    callBacks.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
                callBacks.onFailure();
            }
        });
    }

    public void addUserByGoogle(String uuid, String username, String email, Uri avatar, String playlistId) {
        User user = new User(uuid, username, email, avatar, playlistId);
        database.child(USERS_COLLECTION).child(uuid).setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: add user successfully");
                        playlistModel.addPlaylist(uuid, "Liked Songs", "Like Songs", "", uuid, LocalTime.now().toString(), new PlaylistModel.onPlaylistAddListener() {
                            @Override
                            public void onPlaylistAddSuccess() {
                                Log.e(TAG, "onComplete: add playlist successfully");
                            }

                            @Override
                            public void onPlaylistAddFailure() {
                                Log.e(TAG, "onComplete: add playlist failed");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }

    public void addUserByPhone(String uuid, String username, String phoneNumber, String password, String playlistId) {
        User user = new User(uuid, username, phoneNumber, password, playlistId);
        database.child(USERS_COLLECTION).child(uuid).setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: add user successfully");
                        playlistModel.addPlaylist(uuid, "Liked Songs", "Like Songs", "", uuid, LocalTime.now().toString(), new PlaylistModel.onPlaylistAddListener() {
                            @Override
                            public void onPlaylistAddSuccess() {
                                Log.e(TAG, "onComplete: add playlist successfully");
                            }

                            @Override
                            public void onPlaylistAddFailure() {
                                Log.e(TAG, "onComplete: add playlist failed");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    public void checkUserIsExist(String phone, isExistListener isExistListener) {
        Query query = database.child(USERS_COLLECTION);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isExist = false;
                User user = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getPhoneNumber().equals(phone)) {
                            isExist = true;
                            isExistListener.onExist(user);
                            break;
                        }
                    }
                }
                if (!isExist) {
                    isExistListener.onNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
                isExistListener.onNotFound();
            }
        });
    }

    public void updatePassword(String uid, String newPassword) {
        database.child(USERS_COLLECTION).child(uid).child("password").setValue(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: update password successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
    public void getUser(String uid, onGetUserListener listener) {
        database.child(USERS_COLLECTION).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    listener.onGetUserSuccess(user);
                } else {
                    listener.onGetUserFailure("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetUserFailure(error.getMessage());
            }
        });
    }

    public void updateAvatar(String uid, String avatar) {
        Log.e(TAG, "updateAvatar: " + avatar);
        database.child(USERS_COLLECTION).child(uid).child("avatar").setValue(avatar)
                .addOnCompleteListener(task -> Log.e(TAG, "onComplete: update avatar successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: " + e.getMessage()));
    }

    public void updateUsername(String uid, String username) {
        database.child(USERS_COLLECTION).child(uid).child("username").setValue(username)
                .addOnCompleteListener(task -> Log.e(TAG, "onComplete: update username successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: " + e.getMessage()));
    }

    public void addConfiguration(String uid, String key, ArrayList<Map<String, Object>> values, OnAddConfigurationListener listener) {
        Query query = database.child(USERS_COLLECTION).child(uid).child(Schema.CONFIGURATION).child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    database.child(USERS_COLLECTION).child(uid).child(Schema.CONFIGURATION).child(key).removeValue();
                    database.child(USERS_COLLECTION).child(uid).child(Schema.CONFIGURATION).child(key).setValue(values);
                }else {
                    database.child(USERS_COLLECTION).child(uid).child(Schema.CONFIGURATION).child(key).setValue(values);
                }
                listener.onAddConfigurationSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error occur " + error.toString());
                listener.onAddConfigurationFailure(error.getMessage());
            }
        });
    }

    public void getConfiguration(String uid, String key, onGetConfigListener listener) {
        Query query = database.child(USERS_COLLECTION).child(uid).child(Schema.CONFIGURATION).child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object values = snapshot.getValue();
                    List<Map<String, Object>> list = new ArrayList<>();
                    if (values instanceof List) {
                        for (Object object : (List) values) {
                            if (object instanceof Map) {
                                list.add((Map<String, Object>) object);
                            }
                        }
                    }
                    listener.onCompleted(list);
                } else {
                    listener.onCompleted(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error occur " + error.toString());
                listener.onFailure();
            }
        });
    }
}
