package com.vibe.vibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vibe.vibe.authentication.LoginActivity;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.fragments.AboutUsFragment;
import com.vibe.vibe.fragments.BarCodeFragment;
import com.vibe.vibe.fragments.HomeFragment;
import com.vibe.vibe.fragments.LibraryFragment;
import com.vibe.vibe.fragments.PlayerFragment;
import com.vibe.vibe.fragments.SearchFragment;
import com.vibe.vibe.fragments.SettingFragment;
import com.vibe.vibe.services.SongService;
import com.vibe.vibe.utils.MainActivityListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomAppBar bottomAppBar;
    private ImageView imgUnLike, imgPause, imgSong;
    private TextView tvNameSong, tvNameArtist;
    private CardView bottomCurrentSong;
    private View fragment;
    private Bundle bundle;
    private FirebaseAuth mAuth;
    private boolean isPlaying = false;
    private boolean isNowPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private int seekTo = 0;
    private int index;

    private ArrayList<Song> songs;
    private Song song;
    private static final int REQUEST_CODE = 100;
    private BroadcastReceiver onReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        handlePermissionForActivity();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
//
        if (savedInstanceState == null) {
            bottomNavigationView.getMenu().findItem(R.id.bottom_home).setIcon(R.drawable.home_fill);
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: " + item.getItemId());
                if (item.getItemId() == R.id.nav_home) {
                    replaceFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.nav_settings) {
                    replaceFragment(new SettingFragment());
                } else if (item.getItemId() == R.id.nav_scan) {
                    replaceFragment(new BarCodeFragment());
                } else if (item.getItemId() == R.id.nav_about) {
                    replaceFragment(new AboutUsFragment());
                } else if (item.getItemId() == R.id.nav_logout) {
                    mAuth.signOut();
                    SharedPreferences userRef = getSharedPreferences(Application.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                    SharedPreferences.Editor editor = userRef.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: " + item.getItemId());
                if (item.getItemId() == R.id.bottom_home) {
                    // set checked item in drawer
                    // change icon home_fill in menu bottom
                    // change icon home in menu drawer
                    clearBottomMenuIcon();
                    item.setIcon(R.drawable.home_fill);
                    navigationView.setCheckedItem(R.id.nav_home);
                    replaceFragment(new HomeFragment());
                    return true;
                } else if (item.getItemId() == R.id.bottom_search) {
                    clearBottomMenuIcon();
                    item.setIcon(R.drawable.search_fill);
                    replaceFragment(new SearchFragment());
                    return true;
                } else if (item.getItemId() == R.id.bottom_library) {
                    clearBottomMenuIcon();
                    item.setIcon(R.drawable.lib_fill);
                    replaceFragment(new LibraryFragment());
                    return true;
                }
                return false;
            }
        });
        handleBottomBehavior();
        handleBottomNavigation();
    }

    private void clearBottomMenuIcon() {
        bottomNavigationView.getMenu().findItem(R.id.bottom_home).setIcon(R.drawable.home);
        bottomNavigationView.getMenu().findItem(R.id.bottom_search).setIcon(R.drawable.search);
        bottomNavigationView.getMenu().findItem(R.id.bottom_library).setIcon(R.drawable.library);
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        bottomCurrentSong = findViewById(R.id.bottom_current_song);
        fragment = findViewById(R.id.frameLayout);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        imgUnLike = findViewById(R.id.imgUnLike);
        imgPause = findViewById(R.id.imgPause);
        imgSong = findViewById(R.id.imgSong);
        tvNameSong = findViewById(R.id.tvNameSong);
        tvNameArtist = findViewById(R.id.tvNameArtist);
        mAuth = FirebaseAuth.getInstance();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void handleBottomBehavior() {
        bottomCurrentSong.setOnClickListener(v -> {
            // Corrected code
//            LinearLayout.LayoutParams correctLayoutParams = (LinearLayout.LayoutParams) fragment.getLayoutParams();
//            correctLayoutParams.setMargins(0, 0, 0, 0);
//            fragment.setLayoutParams(correctLayoutParams);
            bottomCurrentSong.setVisibility(View.GONE);
//            bottomNavigationView.setVisibility(View.GONE);
            bottomAppBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            PlayerFragment fragment = new PlayerFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Application.SONGS_ARG, songs);
            bundle.putSerializable(Application.CURRENT_SONG, song);
            bundle.putInt(Application.SONG_INDEX, index);
            bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
            bundle.putBoolean(Application.IS_REPEAT, isRepeat);
            bundle.putInt(Application.SEEK_BAR_PROGRESS, seekTo);
            fragment.setArguments(bundle);
            transaction.setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_up);
            transaction.replace(R.id.frameLayout, fragment);
            transaction.commit();
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showNavigationViews() {
        bottomCurrentSong.setVisibility(View.VISIBLE);
//        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomAppBar.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void handlePermissionForActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = new String[]{
                    Manifest.permission.POST_NOTIFICATIONS
            };
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        }
    }

    private void handleBottomNavigation() {
        imgPause.setOnClickListener(v -> {
            Log.d(TAG, "handleBottomNavigation: " + isPlaying);
            if (isPlaying) {
                sendBroadcastToService(Action.ACTION_PAUSE);
            } else {
                sendBroadcastToService(Action.ACTION_PLAY);
            }
        });
    }

    private void sendBroadcastToService(int action) {
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra(Application.ACTION_TYPE, action);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Application.SONGS_ARG, songs);
        bundle.putSerializable(Application.CURRENT_SONG, song);
        bundle.putInt(Application.SONG_INDEX, index);
        bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
        bundle.putBoolean(Application.IS_REPEAT, isRepeat);
        bundle.putInt(Application.SEEK_BAR_PROGRESS, seekTo);

        intent.putExtras(bundle);
        startService(intent);
    }

}