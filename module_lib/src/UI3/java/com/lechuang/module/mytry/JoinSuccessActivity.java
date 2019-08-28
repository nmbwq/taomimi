package java.com.lechuang.module.mytry;

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

import java.com.lechuang.module.bean.JoinSuccessBean;

import static android.R.id.widget_frame;

@Route(path = ARouters.PATH_JOIN_SUCCESS_A)
public class JoinSuccessActivity extends BaseActivity {
    @Autowired
    public int type;//1为更多，0为参与

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
        JoinSuccessBean joinSuccess = (JoinSuccessBean) getIntent().getSerializableExtra("joinSuccess");
        int id=getIntent().getIntExtra( "id",0 );
        Fragment fragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_JOIN_SUCCESS_F).navigation();
        Bundle bundle = new Bundle();
        bundle.putSerializable("joinSuccess", joinSuccess);
        bundle.putInt( "type",type );
        bundle.putInt( "id",id );
        fragment.setArguments(bundle);
        TryDetailsActivity.refresh=true;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(widget_frame, fragment);
        transaction.commit();
    }

    @Override
    protected void getData() {

    }
}
