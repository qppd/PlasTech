package com.qppd.plastech.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.database.FirebaseDatabase;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.Libs.Imagez.ImageBase64;

public class ResetTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private FunctionClass function;
    private boolean resetOperationStatus = false;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private ImageBase64 imageBase64 = new ImageBase64();

    public ResetTask(Context context, FunctionClass function) {

        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

//        resetOperationStatus = false;
//        try {
//            Bitmap bitmap = imageBase64.loadDrawableToBitmap(context, R.drawable.applogo);
//            Device device = new Device(imageBase64.enCode(bitmap), 0, 0, 0, 0);
//            FirebaseDatabase.getInstance().getReference().child("incubator/device/readings").setValue(device, (error, ref) -> {
//                if (error != null) {
//                    Log.d("Resetting Incubator failed", error.getMessage(), null);
//                    //resetOperationStatus = false;
//                } else {
//                    //resetOperationStatus = true;
//
//                }
//            });
//
//            Control control = new Control(false, false);
//
//            FirebaseDatabase.getInstance().getReference().child("incubator/device/controls").setValue(control, (error, ref) -> {
//                if (error != null) {
//                    Log.d("Resetting Incubator failed", error.getMessage(), null);
//                    resetOperationStatus = false;
//                } else {
//                    resetOperationStatus = true;
//
//                }
//            });
//
//
//            Thread.sleep(2000);
//            return resetOperationStatus;
//        } catch (InterruptedException e) {
//            return resetOperationStatus = false;
//        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            function.showMessage("INCUBATOR STOPPED!");


        } else {
            function.showMessage("STOPPING INCUBATOR FAILED!");
        }

    }

    @Override
    protected void onCancelled() {
        function.showMessage("RESETTING INCUBATOR CANCELLED");
    }


}

