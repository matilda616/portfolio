package com.example.user.seoulapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by LG on 2016-05-31.
 */
public class MyAdapter2 extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<MyItem2> data;
    SQLiteDatabase db;


    public MyAdapter2(Context context, ArrayList<MyItem2> data, int layout, SQLiteDatabase db) {
        this.context = context;
        this.data = data;
        this.layout = layout;
        this.db = db;
        data = new ArrayList<MyItem2>();
        initData();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        TextView _id;
        TextView name_eng;
        TextView type;
        TextView address;
        TextView language;
        ImageView bookmark2;
        TextView ko_name;
        TextView mainkey;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = View.inflate(context, layout, null);
            holder = new ViewHolder();
            holder.name_eng = (TextView)convertView.findViewById(R.id.name_eng);
            holder.type = (TextView)convertView.findViewById(R.id.type);
            holder.address = (TextView)convertView.findViewById(R.id.address);
            holder.language = (TextView)convertView.findViewById(R.id.language);
            holder.bookmark2 = (ImageView)convertView.findViewById(R.id.bookmark2);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        final MyItem2 item = data.get(position);

        holder.name_eng.setText(item.name_eng);
        holder.type.setText(item.type);
        holder.address.setText(item.address);
        holder.language.setText(item.language);
        holder.ko_name.setText(item.ko_name);
        return convertView;
    }

    void initData(){
        data.clear();
        try{
            //select * from person
            Cursor c = db.query("Bookmark_hos", null, null, null, null, null, null);
            while (c.moveToNext()){
                int idx = c.getColumnIndex("_id");
                int id = c.getInt(idx);
                String mainkey = c.getString(1);
                String name_eng = c.getString(2);
                String type = c.getString(3);
                String address = c.getString(4);
                String language = c.getString(5);
                String street_lat = c.getString(6);
                String street_lon = c.getString(7);
                String ko_name  = c.getString(8);

                data.add(new MyItem2(mainkey,name_eng,type,address,language,street_lat, street_lon,ko_name));
            }
            c.close();
        }catch (SQLException e){

        }
    }
}
