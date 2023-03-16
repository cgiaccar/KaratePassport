package it.units.karatepassport;

import static it.units.karatepassport.RegisterActivity.TAG;

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

    FirebaseFirestore fStore;
    ProgressBar progressBar;
    Spinner spinner;  //dropdown menu
    ArrayList<String> userNames;
    ArrayAdapter<String> adapter;
    Button grantBeltButton;
    String selectedUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grant_belt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        spinner = view.findViewById(R.id.spinner);
        grantBeltButton = view.findViewById(R.id.grant_belt_button);
        userNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, userNames);

        CollectionReference usersCollection = fStore.collection("users");
        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userNames.add(document.getString("userName"));
                }
                spinner.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                spinner.setAdapter(adapter);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = (String) parent.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String rankField = "blue";  //TODO should let the master chose the rank (another spinner?)

        grantBeltButton.setOnClickListener(unused ->
                usersCollection.whereEqualTo("userName", selectedUser).get()    //TODO should be user passport number
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Rank granted successfully!", Toast.LENGTH_SHORT).show();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference userReference = usersCollection.document(document.getId());
                                    DocumentReference beltsReference = userReference.collection("belts").document("belts");
                                    Map<String,Object> belt = new HashMap<>();
                                    belt.put(rankField, FieldValue.serverTimestamp());
                                    beltsReference.update(belt);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        })
        );
    }
}