package com.qppd.plastech.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Libs.Functionz.FunctionClass;

public class SignupTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private FunctionClass function;
    private boolean registrationStatus = false;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private String uid;
    private User user;


    public SignupTask(String uid, User user, Context context, FunctionClass function) {
        this.uid = uid;
        this.user = user;
        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        registrationStatus = false;
        try {
            FirebaseDatabase.getInstance().getReference().child("ibuyit/accounts/" + uid).setValue(user, (error, ref) -> {
                if (error != null) {
                    Log.d("Signup Firebase failed", error.getMessage(), null);
                    registrationStatus = false;
                } else {
                    registrationStatus = true;
                    ((Activity) context).finish();

                }
            });


            Thread.sleep(2000);
            return registrationStatus;
        } catch (InterruptedException e) {
            return registrationStatus = false;
        }

    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            function.showMessage("REGISTRATION SUCCESS!");
        } else {
            function.showMessage("REGISTRATION FAILED!");
        }
    }

    @Override
    protected void onCancelled() {
        function.showMessage("SIGNUP CANCELLED");
    }
}

