package com.example.damproject.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.damproject.R;
import com.example.damproject.adapters.IngredientListAdapter;
import com.example.damproject.db.model.Ingredient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IngredientManagerFragment extends Fragment {
    public static final String INGREDIENTS_TABLE_NAME = "ingredients";

    private DatabaseReference db;
    private AlertDialog.Builder formDialog;

    private ArrayList<Ingredient> ingredientList = new ArrayList<>();

    private ArrayList<String> ingredientListText = new ArrayList<>();

    private Ingredient ingredientToBeEdited = null;

    private List<Integer> posSelected = new ArrayList<>();
    private boolean editMode = false;

    // UI Components
    private ListView lvIngredients;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabUpdate;

    // Dialog UI
    private EditText etName;
    private EditText etCalories;
    private EditText etCarbohydrates;
    private EditText etFats;
    private EditText etProteins;

    private View viewForm;


    public IngredientManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient_manager, container, false);

        setupForm(view);
        initComponents(view);

        return view;
    }

    private void setupForm(View view) {
        db = FirebaseDatabase.getInstance().getReference(INGREDIENTS_TABLE_NAME);

        formDialog = new AlertDialog.Builder(getContext());
        formDialog.setTitle(getString(R.string.ingredient_manager_add_title));

        viewForm = View.inflate(getContext(), R.layout.ingredient_manager_form, null);
        etName = viewForm.findViewById(R.id.ingredient_manager_et_name);
        etCalories = viewForm.findViewById(R.id.ingredient_manager_et_calories);
        etCarbohydrates = viewForm.findViewById(R.id.ingredient_manager_et_carbohydrates);
        etFats = viewForm.findViewById(R.id.ingredient_manager_et_fats);
        etProteins = viewForm.findViewById(R.id.ingredient_manager_et_proteins);

        formDialog.setPositiveButton(getString(R.string.dialog_positive_button_label), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (validateForm()) {
                    Ingredient ingredient = createIngredientFromView();

                    if (!editMode) {
                        // insert to db
                        String id = db.push().getKey();
                        final Ingredient ingredientToAdd = new Ingredient(id, ingredient);
                        db.child(id).setValue(ingredientToAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(
                                        getContext(),
                                        getString(R.string.ingredient_manager_ingredient_added) + " " + ingredientToAdd.getName(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        db.child(ingredientToBeEdited.getFbId()).setValue(ingredientToBeEdited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(
                                        getContext(),
                                        getString(R.string.ingredient_manager_ingredient_edited) + " " + ingredientToBeEdited.getName(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.ingredient_manager_form_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        formDialog.setNegativeButton(getString(R.string.dialog_negative_button_label), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private Ingredient createIngredientFromView() {
        String name = etName.getText().toString().trim();
        int calories = Integer.parseInt(etCalories.getText().toString().trim());
        int carbohydrates = Integer.parseInt(etCarbohydrates.getText().toString().trim());
        int fats = Integer.parseInt(etFats.getText().toString().trim());
        int proteins = Integer.parseInt(etProteins.getText().toString().trim());

        return new Ingredient(name, calories, carbohydrates, fats, proteins);
    }

    private boolean validateForm() {
        if (etName.getText().toString().length() <= 0) {
            return false;
        }

        if (etCalories.getText().toString().length() <= 0) {
            return false;
        }

        if (etCarbohydrates.getText().toString().length() <= 0) {
            return false;
        }

        if (etFats.getText().toString().length() <= 0) {
            return false;
        }

        if (etProteins.getText().toString().length() <= 0) {
            return false;
        }

        int calories = Integer.parseInt(etCalories.getText().toString().trim());
        int carbohydrates = Integer.parseInt(etCarbohydrates.getText().toString().trim());
        int fats = Integer.parseInt(etFats.getText().toString().trim());
        int proteins = Integer.parseInt(etProteins.getText().toString().trim());

        if (!(carbohydrates + fats + proteins == calories)) {
            return false;
        }

        return true;
    }

    private void initComponents(final View view) {

        lvIngredients = view.findViewById(R.id.ingredient_manager_lv);
        fabAdd = view.findViewById(R.id.ingredient_manager_fab_add);
        fabDelete = view.findViewById(R.id.ingredient_manager_fab_delete);
        fabUpdate = view.findViewById(R.id.ingredient_manager_fab_update);

        lvIngredients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lvIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean contains = false;
                for (int i = 0; i < posSelected.size(); i++) {
                    if (position == posSelected.get(i)) {
                        contains = true;
                        posSelected.remove(i);
                        break;
                    }
                }

                if (!contains) {
                    posSelected.add(position);
                    ingredientToBeEdited = ingredientList.get(position);
                }

                checkFabStatus();
            }
        });

        fabUpdate.setEnabled(false);

        updateIngredientListText();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice ,ingredientListText);
        lvIngredients.setAdapter(adapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open form for adding
                editMode = false;

                if (viewForm.getParent() != null) {
                    ((ViewGroup) viewForm.getParent()).removeView(viewForm);
                }
                formDialog.setView(viewForm);
                formDialog.show();
            }
        });

        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open form for edit
                editMode = true;

                etName.setText(ingredientToBeEdited.getName());
                etCalories.setText(String.valueOf(ingredientToBeEdited.getCalories()));
                etCarbohydrates.setText(String.valueOf(ingredientToBeEdited.getCarbohydrates()));
                etFats.setText(String.valueOf(ingredientToBeEdited.getFats()));
                etProteins.setText(String.valueOf(ingredientToBeEdited.getProteins()));

                if (viewForm.getParent() != null) {
                    ((ViewGroup) viewForm.getParent()).removeView(viewForm);
                }
                formDialog.setView(viewForm);
                formDialog.show();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < posSelected.size(); i++) {
                    final int index = posSelected.get(i);
                    db.child(ingredientList.get(index).getFbId()).setValue(null);
                }
                posSelected.clear();
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();

                for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    ingredient.setFbId(ingredientSnapshot.getKey());
                    ingredientList.add(ingredient);
                }

                updateIngredientListText();

                ArrayAdapter adapter = (ArrayAdapter) lvIngredients.getAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateIngredientListText() {
        ingredientListText.clear();

        for (Ingredient i : ingredientList) {
            StringBuilder sb = new StringBuilder(i.getName());
            sb.append(" - ").append(i.getCalories()).append(" kcal").append(" | ");
            sb.append(i.getCarbohydrates()).append(" carbs").append(" | ");
            sb.append(i.getFats()).append(" fats").append(" | ");
            sb.append(i.getProteins()).append(" pr");
            ingredientListText.add(sb.toString());
        }
    }

    private void checkFabStatus() {
        if (posSelected.size() <= 0 || posSelected.size() > 1) {
            fabUpdate.setEnabled(false);
            ingredientToBeEdited = null;
        } else {
            fabUpdate.setEnabled(true);
        }
    }

}
