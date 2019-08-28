package java.com.lechuang.login;


import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

@Route(path = ARouters.PATH_REGISTER)
public class RegistActivity extends BaseOtherActivity implements View.OnClickListener {

    private boolean mIsMingWen = false;//标识密码是否为明文转态
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;

    private TextView mTvGetCode, mTvBtn;
    private EditText mEtPhone, mEtPsd, mEtCode, mEtYaoQingMa;
    private ImageView mIvEyes;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    protected void findViews() {
        mTvGetCode = findViewById(R.id.getVer);
        mEtPhone = findViewById(R.id.phone_et);
        mTvBtn = findViewById(R.id.button);
        mEtPsd = findViewById(R.id.pass_et);
        mEtCode = findViewById(R.id.ver_et);
        mIvEyes = findViewById(R.id.iv_eyes);

        mEtYaoQingMa = findViewById(R.id.yaoqingma_et);

        $(R.id.iv_common_close).setOnClickListener(this);
        $(R.id.userxieyi).setOnClickListener(this);
        mTvBtn.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mIvEyes.setOnClickListener(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS)) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_close) {
            finish();
        } else if (id == R.id.getVer) {//获取验证码
            String phone = mEtPhone.getText().toString().trim();

            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                registVerifiCode(phone);
            }

        } else if (id == R.id.button) {//注册
            register();
        } else if (id == R.id.iv_eyes) {//密码明文和隐藏的切换
            changeYanJingState();
        } else if (id == R.id.userxieyi) {//协议
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.xiexi)
                    .withString(Constant.TITLE, "用户协议")
                    .withInt(Constant.TYPE, 4)
                    .navigation();

        }
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
     * 获取验证码
     *
     * @param phone
     */
    private void registVerifiCode(String phone) {

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        NetWork.getInstance()
                .setTag(Qurl.registVerifiCode)
                .getApiService(LoginApi.class)
                .registVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(RegistActivity.this) {

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

        if (StringUtils.verifyPsd(psd)){
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
                .subscribe(new RxObserver<UsersBean>(RegistActivity.this) {

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


}
