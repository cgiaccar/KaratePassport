package it.units.karatepassport;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;

public class BeltLogFragment extends Fragment {

    TextView whiteBeltDate, fifthDanDate;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_belt_log, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        whiteBeltDate = getView().findViewById(R.id.white_belt_date);
        fifthDanDate = getView().findViewById(R.id.fifth_dan_date);

        // initialization of Auth e Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        // this is the reference to what we want to retrieve, to the specific user
        DocumentReference documentReference = fStore.collection("users")
                .document(userID).collection("belts").document("belts");

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if ((e == null) || (documentSnapshot != null && documentSnapshot.exists())) {
                whiteBeltDate.setText(getNonNullDate(documentSnapshot, "white"));
                fifthDanDate.setText(getNonNullDate(documentSnapshot, "fifth"));
            }
        });
    }

    private String getNonNullDate(DocumentSnapshot documentSnapshot, String rank){
        try {
            Date timestamp = documentSnapshot.getTimestamp(rank).toDate();
            return DateFormat.format("MMMM dd, yyyy", timestamp).toString();
        } catch(NullPointerException e){
            return "";
        }
    }
}