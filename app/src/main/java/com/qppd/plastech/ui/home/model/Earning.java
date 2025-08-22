package com.qppd.plastech.ui.home.model;

public class Earning {
    private double totalEarnings;
    private double todayEarnings;
    private double thisWeekEarnings;
    private double thisMonthEarnings;
    private String userLevel;
    private int progressToNextLevel; // Percentage
    private double nextMilestone;

    public Earning() {
        // Default constructor
    }

    public Earning(double totalEarnings, double todayEarnings, double thisWeekEarnings, 
                   double thisMonthEarnings, String userLevel, int progressToNextLevel, double nextMilestone) {
        this.totalEarnings = totalEarnings;
        this.todayEarnings = todayEarnings;
        this.thisWeekEarnings = thisWeekEarnings;
        this.thisMonthEarnings = thisMonthEarnings;
        this.userLevel = userLevel;
        this.progressToNextLevel = progressToNextLevel;
        this.nextMilestone = nextMilestone;
    }

    // Getters and Setters
    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getTodayEarnings() {
        return todayEarnings;
    }

    public void setTodayEarnings(double todayEarnings) {
        this.todayEarnings = todayEarnings;
    }

    public double getThisWeekEarnings() {
        return thisWeekEarnings;
    }

    public void setThisWeekEarnings(double thisWeekEarnings) {
        this.thisWeekEarnings = thisWeekEarnings;
    }

    public double getThisMonthEarnings() {
        return thisMonthEarnings;
    }

    public void setThisMonthEarnings(double thisMonthEarnings) {
        this.thisMonthEarnings = thisMonthEarnings;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public int getProgressToNextLevel() {
        return progressToNextLevel;
    }

    public void setProgressToNextLevel(int progressToNextLevel) {
        this.progressToNextLevel = progressToNextLevel;
    }

    public double getNextMilestone() {
        return nextMilestone;
    }

    public void setNextMilestone(double nextMilestone) {
        this.nextMilestone = nextMilestone;
    }

    // Helper method to format earnings with peso symbol
    public String getFormattedTotalEarnings() {
        return String.format("₱%.2f", totalEarnings);
    }

    public String getFormattedTodayEarnings() {
        return String.format("₱%.2f", todayEarnings);
    }

    public String getFormattedThisWeekEarnings() {
        return String.format("₱%.2f", thisWeekEarnings);
    }

    public String getFormattedThisMonthEarnings() {
        return String.format("₱%.2f", thisMonthEarnings);
    }

    public String getFormattedNextMilestone() {
        return String.format("₱%.2f", nextMilestone);
    }
}
