package com.qppd.plastech.ui.update;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.qppd.plastech.databinding.FragmentUpdateBinding;

public class UpdateFragment extends Fragment {

    private FragmentUpdateBinding binding;
    private View root;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentUpdateBinding.inflate(inflater, container, false);
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