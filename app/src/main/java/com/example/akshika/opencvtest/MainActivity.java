package com.example.akshika.opencvtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import asia.kanopi.fingerscan.Status;

public class MainActivity extends AppCompatActivity {
    ImageView ivFinger;
    TextView tvMessage, texto;
    byte[] img;
    Bitmap bm;
    String imgDecodableString;
    private static final int SCAN_FINGER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        texto = (TextView) findViewById(R.id.texto);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }

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

                        imgDecodableString = Base64.encodeToString(img, Base64.DEFAULT);
                        ivFinger.setImageBitmap(bm);
                        //texto.setText(Arrays.toString(buffer));
                       // Toast.makeText(getApplicationContext(), imgDecodableString , Toast.LENGTH_LONG).show();

                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        tvMessage.setText("-- Error: " +  errorMesssage + " --");
                    }
                }
                break;
            }
        }
    }
}