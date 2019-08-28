package com.common.app.utils;

import android.view.View;

/**
 * Created by lianzun on 2018/10/18.
 */

public abstract class OnClickEvent implements View.OnClickListener {
    public static long lastTime;
    public abstract void singleClick(View v);
    @Override
    public void onClick(View v) {
        if (!onDoubClick ()){
            return;
        }
        singleClick ( v );
    }
    public boolean onDoubClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;

        if (time > 500) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }
}
