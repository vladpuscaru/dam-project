package com.example.damproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.damproject.R;
import com.example.damproject.util.FoodItem;



import java.util.ArrayList;

public class FoodListAdapter extends ArrayAdapter<FoodItem> {

    private ArrayList<FoodItem> data;
    private Context context;

    // UI Components
    private static class ViewHolder {
        private ImageView imgFood;
        private TextView tvFoodTitle;
        private TextView tvFoodCalories;
    }


    public FoodListAdapter(@NonNull Context context, ArrayList<FoodItem> data) {
        super(context, R.layout.adapter_food_item , data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FoodItem current = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_food_item, parent, false);
            viewHolder.tvFoodTitle = convertView.findViewById(R.id.adapter_food_tv_title);
            viewHolder.tvFoodCalories = convertView.findViewById(R.id.adapter_food_tv_calories);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tvFoodTitle.setText(current.getName());
        viewHolder.tvFoodCalories.setText(Integer.toString(current.getTotalCalories()));

        return convertView;
    }
}
