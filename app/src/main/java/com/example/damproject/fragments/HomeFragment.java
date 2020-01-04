package com.example.damproject.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.damproject.AddFoodActivity;
import com.example.damproject.LoginActivity;
import com.example.damproject.MainActivity;
import com.example.damproject.R;
import com.example.damproject.RegisterActivity;
import com.example.damproject.adapters.FoodListAdapter;
import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.util.UTIL;
import com.example.damproject.db.model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    public final static String EDIT_USER_KEY = "edit.user.key";
    public final static String LOGGED_USER_KEY = "logged.user.key";
    public final static String FOOD_TYPE_KEY = "food.type.key";
    public final static int EDIT_USER_REQUEST = 90;
    public final static int ADD_FOOD_REQUEST = 80;

    private User loggedUser;
    private ArrayList<FoodItem> foodBreakfast;
    private ArrayList<FoodItem> foodLunch;
    private ArrayList<FoodItem> foodDinner;
    private ArrayList<FoodItem> foodSnacks;

    private ArrayList<Integer> selectedIndices = new ArrayList<>();

    private int[] totalDataToday = new int[3];
    private int[] totalDataOverall = new int[3];

    //    UI Components
    private ImageView ivUserImg;
    private TextView tvUserName;
    private TextView tvUserAge;
    private TextView tvUserWeight;
    private TextView tvUserHeight;
    private Button btnUserEdit;

    private Button btnAddFood;
    private Button btnDeleteFood;
    private Button btnListBreakfast;
    private Button btnListLunch;
    private Button btnListDinner;
    private Button btnListSnacks;
    private ListView lvFood;

    private PieChart pieChartToday;
    private PieChart pieChartTotal;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        // TODO: remove this test list init
