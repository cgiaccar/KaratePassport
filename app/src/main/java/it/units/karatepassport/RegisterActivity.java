package it.units.karatepassport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    public static final String TAG = "TAG";
    EditText mEmail, mUsername, mPassportNumber, mPassword;
    Button mRegisterBtn;
    TextView mLoginHereBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    DocumentReference passportReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.email);
        mUsername = findViewById(R.id.username);
        mPassportNumber = findViewById(R.id.passportNumber);
        mPassword = findViewById(R.id.password_text);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginHereBtn = findViewById(R.id.textLoginHere);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // check if user is already logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // on Register button click
        mRegisterBtn.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String userName = mUsername.getText().toString();
            String passportNumber = mPassportNumber.getText().toString();
            passportReference = fStore.collection("passports").document(passportNumber);

            // check if user inputs are valid
            if (TextUtils.isEmpty(passportNumber)) {
                mPassportNumber.setError("Passport Number is required.");
                return;
            }

            if (!TextUtils.isDigitsOnly(passportNumber)) {
                mPassportNumber.setError("Invalid Passport Number.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required.");
                return;
            }

            if (password.length() < 6) {
                mPassword.setError("Password must be at least six characters long");
                return;
            }

            // check for passport number uniqueness before continuing
            passportReference.get().addOnCompleteListener(checkingTask -> {
                if (checkingTask.isSuccessful()) {
                    DocumentSnapshot document = checkingTask.getResult();
                    if (document.exists()) {    // the passport already exists, error
                        mPassportNumber.setError("Passport Number already exists.");
                    }

                    else {    // the passport is new, continue with registration
                        progressBar.setVisibility(View.VISIBLE);

                        // register the new user in Firebase
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(creationTask -> {
                            if (creationTask.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                // as soon as the user is created, we save the user data into the Firebase Firestore Database
                                userID = fAuth.getCurrentUser().getUid(); // get current user (currently registering user) unique ID
                                DocumentReference userReference = fStore.collection("users").document(userID); // creates a new user inside the collection of users using their unique ID
                                Map<String, Object> user = new HashMap<>(); // the most popular method to create new data is by using an hashmap
                                user.put("userName", userName);
                                user.put("passportNumber", passportNumber);
                                user.put("email", email);

                                // automatically grants the white belt to a new user
                                DocumentReference beltsReference = userReference.collection("belts").document("belts");
                                Map<String, Object> belt = new HashMap<>();
                                belt.put(Belt.WHITE.rank, FieldValue.serverTimestamp());
                                beltsReference.set(belt);

                                // the passport number is stored in the collection for uniqueness checking
                                Map<String, Object> passport = new HashMap<>();
                                passport.put("passportNumber", passportNumber);
                                passportReference.set(passport);

                                // for debug
                                userReference.set(user).addOnSuccessListener(unused -> {
                                    Log.d(TAG, "onSuccess: user profile is created for " + userID); // log the success message in our logcat
                                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
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

        mLoginHereBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

}