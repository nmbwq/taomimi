package java.com.lechuang.module.zerobuy;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
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
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.ShareAppBean;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.FileUtils;
import com.common.app.utils.LoadImage;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.Utils;
import com.common.app.utils.ZeroBuyLoadImage;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wikikii.bannerlib.banner.util.L;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouwei.mzbanner.BannerBgContainer;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.JoinSuccessBean;
import java.com.lechuang.module.bean.MyRankingBean;
import java.com.lechuang.module.bean.ShowInMyCardBean;
import java.com.lechuang.module.bean.TryDetailsBean;
import java.com.lechuang.module.bean.ZeroBuyDetailsBean;
import java.com.lechuang.module.bean.ZeroBuyJoinSuccessBean;
import java.com.lechuang.module.bean.ZeroBuyShareAppBean;
import java.com.lechuang.module.mytry.adapter.TryDetailsAdapter;
import java.com.lechuang.module.mytry.bean.TryDetailsEntity;
import java.com.lechuang.module.product.adapter.BannerViewHolder;
import java.com.lechuang.module.shareapp.ShareAppActivity;
import java.com.lechuang.module.zerobuy.adapter.ZeroBuyDetailsAdapter;
import java.com.lechuang.module.zerobuy.bean.ZeroBuyDetailsEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_ZERO_BUY_DETAILS)
public class ZeroBuyDetailsActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {
    private PopupWindow mPopupWindow;
    private TextView mTvButton,mTvPopContent,mTvPopFinish,mTvPopNext,mTvPopMinus,mTvPopNum,mTvPopPlus,mTvNowTishi;
    private SmartRefreshLayout mSmartRefreshLayout;
    public BannerBgContainer mBannerBgContainer;
//    private NumSeekBar numSeekBar;
    private RecyclerView mRvFenSi;
    private LinearLayout mLlFriend,mTvPopJingGao,mLlFenxiang,mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mImageView,mIvPopFinish;
    private RelativeLayout mRlListNum;
    private int num=0;
    @Autowired
    public int id;//参与活动商品id
    @Autowired
    public int obtype;//1.参与试用 2参与中查看详情 3待开奖 4未中奖 5已中奖
    @Autowired
    public String winNum;//已开奖状态 需要
    public static boolean refresh=false;
    private Paint paint;
    private int chaNumber=0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_zero_buy_details;
    }

    @Override
    protected void findViews() {
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.iv_common_right).setOnClickListener(this);
        mImageView = $(R.id.iv_tishi);

        mSmartRefreshLayout = $(R.id.mSmartRefreshLayout);
        mLlFriend = $(R.id.ll_friend);
        mLlFenxiang = $(R.id.ll_fenxiang);


        mRvFenSi = $(R.id.rv_mytry);
        mTvButton = $(R.id.tv_btn);
        mImageView.setOnClickListener(this);
        mLlFriend.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        if (obtype==1){
            ((TextView) $(R.id.iv_common_title)).setText("0元抢购");
        }else {
            ((TextView) $(R.id.iv_common_title)).setText("我的抢购");
        }
        mSmartRefreshLayout.setOnRefreshLoadMoreListener ( this );
        mSmartRefreshLayout.setEnableLoadMore ( false );
    }

    @Override
    protected void getData() {
        updataAlipay();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_bind_zfb_save){
            updataAlipay();
        }else if (id== R.id.iv_common_back){
            finish();
        }else if (id==R.id.iv_common_right){
            AndPermission.with(ZeroBuyDetailsActivity.this)
                    .permission( Permission.Group.STORAGE)
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
                            if (AndPermission.hasAlwaysDeniedPermission(ZeroBuyDetailsActivity.this, permissions)) {
                                //这个里面提示的是一直不过的权限
                            }
                        }
                    })
                    .start();
        }else if (id==R.id.iv_tishi){
            mImageView.setVisibility( View.GONE );
        }else if (id==R.id.iv_finish||id==R.id.tv_finish){
            mPopupWindow.dismiss();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }else if (id==R.id.ll_sharepengyou){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
//            addShare(image,SHARE_MEDIA.WEIXIN_CIRCLE);
        }else if (id==R.id.ll_sharehaoyou){
            ShareUtils.umShare(ZeroBuyDetailsActivity.this, SHARE_MEDIA.QQ,MyZeroBuyActivity.uriListUm , new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });

        }else if (id==R.id.ll_sharekongjian){
            ShareUtils.umShare(this, SHARE_MEDIA.QZONE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }else if (id==R.id.ll_friend){
            sharePopWindow();
        }
    }

    private void updataAlipay() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id);
        allParam.put("obtype", obtype);
        if (!TextUtils.isEmpty(winNum)) {
            allParam.put("winNum", winNum);
        }
        NetWork.getInstance()
                .setTag( Qurl.myZeroBuyDetails)
                .getApiService(ModuleApi.class)
                .myZeroBuyDetailsDetails(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZeroBuyDetailsBean> (ZeroBuyDetailsActivity.this,true,false) {

                    @Override
                    public void onSuccess(final ZeroBuyDetailsBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setHomeAdapter(result);
                        if (result.retype==1){
                            mTvButton.setText( "1张绿卡马上抢" );
                            mLlFriend.setVisibility( View.VISIBLE );
                            mLlFenxiang.setVisibility( View.VISIBLE );
                            mImageView.setVisibility( View.VISIBLE );
                            mTvButton.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    getCardsNum(result.retype);
//                                    showPopupWindow();
                                }
                            } );
                        }else if (result.retype==2){
                            mTvButton.setText( "兑换更多抽奖码" );
                            mLlFriend.setVisibility( View.GONE );
                            mImageView.setVisibility( View.GONE );
                            mLlFenxiang.setVisibility( View.VISIBLE );
                            mTvButton.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    getCardsNum(result.retype);
//                                    showPopupWindow();
                                }
                            } );
                        }else if (result.retype==3){//预计开奖
                            mLlFriend.setVisibility( View.VISIBLE );
                            mImageView.setVisibility( View.VISIBLE );
                            mLlFenxiang.setVisibility( View.VISIBLE );
                            mTvButton.setText( "预计开奖时间 "+result.product.preWinTime );
                            mTvButton.setBackground( getResources ().getDrawable ( R.drawable.bg_btn_item_zero_buy_three_content ) );
//                            result.product.preWinTime
                        }else if (result.retype==4){//未中奖
//                            mTvButton.setText( "未中奖" );
                            mLlFenxiang.setVisibility( View.GONE );
                            mImageView.setVisibility( View.GONE );
                            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)mRvFenSi.getLayoutParams();
                            params.bottomMargin = 0;
                            mRvFenSi.setLayoutParams( params );

                        }else if (result.retype==5){//中奖
//                            mTvButton.setText( "中奖" );
                            mLlFriend.setVisibility( View.GONE );
                            mImageView.setVisibility( View.GONE );
                            mLlFenxiang.setVisibility( View.VISIBLE );
                            mTvButton.setText( result.product.weChatNum+"  复制微信号" );
                            mTvButton.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    ClipData clipData = ClipData.newPlainText("app_inviteCode", result.product.weChatNum);
                                    ((ClipboardManager) ZeroBuyDetailsActivity.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
                                    getWechatApi();
                                }
                            } );
                        }else if (result.retype==6){//必得
                            mLlFriend.setVisibility( View.GONE );
                            mImageView.setVisibility( View.GONE );
                            mLlFenxiang.setVisibility( View.VISIBLE );
                            mTvButton.setText( result.product.weChatNum+"  复制微信号" );
                            mTvButton.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    ClipData clipData = ClipData.newPlainText("app_inviteCode", result.product.weChatNum);
                                    ((ClipboardManager) ZeroBuyDetailsActivity.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
                                    getWechatApi();
                                }
                            } );
                        }

                        //设置banner
