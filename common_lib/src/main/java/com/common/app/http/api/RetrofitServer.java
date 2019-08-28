package com.common.app.http.api;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.bean.AppUpdataBean;
import com.common.app.http.bean.GetHostUrlBean;
import com.common.app.http.bean.HuoDongRedBean;
import com.common.app.http.bean.UsersBean;
import com.common.app.http.bean.WxLoginBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Author：CHENHAO
 * date：2018/5/3
 * desc：
 */

public interface RetrofitServer {
    /**
     * 首页banner图
     */
    @GET("banner/json")
    Observable<BaseResponseBean> getBannerImgs();


    @Streaming //大文件时要加不然会OOM
    @GET
    Observable<ResponseBody> downloadFile(@Url String s);

    //main 更新APP
    @FormUrlEncoded
    @POST(Qurl.appUpdata)
    Observable<BaseResponseBean<AppUpdataBean>> appUpdata(@FieldMap Map<String,String> allParam);

    //main 获取活动红包
    @POST(Qurl.getHuoDongRed2_0)
    Observable<BaseResponseBean<HuoDongRedBean>> getHuoDongRed();

    //微信登录请求token
    @FormUrlEncoded
    @POST(Qurl.accessToken)
    Observable<WxLoginBean> accessToken(@FieldMap Map<String,String> allParam);

    //微信登录请求用户信息
    @FormUrlEncoded
    @POST(Qurl.userinfo)
    Observable<WxLoginBean> userinfo(@FieldMap Map<String,String> allParam);
    //微信登录请求用户信息验证
    @FormUrlEncoded
    @POST(Qurl.verify)
    Observable<BaseResponseBean<UsersBean>> verify(@FieldMap Map<String,String> allParam);
    //微信绑定发送验证码
    @Deprecated
    @FormUrlEncoded
    @POST(Qurl.registVerifiCodeWeixinOrQQ)
    Observable<BaseResponseBean<String>> registVerifiCodeWeixinOrQQ(@FieldMap Map<String,String> allParam);

    //微信绑定发送验证码
    @FormUrlEncoded
    @POST(Qurl.thirdVerifiCode)
    Observable<BaseResponseBean<String>> thirdVerifiCode(@FieldMap Map<String,String> allParam);

    //微信绑定发送验证码
    @Deprecated
    @FormUrlEncoded
    @POST(Qurl.registVerifiCodeWeixinOrQQ)
    Observable<BaseResponseBean<UsersBean>> registVerifiCodeWeixinOrQQUserBean(@FieldMap Map<String,String> allParam);

    //用户登录直接绑定接口
    @FormUrlEncoded
    @POST(Qurl.userBindingWeChatOrQQ)
    Observable<BaseResponseBean<UsersBean>> userBindingWeChatOrQQ(@FieldMap Map<String,String> allParam);

    //微信绑定
    @FormUrlEncoded
    @POST(Qurl.thirdCheckVerifiCode)
    Observable<BaseResponseBean<UsersBean>> thirdCheckVerifiCode(@FieldMap Map<String,String> allParam);

    //获取分享商品的域名
    @POST(Qurl.getShareProductUrl)
    Observable<BaseResponseBean<GetHostUrlBean>> getShareProductUrl();

}
