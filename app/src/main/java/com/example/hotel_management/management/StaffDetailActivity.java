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
        android.util.Log.d("StaffDebug", "StaffDetailActivity: onCreate bắt đầu");
        setContentView(R.layout.activity_staff_detail);

        try {
            staff = (Staff) getIntent().getSerializableExtra("staff");
            if (staff != null) {
                android.util.Log.d("StaffDebug", "StaffDetailActivity: Nhận dữ liệu staff: " + staff.getName() + " (ID: " + staff.getId() + ")");
            } else {
                android.util.Log.e("StaffDebug", "StaffDetailActivity: Dữ liệu staff từ Intent bị NULL");
            }
        } catch (Exception e) {
            android.util.Log.e("StaffDebug", "StaffDetailActivity: Lỗi deserializing staff: " + e.getMessage());
        }

        if (staff == null) {
            Toast.makeText(this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
    }

    private void initViews() {
        android.util.Log.d("StaffDebug", "StaffDetailActivity: Đang khởi tạo Views...");
        View btnBack = findViewById(R.id.btnBackStaffDetail);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        
        TextView tvId = findViewById(R.id.tvStaffId);
        if (tvId != null) tvId.setText("ID #" + staff.getId());
        
        // Hero Section
        TextView tvHeroName = findViewById(R.id.tvHeroName);
        if (tvHeroName != null) tvHeroName.setText(staff.getName() != null ? staff.getName() : "Không rõ");
        
        TextView tvHeroAvatarInitials = findViewById(R.id.tvHeroAvatarInitials);
        if (tvHeroAvatarInitials != null) {
            tvHeroAvatarInitials.setText(staff.getAvatar() != null ? staff.getAvatar() : "??");
        }
        
        View viewHeroAvatarBg = findViewById(R.id.viewHeroAvatarBg);
        if (viewHeroAvatarBg != null) {
            setupAvatarBackground(viewHeroAvatarBg, tvHeroAvatarInitials);
        }

        TextView tvRoleBadge = findViewById(R.id.tvHeroRoleBadge);
        if (tvRoleBadge != null) {
            String roleStr = staff.getRole();
            tvRoleBadge.setText(roleStr != null ? roleStr.toUpperCase() : "STAFF");
            // Highlight admin role
            if ("admin".equalsIgnoreCase(roleStr)) {
                tvRoleBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A9a7340")));
                tvRoleBadge.setTextColor(Color.parseColor("#9a7340"));
            } else {
                tvRoleBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A3464b4")));
                tvRoleBadge.setTextColor(Color.parseColor("#3464b4"));
            }
        }
        
        // Session & Permissions
        SessionManager sessionManager = new SessionManager(this);
        String currentUserEmail = sessionManager.getUserEmail();
        String currentUserRole = sessionManager.getUserRole();
        
        // Debug Log
        android.util.Log.d("StaffDebug", "--- PERMISSION CHECK ---");
        android.util.Log.d("StaffDebug", "Current User: " + currentUserEmail + " (Role: " + currentUserRole + ")");
        android.util.Log.d("StaffDebug", "Target Staff: " + (staff != null ? staff.getEmail() : "NULL") + " (Role: " + (staff != null ? staff.getRole() : "NULL") + ")");

        boolean isAdmin = "admin".equalsIgnoreCase(currentUserRole) || 
                         (currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@gmail.com"));
        
        // Normalize target role (could be 'admin', 'Admin', 'Nhân viên', 'staff'...)
        String targetRole = staff != null ? staff.getRole() : "";
        boolean isTargetAdmin = "admin".equalsIgnoreCase(targetRole) || 
                               "Admin".equalsIgnoreCase(targetRole) ||
                               "Quản trị".equalsIgnoreCase(targetRole);
        
        boolean isSelf = staff != null && staff.getEmail() != null && staff.getEmail().equalsIgnoreCase(currentUserEmail);

        android.util.Log.d("StaffDebug", "Final Decision -> isAdmin: " + isAdmin + ", isTargetAdmin: " + isTargetAdmin + ", isSelf: " + isSelf);

        // Info Rows
        setupInfoRow(findViewById(R.id.rowRole), "💼", "CHỨC VỤ", staff.getRole());
        setupInfoRow(findViewById(R.id.rowPhone), "📱", "ĐIỆN THOẠI", staff.getPhone());
        setupInfoRow(findViewById(R.id.rowEmail), "📧", "EMAIL", staff.getEmail());
        
        // Password row logic
        View rowPassword = findViewById(R.id.rowPassword);
        View divPassword = findViewById(R.id.divPassword);
        if (rowPassword != null) {
            boolean canSeePassword = isSelf || (isAdmin && !isTargetAdmin);
            android.util.Log.d("StaffDebug", "canSeePassword: " + canSeePassword);
            if (canSeePassword) {
                rowPassword.setVisibility(View.VISIBLE);
                if (divPassword != null) divPassword.setVisibility(View.VISIBLE);
                setupInfoRow(rowPassword, "🔑", "MẬT KHẨU", staff.getPassword());
            } else {
                rowPassword.setVisibility(View.GONE);
                if (divPassword != null) divPassword.setVisibility(View.GONE);
            }
        }

        View btnEdit = findViewById(R.id.btnEditStaff);
        View btnDelete = findViewById(R.id.btnDeleteStaff);

        if (btnEdit != null) {
            boolean canEdit = isSelf || (isAdmin && !isTargetAdmin);
            android.util.Log.d("StaffDebug", "canEdit: " + canEdit);
            if (canEdit) {
                btnEdit.setVisibility(View.VISIBLE);
                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(this, StaffFormActivity.class);
                    intent.putExtra("staff", staff);
                    startActivity(intent);
                });
            } else {
                btnEdit.setVisibility(View.GONE);
            }
        }

        if (btnDelete != null) {
            boolean canDelete = isAdmin && !isTargetAdmin;
            android.util.Log.d("StaffDebug", "canDelete: " + canDelete);
            if (canDelete) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> showDeleteConfirmation());
            } else {
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    private void setupAvatarBackground(View bgView, TextView textView) {
        try {
            int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b};
            int colorIdx = 0;
            if (staff.getAvatar() != null && !staff.getAvatar().isEmpty()) {
                colorIdx = Math.abs(staff.getAvatar().hashCode()) % colors.length;
            }
            int baseColor = colors[colorIdx];
            
            GradientDrawable avatarBg = new GradientDrawable();
            avatarBg.setShape(GradientDrawable.OVAL); // Circle
            avatarBg.setColor(adjustAlpha(baseColor, 0.15f));
            avatarBg.setStroke(3, adjustAlpha(baseColor, 0.4f));
            bgView.setBackground(avatarBg);
            if (textView != null) textView.setTextColor(baseColor);
        } catch (Exception e) {
            android.util.Log.e("StaffDetail", "Error setting up avatar: " + e.getMessage());
        }
    }

    private void setupInfoRow(View row, String icon, String label, String value) {
        if (row == null) return;
        try {
            TextView tvIcon = row.findViewById(R.id.tvRowIcon);
            TextView tvLabel = row.findViewById(R.id.tvRowLabel);
            TextView tvValue = row.findViewById(R.id.tvRowValue);
            
            if (tvIcon != null) {
                tvIcon.setText(icon);
                tvIcon.setVisibility(View.VISIBLE);
            }
            if (tvLabel != null) tvLabel.setText(label);
            if (tvValue != null) tvValue.setText(value != null && !value.isEmpty() ? value : "---");
        } catch (Exception e) {
            android.util.Log.e("StaffDetail", "Error setting up info row: " + e.getMessage());
        }
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xoá nhân viên?")
                .setMessage("Tài khoản của " + staff.getName() + " sẽ bị xoá vĩnh viễn.")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (dialog, which) -> {
                    try {
                        com.example.hotel_management.data.db.StaffRepository repo = new com.example.hotel_management.data.db.StaffRepository(this);
                        
                        // Rule: At least 1 admin must remain
                        if ("admin".equalsIgnoreCase(staff.getRole())) {
                            int adminCount = repo.getAdminCount();
                            if (adminCount <= 1) {
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle("Không thể xoá")
                                        .setMessage("Hệ thống phải có ít nhất 1 tài khoản Quản trị (Admin).")
                                        .setPositiveButton("Đã hiểu", null)
                                        .show();
                                return;
                            }
                        }

                        repo.deleteStaff(staff.getId());
                        Toast.makeText(this, "Đã xoá nhân viên " + staff.getName(), Toast.LENGTH_SHORT).show();
                        
                        // If deleting self, logout
                        SessionManager sm = new SessionManager(this);
                        if (staff.getEmail().equalsIgnoreCase(sm.getUserEmail())) {
                            sm.logout();
                            Intent intent = new Intent(this, com.example.hotel_management.ui.auth.LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi khi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private int adjustAlpha(int color, float factor) {
        try {
            int alpha = Math.round(Color.alpha(color) * factor);
            if (alpha == 0) alpha = Math.round(255 * factor);
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        } catch (Exception e) {
            return color;
        }
    }
}
