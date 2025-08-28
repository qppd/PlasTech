package com.qppd.plastech.Utils;

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
        // Create data based on actual transaction records from user-provided data
        
        int bottleLarge, bottleSmall, totalWeight, totalRewards;
        
        switch (dayOfMonth) {
            case 20:
                // August 20, 2025 - 60 bottles (32 Small + 28 Large)
                bottleLarge = 28;
                bottleSmall = 32;
                totalWeight = 2714; // Rounded from 2713.7g
                totalRewards = 60;
                break;
            case 21:
                // August 21, 2025 - 119 bottles (42 Small + 77 Large)
                bottleLarge = 77;
                bottleSmall = 42;
                totalWeight = 5357; // Rounded from 5357.01g
                totalRewards = 119;
                break;
            case 22:
                // August 22, 2025 - 65 bottles (26 Small + 39 Large)
                bottleLarge = 39;
                bottleSmall = 26;
                totalWeight = 2956; // Rounded from 2956.43g
                totalRewards = 65;
                break;
            case 25:
                // August 25, 2025 - 65 bottles (31 Small + 34 Large)
                bottleLarge = 34;
                bottleSmall = 31;
                totalWeight = 3012; // Rounded from 3012.3g
                totalRewards = 65;
                break;
            case 26:
                // August 26, 2025 - 65 bottles (32 Small + 33 Large)
                bottleLarge = 33;
                bottleSmall = 32;
                totalWeight = 3024; // Rounded from 3023.78g
                totalRewards = 65;
                break;
            case 27:
                // August 27, 2025 - 65 bottles (32 Small + 33 Large)
                bottleLarge = 33;
                bottleSmall = 32;
                totalWeight = 3017; // Rounded from 3016.69g
                totalRewards = 65;
                break;
            default:
                // Fallback for other dates
                bottleLarge = 5;
                bottleSmall = 8;
                totalWeight = 300;
                totalRewards = 13;
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
}
}