//        List<Ingredient> testIngredients1 = new ArrayList<>();
//        testIngredients1.add(new Ingredient("Cheese", 200, 10, 50, 140));
//        testIngredients1.add(new Ingredient("Bread", 20, 10, 5, 5));
//        List<Ingredient> testIngredients = new ArrayList<>();
//        testIngredients.add(new Ingredient("Bacon", 190, 70, 80, 40));
//        testIngredients.add(new Ingredient("Bread", 60, 30, 10, 20));
//        foodBreakfast.add(new FoodItem("Sandwich BLT", testIngredients));
//        foodBreakfast.add(new FoodItem("Cheesewich", testIngredients1));
        // TODO: remove this test list init


        initComponents(view);

        return view;
    }

    private void initComponents(final View view) {
        getDataForToday();
        getDataOverall();

        loggedUser = getArguments().getParcelable(LoginActivity.USER_KEY);
        foodBreakfast = getArguments().getParcelableArrayList(MainActivity.BREAKFAST_LIST_KEY);
        foodLunch = getArguments().getParcelableArrayList(MainActivity.LUNCH_LIST_KEY);
        foodDinner = getArguments().getParcelableArrayList(MainActivity.DINNER_LIST_KEY);
        foodSnacks = getArguments().getParcelableArrayList(MainActivity.SNACKS_LIST_KEY);

        ivUserImg = view.findViewById(R.id.home_img_user);
        tvUserName = view.findViewById(R.id.home_tv_user_name);
        tvUserAge = view.findViewById(R.id.home_tv_user_age);
        tvUserWeight = view.findViewById(R.id.home_tv_user_weight);
        tvUserHeight = view.findViewById(R.id.home_tv_user_height);
        btnUserEdit = view.findViewById(R.id.home_btn_user_edit);
        lvFood = view.findViewById(R.id.home_lv_subsection_food);
        btnAddFood = view.findViewById(R.id.home_btn_subsection_food_add_button);
        btnDeleteFood = view.findViewById(R.id.home_btn_subsection_food_delete_button);
        btnListBreakfast = view.findViewById(R.id.home_btn_subsection_food_breakfast);
        btnListLunch = view.findViewById(R.id.home_btn_subsection_food_lunch);
        btnListDinner = view.findViewById(R.id.home_btn_subsection_food_dinner);
        btnListSnacks = view.findViewById(R.id.home_btn_subsection_food_snacks);

        pieChartToday = view.findViewById(R.id.home_chart_today);
        pieChartTotal = view.findViewById(R.id.home_chart_total);

        setupPieCharts();

        ivUserImg.setImageBitmap(loggedUser.getImg());

        deActivateButtons();
        setActiveButton(view, btnListBreakfast.getId());
        setAddBtnText();

        editUserView();

        FoodListAdapter adapter = getActiveList();
        lvFood.setAdapter(adapter);
        UTIL.setListViewHeightBasedOnChildren(lvFood);

        btnListBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonListClickHandler(v);
            }
        });
        btnListLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonListClickHandler(v);
            }
        });
        btnListDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonListClickHandler(v);
            }
        });
        btnListSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonListClickHandler(v);
            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {getString(R.string.home_dialog_option_1), getString(R.string.home_dialog_option_2)};

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getString(R.string.home_dialog_title));

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(getString(R.string.home_dialog_option_1))) {
                            Intent intent = new Intent(view.getContext(), AddFoodActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(LOGGED_USER_KEY, loggedUser);
                            bundle.putString(FOOD_TYPE_KEY, getCurrentFoodType());
                            intent.putExtras(bundle);
                            startActivityForResult(intent, ADD_FOOD_REQUEST);
                        } else if (options[which].equals(getString(R.string.home_dialog_option_2))) {
                            //TODO: Implement
                        }
                    }
                });

                builder.show();
            }
        });

        btnUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(EDIT_USER_KEY, loggedUser);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_USER_REQUEST);
            }
        });

        lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View selected = parent.getChildAt(position);

                boolean isSelected = false;
                for (Integer i : selectedIndices) {
                    if (i == position) {
                        isSelected = true;
                        selectedIndices.remove(i);
                        break;
                    }
                }

                if (!isSelected) {
                    selectedIndices.add(position);
                }

                setSelectedItemState(selected, !isSelected);
            }
        });

        btnDeleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<FoodItem> toRemove = new ArrayList<>();
                FoodListAdapter adapter = (FoodListAdapter) lvFood.getAdapter();
                for (int i = 0; i < selectedIndices.size(); i++) {
                    toRemove.add(adapter.getItem(i));
                    switch (getActiveButton()) {
                        case 1:
                            foodBreakfast.remove(adapter.getItem(i));
                            break;
                        case 2:
                            foodLunch.remove(adapter.getItem(i));
                            break;
                        case 3:
                            foodDinner.remove(adapter.getItem(i));
                            break;
                        case 4:
                            foodSnacks.remove(adapter.getItem(i));
                            break;
                    }
                }
                selectedIndices.clear();
                adapter.notifyDataSetChanged();
                UTIL.setListViewHeightBasedOnChildren(lvFood);

                // remove from db
                new AsyncTask<Void, Void, Integer>() {

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        int count = 0;
                        for (FoodItem f : toRemove) {
                            count++;
                            AppDatabase.getInstance(getContext()).foodItemDao().deleteFoodItem(f);
                        }
                        return count;
                    }
                }.execute();
            }
        });
    }

    private void addDataToChart(PieChart chart, int[] data) {
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();
        xEntries.add("Calories");
        xEntries.add("Fats");
        xEntries.add("Proteins");
        int[] colors = new int[]{Color.YELLOW, Color.RED, Color.GREEN};

        for (int i = 0; i < data.length; i++) {
            yEntries.add(new PieEntry(data[i], i));
        }
        PieDataSet pieDataSet = new PieDataSet(yEntries, "Macronutrients");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(colors);

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);
        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void setupPieCharts() {
        Description totalDescription = new Description();
        totalDescription.setText(getString(R.string.home_chart_total_description));
        pieChartTotal.setDescription(totalDescription);
        pieChartTotal.setRotationEnabled(true);
        pieChartTotal.setHoleRadius(25f);
        pieChartTotal.setTransparentCircleAlpha(0);
        pieChartTotal.setCenterText(getString(R.string.home_chart_total_center_text));
        pieChartTotal.setCenterTextSize(10);
        pieChartTotal.setDrawEntryLabels(true);

        addDataToChart(pieChartTotal, totalDataOverall);


        Description todayDescription = new Description();
        todayDescription.setText(getString(R.string.home_chart_total_description));
        pieChartToday.setDescription(todayDescription);
        pieChartToday.setRotationEnabled(true);
        pieChartToday.setHoleRadius(25f);
        pieChartToday.setTransparentCircleAlpha(0);
        pieChartToday.setCenterText(getString(R.string.home_chart_total_center_text));
        pieChartToday.setCenterTextSize(10);
        pieChartToday.setDrawEntryLabels(true);

        addDataToChart(pieChartToday, totalDataToday);
    }

    @SuppressLint("StaticFieldLeak")
    private void getDataOverall() {
        new AsyncTask<Void, Void, List<FoodItem>>() {
            @Override
            protected List<FoodItem> doInBackground(Void... voids) {
                return AppDatabase.getInstance(getContext()).foodItemDao().getAllFoodItems();
            }

            @Override
            protected void onPostExecute(List<FoodItem> foodItems) {
                int totalCarbs = 0;
                int totalFats = 0;
                int totalProteins = 0;

                if (foodItems != null) {
                    for (FoodItem f : foodItems) {
                        totalCarbs += f.getTotalCarbohydrates();
                        totalFats += f.getTotalFats();
                        totalProteins += f.getTotalFats();
                    }
                }

                totalDataOverall[0] = totalCarbs;
                totalDataOverall[1] = totalFats;
                totalDataOverall[2] = totalProteins;
            }
        }.execute();
    }

    private void getDataForToday() {
        int totalCarbs = 0;
        int totalFats = 0;
        int totalProteins = 0;

        if (foodBreakfast != null) {
            for (FoodItem f : foodBreakfast) {
                totalCarbs += f.getTotalCarbohydrates();
                totalFats += f.getTotalFats();
                totalProteins += f.getTotalFats();
            }
        }

        if (foodDinner != null) {
            for (FoodItem f : foodDinner) {
                totalCarbs += f.getTotalCarbohydrates();
                totalFats += f.getTotalFats();
                totalProteins += f.getTotalFats();
            }
        }

        if (foodLunch != null) {
            for (FoodItem f : foodLunch) {
                totalCarbs += f.getTotalCarbohydrates();
                totalFats += f.getTotalFats();
                totalProteins += f.getTotalFats();
            }
        }

        if (foodSnacks != null) {
            for (FoodItem f : foodSnacks) {
                totalCarbs += f.getTotalCarbohydrates();
                totalFats += f.getTotalFats();
                totalProteins += f.getTotalFats();
            }
        }

        totalDataToday[0] = totalCarbs;
        totalDataToday[1] = totalFats;
        totalDataToday[2] = totalProteins;
    }

    private void setSelectedItemState(View selected, boolean b) {
        if (b) {
            selected.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            selected.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private String getCurrentFoodType() {
        switch (getActiveButton()) {
            case 1:
                return "breakfast";
            case 2:
                return "lunch";
            case 3:
                return "dinner";
            case 4:
                return "snacks";
        }
        return "unknown";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_FOOD_REQUEST) {
                Bundle bundle = data.getExtras();
                FoodItem foodItem = bundle.getParcelable(AddFoodActivity.NEW_FOOD_KEY);

                addFoodOnActiveList(foodItem);

                FoodListAdapter adapter = (FoodListAdapter) lvFood.getAdapter();
                adapter.notifyDataSetChanged();
                UTIL.setListViewHeightBasedOnChildren(lvFood);
            } else if (requestCode == EDIT_USER_REQUEST) {
                Bundle bundle = data.getExtras();
                loggedUser = (User) bundle.getParcelable(RegisterActivity.EDITED_USER_KEY);
                editUserView();
            }
        }
    }

    private void editUserView() {
        String userAge = String.format(Locale.US, "%d years old", loggedUser.getAge());
        String userWeight = String.format(Locale.US, "%.2f kg", loggedUser.getWeight());
        String userHeight = String.format(Locale.US, "%.2f cm", loggedUser.getHeight());

        tvUserName.setText(loggedUser.getUsername());
        tvUserAge.setText(userAge);
        tvUserWeight.setText(userWeight);
        tvUserHeight.setText(userHeight);
    }

    private void addFoodOnActiveList(FoodItem foodItem) {
        switch (getActiveButton()) {
            case 1:
                foodBreakfast.add(foodItem);
                break;
            case 2:
                foodLunch.add(foodItem);
                break;
            case 3:
                foodDinner.add(foodItem);
                break;
            case 4:
                foodSnacks.add(foodItem);
                break;
        }
    }

    private FoodListAdapter getActiveList() {
        ArrayList<FoodItem> food = null;
        switch (getActiveButton()) {
            case 1:
                food = foodBreakfast;
                break;
            case 2:
                food = foodLunch;
                break;
            case 3:
                food = foodDinner;
                break;
            case 4:
                food = foodSnacks;
                break;
        }

        return new FoodListAdapter(getContext(), food);
    }


    private void onButtonListClickHandler(View v) {
        deActivateButtons();
        setActiveButton(v, v.getId());
        setAddBtnText();
        selectedIndices.clear();

        lvFood.setAdapter(getActiveList());
        UTIL.setListViewHeightBasedOnChildren(lvFood);
    }

    private Button getActiveButtonView() {
        if (!btnListBreakfast.isEnabled())
            return btnListBreakfast;
        else if (!btnListLunch.isEnabled())
            return btnListLunch;
        else if (!btnListDinner.isEnabled())
            return btnListDinner;
        else
            return btnListSnacks;
    }

    private int getActiveButton() {
        int n = -1;

        if (!btnListBreakfast.isEnabled())
            n = 1;
        else if (!btnListLunch.isEnabled())
            n = 2;
        else if (!btnListDinner.isEnabled())
            n = 3;
        else if (!btnListSnacks.isEnabled())
            n = 4;

        return n;
    }

    private void setActiveButton(@NotNull View view, int id) {
        Button active = view.findViewById(id);
        active.setEnabled(false);
        active.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
    }

    private void deActivateButtons() {
        btnListBreakfast.setEnabled(true);
        btnListBreakfast.setBackgroundColor(getResources().getColor(R.color.colorButton));
        btnListLunch.setEnabled(true);
        btnListLunch.setBackgroundColor(getResources().getColor(R.color.colorButton));
        btnListDinner.setEnabled(true);
        btnListDinner.setBackgroundColor(getResources().getColor(R.color.colorButton));
        btnListSnacks.setEnabled(true);
        btnListSnacks.setBackgroundColor(getResources().getColor(R.color.colorButton));
    }

    private void setAddBtnText() {
        String txt = getActiveButtonView().getText().toString();
        btnAddFood.setText(String.format(Locale.US, "%s %s", getString(R.string.home_subsection_food_button), txt));
    }

}
