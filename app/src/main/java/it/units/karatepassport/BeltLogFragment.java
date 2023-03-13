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

    TextView whiteBeltDate, yellowBeltDate, orangeBeltDate, greenBeltDate, blueBeltDate,
            brownBeltDate, firstDanDate, secondDanDate, thirdDanDate, fourthDanDate, fifthDanDate,
            sixthDanDate, seventhDanDate, eighthDanDate, ninthDanDate, tenthDanDate;
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
        yellowBeltDate = getView().findViewById(R.id.yellow_belt_date);
        orangeBeltDate = getView().findViewById(R.id.orange_belt_date);
        greenBeltDate = getView().findViewById(R.id.green_belt_date);
        blueBeltDate = getView().findViewById(R.id.blue_belt_date);
        brownBeltDate = getView().findViewById(R.id.brown_belt_date);
        firstDanDate = getView().findViewById(R.id.first_dan_date);
        secondDanDate = getView().findViewById(R.id.second_dan_date);
        thirdDanDate = getView().findViewById(R.id.third_dan_date);
        fourthDanDate = getView().findViewById(R.id.fourth_dan_date);
        fifthDanDate = getView().findViewById(R.id.fifth_dan_date);
        sixthDanDate = getView().findViewById(R.id.sixth_dan_date);
        seventhDanDate = getView().findViewById(R.id.seventh_dan_date);
        eighthDanDate = getView().findViewById(R.id.eighth_dan_date);
        ninthDanDate = getView().findViewById(R.id.ninth_dan_date);
        tenthDanDate = getView().findViewById(R.id.tenth_dan_date);

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
                yellowBeltDate.setText(getNonNullDate(documentSnapshot, "yellow"));
                orangeBeltDate.setText(getNonNullDate(documentSnapshot, "orange"));
                greenBeltDate.setText(getNonNullDate(documentSnapshot, "green"));
                blueBeltDate.setText(getNonNullDate(documentSnapshot, "blue"));
                brownBeltDate.setText(getNonNullDate(documentSnapshot, "brown"));
                firstDanDate.setText(getNonNullDate(documentSnapshot, "first"));
                secondDanDate.setText(getNonNullDate(documentSnapshot, "second"));
                thirdDanDate.setText(getNonNullDate(documentSnapshot, "third"));
                fourthDanDate.setText(getNonNullDate(documentSnapshot, "fourth"));
                fifthDanDate.setText(getNonNullDate(documentSnapshot, "fifth"));
                sixthDanDate.setText(getNonNullDate(documentSnapshot, "sixth"));
                seventhDanDate.setText(getNonNullDate(documentSnapshot, "seventh"));
                eighthDanDate.setText(getNonNullDate(documentSnapshot, "eighth"));
                ninthDanDate.setText(getNonNullDate(documentSnapshot, "ninth"));
                tenthDanDate.setText(getNonNullDate(documentSnapshot, "tenth"));
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