package com.qppd.plastech.ui.monitor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qppd.plastech.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomHorizontalCalendar extends HorizontalScrollView {

    private LinearLayout calendarContainer;
    private Calendar currentCalendar;
    private OnDateSelectedListener onDateSelectedListener;
    private View selectedDayView;
    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

    public interface OnDateSelectedListener {
        void onDateSelected(String date, int day, int month, int year);
    }

    public CustomHorizontalCalendar(Context context) {
        super(context);
        init();
    }

    public CustomHorizontalCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomHorizontalCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true); // This makes the content fill the viewport

        calendarContainer = new LinearLayout(getContext());
        calendarContainer.setOrientation(LinearLayout.HORIZONTAL);
        calendarContainer.setPadding(8, 8, 8, 8); // Reduced padding for more space
        
        // Set layout parameters to make the container fill the full width
        HorizontalScrollView.LayoutParams containerParams = new HorizontalScrollView.LayoutParams(
            HorizontalScrollView.LayoutParams.MATCH_PARENT,
            HorizontalScrollView.LayoutParams.MATCH_PARENT
        );
        calendarContainer.setLayoutParams(containerParams);

        addView(calendarContainer);

        currentCalendar = Calendar.getInstance();
        setupCalendar();
    }

    private void setupCalendar() {
        // Set to August 2025 and start from day 20
        currentCalendar.set(2025, Calendar.AUGUST, 20);

        // Clear existing views
        calendarContainer.removeAllViews();

        // Create day views for August 20-28 only (9 days)
        for (int day = 20; day <= 28; day++) {
            currentCalendar.set(Calendar.DAY_OF_MONTH, day);
            View dayView = createDayView(currentCalendar, day);
            calendarContainer.addView(dayView);
        }

        // Set default selection to August 20 (first available date)
        selectDate(20);
        scrollToSelectedDate();
    }



    private View createDayView(Calendar calendar, int day) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dayView = inflater.inflate(R.layout.custom_calendar_day_item, calendarContainer, false);
        
        // Set layout parameters to make it stretch and fit
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, // width = 0dp (will use weight)
            LinearLayout.LayoutParams.MATCH_PARENT, // height = match_parent
            1.0f // weight = 1 (equal distribution)
        );
        dayView.setLayoutParams(params);
        
        TextView tvDayOfWeek = dayView.findViewById(R.id.tvDayOfWeek);
        TextView tvDay = dayView.findViewById(R.id.tvDay);
        TextView tvMonth = dayView.findViewById(R.id.tvMonth);
        
        // Set the values
        tvDayOfWeek.setText(dayOfWeekFormat.format(calendar.getTime()).toUpperCase());
        tvDay.setText(String.valueOf(day));
        tvMonth.setText(monthFormat.format(calendar.getTime()).toUpperCase());
        
        // Store the date in the tag
        String dateString = dateFormat.format(calendar.getTime());
        dayView.setTag(dateString);
        
        // Set click listener
        dayView.setOnClickListener(v -> {
            String clickedDate = (String) v.getTag();
            selectDateByView(v, clickedDate);
        });
        
        return dayView;
    }

    private void selectDate(int day) {
        // Calculate the index for our range (day 20 = index 0, day 21 = index 1, etc.)
        int index = day - 20;
        
        // Validate the day is within our range (20-28)
        if (day < 20 || day > 28 || index < 0 || index >= calendarContainer.getChildCount()) {
            return;
        }
        
        View child = calendarContainer.getChildAt(index);
        if (child != null) {
            selectDateByView(child, (String) child.getTag());
        }
    }

    private void selectDateByView(View dayView, String date) {
        // Deselect previous selection
        if (selectedDayView != null) {
            selectedDayView.setSelected(false);
            TextView prevTvDay = selectedDayView.findViewById(R.id.tvDay);
            TextView prevTvDayOfWeek = selectedDayView.findViewById(R.id.tvDayOfWeek);
            TextView prevTvMonth = selectedDayView.findViewById(R.id.tvMonth);
            
            prevTvDay.setTextColor(getContext().getColor(R.color.black));
            prevTvDayOfWeek.setTextColor(getContext().getColor(R.color.colorGray));
            prevTvMonth.setTextColor(getContext().getColor(R.color.colorGray));
        }
        
        // Select new date
        selectedDayView = dayView;
        selectedDayView.setSelected(true);
        selectedDate = date;
        
        TextView tvDay = dayView.findViewById(R.id.tvDay);
        TextView tvDayOfWeek = dayView.findViewById(R.id.tvDayOfWeek);
        TextView tvMonth = dayView.findViewById(R.id.tvMonth);
        
        tvDay.setTextColor(getContext().getColor(R.color.white));
        tvDayOfWeek.setTextColor(getContext().getColor(R.color.white));
        tvMonth.setTextColor(getContext().getColor(R.color.white));
        
        // Scroll to selected date
        scrollToSelectedDate();
        
        // Notify listener
        if (onDateSelectedListener != null) {
            try {
                String[] dateParts = date.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int dayInt = Integer.parseInt(dateParts[2]);
                onDateSelectedListener.onDateSelected(date, dayInt, month, year);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scrollToSelectedDate() {
        if (selectedDayView != null) {
            post(() -> {
                int scrollX = selectedDayView.getLeft() - (getWidth() / 2) + (selectedDayView.getWidth() / 2);
                smoothScrollTo(scrollX, 0);
            });
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.onDateSelectedListener = listener;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void selectDateByString(String date) {
        for (int i = 0; i < calendarContainer.getChildCount(); i++) {
            View child = calendarContainer.getChildAt(i);
            String childDate = (String) child.getTag();
            
            if (date.equals(childDate)) {
                selectDateByView(child, date);
                break;
            }
        }
    }

    public void goToAugust1st() {
        // Go to August 20th (first available date in our range)
        selectDate(20);
    }

    public void goToToday() {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == 2025 && today.get(Calendar.MONTH) == Calendar.AUGUST) {
            int todayDay = today.get(Calendar.DAY_OF_MONTH);
            // Only select today if it's within our available range (20-28)
            if (todayDay >= 20 && todayDay <= 28) {
                selectDate(todayDay);
            } else {
                selectDate(20); // Default to August 20th if today is outside our range
            }
        } else {
            selectDate(20); // Default to August 20th if not in August
        }
    }
}
