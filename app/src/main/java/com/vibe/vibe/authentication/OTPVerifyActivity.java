package com.vibe.vibe.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vibe.vibe.MainActivity;
import com.vibe.vibe.R;
import com.vibe.vibe.models.UserModel;

import java.util.concurrent.TimeUnit;

public class OTPVerifyActivity extends AppCompatActivity {
    private static final String TAG = OTPVerifyActivity.class.getSimpleName();
    public static final int REGISTRATION = 1;
    public static final int LOGIN = 2;
    public static final int FORGOT_PASSWORD = 3;
    private TextView tvRecentPhone, tvTimer;
    private ImageView imgBack;
    private TextInputEditText edtOTP;
    private MaterialButton btnVerifyOTP;
    private boolean isRecent = false;
    private final int recentTimer = 60;
    private PhoneAuthOptions options;
    private FirebaseAuth mAuth;
    private String verificationId = "";
    private String otp;
    private String phoneNumber, username, password, newPassword;
    private int actionOption = 0;
    private final UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        init();
        countTimer();


        tvRecentPhone.setOnClickListener(v -> {
            if (isRecent) {
                handleRecentVerification();
                countTimer();
            }
        });

        btnVerifyOTP.setOnClickListener(v -> {
            handleVerifyOTP();
        });
    }

    private void handleRecentVerification() {
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void countTimer() {
        isRecent = false;
        new CountDownTimer(recentTimer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("00:" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                isRecent = true;
                btnVerifyOTP.setEnabled(false);
            }
        }.start();
    }

    private void init() {
        imgBack = findViewById(R.id.imgBack);
        edtOTP = findViewById(R.id.edtOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        mAuth = FirebaseAuth.getInstance();
        tvRecentPhone = findViewById(R.id.tvRecent);
        tvTimer = findViewById(R.id.tvTimer);
        getDataIntent();
        options = PhoneAuthOptions.newBuilder()
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallbacks)
                .setPhoneNumber(phoneNumber)
                .build();
    }

    private void prepareOTP() {
        otp = edtOTP.getText().toString().trim().toLowerCase();
        if (otp.isEmpty()) {
            edtOTP.setError("OTP is required");
            edtOTP.requestFocus();
            Toast.makeText(OTPVerifyActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otp.length() < 6) {
            edtOTP.setError("OTP must be 6 characters");
            edtOTP.requestFocus();
            Toast.makeText(OTPVerifyActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void handleVerifyOTP() {
        prepareOTP();
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            switch (actionOption) {
                case REGISTRATION:
                    handleRegister(credential);
                    break;
                case LOGIN:
                    handleLogin(credential);
                    break;
                case FORGOT_PASSWORD:
                    handleForgotPassword(credential);
                    break;
                default:
                    Toast.makeText(OTPVerifyActivity.this, "Something went wrong when try authentication", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void handleForgotPassword(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "handleForgotPassword: Forgot Password success");
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            userModel.updatePassword(user.getUid(), newPassword);
                            Intent intent = new Intent(OTPVerifyActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_right);
                            finish();
                            Toast.makeText(OTPVerifyActivity.this, "Change password success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OTPVerifyActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(OTPVerifyActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleLogin(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                   if (task.isSuccessful()) {
                       Log.d(TAG, "handleLogin: Login success");
                       FirebaseUser user = task.getResult().getUser();
                       if (user != null) {
                           SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                           SharedPreferences.Editor editor = sharedPreferences.edit();
                           editor.putString("uuid", user.getUid());
                           editor.apply();
                           Intent intent = new Intent(OTPVerifyActivity.this, MainActivity.class);
                           intent.putExtra("username", username);
                           startActivity(intent);
                           overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_right);
                           finish();
                       } else {
                           Toast.makeText(OTPVerifyActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                       }
                   }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OTPVerifyActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleRegister(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "handleRegister: register success");
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            userModel.addUserByPhone(user.getUid(), username, phoneNumber, password, user.getUid());
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uuid", user.getUid());
                            editor.apply();
                            Intent intent = new Intent(OTPVerifyActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_right);
                            finish();
                        }
                    } else {
                        Toast.makeText(OTPVerifyActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(OTPVerifyActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        verificationId = intent.getStringExtra("verificationId");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        actionOption = intent.getIntExtra("actionOption", 0);
        newPassword = intent.getStringExtra("newPassword");
        Log.d(TAG, "getDataIntent: " + phoneNumber + " " + verificationId + " " + username + " " + password + " " + actionOption);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: verification completed" + phoneAuthCredential.getSmsCode());
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPVerifyActivity.this, "Something went wrong when recent OTP", Toast.LENGTH_SHORT).show();
        }
    };
    ;

}