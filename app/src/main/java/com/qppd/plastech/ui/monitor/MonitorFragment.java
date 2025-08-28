package com.qppd.plastech.ui.monitor;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.qppd.plastech.Classes.Bin;
import com.qppd.plastech.Classes.BinHistoricalData;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.R;
import com.qppd.plastech.utils.DummyDataGenerator;
import com.qppd.plastech.databinding.FragmentMonitorBinding;
import com.sahana.horizontalcalendar.HorizontalCalendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MonitorFragment extends Fragment {

    private static final String TAG = "MonitorFragment";
    
    private FragmentMonitorBinding binding;
    private View root;
    private Context context;

    // UI Components
    private HorizontalCalendar horizontalCalendar;
    private TextView txtBottleLarge;
    private TextView txtBottleSmall;
    private ImageView imgBinFull;
    private TextView txtBinLevel;
    private TextView txtBinLevelDescription;
    private TextView txtTotalReward;
    private TextView txtTotalWeight;
    private TextView txtCoinStock;
    private ProgressBar loadingProgressBar;
    
    // Cards for animations
    private CardView cardLargeBottle;
    private CardView cardSmallBottle;
    private CardView cardBinLevel;
    private CardView cardTotalRewards;
    private CardView cardTotalWeight;
    private CardView cardMoneyLeft;

    // Firebase helpers
    private FirebaseRTDBHelper<Bin> binFirebaseRTDBHelper = new FirebaseRTDBHelper<>("plastech");
    private FirebaseRTDBHelper<BinHistoricalData> historicalDataHelper = new FirebaseRTDBHelper<>("plastech");
    
    // Utility classes
    private DummyDataGenerator dummyDataGenerator;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    // Animation state
    private Animation binFullAnimation;
    private boolean isDataLoading = false;
    private String currentSelectedDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();

        initializeComponents();
        setupCalendar();
        setupAnimations();
        setupCardInteractions();
        
        // Initialize dummy data generator
        dummyDataGenerator = new DummyDataGenerator();
        
        // Load August 22, 2025 data initially (aligned with updates fragment date range)
        currentSelectedDate = "2025-08-22";
        loadDataForDate(currentSelectedDate);
        
        // Generate dummy data if needed (only once)
        generateDummyDataIfNeeded();

        // Add initial animations to match HomeFragment style
        horizontalCalendar.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));

        return root;
    }

    private void initializeComponents() {
        // Calendar
        horizontalCalendar = root.findViewById(R.id.horizontalCalendar);
        
        // Loading indicator
        loadingProgressBar = root.findViewById(R.id.loadingProgressBar);
        
        // Text views
        txtBottleLarge = root.findViewById(R.id.txtBottleLarge);
        txtBottleSmall = root.findViewById(R.id.txtBottleSmall);
        imgBinFull = root.findViewById(R.id.imgBinFull);
        txtBinLevel = root.findViewById(R.id.txtBinLevel);
        txtBinLevelDescription = root.findViewById(R.id.txtBinLevelDescription);
        txtTotalReward = root.findViewById(R.id.txtTotalReward);
        txtTotalWeight = root.findViewById(R.id.txtTotalWeight);
        txtCoinStock = root.findViewById(R.id.txtCoinStock);
        
        // Cards
        cardLargeBottle = root.findViewById(R.id.cardLargeBottle);
        cardSmallBottle = root.findViewById(R.id.cardSmallBottle);
        cardBinLevel = root.findViewById(R.id.cardBinLevel);
        cardTotalRewards = root.findViewById(R.id.cardTotalRewards);
        cardTotalWeight = root.findViewById(R.id.cardTotalWeight);
        cardMoneyLeft = root.findViewById(R.id.cardMoneyLeft);
    }

    private void setupCalendar() {
        // For now, we'll implement manual date selection through touch events
        // The HorizontalCalendar library in version 1.2.2 may not have the listener interface
        horizontalCalendar.setOnClickListener(v -> {
            // For demonstration, let's create a simple date picker
            showDatePickerDialog();
        });
    }
    
    private void showDatePickerDialog() {
        // Sample dates from August 1-22, 2025 (aligned with updates fragment)
        String[] sampleDates = {
            "2025-08-01", "2025-08-02", "2025-08-03", "2025-08-04", "2025-08-05", "2025-08-06",
            "2025-08-07", "2025-08-08", "2025-08-09", "2025-08-10", "2025-08-11", "2025-08-12",
            "2025-08-13", "2025-08-14", "2025-08-15", "2025-08-16", "2025-08-17", "2025-08-18",
            "2025-08-19", "2025-08-20", "2025-08-21", "2025-08-22"
        };
        
        // Cycle through the August 2025 dates
        int currentIndex = java.util.Arrays.asList(sampleDates).indexOf(currentSelectedDate);
        int nextIndex = (currentIndex + 1) % sampleDates.length;
        String selectedDate = sampleDates[nextIndex];
        
        if (!selectedDate.equals(currentSelectedDate)) {
            currentSelectedDate = selectedDate;
            
            // Add calendar selection feedback
            addCalendarSelectionFeedback();
            
            // Load data for selected date with animation
            loadDataForDate(selectedDate);
            
            Toast.makeText(context, "Viewing data for: " + selectedDate, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupAnimations() {
        // Initialize bin full pulse animation
        binFullAnimation = AnimationUtils.loadAnimation(context, R.anim.bin_full_pulse);
    }
    
    private void setupCardInteractions() {
        // Add touch feedback for cards
        View.OnTouchListener cardTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_up));
                    break;
            }
            return false;
        };
        
        cardLargeBottle.setOnTouchListener(cardTouchListener);
        cardSmallBottle.setOnTouchListener(cardTouchListener);
        cardBinLevel.setOnTouchListener(cardTouchListener);
        cardTotalRewards.setOnTouchListener(cardTouchListener);
        cardTotalWeight.setOnTouchListener(cardTouchListener);
        cardMoneyLeft.setOnTouchListener(cardTouchListener);
    }
    
    private void addCalendarSelectionFeedback() {
        // Simple vibration feedback could be added here
        // For now, just a subtle visual feedback
        horizontalCalendar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
        new Handler().postDelayed(() -> {
            if (horizontalCalendar != null) {
                horizontalCalendar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_up));
            }
        }, 150);
    }
    
    private void generateDummyDataIfNeeded() {
        // Check if historical data exists, if not generate it
        historicalDataHelper.get("bin_history/" + currentSelectedDate, BinHistoricalData.class, 
            new FirebaseRTDBHelper.DataCallback<BinHistoricalData>() {
                @Override
                public void onSuccess(BinHistoricalData data) {
                    // Data exists, no need to generate
                    Log.d(TAG, "Historical data already exists");
                }
                
                @Override
                public void onFailure(Exception e) {
                    // No data exists, generate dummy data
                    Log.d(TAG, "No historical data found, generating dummy data");
                    generateDummyData();
                }
            });
    }
    
    private void generateDummyData() {
        dummyDataGenerator.generateAndUploadDummyData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Dummy data generated successfully");
                // Reload current date data
                loadDataForDate(currentSelectedDate);
            }
            
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to generate dummy data", e);
                Toast.makeText(context, "Failed to generate sample data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataForDate(String date) {
        if (isDataLoading) return;
        
        isDataLoading = true;
        showLoadingState();
        
        // Try to load historical data first
        historicalDataHelper.get("bin_history/" + date, BinHistoricalData.class, 
            new FirebaseRTDBHelper.DataCallback<BinHistoricalData>() {
                @Override
                public void onSuccess(BinHistoricalData data) {
                    isDataLoading = false;
                    hideLoadingState();
                    updateUIWithHistoricalData(data);
                }
                
                @Override
                public void onFailure(Exception e) {
                    // If no historical data, check if it's within August 2025 range and load current bin data
                    String currentDate = "2025-08-22"; // Current demo date
                    if (date.equals(currentDate)) {
                        loadCurrentBinData();
                    } else {
                        isDataLoading = false;
                        hideLoadingState();
                        showNoDataMessage(date);
                    }
                }
            });
    }
    
    private void loadCurrentBinData() {
        binFirebaseRTDBHelper.get("bin", Bin.class, new FirebaseRTDBHelper.DataCallback<Bin>() {
            @Override
            public void onSuccess(Bin bin) {
                isDataLoading = false;
                hideLoadingState();
                updateUIWithCurrentData(bin);
            }
            
            @Override
            public void onFailure(Exception e) {
                isDataLoading = false;
                hideLoadingState();
                showErrorMessage();
            }
        });
    }
    
    private void updateUIWithHistoricalData(BinHistoricalData data) {
        animateDataUpdate(() -> {
            animateValueChange(txtBottleLarge, data.getBottle_large());
            animateValueChange(txtBottleSmall, data.getBottle_small());
            animateValueChange(txtTotalReward, data.getTotal_rewards());
            animateValueChange(txtTotalWeight, data.getTotal_weight());
            animateValueChange(txtCoinStock, data.getCoin_stock());
            
            updateBinLevelDisplay(data.getBin_level());
        });
    }
    
    private void updateUIWithCurrentData(Bin bin) {
        animateDataUpdate(() -> {
            animateValueChange(txtBottleLarge, bin.getBottle_large());
            animateValueChange(txtBottleSmall, bin.getBottle_small());
            animateValueChange(txtTotalReward, bin.getTotal_rewards());
            animateValueChange(txtTotalWeight, bin.getTotal_weight());
            animateValueChange(txtCoinStock, bin.getCoin_stock());
            
            updateBinLevelDisplay(bin.getBin_level());
        });
    }
    
    private void updateBinLevelDisplay(int binLevel) {
        if (binLevel < 100) {
            // Normal bin level
            if (imgBinFull.getVisibility() == View.VISIBLE) {
                // Animate hide bin full image
                fadeOut(imgBinFull, () -> {
                    imgBinFull.setVisibility(View.GONE);
                    imgBinFull.clearAnimation();
                });
            }
            
            txtBinLevel.setVisibility(View.VISIBLE);
            animateValueChange(txtBinLevel, binLevel);
            txtBinLevelDescription.setText("BIN\nLEVEL");
            
        } else {
            // Bin is full
            txtBinLevel.setVisibility(View.GONE);
            
            if (imgBinFull.getVisibility() == View.GONE) {
                imgBinFull.setVisibility(View.VISIBLE);
                fadeIn(imgBinFull, null);
            }
            
            // Start pulsing animation for bin full warning
            imgBinFull.startAnimation(binFullAnimation);
            
            // Shake the bin level card for attention
            cardBinLevel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_animation));
            
            txtBinLevelDescription.setText("BIN IS FULL!\n(This warning will disappear\nonce the bin is emptied)");
        }
    }
    
    private void animateDataUpdate(Runnable updateAction) {
        // Fade out current data
        fadeOutAllCards(() -> {
            // Update the data
            updateAction.run();
            
            // Fade in with new data
            fadeInAllCards(null);
        });
    }
    
    private void animateValueChange(TextView textView, int newValue) {
        try {
            int currentValue = Integer.parseInt(textView.getText().toString());
            
            ValueAnimator animator = ValueAnimator.ofInt(currentValue, newValue);
            animator.setDuration(800);
            animator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                textView.setText(String.format(Locale.getDefault(), "%02d", animatedValue));
            });
            animator.start();
        } catch (NumberFormatException e) {
            // If parsing fails, just set the value directly
            textView.setText(String.format(Locale.getDefault(), "%02d", newValue));
        }
    }
    
    private void fadeOutAllCards(Runnable onComplete) {
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_scale);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                if (onComplete != null) onComplete.run();
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        
        cardLargeBottle.startAnimation(fadeOut);
        cardSmallBottle.startAnimation(fadeOut);
        cardBinLevel.startAnimation(fadeOut);
        cardTotalRewards.startAnimation(fadeOut);
        cardTotalWeight.startAnimation(fadeOut);
        cardMoneyLeft.startAnimation(fadeOut);
    }
    
    private void fadeInAllCards(Runnable onComplete) {
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_scale);
        if (onComplete != null) {
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    onComplete.run();
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
        
        // Stagger the animations slightly for a nice effect
        new Handler().postDelayed(() -> cardLargeBottle.startAnimation(fadeIn), 0);
        new Handler().postDelayed(() -> cardSmallBottle.startAnimation(fadeIn), 100);
        new Handler().postDelayed(() -> cardBinLevel.startAnimation(fadeIn), 200);
        new Handler().postDelayed(() -> cardTotalRewards.startAnimation(fadeIn), 300);
        new Handler().postDelayed(() -> cardTotalWeight.startAnimation(fadeIn), 400);
        new Handler().postDelayed(() -> cardMoneyLeft.startAnimation(fadeIn), 500);
    }
    
    private void fadeOut(View view, Runnable onComplete) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onComplete != null) onComplete.run();
            }
        });
        fadeOut.start();
    }
    
    private void fadeIn(View view, Runnable onComplete) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        if (onComplete != null) {
            fadeIn.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    onComplete.run();
                }
            });
        }
        fadeIn.start();
    }
    
    private void showLoadingState() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }
    
    private void hideLoadingState() {
        loadingProgressBar.setVisibility(View.GONE);
    }
    
    private void showNoDataMessage(String date) {
        Toast.makeText(context, "No data available for " + date, Toast.LENGTH_SHORT).show();
        
        // Set all values to zero with animation
        animateDataUpdate(() -> {
            animateValueChange(txtBottleLarge, 0);
            animateValueChange(txtBottleSmall, 0);
            animateValueChange(txtTotalReward, 0);
            animateValueChange(txtTotalWeight, 0);
            animateValueChange(txtCoinStock, 0);
            updateBinLevelDisplay(0);
        });
    }
    
    private void showErrorMessage() {
        Toast.makeText(context, "Error loading data. Please try again.", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Clear animations to prevent memory leaks
        if (imgBinFull != null) {
            imgBinFull.clearAnimation();
        }
        
        binding = null;
    }
}