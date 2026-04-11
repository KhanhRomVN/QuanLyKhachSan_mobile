package com.example.hotel_management.ui.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Booking;
import java.util.List;

/**
 * Bộ điều phối hiển thị Lịch sử Đặt phòng (BookingHistoryAdapter).
 * Hiển thị các bản ghi đặt phòng trong quá khứ hoặc hiện tại của một khách hàng hoặc một phòng cụ thể.
 * Quản lý giao diện nhãn trạng thái (Đang ở, Hoàn thành, Hủy).
 */
public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private List<Booking> bookings;

    public BookingHistoryAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_booking_history cho từng dòng
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    /**
     * Lớp lưu trữ tham chiếu các View của một bản ghi lịch sử đặt phòng.
     */
    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvStatusBadge, tvTotal, tvRoom, tvType, tvCheckIn, tvCheckOut, tvNights, tvPrice;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvBookingId);
            tvStatusBadge = itemView.findViewById(R.id.tvBookingStatusBadge);
            tvTotal = itemView.findViewById(R.id.tvBookingTotal);
            tvRoom = itemView.findViewById(R.id.tvBookingRoom);
            tvType = itemView.findViewById(R.id.tvBookingType);
            tvCheckIn = itemView.findViewById(R.id.tvBookingCheckIn);
            tvCheckOut = itemView.findViewById(R.id.tvBookingCheckOut);
            tvNights = itemView.findViewById(R.id.tvBookingNights);
            tvPrice = itemView.findViewById(R.id.tvBookingPrice);
        }

        /**
         * Gán dữ liệu booking và thiết lập màu sắc trạng thái.
         */
        public void bind(Booking booking) {
            tvId.setText("#" + booking.getId());
            // Định dạng hiển thị tiền tệ VNĐ
            tvTotal.setText(String.format("%,d ₫", booking.getTotal()));
            tvRoom.setText(booking.getRoom());
            tvType.setText(booking.getType());
            tvCheckIn.setText(booking.getCheckIn());
            tvCheckOut.setText("→ " + booking.getCheckOut());
            tvNights.setText(booking.getNights() + " đêm");
            
            // Định dạng giá phòng rút gọn (Ví dụ: 500K/đêm)
            long p = booking.getPrice();
            String pStr = p >= 1000000 ? String.format("%.1fM/đêm", p / 1000000.0) : String.format("%dK/đêm", p / 1000);
            tvPrice.setText(pStr);

            // Thiết lập màu sắc nhãn trạng thái (Badge)
            GradientDrawable gd = (GradientDrawable) tvStatusBadge.getBackground();
            if (gd != null) {
                gd = (GradientDrawable) gd.mutate(); // Đảm bảo không đổi màu các item khác
                switch (booking.getStatus()) {
                    case "staying":
                        tvStatusBadge.setText("Đang ở");
                        tvStatusBadge.setTextColor(0xFF3464b4); // Xanh dương
                        gd.setColor(0x1A3464b4);
                        gd.setStroke(2, 0x403464b4);
                        break;
                    case "done":
                        tvStatusBadge.setText("Đã xong");
                        tvStatusBadge.setTextColor(0xFF3a8a5a); // Xanh lá
                        gd.setColor(0x1A3a8a5a);
                        gd.setStroke(2, 0x403a8a5a);
                        break;
                    case "cancel":
                        tvStatusBadge.setText("Huỷ");
                        tvStatusBadge.setTextColor(0xFFc0392b); // Đỏ
                        gd.setColor(0x1Ac0392b);
                        gd.setStroke(2, 0x40c0392b);
                        break;
                }
                tvStatusBadge.setBackground(gd);
            }
        }
    }
}
