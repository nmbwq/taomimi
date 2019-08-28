package java.com.lechuang.login;


import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseOtherActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.StringUtils;
import com.lechuang.login.R;

import java.com.lechuang.login.http.api.LoginApi;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//@Route(path = ARouters.PATH_FIND_PSD)
public class OldFindPsdActivity extends BaseOtherActivity implements View.OnClickListener {

    private boolean mIsMingWen = false;//标识密码是否为明文转态

    private EditText mEtForgetPhone,mEtForgetCode,mEtForgetPsd;
    private TextView mTvForgetGetCode,mTvForgetBtn;
    private ImageView mIvForgetEyes,mIvForgetClose;
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_psd_old;
    }

    @Override
    protected void findViews() {
        mEtForgetPhone = findViewById(R.id.et_forget_phone);
        mEtForgetCode = findViewById(R.id.et_forget_code);
        mEtForgetPsd = findViewById(R.id.et_forget_psd);
        mTvForgetGetCode = findViewById(R.id.tv_forget_get_code);
        mTvForgetBtn = findViewById(R.id.tv_forget_button);
        mIvForgetEyes = findViewById(R.id.iv_forget_eyes);
        mIvForgetClose = findViewById(R.id.iv_forget_close);

        mTvForgetGetCode.setOnClickListener(this);
        mTvForgetBtn.setOnClickListener(this);
        mIvForgetEyes.setOnClickListener(this);
        mIvForgetClose.setOnClickListener(this);

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

    private void changeYanJingState(){
        mIvForgetEyes.setImageResource(this.mIsMingWen ? R.drawable.ic_eyes : R.drawable.ic_eyes_s);
        mEtForgetPsd.setTransformationMethod(this.mIsMingWen ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
        this.mIsMingWen = !this.mIsMingWen;
        String text = mEtForgetPsd.getText().toString();
        if (text.length() > 0) {
            mEtForgetPsd.setSelection(text.length());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_forget_close){//关闭当前页
            finish();
        }else if(id == R.id.tv_forget_get_code){//获取验证码
            String phone = mEtForgetPhone.getText().toString();

            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)){
                updatePasswordSendVerifiCode(phone);
            }

        }else if(id == R.id.iv_forget_eyes){//密码明文切换
            changeYanJingState();
        }else if(id == R.id.tv_forget_button){//完成
            findPsd();
        }
    }

    private void updatePasswordSendVerifiCode(String phone) {

        Map<String,String> allParam = new HashMap<>();
        allParam.put("phone",phone);
        allParam.put("type","1");
        NetWork.getInstance()
                .setTag(Qurl.sendVerifiCode)
                .getApiService(LoginApi.class)
                .sendVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(OldFindPsdActivity.this) {

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
        if ( mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        if (mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvForgetGetCode.setText(mCount + "秒重新获取");
                        mCount--;

                        if (mCount < 0) {
                            mTimerTask.cancel();
                            mTimerTask = null;
                            mTimer.cancel();
                            mTimer = null;
                            mCount = 60;
                            mTvForgetGetCode.setText("获取验证码");
                            mTvForgetGetCode.setClickable(true);
                            mTvForgetGetCode.setEnabled(true);
                            mTvForgetGetCode.setTextColor(getResources().getColor(R.color.white));
                        }else {
                            mTvForgetGetCode.setEnabled(false);
                            mTvForgetGetCode.setTextColor(getResources().getColor(R.color.c_B3B3B3));
                        }

                    }
                });
                mTvForgetGetCode.setClickable(false);

            }
        };
        mTimer.schedule(mTimerTask,0,1000);

    }

    /**
     * 找回密码
     *
     */
    private void findPsd() {
        String phone = this.mEtForgetPhone.getText().toString();
        if (!StringUtils.vertifyPhone(phone))
            return;

        String verifiCode = this.mEtForgetCode.getText().toString();
        if (TextUtils.isEmpty(verifiCode)){
            toast("验证码不能为空！");
            return;
        }

        String psd = this.mEtForgetPsd.getText().toString().trim();
        if (TextUtils.isEmpty(psd)){
            toast("密码不能为空！");
            return;
        }
        psd = StringUtils.getMD5(psd);

        Map<String,String> allParam = new HashMap<>();
        allParam.put("phone",phone);
        allParam.put("verifiCode",verifiCode);
        allParam.put("password",psd);

        NetWork.getInstance()
                .setTag(Qurl.sureUserPassword)
                .getApiService(LoginApi.class)
                .sureUserPassword(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(OldFindPsdActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        toast(result);
                        if (result.indexOf ( "成功" )!=1){
                            finish ();
                        }
                    }
                });
    }
}
