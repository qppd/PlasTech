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
        
        // Generate data for the past 30 days
        for (int i = 30; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            
            String date = dateFormat.format(calendar.getTime());
            long timestamp = calendar.getTimeInMillis();
            
            // Generate realistic data with some patterns
            BinHistoricalData data = generateDataForDate(date, timestamp, i);
            dataList.add(data);
        }
        
        return dataList;
    }
    
    private BinHistoricalData generateDataForDate(String date, long timestamp, int daysAgo) {
        // Create realistic patterns - more activity on weekdays, less on weekends
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
        
        // Base multiplier for weekend/weekday activity
        double activityMultiplier = isWeekend ? 0.6 : 1.0;
        
        // Generate bottle counts with realistic patterns
        int baseLarge = (int) (random.nextInt(15) * activityMultiplier) + (isWeekend ? 5 : 10);
        int baseSmall = (int) (random.nextInt(25) * activityMultiplier) + (isWeekend ? 8 : 15);
        
        // Add some randomness but keep it realistic
        int bottleLarge = Math.max(0, baseLarge + random.nextInt(10) - 5);
        int bottleSmall = Math.max(0, baseSmall + random.nextInt(15) - 7);
        
        // Calculate derived values
        int totalBottles = bottleLarge + bottleSmall;
        int binLevel = Math.min(100, (totalBottles * 3) + random.nextInt(20));
        int totalRewards = (bottleLarge * 2) + bottleSmall; // Large bottles worth more
        int totalWeight = (bottleLarge * 500) + (bottleSmall * 300); // grams
        int coinStock = Math.max(50, 200 - (totalRewards / 2) + random.nextInt(50));
        
        // Determine status
        String status = "normal";
        if (binLevel >= 95) {
            status = "full";
        } else if (totalBottles == 0) {
            status = "empty";
        } else if (random.nextInt(20) == 0) { // 5% chance of maintenance
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
        String today = dateFormat.format(new Date());
        long timestamp = System.currentTimeMillis();
        
        BinHistoricalData todayData = generateDataForDate(today, timestamp, 0);
        String key = "bin_history/" + today;
        
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
