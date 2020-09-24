package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugadores);

        rut = (TextInputEditText) findViewById(R.id.rut);
        siguiente = (Button) findViewById(R.id.siguiente);
        //listajugadores = (ListView) findViewById(R.id.lista_jugadores);
        //listajugadores.setEmptyView(findViewById(R.id.mensajevacio));

        siguiente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EventDetails();
            }
        });

        //getSupportActionBar().hide();
        //lista_bd = new ArrayList<>();


    }

    public void EventDetails(){
        final ProgressDialog loading = new ProgressDialog(JugadorActivity.this);
        loading.setMessage("Please Wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("rut",rut.getText().toString());

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
                                 String name = player.getString("name");
                                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                 intent.putExtra("nombre", name);
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
