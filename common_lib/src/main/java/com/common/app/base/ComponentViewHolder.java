package com.common.app.base;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @author: zhengjr
 * @since: 2018/7/11
 * @describe:
 */

public class ComponentViewHolder extends BaseViewHolder{


    public ComponentViewHolder(View view) {
        super(view);
    }

    public void setDisplayImage(@IdRes int viewId,String url){
        ImageView view = getView(viewId);
        Glide.with(BaseApplication.getApplication()).load(url).into(view);
    }
}
