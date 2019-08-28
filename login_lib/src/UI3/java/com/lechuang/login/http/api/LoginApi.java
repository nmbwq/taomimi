package java.com.lechuang.login.http.api;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.api.Qurl;

import java.com.lechuang.login.bean.UsersBean;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author: zhengjr
 * @since: 2018/8/20
 * @describe:
 */

public interface LoginApi {


    //登录
    @FormUrlEncoded
    @POST(Qurl.login)
    Observable<BaseResponseBean<UsersBean>> login(@FieldMap Map<String,String> allParam);

    //注册
    @FormUrlEncoded
    @POST(Qurl.register)
    Observable<BaseResponseBean<UsersBean>> register(@FieldMap Map<String,String> allParam);

    //注册获取验证码
    @FormUrlEncoded
    @POST(Qurl.registVerifiCode)
    Observable<BaseResponseBean<String>> registVerifiCode(@FieldMap Map<String,String> allParam);


    //忘记密码发送验证码
    @FormUrlEncoded
    @POST(Qurl.sendVerifiCode)
    Observable<BaseResponseBean<String>> sendVerifiCode(@FieldMap Map<String,String> allParam);

    //忘记密码
    @FormUrlEncoded
    @POST(Qurl.sureUserPassword)
    Observable<BaseResponseBean<String>> sureUserPassword(@FieldMap Map<String,String> allParam);

    //手机号登录
    @FormUrlEncoded
    @POST(Qurl.identifyUserSMS)
    Observable<BaseResponseBean<UsersBean>> identifyUserSMS(@FieldMap Map<String,String> allParam);

    //设置登录密码 登录
    @FormUrlEncoded
    @POST(Qurl.improveUserInfo)
    Observable<BaseResponseBean<UsersBean>> improveUserInfo(@FieldMap Map<String,String> allParam);


}
