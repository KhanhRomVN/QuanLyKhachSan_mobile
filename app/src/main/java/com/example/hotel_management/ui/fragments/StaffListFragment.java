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

/**
 * Fragment hiển thị Danh sách Nhân viên.
 * Hỗ trợ các chức năng: Xem danh sách nhân viên, Xem thông tin cá nhân hiện tại, Tìm kiếm, Đăng xuất và Chuyển tài khoản nhanh.
 */
public class StaffListFragment extends Fragment implements StaffAdapter.OnStaffClickListener {

    private RecyclerView rvStaff;
    private StaffAdapter adapter;
    private List<Staff> allStaff;
    private List<Staff> filteredStaff = new ArrayList<>();
    private EditText etSearch;
    private TextView tvStaffCount;
    private com.example.hotel_management.data.db.StaffRepository staffRepository;
    private com.example.hotel_management.utils.SessionManager sessionManager;
    private Staff currentUser;

    // Các View hiển thị thông tin người dùng đang đăng nhập (Thẻ trên cùng)
    private View cardCurrentUser;
    private View viewCurrentUserAvatarBg;
    private TextView tvCurrentUserAvatarInitials, tvCurrentUserName, tvCurrentUserEmail;
    private android.widget.ImageButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout fragment_staff_list
        View view = inflater.inflate(R.layout.fragment_staff_list, container, false);
        
        staffRepository = new com.example.hotel_management.data.db.StaffRepository(getContext());
        sessionManager = new com.example.hotel_management.utils.SessionManager(getContext());
        
        initViews(view);
        loadStaff();
        updateCurrentUserUI();
        applyFilters();
        setupRecyclerView();
        
