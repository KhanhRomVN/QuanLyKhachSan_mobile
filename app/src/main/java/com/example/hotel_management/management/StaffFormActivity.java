package com.example.hotel_management.management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Staff;
import com.google.android.material.button.MaterialButton;

public class StaffFormActivity extends AppCompatActivity {

    private Staff staff;
    private boolean isEdit;
    
    private EditText etName, etPhone, etEmail, etPassword;
    private MaterialButton btnRoleAdmin, btnRoleStaff;
    private String selectedRole = "Nhân viên";
    private TextView tvTitle, tvSubTitle, tvPassLabel;
    private Button btnSave;

    private com.example.hotel_management.data.db.StaffRepository staffRepository;
    private com.example.hotel_management.data.db.UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_form);

        staffRepository = new com.example.hotel_management.data.db.StaffRepository(this);
        userRepository = new com.example.hotel_management.data.db.UserRepository(this);
        staff = (Staff) getIntent().getSerializableExtra("staff");
        isEdit = staff != null;

        initViews();
        
        if (isEdit) {
            populateData();
        } else {
            updateRoleUI();
        }
    }

    private void initViews() {
        findViewById(R.id.btnBackForm).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancelForm).setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvFormTitle);
        tvSubTitle = findViewById(R.id.tvFormSubTitle);
        tvPassLabel = findViewById(R.id.tvPassLabel);
        btnSave = findViewById(R.id.btnSaveStaff);

        etName = findViewById(R.id.etStaffNameForm);
        etPhone = findViewById(R.id.etStaffPhoneForm);
        etEmail = findViewById(R.id.etStaffEmailForm);
        etPassword = findViewById(R.id.etStaffPasswordForm);

        btnRoleAdmin = findViewById(R.id.btnRoleAdmin);
        btnRoleStaff = findViewById(R.id.btnRoleStaff);

        btnRoleAdmin.setOnClickListener(v -> {
            selectedRole = "Admin";
            updateRoleUI();
        });
        btnRoleStaff.setOnClickListener(v -> {
            selectedRole = "Nhân viên";
            updateRoleUI();
        });

        if (isEdit) {
            tvTitle.setText("Sửa thông tin");
            tvSubTitle.setText("Cập nhật: " + staff.getName());
            tvPassLabel.setText("Mật khẩu mới (để trống nếu không đổi)");
            btnSave.setText("Lưu thay đổi");
        }

        btnSave.setOnClickListener(v -> handleSave());
    }

    private void updateRoleUI() {
        if ("Admin".equals(selectedRole)) {
            btnRoleAdmin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF9a7340));
            btnRoleAdmin.setTextColor(0xFFFFFFFF);
            
            btnRoleStaff.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x0D000000));
            btnRoleStaff.setTextColor(0xFF7a7568);
        } else {
            btnRoleStaff.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF9a7340));
            btnRoleStaff.setTextColor(0xFFFFFFFF);
            
            btnRoleAdmin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x0D000000));
            btnRoleAdmin.setTextColor(0xFF7a7568);
        }
    }

    private void populateData() {
        if (staff == null) return;
        etName.setText(staff.getName());
        etPhone.setText(staff.getPhone());
        etEmail.setText(staff.getEmail());
        selectedRole = staff.getRole();
        updateRoleUI();
    }

    private void handleSave() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldEmail = isEdit ? staff.getEmail() : null;

        if (staff == null) {
            staff = new Staff(name, email, phone, "Phòng nhân sự", selectedRole, "active", "25/03/2026");
        } else {
            staff.setName(name);
            staff.setEmail(email);
            staff.setPhone(phone);
            staff.setRole(selectedRole);
        }
        
        if (!isEdit || !password.isEmpty()) {
            staff.setPassword(password);
        }

        String internalRole = "Admin".equalsIgnoreCase(selectedRole) ? "admin" : "staff";

        if (isEdit) {
            staffRepository.updateStaff(staff);
            userRepository.updateUser(oldEmail, new com.example.hotel_management.data.model.User(0, email, staff.getPassword(), internalRole));
        } else {
            staffRepository.insertStaff(staff);
            userRepository.register(new com.example.hotel_management.data.model.User(0, email, staff.getPassword(), internalRole));
        }

        Toast.makeText(this, isEdit ? "Đã lưu thay đổi" : "Đã tạo tài khoản mới", Toast.LENGTH_SHORT).show();
        finish();
    }
}
