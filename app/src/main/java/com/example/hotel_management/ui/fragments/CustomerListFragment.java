package com.example.hotel_management.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Booking;
import com.example.hotel_management.data.model.Customer;
import com.example.hotel_management.ui.adapter.CustomerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment hiển thị Danh sách Khách hàng.
 * Quản lý thông tin hồ sơ khách hàng, tìm kiếm và hiển thị các số liệu thống kê về khách.
 */
public class CustomerListFragment extends Fragment implements CustomerAdapter.OnCustomerClickListener {

    private RecyclerView rvCustomers;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private EditText etSearch;
    private TextView tvTotalCount, tvStatTotal, tvStatStaying, tvStatRevenue;
    private com.example.hotel_management.data.db.CustomerRepository customerRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp giao diện từ fragment_customer_list
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
        
        customerRepository = new com.example.hotel_management.data.db.CustomerRepository(getContext());
        initViews(view);
        loadCustomers();
        setupRecyclerView();
        filter(""); // Hiển thị toàn bộ danh sách lúc ban đầu
        updateStats();

        return view;
    }

    /**
     * Tải danh sách khách hàng từ Database.
     */
    private void loadCustomers() {
        customerList = customerRepository.getAllCustomers();
    }

    /**
     * Làm mới dữ liệu mỗi khi người dùng quay lại màn hình này.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadCustomers();
        filter(etSearch.getText().toString());
    }

    /**
     * Ánh xạ View và thiết lập sự kiện.
     */
    private void initViews(View view) {
        rvCustomers = view.findViewById(R.id.rvCustomerList);
        etSearch = view.findViewById(R.id.etSearchCustomer);
        tvTotalCount = view.findViewById(R.id.tvFilteredCount);
        
        // Các TextView hiển thị thống kê ở trên cùng
        tvStatTotal = view.findViewById(R.id.tvStatTotalCustomers);
        tvStatStaying = view.findViewById(R.id.tvStatStayingCustomers);
        tvStatRevenue = view.findViewById(R.id.tvStatTotalRevenue);

        // Nút thêm khách hàng mới
        view.findViewById(R.id.btnAddCustomer).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.hotel_management.management.CustomerFormActivity.class);
            startActivity(intent);
        });

        // Lắng nghe thay đổi trong ô tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Thiết lập hiển thị danh sách bằng RecyclerView.
     */
    private void setupRecyclerView() {
        adapter = new CustomerAdapter(new ArrayList<>(customerList), this);
        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(adapter);
    }

    /**
     * Bộ lọc tìm kiếm: Cho phép tìm theo Tên, CCCD, Số điện thoại hoặc Email.
     */
    private void filter(String text) {
        List<Customer> filteredList = new ArrayList<>();
        for (Customer customer : customerList) {
            String query = text.toLowerCase();
            if (customer.getName().toLowerCase().contains(query) ||
                customer.getCccd().contains(query) ||
                customer.getPhone().contains(query) ||
                customer.getEmail().toLowerCase().contains(query)) {
                filteredList.add(customer);
            }
        }
        adapter.updateList(filteredList);
        tvTotalCount.setText(filteredList.size() + " khách hàng");
    }

    /**
     * Cập nhật các chỉ số thống kê khách hàng trên giao diện.
     */
    private void updateStats() {
        tvStatTotal.setText(String.valueOf(customerList.size())); // Tổng số khách trong DB
        
        int stayingCount = 0;
        long totalRevenue = 0;
        
        for (Customer c : customerList) {
            boolean isStaying = false;
            if (c.getBookings() != null) {
                for (Booking b : c.getBookings()) {
                    totalRevenue += b.getTotal(); // Cộng dồn doanh thu từ tất cả lần đặt phòng
                    if ("staying".equals(b.getStatus())) {
                        isStaying = true; // Kiểm tra khách có đang lưu trú tại khách sạn không
                    }
                }
            }
            if (isStaying) stayingCount++;
        }
        
        tvStatStaying.setText(String.valueOf(stayingCount));
        // Hiển thị doanh thu rút gọn (Ví dụ: 15.5M)
        tvStatRevenue.setText(String.format("%.1fM", totalRevenue / 1000000.0));
    }

    /**
     * Xử lý click vào khách hàng: Chuyển sang màn hình Chi tiết khách hàng.
     */
    @Override
    public void onCustomerClick(Customer customer) {
        Intent intent = new Intent(getContext(), com.example.hotel_management.management.CustomerDetailActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }
}
