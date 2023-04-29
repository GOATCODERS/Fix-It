package com.example.fixit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

public class PreviewActivity extends AppCompatActivity
{
    ConnectionThread checkConnection = new ConnectionThread();
    Bitmap bitmap;
    private ImageView imageView;
    private Button retry, create;



//    ObjectDetectorOptions optionsStream, optionsSingle;
//    ObjectDetector singleDetector, streamDetector;
//    InputImage image;


//    private class YourAnalyzer implements ImageAnalysis.Analyzer {
//
//        @Override
//        public void analyze(ImageProxy imageProxy) {
//            Image mediaImage = null;
//            if (mediaImage != null) {
//                InputImage image =
//                        InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//                // Pass image to an ML Kit Vision API
//                // ...
//            }
//        }
//    }

//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 0);
//        ORIENTATIONS.append(Surface.ROTATION_90, 90);
//        ORIENTATIONS.append(Surface.ROTATION_180, 180);
//        ORIENTATIONS.append(Surface.ROTATION_270, 270);
//    }

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
        setContentView(R.layout.activity_preview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Image Preview");
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);
        retry = (Button) findViewById(R.id.retry);
        create = (Button) findViewById(R.id.create_a_concern);

        openCamera();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewConcern();
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==12)
        {
            if(data!=null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                Toast.makeText(this,"Scanning successful", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,"Scanning unsuccessful", Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,12);
    }

    public void openNewConcern()
    {
        Intent intent = new Intent(this, CreateConcernActivity.class);
        intent.putExtra("bitmap", bitmap);
        startActivity(intent);
        finish();
    }

}