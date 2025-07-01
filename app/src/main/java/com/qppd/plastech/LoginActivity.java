package com.qppd.plastech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.Libs.IntentManager.IntentManagerClass;

public class LoginActivity extends AppCompatActivity {

    private FunctionClass function = new FunctionClass(this);

    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        function.noActionBar(getSupportActionBar());

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentManagerClass.intentsify(LoginActivity.this, MenuActivity.class);
            }
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentManagerClass.intentsify(LoginActivity.this, RegisterActivity.class);
            }
        });
    }
}