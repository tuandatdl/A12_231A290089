# INT4211 - LẬP TRÌNH TRÊN CÁC THIẾT BỊ DI ĐỘNG
## BÁO CÁO THỰC HÀNH LAB A12: SQLITE (1) – TẠO CSDL, THÊM & TRUY VẤN DỮ LIỆU

---

### THÔNG TIN SINH VIÊN
* **Họ và tên:** Đỗ Lê Tuấn Đạt
* **Mã số sinh viên (MSSV):** 231A290089
* **Lớp học phần:** Lập trình thiết bị di động (INT4211)
* **Môi trường thực thi:** Android Studio trên macOS (Apple Silicon)
* **Thiết bị thử nghiệm:** Máy ảo Pixel 8 (Android 17, API 37.1 – aarch64)
* **Repository GitHub:** `https://github.com/tuandatdl/A12_231A290089` (Private, đã mời giảng viên làm Collaborator)

---

### CÁC MỤC ĐÃ HOÀN THÀNH

#### 1. Quản lý tệp CSDL SQLite với DbHelper (Phần 2, 3)
* Thiết kế model `SinhVien.java` với 2 constructor tách biệt cho luồng đọc từ CSDL (`id` thật) và luồng tạo mới (`id = -1`).
* Cài đặt `DbHelper` kế thừa `SQLiteOpenHelper` quản lý tệp `quanlysv.db` (`DB_VERSION = 1`).
* Khởi tạo bảng `sinh_vien` với các ràng buộc toàn vẹn: `id INTEGER PRIMARY KEY AUTOINCREMENT`, `ma_sv TEXT NOT NULL UNIQUE`, `ho_ten TEXT NOT NULL`, `lop TEXT`, `diem REAL DEFAULT 0` cùng 3 bản ghi mẫu khởi tạo trong `onCreate()`.

#### 2. Kiến trúc DAO (Data Access Object) tách biệt hoàn toàn SQL (Phần 4)
* Tập trung 100% câu lệnh truy vấn vào lớp `SinhVienDao.java`; `MainActivity` không chứa bất kỳ chuỗi SQL nào.
* **Thêm dữ liệu (`themMoi`):** Sử dụng `ContentValues` + `db.insert()` phòng chống SQL Injection và xử lý lỗi ký tự đặc biệt; kiểm tra giá trị trả về `-1` để phát hiện trùng lặp khóa `UNIQUE` và phản hồi lỗi trực tiếp lên `EditText`.
* **Truy vấn dữ liệu (`layTatCa`, `dem`, `diemTrungBinh`):** Sử dụng `db.query()` và `db.rawQuery()`, duyệt con trỏ bằng `while (c.moveToNext())`, lấy chỉ số cột qua `getColumnIndexOrThrow()` và luôn đóng `Cursor` trong khối `try-finally`.

#### 3. Bài nâng cao NC1 & NC2
* **NC1 (Lưu lớp mặc định bằng SharedPreferences):** Kết hợp lưu trữ cấu hình nhẹ `SharedPreferences ("cai_dat")` với CSDL quan hệ `SQLite` — tự động lưu lại tên lớp sau mỗi lần thêm sinh viên và khôi phục vào ô nhập khi khởi động lại ứng dụng.
* **NC2 (Thống kê mở rộng bằng SQL):** Bổ sung truy vấn `COUNT(*) WHERE diem >= 5` và `MAX(diem)` trong `SinhVienDao` để hiển thị số sinh viên đạt cùng điểm cao nhất của lớp.
