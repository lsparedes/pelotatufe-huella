package com.example.akshika.opencvtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class PlayersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemPlayers> arrayList;
    private LayoutInflater layoutInflater;
    Random random;

    public PlayersAdapter(Context context, ArrayList<ItemPlayers> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        random = new Random();
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
            convertView = layoutInflater.inflate(R.layout.item_players, null);
        }

        //ImageView iv_main = (ImageView) convertView.findViewById(R.id.iv_main);
        TextView nombre = (TextView) convertView.findViewById(R.id.campeonato);
        TextView serie = (TextView) convertView.findViewById(R.id.serie);
        TextView fecha = (TextView) convertView.findViewById(R.id.fecha);
        TextView id = (TextView) convertView.findViewById(R.id.id_campeonato);
        //ImageView tv_main = (ImageView) convertView.findViewById(R.id.tv_main);

            serie.setText(arrayList.get(position).getSerie());
            nombre.setText(arrayList.get(position).getCampeonato());
            fecha.setText(arrayList.get(position).getFecha());

        //iv_main.setImageResource(R.drawable.circle);
        //int color = Color.argb(255, random.nextInt(250), random.nextInt(250), random.nextInt(250));
        //int color = Color.argb(255, 0,0,0);
        //iv_main.setColorFilter(color);
        //tv_main.setImageResource(R.drawable.ic_baseline_sports_handball_24);

       convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,TurnoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String id_campeonato = arrayList.get(position).getIdCampeonato();
                intent.putExtra("id_campeonato", id_campeonato);
                context.startActivity(intent);

            }
        });

        return convertView;
    }
}
