package java.com.lechuang.module.flashsale;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.androidkun.xtablayout.XTabLayout;
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
import com.common.app.utils.StatusBarUtil;
import com.common.app.view.CommonPopupwind;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.SpannelTextViewSinge;
import com.lechuang.module.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FlashSaleTitleAndTimeBean;
import java.com.lechuang.module.bean.OrderBean;
import java.com.lechuang.module.fensi.FenSiFragment;
import java.com.lechuang.module.fensi.adapter.TabViewPagerAdapter;
import java.com.lechuang.module.flashsale.adapter.FlashSaleTabViewPagerAdapter;
import java.com.lechuang.module.flashsale.adapter.FlashSaleTabsViewPagerAdapter;
import java.com.lechuang.module.flashsale.bean.FlashSaleTabBean;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_FLASH_SALE)
public class FlashSaleActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mXTabLayoutFlashSale;
    private ViewPager mVpFlashSale;
    private List<FlashSaleTabBean> mClassType;
    private List<Fragment> mTabFragment = new ArrayList<> ();//存储fragment
    private List<String> tabS=new ArrayList<>(  );
    private List<String> tabsS=new ArrayList<>(  );
    private List<String> timeNumber=new ArrayList<>(  );
    public static FlashSaleActivity flashSaleActivity;
