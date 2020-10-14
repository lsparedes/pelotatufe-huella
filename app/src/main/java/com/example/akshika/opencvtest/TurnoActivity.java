package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import org.w3c.dom.Text;

import java.util.ArrayList;

public class TurnoActivity extends AppCompatActivity {


    private static ListView listaturno;
    private static ArrayList<ItemTurno> lista_bd;
    private static TurnoAdapter adaptador_turno;
    private static String id, club_local, club_visita;
    private static TextView nombre, club, nombre_visita;
    CardView card_local, card_visita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno);

        //listaturno = (ListView) findViewById(R.id.lista_turno);
        TextView usuario = (TextView) findViewById(R.id.usuario);
        TextView partido = (TextView) findViewById(R.id.partido);
        TextView campeonato = (TextView) findViewById(R.id.campeonato);
        TextView fecha = (TextView) findViewById(R.id.fecha);
        card_local = (CardView) findViewById(R.id.card_local);
        card_visita = (CardView) findViewById(R.id.card_visita);
        nombre = (TextView) findViewById(R.id.nombre);
        nombre_visita = (TextView) findViewById(R.id.nombre_visita);
        club = (TextView) findViewById(R.id.club);

        card_local.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListadoJugadoresActivity.class);
                intent.putExtra("club", club_local);
                startActivity(intent);
            }

        });

        card_visita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListadoJugadoresActivity.class);
                intent.putExtra("club", club_visita);
                startActivity(intent);
            }

        });
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        usuario.setText(nombre+" - Rol: "+rol);

        id = getIntent().getStringExtra("id_campeonato");

        String serie_turno= sharedPreferences.getString("serie","");
        String campeonato_turno = sharedPreferences.getString("campeonato", "");
        String fecha_turno = sharedPreferences.getString("fecha", "");

        campeonato.setText(campeonato_turno);
        partido.setText(serie_turno);
        fecha.setText(fecha_turno);


        //Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();
        //lista_bd = new ArrayList<>();

        ConsultaTurnoLocal();
        ConsultaTurnoVisita();
    }

    public void ConsultaTurnoLocal(){

        final ProgressDialog loading = new ProgressDialog(TurnoActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {

            object.put("championships",id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://proyectos.drup.cl/pelotatufe/api/v1/players/local", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {



                                JSONObject turno = response.getJSONObject("local");
                                loading.dismiss();
                                club_local = turno.getString("club");

                                //ItemTurno turno_players = new ItemTurno();
                                //turno_players.setNombre(club);
                                //lista_bd.add(turno_players);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        nombre.setText(club_local);
                       // adaptador_turno = new TurnoAdapter(getApplicationContext(), lista_bd);
                        //listaturno.setAdapter(adaptador_turno);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                ///Toast.makeText(TurnoActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

    public void ConsultaTurnoVisita(){

        final ProgressDialog loading = new ProgressDialog(TurnoActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {

            object.put("championships",id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://proyectos.drup.cl/pelotatufe/api/v1/players/visita", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {



                            JSONObject turno = response.getJSONObject("visita");
                            loading.dismiss();
                            club_visita = turno.getString("club");

                            //ItemTurno turno_players = new ItemTurno();
                            //turno_players.setNombre(club);
                            //lista_bd.add(turno_players);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        nombre_visita.setText(club_visita);
                        // adaptador_turno = new TurnoAdapter(getApplicationContext(), lista_bd);
                        //listaturno.setAdapter(adaptador_turno);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                //Toast.makeText(TurnoActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }
}
