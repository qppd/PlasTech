package com.qppd.plastech.Utils;

import android.content.Context;
import android.widget.Toast;

import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;

/**
 * MonitorSetupHelper - Utility class to initialize the enhanced monitoring features
 * 
 * This class provides easy setup methods for:
 * - Generating initial dummy data for demonstrations
 * - Checking data integrity
 * - Setting up monitoring preferences
 */
public class MonitorSetupHelper {
    
    private Context context;
    private DummyDataGenerator dummyDataGenerator;
    private FirebaseRTDBHelper rtdbHelper;
    
    public MonitorSetupHelper(Context context) {
        this.context = context;
        this.dummyDataGenerator = new DummyDataGenerator();
        this.rtdbHelper = new FirebaseRTDBHelper("plastech");
    }
    
    /**
     * One-time setup for demo/presentation purposes
     * Generates comprehensive dummy data if none exists
     */
    public void initializeForDemo(SetupCallback callback) {
        Toast.makeText(context, "Setting up demo data...", Toast.LENGTH_SHORT).show();
        
        dummyDataGenerator.generateAndUploadDummyData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Demo data setup complete! Calendar is now fully functional.", Toast.LENGTH_LONG).show();
                if (callback != null) callback.onSetupComplete(true);
            }
            
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Setup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                if (callback != null) callback.onSetupComplete(false);
            }
        });
    }
    
    /**
     * Quick refresh of today's data
     */
    public void refreshTodaysData(SetupCallback callback) {
        dummyDataGenerator.generateTodaysData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Today's data refreshed successfully!", Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onSetupComplete(true);
            }
            
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Failed to refresh data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onSetupComplete(false);
            }
        });
    }
    
    public interface SetupCallback {
        void onSetupComplete(boolean success);
    }
}
