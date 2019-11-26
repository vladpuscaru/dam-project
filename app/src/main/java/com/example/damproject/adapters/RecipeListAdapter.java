package com.example.damproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.damproject.R;
import com.example.damproject.network.model.Recipe;
import com.example.damproject.util.UTIL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

    private List<Recipe> data;
    private Context context;

    // UI Components
    private static class ViewHolder {
        private ImageView imgRecipe;
        private TextView tvTitle;
        private TextView tvCalories;
        private TextView tvCarbohydrates;
        private TextView tvFats;
        private TextView tvProteins;
        private TextView tvTags;
        private ListView lvIngredients;
    }


    public RecipeListAdapter(@NonNull Context context, List<Recipe> data) {
        super(context, R.layout.adapter_recipe_item, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Recipe current = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_recipe_item, parent, false);
            viewHolder.imgRecipe = convertView.findViewById(R.id.adapter_recipe_img);
            viewHolder.tvTitle = convertView.findViewById(R.id.adapter_recipe_tv_title);
            viewHolder.tvCalories = convertView.findViewById(R.id.adapter_recipe_tv_calories);
            viewHolder.tvCarbohydrates = convertView.findViewById(R.id.adapter_recipe_tv_carbohydrates);
            viewHolder.tvFats = convertView.findViewById(R.id.adapter_recipe_tv_fats);
            viewHolder.tvProteins = convertView.findViewById(R.id.adapter_recipe_tv_protein);
            viewHolder.tvTags = convertView.findViewById(R.id.adapter_recipe_tv_tags);
            viewHolder.lvIngredients = convertView.findViewById(R.id.adapter_recipe_lv_ingrdients);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imgRecipe.setImageBitmap(current.getImage());

        viewHolder.tvTitle.setText(String.format(Locale.US, "%s", current.getName()));
        viewHolder.tvCalories.setText(String.format(Locale.US, "%.2f kcal", current.getCalories()));
        viewHolder.tvCarbohydrates.setText(String.format(Locale.US, "%.2f carbs", current.getCarbohydrates()));
        viewHolder.tvFats.setText(String.format(Locale.US, "%.2f fats", current.getFats()));
        viewHolder.tvProteins.setText(String.format(Locale.US, "%.2f pr", current.getProteins()));

        StringBuilder tags = new StringBuilder();
        for (String tag : current.getHealthLabels()) {
            tags.append("#").append(tag).append(" | ");
        }
        viewHolder.tvTags.setText(String.format(Locale.US, "%s", tags.toString()));

        IngredientListTextAdapter adapter = new IngredientListTextAdapter(getContext(), current.getIngredients());
        viewHolder.lvIngredients.setAdapter(adapter);
        UTIL.setListViewHeightBasedOnChildren(viewHolder.lvIngredients);

        return convertView;
    }
}
