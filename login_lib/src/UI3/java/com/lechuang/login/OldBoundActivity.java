package java.com.lechuang.login;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.common.app.http.api.RetrofitServer;
import com.common.app.http.bean.UsersBean;
import com.common.app.utils.StringUtils;
import com.common.app.view.CommonDialog;
import com.lechuang.login.R;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


//@Route(path = ARouters.PATH_BOUND)
public class OldBoundActivity extends BaseOtherActivity implements View.OnClickListener {

    @Autowired
    public String openId;
    @Autowired
    public String name;
    @Autowired
    public String photo;
    @Autowired
    public String type;

    private TextView mTvGetCode, mTvBtn;
    private EditText mEtPhone, mEtCode, mEtYaoQingMa;
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bound_old;
    }

    @Override
    protected void findViews() {
        mEtPhone = findViewById(R.id.phone_et);
        mEtCode = findViewById(R.id.ver_et);
        mEtYaoQingMa = findViewById(R.id.yaoqingma_et);
        mTvGetCode = findViewById(R.id.getVer);
        mTvBtn = findViewById(R.id.button);
        mTvGetCode.setOnClickListener(this);
        mTvBtn.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.iv_back) {
            showFinishDialog();
        } else if (id == R.id.getVer) {
            String phone = mEtPhone.getText().toString().trim();

            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                registVerifiCode(phone);
            }
        } else if (id == R.id.button) {//绑定
            bound();
        }
    }


    private void registVerifiCode(String phone) {

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
//        allParam.put("openId", openId);
//        allParam.put("name", name);
        allParam.put("photo", photo);
        allParam.put("type", type);
        NetWork.getInstance()
                .setTag(Qurl.thirdVerifiCode)
                .getApiService(RetrofitServer.class)
                .thirdVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(OldBoundActivity.this) {

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
     * 绑定
     */
    private void bound() {
        String phone = this.mEtPhone.getText().toString().trim();
        if (!StringUtils.vertifyPhone(phone))
            return;

        String verifiCode = this.mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(verifiCode)) {
            toast("验证码不能为空！");
            return;
        }

        String yaoQaingMa = this.mEtYaoQingMa.getText().toString().trim();
        if (TextUtils.isEmpty(yaoQaingMa)) {
            toast("激活码不能为空！");
            return;
        }

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        allParam.put("openId", openId);
        allParam.put("name", name);
        allParam.put("photo", photo);
        allParam.put("type", type);
        allParam.put("verifiCode", verifiCode);
        allParam.put("invitationCode", yaoQaingMa);

        NetWork.getInstance()
                .setTag(Qurl.thirdCheckVerifiCode)
                .getApiService(RetrofitServer.class)
                .thirdCheckVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<UsersBean>(OldBoundActivity.this) {

                    @Override
                    public void onSuccess(UsersBean result) {
                        if (result == null) {
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
                        mTvGetCode.setText(mCount + "s重新获取");
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
                            mTvGetCode.setTextColor(getResources().getColor(R.color.c_main));
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
        showFinishDialog();
    }

    private CommonDialog mCommonDialog;
    private void showFinishDialog(){

        if (mCommonDialog != null){
            mCommonDialog.dismiss();
            mCommonDialog = null;
        }
        if (mCommonDialog == null){
            mCommonDialog = new CommonDialog(this, R.layout.dialog_layout);
        }
        mCommonDialog.setTextView(R.id.tv_dialog_title,"确认放弃?");
        mCommonDialog.setTextView(R.id.tv_dialog_content,"放弃绑定手机号将不能够使用微信授权登录");
        mCommonDialog.getViewId(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCommonDialog.dismiss();
                finish();
            }
        });
        mCommonDialog.getViewId(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonDialog.dismiss();
            }
        });
        mCommonDialog.show();
    }
}
