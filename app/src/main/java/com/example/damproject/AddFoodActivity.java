package com.example.damproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.damproject.adapters.IngredientListAdapter;
import com.example.damproject.db.AppDatabase;
import com.example.damproject.fragments.HomeFragment;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.db.model.Ingredient;
import com.example.damproject.util.UTIL;

import java.util.ArrayList;
import java.util.List;

public class AddFoodActivity extends AppCompatActivity {

    private class InsertFood extends AsyncTask<FoodItem, Void, Long> {

        @Override
        protected Long doInBackground(FoodItem... foodItems) {
            return AppDatabase.getInstance(getApplicationContext()).foodItemDao().insertFoodItem(foodItems[0]);
        }
    }

    private class InsertIngredient extends AsyncTask<Ingredient, Void, Long> {

        @Override
        protected Long doInBackground(Ingredient... ingredients) {
            return AppDatabase.getInstance(getApplicationContext()).ingredientDao().insertIngredient(ingredients[0]);
        }
    }

    public final static String NEW_FOOD_KEY = "new.food.key";

    public final static int REQUEST_PICTURE_CAPTURE = 0;
    public final static int REQUEST_PICTURE_CHOOSE = 1;

    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private String errorMessage;

    // UI components
    private EditText etFoodName;
    private Button btnCreateIngredient;
    private Button btnChooseIngredient;
    private Button btnAddIngredient;
    private Button btnSubmit;
    private Button btnCancel;
    private Button btnPicture;
    private ImageView imgFood;
    private EditText etIngredientName;
    private EditText etIngredientCalories;
    private EditText etIngredientCarbohydrates;
    private EditText etIngredientFats;
    private EditText etIngredientProteins;
    private ListView lvIngredients;
    private Spinner spnIngredients;

    private View ingredientsChoose;
    private View ingredientsCreate;

