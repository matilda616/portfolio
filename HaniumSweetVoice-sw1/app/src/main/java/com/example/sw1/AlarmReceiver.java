package com.example.sw1;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

/**
 * Created by user on 2016-07-15.
 */
public class AlarmReceiver extends Activity implements View.OnClickListener {

    private AlarmManager mAlarmMgr;
    Button AlarmDeleteCancel;
    Button AlarmDeleteConfirm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_receive_layout);

        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        for (int i = 0; i < 10; i++) {
            vibe.vibrate(new long[]{100, 300, 100, 500, 100, 300, 100, 500, 100, 300, 100, 500, 100, 300, 100, 500, 100, 300, 100, 2500}, -1);
        }

        AlarmDeleteConfirm = (Button) findViewById(R.id.AlarmDeleteConfirm);
        AlarmDeleteConfirm.setOnClickListener(this);
        AlarmDeleteCancel = (Button) findViewById(R.id.AlarmDeleteCancel);
        AlarmDeleteCancel.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AlarmDeleteCancel:
                //   MyGlobals.getInstance().setData(WifiInfo);
                finish();
                break;
            case R.id.AlarmDeleteConfirm:
                onBtnStop();
                break;

        }
    }

    // 알람 중지
    public void onBtnStop() {
        // 수행할 동작을 생성
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // 알람 중지
        mAlarmMgr.cancel(pIntent);
    }
}