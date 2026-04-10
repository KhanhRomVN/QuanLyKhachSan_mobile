# Tóm tắt Codebase Dự án Quản lý Khách sạn (Hotel Management)

Tài liệu này tóm tắt cấu trúc và chức năng của các file quan trọng trong thư mục xử lý giao diện và logic nghiệp vụ của ứng dụng.

## 1. Thư mục gốc: `com.example.hotel_management`

*   **`MainActivity.java`**: Điểm khởi đầu của ứng dụng sau khi đăng nhập. Quản lý việc chuyển đổi giữa các Fragment và thanh điều hướng (Navigation).

## 2. Thư mục `data`: Quản lý dữ liệu

### `data/model` (Các thực thể dữ liệu)
*   **`User.java`**: Thông tin tài khoản người dùng (email, role).
*   **`Room.java`**: Thông tin chi tiết về phòng (số phòng, loại, trạng thái, giá).
*   **`Staff.java`**: Thông tin nhân viên.
*   **`Customer.java`**: Thông tin khách hàng.
*   **`Guest.java`**: Thông tin khách nghỉ.
*   **`Booking.java`** & **`BookingHistory.java`**: Thông tin đặt phòng và lịch sử.
*   **`Transaction.java`**: Lưu trữ các giao dịch thanh toán và hóa đơn.
*   **`MonthlyStat.java`**: Phục vụ việc thống kê theo tháng.
*   **`Comment.java`**: Quản lý các đánh giá/bình luận của khách.

### `data/db` (Xử lý Cơ sở dữ liệu)
*   **`DatabaseHelper.java`**: Quản lý việc tạo, cập nhật cơ sở dữ liệu SQLite và khởi tạo dữ liệu mẫu.
*   **`UserRepository.java`**, **`RoomRepository.java`**, **`StaffRepository.java`**, **`CustomerRepository.java`**, **`TransactionRepository.java`**: Các lớp trung gian thực hiện các thao tác CRUD (Thêm, Đọc, Sửa, Xóa) trên cơ sở dữ liệu cho từng đối tượng tương ứng.

## 3. Thư mục `ui`: Xử lý giao diện người dùng

### `ui/auth` (Xác thực)
*   **`LoginActivity.java`**: Xử lý màn hình đăng nhập, xác thực thông tin admin/nhân viên.

### `ui/fragments` (Các màn hình chức năng)
*   **`RoomListFragment.java`**: Màn hình chính hiển thị danh sách tất cả các phòng và trạng thái của chúng.
*   **`RoomDetailFragment.java`**: Hiển thị chi tiết một phòng, cho phép đặt phòng hoặc thanh toán.
*   **`CustomerListFragment.java`**: Quản lý danh sách khách hàng đã từng ở tại khách sạn.
*   **`StaffListFragment.java`**: Quản lý danh sách nhân viên (chỉ dành cho Admin).
*   **`RevenueFragment.java`**: Hiển thị báo cáo doanh thu dưới dạng biểu đồ và danh sách.
*   **`ManagementFragment.java`**: Các chức năng quản lý tổng quát.
*   **`SettingsFragment.java`**: Cho phép người dùng chỉnh sửa thông tin cá nhân hoặc cài đặt ứng dụng.

### `ui/adapter` (Bộ điều phối hiển thị)
*   **`RoomAdapter.java`**, **`CustomerAdapter.java`**, **`StaffAdapter.java`**, **`TransactionAdapter.java`**, **`BookingHistoryAdapter.java`**: Kết nối dữ liệu từ Model với giao diện RecyclerView để hiển thị danh sách một cách mượt mà.

### `ui/view` (Giao diện tùy chỉnh)
*   **`BarChartView.java`**: Vẽ biểu đồ cột để biểu diễn số liệu doanh thu hoặc thống kê.

## 4. Thư mục `util` & `utils`: Các tiện ích hỗ trợ
*   **`AuthManager.java`**: Quản lý phiên đăng nhập (session) của người dùng hiện tại trong toàn bộ ứng dụng.
*   Các file tiện ích khác giúp định dạng tiền tệ, ngày tháng và kiểm tra dữ liệu đầu vào.
