package java.com.lechuang.module.set;


import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.manger.UserHelper;
import com.lechuang.module.R;

import org.greenrobot.eventbus.EventBus;

@Route(path = ARouters.PATH_COMPLETE_BIND)
public class CompleteBindActivity extends BaseActivity implements View.OnClickListener{
    private TextView mTvAlipayNumber,mTvUpData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_complete_bind;
    }

    @Override
    protected void findViews() {

        mTvAlipayNumber = $(R.id.tv_bind_zfbnum);
        mTvUpData = $(R.id.tv_bind_zfb_updata);
        $ ( R.id.iv_common_back ).setOnClickListener ( this );
        mTvUpData.setOnClickListener ( this );
        ((TextView) $ ( R.id.iv_common_title )).setText ( "支付宝更换绑定" );


    }

    @Override
    protected void initView() {
        mTvAlipayNumber.setText ( "已绑定支付宝账号："+UserHelper.getInstence ().getUserInfo ().getZhifubaoNum () );
    }

    @Override
    protected void getData() {

    }


    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id ==R.id.tv_bind_zfb_updata){
            finish ();
            ARouter.getInstance().build(ARouters.PATH_VERIFY).navigation();
        }else if (id ==R.id.iv_common_back){
            finish ();
        }
    }
}
