package com.qppd.plastech.ui.monitor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.qppd.plastech.databinding.FragmentMonitorBinding;

public class MonitorFragment extends Fragment {

    private FragmentMonitorBinding binding;
    private View root;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}