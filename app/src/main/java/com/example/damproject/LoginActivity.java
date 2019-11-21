package com.example.damproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.damproject.util.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_KEY = "login.user.key";
    public static final int CREATE_USER_REQUEST = 1;

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogIn;
    private Button btnRegister;

    private String typedUsername;
    private String typedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
    }


    private void initComponents() {
        etUsername = findViewById(R.id.login_et_username);
        etPassword = findViewById(R.id.login_et_password);
        btnLogIn = findViewById(R.id.login_btn_login);
        btnRegister = findViewById(R.id.login_btn_register);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = validateUser();

                if (etUsername.getText().toString().equals("admin") &&
                    etPassword.getText().toString().equals("admin")) {

                    User x = null;
                    try {
                        x = new User(etUsername.getText().toString(),
                                etPassword.getText().toString(),
                                new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US).parse("20-20-1992"),
                                100, 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("LOGIN", x.toString());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(USER_KEY, x);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                // TODO: vezi care-i treaba cu fisierele
//                if (user != null) {
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable(USER_KEY, user);
//
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "ERROR: User not found!",
//                            Toast.LENGTH_LONG).show();
//                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, CREATE_USER_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CREATE_USER_REQUEST) {
            String username = data.getStringExtra(RegisterActivity.USERNAME_KEY);
            String password = data.getStringExtra(RegisterActivity.PASSOWRD_KEY);
        }
    }

    private User validateUser() {
        List<User> users = readUsersFromDb(MainActivity.USERS_FILE);
        User user = null;

        for (User u : users) {
            if (u.getUsername().equals(etUsername.getText().toString()) &&
                u.getPassword().equals(etPassword.getText().toString())) {

                user = u;
                break;
            }
        }

        return user;
    }

    private List<User> readUsersFromDb(String fileName) {
        List<User> users = new ArrayList<>();
        try {
            File file = new File(fileName);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            while (in.available() != 0) {
                users.add((User) in.readObject());
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
