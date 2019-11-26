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
import com.example.damproject.db.model.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientListTextAdapter extends ArrayAdapter<String> {

    private List<String> data;
    private Context context;

    // UI Components
    private class ViewHolder {
        private TextView tv;
    }

    public IngredientListTextAdapter(@NonNull Context context, List<String> data) {
        super(context, R.layout.adapter_ingredient_text, data);
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String current = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_ingredient_text, parent, false);
            viewHolder.tv = convertView.findViewById(R.id.adapter_text_ingredient_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv.setText(String.format(Locale.US, "-> %s", current));

        return convertView;
    }
}
