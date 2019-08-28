package com.common.app.http;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.http.down.DownProgressLisenter;
import com.common.app.utils.CustomProgressDialog;
import com.common.app.utils.DeviceUtils;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * Date：2018/3/19
 * Time：17:39
 * author：CH
 */

public abstract class DownProgressRxObserver<T> implements Observer<T>,DownProgressLisenter{

    protected Context mContext;
    //添加tag标签，统一管理请求(比如取消请求等操作)
    protected String tag;

    private MyHandler mHandler;

    public DownProgressRxObserver(Context context) {
        this.mContext = context;
        mHandler = new MyHandler(context);
    }


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onStart();
        //是否有网络
        if (DeviceUtils.isNetworkConnected(mContext)) {
            tag = ApiCancleManager.getInstance().getTagValue();
            if (!TextUtils.isEmpty(tag)) {
                ApiCancleManager.getInstance().add(tag, d);
            }
        }else {
            //没有网络的时候，调用error方法，并且切断与上游的联系
            if (!d.isDisposed()) {
                onFailed("网络连接异常！");
                solveException(ExceptionType.BAD_NETWORK);
                d.dispose();
                return;
            }
        }
    }



    @Override
    public void onNext(T result) {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onErrors(e);
        onFailed(e.toString());
    }

    @Override
    public void onUpdata( int progress,  int count,String path) {
        Message message = new Message();
        message.arg1 = progress;
        message.arg2 = count;
        message.obj = path;
        if (mHandler != null){
            mHandler.sendMessage(message);
        }

//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                onProgress(progress,count);
//            }
//        });
    }

    public abstract void onProgress(int progress, int count);

    /**
     * 连接异常时回调，手动触发
     */
    public void onErrors(@NonNull Throwable e){
        if (e instanceof UnknownHostException || e instanceof ConnectException) {//无网络
            solveException(ExceptionType.BAD_NETWORK);
        } else if (e instanceof JsonParseException || e instanceof JSONException ||
                e instanceof ParseException) {//解析异常
            solveException(ExceptionType.PARSE_DATA_ERROR);
        } else if (e instanceof HttpException) {//http异常，比如 404 500
            solveException(ExceptionType.UNFOUND_ERROR);
        } else if (e instanceof SocketTimeoutException) {//连接超时
            solveException(ExceptionType.TIMEOUT_ERROR);
        } else {//未知错误
            solveException(ExceptionType.UNKNOWN_ERROR);
        }
    }


    /**
     * 重新登录提示
     */
    private synchronized void reLogin() {
//        context.startActivity(new Intent(context,));
    }


    /**
     * 对于异常情况的统一处理
     * @param type 异常的类型
     */
    public void solveException(ExceptionType type){
        switch (type){
            case BAD_NETWORK:
                Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
                break;
            case PARSE_DATA_ERROR:
                Toast.makeText(mContext, "数据解析异常", Toast.LENGTH_SHORT).show();
                break;
            case UNFOUND_ERROR:
                Toast.makeText(mContext, "地址链接错误", Toast.LENGTH_SHORT).show();
                break;
            case TIMEOUT_ERROR:
                Toast.makeText(mContext, "请求超时", Toast.LENGTH_SHORT).show();
                break;
            case UNKNOWN_ERROR:
                Toast.makeText(mContext, "未知错误", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public enum ExceptionType {
        /** 无网络 */
        BAD_NETWORK,
        /** 数据解析异常 */
        PARSE_DATA_ERROR,
        /** 找不到相关连接 */
        UNFOUND_ERROR,
        /** 连接超时 */
        TIMEOUT_ERROR,
        /** 未知错误 */
        UNKNOWN_ERROR
    }

    protected void toast(@StringRes int message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    protected void toast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private class MyHandler extends Handler {
        WeakReference<Context> weakReference ;
        private int lastProgress;

        public MyHandler(Context context ){
            weakReference  = new WeakReference<Context>( context) ;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( weakReference.get() != null && lastProgress < msg.arg1){
                if ( msg.arg1 < 100){
                    onProgress(msg.arg1,msg.arg2);
                    lastProgress = msg.arg1;
                }else {
                    onSuccess((String) msg.obj);
                }


            }
        }
    }

}
