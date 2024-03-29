package com.common.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by lianzun on 2018/9/21.
 */

public class MyViewPager extends ViewPager {
    private int current;
    private int viewHeight = 0;
    private boolean scrollble = true;
    public MyViewPager(Context context, AttributeSet attrs) {
        super ( context, attrs );
    }

    public MyViewPager(Context context) {
        super ( context );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View childView = getChildAt(getCurrentItem());
        if (childView != null)  //有可能没有子view
        {
            childView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            viewHeight = childView.getMeasuredHeight();   //得到父元素对自身设定的高
            // UNSPECIFIED(未指定),父元素部队自元素施加任何束缚，子元素可以得到任意想要的大小
            //EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
            //AT_MOST(至多)，子元素至多达到指定大小的值。
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void resetHeight(int current) {
        this.current = current;
        if (getChildCount() > current) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);
            } else {
                layoutParams.height = viewHeight;
            }
            setLayoutParams(layoutParams);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }


    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure ( widthMeasureSpec, heightMeasureSpec );
        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount (); i++) {
            View child = getChildAt ( i );
            child.measure ( widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec ( 0, MeasureSpec.UNSPECIFIED ) );
            int h = child.getMeasuredHeight ();
            if (h > height) //采用最大的view的高度。
                height = h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }*/
}
