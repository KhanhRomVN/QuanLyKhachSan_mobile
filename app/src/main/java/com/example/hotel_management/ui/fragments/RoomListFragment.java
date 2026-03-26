package com.example.hotel_management.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Room;
import com.example.hotel_management.management.RoomDetailActivity;
import com.example.hotel_management.ui.adapter.RoomAdapter;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomListFragment extends Fragment implements RoomAdapter.OnRoomClickListener {

    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private List<Room> allRooms;
    private List<Room> filteredRooms = new ArrayList<>();
    private EditText etSearch;
    private TextView tvRoomCount;
    private LinearLayout layoutStatusChips;
    private View layoutEmptyState;
    private com.example.hotel_management.data.db.RoomRepository roomRepository;

    private String selectedStatus = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);

        roomRepository = new com.example.hotel_management.data.db.RoomRepository(getContext());
        initViews(view);
        loadRooms();
        setupRecyclerView();
        setupSearch();
        setupStatusChips();
        applyFilters();

        return view;
    }

    private void loadRooms() {
        allRooms = roomRepository.getAllRooms();
        if (allRooms == null) {
            allRooms = new ArrayList<>();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRooms();
        applyFilters();
    }

    private void initViews(View view) {
        rvRooms = view.findViewById(R.id.rvRooms);
        etSearch = view.findViewById(R.id.etSearch);
        tvRoomCount = view.findViewById(R.id.tvRoomCount);
        layoutStatusChips = view.findViewById(R.id.layoutStatusChips);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        view.findViewById(R.id.btnAddRoom).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.hotel_management.management.RoomFormActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new RoomAdapter(filteredRooms, this);
        rvRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRooms.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupStatusChips() {
        layoutStatusChips.removeAllViews();
        String[] statusKeys = {"all", "occupied", "vacant", "dirty", "maintenance"};
        String[] statusLabels = {"Tất cả", "Có khách", "Trống", "Cần dọn", "Bảo trì"};

        for (int i = 0; i < statusKeys.length; i++) {
            final String statusKey = statusKeys[i];
            int count = getCountForStatus(statusKey);
            String label = statusLabels[i] + " (" + count + ")";
            TextView chip = createChip(label, "all".equals(statusKey));
            chip.setOnClickListener(v -> {
                selectedStatus = statusKey;
                updateStatusChipsSelection();
                applyFilters();
            });
            layoutStatusChips.addView(chip);
        }
    }

    private int getCountForStatus(String status) {
        if (allRooms == null) return 0;
        if ("all".equals(status)) return allRooms.size();
        int count = 0;
        for (Room r : allRooms) {
            if (status.equals(r.getStatus())) count++;
        }
        return count;
    }

    private void updateStatusChipsSelection() {
        for (int i = 0; i < layoutStatusChips.getChildCount(); i++) {
            View chip = layoutStatusChips.getChildAt(i);
            if (chip instanceof TextView) {
                String[] statusKeys = {"all", "occupied", "vacant", "dirty", "maintenance"};
                boolean isSelected = statusKeys[i].equals(selectedStatus);
                if (isSelected) {
                    chip.setBackgroundResource(R.drawable.bg_accent_badge);
                    ((TextView) chip).setTextColor(android.graphics.Color.parseColor("#9a7340"));
                } else {
                    chip.setBackgroundResource(R.drawable.bg_chip_inactive);
                    ((TextView) chip).setTextColor(android.graphics.Color.parseColor("#7a7568"));
                }
            }
        }
    }

    private void updateChipsCount() {
        for (int i = 0; i < layoutStatusChips.getChildCount(); i++) {
            View chip = layoutStatusChips.getChildAt(i);
            if (chip instanceof TextView) {
                String[] statusKeys = {"all", "occupied", "vacant", "dirty", "maintenance"};
                String statusKey = statusKeys[i];
                int count = getCountForStatus(statusKey);
                String[] statusLabels = {"Tất cả", "Có khách", "Trống", "Cần dọn", "Bảo trì"};
                ((TextView) chip).setText(statusLabels[i] + " (" + count + ")");
            }
        }
    }

    private TextView createChip(String label, boolean isSelected) {
        TextView chip = new TextView(getContext());
        chip.setText(label);
        chip.setTextSize(13);
        chip.setPadding(20, 10, 20, 10);
        chip.setGravity(android.view.Gravity.CENTER);
        chip.setBackgroundResource(isSelected ? R.drawable.bg_accent_badge : R.drawable.bg_chip_inactive);
        chip.setTextColor(isSelected ? 
            android.graphics.Color.parseColor("#9a7340") : 
            android.graphics.Color.parseColor("#7a7568"));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 8, 0);
        chip.setLayoutParams(params);
        
        return chip;
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase(Locale.getDefault());

        filteredRooms.clear();
        for (Room r : allRooms) {
            boolean matchSearch = r.getNumber().toLowerCase(Locale.getDefault()).contains(query) ||
                                 getTypeLabel(r.getType()).toLowerCase(Locale.getDefault()).contains(query) ||
                                 (r.getGuestName() != null && r.getGuestName().toLowerCase(Locale.getDefault()).contains(query));

            boolean matchStatus = "all".equals(selectedStatus) || r.getStatus().equals(selectedStatus);

            if (matchSearch && matchStatus) {
                filteredRooms.add(r);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateCounts();
        updateEmptyState();
    }

    private String getTypeLabel(String type) {
        if (type == null) return "Đơn";
        switch (type.toLowerCase()) {
            case "single": return "Đơn";
            case "double": return "Đôi";
            case "quad": return "4 người";
            case "phòng đơn": return "Đơn";
            case "phòng đôi": return "Đôi";
            case "phòng 4 người": return "4 người";
            default: return type;
        }
    }

    private void updateCounts() {
        if (allRooms == null) return;
        tvRoomCount.setText(filteredRooms.size() + " phòng");
        updateChipsCount();
    }

    private void updateEmptyState() {
        if (filteredRooms.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvRooms.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvRooms.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRoomClick(Room room) {
        Intent intent = new Intent(getContext(), RoomDetailActivity.class);
        intent.putExtra("room", room);
        startActivity(intent);
    }
}
