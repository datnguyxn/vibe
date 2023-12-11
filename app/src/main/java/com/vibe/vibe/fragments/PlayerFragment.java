package com.vibe.vibe.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vibe.vibe.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = PlayerFragment.class.getSimpleName();
    private ImageView hide, more, ivSongImage, ivLike, ivShuffle, ivPrevious, ivPlay, ivNext, ivRepeat;
    private TextView tvSongName, tvArtistName, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private BottomNavigationView bottomNavigationView;
    private CardView bottomCurrentSong;
    private View fragment;
    private Bundle bundle;
    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomCurrentSong.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragment.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                fragment.setLayoutParams(params);
                replaceFragment(new HomeFragment());
            }
        });
    }

    private void init(View view) {
        hide = view.findViewById(R.id.hide);
        more = view.findViewById(R.id.more);
        ivSongImage = view.findViewById(R.id.ivSongImage);
        ivLike = view.findViewById(R.id.ivLike);
        ivShuffle = view.findViewById(R.id.ivShuffle);
        ivPrevious = view.findViewById(R.id.ivPrevious);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivNext = view.findViewById(R.id.ivNext);
        ivRepeat = view.findViewById(R.id.ivRepeat);
        tvSongName = view.findViewById(R.id.tvSongName);
        tvArtistName = view.findViewById(R.id.tvArtistName);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        seekBar = view.findViewById(R.id.seekBar);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}