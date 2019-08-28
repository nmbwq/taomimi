package java.com.lechuang.module.treasurebox;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.lechuang.module.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

public class ValueRuleActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvCommonTitle,mTvWeekSigninRule;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_week_value_rule;
    }

    @Override
    protected void findViews() {
        mTvCommonTitle = $(R.id.iv_common_title);
        mTvWeekSigninRule = $(R.id.tv_week_signin_rule);
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_value_rule_share).setOnClickListener(this);
        mTvCommonTitle.setText("活动规则");
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void getData() {
        String rule = getIntent().getStringExtra("rule");
        mTvWeekSigninRule.setText(rule);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id == R.id.tv_value_rule_share){
            AndPermission.with(ValueRuleActivity.this)
                    .permission(Permission.Group.STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            //这里需要读写的权限
                            ARouter.getInstance().build(ARouters.PATH_SHARE_APP).navigation();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(@NonNull List<String> permissions) {
                            if (AndPermission.hasAlwaysDeniedPermission(ValueRuleActivity.this, permissions)) {
                                //这个里面提示的是一直不过的权限
                            }
                        }
                    })
                    .start();
        }
    }
}
