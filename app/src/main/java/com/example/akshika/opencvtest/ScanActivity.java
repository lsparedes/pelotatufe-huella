package com.example.akshika.opencvtest;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;


public class ScanActivity extends Activity  {

    private static final String TAG = "OCVSample::Activity";
    private TextView tvStatus;
    private TextView tvError;
    private Fingerprint fingerprint;
    String idRecibidoScan;


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
        idRecibidoScan = getIntent().getStringExtra("id_scan");
        Toast.makeText(getApplicationContext(), "Id jugador Scan: " + idRecibidoScan, Toast.LENGTH_SHORT).show();
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