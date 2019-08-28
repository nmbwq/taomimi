package java.com.lechuang.module.zerobuy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.utils.LogUtils;

import java.com.lechuang.module.bean.JoinSuccessBean;
import java.com.lechuang.module.bean.ZeroBuyJoinSuccessBean;
import java.com.lechuang.module.mytry.TryDetailsActivity;

import static android.R.id.widget_frame;

@Route(path = ARouters.PATH_PARTICIPATE_SUCCESS_A)
public class ParticipateSuccessActivity extends BaseActivity {

    @Autowired
    public int type;//1为更多，0为参与
    @Autowired
    public String id;//1为更多，0为参与
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(widget_frame);
        return frameLayout;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        ZeroBuyJoinSuccessBean joinSuccess = (ZeroBuyJoinSuccessBean) getIntent().getSerializableExtra("joinSuccess");
        Fragment fragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_PARTICIPATE_SUCCESS_F).navigation();
        Bundle bundle = new Bundle();
        bundle.putSerializable("joinSuccess", joinSuccess);
        bundle.putInt( "type",type );
        bundle.putString( "id",id );
        fragment.setArguments(bundle);
        ZeroBuyDetailsActivity.refresh=true;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(widget_frame, fragment);
        transaction.commit();
    }

    @Override
    protected void getData() {

    }
}
