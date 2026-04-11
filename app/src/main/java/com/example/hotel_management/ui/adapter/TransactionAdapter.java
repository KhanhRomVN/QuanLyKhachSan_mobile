package com.example.hotel_management.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Transaction;
import java.util.List;

/**
 * Bộ điều phối hiển thị Giao dịch (TransactionAdapter).
 * Chịu trách nhiệm hiển thị danh sách các hoạt động tài chính (Thanh toán phòng, Dịch vụ, Hoàn tiền) 
 * trong màn hình Doanh thu.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TxViewHolder> {

    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_transaction cho từng dòng
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
        Transaction tx = transactions.get(position);
        holder.bind(tx);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * Lớp lưu trữ tham chiếu các View của một dòng giao dịch.
     */
    static class TxViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvGuest, tvAmount, tvDate, tvRoom, tvType, tvNights;
        View viewStatusDot; // Dấu chấm hiển thị trạng thái màu sắc

        public TxViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvTxId);
            tvGuest = itemView.findViewById(R.id.tvTxGuest);
            tvAmount = itemView.findViewById(R.id.tvTxAmount);
            tvDate = itemView.findViewById(R.id.tvTxDate);
            tvRoom = itemView.findViewById(R.id.tvTxRoom);
            tvType = itemView.findViewById(R.id.tvTxType);
            tvNights = itemView.findViewById(R.id.tvTxNights);
            viewStatusDot = itemView.findViewById(R.id.viewTxStatusDot);
        }

        /**
         * Gán dữ liệu giao dịch và định dạng hiển thị.
         */
        public void bind(Transaction tx) {
            tvId.setText("#" + (tx.getId() != null ? tx.getId() : "TX" + System.currentTimeMillis()));
            tvGuest.setText(tx.getGuest());
            
            // Định dạng số tiền rút gọn (Ví dụ: 1.5M VND hoặc 500K VND)
            double a = tx.getAmount();
            String aStr = a >= 1000000 ? String.format("%.1fM", a / 1000000.0) : String.format("%.0fK", a / 1000.0);
            
            String status = tx.getStatus() != null ? tx.getStatus().toLowerCase() : "paid";
            boolean isRefund = status.equals("refund");
            
            // Hiển thị số tiền với dấu trừ nếu là hoàn tiền
            tvAmount.setText((isRefund ? "-" : "") + aStr);
            tvAmount.setTextColor(isRefund ? 0xFFc0392b : 0xFF1a1810);
            
            tvDate.setText(tx.getDate());
            tvRoom.setText("Phòng " + (tx.getRoom() != null ? tx.getRoom() : "--"));
            tvType.setText(tx.getType() != null ? tx.getType() : "Dịch vụ");
            tvNights.setText((tx.getNights() > 0 ? tx.getNights() + " đêm" : "Vãng lai"));

            // Thiết lập màu sắc dấu chấm trạng thái (Xanh: Đã thu, Vàng: Chờ, Đỏ: Hoàn tiền)
            if (viewStatusDot != null) {
                int dotColor = 0xFF3a8a5a; // Mặc định là Xanh (Đã thanh toán)
                if (status.equals("pending")) dotColor = 0xFFb47814; // Màu hổ phách (Đang xử lý)
                else if (status.equals("refund")) dotColor = 0xFFc0392b; // Màu đỏ (Hoàn tiền)
                
                viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(dotColor));
            }
        }
    }
}
