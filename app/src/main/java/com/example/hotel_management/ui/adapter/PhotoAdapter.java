package com.example.hotel_management.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_management.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<String> photoPaths;

    public PhotoAdapter(List<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String path = photoPaths.get(position);
        try {
            InputStream is = holder.itemView.getContext().getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.ivPhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            holder.ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return photoPaths.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivGalleryPhoto);
        }
    }
}
