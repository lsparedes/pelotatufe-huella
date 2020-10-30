package com.example.akshika.opencvtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import asia.kanopi.fingerscan.Status;

public class CamisetaActivityExtra extends AppCompatActivity {

    private static final int SCAN_FINGER = 0;
    Button siguiente;
    //private static final String TAG = "OCVSample::Activity";
    //byte[] img;

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
                                               View v,
                                               int posicion,
                                               long id) {
                       /* Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString(),
                                Toast.LENGTH_LONG).show();*/

                        SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("camiseta_extra", spn.getItemAtPosition(posicion).toString());
                        editor.apply();


                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

        siguiente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ScanActivity2.class);
                startActivityForResult(intent, SCAN_FINGER);


            }

        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int status;
        String errorMesssage;
        switch (requestCode) {
            case (SCAN_FINGER): {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        Intent intent = new Intent(getApplicationContext(), JugadoresExtrasActivity.class);
                        startActivity(intent);
                        //Toast.makeText(CamisetaActivityExtra.this, "Hola me leyo la huellita bien :)", Toast.LENGTH_LONG).show();

 //                       img = data.getByteArrayExtra("img");
    //                    Log.d(TAG, "esto vale img: "+img);
//                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);

//                        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
//                        byte[] buffer = bmpUtil.convertToBmp24bit(img);
//
 //                       imagen2 = Base64.encodeToString(img, Base64.DEFAULT);
  //                      Log.d(TAG, "esto vale imagen2: "+imagen2);
                        //ivFinger.setImageBitmap(bm);
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        //Toast.makeText(MainActivity.this, "Rayos, mamá ño", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }
}
