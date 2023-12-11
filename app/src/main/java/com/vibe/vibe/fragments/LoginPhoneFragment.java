package com.vibe.vibe.fragments;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vibe.vibe.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginPhoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPhoneFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "LOGIN PHONE FRAGMENT";
    private TextInputEditText edtPhomeNumber;
    private MaterialButton btnNext;
    private ImageView imgBack;
    private Activity activity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginPhoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginPhoneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginPhoneFragment newInstance(String param1, String param2) {
        LoginPhoneFragment fragment = new LoginPhoneFragment();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_login_phone, container, false);
        init(convertView);
        imgBack.setOnClickListener(v -> {
            Log.d(TAG, "push");
//            ?//push
        });
        return convertView;
    }

    private void init(View convertView) {
        edtPhomeNumber = convertView.findViewById(R.id.edtPhoneNumber);
        btnNext = convertView.findViewById(R.id.btnNext);
        imgBack = convertView.findViewById(R.id.imgBack);
    }
}