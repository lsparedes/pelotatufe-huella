package com.example.akshika.opencvtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class PrincipalActivity extends AppCompatActivity {

    Button entrar;
    private static TextInputEditText rut_turno;
    private static String valortext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        entrar = (Button) findViewById(R.id.entrar);
        rut_turno = (TextInputEditText) findViewById(R.id.rut);

        rut_turno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(rut_turno.getText().length() == 0){
                    //siguiente.setEnabled(false);

                    //siguiente.setVisibility(View.INVISIBLE);

                }else{

                    //siguiente.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valortext = rut_turno.getText().toString().trim();
                if(valortext.equals(""))
                {
                    valortext = "0";
                }
                else
                {
                    if(rut_turno.getText().length() >= 8){
                        valortext = FormatearRUT(valortext); //Sustituyes por la funcion que te formateara el rut
                        Log.d("TAG","FORMATEADO: "+valortext);


                    }else{
                        //entrar.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(rut_turno.getText().length() >= 8){
                    //siguiente.setEnabled(false);
                    //siguiente.setVisibility(View.VISIBLE);
                    entrar.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            IngresoTurno();
                        }

                    });
                }else{
                    Log.d("TAG","largo"+rut_turno.getText().length());

                }
            }
        });


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

    public void IngresoTurno(){
        final ProgressDialog loading = new ProgressDialog(PrincipalActivity.this);
        loading.setMessage("Espere un momento...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {

            object.put("rut",valortext);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://pelotatufe.cl/api/v1/players/verified/turno", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String success = response.getString("success");
                            loading.dismiss();
                            if(success == "false"){
                                Toast.makeText(getApplicationContext(), "El usuario no existe.", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                JSONObject turno = response.getJSONObject("turno");
                                String id = turno.getString("id");
                                String name = turno.getString("name");
                                String rol = turno.getString("rol");
                                SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("name", name);
                                editor.putString("rol",rol);
                                editor.putString("id",id);
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                startActivity(intent);
                            }
                            Log.i("success", success);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(PrincipalActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
}
