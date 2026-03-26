package com.example.hotel_management.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.BookingHistory;
import com.example.hotel_management.data.model.Guest;
import com.example.hotel_management.data.model.Room;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.List;
import java.util.Locale;

public class RoomDetailActivity extends AppCompatActivity {

    private Room room;
    private int currentTab = 0; // 0: info, 1: guests, 2: history

    private TextView tvTitle, tvSubtitle, tvStatusBadge;
    private TextView tvHeroNumber, tvHeroType, tvHeroPrice;
    private LinearLayout layoutTabInfo, layoutTabGuests, layoutTabHistory;
    private TextView tabInfo, tabGuests, tabHistory;
    private ChipGroup chipGroupAmenities;
    private CardView cardAmenities, cardNote;
    private View cardActions;
    private TextView tvNote;
    private View rowStatus, rowType, rowMaxGuests, rowPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_room_detail);

            room = (Room) getIntent().getSerializableExtra("room");
            if (room == null) {
                Toast.makeText(this, "Lỗi: không tìm thấy phòng", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            initViews();
            setupTabs();
            updateRoomData();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        findViewById(R.id.btnBackRoomDetail).setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvHeroNumber = findViewById(R.id.tvHeroNumber);
        tvHeroType = findViewById(R.id.tvHeroType);
        tvHeroPrice = findViewById(R.id.tvHeroPrice);
        layoutTabInfo = findViewById(R.id.layoutTabInfo);
        layoutTabGuests = findViewById(R.id.layoutTabGuests);
        layoutTabHistory = findViewById(R.id.layoutTabHistory);
        tabInfo = findViewById(R.id.tabInfo);
        tabGuests = findViewById(R.id.tabGuests);
        tabHistory = findViewById(R.id.tabHistory);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        cardAmenities = findViewById(R.id.cardAmenities);
        cardNote = findViewById(R.id.cardNote);
        cardActions = findViewById(R.id.cardActions);
        tvNote = findViewById(R.id.tvNote);
        rowStatus = findViewById(R.id.rowStatus);
        rowType = findViewById(R.id.rowType);
        rowMaxGuests = findViewById(R.id.rowMaxGuests);
        rowPrice = findViewById(R.id.rowPrice);

        tabInfo.setOnClickListener(v -> switchTab(0));
        tabGuests.setOnClickListener(v -> switchTab(1));
        tabHistory.setOnClickListener(v -> switchTab(2));

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(this, RoomFormActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupTabs() {
        updateTabUI();
    }

    private void switchTab(int tab) {
        currentTab = tab;
        updateTabUI();
    }

    private void updateTabUI() {
        int activeColor = Color.parseColor("#1a1810");
        int inactiveColor = Color.parseColor("#7a7568");

        tabInfo.setTextColor(currentTab == 0 ? activeColor : inactiveColor);
        tabGuests.setTextColor(currentTab == 1 ? activeColor : inactiveColor);
        tabHistory.setTextColor(currentTab == 2 ? activeColor : inactiveColor);

        tabInfo.setSelected(currentTab == 0);
        tabGuests.setSelected(currentTab == 1);
        tabHistory.setSelected(currentTab == 2);

        layoutTabInfo.setVisibility(currentTab == 0 ? View.VISIBLE : View.GONE);
        layoutTabGuests.setVisibility(currentTab == 1 ? View.VISIBLE : View.GONE);
        layoutTabHistory.setVisibility(currentTab == 2 ? View.VISIBLE : View.GONE);

        // Action buttons only visible on info tab
        if (cardActions != null) {
            cardActions.setVisibility(currentTab == 0 ? View.VISIBLE : View.GONE);
        }

        if (currentTab == 1) {
            setupGuestsTab();
        } else if (currentTab == 2) {
            setupHistoryTab();
        }
    }

    private void updateRoomData() {
        tvTitle.setText("Phòng " + room.getNumber());
        String typeLabel = getTypeLabel(room.getType());
        tvSubtitle.setText(typeLabel + " · " + room.getMaxGuests() + " người");

        tvHeroNumber.setText(room.getNumber());
        tvHeroType.setText(typeLabel);
        tvHeroPrice.setText(formatPrice(room.getPrice()));

        setupStatus();
        setupInfoRows();

        // Amenities
        List<String> amenities = room.getAmenities();
        if (amenities != null && !amenities.isEmpty()) {
            cardAmenities.setVisibility(View.VISIBLE);
            chipGroupAmenities.removeAllViews();
            for (String amenity : amenities) {
                Chip chip = createChip(amenity);
                chipGroupAmenities.addView(chip);
            }
        } else {
            cardAmenities.setVisibility(View.GONE);
        }

        // Note
        if (room.getNote() != null && !room.getNote().isEmpty()) {
            tvNote.setText(room.getNote());
        } else {
            tvNote.setText("Không có ghi chú");
        }

        // Update guest tab label
        List<Guest> guests = room.getGuests();
        int guestCount = (guests != null) ? guests.size() : 0;
        tabGuests.setText("Khách (" + guestCount + ")");

        // Update history tab label
        List<BookingHistory> history = room.getHistory();
        if (history != null) {
            tabHistory.setText("Lịch sử (" + history.size() + ")");
        }
    }

    private void setupInfoRows() {
        populateInfoRow(rowStatus, "", "TRẠNG THÁI", getStatusLabel(room.getStatus()));
        // Type row
        populateInfoRow(rowType, "", "LOẠI PHÒNG", getTypeLabel(room.getType()));
        // Max guests row
        populateInfoRow(rowMaxGuests, "", "TỐI ĐA", room.getMaxGuests() + " người");
        // Price row
        populateInfoRow(rowPrice, "", "GIÁ PHÒNG", formatPrice(room.getPrice()) + " VNĐ/đêm");
    }

    private void populateInfoRow(View row, String icon, String label, String value) {
        if (row == null) return;
        TextView tvIcon = row.findViewById(R.id.tvRowIcon);
        TextView tvLabel = row.findViewById(R.id.tvRowLabel);
        TextView tvValue = row.findViewById(R.id.tvRowValue);

        if (tvIcon != null) tvIcon.setText(icon);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value);
    }

    private String getStatusLabel(String status) {
        switch (status) {
            case "occupied": return "Có khách";
            case "vacant": return "Trống";
            case "dirty": return "Cần dọn";
            case "maintenance": return "Bảo trì";
            default: return "Không rõ";
        }
    }

    private Chip createChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A9a7340")));
        chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#409a7340")));
        chip.setChipStrokeWidth(1f * getResources().getDisplayMetrics().density);
        chip.setChipCornerRadius(8f * getResources().getDisplayMetrics().density);
        chip.setTextColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#9a7340")));
        chip.setTextSize(11);
        chip.setClickable(false);
        chip.setCheckable(false);
        return chip;
    }

    private void setupStatus() {
        String status = room.getStatus();
        int badgeBg, badgeStroke, badgeText, numberBg, numberText;
        String label, emoji;

        switch (status) {
            case "occupied":
                badgeBg = Color.parseColor("#1A64A0F0");
                badgeStroke = Color.parseColor("#4064A0F0");
                badgeText = Color.parseColor("#80aaee");
                numberBg = Color.parseColor("#1A64A0F0");
                numberText = Color.parseColor("#80aaee");
                label = "Có khách";
                emoji = "";
                break;
            case "vacant":
                badgeBg = Color.parseColor("#1A6FCF97");
                badgeStroke = Color.parseColor("#406FCF97");
                badgeText = Color.parseColor("#6ec98a");
                numberBg = Color.parseColor("#1A6FCF97");
                numberText = Color.parseColor("#6ec98a");
                label = "Trống";
                emoji = "";
                break;
            case "dirty":
                badgeBg = Color.parseColor("#1AFBBF24");
                badgeStroke = Color.parseColor("#40FBBF24");
                badgeText = Color.parseColor("#f0b43c");
                numberBg = Color.parseColor("#1AFBBF24");
                numberText = Color.parseColor("#f0b43c");
                label = "Cần dọn";
                emoji = "";
                break;
            case "maintenance":
                badgeBg = Color.parseColor("#1AEB5757");
                badgeStroke = Color.parseColor("#40EB5757");
                badgeText = Color.parseColor("#e87070");
                numberBg = Color.parseColor("#1AEB5757");
                numberText = Color.parseColor("#e87070");
                label = "Bảo trì";
                emoji = "";
                break;
            default:
                badgeBg = Color.parseColor("#1A7C7A72");
                badgeStroke = Color.parseColor("#407C7A72");
                badgeText = Color.parseColor("#7C7A72");
                numberBg = Color.parseColor("#1A7C7A72");
                numberText = Color.parseColor("#7C7A72");
                label = "Không rõ";
                emoji = "";
        }

        if (tvStatusBadge.getBackground() instanceof GradientDrawable) {
            GradientDrawable gd = (GradientDrawable) tvStatusBadge.getBackground();
            gd.setColor(badgeBg);
            gd.setStroke((int) (1 * getResources().getDisplayMetrics().density), badgeStroke);
        }
        tvStatusBadge.setTextColor(badgeText);
        tvStatusBadge.setText(label);

        // Update room number background color in hero
        View numberBgView = findViewById(R.id.viewHeroAvatarBg);
        if (numberBgView != null && numberBgView.getBackground() instanceof GradientDrawable) {
            GradientDrawable gd = (GradientDrawable) numberBgView.getBackground();
            gd.setColor(numberBg);
        }
        tvHeroNumber.setTextColor(numberText);
    }

    private void setupGuestsTab() {
        layoutTabGuests.removeAllViews();
        List<Guest> guests = room.getGuests();

        if (guests == null || guests.isEmpty()) {
            if ("vacant".equals(room.getStatus())) {
                // Show booking card
                com.google.android.material.card.MaterialCardView bookingCard = new com.google.android.material.card.MaterialCardView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 0, 24);
                bookingCard.setLayoutParams(params);
                bookingCard.setRadius(18f * getResources().getDisplayMetrics().density);
                bookingCard.setCardElevation(0f);
                bookingCard.setCardBackgroundColor(Color.parseColor("#0D9a7340")); // Very light brown tint
                bookingCard.setStrokeColor(Color.parseColor("#269a7340")); // Light brown stroke
                bookingCard.setStrokeWidth((int)(1 * getResources().getDisplayMetrics().density));
                
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(android.view.Gravity.CENTER);
                layout.setPadding(24, 48, 24, 48);
                
                TextView title = new TextView(this);
                title.setText("Sẵn sàng đón khách");
                title.setTextSize(16);
                title.setTextColor(Color.parseColor("#1a1000"));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                title.setGravity(android.view.Gravity.CENTER);
                
                TextView desc = new TextView(this);
                desc.setText("Phòng hiện đang trống. Hãy chọn khách hàng để tiến hành đặt phòng.");
                desc.setTextSize(12);
                desc.setTextColor(Color.parseColor("#7a7568"));
                desc.setGravity(android.view.Gravity.CENTER);
                LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                descParams.topMargin = (int) (6 * getResources().getDisplayMetrics().density);
                desc.setLayoutParams(descParams);
                
                com.google.android.material.button.MaterialButton btnBook = 
                    new com.google.android.material.button.MaterialButton(this);
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    (int) (180 * getResources().getDisplayMetrics().density),
                    (int) (44 * getResources().getDisplayMetrics().density)
                );
                btnParams.topMargin = (int) (28 * getResources().getDisplayMetrics().density);
                btnBook.setLayoutParams(btnParams);
                btnBook.setText("ĐẶT PHÒNG NGAY");
                btnBook.setTextSize(12);
                btnBook.setLetterSpacing(0.05f);
                btnBook.setTypeface(null, android.graphics.Typeface.BOLD);
                btnBook.setTextColor(Color.parseColor("#FFFFFF"));
                btnBook.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#9a7340")));
                btnBook.setCornerRadius((int) (12 * getResources().getDisplayMetrics().density));
                btnBook.setElevation(0f);
                btnBook.setStateListAnimator(null);
                btnBook.setOnClickListener(v -> openSelectCustomer());
                
                layout.addView(title);
                layout.addView(desc);
                layout.addView(btnBook);
                bookingCard.addView(layout);
                layoutTabGuests.addView(bookingCard);
                
            } else if ("dirty".equals(room.getStatus())) {
                TextView prompt = new TextView(this);
                prompt.setText("Phòng cần được dọn dẹp trước khi nhận khách mới.");
                prompt.setTextSize(13);
                prompt.setTextColor(Color.parseColor("#7a7568"));
                prompt.setGravity(android.view.Gravity.CENTER);
                prompt.setPadding(20, 30, 20, 30);
                layoutTabGuests.addView(prompt);
            } else if ("maintenance".equals(room.getStatus())) {
                TextView prompt = new TextView(this);
                prompt.setText(room.getNote() != null ? room.getNote() : "Phòng đang trong quá trình bảo trì.");
                prompt.setTextSize(13);
                prompt.setTextColor(Color.parseColor("#7a7568"));
                prompt.setGravity(android.view.Gravity.CENTER);
                prompt.setPadding(20, 30, 20, 30);
                layoutTabGuests.addView(prompt);
            }
            return;
        }

        // Show guest list
        for (int i = 0; i < guests.size(); i++) {
            Guest guest = guests.get(i);
            View guestView = getLayoutInflater().inflate(R.layout.item_guest, layoutTabGuests, false);
            TextView tvName = guestView.findViewById(R.id.tvGuestName);
            TextView tvInfo = guestView.findViewById(R.id.tvGuestInfo);
            TextView tvAvatar = guestView.findViewById(R.id.tvGuestAvatar);
            TextView tvStar = guestView.findViewById(R.id.tvRepresentativeStar);

            tvName.setText(guest.getName());
            
            String cccd = guest.getCccd();
            String phone = guest.getPhone();
            
            String cccdVal = (cccd == null || cccd.isEmpty() || "null".equals(cccd) || "-".equals(cccd)) ? "-" : cccd;
            String phoneVal = (phone == null || phone.isEmpty() || "null".equals(phone) || "-".equals(phone)) ? "-" : phone;
            
            tvInfo.setText(String.format("CCCD: %s | SĐT: %s", cccdVal, phoneVal));

            // Set avatar initials and color (Squircle style)
            String initials = getInitials(guest.getName());
            tvAvatar.setText(initials);
            
            int colorIndex = Math.abs(guest.getName().hashCode()) % 6;
            int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b, 0xFF7b5ea7};
            int color = colors[colorIndex];
            
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setCornerRadius(12 * getResources().getDisplayMetrics().density);
            
            // 10% alpha for background
            int alphaBg = Math.round(255 * 0.1f);
            int bgColor = Color.argb(alphaBg, Color.red(color), Color.green(color), Color.blue(color));
            gd.setColor(bgColor);
            
            // 25% alpha for stroke
            int alphaStroke = Math.round(255 * 0.25f);
            int strokeColor = Color.argb(alphaStroke, Color.red(color), Color.green(color), Color.blue(color));
            gd.setStroke((int)(1 * getResources().getDisplayMetrics().density), strokeColor);
            
            tvAvatar.setBackground(gd);
            tvAvatar.setTextColor(color);
            tvAvatar.setTypeface(null, android.graphics.Typeface.BOLD);

            if (i == 0) {
                tvStar.setVisibility(View.VISIBLE);
            }

            layoutTabGuests.addView(guestView);
        }

        // Add checkout button
        View checkoutCard = getLayoutInflater().inflate(R.layout.card_checkout, layoutTabGuests, false);
        TextView tvSummary = checkoutCard.findViewById(R.id.tvCheckoutSummary);
        
        int nights = calculateNights(room.getCheckIn(), room.getCheckOut());
        double total = nights * room.getPrice();
        tvSummary.setText(String.format(Locale.getDefault(), "Tổng tiền: %s (%d đêm × %s)", 
            formatPrice(total), nights, formatPrice(room.getPrice())));

        View btnCheckout = checkoutCard.findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Trả phòng & Thanh toán")
                .setMessage(String.format("Xác nhận khách trả phòng %s?\nTổng tiền: %s", 
                    room.getNumber(), formatPrice(total)))
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    com.example.hotel_management.data.db.RoomRepository repo = 
                        new com.example.hotel_management.data.db.RoomRepository(this);
                    if (repo.checkoutRoom(room, total, nights)) {
                        Toast.makeText(this, "Đã trả phòng " + room.getNumber(), Toast.LENGTH_SHORT).show();
                        // Refresh data
                        room = repo.getRoomById(room.getId());
                        updateRoomData();
                        setupGuestsTab();
                    } else {
                        Toast.makeText(this, "Lỗi khi xử lý trả phòng", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        });
        layoutTabGuests.addView(checkoutCard);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private int calculateNights(String checkIn, String checkOut) {
        try {
            if (checkIn == null || checkOut == null) return 1;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            java.util.Date d1 = sdf.parse(checkIn);
            java.util.Date d2 = sdf.parse(checkOut);
            if (d1 == null || d2 == null) return 1;
            long diff = d2.getTime() - d1.getTime();
            int nights = (int) (diff / (1000 * 60 * 60 * 24));
            return Math.max(1, nights);
        } catch (Exception e) {
            return 1;
        }
    }

    private void setupHistoryTab() {
        layoutTabHistory.removeAllViews();
        List<BookingHistory> history = room.getHistory();

        if (history == null || history.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Chưa có lịch sử sử dụng");
            empty.setTextSize(13);
            empty.setTextColor(Color.parseColor("#7a7870"));
            empty.setGravity(android.view.Gravity.CENTER);
            empty.setPadding(20, 30, 20, 30);
            layoutTabHistory.addView(empty);
            return;
        }

        for (BookingHistory h : history) {
            View historyView = getLayoutInflater().inflate(R.layout.item_history, layoutTabHistory, false);
            TextView tvId = historyView.findViewById(R.id.tvHistoryId);
            TextView tvCheckIn = historyView.findViewById(R.id.tvCheckIn);
            TextView tvCheckOut = historyView.findViewById(R.id.tvCheckOut);
            TextView tvNights = historyView.findViewById(R.id.tvNights);
            TextView tvTotal = historyView.findViewById(R.id.tvHistoryTotal);
            TextView tvGuests = historyView.findViewById(R.id.tvHistoryGuests);

            tvId.setText("#" + h.getId());
            tvCheckIn.setText(h.getCheckIn());
            tvCheckOut.setText(h.getCheckOut());
            tvNights.setText(h.getNights() + " đêm");
            tvTotal.setText(formatPrice(h.getTotal()));
            tvGuests.setText(h.getGuests().size() + " người");

            // Display guest names list
            TextView tvGuestNames = historyView.findViewById(R.id.tvHistoryGuestNames);
            if (h.getGuests() != null && !h.getGuests().isEmpty()) {
                StringBuilder sb = new StringBuilder("Khách: ");
                for (int i = 0; i < h.getGuests().size(); i++) {
                    sb.append(h.getGuests().get(i).getName());
                    if (i < h.getGuests().size() - 1) sb.append(", ");
                }
                tvGuestNames.setText(sb.toString());
                tvGuestNames.setVisibility(View.VISIBLE);
                historyView.findViewById(R.id.tvHistoryGuestNames).setVisibility(View.VISIBLE);
            } else {
                tvGuestNames.setVisibility(View.GONE);
                // Also hide the divider if possible, but the view above is easier to just keep
            }

            layoutTabHistory.addView(historyView);
        }
    }

    private String getTypeLabel(String type) {
        if (type == null) return "Đơn";
        switch (type.toLowerCase()) {
            case "single": return "Đơn";
            case "double": return "Đôi";
            case "quad": return "4 người";
            default: return type;
        }
    }

    private String getTypeIcon(String type) {
        return "";
    }

    private String formatPrice(double price) {
        if (price >= 1000000) {
            return String.format(Locale.getDefault(), "%.1fM", price / 1000000.0);
        } else if (price >= 1000) {
            return String.format(Locale.getDefault(), "%.0fK", price / 1000.0);
        }
        return String.valueOf((int) price);
    }

    private void openSelectCustomer() {
        Intent intent = new Intent(this, SelectCustomerActivity.class);
        intent.putExtra("room", room);
        intent.putExtra("maxGuests", room.getMaxGuests());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when coming back from booking
        if (room != null) {
            room = refreshRoomData();
            if (room != null) {
                updateRoomData();
                if (currentTab == 1) {
                    setupGuestsTab();
                } else if (currentTab == 2) {
                    setupHistoryTab();
                }
            }
        }
    }

    private Room refreshRoomData() {
        com.example.hotel_management.data.db.RoomRepository repo = 
            new com.example.hotel_management.data.db.RoomRepository(this);
        return repo.getRoomById(room.getId());
    }

    private void showDeleteConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Xoá phòng?")
                .setMessage("Dữ liệu của phòng " + room.getNumber() + " sẽ bị xoá vĩnh viễn.")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (dialog, which) -> {
                    com.example.hotel_management.data.db.RoomRepository repo = new com.example.hotel_management.data.db.RoomRepository(this);
                    repo.deleteRoom(room.getId());
                    Toast.makeText(this, "Đã xoá phòng " + room.getNumber(), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
