package com.vibe.vibe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.vibe.vibe.R;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Artist;

import java.util.ArrayList;

public class ArtistFavoriteAdapter extends RecyclerView.Adapter<ArtistFavoriteAdapter.ArtistFavoriteViewHolder> {
    private static final String TAG = "ArtistFavoriteAdapter";
    private Context context;
    private ArrayList<Artist> artists;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Artist artist, int position);
    }

    public ArtistFavoriteAdapter(Context context) {
        this.context = context;
        this.artists = new ArrayList<>();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.artists.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistFavoriteAdapter.ArtistFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false);
        return new ArtistFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistFavoriteAdapter.ArtistFavoriteViewHolder holder, int position) {
        Artist artist = artists.get(position);
        if (artist == null) {
            return;
        }
        holder.tvArtistName.setText(artist.getName());
        holder.tvTitle.setText("Artist");
        Glide.with(context).load(artist.getThumbnail()).into(holder.imvArtist);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ArtistFavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ShapeableImageView imvArtist;
        private TextView tvArtistName, tvTitle;
        public ArtistFavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imvArtist = itemView.findViewById(R.id.imvArtist);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                Artist artist = artists.get(position);
//                check if position is valid
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(artist, position);
                }
            }
        }
    }
}
