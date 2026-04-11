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

/**
 * Hoạt động Đặt phòng (BookingActivity).
 * Xử lý quy trình xác nhận đặt phòng cho khách hàng, bao gồm chọn ngày, 
 * tính toán tổng phí và cập nhật trạng thái phòng trong hệ thống.
 */
public class BookingActivity extends AppCompatActivity {

    private Room room; // Phòng khách chọn đặt
    private java.util.List<Customer> customers; // Danh sách các khách hàng sẽ ở trong phòng

    private TextView tvRoomInfo, tvRoomPrice;
    private android.widget.LinearLayout layoutCustomerCards;
    private TextView tvCheckInDate, tvCheckOutDate, tvNights, tvTotalPrice, tvPriceDetail;
    private MaterialButton btnDecreaseNights, btnIncreaseNights, btnConfirmBooking;

    private Calendar checkInCalendar; // Lịch lưu ngày nhận phòng (mặc định là hôm nay)
    private int nights = 1; // Số đêm lưu trú (mặc định là 1)
    private double pricePerNight; // Giá mỗi đêm của phòng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Nhận đối tượng phòng và danh sách khách hàng từ Intent truyền sang
        room = (Room) getIntent().getSerializableExtra("room");
        customers = (java.util.List<Customer>) getIntent().getSerializableExtra("selectedCustomers");

        if (room == null || customers == null || customers.isEmpty()) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin khách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pricePerNight = room.getPrice();
        checkInCalendar = Calendar.getInstance(); // Khởi tạo thời gian hiện tại

        initViews();
        setupData();
        setupListeners();
        updateDisplay();
    }

    /**
     * Ánh xạ các thành phần giao diện.
     */
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

    /**
     * Hiển thị thông tin phòng và danh sách khách hàng lên giao diện.
     */
    private void setupData() {
        // Thông tin phòng
        String typeLabel = getTypeLabel(room.getType());
        tvRoomInfo.setText("Phòng " + room.getNumber() + " · " + typeLabel);
        tvRoomPrice.setText(formatPrice(pricePerNight) + " VNĐ/đêm");

        // Tạo thẻ hiển thị cho từng khách hàng trong danh sách
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
            
            // Tạo ảnh đại diện viết tắt và màu sắc ngẫu nhiên chuyên nghiệp
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
                gd.setColor(Color.argb(Math.round(255 * 0.1f), Color.red(color), Color.green(color), Color.blue(color)));
                gd.setStroke((int)(1 * getResources().getDisplayMetrics().density), Color.argb(Math.round(255 * 0.25f), Color.red(color), Color.green(color), Color.blue(color)));
                
                avatarBg.setBackground(gd);
                tvInitials.setTextColor(color);
            }
            
            layoutCustomerCards.addView(card);
        }
    }

    /**
     * Thiết lập các sự kiện nút bấm.
     */
    private void setupListeners() {
        // Giảm số đêm (tối thiểu 1)
        btnDecreaseNights.setOnClickListener(v -> {
            if (nights > 1) {
                nights--;
                updateDisplay();
            }
        });

        // Tăng số đêm (tối đa 30)
        btnIncreaseNights.setOnClickListener(v -> {
            if (nights < 30) {
                nights++;
                updateDisplay();
            }
        });

        // Nút xác nhận đặt phòng
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    /**
     * Cập nhật hiển thị ngày tháng và tổng tiền trên giao diện.
     */
    private void updateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        // Hiển thị ngày nhận phòng
        String checkInStr = sdf.format(checkInCalendar.getTime());
        tvCheckInDate.setText(checkInStr);

        // Tính toán và hiển thị ngày trả phòng dự kiến
        Calendar checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, nights);
        String checkOutStr = sdf.format(checkOutCalendar.getTime());
        tvCheckOutDate.setText(checkOutStr);

        // Cập nhật số đêm và tổng tiền
        tvNights.setText(String.valueOf(nights));
        double totalPrice = pricePerNight * nights;
        tvTotalPrice.setText(formatPrice(totalPrice) + " VNĐ");
        tvPriceDetail.setText(nights + " đêm × " + formatPrice(pricePerNight) + "K");
    }

    /**
     * Thực hiện lưu trữ thông tin đặt phòng vào Database.
     */
    private void confirmBooking() {
        RoomRepository repo = new RoomRepository(this);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String checkInStr = sdf.format(checkInCalendar.getTime());
        
        Calendar checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, nights);
        String checkOutStr = sdf.format(checkOutCalendar.getTime());
        
        // Chuyển đổi danh sách khách hàng sang chuỗi định dạng đặc biệt để lưu vào trường 'guests' trong DB
        // Định dạng: Tên|CCCD|SĐT;;Tên|CCCD|SĐT
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
        
        // Cập nhật trạng thái phòng thành 'occupied' (đang có khách) và lưu thông tin khách trú ngụ
        repo.updateRoomStatus(room.getId(), "occupied", guestBuilder.toString(), checkInStr, checkOutStr);

        // Hiển thị thông báo thành công
        StringBuilder guestNames = new StringBuilder();
        for (int i = 0; i < customers.size(); i++) {
            guestNames.append(customers.get(i).getName());
            if (i < customers.size() - 1) guestNames.append(", ");
        }

        Toast.makeText(this, 
            "Đặt phòng thành công!\n" +
            "Khách: " + guestNames.toString() + "\n" +
            "Phòng: " + room.getNumber(), 
            Toast.LENGTH_LONG).show();
        
        // Trả kết quả về cho màn hình trước đó và đóng Activity
        Intent result = new Intent();
        result.putExtra("roomId", room.getId());
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * Chuyển mã loại phòng sang nhãn tiếng Việt.
     */
    private String getTypeLabel(String type) {
        if (type == null) return "Đơn";
        switch (type.toLowerCase()) {
            case "single": return "Phòng đơn";
            case "double": return "Phòng đôi";
            case "quad": return "Phòng 4 người";
            default: return type;
        }
    }

    /**
     * Định dạng giá tiền rút gọn (Ví dụ: 1.5M, 500K).
     */
    private String formatPrice(double price) {
        if (price >= 1000000) {
            return String.format(Locale.getDefault(), "%.1fM", price / 1000000.0);
        } else if (price >= 1000) {
            return String.format(Locale.getDefault(), "%.0fK", price / 1000.0);
        }
        return String.valueOf((int) price);
    }
}
