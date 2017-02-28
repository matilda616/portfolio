package com.example.sw1;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment3 extends Fragment {

    private List<ItemInCardViewFreg3> list;
    private RecyclerView recyclerView;
    private RecyclerAdapterFreg3 rAdapter;

    public static String PREFS_NAME = "MyPref";
    SharedPreferences prefs;

    private String message_name;
    private String message_time;
    private String message_interval;
    private String message_repeat;
    private String message_sound;

    private AlarmManager mAlarmMgr;
    Calendar calendar;

    String date;
    String[] devide_time;
    String[] devide_repeat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment3, container, false);
        calendar = Calendar.getInstance();

        List<ItemInCardViewFreg3> list;
        list = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        // Floating Button; AddAlarmActivity

        prefs = getActivity().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        try {
            message_name = prefs.getString("message_send_name", "");
            message_time = prefs.getString("message_send_time", "");
            message_interval = prefs.getString("message_send_interval", "");
            message_repeat = prefs.getString("message_send_repeat", "");
            message_sound = prefs.getString("message_send_sound", "");

        } catch (Exception e) {
            message_name = "일정을 저장해주세요.";
            message_time = "";
            message_interval = "아무 일정도 저장되어 있지 않습니다.";
            message_repeat = "";
            message_sound = "";
        }
        FloatingActionButton b_addAlarm = (FloatingActionButton) view.findViewById(R.id.etc_b_addAlarm);
        b_addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), AddAlarmActivity.class);
                startActivity(it);
            }
        });

        initialize();
        mAlarmMgr = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

        date = calendar.get(Calendar.YEAR) + "년 "   // 년 정보 얻기
                + (calendar.get(Calendar.MONTH) + 1) + "월 "   // 달 정보 얻기(0 ~ 11)
                + calendar.get(Calendar.DATE) + "일 "
                + calendar.get(Calendar.HOUR) + "시 "
                + calendar.get(Calendar.MINUTE) + "분";

         onBtnAlarm1();

        return view;
    }

    private void initialize() {

        LinearLayoutManager lManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //카드뷰 크기 고정
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lManager);
        try {

            String[] devide_name = message_name.split("\n");
            devide_time = message_time.split("\n");
            String[] devide_interval = message_interval.split("\n");
            String[] devide_repeat = message_repeat.split("\n");
            String[] devide_sound = message_sound.split("\n");

            list = new ArrayList<>();
            for (int i = 0; i < devide_name.length; i++) {
                list.add(new ItemInCardViewFreg3(R.mipmap.barrier_image, devide_name[i], devide_time[i], devide_interval[i], devide_repeat[i], devide_sound[i], Integer.toString(i)));
            }
        } catch (Exception e) {
            list.add(new ItemInCardViewFreg3(R.mipmap.barrier_image, "일정을 저장해주세요", "", "아무 일정도 저장되어 있지 않습니다.", "", "", "error"));
        }
        try {
            for (int i = 0; i < devide_time.length; i++)
                if (devide_time[i].equals(date) && devide_repeat[i].equals("1회")) {
                    onBtnAlarm1();
                } else if (devide_time[i].equals(date)) {
                    onBtnAlarm2();
                }
        } catch (NullPointerException e) {
        }

        //리사이클러 뷰 어댑터 생성
        rAdapter = new RecyclerAdapterFreg3(getActivity().getApplicationContext(), list);

        //리사이클러 뷰에 어댑터 장착
        recyclerView.setAdapter(rAdapter);

    }

    // 1회 알람 시작 - 시간 간격 지정
    public void onBtnAlarm1() {
        // 수행할 동작을 생성
        Intent intent = new Intent(this.getContext(), AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(this.getContext(), 0, intent, 0);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 16, 45, 0);
        /*year -- Value to be used for YEAR field
month -- Value to be used for MONTH field. 0 is January
day -- Value to be used for DAY field
hourOfDay -- Value to be used for HOUR_OF_DAY field
minute -- Value to be used for MINUTE field
second -- Value to be used for SECOND field*/
        mAlarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
    }

    // 반복 알람 시작 - 특정 시간 지정
    public void onBtnAlarm2() {
        // 수행할 동작을 생성
        Intent intent = new Intent(this.getContext(), AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(this.getContext(), 0, intent, 0);
        // 알람이 발생할 정확한 시간을 지정
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 7);
        // 반복 알람 시작
        mAlarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, pIntent);
    }

}
