package com.example.hotel_management.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Transaction;
import com.example.hotel_management.ui.adapter.TransactionAdapter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RevenueFragment extends Fragment {

    private List<Transaction> transactions;
    private com.example.hotel_management.data.db.TransactionRepository transactionRepository;

    private TextView tvMonthLabel, tvMonthRevenue, tvRevenueGrowth;
    private TextView tvTotalYear, tvTotalBookings, tvAvgOccupancy, tvAvgPerBooking;
    private RecyclerView rvTransactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revenue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (getContext() == null) return;
        
        transactionRepository = new com.example.hotel_management.data.db.TransactionRepository(getContext());
        initViews(view);
        loadData();
    }

    private void initViews(View view) {
        tvMonthLabel = view.findViewById(R.id.tvNewMonthLabel);
        tvMonthRevenue = view.findViewById(R.id.tvNewMonthRevenue);
        tvRevenueGrowth = view.findViewById(R.id.tvNewRevenueGrowth);
        
        tvTotalYear = view.findViewById(R.id.tvNewTotalYear);
        tvTotalBookings = view.findViewById(R.id.tvNewTotalBookings);
        tvAvgOccupancy = view.findViewById(R.id.tvNewAvgOccupancy);
        tvAvgPerBooking = view.findViewById(R.id.tvNewAvgPerBooking);
        
        rvTransactions = view.findViewById(R.id.rvNewTransactions);
    }

    private void loadData() {
        transactions = transactionRepository.getAllTransactions();
        if (transactions == null) transactions = new ArrayList<>();

        updateUI();
        setupRecyclerView();
    }

    private void updateUI() {
        Calendar cal = Calendar.getInstance();
        int curMonth = cal.get(Calendar.MONTH) + 1;
        String curMonthStr = String.format("%02d", curMonth);
        
        tvMonthLabel.setText("Tháng " + curMonth + ", " + cal.get(Calendar.YEAR));

        long monthRevenue = 0;
        long totalYear = 0;
        int monthBookings = 0;
        int totalBookings = transactions.size();

        for (Transaction t : transactions) {
            totalYear += t.getAmount();
            
            // Reliable month check: extract MM from DD/MM/YYYY
            String date = t.getDate();
            boolean isCurMonth = false;
            if (date != null && date.length() >= 5) {
                String monthPart = date.substring(3, 5);
                if (monthPart.equals(curMonthStr)) {
                    isCurMonth = true;
                }
            }

            if (isCurMonth) {
                // Accept various "income" statuses
                String status = t.getStatus() != null ? t.getStatus().toLowerCase() : "";
                if (status.equals("paid") || status.equals("income") || status.equals("đã thu")) {
                    monthRevenue += t.getAmount();
                }
                monthBookings++;
            }
        }

        tvMonthRevenue.setText(formatCurrency(monthRevenue));
        tvTotalYear.setText(formatShort(totalYear));
        tvTotalBookings.setText(String.valueOf(totalBookings));
        
        // Mock occupancy for UI consistency
        tvAvgOccupancy.setText(totalBookings > 0 ? "82%" : "0%");
        
        long avg = totalBookings > 0 ? totalYear / totalBookings : 0;
        tvAvgPerBooking.setText(formatShort(avg));
        
        // Simple growth mock
        tvRevenueGrowth.setText("+12.5%");
    }

    private void setupRecyclerView() {
        // Show last 20 transactions for better overview
        List<Transaction> recent = new ArrayList<>();
        int count = Math.min(transactions.size(), 20);
        for (int i = 0; i < count; i++) {
            recent.add(transactions.get(transactions.size() - 1 - i));
        }
        
        TransactionAdapter adapter = new TransactionAdapter(recent);
        rvTransactions.setAdapter(adapter);
    }

    private String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    private String formatShort(long amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000) return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
    }
}
