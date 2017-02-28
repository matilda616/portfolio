package com.example.user.finder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MypageFragment extends Fragment {
    public static OAuthLogin mOAuthLoginInstance;
    public static Context mContext;
    private final String SERVER_ADDRESS = "http://finder.dothome.co.kr";

    public String email = ""; // 아이디 저장할 변수
    TextView e_mail;

    String kakaoID="";     //카카오아이디
    String kakaoNickname="";   //카카오닉네임

    ArrayList<MyListItem> myFoundListItems = new ArrayList<MyListItem>();
    ArrayList<MyListItem> myFindListItems = new ArrayList<MyListItem>();
    ArrayList<AlarmItem> myAlarmListItems = new ArrayList<AlarmItem>();
    ArrayList<MyListItem> myFoundListNum = new ArrayList<MyListItem>();
    ArrayList<MyListItem> myFindListNum = new ArrayList<MyListItem>();
    ArrayList<AlarmItem> myAlarmListNum = new ArrayList<AlarmItem>();
    ArrayList<MyListItem> myFoundListImage = new ArrayList<MyListItem>();
    ArrayList<MyListItem> myLostListImage = new ArrayList<MyListItem>();
    // ArrayList<AlarmItem> myAlarmListToken = new ArrayList<MyListItem>();

    LayoutInflater layoutinflater;
    EditText editText;
    ListView listView;

    AlarmAdapter adapter;
    MyListAdapter myListAdapter;
    MyListAdapter2 myListAdapter2;

    int count;

    String button = "found_mine";


    private android.os.Handler mHandler;
    private ProgressDialog progressdialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOAuthLoginInstance = OAuthLogin.getInstance(); // 로그인 인스턴스 얻기
        LoadKakao();    //종료시 저장한 카카오톡 아이디 얻기

        if(kakaoID.equals("")){
            //카카오톡으로 처음 로그인한경우
            kakaoID = GlobalApplication.getGlobalApplicationContext().getKakaoID();
            kakaoNickname= GlobalApplication.getGlobalApplicationContext().getKakaoNickname();
            email = kakaoID+"@kakao";
        }


    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_mypage, container, false);
        e_mail = (TextView) view.findViewById(R.id.email);

        //카카오톡 아이디 세팅
        e_mail.setText(kakaoNickname);

        //네이버로그인 경우
        if(e_mail.getText().equals(""))
        {
            // new RequestApiTask().execute(); // 네이버 아이디 셋팅
            new RequestApiTask().onPostExecute(new RequestApiTask().doInBackground());
        }


        Button b_logout = (Button) view.findViewById(R.id.button_logout);  // 로그아웃 버튼
        b_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            //검색버튼 누름
            public void onClick(View v) {
                if(kakaoNickname.length()>0){
                    //카카오톡 로그아웃
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            GlobalApplication.getGlobalApplicationContext().setKakaoID("");
                            GlobalApplication.getGlobalApplicationContext().setKakaoNickname("");
                            kakaoID = "";
                            kakaoNickname = "";
                            Intent it = new Intent(getActivity(), LoginActivity.class);
                            startActivity(it);
                            getActivity().finish();
                        }
                    });
                }else {
                    //네이버 로그아웃
                    mOAuthLoginInstance.logout(mContext);
                    Intent it = new Intent(getActivity(), LoginActivity.class);
                    startActivity(it);
                    getActivity().finish();
                }
            }
        });

        final LinearLayout container_layout = (LinearLayout) view.findViewById(R.id.container);
        layoutinflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Button found_mine = (Button) view.findViewById(R.id.found_mine);
        final Button find_mine = (Button) view.findViewById(R.id.find_mine);
        final Button alarm = (Button) view.findViewById(R.id.alarm);


        mHandler = new android.os.Handler();

        if(button.equals("found_mine")) {
            found_mine.setSelected(true);
            found_mine.performClick();
        }

        if(button.equals("find_mine")){
            find_mine.setSelected(true);
            find_mine.performClick();
        }

        if (myFoundListItems.isEmpty()) {
            try {
                new Thread() {
                    public void run() {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        try {
                            URL text = new URL("http://finder.dothome.co.kr/found_select.php");
                            HttpURLConnection conn = (HttpURLConnection) text.openConnection();
                            conn.connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String strUrl = "http://finder.dothome.co.kr/found_select_result.xml";
            new DownloadWebpageTask().onPostExecute(new DownloadWebpageTask().doInBackground(strUrl));

            container_layout.removeAllViews();
            layoutinflater.inflate(R.layout.activity_mypage1, container_layout, true);

            listView = (ListView) container_layout.findViewById(R.id.my_list);
            myListAdapter = new MyListAdapter(container_layout.getContext(), myFoundListItems, myFoundListNum, myFoundListImage, R.layout.activity_mypage1_mylist);
            listView.setAdapter(myListAdapter);
        }

        found_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found_mine.setSelected(true);
                find_mine.setSelected(false);
                alarm.setSelected(false);

                try {
                    new Thread() {
                        public void run() {
                            try {
                                URL text = new URL("http://finder.dothome.co.kr/found_select.php");
                                HttpURLConnection conn = (HttpURLConnection) text.openConnection();
                                conn.connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String strUrl = "http://finder.dothome.co.kr/found_select_result.xml";
                new DownloadWebpageTask().onPostExecute(new DownloadWebpageTask().doInBackground(strUrl));

                container_layout.removeAllViews();
                layoutinflater.inflate(R.layout.activity_mypage1, container_layout, true);

                listView = (ListView) container_layout.findViewById(R.id.my_list);
                myListAdapter = new MyListAdapter(container_layout.getContext(), myFoundListItems, myFoundListNum, myFoundListImage, R.layout.activity_mypage1_mylist);
                listView.setAdapter(myListAdapter);
            }
        });

        find_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found_mine.setSelected(false);
                find_mine.setSelected(true);
                alarm.setSelected(false);

                try {
                    new Thread() {
                        public void run() {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);

                            try {
                                URL text = new URL("http://finder.dothome.co.kr/lost_select.php");
                                HttpURLConnection conn = (HttpURLConnection) text.openConnection();
                                conn.connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String strUrl = "http://finder.dothome.co.kr/lost_select_result.xml";
                new DownloadWebpageTask().onPostExecute(new DownloadWebpageTask().doInBackground(strUrl));

                container_layout.removeAllViews();
                layoutinflater.inflate(R.layout.activity_mypage11, container_layout, true);

                listView = (ListView) container_layout.findViewById(R.id.my_list);
                myListAdapter2 = new MyListAdapter2(container_layout.getContext(), myFindListItems, myFindListNum, myLostListImage, R.layout.activity_mypage11_mylist);
                listView.setAdapter(myListAdapter2);


            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found_mine.setSelected(false);
                find_mine.setSelected(false);
                alarm.setSelected(true);

                try {
                    URL text = new URL("http://finder.dothome.co.kr/alarm_select.php");
                    HttpURLConnection conn = (HttpURLConnection) text.openConnection();
                    conn.connect();
                    Log.e("response", conn.getResponseMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String strUrl = "http://finder.dothome.co.kr/alarm_select_result.xml";
                new DownloadWebpageTask2().onPostExecute(new DownloadWebpageTask().doInBackground(strUrl));

                container_layout.removeAllViews();
                layoutinflater.inflate(R.layout.activity_mypage2, container_layout, true);

                listView = (ListView) container_layout.findViewById(R.id.keyword_list);
                editText = (EditText) container_layout.findViewById(R.id.keyword);
                adapter = new AlarmAdapter(container_layout.getContext(), myAlarmListItems, myAlarmListNum, R.layout.activity_mypage2_keyword);
                listView.setAdapter(adapter);

                count = adapter.getCount();

                if(count == 0) {
                    listView.setBackgroundResource(R.drawable.keyword);
                }


                final Button b_insert = (Button) container_layout.findViewById(R.id.keyword_insert);
                b_insert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        count = adapter.getCount();
                        if(count>=10){
                            AlertDialog.Builder alert = new AlertDialog.Builder((Activity) v.getContext());
                            alert.setTitle("키워드 등록");
                            alert.setMessage("키워드는 최대 10개까지 설정가능합니다");
                            alert.setIcon(R.mipmap.icon);

                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alert.show();

                        }else {
                            progressdialog = new ProgressDialog(getActivity());
                            progressdialog.setMessage("잠시만 기다려주세요.");
                            progressdialog.setCancelable(false);
                            progressdialog.show();

                            mHandler.postDelayed(mRunnable, 2000);

                            String keyword = editText.getText().toString();
                            String token = FirebaseInstanceId.getInstance().getToken();

                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                            try {
                                URL url = new URL(SERVER_ADDRESS + "/alarm_insert.php?"
                                        + "alarmId=" + URLEncoder.encode(email, "UTF-8")
                                        + "&alarmKeyword=" + URLEncoder.encode(keyword, "UTF-8")
                                        + "&token=" + URLEncoder.encode(token, "UTF-8"));

                                url.openStream();
                                String result = getXmlData("alarm_insert_result.xml", "result");

                                alarm.performClick();


                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "키워드 등록 실패:" + keyword, Toast.LENGTH_LONG).show();

                            }

                            new Thread() {
                                public void run() {

                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);

                                    try {
                                        URL text = new URL("http://finder.dothome.co.kr/alarm_select.php");
                                        HttpURLConnection conn = (HttpURLConnection) text.openConnection();
                                        conn.connect();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();

                            String strUrl = "http://finder.dothome.co.kr/alarm_select_result.xml";
                            new DownloadWebpageTask2().onPostExecute(new DownloadWebpageTask().doInBackground(strUrl));

                            adapter = new AlarmAdapter(getContext(), myAlarmListItems, myAlarmListNum, R.layout.activity_mypage2_keyword);
                            editText.setText("");
                            listView.setAdapter(adapter);
                        }
                    }
                });
            }
        });

        try {
            Intent it = getActivity().getIntent();
            int v = it.getExtras().getInt("p");
            if (v == 2) {
                find_mine.performClick();
            }
        } catch(Exception e) { }

        return view;
    }



    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(progressdialog != null && progressdialog.isShowing()){
                progressdialog.dismiss();
            }
        }
    };


    // 이메일 구하기
    public class RequestApiTask { // extends AsyncTask<Void, Void, String> {
        protected String doInBackground() {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            ParsingVersion(mOAuthLoginInstance.requestApi(mContext, at, url));

            return email;
        }

        protected void onPostExecute(String content) {
            e_mail.setText((String) content); // 아이디 텍스트뷰 셋팅
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
        } catch (Exception e) {
            Log.e("dd", "Error in network call", e);
        }

        email = f_array[0];
    }

    // DB에서 불러오기
    private class DownloadWebpageTask  {
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);

                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                // boolean bSet = false;
                int set = 0;
                int id_equal = 0;

                myFindListItems.clear();
                myFoundListItems.clear();
                myFindListNum.clear();
                myFoundListNum.clear();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();

                        if (tag_name.equals("lostId") || tag_name.equals("foundId"))
                            set = 1;
                        else if (id_equal == 1 && tag_name.equals("lostText")) {
                            set = 2;
                            id_equal = 0;
                        }
                        else if (id_equal == 1 && tag_name.equals("foundText")) {
                            set = 3;
                            id_equal = 0;
                        }
                        else if (id_equal == 1 && tag_name.equals("lostNum"))
                            set = 4;
                        else if (id_equal == 1 && tag_name.equals("foundNum"))
                            set = 5;
                        else if (id_equal == 1 && tag_name.equals("foundImage"))
                            set = 6;
                        else if (id_equal == 1 && tag_name.equals("lostImage"))
                            set = 7;

                    } else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();

                        if (set == 1) {
                            Log.e("content", content);

                            if(content.equals(email)) {
                                id_equal = 1;
                                Log.e("id_equal", "1");
                            }
                        } else if (set == 2) {
                            myFindListItems.add(new MyListItem(content));
                            Log.e("findListItem", content);
                        }
                        else if (set == 3) {
                            myFoundListItems.add(new MyListItem(content));
                            Log.e("foundListItem", content);
                        }
                        else if (set == 4) {
                            myFindListNum.add(new MyListItem(content));
                            Log.e("findListNum", content);
                        }
                        else if (set == 5) {
                            myFoundListNum.add(new MyListItem(content));
                            Log.e("foundListNum", content);
                        }
                        else if (set == 6) {
                            myFoundListImage.add(new MyListItem(content));
                            Log.e("foundListImage", content);
                        }
                        else if (set == 7) {
                            myLostListImage.add(new MyListItem(content));
                            Log.e("lostListImage", content);
                        }

                        set = 0;
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }

                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";

                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } finally {
                conn.disconnect();
            }
        }
    }

    // DB에서 ALARM 불러오기
    private class DownloadWebpageTask2 {
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));

                int eventType = xpp.getEventType();
                // boolean bSet = false;
                int set = 0;
                int id_equal = 0;

                myAlarmListItems.clear();
                myAlarmListNum.clear();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();

                        if (tag_name.equals("alarmId"))
                            set = 1;
                        else if (id_equal == 1 && tag_name.equals("alarmNum")) {
                            set = 2;
                        }
                        else if (id_equal == 1 && tag_name.equals("alarmKeyword")) {
                            set = 3;
                            id_equal = 0;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();

                        if (set == 1) {
                            Log.e("content", content);

                            if(content.equals(email)) {
                                id_equal = 1;
                                Log.e("id_equal", "1");
                            }
                        } else if (set == 2) {
                            myAlarmListNum.add(new AlarmItem(content));
                            Log.e("myAlarmListNum", content);
                        }
                        else if (set == 3) {
                            myAlarmListItems.add(new AlarmItem(content));
                            Log.e("myAlarmListItems", content);
                        }

                        set = 0;
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";

                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } finally {
                conn.disconnect();
            }
        }
    }

    // XML 파싱
    private String getXmlData(String filename, String str) {
        String rss = SERVER_ADDRESS + "/";
        String ret = "";




        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            URL server = new URL(rss + filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");

            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equals(str)) {
                        ret = xpp.nextText();
                    }
                }

                eventType = xpp.next();
            }
        } catch(Exception e) {
            Log.e("Error", e.getMessage());
        }


        return ret;
    }

    private void SaveKakao(){
        SharedPreferences pref = getActivity().getSharedPreferences("kakao", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("nick",kakaoNickname);
        editor.putString("id",kakaoID);
        editor.commit();
    }

    private void LoadKakao(){
        SharedPreferences pref = getActivity().getSharedPreferences("kakao",Activity.MODE_PRIVATE);
        kakaoID = pref.getString("id","");
        kakaoNickname=pref.getString("nick","");
        email = kakaoID+"@kakao";
    }

    @Override
    public void onDestroy() {
        SaveKakao();
        super.onDestroy();
    }
}