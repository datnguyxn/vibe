package com.vibe.vibe.authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vibe.vibe.MainActivity;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.User;
import com.vibe.vibe.fragments.LoginPhoneFragment;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.UserModel;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextInputEditText edtPhoneNumber, edtPassword;
    private MaterialButton btnLogin, btnLoginGG, btnLoginPhone;
    private TextView tvForgotPassword, tvRegister;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private PhoneAuthOptions options;
    private DatabaseReference database;
    private Dialog dialog;
    private final UserModel userModel = new UserModel();
    private String uuid = "";
    private String code;
    private String phone, password;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        getDataIntent();

        Log.d(TAG, "LoginActivity: Create");
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLoginPhone.setOnClickListener(v -> {
            Log.d(TAG, "Login use phone number");
            replaceFragment(new LoginPhoneFragment());
        });

        btnLoginGG.setOnClickListener(v -> {
            Log.d(TAG, "Login use Google");
            handleLoginUseGoogle();
        });

        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login use phone number");
            prepareData();
            handleLoginWithPhoneNumber();
        });

    }


    private void init() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginGG = findViewById(R.id.btnLoginGG);
        btnLoginPhone = findViewById(R.id.btnLoginPhone);
        tvForgotPassword = findViewById(R.id.tvForgetPassword);
        tvRegister = findViewById(R.id.tvRegister);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        dialog = new Dialog(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        uuid = sharedPreferences.getString("uuid", "");
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            code = intent.getStringExtra("code");
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    private void handleLoginUseGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Application.RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Application.RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Login Google");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "onActivityResult: Login Google ->" + account.getId());
                handleAuthGoogleByFirebase(account.getIdToken());
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }

        }
    }

    private void handleAuthGoogleByFirebase(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Login Google success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            userModel.addUserByGoogle(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), user.getUid());
                            Log.d(TAG, "onComplete: Login Google success -> " + user.getDisplayName());
                            SharedPreferences sharedPreferences = getSharedPreferences(Application.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Application.SHARED_PREFERENCES_UUID, user.getUid());
                            editor.apply();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Log.d(TAG, "onComplete: Login Google failed");
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                });
    }

    private void prepareData() {
        phone = edtPhoneNumber.getText().toString().toLowerCase();
        password = edtPassword.getText().toString().toLowerCase();

        if (phone.isEmpty()) {
            edtPhoneNumber.setError("Phone is required");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            edtPhoneNumber.setError("Phone must be at least 10 characters");
            edtPhoneNumber.requestFocus();
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

        if (!phone.startsWith(Application.COUNTRY_CODE)) {
            phone = Application.COUNTRY_CODE + phone.substring(1);
        }

        Log.d(TAG, "prepareData: " + phone + " " + password);
    }

    private void handleLoginWithPhoneNumber() {
        userModel.loginWithPhoneNumber(phone, password, new UserModel.LoginCallBacks() {

            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    Log.d(TAG, "onCompleted: Login success");
                    SharedPreferences sharedPreferences = getSharedPreferences(Application.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Application.SHARED_PREFERENCES_UUID, user.getUuid());
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: Login failed");
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}