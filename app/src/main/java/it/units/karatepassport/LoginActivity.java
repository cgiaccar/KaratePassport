package it.units.karatepassport;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    Button loginButton;
    TextView linkToRegister, linkForgotPassword;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password_text);
        linkForgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginBtn);
        linkToRegister = findViewById(R.id.textRegisterHere);
        progressBar = findViewById(R.id.progressBar2);

        fAuth = FirebaseAuth.getInstance();

        // check if user is already logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(view -> {
            String email = this.emailField.getText().toString().trim();
            String password = this.passwordField.getText().toString().trim();

            // check if user inputs are valid
            if (TextUtils.isEmpty(email)) {
                this.emailField.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                this.passwordField.setError("Password is required.");
                return;
            }

            if (password.length() < 6) {
                this.passwordField.setError("Password must be at least six characters long");
            }

            progressBar.setVisibility(View.VISIBLE);

            // authenticate the user
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });

        linkToRegister.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        linkForgotPassword.setOnClickListener(view -> {
            EditText resetMail = new EditText(view.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Reset your Password?");
            passwordResetDialog.setMessage("Enter your email to receive the reset link.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Send", (dialogInterface, i) -> {
                // extract the email and send reset link
                String mail = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused ->
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error! Reset link NOT sent. " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            passwordResetDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // do nothing (automatically closes the dialog)
            });

            passwordResetDialog.show();    // actually creates and shows the dialog
        });
    }
}