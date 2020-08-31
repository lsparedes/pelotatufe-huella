package com.example.akshika.opencvtest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import asia.kanopi.fingerscan.Status;

public class MainActivity extends AppCompatActivity {
    ImageView ivFinger, ivFinger2;
    TextView tvMessage, texto;
    byte[] img;
    Bitmap bm;
    String imgDecodableString;
    private static final int SCAN_FINGER = 0;

    private static final String TAG = "OCVSample::Activity";
    private static Bitmap bitmap, bitmap2;
    private static final int REQUEST_PERMISSION = 100;
    private int w, h;

    TextView tvName;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    FeatureDetector detector;
    DescriptorExtractor descriptor;

    Mat descriptors2, descriptors1;
    Mat img1, img2;
    MatOfKeyPoint keypoints1, keypoints2;
    private static int min_dist = 10;
    private static int min_matches = 750;

    private static MatOfDMatch matches, matches_final_mat;
    DescriptorMatcher matcher;
    String idRecibido;
    private static final int CODIGO_SOLICITUD_PERMISO=123;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        texto = (TextView) findViewById(R.id.texto);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
        ivFinger2 = (ImageView) findViewById(R.id.ivFingerDisplay2);

        requestAppPermissions();


    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        idRecibido = getIntent().getStringExtra("id");
        intent.putExtra("id_scan", idRecibido);
        intent.putExtra("SCAN_FINGER", SCAN_FINGER);
        startActivity(intent);

    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        int status;
//        String errorMesssage;
//        switch (requestCode) {
//            case (SCAN_FINGER): {
//                if (resultCode == RESULT_OK) {
//                    status = data.getIntExtra("status", Status.ERROR);
//                    if (status == Status.SUCCESS) {
//                        tvMessage.setText("Fingerprint captured");
//                        img = data.getByteArrayExtra("img");
//                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
//
//                        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
//                        byte[] buffer = bmpUtil.convertToBmp24bit(img);
//
//                        imgDecodableString = Base64.encodeToString(img, Base64.DEFAULT);
//                        //ivFinger.setImageBitmap(bm);
//
//
//                    } else {
//                        errorMesssage = data.getStringExtra("errorMessage");
//                        tvMessage.setText("-- Error: " + errorMesssage + " --");
//                    }
//                }
//                break;
//            }
//        }
//    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                    try {
                        initializeOpenCVDependencies();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void initializeOpenCVDependencies() throws IOException {


        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matches = new MatOfDMatch();
        img1 = new Mat();
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open("dedo2.png");
        bitmap = BitmapFactory.decodeStream(istr);

        ivFinger2.setImageBitmap(bitmap);

        bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/imagenes/scaneado"+idRecibido+".jpg");
        ivFinger.setImageBitmap(bitmap2);








    }




    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, CODIGO_SOLICITUD_PERMISO); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

}
