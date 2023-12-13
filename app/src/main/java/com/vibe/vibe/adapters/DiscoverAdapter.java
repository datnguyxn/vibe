package com.vibe.vibe.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vibe.vibe.R;
import com.vibe.vibe.entities.Album;

import java.util.ArrayList;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder>{
    private static final String TAG = DiscoverAdapter.class.getSimpleName();
    private ArrayList<Album> albums;
    private Context context;

    public DiscoverAdapter(Context context) {
        this.context = context;
        this.albums = new ArrayList<>();
    }
    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.albums.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DiscoverAdapter.DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover, parent, false);
        return new DiscoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverAdapter.DiscoverViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.tvItemName.setText(album.getName());
        holder.tvItemArtist.setText(album.getArtists().get(0).getName());
        holder.ivItem.setImageURI(Uri.parse(album.getImage()));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class DiscoverViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvItemName, tvItemArtist;
        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemArtist = itemView.findViewById(R.id.tvItemArtist);
        }
    }
}
