package com.example.damproject.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.damproject.R;
import com.example.damproject.adapters.FoodListAdapter;
import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.util.UTIL;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class JournalFragment extends Fragment {

    private ArrayList<FoodItem> result = new ArrayList<>();

    // UI Components
    private EditText etFoodName;
    private EditText etIngredients;
    private EditText etDate;
    private CheckBox cbFoodName;
    private CheckBox cbIngredients;
    private CheckBox cbDate;
    private Button btnSearch;
    private TextView tvFoodName;
    private TextView tvIngredients;
    private TextView tvDate;

    private ListView lvResult;

    private ProgressBar progressBar;


    public JournalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        initComponents(view);

        return view;
    }

    private void initComponents(View view) {
        etFoodName = view.findViewById(R.id.journal_et_food_name);
        etIngredients = view.findViewById(R.id.journal_et_ingredients);
        etDate = view.findViewById(R.id.journal_et_date);
        cbFoodName = view.findViewById(R.id.journal_cb_food_name);
        cbIngredients = view.findViewById(R.id.journal_cb_ingredients);
        cbDate = view.findViewById(R.id.journal_cb_date);
        btnSearch = view.findViewById(R.id.journal_btn_search);
        tvFoodName = view.findViewById(R.id.journal_tv_label_food_name);
        tvIngredients = view.findViewById(R.id.journal_tv_label_ingredients);
        tvDate = view.findViewById(R.id.journal_tv_label_date);
        lvResult = view.findViewById(R.id.journal_lv_result);
        progressBar = view.findViewById(R.id.journal_pb);

        FoodListAdapter adapter = new FoodListAdapter(getContext(), result);
        lvResult.setAdapter(adapter);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                int checked = getCheckedInput();
                result.clear();

                switch (checked) {
                    case 0:
                        new AsyncTask<Void, Void, List<FoodItem>>() {
                            @Override
                            protected List<FoodItem> doInBackground(Void... voids) {
                                return AppDatabase.getInstance(getContext()).foodItemDao().getAllFoodItems();
                            }

                            @Override
                            protected void onPostExecute(List<FoodItem> foodItems) {
                                result.addAll(foodItems);
                                FoodListAdapter adapter = (FoodListAdapter) lvResult.getAdapter();
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }.execute();
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }
        });

        cbFoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unCheckOthers(cbFoodName);
                deActivateFields();
                activateField(cbFoodName);
            }
        });

        cbIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unCheckOthers(cbIngredients);
                deActivateFields();
                activateField(cbIngredients);
            }
        });

        cbDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unCheckOthers(cbDate);
                deActivateFields();
                activateField(cbDate);
            }
        });

    }

    private int getCheckedInput() {
        if (cbFoodName.isChecked()) {
            return 1;
        } else if (cbIngredients.isChecked()) {
            return 2;
        } else if (cbDate.isChecked()) {
            return 3;
        } else {
            return 0;
        }
    }

    private void unCheckOthers(CheckBox cb) {
        if (cbFoodName.getId() != cb.getId()) {
            cbFoodName.setChecked(false);
        }
        if (cbIngredients.getId() != cb.getId()) {
            cbIngredients.setChecked(false);
        }
        if (cbDate.getId() != cb.getId()) {
            cbDate.setChecked(false);
        }
    }

    private void deActivateFields() {
        tvFoodName.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvIngredients.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etFoodName.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etIngredients.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void activateField(CheckBox cb) {
        if (cbFoodName.isChecked()) {
            tvFoodName.setTextColor(getResources().getColor(android.R.color.black));
            etFoodName.setTextColor(getResources().getColor(android.R.color.black));
        } else if (cbIngredients.isChecked()) {
            tvIngredients.setTextColor(getResources().getColor(android.R.color.black));
            etIngredients.setTextColor(getResources().getColor(android.R.color.black));
        } else if (cbDate.isChecked()) {
            tvDate.setTextColor(getResources().getColor(android.R.color.black));
            etDate.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}
