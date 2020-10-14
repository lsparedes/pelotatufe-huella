package com.example.akshika.opencvtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class ListadoJugadoresAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemListadoJugadores> arrayList;
    private LayoutInflater layoutInflater;
    private static final int SCAN_FINGER = 0;


    public ListadoJugadoresAdapter(Context context, ArrayList<ItemListadoJugadores> arrayList){
        this.context = context;
        this.arrayList = arrayList;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_listado_jugadores, null);
        }

        //ImageView iv_main = (ImageView) convertView.findViewById(R.id.iv_main);
        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        //TextView id = (TextView) convertView.findViewById(R.id.id);
        //ImageView tv_main = (ImageView) convertView.findViewById(R.id.tv_main);

        nombre.setText(arrayList.get(position).getNombre());
        //id.setText(arrayList.get(position).getId());

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ScanActivity3.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
                intent.putExtra("SCAN_FINGER", SCAN_FINGER);
                SharedPreferences sharedPref = context.getSharedPreferences("myKey", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("id_scan", arrayList.get(position).getId());
                editor.putString("fingerprint",arrayList.get(position).getFingerprint());
                editor.apply();
                context.startActivity(intent);

            }
        });

        return convertView;
    }
}
