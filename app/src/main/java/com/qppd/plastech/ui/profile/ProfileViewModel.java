package com.qppd.plastech.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ActivityLog>> activityLogsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private FirebaseRTDBHelper<User> userHelper = new FirebaseRTDBHelper<>("plastech");
    private FirebaseRTDBHelper<ActivityLog> activityHelper = new FirebaseRTDBHelper<>("plastech");

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<ActivityLog>> getActivityLogsLiveData() {
        return activityLogsLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void loadUserProfile() {
        isLoadingLiveData.setValue(true);
        
        userHelper.get("users/" + UserGlobal.getUser_id(), User.class, new FirebaseRTDBHelper.DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                userLiveData.setValue(user);
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessageLiveData.setValue("Failed to load user profile: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    public void loadActivityLogs() {
        isLoadingLiveData.setValue(true);
        
        activityHelper.getList("activity_logs/" + UserGlobal.getUser_id(), ActivityLog.class, new FirebaseRTDBHelper.ListCallback<ActivityLog>() {
            @Override
            public void onSuccess(List<ActivityLog> activityLogs) {
                activityLogsLiveData.setValue(activityLogs);
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessageLiveData.setValue("Failed to load activity logs: " + e.getMessage());
                isLoadingLiveData.setValue(false);
            }
        });
    }

    public void updateUserProfile(User updatedUser, ProfileUpdateCallback callback) {
        isLoadingLiveData.setValue(true);
        
        userHelper.save("users/" + UserGlobal.getUser_id(), updatedUser, new FirebaseRTDBHelper.DatabaseCallback() {
            @Override
            public void onSuccess() {
                userLiveData.setValue(updatedUser);
                UserGlobal.setUser(updatedUser);
                isLoadingLiveData.setValue(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                errorMessageLiveData.setValue("Failed to update profile: " + e.getMessage());
                isLoadingLiveData.setValue(false);
                callback.onFailure(e);
            }
        });
    }

    public interface ProfileUpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
