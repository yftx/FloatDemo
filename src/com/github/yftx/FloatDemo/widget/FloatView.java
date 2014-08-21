package com.github.yftx.FloatDemo.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import com.github.yftx.FloatDemo.R;
import com.github.yftx.FloatDemo.utils.LogUtils;

public class FloatView extends ImageView implements View.OnClickListener {
    public static boolean isShow = true;
    private static int VIBRATE_DURATION = 200;
    private final int viewDpWidth = 50;
    private final String FLOAT_X = "float_x";
    private final String FLOAT_Y = "float_Y";
    boolean isAnim = false;
    private float[] temp = new float[4];
    private LayoutParams wLayoutParams;
    private WindowManager mWindowManager;
    private boolean viewAdded = false;
    private int statusBarHeight = 0;
    private int lastAction;
    private int minMove = 10;
    private int screenWidth, screenHeight;
    private int viewWidth;
    private OnClickListener mOnClickListener;

    public FloatView(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wLayoutParams = new LayoutParams();
        wLayoutParams.width = getViewWidth();
        wLayoutParams.height = getViewWidth();
        wLayoutParams.format = PixelFormat.TRANSLUCENT;
        wLayoutParams.type = LayoutParams.TYPE_PHONE;
        wLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        wLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        setImageResource(R.drawable.ic_float_menu);
        init();
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    private int getViewWidth() {
        if (viewWidth == 0) {
            viewWidth = dpToPx(viewDpWidth, getContext());
        }
        return viewWidth;
    }

    private void init() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        int x = sp.getInt(FLOAT_X, 0);
        int y = sp.getInt(FLOAT_Y, 0);
        if (x > 0 && x != screenWidth - getViewWidth()) {
            x = screenWidth - getViewWidth();
        }
        if (y > screenHeight) {
            y = screenHeight - getViewWidth();
        }

        wLayoutParams.x = x;
        wLayoutParams.y = y;
        refresh();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        isAnim = true;
        postInvalidate();
        super.onConfigurationChanged(newConfig);
    }

    public void destory() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        sp.edit().putInt(FLOAT_X, wLayoutParams.x)
                .putInt(FLOAT_Y, wLayoutParams.y).commit();
        try {
            mWindowManager.removeViewImmediate(this);
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getVisibility() != View.VISIBLE)
            return super.onTouchEvent(event);
        if (isAnim)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startVibrator(VIBRATE_DURATION);
                temp[0] = event.getX();
                temp[1] = event.getY();
                temp[2] = event.getX() - getLeft();
                temp[3] = event.getY() - getTop();
                lastAction = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    if (Math.abs(event.getX() - temp[0]) > minMove
                            || Math.abs(event.getY() - temp[1]) > minMove) {
                        lastAction = MotionEvent.ACTION_MOVE;
                        refreshView((int) event.getRawX(), (int) event.getRawY());
                    }
                } else {
                    refreshView((int) event.getRawX(), (int) event.getRawY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    onClick(this);
                } else if (wLayoutParams.x != 0
                        && wLayoutParams.x != screenWidth - getWidth()) {
                    isAnim = true;
                    postInvalidate();
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isAnim) {
            if (wLayoutParams.x == 0
                    || wLayoutParams.x == screenWidth - getWidth()) {
                isAnim = false;
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                sp.edit().putInt(FLOAT_X, wLayoutParams.x)
                        .putInt(FLOAT_Y, wLayoutParams.y).commit();
            } else {
                if (wLayoutParams.x < screenWidth / 2) {
                    wLayoutParams.x -= 15;
                    if (wLayoutParams.x <= 0) {
                        wLayoutParams.x = 0;
                    }
                    refresh();
                } else {
                    wLayoutParams.x += 15;
                    if (wLayoutParams.x > screenWidth - getWidth()) {
                        wLayoutParams.x = screenWidth - getWidth();
                    }
                    refresh();
                }
                invalidate();
            }
        }
        super.onDraw(canvas);
    }

    /**
     * 刷新悬浮窗
     *
     * @param x 当前X轴绝对坐标
     * @param y 当前的Y轴绝对坐标
     */
    private void refreshView(int x, int y) {
        // 状态栏高度不能立即取，不然得到的值是0
        if (statusBarHeight == 0) {
            View rootView = this.getRootView();
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            statusBarHeight = r.top;
        }

        wLayoutParams.x = x - (int) temp[2];
        // y轴减去状态栏的高度，因为状态栏不是用户可以绘制的区域，不然拖动的时候会有跳动
        if (y >= 0) {
            wLayoutParams.y = y - statusBarHeight - (int) temp[3];// STATUS_HEIGHT;
        }
        if (wLayoutParams.x < 0) {
            wLayoutParams.x = 0;
        }
        if (wLayoutParams.x > screenWidth - getWidth()) {
            wLayoutParams.x = screenWidth - getWidth();
        }
        if (wLayoutParams.y < 0) {
            wLayoutParams.y = 0;
        }
        if (wLayoutParams.y > screenHeight - getWidth() - statusBarHeight) {
            wLayoutParams.y = screenHeight - getWidth() - statusBarHeight;
        }
        refresh();
    }

    @Override
    public void setVisibility(int visibility) {
        if (!isShow) {
            visibility = GONE;
        }
        super.setVisibility(visibility);
    }

    /**
     * 添加悬浮窗或者更新悬浮窗 如果悬浮窗还没添加则添加 如果已经添加则更新其位置
     */
    private void refresh() {
        try {
            if (viewAdded) {
                mWindowManager.updateViewLayout(this, wLayoutParams);
            } else {
                setVisibility(getVisibility());
                mWindowManager.addView(this, wLayoutParams);
                viewAdded = true;
            }
        } catch (Exception e) {
            LogUtils.d(e.getMessage());
        }
    }

    private void startVibrator(int vibratorDuration) {
        Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(vibratorDuration);
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null)
            mOnClickListener.onClick();
    }

    public static interface OnClickListener {
        public void onClick();
    }


}


