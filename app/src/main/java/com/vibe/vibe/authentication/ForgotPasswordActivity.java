package com.vibe.vibe.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.utils.HashPassword;

import java.security.NoSuchAlgorithmException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private final UserModel userModel = new UserModel();
    private String verificationId = "";
    private String phoneNumber, newPassword, newPasswordCon;
    private PhoneAuthOptions options;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextInputEditText edtPhoneNumber, edtNewPassword, edtNewPasswordCon;
    private MaterialButton btnSubmit;
    private ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnSubmit.setOnClickListener(v -> {
            prepareData();
            handleForgotPassword();
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
                btnSubmit.setEnabled(false);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + s);
                Intent intent = new Intent(ForgotPasswordActivity.this, OTPVerifyActivity.class);
                intent.putExtra("verificationId", s);
                intent.putExtra("phone", phoneNumber);
                try {
                    String hashPassword = HashPassword.hashPassword(newPassword);
                    intent.putExtra("newPassword", hashPassword);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgotPasswordActivity.this, "Password invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("token", forceResendingToken);
                intent.putExtra("actionOption", OTPVerifyActivity.FORGOT_PASSWORD);
                verificationId = s;
                startActivity(intent);
                finish();
            }
        };
    }

    private void handleForgotPassword() {
        userModel.checkUserIsExist(phoneNumber, new UserModel.isExistListener() {
            @Override
            public void onExist(User user) {
                if (user != null) {
                    try {
                        boolean isMatch = HashPassword.verifyPassword(newPassword, user.getPassword());
                        if (isMatch) {
                            Toast.makeText(ForgotPasswordActivity.this, "New password must be different from old password", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            mAuth = FirebaseAuth.getInstance();
                            options = PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)
                                    .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                                    .setActivity(ForgotPasswordActivity.this)
                                    .setCallbacks(mCallbacks)
                                    .build();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(options);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onExist: " + e.getMessage());
                    }
                } else {
                    edtPhoneNumber.setError("Phone number is not exist");
                    edtPhoneNumber.requestFocus();
                }
            }

            @Override
            public void onNotFound() {
                Toast.makeText(ForgotPasswordActivity.this, "Phone number is not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        edtPhoneNumber = findViewById(R.id.edtPhoneNumberForgotPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtNewPasswordCon = findViewById(R.id.edtConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgBack = findViewById(R.id.imgBack);
        callbackAuth();
    }

    private void prepareData() {
        phoneNumber = edtPhoneNumber.getText().toString().toLowerCase();
        newPassword = edtNewPassword.getText().toString().toLowerCase();
        newPasswordCon = edtNewPasswordCon.getText().toString().toLowerCase();

        if (phoneNumber.isEmpty()) {
            edtPhoneNumber.setError("Phone number is required");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            edtNewPassword.setError("Password is required");
            edtNewPassword.requestFocus();
            return;
        }

        if (newPasswordCon.isEmpty()) {
            edtNewPasswordCon.setError("Confirm password is required");
            edtNewPasswordCon.requestFocus();
            return;
        }

        if (!newPassword.equals(newPasswordCon)) {
            edtNewPasswordCon.setError("Confirm password is not match");
            edtNewPasswordCon.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            edtNewPassword.setError("Password must be at least 6 characters");
            edtNewPassword.requestFocus();
            return;
        }
        if (!phoneNumber.startsWith(Application.COUNTRY_CODE)) {
            phoneNumber = Application.COUNTRY_CODE + phoneNumber.substring(1);
        }

        Log.d(TAG, "prepareData: " + phoneNumber + " " + newPassword);
    }
}