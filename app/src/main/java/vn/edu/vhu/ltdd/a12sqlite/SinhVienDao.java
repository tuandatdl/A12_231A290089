package vn.edu.vhu.ltdd.a12sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) – nơi DUY NHẤT trong ứng dụng biết tới SQL.
 * Activity chỉ gọi themMoi(), layTatCa()... chứ tuyệt đối không viết câu lệnh SQL.
 */
public class SinhVienDao {

    private final DbHelper helper;

    public SinhVienDao(Context context) {
        this.helper = new DbHelper(context.getApplicationContext());
    }

    /**
     * Thêm một sinh viên bằng ContentValues (chống SQL Injection).
     * @return id vừa được SQLite cấp, hoặc -1 nếu vi phạm ràng buộc UNIQUE / NOT NULL.
     */
    public long themMoi(SinhVien sv) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(DbHelper.COL_MA_SV, sv.getMaSv());
        v.put(DbHelper.COL_HO_TEN, sv.getHoTen());
        v.put(DbHelper.COL_LOP, sv.getLop());
        v.put(DbHelper.COL_DIEM, sv.getDiem());

        long id = db.insert(DbHelper.TABLE, null, v);
        if (id != -1) {
            sv.setId(id);
        }
        return id;
    }

    /** Lấy toàn bộ danh sách sinh viên, sắp xếp theo họ tên tăng dần. */
    public List<SinhVien> layTatCa() {
        List<SinhVien> ds = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(
                DbHelper.TABLE,
                null, null, null, null, null,
                DbHelper.COL_HO_TEN + " ASC"
        );

        try {
            // Lấy chỉ số cột MỘT LẦN trước vòng lặp để tối ưu hiệu năng
            int iId = c.getColumnIndexOrThrow(DbHelper.COL_ID);
            int iMa = c.getColumnIndexOrThrow(DbHelper.COL_MA_SV);
            int iTen = c.getColumnIndexOrThrow(DbHelper.COL_HO_TEN);
            int iLop = c.getColumnIndexOrThrow(DbHelper.COL_LOP);
            int iDiem = c.getColumnIndexOrThrow(DbHelper.COL_DIEM);

            while (c.moveToNext()) {
                ds.add(new SinhVien(
                        c.getLong(iId),
                        c.getString(iMa),
                        c.getString(iTen),
                        c.getString(iLop),
                        c.getDouble(iDiem)
                ));
            }
        } finally {
            c.close(); // Bắt buộc đóng Cursor để tránh rò rỉ bộ nhớ
        }
        return ds;
    }

    /** Đếm tổng số sinh viên bằng rawQuery. */
    public int dem() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DbHelper.TABLE, null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    /** Tính điểm trung bình của cả lớp bằng hàm tổng hợp AVG. */
    public double diemTrungBinh() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(" + DbHelper.COL_DIEM + ") FROM " + DbHelper.TABLE, null);
        try {
            return c.moveToFirst() ? c.getDouble(0) : 0;
        } finally {
            c.close();
        }
    }

    /** NC2: Đếm số sinh viên đạt (điểm >= 5) bằng rawQuery. */
    public int demDat() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + DbHelper.TABLE + " WHERE " + DbHelper.COL_DIEM + " >= 5",
                null
        );
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    /** NC2: Lấy điểm cao nhất của lớp bằng hàm tổng hợp MAX. */
    public double diemCaoNhat() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT MAX(" + DbHelper.COL_DIEM + ") FROM " + DbHelper.TABLE,
                null
        );
        try {
            return c.moveToFirst() ? c.getDouble(0) : 0;
        } finally {
            c.close();
        }
    }

    /** Giải phóng kết nối CSDL (gọi trong onDestroy của Activity). */
    public void dong() {
        helper.close();
    }
}
