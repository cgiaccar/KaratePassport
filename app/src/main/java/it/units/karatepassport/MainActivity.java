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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.karatepassport.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    TextView loggedAs;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    private AppBarConfiguration appBarConfiguration;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // app bar and navigation handling
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        DrawerLayout drawerLayout = binding.drawerLayout; //the activity_main.xml
        NavigationView navigationView = binding.navView;

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout(binding.getRoot());
            return true;
        });

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_belt_log, R.id.nav_grant_belt)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        // personalize navigation menu depending on user
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userId);

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
            // Home is selected at the start
            navigationView.getMenu().getItem(0).setChecked(true);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {  // to make the menu icon work
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(View view) {
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}