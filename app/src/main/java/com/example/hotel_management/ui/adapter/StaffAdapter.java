package com.example.hotel_management.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Staff;
import java.util.List;

/**
 * Bộ điều phối hiển thị Nhân viên (StaffAdapter).
 * Hiển thị danh sách nhân viên với các thông tin cơ bản và vai trò. 
 * Hỗ trợ chuyển đổi giao diện dựa trên quyền hạn (Admin/Staff).
 */
public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<Staff> staffList;
    private OnStaffClickListener listener;

    /**
     * Interface xử lý các sự kiện tương tác với nhân viên.
     */
    public interface OnStaffClickListener {
        void onStaffClick(Staff staff); // Xem chi tiết
        void onSwitchAccountClick(Staff staff); // Đăng nhập nhanh vào tài khoản này
    }

    public StaffAdapter(List<Staff> staffList, OnStaffClickListener listener) {
        this.staffList = staffList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_staff
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.bind(staff, listener);
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    /**
     * Lớp lưu trữ tham chiếu các View của một dòng nhân viên.
     */
    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRoleBadge;
        android.widget.ImageButton btnSwitch;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStaffName);
            tvEmail = itemView.findViewById(R.id.tvStaffEmail);
            tvRoleBadge = itemView.findViewById(R.id.tvStaffRoleBadge);
            btnSwitch = itemView.findViewById(R.id.btnSwitchAccount);
        }

        /**
         * Gán dữ liệu nhân viên và thiết lập giao diện dựa trên quyền hạn.
         */
        public void bind(Staff staff, OnStaffClickListener listener) {
            tvName.setText(staff.getName());
            tvEmail.setText(staff.getEmail());
            tvRoleBadge.setText(staff.getRole());

            // Logic hiển thị mờ/rõ dựa trên quyền hạn truy cập thông tin
            com.example.hotel_management.utils.SessionManager sessionManager = new com.example.hotel_management.utils.SessionManager(itemView.getContext());
            String currentEmail = sessionManager.getUserEmail();
            String currentRole = sessionManager.getUserRole();
            
            boolean isAdmin = "admin".equalsIgnoreCase(currentRole);
            boolean isSelf = staff.getEmail().equalsIgnoreCase(currentEmail);
            
            // Nếu là Admin hoặc xem chính mình thì hiển thị rõ nét
            if (isAdmin || isSelf) {
                itemView.setAlpha(1.0f);
            } else {
                itemView.setAlpha(0.6f); // Nhân viên bình thường chỉ thấy mờ thông tin đồng nghiệp khác
            }

            // Thiết lập phong cách nhãn (Badge) theo vai trò
            boolean isManager = staff.getRole().equalsIgnoreCase("Quản lý") || 
                               staff.getRole().equalsIgnoreCase("Admin") || 
                               staff.getRole().equalsIgnoreCase("Trưởng ca");
            
            android.graphics.drawable.Drawable background = tvRoleBadge.getBackground();
            if (background instanceof GradientDrawable) {
                GradientDrawable roleBg = (GradientDrawable) background.mutate();
                if (isManager) {
                    // Phong cách cho cấp quản lý (Màu nâu vàng đặc trưng của app)
                    roleBg.setColor(0x1A9a7340);
                    roleBg.setStroke(2, 0x409a7340);
                    tvRoleBadge.setTextColor(0xFF9a7340);
                } else {
                    // Phong cách cho nhân viên bình thường (Màu xám trung tính)
                    roleBg.setColor(0x0D000000);
                    roleBg.setStroke(2, 0x1A000000);
                    tvRoleBadge.setTextColor(0xFF7a7568);
                }
            }

            itemView.setOnClickListener(v -> listener.onStaffClick(staff));
            if (btnSwitch != null) {
                btnSwitch.setOnClickListener(v -> listener.onSwitchAccountClick(staff));
            }
        }
    }
}
