package com.vibe.vibe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.vibe.vibe.authentication.LoginActivity;
import com.vibe.vibe.constants.Application;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(Application.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                boolean isFirstTime = preferences.getBoolean("firstTime", true);
                SharedPreferences uuidPref = getSharedPreferences(Application.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                String uuid = uuidPref.getString(Application.SHARED_PREFERENCES_UUID, null);
                if (isFirstTime) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (uuid != null) {
                        Log.e(TAG, "run: uuid found " + uuid);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
//                        finish();
                    } else {
                        Log.e(TAG, "run: uuid not found " + uuid);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
//                        finish();
                    }
                }
            }
        }, 1500);
    }
}