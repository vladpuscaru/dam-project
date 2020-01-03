package com.example.damproject.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;

import com.example.damproject.LoginActivity;
import com.example.damproject.MapsActivity;
import com.example.damproject.R;

public class AboutFragment extends Fragment {
    private final static String SHARED_PREFERENCES_NAME = "shared.pref.name";
    private final static String RATING_BAR_SHARED_PREFERENCES = "rating.bar.shared.pref";

    private String username;

    // UI Components
    private RatingBar ratingBar;
    private ListView lvFeatures;
    private SharedPreferences preferences;
    private Button btnMaps;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        initComponents(view);

        return view;
    }

    private void initComponents(View view) {
        Bundle bundle = getArguments();
        username = bundle.getString(LoginActivity.USER_KEY);

        ratingBar = view.findViewById(R.id.about_rb);
        lvFeatures = view.findViewById(R.id.about_lv_features);
        btnMaps = view.findViewById(R.id.about_btn_maps);

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        ArrayAdapter adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.features_array , android.R.layout.simple_list_item_1);
        lvFeatures.setAdapter(adapter);

        if (getActivity() != null) {
            preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            float v = preferences.getFloat(RATING_BAR_SHARED_PREFERENCES + "." + username, -1);
            if (v != -1) {
                ratingBar.setRating(v);
            }

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    SharedPreferences.Editor editor = preferences.edit();
                    String VALUE_NAME = RATING_BAR_SHARED_PREFERENCES + "." + username;
                    editor.putFloat(VALUE_NAME, rating);
                    editor.apply();
                }
            });
        }
    }

}
