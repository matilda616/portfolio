package com.example.sw1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USER on 2016-07-07.
 */
public class CalendarAdapter extends BaseAdapter {
    private ArrayList<DayInfo> mDayList;
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;

    private static final String TAG = "Fragment2";
    /**
     * Adpater 생성자
     *
     * @param context
     *            컨텍스트
     * @param textResource
     *            레이아웃 리소스
     * @param dayList
     *            날짜정보가 들어있는 리스트
     */
    public CalendarAdapter(Context context, int textResource, ArrayList<DayInfo> dayList)
    {
        this.mContext = context;
        this.mDayList = dayList;
        this.mResource = textResource;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mDayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        DayInfo day = mDayList.get(position);


        DayViewHolde dayViewHolder;


        if(convertView == null)
        {
            convertView = mLiInflater.inflate(mResource, null);

            if(position % 7 == 6)
            {
                convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP()+getRestCellWidthDP(), getCellHeightDP()));
            }
            else
            {
                convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP(), getCellHeightDP()));
            }


            dayViewHolder = new DayViewHolde();

            dayViewHolder.llBackground = (LinearLayout)convertView.findViewById(R.id.day_cell_ll_background);
            dayViewHolder.tvDay = (TextView) convertView.findViewById(R.id.day_cell_tv_day);

            convertView.setTag(dayViewHolder);
        }
        else
        {
            dayViewHolder = (DayViewHolde) convertView.getTag();
        }


        if(day != null)
        {
            dayViewHolder.tvDay.setText(day.getDay());

            if(day.isInMonth())
            {
                if(position % 7 == 0)   //일요일
                {
                    dayViewHolder.tvDay.setTextColor(Color.RED);
                }
                else if(position % 7 == 6)      //토요일
                {
                    dayViewHolder.tvDay.setTextColor(Color.BLUE);
                }
                else        //평일
                {
                    dayViewHolder.tvDay.setTextColor(Color.BLACK);
                }

            }
            else    //다른달
            {
                dayViewHolder.tvDay.setTextColor(Color.WHITE);
            }

        }

        return convertView;
    }

    public class DayViewHolde
    {
        public LinearLayout llBackground;
        public TextView tvDay;

    }

    private int getCellWidthDP()
    {
//      int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellWidth = 550/7;

        return cellWidth;
    }

    private int getRestCellWidthDP()
    {
//      int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellWidth = 550%7;

        return cellWidth;
    }

    private int getCellHeightDP()
    {
//      int height = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellHeight =500/6;

        return cellHeight;
    }

}
