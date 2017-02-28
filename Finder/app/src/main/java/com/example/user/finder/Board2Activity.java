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

public class Board2Activity extends AppCompatActivity {
    ImageView iv_board_lost;
    TextView tv_text_lost;

    String text; // 글 내용
    String receiver; // 작성자 전화 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("분실물");

        Bundle extras = getIntent().getExtras();
        String image = extras.getString("image");
        text = extras.getString("text");
        receiver = extras.getString("phone");

        iv_board_lost = (ImageView)findViewById(R.id.find_board_image);
        tv_text_lost = (TextView)findViewById(R.id.find_board_text);

        // 글 내용 설정
        tv_text_lost.setText(text);

        // 이미지 설정
        String URL = "http://finder.dothome.co.kr/lost_images/" + image;

        // Create an object for subclass of AsyncTask
        GetXMLTask task = new GetXMLTask();
        // Execute the task
        task.execute(new String[] { URL });

        // 직접 전달할게요
        Button b_direct = (Button)findViewById(R.id.find_board_direct);
        b_direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Board2Activity.this);
                alert.setTitle("분실물 주인에게 문자 보내기");
                alert.setMessage("분실물 주인에게 문자를 전송합니다!");
                alert.setIcon(R.drawable.icon);

                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alert.setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message1;

                        if (text.length() > 15)
                            message1 = "SWU파인더 알림!" +  "\n" +"글 내용 : [" + text.substring(0, 15) + "....]\n" +"습득자가 게시글을 확인했습니다. 직접 전달할게요!";
                        else
                            message1 = "SWU파인더 알림!" +  "\n" +"글 내용 : [" + text + "]\n" +"습득자가 게시글을 확인했습니다. 직접 전달할게요!";
                        // http://itmir.tistory.com/458
                        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
                        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

                        registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                switch(getResultCode()){
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
                                switch (getResultCode()){
                                    case Activity.RESULT_OK:
                                        // 도착 완료
                                        Toast.makeText(getApplicationContext(), "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                                        break;
                                    case Activity.RESULT_CANCELED:
                                        // 도착 안됨
                                        Toast.makeText(getApplicationContext(), "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }, new IntentFilter("SMS_DELIVERED_ACTION"));

                        SmsManager mSmsManager = SmsManager.getDefault();
                        mSmsManager.sendTextMessage(receiver, null, message1, sentIntent, deliveredIntent);
                    }
                });

                alert.show();
            }
        });

        // 학생지원팀에 맡길게요
        Button b_indirect = (Button)findViewById(R.id.find_board_indirect);
        b_indirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Board2Activity.this);
                alert.setTitle("분실물 주인에게 문자 보내기");
                alert.setMessage("분실물 주인에게 문자를 전송합니다!");
                alert.setIcon(R.drawable.icon);

                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alert.setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message2;

                        if (text.length() > 8)
                            message2 = "SWU파인더 알림!" +"\n" + "글 내용 : [" + text.substring(0, 8) + "....] \n"  +"습득자가 게시글을 확인했습니다. 분실물은 학생지원팀에 맡겨집니다!";
                        else
                            message2 = "SWU파인더 알림!" +"\n" + "글 내용 : [" + text + "] \n"  +"습득자가 게시글을 확인했습니다. 분실물은 학생지원팀에 맡겨집니다!";

                        // http://itmir.tistory.com/458
                        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
                        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

                        registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                switch(getResultCode()){
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
                                switch (getResultCode()){
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
                        mSmsManager.sendTextMessage(receiver, null, message2, sentIntent, deliveredIntent);
                    }
                });

                alert.show();
            }
        });
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
            iv_board_lost.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
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

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}