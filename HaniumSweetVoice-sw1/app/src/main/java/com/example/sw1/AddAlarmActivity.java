package com.example.sw1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddAlarmActivity extends AppCompatActivity {
    // http://gtothe1.tistory.com/entry/%EB%82%A0%EC%A7%9C%EC%99%80-%EC%8B%9C%EA%B0%84-DatePickerDialog
    int year, month, day, hour, minute;
    Switch alarmSound;
    String alarm_sound;
    //sharedPref
    public static String PREFS_NAME = "MyPref";
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int timetomili;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm);
        setTitle("Add Alarm");

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        //sharedPref
        prefs = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        editor = settings.edit();

        findViewById(R.id.datepicker2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddAlarmActivity.this, dateSetListener, year, month, day).show();
            }
        });

        findViewById(R.id.timepicker2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView alarm = (TextView)findViewById(R.id.alarmTime);

                if (alarm.getText().toString() == "")
                    Toast.makeText(AddAlarmActivity.this, "날짜부터 선택해주세요.", Toast.LENGTH_LONG).show();

                else {
                    // TODO Auto-generated method stub
                    new TimePickerDialog(AddAlarmActivity.this, timeSetListener, hour, minute, false).show();
                }
            }
        });

        // http://www.mysamplecode.com/2013/04/android-switch-button-example.html
        alarmSound = (Switch)findViewById(R.id.alarmSound);
        alarmSound.setChecked(true);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            String msg = String.format("%d년 %d월 %d일 ", year, monthOfYear + 1, dayOfMonth);
            // Toast.makeText(AddAlarmActivity.this, msg, Toast.LENGTH_SHORT).show();

            TextView alarm = (TextView)findViewById(R.id.alarmTime);
            alarm.setText(msg);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            String msg = String.format("%d시 %d분", hourOfDay, minute);
            // Toast.makeText(AddAlarmActivity.this, msg, Toast.LENGTH_SHORT).show();

            TextView alarm = (TextView)findViewById(R.id.alarmTime);
            alarm.append(msg);
        }
    };

    public void alarmSave(View v) {
        Spinner spinner = (Spinner)findViewById(R.id.alarmName);
        String alarm_name = spinner.getSelectedItem().toString();

        TextView textView = (TextView)findViewById(R.id.alarmTime);
        String alarm_time = textView.getText().toString();

        RadioGroup rg_interval = (RadioGroup)findViewById(R.id.rg_alarmInterval);
        RadioButton rb_noInterval = (RadioButton)findViewById(R.id.noaInterval);
        RadioButton rb_Interval3 = (RadioButton)findViewById(R.id.aInterval3);
        RadioButton rb_Interval5 = (RadioButton)findViewById(R.id.aInterval5);
        RadioButton rb_Interval10 = (RadioButton)findViewById(R.id.aInterval10);

        RadioGroup rg_repeat = (RadioGroup)findViewById(R.id.rg_alarmRepeat);
        RadioButton rb_Repeat1 = (RadioButton)findViewById(R.id.aRepeat1);
        RadioButton rb_Repeat3 = (RadioButton)findViewById(R.id.aRepeat3);
        RadioButton rb_Repeat5 = (RadioButton)findViewById(R.id.aRepeat5);
        RadioButton rb_Repeat10 = (RadioButton)findViewById(R.id.aRepeat10);

        String alarm_interval = "";
        if (rg_interval.getCheckedRadioButtonId() == R.id.noaInterval) {
            alarm_interval = rb_noInterval.getText().toString();
        }

        else if (rg_interval.getCheckedRadioButtonId() == R.id.aInterval3) {
            alarm_interval = rb_Interval3.getText().toString();
        }

        else if (rg_interval.getCheckedRadioButtonId() == R.id.aInterval5) {
            alarm_interval = rb_Interval5.getText().toString();
        }

        else if (rg_interval.getCheckedRadioButtonId() == R.id.aInterval10) {
            alarm_interval = rb_Interval10.getText().toString();
        }

        String alarm_repeat = "";
        if (rg_repeat.getCheckedRadioButtonId() == R.id.aRepeat1) {
            alarm_repeat = rb_Repeat1.getText().toString();
        }

        else if (rg_repeat.getCheckedRadioButtonId() == R.id.aRepeat3) {
            alarm_repeat = rb_Repeat3.getText().toString();
        }

        else if (rg_repeat.getCheckedRadioButtonId() == R.id.aRepeat5) {
            alarm_repeat = rb_Repeat5.getText().toString();
        }

        else if (rg_repeat.getCheckedRadioButtonId() == R.id.aRepeat10) {
            alarm_repeat = rb_Repeat10.getText().toString();
        }

        if(alarmSound.isChecked())
            alarm_sound = "On";

        else
            alarm_sound = "Off";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save Alarm");
        String message_popup="Name : " + alarm_name + "\nTime : " + alarm_time
                + "\nInterval : " + alarm_interval + "\nRepeat : " + alarm_repeat
                + "\nSound : " + alarm_sound;

        alert.setMessage(message_popup);
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setPositiveButton("확인", null);
        alert.show();
        String pre_message_name;
        String pre_message_time;
        String pre_message_interval;
        String pre_message_repeat;
        String pre_message_sound;
        try {
            pre_message_name = prefs.getString("message_send_name", "");
            pre_message_time = prefs.getString("message_send_time", "");
            pre_message_interval = prefs.getString("message_send_interval", "");
            pre_message_repeat = prefs.getString("message_send_repeat", "");
            pre_message_sound = prefs.getString("message_send_sound", "");
            editor.putString("message_send_name", alarm_name + "\n"+pre_message_name);
            editor.putString("message_send_time", alarm_time + "\n"+pre_message_time);
            editor.putString("message_send_interval",  "Interval : " + alarm_interval + "\n"+pre_message_interval);
            editor.putString("message_send_repeat",  "Repeat : " + alarm_repeat + "\n"+pre_message_repeat);
            editor.putString("message_send_sound", "Sound : " +  alarm_sound + "\n"+pre_message_sound);

            editor.commit();
        }catch (Exception e) {
            editor.putString("message_send_name", alarm_name + "\n");
            editor.putString("message_send_time", alarm_time + "\n");
            editor.putString("message_send_interval", "Interval : " + alarm_interval + "\n");
            editor.putString("message_send_repeat", "Repeat : "+ alarm_repeat + "\n");
            editor.putString("message_send_sound","Sound : "+  alarm_sound + "\n");

            editor.commit();
        }


    }

    public void alarmCancel(View v) {
        finish();
    }
}