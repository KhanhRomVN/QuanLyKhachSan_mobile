package com.example.hotel_management.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Staff;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.hotel_management.utils.SessionManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.text.InputType;

public class StaffDetailActivity extends AppCompatActivity {

    private Staff staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);

        staff = (Staff) getIntent().getSerializableExtra("staff");
        if (staff == null) {
            finish();
            return;
        }

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btnBackStaffDetail).setOnClickListener(v -> finish());
        
        TextView tvId = findViewById(R.id.tvStaffId);
        tvId.setText("ID #" + staff.getId());
        

        // Hero Section
        TextView tvHeroName = findViewById(R.id.tvHeroName);
        tvHeroName.setText(staff.getName());
        
        TextView tvHeroAvatarInitials = findViewById(R.id.tvHeroAvatarInitials);
        tvHeroAvatarInitials.setText(staff.getAvatar());
        
        View viewHeroAvatarBg = findViewById(R.id.viewHeroAvatarBg);
        int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b};
        int colorIdx = 0;
        if (staff.getAvatar() != null) {
            colorIdx = Math.abs(staff.getAvatar().hashCode()) % colors.length;
        }
        int baseColor = colors[colorIdx];
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.RECTANGLE);
        avatarBg.setCornerRadius(18 * getResources().getDisplayMetrics().density);
        avatarBg.setColor(adjustAlpha(baseColor, 0.15f));
        avatarBg.setStroke(3, adjustAlpha(baseColor, 0.4f));
        viewHeroAvatarBg.setBackground(avatarBg);
        tvHeroAvatarInitials.setTextColor(baseColor);

        TextView tvRoleBadge = findViewById(R.id.tvHeroRoleBadge);
        tvRoleBadge.setText(staff.getRole());
        

        // Info Rows
        setupInfoRow(findViewById(R.id.rowEmail), "", "EMAIL", staff.getEmail());
        setupInfoRow(findViewById(R.id.rowPhone), "", "ĐIỆN THOẠI", staff.getPhone());
        setupInfoRow(findViewById(R.id.rowRole), "", "CHỨC VỤ", staff.getRole());


        // Session & Permissions
        SessionManager sessionManager = new SessionManager(this);
        String currentUserEmail = sessionManager.getUserEmail();
        String currentUserRole = sessionManager.getUserRole();
        boolean isAdmin = "admin".equalsIgnoreCase(currentUserRole);

        // Actions
        View btnFastSwitch = findViewById(R.id.btnFastSwitch);
        View btnEdit = findViewById(R.id.btnEditStaff);
        View btnDelete = findViewById(R.id.btnDeleteStaff);

        // Hide Fast Switch if viewing own profile
        if (staff.getEmail().equalsIgnoreCase(currentUserEmail)) {
            btnFastSwitch.setVisibility(View.GONE);
        } else {
            btnFastSwitch.setOnClickListener(v -> showFastSwitchDialog());
        }

        // Only Admin can edit/delete others
        if (!isAdmin && !staff.getEmail().equalsIgnoreCase(currentUserEmail)) {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        } else {
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, StaffFormActivity.class);
                intent.putExtra("staff", staff);
                startActivity(intent);
            });
            btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        }
    }

    private void showFastSwitchDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Đăng nhập nhanh");
        builder.setMessage("Nhập mật khẩu cho " + staff.getName());

        final EditText input = new EditText(this);
        input.setHint("Mật khẩu");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) (24 * getResources().getDisplayMetrics().density);
        params.rightMargin = (int) (24 * getResources().getDisplayMetrics().density);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            String password = input.getText().toString();
            if (password.equals(staff.getPassword())) {
                SessionManager sm = new SessionManager(this);
                sm.setLogin(true, staff.getEmail(), staff.getRole().equalsIgnoreCase("admin") ? "admin" : "staff");
                
                Toast.makeText(this, "Đã chuyển sang tài khoản " + staff.getName(), Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(this, com.example.hotel_management.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void setupInfoRow(View row, String icon, String label, String value) {
        ((TextView) row.findViewById(R.id.tvRowIcon)).setText(icon);
        ((TextView) row.findViewById(R.id.tvRowLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvRowValue)).setText(value);
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xoá nhân viên?")
                .setMessage("Tài khoản của " + staff.getName() + " sẽ bị xoá vĩnh viễn.")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (dialog, which) -> {
                    com.example.hotel_management.data.db.StaffRepository repo = new com.example.hotel_management.data.db.StaffRepository(this);
                    repo.deleteStaff(staff.getId());
                    Toast.makeText(this, "Đã xoá nhân viên " + staff.getName(), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        if (alpha == 0) alpha = Math.round(255 * factor);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
