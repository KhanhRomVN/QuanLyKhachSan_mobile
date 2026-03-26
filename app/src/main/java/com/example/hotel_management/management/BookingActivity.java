package com.example.hotel_management.management;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.R;
import com.google.android.material.card.MaterialCardView;
import com.example.hotel_management.data.db.RoomRepository;
import com.example.hotel_management.data.model.Customer;
import com.example.hotel_management.data.model.Room;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private Room room;
    private java.util.List<Customer> customers;

    private TextView tvRoomInfo, tvRoomPrice;
    private android.widget.LinearLayout layoutCustomerCards;
    private TextView tvCheckInDate, tvCheckOutDate, tvNights, tvTotalPrice, tvPriceDetail;
    private MaterialButton btnDecreaseNights, btnIncreaseNights, btnConfirmBooking;

    private Calendar checkInCalendar;
    private int nights = 1;
    private double pricePerNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        room = (Room) getIntent().getSerializableExtra("room");
        customers = (java.util.List<Customer>) getIntent().getSerializableExtra("selectedCustomers");

        if (room == null || customers == null || customers.isEmpty()) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin khách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pricePerNight = room.getPrice();
        checkInCalendar = Calendar.getInstance();

        initViews();
        setupData();
        setupListeners();
        updateDisplay();
    }

    private void initViews() {
        findViewById(R.id.btnBackBooking).setOnClickListener(v -> finish());

        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        layoutCustomerCards = findViewById(R.id.layoutCustomerCards);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvNights = findViewById(R.id.tvNights);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        btnDecreaseNights = findViewById(R.id.btnDecreaseNights);
        btnIncreaseNights = findViewById(R.id.btnIncreaseNights);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
    }

    private void setupData() {
        // Room info
        String typeLabel = getTypeLabel(room.getType());
        tvRoomInfo.setText("Phòng " + room.getNumber() + " · " + typeLabel);
        tvRoomPrice.setText(formatPrice(pricePerNight) + " VNĐ/đêm");

        // Customer info
        layoutCustomerCards.removeAllViews();
        for (Customer c : customers) {
            View card = getLayoutInflater().inflate(R.layout.item_customer, layoutCustomerCards, false);
            
            TextView tvName = card.findViewById(R.id.tvCustomerName);
            TextView tvIdInfo = card.findViewById(R.id.tvCustomerIDInfo);
            TextView tvInitials = card.findViewById(R.id.tvCustomerInitials);
            View avatarBg = card.findViewById(R.id.viewCustomerAvatarBg);
            
            tvName.setText(c.getName());
            String cccd = c.getCccd() != null && !c.getCccd().isEmpty() ? c.getCccd() : "-";
            String phone = c.getPhone() != null && !c.getPhone().isEmpty() ? c.getPhone() : "-";
            tvIdInfo.setText("CCCD: " + cccd + " | SĐT: " + phone);
            
            // Initials & Avatar Styling (Squircle style)
            if (c.getName() != null && !c.getName().isEmpty()) {
                String initials = "";
                String[] parts = c.getName().trim().split("\\s+");
                if (parts.length >= 2) {
                    initials = (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
                } else if (parts.length > 0) {
                    initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
                }
                tvInitials.setText(initials);
                
                int colorIndex = Math.abs(c.getName().hashCode()) % 6;
                int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b, 0xFF7b5ea7};
                int color = colors[colorIndex];
                
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                gd.setCornerRadius(12 * getResources().getDisplayMetrics().density);
                
                int alphaBg = Math.round(255 * 0.1f);
                int bgColor = Color.argb(alphaBg, Color.red(color), Color.green(color), Color.blue(color));
                gd.setColor(bgColor);
                
                int alphaStroke = Math.round(255 * 0.25f);
                int strokeColor = Color.argb(alphaStroke, Color.red(color), Color.green(color), Color.blue(color));
                gd.setStroke((int)(1 * getResources().getDisplayMetrics().density), strokeColor);
                
                avatarBg.setBackground(gd);
                tvInitials.setTextColor(color);
            }
            
            // Card style for booking summary
            com.google.android.material.card.MaterialCardView materialCard = (com.google.android.material.card.MaterialCardView) card;
            materialCard.setCardBackgroundColor(getResources().getColor(R.color.card_bg));
            materialCard.setStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.card_border)));
            materialCard.setStrokeWidth((int)(1 * getResources().getDisplayMetrics().density));
            
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) materialCard.getLayoutParams();
            if (params == null) {
                params = new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            params.setMargins(0, 0, 0, (int)(10 * getResources().getDisplayMetrics().density));
            materialCard.setLayoutParams(params);
            
            layoutCustomerCards.addView(card);
        }
    }

    private void setupListeners() {
        // Check-in date interaction removed as requested

        btnDecreaseNights.setOnClickListener(v -> {
            if (nights > 1) {
                nights--;
                updateDisplay();
            }
        });

        btnIncreaseNights.setOnClickListener(v -> {
            if (nights < 30) {
                nights++;
                updateDisplay();
            }
        });

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                checkInCalendar.set(Calendar.YEAR, year);
                checkInCalendar.set(Calendar.MONTH, month);
                checkInCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDisplay();
            },
            checkInCalendar.get(Calendar.YEAR),
            checkInCalendar.get(Calendar.MONTH),
            checkInCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 86400000);
        datePickerDialog.show();
    }

    private void updateDisplay() {
        // Check-in date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String checkInStr = sdf.format(checkInCalendar.getTime());
        
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar checkInCopy = (Calendar) checkInCalendar.clone();
        checkInCopy.set(Calendar.HOUR_OF_DAY, 0);
        checkInCopy.set(Calendar.MINUTE, 0);
        checkInCopy.set(Calendar.SECOND, 0);
        checkInCopy.set(Calendar.MILLISECOND, 0);
        
        if (checkInCopy.equals(today)) {
            tvCheckInDate.setText("Hôm nay · " + checkInStr);
        } else if (checkInCopy.getTimeInMillis() == today.getTimeInMillis() + 86400000) {
            tvCheckInDate.setText("Ngày mai · " + checkInStr);
        } else {
            tvCheckInDate.setText(checkInStr);
        }

        // Check-out date
        Calendar checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, nights);
        String checkOutStr = sdf.format(checkOutCalendar.getTime());
        
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        
        if (checkOutCalendar.equals(today)) {
            tvCheckOutDate.setText("Hôm nay · " + checkOutStr);
        } else if (checkOutCalendar.equals(tomorrow)) {
            tvCheckOutDate.setText("Ngày mai · " + checkOutStr);
        } else {
            tvCheckOutDate.setText(checkOutStr);
        }

        // Nights
        tvNights.setText(String.valueOf(nights));

        // Total price
        double totalPrice = pricePerNight * nights;
        tvTotalPrice.setText(formatPrice(totalPrice) + " VNĐ");
        tvPriceDetail.setText(nights + " đêm × " + formatPrice(pricePerNight) + "K");
    }

    private void confirmBooking() {
        // Update room status in database
        RoomRepository repo = new RoomRepository(this);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String checkInStr = sdf.format(checkInCalendar.getTime());
        Calendar checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, nights);
        String checkOutStr = sdf.format(checkOutCalendar.getTime());
        
        StringBuilder guestBuilder = new StringBuilder();
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            String cccd = c.getCccd() != null ? c.getCccd() : "";
            String phone = c.getPhone() != null ? c.getPhone() : "";
            guestBuilder.append(c.getName()).append("|").append(cccd).append("|").append(phone);
            if (i < customers.size() - 1) {
                guestBuilder.append(";;");
            }
        }
        
        repo.updateRoomStatus(room.getId(), "occupied", guestBuilder.toString(), checkInStr, checkOutStr);

        StringBuilder guestNames = new StringBuilder();
        for (int i = 0; i < customers.size(); i++) {
            guestNames.append(customers.get(i).getName());
            if (i < customers.size() - 1) guestNames.append(", ");
        }

        Toast.makeText(this, 
            "Đặt phòng thành công!\n" +
            "Khách: " + guestNames.toString() + "\n" +
            "Phòng: " + room.getNumber() + "\n" +
            "Số đêm: " + nights, 
            Toast.LENGTH_LONG).show();
        
        Intent result = new Intent();
        result.putExtra("roomId", room.getId());
        if (!customers.isEmpty()) {
            result.putExtra("customerId", customers.get(0).getId());
            
            ArrayList<Integer> customerIds = new ArrayList<>();
            for (Customer c : customers) {
                customerIds.add(c.getId());
            }
            result.putIntegerArrayListExtra("customerIds", customerIds);
        }
        result.putExtra("nights", nights);
        setResult(RESULT_OK, result);
        finish();
    }

    private String getTypeLabel(String type) {
        if (type == null) return "Đơn";
        switch (type.toLowerCase()) {
            case "single": return "Đơn";
            case "double": return "Đôi";
            case "quad": return "4 người";
            default: return type;
        }
    }

    private String formatPrice(double price) {
        if (price >= 1000000) {
            return String.format(Locale.getDefault(), "%.1fM", price / 1000000.0);
        } else if (price >= 1000) {
            return String.format(Locale.getDefault(), "%.0fK", price / 1000.0);
        }
        return String.valueOf((int) price);
    }
}
