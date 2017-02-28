package com.example.user.finder;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

/**
 * Created by swu19 on 2016-11-18.
 */
public class GlobalApplication  extends Application {
    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;
    String kakaoID="";
    String kakaoNickname="";
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public String getKakaoNickname()
    {
        return kakaoNickname;
    }

    public void setKakaoNickname(String nickname)
    {
        this.kakaoNickname = nickname;
    }

    public String getKakaoID() {
        return kakaoID;
    }

    public void setKakaoID(String kakaoID) {
        this.kakaoID = kakaoID;
    }
}
