package com.example.damproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damproject.db.AppDatabase;
import com.example.damproject.fragments.HomeFragment;
import com.example.damproject.util.InputError;
import com.example.damproject.db.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
            return AppDatabase.getInstance(getApplicationContext()).userDao().insertUser(users[0]);
        }
    };

    public final static String EDITED_USER_KEY = "new.user.key";
    public final static String USERNAME_KEY = "username.key";
    public final static String PASSOWRD_KEY = "password.key";

    private User loggedUser;

    private InputError inputError;

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
        etWeight.setText(Integer.toString(loggedUser.getWeight()));
        etHeight.setText(Integer.toString(loggedUser.getHeight()));
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

                        Log.d("WTF", loggedUser.toString());
                        Log.d("WTF", AppDatabase.getInstance(getApplicationContext()).userDao().getAllUsers().toString());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    tvErrorReport.setText(inputError.toString());
                    for (EditText et : etInvalid) {
                        et.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
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
        };

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
        if (!checkUsernameUnique(username)) {
            errorMessage += "USERNAME " + username + " is already taken\n";
        }
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
        user.setWeight(Integer.parseInt(etWeight.getText().toString()));
        user.setHeight(Integer.parseInt(etHeight.getText().toString()));

        return user;
    }

    private boolean checkUsernameUnique(String username) {
        boolean ok = true;
        try {
            File file = new File(MainActivity.USERS_FILE);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

            while (in.available() != 0) {
                User user = (User)in.readObject();
                if (username.equals(user.getUsername())) {
                    ok = false;
                    break;
                }
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ok;
    }
}
