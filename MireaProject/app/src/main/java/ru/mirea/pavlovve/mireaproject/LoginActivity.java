package ru.mirea.pavlovve.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private TextView statusTextView;
    private TextView detailTextView;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private View emailPasswordFields;
    private View emailPasswordButtons;
    private View signedInButtons;
    private Button verifyEmailButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        statusTextView = findViewById(R.id.statusTextView);
        detailTextView = findViewById(R.id.detailTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailPasswordFields = findViewById(R.id.emailPasswordFields);
        emailPasswordButtons = findViewById(R.id.emailPasswordButtons);
        signedInButtons = findViewById(R.id.signedInButtons);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);

        findViewById(R.id.signInButton).setOnClickListener(v -> {
            String email = getEmail();
            String password = getPassword();
            signIn(email, password);
        });

        findViewById(R.id.createAccountButton).setOnClickListener(v -> {
            String email = getEmail();
            String password = getPassword();
            createAccount(email, password);
        });

        findViewById(R.id.signOutButton).setOnClickListener(v -> signOut());

        verifyEmailButton.setOnClickListener(v -> sendEmailVerification());

        findViewById(R.id.continueButton).setOnClickListener(v -> openMainScreen());

        if (isPlaceholderFirebaseConfig()) {
            detailTextView.setText(R.string.firebase_config_placeholder);
        }
    }

    private boolean isPlaceholderFirebaseConfig() {
        int resId = getResources().getIdentifier("google_api_key", "string", getPackageName());
        if (resId == 0) {
            return false;
        }
        String apiKey = getString(resId);
        return apiKey.contains("REPLACE_WITH") || apiKey.contains("placeholder");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (currentUser != null) {
            openMainScreen();
        }
    }

    private void openMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        updateUI(mAuth.getCurrentUser());
                        openMainScreen();
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        showAuthError(task.getException());
                        updateUI(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        updateUI(mAuth.getCurrentUser());
                        openMainScreen();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        showAuthError(task.getException());
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }

        verifyEmailButton.setEnabled(false);
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    verifyEmailButton.setEnabled(!user.isEmailVerified());
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.verification_email_sent, user.getEmail()),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(LoginActivity.this,
                                R.string.verification_email_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
            statusTextView.setText(getString(
                    R.string.emailpassword_status_fmt,
                    user.getEmail(),
                    user.isEmailVerified()));
            detailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            emailPasswordButtons.setVisibility(View.GONE);
            emailPasswordFields.setVisibility(View.GONE);
            signedInButtons.setVisibility(View.VISIBLE);
            verifyEmailButton.setEnabled(!user.isEmailVerified());
        } else {
            statusTextView.setText(R.string.signed_out);
            detailTextView.setText(null);
            emailPasswordButtons.setVisibility(View.VISIBLE);
            emailPasswordFields.setVisibility(View.VISIBLE);
            signedInButtons.setVisibility(View.GONE);
        }
    }

    private void showAuthError(@Nullable Exception exception) {
        String message = exception != null && exception.getMessage() != null
                ? exception.getMessage()
                : getString(R.string.auth_failed);

        if (message.contains("API key not valid") || isPlaceholderFirebaseConfig()) {
            statusTextView.setText(R.string.firebase_invalid_api_key);
            detailTextView.setText(R.string.firebase_config_hint);
            Toast.makeText(this, R.string.firebase_invalid_api_key, Toast.LENGTH_LONG).show();
            return;
        }

        statusTextView.setText(R.string.auth_failed);
        detailTextView.setText(message);
        Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
    }

    private boolean validateForm() {
        String email = getEmail();
        String password = getPassword();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.invalid_email));
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError(getString(R.string.invalid_password));
            return false;
        }
        return true;
    }

    private String getEmail() {
        return emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
    }

    private String getPassword() {
        return passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";
    }
}
