package java.com.lechuang.login;


import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
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

import java.com.lechuang.login.bean.UsersBean;
import java.com.lechuang.login.http.api.LoginApi;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SET_PSD)
public class SetPsdActivity extends BaseOtherActivity implements View.OnClickListener {

    @Autowired
    public String phone;
    private EditText mEtPsd,mEtYaoQingMa;
    private ImageView mIvEyes;
    private boolean mIsMingWen = false;//标识密码是否为明文转态

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_psd;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.tv_common_right)).setTextColor(getResources().getColor(R.color.c_main));
        ((TextView) $(R.id.tv_common_right)).setText("完成");
        ((TextView) $(R.id.tv_common_right)).setVisibility(View.VISIBLE);

        ((TextView) $(R.id.iv_common_title)).setText("设置登录密码");
        ((TextView) $(R.id.tv_status_bar)).setBackgroundColor(getResources().getColor(R.color.c_EEEEEE));
        mEtPsd = $(R.id.pass_et);
        mEtYaoQingMa = $(R.id.yaoqingma_et);
        mIvEyes = findViewById(R.id.iv_eyes);


        mIvEyes.setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        $(R.id.iv_common_back).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        ((TextView) $(R.id.tv_phone)).setText(TextUtils.isEmpty(phone) ? "" : phone);
    }

    @Override
    protected void getData() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id == R.id.tv_common_right){//完成
            login();
        }else if (id == R.id.iv_eyes ){
            changeYanJingState();
        }
    }

    private void changeYanJingState(){
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
    private void login() {
        if (!StringUtils.vertifyPhone(phone)){
            toast("手机号不能为空！");
            return;
        }

        String invitationCode = this.mEtYaoQingMa.getText().toString().trim();
        if (TextUtils.isEmpty(invitationCode)){
            toast("激活码不能为空！");
            return;
        }

        String psd = this.mEtPsd.getText().toString().trim();
        if (StringUtils.verifyPsd(psd)) {
            return;
        }
        psd = StringUtils.getMD5(psd);

        Map<String, String> allParam = new HashMap<>();
        allParam.put("phone", phone);
        allParam.put("password", psd);
        allParam.put("invitationCode", invitationCode);

        NetWork.getInstance()
                .setTag(Qurl.improveUserInfo)
                .getApiService(LoginApi.class)
                .improveUserInfo(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<UsersBean>(SetPsdActivity.this) {

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
                            toast(e.toString());
                        }


                    }
                });
    }
}
