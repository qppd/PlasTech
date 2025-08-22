package com.qppd.plastech.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.R;
import com.qppd.plastech.databinding.FragmentHomeBinding;
import com.qppd.plastech.ui.home.adapter.HomeActivityAdapter;
import com.qppd.plastech.ui.home.model.Earning;
import com.qppd.plastech.ui.home.model.NotificationItem;
import com.qppd.plastech.ui.home.model.TipItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    private View root;
    private Context context;
    private HomeViewModel homeViewModel;
    private FunctionClass function;
    
    // UI Components
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtWelcomeMessage;
    private TextView txtCurrentDate;
    private CircleImageView civProfilePicture;
    
    // Earnings section
    private TextView txtTotalEarnings;
    private TextView txtTodayEarnings;
    private TextView txtWeekEarnings;
    private TextView txtMonthEarnings;
    private Chip chipUserLevel;
    private TextView txtProgressLabel;
    private TextView txtProgressPercentage;
    private ProgressBar progressBarEarnings;
    
    // Activities section
    private RecyclerView recyclerViewActivities;
    private TextView txtViewAllActivities;
    private TextView txtNoActivities;
    private ProgressBar progressBarActivities;
    private HomeActivityAdapter activityAdapter;
    
    // Notifications section
    private MaterialCardView cardNotifications;
    private LinearLayout layoutLatestNotification;
    private TextView txtNotificationTitle;
    private TextView txtNotificationMessage;
    private TextView txtNoNotifications;
    
    // Tip section
    private TextView txtTipContent;
    
    // Quick Actions
    private MaterialCardView cardQuickRecycle;
    private MaterialCardView cardQuickMonitor;
    private MaterialCardView cardQuickProfile;
    private MaterialCardView cardQuickRecords;
    private FloatingActionButton fabQuickRecycle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();
        
        function = new FunctionClass(context);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        initializeComponents();
        setupObservers();
        setupRecyclerView();
        setCurrentDateAndGreeting();
        
        // Load data
        homeViewModel.loadHomeData();
        
        return root;
    }

    private void initializeComponents() {
        // Swipe refresh
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            homeViewModel.refreshData();
        });
        
        // Header components
        txtWelcomeMessage = root.findViewById(R.id.txtWelcomeMessage);
        txtCurrentDate = root.findViewById(R.id.txtCurrentDate);
        civProfilePicture = root.findViewById(R.id.civProfilePicture);
        civProfilePicture.setOnClickListener(this);
        
        // Earnings components
        txtTotalEarnings = root.findViewById(R.id.txtTotalEarnings);
        txtTodayEarnings = root.findViewById(R.id.txtTodayEarnings);
        txtWeekEarnings = root.findViewById(R.id.txtWeekEarnings);
        txtMonthEarnings = root.findViewById(R.id.txtMonthEarnings);
        chipUserLevel = root.findViewById(R.id.chipUserLevel);
        txtProgressLabel = root.findViewById(R.id.txtProgressLabel);
        txtProgressPercentage = root.findViewById(R.id.txtProgressPercentage);
        progressBarEarnings = root.findViewById(R.id.progressBarEarnings);
        
        // Activities components
        recyclerViewActivities = root.findViewById(R.id.recyclerViewActivities);
        txtViewAllActivities = root.findViewById(R.id.txtViewAllActivities);
        txtViewAllActivities.setOnClickListener(this);
        txtNoActivities = root.findViewById(R.id.txtNoActivities);
        progressBarActivities = root.findViewById(R.id.progressBarActivities);
        
        // Notifications components
        cardNotifications = root.findViewById(R.id.cardNotifications);
        cardNotifications.setOnClickListener(this);
        layoutLatestNotification = root.findViewById(R.id.layoutLatestNotification);
        txtNotificationTitle = root.findViewById(R.id.txtNotificationTitle);
        txtNotificationMessage = root.findViewById(R.id.txtNotificationMessage);
        txtNoNotifications = root.findViewById(R.id.txtNoNotifications);
        
        // Tip section
        txtTipContent = root.findViewById(R.id.txtTipContent);
        
        // Quick Actions
        cardQuickRecycle = root.findViewById(R.id.cardQuickRecycle);
        cardQuickRecycle.setOnClickListener(this);
        cardQuickMonitor = root.findViewById(R.id.cardQuickMonitor);
        cardQuickMonitor.setOnClickListener(this);
        cardQuickProfile = root.findViewById(R.id.cardQuickProfile);
        cardQuickProfile.setOnClickListener(this);
        cardQuickRecords = root.findViewById(R.id.cardQuickRecords);
        cardQuickRecords.setOnClickListener(this);
        
        // Floating Action Button
        fabQuickRecycle = root.findViewById(R.id.fabQuickRecycle);
        fabQuickRecycle.setOnClickListener(this);
    }

    private void setupObservers() {
        homeViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUserUI(user);
            }
        });

        homeViewModel.getRecentActivitiesLiveData().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                updateActivitiesUI(activities);
            }
        });

        homeViewModel.getEarningsLiveData().observe(getViewLifecycleOwner(), earning -> {
            if (earning != null) {
                updateEarningsUI(earning);
            }
        });

        homeViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null && !notifications.isEmpty()) {
                updateNotificationsUI(notifications);
            }
        });

        homeViewModel.getTipOfDayLiveData().observe(getViewLifecycleOwner(), tip -> {
            if (tip != null) {
                updateTipUI(tip);
            }
        });

        homeViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                swipeRefreshLayout.setRefreshing(isLoading);
                progressBarActivities.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        homeViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                function.showMessage(errorMessage);
            }
        });
    }

    private void setupRecyclerView() {
        activityAdapter = new HomeActivityAdapter();
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewActivities.setAdapter(activityAdapter);
        recyclerViewActivities.setNestedScrollingEnabled(false);
    }

    private void setCurrentDateAndGreeting() {
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        txtCurrentDate.setText(currentDate);
    }

    private void updateUserUI(User user) {
        if (user != null && user.getName() != null) {
            String greeting = getTimeBasedGreeting();
            String welcomeMessage = getString(R.string.home_welcome_message, greeting, user.getName());
            txtWelcomeMessage.setText(welcomeMessage);
        }
        
        // Load profile picture using Glide (placeholder for now)
        Glide.with(this)
                .load(R.drawable.profile) // This would be the user's actual profile photo URL
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(civProfilePicture);
                
        // Add animation
        civProfilePicture.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
    }

    private void updateActivitiesUI(List<ActivityLog> activities) {
        if (activities.isEmpty()) {
            txtNoActivities.setVisibility(View.VISIBLE);
            recyclerViewActivities.setVisibility(View.GONE);
        } else {
            txtNoActivities.setVisibility(View.GONE);
            recyclerViewActivities.setVisibility(View.VISIBLE);
            activityAdapter.setActivities(activities);
            
            // Add animation
            recyclerViewActivities.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
        }
    }

    private void updateEarningsUI(Earning earning) {
        txtTotalEarnings.setText(earning.getFormattedTotalEarnings());
        txtTodayEarnings.setText(earning.getFormattedTodayEarnings());
        txtWeekEarnings.setText(earning.getFormattedThisWeekEarnings());
        txtMonthEarnings.setText(earning.getFormattedThisMonthEarnings());
        
        chipUserLevel.setText(earning.getUserLevel());
        
        String progressText = getString(R.string.home_progress_to_next, earning.getNextMilestone());
        txtProgressLabel.setText(progressText);
        txtProgressPercentage.setText(earning.getProgressToNextLevel() + "%");
        progressBarEarnings.setProgress(earning.getProgressToNextLevel());
        
        // Add animation
        txtTotalEarnings.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
    }

    private void updateNotificationsUI(List<NotificationItem> notifications) {
        if (!notifications.isEmpty()) {
            NotificationItem latestNotification = notifications.get(0);
            
            txtNotificationTitle.setText(latestNotification.getTitle());
            txtNotificationMessage.setText(latestNotification.getMessage());
            
            layoutLatestNotification.setVisibility(View.VISIBLE);
            txtNoNotifications.setVisibility(View.GONE);
            
            // Add unread indicator if notification is not read
            if (!latestNotification.isRead()) {
                // Add a visual indicator for unread notifications
                txtNotificationTitle.setTextColor(context.getColor(R.color.colorPrimary));
            }
        } else {
            layoutLatestNotification.setVisibility(View.GONE);
            txtNoNotifications.setVisibility(View.VISIBLE);
        }
    }

    private void updateTipUI(TipItem tip) {
        txtTipContent.setText(tip.getContent());
        
        // Add animation
        txtTipContent.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
    }

    private String getTimeBasedGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hour >= 5 && hour < 12) {
            return getString(R.string.greeting_morning);
        } else if (hour >= 12 && hour < 18) {
            return getString(R.string.greeting_afternoon);
        } else {
            return getString(R.string.greeting_evening);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civProfilePicture:
                // Navigate to profile with animation
                civProfilePicture.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.profile_photo_animation));
                NavHostFragment.findNavController(this).navigate(R.id.navigation_profile);
                break;
                
            case R.id.txtViewAllActivities:
                // Navigate to full activity logs
                NavHostFragment.findNavController(this).navigate(R.id.navigation_profile);
                break;
                
            case R.id.cardNotifications:
                // Handle notification card click
                function.showMessage("Notifications feature coming soon!");
                break;
                
            case R.id.cardQuickRecycle:
                // Navigate to monitor/recycling section
                NavHostFragment.findNavController(this).navigate(R.id.navigation_monitor);
                break;
                
            case R.id.cardQuickMonitor:
                // Navigate to monitoring
                NavHostFragment.findNavController(this).navigate(R.id.navigation_monitor);
                break;
                
            case R.id.cardQuickProfile:
                // Navigate to profile
                NavHostFragment.findNavController(this).navigate(R.id.navigation_profile);
                break;
                
            case R.id.cardQuickRecords:
                // Navigate to records/updates
                NavHostFragment.findNavController(this).navigate(R.id.navigation_update);
                break;
                
            case R.id.fabQuickRecycle:
                // Navigate to monitor/recycling section with animation
                fabQuickRecycle.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.profile_photo_animation));
                NavHostFragment.findNavController(this).navigate(R.id.navigation_monitor);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}