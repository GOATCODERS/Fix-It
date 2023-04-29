package com.example.fixit;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    FragmentManager manager = getSupportFragmentManager();
    ConnectionThread checkConnection = new ConnectionThread();
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    FirebaseAuth auth;
    FirebaseUser user;

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

        auth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = auth.getCurrentUser();

        if(user == null) {
            openSignInActivity();
        }

        getPermission();

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);


        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navView =(NavigationView) findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int id = item.getItemId();
                drawer.closeDrawer(GravityCompat.START);

                if(id == R.id.profile)
                {
                    openProfileFragment();
                }else if(id == R.id.scan)
                {
                    openMainFragment();
                }else if(id == R.id.notifications)
                {
                    openNotificationsFragment();
                }else if(id == R.id.sign_out)
                {
                    auth.signOut();
                    FirebaseAuth.getInstance().signOut();
                    openSignInActivity();
                }
                return true;
            }
        });
    }

    private void openSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void openProfileFragment()
    {
        manager.beginTransaction()
                .replace(R.id.fragmentContainerView, ProfileFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();

    }

    private void openNotificationsFragment()
    {
        manager.beginTransaction()
                .replace(R.id.fragmentContainerView, NotificationsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();

    }

    private void openMainFragment()
    {
        manager.beginTransaction()
                .replace(R.id.fragmentContainerView, MainFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

    void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode==11)
        {
            if(grantResults.length>0)
            {
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    this.getPermission();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void startScanning(View view) {
    }
}