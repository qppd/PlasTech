package com.qppd.plastech.ui.home.utils;

import android.content.Context;
import android.widget.Toast;

import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.DateTimez.DateTimeClass;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.ui.home.model.Earning;
import com.qppd.plastech.ui.home.model.NotificationItem;
import com.qppd.plastech.ui.home.model.TipItem;

import java.util.ArrayList;
import java.util.List;

public class HomeDataUtils {
    
    private static DateTimeClass dateTime = new DateTimeClass("MM/dd/yyyy HH:mm:ss");
    
    /**
     * Creates sample activity logs for demonstration
     */
    public static List<ActivityLog> createSampleActivities() {
        List<ActivityLog> activities = new ArrayList<>();
        String userId = UserGlobal.getUser_id();
        
        activities.add(new ActivityLog(
            "sample_1",
            userId,
            "Plastic Recycling",
            "Recycled 3 PET bottles — ₱6.00 credited",
            dateTime.getFormattedTime(),
            "Completed"
        ));
        
        activities.add(new ActivityLog(
            "sample_2", 
            userId,
            "Beach Clean-up",
            "Joined community beach clean-up event",
            dateTime.getFormattedTime(),
            "Completed"
        ));
        
        activities.add(new ActivityLog(
            "sample_3",
            userId,
            "Bottle Collection",
            "Recycled 2 1.5L bottles — ₱4.00 credited",
            dateTime.getFormattedTime(),
            "Completed"
        ));
        
        return activities;
    }
    
    /**
     * Creates sample earnings data
     */
    public static Earning createSampleEarnings() {
        Earning earning = new Earning();
        earning.setTotalEarnings(120.00);
        earning.setTodayEarnings(10.00);
        earning.setThisWeekEarnings(45.00);
        earning.setThisMonthEarnings(120.00);
        earning.setUserLevel("Eco Warrior");
        earning.setProgressToNextLevel(75);
        earning.setNextMilestone(500.00);
        return earning;
    }
    
    /**
     * Creates sample notifications
     */
    public static List<NotificationItem> createSampleNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        
        notifications.add(new NotificationItem(
            "notif_1",
            "Earnings Update",
            "₱10.00 credited for today's recycling!",
            dateTime.getFormattedTime(),
            false
        ));
        
        notifications.add(new NotificationItem(
            "notif_2",
            "Milestone Reached",
            "Congratulations! You've reached ₱100 total earnings!",
            dateTime.getFormattedTime(),
            true
        ));
        
        return notifications;
    }
    
    /**
     * Creates daily tip
     */
    public static TipItem createDailyTip() {
        String[] tips = {
            "Rinse plastics before recycling to improve quality!",
            "Did you know? PET bottles can be recycled up to 7 times!",
            "Withdraw your earnings anytime through the app!",
            "Join community events to earn bonus points!",
            "Sort your plastics by type for better rewards!",
            "Clean containers recycle more efficiently!",
            "Check plastic recycling codes before disposal!",
            "Reduce, reuse, then recycle for maximum impact!"
        };
        
        // Select tip based on day of year for consistency
        int tipIndex = (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % tips.length;
        
        return new TipItem(
            "daily_tip",
            "Tip of the Day",
            tips[tipIndex],
            "environmental"
        );
    }
    
    /**
     * Logs a user activity to Firebase
     */
    public static void logUserActivity(String action, String description, LogCallback callback) {
        String userId = UserGlobal.getUser_id();
        if (userId == null) {
            if (callback != null) callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        String activityId = "home_activity_" + System.currentTimeMillis();
        ActivityLog activityLog = new ActivityLog(
            activityId,
            userId,
            action,
            description,
            dateTime.getFormattedTime(),
            "Completed"
        );
        
        FirebaseRTDBHelper<ActivityLog> activityHelper = new FirebaseRTDBHelper<>("plastech");
        activityHelper.save("activity_logs/" + userId + "/" + activityId, 
            activityLog, new FirebaseRTDBHelper.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    if (callback != null) callback.onSuccess();
                }

                @Override
                public void onFailure(Exception e) {
                    if (callback != null) callback.onFailure(e);
                }
            });
    }
    
    /**
     * Shows a welcome message based on user level
     */
    public static void showWelcomeMessage(Context context, Earning earning) {
        String message = "Welcome back! ";
        
        if (earning.getTotalEarnings() >= 500) {
            message += "You're an Eco Champion! 🌟";
        } else if (earning.getTotalEarnings() >= 100) {
            message += "Great job recycling! 🌱";
        } else {
            message += "Start your eco journey today! ♻️";
        }
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    
    public interface LogCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
