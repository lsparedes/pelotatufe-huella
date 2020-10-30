package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import asia.kanopi.fingerscan.Status;

public class ListadoJugadoresActivity extends AppCompatActivity {

    private static final String TAG = "OCVSample::Activity";
    private static final int SCAN_FINGER = 0;
    private static ListView listajugadores;
    private static ArrayList<ItemListadoJugadores> lista_bd;
    private static ListadoJugadoresAdapter adaptador_listado;
    private static String serie_turno, club, id_jugador, fingerprint, id_jugador_recibido, fingerprint_recibido, versus, numero_camiseta;

    ImageView huella, imagen;
    Mat descriptors2, descriptors1;
    Mat img1, img2;
    MatOfKeyPoint keypoints1, keypoints2;
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    private static MatOfDMatch matches;
    String uri;
    InputStream istr, istr2;
    DescriptorMatcher matcher;
    private static Bitmap bitmap, bitmap2;
    Button completar;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        listajugadores = (ListView) findViewById(R.id.lista_jugadores);
        listajugadores.setEmptyView(findViewById(R.id.mensajevacio));
        TextView usuario = (TextView) findViewById(R.id.usuario);
        TextView partido = (TextView) findViewById(R.id.partido);
        TextView serie_citado = (TextView) findViewById(R.id.serie);
        huella = (ImageView) findViewById(R.id.huella);
        imagen = (ImageView) findViewById(R.id.imagen);
        completar = (Button) findViewById(R.id.completar);

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        serie_turno= sharedPreferences.getString("serie","");
        versus = sharedPreferences.getString("id_versus","");
        id_jugador_recibido =  sharedPreferences.getString("id_scan", "");
        fingerprint_recibido = sharedPreferences.getString("fingerprint", "");
        numero_camiseta =  sharedPreferences.getString("numero_camiseta", "");
        club = sharedPreferences.getString("club", "");

        //Toast.makeText(getApplicationContext(),numero_camiseta,Toast.LENGTH_LONG).show();

        completar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeriesExtrasActivity.class);
                startActivity(intent);
            }

        });


        usuario.setText(nombre+" (Rol "+rol+")");
        partido.setText("Club "+club);
        serie_citado.setText("Serie "+serie_turno);
        lista_bd = new ArrayList<>();
        ConsultaJugadores();


    }

    public void ConsultaJugadores(){
        final ProgressDialog loading = new ProgressDialog(ListadoJugadoresActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {

            object.put("serie",serie_turno);
            object.put("club",club);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://proyectos.drup.cl/pelotatufe/api/v1/players", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jugadores = response.getJSONArray("jugadores");
                            Log.d("TAG", "response jugadores "+jugadores);
                            loading.dismiss();
                            for (int i = 0; i < jugadores.length(); i++) {
                                JSONObject  jugadores_club = jugadores.getJSONObject(i);
                                id_jugador   = jugadores_club.getString("id");
                                fingerprint = jugadores_club.getString("fingerprint");
                                String jugador = jugadores_club.getString("name");

                                // adding movie to movies array
                                ItemListadoJugadores listado = new ItemListadoJugadores();
                                listado.setNombre(jugador);
                                listado.setFingerprint(fingerprint);
                                listado.setId(id_jugador);
                                lista_bd.add(listado);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adaptador_listado = new ListadoJugadoresAdapter(getApplicationContext(), lista_bd);
                        listajugadores.setAdapter(adaptador_listado);
                   /*     for (int i = 0; i < turno.length(); i++) {
                            try {
                                JSONArray  campeonatos = response.getJSONArray(i);
                                loading.dismiss();
                                Log.d("TAG", "response campeonato"+campeonatos.getString("championship"));
                                String titulo = campeonatos.getString("championship");

                                // adding movie to movies array
                                ItemPlayers player = new ItemPlayers();
                                player.setNombre(titulo);
                                lista_bd.add(player);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adaptador_jugadores = new PlayersAdapter(getApplicationContext(), lista_bd);
                            listaplayers.setAdapter(adaptador_jugadores);
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                //Toast.makeText(ListadoJugadoresActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

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


        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matches = new MatOfDMatch();
        img1 = new Mat();
        img2 = new Mat();

        //imagen1

        istr = new ByteArrayInputStream(Base64.decode(fingerprint_recibido.getBytes(), Base64.DEFAULT));
        //AssetManager assetManager = getAssets();

        //FileInputStream istr = new FileInputStream());
        //InputStream istr = assetManager.open("imagen_1.txt");
        Log.d(TAG, "Istr" +istr);

        //imagen2
        uri= getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();


        uri = uri +"/imagenes/scaneado"+id_jugador_recibido+".jpg";
        //Toast.makeText(MainActivity.this, "istr:"+istr, Toast.LENGTH_LONG).show();
        Log.d(TAG, "RUTA" +uri);

        istr2 = new FileInputStream(uri);
        Log.d(TAG, "Istr2" +istr2);


        //decodeStram de las imagenes formato InputStream
        bitmap = BitmapFactory.decodeStream(istr);
        bitmap2 = BitmapFactory.decodeStream(istr2);

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

            if(compare>0 && compare<1000) {

                Toast.makeText(ListadoJugadoresActivity.this, "Huella reconocida con exito!", Toast.LENGTH_LONG).show();
                //VerificacionFingerPrint();
                Log.d("compare similares: ", String.valueOf(compare));
                IngresoMatch();
                //new asyncTask(MainActivity.this).execute();
            }
            else if(compare==0) {
                Toast.makeText(ListadoJugadoresActivity.this, "Huella reconocida con exito!", Toast.LENGTH_LONG).show();
                Log.d("compare iguales: ", String.valueOf(compare));
                IngresoMatch();
                // VerificacionFingerPrint();
            }else {
                Toast.makeText(ListadoJugadoresActivity.this, "Huella no encontrada!", Toast.LENGTH_LONG).show();
                Log.d("compare diferentes: ", String.valueOf(compare));
                //startTime = System.currentTimeMillis();


            }
        } else {
            Toast.makeText(ListadoJugadoresActivity.this, "Vuelve a intentarlo!.", Toast.LENGTH_LONG).show();
        }

    }

    public void IngresoMatch(){


        JSONObject object = new JSONObject();
        try {

            object.put("id_player",id_jugador_recibido);
            object.put("serie", serie_turno);
            object.put("versus",versus);
            object.put("club", club);
            object.put("numero", numero_camiseta);

            Log.d("TAG","serie "+serie_turno+"club "+club+"versus "+versus+"id_player "+id_jugador_recibido +"numero_camiseta"+numero_camiseta);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://proyectos.drup.cl/pelotatufe/api/v1/matchs/ingreso", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String success = response.getString("success");
                            Log.d("JSON SUCCESS", String.valueOf(response));
                            //Toast.makeText(getApplicationContext(), "¡Huella reconocida con exito!.", Toast.LENGTH_SHORT).show();

                            String ruta= getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                            File file = new File(ruta +"/imagenes/scaneado"+id_jugador_recibido+".jpg");
                            file.delete();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d("Error", "Error: " + error.getMessage());
                ///Toast.makeText(TurnoActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

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
