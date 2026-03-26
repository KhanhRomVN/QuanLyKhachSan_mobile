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

public class CustomerDetailActivity extends AppCompatActivity {

    private Customer customer;
    private boolean showCCCD = false;

    private TextView tvHeroName, tvHeroPhone, tvHeroInitials;
    private TextView tvHeroBookings, tvHeroNights, tvHeroTotalSpent;
    private View viewHeroAvatarBg, layoutInfo, layoutHistory, layoutActions;
    private TabLayout tabLayout;
    private RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        customer = (Customer) getIntent().getSerializableExtra("customer");
        if (customer == null) {
            finish();
            return;
        }

        initViews();
        // bindData() will be called from onResume()
        setupTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCustomerData();
    }

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

        findViewById(R.id.btnEditCustomer).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.hotel_management.management.CustomerFormActivity.class);
            intent.putExtra("customer", customer);
            startActivity(intent);
        });

        findViewById(R.id.btnDeleteCustomer).setOnClickListener(v -> showDeleteConfirmation());
    }

    private void bindData() {
        TextView tvIdHeader = findViewById(R.id.tvCustomerDetailId);
        tvIdHeader.setText("ID #" + customer.getId());

        tvHeroName.setText(customer.getName());
        tvHeroPhone.setText(customer.getPhone());

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

        // Initials Avatar
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

        // Info Tab Rows
        setupInfoRow(findViewById(R.id.rowCCCD), "", "CCCD", getMaskedCCCD(), true);
        setupInfoRow(findViewById(R.id.rowPhoneDetail), "", "ĐIỆN THOẠI", customer.getPhone(), false);
        setupInfoRow(findViewById(R.id.rowEmailDetail), "", "EMAIL", customer.getEmail(), false);
        setupInfoRow(findViewById(R.id.rowAddress), "", "ĐỊA CHỈ", customer.getAddress(), false);

        // Note
        TextView tvNote = findViewById(R.id.tvCustomerNote);
        if (customer.getNote() != null && !customer.getNote().isEmpty()) {
            tvNote.setText(customer.getNote());
        } else {
            findViewById(R.id.cardCustomerNote).setVisibility(View.GONE);
        }

        // History Setup
        if (customer.getBookings() != null) {
            List<Booking> reversed = new java.util.ArrayList<>(customer.getBookings());
            Collections.reverse(reversed);
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
            rvHistory.setAdapter(new BookingHistoryAdapter(reversed));
        }
    }

    private String getMaskedCCCD() {
        if (showCCCD || customer.getCccd().length() < 6) return customer.getCccd();
        return customer.getCccd().substring(0, 3) + "••••••" + customer.getCccd().substring(customer.getCccd().length() - 2);
    }

    private void setupInfoRow(View row, String icon, String label, String value, boolean isCCCD) {
        ((TextView) row.findViewById(R.id.tvRowIcon)).setText(icon);
        ((TextView) row.findViewById(R.id.tvRowLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvRowValue)).setText(value);
        
        if (isCCCD) {
            row.setOnClickListener(v -> {
                showCCCD = !showCCCD;
                bindData(); // Refresh to update masked CCCD
            });
        }
    }

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
                    layoutActions.setVisibility(View.GONE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

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

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(name.length(), 2)).toUpperCase();
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        if (alpha == 0) alpha = Math.round(255 * factor);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
