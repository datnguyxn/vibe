package com.vibe.vibe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vibe.vibe.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    private static final String TAG = SearchAdapter.class.getSimpleName();
    private Context context;

    public SearchAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_default_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSearch;
        private TextView tvResult, tvGenreResult;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSearch = itemView.findViewById(R.id.ivSearch);
            tvResult = itemView.findViewById(R.id.tvResult);
            tvGenreResult = itemView.findViewById(R.id.tvGenreResult);
        }
    }
}
