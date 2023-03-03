package it.units.karatepassport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    TextView name,email,number,loggedAs;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private DrawerLayout drawerLayout;  //the activity_main
    private NavigationView navigationView;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        number = findViewById(R.id.mainPassportNumber);
        name = findViewById(R.id.mainUserName);
        email = findViewById(R.id.mainEmail);

        // initialization of Auth e Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // navigationView handling
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            return true;
                        case R.id.nav_belt_log:
                            startActivity(new Intent(getApplicationContext(), BeltLog.class));
                            return true;
                        case R.id.nav_grant_belt:
                            startActivity(new Intent(getApplicationContext(), GrantBelt.class));
                            return true;
                    }
            return true;
        });
        navigationView.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
            logout(findViewById(android.R.id.content).getRootView());
            return false;
        });

        // app bar handling
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigationicon);


        userID = fAuth.getCurrentUser().getUid();

        // this is the reference to what we want to retrieve, to the specific user
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener(this, (documentSnapshot, e) -> {
            if ((e == null ) || (documentSnapshot !=null && documentSnapshot.exists())) {

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);   // opens the navigation menu
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}