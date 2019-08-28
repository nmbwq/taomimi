package java.com.lechuang.login;


import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
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

@Route(path = ARouters.PATH_LOGIN)
public class LoginActivity extends BaseOtherActivity implements View.OnClickListener {

    @Autowired
    public boolean mReStartApp = false;//用于判断用户是否token过期，而且不重新登录。
    private boolean mIsLogin = true;//标识是否是登录状态
    private boolean mIsMingWen = false;//标识密码是否为明文转态

    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;

    private TextView mTvGetCode,mTvBtn;
    private EditText mEtPhone,mEtPsd,mEtCode;
    private TextView mTvQuickLogin,mTvAccountLogin;
    private ImageView mIvEyes;
    private View mVQuick,mVLogin;
    private RelativeLayout mParentCode;
    private RelativeLayout mParentPsd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void findViews() {
        mTvGetCode = findViewById(R.id.getVer);
        mEtPhone = findViewById(R.id.phone_et);
        mTvBtn = findViewById(R.id.button);
        mEtPsd = findViewById(R.id.pass_et);
        mEtCode = findViewById(R.id.ver_et);
        mIvEyes = findViewById(R.id.iv_eyes);
        mVQuick = findViewById(R.id.v_quick_login);
        mVLogin = findViewById(R.id.v_account_login);
        mParentCode = findViewById(R.id.parent_code);
        mParentPsd = findViewById(R.id.parent_psd);


        mTvQuickLogin = $(R.id.tv_quick_login);
        mTvAccountLogin = $(R.id.tv_account_login);

        mTvGetCode.setOnClickListener(this);
        $(R.id.iv_common_close).setOnClickListener(this);
        $(R.id.tv_register).setOnClickListener(this);
        $(R.id.getPass).setOnClickListener(this);
        mTvBtn.setOnClickListener(this);
        mTvQuickLogin.setOnClickListener(this);
        mTvAccountLogin.setOnClickListener(this);
        mIvEyes.setOnClickListener(this);

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void getData() {

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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.getVer) {//获取二维码
            String phone = mEtPhone.getText().toString().trim();

            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                quickLoginVerifiCode(phone);
            }

        }else if (id == R.id.iv_common_close){
            finish();
        }else if (id == R.id.button) {//登录或者注册
            if (this.mIsLogin) {
                //登录
                login();
            } else {
                //快捷登录 todo
                quickLogin();
            }

        }else if (id == R.id.tv_quick_login){
            changeLoginRegState(false);

        }else if (id == R.id.tv_account_login){
            changeLoginRegState(true);
        }else if (id == R.id.iv_eyes){
            changeYanJingState();
        }else if (id == R.id.tv_register){
            ARouter.getInstance().build(ARouters.PATH_REGISTER).navigation();
        }else if (id == R.id.getPass) {//忘记密码
            ARouter.getInstance().build(ARouters.PATH_FIND_PSD).navigation();
        }
    }

    private void changeLoginRegState(boolean isLogin) {

        if (isLogin == this.mIsLogin)
            return;

        //每次切换时需要更新状态
        mEtPhone.setText("");
        mEtPsd.setText("");
        mEtCode.setText("");
        mIvEyes.setImageResource(R.drawable.ic_eyes);
        this.mIsMingWen = false;

        mVQuick.setVisibility(!isLogin ? View.VISIBLE : View.INVISIBLE);
        mVLogin.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);
        mParentCode.setVisibility(!isLogin ? View.VISIBLE : View.INVISIBLE);
        mParentPsd.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);
        mTvGetCode.setVisibility(!isLogin ? View.VISIBLE : View.INVISIBLE);
        $(R.id.getPass).setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);

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
    /**
     * 登录
     */
    private void quickLogin() {
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
                .subscribe(new RxObserver<UsersBean>(LoginActivity.this) {

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

    private void quickLoginVerifiCode(String phone) {

        Map<String,String> allParam = new HashMap<>();
        allParam.put("phone",phone);
        allParam.put("type","3");
        NetWork.getInstance()
                .setTag(Qurl.sendVerifiCode)
                .getApiService(LoginApi.class)
                .sendVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(LoginActivity.this) {

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
                        mTvGetCode.setText(mCount + "秒");
                        mCount--;

                        if (mCount < 0) {

                            mTimerTask.cancel();
                            mTimerTask = null;
                            mTimer.cancel();
                            mTimer = null;
                            mCount = 60;
                            mTvGetCode.setText("发送验证码");
                            mTvGetCode.setClickable(true);
                            mTvGetCode.setEnabled(true);
                            mTvGetCode.setTextColor(getResources().getColor(R.color.c_72FFFFFF));
                        } else {
                            mTvGetCode.setEnabled(false);
                            mTvGetCode.setTextColor(getResources().getColor(R.color.white));
                        }


                    }
                });
                mTvGetCode.setClickable(false);


            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);

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
        if (StringUtils.verifyPsd(psd)){
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
                .subscribe(new RxObserver<UsersBean>(LoginActivity.this) {

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
