package com.qppd.plastech.Libs.Tablez;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomTableView extends View {

    private final int columns = 6;
    private final int visibleRows = 11; // 1 for header + 10 data rows
    private final String[] headers = {
            "Date & Time", "Size of plastic", "H and W before crushing",
            "H and W after crushing", "Weight", "Total Rewards"
    };

    private Paint borderPaint;
    private Paint textPaint;
    private Paint headerPaint;

    public CustomTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(2);
        borderPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28);
        textPaint.setAntiAlias(true);

        headerPaint = new Paint();
        headerPaint.setColor(Color.parseColor("#4CAF50")); // Green
        headerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float colWidth = width / columns;
        float rowHeight = height / visibleRows;

        // Draw header background
        canvas.drawRect(0, 0, width, rowHeight, headerPaint);

        // Draw table grid
        for (int i = 0; i <= visibleRows; i++) {
            canvas.drawLine(0, i * rowHeight, width, i * rowHeight, borderPaint);
        }

        for (int j = 0; j <= columns; j++) {
            canvas.drawLine(j * colWidth, 0, j * colWidth, height, borderPaint);
        }

        // Draw header text
        for (int j = 0; j < columns; j++) {
            canvas.drawText(headers[j], j * colWidth + 10, rowHeight / 2 + 10, textPaint);
        }

        // Draw row numbers (1. 2. 3. ...)
        for (int i = 1; i < visibleRows; i++) {
            canvas.drawText(i + ".", 10, (i + 1) * rowHeight - 10, textPaint);
        }
    }
}