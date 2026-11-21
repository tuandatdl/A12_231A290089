package vn.edu.vhu.ltdd.a12sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "A12_231A290089";

    public static final String DB_NAME = "quanlysv.db";
    public static final int DB_VERSION = 1; // Tăng số này mỗi khi đổi cấu trúc bảng

    public static final String TABLE = "sinh_vien";
    public static final String COL_ID = "id";
    public static final String COL_MA_SV = "ma_sv";
    public static final String COL_HO_TEN = "ho_ten";
    public static final String COL_LOP = "lop";
    public static final String COL_DIEM = "diem";

    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_MA_SV + " TEXT NOT NULL UNIQUE, "
                    + COL_HO_TEN + " TEXT NOT NULL, "
                    + COL_LOP + " TEXT, "
                    + COL_DIEM + " REAL DEFAULT 0)";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /** Chỉ chạy MỘT LẦN duy nhất lúc tệp .db chưa tồn tại trên thiết bị. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: tạo bảng " + TABLE);
        db.execSQL(SQL_CREATE);

        // Chèn sẵn 3 dòng mẫu để màn hình có dữ liệu ngay lần chạy đầu
        db.execSQL("INSERT INTO " + TABLE + " ("
                + COL_MA_SV + ", " + COL_HO_TEN + ", " + COL_LOP + ", " + COL_DIEM + ") VALUES "
                + "('231A290089', 'Đỗ Lê Tuấn Đạt', 'CNTT K23', 8.8), "
                + "('231A290090', 'Trần Thị B', 'CNTT K23', 7.2), "
                + "('231A290091', 'Lê Văn C', 'CNTT K23', 9.2)");
    }

    /** Chạy khi DB_VERSION tăng lên. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade " + oldVersion + " -> " + newVersion + ": xóa và tạo lại bảng");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
