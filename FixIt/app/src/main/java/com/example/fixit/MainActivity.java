package com.example.fixit;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    ConnectionThread checkConnection = new ConnectionThread();
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private String mUsername;

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkConnection, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(checkConnection);
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();

        if(!extra.isEmpty())
            mUsername = extra.getString("username");

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navView =(NavigationView) findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.profile)
                {
                    openMainActivity();
                }

                return true;
            }
        });


    }

    private void openMainActivity() {
        ProfileActivity pa = new ProfileActivity();
        Intent intent = new Intent(this, ProfileActivity.class);
        String strUsername = mUsername;

        intent.putExtra("username", strUsername);

        startActivity(intent);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void startScanning(View view) {
    }
}