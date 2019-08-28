package com.common.app.http.down;

/**
 * @author: zhengjr
 * @since: 2018/8/15
 * @describe:
 */

public interface DownProgressLisenter {

    void onStart();

    void onUpdata(int progress,int count,String path);

    void onPause();

    void onSuccess(String path);

    void onFailed(String errorInfo);
}
