package com.common.app.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.common.R;

/**
 * @author: zhengjr
 * @since: 2018/8/10
 * @describe:
 */

public class CommonDialog extends Dialog {

    private Context mContext;
    private boolean mCanCancle = true;//默认能取消
    private int mGravity;
    private int mLeft,mTop,mRight, mBottom,mWidth,mHeight;

    public CommonDialog(@NonNull Context context, @LayoutRes int layoutID) {
        super(context, R.style.CommonDialog);
        this.mContext = context;
        setContentView(layoutID);
    }

    public CommonDialog setPadding(int left, int top, int right, int bottom){
        //设置边框距离
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
        this.mBottom = bottom;
        return this;
    }


    public CommonDialog setGravity(int gravity){
        this.mGravity = gravity;
        return this;
    }

    public CommonDialog setWidth(int width){
        this.mWidth = width;
        return this;
    }

    public  CommonDialog setHeight(int height){
        this.mHeight = height;
        return this;
    }


    /**
     * 设置能否取消
     * @param canCancle
     * @param onCancelListener
     * @return
     */
    public CommonDialog setCanCancel(boolean canCancle,OnCancelListener onCancelListener){
        this.mCanCancle = canCancle;
        setCanceledOnTouchOutside(canCancle);
        if (canCancle && onCancelListener != null){
            setOnCancelListener(onCancelListener);
        }
        setCanceledOnTouchOutside(canCancle);
        setKeyListener();
        return this;
    }

    private CommonDialog setKeyListener(){
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mCanCancle) {

                        return false;
                    }else {
                        return true;
                    }
                }
                return false;
            }
        });
        return this;
    }

    public View getViewId(@IdRes int viewId){
        return this.findViewById(viewId);
    }

    public void setTextView(@IdRes int viewId,String text){
        ((TextView)this.findViewById(viewId)).setText(text);
    }


    @Override
    public void show() {
        Window window = getWindow();
        //设置边框距离
        window.getDecorView().setPadding(this.mLeft, this.mTop, this.mRight, this.mBottom);
        //设置dialog位置
        window.setGravity(this.mGravity);
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置宽高

        lp.width = this.mWidth < 0 ? this.mWidth : WindowManager.LayoutParams.MATCH_PARENT;

        lp.height = this.mHeight < 0 ? this.mHeight : WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);
        if (isShowing()){
            dismiss();
        }else {
            super.show();
        }
    }


}
