package com.example.hotel_management.management;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Customer;

public class CustomerFormActivity extends AppCompatActivity {

    private Customer editCustomer;
    private EditText etName, etCCCD, etPhone, etEmail, etNote;
    private TextView tvTitle, tvSubTitle;

    private com.example.hotel_management.data.db.CustomerRepository customerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);

        customerRepository = new com.example.hotel_management.data.db.CustomerRepository(this);
        editCustomer = (Customer) getIntent().getSerializableExtra("customer");
        initViews();
        
        if (editCustomer != null) {
            bindData();
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvCustomerFormTitle);
        tvSubTitle = findViewById(R.id.tvCustomerFormSubTitle);
        etName = findViewById(R.id.etCustomerName);
        etCCCD = findViewById(R.id.etCustomerCCCD);
        etPhone = findViewById(R.id.etCustomerPhone);
        etEmail = findViewById(R.id.etCustomerEmail);
        etNote = findViewById(R.id.etCustomerNote);

        findViewById(R.id.btnBackCustomerForm).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancelCustomer).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveCustomerForm).setOnClickListener(v -> saveCustomer());
    }

    private void bindData() {
        tvTitle.setText("Sửa khách hàng");
        tvSubTitle.setText("Cập nhật thông tin khách hàng");
        etName.setText(editCustomer.getName());
        etCCCD.setText(editCustomer.getCccd());
        etPhone.setText(editCustomer.getPhone());
        etEmail.setText(editCustomer.getEmail());
        etNote.setText(editCustomer.getNote());
    }

    private void saveCustomer() {
        String name = etName.getText().toString().trim();
        String cccd = etCCCD.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Họ và tên là bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editCustomer == null) {
            editCustomer = new Customer(name, cccd, phone, email);
        } else {
            editCustomer.setName(name);
            editCustomer.setCccd(cccd);
            editCustomer.setPhone(phone);
            editCustomer.setEmail(email);
        }

        editCustomer.setNote(note);

        if (editCustomer.getId() > 0) {
            customerRepository.updateCustomer(editCustomer);
        } else {
            customerRepository.insertCustomer(editCustomer);
        }

        String msg = editCustomer.getId() > 0 ? "Đã cập nhật thông tin khách hàng" : "Đã thêm khách hàng mới";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
