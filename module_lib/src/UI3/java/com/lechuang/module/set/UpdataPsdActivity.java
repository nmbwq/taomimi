package java.com.lechuang.module.set;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.StringUtils;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_UPDATA_PSD)
public class UpdataPsdActivity extends BaseActivity implements View.OnClickListener {

    private boolean mIsMingWen = false;//标识密码是否为明文转态
    private ImageView mIvUpdataPsdEyes;
    private EditText  mEtUpdataPsd, mEtUpdataCode;
    private Timer mTimer;//验证码计时
    private int mCount = 60;//验证码计时累计
    private TimerTask mTimerTask;
    private TextView mEtUpdataPhone,mEtUpdataGetCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_updata_psd;
    }

    @Override
    protected void findViews() {

        ((TextView) $(R.id.iv_common_title)).setText("修改密码");

        mIvUpdataPsdEyes = $(R.id.iv_updata_eyes);
        mEtUpdataPsd = $(R.id.et_updata_psd);

        mEtUpdataPhone = $(R.id.et_updata_psd_phone);
        mEtUpdataCode = $(R.id.et_updata_psd_code);
        mEtUpdataGetCode = $(R.id.et_updata_psd_getcode);

        mEtUpdataPhone.setText ( UserHelper.getInstence ().getUserInfo ().getPhone ());
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.et_updata_phone_complete).setOnClickListener(this);
        $(R.id.et_updata_psd_code).setOnClickListener(this);
        $(R.id.rl_updata_eyes).setOnClickListener(this);
        mEtUpdataGetCode.setOnClickListener(this);

    }

    private void changeYanJingState() {
        mIvUpdataPsdEyes.setImageResource(this.mIsMingWen ? R.drawable.ic_eyes_close : R.drawable.ic_eyes_open);
        mEtUpdataPsd.setTransformationMethod(this.mIsMingWen ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
        this.mIsMingWen = !this.mIsMingWen;
        String text = mEtUpdataPsd.getText().toString();
        if (text.length() > 0) {
            mEtUpdataPsd.setSelection(text.length());
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.rl_updata_eyes) {
            changeYanJingState();
        } else if (id == R.id.et_updata_phone_complete) {
            updataPsd();
        } else if (id == R.id.et_updata_psd_getcode) {
//            String phone = mEtUpdataPhone.getText().toString().trim();
            String phone = UserHelper.getInstence ().getUserInfo ().getPhone ();
            //手机号验证通过发送验证码
            if (StringUtils.vertifyPhone(phone)) {
                sendVerifiCode(phone);
            }
        }
    }

    /**
     * 修改密码
     */
    private void updataPsd() {
        String phone = this.mEtUpdataPhone.getText().toString();
        if (!StringUtils.vertifyPhone(phone))
            return;

        String verifiCode = this.mEtUpdataCode.getText().toString();
        if (TextUtils.isEmpty(verifiCode)) {
            toast("验证码不能为空！");
            return;
        }

        String psd = this.mEtUpdataPsd.getText().toString().trim();
        if (TextUtils.isEmpty(psd)) {
            toast("密码不能为空！");
            return;
        }
        if (psd.length ()<6){
            toast("新密码不能少于6位！");
            return;
        }
        if (StringUtils.verifyPsd(psd)){
            return;
        }
        psd = StringUtils.getMD5(psd);

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        allParam.put("verifiCode", verifiCode);
        allParam.put("password", psd);

        NetWork.getInstance()
                .setTag(Qurl.updateAppUserPassword)
                .getApiService(ModuleApi.class)
                .updateAppUserPassword(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(UpdataPsdActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)) {
                            return;
                        }
                        toast(result);
                        if (result.indexOf ( R.string.s_set_succes )!=1){
                            finish ();
                        }
                    }
                });
    }

    /**
     * 修改密码发送验证码
     *
     * @param phone
     */
    private void sendVerifiCode(String phone) {

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        allParam.put("type", "1");
        NetWork.getInstance()
                .setTag(Qurl.sendVerifiCode)
                .getApiService(ModuleApi.class)
                .sendVerifiCode(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(UpdataPsdActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)) {
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
                        }
                    }
                });
                mEtUpdataGetCode.setClickable(false);

            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);

    }
}
