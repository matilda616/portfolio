package com.example.sw1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by user on 2016-06-22.
 */
public class DeletePopupActivity extends Activity implements View.OnClickListener {
    Button deleteCancel;
    Button deleteConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_delete);

        deleteCancel = (Button) findViewById(R.id.deleteCancel);
        deleteCancel.setOnClickListener(this);
        deleteConfirm = (Button) findViewById(R.id.deleteConfirm);
        deleteConfirm.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteCancel:
                //   MyGlobals.getInstance().setData(WifiInfo);
                finish();
                break;
            case R.id.deleteConfirm:
                finish();
                break;

        }
    }
}