package com.example.hotel_management.management;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.db.CustomerRepository;
import com.example.hotel_management.data.model.Customer;
import com.example.hotel_management.ui.adapter.CustomerAdapter;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectCustomerActivity extends AppCompatActivity {

    private RecyclerView rvCustomers;
    private EditText etSearch;
    private MaterialButton btnConfirm, btnAddNewCustomer;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private List<Customer> filteredList;
    private List<Customer> selectedCustomers = new ArrayList<>();
    private int maxGuests;
    private com.example.hotel_management.data.model.Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer);

        maxGuests = getIntent().getIntExtra("maxGuests", 1);
        room = (com.example.hotel_management.data.model.Room) getIntent().getSerializableExtra("room");

        initViews();
        loadCustomers();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBackSelectCustomer).setOnClickListener(v -> finish());
        rvCustomers = findViewById(R.id.rvCustomers);
        etSearch = findViewById(R.id.etSearch);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnAddNewCustomer = findViewById(R.id.btnAddNewCustomer);

        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddNewCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerFormActivity.class);
            startActivity(intent);
        });

        btnConfirm.setOnClickListener(v -> {
            if (selectedCustomers.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 khách", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("room", room);
            intent.putExtra("selectedCustomers", new ArrayList<>(selectedCustomers));
            intent.putExtra("maxGuests", maxGuests);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Booking successful, close this activity to return to RoomDetailActivity
            finish();
        }
    }

    private void loadCustomers() {
        CustomerRepository repo = new CustomerRepository(this);
        customerList = repo.getAllCustomers();
        filteredList = new ArrayList<>(customerList);
        updateAdapter();
    }

    private void filterCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredList = new ArrayList<>(customerList);
        } else {
            String lowerQuery = query.toLowerCase();
            filteredList = customerList.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery) ||
                        c.getCccd().contains(query) ||
                        c.getPhone().contains(query))
                .collect(Collectors.toList());
        }
        updateAdapter();
    }

    private void updateAdapter() {
        if (adapter == null) {
            adapter = new CustomerAdapter(filteredList, this::onCustomerClick, true);
            rvCustomers.setAdapter(adapter);
        } else {
            adapter.setData(filteredList);
        }
    }

    private void onCustomerClick(Customer customer) {
        // Toggle selection
        boolean removed = false;
        for (int i = 0; i < selectedCustomers.size(); i++) {
            if (selectedCustomers.get(i).getId() == customer.getId()) {
                selectedCustomers.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            if (selectedCustomers.size() < maxGuests) {
                selectedCustomers.add(customer);
            } else {
                Toast.makeText(this, "Phòng này tối đa " + maxGuests + " người", Toast.LENGTH_SHORT).show();
            }
        }

        if (adapter != null) {
            adapter.setSelectedCustomers(selectedCustomers);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomers();
    }
}
