package com.qppd.plastech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.qppd.plastech.Libs.Functionz.FunctionClass;

public class RegisterActivity extends AppCompatActivity {

    private FunctionClass function = new FunctionClass(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        function.noActionBar(getSupportActionBar());
    }
}