package com.example.damproject.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.AddFoodActivity;
import com.example.damproject.LoginActivity;
import com.example.damproject.R;
import com.example.damproject.RegisterActivity;
import com.example.damproject.adapters.FoodListAdapter;
import com.example.damproject.util.FoodItem;
import com.example.damproject.util.Ingredient;
import com.example.damproject.util.User;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
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
        List<Ingredient> testIngredients = new ArrayList<>();
        testIngredients.add(new Ingredient("Bacon", 190, 70, 80, 40));
        testIngredients.add(new Ingredient("Bread", 60, 30, 10, 20));
        foodBreakfast.add(new FoodItem("Sandwich BLT", testIngredients));
        // TODO: remove this test list init

        initComponents(view);

        return view;
    }

    private void initComponents(final View view) {
        loggedUser = getArguments().getParcelable(LoginActivity.USER_KEY);

        Log.d("HOME_FRAGMENT", loggedUser.toString());


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

        String userAge = String.format(Locale.US, "%d years old", loggedUser.getAge());
        String userWeight = String.format(Locale.US, "%d kg", loggedUser.getWeight());
        String userHeight = String.format(Locale.US, "%d cm", loggedUser.getHeight());

        tvUserName.setText(loggedUser.getUsername());
        tvUserAge.setText(userAge);
        tvUserWeight.setText(userWeight);
        tvUserHeight.setText(userHeight);

        FoodListAdapter adapter = getActiveList();
        lvFood.setAdapter(adapter);

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
                Intent intent = new Intent(view.getContext(), AddFoodActivity.class);
                startActivityForResult(intent, ADD_FOOD_REQUEST);
            }
        });

        btnUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getView().getContext(), RegisterActivity.class);
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

                addFoodOnActiveList(foodItem);

                FoodListAdapter adapter = (FoodListAdapter) lvFood.getAdapter();
                adapter.notifyDataSetChanged();

//                Log.d("HOME", String.valueOf(foodBreakfast.size()));
            }

            if (requestCode == EDIT_USER_REQUEST) {
                Bundle bundle = data.getExtras();
                loggedUser = bundle.getParcelable(RegisterActivity.EDITED_USER_KEY);
            }
        }
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
    }

    private int getActiveButton() {
        int n = -1;

        if (!btnListBreakfast.isClickable())
            n = 1;
        if (!btnListLunch.isClickable())
            n = 2;
        if (!btnListDinner.isClickable())
            n = 3;
        if (!btnListSnacks.isClickable())
            n = 4;

        return n;
    }

    private void setActiveButton(@NotNull View view, int id) {
        Button active = view.findViewById(id);
        active.setClickable(false);
        active.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
    }

    private void deActivateButtons() {
        btnListBreakfast.setClickable(true);
        btnListBreakfast.setBackgroundColor(getResources().getColor(R.color.colorButton));
        btnListLunch.setClickable(true);
        btnListLunch.setBackgroundColor(getResources().getColor(R.color.colorButton));
        btnListDinner.setClickable(true);
        btnListDinner.setBackgroundColor(getResources().getColor(R.color.colorButton));
        btnListSnacks.setClickable(true);
        btnListSnacks.setBackgroundColor(getResources().getColor(R.color.colorButton));
    }

}
