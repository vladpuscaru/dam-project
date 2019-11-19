package com.example.damproject.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.LoginActivity;
import com.example.damproject.R;
import com.example.damproject.util.FoodItem;
import com.example.damproject.util.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private User loggedUser;
    private List<FoodItem> food = new ArrayList<>();

//    UI Components
    private ImageView ivUserImg;
    private TextView tvUserName;
    private TextView tvUserAge;
    private TextView tvUserKg;

    private Button btnAddFood;
    private Button btnListBreakfast;
    private Button btnListLunch;
    private Button btnListDinner;
    private Button btnListSnacks;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents(view);

        return view;
    }

    private void initComponents(View view) {
        loggedUser = getArguments().getParcelable(LoginActivity.USER_KEY);

        ivUserImg = view.findViewById(R.id.home_img_user);
        tvUserName = view.findViewById(R.id.home_tv_user_name);
        tvUserAge = view.findViewById(R.id.home_tv_user_age);
        tvUserKg = view.findViewById(R.id.home_tv_user_kg);

        tvUserName.setText(loggedUser.getName());
        tvUserAge.setText("22");
        tvUserKg.setText("71");
    }

}
