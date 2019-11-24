package com.example.damproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.damproject.R;
import com.example.damproject.util.FoodItem;



import java.util.ArrayList;
import java.util.Locale;

public class FoodListAdapter extends ArrayAdapter<FoodItem> {

    private ArrayList<FoodItem> data;
    private Context context;

    // UI Components
    private static class ViewHolder {
        private ImageView imgFood;
        private TextView tvFoodTitle;
        private TextView tvFoodCalories;
        private TextView tvFoodCarbohydrates;
        private TextView tvFoodFats;
        private TextView tvFoodProteins;
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
            viewHolder.tvFoodCarbohydrates = convertView.findViewById(R.id.adapter_food_tv_carbohydrates);
            viewHolder.tvFoodFats = convertView.findViewById(R.id.adapter_food_tv_fats);
            viewHolder.tvFoodProteins = convertView.findViewById(R.id.adapter_food_tv_proteins);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tvFoodTitle.setText(current.getName());
        viewHolder.tvFoodCalories.setText(String.format(Locale.US, "%d calories", current.getTotalCalories()));

        viewHolder.tvFoodCarbohydrates.setText(String.format(Locale.US, "%d carbs", current.getTotalCarbohydrates()));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.tvFoodCarbohydrates.getLayoutParams();
        params.weight = current.getCarbohydratesPercentage();
        viewHolder.tvFoodCarbohydrates.setLayoutParams(params);

        viewHolder.tvFoodFats.setText(String.format(Locale.US, "%d fats", current.getTotalFats()));
        params = (LinearLayout.LayoutParams) viewHolder.tvFoodFats.getLayoutParams();
        params.weight = current.getFatsPercentage();
        viewHolder.tvFoodFats.setLayoutParams(params);

        viewHolder.tvFoodProteins.setText(String.format(Locale.US, "%d proteins", current.getTotalProteins()));
        params = (LinearLayout.LayoutParams) viewHolder.tvFoodProteins.getLayoutParams();
        params.weight = current.getProteinsPercentage();
        viewHolder.tvFoodProteins.setLayoutParams(params);

        Log.d("comeon", current.toString());

        return convertView;
    }
}
