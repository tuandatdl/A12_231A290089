package vn.edu.vhu.ltdd.a12sqlite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A12_231A290089";

    private SinhVienDao dao;
    private final List<SinhVien> danhSach = new ArrayList<>();
    private SinhVienAdapter adapter;

    private EditText edtMaSv, edtHoTen, edtLop, edtDiem;
    private TextView tvThongKe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        edtMaSv = findViewById(R.id.edtMaSv);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtLop = findViewById(R.id.edtLop);
        edtDiem = findViewById(R.id.edtDiem);
        tvThongKe = findViewById(R.id.tvThongKe);
        Button btnThem = findViewById(R.id.btnThem);
        RecyclerView rv = findViewById(R.id.rvSinhVien);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SinhVienAdapter(danhSach);
        rv.setAdapter(adapter);

        // NC1: Đọc tên lớp đã lưu gần nhất để điền sẵn vào edtLop
        SharedPreferences prefs = getSharedPreferences("cai_dat", MODE_PRIVATE);
        edtLop.setText(prefs.getString("lop_mac_dinh", ""));

        // Tạo DAO – chính lúc này tệp quanlysv.db được tạo nếu chưa có
        dao = new SinhVienDao(this);
        napDanhSach();

        btnThem.setOnClickListener(v -> themSinhVien());
    }

    /** Đọc lại toàn bộ dữ liệu từ CSDL và cập nhật danh sách + thống kê (tích hợp NC2). */
    private void napDanhSach() {
        danhSach.clear();
        danhSach.addAll(dao.layTatCa());
        adapter.notifyDataSetChanged();
        tvThongKe.setText(getString(R.string.thong_ke,
                dao.dem(), dao.diemTrungBinh(), dao.demDat(), dao.diemCaoNhat()));
        Log.d(TAG, "Nạp " + danhSach.size() + " sinh viên từ CSDL");
    }

    private void themSinhVien() {
        String maSv = edtMaSv.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String lop = edtLop.getText().toString().trim();
        String diemStr = edtDiem.getText().toString().trim();

        if (maSv.length() != 10) {
            edtMaSv.setError(getString(R.string.err_ma_sv));
            edtMaSv.requestFocus();
            return;
        }

        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_ho_ten));
            edtHoTen.requestFocus();
            return;
        }

        double diem;
        try {
            diem = Double.parseDouble(diemStr);
        } catch (NumberFormatException e) {
            edtDiem.setError(getString(R.string.err_diem));
            edtDiem.requestFocus();
            return;
        }

        if (diem < 0 || diem > 10) {
            edtDiem.setError(getString(R.string.err_diem_range));
            edtDiem.requestFocus();
            return;
        }

        long id = dao.themMoi(new SinhVien(maSv, hoTen, lop, diem));
        if (id == -1) {
            // Vi phạm ràng buộc UNIQUE của cột ma_sv
            edtMaSv.setError(getString(R.string.err_trung_ma));
            edtMaSv.requestFocus();
            Toast.makeText(this, R.string.err_trung_ma, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Đã thêm sinh viên, id = " + id);
        Toast.makeText(this, getString(R.string.da_them, id), Toast.LENGTH_SHORT).show();

        // NC1: Lưu tên lớp vừa nhập vào SharedPreferences
        getSharedPreferences("cai_dat", MODE_PRIVATE)
                .edit()
                .putString("lop_mac_dinh", lop)
                .apply();

        edtMaSv.setText("");
        edtHoTen.setText("");
        edtDiem.setText("");
        edtMaSv.requestFocus();

        // Đọc lại từ CSDL để chắc chắn giao diện khớp 100% với đĩa
        napDanhSach();
    }

    @Override
    protected void onDestroy() {
        dao.dong(); // Trả lại kết nối CSDL
        super.onDestroy();
    }
}
