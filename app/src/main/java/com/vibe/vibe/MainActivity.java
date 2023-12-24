package com.vibe.vibe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vibe.vibe.authentication.LoginActivity;
import com.vibe.vibe.constants.Action;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.fragments.AboutUsFragment;
import com.vibe.vibe.fragments.BarCodeFragment;
import com.vibe.vibe.fragments.HomeFragment;
import com.vibe.vibe.fragments.LibraryFragment;
import com.vibe.vibe.fragments.PlayerFragment;
import com.vibe.vibe.fragments.SearchFragment;
import com.vibe.vibe.fragments.SettingFragment;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.services.SongService;
import com.vibe.vibe.utils.MainActivityListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private FirebaseAuth mAuth;
    private boolean isPlaying = false;
    private boolean isNowPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isLiked = false;
    private int seekTo;
    private int index;
    private final UserModel userModel = new UserModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private ArrayList<Song> songs;
    private Song currentSong;
    private int action;
    private String uid;
    private static final int REQUEST_CODE = 100;
    private BroadcastReceiver onReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            if (bundle.containsKey(Application.CURRENT_SONG)) {
                currentSong = (Song) bundle.getSerializable(Application.CURRENT_SONG);
                songs = (ArrayList<Song>) bundle.getSerializable(Application.SONGS_ARG);
                index = bundle.getInt(Application.SONG_INDEX);

                seekTo = bundle.getInt(Application.SEEK_BAR_PROGRESS, 0);
                Log.w(TAG, "onReceive123: seekTo: " + seekTo);
                isPlaying = bundle.getBoolean(Application.IS_PLAYING);
                isNowPlaying = bundle.getBoolean(Application.IN_NOW_PLAYING);
                isShuffle = bundle.getBoolean(Application.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Application.IS_REPEAT, false);

                action = bundle.getInt(Application.ACTION_TYPE);
                Log.e(TAG, "onReceive: action: " + action + " " + currentSong.getName() + " isPlaying: " + isPlaying + "; isNowPlaying: " + isNowPlaying + "; isShuffle: " + isShuffle + "; isRepeat: " + isRepeat);
                if (songs != null) {
                    Log.e(TAG, "onReceive: songs size: " + songs.size());
                } else {
                    Log.e(TAG, "onReceive: songs is empty 🫶");
                }
                handlePlayCurrentSongBehavior(action);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(onReceiver, SongService.getIntentFilter());
        init();
        handlePermissionForActivity();
        handleBottomBehavior();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        bottomCurrentSong.setVisibility(View.GONE);

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
        SharedPreferences sharedPreferences = getSharedPreferences(Application.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void handleBottomBehavior() {
        imgPause.setOnClickListener(v -> {
            Log.d(TAG, "handleBottomNavigation: " + isPlaying);
            if (isPlaying) {
                sendBroadcastToService(Action.ACTION_PAUSE);
            } else {
                sendBroadcastToService(Action.ACTION_RESUME);
            }
        });
        imgUnLike.setOnClickListener(v -> {
            if (isLiked) {
                isLiked = false;
                imgUnLike.setImageResource(R.drawable.like);
                removeSongFromFavorite();
            } else {
                isLiked = true;
                imgUnLike.setImageResource(R.drawable.unlike);
                addSongFromFavorite();
            }
        });
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
            bundle.putSerializable(Application.CURRENT_SONG, currentSong);
            bundle.putInt(Application.SONG_INDEX, index);
            bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
            bundle.putBoolean(Application.IS_REPEAT, isRepeat);
            bundle.putInt(Application.SEEK_BAR_PROGRESS, seekTo);
            bundle.putBoolean(Application.IS_PLAYING, isPlaying);
            bundle.putInt(Application.ACTION_TYPE, action);
            Log.e(TAG, "handleBottomBehavior: " + isPlaying);
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

    @Override
    public void hideNavigationViews() {
        bottomCurrentSong.setVisibility(View.GONE);
        bottomAppBar.setBackgroundColor(getResources().getColor(R.color.transparent));
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


    private void sendBroadcastToService(int action) {
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra(Application.ACTION_TYPE, action);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Application.SONGS_ARG, songs);
        bundle.putSerializable(Application.CURRENT_SONG, currentSong);
        bundle.putInt(Application.SONG_INDEX, index);
        bundle.putBoolean(Application.IS_SHUFFLE, isShuffle);
        bundle.putBoolean(Application.IS_REPEAT, isRepeat);
        bundle.putInt(Application.SEEK_BAR_PROGRESS, seekTo);

        intent.putExtras(bundle);
        startService(intent);
    }

    private void handlePlayCurrentSongBehavior(int action) {
        switch (action) {
            case Action.ACTION_PLAY:
                updateBottomCurrentSong();
            case Action.ACTION_START:
                Log.e(TAG, "handlePlayCurrentSongBehavior1: " + action);
//                bottomCurrentSong.setVisibility(View.VISIBLE);
                updateBottomCurrentSong();
                break;
            case Action.ACTION_PAUSE:
            case Action.ACTION_RESUME:
                Log.e(TAG, "handlePlayCurrentSongBehavior2: " + action);
                updateStatusCurrentSong();
                break;
            case Action.ACTION_SONG_LIKED:
                Log.e(TAG, "handlePlayCurrentSongBehavior3: " + action + isLiked);
                checkSongAddedToFavorite();
                updateStatusCurrentSong();
                break;
            case Action.ACTION_PLAY_ALBUM:
                Log.e(TAG, "handlePlayCurrentSongBehavior4: " + action);
                updateBottomCurrentSong();
                break;
            case Action.ACTION_PLAY_BACK:
                Log.e(TAG, "handlePlayCurrentSongBehavior5: " + action);
                updateBottomCurrentSong();
                break;
            case Action.ACTION_NEXT:
            case Action.ACTION_PREVIOUS:
                Log.e(TAG, "handlePlayCurrentSongBehavior6: " + action);
                checkSongAddedToFavorite();
                updateBottomCurrentSong();
                break;
            case Action.ACTION_CLOSE: {
                Log.e(TAG, "handleLayoutCurrentSong: hide current song bottom");
                bottomCurrentSong.setVisibility(View.GONE);
                break;
            }
        }
    }

    private void updateBottomCurrentSong() {
        Log.e(TAG, "updateBottomCurrentSong1: " + currentSong.getName());
        Log.e(TAG, "updateBottomCurrentSong2: " + isNowPlaying);
        if (currentSong != null && !isNowPlaying) {
            bottomCurrentSong.setVisibility(View.VISIBLE);
            tvNameSong.setText(currentSong.getName());
            tvNameArtist.setText(currentSong.getArtistName());
            updateStatusCurrentSong();
            if (currentSong.getImageResource() != null) {
                Glide.with(this).load(currentSong.getImageResource()).into(imgSong);
            } else {
                Glide.with(this).load(R.drawable.current_song).into(imgSong);
            }
        } else {
            Log.e(TAG, "updateBottomCurrentSong3: " + isNowPlaying);
//            bottomCurrentSong.setVisibility(View.GONE);
            tvNameSong.setText(currentSong.getName());
            tvNameArtist.setText(currentSong.getArtistName());
            updateStatusCurrentSong();
            if (currentSong.getImageResource() != null) {
                Glide.with(this).load(currentSong.getImageResource()).into(imgSong);
            } else {
                Glide.with(this).load(R.drawable.current_song).into(imgSong);
            }
        }
    }

    private void updateStatusCurrentSong() {
        if (isPlaying) {
            imgPause.setImageResource(R.drawable.pause_no_color);
        } else {
            imgPause.setImageResource(R.drawable.play_no_color);
        }
        checkSongAddedToFavorite();
        if (isLiked) {
            Log.e(TAG, "updateStatusCurrentSong1: " + isLiked);
            imgUnLike.setImageResource(R.drawable.unlike);
        } else {
            Log.e(TAG, "updateStatusCurrentSong2: " + isLiked);
            imgUnLike.setImageResource(R.drawable.like);
        }
    }

    private void checkSongAddedToFavorite() {
        userModel.getConfiguration(uid, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    isLiked = false;
                    imgUnLike.setImageResource(R.drawable.like);
                    return;
                } else {
                    for (Map<String, Object> map : config) {
                        if (Objects.equals(map.get(Schema.SONG_ID), currentSong.getId())) {
                            isLiked = true;
                            imgUnLike.setImageResource(R.drawable.unlike);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }

    private void addSongFromFavorite() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("songId", currentSong.getId());
        data.put("songName", currentSong.getName());
        userModel.getConfiguration(uid, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }
                if (config.size() == 0) {
                    config.add(data);
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        String songId = (String) config.get(i).get("songId");
                        if (songId.equals(currentSong.getId())) {
                            config.remove(i);
                            break;
                        }
                    }
                    config.add(data);
                }
                userModel.addConfiguration(uid, Schema.FAVORITE_SONGS, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        playlistModel.addSongToPrivatePlaylistFavorite(uid, uid, currentSong, new PlaylistModel.onPlaylistAddListener() {
                            @Override
                            public void onPlaylistAddSuccess() {
                                Log.e(TAG, "onPlaylistAddSuccess: ");
                                sendBroadcastToService(Action.ACTION_SONG_LIKED);
                            }

                            @Override
                            public void onPlaylistAddFailure() {
                                Log.d(TAG, "onPlaylistAddFailure: ");
                            }
                        });
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.d(TAG, "onAddConfigurationFailure: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }

    private void removeSongFromFavorite() {
        userModel.getConfiguration(uid, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }

                if (config.size() == 0) {
                    return;
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        if (config.get(i).get("songId").equals(currentSong.getId())) {
                            config.remove(i);
                            break;
                        }
                    }
                }
                userModel.addConfiguration(uid, Schema.FAVORITE_SONGS, config, new UserModel.OnAddConfigurationListener() {
                    @Override
                    public void onAddConfigurationSuccess() {
                        playlistModel.removeSongToPrivatePlaylistFavorite(uid, uid, currentSong, new PlaylistModel.onRemoveSongFromPlaylistListener() {
                            @Override
                            public void onRemoveSongFromPlaylistSuccess() {
                                Log.e(TAG, "onRemoveSongFromPlaylistSuccess: ");
                                sendBroadcastToService(Action.ACTION_SONG_LIKED);
                            }

                            @Override
                            public void onRemoveSongFromPlaylistFailure(String error) {
                                Log.d(TAG, "onRemoveSongFromPlaylistFailure: " + error);
                            }
                        });
                    }

                    @Override
                    public void onAddConfigurationFailure(String error) {
                        Log.d(TAG, "onAddConfigurationFailure: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_CODE) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
//            refund to to main activity again
            handleBottomBehavior();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onReceiver);
    }
}