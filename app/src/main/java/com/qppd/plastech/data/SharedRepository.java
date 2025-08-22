package com.qppd.plastech.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qppd.plastech.ui.home.model.Earning;
import com.qppd.plastech.Classes.User;

public class SharedRepository {

    private static SharedRepository instance;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Earning> earningsLiveData = new MutableLiveData<>();

    private SharedRepository() {
        // Initialize with default or mock data if needed
    }

    public static SharedRepository getInstance() {
        if (instance == null) {
            instance = new SharedRepository();
        }
        return instance;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void setUser(User user) {
        userLiveData.setValue(user);
    }

    public LiveData<Earning> getEarningsLiveData() {
        return earningsLiveData;
    }

    public void setEarnings(Earning earnings) {
        earningsLiveData.setValue(earnings);
    }
}
