package com.example.damproject;

import androidx.annotation.NonNull;
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
import android.os.Build;
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
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.adapters.IngredientListAdapter;
import com.example.damproject.adapters.IngredientListTextAdapter;
import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.User;
import com.example.damproject.fragments.HomeFragment;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.db.model.Ingredient;
import com.example.damproject.fragments.IngredientManagerFragment;
import com.example.damproject.util.UTIL;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    public class InsertFood extends AsyncTask<FoodItem, Void, Long> {

        @Override
        protected Long doInBackground(FoodItem... foodItems) {
            long maxId = AppDatabase.getInstance(getApplicationContext()).foodItemDao().getMaxId();
            foodItems[0].setId(maxId + 1);
            foodItems[0].setType(foodType);
            foodItems[0].setDate(MainActivity.TODAY);

            long id = AppDatabase.getInstance(getApplicationContext()).foodItemDao().insertFoodItem(foodItems[0]);

            for (Ingredient i : foodItems[0].getIngredients()) {
                i.setFoodId(foodItems[0].getId());
                // add ingredient to db
                AppDatabase.getInstance(getApplicationContext()).ingredientDao().insertIngredient(i);
            }

            return id;
        }
    }

    public final static String NEW_FOOD_KEY = "new.food.key";

    public final static int REQUEST_PICTURE_CAPTURE = 0;
    public final static int REQUEST_PICTURE_CHOOSE = 1;

    private ArrayList<String> spnIngredientsNamesList = new ArrayList<>();
    private ArrayList<Ingredient> spnIngredientsList = new ArrayList<>();

    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private String errorMessage;
    private User loggedUser;

    private DatabaseReference db;

    private String foodType;

    // UI components
    private EditText etFoodName;
    private Button btnAddIngredient;
    private Button btnSubmit;
    private Button btnCancel;
