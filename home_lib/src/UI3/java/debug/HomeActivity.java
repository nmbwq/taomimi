package java.debug;


import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.lechuang.home.R;

import java.com.lechuang.home.HomeFragment;

public class HomeActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initView() {

        HomeFragment homeFragment = (HomeFragment) ARouter.getInstance().build(ARouters.PATH_HOME).navigation();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.rl_home_content,homeFragment)
                .show(homeFragment)
                .commit();
    }

    @Override
    protected void getData() {

    }
}
