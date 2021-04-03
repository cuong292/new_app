package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBar actionbar;
    private View containerView;
    private HomeFragment homeFragment;
    private FavoriteFragment favoriteFragment;
    private GalleryFragment galleryFragment;

    private String searchQuery = "";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.navigation);
        toolbar = findViewById(R.id.toolbar);
        containerView = findViewById(R.id.container_view);
        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                homeFragment.setSearchQuery(searchQuery);
                transaction.replace(R.id.container_view, homeFragment);
                transaction.commit();
                toolbar.setTitle("Trang chủ");
                drawerLayout.close();
                return false;
            } else if (item.getItemId() == R.id.nav_gallery) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (galleryFragment == null) {
                    galleryFragment = new GalleryFragment();
                }
                transaction.replace(R.id.container_view, galleryFragment);
                transaction.commit();
                toolbar.setTitle("Thư viện");
                drawerLayout.close();
                return false;
            } else if (item.getItemId() == R.id.nav_favorite) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (favoriteFragment == null) {
                    favoriteFragment = new FavoriteFragment();
                }
                transaction.replace(R.id.container_view, new FavoriteFragment());
                transaction.commit();
                toolbar.setTitle("Yêu thích");
                drawerLayout.close();
                return false;
            }
            return true;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Trang chủ");
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayHomeAsUpEnabled(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        homeFragment.setSearchQuery(searchQuery);
        transaction.replace(R.id.container_view, homeFragment);
        transaction.commit();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // Android home
        if (itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
            // manage other entries if you have it ...
        }
        return true;
    }

    public void setQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}