//    private Button btnPicture;
//    private ImageView imgFood;
    private EditText etIngredientName;
    private EditText etIngredientCalories;
    private EditText etIngredientCarbohydrates;
    private EditText etIngredientFats;
    private EditText etIngredientProteins;
    private ListView lvIngredients;

    private TextView tvInputError;
    private TextView tvIngredientInputError;
    private List<View> invalidInputs = new ArrayList<>();

    private Switch swIngredients;
    private Spinner spnIngredients;
    private View viewIngredientsCreate;
    private View viewIngredientsChoose;
    private TextView tvIngredientsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initComponents();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            loggedUser = bundle.getParcelable(HomeFragment.LOGGED_USER_KEY);
            foodType = bundle.getString(HomeFragment.FOOD_TYPE_KEY);
        } else {
            loggedUser = null;
            foodType = "unknown";
        }
    }

    private void initComponents() {
        db = FirebaseDatabase.getInstance().getReference(IngredientManagerFragment.INGREDIENTS_TABLE_NAME);

        etFoodName = findViewById(R.id.add_food_et_name);
        btnAddIngredient = findViewById(R.id.add_food_btn_add_ingredient);
        btnSubmit = findViewById(R.id.add_food_btn_submit);
        btnCancel = findViewById(R.id.add_food_btn_cancel);
//        btnPicture = findViewById(R.id.add_food_input_picture_btn);
//        imgFood = findViewById(R.id.add_food_input_picture_img);
        etIngredientName = findViewById(R.id.add_food_ingredient_et_name);
        etIngredientCalories = findViewById(R.id.add_food_ingredient_et_calories);
        etIngredientCarbohydrates = findViewById(R.id.add_food_ingredient_et_carbohydrates);
        etIngredientFats = findViewById(R.id.add_food_ingredient_et_fats);
        etIngredientProteins = findViewById(R.id.add_food_ingredient_et_proteins);
        lvIngredients = findViewById(R.id.add_food_lv_ingredients);
        tvInputError = findViewById(R.id.add_food_input_error);
        tvIngredientInputError = findViewById(R.id.add_food_input_ingredients_error);
        swIngredients = findViewById(R.id.add_food_input_ingredients_switch);
        spnIngredients = findViewById(R.id.add_food_input_ingredients_spn);
        viewIngredientsChoose = findViewById(R.id.add_food_input_ingredients_choose);
        viewIngredientsCreate = findViewById(R.id.add_food_input_ingredients_create);
        tvIngredientsInfo = findViewById(R.id.add_food_input_ingredients_tv_info);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item ,spnIngredientsNamesList);
        spnIngredients.setAdapter(adapter);

        swIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swIngredients.isChecked()) {
                    // Create ingredient
                    viewIngredientsChoose.setVisibility(View.GONE);
                    viewIngredientsCreate.setVisibility(View.VISIBLE);
                    tvIngredientsInfo.setText(getString(R.string.add_food_add_ingredient_description));
                    swIngredients.setText(getString(R.string.add_food_input_ingredients_switch_on_label));
                } else {
                    // Choose ingredient
                    viewIngredientsCreate.setVisibility(View.GONE);
                    viewIngredientsChoose.setVisibility(View.VISIBLE);
                    tvIngredientsInfo.setText(getString(R.string.add_food_add_ingredient_description_choose));
                    swIngredients.setText(getString(R.string.add_food_input_ingredients_switch_off_label));
                }
            }
        });

        hideErrors();

        IngredientListAdapter listAdapter = new IngredientListAdapter(getApplicationContext(), ingredients);
        lvIngredients.setAdapter(listAdapter);
        UTIL.setListViewHeightBasedOnChildren(lvIngredients);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swIngredients.isChecked()) {
                    // Ingredient was chosen
                    Ingredient ingredientToAdd = spnIngredientsList.get(spnIngredients.getSelectedItemPosition());
                    ingredients.add(ingredientToAdd);
                    IngredientListAdapter adapter = (IngredientListAdapter) lvIngredients.getAdapter();
                    adapter.notifyDataSetChanged();
                    UTIL.setListViewHeightBasedOnChildren(lvIngredients);
                } else {
                    // Ingredient was created
                    if (validateIngredient()) {
                        Ingredient i = createIngredientFromView();
                        ingredients.add(i);


                        clearIngredientForm();
                        IngredientListAdapter adapter = (IngredientListAdapter) lvIngredients.getAdapter();
                        adapter.notifyDataSetChanged();
                        UTIL.setListViewHeightBasedOnChildren(lvIngredients);
                    } else {
                        showIngredientErrors();
                    }
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFood()) {
                    FoodItem foodItem = createFoodFromView();
                    foodItem.setUserId(loggedUser.getId());
                    foodItem.setIngredients(ingredients);

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

//        btnPicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this);
//                builder.setTitle("Choose a picture for your food");
//
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        if (options[which].equals("Take Photo")) {
//                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, REQUEST_PICTURE_CAPTURE);
//                        } else if (options[which].equals("Choose from Gallery")) {
//                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(intent, REQUEST_PICTURE_CHOOSE);
//                        } else if (options[which].equals("Cancel")) {
//                            dialog.dismiss();
//                        }
//
//                    }
//                });
//
//                builder.show();
//            }
//        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spnIngredientsList.clear();
                spnIngredientsNamesList.clear();

                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    Ingredient ingredient = i.getValue(Ingredient.class);
                    spnIngredientsList.add(ingredient);
                    spnIngredientsNamesList.add(ingredient.getName());
                }

                ArrayAdapter adapter = (ArrayAdapter) spnIngredients.getAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (resultCode == RESULT_OK && data != null) {
//            switch (requestCode) {
//                case REQUEST_PICTURE_CAPTURE:
//                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                    imgFood.setImageBitmap(selectedImage);
//                    break;
//                case REQUEST_PICTURE_CHOOSE:
//                    Uri uriSelectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    if (uriSelectedImage != null) {
//                        Cursor cursor = getContentResolver().query(uriSelectedImage, filePathColumn, null, null, null);
//                        if (cursor != null) {
//                            cursor.moveToFirst();
//
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            String picturePath = cursor.getString(columnIndex);
//
//                            imgFood.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//                            cursor.close();
//                        }
//                    }
//                    break;
//            }
//        }
//    }

    private void hideErrors() {
        tvIngredientInputError.setVisibility(View.GONE);
        tvInputError.setVisibility(View.GONE);

        for (View v : invalidInputs) {
            v.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void highlightInvalidInputs() {
        for (View v : invalidInputs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setBackground(getResources().getDrawable(R.drawable.error_input_background));
            }
//            v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
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
        foodItem.setDate(new Date());

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

}
