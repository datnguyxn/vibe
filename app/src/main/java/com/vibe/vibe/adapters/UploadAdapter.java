package com.vibe.vibe.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.fragments.MoreOptionBottomSheetFragment;

import java.util.ArrayList;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {
    private static final String TAG = "UploadAdapter";
    private Context context;
    private ArrayList<Song> songs;
    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public UploadAdapter(Context context) {
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
    public UploadAdapter.UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upload, parent, false);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadAdapter.UploadViewHolder holder, int position) {
        Song song = songs.get(position);
        if (song == null) {
            return;
        }
        holder.tvSongTitleUpload.setText(song.getName());
        holder.tvSongArtistUpload.setText(song.getArtistName());
        Glide.with(context).load(song.getImageResource()).into(holder.imageSongUpload);
        holder.ivMoreOptionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onBindViewHolder: " + position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Application.CURRENT_SONG, song);
                MoreOptionBottomSheetFragment bottomSheetFragment = new MoreOptionBottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class UploadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageSongUpload, ivMoreOptionUpload;
        private TextView tvSongTitleUpload, tvSongArtistUpload;
        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSongUpload = itemView.findViewById(R.id.imageSongUpload);
            ivMoreOptionUpload = itemView.findViewById(R.id.ivMoreOptionUpload);
            tvSongTitleUpload = itemView.findViewById(R.id.tvSongTitleUpload);
            tvSongArtistUpload = itemView.findViewById(R.id.tvSongArtistUpload);
            itemView.setOnClickListener(this);
            ivMoreOptionUpload.setOnClickListener(this);
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
