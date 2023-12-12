package com.vibe.vibe.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vibe.vibe.R;
import com.vibe.vibe.authentication.LoginActivity;
import com.vibe.vibe.authentication.OTPVerifyActivity;
import com.vibe.vibe.authentication.RegisterActivity;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.utils.HashPassword;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

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
    private static final String TAG = LoginPhoneFragment.class.getSimpleName();
    private TextInputEditText edtPhoneNumber;
    private MaterialButton btnNext;
    private ImageView imgBack;
    private Activity activity;
    private String phoneNumber;
    private PhoneAuthOptions options;
    private String verificationId = "";
    private final UserModel userModel = new UserModel();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private Bundle bundle;

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
        return inflater.inflate(R.layout.fragment_login_phone, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        imgBack.setOnClickListener(v -> {
            Log.d(TAG, "Back to login");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Next to verify");
                phoneNumber = edtPhoneNumber.getText().toString().toLowerCase();
                if (phoneNumber.isEmpty()) {
                    edtPhoneNumber.setError("Phone number is required");
                    edtPhoneNumber.setFocusable(true);
                    return;
                }
                if (phoneNumber.length() < 10) {
                    edtPhoneNumber.setError("Phone number is invalid");
                    edtPhoneNumber.setFocusable(true);
                    return;
                }
                if (!phoneNumber.startsWith(Application.COUNTRY_CODE)) {
                    phoneNumber = Application.COUNTRY_CODE + phoneNumber.substring(1);
                }
                Log.d(TAG, "onClick: " + phoneNumber);
                handleLoginWithoutPassword();
            }
        });
    }

    private void init(View convertView) {
        edtPhoneNumber = convertView.findViewById(R.id.edtPhoneNumber);
        btnNext = convertView.findViewById(R.id.btnNext);
        imgBack = convertView.findViewById(R.id.imgBack);
        bundle = new Bundle();
        mAuth = FirebaseAuth.getInstance();
        callbackAuth();
    }

    private void handleLoginWithoutPassword() {
        userModel.checkUserIsExist(phoneNumber, new UserModel.isExistListener() {
            @Override
            public void onExist(User user) {
                if (user != null) {
                    Log.d(TAG, "onExist: " + user.toMap().toString());
                    options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(getActivity())
                            .setCallbacks(mCallbacks)
                            .build();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(options);
                } else {
                    Log.d(TAG, "onExist: " + "User is not exist");
                    Toast.makeText(getActivity(), "User is not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNotFound() {
                Log.d(TAG, "onNotFound: " + "User is not found");
                Toast.makeText(getActivity(), "User is not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callbackAuth() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: verification completed" + phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
                btnNext.setEnabled(false);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + s);
                Intent intent = new Intent(getActivity(), OTPVerifyActivity.class);
                intent.putExtra("verificationId", s);
                intent.putExtra("phone", phoneNumber);
                intent.putExtra("token", forceResendingToken);
                intent.putExtra("actionOption", OTPVerifyActivity.LOGIN);
                verificationId = s;
                startActivity(intent);
                getActivity().finish();
            }
        };
    }
}