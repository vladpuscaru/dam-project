package com.example.damproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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

    private User loggedUser;

    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        openDefaultFragment(savedInstanceState);
    }

    private void initComponents() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Bundle bundle = getIntent().getExtras();
        loggedUser = bundle.getParcelable(LoginActivity.USER_KEY);
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(LoginActivity.USER_KEY, loggedUser);
        fragment.setArguments(bundle);
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
