package com.github.yftx.FloatDemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by yftx on 8/19/14.
 */
public class FuntionUiActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.funtion_ui);
    }
}