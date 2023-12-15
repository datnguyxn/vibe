package com.vibe.vibe.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vibe.vibe.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreOptionBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreOptionBottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = MoreOptionBottomSheetFragment.class.getSimpleName();
    private ImageView ivMorePicture;
    private TextView tvMoreSong, tvMoreArtist, tvLikeArtist,tvAddToPlaylist, tvDownload, tvShare, tvClose;

    public MoreOptionBottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreOptionBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreOptionBottomSheetFragment newInstance(String param1, String param2) {
        MoreOptionBottomSheetFragment fragment = new MoreOptionBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_option_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
            ivMorePicture = view.findViewById(R.id.ivMorePicture);
            tvMoreSong = view.findViewById(R.id.tvMoreSong);
            tvMoreArtist = view.findViewById(R.id.tvMoreArtist);
            tvLikeArtist = view.findViewById(R.id.tvLikeArtist);
            tvAddToPlaylist = view.findViewById(R.id.tvAddToPlaylist);
            tvDownload = view.findViewById(R.id.tvDownload);
            tvShare = view.findViewById(R.id.tvShare);
            tvClose = view.findViewById(R.id.tvClose);
            tvClose.setOnClickListener(v -> dismiss());
    }
}