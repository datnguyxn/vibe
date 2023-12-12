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
import com.vibe.vibe.entities.User;
import com.vibe.vibe.utils.HashPassword;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserModel extends Model {
    private static final String TAG = "UserModel";
    private PhoneAuthOptions phoneAuthOptions;
    private String verificationId = "";
    private final PlaylistModel playlistModel = new PlaylistModel();
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
}
