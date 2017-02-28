package com.example.user.finder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Search_Board1Activity extends AppCompatActivity {
    ImageView iv_board_found;
    TextView tv_text_found;
    TextView foundPlace;

    String image;
    String text;
    String receiver;
    String place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("습득물");

        Bundle extras = getIntent().getExtras();
        image = extras.getString("image");
        text = extras.getString("text");
        receiver = extras.getString("phone");
        place = extras.getString("place");

        iv_board_found = (ImageView)findViewById(R.id.found_board_image);
        tv_text_found = (TextView)findViewById(R.id.found_board_text);
        foundPlace = (TextView)findViewById(R.id.found_board_deliver);

        tv_text_found.setText(text);
        foundPlace.setText(place);

        // 이미지 설정
        String URL = "http://finder.dothome.co.kr/found_images/" + image;

        // Create an object for subclass of AsyncTask
        GetXMLTask task = new GetXMLTask();
        // Execute the task
        task.execute(new String[] { URL });

        Button b_message = (Button)findViewById(R.id.found_board_message);
        if (place.equals("맡길게요 (총학생회실)"))
            b_message.setVisibility(View.GONE);
        else {
            b_message.setOnClickListener(new View.OnClickListener() {
                @Override
                // 습득자가 직접 전달 선택했을 때 뜨는 버튼
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(Search_Board1Activity.this);
                    alert.setTitle("습득자에게 문자 보내기");
                    alert.setMessage("습득자에게 문자를 전송합니다!");
                    alert.setIcon(R.mipmap.icon);

                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alert.setPositiveButton("전송", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String message = "SWU파인더 알림!" + "\n" + "글 내용 : [" + text + "]\n" + "제가 습득물 주인이에요!";

                            // http://itmir.tistory.com/458
                            PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
                            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

                            registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    switch (getResultCode()) {
                                        case Activity.RESULT_OK:
                                            // 전송 성공
                                            Toast.makeText(getApplicationContext(), "전송 완료", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                            // 전송 실패
                                            Toast.makeText(getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                                            // 서비스 지역 아님
                                            Toast.makeText(getApplicationContext(), "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                                            // 무선 꺼짐
                                            Toast.makeText(getApplicationContext(), "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_NULL_PDU:
                                            // PDU 실패
                                            Toast.makeText(getApplicationContext(), "PDU Null", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            }, new IntentFilter("SMS_SENT_ACTION"));

                            registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    switch (getResultCode()) {
                                        case Activity.RESULT_OK:
                                            // 도착 완료
                                            Toast.makeText(getApplicationContext(), "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                                            break;
                                        case Activity.RESULT_CANCELED:
                                            // 도착 실패
                                            Toast.makeText(getApplicationContext(), "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            }, new IntentFilter("SMS_DELIVERED_ACTION"));

                            SmsManager mSmsManager = SmsManager.getDefault();
                            mSmsManager.sendTextMessage(receiver, null, message, sentIntent, deliveredIntent);
                        }
                    });

                    alert.show();
                }
            });
        }
    }

    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }

            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            iv_board_found.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

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
            URLConnection connection = url.openConnection();

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
}