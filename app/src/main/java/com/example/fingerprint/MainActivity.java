package com.example.fingerprint;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import asia.kanopi.fingerscan.Status;

public class MainActivity extends AppCompatActivity {

    // VARIABLES LEER HUELLA
    ImageView ivFinger;
    TextView tvMessage, texto;
    byte[] img;
    Bitmap bm;
    String imgDecodableString;
    private static final int SCAN_FINGER = 0;

    //VARIABLES PARA COMPARAR IMAGENES
    ImageView  mOpenCvCameraView;
    private static final String TAG = "OCVSample::Activity";
    private static Bitmap bitmap, bitmap2;
    private static final int REQUEST_PERMISSION = 100;
    private int w, h;
    TextView tvName;
    //LIBRERIA OPENCV
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    Mat descriptors1;
    Mat img1;
    MatOfKeyPoint keypoints1;
    private static int min_dist = 10;
    private static int min_matches = 750;
    private static MatOfDMatch matches, matches_final_mat;
    DescriptorMatcher matcher;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvMessage = (TextView) findViewById(R.id.tvMessage);
        texto = (TextView) findViewById(R.id.texto);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
        mOpenCvCameraView = (ImageView) findViewById(R.id.imagenasset);
    }

    //huella
    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }

    //comparacion imagenes
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

    //comparacion imagenes
    private void initializeOpenCVDependencies() throws IOException {

        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matches = new MatOfDMatch();
        img1 = new Mat();
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open("dedo2.png");
        bitmap = BitmapFactory.decodeStream(istr);
        mOpenCvCameraView.setImageBitmap(bitmap);
    }

    //imprimiendo la imagen desde la huella
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int status;
        String errorMesssage;
        switch(requestCode) {
            case (SCAN_FINGER) : {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        tvMessage.setText("Fingerprint captured");
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
                        byte[] buffer = bmpUtil.convertToBmp24bit(img);

                        //imgDecodableString = Base64.encodeToString(img, Base64.DEFAULT);
                        ivFinger.setImageBitmap(bm);
                        //texto.setText(Arrays.toString(buffer));
                        //Toast.makeText(getApplicationContext(), imgDecodableString , Toast.LENGTH_LONG).show();

                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        tvMessage.setText("-- Error: " +  errorMesssage + " --");
                    }
                }
                break;
            }
        }
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