package java.debug;


import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.lechuang.module.R;

public class ModuleActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_module;
    }

    @Override
    protected void findViews() {

        $(R.id.btv_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_NEWS).navigation();
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }
}
