# Hướng dẫn chạy dự án Muzi

Dự án Muzi là một ứng dụng Android cơ bản.

## Cách chạy (Command Line)

Hệ thống của bạn có phiên bản Gradle mặc định cũ, không tương thích với dự án này. Vì vậy, bạn **PHẢI** sử dụng Gradle Wrapper đi kèm theo dự án.

1. **Build APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   *Lần chạy đầu tiên sẽ tự động tải phiên bản Gradle 8.2 phù hợp.*

2. **Cài đặt vào thiết bị**:
   Kết nối thiết bị của bạn và chạy:
   ```bash
   ./gradlew installDebug
   ```
   Hoặc cài file APK thủ công:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Cách chạy (Android Studio)
1. Mở dự án trong Android Studio.
2. Đợi Sync xong.
3. Nhấn nút Run.

## Cấu trúc dự án
- `app/src/main/java`: Mã nguồn Java (MainActivity).
- `app/src/main/res/layout`: Giao diện XML.


adb logcat | grep com.example.hotel_management
