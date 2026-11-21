package vn.edu.vhu.ltdd.a12sqlite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class SinhVienAdapter extends RecyclerView.Adapter<SinhVienAdapter.SvViewHolder> {

    static class SvViewHolder extends RecyclerView.ViewHolder {
        final TextView tvHoTen, tvMaLop, tvDiem;

        SvViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvMaLop = itemView.findViewById(R.id.tvMaLop);
            tvDiem = itemView.findViewById(R.id.tvDiem);
        }
    }

    private final List<SinhVien> danhSach;

    public SinhVienAdapter(List<SinhVien> danhSach) {
        this.danhSach = danhSach;
    }

    @NonNull
    @Override
    public SvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sinh_vien, parent, false);
        return new SvViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SvViewHolder holder, int position) {
        SinhVien sv = danhSach.get(position);
        holder.tvHoTen.setText(sv.getHoTen());
        holder.tvMaLop.setText(holder.itemView.getContext()
                .getString(R.string.ma_lop_format, sv.getMaSv(), sv.getLop()));
        holder.tvDiem.setText(String.format(Locale.US, "%.1f", sv.getDiem()));
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }
}