//    private String[] tabsS = getResources().getStringArray(R.array.s_flashsale_tab);
//    private String[] tabsS=new String[13] ;
    private int titleNum=0;
    private int getNum=0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_flash_sale;
    }

    @Override
    protected void findViews() {
        flashSaleActivity=this;
        mXTabLayoutFlashSale = findViewById ( R.id.xTablayout_flashsale );
        mVpFlashSale = findViewById ( R.id.vp_flashsale );

        $ ( R.id.iv_common_back ).setOnClickListener ( this );
        ((TextView) $ ( R.id.iv_common_title )).setText ( "限时抢购" );


    }

    @Override
    protected void initView() {
        getTabTitle();
       /* mClassType = new ArrayList<>();

        mXTabLayoutFlashSale.removeAllTabs ();
        for (int i = 0;i<tabS.size();i++){
            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_FLASH_SALE_FRAGMENT).withString("xtabs",tabS.get( i )  ).navigation());
        }
        mVpFlashSale.setOffscreenPageLimit(tabS.size() + 1);
        mVpFlashSale.setAdapter(new FlashSaleTabsViewPagerAdapter( getSupportFragmentManager(), mTabFragment));
        mXTabLayoutFlashSale.setupWithViewPager ( mVpFlashSale );

        for (int i = 0;i<tabS.size();i++){
            TabLayout.Tab tab = mXTabLayoutFlashSale.getTabAt( i );
            View view = LayoutInflater.from( FlashSaleActivity.this ).inflate( R.layout.activity_flashsale_tablayout,null );
            TextView textView1 = (TextView)view.findViewById( R.id.tv_tablayout1 );
            TextView textView2 = (TextView)view.findViewById( R.id.tv_tablayout2 );
            textView1.setText( tabS.get( i ) );
            textView2.setText( tabsS.get( i ) );
            tab.setCustomView( view );

        }
        mXTabLayoutFlashSale.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVpFlashSale.setCurrentItem( tab.getPosition() );
                View view = tab.getCustomView();
//                if (null!=view&&view instanceof TextView){
                TextView title1 = view.findViewById( R.id.tv_tablayout1 );
                TextView title2 = view.findViewById( R.id.tv_tablayout2 );
                title1.setTextColor( getResources().getColor( R.color.white ) );
                title2.setTextColor( getResources().getColor( R.color.white ) );
                loadData(tabS.get( tab.getPosition() ));

//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
//                if (null!=view&&view instanceof TextView){
                TextView title1 = view.findViewById( R.id.tv_tablayout1 );
                TextView title2 = view.findViewById( R.id.tv_tablayout2 );
                title1.setTextColor( getResources().getColor( R.color.c_333333 ) );
                title2.setTextColor( getResources().getColor( R.color.c_999999 ) );
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );
        */



    }

    @Override
    protected void getData() {

    }
    private void getTabTitle(){
        NetWork.getInstance()
                .setTag( Qurl.titleAndTime )
                .getApiService( ModuleApi.class )
                .getTitleAndTime( )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<FlashSaleTitleAndTimeBean>( FlashSaleActivity.this, false, true ) {
                    @Override
                    public void onSuccess(FlashSaleTitleAndTimeBean result) {
                        if (result == null) {
                            return;
                        }
                        //设置Tab数据
//                        for ()
                        for (int i=0;i<result.getList().size();i++){
                            tabS.add( result.list.get( i ).time );
                            tabsS.add( result.list.get( i ).statusStr );
                            timeNumber.add( result.list.get( i ).timeNum );
                            if (result.list.get( i ).status==1){
                                getNum=titleNum;
                            }
                            titleNum++;
                            if (titleNum==result.getList().size()){
                                addTabTitle();
                            }
                        }
//                        setTabTitle(result.getList());

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed( errorCode, moreInfo );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError( e );
                    }
                } );
    }
   /*
    private List<FlashSaleTitleAndTimeBean.ListBean> mProductList;
    private BaseQuickAdapter<FlashSaleTitleAndTimeBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;
    private void setTabTitle(final List<FlashSaleTitleAndTimeBean.ListBean> productList) {
        if (productList == null || productList.size () <= 0) {
            return;
        }

        if (mProductList == null) {
            mProductList = new ArrayList<> ();
        }

        mProductList.addAll ( productList );
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<FlashSaleTitleAndTimeBean.ListBean, BaseViewHolder> ( R.layout.item_flashsale_list, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, FlashSaleTitleAndTimeBean.ListBean item) {
                    try {


                    } catch (Exception e) {

                    }
                }
            };
        }
    }*/
    public void addTabTitle(){
         mClassType = new ArrayList<>();

        mXTabLayoutFlashSale.removeAllTabs ();
        for (int i = 0;i<tabS.size();i++){
            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_FLASH_SALE_FRAGMENT).withString("xtabs",timeNumber.get( i )  ).withInt( "getNum",getNum ).withInt( "num",i ).navigation());
        }
        mVpFlashSale.setOffscreenPageLimit(tabS.size() + 1);
        mVpFlashSale.setAdapter(new FlashSaleTabsViewPagerAdapter( getSupportFragmentManager(), mTabFragment));
        mXTabLayoutFlashSale.setupWithViewPager ( mVpFlashSale );

        for (int i = 0;i<tabS.size();i++){
            TabLayout.Tab tab = mXTabLayoutFlashSale.getTabAt( i );
            View view = LayoutInflater.from( FlashSaleActivity.this ).inflate( R.layout.activity_flashsale_tablayout,null );
            TextView textView1 = (TextView)view.findViewById( R.id.tv_tablayout1 );
            TextView textView2 = (TextView)view.findViewById( R.id.tv_tablayout2 );
            textView1.setText( tabS.get( i ) );
            textView2.setText( tabsS.get( i ) );
            tab.setCustomView( view );

        }
        mXTabLayoutFlashSale.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVpFlashSale.setCurrentItem( tab.getPosition() );
                View view = tab.getCustomView();
//                if (null!=view&&view instanceof TextView){
                TextView title1 = view.findViewById( R.id.tv_tablayout1 );
                TextView title2 = view.findViewById( R.id.tv_tablayout2 );
                title1.setTextColor( getResources().getColor( R.color.white ) );
                title2.setTextColor( getResources().getColor( R.color.white ) );
                loadData(tabS.get( tab.getPosition() ));

//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
//                if (null!=view&&view instanceof TextView){
                TextView title1 = view.findViewById( R.id.tv_tablayout1 );
                TextView title2 = view.findViewById( R.id.tv_tablayout2 );
                title1.setTextColor( getResources().getColor( R.color.c_333333 ) );
                title2.setTextColor( getResources().getColor( R.color.c_999999 ) );
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );
        mXTabLayoutFlashSale.getTabAt( getNum ).select();
    }
    /*@Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }*/
    public void  loadData(final String url){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String json = url;

                //在子线程当中发送数据给主线程
                EventBus.getDefault().post(json);
            }
        }.start();
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


    @Override
    public void onClick(View v) {
        int id = v.getId ();
        if (id == R.id.iv_common_back) {//关闭
            finish ();
        }
    }
}
