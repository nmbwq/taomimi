package com.common.app.utils;

import android.content.Context;
import android.content.res.Resources;

import com.common.R;

/**
 * Created by lianzun on 2018/9/22.
 */

public class ADFilterTool {
    public static boolean hasAd(Context context,String url){
        Resources res = context.getResources ();
        String[] adUrls = res.getStringArray ( R.array.adBlockUrl );
        for (String adUrl : adUrls){
            if (url.contains ( adUrl )){
                return true;
            }
        }
        return false;
    }
}
