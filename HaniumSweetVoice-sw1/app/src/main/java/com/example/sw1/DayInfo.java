package com.example.sw1;

public class DayInfo {
    private String day;
    private String month;
    private String year;
    private boolean inMonth;
    private boolean startSwitch;



    public boolean getStartSwitch(){
        return startSwitch;
    }
    /**
     * 날짜를 반환한다.
     *
     * @return day 날짜
     */
    public String getDay()
    {
        return day;
    }

    public String getMonth(){
        return month;
    }


    public String getYear(){
        return year;
    }



    public void setStartSwitch(boolean startSwitch){
        this.startSwitch=startSwitch;
    }
    /**
     * 날짜를 저장한다.
     *
     * @param day 날짜
     */
    public void setDay(String day)
    {
        this.day = day;
    }

    public void setMonth(String month){
        this.month=month;
    }

    public void setYear(String year){
        this.year=year;
    }
    /**
     * 이번달의 날짜인지 정보를 반환한다.
     *
     * @return inMonth(true/false)
     */
    public boolean isInMonth()
    {
        return inMonth;
    }

    /**
     * 이번달의 날짜인지 정보를 저장한다.
     *
     * @param inMonth(true/false)
     */
    public void setInMonth(boolean inMonth)
    {
        this.inMonth = inMonth;
    }

}