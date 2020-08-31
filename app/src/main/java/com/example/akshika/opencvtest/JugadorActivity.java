package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JugadorActivity extends AppCompatActivity {

    private ListView listajugadores;
    static ArrayList<ItemJugadores> lista_bd;
    JugadoresAdapter adaptador_jugadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugadores);

        listajugadores = (ListView) findViewById(R.id.lista_jugadores);
        listajugadores.setEmptyView(findViewById(R.id.mensajevacio));

        getSupportActionBar().hide();

        lista_bd = new ArrayList<>();
        new Jugadores().execute();


    }

    private class Jugadores extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(JugadorActivity.this);
            pDialog.setMessage("Cargando información...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String url = "https://jsonplaceholder.typicode.com/users";
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.d("JSON", jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonObj = new JSONArray(jsonStr);

                    for (int i = 0; i < jsonObj.length(); i++) {
                        JSONObject c = jsonObj.getJSONObject(i);
                        String id = c.getString("id");
                        String nombre = c.getString("name");


                        Log.d("Id", c.getString("id"));

                        ItemJugadores e=new ItemJugadores();
                        e.setId(id);
                        e.setNombre(nombre);

                        lista_bd.add(e);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Esta habiendo problemas para cargar el JSON");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
            adaptador_jugadores = new JugadoresAdapter(JugadorActivity.this,lista_bd);
            listajugadores.setAdapter(adaptador_jugadores);

        }


    }
}
