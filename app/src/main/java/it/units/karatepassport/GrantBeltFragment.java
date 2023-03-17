package it.units.karatepassport;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GrantBeltFragment extends Fragment {

    private static final String TAG = "TAG" ;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    Spinner userSpinner, rankSpinner;  //dropdown menus
    ArrayList<String> userNames, ranks;
    ArrayAdapter<String> userAdapter, rankAdapter;
    Button grantBeltButton, selectUserButton;
    String selectedUser, selectedRank;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grant_belt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        userSpinner = view.findViewById(R.id.user_spinner);
        rankSpinner = view.findViewById(R.id.rank_spinner);
        selectUserButton = view.findViewById(R.id.select_user_button);
        grantBeltButton = view.findViewById(R.id.grant_belt_button);
        userNames = new ArrayList<>();
        ranks = new ArrayList<>();
        for (Belt belt : Belt.values()) {
            ranks.add(belt.rank);
        }
        userAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, userNames);
        rankAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, ranks);

        CollectionReference usersCollection = fStore.collection("users");
        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userNames.add(document.getString("userName"));
                }
                userSpinner.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                userSpinner.setAdapter(userAdapter);
            }
        });

        rankSpinner.setAdapter(rankAdapter);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rankSpinner.setVisibility(View.GONE);
                grantBeltButton.setVisibility(View.GONE);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectUserButton.setOnClickListener(unused -> {
            selectedUser = userSpinner.getSelectedItem().toString();
            rankSpinner.setVisibility(View.VISIBLE);
            grantBeltButton.setVisibility(View.VISIBLE);
        });

        grantBeltButton.setOnClickListener(unused -> {
            selectedRank = rankSpinner.getSelectedItem().toString();
            usersCollection.whereEqualTo("userName", selectedUser).get()    //TODO should be user passport number
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Rank granted successfully!", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference userReference = usersCollection.document(document.getId());
                                DocumentReference beltsReference = userReference.collection("belts").document("belts");
                                Map<String, Object> belt = new HashMap<>();
                                belt.put(selectedRank, FieldValue.serverTimestamp());
                                beltsReference.update(belt);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        });
    }
}