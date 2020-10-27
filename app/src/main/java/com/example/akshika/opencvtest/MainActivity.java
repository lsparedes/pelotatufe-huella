package com.example.akshika.opencvtest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
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

import java.io.ByteArrayInputStream;
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
    InputStream istr, istr2;
    Button consultar, enrolar, jugador_enrolado, cuenta_activada, citado;
    private static final int SCAN_FINGER = 0;
    byte[] img;

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
    private static String nombre_jugador, club, fingerprint, confirmacion, hour, serie;
    TextView nombre_usuario, club_usuario, serie_usuario;


    private static MatOfDMatch matches, matches_final_mat;
    DescriptorMatcher matcher;
    private static String idRecibido,imagen2 ;
    private static final int CODIGO_SOLICITUD_PERMISO=123;
    TextView usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        //tvMessage = (TextView) findViewById(R.id.tvMessage);
        //texto = (TextView) findViewById(R.id.texto);
        huella = (ImageView) findViewById(R.id.huella);
        imagen = (ImageView) findViewById(R.id.imagen);
        nombre_usuario = (TextView) findViewById(R.id.nombre);
        club_usuario = (TextView) findViewById(R.id.equipo);
        serie_usuario = (TextView) findViewById(R.id.serie);
        //consultar = (Button) findViewById(R.id.validar);
        enrolar = (Button) findViewById(R.id.enrolar);
        jugador_enrolado = (Button) findViewById(R.id.jugador_enrolado);
        //cuenta_activada = (Button) findViewById(R.id.cuenta);
        //citado = (Button) findViewById(R.id.citacion);
        usuario = (TextView) findViewById(R.id.usuario);

        nombre_jugador =  nombre_jugador = getIntent().getStringExtra("nombre");
        idRecibido = getIntent().getStringExtra("id");
        club = getIntent().getStringExtra("club");
        fingerprint = getIntent().getStringExtra("fingerprint");
        confirmacion = getIntent().getStringExtra("confirmacion");
        //hour = getIntent().getStringExtra("citado_hoy");
        serie = getIntent().getStringExtra("serie");

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        usuario.setText(nombre+" (Rol "+rol+")");
        nombre_usuario.setText(nombre_jugador);
        club_usuario.setText(club);
        serie_usuario.setText(serie);


        if(fingerprint.equals("null")){
            enrolar.setVisibility(View.VISIBLE);
            jugador_enrolado.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_cancel_24, 0);
        }else{
            //consultar.setVisibility(View.VISIBLE);
            jugador_enrolado.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);
        }
        //if(confirmacion.equals("null")){
          //  cuenta_activada.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_cancel_24, 0);
        //}else{

          //  cuenta_activada.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);
        //}
      /* if(hour.equals("no_citado_hoy")){
            consultar.setVisibility(View.INVISIBLE);
            citado.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_cancel_24, 0);
        }else{
            citado.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0);
        }*/
        requestAppPermissions();


    }

//    public void startScan(View view) {
//        Intent intent = new Intent(this, ScanActivity.class);
//        idRecibido = getIntent().getStringExtra("id");
//        Toast.makeText(getApplicationContext(), idRecibido, Toast.LENGTH_SHORT).show();
//        intent.putExtra("id_scan", idRecibido);
//        intent.putExtra("SCAN_FINGER", SCAN_FINGER);
//        startActivity(intent);
//
//    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }

    public void IngresoImagen(){
        final ProgressDialog loading = new ProgressDialog(MainActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("image",imagen2);
            object.put("id", idRecibido);
            Log.d(TAG, "imagen 2 es: "+imagen2);
            Log.d(TAG, "id recibido: "+idRecibido);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://proyectos.drup.cl/pelotatufe/api/v1/players/enroll", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
         //               Toast.makeText(Login_screen.this,"String Response : "+ response.toString(),Toast.LENGTH_LONG).show();

                        //Log.i("JSON", String.valueOf(response));
                        //loading.dismiss();
                        try {

                            String success = response.getString("success");
                            loading.dismiss();
                            Log.i("success", success);
                            if(success == "false"){
                                Toast.makeText(getApplicationContext(), "Error al enrolar.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "¡Enrolado con éxito!.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.i("success", "hola");


//                        resultTextView.setText("String Response : "+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int status;
        String errorMesssage;
        switch (requestCode) {
            case (SCAN_FINGER): {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        //Toast.makeText(MainActivity.this, "Hola me leyo la huellita bien :)", Toast.LENGTH_LONG).show();

                        img = data.getByteArrayExtra("img");
                        Log.d(TAG, "esto vale img: "+img);
//                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);

//                        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
//                        byte[] buffer = bmpUtil.convertToBmp24bit(img);
//
                        imagen2 = Base64.encodeToString(img, Base64.DEFAULT);
                        Log.d(TAG, "esto vale imagen2: "+imagen2);
                        //ivFinger.setImageBitmap(bm);

                        IngresoImagen();
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        //Toast.makeText(MainActivity.this, "Rayos, mamá ño", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
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




}
