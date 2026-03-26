package com.example.hotel_management.management;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Room;
import com.google.android.material.button.MaterialButton;

public class RoomFormActivity extends AppCompatActivity {

    private Room room;
    private boolean isEdit = false;

    private EditText etRoomNumber, etPrice, etNote;
    private MaterialButton btnTypeSingle, btnTypeDouble, btnTypeQuad;
    private String selectedType = "Phòng đơn";
    private com.google.android.material.chip.ChipGroup cgAmenities;

    private com.example.hotel_management.data.db.RoomRepository roomRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_form);

        roomRepository = new com.example.hotel_management.data.db.RoomRepository(this);
        room = (Room) getIntent().getSerializableExtra("room");
        isEdit = (room != null);

        initViews();
        
        if (isEdit) {
            fillData();
        } else {
            updateTypeSelection();
        }
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        
        TextView tvTitle = findViewById(R.id.tvFormTitle);
        tvTitle.setText(isEdit ? "Sửa phòng " + room.getNumber() : "Thêm phòng mới");

        etRoomNumber = findViewById(R.id.etRoomNumber);
        etPrice = findViewById(R.id.etPrice);
        etNote = findViewById(R.id.etNote);
        
        btnTypeSingle = findViewById(R.id.btnTypeSingle);
        btnTypeDouble = findViewById(R.id.btnTypeDouble);
        btnTypeQuad = findViewById(R.id.btnTypeQuad);

        btnTypeSingle.setOnClickListener(v -> {
            selectedType = "Phòng đơn";
            updateTypeSelection();
        });

        btnTypeDouble.setOnClickListener(v -> {
            selectedType = "Phòng đôi";
            updateTypeSelection();
        });

        btnTypeQuad.setOnClickListener(v -> {
            selectedType = "Phòng 4 người";
            updateTypeSelection();
        });

        cgAmenities = findViewById(R.id.cgAmenities);

        findViewById(R.id.btnSave).setOnClickListener(v -> handleSave());
    }

    private void updateTypeSelection() {
        int primaryColor = getResources().getColor(R.color.primary);
        int inactiveBg = android.graphics.Color.parseColor("#0D000000");
        int inactiveText = android.graphics.Color.parseColor("#7a7568");

        // Reset all
        btnTypeSingle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveBg));
        btnTypeSingle.setTextColor(inactiveText);
        btnTypeDouble.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveBg));
        btnTypeDouble.setTextColor(inactiveText);
        btnTypeQuad.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveBg));
        btnTypeQuad.setTextColor(inactiveText);

        // Set active
        if ("Phòng đơn".equals(selectedType)) {
            btnTypeSingle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            btnTypeSingle.setTextColor(android.graphics.Color.WHITE);
        } else if ("Phòng đôi".equals(selectedType)) {
            btnTypeDouble.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            btnTypeDouble.setTextColor(android.graphics.Color.WHITE);
        } else if ("Phòng 4 người".equals(selectedType)) {
            btnTypeQuad.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
            btnTypeQuad.setTextColor(android.graphics.Color.WHITE);
        }
    }

    private void fillData() {
        etRoomNumber.setText(room.getNumber());
        etPrice.setText(String.valueOf((int)room.getPrice()));
        etNote.setText(room.getNote());
        
        selectedType = room.getType();
        if (selectedType == null || selectedType.isEmpty()) {
            selectedType = "Phòng đơn";
        }
        updateTypeSelection();

        if (room.getAmenities() != null) {
            for (int i = 0; i < cgAmenities.getChildCount(); i++) {
                com.google.android.material.chip.Chip chip = (com.google.android.material.chip.Chip) cgAmenities.getChildAt(i);
                if (room.getAmenities().contains(chip.getText().toString())) {
                    chip.setChecked(true);
                }
            }
        }
    }

    private void handleSave() {
        String number = etRoomNumber.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (number.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (room == null) {
            room = new Room();
            room.setStatus("vacant");
        }
        
        room.setNumber(number);
        room.setPrice(Double.parseDouble(priceStr));
        room.setNote(note);
        room.setType(selectedType);

        // Set max guests based on type
        if ("Phòng đơn".equals(selectedType)) {
            room.setMaxGuests(1);
        } else if ("Phòng đôi".equals(selectedType)) {
            room.setMaxGuests(2);
        } else if ("Phòng 4 người".equals(selectedType)) {
            room.setMaxGuests(4);
        }

        java.util.List<String> selectedAmenities = new java.util.ArrayList<>();
        for (int i = 0; i < cgAmenities.getChildCount(); i++) {
            com.google.android.material.chip.Chip chip = (com.google.android.material.chip.Chip) cgAmenities.getChildAt(i);
            if (chip.isChecked()) {
                selectedAmenities.add(chip.getText().toString());
            }
        }
        room.setAmenities(selectedAmenities);

        if (isEdit) {
            roomRepository.updateRoom(room);
        } else {
            roomRepository.insertRoom(room);
        }

        Toast.makeText(this, isEdit ? "Đã lưu thay đổi" : "Đã tạo phòng mới", Toast.LENGTH_SHORT).show();
        finish();
    }
}
