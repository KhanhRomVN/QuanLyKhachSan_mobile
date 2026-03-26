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
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
        
        customerRepository = new com.example.hotel_management.data.db.CustomerRepository(getContext());
        initViews(view);
        loadCustomers();
        setupRecyclerView();
        filter("");
        updateStats();

        return view;
    }

    private void loadCustomers() {
        customerList = customerRepository.getAllCustomers();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCustomers();
        filter(etSearch.getText().toString());
    }

    private void initViews(View view) {
        rvCustomers = view.findViewById(R.id.rvCustomerList);
        etSearch = view.findViewById(R.id.etSearchCustomer);
        tvTotalCount = view.findViewById(R.id.tvFilteredCount);
        tvStatTotal = view.findViewById(R.id.tvStatTotalCustomers);
        tvStatStaying = view.findViewById(R.id.tvStatStayingCustomers);
        tvStatRevenue = view.findViewById(R.id.tvStatTotalRevenue);

        view.findViewById(R.id.btnAddCustomer).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.hotel_management.management.CustomerFormActivity.class);
            startActivity(intent);
        });

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

    private void setupRecyclerView() {
        adapter = new CustomerAdapter(new ArrayList<>(customerList), this);
        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(adapter);
    }


    private void filter(String text) {
        List<Customer> filteredList = new ArrayList<>();
        for (Customer customer : customerList) {
            if (customer.getName().toLowerCase().contains(text.toLowerCase()) ||
                customer.getCccd().contains(text) ||
                customer.getPhone().contains(text) ||
                customer.getEmail().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(customer);
            }
        }
        adapter.updateList(filteredList);
        tvTotalCount.setText(filteredList.size() + " khách hàng");
    }

    private void updateStats() {
        tvStatTotal.setText(String.valueOf(customerList.size()));
        
        int stayingCount = 0;
        long totalRevenue = 0;
        for (Customer c : customerList) {
            boolean isStaying = false;
            if (c.getBookings() != null) {
                for (Booking b : c.getBookings()) {
                    totalRevenue += b.getTotal();
                    if ("staying".equals(b.getStatus())) {
                        isStaying = true;
                    }
                }
            }
            if (isStaying) stayingCount++;
        }
        
        tvStatStaying.setText(String.valueOf(stayingCount));
        tvStatRevenue.setText(String.format("%.1fM", totalRevenue / 1000000.0));
    }

    @Override
    public void onCustomerClick(Customer customer) {
        Intent intent = new Intent(getContext(), com.example.hotel_management.management.CustomerDetailActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }
}
