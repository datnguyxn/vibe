package com.vibe.vibe;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();
    public static final String CHANNEL_ID = "Vibe";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Vibe", NotificationManager.IMPORTANCE_HIGH);
            Log.e(TAG, "createNotificationChannel: " + channel.toString());
            channel.setDescription("Vibe");
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
