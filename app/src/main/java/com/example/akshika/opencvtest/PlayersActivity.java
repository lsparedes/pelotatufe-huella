package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayersActivity extends AppCompatActivity {


    private static ListView listaplayers;
    private static ArrayList<ItemPlayers> lista_bd;
    private static PlayersAdapter adaptador_jugadores;
    TextView usuario;
    private static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        listaplayers = (ListView) findViewById(R.id.lista_players);
        listaplayers.setEmptyView(findViewById(R.id.mensajevacio));
        usuario = (TextView) findViewById(R.id.usuario);

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        id = sharedPreferences.getString("id","");
        usuario.setText(nombre+" (Rol "+rol+")");
        lista_bd = new ArrayList<>();

        ConsultaPlayers();
    }

    public void ConsultaPlayers(){
        final ProgressDialog loading = new ProgressDialog(PlayersActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {

            object.put("turno",id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://pelotatufe.cl/api/v1/players/turn", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray turno = response.getJSONArray("turno");
                            Log.d("TAG", "response campeonato"+turno);
                            loading.dismiss();
                            for (int i = 0; i < turno.length(); i++) {
                                JSONObject  campeonatos = turno.getJSONObject(i);
                                String titulo = campeonatos.getString("championship");
                                String hora = campeonatos.getString("start");
                                String serie = campeonatos.getString("serie");
                                String id = campeonatos.getString("id");
                                Log.d("TAG", "largo"+turno.length());

                                // adding movie to movies array
                                ItemPlayers player = new ItemPlayers();
                                player.setCampeonato(titulo);
                                player.setSerie(serie);
                                player.setIdCampeonato(id);
                                player.setFecha(hora);
                                lista_bd.add(player);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adaptador_jugadores = new PlayersAdapter(getApplicationContext(), lista_bd);
                        listaplayers.setAdapter(adaptador_jugadores);
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
                //Toast.makeText(PlayersActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }
}

