package java.com.lechuang.module.superentrance;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.lechuang.module.R;

@Route(path = ARouters.PATH_SUPERTUTORIAL)
public class SuperTutorialActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_super_tutorial;
    }

    @Override
    protected void findViews() {
        ((TextView) $ ( R.id.iv_common_title )).setText ( "佣金教程" );
        ((LinearLayout) $ ( R.id.ll_title_back )).setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                finish ();
            }
        } );
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

}
