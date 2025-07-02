package com.qppd.plastech.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.Firebasez.FirebaseAuthHelper;
import com.qppd.plastech.Libs.Firebasez.FirebaseDatabaseHelper;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.Libs.IntentManager.IntentManagerClass;
import com.qppd.plastech.MainActivity;
import com.qppd.plastech.MenuActivity;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private FunctionClass function;

    private FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
    FirebaseDatabaseHelper<User> userFirebaseDatabaseHelper = new FirebaseDatabaseHelper<>("plastech");

    private boolean loginStatus = false;

    private String uid;

    public LoginTask(String uid, Context context, FunctionClass function) {
        this.uid = uid;
        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        loginStatus = false;
        try {

            userFirebaseDatabaseHelper.getRef()
                    .child("accounts/" + uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            loginStatus = true;
                            UserGlobal.setUser_id(uid);
                            UserGlobal.setUser(snapshot.getValue(User.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loginStatus = false;
                        }
                    });


            Thread.sleep(2000);
            return loginStatus;
        } catch (InterruptedException e) {
            return loginStatus = false;
        }

    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {

            if (UserGlobal.getUser().getStatus() == 0) {
                firebaseAuthHelper.logout();
                function.showMessage("Failed to login! Account locked!");
            } else {
                function.showMessage("LOGIN SUCCESS!");
                IntentManagerClass.intentsify(((Activity) context), MenuActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ((Activity) context).finish();
            }

        } else {
            function.showMessage("LOGIN FAILED!");
        }
    }

    @Override
    protected void onCancelled() {
        function.showMessage("LOGIN CANCELLED");
    }
}

