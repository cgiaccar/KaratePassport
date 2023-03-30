package it.units.karatepassport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment {

    TextView userName, email, passportNumber, currentBelt;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // initialization of Auth and Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        passportNumber = view.findViewById(R.id.passport_number);
        userName = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.home_email);
        currentBelt = view.findViewById(R.id.current_belt);
        progressBar = view.findViewById(R.id.progressBar3);

        DocumentReference documentReference = fStore.collection("users").document(userId);

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if ((e == null) || (documentSnapshot != null && documentSnapshot.exists())) {
                String snapshotNumber = documentSnapshot.getString("passportNumber");
                passportNumber.setText(getString(R.string.passport_number, snapshotNumber));
                String snapshotName = documentSnapshot.getString("userName");
                Boolean isMaster = documentSnapshot.getBoolean("isMaster");
                if (isMaster == Boolean.TRUE) {
                    userName.setText(getString(R.string.master_name, snapshotName));
                } else userName.setText(snapshotName);
                email.setText(documentSnapshot.getString("email"));
                currentBelt.setText(documentSnapshot.getString("currentBelt"));
            }
            progressBar.setVisibility(View.GONE);
            passportNumber.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            currentBelt.setVisibility(View.VISIBLE);
        });
    }

}
