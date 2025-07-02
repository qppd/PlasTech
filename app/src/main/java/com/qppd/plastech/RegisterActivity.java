package com.qppd.plastech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.AutoTimez.AutotimeClass;
import com.qppd.plastech.Libs.DateTimez.DateTimeClass;
import com.qppd.plastech.Libs.Firebasez.FirebaseAuthHelper;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.Libs.Permissionz.PermissionClass;
import com.qppd.plastech.Libs.Validatorz.ValidatorClass;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = RegisterActivity.class.getSimpleName();

    private FunctionClass function = new FunctionClass(this);
    private AutotimeClass autotime = new AutotimeClass(this);
    private PermissionClass permission = new PermissionClass(this, null);
    private DateTimeClass datetime = new DateTimeClass("MM/dd/YYYY HH:mm:ss");

    private FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
    private FirebaseRTDBHelper<User> userFirebaseRTDBHelper = new FirebaseRTDBHelper<User>("plastech");

    private EditText edtEmail;
    private EditText edtName;
    private EditText edtPhone;
    private EditText edtPassword;
    private EditText edtConfirmPassword;

    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        function.noActionBar(getSupportActionBar());

        initializeComponents();

    }

    private void initializeComponents() {

        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    private void attemptSignup() {

        // Reset errors.
        edtEmail.setError(null);
        edtName.setError(null);
        edtPhone.setError(null);
        edtPassword.setError(null);
        edtConfirmPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        String email = edtEmail.getText().toString();
        String name = edtName.getText().toString();
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();
        String confirm_password = edtConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email address is required!");
            focusView = edtEmail;
            cancel = true;
        } else if (!ValidatorClass.validateEmailOnly(email)) {
            edtEmail.setError("Email address invalid!");
            focusView = edtEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Full name is required!");
            focusView = edtName;
            cancel = true;
        } else if (!ValidatorClass.validateLetterOnly(name)) {
            edtName.setError("Full name invalid!");
            focusView = edtName;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Contact no. is required!");
            focusView = edtPhone;
            cancel = true;
        } else if (!ValidatorClass.validatePhoneOnly(name)) {
            edtPhone.setError("Contact no. invalid!");
            focusView = edtPhone;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password is required!");
            focusView = edtPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            edtConfirmPassword.setError("Password confirmation is required!");
            focusView = edtConfirmPassword;
            cancel = true;
        }
        if (!ValidatorClass.validatePasswordOnly(password)) {
            edtPassword.setError("Invalid Password! Password must be 6 characters minimum, contains letter, and a number!");
            focusView = edtPassword;
            cancel = true;
        } else if (!password.equals(confirm_password)) {
            edtPassword.setError("Password do not match!");
            focusView = edtPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            firebaseAuthHelper.register(email, password, new FirebaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {

                    String uid = Objects.requireNonNull(user).getUid();

                    User userInfo = new User(email, name, phone, password, 1, datetime.getFormattedTime());
                    userFirebaseRTDBHelper.save("users/" + uid, userInfo, new FirebaseRTDBHelper.DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            function.showMessage("Registration successful!");
                            RegisterActivity.this.finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            function.showMessage("Registration failed!");
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {

                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                attemptSignup();
                break;

        }
    }


}