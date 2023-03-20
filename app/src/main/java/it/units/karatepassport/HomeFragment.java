package it.units.karatepassport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment {

    TextView name,email,number,currentBelt;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        number = view.findViewById(R.id.mainPassportNumber);
        name = view.findViewById(R.id.mainUserName);
        email = view.findViewById(R.id.mainEmail);
        currentBelt = view.findViewById(R.id.current_belt);

        // initialization of Auth e Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        // this is the reference to what we want to retrieve, to the specific user
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if ((e == null) || (documentSnapshot != null && documentSnapshot.exists())) {

                //using the names assigned in Register.java, visible also in the FirestoreDB
                number.setText(documentSnapshot.getString("passportNumber"));
                name.setText(documentSnapshot.getString("userName"));
                email.setText(documentSnapshot.getString("email"));
                String snapshotBelt = documentSnapshot.getString("currentBelt");
                currentBelt.setText(getString(R.string.current_belt, snapshotBelt));
            }
        });
    }

}
