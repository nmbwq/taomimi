package com.common.app.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by qianniao_001 on 2019/3/29.
 */

public class MiddleHorizal extends HorizontalScrollView {
    private onMiddleItemChangedListener middleItemChangedListener;
    private Handler mHandler = new Handler();
    private int currentX = 0;//记录当前滚动的距离
    private int scrollDealy = 50;//滚动监听间隔
    int current = -1; //当前位于中间item的位置
    double halfScreenWidth; //屏幕的一半宽度

    LinearLayout content; //内容view


    public MiddleHorizal(Context context, AttributeSet attrs) {
        super(context, attrs);
        halfScreenWidth = 1.0 * getScreenWidth(context) / 2;
    }

    public MiddleHorizal(Context context) {
        super(context, null);
    }

    /**
     * 滚动监听runnable
     */
    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            if (getScrollX() == currentX) {
                //滚动停止  取消监听线程
                mHandler.removeCallbacks(this);
                setMiddleItem();
                return;
            }
            else {
                //手指离开屏幕    view还在滚动的时候
                mHandler.postDelayed(this, scrollDealy);
            }
            currentX = getScrollX();
        }
    };

    /**
     * 判断中间项 并滚动至屏幕中央
     */
    private void setMiddleItem() {
        if (content != null) {
            int minWidth = -1;
            int lastCurrent = current;
            int minDivider = 0;

            //判断距离屏幕中间距离最短的item
            for (int i = 0; i < content.getChildCount(); i++) {
                int divider = (int) ((content.getChildAt(i).getX() + 1.0 * content.getChildAt(i).getWidth() / 2 - currentX) - halfScreenWidth);
                int absDivider = Math.abs(divider);
                if (minWidth < 0) {
                    minWidth = absDivider;
                }
                else if (minWidth > absDivider) {
                    minWidth = absDivider;
                    minDivider = divider;
                    current = i;
                }
            }
            //如果中间项变化，则恢复原状
            if (lastCurrent != current && lastCurrent >= 0) {
                TextView lastMiddleView = (TextView) content.getChildAt(lastCurrent);
                lastMiddleView.setTextSize(20);
                lastMiddleView.setTextColor(Color.RED);
            }

            //滚动至中间位置
            scrollBy(minDivider, 0);
            middleItemChangedListener.middleItemChanged(current);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //手指在上面移动的时候   取消滚动监听线程
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //手指移动的时候
                mHandler.post(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //初始化，使得中间项在屏幕中间位置
        if (getChildCount() > 0 && current < 0 && middleItemChangedListener != null) {
            content = (LinearLayout) getChildAt(0);
            setMiddleItem();
        }
    }

    public void setMiddleItemChangedListener(onMiddleItemChangedListener middleItemChangedListener) {
        this.middleItemChangedListener = middleItemChangedListener;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 回调，将中间项位置传递出去
     */
    public interface onMiddleItemChangedListener {
        void middleItemChanged(int current);
    }
}
