package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JugadoresExtrasActivity extends AppCompatActivity {

    private static String serie_seleccionada, series_jugadores, club_jugadores;
    public static String url_seleccionada, fingerprint_extra, id_extra, serie_extra,versus_extra,camiseta_extra;
    private static ListView lista;
    private static ArrayList<ItemJugadoresExtras> lista_jugadores;
    private static JugadoresExtrasAdapter adaptador_jugadores;
    TextView usuario, serie, club;
    ImageView huella, imagen;

    private static final String TAG = "OCVSample::Activity";
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

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugadores_extras);

        club = (TextView) findViewById(R.id.club);
        serie= (TextView) findViewById(R.id.serie);
        usuario= (TextView) findViewById(R.id.usuario);
        lista = (ListView) findViewById(R.id.lista_jugadores);
        huella = (ImageView) findViewById(R.id.huella);
        imagen = (ImageView) findViewById(R.id.imagen);

        lista.setEmptyView(findViewById(R.id.mensajevacio));
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        club_jugadores = sharedPreferences.getString("club", "");
        String series_jugadores = sharedPreferences.getString("serie","");
        serie_seleccionada = getIntent().getStringExtra("listado_serie");
        series_jugadores = sharedPreferences.getString("serie","");
        id_extra =  sharedPreferences.getString("id_jugador_extra", "");
        fingerprint_extra = sharedPreferences.getString("fingerprint_extra", "");
        serie_extra= sharedPreferences.getString("serie","");
        versus_extra = sharedPreferences.getString("id_versus","");
        camiseta_extra = sharedPreferences.getString("camiseta_extra","");

        usuario.setText(nombre+" (Rol "+rol+")");
        club.setText(club_jugadores);
        serie.setText(series_jugadores);

        lista_jugadores = new ArrayList<>();

        if(serie_seleccionada.equals("Segunda Infantil") && series_jugadores.equals("Primera infantil")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/segunda/infantil";
        }
        else if(serie_seleccionada.equals("Tercera Infantil") && series_jugadores.equals("Segunda infantil")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/tercera/infantil";
        }
        else if(serie_seleccionada.equals("Primera Infantil") && series_jugadores.equals("Juvenil")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/primera/infantil";
        }
        else if(serie_seleccionada.equals("Primera Infantil") && series_jugadores.equals("Primera adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/primera/infantil/adulta";
        }
        else if(serie_seleccionada.equals("Segunda Adulta") && series_jugadores.equals("Primera adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/segunda/adulta";
        }
        else if(serie_seleccionada.equals("Seniors") && series_jugadores.equals("Primera adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/seniors";
        }
        else if(serie_seleccionada.equals("Super Seniors") && series_jugadores.equals("Primera adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/super/seniors";
        }
        else if(serie_seleccionada.equals("Primera Infantil") && series_jugadores.equals("Segunda adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/primera/infantil/adulta";
        }
        else if(serie_seleccionada.equals("Seniors") && series_jugadores.equals("Segunda adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/seniors";
        }
        else if(serie_seleccionada.equals("Super Seniors") && series_jugadores.equals("Segunda adulta")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/super/seniors";
        }
        else if(serie_seleccionada.equals("Primera Infantil") && series_jugadores.equals("Honor")){
            url_seleccionada= "http://proyectos.drup.cl/pelotatufe/api/v1/primera/infantil";
        }
        else if(serie_seleccionada.equals("Seniors") && series_jugadores.equals("Honor")){
            url_seleccionada ="http://proyectos.drup.cl/pelotatufe/api/v1/seniors";
        }
        else if(serie_seleccionada.equals("Primera Adulta") && series_jugadores.equals("Honor")){
            url_seleccionada ="http://proyectos.drup.cl/pelotatufe/api/v1/primera/adulta";
        }
        else if(serie_seleccionada.equals("Segunda Adulta") && series_jugadores.equals("Honor")){
            url_seleccionada ="http://proyectos.drup.cl/pelotatufe/api/v1/segunda/adulta";
        }
        else if(serie_seleccionada.equals("Super Seniors") && series_jugadores.equals("Honor")){
            url_seleccionada ="http://proyectos.drup.cl/pelotatufe/api/v1/super/seniors";
        }
        else if(serie_seleccionada.equals("Super Seniors") && series_jugadores.equals("Seniors")){
            url_seleccionada ="http://proyectos.drup.cl/pelotatufe/api/v1/super/seniors";
        }
        else if(serie_seleccionada.equals("Super Seniors") && series_jugadores.equals("Super seniors")){
            url_seleccionada ="http://proyectos.drup.cl/pelotatufe/api/v1/super/seniors";
        }


        //Toast.makeText(JugadoresExtrasActivity.this,  url_seleccionada, Toast.LENGTH_LONG).show();
        ConsultaJugadores();

    }

    public void ConsultaJugadores(){
        final ProgressDialog loading = new ProgressDialog(JugadoresExtrasActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();


        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_seleccionada, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jugadores = response.getJSONArray("data");
                            Log.d("TAG", "response jugadores "+response);
                            loading.dismiss();
                            for (int i = 0; i < jugadores.length(); i++) {
                                JSONObject  jugadores_club = jugadores.getJSONObject(i);
                                String name= jugadores_club.getString("name");
                                String id = jugadores_club.getString("id");
                                String fingerprint = jugadores_club.getString("fingerprint");
                                Log.d("TAG", "nombre jugadores "+name);

                                ItemJugadoresExtras adapter= new ItemJugadoresExtras();
                                adapter.setNombre(name);
                                adapter.setIdJugadores(id);
                                adapter.setFingerPrint(fingerprint);
                                lista_jugadores.add(adapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adaptador_jugadores = new JugadoresExtrasAdapter(getApplicationContext(), lista_jugadores);
                        lista.setAdapter(adaptador_jugadores);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());

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

        istr = new ByteArrayInputStream(Base64.decode(fingerprint_extra.getBytes(), Base64.DEFAULT));
        //AssetManager assetManager = getAssets();

        //FileInputStream istr = new FileInputStream());
        //InputStream istr = assetManager.open("imagen_1.txt");
        Log.d(TAG, "Istr" +istr);

        //imagen2
        uri= getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();


        uri = uri +"/imagenes/scaneado"+id_extra+".jpg";
        //Toast.makeText(MainActivity.this, "istr:"+istr, Toast.LENGTH_LONG).show();
        Log.d(TAG, "RUTA" +uri);

        istr2 = new FileInputStream(uri);
        Log.d(TAG, "Istr2" +istr2);


        //decodeStram de las imagenes formato InputStream
        bitmap = BitmapFactory.decodeStream(istr);
        bitmap2 = BitmapFactory.decodeStream(istr2);

        if (bitmap != null && bitmap2 != null) {

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

                Toast.makeText(JugadoresExtrasActivity.this, "Huella reconocida con exito!", Toast.LENGTH_LONG).show();
                //VerificacionFingerPrint();
                Log.d("compare similares: ", String.valueOf(compare));
                IngresoMatch();
                //new asyncTask(MainActivity.this).execute();
            }
            else if(compare==0) {
                Toast.makeText(JugadoresExtrasActivity.this, "Huella reconocida con exito!", Toast.LENGTH_LONG).show();
                Log.d("compare iguales: ", String.valueOf(compare));
                IngresoMatch();
                // VerificacionFingerPrint();
            }else {
                Toast.makeText(JugadoresExtrasActivity.this, "Huella no encontrada!", Toast.LENGTH_LONG).show();
                Log.d("compare diferentes: ", String.valueOf(compare));
                //startTime = System.currentTimeMillis();
                IngresoMatch();
            }
        } else {
            Toast.makeText(JugadoresExtrasActivity.this, "Vuelve a intentarlo!.", Toast.LENGTH_LONG).show();
        }

    }

    public void IngresoMatch(){


        JSONObject object = new JSONObject();
        try {

            object.put("id_player",id_extra);
            object.put("serie", serie_extra);
            object.put("versus",versus_extra);
            object.put("club", club_jugadores);
            object.put("numero", camiseta_extra);

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
                            Log.i("JSON SUCCESS", success);
                            //Toast.makeText(getApplicationContext(), "¡Huella reconocida con exito!.", Toast.LENGTH_SHORT).show();

                            String ruta= getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                            File file = new File(ruta +"/imagenes/scaneado"+id_extra+".jpg");
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
