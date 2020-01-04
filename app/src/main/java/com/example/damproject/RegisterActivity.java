package com.example.damproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.db.AppDatabase;
import com.example.damproject.fragments.HomeFragment;
import com.example.damproject.util.InputError;
import com.example.damproject.db.model.User;
import com.example.damproject.util.UTIL;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private class UpdateUser extends AsyncTask<User, Void, Integer> {

        @Override
        protected Integer doInBackground(User... users) {
            return AppDatabase.getInstance(getApplicationContext()).userDao().updateUser(users[0]);
        }
    }

    private class InsertUser extends AsyncTask<User, Void, Long> {

        @Override
        protected Long doInBackground(User... users) {
            long maxId = AppDatabase.getInstance(getApplicationContext()).userDao().getMaxId();
            users[0].setId(maxId + 1);
            return AppDatabase.getInstance(getApplicationContext()).userDao().insertUser(users[0]);
        }
    }

    public final static int REQUEST_IMAGE_CAPTURE = 108;
    public final static String EDITED_USER_KEY = "new.user.key";
    public final static String USERNAME_KEY = "username.key";
    public final static String PASSOWRD_KEY = "password.key";

    private User loggedUser;

    private InputError inputError;
    private String weightFrom;
    private String heightFrom;

    // UI Components
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassowrd;
    private EditText etBirthday;
    private EditText etWeight;
    private EditText etHeight;
    private Button btnRegister;
    private Button btnCancel;
    private Button btnOpenDatePicker;
    private TextView tvErrorReport;

    private Button btnAvatar;
    private ImageView imgPreview;

    private Spinner spnWeight;
    private Spinner spnHeight;


    private DatePickerDialog.OnDateSetListener mDateSetListener;


    private List<EditText> etInvalid = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loggedUser = (User) bundle.getParcelable(HomeFragment.EDIT_USER_KEY);
            setCurrentUserData();
            btnRegister.setText(getString(R.string.register_btn_save));
        } else {
            loggedUser = null;
        }

    }

    private void setCurrentUserData() {
        etUsername.setText(loggedUser.getUsername());
        etPassword.setText(loggedUser.getPassword());
        String birthday = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US).format(loggedUser.getBirthday());
        etBirthday.setText(birthday);
        etWeight.setText(String.valueOf(loggedUser.getWeight()), TextView.BufferType.EDITABLE);
        etHeight.setText(String.valueOf(loggedUser.getHeight()), TextView.BufferType.EDITABLE);
        imgPreview.setImageBitmap(loggedUser.getImg());

        int size = spnWeight.getAdapter().getCount();
        for (int i = 0; i < size; i++) {
            if (spnWeight.getAdapter().getItem(i).toString().equalsIgnoreCase(loggedUser.getWeightMeasureUnit())) {
                spnWeight.setSelection(i);
            }
        }

        size = spnHeight.getAdapter().getCount();
        for (int i = 0; i < size; i++) {
            if (spnHeight.getAdapter().getItem(i).toString().equalsIgnoreCase(loggedUser.getHeightMeasureUnit())) {
                spnHeight.setSelection(i);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap image = (Bitmap) extras.get("data");
                    imgPreview.setImageBitmap(image);

                    if (loggedUser != null) {
                        loggedUser.setImg(image);
                    }
                }
            }
        }
    }

    private void initComponents() {
        etUsername = findViewById(R.id.register_et_username);
        etPassword = findViewById(R.id.register_et_password);
        etConfirmPassowrd = findViewById(R.id.register_et_confirm_password);
        etBirthday = findViewById(R.id.register_et_birthday);
        etWeight = findViewById(R.id.register_et_weight);
        etHeight = findViewById(R.id.register_et_height);
        tvErrorReport = findViewById(R.id.register_tv_error_report);
        btnRegister = findViewById(R.id.register_btn_register);
        btnCancel = findViewById(R.id.register_btn_cancel);
        btnOpenDatePicker = findViewById(R.id.register_btn_open_dp);
        spnWeight = findViewById(R.id.register_spn_weight);
        spnHeight = findViewById(R.id.register_spn_height);
        btnAvatar = findViewById(R.id.register_btn_avatar);
        imgPreview = findViewById(R.id.register_img_preview);

        ArrayAdapter adapterWeight = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.weights_array));
        spnWeight.setAdapter(adapterWeight);
        ArrayAdapter adapterHeight = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.heights_array));
        spnHeight.setAdapter(adapterHeight);

        weightFrom = spnWeight.getSelectedItem().toString();
        heightFrom = spnHeight.getSelectedItem().toString();

        btnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String option_1 = getString(R.string.register_dialog_option_1);
                final String option_2 = getString(R.string.register_dialog_option_2);
                final String title = getString(R.string.register_dialog_title);

                final CharSequence[] options = {option_1, option_2};

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle(title);

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(option_1)) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        } else if (options[which].equals(option_2)) {
                            //TODO: Implement
                        }
                    }
                });

                builder.show();
            }
        });

        spnWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (etWeight.getText().toString().length() > 0) {
                    float weight = Float.parseFloat(etWeight.getText().toString().trim());

                    String from = weightFrom;
                    String to = spnWeight.getItemAtPosition(position).toString();
                    weightFrom = to;

                    etWeight.setText(String.format(Locale.US, "%.2f", UTIL.weightConverter(weight, from, to)));
                } else {
                    weightFrom = spnWeight.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        spnHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (etHeight.getText().toString().length() > 0) {
                    float height = Float.parseFloat(etHeight.getText().toString().trim());

                    String from = heightFrom;
                    String to = spnHeight.getItemAtPosition(position).toString();
                    heightFrom = to;

                    etHeight.setText(String.format(Locale.US, "%.2f", UTIL.heightConverter(height, from, to)));
                } else {
                    heightFrom = spnHeight.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    Intent intent;
                    // creating a new user
                    if (loggedUser == null) {
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra(USERNAME_KEY, etUsername.getText().toString());
                        intent.putExtra(PASSOWRD_KEY, etPassword.getText().toString());
                        // Add user to database
                        new InsertUser().execute(createUserFromView());
                    }
                    // edited a current user
                    else {
                        long id = loggedUser.getId();
                        loggedUser = createUserFromView();
                        loggedUser.setId(id);

                        intent = new Intent(getApplicationContext(), HomeFragment.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(EDITED_USER_KEY, loggedUser);
                        intent.putExtras(bundle);
                        // Edit user in the database
                        new UpdateUser().execute(loggedUser);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    tvErrorReport.setText(inputError.toString());
                    for (EditText et : etInvalid) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            et.setBackground(getResources().getDrawable(R.drawable.error_input_background));
                        }
                    }
                }
            }
        });



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (loggedUser == null) {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), HomeFragment.class);
                }
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        btnOpenDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_DeviceDefault,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etBirthday.setText(String.format(Locale.US, "%d-%d-%d", dayOfMonth, month, year));
            }
        }

        ;

    }

    private boolean validate() {
        boolean valid = true;
        String errorMessage = "";
        for (EditText et : etInvalid) {
            et.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        etInvalid.clear();

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassowrd.getText().toString();
        String birthday = etBirthday.getText().toString();
        Date dateBirthday;
        String weight = etWeight.getText().toString();
        String height = etHeight.getText().toString();
        /*
         * Check USERNAME
         * not-empty
         * min 3 length max 10 length
         * unique
         */
        if (username.length() == 0) {
            errorMessage += "USERNAME field is required\n";
            if (!etInvalid.contains(etUsername)) {
                etInvalid.add(etUsername);
            }
        }
        if (username.length() < 3 || username.length() > 10) {
            errorMessage += "USERNAME length should be between 3 and 10\n";
            if (!etInvalid.contains(etUsername)) {
                etInvalid.add(etUsername);
            }
        }
//        if (!checkUsernameUnique(username)) {
//            errorMessage += "USERNAME " + username + " is already taken\n";
//        }
        /*
         * Check PASSWORD
         * not-empty
         * min 4 length
         */
        if (password.length() == 0) {
            errorMessage += "PASSWORD field is required\n";
            if (!etInvalid.contains(etPassword)) {
                etInvalid.add(etPassword);
            }
        }
        if (password.length() < 4) {
            errorMessage += "PASSOWRD length should be at least 4\n";
            if (!etInvalid.contains(etPassword)) {
                etInvalid.add(etPassword);
            }
        }
        /*
         * Check CONFIRM PASSWORD
         * matches PASSWORD
         */
        if (!(confirmPassword.equals(password))) {
            errorMessage += "CONFIRMED PASSWORD should match PASSWORD\n";
            if (!etInvalid.contains(etConfirmPassowrd)) {
                etInvalid.add(etConfirmPassowrd);
            }
        }
        /*
         * Check BIRTHDAY
         * not-empty
         * format matches app's date format (MainActivity.DATE_FORMAT)
         */
        if (birthday.length() == 0) {
            errorMessage += "BIRTHDAY field is required\n";
            if (!etInvalid.contains(etBirthday)) {
                etInvalid.add(etBirthday);
            }
        }
        try {
            dateBirthday = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US)
                    .parse(birthday);
        } catch (Exception e) {
            errorMessage += "BIRTHDAY format invalid! Try dd-MM-yyyy";
            if (!etInvalid.contains(etBirthday)) {
                etInvalid.add(etBirthday);
            }
        }
        /*
         * Check WEIGHT
         * not-empty
         */
        if (weight.length() == 0) {
            errorMessage += "WEIGHT field is required";
            if (!etInvalid.contains(etWeight)) {
                etInvalid.add(etWeight);
            }
        }
        /*
         * Check HEIGHT
         * not-empty
         */
        if (height.length() == 0) {
            errorMessage += "HEIGHT field is required";
            if (!etInvalid.contains(etHeight)) {
                etInvalid.add(etHeight);
            }
        }


        if (errorMessage.length() > 0) {
            valid = false;
            this.inputError = new InputError(errorMessage);
        }

        return valid;
    }

    private User createUserFromView() {
        User user = new User();
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        try {
            user.setBirthday(new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US).parse(etBirthday.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setWeight(Float.parseFloat(etWeight.getText().toString()));
        user.setHeight(Float.parseFloat(etHeight.getText().toString()));
        user.setImg(((BitmapDrawable)imgPreview.getDrawable()).getBitmap());

        return user;
    }
}
