package java.com.lechuang.module.weeksignin;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.common.app.base.BaseActivity;
import com.lechuang.module.R;

public class WeekSigninRuleActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvCommonTitle,mTvWeekSigninRule;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_week_signin_rule;
    }

    @Override
    protected void findViews() {
        mTvCommonTitle = $(R.id.iv_common_title);
        mTvWeekSigninRule = $(R.id.tv_week_signin_rule);
        $(R.id.iv_common_back).setOnClickListener(this);
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
        }
    }
}
