package com.example.damproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.fragments.FoodListFragment;
import com.example.damproject.fragments.HomeFragment;
import com.example.damproject.fragments.IngredientManagerFragment;
import com.example.damproject.fragments.JournalFragment;
import com.example.damproject.fragments.AboutFragment;
import com.example.damproject.db.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public final static String BREAKFAST_LIST_KEY = "breakfast.key.list";
    public final static String LUNCH_LIST_KEY = "lunch.key.list";
    public final static String DINNER_LIST_KEY = "dinner.key.list";
    public final static String SNACKS_LIST_KEY = "snacks.key.list";

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    public static Date TODAY;

    private User loggedUser;
    private ArrayList<FoodItem> foodBreakfast = new ArrayList<>();
    private ArrayList<FoodItem> foodLunch = new ArrayList<>();
    private ArrayList<FoodItem> foodDinner = new ArrayList<>();
    private ArrayList<FoodItem> foodSnacks = new ArrayList<>();

    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            TODAY = DATE_FORMATTER.parse(DATE_FORMATTER.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initComponents();

        new AsyncTask<Void, Void, List<FoodItem>>() {

            @Override
            protected List<FoodItem> doInBackground(Void... voids) {
                return AppDatabase.getInstance(getApplicationContext()).foodItemDao().getFoodItemsByDateAndUserId(TODAY, loggedUser.getId());
            }

            @Override
            protected void onPostExecute(List<FoodItem> foodItems) {
                for (FoodItem f : foodItems) {
                    if (f.getType().equals("breakfast")) {
                        foodBreakfast.add(f);
                    } else if (f.getType().equals("lunch")) {
                        foodLunch.add(f);
                    } else if (f.getType().equals("dinner")) {
                        foodDinner.add(f);
                    } else {
                        foodSnacks.add(f);
                    }
                }
            }
        }.execute();

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
                        case R.id.nav_foodList:
                            currentFragment = createFoodListFragment();
                            break;
                        case R.id.nav_journal:
                            currentFragment = createJournalFragment();
                            break;
                        case R.id.nav_about:
                            currentFragment = createAboutFragment();
                            break;
                        case R.id.nav_ingredient_manager:
                            currentFragment = createIngredientManagerFragment();
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

    private Fragment createFoodListFragment() {
        Fragment fragment = new FoodListFragment();
        return fragment;
    }

    private Fragment createHomeFragment() {
        Fragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LoginActivity.USER_KEY, loggedUser);
        bundle.putParcelableArrayList(BREAKFAST_LIST_KEY, foodBreakfast);
        bundle.putParcelableArrayList(LUNCH_LIST_KEY, foodLunch);
        bundle.putParcelableArrayList(DINNER_LIST_KEY, foodDinner);
        bundle.putParcelableArrayList(SNACKS_LIST_KEY, foodSnacks);
        fragment.setArguments(bundle);
        return fragment;
    }

    private Fragment createAboutFragment() {
        Fragment fragment = new AboutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LoginActivity.USER_KEY, loggedUser.getUsername());
        fragment.setArguments(bundle);
        return fragment;
    }

    private Fragment createJournalFragment() {
        Fragment fragment = new JournalFragment();
        return fragment;
    }

    private Fragment createIngredientManagerFragment() {
        Fragment fragment = new IngredientManagerFragment();
        return fragment;
    }
}
