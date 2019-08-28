package java.com.lechuang.module.signin;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.lechuang.module.R;

@Route(path = ARouters.PATH_SIGN_IN_CHALLENGE)
public class SignInChallengeActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_signin_challenge;
    }

    @Override
    protected void findViews() {
        ((TextView) $ ( R.id.iv_common_title )).setText ( "挑战规则" );
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
