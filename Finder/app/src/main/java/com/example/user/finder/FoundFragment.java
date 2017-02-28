package com.example.user.finder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FoundFragment extends Fragment {
    ArrayList<String> idArray = new ArrayList<String>();
    ArrayList<String> imageArray = new ArrayList<String>();
    ArrayList<String> textArray = new ArrayList<String>();
    ArrayList<String> phoneArray = new ArrayList<String>();
    ArrayList<String> placeArray = new ArrayList<String>();

    ArrayList<IdItem> idItems = new ArrayList<IdItem>();
    ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>(); // 이미지 저장
    ArrayList<TextItem> textItems = new ArrayList<TextItem>(); // 글 저장
    ArrayList<PhoneItem> phoneItems = new ArrayList<PhoneItem>();
    ArrayList<PlaceItem> placeItems = new ArrayList<PlaceItem>(); // 전달 여부 저장

    ImageView imageView;
    TextView textView;
    GridView gridView;

    String kakaoID="";     //카카오아이디
    String kakaoNickname="";   //카카오닉네임

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // http://docko.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-androidosNetworkOnMainThreadException
        new Thread() {
            public void run() {
                try {
                    URL text = new URL("http://finder.dothome.co.kr/found_select.php");
                    HttpURLConnection conn = (HttpURLConnection)text.openConnection();
                    conn.connect();
                    readStream(conn.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        LoadKakao();    //종료시 저장한 카카오톡 아이디 얻기

        if(kakaoID.equals("")){
            kakaoID = GlobalApplication.getGlobalApplicationContext().getKakaoID();
            kakaoNickname= GlobalApplication.getGlobalApplicationContext().getKakaoNickname();
        }

        SaveKakao();
        Log.d("ddfsw",kakaoID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_found, container, false);
        final EditText search = (EditText)view.findViewById(R.id.search_found);


        // 검색 버튼
        Button b_search = (Button)view.findViewById(R.id.b_search);
        b_search.setOnClickListener(new View.OnClickListener() {
            @Override
            // 검색 버튼 누름
            public void onClick(View v) {
                if (search.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "검색어를 입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Intent it = new Intent(getActivity(), SearchActivity.class);
                    it.putExtra("s_text", search.getText().toString());
                    search.setText("");
                    startActivity(it);
                }
            }
        });

        // 플로팅 버튼 = 글쓰기
        FloatingActionButton b_write = (FloatingActionButton)view.findViewById(R.id.b_write);  // 글쓰기 버튼
        b_write.setOnClickListener(new View.OnClickListener() {
            @Override
            // 글쓰기 버튼
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), Write1Activity.class);
                startActivity(it);
            }
        });

        gridView = (GridView)view.findViewById(R.id.image_grid);

        return view;
    }

    public void onReady() {
        idArray.clear();
        for(int i = textItems.size() - 1; i >= 0; i--) {
            idArray.add(idItems.get(i).toString());
        }

        imageArray.clear();
        for(int i = textItems.size() - 1; i >= 0; i--) {
            imageArray.add(imageItems.get(i).image);
        }

        textArray.clear();
        for(int i = textItems.size() - 1; i >= 0; i--) {
            textArray.add(textItems.get(i).toString());
        }

        phoneArray.clear();
        for(int i = textItems.size() - 1; i >= 0; i--) {
            phoneArray.add(phoneItems.get(i).toString());
        }

        placeArray.clear();
        for (int i = textItems.size() - 1; i >= 0; i--) {
            placeArray.add(placeItems.get(i).toString());
        }

        gridView.setAdapter(new gridAdapter());
    }

    public class gridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public gridAdapter() {
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            // 그리드뷰에 출력할 목록 수
            return textArray.size();
        }

        @Override
        public Object getItem(int position) {
            // 아이템을 호출할 때 사용하는 메소드
            return textArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            // 아이템의 아이디를 구할 때 사용하는 메소드
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_found_layout, parent, false);
            }

            imageView = (ImageView)convertView.findViewById(R.id.iv_image);
            textView = (TextView)convertView.findViewById(R.id.tv_found);

            // http://theopentutorials.com/tutorials/android/imageview/android-how-to-load-image-from-url-in-imageview/;
            final String image = imageArray.get(position).toString();
            final String text = textArray.get(position).toString();
            final String phone = phoneArray.get(position).toString();
            final String place = placeArray.get(position).toString();
            String URL = "http://finder.dothome.co.kr/found_images/" + image;

            // 이미지 설정
            // Create an object for subclass of AsyncTask
            GetXMLTask task = new GetXMLTask();
            // Execute the task
            task.onPostExecute(task.doInBackground(new String[] { URL }));

            textView.setText(textArray.get(position));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle mBundle = new Bundle();
                    Intent it = new Intent(getActivity(), Board1Activity.class);

                    mBundle.putString("image", image);
                    mBundle.putString("text", text);
                    mBundle.putString("phone", phone);
                    mBundle.putString("place", place);
                    it.putExtras(mBundle);
                    startActivity(it);
                }
            });

            return convertView;
        }
    }

    private class GetXMLTask { // extends AsyncTask<String, Void, Bitmap> {
        String text;

        // @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;

            for (String url : urls) {
                map = downloadImage(url);
            }

            return map;
        }

        // Sets the Bitmap returned by doInBackground
        // @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 2;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString) throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            final URLConnection connection = url.openConnection();

            // http://gochul.tistory.com/3
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return stream;
        }
    }

    private void readStream(InputStream in) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";

            while ((line = reader.readLine()) != null) {
                textView.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String strUrl = "http://finder.dothome.co.kr/found_select_result.xml";
        new DownloadWebpageTask().execute(strUrl);
    }

    // DB에서 불러오기
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
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

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();

                        if (tag_name.equals("foundId"))
                            set = 1;
                        else if (tag_name.equals("foundImage"))
                            set = 2;
                        else if (tag_name.equals("foundText"))
                            set = 3;
                        else if (tag_name.equals("foundPhone"))
                            set = 4;
                        else if (tag_name.equals("foundPlace"))
                            set = 5;
                    } else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();

                        if (set == 1)
                            idItems.add(new IdItem(content));
                        else if (set == 2)
                            imageItems.add(new ImageItem(content));
                        else if (set == 3)
                            textItems.add(new TextItem(content));
                        else if (set == 4)
                            phoneItems.add(new PhoneItem(content));
                        else if (set == 5)
                            placeItems.add(new PlaceItem(content));

                        set = 0;
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }

                onReady();
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
    private void SaveKakao(){
        SharedPreferences pref = getActivity().getSharedPreferences("kakao", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("nick",kakaoNickname);
        editor.putString("id",kakaoID);
        GlobalApplication.getGlobalApplicationContext().setKakaoID(kakaoID);
        GlobalApplication.getGlobalApplicationContext().setKakaoNickname(kakaoNickname);
        editor.commit();
    }

    private void LoadKakao(){
        SharedPreferences pref =  getActivity().getSharedPreferences("kakao",Activity.MODE_PRIVATE);
        kakaoID = pref.getString("id","");
        kakaoNickname=pref.getString("nick","");
    }

    @Override
    public void onDestroy() {
        SaveKakao();
        super.onDestroy();
    }
}