        return view;
    }

    /**
     * Tải danh sách toàn bộ nhân viên từ cơ sở dữ liệu.
     */
    private void loadStaff() {
        allStaff = staffRepository.getAllStaff();
    }

    /**
     * Làm mới dữ liệu khi người dùng quay lại màn hình này.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadStaff();
        updateCurrentUserUI();
        applyFilters();
    }

    /**
     * Ánh xạ các thành phần giao diện và thiết lập các sự kiện nút bấm.
     */
    private void initViews(View view) {
        rvStaff = view.findViewById(R.id.rvStaff);
        etSearch = view.findViewById(R.id.etStaffSearch);
        tvStaffCount = view.findViewById(R.id.tvStaffCount);
        
        // UI người dùng hiện tại
        cardCurrentUser = view.findViewById(R.id.cardCurrentUser);
        viewCurrentUserAvatarBg = view.findViewById(R.id.viewCurrentUserAvatarBg);
        tvCurrentUserAvatarInitials = view.findViewById(R.id.tvCurrentUserAvatarInitials);
        tvCurrentUserName = view.findViewById(R.id.tvCurrentUserName);
        tvCurrentUserEmail = view.findViewById(R.id.tvCurrentUserEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        btnLogout.setOnClickListener(v -> handleLogout());
        
        // Nút thêm nhân viên mới
        View btnAdd = view.findViewById(R.id.btnAddStaff);
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), StaffFormActivity.class));
        });
        
        // Lắng nghe thay đổi ô tìm kiếm
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

    /**
     * Cập nhật thông tin của người dùng đang đăng nhập lên thẻ thông tin trên cùng.
     */
    private void updateCurrentUserUI() {
        String email = sessionManager.getUserEmail();
        currentUser = null;
        // Tìm thông tin hồ sơ staff tương ứng với email đang đăng nhập
        for (Staff s : allStaff) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                currentUser = s;
                break;
            }
        }

        if (currentUser != null) {
            tvCurrentUserName.setText(currentUser.getName());
            tvCurrentUserEmail.setText(currentUser.getEmail());
            tvCurrentUserAvatarInitials.setText(currentUser.getAvatar());
            
            // Logic đổ màu Avatar ngẫu nhiên theo ký tự đầu
            int[] colors = {0xFF9a7340, 0xFF3464b4, 0xFF3a8a5a, 0xFFb47814, 0xFFc0392b};
            int colorIdx = Math.abs(currentUser.getAvatar().hashCode()) % colors.length;
            int baseColor = colors[colorIdx];
            
            android.graphics.drawable.GradientDrawable avatarBg = new android.graphics.drawable.GradientDrawable();
            avatarBg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            avatarBg.setCornerRadius(14 * getResources().getDisplayMetrics().density);
            avatarBg.setColor(adjustAlpha(baseColor, 0.15f));
            avatarBg.setStroke(3, adjustAlpha(baseColor, 0.4f));
            viewCurrentUserAvatarBg.setBackground(avatarBg);
            tvCurrentUserAvatarInitials.setTextColor(baseColor);
            
            // Click vào thẻ cá nhân để xem chi tiết thông tin của chính mình
            cardCurrentUser.setOnClickListener(v -> onStaffClick(currentUser));
        }
    }

    /**
     * Phương thức tiện ích để điều chỉnh độ trong suốt (alpha) cho màu sắc UI.
     */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(android.graphics.Color.alpha(color) * factor);
        if (alpha == 0) alpha = Math.round(255 * factor);
        return android.graphics.Color.argb(alpha, android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color));
    }

    /**
     * Xử lý đăng xuất tài khoản.
     */
    private void handleLogout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
            .setTitle("Đăng xuất?")
            .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                // Xóa trạng thái đăng nhập trong Session
                sessionManager.setLogin(false, "", "");
                // Chuyển về màn hình Đăng nhập và xóa hết stack cũ
                Intent intent = new Intent(getContext(), com.example.hotel_management.ui.auth.LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Thiết lập danh sách hiển thị nhân viên bằng RecyclerView.
     */
    private void setupRecyclerView() {
        adapter = new StaffAdapter(filteredStaff, this);
        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStaff.setAdapter(adapter);
    }

    /**
     * Lọc danh sách nhân viên: Tìm kiếm theo tên/email và lọc bỏ người dùng hiện tại khỏi danh sách hiển thị bên dưới.
     */
    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase();
        filteredStaff.clear();
        String currentEmail = sessionManager.getUserEmail();
        
        for (Staff s : allStaff) {
            // Không hiển thị chính mình trong danh sách danh bạ chung (vì đã có card trên cùng)
            if (s.getEmail().equalsIgnoreCase(currentEmail)) continue;

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

    /**
     * Xử lý click vào nhân viên: Chuyển sang màn hình Chi tiết nhân viên.
     */
    @Override
    public void onStaffClick(Staff staff) {
        Intent intent = new Intent(getContext(), StaffDetailActivity.class);
        if (staff != null) {
            intent.putExtra("staff", staff);
        }
        startActivity(intent);
    }

    /**
     * Xử lý click chuyển đổi tài khoản (nếu được hỗ trợ bởi Adapter).
     */
    @Override
    public void onSwitchAccountClick(Staff staff) {
        showFastSwitchDialog(staff);
    }

    /**
     * Giao diện hộp thoại chuyển đổi tài khoản nhanh (hỏi mật khẩu).
     */
    private void showFastSwitchDialog(Staff staff) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Đăng nhập nhanh");
        builder.setMessage("Nhập mật khẩu cho " + staff.getName());

        final EditText input = new EditText(getContext());
        input.setHint("Mật khẩu");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        
        android.widget.FrameLayout container = new android.widget.FrameLayout(getContext());
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) (24 * getResources().getDisplayMetrics().density);
        params.rightMargin = (int) (24 * getResources().getDisplayMetrics().density);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            String password = input.getText().toString();
            // So sánh mật khẩu trực tiếp (Dùng cho demo hoặc hệ thống nội bộ đơn giản)
            if (password.equals(staff.getPassword())) {
                sessionManager.setLogin(true, staff.getEmail(), staff.getRole().equalsIgnoreCase("admin") ? "admin" : "staff");
                Toast.makeText(getContext(), "Đã chuyển sang tài khoản " + staff.getName(), Toast.LENGTH_SHORT).show();
                
                // Khởi động lại ứng dụng vào màn hình chính
                Intent intent = new Intent(getContext(), com.example.hotel_management.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
