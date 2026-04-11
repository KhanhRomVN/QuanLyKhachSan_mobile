package com.example.hotel_management.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_management.MainActivity;
import com.example.hotel_management.R;
import com.example.hotel_management.data.db.UserRepository;
import com.example.hotel_management.data.model.User;

/**
 * Hoạt động Đăng nhập (LoginActivity).
 * Xử lý xác thực người dùng, kiểm tra phiên làm việc và điều hướng vào màn hình chính.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Kiểm tra phiên đăng nhập (Session)
        // Thực hiện trước setContentView để tránh hiện tượng nháy màn hình nếu đã đăng nhập rồi
        com.example.hotel_management.utils.SessionManager sessionManager = new com.example.hotel_management.utils.SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            // Nếu đã đăng nhập, chuyển thẳng vào màn hình chính (MainActivity)
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Kết thúc Activity này để không quay lại được bằng nút Back
            return;
        }

        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        initViews();
        setupListeners();
    }

    /**
     * Ánh xạ các thành phần giao diện từ file XML.
     */
    private void initViews() {
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    /**
     * Thiết lập các sự kiện lắng nghe tương tác người dùng.
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    /**
     * Xử lý logic khi người dùng nhấn nút Đăng nhập.
     */
    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra tài khoản trong cơ sở dữ liệu thông qua Repository
        User user = userRepository.login(email, password);
        if (user != null) {
            // Đăng nhập thành công: Lưu thông tin vào SessionManager (SharedPreferences)
            com.example.hotel_management.utils.SessionManager sessionManager = new com.example.hotel_management.utils.SessionManager(this);
            sessionManager.setLogin(true, email, user.getRole());
            
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            
            // Chuyển hướng vào màn hình chính
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Đăng nhập thất bại
            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }
}
