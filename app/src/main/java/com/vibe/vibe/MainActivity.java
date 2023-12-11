package com.vibe.vibe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vibe.vibe.authentication.LoginActivity;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.fragments.AboutUsFragment;
import com.vibe.vibe.fragments.BarCodeFragment;
import com.vibe.vibe.fragments.HomeFragment;
import com.vibe.vibe.fragments.LibraryFragment;
import com.vibe.vibe.fragments.PlayerFragment;
import com.vibe.vibe.fragments.SearchFragment;
import com.vibe.vibe.fragments.SettingFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CardView bottomCurrentSong;
    private View fragment;
    private Bundle bundle;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
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
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragment.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            fragment.setLayoutParams(params);
            bundle = new Bundle();
            bundle.putString("bottomCurrentSong", bottomCurrentSong.toString());
            bundle.putString("bottomNavigationView", bottomNavigationView.toString());
            bundle.putString("fragment", fragment.toString());
            PlayerFragment playerFragment = new PlayerFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            playerFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayout, playerFragment);
            fragmentTransaction.commit();
            bottomCurrentSong.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
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
}