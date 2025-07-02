package com.qppd.plastech.ui.monitor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.qppd.plastech.Classes.Bin;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.R;
import com.qppd.plastech.databinding.FragmentMonitorBinding;

public class MonitorFragment extends Fragment {

    private FragmentMonitorBinding binding;
    private View root;
    private Context context;

    private TextView txtBottleLarge;
    private TextView txtBottleSmall;

    private ImageView imgBinFull;
    private TextView txtBinLevel;
    private TextView txtBinLevelDescription;

    private TextView txtTotalReward;
    private TextView txtTotalWeight;
    private TextView txtCoinStock;

    private FirebaseRTDBHelper<Bin> binFirebaseRTDBHelper = new FirebaseRTDBHelper<>("plastech");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();

        initializeComponents();
        loadBin();

        return root;
    }

    private void initializeComponents() {
        txtBottleLarge = root.findViewById(R.id.txtBottleLarge);
        txtBottleSmall = root.findViewById(R.id.txtBottleSmall);

        imgBinFull = root.findViewById(R.id.imgBinFull);
        txtBinLevel = root.findViewById(R.id.txtBinLevel);
        txtBinLevelDescription = root.findViewById(R.id.txtBinLevelDescription);

        txtTotalReward = root.findViewById(R.id.txtTotalReward);
        txtTotalWeight = root.findViewById(R.id.txtTotalWeight);
        txtCoinStock = root.findViewById(R.id.txtCoinStock);
    }

    private void loadBin() {
        binFirebaseRTDBHelper.getRef().child("bin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Bin bin = snapshot.getValue(Bin.class);

                    txtBottleLarge.setText(String.format("%02d", bin.getBottle_large()));
                    txtBottleSmall.setText(String.format("%02d", bin.getBottle_small()));

                    if(bin.getBin_level() < 100){
                        imgBinFull.setVisibility(View.GONE);
                        txtBinLevel.setVisibility(View.VISIBLE);
                        txtBinLevel.setText(String.format("%02d", bin.getBin_level()));
                        txtBinLevelDescription.setText("BIN\nLEVEL");

                    }
                    else{ // Bin is full
                        imgBinFull.setVisibility(View.VISIBLE);
                        txtBinLevel.setVisibility(View.GONE);
                        txtBinLevelDescription.setText("BIN IS FULL!\n(This warning will disappear\nonce the bin is emptied)");
                    }


                    txtTotalReward.setText(String.format("%02d", bin.getTotal_rewards()));
                    txtTotalWeight.setText(String.format("%02d", bin.getTotal_weight()));
                    txtTotalWeight.setText(String.format("%02d", bin.getCoin_stock()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}