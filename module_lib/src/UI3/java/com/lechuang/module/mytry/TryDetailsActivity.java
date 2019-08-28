package java.com.lechuang.module.mytry;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.Utils;
import com.common.app.view.NumSeekBar;
import com.common.app.view.SquareImageView;
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
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.JoinSuccessBean;
import java.com.lechuang.module.bean.MyTryBean;
import java.com.lechuang.module.bean.ShowInMyCardBean;
import java.com.lechuang.module.bean.TryDetailsBean;
import java.com.lechuang.module.bean.TryRuleBean;
import java.com.lechuang.module.mytry.adapter.MyTryRvAdapter;
import java.com.lechuang.module.mytry.adapter.TryDetailsAdapter;
import java.com.lechuang.module.mytry.adapter.TryDetailsBannerViewHolder;
import java.com.lechuang.module.mytry.bean.MyTryAllEntity;
import java.com.lechuang.module.mytry.bean.TryDetailsEntity;
import java.com.lechuang.module.product.adapter.BannerViewHolder;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_TRY_DETAILS)
public class TryDetailsActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {
    private PopupWindow mPopupWindow;
    private TextView mTvButton,mTvPopContent,mTvPopFinish,mTvPopNext,mTvPopMinus,mTvPopNum,mTvPopPlus,mTvNowTishi;
    private SmartRefreshLayout mSmartRefreshLayout;
    public BannerBgContainer mBannerBgContainer;
//    private NumSeekBar numSeekBar;
    private RecyclerView mRvFenSi;
    private LinearLayout mLlFriend,mTvPopJingGao,mLlFenxiang;
    private ImageView mImageView;
    private int num=0;
    @Autowired
    public int id;//参与活动商品id
    @Autowired
    public int obtype;//1.参与试用 2参与中查看详情 3待开奖 4未中奖 5已中奖
    @Autowired
    public String winNum;//已开奖状态 需要
    public static boolean refresh=false;
    private LinearLayout mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mIvPopFinish;
    private int chaNumber=0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_try_details;
    }

    @Override
    protected void findViews() {
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.iv_common_right).setOnClickListener(this);
        mImageView = $(R.id.iv_tishi);
        mImageView.setOnClickListener(this);
        mSmartRefreshLayout = $(R.id.mSmartRefreshLayout);
        mLlFriend = $(R.id.ll_friend);
        mLlFenxiang = $(R.id.ll_fenxiang);
        mLlFenxiang.setOnClickListener( this );


        mRvFenSi = $(R.id.rv_mytry);
        mTvButton = $(R.id.tv_btn);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        if (obtype==1){
            ((TextView) $(R.id.iv_common_title)).setText("试用专区");
        }else {
            ((TextView) $(R.id.iv_common_title)).setText("我的试用");
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
            AndPermission.with(TryDetailsActivity.this)
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
                            if (AndPermission.hasAlwaysDeniedPermission(TryDetailsActivity.this, permissions)) {
                                //这个里面提示的是一直不过的权限
                            }
                        }
                    })
                    .start();
        }else if (id==R.id.iv_tishi){
            mImageView.setVisibility( View.GONE );
        }else if (id==R.id.ll_fenxiang){
            getPopShareApp();
        }else if (id==R.id.iv_finish){
            mPopupWindow.dismiss();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN, MyTryActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, MyTryActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.QQ, MyTryActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.QZONE, MyTryActivity.uriListUm, new UMShareListener() {
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
        }
    }
    private void getPopShareApp(){
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

    private void updataAlipay() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id);
        allParam.put("obtype", obtype);
        if (!TextUtils.isEmpty(winNum)) {
            allParam.put("winNum", winNum);
        }

        NetWork.getInstance()
                .setTag( Qurl.tryDetails)
                .getApiService(ModuleApi.class)
                .tryDetails(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TryDetailsBean> (TryDetailsActivity.this,true,false) {

                    @Override
                    public void onSuccess(final TryDetailsBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setHomeAdapter(result);
                        if (result.retype==1){
                            mTvButton.setText( "参与试用" );
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
                            mTvButton.setText( "兑换更多试用码" );
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
                                    ((ClipboardManager) TryDetailsActivity.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
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
    /**
     * 设置adapter数据
     *
     * @param result
     */
    private List<TryDetailsEntity> mHomeAllEntities;
    private TryDetailsAdapter mHomeRvAdapter;
    private void setHomeAdapter(TryDetailsBean result) {
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        mHomeAllEntities.clear();

        if (result.product.showImgList != null && result.product.showImgList.size() > 0){
            TryDetailsEntity homeAllEntity = new TryDetailsEntity(TryDetailsEntity.TYPE_HEADER);
            //更新使用者数据
            homeAllEntity.mBanner = result.product;
            homeAllEntity.retype = result.retype;
            homeAllEntity.shortRegular = result.shortRegular;
            homeAllEntity.winNum = result.product.winNum;
            mHomeAllEntities.add(homeAllEntity);
        }
//        if (result.retype==4||result.retype==5){
//
//        }else {
            for (int i =0;i<result.product.detailImgList.size();i++){
                TryDetailsEntity homeAllEntity = new TryDetailsEntity(TryDetailsEntity.TYPE_PRODUCT);
                homeAllEntity.images = result.product.detailImgList.get( i );
                mHomeAllEntities.add(homeAllEntity);
            }
//        }
        if (mHomeRvAdapter ==null){
            mHomeRvAdapter = new TryDetailsAdapter<TryDetailsEntity, BaseViewHolder>(mHomeAllEntities){
                @Override
                protected void addItemTypeView() {
                    addItemType(TryDetailsEntity.TYPE_HEADER, R.layout.activity_try_details_title);
                    addItemType(TryDetailsEntity.TYPE_PRODUCT, R.layout.item_trydetails_list);
                }
                @Override
                protected void convert(BaseViewHolder helper, final TryDetailsEntity item) {

                    if (helper.getItemViewType() == TryDetailsEntity.TYPE_HEADER) {
                        setBannerData(helper,item);
//                        setMarqueeView(helper,item.UseList);
//                        ImageView imageView=helper.getView( R.id.iv_macard_bg );
//                        ImageView ivDi=helper.getView( R.id.iv_dibu );
//                        LogUtils.w( "tag1","是"+item.imgUrl );
//                        if (item.imgUrl!=null){
//                            Glide.with( BaseApplication.getApplication()).load(item.imgUrl).placeholder(R.drawable.bg_common_img_null).into(imageView);
//                            ivDi.setVisibility( View.VISIBLE );
//                        }

                    } else if (helper.getItemViewType() == TryDetailsEntity.TYPE_PRODUCT) {
                        try {
                            ImageView squareImageView = helper.getView( R.id.iv_item_all_product_tupian );
                            Glide.with(BaseApplication.getApplication()).load(item.images).into(squareImageView);
                            //商品图片
                            /*ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_all_product_tupian);
                            Glide.with(BaseApplication.getApplication()).load(item.mProductListBean.showImg).placeholder(R.drawable.bg_common_img_null).into(ivItemAllFenLei);
                            LogUtils.w( "tag1","图片是啥"+item.mProductListBean.showImg );*/

                        } catch (Exception e) {
                            Logger.e("---->", e.toString());
                        }
                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( TryDetailsActivity.this, 1 );
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


    //设置Banner
    private List<String> mBannderData = new ArrayList<>();
    private void setBannerData(BaseViewHolder helper, final TryDetailsEntity list){
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
//        bannerView.setPages(mBannderData, new MZHolderCreator<TryDetailsBannerViewHolder>() {
//            @Override
//            public TryDetailsBannerViewHolder createViewHolder() {
//                return new TryDetailsBannerViewHolder();
//            }
//        });
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
        RelativeLayout winningNum=helper.getView( R.id.rl_winning_num );//中奖码
        TextView tishiOne=helper.getView( R.id.tv_try_tishione );//提示1
        TextView tishiTwo=helper.getView( R.id.tv_try_tishitwo );//提示2
        TextView zhongJiang=helper.getView( R.id.tv_try_zhongjiang );//中奖
        TextView showWinningNum=helper.getView( R.id.tv_winning_num );//显示的中奖码

        TextView textViewGuize=helper.getView( R.id.tv_guize );
        textViewGuize.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_TRY_RULE).navigation();
            }
        } );

        if (list.retype==1||list.retype==2||list.retype==3){//参与试用||兑换更多试用码
            guize.setVisibility( View.VISIBLE );
            textView.setText( list.shortRegular );
            winningBuju.setVisibility( View.GONE );
            zhongJiang.setVisibility( View.GONE );

//        }else if (list.retype==3){//预计开奖

        }else if (list.retype==4){//未中奖
            guize.setVisibility( View.GONE );
            tryTime.setVisibility( View.VISIBLE );
            zhongJiang.setVisibility( View.VISIBLE );
            zhongJiang.setText( "未中奖, 试试其他商品吧");
            tryTime.setText( "开始时间 "+list.mBanner.startTime+"\n结束时间 "+ list.mBanner.endTime);
            winningNum.setVisibility( View.VISIBLE );//中奖码
            showWinningNum.setText( list.winNum );
            tishiOne.setVisibility( View.GONE );
            tishiTwo.setVisibility( View.GONE );
        }else if (list.retype==5){//中奖
            guize.setVisibility( View.GONE );
            tryTime.setVisibility( View.VISIBLE );
            tryTime.setText( "开始时间 "+list.mBanner.startTime+"\n结束时间 "+ list.mBanner.endTime);
            zhongJiang.setVisibility( View.VISIBLE );
            zhongJiang.setText( "恭喜您, 已中奖, 中奖码为"+ list.winNum);
            winningNum.setVisibility( View.GONE );//中奖码
            tishiOne.setVisibility( View.VISIBLE );
            tishiTwo.setVisibility( View.VISIBLE );
        }

    }

    private void getCardsNum(final int retype){
        NetWork.getInstance()
                .setTag(Qurl.haveCardsNums)
                .getApiService(ModuleApi.class)
                .haveCardsNums()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShowInMyCardBean>(TryDetailsActivity.this, true, true) {
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
                            /*if (Integer.parseInt( result.number )==0){
                                showPopupWindow( Integer.parseInt(result.number) );
                            }else {
                                showPopupWindowOther( Integer.parseInt(result.number) );
                            }*/
                            showPopupWindowOther( Integer.parseInt(result.number) );
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
    private void getProduct(final int type,String num,final int id) {

        ApiCancleManager.getInstance().removeAll();
        final Map<String, Object> allParam = new HashMap<>();

        allParam.put("num", num);
        allParam.put("page", 1 + "");
        allParam.put("type", "0");//参与活动
        if (!TextUtils.isEmpty(""+id)) {
            allParam.put("id", id);
        }

        NetWork.getInstance()
                .setTag(Qurl.joinSuccess)
                .getApiService(ModuleApi.class)
                .joinSuccess(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<JoinSuccessBean>(TryDetailsActivity.this, false, true) {
                    @Override
                    public void onSuccess(JoinSuccessBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        if (!TextUtils.isEmpty(""+id)) {//id != null 是点击参与试用，需要跳转页面
                            mPopupWindow.dismiss();
                            updataAlipay();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("joinSuccess", result);
                            bundle.putInt( "id", id );
                            ARouter.getInstance().build(ARouters.PATH_JOIN_SUCCESS_A).withInt("type",type  ).with(bundle).navigation();
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
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_try_details, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mTvPopContent,mTvPopFinish,mTvPopNext
        mTvPopContent = (TextView) contentView.findViewById ( R.id.tv_content );
        mTvPopFinish = (TextView) contentView.findViewById ( R.id.tv_finish );
        mTvPopNext = (TextView) contentView.findViewById ( R.id.tv_next );
//        content=1;
        if (content==0 ){
            mTvPopFinish.setText( "放弃" );
            mTvPopNext.setText( "去邀请" );
            mTvPopContent.setText ( "您的黄卡当前为"+content+"张\n快去邀请好友获得黄卡吧!" );
            mTvPopNext.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    mPopupWindow.dismiss();
                    getPopShareApp();
                }
            } );
        }else {
            mTvPopFinish.setText( "取消" );
            mTvPopNext.setText( "确定" );
            mTvPopContent.setText ( "您的黄卡当前为"+content+"张\n确定参与试用该商品吗？" );
            mTvPopNext.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    getProduct(0,"1",id);
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
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_try_details_other, null );
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
                    mTvNowTishi.setText( "黄卡数量不足" );
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
                getProduct(1,mTvPopNum.getText().toString(),id);
            }
        } );
        mTvPopContent.setText ( "您当前有"+content+"张黄卡,请选择兑换试用码" );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
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
