package com.example.hotel_management.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Booking;
import com.example.hotel_management.data.model.Customer;
import com.example.hotel_management.ui.adapter.BookingHistoryAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import java.util.Collections;
import java.util.List;

/**
 * Hoạt động Chi tiết Khách hàng (CustomerDetailActivity).
 * Hiển thị hồ sơ đầy đủ, lịch sử đặt phòng và các thống kê chi tiêu của một khách hàng.
 */
public class CustomerDetailActivity extends AppCompatActivity {

    private Customer customer; // Đối tượng khách hàng đang được xem
    private boolean showCCCD = false; // Cờ điều khiển việc ẩn/hiện mã số CCCD vì lý do bảo mật

    // Các thành phần giao diện Hero Section (Ảnh đại diện và Thống kê nhanh)
    private TextView tvHeroName, tvHeroPhone, tvHeroInitials;
    private TextView tvHeroBookings, tvHeroNights, tvHeroTotalSpent;
    private View viewHeroAvatarBg, layoutInfo, layoutHistory, layoutActions;
    private TabLayout tabLayout;
    private RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        // Lấy dữ liệu khách hàng từ Intent
        customer = (Customer) getIntent().getSerializableExtra("customer");
        if (customer == null) {
            finish();
            return;
        }

        initViews();
        setupTabs();
    }

    /**
     * Cập nhật lại dữ liệu khách hàng mỗi khi màn hình được hiển thị lại.
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshCustomerData();
    }

    /**
     * Tải lại thông tin khách hàng và lịch sử đặt phòng mới nhất từ Database.
     */
    private void refreshCustomerData() {
        com.example.hotel_management.data.db.CustomerRepository repo = new com.example.hotel_management.data.db.CustomerRepository(this);
        List<Booking> bookings = repo.getCustomerBookings(customer);
        customer = new Customer(
            customer.getId(),
            customer.getName(),
            customer.getCccd(),
            customer.getPhone(),
            customer.getEmail(),
            customer.getAddress(),
            customer.getNote(),
            bookings
        );
        bindData();
    }

    /**
     * Ánh xạ các View và thiết lập sự kiện nút bấm.
     */
    private void initViews() {
        findViewById(R.id.btnBackCustomerDetail).setOnClickListener(v -> finish());
        
        tvHeroName = findViewById(R.id.tvHeroCustomerName);
        tvHeroPhone = findViewById(R.id.tvHeroCustomerPhone);
        tvHeroInitials = findViewById(R.id.tvHeroCustomerInitials);
        viewHeroAvatarBg = findViewById(R.id.viewHeroCustomerAvatarBg);
        
        tvHeroBookings = findViewById(R.id.tvHeroStatBookings);
        tvHeroNights = findViewById(R.id.tvHeroStatNights);
        tvHeroTotalSpent = findViewById(R.id.tvHeroStatTotalSpent);
        
        tabLayout = findViewById(R.id.tabCustomerDetail);
        layoutInfo = findViewById(R.id.layoutCustomerInfoTab);
        layoutHistory = findViewById(R.id.layoutCustomerHistoryTab);
        layoutActions = findViewById(R.id.layoutCustomerActions);
        rvHistory = findViewById(R.id.rvBookingHistory);

        // Nút Sửa khách hàng
        findViewById(R.id.btnEditCustomer).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.hotel_management.management.CustomerFormActivity.class);
            intent.putExtra("customer", customer);
            startActivity(intent);
        });

        // Nút Xóa khách hàng
        findViewById(R.id.btnDeleteCustomer).setOnClickListener(v -> showDeleteConfirmation());
    }

    /**
     * Đổ dữ liệu từ đối tượng Customer vào các thành phần giao diện.
     */
    private void bindData() {
        TextView tvIdHeader = findViewById(R.id.tvCustomerDetailId);
        tvIdHeader.setText("ID #" + customer.getId());

        tvHeroName.setText(customer.getName());
        tvHeroPhone.setText(customer.getPhone());

        // Tính toán các chỉ số thống kê
        int bookingCount = customer.getBookings() != null ? customer.getBookings().size() : 0;
        tvHeroBookings.setText(String.valueOf(bookingCount));

        long totalAmount = 0;
        int totalNights = 0;
        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                totalAmount += b.getTotal();
                totalNights += b.getNights();
            }
        }
        tvHeroNights.setText(String.valueOf(totalNights));
        tvHeroTotalSpent.setText(String.format("%.1fM", totalAmount / 1000000.0));

        // Thiết lập Avatar dựa trên tên khách hàng
        String initials = getInitials(customer.getName());
        tvHeroInitials.setText(initials);
        int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b, 0xFF7b5ea7};
        int color = colors[Math.abs(customer.getName().hashCode()) % colors.length];
        
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(18 * getResources().getDisplayMetrics().density);
        gd.setColor(adjustAlpha(color, 0.15f));
        gd.setStroke(3, adjustAlpha(color, 0.4f));
        viewHeroAvatarBg.setBackground(gd);
        tvHeroInitials.setTextColor(color);

        // Thiết lập các hàng thông tin chi tiết
        setupInfoRow(findViewById(R.id.rowCCCD), "", "CCCD", getMaskedCCCD(), true);
        setupInfoRow(findViewById(R.id.rowPhoneDetail), "", "ĐIỆN THOẠI", customer.getPhone(), false);
        setupInfoRow(findViewById(R.id.rowEmailDetail), "", "EMAIL", customer.getEmail(), false);
        setupInfoRow(findViewById(R.id.rowAddress), "", "ĐỊA CHỈ", customer.getAddress(), false);

        // Hiển thị Ghi chú (nếu có)
        TextView tvNote = findViewById(R.id.tvCustomerNote);
        if (customer.getNote() != null && !customer.getNote().isEmpty()) {
            tvNote.setText(customer.getNote());
        } else {
            findViewById(R.id.cardCustomerNote).setVisibility(View.GONE);
        }

        // Thiết lập danh sách Lịch sử đặt phòng (RecyclerView)
        if (customer.getBookings() != null) {
            List<Booking> reversed = new java.util.ArrayList<>(customer.getBookings());
            Collections.reverse(reversed); // Hiển thị đơn mới nhất lên đầu
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
            rvHistory.setAdapter(new BookingHistoryAdapter(reversed));
        }
    }

    /**
     * Trả về mã số CCCD ở dạng đã được ẩn (ví dụ: 031••••••22) trừ khi người dùng nhấn xem.
     */
    private String getMaskedCCCD() {
        if (showCCCD || customer.getCccd().length() < 6) return customer.getCccd();
        return customer.getCccd().substring(0, 3) + "••••••" + customer.getCccd().substring(customer.getCccd().length() - 2);
    }

    /**
     * Thiết lập dữ liệu cho một hàng thông tin.
     */
    private void setupInfoRow(View row, String icon, String label, String value, boolean isCCCD) {
        ((TextView) row.findViewById(R.id.tvRowIcon)).setText(icon);
        ((TextView) row.findViewById(R.id.tvRowLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvRowValue)).setText(value);
        
        // Nếu là hàng CCCD, cho phép click để ẩn/hiện số thực tế
        if (isCCCD) {
            row.setOnClickListener(v -> {
                showCCCD = !showCCCD;
                bindData(); 
            });
        }
    }

    /**
     * Thiết lập cơ chế chuyển đổi giữa các Tab: Thông tin & Lịch sử.
     */
    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutInfo.setVisibility(View.VISIBLE);
                    layoutHistory.setVisibility(View.GONE);
                    layoutActions.setVisibility(View.VISIBLE);
                } else {
                    layoutInfo.setVisibility(View.GONE);
                    layoutHistory.setVisibility(View.VISIBLE);
                    layoutActions.setVisibility(View.GONE); // Ẩn các nút Xóa/Sửa khi ở tab Lịch sử
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * Hiển thị hộp thoại xác nhận trước khi xóa khách hàng.
     */
    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xoá khách hàng?")
                .setMessage("Toàn bộ thông tin và lịch sử của " + customer.getName() + " sẽ bị xoá vĩnh viễn.")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (dialog, which) -> {
                    com.example.hotel_management.data.db.CustomerRepository repo = new com.example.hotel_management.data.db.CustomerRepository(this);
                    repo.deleteCustomer(customer.getId());
                    Toast.makeText(this, "Đã xoá khách hàng " + customer.getName(), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    /**
     * Trả về chữ cái viết tắt của tên khách hàng để làm ảnh đại diện.
     */
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(name.length(), 2)).toUpperCase();
    }

    /**
     * Điều chỉnh độ trong suốt của màu sắc cho UI.
     */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        if (alpha == 0) alpha = Math.round(255 * factor);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
