package com.vibe.vibe.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vibe.vibe.R;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.models.UserModel;
import com.vibe.vibe.utils.HashPassword;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    public static final String COUNTRY_CODE = "+84";
    private TextInputEditText edtUserName, edtPhone, edtPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private PhoneAuthOptions options;
    private FirebaseAuth mAuth;
    private String verificationId = "";
    private String username, phone, password;
    private final UserModel userModel = new UserModel();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        Log.d(TAG, "onCreate: ");
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        btnRegister.setOnClickListener(v -> {
            prepareData();
            handleRegister();
        });
    }

    private void init() {
        edtUserName = findViewById(R.id.edtUserName);
        edtPhone = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        mAuth = FirebaseAuth.getInstance();
        callbackAuth();
    }

    private void prepareData() {
        username = edtUserName.getText().toString().trim().toLowerCase();
        phone = edtPhone.getText().toString().trim().toLowerCase();
        password = edtPassword.getText().toString().trim().toLowerCase();
        if (username.isEmpty()) {
            edtUserName.setError("Username is required");
            edtUserName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            edtPhone.setError("Phone is required");
            edtPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return;
        }

        if (!phone.startsWith(COUNTRY_CODE)) {
            phone = COUNTRY_CODE + phone.substring(1);
        }

        Log.d(TAG, "prepareData: " + username + " " + phone + " " + password);
    }

    private void handleRegister() {
        userModel.checkUserIsExist(phone, new UserModel.isExistListener() {
            @Override
            public void onExist(User user) {
                if (user != null) {
                    edtPhone.setError("Phone number is already exist");
                    edtPhone.requestFocus();
                }
            }

            @Override
            public void onNotFound() {
                options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(RegisterActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(options);
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
                btnRegister.setEnabled(false);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + s);
                Intent intent = new Intent(RegisterActivity.this, OTPVerifyActivity.class);
                intent.putExtra("verificationId", s);
                intent.putExtra("username", username);
                intent.putExtra("phone", phone);
                try {
                    String hashPassword = HashPassword.hashPassword(password);
                    intent.putExtra("password", hashPassword);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Password invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("token", forceResendingToken);
                intent.putExtra("actionOption", OTPVerifyActivity.REGISTRATION);
                verificationId = s;
                startActivity(intent);
                finish();
            }
        };
    }
}