/*
 * SidePanel Application For Android
 * Copyright 2013 Young Bin Han
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.yftx.FloatDemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class TouchDetectService extends Service {
    private ImageView mTouchDetector;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private static int VIBRATE_DURATION = 200;
    private static final String TAG = "TouchDetectService";

    private OnTouchListener mViewTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startVibrator(VIBRATE_DURATION);
                    openActivity(FuntionUiActivity.class);

            }
            return true;
        }
    };

    @Override
    public IBinder onBind(Intent agr0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTouchDetector = new ImageView(this);
        mTouchDetector.setImageResource(R.drawable.ic_float_menu);

        int orgWidth = mTouchDetector.getDrawable().getIntrinsicWidth();
        int orgHeight = mTouchDetector.getDrawable().getIntrinsicHeight();

        mTouchDetector.setOnTouchListener(mViewTouchListener);
        mParams = new WindowManager.LayoutParams(
                orgWidth,
                orgHeight,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.LEFT | Gravity.CENTER;
        mTouchDetector.setScaleType(ImageView.ScaleType.FIT_XY);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mTouchDetector, mParams);
    }


    @Override
    public void onDestroy() {
        if (mWindowManager != null) {
            if (mTouchDetector != null) mWindowManager.removeView(mTouchDetector);
        }
        super.onDestroy();
    }


    private void openActivity(Class clazz) {
        Intent intent = new Intent(getBaseContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }

    private void startVibrator(int vibratorDuration) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(vibratorDuration);
    }
}