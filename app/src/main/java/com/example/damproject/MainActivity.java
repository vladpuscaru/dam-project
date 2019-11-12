package com.example.damproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.damproject.fragments.HistoryFragment;
import com.example.damproject.fragments.HomeFragment;
import com.example.damproject.fragments.JournalFragment;
import com.example.damproject.fragments.SettingsFragment;
import com.example.damproject.util.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String USERS_KEY = "dom.example.damproject.USERS_KEY";

    private ArrayList<User> users = new ArrayList<>();

    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putParcelableArrayListExtra(USERS_KEY, users);
            startActivity(intent);
        }

        openDefaultFragment(savedInstanceState);
    }

    private void initComponents() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // TODO: Change this to take data from the database
        User mainUser = new User("admin", "admin");
        users.add(mainUser);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            currentFragment = createHomeFragment();
                            break;
                        case R.id.nav_journal:
                            currentFragment = createJournalFragment();
                            break;
                        case R.id.nav_history:
                            currentFragment = createHistoryFragment();
                            break;
                        case R.id.nav_settings:
                            currentFragment = createSettingsFragment();
                            break;
                    }

                    openFragment();
                    return true;
                }

            };

    private void openFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
    }

    private void openDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            currentFragment = createHomeFragment();
            openFragment();
        }
    }

    private Fragment createHomeFragment() {
        Fragment fragment = new HomeFragment();
        return fragment;
    }

    private Fragment createSettingsFragment() {
        Fragment fragment = new SettingsFragment();
        return fragment;
    }

    private Fragment createHistoryFragment() {
        Fragment fragment = new HistoryFragment();
        return fragment;
    }

    private Fragment createJournalFragment() {
        Fragment fragment = new JournalFragment();
        return fragment;
    }


}
