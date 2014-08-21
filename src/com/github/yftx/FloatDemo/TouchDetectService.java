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
import android.content.Intent;
import android.os.IBinder;
import com.github.yftx.FloatDemo.widget.FloatView;

public class TouchDetectService extends Service {


    private FloatView mFloatView;

    @Override
    public IBinder onBind(Intent agr0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatView = new FloatView(this);
        mFloatView.setOnClickListener(new FloatView.OnClickListener() {
            @Override
            public void onClick() {
                openActivity(FuntionUiActivity.class);
            }
        });
    }


    @Override
    public void onDestroy() {
        if (mFloatView == null) return;
        mFloatView.destory();
        super.onDestroy();
    }


    private void openActivity(Class clazz) {
        Intent intent = new Intent(getBaseContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }

}