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
        
        // Generate data for August 22-23, 2025 based on real transaction data
        int[] augustDays = {22, 23};
        
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
        
        if (dayOfMonth == 22) {
            // August 22, 2025 data - 16 transactions
            // Large bottles: 6 (9.5"x9", 9.5"x9", 10"x9", 9.4"x8", 9"x8.5", 9.5"x9", 9.8"x8.7")
            // Small bottles: 10 (5"x5", 6.6"x6", 5.5"x5", 5.6"x5", 5.5"x5", 5"x5", 5"x5", 6.7"x5", 6.4"x5")
            bottleLarge = 7; // 7 large bottles
            bottleSmall = 9; // 9 small bottles
            // Total weight: Large: 45+46+45+44+47+45+48 = 320g, Small: 15+17+15+18+16+16+15+17+18 = 147g
            totalWeight = 467; // 320 + 147 = 467g
            totalRewards = 16; // ₱1.00 each = ₱16.00
        } else if (dayOfMonth == 23) {
            // August 23, 2025 data - 13 transactions  
            // Large bottles: 8 (9.5"x8", 9.5"x9", 10"x8", 8.4"x8", 9.5"x9", 9.9x8", 8.7x9")
            // Small bottles: 5 (5"x5", 6.5'x5", 5.5"x5, 5.5"x5", 5.7"x5", 7.2"x6")
            bottleLarge = 7; // 7 large bottles  
            bottleSmall = 6; // 6 small bottles
            // Total weight: Large: 40+43+39+36+44+45+43 = 290g, Small: 15+21+10+20+25+19 = 110g
            totalWeight = 400; // 290 + 110 = 400g
            totalRewards = 13; // ₱1.00 each = ₱13.00
        } else {
            // Fallback for other dates (shouldn't be used with current data)
            bottleLarge = 5;
            bottleSmall = 8;
            totalWeight = 300;
            totalRewards = 13;
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
        // Generate data for August 23, 2025 (latest data from provided transactions)
        String currentDate = "2025-08-23";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.AUGUST, 23);
        long timestamp = calendar.getTimeInMillis();
        
        BinHistoricalData todayData = generateDataForDate(currentDate, timestamp, 23);
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
