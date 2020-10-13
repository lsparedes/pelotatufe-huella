package com.example.akshika.opencvtest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class TurnoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemTurno> arrayList;
    private LayoutInflater layoutInflater;

    public TurnoAdapter(Context context, ArrayList<ItemTurno> arrayList){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_turno, null);
        }

        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        nombre.setText(arrayList.get(position).getNombre());

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ListadoJugadoresActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //String id_jugador = arrayList.get(position).getId();
                //intent.putExtra("id", id_jugador);
                context.startActivity(intent);
                //Toast.makeText(parent.getContext(), "hola", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
