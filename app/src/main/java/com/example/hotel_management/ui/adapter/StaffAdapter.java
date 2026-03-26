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

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<Staff> staffList;
    private OnStaffClickListener listener;

    public interface OnStaffClickListener {
        void onStaffClick(Staff staff);
    }

    public StaffAdapter(List<Staff> staffList, OnStaffClickListener listener) {
        this.staffList = staffList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRoleBadge;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStaffName);
            tvEmail = itemView.findViewById(R.id.tvStaffEmail);
            tvRoleBadge = itemView.findViewById(R.id.tvStaffRoleBadge);
        }

        public void bind(Staff staff, OnStaffClickListener listener) {
            tvName.setText(staff.getName());
            tvEmail.setText(staff.getEmail());
            tvRoleBadge.setText(staff.getRole());

            // Permissions / Disabled look
            com.example.hotel_management.utils.SessionManager sessionManager = new com.example.hotel_management.utils.SessionManager(itemView.getContext());
            String currentEmail = sessionManager.getUserEmail();
            String currentRole = sessionManager.getUserRole();
            
            boolean isAdmin = "admin".equalsIgnoreCase(currentRole);
            boolean isSelf = staff.getEmail().equalsIgnoreCase(currentEmail);
            
            if (isAdmin || isSelf) {
                itemView.setAlpha(1.0f);
            } else {
                itemView.setAlpha(0.6f);
            }

            // Role Badge styling
            boolean isManager = staff.getRole().equalsIgnoreCase("Quản lý") || 
                               staff.getRole().equalsIgnoreCase("Admin") || 
                               staff.getRole().equalsIgnoreCase("Trưởng ca");
            GradientDrawable roleBg = (GradientDrawable) tvRoleBadge.getBackground();
            if (isManager) {
                roleBg.setColor(0x1A9a7340);
                roleBg.setStroke(2, 0x409a7340);
                tvRoleBadge.setTextColor(0xFF9a7340);
            } else {
                roleBg.setColor(0x0D000000);
                roleBg.setStroke(2, 0x1A000000);
                tvRoleBadge.setTextColor(0xFF7a7568);
            }

            itemView.setOnClickListener(v -> listener.onStaffClick(staff));
        }
    }
}
