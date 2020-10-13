package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoJugadoresActivity extends AppCompatActivity {


    private static ListView listajugadores;
    private static ArrayList<ItemListadoJugadores> lista_bd;
    private static ListadoJugadoresAdapter adaptador_listado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        listajugadores = (ListView) findViewById(R.id.lista_jugadores);
        TextView usuario = (TextView) findViewById(R.id.usuario);

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        usuario.setText(nombre+" - Rol: "+rol);

        lista_bd = new ArrayList<>();

        ConsultaJugadores();
    }

    public void ConsultaJugadores(){
        final ProgressDialog loading = new ProgressDialog(ListadoJugadoresActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        // Enter the correct url for your api service site
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, "https://api.androidhive.info/json/movies.json", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                loading.dismiss();
                                Log.d("TAG", "response title"+obj.getString("title"));
                                String titulo = obj.getString("title");

                                // adding movie to movies array
                                ItemListadoJugadores listado = new ItemListadoJugadores();
                                listado.setNombre(titulo);
                                lista_bd.add(listado);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adaptador_listado = new ListadoJugadoresAdapter(getApplicationContext(), lista_bd);
                            listajugadores.setAdapter(adaptador_listado);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(ListadoJugadoresActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }
}
