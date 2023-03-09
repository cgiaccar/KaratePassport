package it.units.karatepassport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.karatepassport.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    TextView name,email,number,loggedAs;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private DrawerLayout drawerLayout;  //the activity_main
    private NavigationView navigationView;
    private NavController navController;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // app bar handling
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // navigation handling
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragments_host);
        navController = navHostFragment.getNavController();
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_belt_log, R.id.nav_grant_belt)
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        // changing navigation header depending on user
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if ((e == null) || (documentSnapshot != null && documentSnapshot.exists())) {
                //change the "Logged in as ..." message in navigation header
                headerView = navigationView.getHeaderView(0);
                loggedAs = headerView.findViewById(R.id.loggedAs);
                loggedAs.setText(getString(R.string.logged_as, documentSnapshot.getString("userName")));

                //only master users can see the "Grant a Belt" option
                Boolean isMaster = documentSnapshot.getBoolean("isMaster");
                if (isMaster == Boolean.TRUE) {
                    navigationView.getMenu().findItem(R.id.nav_grant_belt).setVisible(true);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {  // to make the menu icon work
        NavController navController = Navigation.findNavController(this, R.id.fragments_host);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(View view) {
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}