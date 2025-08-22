package com.qppd.plastech.utils;

import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.DateTimez.DateTimeClass;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogUtils {

    private static FirebaseRTDBHelper<ActivityLog> activityLogHelper = new FirebaseRTDBHelper<>("plastech");
    private static DateTimeClass dateTime = new DateTimeClass("MM/dd/yyyy HH:mm:ss");

    public static void createSampleActivityLogs() {
        String userId = UserGlobal.getUser_id();
        if (userId == null) return;

        List<ActivityLog> sampleLogs = new ArrayList<>();
        
        // Create sample activity logs
        sampleLogs.add(new ActivityLog(
            "activity_" + System.currentTimeMillis() + "_1",
            userId,
            "Profile Update",
            "Updated profile picture",
            dateTime.getFormattedTime(),
            "Completed"
        ));

        sampleLogs.add(new ActivityLog(
            "activity_" + System.currentTimeMillis() + "_2",
            userId,
            "Trash Collection",
            "Picked up 5 bags of plastic waste from beach area",
            dateTime.getFormattedTime(),
            "Completed"
        ));

        sampleLogs.add(new ActivityLog(
            "activity_" + System.currentTimeMillis() + "_3",
            userId,
            "Data Upload",
            "Uploaded monitoring data for location #1234",
            dateTime.getFormattedTime(),
            "Pending"
        ));

        sampleLogs.add(new ActivityLog(
            "activity_" + System.currentTimeMillis() + "_4",
            userId,
            "Report Submission",
            "Submitted monthly environmental report",
            dateTime.getFormattedTime(),
            "Completed"
        ));

        sampleLogs.add(new ActivityLog(
            "activity_" + System.currentTimeMillis() + "_5",
            userId,
            "Account Login",
            "Logged into the application",
            dateTime.getFormattedTime(),
            "Completed"
        ));

        // Save each log to Firebase
        for (ActivityLog log : sampleLogs) {
            activityLogHelper.save("activity_logs/" + userId + "/" + log.getId(), 
                log, new FirebaseRTDBHelper.DatabaseCallback() {
                    @Override
                    public void onSuccess() {
                        // Log saved successfully
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure
                    }
                });
        }
    }
}
