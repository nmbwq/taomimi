package com.lechuang.shengqianshou.wxapi;


import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.database.UserInfoBeanDao;
import com.common.app.database.bean.UserInfoBean;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.api.RetrofitServer;
import com.common.app.http.bean.UsersBean;
import com.common.app.http.bean.WxLoginBean;
import com.common.app.utils.Logger;
import com.common.app.utils.SPUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: zhengjr
 * @since: 2018/9/17
 * @describe:  微信回调类，建议写成接口的形式将回调放给当前的调用者
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    /**
     * 微信登录相关
     */
    private IWXAPI api;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WXAPPID, true);
        //将应用的appid注册到微信
        api.registerApp(BuildConfig.WXAPPID);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Logger.d("----", "baseResp:" + JSON.toJSONString(baseResp));
        Logger.d("----", "baseResp:" + baseResp.errStr + "," + baseResp.openId + "," + baseResp.transaction + "," + baseResp.errCode);
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "授权成功！";
                toast(result);
                String code = ((SendAuth.Resp) baseResp).code;
                getUserInfoAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                toast(result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                toast(result);
                finish();
                break;
            default:
                result = "发送返回";
                toast(result);
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
        UMShareAPI.get ( this ).onActivityResult ( requestCode,resultCode,data );
    }

    private void getUserInfoAccessToken(String code) {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("appid", BuildConfig.WXAPPID);
        allParam.put("secret", BuildConfig.WXAPPSECRET);
        allParam.put("code", code);
        allParam.put("grant_type", "authorization_code");
        NetWork.getInstance().getApiService(RetrofitServer.class)
                .accessToken(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WxLoginBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WxLoginBean wxLoginBean) {
                        if (wxLoginBean != null && !TextUtils.isEmpty(wxLoginBean.openid)) {
                            getUserInfo(wxLoginBean.openid, wxLoginBean.access_token);
                        }else {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("获取授权信息失败！");
                        finish();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getUserInfo(String openId, String access_token) {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("openId", openId);
        allParam.put("access_token", access_token);

        NetWork.getInstance().getApiService(RetrofitServer.class)
                .userinfo(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WxLoginBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WxLoginBean wxLoginBean) {

                        if (wxLoginBean != null && !TextUtils.isEmpty(wxLoginBean.openid)
                                && !TextUtils.isEmpty(wxLoginBean.nickname)
//                                && !TextUtils.isEmpty(wxLoginBean.headimgurl)
                                && !TextUtils.isEmpty(wxLoginBean.openid)) {
                            if (TextUtils.isEmpty(wxLoginBean.headimgurl)){
                                wxLoginBean.headimgurl = "http://img.taoyouji666.com/6d15a638146d4a3a8e5d3432e3e98919";
                            }
                            verify(wxLoginBean);
                        }else {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("获取授权信息失败！");
                        finish();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 验证
     * 这个地方维护，最好不要动代码。建议跟后台商量重构第三方登录的逻辑，太乱了。换个开发者估计都懵逼了
     */
    private void verify(final WxLoginBean wxLoginBean){
        Map<String, String> allParam = new HashMap<>();
        allParam.put("openId", wxLoginBean.openid);
        allParam.put("unionId", wxLoginBean.unionid);
        allParam.put("type", "1");


        final boolean tagBound = SPUtils.getInstance().getBoolean(BaseApplication.getApplication(),"tagBound",false);
        if (tagBound){
            allParam.put("phone", UserHelper.getInstence().getUserInfo().getPhone());
            allParam.put("name",wxLoginBean.nickname);
            allParam.put("photo", wxLoginBean.headimgurl);
            NetWork.getInstance()
                    .setTag(Qurl.userBindingWeChatOrQQ)
                    .getApiService(RetrofitServer.class)
                    .userBindingWeChatOrQQ(allParam)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxObserver<UsersBean>(WXEntryActivity.this) {

                        @Override
                        public void onSuccess(UsersBean result) {
                            if (result == null) {
                                finish();
                                return;
                            }
                            try {

                                SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"tagBound",false);
                                //更新数据
                                String phone = UserHelper.getInstence().getUserInfo().getPhone();
                                UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
                                UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
                                if (unique != null) {
                                    unique.setIsLogin(true);
                                    unique.setSafeToken(result.safeToken);
                                    if (!TextUtils.isEmpty(result.photo)){
                                        unique.setPhoto(result.photo);
                                    }else if(!TextUtils.isEmpty(result.weixinPhoto)){
                                        unique.setPhoto(result.weixinPhoto);
                                    }else{
                                        unique.setPhoto("");
                                    }
                                    unique.setWxPhoto(result.weixinPhoto);
                                    unique.setWxName(result.weixinName);
                                    userInfoDao.update(unique);
                                }
                                finish();

                            } catch (Exception e) {
                                toast(e.toString());
                                finish();
                            }


                        }

                        @Override
                        public void onFailed(int errorCode, String moreInfo) {
                            super.onFailed(errorCode, moreInfo);
                            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"tagBound",false);
                            finish();
                        }
                    });
        }else {
            NetWork.getInstance()
                    .setTag(Qurl.verify)
                    .getApiService(RetrofitServer.class)
                    .verify(allParam)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxObserver<UsersBean>(WXEntryActivity.this) {

                        @Override
                        public void onSuccess(UsersBean result) {
                            if (result == null) {
                                finish();
                                return;
                            }
                            try {

                                UserInfoBean userInfoBean = new UserInfoBean();
                                userInfoBean.setIsLogin(true);
                                userInfoBean.setId(result.id);
                                userInfoBean.setIsActiveStatus(result.isAgencyStatus);
                                userInfoBean.setSafeToken(result.safeToken);
                                userInfoBean.setPhone(result.phone);
                                if (!TextUtils.isEmpty(result.photo)){
                                    userInfoBean.setPhoto(result.photo);
                                }else if(!TextUtils.isEmpty(result.weixinPhoto)){
                                    userInfoBean.setPhoto(result.weixinPhoto);
                                }else{
                                    userInfoBean.setPhoto("");
                                }
                                userInfoBean.setWxPhoto(result.weixinPhoto);
                                userInfoBean.setWxName(result.weixinName);
                                userInfoBean.setInvitationCode(result.invitationCode);
                                userInfoBean.setFirstLoginFlag(result.firstLoginFlag == 0 ? true : false);
                                userInfoBean.setAgencyLevel(result.agencyLevel);
                                UserHelper.getInstence().saveUserInfo(userInfoBean);
                                //登录成功，发送通知刷新界面
                                EventBus.getDefault().post(Constant.LOGIN_SUCCESS);
                                finish();

                            } catch (Exception e) {
                                toast(e.toString());
                                finish();
                            }


                        }

                        @Override
                        public void onFailed(int errorCode, String moreInfo) {
                            super.onFailed(errorCode, moreInfo);
                            ARouter.getInstance().build(ARouters.PATH_BOUND)
                                    .withString("openId",wxLoginBean.openid)
                                    .withString("unionId",wxLoginBean.unionid)
                                    .withString("name",wxLoginBean.nickname)
                                    .withString("photo",wxLoginBean.headimgurl)
                                    .withString("type","1")
                                    .navigation();
                            finish();
                        }
                    });
        }

    }

    private void boundWX(String openId,String nickname,String headimgurl,String  type){
        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", UserHelper.getInstence().getUserInfo().getPhone());
        allParam.put("openId", openId);
        allParam.put("name", nickname);
        allParam.put("photo", headimgurl);
        allParam.put("type", type);
        NetWork.getInstance()
                .setTag(Qurl.registVerifiCodeWeixinOrQQ)
                .getApiService(RetrofitServer.class)
                .registVerifiCodeWeixinOrQQUserBean(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<UsersBean>(WXEntryActivity.this) {

                    @Override
                    public void onSuccess(UsersBean result) {
                        if (result == null) {
                            return;
                        }
                        //更新数据
                        String phone = UserHelper.getInstence().getUserInfo().getPhone();
                        UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
                        UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
                        if (unique != null) {
                            unique.setIsLogin(true);
                            unique.setSafeToken(result.safeToken);
                            if (!TextUtils.isEmpty(result.photo)){
                                unique.setPhoto(result.photo);
                            }else if(!TextUtils.isEmpty(result.weixinPhoto)){
                                unique.setPhoto(result.weixinPhoto);
                            }else{
                                unique.setPhoto("");
                            }
                            unique.setWxPhoto(result.weixinPhoto);
                            unique.setWxName(result.weixinName);
                            userInfoDao.update(unique);
                        }
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        finish();
                    }
                });
    }
}
