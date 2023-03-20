package it.units.karatepassport;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GrantBeltFragment extends Fragment {

    FirebaseFirestore fStore;
    ProgressBar progressBar;
    Spinner passportsSpinner, ranksSpinner;  //dropdown menus
    ArrayList<String> passportNumbers, ranks;
    ArrayAdapter<String> passportsAdapter, ranksAdapter;
    Button selectPassportButton, grantBeltButton;
    String selectedPassport, selectedRank;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grant_belt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        passportsSpinner = view.findViewById(R.id.user_spinner);
        ranksSpinner = view.findViewById(R.id.rank_spinner);
        selectPassportButton = view.findViewById(R.id.select_user_button);
        grantBeltButton = view.findViewById(R.id.grant_belt_button);
        passportNumbers = new ArrayList<>();
        ranks = new ArrayList<>();
        for (Belt belt : Belt.values()) {
            ranks.add(belt.rank);
        }
        passportsAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, passportNumbers);
        ranksAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, ranks);

        CollectionReference usersCollection = fStore.collection("users");
        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    passportNumbers.add(document.getString("passportNumber"));
                }
                Collections.sort(passportNumbers);
                passportsSpinner.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                passportsSpinner.setAdapter(passportsAdapter);
            }
        });

        ranksSpinner.setAdapter(ranksAdapter);

        selectPassportButton.setOnClickListener(unused -> {
            selectedPassport = passportsSpinner.getSelectedItem().toString();
            ranksSpinner.setVisibility(View.VISIBLE);
            grantBeltButton.setVisibility(View.VISIBLE);
        });

        passportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ranksSpinner.setVisibility(View.GONE);
                grantBeltButton.setVisibility(View.GONE);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        grantBeltButton.setOnClickListener(unused -> {
            selectedRank = ranksSpinner.getSelectedItem().toString();
            usersCollection.whereEqualTo("passportNumber", selectedPassport).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Rank granted successfully!", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference userReference = usersCollection.document(document.getId());
                                Map<String, Object> user = new HashMap<>(); // the most popular method to create new data is by using an hashmap
                                user.put("currentBelt", selectedRank);
                                userReference.update(user);
                                DocumentReference beltsReference = userReference.collection("belts").document(selectedRank);
                                Map<String, Object> belt = new HashMap<>();
                                belt.put("timestamp", FieldValue.serverTimestamp());
                                beltsReference.set(belt);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        ranksSpinner.setVisibility(View.GONE);
                        grantBeltButton.setVisibility(View.GONE);
                    });
        });
    }
}