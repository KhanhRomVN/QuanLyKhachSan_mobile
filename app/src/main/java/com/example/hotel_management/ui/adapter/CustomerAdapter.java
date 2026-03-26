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

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers;
    private OnCustomerClickListener listener;
    private boolean selectionMode;
    private java.util.List<Customer> selectedCustomers = new java.util.ArrayList<>();

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    public CustomerAdapter(List<Customer> customers, OnCustomerClickListener listener) {
        this.customers = customers;
        this.listener = listener;
        this.selectionMode = false;
    }

    public CustomerAdapter(List<Customer> customers, OnCustomerClickListener listener, boolean selectionMode) {
        this.customers = customers;
        this.listener = listener;
        this.selectionMode = selectionMode;
    }

    public void setData(List<Customer> newData) {
        this.customers = newData;
        notifyDataSetChanged();
    }

    public void setSelectedCustomers(java.util.List<Customer> selected) {
        this.selectedCustomers = (selected != null) ? selected : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    public void updateList(List<Customer> newList) {
        this.customers = newList;
        notifyDataSetChanged();
    }

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

        public void bind(Customer customer, OnCustomerClickListener listener, boolean selectionMode, java.util.List<Customer> selectedCustomers) {
            tvName.setText(customer.getName());
            String cccd = customer.getCccd() != null && !customer.getCccd().isEmpty() ? customer.getCccd() : "-";
            String phone = customer.getPhone() != null && !customer.getPhone().isEmpty() ? customer.getPhone() : "-";
            tvIDInfo.setText("CCCD: " + cccd + " | SDT: " + phone);

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

            // Avatar styling
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

            // Selection mode indicator
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
}
