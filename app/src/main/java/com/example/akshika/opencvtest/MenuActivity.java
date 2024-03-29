package com.example.akshika.opencvtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button enrolar, validar, qr;
    TextView usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        enrolar = (Button) findViewById(R.id.enrolar);
        validar = (Button) findViewById(R.id.validar);
        qr = (Button) findViewById(R.id.validar_qr);

        usuario = (TextView) findViewById(R.id.usuario);

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String nombre= sharedPreferences.getString("name","");
        String rol = sharedPreferences.getString("rol", "");
        usuario.setText(nombre+" (Rol "+rol+")");

        enrolar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JugadorActivity.class);
                startActivity(intent);
            }

        });

        validar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayersActivity.class);
                startActivity(intent);
            }

        });


        qr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://pelotatufe.cl/jugador/validación/code/view";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

        });


    }
}
