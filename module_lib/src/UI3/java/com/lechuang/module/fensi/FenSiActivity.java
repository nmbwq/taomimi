package java.com.lechuang.module.fensi;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.androidkun.xtablayout.XTabLayout;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.utils.Utils;
import com.common.app.view.CommonPopupwind;
import com.lechuang.module.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.fensi.adapter.TabViewPagerAdapter;
import java.util.ArrayList;
import java.util.List;

@Route(path = ARouters.PATH_FENSI_A)
public class FenSiActivity extends BaseActivity implements View.OnClickListener {

    private XTabLayout mXTabLayoutFenSi;
    private ViewPager mVpFenSi;
    private List<Fragment> mTabFragment = new ArrayList<> ();//存储fragment
    public TextView mTvCommonRight;//右面的弹出框
    private CommonPopupwind mCommonPopupwind;
    private TextView mTvFenSiAll;
    private TextView mTvFenSiSuperVip;
    private TextView mTvFenSiPlainVip;
    public String type = null;
    public String variety = "2";

    public TextView getmTvCommonRight() {
        return mTvCommonRight;
    }

    public void setmTvCommonRight(TextView mTvCommonRight) {
        this.mTvCommonRight = mTvCommonRight;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fen_si;
    }

    @Override
    protected void findViews() {
        EventBus.getDefault ().register ( this );
        mXTabLayoutFenSi = findViewById ( R.id.xTablayout_fensi );

        mVpFenSi = findViewById ( R.id.vp_fensi );

        $ ( R.id.iv_common_back ).setOnClickListener ( this );
        ((TextView) $ ( R.id.iv_common_title )).setText ( "粉丝" );

        mTvCommonRight = findViewById ( R.id.tv_common_right );
        mTvCommonRight.setOnClickListener ( this );
    }

    @Override
    protected void initView() {

        String[] fenSiTab = getResources ().getStringArray ( R.array.s_fensi_tab );
        mXTabLayoutFenSi.removeAllTabs ();

        for (int i = 0; i < fenSiTab.length; i++) {
            mXTabLayoutFenSi.addTab ( mXTabLayoutFenSi.newTab ().setText ( fenSiTab[i] ) );
//            mXTabLayoutFenSi.addView(  );
//            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_FENSI_F).withString ( "type",i+"" ).navigation());
        }
        mTabFragment.add ( (Fragment) ARouter.getInstance ().build ( ARouters.PATH_FENSI_F ).withString ( "type", "0" ).navigation () );
        mTabFragment.add ( (Fragment) ARouter.getInstance ().build ( ARouters.PATH_FENSI_FS ).withString ( "type", "1" ).navigation () );
        mTabFragment.add ( (Fragment) ARouter.getInstance ().build ( ARouters.PATH_FENSI_FR ).withString ( "type", "2" ).navigation () );
        mVpFenSi.setOffscreenPageLimit ( 2 );
        mVpFenSi.setAdapter ( new TabViewPagerAdapter ( BaseApplication.getApplication (), getSupportFragmentManager (), fenSiTab, mTabFragment ) );
        mXTabLayoutFenSi.setupWithViewPager ( mVpFenSi );

    }

    @Override
    protected void getData() {

    }

    @Override
    protected void savedInstance(@Nullable Bundle savedInstanceState) {
        super.savedInstance ( savedInstanceState );
        //数据恢复
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState ( outState );
        //数据暂存
    }

    private CommonPopupwind createPopuwind() {
        mCommonPopupwind = new CommonPopupwind.Builder ()
                .showAsDropDown ( mTvCommonRight )
                .setWindAlpha ( getWindow (), 0.5f )
                .build ( FenSiActivity.this, R.layout.popupwind_fensi );
        mTvFenSiAll = (TextView) mCommonPopupwind.findViewById ( R.id.tv_fensi_all );
        mTvFenSiSuperVip = (TextView) mCommonPopupwind.findViewById ( R.id.tv_fensi_super_vip );
        mTvFenSiPlainVip = (TextView) mCommonPopupwind.findViewById ( R.id.tv_fensi_plain_vip );
        mTvFenSiAll.setOnClickListener ( this );
        mTvFenSiSuperVip.setOnClickListener ( this );
        mTvFenSiPlainVip.setOnClickListener ( this );


        return mCommonPopupwind;
    }

    private void showPop() {
        if (variety.equals ( "0" )) {
            mTvFenSiAll.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiSuperVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiPlainVip.setTextColor ( getResources ().getColor ( R.color.c_main ) );
        } else if (variety.equals ( "1" )) {
            mTvFenSiAll.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiSuperVip.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvFenSiPlainVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
        } else {
            mTvFenSiAll.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvFenSiSuperVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiPlainVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        if (id == R.id.iv_common_back) {//关闭
            finish ();
        } else if (id == R.id.tv_common_right) {
            if (mCommonPopupwind == null) {
                mCommonPopupwind = createPopuwind ();
            }
            mCommonPopupwind.showPopupwind ( mCommonPopupwind );
            showPop ();
        } else if (id == R.id.tv_fensi_all) {
            changePopupwindTvStyle ( R.string.s_all );
        } else if (id == R.id.tv_fensi_super_vip) {
            changePopupwindTvStyle ( R.string.s_super_vip );
        } else if (id == R.id.tv_fensi_plain_vip) {

            changePopupwindTvStyle ( R.string.s_plain_vip );
        }
    }

    private void changePopupwindTvStyle(@StringRes int tvId) {
        FenSiFragment fenSiFragment = new FenSiFragment ();
        mTvCommonRight.setText ( tvId );
        if (tvId == R.string.s_all) {
            mTvFenSiAll.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvFenSiSuperVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiPlainVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            fenSiFragment.page = 1;
            variety = "2";
            sendData ( variety );
//            fenSiFragment.getAllData ();
        } else if (tvId == R.string.s_super_vip) {
            mTvFenSiAll.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiSuperVip.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvFenSiPlainVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            fenSiFragment.page = 1;
            variety = "1";
            sendData ( variety );
//            fenSiFragment.getAllData ();
        } else if (tvId == R.string.s_plain_vip) {
            mTvFenSiAll.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiSuperVip.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvFenSiPlainVip.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            fenSiFragment.page = 1;
            variety = "0";
            sendData ( variety );
//            fenSiFragment.getAllData ();
        }
        if (mCommonPopupwind != null) {
            mCommonPopupwind.dismiss ();
        }
    }

    @Override
    public void onPause() {
        super.onPause ();
        EventBus.getDefault ().unregister ( this );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase ( Constant.SENDDATA )) {
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            mTvCommonRight.setText ( "全部" );
            variety = "2";
        } else if (message.equalsIgnoreCase ( Constant.SENDDATAO )) {
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            mTvCommonRight.setText ( "普通会员" );
            variety = "0";
        } else if (message.equalsIgnoreCase ( Constant.SENDDATAONE )) {
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            mTvCommonRight.setText ( "超级会员" );
            variety = "1";
        }
    }

    private void sendData(String variety) {
        if (variety.equals ( "0" )) {
            EventBus.getDefault ().post ( Constant.SENDDATAO0 );
        } else if (variety.equals ( "1" )) {
            EventBus.getDefault ().post ( Constant.SENDDATAONE1 );
        } else {
            EventBus.getDefault ().post ( Constant.SENDDATA2 );
        }
    }

}
