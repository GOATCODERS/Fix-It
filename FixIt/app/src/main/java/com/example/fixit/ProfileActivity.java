package com.example.fixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    private TextView email, dob;
    private EditText name, contact;
    private ImageButton datePicker;
    private ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    private FirebaseUser user;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile Details");
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.edit_profile_name);
        email = (TextView) findViewById(R.id.edit_profile_email);
        dob = (TextView) findViewById(R.id.profile_dob);
        contact = (EditText) findViewById(R.id.edit_profile_contact);
        datePicker = (ImageButton) findViewById(R.id.dob_date_picker);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        datePicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                openDatePicker();
            }
        });

        init();

    }

    public void init() {
        progressBar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String uid = user.getUid();

        System.out.println("current user mUsername = " + uid);

        DatabaseReference checkDatabase = reference.child(uid);

        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String strName, strEmail, strContact, strAge;
                User userData = snapshot.getValue(User.class);

                if (userData != null) {
                    strContact = userData.getContact();
                    strName = userData.getName();
                    strEmail = userData.getEmail();
                    strAge = userData.getDateOfBirth();

                    name.setText(strName);
                    email.setText(strEmail);


                    if (strContact != null) {
                        contact.setText(strContact);
                    }
                    if (strAge != null) {
                        dob.setText(strAge);
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "User data not available at the moment", Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        String currentDate = dayOfMonth + "-" + month + "-" + year;
        dob.setText(currentDate);
    }

    public void openDatePicker() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    public void editProfile(View view) {
    }
}