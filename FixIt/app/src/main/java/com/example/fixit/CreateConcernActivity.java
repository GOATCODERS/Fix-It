package com.example.fixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateConcernActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ConnectionThread checkConnection = new ConnectionThread();
    private Spinner spLocation;
    private EditText fixDate, accountNumber;
    private ImageButton imageButton;
    private ImageView imageView;
    private Bitmap bitmap;
    StorageReference storage;

    public String mUsername;

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
        setContentView(R.layout.activity_create_concern);
        spLocation = (Spinner) findViewById(R.id.spinner_location);
        fixDate = (EditText) findViewById(R.id.fix_date);
        imageButton = (ImageButton) findViewById(R.id.fix_date_picker);
        imageView = (ImageView) findViewById(R.id.image_view);
        accountNumber = (EditText) findViewById(R.id.city_account_number);

        storage = FirebaseStorage.getInstance().getReference();

        Bundle extra = getIntent().getExtras();


        if(!extra.isEmpty())
            mUsername = extra.getString("username");
        bitmap = (Bitmap) getIntent().getParcelableExtra("bitmap");


        try {
            storeImage();
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_location, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLocation.setAdapter(adapter);


    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        calendar.set(Calendar.MONTH, month);
//        calendar.set(Calendar.YEAR, year);

//        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String currentDate = dayOfMonth + "-" + month + "-" + year;
        fixDate.setText(currentDate);
    }

    public void openDatePicker(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    private boolean validateAccount() {
        String strAccount = accountNumber.getText().toString();
        if (strAccount.isEmpty()) {
            accountNumber.setError("This field cannot be empty");
            return false;
        } else if (strAccount.length() < 10) {
            accountNumber.setError("Account Number is invalid");
            return false;
        } else {
            accountNumber.setError(null);
            return true;
        }
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickSubmit(View view) {
        if (validateAccount()) {
            Toast.makeText(this, "Submitted Successfully", Toast.LENGTH_LONG).show();
        }
    }

    private void storeImage() throws IOException {
        try {
//        String path = getApplicationContext().getFilesDir().getPath();
//        File imageFile = new File(path, "FixIt_Capture");
//
//        if(!imageFile.exists())
//        {
//            imageFile.mkdirs();
//        }
//
//        File imageFile2 = new File(imageFile, fileName + ".png");
//        FileOutputStream fileStream = new FileOutputStream(imageFile2);
            //
//        fileStream.flush();
//        fileStream.close();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            byte bb[] = bytes.toByteArray();

            uploadToFirebase(bb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadToFirebase(byte[] bb)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyMMddHHmmss", Locale.ENGLISH);
        String fileName = dateFormat.format(new Date());

        StorageReference reference = storage.child(mUsername+ "/"+fileName+".jpg");
        reference.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateConcernActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateConcernActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
            }
        });
    }

}