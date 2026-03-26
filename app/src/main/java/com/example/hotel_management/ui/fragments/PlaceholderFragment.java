package com.example.hotel_management.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_TITLE = "title";

    public static PlaceholderFragment newInstance(String title) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : "Placeholder";
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFFE8E4DC);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        tvTitle.setTextSize(24);
        tvTitle.setTextColor(0xFF1A1810);
        tvTitle.setGravity(Gravity.CENTER);
        
        TextView tvSub = new TextView(getContext());
        tvSub.setText("Tính năng " + title + " đang được phát triển");
        tvSub.setTextSize(14);
        tvSub.setTextColor(0xFF7A7568);
        tvSub.setGravity(Gravity.CENTER);
        tvSub.setPadding(0, 16, 0, 0);

        layout.addView(tvTitle);
        layout.addView(tvSub);

        return layout;
    }
}
