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
    private static Bitmap bitmap, bitmap2;
    private static final int REQUEST_PERMISSION = 100;
    private int w, h;
    //private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView mOpenCvCameraView, mOpenCvCameraView2;
    TextView tvName;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    FeatureDetector detector;
    DescriptorExtractor descriptor;

    Mat descriptors2,descriptors1;
    Mat img1, img2;
    MatOfKeyPoint keypoints1,keypoints2;
    private static int min_dist = 10;
    private static int min_matches = 750;
    private Fingerprint fingerprint;


    private static MatOfDMatch matches, matches_final_mat;
    DescriptorMatcher matcher;



    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

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
        fingerprint.scan(this, printHandler, updateHandler);

        //mOpenCvCameraView.enableView();
        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matches = new MatOfDMatch();
        img1 = new Mat();
        img2= new Mat();
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open("dedo2.png");
        InputStream istr2 = assetManager.open("dedo2-reves.png");
        bitmap = BitmapFactory.decodeStream(istr);
        bitmap2 = BitmapFactory.decodeStream(istr2);
        Utils.bitmapToMat(bitmap, img1);
        Utils.bitmapToMat(bitmap2, img2);
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
        mOpenCvCameraView.setImageBitmap(bitmap);
        mOpenCvCameraView2.setImageBitmap(bitmap2);

        if (bitmap != null && bitmap2 != null) {
					/*if(bmpimg1.getWidth()!=bmpimg2.getWidth()){
						bmpimg2 = Bitmap.createScaledBitmap(bmpimg2, bmpimg1.getWidth(), bmpimg1.getHeight(), true);
					}*/
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

                Toast.makeText(ScanActivity.this, "Imágenes similares", Toast.LENGTH_LONG).show();

                //new asyncTask(MainActivity.this).execute();
            }
            else if(compare==0) {
                Toast.makeText(ScanActivity.this, "Imágenes exactamente iguales", Toast.LENGTH_LONG).show();
                Log.d("valor de compare: ", String.valueOf(compare));

            }else
                Toast.makeText(ScanActivity.this, "Imágenes diferentes", Toast.LENGTH_LONG).show();
            Log.d("valor de compare: ", String.valueOf(compare));
            //startTime = System.currentTimeMillis();
        } else
            Toast.makeText(ScanActivity.this,
                    "No hay imágenes seleccionadas.", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.layout);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }

        mOpenCvCameraView = (ImageView) findViewById(R.id.img1);
        mOpenCvCameraView2 = (ImageView) findViewById(R.id.img2);

        //mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        //mOpenCvCameraView.setCvCameraViewListener(this);
        tvName = (TextView) findViewById(R.id.text1);
        fingerprint = new Fingerprint();

    }

//    @Override
//    protected void onStart() {
//        fingerprint.scan(this, printHandler, updateHandler);
//        //Toast.makeText(getApplicationContext(), (CharSequence) printHandler, Toast.LENGTH_LONG).show();
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        fingerprint.turnOffReader();
//        super.onStop();
//    }

    Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");

            switch (status) {
                case Status.INITIALISED:
                    Toast.makeText(getApplicationContext(),"Setting up reader", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Setting up reader");
                    break;
                case Status.SCANNER_POWERED_ON:
                    Toast.makeText(getApplicationContext(),"Reader powered on", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Reader powered on");
                    break;
                case Status.READY_TO_SCAN:
                    Toast.makeText(getApplicationContext(),"Ready to scan finger", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Ready to scan finger");
                    break;
                case Status.FINGER_DETECTED:
                    Toast.makeText(getApplicationContext(),"Finger detected", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Finger detected");
                    break;
                case Status.RECEIVING_IMAGE:
                    Toast.makeText(getApplicationContext(),"Receiving image", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Receiving image");
                    break;
                case Status.FINGER_LIFTED:
                    Toast.makeText(getApplicationContext(),"Finger has been lifted off reader", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Finger has been lifted off reader");
                    break;
                case Status.SCANNER_POWERED_OFF:
                    Toast.makeText(getApplicationContext(),"Reader is off", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Reader is off");
                    break;
                case Status.SUCCESS:
                    Toast.makeText(getApplicationContext(),"Fingerprint successfully captured", Toast.LENGTH_SHORT);
                    //tvStatus.setText("Fingerprint successfully captured");
                    break;
                case Status.ERROR:
                    Toast.makeText(getApplicationContext(),"Error "+msg.getData().getString("errorMessage"), Toast.LENGTH_SHORT);
                    //tvStatus.setText("Error");
                    //tvError.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error "+ msg.getData().getString("errorMessage"), Toast.LENGTH_SHORT);
                    //tvStatus.setText(String.valueOf(status));
                    //tvError.setText(msg.getData().getString("errorMessage"));
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