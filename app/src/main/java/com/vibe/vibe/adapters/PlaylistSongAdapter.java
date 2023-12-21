package com.vibe.vibe.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.constants.Schema;
import com.vibe.vibe.entities.Song;
import com.vibe.vibe.fragments.MoreOptionBottomSheetFragment;
import com.vibe.vibe.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.PlaylistViewHolder> {
    private static final String TAG = PlaylistSongAdapter.class.getSimpleName();
    private ArrayList<Song> songs;
    private Context context;
    private String id;
    private boolean isLiked = false;

    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private final UserModel userModel = new UserModel();

    public PlaylistSongAdapter(Context context) {
        this.context = context;
        this.songs = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Application.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        id = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
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
    public PlaylistSongAdapter.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_song, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongAdapter.PlaylistViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvSongTitle.setText(song.getName());
        holder.tvSongArtist.setText(song.getArtistName());
        Glide.with(context).load(song.getImageResource()).into(holder.imageSong);
        userModel.getConfiguration(id, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(ArrayList<Map<String, Object>> config) {
                if (config == null) {
                    isLiked = false;
                    holder.ivLikeStatus.setImageResource(R.drawable.like);
                    return;
                } else {
                    for (Map<String, Object> map : config) {
                        if (Objects.equals(map.get(Schema.SONG_ID), song.getId())) {
                            isLiked = true;
                            holder.ivLikeStatus.setImageResource(R.drawable.unlike);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
        holder.ivMoreOption.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: " + position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Application.CURRENT_SONG, song);
            bundle.putBoolean("Like", isLiked);
            MoreOptionBottomSheetFragment bottomSheetFragment = new MoreOptionBottomSheetFragment();
            bottomSheetFragment.setArguments(bundle);
            bottomSheetFragment.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView imageSong, ivLikeStatus, ivMoreOption;
        private TextView tvSongTitle, tvSongArtist;
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSong = itemView.findViewById(R.id.imageSong);
            ivLikeStatus = itemView.findViewById(R.id.ivLikeStatus);
            ivMoreOption = itemView.findViewById(R.id.ivMoreOption);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
