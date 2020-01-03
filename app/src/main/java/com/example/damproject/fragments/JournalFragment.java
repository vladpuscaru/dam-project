package com.example.damproject.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.MainActivity;
import com.example.damproject.R;
import com.example.damproject.adapters.FoodListAdapter;
import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.util.UTIL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalFragment extends Fragment {

    private ArrayList<FoodItem> result = new ArrayList<>();

    // UI Components
    private EditText etFoodName;
    private EditText etIngredients;
    private EditText etDate;
    private CheckBox cbFoodName;
    private CheckBox cbIngredients;
    private CheckBox cbDate;
    private Button btnSearch;
    private TextView tvFoodName;
    private TextView tvIngredients;
    private TextView tvDate;
    private TextView tvResultEmpty;

    private ListView lvResult;

    private ProgressBar progressBar;
    private ProgressBar pbSave;

    private FloatingActionButton fabSave;


    public JournalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        initComponents(view);

        return view;
    }

    private void initComponents(View view) {
        etFoodName = view.findViewById(R.id.journal_et_food_name);
        etIngredients = view.findViewById(R.id.journal_et_ingredients);
        etDate = view.findViewById(R.id.journal_et_date);
        cbFoodName = view.findViewById(R.id.journal_cb_food_name);
        cbIngredients = view.findViewById(R.id.journal_cb_ingredients);
        cbDate = view.findViewById(R.id.journal_cb_date);
        btnSearch = view.findViewById(R.id.journal_btn_search);
        tvFoodName = view.findViewById(R.id.journal_tv_label_food_name);
        tvIngredients = view.findViewById(R.id.journal_tv_label_ingredients);
        tvDate = view.findViewById(R.id.journal_tv_label_date);
        lvResult = view.findViewById(R.id.journal_lv_result);
        progressBar = view.findViewById(R.id.journal_pb);
        tvResultEmpty = view.findViewById(R.id.journal_tv_resultEmpty);
        fabSave = view.findViewById(R.id.journal_fab_save);
        pbSave = view.findViewById(R.id.journal_pb_save);

        pbSave.setVisibility(View.GONE);

        fabSave.hide();

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
            }
        });

        FoodListAdapter adapter = new FoodListAdapter(getContext(), result);
        lvResult.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                int checked = getCheckedInput();
                result.clear();

                switch (checked) {
                    case 0:
                        new AsyncTask<Void, Void, List<FoodItem>>() {
                            @Override
                            protected List<FoodItem> doInBackground(Void... voids) {
                                return AppDatabase.getInstance(getContext()).foodItemDao().getAllFoodItems();
                            }

                            @Override
                            protected void onPostExecute(List<FoodItem> foodItems) {
                                result.addAll(foodItems);
                                FoodListAdapter adapter = (FoodListAdapter) lvResult.getAdapter();
                                adapter.notifyDataSetChanged();
                                checkResultEmpty();
                                progressBar.setVisibility(View.GONE);
                            }
                        }.execute();
                        break;
                    case 1:
                        String foodName = etFoodName.getText().toString();

                        new AsyncTask<String, Void, List<FoodItem>>() {
                            @Override
                            protected List<FoodItem> doInBackground(String... strings) {
                                return AppDatabase.getInstance(getContext()).foodItemDao().getFoodItemsByName(strings[0]);
                            }

                            @Override
                            protected void onPostExecute(List<FoodItem> foodItems) {
                                result.addAll(foodItems);
                                FoodListAdapter adapter = (FoodListAdapter) lvResult.getAdapter();
                                adapter.notifyDataSetChanged();
                                checkResultEmpty();
                                progressBar.setVisibility(View.GONE);
                            }
                        }.execute(foodName);
                        break;
                    case 2:
                        String ingredients = etIngredients.getText().toString();

                        new AsyncTask<String, Void, List<FoodItem>>() {
                            @Override
                            protected List<FoodItem> doInBackground(String... strings) {
                                List<FoodItem> result = new ArrayList<>();
                                String[] iNames = strings[0].split(",");

                                for (String iName : iNames) {
                                    List<FoodItem> partialResult = AppDatabase
                                            .getInstance(getContext())
                                            .foodItemDao()
                                            .getFoodItemsByIngredientName(iName.trim());
                                    for (int i = 0; i < partialResult.size(); i++) {
                                        FoodItem current = partialResult.get(i);
                                        FoodItem rCurrent = null;
                                        if (result.size() > i) {
                                            rCurrent = result.get(i);
                                        }

                                        if (rCurrent == null) {
                                            result.add(current);
                                        } else if (!current.getName().equalsIgnoreCase(rCurrent.getName())) {
                                            result.add(current);
                                        }
                                    }
                                }

                                return result;
                            }

                            @Override
                            protected void onPostExecute(List<FoodItem> foodItems) {
                                result.addAll(foodItems);
                                FoodListAdapter adapter = (FoodListAdapter) lvResult.getAdapter();
                                adapter.notifyDataSetChanged();
                                checkResultEmpty();
                                progressBar.setVisibility(View.GONE);
                            }
                        }.execute(ingredients);
                        break;
                    case 3:
                        String strDate = etDate.getText().toString();
                        Date date = null;

                        try {
                            date = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US).parse(strDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (date == null) {
                            activateErrorUI();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            new AsyncTask<Date, Void, List<FoodItem>>() {
                                @Override
                                protected List<FoodItem> doInBackground(Date... dates) {
                                    return AppDatabase.getInstance(getContext()).foodItemDao().getFoodItemsByDate(dates[0]);
                                }

                                @Override
                                protected void onPostExecute(List<FoodItem> foodItems) {
                                    result.addAll(foodItems);
                                    FoodListAdapter adapter = (FoodListAdapter) lvResult.getAdapter();
                                    adapter.notifyDataSetChanged();
                                    checkResultEmpty();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }.execute(date);
                        }

                        break;
                }
            }
        });

        cbFoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unCheckOthers(cbFoodName);
                deActivateFields();
                activateField();
            }
        });

        cbIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unCheckOthers(cbIngredients);
                deActivateFields();
                activateField();
            }
        });

        cbDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unCheckOthers(cbDate);
                deActivateFields();
                activateField();
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void saveToFile() {
        // Input pop up for file name
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.journal_dialog_title));

        final EditText etFileName = new EditText(getContext());
        etFileName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        etFileName.setText(getString(R.string.journal_default_file_name));

        dialog.setView(etFileName);

        dialog.setPositiveButton(getString(R.string.dialog_positive_button_label), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fabSave.hide();
                pbSave.setVisibility(View.VISIBLE);

                final String fileName = etFileName.getText().toString() + ".csv";

                new AsyncTask<List<FoodItem>, Integer, Void>() {
                    @Override
                    protected Void doInBackground(List<FoodItem>... lists) {
                        StringBuilder data = new StringBuilder();
                        // Nr. crt.
                        data.append(getString(R.string.csv_column_1_label)).append(",");
                        // Registered On
                        data.append(getString(R.string.csv_column_2_label)).append(",");
                        // Name
                        data.append(getString(R.string.csv_column_3_label)).append(",");
                        // Total Calories
                        data.append(getString(R.string.csv_column_4_label)).append(",");
                        // Carbohydrates
                        data.append(getString(R.string.csv_column_5_label)).append(",");
                        // Fats
                        data.append(getString(R.string.csv_column_6_label)).append(",");
                        // Proteins
                        data.append(getString(R.string.csv_column_7_label)).append(",");
                        // Served At
                        data.append(getString(R.string.csv_column_8_label)).append("\n");

                        for (int i = 0; i < result.size(); i++) {
                            FoodItem current = result.get(i);

                            data.append(String.valueOf(i + 1)).append(",");
                            String strDate = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US).format(current.getDate());
                            data.append(strDate).append(",");
                            data.append(current.getName()).append(",");
                            data.append(current.getTotalCalories()).append(",");
                            data.append(current.getTotalCarbohydrates()).append(",");
                            data.append(current.getTotalFats()).append(",");
                            data.append(current.getTotalProteins()).append(",");
                            data.append(current.getType()).append("\n");

                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            publishProgress((int) ((float) i / result.size() * 100));
                        }

                        try {
                            // Save the file into the device
                            FileOutputStream out = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            out.write((data.toString()).getBytes());
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        pbSave.setProgress(values[0]);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        pbSave.setVisibility(View.GONE);
                        Toast.makeText(getContext(), getString(R.string.csv_save_done) + " " + fileName, Toast.LENGTH_LONG).show();
                    }
                }.execute(result);
            }
        });

        dialog.setNegativeButton(getString(R.string.dialog_negative_button_label), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void activateErrorUI() {
        Toast.makeText(getContext(), R.string.error_date_format, Toast.LENGTH_LONG).show();
        checkResultEmpty();
    }

    private void checkResultEmpty() {
        if (result.isEmpty()) {
            tvResultEmpty.setVisibility(View.VISIBLE);
            fabSave.hide();
        } else {
            tvResultEmpty.setVisibility(View.GONE);
            fabSave.show();
        }
    }

    private int getCheckedInput() {
        if (cbFoodName.isChecked()) {
            return 1;
        } else if (cbIngredients.isChecked()) {
            return 2;
        } else if (cbDate.isChecked()) {
            return 3;
        } else {
            return 0;
        }
    }

    private void unCheckOthers(CheckBox cb) {
        if (cbFoodName.getId() != cb.getId()) {
            cbFoodName.setChecked(false);
        }
        if (cbIngredients.getId() != cb.getId()) {
            cbIngredients.setChecked(false);
        }
        if (cbDate.getId() != cb.getId()) {
            cbDate.setChecked(false);
        }
    }

    private void deActivateFields() {
        tvFoodName.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvIngredients.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etFoodName.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etIngredients.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etDate.setTextColor(getResources().getColor(android.R.color.darker_gray));

        btnSearch.setText(getString(R.string.journal_btn_submit_label_no_fields));
    }

    private void activateField() {
        if (cbFoodName.isChecked()) {
            tvFoodName.setTextColor(getResources().getColor(android.R.color.black));
            etFoodName.setTextColor(getResources().getColor(android.R.color.black));
        } else if (cbIngredients.isChecked()) {
            tvIngredients.setTextColor(getResources().getColor(android.R.color.black));
            etIngredients.setTextColor(getResources().getColor(android.R.color.black));
        } else if (cbDate.isChecked()) {
            tvDate.setTextColor(getResources().getColor(android.R.color.black));
            etDate.setTextColor(getResources().getColor(android.R.color.black));
        }

        btnSearch.setText(getString(R.string.journal_btn_submit_label));
    }
}
