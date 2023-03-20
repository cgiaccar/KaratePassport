package it.units.karatepassport;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;

public class BeltLogFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ProgressBar progressBar;
    TableLayout kyuTable, danTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_belt_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // initialization of Auth and Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        progressBar = view.findViewById(R.id.log_progress_bar);
        kyuTable = view.findViewById(R.id.kyu_table);
        danTable = view.findViewById(R.id.dan_table);

        CollectionReference beltsReference = fStore.collection("users")
                .document(userID).collection("belts");
        beltsReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String rank = document.getId();
                    Belt belt = Belt.getByRank(rank);
                    TextView textView = view.findViewById(belt.textView);
                    Date date = document.getTimestamp("timestamp").toDate();
                    textView.setText(DateFormat.format("MMMM dd, yyyy", date).toString());
                }
                progressBar.setVisibility(View.GONE);
                kyuTable.setVisibility(View.VISIBLE);
                danTable.setVisibility(View.VISIBLE);
            }
        });
    }
}