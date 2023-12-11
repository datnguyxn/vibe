package com.vibe.vibe.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.database.DatabaseReference;
import com.vibe.vibe.entities.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserModel extends Model {
    private static final String TAG = "UserModel";
    private PhoneAuthOptions phoneAuthOptions;
    private String verificationId = "";
    private PlaylistModel playlistModel = new PlaylistModel();
    private final String USERS_COLLECTION = "users";

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

    public UserModel() {
        super();
    }

    public UserModel(DatabaseReference database) {
        super(database);
    }

    public void addUserByGoogle(String uuid, String username, String email, Uri avatar) {
        User user = new User(uuid, username, email, avatar.toString());
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
                        Log.e(TAG,"onFailure: " + e.getMessage());
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
                        Log.e(TAG,"onFailure: " + e.getMessage());
                    }
                });
    }

    public void checkUserIsExist(String phone, isExistListener isExistListener) {
        database.child(USERS_COLLECTION).orderByChild("phoneNumber").equalTo(phone).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    User user = task.getResult().getValue(User.class);
                    Log.e(TAG, "checkUserIsExist: user is exist");
                    isExistListener.onExist(user);
                } else {
                    Log.e(TAG, "checkUserIsExist: user is not exist");
                    isExistListener.onNotFound();
                }
            } else {
                Log.e(TAG, "checkUserIsExist: " + task.getException().getMessage());
            }
        });
    }
}
