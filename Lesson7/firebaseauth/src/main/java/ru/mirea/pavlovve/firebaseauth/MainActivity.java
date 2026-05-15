package ru.mirea.pavlovve.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.pavlovve.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.createAccountButton.setOnClickListener(v -> {
            createAccount();
        });

        binding.signInButton.setOnClickListener(v -> {
            signIn();
        });

        binding.signOutButton.setOnClickListener(v -> {
            signOut();
        });

        binding.verifyEmailButton.setOnClickListener(v -> {
            sendEmailVerification();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        updateUI(currentUser);
    }

    private void createAccount() {

        String email =
                binding.emailEditText.getText()
                        .toString();

        String password =
                binding.passwordEditText.getText()
                        .toString();

        if (!validateForm(email, password)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(this,
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Account created",
                                        Toast.LENGTH_SHORT
                                ).show();

                                FirebaseUser user =
                                        mAuth.getCurrentUser();

                                updateUI(user);

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Create account failed",
                                        Toast.LENGTH_SHORT
                                ).show();

                                updateUI(null);
                            }
                        });
    }

    private void signIn() {

        String email =
                binding.emailEditText.getText()
                        .toString();

        String password =
                binding.passwordEditText.getText()
                        .toString();

        if (!validateForm(email, password)) {
            return;
        }

        mAuth.signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(this,
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Sign in success",
                                        Toast.LENGTH_SHORT
                                ).show();

                                FirebaseUser user =
                                        mAuth.getCurrentUser();

                                updateUI(user);

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Authentication failed",
                                        Toast.LENGTH_SHORT
                                ).show();

                                updateUI(null);
                            }
                        });
    }

    private void signOut() {

        mAuth.signOut();

        updateUI(null);
    }

    private void sendEmailVerification() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user != null) {

            user.sendEmailVerification()
                    .addOnCompleteListener(
                            task -> {

                                if (task.isSuccessful()) {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Verification email sent",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Failed to send email",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
        }
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            user.reload();

            binding.statusTextView.setText(
                    "Signed in"
            );

            binding.userInfoTextView.setText(
                    "Email: "
                            + user.getEmail()
                            + "\nVerified: "
                            + user.isEmailVerified()
                            + "\nUID: "
                            + user.getUid()
            );

            binding.authButtonsLayout
                    .setVisibility(View.GONE);

            binding.userButtonsLayout
                    .setVisibility(View.VISIBLE);

        } else {

            binding.statusTextView.setText(
                    "Signed out"
            );

            binding.userInfoTextView.setText("");

            binding.authButtonsLayout
                    .setVisibility(View.VISIBLE);

            binding.userButtonsLayout
                    .setVisibility(View.GONE);
        }
    }

    private boolean validateForm(
            String email,
            String password
    ) {

        if (TextUtils.isEmpty(email)) {

            binding.emailEditText
                    .setError("Required");

            return false;
        }

        if (TextUtils.isEmpty(password)) {

            binding.passwordEditText
                    .setError("Required");

            return false;
        }

        if (password.length() < 6) {

            binding.passwordEditText
                    .setError("Minimum 6 symbols");

            return false;
        }

        return true;
    }
}