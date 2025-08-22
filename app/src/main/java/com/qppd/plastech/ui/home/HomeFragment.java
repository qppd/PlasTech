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
import com.qppd.plastech.ui.home.model.Earning;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();
        
        function = new FunctionClass(context);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        initializeComponents();
        setupObservers();
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


    }

    private void setupObservers() {
        homeViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUserUI(user);
            }
        });

        homeViewModel.getEarningsLiveData().observe(getViewLifecycleOwner(), earning -> {
            if (earning != null) {
                updateEarningsUI(earning);
            }
        });
        
        homeViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });

        homeViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                function.showMessage(errorMessage);
            }
        });
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

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}