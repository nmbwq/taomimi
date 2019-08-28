package java.com.lechuang.module.bigwhell;


import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.TryRuleBean;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_BIG_WHELL_RULE)
public class BigWhellRuleActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvContent;
    @Autowired
    public String rule;//参与活动商品id


    @Override
    protected int getLayoutId() {
        return R.layout.activity_big_whell_rule;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("活动规则");
        $(R.id.iv_common_back).setOnClickListener(this);
        mTvContent = $(R.id.tv_content);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void getData() {
//        updataAlipay();//网络请求
        mTvContent.setText( rule );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id== R.id.iv_common_back){
            finish();
        }
    }




}
