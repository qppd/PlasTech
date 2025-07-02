package com.qppd.plastech.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.LoginActivity;

public class LogoutTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private FunctionClass function;


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private boolean logoutStatus = false;

    public LogoutTask(Context context, FunctionClass function) {
        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        logoutStatus = false;
        try {

            if (firebaseAuth.getCurrentUser() != null) {
                firebaseAuth.signOut();
                logoutStatus = true;
            } else {
                logoutStatus = false;
            }

            Thread.sleep(2000);
            return logoutStatus;
        } catch (InterruptedException e) {
            return logoutStatus = false;
        }

    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            function.showMessage("LOGOUT SUCCESS!");
            context.startActivity(new Intent(((Activity) context), LoginActivity.class));
            ((Activity) context).finish();

        } else {
            function.showMessage("LOGOUT FAILED!");
        }
    }

    @Override
    protected void onCancelled() {
        function.showMessage("LOGIN CANCELLED");
    }
}

