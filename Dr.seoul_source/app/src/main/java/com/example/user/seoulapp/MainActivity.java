package com.example.user.seoulapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends ActionBarActivity implements TabHost.OnTabChangeListener{


    FragmentTabHost tabhost;
    private static final String TAB1 = "tab1";
    private static final String TAB2 = "tab2";

    TabSpec tab1, tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabhost = (FragmentTabHost)findViewById(R.id.tabhost);
        tabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);
        tabhost.setOnTabChangedListener(this);

        tab1 = tabhost.newTabSpec(TAB1);
        tab2 = tabhost.newTabSpec(TAB2);

        tab1.setIndicator("",getResources().getDrawable(R.drawable.map_blue));
        tab2.setIndicator("",getResources().getDrawable(R.drawable.bookmark_blue));


        tabhost.addTab(tab1,Fragment1.class,null);
        tabhost.addTab(tab2,Fragment2.class,null);
        tabhost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#4ba2ff"));
        tabhost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#4ba2ff"));


    }

    @Override
    public void onTabChanged(String tabId) {
       /* if(tabId.equals(TAB1))
            tab1.setIndicator("",getResources().getDrawable(R.drawable.map_white));
        else if(tabId.equals(TAB2))
            tab2.setIndicator("",getResources().getDrawable(R.drawable.bookmark_white));
*/
        //tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setForeground(R.drawable.map_blue););

        int menu_off[] = {R.drawable.map_blue, R.drawable.bookmark_blue};
        int menu_on[] = {R.drawable.map_white, R.drawable.bookmark_white};

        for(int i = 0; i < tabhost.getTabWidget().getChildCount();i++){
            ImageView iv = (ImageView)tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.icon);
            iv.setImageDrawable(getResources().getDrawable(menu_off[i]));
        }
        ImageView ip = (ImageView)tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.icon);
        ip.setImageDrawable(getResources().getDrawable(menu_on[tabhost.getCurrentTab()]));
    }
}