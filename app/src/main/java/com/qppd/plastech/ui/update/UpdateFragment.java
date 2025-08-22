package com.qppd.plastech.ui.update;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.qppd.plastech.R;
import com.qppd.plastech.databinding.FragmentUpdateBinding;

public class UpdateFragment extends Fragment {

    private FragmentUpdateBinding binding;
    private Context context;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUpdateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = root.getContext();

        TablePagerAdapter adapter = new TablePagerAdapter(requireActivity());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Daily");
                            break;
                        case 1:
                            tab.setText("Weekly");
                            break;
                        case 2:
                            tab.setText("Monthly");
                            break;
                    }
                }).attach();

        // Add animations to match HomeFragment style
        binding.tabLayout.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
        binding.viewPager.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}