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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonitorFragment extends Fragment {

    private static final String TAG = "MonitorFragment";
    
    private FragmentMonitorBinding binding;
    private View root;
    private Context context;

    // UI Components
    private CustomHorizontalCalendar customHorizontalCalendar;
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
    
    // Calendar constants
    private int currentYear = 2025;
    private int currentMonth = 8; // August
    private int daysInCurrentMonth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();

        initializeComponents();
        calculateCurrentMonthDays();
        setupCalendar();
        setupAnimations();
        setupCardInteractions();
        
        // Initialize dummy data generator (now aligned with DailyTableFragment data)
        dummyDataGenerator = new DummyDataGenerator();
        
        // Log quick data summary and alignment verification
        Log.i(TAG, "DummyDataGenerator initialized: " + dummyDataGenerator.getQuickDataSummary());
        Log.d(TAG, dummyDataGenerator.verifyDataAlignment());
        
        // Generate dummy data if needed (only once) - do this first
        generateDummyDataIfNeeded();
        
        // Load data-available date initially (August 20, 2025 - first date with data)
        currentSelectedDate = "2025-08-20";
        
        // Load data for the current date with a delay to ensure setup is complete
        new Handler().postDelayed(() -> {
            if (isAdded() && context != null) {
                Log.d(TAG, "Initial data load for current date: " + currentSelectedDate);
                loadDataForDate(currentSelectedDate);
            }
        }, 2000); // Wait 2 seconds for data generation and calendar setup

        // Try to navigate calendar to August 1st after layout is complete
        new Handler().postDelayed(() -> {
            if (isAdded() && context != null && customHorizontalCalendar != null) {
                customHorizontalCalendar.goToAugust1st();
            }
        }, 1000); // Wait 1 second for calendar to be fully initialized

        // Add initial animations to match HomeFragment style
        customHorizontalCalendar.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));

        return root;
    }

    private void initializeComponents() {
        // Custom Calendar
        customHorizontalCalendar = root.findViewById(R.id.customHorizontalCalendar);
        
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

    private void calculateCurrentMonthDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth - 1, 1); // Month is 0-based
        daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "Current month (" + currentMonth + "/" + currentYear + ") has " + daysInCurrentMonth + " days");
    }

    private void setupCalendar() {
        Log.d(TAG, "Setting up custom horizontal calendar");
        
        if (customHorizontalCalendar != null) {
            // Set up date selection listener
            customHorizontalCalendar.setOnDateSelectedListener(new CustomHorizontalCalendar.OnDateSelectedListener() {
                @Override
                public void onDateSelected(String date, int day, int month, int year) {
                    Log.d(TAG, "Date selected: " + date);
                    currentSelectedDate = date;
                    
                    // Check if data is available for this date
                    if (hasDataForDate(date)) {
                        loadDataForDate(date);
                        Toast.makeText(context, "Aug " + day + ", " + year + " - Data loaded", Toast.LENGTH_SHORT).show();
                    } else {
                        // Show message that no data is available for this date
                        clearDisplayedData();
                        Toast.makeText(context, "Aug " + day + ", " + year + " - No data available", Toast.LENGTH_SHORT).show();
                    }
                    
                    addCalendarSelectionFeedback();
                }
            });
            
            // Add long click listener for help
            customHorizontalCalendar.setOnLongClickListener(v -> {
                String datesInfo = "Calendar Navigation:\n" +
                                 "• Scroll left/right to see all August dates\n" +
                                 "• Tap any date to load data for that day\n" +
                                 "• Long press for help\n\n" +
                                 "Data available: Aug 20-27, 2025 (8 days)\n" +
                                 "Full month view: Aug 1-31, 2025\n\n" +
                                 "Testing data load for Aug 20...";
                Toast.makeText(context, datesInfo, Toast.LENGTH_LONG).show();
                
                // Test loading data for Aug 20 (known to have data)
                new Handler().postDelayed(() -> {
                    Log.d(TAG, "Testing data load for Aug 20...");
                    customHorizontalCalendar.selectDateByString("2025-08-20");
                }, 2000);
                
                return true;
            });
        }
        
        Log.d(TAG, "Custom calendar setup completed");
    }
    
    private void clearDisplayedData() {
        // Clear all displayed data when no data is available for selected date
        if (txtBottleLarge != null) txtBottleLarge.setText("0");
        if (txtBottleSmall != null) txtBottleSmall.setText("0");
        if (txtBinLevel != null) txtBinLevel.setText("0%");
        if (txtBinLevelDescription != null) txtBinLevelDescription.setText("No data");
        if (txtTotalReward != null) txtTotalReward.setText("₱0.00");
        if (txtTotalWeight != null) txtTotalWeight.setText("0.0 kg");
        if (txtCoinStock != null) txtCoinStock.setText("₱0.00");
        
        // Stop any animations
        if (imgBinFull != null && binFullAnimation != null) {
            imgBinFull.clearAnimation();
        }
    }

    private void setupAnimations() {
        // Initialize bin full pulse animation
        binFullAnimation = AnimationUtils.loadAnimation(context, R.anim.bin_full_pulse);
    }
    
    private boolean hasDataForDate(String date) {
        // Check if we have data for this date (Aug 20-27 have data)
        return dummyDataGenerator != null && dummyDataGenerator.hasDataForDate(date);
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
        // For now, just a subtle visual feedback
        customHorizontalCalendar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
        new Handler().postDelayed(() -> {
            if (customHorizontalCalendar != null) {
                customHorizontalCalendar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_up));
            }
        }, 150);
    }
    
    private void generateDummyDataIfNeeded() {
        // Check if historical data exists for all dates in range, if not generate it
        String[] allDates = dummyDataGenerator.getSupportedDates();
        checkAndGenerateDataForDates(allDates, 0);
    }
    
    private void checkAndGenerateDataForDates(String[] dates, int index) {
        if (index >= dates.length) {
            // All dates checked
            Log.d(TAG, "Data availability check completed for all dates");
            return;
        }
        
        String dateToCheck = dates[index];
        historicalDataHelper.get("bin_history/" + dateToCheck, BinHistoricalData.class, 
            new FirebaseRTDBHelper.DataCallback<BinHistoricalData>() {
                @Override
                public void onSuccess(BinHistoricalData data) {
                    // Data exists for this date, check next
                    Log.d(TAG, "Historical data exists for " + dateToCheck);
                    checkAndGenerateDataForDates(dates, index + 1);
                }
                
                @Override
                public void onFailure(Exception e) {
                    // No data exists, generate dummy data for all dates
                    Log.d(TAG, "Missing data detected, generating complete dataset");
                    generateDummyData();
                }
            });
    }
    
    private void generateDummyData() {
        Toast.makeText(context, "Generating complete historical data for Aug 20-27...", Toast.LENGTH_SHORT).show();
        
        dummyDataGenerator.generateAndUploadDummyData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Complete dummy data generated successfully for all dates");
                Toast.makeText(context, "Historical data ready for all dates!", Toast.LENGTH_SHORT).show();
                
                // Reload current date data with a slight delay to ensure Firebase sync
                new Handler().postDelayed(() -> {
                    if (isAdded() && context != null) {
                        loadDataForDate(currentSelectedDate);
                    }
                }, 1000);
            }
            
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to generate complete dummy data", e);
                Toast.makeText(context, "Failed to generate complete sample data", Toast.LENGTH_SHORT).show();
                isDataLoading = false;
                hideLoadingState();
            }
        });
    }

    private void loadDataForDate(String date) {
        if (isDataLoading) {
            Log.d(TAG, "Already loading data, skipping request for " + date);
            return;
        }
        
        Log.d(TAG, "loadDataForDate called with: " + date);
        
        isDataLoading = true;
        showLoadingState();
        
        // Check if this date has actual data (Aug 20-27)
        boolean hasActualData = hasDataForDate(date);
        
        Log.d(TAG, "Date " + date + " has actual data: " + hasActualData);
        
        if (hasActualData) {
            // Try to load historical data from Firebase
            Log.d(TAG, "Attempting to load historical data for: " + date);
            
            historicalDataHelper.get("bin_history/" + date, BinHistoricalData.class, 
                new FirebaseRTDBHelper.DataCallback<BinHistoricalData>() {
                    @Override
                    public void onSuccess(BinHistoricalData data) {
                        Log.d(TAG, "Successfully loaded historical data for " + date);
                        if (data != null) {
                            Log.d(TAG, "Data details - Large: " + data.getBottle_large() + 
                                      ", Small: " + data.getBottle_small() + 
                                      ", Rewards: " + data.getTotal_rewards());
                        }
                        isDataLoading = false;
                        hideLoadingState();
                        updateUIWithHistoricalData(data);
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "No data found in Firebase for " + date + ", generating data...");
                        // If no historical data in Firebase, generate it and retry
                        generateDummyDataAndRetry(date);
                    }
                });
        } else {
            // For dates without actual data (Aug 1-19, 28-31), show empty state
            Log.d(TAG, "No actual data for " + date + ", showing empty state");
            isDataLoading = false;
            hideLoadingState();
            showEmptyDataState(date);
        }
    }
    
    private void generateDummyDataAndRetry(String requestedDate) {
        Log.d(TAG, "Starting data generation for " + requestedDate);
        Toast.makeText(context, "Generating data for " + requestedDate + "...", Toast.LENGTH_SHORT).show();
        
        dummyDataGenerator.generateAndUploadDummyData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Data generated successfully, retrying load for " + requestedDate);
                
                // Add a small delay to ensure Firebase has processed the upload
                new Handler().postDelayed(() -> {
                    // Retry loading the data after generation
                    historicalDataHelper.get("bin_history/" + requestedDate, BinHistoricalData.class, 
                        new FirebaseRTDBHelper.DataCallback<BinHistoricalData>() {
                            @Override
                            public void onSuccess(BinHistoricalData data) {
                                Log.d(TAG, "Successfully loaded data after generation for " + requestedDate);
                                if (data != null) {
                                    Log.d(TAG, "Generated data details - Large: " + data.getBottle_large() + 
                                              ", Small: " + data.getBottle_small() + 
                                              ", Rewards: " + data.getTotal_rewards());
                                }
                                isDataLoading = false;
                                hideLoadingState();
                                updateUIWithHistoricalData(data);
                            }
                            
                            @Override
                            public void onFailure(Exception e) {
                                // If still fails after generation, show error
                                Log.e(TAG, "Failed to load data even after generation for " + requestedDate, e);
                                isDataLoading = false;
                                hideLoadingState();
                                Toast.makeText(context, "Data generation completed but failed to load for " + requestedDate, Toast.LENGTH_LONG).show();
                                showEmptyDataState(requestedDate);
                            }
                        });
                }, 1000); // Wait 1 second for Firebase to process
            }
            
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to generate data for " + requestedDate, e);
                isDataLoading = false;
                hideLoadingState();
                Toast.makeText(context, "Failed to generate data for " + requestedDate + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                showEmptyDataState(requestedDate);
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
        if (data == null) {
            Log.e(TAG, "Historical data is null");
            showEmptyDataState(currentSelectedDate);
            return;
        }
        
        Log.d(TAG, "Updating UI with historical data for " + currentSelectedDate + ": " + 
              "Large=" + data.getBottle_large() + 
              ", Small=" + data.getBottle_small() + 
              ", Rewards=" + data.getTotal_rewards() + 
              ", Weight=" + data.getTotal_weight() + 
              ", Coins=" + data.getCoin_stock() + 
              ", BinLevel=" + data.getBin_level());

        animateDataUpdate(() -> {
            // Add null checks for TextViews and ensure data is loaded every time
            if (txtBottleLarge != null) {
                Log.d(TAG, "Updating txtBottleLarge to: " + data.getBottle_large());
                animateValueChange(txtBottleLarge, data.getBottle_large());
            } else {
                Log.e(TAG, "txtBottleLarge is null");
            }
            
            if (txtBottleSmall != null) {
                Log.d(TAG, "Updating txtBottleSmall to: " + data.getBottle_small());
                animateValueChange(txtBottleSmall, data.getBottle_small());
            } else {
                Log.e(TAG, "txtBottleSmall is null");
            }
            
            if (txtTotalReward != null) {
                Log.d(TAG, "Updating txtTotalReward to: " + data.getTotal_rewards());
                animateValueChange(txtTotalReward, data.getTotal_rewards());
            } else {
                Log.e(TAG, "txtTotalReward is null");
            }
            
            if (txtTotalWeight != null) {
                Log.d(TAG, "Updating txtTotalWeight to: " + data.getTotal_weight());
                animateValueChange(txtTotalWeight, data.getTotal_weight());
            } else {
                Log.e(TAG, "txtTotalWeight is null");
            }
            
            if (txtCoinStock != null) {
                Log.d(TAG, "Updating txtCoinStock to: " + data.getCoin_stock());
                animateValueChange(txtCoinStock, data.getCoin_stock());
            } else {
                Log.e(TAG, "txtCoinStock is null");
            }
            
            // Update bin level display with logging
            Log.d(TAG, "Updating bin level display to: " + data.getBin_level());
            updateBinLevelDisplay(data.getBin_level());
        });
    }
    
    private void updateUIWithCurrentData(Bin bin) {
        if (bin == null) {
            showErrorMessage();
            return;
        }
        
        animateDataUpdate(() -> {
            // Add null checks for TextViews
            if (txtBottleLarge != null) animateValueChange(txtBottleLarge, bin.getBottle_large());
            if (txtBottleSmall != null) animateValueChange(txtBottleSmall, bin.getBottle_small());
            if (txtTotalReward != null) animateValueChange(txtTotalReward, bin.getTotal_rewards());
            if (txtTotalWeight != null) animateValueChange(txtTotalWeight, bin.getTotal_weight());
            if (txtCoinStock != null) animateValueChange(txtCoinStock, bin.getCoin_stock());
            
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
        if (textView == null || !isAdded() || context == null) {
            return;
        }
        
        try {
            String currentText = textView.getText().toString();
            int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);
            
            ValueAnimator animator = ValueAnimator.ofInt(currentValue, newValue);
            animator.setDuration(800);
            animator.addUpdateListener(animation -> {
                if (textView != null && isAdded()) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    textView.setText(String.format(Locale.getDefault(), "%02d", animatedValue));
                }
            });
            animator.start();
        } catch (NumberFormatException e) {
            // Fallback: set value directly
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
    
    private void showEmptyDataState(String date) {
        String message = "No data available for " + formatDateForDisplay(date);
        
        // Provide helpful information about available dates
        if (date.startsWith("2025-08")) {
            String dayStr = date.substring(8);
            int day = Integer.parseInt(dayStr);
            
            if (day >= 20 && day <= 27) {
                message += "\n\nData should be available for this date.\nTry again or restart the app.";
            } else {
                message += "\n\nData is available for:\nAug 20-27, 2025\n(Total: 454 transactions + generated data)";
            }
        }
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        
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
    
    private String formatDateForDisplay(String date) {
        try {
            String dayStr = date.substring(8);
            return "Aug " + dayStr + ", 2025";
        } catch (Exception e) {
            return date;
        }
    }
    
    private void showErrorMessage() {
        Toast.makeText(context, "Error loading data. Please try again.", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Cancel any pending operations
        isDataLoading = false;
        
        // Clear animations to prevent memory leaks
        if (imgBinFull != null) {
            imgBinFull.clearAnimation();
        }
        
        // Clear handlers and animations
        if (customHorizontalCalendar != null) {
            customHorizontalCalendar.clearAnimation();
        }
        
        // Nullify references to prevent memory leaks
        dummyDataGenerator = null;
        binding = null;
        context = null;
    }
}
