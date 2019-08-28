package java.com.lechuang.brand;

import android.graphics.Paint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.Logger;
import com.common.app.utils.StringUtils;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.NoShouYiTiaoJian;
import com.common.app.view.SpannelTextViewMore;
import com.common.app.view.SpannelTextViewSinge;
import com.common.app.view.TiaoJianView;
import com.common.app.view.TransChangeNesScrollView;
import com.lechuang.brand.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.com.lechuang.brand.adapter.BannerViewHolder;
import java.com.lechuang.brand.bean.BrandInfoBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_BRAND_ACTIVITY)
public class BrandActivity extends BaseActivity implements  View.OnClickListener {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_brand;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("品牌闪购");
        $(R.id.iv_common_back).setOnClickListener(this);

//        mIvCommonBack = $(R.id.iv_common_back);
    }

    @Override
    protected void initView() {

        ARouter.getInstance().inject(this);


    }

    @Override
    protected void getData() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        }
    }
}
