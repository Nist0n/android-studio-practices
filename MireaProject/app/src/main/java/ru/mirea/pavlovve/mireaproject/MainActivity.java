package ru.mirea.pavlovve.mireaproject;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class MainActivity extends AppCompatActivity {

    NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DataFragment())
                .commit();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.nav_files) {
                    selectedFragment = new FilesFragment();
                } else if (item.getItemId() == R.id.nav_data) {
                    selectedFragment = new DataFragment();
                } else if (item.getItemId() == R.id.nav_web) {
                    selectedFragment = new WebViewFragment();
                } else if (item.getItemId() == R.id.nav_background_work) {
                    selectedFragment = new BackgroundWorkFragment();
                } else if (item.getItemId() == R.id.nav_compass) {
                    selectedFragment = new CompassFragment();
                } else if (item.getItemId() == R.id.nav_camera_collage) {
                    selectedFragment = new CameraCollageFragment();
                } else if (item.getItemId() == R.id.nav_microphone) {
                    selectedFragment = new MicrophoneFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
}