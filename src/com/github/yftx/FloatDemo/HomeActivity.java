package com.github.yftx.FloatDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                start();
                break;
            case R.id.stop:
                stop();
                break;
        }
    }

    private void stop() {
        stopService(new Intent(this, TouchDetectService.class));
    }

    private void start() {
        startService(new Intent(this, TouchDetectService.class));
    }
}
