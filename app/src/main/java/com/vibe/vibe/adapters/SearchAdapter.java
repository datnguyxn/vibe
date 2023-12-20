package com.vibe.vibe.adapters;

import android.content.Context;
import android.util.Log;
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
import com.vibe.vibe.entities.Artist;
import com.vibe.vibe.entities.Playlist;
import com.vibe.vibe.entities.Song;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = SearchAdapter.class.getSimpleName();
    private static final int TYPE_ARTIST = 0;
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PLAYLIST = 2;
    private static final int TYPE_DEFAULT = -1;
    private Context context;
    private ArrayList<Object> results = new ArrayList<>();

    public SearchAdapter(Context context) {
        this.context = context;
    }
    public SearchAdapter(Context context, ArrayList<Object> results) {
        this.context = context;
        this.results = results;
    }

    public void setResults(ArrayList<Object> results) {
        this.results = results;
        notifyDataSetChanged();
    }


    public void addResults(ArrayList<Object> results) {
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    public void clearResults() {
        this.results.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        Object item = results.get(position);
        if (item instanceof Artist) {
            return TYPE_ARTIST;
        } else if (item instanceof Song) {
            return TYPE_SONG;
        } else if (item instanceof Album) {
            return TYPE_PLAYLIST;
        }

        return TYPE_DEFAULT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_ARTIST:
                View v1 = inflater.inflate(R.layout.item_search_artist, parent, false);
                viewHolder = new SearchArtistViewHolder(v1);
                break;
            case TYPE_SONG:
            case TYPE_PLAYLIST:
            case TYPE_DEFAULT:
                View v3 = inflater.inflate(R.layout.item_default_search, parent, false);
                viewHolder = new SearchDefaultViewHolder(v3);
                break;

        }
        return viewHolder;
    }

    public void orderByType() {
        ArrayList<Object> artists = new ArrayList<>();
        ArrayList<Object> songs = new ArrayList<>();
        ArrayList<Object> playlists = new ArrayList<>();
        for (Object item : results) {
            if (item instanceof Artist) {
                artists.add(item);
            } else if (item instanceof Song) {
                songs.add(item);
            } else if (item instanceof Album) {
                playlists.add(item);
            }
        }
        results.clear();
        results.addAll(artists);
        results.addAll(songs);
        results.addAll(playlists);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = results.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_ARTIST:
                SearchArtistViewHolder searchArtistViewHolder = (SearchArtistViewHolder) holder;
                Artist artist = (Artist) item;
                searchArtistViewHolder.tvArtistName.setText(artist.getName());
                Glide.with(context).load(artist.getThumbnail()).into(searchArtistViewHolder.imvLibraryArtist);
                searchArtistViewHolder.tvTitle.setText("Artists");
                break;
            case TYPE_SONG:
                SearchDefaultViewHolder searchSongViewHolder = (SearchDefaultViewHolder) holder;
                Song song = (Song) item;
                Log.e(TAG, "onBindViewHolder: " + song.toString());
                searchSongViewHolder.tvPlaylistName.setText(song.getName());
                Glide.with(context).load(song.getImageResource()).into(searchSongViewHolder.imvPlaylist);
                searchSongViewHolder.tvTitle.setText("Songs");
                break;
            case TYPE_PLAYLIST:
                SearchDefaultViewHolder searchDefaultViewHolder = (SearchDefaultViewHolder) holder;
                Album album = (Album) item;
                Log.e(TAG, "onBindViewHolder: " + album.toString());
                searchDefaultViewHolder.tvPlaylistName.setText(album.getName());
                Glide.with(context).load(album.getImage()).into(searchDefaultViewHolder.imvPlaylist);
                searchDefaultViewHolder.tvTitle.setText("Albums");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return results == null ? 0 : results.size();
    }


    public static class SearchDefaultViewHolder extends RecyclerView.ViewHolder {

        private ImageView imvPlaylist;
        private TextView tvPlaylistName, tvTitle;
        public SearchDefaultViewHolder(@NonNull View itemView) {
            super(itemView);
            imvPlaylist = itemView.findViewById(R.id.imvPlaylist);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    public static class SearchArtistViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvLibraryArtist;
        private TextView tvArtistName;
        private TextView tvTitle;

        public SearchArtistViewHolder(View itemView) {
            super(itemView);
            imvLibraryArtist = itemView.findViewById(R.id.imvArtist);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
//            tvTitle.setText("Artists");
            tvTitle.setOnClickListener(v -> {
                Log.e(TAG, "SearchArtistViewHolder: " + tvTitle.getText().toString());
            });
        }
    }
}
