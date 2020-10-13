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

public class ListadoJugadoresAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemListadoJugadores> arrayList;
    private LayoutInflater layoutInflater;


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
        //ImageView tv_main = (ImageView) convertView.findViewById(R.id.tv_main);

        nombre.setText(arrayList.get(position).getNombre());
        //iv_main.setImageResource(R.drawable.circle);
        //int color = Color.argb(255, random.nextInt(250), random.nextInt(250), random.nextInt(250));
        //int color = Color.argb(255, 0,0,0);
        //iv_main.setColorFilter(color);
        //tv_main.setImageResource(R.drawable.ic_baseline_sports_handball_24);

        return convertView;
    }
}
