package com.example.fixit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
    private TextView results;
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

        imageView = (ImageView) findViewById(R.id.imageView);
        results = (TextView) findViewById(R.id.tvResults);
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

//         optionsStream =
//                new ObjectDetectorOptions.Builder()
//                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//                        .enableClassification()  // Optional
//                        .build();
//
//        // Multiple object detection in static images
//         optionsSingle =
//                new ObjectDetectorOptions.Builder()
//                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
//                        .enableMultipleObjects()
//                        .enableClassification()  // Optional
//                        .build();
//
//        singleDetector = ObjectDetection.getClient(optionsSingle);
//        streamDetector = ObjectDetection.getClient(optionsStream);
//
//        try {
//            image = InputImage.fromMediaImage(null, getRotationCompensation("jk", this, isFinishing()));
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }

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
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
//            throws CameraAccessException {
//        // Get the device's current rotation relative to its "native" orientation.
//        // Then, from the ORIENTATIONS table, look up the angle the image must be
//        // rotated to compensate for the device's rotation.
//        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int rotationCompensation = ORIENTATIONS.get(deviceRotation);
//
//        // Get the device's sensor orientation.
//        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
//        int sensorOrientation = cameraManager
//                .getCameraCharacteristics(cameraId)
//                .get(CameraCharacteristics.SENSOR_ORIENTATION);
//
//        if (isFrontFacing) {
//            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
//        } else { // back-facing
//            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
//        }
//        return rotationCompensation;
//    }
}