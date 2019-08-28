package com.common.app.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.common.R;
import com.common.app.base.BaseApplication;

import io.reactivex.internal.schedulers.ImmediateThinScheduler;

/**
 * @author: zhengjr
 * @since: 2018/8/21
 * @describe:
 */

public class CommonPopupwind extends PopupWindow implements PopupWindow.OnDismissListener {

    private Context mContext;
    private View mLayoutView;
    private int mHeight;
    private View mAsDropDown;
    private float mAlpha = 1;//默认是不渐变
    private Window mWindow;


    public CommonPopupwind(Builder builder) {
        this.mContext = builder.mContext;
        this.mLayoutView = builder.mLayoutView;
        this.mHeight = builder.mHeight;
        this.mAsDropDown = builder.mAsDropDown;
        this.mAlpha = builder.mAlpha;
        this.mWindow = builder.mWindow;
        setOnDismissListener(this);
    }

    public static class Builder{

        private Context mContext;
        private View mLayoutView;
        private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        private View mAsDropDown;
        private CommonPopupwind mCommonPopupwind;
        private float mAlpha = 1;//默认是不渐变
        private Window mWindow;


        /**
         * 设置高度，默认为WRAP_CONTENT
         * @param height
         * @return
         */
        public Builder setHeight(int height){
            this.mHeight = height;
            return this;
        }

        public Builder showAsDropDown(View asDropDown){
            this.mAsDropDown = asDropDown;
            return this;
        }

        public Builder setWindAlpha(Window window,float alpha){
            this.mWindow = window;
            this.mAlpha = alpha;
            return this;
        }

        public CommonPopupwind build(Context context,@LayoutRes int layoutId){
            this.mContext = context;
            View view = LayoutInflater.from(context).inflate(layoutId,null);
            this.mLayoutView = view;
            this.mCommonPopupwind = new CommonPopupwind(this);
            mCommonPopupwind.setContentView(view);
            mCommonPopupwind.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mCommonPopupwind.setHeight(this.mHeight);
            mCommonPopupwind.setFocusable(true);
            mCommonPopupwind.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
            return mCommonPopupwind;
        }
    }

    public View findViewById(@IdRes int viewId){
        return this.mLayoutView.findViewById(viewId);
    }


    public void showPopupwind(CommonPopupwind commonPopupwind) {
        if (commonPopupwind != null && !commonPopupwind.isShowing()) {
            commonPopupwind.showAsDropDown(this.mAsDropDown);
        }
        alpha(this.mAlpha);

    }

    @Override
    public void onDismiss() {
        //popupwindow消失的时候恢复成原来的透明度
        alpha(1f);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    private void alpha(float alpha){
        if (this.mWindow != null){
            WindowManager.LayoutParams lp = this.mWindow.getAttributes();
            lp.alpha = alpha; //0.0-1.0
            this.mWindow.setAttributes(lp);
        }
    }
}
