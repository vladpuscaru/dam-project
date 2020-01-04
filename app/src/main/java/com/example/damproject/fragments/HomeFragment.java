package com.example.damproject.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.AddFoodActivity;
import com.example.damproject.LoginActivity;
import com.example.damproject.MainActivity;
import com.example.damproject.R;
import com.example.damproject.RegisterActivity;
import com.example.damproject.adapters.FoodListAdapter;
import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.db.model.Ingredient;
import com.example.damproject.util.UTIL;
import com.example.damproject.db.model.User;
import com.github.mikephil.charting.charts.PieChart;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    public final static String EDIT_USER_KEY = "edit.user.key";
    public final static String LOGGED_USER_KEY = "logged.user.key";
    public final static String FOOD_TYPE_KEY = "food.type.key";
    public final static int EDIT_USER_REQUEST = 90;
    public final static int ADD_FOOD_REQUEST = 80;

    private AlertDialog.Builder pickDialog;
    private View pickView;
    private ListView lvPickForm;

    private User loggedUser;
    private ArrayList<FoodItem> foodBreakfast;
    private ArrayList<FoodItem> foodLunch;
    private ArrayList<FoodItem> foodDinner;
    private ArrayList<FoodItem> foodSnacks;

    private ArrayList<FoodItem> foodPick = new ArrayList<>();

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

    private ProgressBar progressBar;

    private PieChartView pieChartToday;
    private PieChartView pieChartTotal;
    private PieChartView pieChartCategory;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents(view);

        return view;
    }

    private void initComponents(final View view) {


        loggedUser = getArguments().getParcelable(LoginActivity.USER_KEY);
        foodBreakfast = getArguments().getParcelableArrayList(MainActivity.BREAKFAST_LIST_KEY);
        foodLunch = getArguments().getParcelableArrayList(MainActivity.LUNCH_LIST_KEY);
        foodDinner = getArguments().getParcelableArrayList(MainActivity.DINNER_LIST_KEY);
        foodSnacks = getArguments().getParcelableArrayList(MainActivity.SNACKS_LIST_KEY);

//        Log.d("WTF", "\nBREAKFAST: " + "\n" + foodBreakfast + "\n");
//        Log.d("WTF", "LUNCH: " + "\n" + foodLunch + "\n");
//        Log.d("WTF", "DINNER: " + "\n" + foodDinner + "\n");
//        Log.d("WTF", "SNACKS: " + "\n" + foodSnacks + "\n");

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
        progressBar = view.findViewById(R.id.home_pb);


        pieChartToday = view.findViewById(R.id.home_chart_today);
        pieChartTotal = view.findViewById(R.id.home_chart_total);
        pieChartCategory = view.findViewById(R.id.home_chart_category);

        pieChartCategory.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                Toast.makeText(
                        getContext(),
                        String.valueOf(value.getLabelAsChars()) + ": " + value.getValue() + " calories",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onValueDeselected() {
                return;
            }
        });

        getDataForToday();
        getDataOverall();
        getDataForCategory();

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
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (options[which].equals(getString(R.string.home_dialog_option_1))) {
                            Intent intent = new Intent(view.getContext(), AddFoodActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(LOGGED_USER_KEY, loggedUser);
                            bundle.putString(FOOD_TYPE_KEY, getCurrentFoodType());
                            intent.putExtras(bundle);
                            startActivityForResult(intent, ADD_FOOD_REQUEST);
                        } else if (options[which].equals(getString(R.string.home_dialog_option_2))) {
                            progressBar.setVisibility(View.VISIBLE);
                            new AsyncTask<Void, Void, List<FoodItem>>() {
                                @Override
                                protected List<FoodItem> doInBackground(Void... voids) {
                                    return AppDatabase.getInstance(getContext()).foodItemDao().getAllFoodItems();
                                }

                                @Override
                                protected void onPostExecute(final List<FoodItem> foodItems) {
                                    foodPick.clear();
                                    foodPick.addAll(foodItems);

                                    pickDialog = new AlertDialog.Builder(getContext());
                                    pickDialog.setTitle(getString(R.string.home_pick_dialog_title));

                                    pickView = View.inflate(getContext(), R.layout.home_pick_food_form, null);
                                    pickDialog.setView(pickView);

                                    lvPickForm = pickView.findViewById(R.id.home_pick_food_form_lv);
                                    FoodListAdapter adapterPick = new FoodListAdapter(getContext(), foodPick);
                                    lvPickForm.setAdapter(adapterPick);

                                    final AlertDialog alertDialogPick = pickDialog.create();

                                    lvPickForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            FoodItem foodPicked = foodItems.get(position);
                                            addFoodOnActiveList(foodPicked);

                                            FoodListAdapter adapter = (FoodListAdapter) lvFood.getAdapter();
                                            adapter.notifyDataSetChanged();
                                            UTIL.setListViewHeightBasedOnChildren(lvFood);
                                            getDataForCategory();

                                            // add to database
                                            // TODO: not that good logic
                                            new AsyncTask<FoodItem, Void, Long>() {

                                                @Override
                                                protected Long doInBackground(FoodItem... foodItems) {
                                                    long maxId = AppDatabase.getInstance(getContext()).foodItemDao().getMaxId();
                                                    foodItems[0].setId(maxId + 1);
                                                    foodItems[0].setType(getCurrentFoodType());
                                                    foodItems[0].setDate(MainActivity.TODAY);

                                                    long id = AppDatabase.getInstance(getContext()).foodItemDao().insertFoodItem(foodItems[0]);

                                                    for (Ingredient i : foodItems[0].getIngredients()) {
                                                        i.setFoodId(foodItems[0].getId());
                                                        // add ingredient to db
                                                        AppDatabase.getInstance(getContext()).ingredientDao().insertIngredient(i);
                                                    }

                                                    return id;
                                                }
                                            }.execute(foodPicked);

                                            alertDialogPick.dismiss();
                                        }
                                    });

                                    pickDialog.setNegativeButton(getString(R.string.dialog_negative_button_label), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    progressBar.setVisibility(View.VISIBLE);


                                    alertDialogPick.show();
                                }
                            }.execute();
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
            @SuppressLint("StaticFieldLeak")
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

    private void getDataForCategory() {
        int[] data = new int[4];
        // Get total calories for breakfast
        for (FoodItem f : foodBreakfast) {
            data[0] += f.getTotalCalories();
        }
        // Get total calories for lunch
        for (FoodItem f : foodLunch) {
            data[1] += f.getTotalCalories();
        }
        // Get total calories for dinner
        for (FoodItem f : foodDinner) {
            data[2] += f.getTotalCalories();
        }
        // Get total calories for snacks
        for (FoodItem f : foodSnacks) {
            data[3] += f.getTotalCalories();
        }

        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(
                new SliceValue(
                        data[0],
                        getResources().getColor(R.color.colorBreakfast)
                ).setLabel(getString(R.string.home_btn_subsection_food_breakfast))
        );
        pieData.add(
                new SliceValue(
                        data[1],
                        getResources().getColor(R.color.colorLunch)
                ).setLabel(getString(R.string.home_btn_subsection_food_lunch)));
        pieData.add(
                new SliceValue(
                        data[2],
                        getResources().getColor(R.color.colorDinner)
                ).setLabel(getString(R.string.home_btn_subsection_food_dinner)));
        pieData.add(
                new SliceValue(
                        data[3],
                        getResources().getColor(R.color.colorSnacks)
                ).setLabel(getString(R.string.home_btn_subsection_food_snacks)));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1(getString(R.string.home_chart_category_text)).setCenterText1FontSize(9);

        pieChartCategory.setPieChartData(pieChartData);
        pieChartCategory.invalidate();
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

                List<SliceValue> pieData = new ArrayList<>();
                pieData.add(
                        new SliceValue(
                                totalDataOverall[0],
                                getResources().getColor(R.color.colorCarbohydrates)
                        ).setLabel(getString(R.string.add_food_ingredients_create_carbohydrates_label))
                );
                pieData.add(
                        new SliceValue(
                                totalDataOverall[1],
                                getResources().getColor(R.color.colorFat)
                        ).setLabel(getString(R.string.add_food_ingredients_create_fat_label)));
                pieData.add(
                        new SliceValue(
                                totalDataOverall[2],
                                getResources().getColor(R.color.colorProtein)
                        ).setLabel(getString(R.string.add_food_ingredients_create_protein_label)));

                PieChartData pieChartData = new PieChartData(pieData);
                pieChartData.setHasLabels(true).setValueLabelTextSize(11);
                pieChartData.setHasCenterCircle(true).setCenterText1(getString(R.string.home_chart_total_description)).setCenterText1FontSize(9);

                pieChartTotal.setPieChartData(pieChartData);
                pieChartTotal.invalidate();
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


        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(
                new SliceValue(
                        totalDataToday[0],
                        getResources().getColor(R.color.colorCarbohydrates)
                ).setLabel(getString(R.string.add_food_ingredients_create_carbohydrates_label))
        );
        pieData.add(
                new SliceValue(
                        totalDataToday[1],
                        getResources().getColor(R.color.colorFat)
                ).setLabel(getString(R.string.add_food_ingredients_create_fat_label)));
        pieData.add(
                new SliceValue(
                        totalDataToday[2],
                        getResources().getColor(R.color.colorProtein)
                ).setLabel(getString(R.string.add_food_ingredients_create_protein_label)));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1(getString(R.string.home_chart_today_description)).setCenterText1FontSize(9);

        pieChartToday.setPieChartData(pieChartData);
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
                getDataForCategory();
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
