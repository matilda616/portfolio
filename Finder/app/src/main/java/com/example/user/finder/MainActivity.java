package com.example.user.finder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nhn.android.naverlogin.OAuthLogin;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    Fragment fragment = new Fragment();
    private TabLayout tabLayout;

    public static OAuthLogin mOAuthLoginInstance;
    public static Context mContext;
    String email = ""; // 아이디 저장 변수

    public static Activity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = MainActivity.this;
        // http://blog.naver.com/hmw5233/220082502886
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar);
        // https://plus.google.com/+%E6%B5%B7%E6%9E%97/posts/YxTJB1r3ufk
        getSupportActionBar().setElevation(0);

        final TextView title_text = (TextView)findViewById(R.id.title_text);
        title_text.setText("습득물");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        int v = -1;

        try {
            Intent intent = getIntent();
            v = intent.getExtras().getInt("p");

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(v);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            setupTabIcons();
        } catch (Exception e) {
            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            setupTabIcons();
        }

        // 로그인 인스턴스
        mOAuthLoginInstance = OAuthLogin.getInstance();
        // new RequestApiTask().execute();
        new RequestApiTask().onPostExecute(new RequestApiTask().doInBackground());

        // http://www.kmshack.kr/2014/01/android-viewpager-%EC%84%B1%EB%8A%A5%ED%96%A5%EC%83%81-%EB%B0%A9%EB%B2%95/
        // http://blog.asamaru.net/2015/09/21/android-viewpager-dot-setonpagechangelistener-deprecated/
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    title_text.setText("습득물");
                else if (position == 1)
                    title_text.setText("분실물");
                else if (position == 2)
                    title_text.setText("마이페이지");
                // mSectionsPagerAdapter.notifyDataSetChanged();
                // = mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Button app_info = (Button)findViewById(R.id.app_info);
        app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), AppInfo.class);
                startActivity(it);
            }
        });
    }

    // http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
    private void setupTabIcons() {
        // http://stackoverflow.com/questions/34237896/how-to-increase-icon-size-of-tabs-in-tablayout
        View view1 = getLayoutInflater().inflate(R.layout.tab_image, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.found_icon);
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = getLayoutInflater().inflate(R.layout.tab_image, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.find_icon);
        tabLayout.getTabAt(1).setCustomView(view2);

        View view3 = getLayoutInflater().inflate(R.layout.tab_image, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.mypage_icon);
        tabLayout.getTabAt(2).setCustomView(view3);

        // tabLayout.getTabAt(0).setIcon(R.drawable.found_icon);
        // tabLayout.getTabAt(1).setIcon(R.drawable.lost_icon);
        // tabLayout.getTabAt(2).setIcon(R.drawable.mypage_icon);
    }

    // A placeholder fragment containing a simple view.
    public static class PlaceholderFragment extends Fragment {
        //The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() { }

        // Returns a new instance of this fragment for the given section number.
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    // A {@link FragmentPagerAdapter} that returns a fragment
    // corresponding to one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                fragment = new FoundFragment();
            else if (position == 1)
                fragment = new FindFragment();
            else if (position == 2)
                fragment = new MypageFragment();

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    // 이메일 구하기
    public class RequestApiTask { // extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() { }

        protected String doInBackground() {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            ParsingVersion(mOAuthLoginInstance.requestApi(mContext, at, url));
            return email;
        }

        protected void onPostExecute(String content) {
            // Main Activity 들어가면 Toast로 id를 띄워줌
            // Toast.makeText(getApplicationContext(), "id = " + (String)content, Toast.LENGTH_LONG).show();
        }
    }

    // ID 파싱
    public void ParsingVersion(String data) {
        String f_array[] = new String[9];

        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
            parser.setInput(input, "UTF-8");

            int parserEvent = parser.getEventType();
            String tag;
            boolean inText = false;
            boolean lastMatTag = false;

            int colIdx = 0;

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.compareTo("xml") == 0) {
                            inText = false;
                        } else if (tag.compareTo("data") == 0) {
                            inText = false;
                        } else if (tag.compareTo("result") == 0) {
                            inText = false;
                        } else if (tag.compareTo("resultcode") == 0) {
                            inText = false;
                        } else if (tag.compareTo("message") == 0) {
                            inText = false;
                        } else if (tag.compareTo("response") == 0) {
                            inText = false;
                        } else {
                            inText = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tag = parser.getName();
                        if (inText) {
                            if (parser.getText() == null) {
                                f_array[colIdx] = "";
                            } else {
                                f_array[colIdx] = parser.getText().trim();
                            }
                            colIdx++;
                        }
                        inText = false;
                        break;

                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        inText = false;
                        break;
                }
                parserEvent = parser.next();
            }
        }

        catch (Exception e) {
            Log.e("dd", "Error in network call", e);
        }

        email = f_array[0];
    }
}