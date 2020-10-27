package com.example.akshika.opencvtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CamisetaActivity extends AppCompatActivity {

    private static final int SCAN_FINGER = 0;
    Button siguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_camiseta);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        siguiente = (Button) findViewById(R.id.siguiente);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, R.layout.item_camiseta);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn,
                                               android.view.View v,
                                               int posicion,
                                               long id) {
                       /* Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString(),
                                Toast.LENGTH_LONG).show();*/

                        SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("numero_camiseta", spn.getItemAtPosition(posicion).toString());
                        editor.apply();


                    }
                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

        siguiente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity3.class);
                intent.putExtra("SCAN_FINGER", SCAN_FINGER);
                startActivity(intent);
            }

        });


    }
}
