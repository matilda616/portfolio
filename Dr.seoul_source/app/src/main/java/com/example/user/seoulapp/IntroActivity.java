package com.example.user.seoulapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

/**
 * Created by swu19 on 2016-10-09.
 */
public class IntroActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(checkNetwokState()) {
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);

                    startActivity(intent);
                    finish();
                }else{

                }
            }
        }, 1000);


    }

    public boolean checkNetwokState() {
        ConnectivityManager manager =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;
        if(lte_4g != null)
            blte_4g = lte_4g.isConnected();
        if (mobile.isConnected() || wifi.isConnected() || blte_4g)
            return true;
        else {
            AlertDialog.Builder dlg = new AlertDialog.Builder(IntroActivity.this);
            dlg.setTitle("NETWORK ERROR");
            dlg.setMessage("Please check your network! ");
            dlg.setIcon(R.drawable.icon);
            dlg.setNegativeButton("close", new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            dlg.setCancelable(false);
            dlg.show();

            return false;
        }
    }



}
