package com.example.hotel_management.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.hotel_management.data.model.MonthlyStat;
import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private Paint barPaint, textPaint, activeBarPaint;
    private List<MonthlyStat> data = new ArrayList<>();
    private int selectedIndex = 11; // Default to last month
    private OnMonthSelectedListener listener;
    private long maxVal = 0;
    private String metric = "revenue"; // revenue, bookings, occupancy

    public interface OnMonthSelectedListener {
        void onMonthSelected(int index, MonthlyStat stat);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(0xFFb0ab9e);

        activeBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setData(List<MonthlyStat> data, String metric) {
        this.data = data;
        this.metric = metric;
        calculateMax();
        invalidate();
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        invalidate();
    }

    public void setOnMonthSelectedListener(OnMonthSelectedListener listener) {
        this.listener = listener;
    }

    private void calculateMax() {
        maxVal = 0;
        for (MonthlyStat s : data) {
            long val = getVal(s);
            if (val > maxVal) maxVal = val;
        }
    }

    private long getVal(MonthlyStat s) {
        switch (metric) {
            case "bookings": return s.getBookings();
            case "occupancy": return s.getOccupancy();
            default: return s.getRevenue();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.isEmpty()) return;

        int w = getWidth();
        int h = getHeight();
        int paddingBottom = 40;
        int chartH = h - paddingBottom;
        int barCount = data.size();
        float spacing = 12f;
        float barW = (w - (spacing * (barCount + 1))) / barCount;

        for (int i = 0; i < barCount; i++) {
            MonthlyStat s = data.get(i);
            long val = getVal(s);
            float pct = maxVal > 0 ? (float) val / maxVal : 0.1f;
            if (pct < 0.1f) pct = 0.1f;

            float left = spacing + i * (barW + spacing);
            float top = chartH - (pct * chartH * 0.9f);
            float right = left + barW;
            float bottom = chartH;

            RectF rect = new RectF(left, top, right, bottom);
            
            if (i == selectedIndex) {
                activeBarPaint.setShader(new LinearGradient(0, top, 0, bottom, 0xFF9a7340, 0x999a7340, Shader.TileMode.CLAMP));
                canvas.drawRoundRect(rect, 8f, 8f, activeBarPaint);
                
                // Active stroke
                activeBarPaint.setShader(null);
                activeBarPaint.setStyle(Paint.Style.STROKE);
                activeBarPaint.setStrokeWidth(2f);
                activeBarPaint.setColor(0xFF9a7340);
                canvas.drawRoundRect(rect, 8f, 8f, activeBarPaint);
                activeBarPaint.setStyle(Paint.Style.FILL);
                
                textPaint.setColor(0xFF9a7340);
                textPaint.setFakeBoldText(true);
            } else {
                barPaint.setColor(0x339a7340);
                canvas.drawRoundRect(rect, 8f, 8f, barPaint);
                textPaint.setColor(0xFFb0ab9e);
                textPaint.setFakeBoldText(false);
            }

            canvas.drawText(s.getMonth(), left + barW / 2, h - 10, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            int w = getWidth();
            int barCount = data.size();
            float spacing = 12f;
            float barW = (w - (spacing * (barCount + 1))) / barCount;

            for (int i = 0; i < barCount; i++) {
                float left = spacing + i * (barW + spacing);
                float right = left + barW;
                if (x >= left && x <= right) {
                    selectedIndex = i;
                    if (listener != null) listener.onMonthSelected(i, data.get(i));
                    invalidate();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
