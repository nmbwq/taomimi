package java.com.lechuang.module.set;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.lechuang.module.R;

import org.greenrobot.eventbus.EventBus;

import java.com.lechuang.module.ModuleApi;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_BIND_ZFB)
public class BindZfbActivity extends BaseActivity implements View.OnClickListener{
    private EditText mEtAlipayNumber, mEtAlipayRealName;
    private TextView mTvSave;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_zfb;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("绑定支付宝");
        $(R.id.iv_common_back).setOnClickListener(this);
        mEtAlipayNumber = $(R.id.et_bind_alipayNumber);
        mEtAlipayRealName = $(R.id.et_bind_alipayRealName);
        mTvSave = $(R.id.et_bind_zfb_save);
        mTvSave.setOnClickListener ( this );
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
        if (id == R.id.et_bind_zfb_save){
            updataAlipay();
        }else if (id== R.id.iv_common_back){
            finish();
        }
    }

    private void updataAlipay() {
        final String alipayNumber = mEtAlipayNumber.getText().toString();
        String alipayRealName = mEtAlipayRealName.getText().toString();
        if (TextUtils.isEmpty(alipayNumber)){
            toast("支付宝账号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(alipayRealName)){
            toast("支付宝账号不能为空！");
            return;
        }

        Map<String,String> allParam = new HashMap<> ();
        allParam.put("alipayNumber",alipayNumber);
        allParam.put("alipayRealName",alipayRealName);

        NetWork.getInstance()
                .setTag( Qurl.updateAppUser)
                .getApiService(ModuleApi.class)
                .updateAppUser(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String> (BindZfbActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        toast(result);
                        if (result.indexOf ( R.string.s_set_succes )!=1){
                            UserHelper.getInstence ().getUserInfo ().setZhifubaoNum ( alipayNumber );
                            finish ();
                        }
                    }
                });
    }
}
