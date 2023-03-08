package it.units.karatepassport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
        setContentView(R.layout.main_content);

        if (savedInstanceState == null) {
            Fragment fragment = new Fragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_content, fragment)
                    .commit();
        }

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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation);
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