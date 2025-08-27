package com.qppd.plastech.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.DateTimez.DateTimeClass;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.data.SharedRepository;
import com.qppd.plastech.ui.home.model.Earning;
import com.qppd.plastech.ui.home.model.NotificationItem;
import com.qppd.plastech.ui.home.model.TipItem;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ActivityLog>> recentActivitiesLiveData = new MutableLiveData<>();
    private MutableLiveData<Earning> earningsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<NotificationItem>> notificationsLiveData = new MutableLiveData<>();
    private MutableLiveData<TipItem> tipOfDayLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private FirebaseRTDBHelper<User> userHelper = new FirebaseRTDBHelper<>("plastech");
    private FirebaseRTDBHelper<ActivityLog> activityHelper = new FirebaseRTDBHelper<>("plastech");
    private DateTimeClass dateTime = new DateTimeClass("MM/dd/yyyy HH:mm:ss");
    private final SharedRepository sharedRepository = SharedRepository.getInstance();

    // Getters for LiveData
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<ActivityLog>> getRecentActivitiesLiveData() {
        return recentActivitiesLiveData;
    }

    public LiveData<Earning> getEarningsLiveData() {
        return earningsLiveData;
    }

    public LiveData<List<NotificationItem>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public LiveData<TipItem> getTipOfDayLiveData() {
        return tipOfDayLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public HomeViewModel() {
        // Observe shared repository data
        sharedRepository.getUserLiveData().observeForever(userLiveData::setValue);
        sharedRepository.getEarningsLiveData().observeForever(earningsLiveData::setValue);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove observers to prevent memory leaks
        sharedRepository.getUserLiveData().removeObserver(userLiveData::setValue);
        sharedRepository.getEarningsLiveData().removeObserver(earningsLiveData::setValue);
    }

    public void updateSharedUser(User user) {
        sharedRepository.setUser(user);
    }

    public void updateSharedEarnings(Earning earnings) {
        sharedRepository.setEarnings(earnings);
    }

    public void loadHomeData() {
        loadUserProfile();
        loadRecentActivities();
        loadEarnings();
        loadNotifications();
        loadTipOfDay();
    }

    private void loadUserProfile() {
        userHelper.get("users/" + UserGlobal.getUser_id(), User.class, new FirebaseRTDBHelper.DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessageLiveData.setValue("Failed to load user profile: " + e.getMessage());
            }
        });
    }

    private void loadRecentActivities() {
        isLoadingLiveData.setValue(true);
        
        activityHelper.getList("activity_logs/" + UserGlobal.getUser_id(), ActivityLog.class, new FirebaseRTDBHelper.ListCallback<ActivityLog>() {
            @Override
            public void onSuccess(List<ActivityLog> activityLogs) {
                // Show only recent 3 activities
                List<ActivityLog> recentActivities = activityLogs.size() > 3 ? 
                    activityLogs.subList(0, 3) : activityLogs;
                recentActivitiesLiveData.setValue(recentActivities);
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                // Load dummy data if Firebase fails
                loadDummyRecentActivities();
                isLoadingLiveData.setValue(false);
            }
        });
    }

    private void loadEarnings() {
        // For now, load dummy earnings data
        // In a real implementation, this would come from Firebase
        loadDummyEarnings();
    }

    private void loadNotifications() {
        // Load dummy notifications data
        loadDummyNotifications();
    }

    private void loadTipOfDay() {
        // Load dummy tip of the day
        loadDummyTipOfDay();
    }

    // Dummy data methods for demonstration
    private void loadDummyRecentActivities() {
        List<ActivityLog> dummyActivities = new ArrayList<>();
        String userId = UserGlobal.getUser_id();
        
        // Recent activities based on real transaction data from 8/23/2025
        dummyActivities.add(new ActivityLog(
            "dummy_1",
            userId,
            "Plastic Recycling",
            "Recycled Large bottle (8.7\" x 9\") — ₱1.00 credited",
            "8/23/2025 12:33:00",
            "Completed"
        ));
        
        dummyActivities.add(new ActivityLog(
            "dummy_2",
            userId,
            "Plastic Recycling", 
            "Recycled Large bottle (9.9\" x 8\") — ₱1.00 credited",
            "8/23/2025 12:18:00",
            "Completed"
        ));
        
        dummyActivities.add(new ActivityLog(
            "dummy_3",
            userId,
            "Plastic Recycling",
            "Recycled Large bottle (9.5\" x 9\") — ₱1.00 credited",
            "8/23/2025 12:07:00",
            "Completed"
        ));
        
        recentActivitiesLiveData.setValue(dummyActivities);
    }

    private void loadDummyEarnings() {
        Earning earning = new Earning();
        
        // Based on real transaction data:
        // 8/22/2025: 16 transactions × ₱1.00 = ₱16.00
        // 8/23/2025: 13 transactions × ₱1.00 = ₱13.00  
        // Total: ₱29.00
        earning.setTotalEarnings(29.00);
        earning.setTodayEarnings(13.00); // August 23 earnings
        earning.setThisWeekEarnings(29.00); // Both days are in same week
        earning.setThisMonthEarnings(29.00); // Both days are in August
        earning.setUserLevel("Eco Beginner");
        earning.setProgressToNextLevel(29); // 29% to next milestone (₱100)
        earning.setNextMilestone(100.00);
        
        earningsLiveData.setValue(earning);
    }

    private void loadDummyNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        
        notifications.add(new NotificationItem(
            "earning_notification",
            "Earnings Update",
            "₱13.00 credited for today's recycling!",
            "8/23/2025 12:33:00",
            false
        ));
        
        notifications.add(new NotificationItem(
            "daily_summary",
            "Daily Summary",
            "Great job! You recycled 13 bottles today earning ₱13.00",
            "8/23/2025 12:35:00",
            false
        ));
        
        notifications.add(new NotificationItem(
            "milestone_progress",
            "Milestone Progress",
            "You're 29% towards your next milestone of ₱100!",
            "8/23/2025 12:30:00",
            true
        ));
        
        notificationsLiveData.setValue(notifications);
    }

    private void loadDummyTipOfDay() {
        String[] tips = {
            "Rinse plastics before recycling to improve quality!",
            "Did you know? PET bottles can be recycled up to 7 times!",
            "Withdraw your earnings anytime through the app!",
            "Join community events to earn bonus points!",
            "Sort your plastics by type for better rewards!"
        };
        
        // Select a random tip based on current day
        int tipIndex = (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % tips.length;
        
        TipItem tip = new TipItem(
            "daily_tip",
            "Tip of the Day",
            tips[tipIndex],
            "tip"
        );
        
        tipOfDayLiveData.setValue(tip);
    }

    public void refreshData() {
        loadHomeData();
    }
}
