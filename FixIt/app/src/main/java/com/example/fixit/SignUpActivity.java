package com.example.fixit;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity
{
    private EditText name, email, password, confirmPassword;
    TextView upper, lower, digit, charCounter, validatePassword;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    private FirebaseUser user;

    ConnectionThread checkConnection = new ConnectionThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.signup_name);
        email = (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        confirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strPassword = confirmPassword.getText().toString();
                validatePassword.setVisibility(View.VISIBLE);
                confirmPassword(strPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkConnection, filter);
        super.onStart();
        if(user != null)
        {
            openMainActivity();
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(checkConnection);
        super.onStop();
    }

    public void createUser(View view) {
        progressBar.setVisibility(ProgressBar.VISIBLE);

            if (!validatePassword() | !validateEmail() | !validateName() | !validateConfirmPassword())
            {

            } else {

                if (!checkUser()) {

                    String email = this.email.getText().toString();
                    String password = this.password.getText().toString();

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            if (task.isSuccessful()) {
                                addToDatabase(view);
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });

                } else {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(this, "Failed to create an Account", Toast.LENGTH_LONG).show();
                    email.requestFocus();
                }
            }
    }

    public void openSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validatePassword() {
        String strPassword = this.password.getText().toString();

        if (strPassword.isEmpty()) {
            this.password.setError("Password cannot be empty");
            return false;
        } else {
            this.password.setError(null);
        }
        Pattern upper = Pattern.compile("[A-Z]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");

        if (!upper.matcher(strPassword).find()) {
            this.upper.setTextColor(Color.RED);
        } else {
            this.upper.setTextColor(Color.GREEN);
        }

        if (!lower.matcher(strPassword).find()) {
            this.lower.setTextColor(Color.RED);
        } else {
            this.lower.setTextColor(Color.GREEN);
        }

        if (!digit.matcher(strPassword).find()) {
            this.digit.setTextColor(Color.RED);
        } else {
            this.digit.setTextColor(Color.GREEN);
        }

        if (password.length() < 8) {
            this.charCounter.setTextColor(Color.RED);
        } else {
            this.charCounter.setTextColor(Color.GREEN);
        }
        boolean isStrong = !(password.length() < 8) && digit.matcher(strPassword).find() && lower.matcher(strPassword).find()
                && upper.matcher(strPassword).find();
        if (!isStrong) {
            System.out.println("passed 'if' in validatePassword in SignUpActivity");
            this.password.setError("Password not strong");
            this.password.requestFocus();

        } else {
            System.out.println("passed 'else' in validatePassword in SignUpActivity");
            this.password.setError(null);
        }

        return isStrong;

    }

    private void confirmPassword(String password) {
        String temp = this.password.getText().toString();

        if (!temp.equals(password)) {
            validatePassword.setTextColor(Color.RED);
            validatePassword.setText(R.string.password_does_not_match);
        } else {
            validatePassword.setTextColor(Color.GREEN);
            validatePassword.setText(R.string.password_match);
        }
    }

    private boolean validateConfirmPassword() {
        String strPassword = confirmPassword.getText().toString();

        if (strPassword.isEmpty()) {
            confirmPassword.setError("This cannot be empty");
            confirmPassword.requestFocus();
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.requestFocus();
            return true;
        }
    }

    private boolean validateName() {
        String strUsername = name.getText().toString();
        Pattern pattern = Pattern.compile("^[a-zA-Z ]*$");

        MatchResult result =  pattern.matcher(strUsername);

        if (strUsername.isEmpty()) {
            name.setError(getString(R.string.mame_cannot_be_empty));
            name.requestFocus();
            return false;
        } else if (!pattern.matcher(strUsername).find() ) {
            name.setError("Name cannot contain a number(0-9) or any special char(@*!)");
            name.requestFocus();
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String strPassword = email.getText().toString();

        if (strPassword.isEmpty()) {
            email.setError("Email cannot be empty");
            email.requestFocus();
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean checkUser() {
        final boolean[] available = {false};
        String strEmail = email.getText().toString();

        reference = database.getReference("Users");
        Query checkDatabase = reference.orderByChild("email").equalTo(strEmail);

        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email.setError("Username already registered");
                    email.requestFocus();
                    available[0] = true;
                } else {
                    email.setError(null);
                    available[0] = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        System.err.println("a new user is available");
        return available[0];
    }

    public void addToDatabase(View view) {
        String name = this.name.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        User user = new User(name, email, password);
        this.user = this.auth.getCurrentUser();

        reference = database.getReference("Users");
        reference.child(this.user.getUid()).setValue(user)
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    getUser().sendEmailVerification();
                    Toast.makeText(SignUpActivity.this, "Account created, please verify your Email", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(SignUpActivity.this, "Account could not created", Toast.LENGTH_LONG).show();
                }

            }
        });

        openSignIn(view);
    }

    public EditText getName() {
        return name;
    }

    public void setName(EditText name) {
        this.name = name;
    }

    public FirebaseUser getUser() {
        return user;
    }

}

