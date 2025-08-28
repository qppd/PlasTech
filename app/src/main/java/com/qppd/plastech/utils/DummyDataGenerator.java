package com.qppd.plastech.utils;

import com.qppd.plastech.Classes.BinHistoricalData;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DummyDataGenerator {
    
    private FirebaseRTDBHelper<BinHistoricalData> historicalDataHelper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Random random = new Random();
    
    public DummyDataGenerator() {
        historicalDataHelper = new FirebaseRTDBHelper<>("plastech");
    }
    
    public interface DataGenerationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    
    public void generateAndUploadDummyData(DataGenerationCallback callback) {
        List<BinHistoricalData> dummyDataList = generateDummyHistoricalData();
        uploadDummyData(dummyDataList, 0, callback);
    }
    
    private List<BinHistoricalData> generateDummyHistoricalData() {
        List<BinHistoricalData> dataList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        // Generate data for August 20-27, 2025 based on real transaction data
        int[] augustDays = {20, 21, 22, 25, 26, 27};
        
        for (int day : augustDays) {
            calendar.set(2025, Calendar.AUGUST, day);
            
            String date = dateFormat.format(calendar.getTime());
            long timestamp = calendar.getTimeInMillis();
            
            // Generate data based on actual transaction records
            BinHistoricalData data = generateDataForDate(date, timestamp, day);
            dataList.add(data);
        }
        
        return dataList;
    }
    
    private BinHistoricalData generateDataForDate(String date, long timestamp, int dayOfMonth) {
        // Create data based on actual transaction records from 379 rows in DailyTableFragment
        
        int bottleLarge, bottleSmall, totalWeight, totalRewards;
        
        switch (dayOfMonth) {
            case 20:
                // August 20, 2025 - 63 transactions (27 Large + 36 Small)
                bottleLarge = 27;
                bottleSmall = 36;
                totalWeight = 2714; // Aggregated from individual transaction weights
                totalRewards = 63;
                break;
            case 21:
                // August 21, 2025 - 64 transactions (36 Large + 28 Small)
                bottleLarge = 36;
                bottleSmall = 28;
                totalWeight = 3358; // Aggregated from individual transaction weights
                totalRewards = 64;
                break;
            case 22:
                // August 22, 2025 - 63 transactions (38 Large + 25 Small)
                bottleLarge = 38;
                bottleSmall = 25;
                totalWeight = 2956; // Aggregated from individual transaction weights
                totalRewards = 63;
                break;
            case 25:
                // August 25, 2025 - 63 transactions (32 Large + 31 Small)
                bottleLarge = 32;
                bottleSmall = 31;
                totalWeight = 1696; // Aggregated from individual transaction weights
                totalRewards = 63;
                break;
            case 26:
                // August 26, 2025 - 63 transactions (33 Large + 30 Small)
                bottleLarge = 33;
                bottleSmall = 30;
                totalWeight = 2603; // Aggregated from individual transaction weights
                totalRewards = 63;
                break;
            case 27:
                // August 27, 2025 - 63 transactions (36 Large + 27 Small)
                bottleLarge = 36;
                bottleSmall = 27;
                totalWeight = 2104; // Aggregated from individual transaction weights
                totalRewards = 63;
                break;
            default:
                // Default case - should not happen with current data
                bottleLarge = 10;
                bottleSmall = 8;
                totalWeight = 300;
                totalRewards = 18;
                break;
        }
        
        int totalBottles = bottleLarge + bottleSmall;
        
        // Calculate bin level based on bottle count (realistic capacity management)
        // Assume bin can hold ~50 bottles before being considered full
        int binLevel = Math.min(100, (totalBottles * 100) / 50);
        
        // Coin stock simulation - starts high and decreases as rewards are given
        int baseStock = 500;
        int coinStock = Math.max(50, baseStock - (totalRewards * 5)); // Decrease based on rewards given
        
        // Status based on bin level and activity
        String status = "normal";
        if (binLevel >= 85) {
            status = "high";
        } else if (totalBottles <= 5) {
            status = "low";
        }
        
        return new BinHistoricalData(date, timestamp, bottleLarge, bottleSmall, 
                                   binLevel, totalRewards, totalWeight, coinStock, status);
    }
    
    private void uploadDummyData(List<BinHistoricalData> dataList, int index, DataGenerationCallback callback) {
        if (index >= dataList.size()) {
            callback.onSuccess();
            return;
        }
        
        BinHistoricalData data = dataList.get(index);
        String key = "bin_history/" + data.getDate();
        
        historicalDataHelper.save(key, data, new FirebaseRTDBHelper.DatabaseCallback() {
            @Override
            public void onSuccess() {
                // Upload next item
                uploadDummyData(dataList, index + 1, callback);
            }
            
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
    
    public void generateTodaysData(DataGenerationCallback callback) {
        // Generate data for August 27, 2025 (latest data from provided transactions)
        String currentDate = "2025-08-27";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.AUGUST, 27);
        long timestamp = calendar.getTimeInMillis();
        
        BinHistoricalData todayData = generateDataForDate(currentDate, timestamp, 27);
        String key = "bin_history/" + currentDate;
        
        historicalDataHelper.save(key, todayData, new FirebaseRTDBHelper.DatabaseCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
            
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
    
    /**
     * Check if data exists for a specific date
     */
    public boolean hasDataForDate(String date) {
        try {
            String[] parts = date.split("-");
            int day = Integer.parseInt(parts[2]);
            
            // Check if the day is in our supported range (all available dates from DailyTableFragment)
            int[] supportedDays = {20, 21, 22, 25, 26, 27};
            for (int supportedDay : supportedDays) {
                if (day == supportedDay) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get all supported dates for August 2025
     */
    public String[] getSupportedDates() {
        return new String[]{
            "2025-08-20", "2025-08-21", "2025-08-22", 
            "2025-08-25", "2025-08-26", "2025-08-27"
        };
    }
    
    /**
     * Get available dates for calendar selection in MonitorFragment
     * This method provides the dates that have actual transaction data
     * from the DailyTableFragment's 379 transaction records
     */
    public String[] getAvailableDates() {
        return getSupportedDates();
    }
    
    /**
     * Get total transaction count across all dates (379 transactions)
     */
    public int getTotalTransactionCount() {
        return 379; // Sum of all transactions: 63+64+63+63+63+63
    }
    
    /**
     * Get formatted date for display
     */
    public String getFormattedDateForDisplay(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            return date;
        }
    }
}

