package com.example.akshika.opencvtest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;


public class ScanActivity extends Activity  {

    private static final String TAG = "OCVSample::Activity";
    private TextView tvStatus;
    private TextView tvError;
    private Fingerprint fingerprint;



    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }







    public ScanActivity() {

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvError = (TextView) findViewById(R.id.tvError);
        fingerprint = new Fingerprint();

    }

    @Override
   protected void onStart() {
        fingerprint.scan(this, printHandler, updateHandler);
        //Toast.makeText(getApplicationContext(), (CharSequence) printHandler, Toast.LENGTH_LONG).show();
       super.onStart();
    }

    @Override
    protected void onStop() {
        fingerprint.turnOffReader();
        super.onStop();
    }

    Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");

            switch (status) {
                case Status.INITIALISED:
                   // Toast.makeText(getApplicationContext(),"Setting up reader", Toast.LENGTH_SHORT);
                    tvStatus.setText("Setting up reader");
                    break;
                case Status.SCANNER_POWERED_ON:
                    //Toast.makeText(getApplicationContext(),"Reader powered on", Toast.LENGTH_SHORT);
                    tvStatus.setText("Reader powered on");
                    break;
                case Status.READY_TO_SCAN:
                    //Toast.makeText(getApplicationContext(),"Ready to scan finger", Toast.LENGTH_SHORT);
                    tvStatus.setText("Ready to scan finger");
                    break;
                case Status.FINGER_DETECTED:
                    //Toast.makeText(getApplicationContext(),"Finger detected", Toast.LENGTH_SHORT);
                    tvStatus.setText("Finger detected");
                    break;
                case Status.RECEIVING_IMAGE:
                    //Toast.makeText(getApplicationContext(),"Receiving image", Toast.LENGTH_SHORT);
                    tvStatus.setText("Receiving image");
                    break;
                case Status.FINGER_LIFTED:
                    //Toast.makeText(getApplicationContext(),"Finger has been lifted off reader", Toast.LENGTH_SHORT);
                    tvStatus.setText("Finger has been lifted off reader");
                    break;
                case Status.SCANNER_POWERED_OFF:
                    //Toast.makeText(getApplicationContext(),"Reader is off", Toast.LENGTH_SHORT);
                    tvStatus.setText("Reader is off");
                    break;
                case Status.SUCCESS:
                    //Toast.makeText(getApplicationContext(),"Fingerprint successfully captured", Toast.LENGTH_SHORT);
                    tvStatus.setText("Fingerprint successfully captured");
                    break;
                case Status.ERROR:
                    //Toast.makeText(getApplicationContext(),"Error "+msg.getData().getString("errorMessage"), Toast.LENGTH_SHORT);
                    tvStatus.setText("Error");
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    //Toast.makeText(getApplicationContext(),"Error "+ msg.getData().getString("errorMessage"), Toast.LENGTH_SHORT);
                    tvStatus.setText(String.valueOf(status));
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;


            }
        }
    };

    Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            byte[] image;
            String errorMessage = "empty";
            int status = msg.getData().getInt("status");
            Intent intent = new Intent();
            intent.putExtra("status", status);
            if (status == Status.SUCCESS) {
                image = msg.getData().getByteArray("img");
                intent.putExtra("img", image);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    };










}