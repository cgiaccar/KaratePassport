package it.units.karatepassport;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class GrantBeltFragment extends Fragment {

    FirebaseFirestore fStore;
    ProgressBar progressBar;
    Spinner spinner;  //dropdown menu
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grant_belt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        progressBar = getView().findViewById(R.id.progress_bar);

        spinner = getView().findViewById(R.id.spinner);
        ArrayList<String> userNames = new ArrayList<>();
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
    }
}