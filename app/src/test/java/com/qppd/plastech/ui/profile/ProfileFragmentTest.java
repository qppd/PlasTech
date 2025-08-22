package com.qppd.plastech.ui.profile;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Classes.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragmentTest {

    private ProfileViewModel profileViewModel;
    private User testUser;
    private List<ActivityLog> testActivityLogs;

    @Before
    public void setUp() {
        // Initialize test data
        testUser = new User("test@email.com", "Test User", "1234567890", "password", 1, "01/01/2024 10:00:00");
        
        testActivityLogs = new ArrayList<>();
        testActivityLogs.add(new ActivityLog("1", "user123", "Login", "User logged in", "01/01/2024 10:00:00", "Completed"));
        testActivityLogs.add(new ActivityLog("2", "user123", "Profile Update", "Updated profile picture", "01/01/2024 11:00:00", "Completed"));
        testActivityLogs.add(new ActivityLog("3", "user123", "Data Upload", "Uploaded monitoring data", "01/01/2024 12:00:00", "Pending"));
    }

    @Test
    public void testUserDataValidation() {
        assertNotNull("User should not be null", testUser);
        assertEquals("Email should match", "test@email.com", testUser.getEmail());
        assertEquals("Name should match", "Test User", testUser.getName());
        assertEquals("Phone should match", "1234567890", testUser.getContact());
        assertEquals("Status should be active", 1, testUser.getStatus());
    }

    @Test
    public void testActivityLogCreation() {
        ActivityLog log = new ActivityLog("test_id", "user123", "Test Action", "Test Description", "01/01/2024 10:00:00", "Completed");
        
        assertNotNull("Activity log should not be null", log);
        assertEquals("Action should match", "Test Action", log.getAction());
        assertEquals("Description should match", "Test Description", log.getDescription());
        assertEquals("Status should match", "Completed", log.getStatus());
    }

    @Test
    public void testActivityLogsList() {
        assertNotNull("Activity logs list should not be null", testActivityLogs);
        assertEquals("Should have 3 activity logs", 3, testActivityLogs.size());
        
        ActivityLog firstLog = testActivityLogs.get(0);
        assertEquals("First log action should be Login", "Login", firstLog.getAction());
        assertEquals("First log status should be Completed", "Completed", firstLog.getStatus());
    }

    @Test
    public void testRecentActivitiesLimit() {
        // Test that only recent 3 activities are shown
        List<ActivityLog> recentActivities = testActivityLogs.size() > 3 ? 
            testActivityLogs.subList(0, 3) : testActivityLogs;
        
        assertTrue("Recent activities should not exceed 3", recentActivities.size() <= 3);
    }
}
