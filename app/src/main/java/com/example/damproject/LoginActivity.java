package com.example.damproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.damproject.util.User;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_KEY = "login.user.key";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogIn;

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
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    User user = new User(typedUsername, typedPassword);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(USER_KEY, user);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateFields() {
        // TODO: validate inputs (check with database)
        typedUsername = etUsername.getText().toString();
        typedPassword = etPassword.getText().toString();
        return typedUsername.equals("admin") && typedPassword.equals("admin");
    }
}
