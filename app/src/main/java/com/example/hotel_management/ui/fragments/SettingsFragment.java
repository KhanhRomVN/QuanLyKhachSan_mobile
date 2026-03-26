package com.example.hotel_management.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.hotel_management.R;
import com.example.hotel_management.R;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        // Profile
        TextView tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileEmail.setText("admin@hotel.com");
        
        // Logout
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            com.example.hotel_management.utils.SessionManager sessionManager = new com.example.hotel_management.utils.SessionManager(requireContext());
            sessionManager.logout();
            
            android.widget.Toast.makeText(requireContext(), "Đã đăng xuất", android.widget.Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(requireContext(), com.example.hotel_management.ui.auth.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Placeholders
        View.OnClickListener placeholderListener = v -> 
            android.widget.Toast.makeText(requireContext(), "Tính năng đang phát triển", android.widget.Toast.LENGTH_SHORT).show();

        view.findViewById(R.id.btnChangePassword).setOnClickListener(placeholderListener);
        view.findViewById(R.id.btnLanguage).setOnClickListener(placeholderListener);
        view.findViewById(R.id.btnNotification).setOnClickListener(placeholderListener);
        view.findViewById(R.id.btnHelpCenter).setOnClickListener(placeholderListener);
        view.findViewById(R.id.btnTerms).setOnClickListener(placeholderListener);
        view.findViewById(R.id.btnAbout).setOnClickListener(placeholderListener);

        return view;
    }
}
