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
import com.vibe.vibe.entities.Playlist;

import java.util.ArrayList;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryAdapterHolder>{
    private static final String TAG = LibraryAdapter.class.getSimpleName();
    private ArrayList<Playlist> playlists;
    private Context context;

    public LibraryAdapter(Context context) {
        this.context = context;
        this.playlists = new ArrayList<>();
    }
    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }
    public void clearData() {
        this.playlists.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public LibraryAdapter.LibraryAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_library_playlist, parent, false);
        return new LibraryAdapter.LibraryAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.LibraryAdapterHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.ivPlaylist.setImageURI(Uri.parse(playlist.getImage()));
        holder.tvPlaylistItemName.setText(playlist.getPlaylistName());
        holder.tvNumberSong.setText(playlist.getSongs().size() + " songs");
    }


    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class LibraryAdapterHolder extends RecyclerView.ViewHolder{
        private ImageView ivPlaylist;
        private TextView tvPlaylistItemName, tvNumberSong;
        public LibraryAdapterHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaylist = itemView.findViewById(R.id.ivPlaylist);
            tvPlaylistItemName = itemView.findViewById(R.id.tvPlaylistItemName);
            tvNumberSong = itemView.findViewById(R.id.tvNumberSong);
        }
    }
}
