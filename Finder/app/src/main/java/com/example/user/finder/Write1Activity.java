package com.example.user.finder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class Write1Activity extends AppCompatActivity {
    private final String SERVER_ADDRESS = "http://finder.dothome.co.kr";
    String upLoadServerUri = "http://finder.dothome.co.kr/upload_found_image.php";

    public static OAuthLogin mOAuthLoginInstance;
    public static Context mContext;
    public String email; // 아이디 저장 변수
    String kakaoID="";     //카카오아이디


    // Image File Path
    String uploadFilePath;
    String img;
    int serverResponseCode = 0;
    private static int RESULT_LOAD_IMAGE = 100;

    MainActivity ma = (MainActivity) MainActivity.mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write1);

        setTitle("습득물");

        mOAuthLoginInstance = OAuthLogin.getInstance();

        //카카오톡 아이디 셋팅
        kakaoID = GlobalApplication.getGlobalApplicationContext().getKakaoID();
        email = kakaoID+"@kakao";

        //네이버로그인 경우
        if(kakaoID.equals("")){
            new RequestApiTask().execute(); // 네이버 아이디 셋팅
        }


        // http://viralpatel.net/blogs/pick-image-from-galary-android-app/
        // http://ankyu.entersoft.kr/Lecture/android/gallery_01.asp
        Button buttonLoadImage = (Button) findViewById(R.id.gallery);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent it = new Intent(Intent.ACTION_PICK);
                it.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                it.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(it, RESULT_LOAD_IMAGE);
            }
        });

        // 게시글
        final EditText foundText = (EditText)findViewById(R.id.found_text);

        // 연락처
        final EditText foundPhone = (EditText)findViewById(R.id.found_phone);

        // 전달 여부
        final RadioButton foundPlace1 = (RadioButton)findViewById(R.id.direct);
        final RadioButton foundPlace2 = (RadioButton)findViewById(R.id.indirect);

        // 저장 버튼
        Button foundSave = (Button)findViewById(R.id.found_save);
        foundSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) findViewById(R.id.found_image);

                // 총학생회실에 맡길게요; TEXT, PLACE
                if (foundText.getText().toString().equals("") || (!foundPlace1.isChecked() && !foundPlace2.isChecked())) {
                    Toast.makeText(getApplicationContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return ;
                }

                // 직접 줄게요; TEXT, PHONE, PLACE
                else if (foundPlace1.isChecked() && foundPhone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return ;
                }

                // http://www.androidside.com/bbs/board.php?bo_table=b49&wr_id=140644
                else if(imageView.getDrawable() == null)
                    img = "img_swu.jpg";

                else {
                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(uploadFilePath);
                        }
                    }).start();
                }

                // 웹 서버 연동
                runOnUiThread(new Runnable() {
                    public void run() {
                        String text = foundText.getText().toString();
                        String phone;
                        if (foundPhone.getText().toString().equals(""))
                            phone = "0";
                        else
                            phone = foundPhone.getText().toString();

                        String place = "";
                        if (foundPlace1.isChecked())
                            place = foundPlace1.getText().toString();
                        else if (foundPlace2.isChecked())
                            place = foundPlace2.getText().toString();

                        try {
                            URL url = new URL(SERVER_ADDRESS + "/found_insert.php?"
                                    + "foundid=" + URLEncoder.encode(email, "UTF-8")
                                    + "&foundimage=" + URLEncoder.encode(img, "UTF-8")
                                    + "&foundtext=" + URLEncoder.encode(text, "UTF-8")
                                    + "&foundphone=" + URLEncoder.encode(phone, "UTF-8")
                                    + "&foundplace=" + URLEncoder.encode(place, "UTF-8"));
                            url.openStream();

                            String result = getXmlData("found_insert_result.xml", "result");

                            if (result.equals("1")) {
                                Toast.makeText(getApplicationContext(), "DB insert 성공", Toast.LENGTH_SHORT).show();

                                // http://docko.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-androidosNetworkOnMainThreadException
                                new Thread() {
                                    public void run() {
                                        try {
                                            // http://i5on9i.blogspot.kr/2016/08/firebase-cloud-message-topic-messaging.html
                                            String text = foundText.getText().toString();

                                            URL url = new URL("http://finder.dothome.co.kr/alarm_push.php?"
                                                    + "foundText=" + URLEncoder.encode(text, "UTF-8"));
                                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                            conn.connect();
                                            Log.e("response", conn.getResponseMessage());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                                ma.finish();
                                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(it);
                                finish();
                            } else
                                Toast.makeText(getApplicationContext(), "DB insert 실패", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "DB insert ERROR" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 600 * 600;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            // Toast.makeText(MainActivity.this, "Source File not exist", Toast.LENGTH_SHORT).show();

            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + img + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bufferSize = 1*700*700;
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Write1Activity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Write1Activity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Write1Activity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return serverResponseCode;
        } // End else block
    }

    // 이미지 URI
    public String getImagePathToUri(Uri data) {
        String display_name = "";
        Cursor cursor = getContentResolver().query(data, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
            display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        }

        cursor.close();
        return display_name;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ImageView imageView = (ImageView) findViewById(R.id.found_image);
                imageView.setImageBitmap(bitmap);

                uploadFilePath = getImagePathToUri(data.getData());
                img = createFilename();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime()) + email + ".jpg";

        return curDateStr;
    }

    // 이메일 구하기
    public class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            ParsingVersion(mOAuthLoginInstance.requestApi(mContext, at, url));

            return email;
        }

        protected void onPostExecute(String content) {
            email = (String)content;
            Toast.makeText(getApplicationContext(),email,Toast.LENGTH_LONG).show();
        }
    }

    // 아이디 파싱
    public void ParsingVersion(String data) {
        String f_array[] = new String[9];

        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory
                    .newInstance();
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

    public void onBackClicked(View v){
        finish();
    }

    public void onBackPressed() {
        Intent it = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(it);
        finish();
    }


}