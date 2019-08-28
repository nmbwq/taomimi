package java.com.lechuang.login;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseOtherActivity;
import com.common.app.constants.Constant;
import com.common.app.database.bean.UserInfoBean;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.StringUtils;
import com.lechuang.login.R;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.login.bean.UsersBean;
import java.com.lechuang.login.http.api.LoginApi;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_PHONE_LOGIN)
public class PhoneLoginActivity extends BaseOtherActivity implements View.OnClickListener {

    private EditText mEtPhone, mEtCode;
    private TextView mTvGetCode, mTvBtn;
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_login;
    }

    @Override
    protected void findViews() {

        mEtPhone = findViewById(R.id.phone_et);
        mEtCode = findViewById(R.id.ver_et);
        mTvGetCode = findViewById(R.id.getVer);
        mTvBtn = findViewById(R.id.button);

        $(R.id.iv_close).setOnClickListener(this);
        $(R.id.tv_psd_login).setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mTvBtn.setOnClickListener(this);
        findViewById(R.id.tv_wx).setOnClickListener(this);

    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void getData() {

    }
    /**
     * 微信登录相关
     */
    private IWXAPI api;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            finish();
        }  else if (id == R.id.tv_wx) {
            //通过WXAPIFactory工厂获取IWXApI的示例
            api = WXAPIFactory.createWXAPI(this, BuildConfig.WXAPPID, true);
            //将应用的appid注册到微信
            api.registerApp(BuildConfig.WXAPPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";//
            req.state = "app_wechat";
            api.sendReq(req);
        } else if (id == R.id.tv_psd_login) {//账号密码登录
            startActivity(new Intent(this, OldLoginActivity.class));
            finish();
        } else if (id == R.id.getVer) {//获取二维码
            String phone = mEtPhone.getText().toString().trim();

            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                updatePasswordSendVerifiCode(phone);
            }

        } else if (id == R.id.button) {//登录或者注册
            login();

        }
    }

    /**
     * 登录
     */
    private void login() {
        final String phone = this.mEtPhone.getText().toString().trim();
        if (!StringUtils.vertifyPhone(phone))
            return;

        String verifiCode = this.mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(verifiCode)) {
            toast("验证码不能为空！");
            return;
        }

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        allParam.put("verifiCode", verifiCode);

        NetWork.getInstance()
                .setTag(Qurl.identifyUserSMS)
                .getApiService(LoginApi.class)
                .identifyUserSMS(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<UsersBean>(PhoneLoginActivity.this) {

                    @Override
                    public void onSuccess(UsersBean result) {
                        if (result == null) {
                            return;
                        }
                        try {
                            UserInfoBean userInfoBean = new UserInfoBean();
                            userInfoBean.setIsLogin(true);
                            userInfoBean.setId(result.user.id);
                            userInfoBean.setIsActiveStatus(result.user.isAgencyStatus);
                            userInfoBean.setSafeToken(result.user.safeToken);
                            userInfoBean.setPhone(result.user.phone);
                            if (!TextUtils.isEmpty(result.user.photo)){
                                userInfoBean.setPhoto(result.user.photo);
                            }else if(!TextUtils.isEmpty(result.user.weixinPhoto)){
                                userInfoBean.setPhoto(result.user.weixinPhoto);
                            }else{
                                userInfoBean.setPhoto("");
                            }
                            userInfoBean.setWxPhoto(result.user.weixinPhoto);
                            userInfoBean.setWxName(result.user.weixinName);
                            userInfoBean.setInvitationCode(result.user.invitationCode);
                            userInfoBean.setFirstLoginFlag(result.user.firstLoginFlag == 0 ? true : false);
                            userInfoBean.setAgencyLevel(result.user.agencyLevel);
                            UserHelper.getInstence().saveUserInfo(userInfoBean);

                            //登录成功，发送通知刷新界面
                            EventBus.getDefault().post(Constant.LOGIN_SUCCESS);
                            finish();
                        }catch (Exception e){

                            toast(e.getMessage());
                        }


                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (errorCode == 300 && moreInfo.equalsIgnoreCase("请先设置登录密码")) {
//                            Intent intent = new Intent(PhoneLoginActivity.this, SetPsdActivity.class);
//                            intent.putExtra("phone",phone);
//                            startActivity(intent);
                            ARouter.getInstance().build(ARouters.PATH_SET_PSD).withString("phone", phone).navigation();
                        }
                    }
                });
    }

    private void updatePasswordSendVerifiCode(String phone) {

        Map<String,String> allParam = new HashMap<>();
        allParam.put("phone",phone);
        allParam.put("type","3");
        NetWork.getInstance()
                .setTag(Qurl.sendVerifiCode)
                .getApiService(LoginApi.class)
                .sendVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(PhoneLoginActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        //设置验证码按钮状态
                        if (result.indexOf("成功") != 1) {
                            setGetCodeBtn();
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }
    private void setGetCodeBtn() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvGetCode.setText(mCount + "秒重新获取");
                        mCount--;

                        if (mCount < 0) {

                            mTimerTask.cancel();
                            mTimerTask = null;
                            mTimer.cancel();
                            mTimer = null;
                            mCount = 60;
                            mTvGetCode.setText("获取验证码");
                            mTvGetCode.setClickable(true);
                            mTvGetCode.setEnabled(true);
                            mTvGetCode.setTextColor(getResources().getColor(R.color.white));
                        }else {
                            mTvGetCode.setEnabled(false);
                            mTvGetCode.setTextColor(getResources().getColor(R.color.c_B3B3B3));
                        }
                    }
                });
                mTvGetCode.setClickable(false);

            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS)){
            finish();
        }
    }
}
