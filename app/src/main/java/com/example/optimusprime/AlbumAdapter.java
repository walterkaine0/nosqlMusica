package com.example.optimusprime;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import android.view.View;
import com.bumptech.glide.Glide;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<DataSnapshot> albumList;
    private OnAlbumClickListener listener;

    public interface OnAlbumClickListener {
        void onAlbumClick(String albumId);
    }

    public AlbumAdapter(List<DataSnapshot> albumList, OnAlbumClickListener listener) {
        this.albumList = albumList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataSnapshot snapshot = albumList.get(position);
        Album album = snapshot.getValue(Album.class);

        if (album != null) {
            holder.title.setText(album.title);
            Glide.with(holder.itemView.getContext())
                    .load(album.coverUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.image);

            holder.itemView.setOnClickListener(v -> listener.onAlbumClick(snapshot.getKey()));
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.idTVAlbumName);
            image = itemView.findViewById(R.id.idIVAlbum);
        }
    }
}
