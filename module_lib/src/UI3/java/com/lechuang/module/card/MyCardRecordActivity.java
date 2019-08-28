package java.com.lechuang.module.card;


import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.androidkun.xtablayout.XTabLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.Utils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.EarningsRecordBean;
import java.com.lechuang.module.fensi.adapter.TabViewPagerAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


@Route(path = ARouters.PATH_MY_CARD_RECORD)
public class MyCardRecordActivity extends BaseActivity implements View.OnClickListener {
    private XTabLayout mXTabLayoutFenSi;
    private ViewPager mVpFenSi;
    private List<Fragment> mTabFragment = new ArrayList<> ();//存储fragment
    private ImageView back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mycard_record;
    }

    @Override
    protected void findViews() {
        mXTabLayoutFenSi = findViewById ( R.id.xTablayout_myCardRecord );
        back = findViewById ( R.id.iv_common_back );

        mVpFenSi = findViewById ( R.id.vp_myCardRecord );
        back.setOnClickListener( this );
    }

    @Override
    protected void initView() {
        String[] fenSiTab = getResources ().getStringArray ( R.array.s_mycard_tab );
        mXTabLayoutFenSi.removeAllTabs ();

        for (int i = 0; i < fenSiTab.length; i++) {
            mXTabLayoutFenSi.addTab ( mXTabLayoutFenSi.newTab ().setText ( fenSiTab[i] ) );
//            mXTabLayoutFenSi.addView(  );
//            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_FENSI_F).withString ( "type",i+"" ).navigation());
        }
        mTabFragment.add ( (Fragment) ARouter.getInstance ().build ( ARouters.PATH_MY_CARD_USED ).withString ( "type", "0" ).navigation () );
        mTabFragment.add ( (Fragment) ARouter.getInstance ().build ( ARouters.PATH_MY_CARD_FAILURE ).withString ( "type", "1" ).navigation () );
//        mTabFragment.add ( (Fragment) ARouter.getInstance ().build ( ARouters.PATH_FENSI_FR ).withString ( "type", "2" ).navigation () );
        mVpFenSi.setOffscreenPageLimit ( 2 );
        mVpFenSi.setAdapter ( new TabViewPagerAdapter( BaseApplication.getApplication (), getSupportFragmentManager (), fenSiTab, mTabFragment ) );
        mXTabLayoutFenSi.setupWithViewPager ( mVpFenSi );
    }

    @Override
    protected void getData() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        if (id == R.id.iv_common_back) {//关闭
            finish ();
        } else if (id == R.id.tv_common_right) {

        }
    }
}
