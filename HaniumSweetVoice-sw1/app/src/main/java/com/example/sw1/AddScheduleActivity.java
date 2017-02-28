package com.example.sw1;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddScheduleActivity extends AppCompatActivity {
    // http://gtothe1.tistory.com/entry/%EB%82%A0%EC%A7%9C%EC%99%80-%EC%8B%9C%EA%B0%84-DatePickerDialog
    private static final String TAG = "AddScheduleActivity";
    int year, month, day, hour, minute;
    Switch scheduleSound;
    String schedule_sound;

    String schedule_name;
    String schedule_time2;
    String schedule_time3;
    String schedule_repeat = "";    //반복여부
    String schedule_interval = "";  //알람간격


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule);
        setTitle("Add Schedule");

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        findViewById(R.id.datepicker1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddScheduleActivity.this, dateSetListener, year, month, day).show();
            }
        });

        findViewById(R.id.timepicker1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView alarm = (TextView)findViewById(R.id.scheduleTime);

                if (alarm.getText().toString() == "")
                    Toast.makeText(AddScheduleActivity.this, "날짜부터 선택해주세요.", Toast.LENGTH_LONG).show();

                else {
                    // TODO Auto-generated method stub
                    new TimePickerDialog(AddScheduleActivity.this, timeSetListener, hour, minute, false).show();
                }
            }
        });

        // http://www.mysamplecode.com/2013/04/android-switch-button-example.html
        scheduleSound = (Switch)findViewById(R.id.scheduleSound);
        scheduleSound.setChecked(true);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            String msg = String.format("%d년 %d월 %d일 ", year, monthOfYear + 1, dayOfMonth);
            // Toast.makeText(AddScheduleActivity.this, msg, Toast.LENGTH_SHORT).show();

            schedule_time2 = String.format("%d%d%d",year,monthOfYear+1,dayOfMonth);

            TextView alarm = (TextView)findViewById(R.id.scheduleTime);
            alarm.setText(msg);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            String msg = String.format("%d시 %d분", hourOfDay, minute);
            // Toast.makeText(AddScheduleActivity.this, msg, Toast.LENGTH_SHORT).show();

            schedule_time3 = String.format("%d%d",hourOfDay,minute);

            TextView alarm = (TextView)findViewById(R.id.scheduleTime);
            alarm.append(msg);
        }
    };

    //저장버튼 클릭
    public void scheduleSave(View v) {
        EditText editText = (EditText)findViewById(R.id.scheduleName);
        schedule_name = editText.getText().toString();   //스케쥴이름

        TextView textView = (TextView)findViewById(R.id.scheduleTime);
        String schedule_time = textView.getText().toString();   //스케쥴 시간 년월일시분

        RadioGroup rg_interval = (RadioGroup)findViewById(R.id.rg_scheduleInterval);
        RadioButton rb_noInterval = (RadioButton)findViewById(R.id.nosInterval);
        RadioButton rb_Interval3 = (RadioButton)findViewById(R.id.sInterval3);
        RadioButton rb_Interval5 = (RadioButton)findViewById(R.id.sInterval5);
        RadioButton rb_Interval10 = (RadioButton)findViewById(R.id.sInterval10);

        RadioGroup rg_repeat = (RadioGroup)findViewById(R.id.rg_scheduleRepeat);
        RadioButton rb_Repeat1 = (RadioButton)findViewById(R.id.sRepeat1);
        RadioButton rb_Repeat3 = (RadioButton)findViewById(R.id.sRepeat3);
        RadioButton rb_Repeat5 = (RadioButton)findViewById(R.id.sRepeat5);
        RadioButton rb_Repeat10 = (RadioButton)findViewById(R.id.sRepeat10);

        schedule_interval = "";  //알람간격
        if (rg_interval.getCheckedRadioButtonId() == R.id.nosInterval) {
            schedule_interval = rb_noInterval.getText().toString();
        }

        else if (rg_interval.getCheckedRadioButtonId() == R.id.sInterval3) {
            schedule_interval = rb_Interval3.getText().toString();
        }

        else if (rg_interval.getCheckedRadioButtonId() == R.id.sInterval5) {
            schedule_interval = rb_Interval5.getText().toString();
        }

        else if (rg_interval.getCheckedRadioButtonId() == R.id.sInterval10) {
            schedule_interval = rb_Interval10.getText().toString();
        }

        schedule_repeat = "";    //반복여부
        if (rg_repeat.getCheckedRadioButtonId() == R.id.sRepeat1) {
            schedule_repeat = rb_Repeat1.getText().toString();
        }

        else if (rg_repeat.getCheckedRadioButtonId() == R.id.sRepeat3) {
            schedule_repeat = rb_Repeat3.getText().toString();
        }

        else if (rg_repeat.getCheckedRadioButtonId() == R.id.sRepeat5) {
            schedule_repeat = rb_Repeat5.getText().toString();
        }

        else if (rg_repeat.getCheckedRadioButtonId() == R.id.sRepeat10) {
            schedule_repeat = rb_Repeat10.getText().toString();
        }

        if(scheduleSound.isChecked())
            schedule_sound = "1";

        else
            schedule_sound = "0";


    }


    public void scheduleCancel(View v) {
        finish();
    }


}