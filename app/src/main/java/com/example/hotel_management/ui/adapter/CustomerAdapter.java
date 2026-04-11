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
import com.example.hotel_management.data.model.Booking;
import com.example.hotel_management.data.model.Customer;
import java.util.List;

/**
 * Bộ điều phối hiển thị Khách hàng (CustomerAdapter).
 * Hỗ trợ hiển thị danh sách khách hàng và chế độ chọn khách hàng (Selection Mode) khi đặt phòng.
 */
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers;
    private OnCustomerClickListener listener;
    private boolean selectionMode; // Chế độ chọn (dùng khi đặt phòng)
    private java.util.List<Customer> selectedCustomers = new java.util.ArrayList<>(); // Danh sách khách đang được chọn

    /**
     * Interface xử lý sự kiện click vào khách hàng.
     */
    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    // Constructor dùng cho danh sách thông thường
    public CustomerAdapter(List<Customer> customers, OnCustomerClickListener listener) {
        this.customers = customers;
        this.listener = listener;
        this.selectionMode = false;
    }

    // Constructor dùng cho chế độ chọn
    public CustomerAdapter(List<Customer> customers, OnCustomerClickListener listener, boolean selectionMode) {
        this.customers = customers;
        this.listener = listener;
        this.selectionMode = selectionMode;
    }

    /**
     * Làm mới toàn bộ dữ liệu danh sách.
     */
    public void setData(List<Customer> newData) {
        this.customers = newData;
        notifyDataSetChanged();
    }

    /**
     * Cập nhật danh sách các khách hàng đang được chọn (để highlight trên UI).
     */
    public void setSelectedCustomers(java.util.List<Customer> selected) {
        this.selectedCustomers = (selected != null) ? selected : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_customer cho mỗi dòng
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer, listener, selectionMode, selectedCustomers);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    /**
     * Cập nhật danh sách sau khi lọc (search).
     */
    public void updateList(List<Customer> newList) {
        this.customers = newList;
        notifyDataSetChanged();
    }

    /**
     * Lớp lưu trữ tham chiếu các View của một dòng khách hàng.
     */
    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvIDInfo, tvStayingBadge;
        View viewAvatarBg;
        com.google.android.material.card.MaterialCardView cardCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvCustomerInitials);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvIDInfo = itemView.findViewById(R.id.tvCustomerIDInfo);
            tvStayingBadge = itemView.findViewById(R.id.tvStayingBadge);
            viewAvatarBg = itemView.findViewById(R.id.viewCustomerAvatarBg);
            cardCustomer = itemView.findViewById(R.id.cardCustomer);
        }

        /**
         * Gán dữ liệu khách hàng vào các View.
         */
        public void bind(Customer customer, OnCustomerClickListener listener, boolean selectionMode, java.util.List<Customer> selectedCustomers) {
            tvName.setText(customer.getName());
            String cccd = customer.getCccd() != null && !customer.getCccd().isEmpty() ? customer.getCccd() : "-";
            String phone = customer.getPhone() != null && !customer.getPhone().isEmpty() ? customer.getPhone() : "-";
            tvIDInfo.setText("CCCD: " + cccd + " | SDT: " + phone);

            // Kiểm tra trạng thái lưu trú hiện tại của khách
            boolean isStaying = false;
            if (customer.getBookings() != null) {
                for (Booking b : customer.getBookings()) {
                    if ("staying".equals(b.getStatus())) {
                        isStaying = true;
                        break;
                    }
                }
            }
            tvStayingBadge.setVisibility(isStaying ? View.VISIBLE : View.GONE);

            // Thiết lập Avatar dựa trên tên khách hàng
            String initials = getInitials(customer.getName());
            tvInitials.setText(initials);

            int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b, 0xFF7b5ea7};
            int color = colors[Math.abs(customer.getName().hashCode()) % colors.length];

            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setCornerRadius(12 * itemView.getContext().getResources().getDisplayMetrics().density);
            gd.setColor(adjustAlpha(color, 0.1f));
            gd.setStroke(3, adjustAlpha(color, 0.25f));
            viewAvatarBg.setBackground(gd);
            tvInitials.setTextColor(color);

            // Xử lý giao diện cho Chế độ Chọn (Selection Mode)
            if (selectionMode) {
                boolean isSelected = false;
                if (selectedCustomers != null) {
                    for (Customer c : selectedCustomers) {
                        if (c.getId() == customer.getId()) {
                            isSelected = true;
                            break;
                        }
                    }
                }
                
                // Highlight item nếu đang được chọn (màu nâu nhạt)
                if (isSelected) {
                    cardCustomer.setCardBackgroundColor(Color.parseColor("#1A9a7340"));
                    cardCustomer.setStrokeColor(Color.parseColor("#409a7340"));
                    tvName.setTextColor(Color.parseColor("#9a7340"));
                } else {
                    cardCustomer.setCardBackgroundColor(itemView.getContext().getColor(R.color.card_bg));
                    cardCustomer.setStrokeColor(Color.parseColor("#1A000000"));
                    tvName.setTextColor(Color.parseColor("#1a1810"));
                }
            } else {
                cardCustomer.setCardBackgroundColor(itemView.getContext().getColor(R.color.card_bg));
                cardCustomer.setStrokeColor(Color.parseColor("#1A000000"));
                tvName.setTextColor(Color.parseColor("#1a1810"));
            }

            itemView.setOnClickListener(v -> listener.onCustomerClick(customer));
        }

        /**
         * Lấy chữ cái đầu của tên khách (Ví dụ: "Hồ Anh Quý" -> "AQ").
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
         * Điều chỉnh độ trong suốt màu sắc cho UI.
         */
        private int adjustAlpha(int color, float factor) {
            int alpha = Math.round(Color.alpha(color) * factor);
            if (alpha == 0) alpha = Math.round(255 * factor);
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        }
    }
}
