package com.qppd.plastech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.qppd.plastech.Libs.AutoTimez.AutotimeClass;
import com.qppd.plastech.Libs.Firebasez.FirebaseAuthHelper;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.Libs.IntentManager.IntentManagerClass;
import com.qppd.plastech.Libs.Permissionz.PermissionClass;
import com.qppd.plastech.Libs.Validatorz.ValidatorClass;
import com.qppd.plastech.Tasks.LoginTask;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private FunctionClass function = new FunctionClass(this);
    private AutotimeClass autotime = new AutotimeClass(this);
    private PermissionClass permission = new PermissionClass(this, this);

    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.INTERNET,
            android.Manifest.permission.CALL_PHONE, android.Manifest.permission.CHANGE_NETWORK_STATE};

    private EditText email;
    private EditText password;

    private Button btnLogin;
    private Button btnRegister;

    private FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        function.noActionBar(getSupportActionBar());
        autotime.checkAutotime();

        if (!permission.hasPermissions()) {
            permission.requestPermissions(PERMISSION_ALL);
        }

        initializeComponents();

    }

    void initializeComponents() {
        email = findViewById(R.id.edtEmail);
        email.setText("sajedhm@gmail.com");
        password = findViewById(R.id.edtPassword);
        password.setText("Jedtala01+");
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                attemptSignin();
                break;
            case R.id.btnRegister:
                IntentManagerClass.intentsify(this, RegisterActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
        }
    }

    private void attemptSignin() {
        // Reset errors.
        email.setError(null);
        password.setError(null);

        boolean cancel = false;
        View focusView = null;

        String signin_email = email.getText().toString();
        String signin_password = password.getText().toString();


        if (TextUtils.isEmpty(signin_email)) {
            email.setError("Email is empty!");
            focusView = email;
            cancel = true;
        } else if (!ValidatorClass.validateEmailOnly(signin_email)) {
            email.setError("Invalid Email!");
            focusView = email;
            cancel = true;
        }
        if (TextUtils.isEmpty(signin_password)) {
            password.setError("Password is empty!");
            focusView = password;
            cancel = true;
        } else if (!ValidatorClass.validatePasswordOnly(signin_password)) {
            password.setError("Invalid Password!");
            focusView = password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt signup and focus the
            // field with an error.
            focusView.requestFocus();
        } else {

            firebaseAuthHelper.login(signin_email, signin_password, new FirebaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    String uid = Objects.requireNonNull(user).getUid();
                    //function.showMessage(uid);
                    LoginTask loginTask = new LoginTask(uid, LoginActivity.this, function);
                    loginTask.execute((Void) null);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            });


        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}