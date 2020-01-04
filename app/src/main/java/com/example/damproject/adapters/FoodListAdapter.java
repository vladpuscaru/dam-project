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

import com.example.damproject.MainActivity;
import com.example.damproject.R;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.db.model.Ingredient;


import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodListAdapter extends ArrayAdapter<FoodItem> {

    private ArrayList<FoodItem> data;
    private Context context;

    // UI Components
    private static class ViewHolder {
//        private ImageView imgFood;
        private TextView tvFoodTitle;
        private TextView tvFoodCalories;
        private TextView tvFoodCarbohydrates;
        private TextView tvFoodFats;
        private TextView tvFoodProteins;

        private TextView tvRegisteredOn;
        private TextView tvServedAt;
        private TextView tvIngredients;
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
            viewHolder.tvRegisteredOn = convertView.findViewById(R.id.adapter_food_tv_registeredOn);
            viewHolder.tvServedAt = convertView.findViewById(R.id.adapter_food_tv_servedAt);
            viewHolder.tvIngredients = convertView.findViewById(R.id.adapter_food_tv_ingredients);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tvFoodTitle.setText(current.getName());
        viewHolder.tvFoodCalories.setText(String.format(Locale.US, "%d calories", current.getTotalCalories()));

        String strDate = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US).format(current.getDate());
        viewHolder.tvRegisteredOn.setText(strDate);

        viewHolder.tvServedAt.setText(current.getType());

        StringBuilder strIngredients = new StringBuilder();
        for (Ingredient i : current.getIngredients()) {
            strIngredients.append(i.getName()).append(", ");
        }
        strIngredients.delete(strIngredients.length() - 2, strIngredients.length() - 1);
        viewHolder.tvIngredients.setText(strIngredients.toString());

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

        return convertView;
    }
}
