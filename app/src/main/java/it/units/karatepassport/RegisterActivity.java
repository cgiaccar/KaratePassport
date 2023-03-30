package it.units.karatepassport;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText userNameField, passportNumberField, emailField, passwordField;
    Button registerButton;
    TextView linkToLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    String userId;
    DocumentReference passportReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameField = findViewById(R.id.username);
        passportNumberField = findViewById(R.id.passportNumber);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password_text);
        registerButton = findViewById(R.id.registerBtn);
        linkToLogin = findViewById(R.id.textLoginHere);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // check if user is already logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        registerButton.setOnClickListener(view -> {
            String userName = userNameField.getText().toString();
            String passportNumber = passportNumberField.getText().toString();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            passportReference = fStore.collection("passports").document(passportNumber);

            // check if user inputs are valid
            if (TextUtils.isEmpty(passportNumber)) {
                passportNumberField.setError("Passport Number is required.");
                return;
            }

            if (!TextUtils.isDigitsOnly(passportNumber)) {
                passportNumberField.setError("Invalid Passport Number.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailField.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordField.setError("Password is required.");
                return;
            }

            if (password.length() < 6) {
                passwordField.setError("Password must be at least six characters long");
                return;
            }

            // check for passport number uniqueness before continuing
            passportReference.get().addOnCompleteListener(checkingTask -> {
                if (checkingTask.isSuccessful()) {
                    DocumentSnapshot document = checkingTask.getResult();
                    if (document.exists()) {
                        passportNumberField.setError("Passport Number already exists.");
                    }
                    else {    // the passport is new, continue with registration
                        progressBar.setVisibility(View.VISIBLE);

                        // register the new user in Firebase
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(creationTask -> {
                            if (creationTask.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();

                                // save new user's data in database
                                userId = fAuth.getCurrentUser().getUid(); // get new user's ID from Firebase Auth
                                DocumentReference userReference = fStore.collection("users").document(userId); // use ID to create new user in database
                                Map<String, Object> user = new HashMap<>();
                                user.put("userName", userName);
                                user.put("passportNumber", passportNumber);
                                user.put("email", email);
                                user.put("currentBelt", Belt.WHITE.rank);
                                userReference.set(user);

                                // automatically grants the white belt to the new user
                                DocumentReference beltsReference = userReference.collection("belts").document(Belt.WHITE.rank);
                                Map<String, Object> belt = new HashMap<>();
                                belt.put("timestamp", FieldValue.serverTimestamp());
                                beltsReference.set(belt);

                                // the passport number is stored in the collection for uniqueness checking
                                Map<String, Object> passportOwner = new HashMap<>();
                                passportOwner.put("owner", userId);
                                passportReference.set(passportOwner);

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error! " + creationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error! " + checkingTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        linkToLogin.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

}