package com.example.fixit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadProfilePictureActivity extends AppCompatActivity
{
    ConnectionThread checkConnection = new ConnectionThread();
    Bitmap bitmap;
    Uri uri;
    private ImageView imageView;
    StorageReference storage;
    FirebaseDatabase database;
    DatabaseReference reference;
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
        setContentView(R.layout.activity_upload_profile_picture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Update profile picture");
        setSupportActionBar(toolbar);

        storage = FirebaseStorage.getInstance().getReference("User_dp");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        imageView = (ImageView) findViewById(R.id.profile_dp);
        Button upload = (Button) findViewById(R.id.upload_image);
        Button capture = (Button) findViewById(R.id.capture_image);
        Button update = (Button) findViewById(R.id.update_picture);
        Button cancel = (Button) findViewById(R.id.btnCancel);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri = null;
                openCamera();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = null;
                openImagePicker();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfilePicture();
            }
        });
    }

    public void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,11);
    }

    public void openImagePicker(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,10);
    }

    public void updateProfilePicture()
    {
        if(bitmap != null)
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            byte bb[] = bytes.toByteArray();
            uploadToFirebase(bb);
        }else if(uri != null)
        {

        }else
        {
            Toast.makeText(this, "You haven't selected anything", Toast.LENGTH_LONG).show();
        }
    }
    private void uploadToFirebase(byte[] bb)
    {
        String fileName = user.getUid() + ".jpg";

        StorageReference storageReference = storage.child(fileName);

        storageReference.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child(user.getUid()).child("profilePic").setValue(fileName);
                Toast.makeText(UploadProfilePictureActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadProfilePictureActivity.this, "Failed to Update", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        if(requestCode==11)
        {
            if(data!=null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }else {
                Toast.makeText(this,"Capturing was not successful", Toast.LENGTH_LONG).show();
            }

        }else if(requestCode==10)
        {
            if(data!=null) {
                uri = (Uri) data.getData();
                imageView.setImageURI(uri);
            }else {
                Toast.makeText(this,"Uploading was not successful", Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}