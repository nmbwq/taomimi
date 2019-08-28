package com.common.app.http;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.api.Qurl;
import com.common.app.receiver.TagAliasOperatorHelper;
import com.common.app.utils.Logger;
import com.common.app.view.ProgressDialog;
import com.google.gson.JsonParseException;
import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CustomProgressDialog;
import com.common.app.utils.DeviceUtils;
import com.umeng.commonsdk.debug.E;

import org.json.JSONException;

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

public abstract class RxObserver<T> implements Observer<BaseResponseBean<T>> {

    protected Context context;
    //添加tag标签，统一管理请求(比如取消请求等操作)
    protected String tag;
    protected CustomProgressDialog progressDialog;

    public RxObserver(Context context) {
        this.context = context;
        //弹窗进度条,能取消
        createProgressDialog(context, true);
    }

    public RxObserver(Context context, boolean canCancle) {
        this.context = context;
        //弹窗进度条
        createProgressDialog(context, canCancle);
    }

    public RxObserver(Context context, boolean canCancle, @DrawableRes int loadImg,String txt) {
        this.context = context;
        //弹窗进度条
        createProgressDialog(context, canCancle,loadImg,txt);
    }

    public RxObserver(Context context, boolean canCancle, boolean isShowProgress) {
        this.context = context;
        //弹窗进度条
        if (isShowProgress) {
            createProgressDialog(context, canCancle);
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        //显示进度条
        showProgressDialog();
        //是否有网络
        if (DeviceUtils.isNetworkConnected(context)) {
            tag = ApiCancleManager.getInstance().getTagValue();
            if (!TextUtils.isEmpty(tag)) {
                ApiCancleManager.getInstance().add(tag, d);
            }
        } else {
            //没有网络的时候，调用error方法，并且切断与上游的联系
            if (!d.isDisposed()) {
                stopProgressDialog();
                solveException(ExceptionType.BAD_NETWORK);
                d.dispose();
                onFailed(0, "无网络");
                return;
            }
        }
    }

    @Override
    public void onNext(BaseResponseBean<T> result) {
        //关闭弹窗进度条
        stopProgressDialog();


        //转链接专用
        tag = ApiCancleManager.getInstance().getTagValue();
        if (TextUtils.equals(tag, Qurl.longChangeShort)){

            if (result.urls != null && result.urls.size() > 0){
                onSuccess(result.urls.get(0));
            }else {
                onFailed(result.errorCode, "暂无信息");
            }
            return;
        }


        if (result.errorCode == 200) {
            onSuccess(result.data);
        } else if (result.errorCode == 401 || result.errorCode == 303) {  //错误码401 303 登录
            onFailed(result.errorCode, result.moreInfo);
            if (!TextUtils.isEmpty(result.moreInfo)) {
                toast(result.moreInfo);
            }
            reLogin();
        } else if (result.errorCode == 300) {
            onFailed(result.errorCode, result.moreInfo);
            if (!TextUtils.isEmpty(result.moreInfo)) {
                if (!result.moreInfo.equals( "秘钥错误" ))
                toast(result.moreInfo);
            }
        } else if (result.errorCode == 11003){
            onFailed_11003(result.data);
        }else if (result.errorCode == 11004){
            onFailed_11004( result.data );
        }else if (result.errorCode == 4011){//授权过期跳授权
            onFailed_4011();
        }else {
            onFailed(result.errorCode, result.moreInfo);
            if (!TextUtils.isEmpty(result.moreInfo)) {
                toast(result.moreInfo);
            }
        }

//        if (tBaseResponseBean.errorCode >= 0) {
//            onSuccess(tBaseResponseBean.data);
//        }else {
//            onFailed(tBaseResponseBean.data);
//        }
    }

    /*@Override
    public void onNext(@NonNull T response) {
        //关闭弹窗进度条
        stopProgressDialog();

        if (response.errorCode >= 0) {
            onSuccess(response);
        }else {
            onFailed(response);
        }
    }*/

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        //关闭弹窗进度条
        stopProgressDialog();
        onErrors(e);
    }

    /**
     * 创建进度条实例
     */
    public void createProgressDialog(Context cxt, boolean canCancle,@DrawableRes int loadImg,String txt) {
        try {
            ProgressDialog.getInstance().createDialog(cxt, canCancle,loadImg,txt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建进度条实例
     */
    public void createProgressDialog(Context cxt, boolean canCancle) {
        try {
            ProgressDialog.getInstance().createDialog(cxt,canCancle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动加载进度条
     */
    public void showProgressDialog() {
        try {
            ProgressDialog.getInstance().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭加载进度条
     */
    public void stopProgressDialog() {
        try {
            ProgressDialog.getInstance().dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求成功回调
     *
     * @param result 服务端返回的数据
     */
    public abstract void onSuccess(T result);

    /**
     * 请求失败的回调 (非 200的情况)，开发者手动去触发
     *
     * @param moreInfo 错误信息数据
     */
    public void onFailed(int errorCode, String result) {
    }
    //大转盘使用弹出自定义觅卡不足
    public void onFailed_11004(T result) {

    }
    //签到
    public void onFailed_11003(T result) {

    }

    /**
     * 授权过期跳授权
     */
    public void onFailed_4011(){}

    /**
     * 连接异常时回调，手动触发
     */
    public void onErrors(@NonNull Throwable e) {
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
        try {
            String id = UserHelper.getInstence().getUserInfo().getId();
            if (!TextUtils.isEmpty(id)){
                TagAliasOperatorHelper.TagAliasBean alias = new TagAliasOperatorHelper.TagAliasBean();
                alias.setAction(TagAliasOperatorHelper.ACTION_DELETE);
                alias.setAlias(id);
                alias.setAliasAction(true);
                Logger.e("---Alias", id);
            }

            UserHelper.getInstence ().deleteAllUserInfo ();
            ARouter.getInstance().build(ARouters.PATH_LOGIN).withBoolean("mReStartApp",false).navigation();
        }catch (Exception e){

        }


    }


    /**
     * 对于异常情况的统一处理
     *
     * @param type 异常的类型
     */
    public void solveException(ExceptionType type) {
        switch (type) {
            case BAD_NETWORK:
                Toast.makeText(context, "无网络", Toast.LENGTH_SHORT).show();
                onFailed(0, "无网络");
                break;
            case PARSE_DATA_ERROR:
                Toast.makeText(context, "数据解析异常", Toast.LENGTH_SHORT).show();
                onFailed(0, "数据解析异常");
                break;
            case UNFOUND_ERROR:
                Toast.makeText(context, "地址链接错误", Toast.LENGTH_SHORT).show();
                onFailed(0, "地址链接错误");
                break;
            case TIMEOUT_ERROR:
                Toast.makeText(context, "请求超时", Toast.LENGTH_SHORT).show();
                onFailed(0, "请求超时");
                break;
            case UNKNOWN_ERROR:
                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                onFailed(0, "未知错误");
                break;
        }
    }

    public enum ExceptionType {
        /**
         * 无网络
         */
        BAD_NETWORK,
        /**
         * 数据解析异常
         */
        PARSE_DATA_ERROR,
        /**
         * 找不到相关连接
         */
        UNFOUND_ERROR,
        /**
         * 连接超时
         */
        TIMEOUT_ERROR,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR
    }

    protected void toast(@StringRes int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    protected void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
