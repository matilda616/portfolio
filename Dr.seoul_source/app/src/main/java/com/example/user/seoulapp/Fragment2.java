package com.example.user.seoulapp;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Fragment2 extends android.support.v4.app.Fragment {
    SQLiteDatabase db;
    MyHelper helper;
    ArrayList<MyItem> data = new ArrayList<MyItem>();
    ArrayList<MyItem2> data2 = new ArrayList<MyItem2>();
    MyAdapter adapter;
    MyAdapter2 adapter2;
    private ArrayList<String> mGroupList, mGroupList2 = null;
    private ArrayList<ArrayList<String>> mChildList, mChildList2 = null;
    private ArrayList<String> mChildListContent, mChildListContent2 = null;
    private ExpandableListView mListView;
    BaseExpandableAdapter p_expandableAdapter, p_expandableAdapter2;
    BaseExpandableAdapter2 h_expandableAdapter,h_expandableAdapter2;
    Button b_pharmacy,b_hospital;
    boolean isPharmacy = true;

    @Override
    public void onStart() {
        super.onStart();

        mListView = (ExpandableListView)getView().findViewById(R.id.list);

        insertBookmarkPha();

        p_expandableAdapter = new BaseExpandableAdapter(getActivity(), mGroupList, mChildList,data);
        mListView.setAdapter(p_expandableAdapter);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    //약국 북마크 세부사항입력
    public void insertBookmarkPha(){
        doDBOpen();
        adapter = new MyAdapter(getContext(), data, R.layout.item, db);
        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();


        for(int i = 0; i < data.size(); i++){
            //if(data.get(i).type.equals("pharmacy")){
                mGroupList.add(data.get(i).name_eng);

                mChildListContent = new ArrayList<String>();
                 mChildListContent.add("korea name: "+data.get(i).ko_name);
                mChildListContent.add("address: "+data.get(i).address);
                mChildListContent.add("language: "+data.get(i).language);
                mChildListContent.add("tel: "+data.get(i).tel);

                mChildList.add(mChildListContent);
        }

    }

    //병원 북마크 세부사항입력
    public void insertBookmarkHos(){
        doDBOpen();
        adapter2 = new MyAdapter2(getContext(), data2, R.layout.item, db);
        mGroupList2 = new ArrayList<String>();
        mChildList2 = new ArrayList<ArrayList<String>>();


        for(int i = 0; i < data2.size(); i++){
            //if(data.get(i).type.equals("pharmacy")){
            mGroupList2.add(data2.get(i).name_eng);

            mChildListContent2 = new ArrayList<String>();
            mChildListContent2.add("korea name: "+data2.get(i).ko_name);
            mChildListContent2.add("address: "+data2.get(i).address);
            mChildListContent2.add("type: "+data2.get(i).type);
            mChildListContent2.add("language: "+data2.get(i).language);
            mChildList2.add(mChildListContent2);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.fragment2,container,false);

        b_pharmacy = (Button)v.findViewById(R.id.b_pharmacy);
        b_pharmacy.setBackgroundColor(Color.parseColor("#2582e6"));
        b_pharmacy.setTextColor(Color.parseColor("#fff1efef"));

        b_pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBookmarkPha();

                b_pharmacy.setBackgroundColor(Color.parseColor("#2582e6"));
                b_pharmacy.setTextColor(Color.parseColor("#fff1efef"));
                b_hospital.setBackgroundColor(Color.parseColor("#4ba2ff"));
                b_hospital.setTextColor(Color.parseColor("#ffffff"));

                p_expandableAdapter = new BaseExpandableAdapter(getActivity(), mGroupList, mChildList,data);
                mListView.setAdapter(p_expandableAdapter);
            }
        });

        b_hospital = (Button)v.findViewById(R.id.b_hospital);
        b_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPharmacy = false;

                insertBookmarkHos();

                b_hospital.setBackgroundColor(Color.parseColor("#2582e6"));
                b_hospital.setTextColor(Color.parseColor("#fff1efef"));
                b_pharmacy.setBackgroundColor(Color.parseColor("#4ba2ff"));
                b_pharmacy.setTextColor(Color.parseColor("#ffffff"));

                h_expandableAdapter = new BaseExpandableAdapter2(getActivity(), mGroupList2, mChildList2,data2);
                mListView.setAdapter(h_expandableAdapter);
            }
        });
        return v;
    }

    void doDBOpen() {
        if (helper == null) {
            helper = new MyHelper(getActivity(), "MyDB.db", null, 1);
        }
        db = helper.getWritableDatabase();
    }
    void doDBClose(){
        if(db != null){
            if(db.isOpen()){
                db.close();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        doDBClose();
    }

    public class BaseExpandableAdapter extends BaseExpandableListAdapter {
        private ArrayList<String> groupList = null;
        private ArrayList<ArrayList<String>> childList = null;
        private LayoutInflater inflater = null;
        private ViewHolder viewHolder = null;
        Context context;
        ArrayList<MyItem> data;
        SQLiteDatabase db;
        MyHelper helper;

        public BaseExpandableAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList,  ArrayList<MyItem> data){
            super();
            this.context=c;
            this.inflater = LayoutInflater.from(c);
            this.groupList = groupList;
            this.childList = childList;
            this.data = data;
        }

        @Override
        public String getGroup(int groupPosition){
            return groupList.get(groupPosition);
        }

        @Override
        public int getGroupCount(){
            return groupList.size();
        }

        @Override
        public long getGroupId(int groupPosition){
            return groupPosition;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.item, parent, false);
                viewHolder.name_eng = (TextView)v.findViewById(R.id.name_eng);
                viewHolder.bookmark2 = (ImageView)v.findViewById(R.id.bookmark2);
                viewHolder.streetview = (ImageView)v.findViewById(R.id.streetview);
                viewHolder.call = (ImageView)v.findViewById(R.id.call);
                viewHolder.gotomap = (ImageView)v.findViewById(R.id.gotomap);
                v.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)v.getTag();
            }

            viewHolder.name_eng.setText(getGroup(groupPosition));
            viewHolder.bookmark2.setImageResource(R.drawable.book_delete);
            viewHolder.streetview.setImageResource(R.drawable.book_street);
            viewHolder.call.setImageResource(R.drawable.book_call);
            viewHolder.gotomap.setImageResource(R.drawable.book_pill);
            final String name_eng = getGroup(groupPosition);

            viewHolder.bookmark2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doDBOpen();
                    String sql = "delete from Bookmark_pha where name_eng_pha = '" + name_eng + "';";
                    db.execSQL(sql);
                    initData();
                    insertBookmarkPha();
                    p_expandableAdapter2 = new BaseExpandableAdapter(getActivity(), mGroupList, mChildList,data);
                    mListView.setAdapter(p_expandableAdapter2);
                    doDBClose();

                }

            }); viewHolder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String tel ="";
                    for(int i = 0; i < data.size(); i++){
                        if (name_eng.equals(data.get(i).name_eng))
                            tel = data.get(i).tel;
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:02-"+ tel));
                    startActivity(intent);
                }

            });
            viewHolder.streetview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String lat_st = "", lon_st = "";
                    Double lat, lon;
                    for(int i = 0; i < data.size(); i++){
                        if(name_eng.equals(data.get(i).name_eng) ) {
                            lat_st = data.get(i).street_lat;
                            lon_st = data.get(i).street_lon;
                        }
                    }

                    lat = Double.parseDouble(lat_st);
                    lon = Double.parseDouble(lon_st);

                    Intent intent = new Intent(getActivity(),StreetView.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat",lat);
                    bundle.putDouble("lon",lon);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }

            });
            viewHolder.gotomap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String lat_st = "", lon_st = "";
                    Double lat, lon;
                    String mainkey="";
                    for(int i = 0; i < data.size(); i++){
                        if(name_eng.equals(data.get(i).name_eng)) {
                            lat_st = data.get(i).street_lat;
                            lon_st = data.get(i).street_lon;
                            mainkey = data.get(i).mainkey;
                        }
                    }

                    lat = Double.parseDouble(lat_st);
                    lon = Double.parseDouble(lon_st);


                    //fragmsnt1이 생성 전에만 가능
                    Fragment1 fragment1 = new Fragment1();
                    Bundle bundle = new Bundle(4);
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lon", lon);
                    bundle.putString("mainkey",mainkey);
                    bundle.putString("type","pharmacy");
                    fragment1.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,fragment1);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

            });


            return v;
        }

        @Override
        public String getChild(int groupPosition, int childPosition){
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public int getChildrenCount(int groupPosition){
            return childList.get(groupPosition).size();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition){
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.item, null);
                viewHolder.type = (TextView) v.findViewById(R.id.type);
                v.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)v.getTag();
            }

            viewHolder.type.setText(getChild(groupPosition, childPosition));

            return v;
        }

        @Override
        public boolean hasStableIds(){return true;}

        @Override
        public boolean isChildSelectable(int groupPostion, int childPosition){return true;}
        void doDBOpen() {
            if (helper == null) {
                helper = new MyHelper(context, "MyDB.db", null, 1);
            }
            db = helper.getWritableDatabase();
        }
        void doDBClose(){
            if(db != null){
                if(db.isOpen()){
                    db.close();
                }
            }
        }


        void initData(){
            data.clear();
            try{
                //select * from person
                Cursor c = db.query("Bookmark_pha", null, null, null, null, null, null);
                while (c.moveToNext()){
                    int idx = c.getColumnIndex("_id");
                    int id = c.getInt(idx);
                    String mainkey = c.getString(1);
                    String name_eng = c.getString(2);
                    String type = c.getString(3);
                    String address = c.getString(4);
                    String tel = c.getString(5);
                    String language = c.getString(6);
                    String street_lat = c.getString(7);
                    String street_lon = c.getString(8);
                    String ko_name = c.getString(9);
                    data.add(new MyItem(mainkey,name_eng,type,address,tel,language,street_lat,street_lon, ko_name));
                }
                c.close();
            }catch (SQLException e){

            }
        }

        class ViewHolder{
            public ImageView bookmark2;
            public TextView name_eng;
            public TextView type;
            public ImageView streetview;
            public ImageView call;
            public ImageView gotomap;
        }
    }

    //병원
    public class BaseExpandableAdapter2 extends BaseExpandableListAdapter {
        private ArrayList<String> groupList = null;
        private ArrayList<ArrayList<String>> childList = null;
        private LayoutInflater inflater = null;
        private ViewHolder viewHolder = null;
        Context context;
        ArrayList<MyItem2> data2;
        SQLiteDatabase db;
        MyHelper helper;

        public BaseExpandableAdapter2(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList,  ArrayList<MyItem2> data){
            super();
            this.context=c;
            this.inflater = LayoutInflater.from(c);
            this.groupList = groupList;
            this.childList = childList;
            this.data2 = data;
        }

        @Override
        public String getGroup(int groupPosition){
            return groupList.get(groupPosition);
        }

        @Override
        public int getGroupCount(){
            return groupList.size();
        }

        @Override
        public long getGroupId(int groupPosition){
            return groupPosition;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.item, parent, false);
                viewHolder.name_eng = (TextView)v.findViewById(R.id.name_eng);
                viewHolder.bookmark2 = (ImageView)v.findViewById(R.id.bookmark2);
                viewHolder.streetview = (ImageView)v.findViewById(R.id.streetview);
                viewHolder.gotomap = (ImageView)v.findViewById(R.id.call);
                v.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)v.getTag();
            }

            viewHolder.name_eng.setText(getGroup(groupPosition));
            viewHolder.bookmark2.setImageResource(R.drawable.book_delete);
            viewHolder.streetview.setImageResource(R.drawable.book_street);
            viewHolder.gotomap.setImageResource(R.drawable.book_hos);
            final String name_eng = getGroup(groupPosition);
            viewHolder.bookmark2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doDBOpen();
                    String sql = "delete from Bookmark_hos where name_eng_hos = '" + name_eng + "';";
                    db.execSQL(sql);
                    initData();
                    insertBookmarkHos();
                    h_expandableAdapter2 = new BaseExpandableAdapter2(getActivity(), mGroupList2, mChildList2,data2);
                    mListView.setAdapter(h_expandableAdapter2);
                    doDBClose();

                }

            });
            viewHolder.streetview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String lat_st = "", lon_st = "";
                    Double lat, lon;
                    for(int i = 0; i < data2.size(); i++){
                        if(name_eng.equals(data2.get(i).name_eng) ) {
                            lat_st = data2.get(i).street_lat;
                            lon_st = data2.get(i).street_lon;
                        }
                    }

                    lat = Double.parseDouble(lat_st);
                    lon = Double.parseDouble(lon_st);

                    Intent intent = new Intent(getActivity(),StreetView.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat",lat);
                    bundle.putDouble("lon",lon);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }

            });
            viewHolder.gotomap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String lat_st = "", lon_st = "";
                    Double lat, lon;
                    String mainkey = "";
                    for(int i = 0; i < data2.size(); i++){
                        if(name_eng.equals(data2.get(i).name_eng)) {
                            lat_st = data2.get(i).street_lat;
                            lon_st = data2.get(i).street_lon;
                            mainkey = data2.get(i).mainkey;
                        }
                    }
                    lat = Double.parseDouble(lat_st);
                    lon = Double.parseDouble(lon_st);



                    //fragmsnt1이 생성 전에만 가능
                    Fragment1 fragment1 = new Fragment1();
                    Bundle bundle = new Bundle(4);
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lon", lon);
                    bundle.putString("mainkey",mainkey);
                    bundle.putString("type","hospital");
                    fragment1.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container,fragment1);
                    fragmentTransaction.commit();
                }

            });

            return v;
        }

        @Override
        public String getChild(int groupPosition, int childPosition){
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public int getChildrenCount(int groupPosition){
            return childList.get(groupPosition).size();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition){
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.item, null);
                viewHolder.type = (TextView) v.findViewById(R.id.type);
                v.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)v.getTag();
            }

            viewHolder.type.setText(getChild(groupPosition, childPosition));

            return v;
        }

        @Override
        public boolean hasStableIds(){return true;}

        @Override
        public boolean isChildSelectable(int groupPostion, int childPosition){return true;}
        void doDBOpen() {
            if (helper == null) {
                helper = new MyHelper(context, "MyDB.db", null, 1);
            }
            db = helper.getWritableDatabase();
        }
        void doDBClose(){
            if(db != null){
                if(db.isOpen()){
                    db.close();
                }
            }
        }


        void initData(){
            data2.clear();
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
                    String ko_name = c.getString(8);

                    data2.add(new MyItem2(mainkey,name_eng,type,address,language,street_lat,street_lon, ko_name));
                }
                c.close();
            }catch (SQLException e){

            }
        }

        class ViewHolder{
            public ImageView bookmark2;
            public TextView name_eng;
            public TextView type;
            public ImageView streetview;
            public ImageView gotomap;
        }

    }
}