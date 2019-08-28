package java.com.lechuang.module.upgrade;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ShareUtils;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouwei.mzbanner.BannerBgContainer;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FlashSaleTitleAndTimeBean;
import java.com.lechuang.module.bean.MyRankingBean;
import java.com.lechuang.module.bean.ShowInMyCardBean;
import java.com.lechuang.module.bean.UpGradeDetailsBean;
import java.com.lechuang.module.bean.ZeroBuyDetailsBean;
import java.com.lechuang.module.bean.ZeroBuyJoinSuccessBean;
import java.com.lechuang.module.flashsale.FlashSaleActivity;
import java.com.lechuang.module.flashsale.adapter.FlashSaleTabsViewPagerAdapter;
import java.com.lechuang.module.flashsale.bean.FlashSaleTabBean;
import java.com.lechuang.module.mytry.bean.TryDetailsEntity;
import java.com.lechuang.module.product.adapter.BannerViewHolder;
import java.com.lechuang.module.upgrade.adapter.UpGradeDetailsTabsViewPagerAdapter;
import java.com.lechuang.module.zerobuy.MyZeroBuyActivity;
import java.com.lechuang.module.zerobuy.adapter.ZeroBuyDetailsAdapter;
import java.com.lechuang.module.zerobuy.bean.ZeroBuyDetailsEntity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_UP_GRADE_DETAILS)
public class UpGradeDetailsActivity extends BaseActivity implements View.OnClickListener{
    private TabLayout mXTabLayoutFlashSale;
    private ViewPager mVpFlashSale;
    private List<FlashSaleTabBean> mClassType;
    private List<Fragment> mTabFragment = new ArrayList<> ();//存储fragment
    private List<String> tabS=new ArrayList<>(  );
    private List<String> tabsS=new ArrayList<>(  );
    private List<String> timeNumber=new ArrayList<>(  );
    private List<String> price=new ArrayList<>(  );
    private List<String> id=new ArrayList<>(  );
    public static UpGradeDetailsActivity flashSaleActivity;
    //    private String[] tabsS = getResources().getStringArray(R.array.s_flashsale_tab);
//    private String[] tabsS=new String[13] ;
    private int titleNum=0;
    private int getNum=0;
    @Autowired
    public int intId;//点击了第几个


    @Override
    protected int getLayoutId() {
        return R.layout.activity_up_grade_details;
    }

    @Override
    protected void findViews() {
        flashSaleActivity=this;
        mXTabLayoutFlashSale = findViewById ( R.id.xTablayout_flashsale );
        mVpFlashSale = findViewById ( R.id.vp_flashsale );

        $ ( R.id.iv_common_back ).setOnClickListener ( this );
        ((TextView) $ ( R.id.iv_common_title )).setText ( "商品详情" );


    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
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
        getTabTitle();
    }
    private void getTabTitle(){
        NetWork.getInstance()
                .setTag( Qurl.getProDetail )
                .getApiService( ModuleApi.class )
                .getProDetail( )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<UpGradeDetailsBean>( UpGradeDetailsActivity.this, false, true ) {
                    @Override
                    public void onSuccess(UpGradeDetailsBean result) {
                        if (result == null) {
                            return;
                        }
                        //设置Tab数据
//                        for ()
                        for (int i=0;i<result.getList().size();i++){
                            tabS.add( result.list.get( i ).showImg );//顶部图片
                            tabsS.add( result.list.get( i ).img );//带文字的图片
                            timeNumber.add( result.list.get( i ).detailImg );//详情图片
                            DecimalFormat decimalFormat=new DecimalFormat("#0.00");
                            String pricenum=decimalFormat.format(result.list.get( i ).price)+"";
                            price.add( pricenum );//金额
                            id.add( result.list.get( i ).id+"" );//id
                            if (intId==1000000000){
                                getNum=0;
                            }else {
                                if (result.list.get( i ).bannerId==intId){
                                    getNum=titleNum;
                                }
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
            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_UP_GRADE_DETAILS_F).withString("img",tabsS.get( i )  ).withString( "detailImg",timeNumber.get(i) ).withString( "price",price.get(i) ).withString( "id",id.get(i) ).navigation());
//            if (i==0){
//                mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_UP_GRADE_DETAILS_F).withString("img",tabsS.get( i )  ).withString( "detailImg",timeNumber.get(i) ).withString( "price",price.get(i) ).withString( "id",id.get(i) ).navigation());
//            }else {
//                mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_UP_GRADE_DETAILS_OTHER_F).withString("img",tabsS.get( i )  ).withString( "detailImg",timeNumber.get(i) ).withString( "price",price.get(i) ).withString( "id",id.get(i) ).navigation());
//            }
        }
        mVpFlashSale.setOffscreenPageLimit(tabS.size() + 1);
//        mVpFlashSale.setOffscreenPageLimit( 1);
        mVpFlashSale.setAdapter(new UpGradeDetailsTabsViewPagerAdapter( getSupportFragmentManager(), mTabFragment));
        mXTabLayoutFlashSale.setupWithViewPager ( mVpFlashSale );

        for (int i = 0;i<tabS.size();i++){
            TabLayout.Tab tab = mXTabLayoutFlashSale.getTabAt( i );
            View view = LayoutInflater.from( UpGradeDetailsActivity.this ).inflate( R.layout.activity_upgradedetails_tablayout,null );
            ImageView imageView = (ImageView)view.findViewById( R.id.iv_tupian );
            Glide.with(BaseApplication.getApplication()).load(tabS.get(i)).into(imageView);
            tab.setCustomView( view );

        }
        mXTabLayoutFlashSale.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVpFlashSale.setCurrentItem( tab.getPosition() );
                View view = tab.getCustomView();
//                if (null!=view&&view instanceof TextView){
//                TextView title1 = view.findViewById( R.id.tv_tablayout1 );
//                TextView title2 = view.findViewById( R.id.tv_tablayout2 );
//                title1.setTextColor( getResources().getColor( R.color.white ) );
//                title2.setTextColor( getResources().getColor( R.color.white ) );
//                RelativeLayout relativeLayout=view.findViewById(R.id.rl_all);
//                relativeLayout.setBackground(getResources().getDrawable(R.drawable.tabdown_upgradedetails_selected));
                loadData(tabS.get( tab.getPosition() ));

//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
//                if (null!=view&&view instanceof TextView){
//                TextView title1 = view.findViewById( R.id.tv_tablayout1 );
//                TextView title2 = view.findViewById( R.id.tv_tablayout2 );
//                title1.setTextColor( getResources().getColor( R.color.c_333333 ) );
//                title2.setTextColor( getResources().getColor( R.color.c_999999 ) );
//                RelativeLayout relativeLayout=view.findViewById(R.id.rl_all);
//                relativeLayout.setBackground(getResources().getDrawable(R.drawable.tabdown_upgradedetails_unselected));
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
