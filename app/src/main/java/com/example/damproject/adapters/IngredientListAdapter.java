package com.example.damproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.damproject.R;
import com.example.damproject.util.Ingredient;

import java.util.ArrayList;
import java.util.Locale;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> {

    private ArrayList<Ingredient> data;
    private Context context;

    // UI Components
    private class ViewHolder {
        private TextView tvName;
        private TextView tvCalories;
        private TextView tvCarbohydrates;
        private TextView tvFats;
        private TextView tvProteins;
    }

    public IngredientListAdapter(@NonNull Context context, ArrayList<Ingredient> data) {
        super(context, R.layout.adapter_ingredient_item,data);
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ingredient current = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_ingredient_item, parent, false);
            viewHolder.tvName = convertView.findViewById(R.id.adapter_ingredient_name);
            viewHolder.tvCalories = convertView.findViewById(R.id.adapter_ingredient_calories);
            viewHolder.tvCarbohydrates = convertView.findViewById(R.id.adapter_ingredient_carbohydrates);
            viewHolder.tvFats = convertView.findViewById(R.id.adapter_ingredient_fat);
            viewHolder.tvProteins = convertView.findViewById(R.id.adapter_ingredient_protein);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(String.format(Locale.US, "%s", current.getName()));
        viewHolder.tvCalories.setText(String.format(Locale.US, "%d g", current.getCalories()));
        viewHolder.tvCarbohydrates.setText(String.format(Locale.US, "%d g", current.getCarbohydrates()));
        viewHolder.tvFats.setText(String.format(Locale.US, "%d g", current.getFats()));
        viewHolder.tvProteins.setText(String.format(Locale.US, "%d g", current.getProteins()));

        return convertView;
    }
}
