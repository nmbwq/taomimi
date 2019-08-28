package java.com.lechuang.module.zerobuy;


import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseApplication;
import com.common.app.base.FragmentActivity;
import com.common.app.base.bean.BaseItemEntity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.LoadImage;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ZeroBuyLoadImage;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sunfusheng.marqueeview.MarqueeView;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.JoinSuccessTryCodeEntity;
import java.com.lechuang.module.bean.MyTryBean;
import java.com.lechuang.module.bean.MyZeroBuyBean;
import java.com.lechuang.module.bean.ZeroBuyShareAppBean;
import java.com.lechuang.module.mytry.adapter.MyTryRvAdapter;
import java.com.lechuang.module.mytry.bean.MyTryAllEntity;
import java.com.lechuang.module.zerobuy.adapter.MyZeroBuyAdapter;
import java.com.lechuang.module.zerobuy.bean.MyZeroBuyEntity;
import java.com.lechuang.module.zerobuy.bean.MyZeroBuyOtherEntity;
import java.com.lechuang.module.zerobuy.bean.ZeroBuyDetailsEntity;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MY_ZERO_BUY)
public class MyZeroBuyActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartFenSi;
    private ImageView imageView;
    private int page = 1;//页数
//    private LRecyclerView mRvFenSi;
    private RecyclerView mRvFenSi;
    private boolean mload = true;
    private TextView mTvVipNumber,mTvCommonNumber;
    @Autowired()
    public String userId;
    private LinearLayout mNetError;
    private boolean show=false;
    private boolean loadMore=true;//true没有上拉加载更多，false上拉加载更多

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_zerobuy;
    }

    @Override
    protected void findViews() {
        mSmartFenSi = $ ( R.id.smart_fensi );
        mRvFenSi = $ ( R.id.rv_mytry );
        mTvVipNumber = $ ( R.id.tv_vip_number );
        mTvCommonNumber = $ ( R.id.tv_common_number );
        imageView = $ ( R.id.iv_macard_bg );
        ((TextView) $(R.id.iv_common_title)).setText("0元抢购");
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_my_zero_buy_null ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "啧啧, 这里啥都没有~" );

    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        mSmartFenSi.setOnRefreshLoadMoreListener ( this );
    }

    @Override
    protected void getData() {
//设置自动加载数据
        mSmartFenSi.autoRefresh ( 500 );
        getShareImager();
//        getAllData();
    }

    /**
     * 获取刷新数据
     */
    private void getAllData() {
        Map<String, Object> allParam = new HashMap<> ();
//        allParam.put ( "userId", userId );
        allParam.put ( "page", page+"" );
        NetWork.getInstance ()
                .setTag ( Qurl.myZeroBuy )
                .getApiService ( ModuleApi.class )
                .getMyZeroBuy ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<MyZeroBuyBean> ( MyZeroBuyActivity.this, true, false ) {

                    @Override
                    public void onSuccess(MyZeroBuyBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        if (result.proList==null||result.proList.size()<1){
                            mNetError.setVisibility ( View.VISIBLE );
                            mRvFenSi.setVisibility( View.GONE );
                        }else {
                            mNetError.setVisibility ( View.GONE );
                            mRvFenSi.setVisibility( View.VISIBLE );
                            setHomeAdapter(result);
                        }
                        //表头数据
//                        setMarqueeView(result.use_list);
                        //背景色rl_all_colour
//                        Glide.with( BaseApplication.getApplication()).load(item.img).centerCrop().placeholder(R.drawable.bg_mycard_img_null).into(imageView);
//                        Glide.with( BaseApplication.getApplication()).load(result.pro_list.get( 0 ).showImg).centerCrop().placeholder(R.drawable.bg_mycard_img_null).into(imageView);
//                        setMyTryData ( result.pro_list );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }
                } );
    }
    /**
     * 设置adapter数据
     *
     * @param result
     */
    private ArrayList<BaseItemEntity> mHomeAllEntities= new ArrayList<>();
    private BaseAdapter<BaseItemEntity, BaseViewHolder> mHomeRvAdapter;
    private void setHomeAdapter(MyZeroBuyBean result) {
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        mNetError.setVisibility ( View.GONE );
        if (page == 1) {
            mHomeAllEntities.clear();

            if (result.imgUrl !=null&&result.imgUrl.length()>0){
                MyZeroBuyEntity homeAllEntity = new MyZeroBuyEntity(MyZeroBuyEntity.TYPE_HEADER);
                if (result.useList != null && result.useList.size() > 0){
                    homeAllEntity.UseList = result.useList;
                }
                homeAllEntity.imgUrl = result.imgUrl;
                mHomeAllEntities.add(homeAllEntity);
            }
            if (result.proList != null && result.proList.size() > 0){
                //更新使用者数据
                for (int i = 0; i < result.proList.size(); i++){
                    MyZeroBuyOtherEntity homeAllProduct = new MyZeroBuyOtherEntity(MyZeroBuyOtherEntity.TYPE_PRODUCT);
                    homeAllProduct.mProductListBean = result.proList.get(i);
                    mHomeAllEntities.add(homeAllProduct);
                }
            }


            //海报图
//            homeAllEntity.placardBanner = result.placardBanner;
        } else {
            if (result.proList == null || result.proList.size() <= 0) {
                setRefreshLoadMoreState(true, true);
            } else {
                mSmartFenSi.setNoMoreData(false);
            }

            for (int i = 0; result.proList != null && i < result.proList.size(); i++) {
                MyZeroBuyOtherEntity homeAllProduct = new MyZeroBuyOtherEntity(MyZeroBuyOtherEntity.TYPE_PRODUCT);
                homeAllProduct.mProductListBean = result.proList.get(i);
                mHomeAllEntities.add(homeAllProduct);
            }
        }
        if (mHomeRvAdapter ==null){
            mHomeRvAdapter = new BaseAdapter<BaseItemEntity, BaseViewHolder>(mHomeAllEntities){
                @Override
                protected void addItemTypeView() {
                    addItemType(MyZeroBuyEntity.TYPE_HEADER, R.layout.activity_my_zero_buy_title_one);
                    addItemType( MyZeroBuyOtherEntity.TYPE_PRODUCT, R.layout.item_my_zero_buy_list_two);
                }
                @Override
                protected void convert(BaseViewHolder helper, final BaseItemEntity item) {
                    int itemType = item.getItemType();
                    if (itemType == MyZeroBuyEntity.TYPE_HEADER) {
                        MyZeroBuyEntity myZeroBuyEntity = (MyZeroBuyEntity) item;
                        RelativeLayout llHomeKuaiBao = helper.getView(R.id.rl_marqueeview);
                        setMarqueeView(helper,myZeroBuyEntity.UseList,llHomeKuaiBao);
                        ImageView ivDi=helper.getView( R.id.iv_dibu );
                        ImageView beijing=helper.getView( R.id.iv_macard_bg );
                        if (myZeroBuyEntity.imgUrl!=null){
                            Glide.with(BaseApplication.getApplication()).load(myZeroBuyEntity.imgUrl)
//                                    .placeholder(R.drawable.bg_common_img_null)
                                    .into(beijing);
                            ivDi.setVisibility( View.VISIBLE );
                        }

//                        ImageView imageView=helper.getView( R.id.iv_macard_bg );
//                        ImageView ivDi=helper.getView( R.id.iv_dibu );
//                        if (item.imgUrl!=null){
//                            Glide.with(BaseApplication.getApplication()).load(item.imgUrl).placeholder(R.drawable.bg_common_img_null).into(imageView);
//                            ivDi.setVisibility( View.VISIBLE );
//                        }


                    } else if (itemType == MyZeroBuyOtherEntity.TYPE_PRODUCT) {
                        try {
                            final MyZeroBuyOtherEntity myZeroBuyEntity = (MyZeroBuyOtherEntity) item;
                            //商品图片
                            ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_all_product_tupian);
                            Glide.with(BaseApplication.getApplication()).load(myZeroBuyEntity.mProductListBean.showImgList.get( 0 )).placeholder(R.drawable.bg_common_img_null).into(ivItemAllFenLei);
                            //标题
                            helper.setText( R.id.tv_item_all_product_biaoti,myZeroBuyEntity.mProductListBean.name );
                            //进度条
                            NumSeekBar numSeekBar=helper.getView( R.id.numSeekBar );
                            int num=myZeroBuyEntity.mProductListBean.realNum*100/myZeroBuyEntity.mProductListBean.needNum;
                            numSeekBar.setProgress( num );
                            //参与人次
                            helper.setText( R.id.item_mytry_person,"需"+myZeroBuyEntity.mProductListBean.needNum+"人次" );
                            //倒计时 tv_card_directions
                            CountDownTextView countDownTextView=helper.getView(R.id.tv_card_time);
                            countDownTextView.setNormalText( "仅剩 23:59:59" ).setShowFormatTime( true ).setCountDownText( "仅剩 ","" )
                                    .setCloseKeepCountDown( false ).setShowFormatTimeOther(3).setOnCountDownFinishListener( new CountDownTextView.OnCountDownFinishListener() {
                                @Override
                                public void onFinish() {
//                                countDownTextView.setText( "过期了" );
                                }
                            } );
                            countDownTextView.setVisibility( View.VISIBLE );
                            countDownTextView.startCountDown( myZeroBuyEntity.mProductListBean.countDown /1000 );
                            /*helper.getView( R.id.ll_all_item ).setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    ARouter.getInstance().build(ARouters.PATH_TRY_DETAILS).withInt( "id",item.mProductListBean.id ).withInt( "obtype",1 ).navigation();
                                }
                            } );*/
                            helper.getView( R.id.tv_item_canjia ).setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
//                                    getProduct(item.mProductListBean.id+"");
                                    ARouter.getInstance().build(ARouters.PATH_ZERO_BUY_DETAILS).withInt( "id",myZeroBuyEntity.mProductListBean.id ).withInt( "obtype",1 ).navigation();
                                }
                            } );
                        } catch (Exception e) {
                            Logger.e("---->", e.toString());
                        }
                    }
                }
                @Override
                protected int onAttachedToRv(int position) {
                    if (position == MyZeroBuyEntity.TYPE_HEADER){
                        return 2;

                    }else if (position == MyZeroBuyOtherEntity.TYPE_PRODUCT){
                        return 1;
                    }
                    return super.onAttachedToRv(position);

                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( MyZeroBuyActivity.this, 2 );
            mRvFenSi.setLayoutManager ( gridLayoutManager );


            mRvFenSi.setAdapter ( mHomeRvAdapter );

        } else {
            /*mHomeRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    int itemViewType = mHomeRvAdapter.getItemViewType(position);
                    if (itemViewType == HomeAllEntity.TYPE_PRODUCT) {
                        try {
                            RouterBean routerBean = new RouterBean();
                            routerBean.type = 9;
                            routerBean.tbCouponId = mHomeAllEntities.get(position).mProductListBean.tbCouponId;
                            routerBean.mustParam = "type=1"
                                    + "&id=" + (TextUtils.isEmpty(mHomeAllEntities.get(position).mProductListBean.id) ? "" : mHomeAllEntities.get(position).mProductListBean.id)
                                    + "&tbItemId=" + mHomeAllEntities.get(position).mProductListBean.tbItemId;
                            LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                        } catch (Exception e) {
                            toast(e.toString());
                        }
                    }
                }
            });*/
            mHomeRvAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 获取底部的商品
     */
    private void getProductList() {
        ApiCancleManager.getInstance().removeAll();
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("page", page + "");

        NetWork.getInstance()
                .setTag(Qurl.myZeroBuy)
                .getApiService(ModuleApi.class)
                .getMyZeroBuy(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyZeroBuyBean>(MyZeroBuyActivity.this, true, false) {

                    @Override
                    public void onSuccess(MyZeroBuyBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        //设置底部商品数据（下拉加载更多）
                        setHomeAdapter(result);
//                        setProductData(result.productList);

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }


    private List<CharSequence> mMarqueeData = new ArrayList<>();
    private void setMarqueeView(BaseViewHolder helper,List<MyZeroBuyBean.UseListBean> UseList,RelativeLayout llHomeKuaiBao){

        MarqueeView tvScrollHomeAll = helper.getView(R.id.tv_mytry_name);
        RelativeLayout relativeLayout=helper.getView( R.id.rl_guize );
        relativeLayout.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_ZERO_BUY_RULE).navigation();
            }
        } );

        if (UseList == null || UseList.size()<=0){
            llHomeKuaiBao.setVisibility(View.GONE);
            return;
        }
        llHomeKuaiBao.setVisibility(View.VISIBLE);
        if(mMarqueeData==null){
            mMarqueeData = new ArrayList<>();
        }
        mMarqueeData.clear();
        for (int i = 0; i < UseList.size(); i++) {
            SpannableString ss1= new SpannableString(UseList.get( i ).nickName + "参与了" + UseList.get( i ).proName+"活动");
            if (UseList.get( i ).nickName.length()>4){
                ss1 = new SpannableString(UseList.get( i ).nickName.substring( 0,1 )+"..."
                        +UseList.get( i ).nickName.substring( UseList.get( i ).nickName.length()-1,UseList.get( i ).nickName.length() )
                        + "参与了" + UseList.get( i ).proName+"活动");
            }
            mMarqueeData.add(ss1);
        }
        tvScrollHomeAll.startWithList(mMarqueeData, R.anim.anim_bottom_in, R.anim.anim_top_out);
        tvScrollHomeAll.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                //上下滑动的红包
//                toast(position + textView.getText().toString());

            }
        });
        tvScrollHomeAll.startFlipping();

    }

    //请求获取邀请好友图片以及二维码
    private void getShareImager(){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("type", 6);
        NetWork.getInstance()
                .setTag(Qurl.zeroBuyShareApp)
                .getApiService(ModuleApi.class)
                .zeroBuyShareApp(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZeroBuyShareAppBean>(MyZeroBuyActivity.this, true, true) {

                    @Override
                    public void onSuccess(ZeroBuyShareAppBean result) {
                        if (result == null ) {
                            return;
                        }
                        DisplayMetrics mDisplayMetrics = new DisplayMetrics();//屏幕分辨率容器
                        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
                        int width = mDisplayMetrics.widthPixels;
                        if (width <= 720) {
                            LoadImage.AddTagBean.small = true;
                        }
                        //yCodeNum和yBitmap是越大距离底部越远
                        ZeroBuyLoadImage.AddTagBean addTagBean = new ZeroBuyLoadImage.AddTagBean();
                        addTagBean.codeHttp = TextUtils.isEmpty(result.qrCodeLink) ? "" : result.qrCodeLink;
                        addTagBean.codeNum = TextUtils.isEmpty(result.inviteCode) ? "" : result.inviteCode;
                        addTagBean.xCodeNum = 0;
                        addTagBean.yCodeNum = R.dimen.dp_60;
                        addTagBean.xWidth = 180 + 5;//二维码的宽高
                        addTagBean.xBitmap = 0;
                        addTagBean.yBitmap = R.dimen.dp_35;
                        addTagBean.size = 12;
                        List<String> shareImage = new ArrayList<>();
                        shareImage.add( result.shareImage );
                        createBitmap(shareImage, addTagBean);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }
    public static ArrayList<File> uriListUm = new ArrayList<>();
    private void createBitmap(List<String> loadHttp, ZeroBuyLoadImage.AddTagBean addTagBean) {

        ZeroBuyLoadImage.getInstance().startLoadImages(this, true, loadHttp, addTagBean, new ZeroBuyLoadImage.OnLoadImageLisenter() {
            @Override
            public void onSuccess(List<File> path, int failNum, boolean isCancle) {
                if (isCancle) {
                    toast("已取消！");
                    return;
                }
                List<File> pathCopy = new ArrayList<>();

                //非空过滤
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i) != null) {
                        pathCopy.add(path.get(i));
                    }
                }


                List<String> shareFile = new ArrayList<>();
                for (int i = 0; i < pathCopy.size(); i++) {
                    shareFile.add(pathCopy.get(i).getAbsolutePath());
                }

                if (shareFile == null || shareFile.size() <= 0) {
                    toast("图片路径出错！");
                    return;
                }

                File shareFiles=new File( shareFile.get( 0 ) );
                uriListUm.add(shareFiles);
//                getArrayList(shareFile);
            }
        });
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        if (mHomeRvAdapter != null) {
            loadMore=false;
            page++;
            getProductList ();
        } else {
            show=false;
            loadMore=true;
            page = 1;
            getAllData();
            mSmartFenSi.finishLoadMore(100);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        show=false;
        loadMore=true;
        page = 1;
        getAllData ();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartFenSi.finishRefresh ( state );
        } else if (noMoreData) {
            mSmartFenSi.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartFenSi.finishLoadMore ( state );
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id ==R.id.iv_common_back){
            finish ();
        }else if (id == R.id.tv_common_right){
            Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_Mine_ZERO_BUY).navigation();
            FragmentActivity.start(MyZeroBuyActivity.this, mineTryFragment.getClass());
        }
    }
}
