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
import org.w3c.dom.Text;

import java.util.Date;

public class BeltLogFragment extends Fragment {

    enum Rank {
        WHITE(R.id.white_belt_date, "white"),
        YELLOW(R.id.yellow_belt_date, "yellow"),
        ORANGE(R.id.orange_belt_date,"orange"),
        GREEN(R.id.green_belt_date,"green"),
        BLUE(R.id.blue_belt_date, "blue"),
        BROWN(R.id.brown_belt_date,"brown"),
        FIRST(R.id.first_dan_date,"first"),
        SECOND(R.id.second_dan_date,"second"),
        THIRD(R.id.third_dan_date,"third"),
        FOURTH(R.id.fourth_dan_date, "fourth"),
        FIFTH(R.id.fifth_dan_date,"fifth"),
        SIXTH(R.id.sixth_dan_date,"sixth"),
        SEVENTH(R.id.seventh_dan_date,"seventh"),
        EIGHTH(R.id.eighth_dan_date,"eighth"),
        NINTH(R.id.ninth_dan_date,"ninth"),
        TENTH(R.id.tenth_dan_date,"tenth");

        public final int date;
        public final String fieldDB;

        Rank(int date, String fieldDB) {
            this.date = date;
            this.fieldDB = fieldDB;
        }
    }

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

        // initialization of Auth e Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        // this is the reference to what we want to retrieve, to the specific user
        DocumentReference documentReference = fStore.collection("users")
                .document(userID).collection("belts").document("belts");

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if ((e == null) || (documentSnapshot != null && documentSnapshot.exists())) {
                for(Rank rank : Rank.values()) {
                    TextView textView = getView().findViewById(rank.date);
                    textView.setText((getNonNullDate(documentSnapshot, rank.fieldDB)));
                }
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