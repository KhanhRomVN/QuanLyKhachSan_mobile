package com.example.hotel_management;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.hotel_management.ui.fragments.RoomListFragment;
import com.example.hotel_management.ui.fragments.StaffListFragment;
import com.example.hotel_management.ui.fragments.CustomerListFragment;
import com.example.hotel_management.ui.fragments.RevenueFragment;
import com.example.hotel_management.ui.fragments.PlaceholderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemActiveIndicatorEnabled(false);
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;
            
            if (itemId == R.id.nav_rooms) {
                fragment = new RoomListFragment();
            } else if (itemId == R.id.nav_staff) {
                fragment = new StaffListFragment();
            } else if (itemId == R.id.nav_customers) {
                fragment = new CustomerListFragment();
            } else if (itemId == R.id.nav_revenue) {
                fragment = new RevenueFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

        // Default selection
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_rooms);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        int tabToSelect = intent.getIntExtra("select_tab", R.id.nav_rooms);
        bottomNav.setSelectedItemId(tabToSelect);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
