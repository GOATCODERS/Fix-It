package com.example.fixit;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private EditText email, password;
    private TextView errorMessage;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseAuth auth;
    private FirebaseUser user;

    String strUsername;

    ConnectionThread checkConnection = new ConnectionThread();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkConnection, filter);
        super.onStart();

        if (user != null) {
            openMainActivity();
        }
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

        email = (EditText) findViewById(R.id.sign_in_email);
        password = (EditText) findViewById(R.id.sign_in_password);
        errorMessage = (TextView)findViewById(R.id.sign_in_failed);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        errorMessage.setVisibility(View.GONE);

    }

    public void signIn(View view)
    {
        if (!validateUser() | !validatePassword()) {
        } else {
            checkUser();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", strUsername);
        startActivity(intent);
        finish();
    }

    public void openSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateUser() {
        String strEmail = email.getText().toString();

        if (strEmail.isEmpty()) {
            email.setError("Username cannot be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String strPassword = password.getText().toString();

        if (strPassword.isEmpty()) {
            password.setError("Username cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void checkUser() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();

        auth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                if (task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                    openMainActivity();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch(FirebaseAuthEmailException e){
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(e.getMessage());
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(e.getMessage());
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Exception e) {

                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(e.getMessage());
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

//        database = FirebaseDatabase.getInstance();
//        reference = database.getReference("Users");
//        Query checkDatabase = reference.orderByChild("email").equalTo(strEmail);
//
//
//        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    username.setError(null);
//                    String dbPassword = snapshot.child(strUsername).child("password").getValue(String.class);
//
//                    if (dbPassword.equals(strPassword)) {
//                        password.setError(null);
//                        openMainActivity();
//                    } else {
//                        password.setError("Invalid password");
//                        password.requestFocus();
//                    }
//
//                } else {
//                    username.setError("Username does not exist");
//                    username.requestFocus();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                System.out.println("profile fragment is complete " + error.getMessage());
//            }
//        });

    }

}