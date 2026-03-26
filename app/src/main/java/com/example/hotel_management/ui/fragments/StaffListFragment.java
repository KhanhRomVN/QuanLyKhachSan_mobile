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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Staff;
import com.example.hotel_management.management.StaffDetailActivity;
import com.example.hotel_management.management.StaffFormActivity;
import com.example.hotel_management.ui.adapter.StaffAdapter;
import java.util.ArrayList;
import java.util.List;

public class StaffListFragment extends Fragment implements StaffAdapter.OnStaffClickListener {

    private RecyclerView rvStaff;
    private StaffAdapter adapter;
    private List<Staff> allStaff;
    private List<Staff> filteredStaff = new ArrayList<>();
    private EditText etSearch;
    private TextView tvStaffCount;
    private com.example.hotel_management.data.db.StaffRepository staffRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_list, container, false);
        
        staffRepository = new com.example.hotel_management.data.db.StaffRepository(getContext());
        initViews(view);
        loadStaff();
        applyFilters();
        setupRecyclerView();
        
        return view;
    }

    private void loadStaff() {
        allStaff = staffRepository.getAllStaff();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStaff();
        applyFilters();
    }

    private void initViews(View view) {
        rvStaff = view.findViewById(R.id.rvStaff);
        etSearch = view.findViewById(R.id.etStaffSearch);
        tvStaffCount = view.findViewById(R.id.tvStaffCount);
        
        com.example.hotel_management.utils.SessionManager sessionManager = new com.example.hotel_management.utils.SessionManager(getContext());
        View btnAdd = view.findViewById(R.id.btnAddStaff);
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), StaffFormActivity.class));
        });
        
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


    private void setupRecyclerView() {
        adapter = new StaffAdapter(filteredStaff, this);
        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStaff.setAdapter(adapter);
    }


    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase();
 
        filteredStaff.clear();
        for (Staff s : allStaff) {
            boolean matchSearch = s.getName().toLowerCase().contains(query) || 
                                 s.getEmail().toLowerCase().contains(query);
            
            if (matchSearch) {
                filteredStaff.add(s);
            }
        }
        
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        tvStaffCount.setText(filteredStaff.size() + " nhân viên");
    }

    @Override
    public void onStaffClick(Staff staff) {
        Intent intent = new Intent(getContext(), StaffDetailActivity.class);
        intent.putExtra("staff", staff);
        startActivity(intent);
    }
}
