package it.units.karatepassport;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    TextView name,email,number,loggedAs;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private DrawerLayout drawerLayout;  //the activity_main
    private NavigationView navigationView;
    View headerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_content, container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        number = getView().findViewById(R.id.mainPassportNumber);
        name = getView().findViewById(R.id.mainUserName);
        email = getView().findViewById(R.id.mainEmail);

        // initialization of Auth e Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        // this is the reference to what we want to retrieve, to the specific user
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if ((e == null) || (documentSnapshot != null && documentSnapshot.exists())) {

                //using the names assigned in Register.java, visible also in the FirestoreDB
                String nameString = documentSnapshot.getString("userName");
                number.setText(documentSnapshot.getString("passportNumber"));
                name.setText(nameString);
                email.setText(documentSnapshot.getString("email"));

                //change the "Logged in as ..." message in navigation header
                headerView = navigationView.getHeaderView(0);
                loggedAs = headerView.findViewById(R.id.loggedAs);
                loggedAs.setText(getString(R.string.logged_as, nameString));

                //only master users can see the "Grant a Belt" option
                Boolean isMaster = documentSnapshot.getBoolean("isMaster");
                if (isMaster == Boolean.FALSE) {
                    navigationView.getMenu().findItem(R.id.nav_grant_belt).setVisible(false);
                }
            }
        });
    }

}
