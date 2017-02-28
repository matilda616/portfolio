package com.example.user.finder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayList<String> idArray = new ArrayList<String>();
    ArrayList<String> imageArray = new ArrayList<String>();
    ArrayList<String> textArray = new ArrayList<String>();
    ArrayList<String> phoneArray = new ArrayList<String>();
    ArrayList<String> placeArray = new ArrayList<String>();

    ArrayList<IdItem> idItems = new ArrayList<IdItem>(); // ID 저장
    ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>(); // 이미지 저장
    ArrayList<TextItem> textItems = new ArrayList<TextItem>(); // 글 저장
    ArrayList<PhoneItem> phoneItems = new ArrayList<PhoneItem>(); // 연락처 저장
    ArrayList<PlaceItem> placeItems = new ArrayList<PlaceItem>(); // 전달 여부 저장

    String s_text;
    GridView gridView;
    ImageView imageView;
    TextView textView;

    private final String SERVER_ADDRESS = "http://finder.dothome.co.kr";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent it = getIntent();
        s_text = it.getStringExtra("s_text");

        setTitle("'"+ s_text + "'" + " 검색 결과");

        // 웹 서버 연동
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(SERVER_ADDRESS + "/found_search.php?"
                            + "search=" + URLEncoder.encode(s_text, "UTF-8"));
                    url.openStream();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "found select error" + e, Toast.LENGTH_LONG).show();
                }
            }
        });

        String strUrl = "http://finder.dothome.co.kr/found_search_result.xml";
        new DownloadWebpageTask().execute(strUrl);

        gridView = (GridView) findViewById(R.id.image_grid_search);
    }

    public class gridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public gridAdapter() {
            inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                convertView = inflater.inflate(R.layout.activity_search_layout, parent, false);
            }

            imageView = (ImageView)convertView.findViewById(R.id.search_image);
            textView = (TextView)convertView.findViewById(R.id.search_found);

            final String text = textArray.get(position).toString();
            final String image = imageArray.get(position).toString();
            final String phone = phoneArray.get(position).toString();
            final String place = placeArray.get(position).toString();
            String URL = "http://finder.dothome.co.kr/found_images/" + image;

            // 이미지 설정
            // Create an object for subclass of AsyncTask
            GetXMLTask task = new GetXMLTask();
            // Execute the task
            // task.execute(new String[] { URL });
            task.onPostExecute(task.doInBackground(new String[] { URL }));

            // 글 설정
            textView.setText(text);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle mBundle = new Bundle();
                    Intent it = new Intent(getApplicationContext(), Search_Board1Activity.class);

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

    // 디비에서 이미지 이름 불러오기
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
                        else if(tag_name.equals("foundPlace"))
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
                        else if(set == 5)
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

    public void onReady() {
        idArray.clear();
        for(int i = textItems.size() - 1; i >= 0; i--) {
            idArray.add(idItems.get(i).toString());
        }

        imageArray.clear();
        for(int i = textItems.size() - 1; i >= 0; i--) {
            imageArray.add(imageItems.get(i).toString());
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
        for(int i = textItems.size() - 1; i >= 0; i--){
            placeArray.add(placeItems.get(i).toString());
        }

        gridView.setAdapter(new gridAdapter());
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
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                // stream.close();
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

    public void onBackClicked(View v) {
        finish();
    }
}