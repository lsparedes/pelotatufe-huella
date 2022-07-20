package com.example.akshika.opencvtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SeriesExtrasActivity extends AppCompatActivity {

    TextView usuario, serie, club;
    ListView lista;
    private static ArrayList<String> lista_series;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_extras);

        club = (TextView) findViewById(R.id.club);
        serie= (TextView) findViewById(R.id.serie);
        usuario= (TextView) findViewById(R.id.usuario);
        lista = (ListView) findViewById(R.id.lista_serie);

        lista.setEmptyView(findViewById(R.id.mensajevacio));
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        String club_jugadores = sharedPreferences.getString("club", "");
        String series_jugadores = sharedPreferences.getString("serie","");

        usuario.setText(nombre+" (Rol "+rol+")");
        club.setText(club_jugadores);
        serie.setText(series_jugadores);

        lista_series = new ArrayList<String>();
        if(series_jugadores.equals("PRIMERA INFANTIL")){
            lista_series.add("SEGUNDA INFANTIL");
        }
        else if(series_jugadores.equals("SEGUNDA INFANTIL")){
            lista_series.add("TERCERA INFANTIL");
        }
        else if(series_jugadores.equals("JUVENIL")){
            lista_series.add("PRIMERA INFANTIL");
        }
        else if(series_jugadores.equals("PRIMERA ADULTA")){
            lista_series.add("PRIMERA INFANTIL");
            lista_series.add("SEGUNDA INFANTIL");
            lista_series.add("SENIORS");
            lista_series.add("SUPER SENIORS");
        }
        else if(series_jugadores.equals("SEGUNDA ADULTA")){
            lista_series.add("PRIMERA INFANTIL");
            lista_series.add("SENIORS");
            lista_series.add("SUPER SENIORS");
        }
        else if(series_jugadores.equals("HONOR")) {
            lista_series.add("PRIMERA INFANTIL");
            lista_series.add("SENIORS");
            lista_series.add("PRIMERA ADULTA");
            lista_series.add("SEGUNDA ADULTA");
            lista_series.add("SUPER SENIORS");
        }
        else if(series_jugadores.equals("SENIORS")){
            lista_series.add("SUPER SENIORS");
        }
        else if(series_jugadores.equals("SUPER SENIORS")){
            lista_series.add("SUPER SENIORS");
        }
        Log.d("TAG", String.valueOf(lista_series));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_series_extras, R.id.nombre_serie,lista_series);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), JugadoresExtrasActivity.class);
                intent.putExtra("listado_serie", lista_series.get(position));
                startActivity(intent);

            }
        });
    }
}
