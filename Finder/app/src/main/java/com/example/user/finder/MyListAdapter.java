package com.example.user.finder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    Context context;
    int layout;
    ListView listView;

    public static OAuthLogin mOAuthLoginInstance;
    public static Context mContext;
    public String email = ""; // 아이디 저장할 변수

    String kakaoID="";     //카카오아이디

    ArrayList<MyListItem> data;
    ArrayList<MyListItem> foundNum;
    ArrayList<MyListItem> foundImage;


    private final String SERVER_ADDRESS = "http://finder.dothome.co.kr";

    public MyListAdapter(Context context, ArrayList<MyListItem> data, ArrayList<MyListItem> foundNum, ArrayList<MyListItem> foundImage,int layout) {
        this.context = context;
        this.data = data;
        this.foundNum = foundNum;
        this.foundImage = foundImage;
        this.layout = layout;
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

    public Context getContext() {
        return this.context;
    }

    class ViewHolder {
        TextView myListView;
        Button deleteButton;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;

        kakaoID = GlobalApplication.getGlobalApplicationContext().getKakaoID();

        if(kakaoID.equals(""))
            new RequestApiTask().onPostExecute(new RequestApiTask().doInBackground());
        else
            email =  kakaoID+"@kakao";



        if(convertView == null) {
            convertView = View.inflate(context, layout, null);
            holder = new ViewHolder();
            holder.myListView = (TextView)convertView.findViewById(R.id.tv_text);
            holder.deleteButton = (Button)convertView.findViewById(R.id.b_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final MyListItem item = data.get(position);
        final MyListItem num = foundNum.get(position);
        final MyListItem image = foundImage.get(position);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder((Activity) v.getContext());
                alert.setTitle("내 글 (습득물) 삭제");
                alert.setMessage("글을 삭제하시겠습니까?");
                alert.setIcon(R.mipmap.icon);

                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("deleteopenStream", "1");

                        try {
                            Log.e("deleteopenStream", num.toString());
                            URL url = new URL(SERVER_ADDRESS + "/foundDelete.php?"
                                    + "foundNum=" + num.text
                                    + "&foundImage=" + image.text);

                            url.openStream();
                            String result = getXmlData("foundDelete_result.xml", "result");
                            Log.e("result", result);
                        } catch (Exception e) {
                            Log.e("delete exception(found)", "");
                        }

                        Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_LONG).show();

                        try {
                            URL text = new URL("http://finder.dothome.co.kr/found_select.php");
                            HttpURLConnection conn = (HttpURLConnection) text.openConnection();
                            conn.connect();
                            Log.e("response", conn.getResponseMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String strUrl = "http://finder.dothome.co.kr/found_select_result.xml";
                        new DownloadWebpageTask().onPostExecute(new DownloadWebpageTask().doInBackground(strUrl));

                        listView = (ListView) parent.findViewById(R.id.my_list);
                        MyListAdapter adapter = new MyListAdapter(context, data, foundNum, foundImage, R.layout.activity_mypage1_mylist);
                        listView.setAdapter(adapter);


                    }
                });

                alert.show();
            }
        });

        holder.myListView.setText(item.text);
        holder.myListView.setTextColor(Color.BLACK);

        Log.e("converView", "convertView");
        return convertView;
    }

    // 이메일 구하기
    public class RequestApiTask { // extends AsyncTask<Void, Void, String> {
        protected String doInBackground() {
            mOAuthLoginInstance = OAuthLogin.getInstance(); // 로그인 인스턴스 얻기
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            ParsingVersion(mOAuthLoginInstance.requestApi(mContext, at, url));

            return email;
        }

        protected void onPostExecute(String content) {
        }
    }

    // 파싱
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

    // DB에서 불러오기
    private class DownloadWebpageTask {
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

                data.clear();
                foundNum.clear();
                foundImage.clear();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();

                        if (tag_name.equals("foundId"))
                            set = 1;
                        else if (id_equal == 1 && tag_name.equals("foundText")) {
                            set = 2;
                            id_equal = 0;
                        }
                        else if (id_equal == 1 && tag_name.equals("foundNum"))
                            set = 3;
                        else if (id_equal == 1 && tag_name.equals("foundImage"))
                            set = 4;
                    } else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();

                        if (set == 1) {
                            Log.e("content", content);

                            if(content.equals(email)) {
                                id_equal = 1;
                                Log.e("id_equal", "1");
                            }
                        } else if (set == 2) {
                            data.add(new MyListItem(content));
                            Log.e("foundListItem", content);
                        }
                        else if (set == 3) {
                            foundNum.add(new MyListItem(content));
                            Log.e("foundListNum", content);
                        }
                        else if (set == 4) {
                            foundImage.add(new MyListItem(content));
                            Log.e("foundListImage", content);
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
}