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

import com.bumptech.glide.Glide;
import com.vibe.vibe.R;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Playlist;

import java.util.ArrayList;

public class RandomPlaylistsAdapter extends RecyclerView.Adapter<RandomPlaylistsAdapter.RandomPlaylistsAdapterHolder>{
    private static final String TAG = RandomPlaylistsAdapter.class.getSimpleName();
    private ArrayList<Album> albums;
    private Context context;

    public RandomPlaylistsAdapter(Context context) {
        this.context = context;
        this.albums = new ArrayList<>();
    }
    public void setPlaylists(ArrayList<Album> playlists) {
        this.albums = playlists;
        notifyDataSetChanged();
    }
    public void clearData() {
        this.albums.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RandomPlaylistsAdapter.RandomPlaylistsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover, parent, false);
        return new RandomPlaylistsAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RandomPlaylistsAdapter.RandomPlaylistsAdapterHolder holder, int position) {
//        Playlist playlist = playlists.get(position);
//        holder.tvItemName.setText(playlist.getPlaylistName());
//        holder.ivItem.setImageURI(Uri.parse(playlist.getImage()));
        Album album = albums.get(position);
        holder.tvItemName.setText(album.getName());
        Glide.with(context).load(album.getImage()).into(holder.ivItem);

    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class RandomPlaylistsAdapterHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvItemName, tvItemArtist;
        public RandomPlaylistsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
//            tvItemArtist = itemView.findViewById(R.id.tvItemArtist);
        }
    }
}
