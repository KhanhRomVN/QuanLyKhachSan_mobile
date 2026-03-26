package com.example.hotel_management.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.hotel_management.R;
import com.example.hotel_management.management.CreateHotelActivity;
import com.example.hotel_management.management.HotelManagementActivity;
import com.example.hotel_management.management.ManageRoomActivity;

public class ManagementFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management, container, false);

        view.findViewById(R.id.cardManageRooms).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManageRoomActivity.class));
        });

        view.findViewById(R.id.cardHotelManagement).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HotelManagementActivity.class));
        });

        view.findViewById(R.id.cardCreateHotel).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CreateHotelActivity.class));
        });

        return view;
    }
}
