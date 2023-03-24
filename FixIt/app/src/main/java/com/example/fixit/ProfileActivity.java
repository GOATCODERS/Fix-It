package com.example.fixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity
{
    private TextView name, email, username, password, address, contact;
    String mUsername;

    FirebaseDatabase database;
    DatabaseReference reference;

    ConnectionThread checkConnection = new ConnectionThread();

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
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.profile_name);
        email = (TextView) findViewById(R.id.profile_email);
        username = (TextView) findViewById(R.id.profile_username);
        password = (TextView) findViewById(R.id.profile_password_redirect);
        address = (TextView) findViewById(R.id.profile_address_redirect);
        contact = (TextView) findViewById(R.id.profile_contact);

        Bundle extra = getIntent().getExtras();

        if(!extra.isEmpty())
            mUsername = extra.getString("username");

        init();

    }

    public void init()
    {
        System.out.println("current user username = "+ mUsername);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        Query checkDatabase = reference.orderByChild("username").equalTo(mUsername);

        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String strName, strEmail, strPassword,strContact, strAddress;
                strName = snapshot.child(mUsername).child("name").getValue(String.class);
                strEmail = snapshot.child(mUsername).child("email").getValue(String.class);
                strContact = snapshot.child(mUsername).child("contact").getValue(String.class);
                strAddress = snapshot.child(mUsername).child("address").getValue(String.class);

                name.setText(strName);
                email.setText(strEmail);
                username.setText("@"+mUsername);

                if(strContact != null)
                {
                    contact.setText(strContact);
                }
                if(strAddress != null)
                {
                    address.setText(strAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void editProfile(View view) {
    }
}