//                        setBannerData(result.product.showImgList);
                        //价格
//                        mTvPrice.setText( ""+result.product.price );
                        //原价
//                        mTvYuanPrice.setText( result.ProductBean.price );
                        //标题
//                        mTvTitle.setText( result.product.name );
                        //进度条
//                        int num=result.product.realNum*100/result.product.needNum;
//                        numSeekBar.setProgress( num );
                        //
                        //
//                        mTvContent.setText( result.regular );

                    }
                });
    }
    /**
     * 跳转到微信
     */
    private void getWechatApi(){
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            toast( "检查到您手机没有安装微信，请安装后使用该功能" );
        }
    }
    //我的排行
    private void getMyRanking(){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id);
//        if (!TextUtils.isEmpty(winNum)) {
//            allParam.put("winNum", winNum);
//        }

        NetWork.getInstance()
                .setTag( Qurl.zeroBuyMyRanking)
                .getApiService(ModuleApi.class)
                .zeroBuyHaveCardsNums(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyRankingBean> (ZeroBuyDetailsActivity.this,true,true) {

                    @Override
                    public void onSuccess(final MyRankingBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        showZeroBuyPeoplePopuwind(result.list);
//                        setHomeAdapter(result);


                    }
                });
    }
    /**
     * 设置adapter数据
     *
     * @param result
     */
    private List<ZeroBuyDetailsEntity> mHomeAllEntities;
    private ZeroBuyDetailsAdapter mHomeRvAdapter;
    private void setHomeAdapter(ZeroBuyDetailsBean result) {
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        mHomeAllEntities.clear();

        if (result.product.showImgList != null && result.product.showImgList.size() > 0){
            ZeroBuyDetailsEntity homeAllEntity = new ZeroBuyDetailsEntity(ZeroBuyDetailsEntity.TYPE_HEADER);
            //更新使用者数据
            homeAllEntity.mBanner = result.product;
            homeAllEntity.retype = result.retype;
            homeAllEntity.shortRegular = result.shortRegular;
            homeAllEntity.winNum = result.product.winNum;
            homeAllEntity.sum = result.product.sum;
            homeAllEntity.keyNumStr = result.product.keyNumStr;
            homeAllEntity.mustWinUser = result.mustWinUser;
            homeAllEntity.luckUser = result.luckUser;
            mHomeAllEntities.add(homeAllEntity);
        }
        if (result.retype==4||result.retype==5){

        }else {
//            for (int i =0;i<result.product.detailImgList.size();i++){
//                ZeroBuyDetailsEntity homeAllEntity = new ZeroBuyDetailsEntity(ZeroBuyDetailsEntity.TYPE_PRODUCT);
//                homeAllEntity.images = result.product.detailImgList.get( i );
//                mHomeAllEntities.add(homeAllEntity);
//            }
        }
        if (mHomeRvAdapter ==null){
            mHomeRvAdapter = new ZeroBuyDetailsAdapter<ZeroBuyDetailsEntity, BaseViewHolder>(mHomeAllEntities){
                @Override
                protected void addItemTypeView() {
                    addItemType(TryDetailsEntity.TYPE_HEADER, R.layout.activity_zero_buy_details_title);
                    addItemType(TryDetailsEntity.TYPE_PRODUCT, R.layout.item_trydetails_list);
                }
                @Override
                protected void convert(BaseViewHolder helper, final ZeroBuyDetailsEntity item) {

                    if (helper.getItemViewType() == ZeroBuyDetailsEntity.TYPE_HEADER) {
                        setBannerData(helper,item);
                        mRlListNum=helper.getView( R.id.tv_zerobuy_num_list );
                        mRlListNum.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                getMyRanking();
                            }
                        } );

                    } else if (helper.getItemViewType() == ZeroBuyDetailsEntity.TYPE_PRODUCT) {
                        try {
                            ImageView squareImageView = helper.getView( R.id.iv_item_all_product_tupian );
                            Glide.with(BaseApplication.getApplication()).load(item.images).placeholder(R.drawable.bg_trydetails_null).into(squareImageView);

                        } catch (Exception e) {
                            Logger.e("---->", e.toString());
                        }
                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( ZeroBuyDetailsActivity.this, 1 );
            mRvFenSi.setLayoutManager ( gridLayoutManager );


            mRvFenSi.setAdapter ( mHomeRvAdapter );

        } else {
            mHomeRvAdapter.notifyDataSetChanged();
        }


    }


    //设置Banner
    private List<String> mBannderData = new ArrayList<>();
    private void setBannerData(BaseViewHolder helper, final ZeroBuyDetailsEntity list){
        MZBannerView bannerView = helper.getView(R.id.banner_all);
        if (list.mBanner.showImgList == null || list.mBanner.showImgList.size() <= 0) {
            return;
        }
        //价格
//        mTvPrice.setText( ""+list.price );


        BannerBgContainer bannerBgContainer = mBannerBgContainer;
        if (mBannderData == null) {
            mBannderData = new ArrayList<>();
        }
        mBannderData.clear();
        for (int i = 0; i < list.mBanner.showImgList.size(); i++) {
            mBannderData.add(list.mBanner.showImgList.get(i));
        }
        bannerView.setBannerBgContainer(bannerBgContainer);
        bannerView.setPages(mBannderData, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        bannerView.setIndicatorVisible( false );
        bannerView.setIndicatorNumVisible( true );
        bannerView.start();
//        mTvContent = $(R.id.tv_content);
//        mTvPrice = $(R.id.tv_price);
//        mTvYuanPrice = $(R.id.tv_yuan_price);
//        mTvTitle = $(R.id.tv_biaoti);

        if (!TextUtils.isEmpty( list.mBanner.needNum+"" )){
            TextView mTvNeedPeople=helper.getView( R.id.tv_try_details_needpeople );
            mTvNeedPeople.setText( "需"+list.mBanner.needNum+"人次" );
        }
        if (!TextUtils.isEmpty( list.mBanner.realNum+"" )){
            TextView mTvAllPeople=helper.getView( R.id.tv_try_details_allpeople );
            mTvAllPeople.setText( "共"+list.mBanner.realNum+"人次参与" );
        }
        chaNumber=list.mBanner.needNum-list.mBanner.realNum;
        TextView mTvYuanPrice=helper.getView( R.id.tv_yuan_price );

        TextView mTvTitle=helper.getView( R.id.tv_biaoti );
        NumSeekBar numSeekBar = helper.getView(R.id.numSeekBar);
        //原价
        mTvYuanPrice.setText( ""+list.mBanner.price );
        mTvYuanPrice.getPaint().setFlags( Paint.STRIKE_THRU_TEXT_FLAG);
        //标题
        mTvTitle.setText( list.mBanner.name );
        //进度条
        int num=list.mBanner.realNum*100/list.mBanner.needNum;
        numSeekBar.setSeekBarStyle(R.drawable.pro_seekbar_10);
        numSeekBar.setProgress( num );


        LinearLayout guize=helper.getView( R.id.ll_try_guize );//规则
        TextView textView=helper.getView( R.id.tv_try_short_regular );//短提示
        LinearLayout winningBuju=helper.getView( R.id.ll_winning_buju );//底部
        TextView tryTime=helper.getView( R.id.tv_yry_time );//时间
        //抽奖布局 ll_lottery_yards 抽奖码数量 tv_zerobuy_num 抽奖排行 tv_zerobuy_num_list
        //人气头像 iv_zero_buy_details_photo_renqi 中奖头像 iv_zero_buy_details_photo_win
        RelativeLayout choujianglan=helper.getView( R.id.rl_choujianglan );//抽奖栏目点击事件
        LinearLayout lotteryYards=helper.getView( R.id.ll_lottery_yards );//抽奖布局
        TextView mun=helper.getView( R.id.tv_zerobuy_num );//抽奖码数量
        ImageView renqiPhoto =helper.getView(R.id.iv_zero_buy_details_photo_renqi);//人气头像
        ImageView luckPhoto =helper.getView(R.id.iv_zero_buy_details_photo_win);//中奖头像
        TextView renqiName =helper.getView(R.id.tv_renqi_name);//人气名称
        TextView luckName =helper.getView(R.id.tv_winner_name);//中奖名称
        TextView renqiPhone =helper.getView(R.id.tv_renqi_phone);//人气电话
        TextView luckPhone =helper.getView(R.id.tv_winner_phone);//中奖电话
        RelativeLayout winningNum=helper.getView( R.id.rl_winning_num );//中奖码
        TextView tishiOne=helper.getView( R.id.tv_try_tishione );//提示1
        TextView tishiTwo=helper.getView( R.id.tv_try_tishitwo );//提示2
        TextView zhongJiang=helper.getView( R.id.tv_try_zhongjiang );//中奖
        TextView showWinningNum=helper.getView( R.id.tv_winning_num );//显示的中奖码

        TextView textViewGuize=helper.getView( R.id.tv_guize );
        textViewGuize.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_ZERO_BUY_RULE).navigation();
            }
        } );

        if (list.retype==1||list.retype==2||list.retype==3){//参与试用||兑换更多试用码
            guize.setVisibility( View.VISIBLE );
            textView.setText( list.shortRegular );
            winningBuju.setVisibility( View.GONE );
            zhongJiang.setVisibility( View.GONE );
            if (list.retype==1){//参与试用
                lotteryYards.setVisibility( View.GONE );
            }else {
                lotteryYards.setVisibility( View.VISIBLE );

            }

//        }else if (list.retype==3){//预计开奖

        }else if (list.retype==4){ //未中奖
            guize.setVisibility( View.GONE );
            tryTime.setVisibility( View.VISIBLE );
            zhongJiang.setVisibility( View.VISIBLE );
            lotteryYards.setVisibility( View.VISIBLE );
            zhongJiang.setText( "未中奖, 试试其他商品吧");
            tryTime.setText( "开始时间 "+list.mBanner.startTime+"\n结束时间 "+ list.mBanner.realEndTime);
            winningNum.setVisibility( View.VISIBLE );//中奖码
            showWinningNum.setText( list.winNum );
            tishiOne.setVisibility( View.GONE );
            tishiTwo.setVisibility( View.GONE );
            //给人气用户添加图片
            if (list.mustWinUser.photo==null){
                Glide.with(BaseApplication.getApplication())
                        .load(R.drawable.ic_common_user_def)
                        .placeholder(R.drawable.ic_common_user_def)
                        .transform(new GlideRoundTransform(BaseApplication.getApplication(), 0))
                        .into(renqiPhoto);
            }else {
                Glide.with(BaseApplication.getApplication())
                        .load(list.mustWinUser.photo)
                        .placeholder(R.drawable.ic_common_user_def)
                        .transform(new GlideRoundTransform(BaseApplication.getApplication(), 0))
                        .into(renqiPhoto);
            }
            //给人气添加名字
            if (list.mustWinUser.nickName.length()>4){
                renqiName.setText(list.mustWinUser.nickName.charAt( 0 )+"****"+list.mustWinUser.nickName.charAt(list.mustWinUser.nickName.length()-1)  );
            }else {
                renqiName.setText( list.mustWinUser.nickName );
            }
            //给人气添加手机号
            renqiPhone.setText(list.mustWinUser.phone.substring(0, 3) + "****" + list.mustWinUser.phone.substring(7, 11));
            //给人气添加抽奖码
            helper.setText( R.id.tv_renqi_num ,"抽奖码"+list.mustWinUser.sum+"个");

            //给中奖用户添加图片
            if (list.luckUser.photo==null){
                Glide.with(BaseApplication.getApplication())
                        .load(R.drawable.ic_common_user_def)
                        .placeholder(R.drawable.ic_common_user_def)
                        .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                        .into(luckPhoto);
            }else {
                Glide.with(BaseApplication.getApplication())
                        .load(list.luckUser.photo)
                        .placeholder(R.drawable.ic_common_user_def)
                        .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                        .into(luckPhoto);
            }
            //给中奖添加名字
            if (list.luckUser.nickName.length()>4){
                luckName.setText(list.luckUser.nickName.charAt( 0 )+"****"+list.luckUser.nickName.charAt(list.luckUser.nickName.length()-1)  );
            }else {
                luckName.setText( list.luckUser.nickName );
            }
            //给中奖添加手机号
            luckPhone.setText(list.luckUser.phone.substring(0, 3) + "****" + list.luckUser.phone.substring(7, 11));
            //给中奖添加抽奖码
            helper.setText( R.id.tv_winner_num ,"抽奖码"+list.luckUser.keyNum);
            helper.setText( R.id.tv_winning_num,"中奖码 "+list.mBanner.winNum );
        }else if (list.retype==5){//中奖
            guize.setVisibility( View.GONE );
            lotteryYards.setVisibility( View.VISIBLE );
            tryTime.setVisibility( View.VISIBLE );
            tryTime.setText( "开始时间 "+list.mBanner.startTime+"\n结束时间 "+ list.mBanner.realEndTime);
            zhongJiang.setVisibility( View.VISIBLE );
            zhongJiang.setText( "恭喜您, 已中奖, 中奖码为"+ list.winNum);
            winningNum.setVisibility( View.GONE );//中奖码
            tishiOne.setVisibility( View.VISIBLE );
            tishiTwo.setVisibility( View.VISIBLE );
        }else if (list.retype==6){//必得
            guize.setVisibility( View.GONE );
            lotteryYards.setVisibility( View.VISIBLE );
            tryTime.setVisibility( View.VISIBLE );
            tryTime.setText( "开始时间 "+list.mBanner.startTime+"\n结束时间 "+ list.mBanner.realEndTime);
            zhongJiang.setVisibility( View.VISIBLE );
            zhongJiang.setText( "恭喜您, 抢购成功, 人气王必得商品");
            winningNum.setVisibility( View.GONE );//中奖码
            tishiOne.setVisibility( View.VISIBLE );
            tishiTwo.setVisibility( View.VISIBLE );
        }
        mun.setText( "  "+list.sum+"个  ");
        choujianglan.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (!TextUtils.isEmpty(list.keyNumStr) && list.keyNumStr.contains(",")) {

                    String[] split = list.keyNumStr.split(",");
                    ArrayList<String> stringList = new ArrayList<>();
                    for (int i = 0; i < split.length; i++) {
                        stringList.add(split[i]);
                    }
                    showTryCodePopuwind(stringList);
                }else if (!TextUtils.isEmpty(list.keyNumStr)){
                    ArrayList<String> stringList = new ArrayList<>();
                    stringList.add(list.keyNumStr);
                    showTryCodePopuwind(stringList);
                }
            }
        } );

    }

    private void getCardsNum(final int retype){
        NetWork.getInstance()
                .setTag(Qurl.zeroBuyHaveCardsNums)
                .getApiService(ModuleApi.class)
                .zeroBuyHaveCardsNums()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShowInMyCardBean>(ZeroBuyDetailsActivity.this, true, true) {
                    @Override
                    public void onSuccess(ShowInMyCardBean result) {
                        if (result == null || TextUtils.isEmpty(result.number)) {
//                            if (retype==1){
                                showPopupWindow(0);
//                            }
                            return;
                        }
                        if (retype==1){
                            showPopupWindow( Integer.parseInt(result.number) );
                        }else if (retype==2){
                            if (Integer.parseInt( result.number )==0){
                                showPopupWindow( Integer.parseInt(result.number) );
                            }else {
                                showPopupWindowOther( Integer.parseInt(result.number) );
                            }
//                            showPopupWindowOther( Integer.parseInt(result.number) );
                        }
//                        toast( result.number );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
//                        if (retype==1){
                            showPopupWindow(0);
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        if (retype==1){
                            showPopupWindow(0);
//                        }
                    }
                });
    }
    private void getProduct(final int type, String num, final String id) {

        ApiCancleManager.getInstance().removeAll();
        final Map<String, Object> allParam = new HashMap<>();

        allParam.put("num", num);
        allParam.put("page", 1 + "");
        allParam.put("type", type);//参与活动
        if (!TextUtils.isEmpty(id)) {
            allParam.put("id", id);
        }

        NetWork.getInstance()
                .setTag(Qurl.zeroBuyJoinSuccess)
                .getApiService(ModuleApi.class)
                .zeroBuyJoinSuccess(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZeroBuyJoinSuccessBean>(ZeroBuyDetailsActivity.this, false, true) {
                    @Override
                    public void onSuccess(ZeroBuyJoinSuccessBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        if (!TextUtils.isEmpty(id)) {//id != null 是点击参与试用，需要跳转页面
                            mPopupWindow.dismiss();
                            updataAlipay();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("joinSuccess", result);
                            ARouter.getInstance().build(ARouters.PATH_PARTICIPATE_SUCCESS_A).with(bundle).withInt("type",type  ).withString( "id",id ).navigation();
//                            ARouter.getInstance().build(ARouters.PATH_JOIN_SUCCESS_A).navigation();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
//                        if (errorCode==11003){
//                            toast( "活动已截止" );
//                        }
//                        toast( moreInfo );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                    }
                });

    }

    private void showPopupWindow( int content) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_zero_buy_details, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mTvPopContent,mTvPopFinish,mTvPopNext
        mTvPopContent = (TextView) contentView.findViewById ( R.id.tv_content );
        mTvPopFinish = (TextView) contentView.findViewById ( R.id.tv_finish );
        mTvPopNext = (TextView) contentView.findViewById ( R.id.tv_next );
//        content=1;
        if (content==0 ){
            mTvPopFinish.setText( "放弃" );
            mTvPopNext.setText( "去邀请" );
            mTvPopContent.setText ( "您当前的绿卡为"+content+"张\n快去邀请好友获得绿卡吧!" );
            mTvPopNext.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    mPopupWindow.dismiss();
                    sharePopWindow();
                    /*AndPermission.with(ZeroBuyDetailsActivity.this)
                            .permission( Permission.Group.STORAGE)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(ZeroBuyDetailsActivity.this, permissions)) {
                                        //这个里面提示的是一直不过的权限
                                    }
                                }
                            })
                            .start();*/
                }
            } );
            mTvPopFinish.setOnClickListener( this );
        }else {
            mTvPopFinish.setText( "取消" );
            mTvPopNext.setText( "确定" );
            mTvPopContent.setText ( "您的绿卡当前为"+content+"张\n确定参与该商品0元购活动吗？" );
            mTvPopNext.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    getProduct(0,"1",""+id);
                }
            } );
        }
        mTvPopFinish.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    private void showPopupWindowOther(final int content) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_zero_buy_details_other, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mTvPopContent,mTvPopFinish,mTvPopNext
        mTvPopContent = (TextView) contentView.findViewById ( R.id.tv_content );
        mTvPopFinish = (TextView) contentView.findViewById ( R.id.tv_finish );
        mTvPopNext = (TextView) contentView.findViewById ( R.id.tv_next );
        mTvNowTishi = (TextView) contentView.findViewById ( R.id.tv_nowtishi );
        //mTvPopMinus,mTvPopNum,mTvPopPlus
        mTvPopMinus = (TextView) contentView.findViewById ( R.id.minus );
        mTvPopNum = (TextView) contentView.findViewById ( R.id.num );
        mTvPopPlus = (TextView) contentView.findViewById ( R.id.plus );
        mTvPopJingGao = (LinearLayout) contentView.findViewById ( R.id.ll_jinggao );

        mTvPopMinus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nowNum=Integer.parseInt( mTvPopNum.getText().toString() );
                if (nowNum-1<1){
                    //出提示
                    mTvPopJingGao.setVisibility( View.GONE );
                }else {
                    int nextNum=nowNum - 1;
                    mTvPopNum.setText( ""+nextNum );
                    mTvPopJingGao.setVisibility( View.GONE );
                }
            }
        } );
        mTvPopPlus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nowNum=Integer.parseInt( mTvPopNum.getText().toString() );
                if (chaNumber!=0&&nowNum+1>chaNumber){
                    //出提示
                    mTvNowTishi.setText( "已达到参与人次上限" );
                    mTvPopJingGao.setVisibility( View.VISIBLE );
                    return;
                }
                if (nowNum+1>content){
                    //出提示
                    mTvNowTishi.setText( "绿卡数量不足" );
                    mTvPopJingGao.setVisibility( View.VISIBLE );
//                    if (nowNum<100){
//                        int nextNum=nowNum + 1;
//                        mTvPopNum.setText( ""+nextNum );
//                    }
                }else {
                    int nextNum=nowNum + 1;
                    mTvPopNum.setText( ""+nextNum );
                }
            }
        } );
        /*mTvPopPlus.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        } );*/
        mTvPopFinish.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
        mTvPopNext.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                getProduct(1,mTvPopNum.getText().toString(),""+id);
            }
        } );
        mTvPopContent.setText ( "您当前有"+content+"张绿卡,请选择兑换抽奖码" );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    private ArrayList<MyRankingBean.ListBean> mListString = new ArrayList<>();
    /**
     * 弹出0元购排名弹框
     *
     * @param showImgList
     */
    private void showZeroBuyPeoplePopuwind(List<MyRankingBean.ListBean> showImgList) {
        if (showImgList == null || showImgList.size() < 1) {
            return;
        }
        if (mListString == null) {
            mListString = new ArrayList<>();
        }
        mListString.clear();
        mListString.addAll(showImgList);
//        mNumTryCode = mListString.size() + "";

        View contentView = LayoutInflater.from(ZeroBuyDetailsActivity.this).inflate(R.layout.layout_popu_zero_buy_details_code, null);

//        TextView tvMineTryCode = contentView.findViewById(R.id.tv_mine_try_code);
//        SpannableString ss1 = new SpannableString("我的试用码(" + mNumTryCode + "个)");
//        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_CFCFCF)), 5, mNumTryCode.length() + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvMineTryCode.setText(ss1);
        RecyclerView rvMineTryCode = contentView.findViewById(R.id.rv_mine_try_code);
        rvMineTryCode.setLayoutManager(new GridLayoutManager(ZeroBuyDetailsActivity.this, 1));
        rvMineTryCode.setItemViewCacheSize( 11 );
        rvMineTryCode.setAdapter(new BaseQuickAdapter<MyRankingBean.ListBean, BaseViewHolder>(R.layout.layout_item_mine_zero_buy_code, mListString) {
            @Override
            protected void convert(BaseViewHolder helper, MyRankingBean.ListBean item) {
                //前三名图片 iv_ranking 后几名排名 tv_ranking 最后我的 tv_ranking_me 头像 iv_photo 名字 tv_name 必得 tv_shall_be 数量 tv_number
                //排名
                ImageView imgRankingNum=helper.getView( R.id.iv_ranking );
                TextView tvRankingNum=helper.getView( R.id.tv_ranking );
                TextView tvRankingNumMe=helper.getView( R.id.tv_ranking_me );
                TextView tvShallBe=helper.getView( R.id.tv_shall_be );
                TextView tvMyName=helper.getView( R.id.tv_my_name );//我标记
                if (item.ranking==1){
                    imgRankingNum.setBackground( getResources ().getDrawable ( R.drawable.ic_zero_buy_details_one ) );
                    imgRankingNum.setVisibility( View.VISIBLE );
                    tvRankingNum.setVisibility( View.GONE );
                    tvShallBe.setVisibility( View.VISIBLE );
                }else if (item.ranking==2){
                    imgRankingNum.setBackground( getResources ().getDrawable ( R.drawable.ic_zero_buy_details_two ) );
                    imgRankingNum.setVisibility( View.VISIBLE );
                    tvRankingNum.setVisibility( View.GONE );
                }else if (item.ranking==3){
                    imgRankingNum.setBackground( getResources ().getDrawable ( R.drawable.ic_zero_buy_details_three ) );
                    imgRankingNum.setVisibility( View.VISIBLE );
                    tvRankingNum.setVisibility( View.GONE );
                }else if (item.ranking>10){
                    tvRankingNumMe.setVisibility( View.VISIBLE );
                    tvRankingNum.setVisibility( View.GONE );
                    if (item.ranking>100){
                        tvRankingNumMe.setText( "100+" );
                    }else {
                        tvRankingNumMe.setText( item.ranking+"" );
                    }

                }else if (item.ranking==10){
                    tvRankingNum.setText( "10" );
                }else {
                    tvRankingNum.setText( "0"+item.ranking );
                }

                //头像
                //用户头像
                ImageView ivPhono=helper.getView( R.id.iv_photo );
                if (item.photo==null){
                    Glide.with(BaseApplication.getApplication())
                            .load( R.drawable.ic_common_user_def)
                            .placeholder(R.drawable.ic_common_user_def)
                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                            .into(ivPhono);
                }else {
                    Glide.with(BaseApplication.getApplication())
                            .load( item.photo)
                            .placeholder(R.drawable.ic_common_user_def)
                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                            .into(ivPhono);
                }

                //名字
                String name;
                if (item.nickName==null){
                    name="木有昵称";
                }else {
                    if (item.nickName.length()>4){
//                    String name=item.nickName.substring( 0 )+"****"+item.nickName.substring(item.nickName.length()-1,item.nickName.length()  );
                        name=item.nickName.charAt( 0 )+"****"+item.nickName.charAt(item.nickName.length()-1);
                    }else {
                        name=item.nickName;
                    }
                }
                helper.setText( R.id.tv_name,name );
//                LogUtils.w( "tag1","item.nickName="+item.nickName+"   item.phone"+item.phone+"     phone"+ UserHelper.getInstence().getUserInfo().getPhone()
//                        +"  item.id"+item.appUserId+"   id"+ UserHelper.getInstence().getUserInfo().getId());
                if (item.phone.equals( UserHelper.getInstence().getUserInfo().getPhone() )){
                    tvMyName.setVisibility( View.VISIBLE );
                }
//                helper.setText(R.id.iv_item_try_content, item);

                //数量
                helper.setText( R.id.tv_number,item.sum+"个" );
            }
        });
        contentView.findViewById(R.id.iv_mine_try_code_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    private ArrayList<String> listString = new ArrayList<>();
    private String numTryCode = "0";

    /**
     * 弹出试用码弹框
     *
     * @param showImgList
     */
    private void showTryCodePopuwind(ArrayList<String> showImgList) {
        if (showImgList == null || showImgList.size() < 1) {
            return;
        }
        if (listString == null) {
            listString = new ArrayList<>();
        }
        listString.clear();
        listString.addAll(showImgList);
        numTryCode = listString.size() + "";

        View contentView = LayoutInflater.from(ZeroBuyDetailsActivity.this).inflate(R.layout.layout_popu_mine_try_code, null);

        TextView tvMineTryCode = contentView.findViewById(R.id.tv_mine_try_code);
        SpannableString ss1 = new SpannableString("我的抽奖码(" + numTryCode + "个)");
        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_CFCFCF)), 5, numTryCode.length() + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMineTryCode.setText(ss1);
        RecyclerView rvMineTryCode = contentView.findViewById(R.id.rv_mine_try_code);
        rvMineTryCode.setLayoutManager(new GridLayoutManager(ZeroBuyDetailsActivity.this, 3));
        rvMineTryCode.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_item_mine_try_code, listString) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.iv_item_try_content, item);
            }
        });
        contentView.findViewById(R.id.iv_mine_try_code_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    //分享弹框
    private void sharePopWindow(){
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_share_goods, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian
        mLlPopweixin = (LinearLayout) contentView.findViewById ( R.id.ll_shareweixin );
        mLlPoppengyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharepengyou );
        mLlPophaoyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharehaoyou );
        mLlPopkongjian = (LinearLayout) contentView.findViewById ( R.id.ll_sharekongjian );
        mIvPopFinish = (ImageView) contentView.findViewById ( R.id.iv_finish );
        mLlPopweixin.setOnClickListener( this );
        mLlPoppengyou.setOnClickListener( this );
        mLlPophaoyou.setOnClickListener( this );
        mLlPopkongjian.setOnClickListener( this );
        mIvPopFinish.setOnClickListener( this );

        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
    //url转bitmap
    Bitmap bitmap;
    public Bitmap returnBitMap(final String url){

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return bitmap;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        getAllData();
        if (refresh){
            mSmartRefreshLayout.autoRefresh(500);
            refresh=false;
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        num=0;
        updataAlipay();
    }
    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        mSmartRefreshLayout.finishRefresh ( state );

    }
}
