package java.com.lechuang.module.set;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.StringUtils;
import com.common.app.utils.Utils;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_NICK)
public class UpdataNickActivity extends BaseActivity implements View.OnClickListener {


    private EditText mEtUpdataNick;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_updata_nick;
    }

    @Override
    protected void findViews() {


        mEtUpdataNick = $(R.id.et_updata_nick);
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.et_updata_complete).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ((TextView) $(R.id.iv_common_title)).setText("修改昵称");
    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if(id == R.id.et_updata_complete){
            if (mEtUpdataNick.length ()<2){
                Utils.toast ( "请输入至少2位！" );
                return;
            }
            updataNick();
        }
    }

    private void updataNick() {
        String nick = this.mEtUpdataNick.getText().toString();
        if (TextUtils.isEmpty(nick)){
            toast("昵称不能为空！");
            return;
        }

        Map<String,String> allParam = new HashMap<>();
        allParam.put("nickName",nick);

        NetWork.getInstance()
                .setTag(Qurl.updateAppUser)
                .getApiService(ModuleApi.class)
                .updateAppUser(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(UpdataNickActivity.this) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        toast(result);
                        if (result.indexOf ( R.string.s_set_succes )!=1){
                            finish ();
                        }
                    }
                });
    }
}
