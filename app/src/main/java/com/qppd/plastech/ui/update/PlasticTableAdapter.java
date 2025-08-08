package com.qppd.plastech.ui.update;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.qppd.plastech.R;

public class PlasticTableAdapter extends AbstractTableAdapter<String, String, String> {
    private final Context context;
    private static final int REWARD_COLUMN_INDEX = 5;

    public PlasticTableAdapter(Context context) {
        super();
        this.context = context;
    }

    public static class CellViewHolder extends AbstractViewHolder {
        public final TextView cell_textview;

        public CellViewHolder(@NonNull View itemView) {
            super(itemView);
            cell_textview = itemView.findViewById(R.id.cell_data);
        }
    }

    public static class RewardCellViewHolder extends AbstractViewHolder {
        public final TextView cell_textview;

        public RewardCellViewHolder(@NonNull View itemView) {
            super(itemView);
            cell_textview = itemView.findViewById(R.id.cell_data);
        }
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int columnPosition) {
        if (columnPosition == REWARD_COLUMN_INDEX) {
            return 1; // View type for the reward column
        }
        return 0;
    }

    public static class ColumnHeaderViewHolder extends AbstractViewHolder {
        public final TextView column_header_textView;

        public ColumnHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            column_header_textView = itemView.findViewById(R.id.column_header_textView);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View layout = LayoutInflater.from(context)
                    .inflate(R.layout.table_view_reward_cell_layout, parent, false);
            return new RewardCellViewHolder(layout);
        } else {
            View layout = LayoutInflater.from(context)
                    .inflate(R.layout.table_view_cell_layout, parent, false);
            return new CellViewHolder(layout);
        }
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable String cellItemModel, int columnPosition, int rowPosition) {

        if (holder.getItemViewType() == 1) {
            RewardCellViewHolder viewHolder = (RewardCellViewHolder) holder;
            viewHolder.cell_textview.setText(cellItemModel);
        } else {
            CellViewHolder viewHolder = (CellViewHolder) holder;
            viewHolder.cell_textview.setText(cellItemModel);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context)
                .inflate(R.layout.table_view_column_header_layout, parent, false);
        return new ColumnHeaderViewHolder(layout);
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable String columnHeaderItemModel, int columnPosition) {
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.column_header_textView.setText(columnHeaderItemModel);
        columnHeaderViewHolder.column_header_textView.setTextColor(Color.BLACK);
        
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Column " + columnPosition + " clicked.", Toast.LENGTH_SHORT).show();
        });
    }

    public static class RowHeaderViewHolder extends AbstractViewHolder {
        public final TextView row_header_textView;

        public RowHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            row_header_textView = itemView.findViewById(R.id.row_header_textview);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context)
                .inflate(R.layout.table_view_row_header_layout, parent, false);
        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable String rowHeaderItemModel, int rowPosition) {
        // This is not used anymore
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.table_view_corner_layout, parent, false);
    }
}
