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
import com.vibe.vibe.entities.Song;

import java.util.ArrayList;

public class RecentlySongsAdapter  extends RecyclerView.Adapter<RecentlySongsAdapter.RecentlySongsViewHolder>{

    private static final String TAG = RecentlySongsAdapter.class.getSimpleName();
    private ArrayList<Song> songs;
    private Context context;

    public RecentlySongsAdapter(Context context) {
        this.context = context;
        this.songs = new ArrayList<>();
    }
    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }
    public void clearData() {
        this.songs.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecentlySongsAdapter.RecentlySongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover, parent, false);
        return new RecentlySongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlySongsAdapter.RecentlySongsViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvItemName.setText(song.getName());
        holder.tvItemArtist.setText(song.getArtistName());
        holder.ivItem.setImageURI(Uri.parse(song.getImageResource()));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class RecentlySongsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvItemName, tvItemArtist;
        public RecentlySongsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemArtist = itemView.findViewById(R.id.tvItemArtist);
        }
    }
}
