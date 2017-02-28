package com.example.sw1;

/**
 * Created by user on 2016-05-22.
 */

/**
 * Created by user on 2016-05-22.
 */
public class ItemInCardViewFreg3 {
    private int img;
    private String AlarmName;
    private String AlarmTime;
    private String AlarmInterval;
    private String AlarmRepeat;
    private String AlarmSound;
    private String Status;


    public ItemInCardViewFreg3(int img, String AlarmName, String AlarmTime, String AlarmInterval, String AlarmRepeat, String AlarmSound, String Status) {
        this.img = img;
        this.AlarmName = AlarmName;
        this.AlarmTime = AlarmTime;
        this.AlarmInterval = AlarmInterval;
        this.AlarmRepeat = AlarmRepeat;
        this.AlarmSound = AlarmSound;
        this.Status = Status;

    }

    public String getAlarmName() {
        return AlarmName;
    }

    public String getAlarmTime() {
        return AlarmTime;
    }

    public int getImg() {
        return img;
    }

    public String getAlarmInterval() {
        return AlarmInterval;
    }

    public String getAlarmRepeat() {
        return AlarmRepeat;
    }

    public String getAlarmSound() {
        return AlarmSound;
    }

    public String getStatus() {
        return Status;
    }
}

//[출처] 분홍뱀의 카드뷰 만들기~!(RecyclerView + CardView)|작성자 pinkysnake