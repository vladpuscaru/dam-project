package com.example.damproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.damproject.db.AppDatabase;
import com.example.damproject.db.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_KEY = "login.user.key";
    public static final int CREATE_USER_REQUEST = 1;

    // UI Components
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogIn;
    private Button btnRegister;
    private ProgressBar pb;

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

        pb = findViewById(R.id.login_pb);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                User tryingToLoggin = new User(etUsername.getText().toString(), etPassword.getText().toString(), null, 0.2f, 0.2f);
                pb.setVisibility(View.VISIBLE);

                new AsyncTask<User, Void, User>() {
                    @Override
                    protected User doInBackground(User... users) {
                        return AppDatabase.getInstance(getApplicationContext()).userDao().getUserByCredentials(users[0].getUsername(), users[0].getPassword());
                    }

                    @Override
                    protected void onPostExecute(User user) {
                        pb.setVisibility(View.GONE);
                        if (user != null) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(USER_KEY, user);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Username/password incorrect",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute(tryingToLoggin);
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
            etUsername.setText(username);
            etPassword.setText(password);
        }
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
