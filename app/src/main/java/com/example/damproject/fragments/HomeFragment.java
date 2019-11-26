package com.example.damproject.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.damproject.db.model.Ingredient;
import com.example.damproject.db.model.Menu;
import com.example.damproject.util.MenuType;
import com.example.damproject.util.UTIL;
import com.example.damproject.db.model.User;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private class InsertMenu extends AsyncTask<Menu, Void, Long> {

        @Override
        protected Long doInBackground(Menu... menus) {
            return AppDatabase.getInstance(getContext()).menuDao().insertMenu(menus[0]);
        }
    }

    public final static String EDIT_USER_KEY = "edit.user.key";
    public final static int EDIT_USER_REQUEST = 90;
    public final static int ADD_FOOD_REQUEST = 80;

    private User loggedUser;
    private ArrayList<FoodItem> foodBreakfast = new ArrayList<>();
    private ArrayList<FoodItem> foodLunch = new ArrayList<>();
    private ArrayList<FoodItem> foodDinner = new ArrayList<>();
    private ArrayList<FoodItem> foodSnacks = new ArrayList<>();

    //    UI Components
    private ImageView ivUserImg;
    private TextView tvUserName;
    private TextView tvUserAge;
    private TextView tvUserWeight;
    private TextView tvUserHeight;
    private Button btnUserEdit;

    private Button btnAddFood;
    private Button btnListBreakfast;
    private Button btnListLunch;
    private Button btnListDinner;
    private Button btnListSnacks;
    private ListView lvFood;

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
        loggedUser = getArguments().getParcelable(LoginActivity.USER_KEY);


        ivUserImg = view.findViewById(R.id.home_img_user);
        tvUserName = view.findViewById(R.id.home_tv_user_name);
        tvUserAge = view.findViewById(R.id.home_tv_user_age);
        tvUserWeight = view.findViewById(R.id.home_tv_user_weight);
        tvUserHeight = view.findViewById(R.id.home_tv_user_height);
        btnUserEdit = view.findViewById(R.id.home_btn_user_edit);
        lvFood = view.findViewById(R.id.home_lv_subsection_food);
        btnAddFood = view.findViewById(R.id.home_btn_subsection_food_add_button);
        btnListBreakfast = view.findViewById(R.id.home_btn_subsection_food_breakfast);
        btnListLunch = view.findViewById(R.id.home_btn_subsection_food_lunch);
        btnListDinner = view.findViewById(R.id.home_btn_subsection_food_dinner);
        btnListSnacks = view.findViewById(R.id.home_btn_subsection_food_snacks);

        deActivateButtons();
        setActiveButton(view, btnListBreakfast.getId());

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
                final CharSequence[] options = {"Create a new food item", "Choose from Database"};

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Add to your menu");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals("Create a new food item")) {
                            Intent intent = new Intent(view.getContext(), AddFoodActivity.class);
                            startActivityForResult(intent, ADD_FOOD_REQUEST);
                        } else if (options[which].equals("Choose from Database")) {

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_FOOD_REQUEST) {
                Bundle bundle = data.getExtras();
                FoodItem foodItem = bundle.getParcelable(AddFoodActivity.NEW_FOOD_KEY);

                SimpleDateFormat format = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US);
                Date now = null;
                try {
                    now = format.parse(format.format(new Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Menu menu = new Menu(loggedUser.getId(),
                        foodItem.getId(),
                        MenuType.valueOf(getActiveButtonView().getText().toString()),
                        now);
                // ad menu for user
                new InsertMenu().execute(menu);

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
        String userWeight = String.format(Locale.US, "%d kg", loggedUser.getWeight());
        String userHeight = String.format(Locale.US, "%d cm", loggedUser.getHeight());

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

}
