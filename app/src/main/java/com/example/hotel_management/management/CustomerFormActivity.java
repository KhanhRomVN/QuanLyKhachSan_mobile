package com.example.hotel_management.management;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Customer;

/**
 * Hoạt động Biểu mẫu Khách hàng (CustomerFormActivity).
 * Dùng để Thêm mới khách hàng hoặc Chỉnh sửa thông tin hồ sơ hiện có.
 */
public class CustomerFormActivity extends AppCompatActivity {

    private Customer editCustomer; // Đối tượng khách hàng đang được xử lý
    private EditText etName, etCCCD, etPhone, etEmail, etNote;
    private TextView tvTitle, tvSubTitle;

    private com.example.hotel_management.data.db.CustomerRepository customerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);

        customerRepository = new com.example.hotel_management.data.db.CustomerRepository(this);
        
        // Kiểm tra xem Activity được gọi để Thêm mới hay Sửa (thông qua đối tượng truyền vào Intent)
        editCustomer = (Customer) getIntent().getSerializableExtra("customer");
        initViews();
        
        if (editCustomer != null) {
            bindData(); // Đổ dữ liệu cũ vào các ô nhập liệu nếu là chế độ Sửa
        }
    }

    /**
     * Ánh xạ các thành phần giao diện và thiết lập sự kiện.
     */
    private void initViews() {
        tvTitle = findViewById(R.id.tvCustomerFormTitle);
        tvSubTitle = findViewById(R.id.tvCustomerFormSubTitle);
        etName = findViewById(R.id.etCustomerName);
        etCCCD = findViewById(R.id.etCustomerCCCD);
        etPhone = findViewById(R.id.etCustomerPhone);
        etEmail = findViewById(R.id.etCustomerEmail);
        etNote = findViewById(R.id.etCustomerNote);

        // Các nút điều hướng và lưu trữ
        findViewById(R.id.btnBackCustomerForm).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancelCustomer).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveCustomerForm).setOnClickListener(v -> saveCustomer());
    }

    /**
     * Đổ dữ liệu khách hàng hiện tại vào form (Dùng cho chế độ Sửa).
     */
    private void bindData() {
        tvTitle.setText("Sửa khách hàng");
        tvSubTitle.setText("Cập nhật thông tin khách hàng");
        etName.setText(editCustomer.getName());
        etCCCD.setText(editCustomer.getCccd());
        etPhone.setText(editCustomer.getPhone());
        etEmail.setText(editCustomer.getEmail());
        etNote.setText(editCustomer.getNote());
    }

    /**
     * Xử lý lưu khách hàng vào cơ sở dữ liệu.
     */
    private void saveCustomer() {
        // Lấy dữ liệu từ các ô nhập liệu
        String name = etName.getText().toString().trim();
        String cccd = etCCCD.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        // Kiểm tra tính hợp lệ tối thiểu
        if (name.isEmpty()) {
            Toast.makeText(this, "Họ và tên là bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật hoặc tạo mới đối tượng Customer
        if (editCustomer == null) {
            // Chế độ thêm mới: Khởi tạo ID mặc định là 0 (SQLite sẽ tự tăng)
            editCustomer = new Customer(name, cccd, phone, email);
        } else {
            // Chế độ sửa: Cập nhật các trường thông tin
            editCustomer.setName(name);
            editCustomer.setCccd(cccd);
            editCustomer.setPhone(phone);
            editCustomer.setEmail(email);
        }

        editCustomer.setNote(note);

        // Gọi Repository để lưu vào Database dựa trên ID
        if (editCustomer.getId() > 0) {
            customerRepository.updateCustomer(editCustomer);
        } else {
            customerRepository.insertCustomer(editCustomer);
        }

        // Thông báo kết quả và đóng màn hình
        String msg = editCustomer.getId() > 0 ? "Đã cập nhật thông tin khách hàng" : "Đã thêm khách hàng mới";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
