package java.com.lechuang.module.set;


import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.StringUtils;
import com.lechuang.module.R;

import org.greenrobot.eventbus.EventBus;

import java.com.lechuang.module.ModuleApi;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_VERIFY)
public class AuthenticationActivity extends BaseActivity implements View.OnClickListener{
    private EditText mEtUpdataCode;
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;
    private TextView mEtUpdataPhone,mEtUpdataGetCode;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_authentication;
    }

    @Override
    protected void findViews() {
        mEtUpdataPhone = $(R.id.et_authentication_phone_phone);
        mEtUpdataCode = $(R.id.et_authentication_phone_code);
        mEtUpdataGetCode = $(R.id.et_authentication_phone_getcode);
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.et_authentication_phone_getcode).setOnClickListener(this);
        $(R.id.et_authentication_phone_complete).setOnClickListener(this);
        mEtUpdataPhone.setText ( UserHelper.getInstence ().getUserInfo ().getPhone () );
        ((TextView) $ ( R.id.iv_common_title )).setText ( "身份验证" );
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id== R.id.iv_common_back){
            finish();
        }else  if (id == R.id.et_authentication_phone_getcode){
            String phone = UserHelper.getInstence ().getUserInfo ().getPhone ();
            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                sendVerifiCode(phone);
            }
        }else if (id == R.id.et_authentication_phone_complete){
            updataPhone();
        }
    }

    /**
     * 修改手机号
     *
     */
    private void updataPhone() {
        String phone = this.mEtUpdataPhone.getText().toString();
        if (!StringUtils.vertifyPhone(phone))
            return;
        String verifiCode = this.mEtUpdataCode.getText().toString();
        if (TextUtils.isEmpty(verifiCode)){
            toast("验证码不能为空！");
            return;
        }
        Map<String,String> allParam = new HashMap<> ();
        allParam.put("verifiCode",verifiCode);

        NetWork.getInstance()
                .setTag( Qurl.authentication)
                .getApiService(ModuleApi.class)
                .authentication(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String> (AuthenticationActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        toast(result);
                        if ( result.toString ().indexOf ( "验证码正确" )!=-1){
                            finish ();
                            ARouter.getInstance().build(ARouters.PATH_BIND_ZFB).navigation();
                        }
                    }
                });
    }

    private void sendVerifiCode(String phone) {

        Map<String,String> allParam = new HashMap<>();
        allParam.put("phone",phone);
        allParam.put("type","1");
        NetWork.getInstance()
                .setTag(Qurl.sendVerifiCode)
                .getApiService(ModuleApi.class)
                .sendVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(AuthenticationActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        //设置验证码按钮状态
                        setGetCodeBtn();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        setGetCodeBtn();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
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
                        mEtUpdataGetCode.setText(mCount + "s后可以重发");
                        mCount--;

                        if (mCount < 0) {

                            mTimerTask.cancel();
                            mTimerTask = null;
                            mTimer.cancel();
                            mTimer = null;
                            mCount = 60;
                            mEtUpdataGetCode.setText("获取验证码");
                            mEtUpdataGetCode.setClickable(true);
                            mEtUpdataGetCode.setEnabled(true);
                        }else {
                            mEtUpdataGetCode.setEnabled(false);
                        }
                    }
                });
                mEtUpdataGetCode.setClickable(false);

            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);

    }
}
