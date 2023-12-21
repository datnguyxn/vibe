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
    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        Glide.with(context).load(Uri.parse(song.getImageResource())).into(holder.ivItem);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class RecentlySongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivItem;
        private TextView tvItemName, tvItemArtist;
        public RecentlySongsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemArtist = itemView.findViewById(R.id.tvItemArtist);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                Song song = songs.get(position);
//                check if position is valid
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(song, position);
                }
            }
        }
    }
}
