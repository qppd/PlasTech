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
        
        // Generate data for August 1-22, 2025 to align with updates fragment
        int[] augustDays = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        
        for (int day : augustDays) {
            calendar.set(2025, Calendar.AUGUST, day);
            
            String date = dateFormat.format(calendar.getTime());
            long timestamp = calendar.getTimeInMillis();
            
            // Generate realistic data that aligns with updates fragment
            BinHistoricalData data = generateDataForDate(date, timestamp, day);
            dataList.add(data);
        }
        
        return dataList;
    }
    
    private BinHistoricalData generateDataForDate(String date, long timestamp, int dayOfMonth) {
        // Create realistic patterns based on the daily update fragment data
        // Each day has 1 entry in daily table, so simulate realistic accumulation
        
        // Philippine bottle brands distribution (aligned with daily table)
        // Large bottles: 1.5L and 1L bottles (reward ₱2 each)
        // Small bottles: 500ml and 350ml bottles (reward ₱1 each)
        
        // Generate bottle counts that reflect realistic daily collection
        // Based on the daily table showing 1 entry per day, simulate realistic daily accumulation
        
        Random dailyRandom = new Random(date.hashCode()); // Consistent data for same date
        
        // Simulate daily collections - each day receives multiple bottles
        int bottleLarge = dailyRandom.nextInt(8) + 3; // 3-10 large bottles per day
        int bottleSmall = dailyRandom.nextInt(12) + 5; // 5-16 small bottles per day
        
        // Add weekend/weekday variation
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
        
        if (isWeekend) {
            // More activity on weekends (people at home, more consumption)
            bottleLarge += dailyRandom.nextInt(5);
            bottleSmall += dailyRandom.nextInt(8);
        }
        
        // Calculate derived values based on realistic metrics
        int totalBottles = bottleLarge + bottleSmall;
        
        // Bin level calculation (realistic capacity management)
        int binLevel = Math.min(100, (totalBottles * 2) + dailyRandom.nextInt(20));
        
        // Total rewards: ₱2 for large, ₱1 for small (aligned with updates fragment)
        int totalRewards = (bottleLarge * 2) + (bottleSmall * 1);
        
        // Total weight based on realistic bottle weights from updates fragment
        // Large bottles: 25-50g average ~37g, Small bottles: 12-25g average ~18g
        int largeBotleWeight = bottleLarge * (35 + dailyRandom.nextInt(10)); // 35-44g per large bottle
        int smallBottleWeight = bottleSmall * (15 + dailyRandom.nextInt(8)); // 15-22g per small bottle
        int totalWeight = largeBotleWeight + smallBottleWeight;
        
        // Coin stock simulation (starts high, decreases as rewards are given)
        int baseStock = 500;
        int dailyDeduction = (dayOfMonth - 1) * 15; // Gradual decrease over month
        int coinStock = Math.max(50, baseStock - dailyDeduction - (totalRewards / 2) + dailyRandom.nextInt(30));
        
        // Determine status
        String status = "normal";
        if (binLevel >= 95) {
            status = "full";
        } else if (totalBottles <= 2) {
            status = "low";
        } else if (dailyRandom.nextInt(30) == 0) { // ~3% chance of maintenance
            status = "maintenance";
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
        // Generate data for August 22, 2025 (current demo date)
        String currentDate = "2025-08-22";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.AUGUST, 22);
        long timestamp = calendar.getTimeInMillis();
        
        BinHistoricalData todayData = generateDataForDate(currentDate, timestamp, 22);
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
