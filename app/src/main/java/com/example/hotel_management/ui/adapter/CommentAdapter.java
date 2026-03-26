package com.example.hotel_management.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import com.example.hotel_management.data.model.Comment;
import java.io.InputStream;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvUser.setText(comment.getUserName());
        holder.tvDate.setText(comment.getDate());
        holder.tvText.setText(comment.getCommentText());
        
        // Handle rating stars visibility
        int rating = (int) comment.getRating();
        for (int i = 0; i < holder.ratingLayout.getChildCount(); i++) {
            holder.ratingLayout.getChildAt(i).setVisibility(i < rating ? View.VISIBLE : View.GONE);
        }

        // Use default avatar if no imagePath provided for mock
        if (comment.getUserAvatar() != null && !comment.getUserAvatar().isEmpty()) {
            try {
                InputStream is = holder.itemView.getContext().getAssets().open(comment.getUserAvatar());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.ivAvatar.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.ivAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUser, tvDate, tvText;
        ViewGroup ratingLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivCommentAvatar);
            tvUser = itemView.findViewById(R.id.tvCommentUser);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            ratingLayout = itemView.findViewById(R.id.tvCommentRatingLayout);
            tvText = itemView.findViewById(R.id.tvCommentText);
        }
    }
}
