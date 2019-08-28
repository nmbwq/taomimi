package java.com.lechuang.login;


import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
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
import com.common.app.utils.ActivityStackManager;
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

//@Route(path = ARouters.PATH_LOGIN)
public class OldLoginActivity extends BaseOtherActivity implements View.OnClickListener {

    @Autowired
    public boolean mReStartApp = false;//用于判断用户是否token过期，而且不重新登录。
    private boolean mIsLogin = false;//标识是否是登录状态
    private boolean mIsMingWen = false;//标识密码是否为明文转态

    private ImageView mIvClose, mIvEyes;

    private TextView mTvLogin, mTvReg, mTvGetCode, mTvBtn, mTvXieYi, mTvQq, mTvPhoneLogin;

    private View mVLogin, mVReg;

    private EditText mEtPhone, mEtCode, mEtPsd, mEtYaoQingMa;

    private RelativeLayout mRlCode, mRlLogin, mRlYaoQingMa;
    private LinearLayout mLlOhterLogin;
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_old;
    }

    @Override
    protected void findViews() {

        mIvClose = findViewById(R.id.iv_close);
        mTvLogin = findViewById(R.id.login_tv);
        mVLogin = findViewById(R.id.login_line);
        mTvReg = findViewById(R.id.reg_tv);
        mVReg = findViewById(R.id.reg_line);
        mRlCode = findViewById(R.id.verrl);
        mEtPhone = findViewById(R.id.phone_et);
        mEtCode = findViewById(R.id.ver_et);
        mEtPsd = findViewById(R.id.pass_et);
        mEtYaoQingMa = findViewById(R.id.yaoqingma_et);
        mTvGetCode = findViewById(R.id.getVer);
        mTvQq = findViewById(R.id.tv_qq);
        mRlYaoQingMa = findViewById(R.id.rl_yaoqingma);
        mRlLogin = findViewById(R.id.delu_rl);
        mLlOhterLogin = findViewById(R.id.bottomll);
        mTvBtn = findViewById(R.id.button);
        mTvXieYi = findViewById(R.id.userxieyi);
        mIvEyes = findViewById(R.id.iv_eyes);
        mTvPhoneLogin = findViewById(R.id.tv_phone_login);

        mIvClose.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
        mTvReg.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mTvXieYi.setOnClickListener(this);
        mTvBtn.setOnClickListener(this);
        mTvQq.setOnClickListener(this);
        mIvEyes.setOnClickListener(this);
        findViewById(R.id.getPass).setOnClickListener(this);
        findViewById(R.id.tv_phone_login).setOnClickListener(this);
        findViewById(R.id.tv_wx).setOnClickListener(this);

    }


    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
        changeLoginRegState(true);
        mTvXieYi.setText("《" + BuildConfig.APP_NAME + "用户协议》");
    }

    private void changeLoginRegState(boolean isLogin) {

        if (isLogin == this.mIsLogin)
            return;

        //每次切换时需要更新状态
        mEtPhone.setText("");
        mEtPsd.setText("");
        mEtCode.setText("");
        mEtYaoQingMa.setText("");
        mIvEyes.setImageResource(R.drawable.ic_eyes);
        this.mIsMingWen = false;

        mTvLogin.setTextColor(getResources().getColor(isLogin ? R.color.c_main : R.color.c_848484));
        mTvReg.setTextColor(getResources().getColor(!isLogin ? R.color.c_main : R.color.c_848484));

        mVLogin.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);
        mVReg.setVisibility(!isLogin ? View.VISIBLE : View.INVISIBLE);

        mRlCode.setVisibility(!isLogin ? View.VISIBLE : View.GONE);
        mRlYaoQingMa.setVisibility(!isLogin ? View.VISIBLE : View.GONE);

        mTvBtn.setText(isLogin ? "登录" : "完成");
        mRlLogin.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        mLlOhterLogin.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        mTvPhoneLogin.setVisibility(isLogin ? View.VISIBLE : View.GONE);

        this.mIsLogin = !this.mIsLogin;
    }

    private void changeYanJingState() {

        mIvEyes.setImageResource(this.mIsMingWen ? R.drawable.ic_eyes : R.drawable.ic_eyes_s);
        mEtPsd.setTransformationMethod(this.mIsMingWen ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
        this.mIsMingWen = !this.mIsMingWen;
        String text = mEtPsd.getText().toString();
        if (text.length() > 0) {
            mEtPsd.setSelection(text.length());
        }

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
            onBackPressed();
        } else if (id == R.id.tv_wx) {
            //通过WXAPIFactory工厂获取IWXApI的示例
            api = WXAPIFactory.createWXAPI(this, BuildConfig.WXAPPID, true);
            //将应用的appid注册到微信
            api.registerApp(BuildConfig.WXAPPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";//
            req.state = "app_wechat";
            api.sendReq(req);
        } else if (id == R.id.login_tv) {//切换登录状态
            changeLoginRegState(true);

        } else if (id == R.id.reg_tv) {//切换注册状态
            changeLoginRegState(false);
            mIsMingWen = true;
            mIvEyes.setImageResource(R.drawable.ic_eyes_s);
            mEtPsd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else if (id == R.id.getVer) {//获取二维码
            String phone = mEtPhone.getText().toString().trim();

            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                registVerifiCode(phone);
            }

        } else if (id == R.id.userxieyi) {//协议
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.xiexi)
                    .withString(Constant.TITLE, "用户协议")
                    .withInt(Constant.TYPE, 4)
                    .navigation();

        } else if (id == R.id.button) {//登录或者注册
            if (this.mIsLogin) {
                //登录
                login();
            } else {
                //注册
                register();
            }

        } else if (id == R.id.tv_qq) {//QQ登录

        } else if (id == R.id.iv_eyes) {//密码明文和隐藏的切换
            changeYanJingState();
        } else if (id == R.id.getPass) {//忘记密码
            ARouter.getInstance().build(ARouters.PATH_FIND_PSD).navigation();
        } else if (id == R.id.tv_phone_login) {//手机号登录
            startActivity(new Intent(this, PhoneLoginActivity.class));
            finish();
        }
    }

    /**
     * 登录
     */
    private void login() {
        String phone = this.mEtPhone.getText().toString().trim();
        if (!StringUtils.vertifyPhone(phone))
            return;

        String psd = this.mEtPsd.getText().toString().trim();
        if (TextUtils.isEmpty(psd)) {
            toast("密码不能为空！");
            return;
        }
        psd = StringUtils.getMD5(psd);

        Map<String, String> allParam = new HashMap<>();
        allParam.put("u", phone);
        allParam.put("p", psd);

        NetWork.getInstance()
                .setTag(Qurl.login)
                .getApiService(LoginApi.class)
                .login(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<UsersBean>(OldLoginActivity.this) {

                    @Override
                    public void onSuccess(UsersBean result) {
                        if (result == null) {
                            return;
                        }

                        /*public long createTime;
                        public String createTimeStr;
                        public int firstLoginFlag;
                        public String id;
                        public String invitationCode;
                        public int isAgencyStatus;
                        public int isInvitation;
                        public String openImPassword;
                        public String password;
                        public String phone;
                        public String rmUserId;
                        public String safeToken;
                        public int status;
                        public int superiorId;
                        public int verifiCode;*/
                        try {
                            UserInfoBean userInfoBean = new UserInfoBean();
                            userInfoBean.setIsLogin(true);
                            userInfoBean.setId(result.user.id);
                            userInfoBean.setIsActiveStatus(result.user.isAgencyStatus);
                            userInfoBean.setSafeToken(result.user.safeToken);
                            userInfoBean.setPhone(result.user.phone);
                            if (!TextUtils.isEmpty(result.user.photo)) {
                                userInfoBean.setPhoto(result.user.photo);
                            } else if (!TextUtils.isEmpty(result.user.weixinPhoto)) {
                                userInfoBean.setPhoto(result.user.weixinPhoto);
                            } else {
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
                        } catch (Exception e) {
                            toast(e.toString());
                        }


                    }
                });
    }

    /**
     * 注册
     */
    private void register() {

        String phone = this.mEtPhone.getText().toString().trim();
        if (!StringUtils.vertifyPhone(phone))
            return;

        String verifiCode = this.mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(verifiCode)) {
            toast("验证码不能为空！");
            return;
        }

        String psd = this.mEtPsd.getText().toString().trim();
        if (TextUtils.isEmpty(psd)) {
            toast("密码不能为空！");
            return;
        }
        psd = StringUtils.getMD5(psd);

        String yaoQaingMa = this.mEtYaoQingMa.getText().toString().trim();
        if (TextUtils.isEmpty(yaoQaingMa)) {
            toast("激活码不能为空！");
            return;
        }

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        allParam.put("verifiCode", verifiCode);
        allParam.put("password", psd);
        allParam.put("invitationCode", yaoQaingMa);

        NetWork.getInstance()
                .setTag(Qurl.register)
                .getApiService(LoginApi.class)
                .register(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<UsersBean>(OldLoginActivity.this) {

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
                            userInfoBean.setPhoto(result.user.photo);
                            userInfoBean.setWxPhoto(result.user.weixinPhoto);
                            userInfoBean.setWxName(result.user.weixinName);
                            userInfoBean.setInvitationCode(result.user.invitationCode);
                            userInfoBean.setFirstLoginFlag(result.user.firstLoginFlag == 0 ? true : false);
                            userInfoBean.setAgencyLevel(result.user.agencyLevel);
                            UserHelper.getInstence().saveUserInfo(userInfoBean);

                            //登录成功，发送通知刷新界面
                            EventBus.getDefault().post(Constant.LOGIN_SUCCESS);
                            finish();
                        } catch (Exception e) {
                            toast(e.toString());
                        }
                    }
                });

    }

    private void registVerifiCode(String phone) {

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        NetWork.getInstance()
                .setTag(Qurl.registVerifiCode)
                .getApiService(LoginApi.class)
                .registVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(OldLoginActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (result.indexOf("成功") != 1) {
                            setGetCodeBtn();
                        }
                        if (TextUtils.isEmpty(result)) {
                            return;
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
                        } else {
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
    public void onBackPressed() {
        super.onBackPressed();
        if (mReStartApp) {
            //清空用户列表，发出退出成功，清空栈的信息直到最后一条
            UserHelper.getInstence().deleteAllUserInfo();
            EventBus.getDefault().post(Constant.LOGOUT_SUCCESS);
            ActivityStackManager.getInstance().finishActivityToLast();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS)) {
            finish();
        }
    }
}
