package com.example.user.seoulapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends android.support.v4.app.Fragment implements  MapView.POIItemEventListener, LocationListener {
    LocationManager locationManager;


    MapView mapView;
    MapPointBounds mapPointBounds;

    ArrayList<pharmacyEng> pharmacyItem = new ArrayList<pharmacyEng>();
    ArrayList<hospitalEng> hospitalItem = new ArrayList<hospitalEng>();

    getPharmLan getPharmLan;
    double culat = 0;
    double culon = 0;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    double lat = 0;
    double lon = 0;
    double street_lat = 0;
    double street_lon = 0;
    String provider;

    String pharm_name;
    String go_name;
    String hospital_name;
    boolean open = false;
    Button b_pharmacy, b_hospital;
    LinearLayout linearlayout;

    ImageView handle;
    String mapType = "pharmacy";
    String select = "Pharmacy";
    String gotoType = "pharmacy";
    ImageView b_current;

    SQLiteDatabase db;
    MyHelper helper;

    boolean isBookmark = false;

    String p_name, address2, tel2, language2, type, mainkey, ko_name="";

    Integer int_key, go_key;

    String popup_item;

    MapPOIItem poiItem;

    boolean locationTag = true;
    boolean backgroudLock;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우
            //최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 사용자가 임의로 권한을 취소시킨 경우
                // 권한 재요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }else {

            //배열에 약국, 병원정보 저장
            pharmacyJsonParsing();
            hopitalJsonParsing();
            getPharmLan = new getPharmLan();
            getPharmLan.execute();
            setHasOptionsMenu(true);

            //db open
            doDBOpen();

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        final MenuItem item2 = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner)item2.getActionView();
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext() ,R.array.select,
                R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setPrompt("Select");
        spinner.setSelection(0);
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select = adapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                select = "Pharmacy";
            }
        });
        searchView.setQueryHint("enter dong");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int a = 0;
                //병원 검색
                if (select.equals("Hospital")) {
                    mapView.removeAllPOIItems();


                    mapType = "hospital";
                    for (int i = 0; i < hospitalItem.size(); i++) {
                        if (hospitalItem.get(i).getH_ENG_DONG().matches(".*(?i)" + query + ".*")) {
                            double lat = hospitalItem.get(i).getLat();
                            double lon = hospitalItem.get(i).getLon();
                            String name = hospitalItem.get(i).getNAME_ENG();
                            String mainkey = hospitalItem.get(i).getMAIN_KEY();
                            int key = Integer.parseInt(mainkey.substring(mainkey.length()-4,mainkey.length()));
                            MapPOIItem poiItem = new MapPOIItem();
                            poiItem.setItemName(name);
                            poiItem.setTag(key);
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
                            poiItem.setMapPoint(mapPoint);
                            mapPointBounds.add(mapPoint);
                            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                            poiItem.setCustomImageResourceId(R.drawable.ic_hospital_green);
                            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                            poiItem.setCustomSelectedImageResourceId(R.drawable.ic_hospital_red);
                            poiItem.setCustomImageAutoscale(false);
                            poiItem.setCustomImageAnchor(0.5f, 1.0f);

                            mapView.addPOIItem(poiItem);
                            mapView.selectPOIItem(poiItem, true);

                            a = i;
                        }
                    }
                }

                //약국 검색
                if (select.equals("Pharmacy")) {
                    mapType = "pharmacy";
                    mapView.removeAllPOIItems();
                    for (int i = 0; i < pharmacyItem.size(); i++) {
                        if (pharmacyItem.get(i).getH_ENG_DONG().matches(".*(?i)" + query + ".*")) {

                            double lat = pharmacyItem.get(i).getLat();
                            double lon = pharmacyItem.get(i).getLon();
                            String name = pharmacyItem.get(i).getNAME_ENG();
                            String mainkey = pharmacyItem.get(i).getMAIN_KEY();
                            int key = Integer.parseInt(mainkey.substring(mainkey.length()-4,mainkey.length()));

                            MapPOIItem poiItem = new MapPOIItem();
                            poiItem.setItemName(name);
                            poiItem.setTag(key);
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
                            poiItem.setMapPoint(mapPoint);
                            mapPointBounds.add(mapPoint);
                            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                            poiItem.setCustomImageResourceId(R.drawable.ic_pill_blue);
                            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                            poiItem.setCustomSelectedImageResourceId(R.drawable.ic_pill_red);
                            poiItem.setCustomImageAutoscale(false);
                            poiItem.setCustomImageAnchor(0.5f, 1.0f);

                            mapView.addPOIItem(poiItem);
                            mapView.selectPOIItem(poiItem, true);
                            a = i;
                        }
                    }
                }

                //지도 카메라 검색 지점으로 이동
                if (select.equals("Pharmacy")) {
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(pharmacyItem.get(a).getLat(), pharmacyItem.get(a).getLon()), true);
                } else if (select.equals("Hospital")) {
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(hospitalItem.get(a).getLat(), hospitalItem.get(a).getLon()), true);
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInistanceState) {
        // Inflate the layout for this fragment0
        View view = inflater.inflate(R.layout.fragment1, container, false);

        //gps 권한 체크

        //지도 부분
        mapView = new MapView(getActivity());
        mapView.setDaumMapApiKey("1f29294b860e7c404134f64b52024e83");
        mapView.setPOIItemEventListener(this);
        mapView.setZoomLevel(3, true);

        RelativeLayout map = (RelativeLayout) view.findViewById(R.id.map_view);

        map.addView(mapView);


//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapPointBounds = new MapPointBounds();


        //current position
        b_current = (ImageView) view.findViewById(R.id.gps);
        b_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

                try {
                    locationTag = true;
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    // default
                    Criteria criteria = new Criteria();

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Fragment1.this);
                        Location location = getLastKnownLocation();

                        if (location != null)
                            onLocationChanged(location);


                    } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Fragment1.this);
                        Location location = getLastKnownLocation();

                        if (location != null)
                            onLocationChanged(location);
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Fragment1.this);
                        Location location =getLastKnownLocation();

                        if (location != null)
                            onLocationChanged(location);
                    }

                } catch (SecurityException e) {
                    Toast.makeText(getActivity(), "gps error" + e, Toast.LENGTH_LONG).show();
                }
            }
        });

        //zoom in
        ImageView b_zoom_in = (ImageView) view.findViewById(R.id.zoomin);
        b_zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomIn(true);
            }
        });

        //zoom out
        ImageView b_zoom_out = (ImageView) view.findViewById(R.id.zoomout);
        b_zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomOut(true);
            }
        });


        //상단바 click
        final LinearLayout list_linear = (LinearLayout) view.findViewById(R.id.list_linear);

        b_pharmacy = (Button) view.findViewById(R.id.pharmacy);
        b_hospital = (Button) view.findViewById(R.id.hospital);

        b_pharmacy.setBackgroundColor(Color.parseColor("#2582e6"));
        b_pharmacy.setTextColor(Color.parseColor("#fff1efef"));
        //상단바 pharmacy 선택
        b_pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (culat == 0)
                    Toast.makeText(getActivity(), "click gps button...", Toast.LENGTH_LONG).show();

                b_pharmacy.setBackgroundColor(Color.parseColor("#2582e6"));
                b_pharmacy.setTextColor(Color.parseColor("#fff1efef"));

                b_hospital.setBackgroundColor(Color.parseColor("#4ba2ff"));
                b_hospital.setTextColor(Color.parseColor("#ffffff"));

                mapType = "pharmacy";
                mapView.removeAllPOIItems();

                if(culat!=0) {
                    MapPOIItem marker_poiItem = new MapPOIItem();
                    marker_poiItem.setItemName("current position");
                    MapPoint markerPoint = MapPoint.mapPointWithGeoCoord(culat, culon);
                    marker_poiItem.setMapPoint(markerPoint);
                    mapPointBounds.add(markerPoint);
                    marker_poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    marker_poiItem.setCustomImageResourceId(R.drawable.current_point);
                    marker_poiItem.setTag(1);
                    mapView.addPOIItem(marker_poiItem);
                    mapView.deselectPOIItem(marker_poiItem);
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(culat, culon), true);
                }

                for (int i = 0; i < pharmacyItem.size(); i++) {
                    double distance = getDistance(culat, culon, pharmacyItem.get(i).getLat(), pharmacyItem.get(i).getLon());

                    //거리가 2km 이내
                    if (distance <= 2.0) {
                        lat = pharmacyItem.get(i).getLat();
                        lon = pharmacyItem.get(i).getLon();
                        pharm_name = pharmacyItem.get(i).getNAME_ENG();
                        String temp_key = pharmacyItem.get(i).getMAIN_KEY();
                        String key = temp_key.substring(temp_key.length()-4,temp_key.length());
                        int_key = Integer.parseInt(key);

                        MapPOIItem poiItem = new MapPOIItem();
                        poiItem.setItemName(pharm_name);
                        poiItem.setTag(i);
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
                        poiItem.setMapPoint(mapPoint);
                        mapPointBounds.add(mapPoint);
                        poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        poiItem.setCustomImageResourceId(R.drawable.ic_pill_blue);
                        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                        poiItem.setCustomSelectedImageResourceId(R.drawable.ic_pill_red);
                        poiItem.setCustomImageAutoscale(false);
                        poiItem.setCustomImageAnchor(0.5f, 1.0f);
                        poiItem.setTag(int_key);

                        mapView.addPOIItem(poiItem);
                        mapView.selectPOIItem(poiItem, true);

                    }
                }


            }
        });

        //상단바 hospital 클릭
        b_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                b_hospital.setBackgroundColor(Color.parseColor("#2582e6"));
                b_hospital.setTextColor(Color.parseColor("#fff1efef"));

                b_pharmacy.setTextColor(Color.parseColor("#ffffff"));
                b_pharmacy.setBackgroundColor(Color.parseColor("#4ba2ff"));

                mapType = "hospital";

                PopupMenu popup = new PopupMenu(getContext(), v);

                MenuInflater inflater = popup.getMenuInflater();
                Menu menu = popup.getMenu();

                inflater.inflate(R.menu.menu_hospital, menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (culat == 0)
                                Toast.makeText(getActivity(), "click gps button...", Toast.LENGTH_LONG).show();
                            else {
                                switch (item.getItemId()) {

                                    case R.id.general:
                                        popup_item = "general";
                                        nearbyHospitalsearch("general");
                                        break;
                                    case R.id.dermatology:
                                        popup_item = "dermatology";
                                        nearbyHospitalsearch("dermatology");
                                        break;
                                    case R.id.dental:
                                        popup_item = "dental";
                                        nearbyHospitalsearch("dental");
                                        break;
                                    case R.id.ophthalmology:
                                        popup_item = "ophthalmology";
                                        nearbyHospitalsearch("ophthalmology");
                                        break;
                                    case R.id.oriental:
                                        popup_item = "oriental";
                                        nearbyHospitalsearch("oriental");
                                        break;
                                    case R.id.internal:
                                        popup_item = "internal";
                                        nearbyHospitalsearch("internal");
                                        break;
                                    case R.id.urology:
                                        popup_item = "urology";
                                        nearbyHospitalsearch("urology");
                                        break;
                                    case R.id.surgery:
                                        popup_item = "surgery";
                                        nearbyHospitalsearch("surgery");
                                        break;
                                    case R.id.rehabilitation:
                                        popup_item = "rehabilitation";
                                        nearbyHospitalsearch("rehabilitation");
                                        break;
                                    case R.id.obstetrics:
                                        popup_item = "obstetrics";
                                        nearbyHospitalsearch("obstetrics");
                                        break;
                                    case R.id.children:
                                        popup_item = "children";
                                        nearbyHospitalsearch("children");
                                        break;
                                    case R.id.plastic:
                                        popup_item = "plastic";
                                        nearbyHospitalsearch("plastic");
                                        break;
                                }
                            }
                            return false;
                        }
                    });
                    popup.show();

            }
        });

        return view;
    }

    //hospital 마커찍기
    public void nearbyHospitalsearch(String searchtype){

        mapView.removeAllPOIItems();

        if(culat!=0) {
            MapPOIItem marker_poiItem = new MapPOIItem();
            marker_poiItem.setItemName("current position");
            MapPoint markerPoint = MapPoint.mapPointWithGeoCoord(culat, culon);
            marker_poiItem.setMapPoint(markerPoint);
            mapPointBounds.add(markerPoint);
            marker_poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker_poiItem.setCustomImageResourceId(R.drawable.current_point);
            marker_poiItem.setTag(1);
            mapView.addPOIItem(marker_poiItem);
            mapView.deselectPOIItem(marker_poiItem);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(culat, culon), true);
        }

        for (int i = 0; i < hospitalItem.size(); i++) {
            String type = hospitalItem.get(i).getType();

            if (type.toString().equals(searchtype)) {
                lat = hospitalItem.get(i).getLat();
                lon = hospitalItem.get(i).getLon();

                double distance = getDistance(culat, culon, lat, lon);

                if (distance <= 2.0) {
                    hospital_name = hospitalItem.get(i).getNAME_ENG();

                    String temp_key = hospitalItem.get(i).getMAIN_KEY();
                    String key = temp_key.substring(temp_key.length()-4,temp_key.length());
                    int_key = Integer.parseInt(key);

                    MapPOIItem poiItem = new MapPOIItem();
                    poiItem.setItemName(hospital_name);
                    poiItem.setTag(i);
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
                    poiItem.setMapPoint(mapPoint);
                    mapPointBounds.add(mapPoint);
                    poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    poiItem.setCustomImageResourceId(R.drawable.ic_hospital_green);
                    poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                    poiItem.setCustomSelectedImageResourceId(R.drawable.ic_hospital_red);
                    poiItem.setCustomImageAutoscale(false);
                    poiItem.setCustomImageAnchor(0.5f, 1.0f);
                    poiItem.setTag(int_key);

                    mapView.addPOIItem(poiItem);
                    mapView.selectPOIItem(poiItem, true);

                }
            }
        }
    }



    //현위치
    @Override
    public void onLocationChanged(Location location) {
        //위치정보 한번만 받아오도록(업데이트 금지)
        if(locationTag){
            culat = location.getLatitude();
            culon = location.getLongitude();

            new getMarkerPoint().execute();
            Log.d("current position", culat + " " + culon);

            locationTag=false;
        }
    }

    //현위치를 중심으로 2km이내 약국 마커찍기
    public void setMarkerPoint(){
        if(mapType.equals("pharmacy")){
            for(int i=0;i<pharmacyItem.size();i++){
                double distance = getDistance(culat,culon,pharmacyItem.get(i).getLat(),pharmacyItem.get(i).getLon());

                //거리가 2km 이내
                if(distance<=2.0){
                    lat = pharmacyItem.get(i).getLat();
                    lon = pharmacyItem.get(i).getLon();
                    pharm_name = pharmacyItem.get(i).getNAME_ENG();

                    String temp_key =pharmacyItem.get(i).getMAIN_KEY();
                    String key = temp_key.substring(temp_key.length()-4,temp_key.length());
                    int_key = Integer.parseInt(key);

                    poiItem = new MapPOIItem();
                    poiItem.setItemName(pharm_name);
                    poiItem.setTag(i);
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
                    poiItem.setMapPoint(mapPoint);
                    mapPointBounds.add(mapPoint);
                    poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    poiItem.setCustomImageResourceId(R.drawable.ic_pill_blue);
                    poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                    poiItem.setCustomSelectedImageResourceId(R.drawable.ic_pill_red);
                    poiItem.setCustomImageAutoscale(false);
                    poiItem.setCustomImageAnchor(0.5f, 1.0f);
                    poiItem.setTag(int_key);

                    mapView.addPOIItem(poiItem);
                    mapView.selectPOIItem(poiItem, true);

                }
            }

        }

        if(mapType.equals("hospital")){
            nearbyHospitalsearch(popup_item);
        }


    }

    public class getMarkerPoint extends AsyncTask<Void, Integer, Void>{
        ProgressDialog asyncDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("please wait....");
            asyncDialog.setCanceledOnTouchOutside(false);
            // show dialog
            asyncDialog.show();

            mapView.removeAllPOIItems();

            MapPOIItem marker_poiItem = new MapPOIItem();
            marker_poiItem.setItemName("current position");
            MapPoint markerPoint = MapPoint.mapPointWithGeoCoord(culat,culon);
            marker_poiItem.setMapPoint(markerPoint);
            mapPointBounds.add(markerPoint);
            marker_poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker_poiItem.setCustomImageResourceId(R.drawable.current_point);
            marker_poiItem.setTag(1);
            mapView.addPOIItem(marker_poiItem);
            mapView.deselectPOIItem(marker_poiItem);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(culat, culon), true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
             super.onPostExecute(result);
    }

        @Override
        protected Void doInBackground(Void... params) {
            setMarkerPoint();
            return null;
        }
    }


    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getActivity(), "Gps is turned off!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    //마커 click 이벤트
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        SlidingDrawer slidingDrawer = (SlidingDrawer)getActivity().findViewById(R.id.slide);
        TextView name = (TextView)getActivity().findViewById(R.id.name);
        TextView t_name = (TextView)getActivity().findViewById(R.id.t_name);
        TextView address = (TextView)getActivity().findViewById(R.id.address);
        TextView tel = (TextView)getActivity().findViewById(R.id.tel);
        TextView language = (TextView)getActivity().findViewById(R.id.language);
        final ImageView call = (ImageView)getActivity().findViewById(R.id.b_call);
        final ImageView street = (ImageView)getActivity().findViewById(R.id.b_street);
        final ImageView bookmark = (ImageView)getActivity().findViewById(R.id.bookmark);
        slidingDrawer.setVisibility(View.VISIBLE);
        slidingDrawer.open();
        p_name = mapPOIItem.getItemName();


        int tag = mapPOIItem.getTag();
        if(tag==1){
            slidingDrawer.setVisibility(View.GONE);
        }
        handle = (ImageView)getActivity().findViewById(R.id.handle);

        //즐겨찾기 여부 확인 및 표시
        isBookmark = searchData(p_name);
        if(isBookmark){
            bookmark.setImageResource(R.drawable.star_in);
        }else{
            bookmark.setImageResource(R.drawable.star_out);
        }

        if(mapType.equals("pharmacy")){
            handle.setImageResource(R.drawable.h_pharmacy);
            //for문 빼고 pharmacyItem.get(tag).~~~
            for(int i=0;i<pharmacyItem.size();i++){
                String temp_key = pharmacyItem.get(i).getMAIN_KEY();
                int key =Integer.parseInt(temp_key.substring(temp_key.length()-4,temp_key.length()));
                if(key==tag){
                    ko_name = pharmacyItem.get(i).getKo_name();
                    mainkey = pharmacyItem.get(i).getMAIN_KEY();
                    address2 = pharmacyItem.get(i).getH_ENG_CITY()+" "+pharmacyItem.get(i).getH_ENG_GU()+" "+pharmacyItem.get(i).getH_ENG_DONG();
                    tel2 = pharmacyItem.get(i).getTEL();
                    language2 = pharmacyItem.get(i).getLan();
                    type = "pharmacy";
                    street_lat = pharmacyItem.get(i).getLat();
                    street_lon = pharmacyItem.get(i).getLon();

                /*    if (p_name.length() > 10){

                        t_name.setText(p_name+" \n("+ko_name+")");
                    }else{
                        t_name.setText(p_name+" ("+ko_name+")");
                    }*/

                    t_name.setText(p_name+" ("+ko_name+")");
                    name.setText("pharmacy name: "+p_name);
                    address.setText("address: "+address2);
                    tel.setText("tel: "+tel2);
                    language.setText("language :"+language2);


                    call.setVisibility(View.VISIBLE);
                    call.setImageResource(R.drawable.call);
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(tel2.equals(null)){
                                Toast.makeText(getActivity(),"phone number is not exist",Toast.LENGTH_LONG).show();
                            }else{
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:02-"+tel2));
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        }

        if(mapType.equals("hospital")){
            call.setVisibility(View.GONE);
            handle.setImageResource(R.drawable.h_hospital);
            for(int i=0;i<hospitalItem.size();i++){
                String temp_key = hospitalItem.get(i).getMAIN_KEY();
                int key =Integer.parseInt(temp_key.substring(temp_key.length()-4,temp_key.length()));
                if(key==tag){
                    ko_name = hospitalItem.get(i).getKo_name();
                    mainkey = hospitalItem.get(i).getMAIN_KEY();
                    address2 = hospitalItem.get(i).getH_ENG_CITY()+" "+hospitalItem.get(i).getH_ENG_GU()+" "+hospitalItem.get(i).getH_ENG_DONG();
                    language2 = hospitalItem.get(i).getLan();
                    type = hospitalItem.get(i).getType();
                    street_lat = hospitalItem.get(i).getLat();
                    street_lon = hospitalItem.get(i).getLon();

                    t_name.setText(p_name+" ("+ko_name+")");
                    name.setText("hospital name: "+p_name);
                    address.setText("address: "+address2);
                    tel.setText("type: "+type);    //병원 타입
                    language.setText("language :"+language2);
                }
            }

        }

        street.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StreetView.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat",street_lat);
                bundle.putDouble("lon",street_lon);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doDBOpen();
                if (mapType.equals("pharmacy")) {
                    if (!isBookmark) {
                        isBookmark = true;
                        String sql = "insert into Bookmark_pha (mainkey, name_eng_pha, type_pha, address_pha, tel_pha, language_pha, street_lat, street_lon, ko_name) values ('" + mainkey+"', '"+p_name + "', '" + type + "', '" + address2
                                + "', '" + tel2 + "', '" + language2 +"', '"+ street_lat + "', '" + street_lon + "', '"+ko_name+"');";
                        db.execSQL(sql);
                        bookmark.setImageResource(R.drawable.star_in);
                        Toast.makeText(getContext(), "bookmark save", Toast.LENGTH_LONG).show();
                        doDBClose();
                    } else {
                        isBookmark = false;
                        String sql = "delete from Bookmark_pha where name_eng_pha = '" + p_name + "';";
                        db.execSQL(sql);
                        bookmark.setImageResource(R.drawable.star_out);
                        Toast.makeText(getContext(), "bookmark delete", Toast.LENGTH_LONG).show();
                        doDBClose();
                    }

                }else if(mapType.equals("hospital")){
                    if (!isBookmark) {
                        isBookmark = true;

                        String sql = "insert into Bookmark_hos (mainkey, name_eng_hos, type_hos, address_hos, language_hos, street_lat, street_lon, ko_name) values ('" + mainkey+"', '"+p_name + "', '" + type + "', '" + address2
                                + "', '"  + language2 + "', '" + street_lat +"', '" +street_lon +"', '"+ko_name+"');";
                        db.execSQL(sql);
                        bookmark.setImageResource(R.drawable.star_in);
                        Toast.makeText(getContext(), "bookmark save", Toast.LENGTH_LONG).show();
                        doDBClose();
                    } else {
                        isBookmark = false;
                        String sql = "delete from Bookmark_hos where name_eng_hos = '" + p_name + "';";
                        db.execSQL(sql);
                        bookmark.setImageResource(R.drawable.star_out);
                        Toast.makeText(getContext(), "bookmark delete", Toast.LENGTH_LONG).show();
                        doDBClose();
                    }
                }


            }
        });
    }

    //즐겨찾기 여부 확인
    public boolean searchData(String name){
        doDBOpen();
        String sql="";
        if(mapType.equals("pharmacy")){
            sql = "select name_eng_pha from Bookmark_pha where name_eng_pha = '"+name+"';";
        }else if(mapType.equals("hospital")){
            sql = "select name_eng_hos from Bookmark_hos where name_eng_hos = '"+name+"';";
        }
        Cursor c = db.rawQuery(sql, null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        while (c.moveToNext()){
            c.close();
            doDBClose();
            return true;
        }
        c.close();
        doDBClose();
        return false;
    }


    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    //거리구하기 공식(km단위)
    public Double getDistance(Double startPointLon, Double startPointLat, Double endPointLon, Double endPointLat){
        double d2r = Math.PI / 180;
        double dStartPointLon = startPointLon;
        double dStartPointLat = startPointLat;
        double dEndPointLon =   endPointLon;
        double dEndPointLat =   endPointLat;

        double dLon = (dEndPointLon - dStartPointLon) * d2r;
        double dLat = (dEndPointLat - dStartPointLat) * d2r;

        double a = Math.pow(Math.sin(dLat / 2.0), 2)
                + Math.cos(dStartPointLat * d2r)
                * Math.cos(dEndPointLat * d2r)
                * Math.pow(Math.sin(dLon / 2.0), 2);

        double c = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 2;

        double distance = c * 6378;


        return distance;
    }


    //약국정보를 pharmacyItem 배열에 넣음
    public void pharmacyJsonParsing(){
        String file = "pharmacy_eng.json";
        String result = "";


        try {
            InputStream is = getResources().getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "utf-8");

            JSONObject json = new JSONObject(result);
            JSONArray jArr = json.getJSONArray("DATA");


            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);

                String H_ENG_CITY = json.getString("H_ENG_CITY");
                String H_ENG_DONG = json.getString("H_ENG_DONG");
                String NAME_ENG = json.getString("NAME_ENG");
                String MAIN_KEY = json.getString("MAIN_KEY");
                String TEL = json.getString("TEL");
                String H_ENG_GU = json.getString("H_ENG_GU");
                double lat = json.getDouble("lat");
                double lon = json.getDouble("lon");
                pharmacyItem.add(new pharmacyEng(NAME_ENG, TEL, H_ENG_CITY, H_ENG_GU, H_ENG_DONG, MAIN_KEY, lat, lon));
            }
        }catch (Exception e) {
            Log.d("pharmjson error",e.getMessage());
        }
    }


    //가능언어를 한국어 json 파일에서 찾음
    public class getPharmLan extends AsyncTask<Void, Integer, Void>{
        String pharm_file = "pharmacy_kor.json";
        String hos_file = "hospital_kor.json";
        String result = "";
        String result2 ="";
        CustomProgressDialog customProgressDialog = new CustomProgressDialog(getActivity());

        protected  void cancel(){
            return;
        }

        @Override
        protected void onPreExecute() {
            customProgressDialog .getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            customProgressDialog.setCanceledOnTouchOutside(false);
            // show dialog
            customProgressDialog.show();
            backgroudLock = true;
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            customProgressDialog.dismiss();
            Toast.makeText(getActivity(), "Click gps button!", Toast.LENGTH_LONG).show();
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                InputStream inputStream = getResources().getAssets().open(pharm_file);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                result = new String(buffer, "utf-8");

                JSONObject p_json = new JSONObject(result);
                JSONArray p_jArr = p_json.getJSONArray("DATA");


                for(int j = 0; j<pharmacyItem.size();j++) {
                    String temp = pharmacyItem.get(j).getMAIN_KEY();

                    for (int i = 0; i < p_jArr.length(); i++) {
                        p_json = p_jArr.getJSONObject(i);

                        if (p_json.getString("MAIN_KEY").equals(temp)) {
                            String AVAIL_LAN = p_json.getString("AVAIL_LAN");
                            String AVAIL_LAN_eng = "";
                            String NAME_KOR = p_json.getString("NAME_KOR");

                            if(AVAIL_LAN.matches(".*영어.*"))
                                AVAIL_LAN_eng += "english ";
                            if(AVAIL_LAN.matches(".*중국어.*"))
                                AVAIL_LAN_eng += "chinese ";
                            if(AVAIL_LAN.matches(".*일본어.*") || AVAIL_LAN.matches(".*일어.*"))
                                AVAIL_LAN_eng += "japanese ";
                            if(AVAIL_LAN.matches(".*러시아어.*"))
                                AVAIL_LAN_eng += "russians ";
                            if(AVAIL_LAN.matches(".*독일어.*"))
                                AVAIL_LAN_eng += "deutsche ";

                            pharmacyItem.get(j).setLan(AVAIL_LAN_eng);
                            pharmacyItem.get(j).setKo_name(NAME_KOR);

                            break;
                        }

                    }


                }

                InputStream inputStream2 = getResources().getAssets().open(hos_file);
                int size2 = inputStream2.available();
                byte[] buffer2 = new byte[size2];
                inputStream2.read(buffer2);
                inputStream2.close();
                result2 = new String(buffer2, "utf-8");

                JSONObject h_json2 = new JSONObject(result2);
                JSONArray h_jArr2 = h_json2.getJSONArray("DATA");


                for(int j = 0; j<hospitalItem.size();j++) {
                    String temp = hospitalItem.get(j).getMAIN_KEY();

                    for (int i = 0; i < h_jArr2.length(); i++) {
                        h_json2 = h_jArr2.getJSONObject(i);
                        if (h_json2.getString("MAIN_KEY").equals(temp)) {
                            String AVAIL_LAN = h_json2.getString("TARGET");
                            String AVAIL_LAN_eng = "English ";
                            String NAME_KOR = h_json2.getString("NAME_KOR");

                            if(AVAIL_LAN.matches(".*미국.*"))
                                AVAIL_LAN_eng += " ";
                            if(AVAIL_LAN.matches(".*중국.*"))
                                AVAIL_LAN_eng += "Chinese ";
                            if(AVAIL_LAN.matches(".*일본.*") || AVAIL_LAN.matches(".*일어.*"))
                                AVAIL_LAN_eng += "Japanese ";
                            if(AVAIL_LAN.matches(".*러시아.*"))
                                AVAIL_LAN_eng += "Russians ";
                            if(AVAIL_LAN.matches(".*독일.*"))
                                AVAIL_LAN_eng += "Germany ";
                            if(AVAIL_LAN.matches(".*중동.*"))
                                AVAIL_LAN_eng += "Middle east ";
                            if(AVAIL_LAN.matches(".*몽골.*"))
                                AVAIL_LAN_eng += "Mongolia ";
                            if(AVAIL_LAN.matches(".*베트남.*"))
                                AVAIL_LAN_eng += "Vietnam ";
                            if(AVAIL_LAN.matches(".*중앙아시아 여러나라.*"))
                                AVAIL_LAN_eng += "Asia ";
                            if(AVAIL_LAN.matches(".*동남아.*") || AVAIL_LAN.matches(".*동남아시아.*"))
                                AVAIL_LAN_eng += "South-East asia ";
                            if(AVAIL_LAN.matches(".*우즈베키스탄.*"))
                                AVAIL_LAN_eng += "Uzbekistan ";
                            if(AVAIL_LAN.matches(".*필리핀.*"))
                                AVAIL_LAN_eng += "Philippines ";


                            hospitalItem.get(j).setLan(AVAIL_LAN_eng);
                            hospitalItem.get(j).setKo_name(NAME_KOR);




                            break;
                        }

                    }

                }
            }catch (Exception e) {
                Log.d("getLanguage error ", e.getMessage());
            }
            return null;
        }

    }

    //병원정보를 hospitaltem 배열에 넣음
    public void hopitalJsonParsing(){
        String file = "hospital_eng.json";
        String result = "";

        try {
            InputStream is = getResources().getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "utf-8");

            JSONObject json = new JSONObject(result);
            JSONArray jArr = json.getJSONArray("DATA");


            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);

                String H_ENG_CITY = json.getString("H_ENG_CITY");
                String H_ENG_DONG = json.getString("H_ENG_DONG");
                String NAME_ENG = json.getString("NAME_ENG");
                String MAIN_KEY = json.getString("MAIN_KEY");
                String H_ENG_GU = json.getString("H_ENG_GU");
                double lat = json.getDouble("lat");
                double lon = json.getDouble("lon");
                String type = json.getString("type");

                hospitalItem.add(new hospitalEng(H_ENG_CITY,NAME_ENG, H_ENG_GU, H_ENG_DONG, MAIN_KEY, lat, lon, type));
            }

        }catch (Exception e) {
            Log.d("hospitaljson error",e.getMessage());
        }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //fagment2 -> fragment1 마커 이동

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Double lat = getArguments().getDouble("lat");
            Double lon = getArguments().getDouble("lon");
            String type = getArguments().getString("type");
            String mainkey = getArguments().getString("mainkey");
            go_key = Integer.parseInt(mainkey.substring(mainkey.length()-4,mainkey.length()));


            if(type.equals("pharmacy")){
                mapType = "pharmacy";
                for (int i = 0; i < pharmacyItem.size(); i++) {
                    if (mainkey.equals(pharmacyItem.get(i).getMAIN_KEY())) {
                        go_name = pharmacyItem.get(i).getNAME_ENG();
                        break;
                    }
                }
            }else if(type.equals("hospital")){
                mapType = "hospital";
                for (int i = 0; i < hospitalItem.size();i++) {
                    if (mainkey.equals(hospitalItem.get(i).getMAIN_KEY())) {
                        go_name = hospitalItem.get(i).getNAME_ENG();
                        break;
                    }
                }
            }


            mapView.removeAllPOIItems();
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lon), true);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(go_name);
            poiItem.setTag(go_key);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            if(type.equals("pharmacy"))
                poiItem.setCustomImageResourceId(R.drawable.ic_pill_red); // 마커 이미지.
            else if(type.equals("hospital"))
                poiItem.setCustomImageResourceId(R.drawable.ic_hospital_red); // 마커 이미지.
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mapView.selectPOIItem(poiItem, true);

            setHasOptionsMenu(false);
            linearlayout = (LinearLayout) getView().findViewById(R.id.linearLayout);
            ImageView imageview = (ImageView) getView().findViewById(R.id.gps);
            Button backBtn = (Button)getView().findViewById(R.id.backButton);



            linearlayout.setVisibility(View.GONE);
            imageview.setVisibility(View.GONE);
            backBtn.setVisibility(View.VISIBLE);

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getFragmentManager().getBackStackEntryCount() > 0 )
                        getFragmentManager().popBackStack();
                }
            });

        }
    }

    public class CustomProgressDialog extends Dialog{
        public CustomProgressDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
            setContentView(R.layout.custom_dialog); // 다이얼로그에 박을 레이아웃
        }
    }

    @Override
    public void onDestroyView() {
        try
        {
            if (getPharmLan.getStatus() == AsyncTask.Status.RUNNING)
            {
                getPharmLan.cancel(true);
            }
            else
            {
            }
        }
        catch (Exception e)
        {
        }

        super.onDestroyView();
    }

    public Location getLastKnownLocation() {
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return bestLocation;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}