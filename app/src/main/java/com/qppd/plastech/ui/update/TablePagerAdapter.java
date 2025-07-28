package com.qppd.plastech.ui.update;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TablePagerAdapter extends FragmentStateAdapter {
    public TablePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new DailyTableFragment();
            case 1: return new WeeklyTableFragment();
            case 2: return new MonthlyTableFragment();
            default: return new DailyTableFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

