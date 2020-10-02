package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JugadorActivity extends AppCompatActivity {

    private ListView listajugadores;
    static ArrayList<ItemJugadores> lista_bd;
    JugadoresAdapter adaptador_jugadores;
    public static TextInputEditText rut;
    Button siguiente;
    String hour;
    private static String valortext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugadores);

        rut = (TextInputEditText) findViewById(R.id.rut);
        siguiente = (Button) findViewById(R.id.siguiente);
        //listajugadores = (ListView) findViewById(R.id.lista_jugadores);
        //listajugadores.setEmptyView(findViewById(R.id.mensajevacio));
        siguiente.setVisibility(View.INVISIBLE);
        rut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(rut.getText().length() == 0){
                    //siguiente.setEnabled(false);

                    siguiente.setVisibility(View.INVISIBLE);
                    Log.d("TAG","largobefore"+rut.getText().length());
                }else{
                    Log.d("TAG","largobfeore"+rut.getText().length());
                    //siguiente.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valortext = rut.getText().toString().trim();
                if(valortext.equals(""))
                {
                    valortext = "0";
                }
                else
                {
                    if(rut.getText().length() >= 8){
                        valortext = FormatearRUT(valortext); //Sustituyes por la funcion que te formateara el rut
                        Log.d("TAG","FORMATEADO: "+valortext);
                        siguiente.setVisibility(View.VISIBLE);


                    }else{
                        siguiente.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(rut.getText().length() >= 8){
                    //siguiente.setEnabled(false);
                    siguiente.setVisibility(View.VISIBLE);
                    siguiente.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            IngresoJugadores();
                        }

                    });
                }else{
                    Log.d("TAG","largo"+rut.getText().length());

                }
            }
        });




        //getSupportActionBar().hide();
        //lista_bd = new ArrayList<>();


    }

    public String FormatearRUT(String rut) {

        int cont = 0;
        String format;
        rut = rut.replace(".", "");
        rut = rut.replace("-", "");
        format = "-" + rut.substring(rut.length() - 1);
        for (int i = rut.length() - 2; i >= 0; i--) {
            format = rut.substring(i, i + 1) + format;
            cont++;
            if (cont == 3 && i != 0) {
                format = "." + format;
                cont = 0;
            }
        }
        return format;
    }

    public void IngresoJugadores(){
        final ProgressDialog loading = new ProgressDialog(JugadorActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            //object.put("rut",rut.getText().toString());
            object.put("rut",valortext);
            Log.d("TAG","Valor text"+valortext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://proyectos.drup.cl/pelotatufe/api/v1/players/verified", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(Login_screen.this,"String Response : "+ response.toString(),Toast.LENGTH_LONG).show();

                            //Log.i("JSON", String.valueOf(response));
                            //loading.dismiss();
                        try {

                            String success = response.getString("success");
                            loading.dismiss();
                            if(success == "false"){
                                Toast.makeText(getApplicationContext(), "Este jugador no existe.", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                 JSONObject player = response.getJSONObject("player");
                                 //JSONObject clubjson = player.getJSONObject("club");
                                 //JSONObject seriejson = player.getJSONObject("serie");
                                 Log.i("Player 1", String.valueOf(player));
                                 //JSONObject player_matches = player.getJSONObject("players_matches");
                                // Log.i("Player 2", String.valueOf(player_matches));

                                /* JSONArray match = player_matches.getJSONArray("match");
                                 if(match.length() != 0){
                                     JSONObject comienzo_match = match.getJSONObject(0);
                                      hour = comienzo_match.getString("hour");
                                 }
                                 else{
                                     hour = "no citado";
                                 }*/
                                 //Log.i("match", String.valueOf(match));

                                 String id = player.getString("id");
                                 String confirmacion = player.getString("rut");
                                 String name = player.getString("name");
                                 String club = player.getString("club");
                                 String serie = player.getString("serie");
                                 String fingerprint = player.getString("fingerprint");


                                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                 intent.putExtra("id", id);
                                 intent.putExtra("nombre", name);
                                 intent.putExtra("club", club);
                                 intent.putExtra("serie", serie);
                                 intent.putExtra("fingerprint", fingerprint);
                                 intent.putExtra("confirmacion", confirmacion);
                                 //intent.putExtra("hour",hour);
                                 startActivity(intent);
                            }
                            Log.i("success", success);
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
                Toast.makeText(JugadorActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

}
