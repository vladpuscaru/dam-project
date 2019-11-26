package com.example.damproject.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.damproject.R;
import com.example.damproject.adapters.RecipeListAdapter;
import com.example.damproject.network.HttpsManager;
import com.example.damproject.network.HttpResponse;
import com.example.damproject.network.JsonParser;
import com.example.damproject.network.model.Recipe;
import com.example.damproject.util.UTIL;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodListFragment extends Fragment {

    private static String API_URL = "https://api.edamam.com/search?";
    private final static String API_APP_ID = "app_id=9a085b11";
    private final static String API_APP_KEY = "app_key=1071ce1021b6c5aa23d8761070e36cd0";

    private HttpResponse httpResponse;
    private final List<Recipe> recipeList = new ArrayList<>();


    // UI Components
    private ListView lvFoods;
    private Button btnSearch;
    private EditText etSearch;
    private ProgressBar pb;

    public FoodListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        initComponents(view);

        return view;
    }

    private void initComponents(View view) {
        lvFoods = view.findViewById(R.id.food_list_lv);
        btnSearch = view.findViewById(R.id.food_list_btn_search);
        etSearch = view.findViewById(R.id.food_list_et_search);
        pb = view.findViewById(R.id.food_list_pb);

        RecipeListAdapter adapter = new RecipeListAdapter(getContext(), recipeList);
        lvFoods.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                new HttpsManager() {
                    @Override
                    protected void onPostExecute(String s) {
                        httpResponse = JsonParser.parseJson(s);
                        if (httpResponse != null) {

                            recipeList.clear();
                            recipeList.addAll(httpResponse.getFoodList());
                            RecipeListAdapter adapter = (RecipeListAdapter) lvFoods.getAdapter();
                            adapter.notifyDataSetChanged();

                            pb.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "E NULL", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(constructAPIQuery(etSearch.getText().toString()));
            }
        });
    }

    private String constructAPIQuery(String searcheable) {
        StringBuilder API_QUERY = new StringBuilder();

        API_QUERY.append(API_URL);
        API_QUERY.append("q=").append(searcheable);
        API_QUERY.append("&").append(API_APP_ID);
        API_QUERY.append("&").append(API_APP_KEY);

        return API_QUERY.toString();
    }

}
