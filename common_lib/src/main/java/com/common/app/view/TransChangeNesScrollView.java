package com.common.app.view;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;
import android.widget.TableLayout;

import com.androidkun.xtablayout.XTabLayout;
import com.common.app.utils.Logger;

/**
 * Created by cmd on 2018/5/6.
 */

public class TransChangeNesScrollView extends NestedScrollView {

    private View mTransChangeView;
    private float mHeightPixels;

    public TransChangeNesScrollView(Context context) {
        this(context, null);
    }

    public TransChangeNesScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransChangeNesScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeightPixels = getContext().getResources().getDisplayMetrics().heightPixels;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 2);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);


        if (this.mTagView != null && mTagView.getTop() <= getScrollY()) {
            if (mTopTab != null)
                mTopTab.setVisibility(VISIBLE);
        } else {
            if (mTopTab != null)
                mTopTab.setVisibility(INVISIBLE);
        }

        Logger.e("----",getScrollY() + "");
        if (this.mTagTopView != null && mTagTopView.getTop() <= getScrollY()) {
            if (mToTop != null)
                mToTop.setVisibility(VISIBLE);
        } else {
            if (mToTop != null)
                mToTop.setVisibility(INVISIBLE);
        }

        //需要渐变的头部
        if (mTransChangeView != null) {
            // alpha = 滑出去的高度/(screenHeight/3);

            float scrollY = getScrollY();//获取划出去的高度
            float sulv = mHeightPixels / 3;
            if (scrollY <= 0) {
                mTransChangeView.setAlpha(0);
                mTransChangeView.setVisibility(GONE);
            } else {
                mTransChangeView.setAlpha(scrollY / sulv);
                mTransChangeView.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 设置需要渐变view
     *
     * @param transChangeView
     */
    public void setTransparentChange(View transChangeView) {
        this.mTransChangeView = transChangeView;
    }

    /**
     * 设置头部的tablayout显示隐藏
     *
     * @param lineView  标记线
     * @param topTab
     */
    private TiaoJianView mTagView;
    private TiaoJianView mTopTab;

    public void setTopTabLayout(TiaoJianView tagView, TiaoJianView topTab) {
        this.mTagView = tagView;
        this.mTopTab = topTab;
    }


    private View mToTop;
    private View mTagTopView;

    public void setToTopView(View toTop,View tagTopView){
        this.mToTop = toTop;
        this.mTagTopView = tagTopView;
    }

}
