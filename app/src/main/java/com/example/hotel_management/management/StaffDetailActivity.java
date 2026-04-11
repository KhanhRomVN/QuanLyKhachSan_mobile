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

/**
 * Hoạt động Chi tiết Nhân viên (StaffDetailActivity).
 * Hiển thị hồ sơ đầy đủ của một nhân viên và quản lý các quyền Sửa/Xóa dựa trên vai trò (RBAC).
 */
public class StaffDetailActivity extends AppCompatActivity {

    private Staff staff; // Đối tượng nhân viên đang được xem

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("StaffDebug", "StaffDetailActivity: onCreate bắt đầu");
        setContentView(R.layout.activity_staff_detail);

        // Lấy dữ liệu nhân viên từ Intent truyền sang
        try {
            staff = (Staff) getIntent().getSerializableExtra("staff");
        } catch (Exception e) {
            android.util.Log.e("StaffDebug", "StaffDetailActivity: Lỗi nhận dữ liệu: " + e.getMessage());
        }

        if (staff == null) {
            Toast.makeText(this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
    }

    /**
     * Khởi tạo giao diện và xử lý logic phân quyền.
     */
    private void initViews() {
        android.util.Log.d("StaffDebug", "StaffDetailActivity: Đang khởi tạo Views...");
        
        // Nút quay lại
        View btnBack = findViewById(R.id.btnBackStaffDetail);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        
        // Hiển thị ID nhân viên
        TextView tvId = findViewById(R.id.tvStaffId);
        if (tvId != null) tvId.setText("ID #" + staff.getId());
        
        // Khu vực Hero (Avatar và Tên)
        TextView tvHeroName = findViewById(R.id.tvHeroName);
        if (tvHeroName != null) tvHeroName.setText(staff.getName());
        
        TextView tvHeroAvatarInitials = findViewById(R.id.tvHeroAvatarInitials);
        if (tvHeroAvatarInitials != null) tvHeroAvatarInitials.setText(staff.getAvatar());
        
        View viewHeroAvatarBg = findViewById(R.id.viewHeroAvatarBg);
        if (viewHeroAvatarBg != null) {
            setupAvatarBackground(viewHeroAvatarBg, tvHeroAvatarInitials);
        }

        // Nhãn vai trò (Admin/Staff)
        TextView tvRoleBadge = findViewById(R.id.tvHeroRoleBadge);
        if (tvRoleBadge != null) {
            String roleStr = staff.getRole();
            tvRoleBadge.setText(roleStr != null ? roleStr.toUpperCase() : "STAFF");
            if ("admin".equalsIgnoreCase(roleStr)) {
                tvRoleBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A9a7340")));
                tvRoleBadge.setTextColor(Color.parseColor("#9a7340"));
            } else {
                tvRoleBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A3464b4")));
                tvRoleBadge.setTextColor(Color.parseColor("#3464b4"));
            }
        }
        
        // --- KIỂM TRA QUYỀN TRUY CẬP (RBAC LOGIC) ---
        SessionManager sessionManager = new SessionManager(this);
        String currentUserEmail = sessionManager.getUserEmail();
        String currentUserRole = sessionManager.getUserRole();
        
        // Điều kiện 1: Người dùng hiện tại có phải là Admin hay không?
        boolean isAdmin = "admin".equalsIgnoreCase(currentUserRole);
        
        // Điều kiện 2: Nhân viên mục tiêu (đang xem) có phải là Admin hay không?
        String targetRole = staff.getRole();
        boolean isTargetAdmin = "admin".equalsIgnoreCase(targetRole) || "Admin".equalsIgnoreCase(targetRole) || "Quản trị".equalsIgnoreCase(targetRole);
        
        // Điều kiện 3: Có phải đang tự xem hồ sơ của mình không?
        boolean isSelf = staff.getEmail().equalsIgnoreCase(currentUserEmail);

        // Hiển thị các hàng thông tin cơ bản
        setupInfoRow(findViewById(R.id.rowRole), "💼", "CHỨC VỤ", staff.getRole());
        setupInfoRow(findViewById(R.id.rowPhone), "📱", "ĐIỆN THOẠI", staff.getPhone());
        setupInfoRow(findViewById(R.id.rowEmail), "📧", "EMAIL", staff.getEmail());
        
        // Xử lý hiển thị mật khẩu:
        // Quyền: Chỉ bản thân được thấy mật khẩu mình, hoặc Admin được thấy mật khẩu của nhân viên cấp dưới (không được thấy mật khẩu Admin khác).
        View rowPassword = findViewById(R.id.rowPassword);
        View divPassword = findViewById(R.id.divPassword);
        if (rowPassword != null) {
            boolean canSeePassword = isSelf || (isAdmin && !isTargetAdmin);
            if (canSeePassword) {
                rowPassword.setVisibility(View.VISIBLE);
                if (divPassword != null) divPassword.setVisibility(View.VISIBLE);
                setupInfoRow(rowPassword, "🔑", "MẬT KHẨU", staff.getPassword());
            } else {
                rowPassword.setVisibility(View.GONE);
                if (divPassword != null) divPassword.setVisibility(View.GONE);
            }
        }

        // Xử lý hiển thị nút Sửa:
        // Quyền: Tương tự như xem mật khẩu.
        View btnEdit = findViewById(R.id.btnEditStaff);
        if (btnEdit != null) {
            boolean canEdit = isSelf || (isAdmin && !isTargetAdmin);
            btnEdit.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, StaffFormActivity.class);
                intent.putExtra("staff", staff);
                startActivity(intent);
            });
        }

        // Xử lý hiển thị nút Xóa:
        // Quyền: Chỉ QUẢN TRỊ VIÊN mới có quyền xóa, và chỉ xóa được các tài khoản KHÔNG PHẢI QUẢN TRỊ VIÊN.
        View btnDelete = findViewById(R.id.btnDeleteStaff);
        if (btnDelete != null) {
            boolean canDelete = isAdmin && !isTargetAdmin;
            btnDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        }
    }

    /**
     * Thiết lập màu nền Avatar dựa trên mã băm của tên (đảm bảo mỗi người có một màu đặc trưng riêng).
     */
    private void setupAvatarBackground(View bgView, TextView textView) {
        int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b};
        int colorIdx = Math.abs(staff.getAvatar().hashCode()) % colors.length;
        int baseColor = colors[colorIdx];
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(adjustAlpha(baseColor, 0.15f));
        avatarBg.setStroke(3, adjustAlpha(baseColor, 0.4f));
        bgView.setBackground(avatarBg);
        if (textView != null) textView.setTextColor(baseColor);
    }

    /**
     * Thiết lập nội dung cho một hàng thông tin.
     */
    private void setupInfoRow(View row, String icon, String label, String value) {
        if (row == null) return;
        TextView tvIcon = row.findViewById(R.id.tvRowIcon);
        TextView tvLabel = row.findViewById(R.id.tvRowLabel);
        TextView tvValue = row.findViewById(R.id.tvRowValue);
        
        if (tvIcon != null) tvIcon.setText(icon);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value != null && !value.isEmpty() ? value : "---");
    }

    /**
     * Hiển thị hộp thoại xác nhận xóa nhân viên với các ràng buộc nghiệp vụ.
     */
    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xoá nhân viên?")
                .setMessage("Tài khoản của " + staff.getName() + " sẽ bị xoá vĩnh viễn.")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (dialog, which) -> {
                    com.example.hotel_management.data.db.StaffRepository repo = new com.example.hotel_management.data.db.StaffRepository(this);
                    
                    // Ràng buộc bảo mật: Hệ thống phải luôn có ít nhất 1 Quản trị viên.
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

                    // Thực hiện xóa trong cơ sở dữ liệu
                    repo.deleteStaff(staff.getId());
                    Toast.makeText(this, "Đã xoá nhân viên " + staff.getName(), Toast.LENGTH_SHORT).show();
                    
                    // Nếu tự xóa chính mình (nếu logic cho phép), tiến hành đăng xuất.
                    SessionManager sm = new SessionManager(this);
                    if (staff.getEmail().equalsIgnoreCase(sm.getUserEmail())) {
                        sm.logout();
                        Intent intent = new Intent(this, com.example.hotel_management.ui.auth.LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                })
                .show();
    }

    /**
     * Điều chỉnh độ trong suốt của màu sắc.
     */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        if (alpha == 0) alpha = Math.round(255 * factor);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
