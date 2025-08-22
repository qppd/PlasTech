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
        
        dummyActivities.add(new ActivityLog(
            "dummy_1",
            userId,
            "Plastic Recycling",
            "Recycled 3 PET bottles — ₱6.00 credited",
            dateTime.getFormattedTime(),
            "Completed"
        ));
        
        dummyActivities.add(new ActivityLog(
            "dummy_2",
            userId,
            "Clean-up Event",
            "Joined Beach Clean-up Drive",
            dateTime.getFormattedTime(),
            "Completed"
        ));
        
        dummyActivities.add(new ActivityLog(
            "dummy_3",
            userId,
            "Bottle Collection",
            "Recycled 2 1.5L bottles — ₱4.00 credited",
            dateTime.getFormattedTime(),
            "Completed"
        ));
        
        recentActivitiesLiveData.setValue(dummyActivities);
    }

    private void loadDummyEarnings() {
        Earning earning = new Earning();
        earning.setTotalEarnings(120.00);
        earning.setTodayEarnings(10.00);
        earning.setThisWeekEarnings(45.00);
        earning.setThisMonthEarnings(120.00);
        earning.setUserLevel("Eco Warrior");
        earning.setProgressToNextLevel(75); // 75% to next milestone
        earning.setNextMilestone(500.00);
        
        earningsLiveData.setValue(earning);
    }

    private void loadDummyNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        
        notifications.add(new NotificationItem(
            "earning_notification",
            "Earnings Update",
            "₱10.00 credited for today's recycling!",
            dateTime.getFormattedTime(),
            false
        ));
        
        notifications.add(new NotificationItem(
            "milestone_notification",
            "Milestone Reached",
            "Congratulations! You've reached ₱100 total earnings!",
            dateTime.getFormattedTime(),
            false
        ));
        
        notifications.add(new NotificationItem(
            "event_notification",
            "Upcoming Event",
            "Beach Clean-up Drive this Saturday. Join us!",
            dateTime.getFormattedTime(),
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
