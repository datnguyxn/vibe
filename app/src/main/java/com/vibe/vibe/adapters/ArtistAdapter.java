package com.vibe.vibe.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vibe.vibe.R;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Artist;
import com.vibe.vibe.entities.Song;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>{
    private static final String TAG = "ArtistAdapter";
    private Context context;
    private ArrayList<Album> albums;
    public ArtistAdapter(Context context) {
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

    public interface OnItemClickListener {
        void onItemClick(Album album, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.tvItemName.setText(album.getName());
        Glide.with(context).load(album.getImage()).into(holder.ivItem);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivItem;
        private TextView tvItemName, tvItemArtist;
        private LinearLayout itemHome;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            itemHome = itemView.findViewById(R.id.itemHome);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                Album album = albums.get(position);
//                check if position is valid
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(album, position);
                }
            }
        }
    }
}
