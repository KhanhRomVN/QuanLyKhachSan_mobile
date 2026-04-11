package com.example.hotel_management.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Guest;
import com.example.hotel_management.data.model.Room;
import java.util.List;
import java.util.Locale;

/**
 * Bộ điều phối hiển thị Phòng (RoomAdapter).
 * Chịu trách nhiệm hiển thị từng thẻ phòng trên danh sách, bao gồm trạng thái, 
 * thông tin khách đang ở, tiện nghi và giá phòng.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnRoomClickListener listener;

    // Bảng màu cho ảnh đại diện khách hàng
    private static final int[] AVATAR_COLORS = {
        0xFF9a7340, 0xFF64A0F0, 0xFF6FCF97, 0xFFFBBF24, 0xFFEB5757, 0xFF7B5EA7
    };

    /**
     * Interface xử lý sự kiện khi người dùng nhấn vào một phòng.
     */
    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(List<Room> roomList, OnRoomClickListener listener) {
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_room cho mỗi dòng trong danh sách
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.tvNumber.setText(room.getNumber());
        holder.tvType.setText(getTypeLabel(room.getType()));
        holder.tvMaxGuests.setText("Tối đa " + room.getMaxGuests() + " khách");

        // Thiết lập trạng thái phòng (Màu sắc nhãnbadge)
        setupStatus(holder, room);

        // Hiển thị thông tin khách nếu phòng đang có khách (occupied)
        if ("occupied".equals(room.getStatus()) && room.getGuests() != null && !room.getGuests().isEmpty()) {
            holder.layoutGuests.setVisibility(View.VISIBLE);
            holder.layoutEmptyIndicator.setVisibility(View.GONE);
            setupGuests(holder, room);
        } else {
            // Hiển thị thông báo trạng thái nếu phòng không có khách
            holder.layoutGuests.setVisibility(View.GONE);
            holder.layoutEmptyIndicator.setVisibility(View.VISIBLE);
            String emptyMsg;
            if ("occupied".equals(room.getStatus())) {
                emptyMsg = "Phòng đang có khách";
            } else if ("dirty".equals(room.getStatus())) {
                emptyMsg = "Cần dọn dẹp trước khi nhận khách";
            } else if ("maintenance".equals(room.getStatus())) {
                emptyMsg = (room.getNote() != null ? room.getNote() : "Đang bảo trì");
            } else {
                emptyMsg = "Phòng đang trống, sẵn sàng nhận khách";
            }
            holder.tvEmptyMessage.setText(emptyMsg);
        }

        // Hiển thị giá phòng
        String priceStr = formatPrice(room.getPrice()) + "/đêm";
        holder.tvPrice.setText(priceStr);

        // Hiển thị các tiện nghi (Wifi, Điều hòa...) dưới dạng chip nhỏ
        setupAmenities(holder, room);

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && room != null) {
                listener.onRoomClick(room);
            }
        });
    }

    /**
     * Chuyển mã loại phòng sang tên hiển thị tiếng Việt.
     */
    private String getTypeLabel(String type) {
        if (type == null) return "Đơn";
        switch (type.toLowerCase()) {
            case "single": case "phòng đơn": return "Phòng đơn";
            case "double": case "phòng đôi": return "Phòng đôi";
            case "quad": case "phòng 4 người": return "Phòng 4 người";
            default: return type;
        }
    }

    /**
     * Thiết lập giao diện màu sắc cho nhãn trạng thái phòng.
     */
    private void setupStatus(RoomViewHolder holder, Room room) {
        String status = room.getStatus();
        int resId;
        int numberBg, numberText;
        String label;

        switch (status) {
            case "occupied":
                resId = R.drawable.bg_status_occupied;
                numberBg = 0x1A64A0F0;
                numberText = 0xFF80aaee;
                label = "Có khách";
                break;
            case "vacant":
                resId = R.drawable.bg_status_vacant;
                numberBg = 0x1A6FCF97;
                numberText = 0xFF6ec98a;
                label = "Trống";
                break;
            case "dirty":
                resId = R.drawable.bg_status_dirty;
                numberBg = 0x1AFBBF24;
                numberText = 0xFFf0b43c;
                label = "Cần dọn";
                break;
            case "maintenance":
                resId = R.drawable.bg_status_maintenance;
                numberBg = 0x1AEB5757;
                numberText = 0xFFe87070;
                label = "Bảo trì";
                break;
            default:
                resId = R.drawable.bg_status_vacant;
                numberBg = 0x1A7C7A72;
                numberText = 0xFF7C7A72;
                label = "Không rõ";
        }

        holder.tvStatusBadge.setBackgroundResource(resId);
        holder.tvStatusBadge.setTextColor(Color.WHITE);
        holder.tvStatusBadge.setText(label);

        // Cập nhật màu sắc cho khối số phòng
        holder.cardBadge.setCardBackgroundColor(numberBg);
        holder.tvNumber.setTextColor(numberText);
    }

    /**
     * Hiển thị danh sách khách hàng đang ở trong phòng (bao gồm ảnh đại diện xếp chồng).
     */
    private void setupGuests(RoomViewHolder holder, Room room) {
        List<Guest> guests = room.getGuests();
        if (guests == null || guests.isEmpty()) return;

        holder.layoutAvatars.removeAllViews();
        holder.tvGuestNames.setVisibility(View.VISIBLE);

        // Hiển thị tối đa 2 avatar khách hàng, những người còn lại hiển thị dạng "+X"
        int maxVisible = Math.min(guests.size(), 2);
        float density = holder.itemView.getContext().getResources().getDisplayMetrics().density;
        int sizePx = (int) (34 * density);
        int overlapPx = (int) (-12 * density);

        for (int i = 0; i < maxVisible; i++) {
            TextView avatar = createAvatar(holder.itemView.getContext(), guests.get(i).getName(), 34);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            params.setMargins(i > 0 ? overlapPx : 0, 0, 0, 0);
            holder.layoutAvatars.addView(avatar, params);
        }

        if (guests.size() > 2) {
            TextView moreBadge = new TextView(holder.itemView.getContext());
            moreBadge.setText("+" + (guests.size() - 2));
            moreBadge.setTextSize(9);
            moreBadge.setTextColor(Color.parseColor("#9a7340"));
            android.graphics.drawable.GradientDrawable moreGd = new android.graphics.drawable.GradientDrawable();
            moreGd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            moreGd.setCornerRadius(9 * density);
            moreGd.setColor(0x1A9a7340);
            moreGd.setStroke((int)(1 * density), 0x409a7340);
            moreBadge.setBackground(moreGd);
            moreBadge.setGravity(android.view.Gravity.CENTER);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            params.setMargins(overlapPx, 0, 0, 0);
            holder.layoutAvatars.addView(moreBadge, params);
        }

        // Hiển thị thời gian lưu trú và số khách + tổng tiền dự tính
        long total = (long) (room.getPrice() * room.getNights());
        holder.tvStayDuration.setText(String.format("%d đêm · %s → %s",
                room.getNights(), room.getCheckIn(), room.getCheckOut()));
        holder.tvGuestNames.setText(String.format("%d người · Tổng: %s",
                guests.size(), formatPrice(total)));
    }

    /**
     * Tạo một View chứa ảnh đại diện viết tắt (Initials Avatar).
     */
    private TextView createAvatar(android.content.Context context, String name, int sizePx) {
        TextView avatar = new TextView(context);
        int size = (int) (sizePx * context.getResources().getDisplayMetrics().density);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        avatar.setGravity(android.view.Gravity.CENTER);

        int colorIndex = Math.abs(name.hashCode()) % AVATAR_COLORS.length;
        int color = AVATAR_COLORS[colorIndex];

        float density = context.getResources().getDisplayMetrics().density;
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setCornerRadius(9 * density); 
        gd.setColor((0x1A000000 | (color & 0x00FFFFFF))); 
        gd.setStroke((int)(1 * density), (0x40000000 | (color & 0x00FFFFFF))); 

        avatar.setBackground(gd);
        avatar.setTextColor(color);
        avatar.setTextSize(10);
        avatar.setTypeface(null, android.graphics.Typeface.BOLD);

        // Lấy các chữ cái đầu của tên (ví dụ: "Nguyen Van A" -> "NA")
        String[] parts = name.trim().split("\\s+");
        String initials = "";
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials += parts[0].charAt(0);
            if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
                initials += parts[parts.length - 1].charAt(0);
            }
        }
        avatar.setText(initials.toUpperCase(Locale.ROOT));

        return avatar;
    }

    /**
     * Hiển thị danh sách tiện nghi rút gọn (Tối đa 3 cái đầu tiên).
     */
    private void setupAmenities(RoomViewHolder holder, Room room) {
        holder.layoutAmenities.removeAllViews();
        List<String> amenities = room.getAmenities();
        if (amenities == null || amenities.isEmpty()) {
            holder.layoutAmenities.setVisibility(View.GONE);
            return;
        }

        holder.layoutAmenities.setVisibility(View.VISIBLE);
        int maxVisible = Math.min(amenities.size(), 3);

        for (int i = 0; i < maxVisible; i++) {
            TextView amenityChip = new TextView(holder.itemView.getContext());
            amenityChip.setText(amenities.get(i));
            amenityChip.setTextSize(9);
            amenityChip.setTextColor(Color.parseColor("#9a7340"));
            amenityChip.setBackgroundColor(Color.parseColor("#1A9a7340"));
            amenityChip.setPadding(6, 4, 6, 4);
            amenityChip.setGravity(android.view.Gravity.CENTER);
            holder.layoutAmenities.addView(amenityChip);

            if (i < maxVisible - 1) {
                View spacer = new View(holder.itemView.getContext());
                spacer.setLayoutParams(new LinearLayout.LayoutParams(4, 0));
                holder.layoutAmenities.addView(spacer);
            }
        }

        if (amenities.size() > 3) {
            TextView moreChip = new TextView(holder.itemView.getContext());
            moreChip.setText("+" + (amenities.size() - 3));
            moreChip.setTextSize(9);
            moreChip.setTextColor(Color.parseColor("#7a7568"));
            moreChip.setPadding(4, 4, 4, 4);
            holder.layoutAmenities.addView(moreChip);
        }
    }

    /**
     * Định dạng tiền tệ ngắn gọn (Ví dụ: 1000000 -> 1M VND).
     */
    private String formatPrice(double price) {
        if (price >= 1000000) return String.format(Locale.getDefault(), "%.1fM", price / 1000000.0) + "VND";
        if (price >= 1000) return String.format(Locale.getDefault(), "%.0fK", price / 1000.0) + "VND";
        return String.valueOf((int) price) + "VND";
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    /**
     * Lớp lưu trữ các tham chiếu đến các thành phần giao diện của một item trong danh sách.
     */
    static class RoomViewHolder extends RecyclerView.ViewHolder {
        CardView cardBadge;
        TextView tvNumber, tvType, tvMaxGuests, tvStatusBadge;
        LinearLayout layoutGuests, layoutEmptyIndicator, layoutAvatars, layoutAmenities;
        TextView tvGuestNames, tvStayDuration, tvEmptyMessage;
        TextView tvPrice;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBadge = itemView.findViewById(R.id.cardRoomBadge);
            tvNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvType = itemView.findViewById(R.id.tvRoomType);
            tvMaxGuests = itemView.findViewById(R.id.tvMaxGuests);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            layoutGuests = itemView.findViewById(R.id.layoutGuests);
            layoutEmptyIndicator = itemView.findViewById(R.id.layoutEmptyIndicator);
            layoutAvatars = itemView.findViewById(R.id.layoutAvatars);
            layoutAmenities = itemView.findViewById(R.id.layoutAmenities);
            tvGuestNames = itemView.findViewById(R.id.tvGuestNames);
            tvStayDuration = itemView.findViewById(R.id.tvStayDuration);
            tvEmptyMessage = itemView.findViewById(R.id.tvEmptyMessage);
            tvPrice = itemView.findViewById(R.id.tvRoomPrice);
        }
    }
}
