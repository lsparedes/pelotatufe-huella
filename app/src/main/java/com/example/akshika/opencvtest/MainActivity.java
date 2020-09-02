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
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import asia.kanopi.fingerscan.Status;

public class MainActivity extends AppCompatActivity {
    ImageView huella, imagen;
    TextView tvMessage, texto;
    String uri;
    Button consultar, enrolar;
    private static final int SCAN_FINGER = 0;

    private static final String TAG = "OCVSample::Activity";
    private static Bitmap bitmap, bitmap2;
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
        huella = (ImageView) findViewById(R.id.huella);
        imagen = (ImageView) findViewById(R.id.imagen);
        consultar = (Button) findViewById(R.id.buttonScan);
        enrolar = (Button) findViewById(R.id.enrolar);
        consultar.setText(Html.fromHtml("<b><big>" + "Consultar" + "</big></b>" +  "<br />" +
                "<small>" + "Validar huella digital" + "</small>" + "<br />"));
        enrolar.setText(Html.fromHtml("<b><big>" + "Enrolar" + "</big></b>" +  "<br />" +
                "<small>" + "Registrar huella digital" + "</small>" + "<br />"));
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
        img2 = new Mat();
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open("dedo2.png");
        Log.d(TAG, "Istr" +istr);
        uri= getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        uri = uri +"/icon4.png";
        Log.d(TAG, "RUTA" +uri);
        InputStream fileInputStream = new FileInputStream(uri);
        Log.d(TAG, "FiLeinputStream" +fileInputStream);
        bitmap = BitmapFactory.decodeStream(istr);
        bitmap2 = BitmapFactory.decodeStream(fileInputStream);
        //bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/imagenes/scaneado"+idRecibido+".jpg");
        Utils.bitmapToMat(bitmap, img1);
        Utils.bitmapToMat(bitmap2, img2);
        imagen.setImageBitmap(bitmap);
        huella.setImageBitmap(bitmap2);

        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGB2GRAY);

        descriptors1 = new Mat();
        descriptors2 = new Mat();
        keypoints1 = new MatOfKeyPoint();
        keypoints2 = new MatOfKeyPoint();
        detector.detect(img1, keypoints1);
        detector.detect(img2, keypoints2);
        descriptor.compute(img1, keypoints1, descriptors1);
        descriptor.compute(img2, keypoints2, descriptors2);

        if (bitmap != null && bitmap2 != null) {
//					/*if(bmpimg1.getWidth()!=bmpimg2.getWidth()){
//						bmpimg2 = Bitmap.createScaledBitmap(bmpimg2, bmpimg1.getWidth(), bmpimg1.getHeight(), true);
//					}*/
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            bitmap2 = Bitmap.createScaledBitmap(bitmap2, 100, 100, true);
            Mat img1 = new Mat();
            Utils.bitmapToMat(bitmap, img1);
            Mat img2 = new Mat();
            Utils.bitmapToMat(bitmap2, img2);
            Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGBA2GRAY);
            img1.convertTo(img1, CvType.CV_32F);
            img2.convertTo(img2, CvType.CV_32F);
            //Log.d("ImageComparator", "img1:"+img1.rows()+"x"+img1.cols()+" img2:"+img2.rows()+"x"+img2.cols());
            Mat hist1 = new Mat();
            Mat hist2 = new Mat();
            MatOfInt histSize = new MatOfInt(180);
            MatOfInt channels = new MatOfInt(0);
            ArrayList<Mat> bgr_planes1= new ArrayList<Mat>();
            ArrayList<Mat> bgr_planes2= new ArrayList<Mat>();
            Core.split(img1, bgr_planes1);
            Core.split(img2, bgr_planes2);
            MatOfFloat histRanges = new MatOfFloat (0f, 180f);
            boolean accumulate = false;
            Imgproc.calcHist(bgr_planes1, channels, new Mat(), hist1, histSize, histRanges, accumulate);
            Core.normalize(hist1, hist1, 0, hist1.rows(), Core.NORM_MINMAX, -1, new Mat());
            Imgproc.calcHist(bgr_planes2, channels, new Mat(), hist2, histSize, histRanges, accumulate);
            Core.normalize(hist2, hist2, 0, hist2.rows(), Core.NORM_MINMAX, -1, new Mat());
            img1.convertTo(img1, CvType.CV_32F);
            img2.convertTo(img2, CvType.CV_32F);
            hist1.convertTo(hist1, CvType.CV_32F);
            hist2.convertTo(hist2, CvType.CV_32F);


            double compare = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CHISQR);
            Log.d("ImageComparator", "compare: "+compare);
            Log.d("LOG!", "number of query Keypoints= " + keypoints1.size());
            Log.d("LOG!", "number of dup Keypoints= " + keypoints2.size());
            Log.d("LOG!", "number of descriptors= " + descriptors1.size());
            Log.d("LOG!", "number of dupDescriptors= " + descriptors2.size());
            matcher.match(descriptors1, descriptors2, matches);
            Log.d("LOG!", "Matches Size " + matches.size());

            if(compare>0 && compare<1500) {

                Toast.makeText(MainActivity.this, "Imágenes similares", Toast.LENGTH_LONG).show();

                //new asyncTask(MainActivity.this).execute();
            }
            else if(compare==0) {
                Toast.makeText(MainActivity.this, "Imágenes exactamente iguales", Toast.LENGTH_LONG).show();
                Log.d("valor de compare: ", String.valueOf(compare));

            }else
                Toast.makeText(MainActivity.this, "Imágenes diferentes", Toast.LENGTH_LONG).show();
            Log.d("valor de compare: ", String.valueOf(compare));
            //startTime = System.currentTimeMillis();
        } else
            Toast.makeText(MainActivity.this, "No hay imágenes seleccionadas.", Toast.LENGTH_LONG).show();


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