    private TextView tvInputError;
    private TextView tvIngredientInputError;
    private List<View> invalidInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initComponents();
    }

    private void initComponents() {
        etFoodName = findViewById(R.id.add_food_et_name);
        btnCreateIngredient = findViewById(R.id.add_food_btn_create_ingredient);
        btnChooseIngredient = findViewById(R.id.add_food_btn_choose_ingredient);
        btnAddIngredient = findViewById(R.id.add_food_btn_add_ingredient);
        btnSubmit = findViewById(R.id.add_food_btn_submit);
        btnCancel = findViewById(R.id.add_food_btn_cancel);
        btnPicture = findViewById(R.id.add_food_input_picture_btn);
        imgFood = findViewById(R.id.add_food_input_picture_img);
        etIngredientName = findViewById(R.id.add_food_ingredient_et_name);
        etIngredientCalories = findViewById(R.id.add_food_ingredient_et_calories);
        etIngredientCarbohydrates = findViewById(R.id.add_food_ingredient_et_carbohydrates);
        etIngredientFats = findViewById(R.id.add_food_ingredient_et_fats);
        etIngredientProteins = findViewById(R.id.add_food_ingredient_et_proteins);
        lvIngredients = findViewById(R.id.add_food_lv_ingredients);
        ingredientsChoose = findViewById(R.id.add_food_input_ingredients_choose);
        ingredientsCreate = findViewById(R.id.add_food_input_ingredients_create);
        spnIngredients = findViewById(R.id.add_food_spn_ingredients);
        tvInputError = findViewById(R.id.add_food_input_error);
        tvIngredientInputError = findViewById(R.id.add_food_input_ingredients_error);

        hideErrors();
        setSpnIngredients();

        IngredientListAdapter listAdapter = new IngredientListAdapter(getApplicationContext(), ingredients);
        lvIngredients.setAdapter(listAdapter);
        UTIL.setListViewHeightBasedOnChildren(lvIngredients);

        deActivateButtons();
        setButtonActive(btnCreateIngredient.getId());

        deActiveateMods();
        setActiveMode();


        btnChooseIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivateButtons();
                deActiveateMods();

                setButtonActive(btnChooseIngredient.getId());
                setActiveMode();
            }
        });
        btnCreateIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivateButtons();
                deActiveateMods();

                setButtonActive(btnCreateIngredient.getId());
                setActiveMode();
            }
        });

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getActiveButton()) {
                    case 1:
                        if (validateIngredient()) {
                            Ingredient i = createIngredientFromView();

                            ingredients.add(i);

                            // add ingredient to db
                            new InsertIngredient().execute(i);

                            clearIngredientForm();
                            Log.d("ADDINGREDIENT", "I am here!");
                            IngredientListAdapter adapter = (IngredientListAdapter) lvIngredients.getAdapter();
                            adapter.notifyDataSetChanged();
                            UTIL.setListViewHeightBasedOnChildren(lvIngredients);
                        } else {
                            showIngredientErrors();
                        }
                        break;
                    case 2: break;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFood()) {
                    FoodItem foodItem = createFoodFromView();

                    // insert to db
                    new InsertFood().execute(foodItem);

                    Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(NEW_FOOD_KEY, foodItem);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showFoodErrors();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this);
                builder.setTitle("Choose a picture for your food");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (options[which].equals("Take Photo")) {
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_PICTURE_CAPTURE);
                        } else if (options[which].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_PICTURE_CHOOSE);
                        } else if (options[which].equals("Cancel")) {
                            dialog.dismiss();
                        }

                    }
                });

                builder.show();
            }
        });
    }

    private void setSpnIngredients() {
        // TODO: do async
        List<Ingredient> ingredients = AppDatabase.getInstance(getApplicationContext()).ingredientDao().getAllIngredients();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, ingredients);
        spnIngredients.setAdapter(spinnerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_PICTURE_CAPTURE:
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    imgFood.setImageBitmap(selectedImage);
                    break;
                case REQUEST_PICTURE_CHOOSE:
                    Uri uriSelectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    if (uriSelectedImage != null) {
                        Cursor cursor = getContentResolver().query(uriSelectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);

                            imgFood.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                            cursor.close();
                        }
                    }
                    break;
            }
        }
    }

    private void hideErrors() {
        tvIngredientInputError.setVisibility(View.GONE);
        tvInputError.setVisibility(View.GONE);

        for (View v : invalidInputs) {
            v.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void highlightInvalidInputs() {
        for (View v : invalidInputs) {
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private void showFoodErrors() {
        tvInputError.setText(errorMessage);
        tvInputError.setVisibility(View.VISIBLE);
        highlightInvalidInputs();
    }

    private void showIngredientErrors() {
        tvIngredientInputError.setText(errorMessage);
        tvIngredientInputError.setVisibility(View.VISIBLE);
        highlightInvalidInputs();
    }

    private FoodItem createFoodFromView() {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(etFoodName.getText().toString());
        foodItem.setIngredients(ingredients);

        return foodItem;
    }

    private Ingredient createIngredientFromView() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(etIngredientName.getText().toString());
        ingredient.setCalories(Integer.parseInt(etIngredientCalories.getText().toString()));
        ingredient.setCarbohydrates(Integer.parseInt(etIngredientCarbohydrates.getText().toString()));
        ingredient.setFats(Integer.parseInt(etIngredientFats.getText().toString()));
        ingredient.setProteins(Integer.parseInt(etIngredientProteins.getText().toString()));

        return ingredient;
    }

    private void clearIngredientForm() {
        etIngredientName.setText(getString(R.string.empty_string));
        etIngredientCalories.setText(getString(R.string.empty_string));
        etIngredientCarbohydrates.setText(getString(R.string.empty_string));
        etIngredientFats.setText(getString(R.string.empty_string));
        etIngredientProteins.setText(getString(R.string.empty_string));

        tvIngredientInputError.setVisibility(View.GONE);

        if (errorMessage.length() > 0) {
            errorMessage = "";
        }

        if (invalidInputs.size() > 0) {
            invalidInputs.clear();
        }
    }

    private boolean validateIngredient() {
        boolean ok = true;
        errorMessage = "";
        hideErrors();
        invalidInputs.clear();

        /*
            * Check ingredient NAME
            * not empty
            * between 3 - 15 characters
        */
        if (etIngredientName.getText().toString().length() == 0) {
            errorMessage += "Ingredient NAME is required";
            if (!invalidInputs.contains(etIngredientName)) {
                invalidInputs.add(etIngredientName);
            }
        }
        if (etIngredientName.getText().toString().length() < 3 || etIngredientName.getText().toString().length() > 15) {
            errorMessage += "Ingredient NAME should be between 3 and 15 characters";
            if (!invalidInputs.contains(etIngredientName)) {
                invalidInputs.add(etIngredientName);
            }
        }
        /*
            * Check ingredient CALORIES
            * not empty
        */
        if (etIngredientCalories.getText().toString().length() == 0) {
            errorMessage += "Ingredient CALORIES is required";
            if (!invalidInputs.contains(etIngredientCalories)) {
                invalidInputs.add(etIngredientCalories);
            }
        }
        /*
            * Check ingredient CARBOHYDRATES
            * not empty
            * doesn't exceed no. of calories
        */
        if (etIngredientCarbohydrates.getText().toString().length() == 0) {
            errorMessage += "Ingredient CARBOHYDRATES is required";
            if (!invalidInputs.contains(etIngredientCarbohydrates)) {
                invalidInputs.add(etIngredientCarbohydrates);
            }
        }
        int nCalories = 0;
        try {
            nCalories = Integer.parseInt(etIngredientCalories.getText().toString());
        } catch (Exception e) {

        }
        int nCarbohydrates = 0;
        try {
            nCarbohydrates = Integer.parseInt(etIngredientCarbohydrates.getText().toString());
        } catch (Exception e) {

        }
        if (nCarbohydrates > nCalories) {
            errorMessage += "Ingredient CARBOHYDRATES exceeds CALORIES";
            if (!invalidInputs.contains(etIngredientCarbohydrates)) {
                invalidInputs.add(etIngredientCarbohydrates);
            }
        }
        /*
            * Check ingredient FATS
            * not empty
            * doesn't exceed no. of calories
        */
        if (etIngredientFats.getText().toString().length() == 0) {
            errorMessage += "Ingredient FATS is required";
            if (!invalidInputs.contains(etIngredientFats)) {
                invalidInputs.add(etIngredientFats);
            }
        }
        int nFats = 0;
        try {
            nFats = Integer.parseInt(etIngredientFats.getText().toString());
        } catch (Exception e) {

        }
        if (nCarbohydrates > nCalories) {
            errorMessage += "Ingredient FATS exceeds CALORIES";
            if (!invalidInputs.contains(etIngredientFats)) {
                invalidInputs.add(etIngredientFats);
            }
        }
        /*
            * Check ingredient PROTEINS
            * not empty
            * doesn't exceed no. of calories
        */
        if (etIngredientProteins.getText().toString().length() == 0) {
            errorMessage += "Ingredient PROTEINS is required";
            if (!invalidInputs.contains(etIngredientProteins)) {
                invalidInputs.add(etIngredientProteins);
            }
        }
        int nProteins = 0;
        try {
            nProteins = Integer.parseInt(etIngredientProteins.getText().toString());
        } catch (Exception e) {

        }
        if (nCarbohydrates > nCalories) {
            errorMessage += "Ingredient PROTEINS exceeds CALORIES";
            if (!invalidInputs.contains(etIngredientProteins)) {
                invalidInputs.add(etIngredientProteins);
            }
        }
        /*
            * Check ingredient MACRO's SUM
            * doesn't exceed/is less than no. of calories
        */
        int macroSum = nCarbohydrates + nFats + nProteins;
        if (macroSum != nCalories) {
            if (!invalidInputs.contains(etIngredientCalories)) {
                invalidInputs.add(etIngredientCalories);
            }
            if (!invalidInputs.contains(etIngredientCarbohydrates)) {
                invalidInputs.add(etIngredientCarbohydrates);
            }
            if (!invalidInputs.contains(etIngredientFats)) {
                invalidInputs.add(etIngredientFats);
            }
            if (!invalidInputs.contains(etIngredientProteins)) {
                invalidInputs.add(etIngredientProteins);
            }
        }
        if (macroSum > nCalories) {
            errorMessage += "Ingredients MACRO NUTRIENTS exceeds CALORIES";
        } else if (macroSum < nCalories) {
            errorMessage += "Ingredients MACRO NUTRIENTS exceeds CALORIES";
        }

        if (errorMessage.length() > 0) {
            ok = false;
        }
        return ok;
    }

    private boolean validateFood() {
        boolean ok = true;
        errorMessage = "";
        hideErrors();
        invalidInputs.clear();

        /*
            * Check NAME
            * not empty
        */
        if (etFoodName.length() == 0) {
            errorMessage += "Food NAME field is required";
            invalidInputs.add(etFoodName);
        }
        /*
            * Check INGREDIENTS LIST
            * not empty
        */
        if (ingredients.size() == 0) {
            errorMessage += "Please add at least 1 ingredient";
            invalidInputs.add(findViewById(R.id.add_food_input_ingredients));
        }


        if (errorMessage.length() > 0) {
            ok = false;
        }
        return ok;
    }

    private int getActiveButton() {
        int n = -1;
        if (!btnCreateIngredient.isEnabled())
            n = 1;
        else if (!btnChooseIngredient.isEnabled())
            n = 2;
        return n;
    }

    private void deActiveateMods() {
        ingredientsChoose.setVisibility(View.GONE);
        ingredientsCreate.setVisibility(View.GONE);
    }

    private void setActiveMode() {
        if (!btnCreateIngredient.isEnabled())
            ingredientsCreate.setVisibility(View.VISIBLE);
        else if (!btnChooseIngredient.isEnabled())
            ingredientsChoose.setVisibility(View.VISIBLE);
    }

    private void setButtonActive(int id) {
        Button button = findViewById(id);
        button.setTextColor(getResources().getColor(R.color.colorButton));
        button.setEnabled(false);
    }

    private void deActivateButtons() {
        btnChooseIngredient.setEnabled(true);
        btnChooseIngredient.setTextColor(getResources().getColor(R.color.colorAccent));
        btnCreateIngredient.setEnabled(true);
        btnCreateIngredient.setTextColor(getResources().getColor(R.color.colorAccent));
    }


}
