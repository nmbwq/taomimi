package com.lechuang.module.friends;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.FriendsLoadBean;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.CustomProgressDialog;
import com.common.app.utils.LoadImage;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.StringUtils;
import com.common.app.utils.Utils;
import com.common.app.view.CommonDialog;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.adress.SelectCityUtils.utils.ImgUtils;
import java.com.lechuang.module.bean.FriendsBean;
import java.com.lechuang.module.bean.FriendsShareImagesBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

/**
 * @author: zhengjr
 * @since: 2018/9/10
 * @describe:
 */

@Route(path = ARouters.PATH_FRAGMENT_CONTENT)
public class FriendsContentFragment extends LazyBaseFragment implements OnRefreshLoadMoreListener, EasyPermissions.PermissionCallbacks {

    @Autowired(name = Constant.TYPE)
    public String type;
    private SmartRefreshLayout mSmartFriends;
    private LinearLayout mNetError;
    private int page = 1;
    private RecyclerView mRvFriendsContent;
    private ImageView mIvCommonImage;
    private ImageView mIvHomeAllTop;
    private CustomProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_firends_content;
    }

    @Override
    protected void findViews() {
        mSmartFriends = $( R.id.smart_friends_refresh );
        mNetError = $( R.id.ll_net_error );
        mIvCommonImage = $( R.id.iv_common_image );
        mRvFriendsContent = $( R.id.rv_friends_content );
        mIvHomeAllTop = $( R.id.iv_home_all_top );
        mIvHomeAllTop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRvFriendsContent != null) {
                    mRvFriendsContent.scrollToPosition( 0 );
                    mIvHomeAllTop.setVisibility( View.INVISIBLE );
                }
            }
        } );

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject( this );
        mSmartFriends.setOnRefreshLoadMoreListener( this );
        mIvCommonImage.setImageDrawable( getResources().getDrawable( R.drawable.ic_friends_sucai_null ) );
        $( R.id.tv_common_click_try ).setVisibility( View.GONE );

        mRvFriendsContent.addOnScrollListener( new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;

                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition <= 15) {
                        mIvHomeAllTop.setVisibility( View.INVISIBLE );
                    } else {
                        mIvHomeAllTop.setVisibility( View.VISIBLE );
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
            }
        } );
    }

    @Override
    protected void getData() {
        mSmartFriends.autoRefresh( 100 );
    }

    /**
     * 获取数据
     */
    private RxObserver<FriendsBean> mRxObserver;

    private void getContentData() {
        if (mRxObserver == null) {
            mRxObserver = new RxObserver<FriendsBean>( getActivity(), true, false ) {
                @Override
                public void onSuccess(FriendsBean result) {

                    if (result == null || result.list == null || result.list.size() <= 0) {

                        if (mFriendsBeans == null || mFriendsBeans.size() <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility( View.VISIBLE );
                        }
                        setRefreshLoadMoreState( true, true );
                        return;
                    }
                    setRefreshLoadMoreState( true, false );

                    //设置底部商品数据
                    setContentData( result.list );
                }

                @Override
                public void onFailed(int errorCode, String moreInfo) {
                    super.onFailed( errorCode, moreInfo );
                    if (mFriendsBeans == null || mFriendsBeans.size() <= 0 || mBaseProductQuickAdapter == null) {
                        mNetError.setVisibility( View.VISIBLE );
                    }
                    setRefreshLoadMoreState( false, false );

                }

                @Override
                public void onError(Throwable e) {
                    super.onError( e );
                    if (mFriendsBeans == null || mFriendsBeans.size() <= 0 || mBaseProductQuickAdapter == null) {
                        mNetError.setVisibility( View.VISIBLE );
                    }
                    setRefreshLoadMoreState( false, false );

                }
            };
        }
        if (TextUtils.equals( type, "1" )) {
            getLeftData( mRxObserver );
        } else {
            getRightData( mRxObserver );
        }
    }

    /**
     * 设置数据内容
     */
    private List<FriendsBean.ListBean> mFriendsBeans = new ArrayList<>();
    private BaseQuickAdapter<FriendsBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;
    private int mPositionSign = 0;//用户记录朋友圈左面商品显示价格的下标

    private void setContentData(List<FriendsBean.ListBean> friendsBeans) {
        mNetError.setVisibility( View.GONE );

        if (mFriendsBeans == null) {
            mFriendsBeans = new ArrayList<>();
        }

        if (page == 1) {
            mFriendsBeans.clear();
        }

        mFriendsBeans.addAll( friendsBeans );
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<FriendsBean.ListBean, BaseViewHolder>( R.layout.item_friends_vp_content, mFriendsBeans ) {
                @Override
                protected void convert(BaseViewHolder helper, final FriendsBean.ListBean item) {

                    try {

                        //发布者头像
                        ImageView ivUserFriendsPhoto = helper.getView( R.id.iv_friends_user_photo );
                        Glide.with( BaseApplication.getApplication() ).load( item.cfMasterImg )
                                .placeholder( R.drawable.ic_common_user_def )
                                .transform( new GlideRoundTransform( BaseApplication.getApplication(), 100 ) )
                                .into( ivUserFriendsPhoto );
                        //发布者名称
                        helper.setText( R.id.tv_user_name, item.cfMasterName );
                        //发布创建日期
                        helper.setText( R.id.tv_time, new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( new Date( item.cfCreateTime ) ) );
                        //发布的内容描述（分享的文案）
                        helper.setText( R.id.tv_friends_describe, item.cfShareCopy );
                        helper.getView( R.id.tv_friends_describe ).setOnLongClickListener( new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                String cfShareCopy = item.cfShareCopy;
                                if (!TextUtils.isEmpty( cfShareCopy )) {
                                    // 获取系统剪贴板
                                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService( Context.CLIPBOARD_SERVICE );

                                    // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                                    ClipData clipData = ClipData.newPlainText( "app_friends_cfShareCopy", TextUtils.isEmpty( cfShareCopy ) ? "" : cfShareCopy );

                                    // 把数据集设置（复制）到剪贴板
                                    clipboard.setPrimaryClip( clipData );
                                    toast( "复制成功！" );
                                } else {
                                    toast( "复制失败！" );
                                }
                                return false;
                            }
                        } );
                        //评论
                        helper.getView( R.id.rl_friends_pinglu_parent ).setVisibility( TextUtils.isEmpty( item.cfComments ) ? View.GONE : View.VISIBLE );
                        helper.setText( R.id.tv_friends_pinglun, TextUtils.isEmpty( item.cfComments ) ? "" : item.cfComments );

                        //预计赚和升级赚
                        helper.getView( R.id.tv_friends_yujizhuan ).setVisibility( TextUtils.isEmpty( item.ZhuanMoney ) ? View.GONE : View.VISIBLE );
                        helper.setText( R.id.tv_friends_yujizhuan, TextUtils.isEmpty( item.ZhuanMoney ) ? "" : "预计赚  ¥" + StringUtils.stringToStringDeleteZero( item.ZhuanMoney ) );
                        helper.getView( R.id.tv_friends_shengjizhuan ).setVisibility( TextUtils.isEmpty( item.upZhuanMoney ) ? View.GONE : View.VISIBLE );
                        helper.setText( R.id.tv_friends_shengjizhuan, TextUtils.isEmpty( item.upZhuanMoney ) ? "" : "升级赚  ¥" + StringUtils.stringToStringDeleteZero( item.upZhuanMoney ) );

                        //查看
                        TextView tvFriendsChaKan = helper.getView( R.id.tv_friends_chakan );
                        //一键分享
                        TextView tvFriendsOnekeyShare = helper.getView( R.id.tv_friends_onekey_share );
//                        tvFriendsOnekeyShare.setText(item.shareCount + "");
                        //复制
                        LinearLayout tvFriendsCopy = helper.getView( R.id.tv_friends_copy );

                        tvFriendsChaKan.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //查看是跳转到商品详情
                                if (TextUtils.isEmpty( item.cfProductId )) {
                                    Utils.toast( "获取参数出错！" );
                                    return;
                                }
                                //todo
//                                findProductInfo(listBean.cfProductId);//查询商品详情，然后跳转到商品详情页
                                RouterBean routerBean = new RouterBean();
                                routerBean.type = 9;
                                routerBean.tbCouponId = item.tbCouponId;
                                routerBean.mustParam = "type=4"
//                                        + "&id=" + item.id
                                        + "&tbItemId=" + item.cfProductId;
//                                routerBean.t = "2";
//                                routerBean.id = item.id;
//                                routerBean.i = item.cfProductId;

                                LinkRouterUtils.getInstance().setRouterBean( getActivity(), routerBean );

                            }
                        } );

                        tvFriendsOnekeyShare.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (TextUtils.isEmpty( item.cfType )
                                        || TextUtils.isEmpty( item.cfNumber + "" )
                                        || TextUtils.isEmpty( item.id )) {
                                    Utils.toast( "获取参数出错！" );
                                    return;
                                }
                                showShareDialog( item );

                            }
                        } );

                        tvFriendsCopy.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String cfComments = item.cfComments;
                                if (!TextUtils.isEmpty( cfComments )) {
                                    // 获取系统剪贴板
                                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService( Context.CLIPBOARD_SERVICE );

                                    // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                                    ClipData clipData = ClipData.newPlainText( "app_friends_cfComments", TextUtils.isEmpty( cfComments ) ? "" : cfComments );

                                    // 把数据集设置（复制）到剪贴板
                                    clipboard.setPrimaryClip( clipData );
                                    toast( "复制成功！" );
                                } else {
                                    toast( "复制失败！" );
                                }

                            }
                        } );

                       /* 每日必推：
                        1.全是商品
                        1.1 单商品分享，点击图片，全屏查看该图片
                        1.1.1 点击查看按钮，进入商品详情
                        1.2 多商品分享，点击图片，进入商品详情

                        推广物料：
                        1.全不是商品
                        2.点击图片，全屏查看该图片

                        cfType  =   0单个商品 ; 1 多个商品; 2推广物料
                        结论：当cfType ==   0 || 2时，只显示图片，新建adapter1；点击图片放大图片，点击查看进入查看详情（显示查看按钮）
                             当cfType ==   1       ，显示图片和价格，新建adapter2；点击图片查看详情（隐藏查看按钮）
                             单商品显示查看、分享，点击图片全屏放大。图片不带价格
                             多商品显示    、分享，点击图片进入商品详情。图片带价格
                             物料显示      、分享，点击图片全屏放大。图片不带价格
                        */
                        String cfType = item.cfType;

                        if (TextUtils.equals( cfType, "0" )) {//如果是单个商品，显示查看和分享按钮，其他隐藏
                            tvFriendsChaKan.setVisibility( View.GONE );
                        } else {
                            tvFriendsChaKan.setVisibility( View.GONE );
                        }

                        RecyclerView rv_friends_img = helper.getView( R.id.rv_friends_img );
                        rv_friends_img.setLayoutManager( new GridLayoutManager( getContext(), 3 ) );
                        rv_friends_img.setHasFixedSize( true );
                        rv_friends_img.setNestedScrollingEnabled( false );

                        CardView rlFriendsImgParent = helper.getView( R.id.fl_friends_img_parent );

                        //多商品
                        if (item.product != null && TextUtils.equals( cfType, "1" )) {
                            //多张图片
                            if (item.product.size() > 1) {
                                rv_friends_img.setVisibility( View.VISIBLE );
                                rlFriendsImgParent.setVisibility( View.GONE );
                                BaseQuickAdapter<FriendsBean.ListBean.ProductBean, BaseViewHolder> baseQuickAdapter = new BaseQuickAdapter<FriendsBean.ListBean.ProductBean, BaseViewHolder>( R.layout.item_friends_vpcontent_child, item.product ) {

                                    @Override
                                    protected void convert(BaseViewHolder helper, FriendsBean.ListBean.ProductBean item) {

                                        try {
                                            ImageView ivProductImg = helper.getView( R.id.siv_product_img );
                                            Glide.with( BaseApplication.getApplication() ).load( item.imgUrl )
                                                    .placeholder( R.drawable.bg_common_img_null )
                                                    .into( ivProductImg );

                                            helper.getView( R.id.tv_product_price ).setVisibility( View.VISIBLE );
                                            helper.setText( R.id.tv_product_price, "¥" + StringUtils.doubleToStringDeleteZero( item.cfCouponAfterPrice ) );
                                        } catch (Exception e) {

                                        }


                                    }
                                };

                                rv_friends_img.setAdapter( baseQuickAdapter );
                                baseQuickAdapter.setOnItemClickListener( new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                        //点击图片查看详情
                                        RouterBean routerBean = new RouterBean();
                                        routerBean.type = 9;
                                        routerBean.tbCouponId = item.product.get( position ).tbCouponId;
                                        routerBean.mustParam = "type=4"
                                                + "&tbItemId=" + item.product.get( position ).cfProductId;

                                        LinkRouterUtils.getInstance().setRouterBean( getActivity(), routerBean );


                                    }
                                } );
                            } else if (item.product.size() == 1) {//单张图片
                                rv_friends_img.setVisibility( View.GONE );
                                rlFriendsImgParent.setVisibility( View.VISIBLE );
                                try {
                                    final ImageView ivProductImg = helper.getView( R.id.iv_product_img );
                                    Glide.with( BaseApplication.getApplication() ).load( item.product.get( 0 ).imgUrl )
                                            .asBitmap()
                                            .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                                            .placeholder( R.drawable.bg_common_img_null )
                                            .into( new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    ViewGroup.LayoutParams params = ivProductImg.getLayoutParams();
                                                    params.width = resource.getWidth();
                                                    params.height = resource.getHeight();
                                                    ivProductImg.setLayoutParams( params );
                                                    ivProductImg.setImageBitmap( resource );
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                    super.onLoadFailed( e, errorDrawable );
                                                    ViewGroup.LayoutParams params = ivProductImg.getLayoutParams();
                                                    params.width = errorDrawable.getIntrinsicWidth();
                                                    params.height = errorDrawable.getIntrinsicHeight();
                                                    ivProductImg.setLayoutParams( params );
                                                    ivProductImg.setImageDrawable( errorDrawable );
                                                }
                                            } );

                                    helper.getView( R.id.tv_product_price ).setVisibility( View.VISIBLE );
                                    helper.setText( R.id.tv_product_price, "¥" + StringUtils.doubleToStringDeleteZero( item.cfCouponAfterPrice ) );
                                    rlFriendsImgParent.setOnClickListener( new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //点击图片查看详情
                                            RouterBean routerBean = new RouterBean();
                                            routerBean.type = 9;
                                            routerBean.tbCouponId = item.product.get( 1 ).tbCouponId;
                                            routerBean.mustParam = "type=4"
                                                    + "&tbItemId=" + item.product.get( 1 ).cfProductId;

                                            LinkRouterUtils.getInstance().setRouterBean( getActivity(), routerBean );
                                        }
                                    } );
                                } catch (Exception e) {

                                }
                            }


                        } else if (item.detailImages != null && TextUtils.equals( cfType, "0" )) {
                            //多张图片
                            if (item.detailImages.size() > 1) {
                                rv_friends_img.setVisibility( View.VISIBLE );
                                rlFriendsImgParent.setVisibility( View.GONE );
                                mPositionSign = 0;
                                BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>( R.layout.item_friends_vpcontent_child, item.detailImages ) {

                                    @Override
                                    protected void convert(BaseViewHolder helper, String itemOuter) {

                                        try {
                                            ImageView ivProductImg = helper.getView( R.id.siv_product_img );
                                            Glide.with( BaseApplication.getApplication() ).load( itemOuter )
                                                    .placeholder( R.drawable.bg_common_img_null )
                                                    .into( ivProductImg );

                                            if (item.signImg == mPositionSign) {
                                                helper.getView( R.id.tv_product_price ).setVisibility( View.VISIBLE );
                                                helper.setText( R.id.tv_product_price, "¥" + StringUtils.doubleToStringDeleteZero( item.cfCouponAfterPrice ) );
                                            } else {
                                                helper.getView( R.id.tv_product_price ).setVisibility( View.INVISIBLE );
                                            }
                                            mPositionSign++;
                                        } catch (Exception e) {

                                        }

                                    }
                                };

                                rv_friends_img.setAdapter( baseQuickAdapter );
                                baseQuickAdapter.setOnItemClickListener( new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                        //点击图片放大图片，点击查看进入查看详情（显示查看按钮）
                                        if (position == item.signImg) {
                                            RouterBean routerBean = new RouterBean();
                                            routerBean.type = 9;
                                            routerBean.tbCouponId = item.tbCouponId;
                                            routerBean.mustParam = "type=4"
//                                                + "&id=" + item.product.get(position).id
                                                    + "&tbItemId=" + item.cfProductId;
                                            LinkRouterUtils.getInstance().setRouterBean( getActivity(), routerBean );
                                        } else {
                                            //点击图片放大图片，点击查看进入查看详情（显示查看按钮）
                                            ARouter.getInstance().build( ARouters.PATH_PREIMG )
                                                    .withStringArrayList( "urls", item.detailImages )
                                                    .withInt( "position", position )
                                                    .withBoolean( "withDelete", false )
                                                    .withBoolean( "showLoad", true )
                                                    .navigation();
                                        }

                                    }
                                } );

                            } else if (item.detailImages.size() == 1) {//单张图片
                                rv_friends_img.setVisibility( View.GONE );
                                rlFriendsImgParent.setVisibility( View.VISIBLE );

                                try {
                                    final ImageView ivProductImg = helper.getView( R.id.iv_product_img );
                                    Glide.with( BaseApplication.getApplication() ).load( item.detailImages.get( 0 ) )
                                            .asBitmap()
                                            .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                                            .placeholder( R.drawable.bg_common_img_null )
                                            .into( new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    ViewGroup.LayoutParams params = ivProductImg.getLayoutParams();
                                                    params.width = resource.getWidth();
                                                    params.height = resource.getHeight();
                                                    ivProductImg.setLayoutParams( params );
                                                    ivProductImg.setImageBitmap( resource );
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                    super.onLoadFailed( e, errorDrawable );
                                                    ViewGroup.LayoutParams params = ivProductImg.getLayoutParams();
                                                    params.width = errorDrawable.getIntrinsicWidth();
                                                    params.height = errorDrawable.getIntrinsicHeight();
                                                    ivProductImg.setLayoutParams( params );
                                                    ivProductImg.setImageDrawable( errorDrawable );
                                                }
                                            } );

                                    helper.getView( R.id.tv_product_price ).setVisibility( View.VISIBLE );
                                    helper.setText( R.id.tv_product_price, "¥" + StringUtils.doubleToStringDeleteZero( item.cfCouponAfterPrice ) );
                                    rlFriendsImgParent.setOnClickListener( new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //这里是单张图片，点击应该直接跳转到详情，不应该有查看大图的行为（物料可以有）
                                            RouterBean routerBean = new RouterBean();
                                            routerBean.type = 9;
                                            routerBean.tbCouponId = item.tbCouponId;
                                            routerBean.mustParam = "type=4"
//                                                + "&id=" + item.product.get(position).id
                                                    + "&tbItemId=" + item.cfProductId;
                                            LinkRouterUtils.getInstance().setRouterBean( getActivity(), routerBean );
                                        }
                                    } );
                                } catch (Exception e) {

                                }

                            }


                        } else if (item.detailImages != null) {//物料
                            //多张图片
                            if (item.detailImages.size() > 1) {
                                rv_friends_img.setVisibility( View.VISIBLE );
                                rlFriendsImgParent.setVisibility( View.GONE );
                                BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>( R.layout.item_friends_vpcontent_child, item.detailImages ) {

                                    @Override
                                    protected void convert(BaseViewHolder helper, String item) {

                                        try {
                                            ImageView ivProductImg = helper.getView( R.id.siv_product_img );
                                            Glide.with( BaseApplication.getApplication() ).load( item )
                                                    .placeholder( R.drawable.bg_common_img_null )
                                                    .into( ivProductImg );

                                            helper.getView( R.id.tv_product_price ).setVisibility( View.INVISIBLE );
                                        } catch (Exception e) {

                                        }

                                    }
                                };

                                rv_friends_img.setAdapter( baseQuickAdapter );
                                baseQuickAdapter.setOnItemClickListener( new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                        //点击图片放大图片，点击查看进入查看详情（显示查看按钮）
                                        ARouter.getInstance().build( ARouters.PATH_PREIMG )
                                                .withStringArrayList( "urls", item.detailImages )
                                                .withInt( "position", position )
                                                .withBoolean( "withDelete", false )
                                                .withBoolean( "showLoad", true )
                                                .navigation();

                                    }
                                } );
                            } else if (item.detailImages.size() == 1) {//单张图片
                                rv_friends_img.setVisibility( View.GONE );
                                rlFriendsImgParent.setVisibility( View.VISIBLE );
                                try {
                                    final ImageView ivProductImg = helper.getView( R.id.iv_product_img );
                                    Glide.with( BaseApplication.getApplication() ).load( item.detailImages.get( 0 ) )
                                            .asBitmap()
                                            .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                                            .placeholder( R.drawable.bg_common_img_null )
                                            .into( new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    ViewGroup.LayoutParams params = ivProductImg.getLayoutParams();
                                                    params.width = resource.getWidth();
                                                    params.height = resource.getHeight();
                                                    ivProductImg.setLayoutParams( params );
                                                    ivProductImg.setImageBitmap( resource );
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                    super.onLoadFailed( e, errorDrawable );
                                                    ViewGroup.LayoutParams params = ivProductImg.getLayoutParams();
                                                    params.width = errorDrawable.getIntrinsicWidth();
                                                    params.height = errorDrawable.getIntrinsicHeight();
                                                    ivProductImg.setLayoutParams( params );
                                                    ivProductImg.setImageDrawable( errorDrawable );
                                                }
                                            } );

                                    helper.getView( R.id.tv_product_price ).setVisibility( View.INVISIBLE );
                                    rlFriendsImgParent.setOnClickListener( new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //点击图片放大图片，点击查看进入查看详情（显示查看按钮）
                                            ARouter.getInstance().build( ARouters.PATH_PREIMG )
                                                    .withStringArrayList( "urls", item.detailImages )
                                                    .withInt( "position", 0 )
                                                    .withBoolean( "withDelete", false )
                                                    .withBoolean( "showLoad", true )
                                                    .navigation();
                                        }
                                    } );
                                } catch (Exception e) {

                                }
                            }

                        }


                    } catch (Exception e) {

                    }
                }
            };
            mRvFriendsContent.setHasFixedSize( true );
            mRvFriendsContent.setNestedScrollingEnabled( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), 1 );
            mRvFriendsContent.setLayoutManager( gridLayoutManager );
            mRvFriendsContent.setAdapter( mBaseProductQuickAdapter );

        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 分享的弹出框
     */
    private CommonDialog mShareDialog;

    private void showShareDialog(final FriendsBean.ListBean item) {
        if (mShareDialog != null) {
            mShareDialog.dismiss();
            mShareDialog = null;
        }
        if (mShareDialog == null) {
            mShareDialog = new CommonDialog( getActivity(), R.layout.dialog_friends_share );
        }
        mShareDialog.setGravity( Gravity.BOTTOM );
        TextView tvDialogHint = (TextView) mShareDialog.getViewId( R.id.tv_dialog_hint );
        SpannableString sp = new SpannableString( "小提示:微信朋友圈如无法一键分享,请先“一键存图”,在手动“发朋友圈”哦~" );
        int length1 = "小提示:微信朋友圈如无法一键分享,请先".length();
        int length2 = "小提示:微信朋友圈如无法一键分享,请先“一键存图”,在手动".length();
        sp.setSpan( new ForegroundColorSpan( getResources().getColor( R.color.c_main ) ), length1, length1 + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        sp.setSpan( new ForegroundColorSpan( getResources().getColor( R.color.c_main ) ), length2, length2 + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tvDialogHint.setText( sp );
        mShareDialog.getViewId( R.id.share_weixin ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPermission( item, SHARE_MEDIA.WEIXIN );
                mShareDialog.dismiss();
            }
        } );
        mShareDialog.getViewId( R.id.share_friends ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPermission( item, SHARE_MEDIA.WEIXIN_CIRCLE );
                mShareDialog.dismiss();
            }
        } );
        mShareDialog.getViewId( R.id.save_local ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {//保存到本地
                openPermission( item, null );
                mShareDialog.dismiss();
            }
        } );
        mShareDialog.getViewId( R.id.share_qq ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPermission( item, SHARE_MEDIA.QQ );
                mShareDialog.dismiss();
            }
        } );
        mShareDialog.getViewId( R.id.share_sina ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPermission( item, SHARE_MEDIA.SINA );
                mShareDialog.dismiss();
            }
        } );
        mShareDialog.getViewId( R.id.share_qq_kongjian ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPermission( item, SHARE_MEDIA.QZONE );
                mShareDialog.dismiss();
            }
        } );
        mShareDialog.getViewId( R.id.tv_cancle ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
            }
        } );


        mShareDialog.show();
    }

    private void openPermission(final FriendsBean.ListBean item, final SHARE_MEDIA media) {
        AndPermission.with( getActivity() )
                .permission( Permission.Group.CAMERA, Permission.Group.STORAGE )
                .onGranted( new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        String cfShareCopy = item.cfShareCopy;
                        if (!TextUtils.isEmpty( cfShareCopy )) {
                            // 获取系统剪贴板
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService( Context.CLIPBOARD_SERVICE );

                            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                            ClipData clipData = ClipData.newPlainText( "app_friends_share_copy", TextUtils.isEmpty( cfShareCopy ) ? "" : cfShareCopy );

                            // 把数据集设置（复制）到剪贴板
                            clipboard.setPrimaryClip( clipData );
                            toast( "复制成功！" );
                        } else {
                            toast( "复制失败！" );
                        }
                        getShareImages( item.id, item.cfType, item.cfNumber, media );

                    }
                } )
                .onDenied( new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                        //权限申请失败
                    }
                } ).start();
    }

    /**
     */
    /**
     * 获取左边数据
     */
    private void getLeftData(RxObserver<FriendsBean> rxObserver) {
        Map<String, String> allParam = new HashMap<>();
        allParam.put( "page", page + "" );

        NetWork.getInstance()
                .setTag( Qurl.friendsLeft )
                .getApiService( ModuleApi.class )
                .friendsLeft( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( rxObserver );
    }

    /**
     * 获取右边数据
     */
    private void getRightData(RxObserver<FriendsBean> rxObserver) {
        Map<String, String> allParam = new HashMap<>();
        allParam.put( "page", page + "" );

        NetWork.getInstance()
                .setTag( Qurl.friendsRight )
                .getApiService( ModuleApi.class )
                .friendsRight( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( rxObserver );
    }


    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartFriends.finishRefresh( state );
            if (!state) {
                //第一次加载失败时，再次显示时可以重新加载
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartFriends.finishLoadMoreWithNoMoreData();
        } else {
            mSmartFriends.finishLoadMore( state );
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        page++;
        getContentData();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

        page = 1;
        mSmartFriends.setNoMoreData( false );
        getContentData();
    }

    private ArrayList<FriendsLoadBean> mFriendsLoadBeans;

    private void getShareImages(String id, String cfType, int cfNumber, final SHARE_MEDIA media) {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put( "id", id );
        allParam.put( "cfType", cfType );
        allParam.put( "cfNumber", cfNumber );

        NetWork.getInstance()
                .setTag( Qurl.friendsShareImages2 )
                .getApiService( ModuleApi.class )
                .friendsShareImages2( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<FriendsShareImagesBean>( getActivity(), false, true ) {
                    @Override
                    public void onSuccess(FriendsShareImagesBean result) {
                        if (result == null || result.circleList == null || result.circleList.size() <= 0) {

                            toast( "获取图片地址出错" );
                            return;
                        }

                        if (result.outCount > 0) {
                            toast( result.outCount + "个商品已失效！" );
                        }

                        int cfType = TextUtils.isEmpty( result.cfType ) ? -1 : Integer.valueOf( result.cfType );
                        if (cfType == -1) {
                            toast( "cftype参数错误" );
                            return;
                        }

                        if (mFriendsLoadBeans == null) {
                            mFriendsLoadBeans = new ArrayList<>();
                        }
                        mFriendsLoadBeans.clear();

                        try {

                            for (int i = 0; i < result.circleList.size(); i++) {

                                if (cfType == 1) {
                                    FriendsLoadBean friendsLoadBean = new FriendsLoadBean();
                                    friendsLoadBean.drawCode = cfType == 1;//多商品每个都需要合成二维码图片
                                    friendsLoadBean.shopType = result.circleList.get( i ).shopType;
                                    friendsLoadBean.cfShareCopy = result.circleList.get( i ).cfShareCopy;
                                    friendsLoadBean.cfProductTitle = result.circleList.get( i ).cfProductTitle;
                                    friendsLoadBean.qrCodeUrl = result.circleList.get( i ).qrCodeUrl;
                                    friendsLoadBean.cfCouponAfterPrice = StringUtils.doubleToStringDeleteZero( result.circleList.get( i ).cfCouponAfterPrice );
                                    friendsLoadBean.cfPrice = StringUtils.doubleToStringDeleteZero( result.circleList.get( i ).cfPrice );
                                    friendsLoadBean.couponPrice = StringUtils.doubleToStringDeleteZero( result.circleList.get( i ).couponPrice );
                                    friendsLoadBean.productUrl = result.circleList.get( i ).detailImages.get( 0 );
                                    mFriendsLoadBeans.add( friendsLoadBean );
                                } else {//单商品和物料的情况
                                    if (result.circleList.get( 0 ).detailImages != null && result.circleList.get( 0 ).detailImages.size() > 0) {
                                        //这里单商品和物料result.circleList的size只有一个
                                        for (int j = 0; j < result.circleList.get( 0 ).detailImages.size(); j++) {
                                            FriendsLoadBean friendsLoadBean = new FriendsLoadBean();
                                            friendsLoadBean.drawCode = cfType == 0 && j == 0;//单商品时只有第一张需要合成二维码图片
                                            if (cfType == 0) {//单商品需要，物料不需要
                                                friendsLoadBean.shopType = result.circleList.get( i ).shopType;
                                                friendsLoadBean.cfShareCopy = result.circleList.get( i ).cfShareCopy;
                                                friendsLoadBean.cfProductTitle = result.circleList.get( i ).cfProductTitle;
                                                friendsLoadBean.qrCodeUrl = result.circleList.get( i ).qrCodeUrl;
                                                friendsLoadBean.cfCouponAfterPrice = StringUtils.doubleToStringDeleteZero( result.circleList.get( i ).cfCouponAfterPrice );
                                                friendsLoadBean.cfPrice = StringUtils.doubleToStringDeleteZero( result.circleList.get( i ).cfPrice );
                                                friendsLoadBean.couponPrice = StringUtils.doubleToStringDeleteZero( result.circleList.get( i ).couponPrice );
                                            }

                                            friendsLoadBean.productUrl = result.circleList.get( 0 ).detailImages.get( j );
                                            mFriendsLoadBeans.add( friendsLoadBean );
                                        }
                                    } else {
                                        toast( "detailImages获取图片地址出错" );
                                    }

                                }
                            }
                            LoadImage.getInstance().startLoadImages( getActivity(), true, mFriendsLoadBeans, new LoadImage.OnLoadImageLisenter() {
                                @Override
                                public void onSuccess(List<File> path, int failNum, boolean isCancle) {
                                    if (isCancle) {
                                        toast( "已取消！" );
                                        return;
                                    }
                                    List<File> pathCopy = new ArrayList<>();

                                    //非空过滤
                                    for (int i = 0; i < path.size(); i++) {
                                        if (path.get( i ) != null) {
                                            pathCopy.add( path.get( i ) );
                                        }
                                    }

                                    File[] shareFile = new File[pathCopy.size()];
                                    for (int i = 0; i < pathCopy.size(); i++) {
                                        shareFile[i] = pathCopy.get( i );
                                    }
                                    if (shareFile == null || shareFile.length <= 0) {
                                        toast( "图片路径出错！" );
                                        return;
                                    }

                                    ArrayList<File> uriListUm = new ArrayList<>();
                                    for (int i = 0; i < shareFile.length; i++) {
                                        //将所有的本地地址转换为Uri并添加至集合
                                        uriListUm.add( shareFile[i] );
                                    }

                                    if (media == null) { //保存到本地
                                        try {
//                                            for (int i = 0; i < pathCopy.size(); i++) {
//                                                File file = pathCopy.get( i );
//                                                MediaStore.Images.Media.insertImage( getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), null );
//                                                // 发送广播，通知刷新图库的显示
//                                                getActivity().sendBroadcast( new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse( "file://" + file.getName() ) ) );
//                                            }
//                                            toast( "图片已保存" );
                                            requestPermission();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else if (media == SHARE_MEDIA.WEIXIN) {//微信
                                        ShareUtils.shareToWChart( getActivity(), shareFile, "" );
                                    } else if (media == SHARE_MEDIA.WEIXIN_CIRCLE) {//微信圈
//                                        ShareUtils.shareToWChartFs(getActivity(), shareFile, "");
                                        ShareUtils.umShare( getActivity(), media, uriListUm, new UMShareListener() {
                                            @Override
                                            public void onStart(SHARE_MEDIA share_media) {
                                                Logger.e( "-----", "onStart" );

                                            }

                                            @Override
                                            public void onResult(SHARE_MEDIA share_media) {
                                                Logger.e( "-----", "onResult" );
                                            }

                                            @Override
                                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                                Logger.e( "-----", "onError" );
                                            }

                                            @Override
                                            public void onCancel(SHARE_MEDIA share_media) {
                                                Logger.e( "-----", "onCancel" );
                                            }
                                        } );

                                    } else if (media == SHARE_MEDIA.QQ) {
                                        ShareUtils.shareToQQ( getActivity(), shareFile, "" );
                                    } else if (media == SHARE_MEDIA.QZONE) {//qq和qq空间
                                        ShareUtils.umShare( getActivity(), media, uriListUm, new UMShareListener() {
                                            @Override
                                            public void onStart(SHARE_MEDIA share_media) {
                                                Logger.e( "-----", "onStart" );

                                            }

                                            @Override
                                            public void onResult(SHARE_MEDIA share_media) {
                                                Logger.e( "-----", "onResult" );
                                            }

                                            @Override
                                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                                Logger.e( "-----", "onError" );
                                            }

                                            @Override
                                            public void onCancel(SHARE_MEDIA share_media) {
                                                Logger.e( "-----", "onCancel" );
                                            }
                                        } );

                                    } else if (media == SHARE_MEDIA.SINA) {//新浪微博
                                        ShareUtils.shareToSinaWeiBo( getActivity(), shareFile, "" );
                                    }


                                }

                            } );
                        } catch (Exception e) {

                            Logger.e( "----", e.toString() );
                        }
                    }
//                    @Override
//                    public void onFailed(int errorCode, String moreInfo) {
//                        super.onFailed(errorCode, moreInfo);
//                        setRefreshLoadMoreState ( false, false );
//                    }

//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        setRefreshLoadMoreState ( false, false );
//                    }

                    /**
                     * 授权过期跳授权
                     */
                    public void onFailed_4011() {
                        getShouQuan();
                    }
                } );
    }

    private void getShouQuan() {

        final AlibcLogin alibcLogin = AlibcLogin.getInstance();
        if (alibcLogin.isLogin()) {
            showProgressDialog();
            alibcLogin.logout( new AlibcLoginCallback() {
                @Override
                public void onSuccess(int i) {
                    alibcLogin.showLogin( new AlibcLoginCallback() {
                        @Override
                        public void onSuccess(int i) {
                            stopProgressDialog();
                            ARouter.getInstance().build( ARouters.PATH_COMMOM_WEB )
                                    .withString( "loadUrl", Qurl.HOST + Qurl.shouquan )
                                    .withInt( "type", 4 )
                                    .withString( Constant.TITLE, "授权登陆" )
                                    .navigation();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast( s );
                            stopProgressDialog();
                        }
                    } );
                }

                @Override
                public void onFailure(int i, String s) {
                    stopProgressDialog();
                }
            } );

        } else {
            alibcLogin.showLogin( new AlibcLoginCallback() {
                @Override
                public void onSuccess(int i) {
                    ARouter.getInstance().build( ARouters.PATH_COMMOM_WEB )
                            .withString( "loadUrl", Qurl.HOST + Qurl.shouquan )
                            .withInt( "type", 4 )
                            .withString( Constant.TITLE, "授权登陆" )
                            .navigation();
                }

                @Override
                public void onFailure(int i, String s) {
                    toast( s );
                }
            } );
        }

    }

    /**
     * 启动加载进度条
     */
    protected void showProgressDialog() {
        try {
            createProgressDialog( getContext() );
            if (progressDialog != null) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建进度条实例
     */
    protected void createProgressDialog(Context cxt) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog( cxt, true );
                progressDialog.setCanceledOnTouchOutside( false );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭加载进度条
     */
    protected void stopProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //---------------------------------下面是图片保存到本地的操作方法----------------------------------------

    private static final int REQUEST_CODE_SAVE_IMG = 10;

    //同意授权
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Log.i( TAG, "onPermissionsGranted:" + requestCode + ":" + list.size() );
        saveImage();
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i( TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size() );
        if (EasyPermissions.somePermissionPermanentlyDenied( this, perms )) {
            //打开系统设置，手动授权
            new AppSettingsDialog.Builder( this ).build().show();
        }
    }

    //授权结果，分发下去
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult( requestCode, permissions, grantResults, this );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //拒绝授权后，从系统设置了授权后，返回APP进行相应的操作
            Log.i( TAG, "onPermissionsDenied:------>自定义设置授权后返回APP" );
            saveImage();
        }
    }

    /**
     * 请求读取sd卡的权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //读取sd卡的权限
            String[] mPermissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions( getActivity(), mPermissionList )) {
                //已经同意过
                saveImage();
            } else {
                //未同意过,或者说是拒绝了，再次申请权限
                EasyPermissions.requestPermissions(
                        this,  //上下文
                        "保存图片需要读取sd卡的权限", //提示文言
                        REQUEST_CODE_SAVE_IMG, //请求码
                        mPermissionList //权限列表
                );
            }
        } else {
            saveImage();
        }
    }

    //保存图片
    private void saveImage() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mFriendsLoadBeans.size(); i++) {
                    Bitmap bitmap = ImgUtils.returnBitMap( mFriendsLoadBeans.get( i ).productUrl );
                    boolean isSaveSuccess = ImgUtils.saveImageToGallery( getActivity(), bitmap );
                    //最后时候提示保存成功
                    if (i == mFriendsLoadBeans.size() - 1) {
                        Message message = handler.obtainMessage();
                        handler.sendMessage( message );
                    }
                }

            }
        } ).start();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage( msg );
            Toast.makeText( getActivity(), "保存图片成功", Toast.LENGTH_SHORT ).show();

        }
    };


}
