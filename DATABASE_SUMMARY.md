# Tóm tắt Cấu trúc Cơ sở dữ liệu (Database Summary)

Hệ thống sử dụng SQLite để quản lý dữ liệu cục bộ. Tên file cơ sở dữ liệu là `muzi_hotel.db`.

## 1. Danh sách các bảng (Tables)

### Bảng `users` (Quản lý tài khoản)
Lưu trữ thông tin tài khoản đăng nhập vào hệ thống.
*   `id`: Khóa chính (Tự động tăng).
*   `email`: Địa chỉ email (Dùng làm tên đăng nhập, phải là duy nhất).
*   `password`: Mật khẩu đăng nhập.
*   `role`: Quyền hạn (ví dụ: `admin`, `staff`).

### Bảng `rooms` (Quản lý phòng)
Lưu trữ thông tin và trạng thái hiện tại của các phòng trong khách sạn.
*   `id`: Khóa chính.
*   `number`: Số phòng (ví dụ: 101, 102).
*   `type`: Loại phòng (Phòng đơn, Phòng đôi, VIP...).
*   `status`: Trạng thái (`vacant` - trống, `occupied` - có khách, `cleaning` - đang dọn).
*   `price`: Giá phòng mỗi đêm (Số thực).
*   `description`: Mô tả chi tiết về phòng.
*   `guest_name`: Tên khách đang ở (nếu có).
*   `check_in`: Thời gian nhận phòng.
*   `check_out`: Thời gian trả phòng.
*   `max_guests`: Số lượng khách tối đa.
*   `amenities`: Danh sách tiện nghi (Wifi, Tivi, Điều hòa...).

### Bảng `staff` (Quản lý nhân viên)
Lưu trữ thông tin chi tiết về đội ngũ nhân viên.
*   `id`: Khóa chính.
*   `name`: Họ và tên nhân viên.
*   `email`: Email liên lạc.
*   `password`: Mật khẩu riêng cho nhân viên.
*   `phone`: Số điện thoại.
*   `role`: Chức vụ.
*   `department`: Bộ phận làm việc.
*   `status`: Trạng thái làm việc (`active`, `inactive`).
*   `start_date`: Ngày bắt đầu làm việc.

### Bảng `customers` (Quản lý khách hàng)
Lưu trữ thông tin hồ sơ của khách hàng đã hoặc đang sử dụng dịch vụ.
*   `id`: Khóa chính.
*   `name`: Tên khách hàng.
*   `cccd`: Số Căn cước công dân/Chứng minh thư.
*   `phone`: Số điện thoại liên hệ.
*   `email`: Email khách hàng.
*   `address`: Địa chỉ thường trú.
*   `dob`: Ngày sinh.
*   `gender`: Giới tính.

### Bảng `transactions` (Quản lý giao dịch & Hóa đơn)
Lưu trữ lịch sử thanh toán và doanh thu.
*   `id`: Khóa chính.
*   `tx_id`: Mã giao dịch định danh.
*   `title`: Tiêu đề giao dịch (ví dụ: Thanh toán phòng 101).
*   `room`: Số phòng liên quan.
*   `guest`: Tên khách hàng thực hiện giao dịch.
*   `amount`: Tổng số tiền thanh toán.
*   `type`: Loại giao dịch (Thanh toán phòng, Dịch vụ thêm...).
*   `date`: Ngày thực hiện giao dịch.
*   `status`: Trạng thái (Thành công, Chờ xử lý...).
*   `check_in`: Ngày khách nhận phòng (để đối soát).
*   `nights`: Số đêm đã ở.

## 2. Quản lý dữ liệu
*   **DatabaseHelper**: Phụ trách khởi tạo các bảng trên và thêm dữ liệu mặc định (Admin mặc định và các phòng mẫu).
*   **Repository Pattern**: Mỗi bảng có một lớp Repository tương ứng (`RoomRepository`, `CustomerRepository`, ...) để thực hiện các truy vấn SQL, giúp mã nguồn sạch sẽ và dễ bảo trì hơn. 
