package com.example.fixit;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity
{
    private EditText username, password;

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
        setContentView(R.layout.activity_sign_in);

        username = (EditText) findViewById(R.id.sign_in_username);
        password = (EditText) findViewById(R.id.sign_in_password);

    }

    public void signIn(View view)
    {

        if(!validateUser() | !validatePassword())
        {

        }else{
            checkUser();
        }


    }

    private void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        String strUsername = username.getText().toString().trim();

        intent.putExtra("username", strUsername);

        startActivity(intent);


    }

    public void openSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private boolean validateUser()
    {
        String strUsername = username.getText().toString();

        if(strUsername.isEmpty())
        {
            username.setError("Username cannot be empty");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        String strPassword = password.getText().toString();

        if(strPassword.isEmpty())
        {
            password.setError("Username cannot be empty");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    private void checkUser()
    {
        String strUsername = username.getText().toString();
        String strPassword = password.getText().toString();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        Query checkDatabase = reference.orderByChild("username").equalTo(strUsername);

        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists()){
                    username.setError(null);
                    String dbPassword = snapshot.child(strUsername).child("password").getValue(String.class);

                    if(dbPassword.equals(strPassword))
                    {
                        password.setError(null);
                        openMainActivity();
                    }else {
                        password.setError("Invalid password");
                        password.requestFocus();
                    }
                }else{
                    username.setError("Username does not exist");
                    username.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}