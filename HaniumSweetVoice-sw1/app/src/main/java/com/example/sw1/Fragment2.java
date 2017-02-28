package com.example.sw1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment2 extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    public static int SUNDAY        = 1;
    public static int MONDAY        = 2;
    public static int TUESDAY       = 3;
    public static int WEDNSESDAY    = 4;
    public static int THURSDAY      = 5;
    public static int FRIDAY        = 6;
    public static int SATURDAY      = 7;

    private TextView mTvCalendarTitle;
    private GridView mGvCalendar;

    private ArrayList<DayInfo> mDayList;


    private CalendarAdapter mCalendarAdapter;

    Calendar mLastMonthCalendar;
    Calendar mThisMonthCalendar;
    Calendar mNextMonthCalendar;


    LayoutInflater layoutinflater;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment2, container, false);

        //날짜정보 저장하는 리스트 생성
        mDayList = new ArrayList<DayInfo>();

        TextView bLastMonth = (TextView)view.findViewById(R.id.gv_calendar_activity_b_last);    //이전달버튼
        TextView bNextMonth = (TextView)view.findViewById(R.id.gv_calendar_activity_b_next);    //다음달버튼
        mTvCalendarTitle = (TextView)view.findViewById(R.id.gv_calendar_activity_tv_title); //이번달 표시
        mGvCalendar = (GridView)view.findViewById(R.id.gv_calendar_activity_gv_calendar);


        //이번달의 캘린더 인스턴스를 생성
        mThisMonthCalendar = Calendar.getInstance();
        mThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mThisMonthCalendar);

        //이번달, 다음달 버튼
        bNextMonth.setOnClickListener(this);
        bLastMonth.setOnClickListener(this);
        mGvCalendar.setOnItemClickListener(this);

        //일정표시 부분화면 설정
        final LinearLayout container_layout = (LinearLayout)view.findViewById(R.id.container);
        layoutinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutinflater.inflate(R.layout.activity_container, container_layout, true);

        //일정표시 위한 recyclerview 설정
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        // Floating Button; AddAlarmActivity
        FloatingActionButton b_addSchedule = (FloatingActionButton)view.findViewById(R.id.b_addSchedule);
        b_addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), AddScheduleActivity.class);
                startActivity(it);
            }
        });

        return view;
    }


    /**
     * 달력을 셋팅한다.
     *
     * @param calendar 달력에 보여지는 이번달의 Calendar 객체
     */
    public void getCalendar(Calendar calendar)
    {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        mDayList.clear();

        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);
        Log.e("지난달 마지막일", calendar.get(Calendar.DAY_OF_MONTH) + "");

        // 지난달의 마지막 일자를 구한다.
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);
        Log.e("이번달 시작일", calendar.get(Calendar.DAY_OF_MONTH)+"");

        if(dayOfMonth == SUNDAY)
        {
            dayOfMonth += 7;
        }

        lastMonthStartDay -= (dayOfMonth-1)-1;


        // 캘린더 타이틀(년월 표시)을 세팅한다.
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");

        DayInfo day;


        Log.e("DayOfMonth", dayOfMonth + "");

        //배열에 날짜생성
        for(int i=0; i<dayOfMonth-1; i++)
        {
            int year = mThisMonthCalendar.get(Calendar.YEAR);
            int month = mThisMonthCalendar.get(Calendar.MONTH) + 1;
            int date = lastMonthStartDay+i;
            day = new DayInfo();

            //position에 해당하는 년월일 저장
            day.setYear(Integer.toString(year));
            day.setMonth(Integer.toString(month));
            day.setDay(Integer.toString(date));

            day.setInMonth(false);

            mDayList.add(day);
        }
        for(int i=1; i <= thisMonthLastDay; i++)
        {
            int year = mThisMonthCalendar.get(Calendar.YEAR);
            int month = mThisMonthCalendar.get(Calendar.MONTH) + 1;
            day = new DayInfo();

            //position에 해당하는 년월일 저장
            day.setDay(Integer.toString(i));
            day.setYear(Integer.toString(year));
            day.setMonth(Integer.toString(month));

            day.setInMonth(true);

            mDayList.add(day);
        }
        for(int i=1; i<42-(thisMonthLastDay+dayOfMonth-1)+1; i++)
        {
            int month = mThisMonthCalendar.get(Calendar.MONTH) + 1;
            int year = mThisMonthCalendar.get(Calendar.YEAR);
            day = new DayInfo();

            //position에 해당하는 년월일 저장
            day.setDay(Integer.toString(i));
            day.setInMonth(false);
            day.setYear(Integer.toString(year));
            day.setMonth(Integer.toString(month));

            mDayList.add(day);
        }

        mCalendarAdapter = new CalendarAdapter(getContext(), R.layout.day, mDayList);
        mGvCalendar.setAdapter(mCalendarAdapter);
    }

    /**
     * 지난달의 Calendar 객체를 반환한다.
     *
     * @param calendar
     * @return LastMonthCalendar
     */
    private Calendar getLastMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    /**
     * 다음달의 Calendar 객체를 반환한다.
     *
     * @param calendar
     * @return NextMonthCalendar
     */
    private Calendar getNextMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }


    //날짜를 눌렀을때 액션(cardview 내용 갱신)
    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3)
    {
        int year = Integer.parseInt(mDayList.get(position).getYear());
        int month= Integer.parseInt(mDayList.get(position).getMonth());
        int day= Integer.parseInt(mDayList.get(position).getDay());

        String date = year+"-"+month+"-"+day;

        List<ItemInCardViewFreg2> items = new ArrayList<>();
        ItemInCardViewFreg2[] item = new ItemInCardViewFreg2[4];
        item[0] = new ItemInCardViewFreg2("title", "date:"+date+'\n'+"interval:"+'\n'+"Repeat:"+'\n'+ "Sound:");
        item[1]= new ItemInCardViewFreg2("title2",date);
        item[2] = new ItemInCardViewFreg2("title3",date);
        item[3] = new ItemInCardViewFreg2("장볼것ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ",date);

        for(int i =0;i<item.length;i++) {
            items.add(item[i]);
        }
        recyclerView.setAdapter(new RecyclerAdapterFreg2(getActivity(), items, R.layout.activity_container));
    }

    @Override
    //다음달 이전달 버튼 액션
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.gv_calendar_activity_b_last:
                mThisMonthCalendar = getLastMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
                break;
            case R.id.gv_calendar_activity_b_next:
                mThisMonthCalendar = getNextMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
                break;
        }
    }
}