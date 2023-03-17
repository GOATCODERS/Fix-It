package com.example.fixit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private EditText name, email, username, password, confirmPassword;
    private TextView upper, lower, digit, charCounter, validatePassword;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = (EditText) findViewById(R.id.signup_name);
        email = (EditText) findViewById(R.id.signup_email);
        username = (EditText) findViewById(R.id.signup_username);
        password = (EditText) findViewById(R.id.signup_password);
        confirmPassword = (EditText) findViewById(R.id.signup_confirm_password);

        upper = (TextView) findViewById(R.id.signup_upper);
        lower = (TextView) findViewById(R.id.signup_lower);
        digit = (TextView) findViewById(R.id.signup_digit);
        charCounter = (TextView) findViewById(R.id.signup_charCounter);
        validatePassword = (TextView) findViewById(R.id.signup_validate);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String strPassword = password.getText().toString();
                validatePassword(strPassword);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strPassword = confirmPassword.getText().toString();
                validatePassword.setVisibility(View.VISIBLE);
                confirmPassword(strPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strPassword = confirmPassword.getText().toString();

                if(strPassword.isEmpty())
                {
                    validatePassword.setVisibility(View.GONE);
                }
            }
        });
    }

    public void createUser(View view){
        if(!validateUser()| !validatePassword()|!validateEmail()|!validateName()|!validateConfirmPassword())
        {


        }else{
            if(!checkUser()) {
                String name = this.name.getText().toString();
                String email = this.email.getText().toString();
                String username = this.username.getText().toString();
                String password = this.password.getText().toString();
                String confirmPassword = this.confirmPassword.getText().toString();

                User user = new User(name, email, username, password);

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                reference.child(username).setValue(user);

                Toast.makeText(this, "New Account created", Toast.LENGTH_LONG).show();
                openSignIn(view);
            }else{
                Toast.makeText(this, "Failed to create an Account", Toast.LENGTH_LONG).show();
                username.requestFocus();
            }
        }

    }

    public void openSignIn(View view){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void validatePassword(String password){
        Pattern upper = Pattern.compile("[A-Z]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");

        if(!upper.matcher(password).find())
        {
            this.upper.setTextColor(Color.RED);
        }else{
            this.upper.setTextColor(Color.GREEN);
        }

        if(!lower.matcher(password).find())
        {
            this.lower.setTextColor(Color.RED);
        }else{
            this.lower.setTextColor(Color.GREEN);
        }

        if(!digit.matcher(password).find())
        {
            this.digit.setTextColor(Color.RED);
        }else{
            this.digit.setTextColor(Color.GREEN);
        }

        if(password.length() < 8)
        {
            this.charCounter.setTextColor(Color.RED);
        }else{
            this.charCounter.setTextColor(Color.GREEN);
        }
    }

    private void confirmPassword(String password){
        String temp = this.password.getText().toString();

        if(!temp.equals(password))
        {
            validatePassword.setTextColor(Color.RED);
        }else{
            validatePassword.setTextColor(Color.GREEN);
        }
    }

    private boolean validateUser(){
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

    private boolean validatePassword(){
        String strPassword = password.getText().toString();

        if(strPassword.isEmpty())
        {
            password.setError("Password cannot be empty");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(){
        String strPassword = confirmPassword.getText().toString();

        if(strPassword.isEmpty())
        {
            confirmPassword.setError("Email cannot be empty");
            return false;
        }else{
            confirmPassword.setError(null);
            return true;
        }
    }

    private boolean validateName(){
        String strUsername = name.getText().toString();

        if(strUsername.isEmpty())
        {
            name.setError("Name cannot be empty");
            return false;
        }else{
            name.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String strPassword = email.getText().toString();

        if(strPassword.isEmpty())
        {
            email.setError("Email cannot be empty");
            return false;
        }else{
            email.setError(null);
            return true;
        }
    }

    private boolean checkUser()
    {
        final boolean[] available = {false};
        String strUsername = username.getText().toString();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        Query checkDatabase = reference.orderByChild("username").equalTo(strUsername);

        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists()){
                    username.setError("Username already registered");
                    username.requestFocus();
                    available[0] = true;
                    System.err.println("Passed here");
                }else{
                    username.setError(null);
                    available[0] = false;
                    System.err.println("Passed here else");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        System.err.println("a new user is available");
        return available[0];
    }

}

