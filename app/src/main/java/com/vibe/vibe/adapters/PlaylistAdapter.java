package com.vibe.vibe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.vibe.vibe.R;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{
    private static final String TAG = "PlaylistAdapter";
    private Context context;
    private ArrayList<Playlist> playlists;
    private String username;
    public PlaylistAdapter(Context context) {
        this.context = context;
        this.playlists = new ArrayList<>();
        username = "";
    }

    public void setPlaylistsForAdapter(ArrayList<Playlist> playlists, String username) {
        this.playlists = playlists;
        this.username = username;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.playlists.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Playlist playlist, int position);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.tvPlaylistName.setText(playlist.getPlaylistName());
        if (playlist.getImage().equals("")) {
            Glide.with(context).load(R.drawable.default_playlist).into(holder.imvPlaylist);
        } else {
            Glide.with(context).load(playlist.getImage()).into(holder.imvPlaylist);
        }
        if (playlist.getPlaylistName().equals("Liked Songs")) {
            Glide.with(context).load(R.drawable.like_song).into(holder.imvPlaylist);
        }
        holder.tvTitle.setText("Playlist ・" + username);

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ShapeableImageView imvPlaylist;
        private TextView tvPlaylistName, tvTitle;
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imvPlaylist = itemView.findViewById(R.id.imvPlaylist);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                Playlist playlist = playlists.get(position);
//                check if position is valid
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(playlist, position);
                }
            }
        }
    }
}
