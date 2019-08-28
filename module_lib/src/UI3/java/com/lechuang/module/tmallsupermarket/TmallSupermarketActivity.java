package java.com.lechuang.module.tmallsupermarket;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
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
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseApplication;
import com.common.app.base.FragmentActivity;
import com.common.app.base.bean.BaseItemEntity;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.LoadImage;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.Utils;
import com.common.app.utils.ZeroBuyLoadImage;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.NumSeekBar;
import com.common.app.view.SquareImageView;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sunfusheng.marqueeview.MarqueeView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouwei.mzbanner.BannerBgContainer;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.JoinSuccessBean;
import java.com.lechuang.module.bean.MyZeroBuyBean;
import java.com.lechuang.module.bean.ShowInMyCardBean;
import java.com.lechuang.module.bean.TmallSupermarketBean;
import java.com.lechuang.module.bean.TryDetailsBean;
import java.com.lechuang.module.bean.TryRuleBean;
import java.com.lechuang.module.bean.ZeroBuyShareAppBean;
import java.com.lechuang.module.mytry.MyTryActivity;
import java.com.lechuang.module.mytry.TryDetailsActivity;
import java.com.lechuang.module.mytry.adapter.TryDetailsAdapter;
import java.com.lechuang.module.mytry.bean.MyTryAllEntity;
import java.com.lechuang.module.mytry.bean.TryDetailsEntity;
import java.com.lechuang.module.product.adapter.BannerViewHolder;
import java.com.lechuang.module.tmallsupermarket.adapter.TmallSupermarketAdapter;
import java.com.lechuang.module.tmallsupermarket.bean.TmallSupermarketEntity;
import java.com.lechuang.module.zerobuy.bean.MyZeroBuyEntity;
import java.com.lechuang.module.zerobuy.bean.MyZeroBuyOtherEntity;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_TMALL_SUPERMARKET)
public class TmallSupermarketActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartRefreshLayout;
    public BannerBgContainer mBannerBgContainer;
    private RecyclerView mRvFenSi;
    private TextView mTvTitle,mTvRight;
    private int num=0;
    private int page = 1;//页数
    public static boolean refresh=false;
    private Map<String,Integer> showOne=new HashMap<>(  );
    private Map<String,Integer> showTwo=new HashMap<>(  );
    private Map<String,Integer> showThree=new HashMap<>(  );
    private Map<String,Integer> showFour=new HashMap<>(  );
    private Map<String,Integer> showFive=new HashMap<>(  );
    private int itemNum=0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tmall_supermarket;
//        return R.layout.activity_try_details;
    }

    @Override
    protected void findViews() {
        $(R.id.iv_common_back).setOnClickListener(this);
        mRvFenSi=$(R.id.rv_mytry);
        mTvTitle=$(R.id.iv_common_title);
        mTvRight=$(R.id.tv_common_right);
        mTvRight.setOnClickListener(this);
        mSmartRefreshLayout = $(R.id.mSmartRefreshLayout);

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mTvTitle.setText("淘宝福利专区");
        mSmartRefreshLayout.setOnRefreshLoadMoreListener ( this );
        mSmartRefreshLayout.setEnableLoadMore ( true );
    }

    @Override
    protected void getData() {
        updataAlipay();
//        mSmartRefreshLayout.autoRefresh(500);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id== R.id.iv_common_back){
            finish();
        }else if (id==R.id.tv_common_right){
            ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_RULE).navigation();
        }
    }

    int ceshi=0;
    private void updataAlipay() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("obtype", 1);
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag( Qurl.tmallSupermarket)
                .getApiService(ModuleApi.class)
                .getTmallSupermarket(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TmallSupermarketBean> (TmallSupermarketActivity.this,true,true) {

                    @Override
                    public void onSuccess(final TmallSupermarketBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true,0 );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false ,0);
                        setHomeAdapter(result,1);
//                        if (ceshi==0){
//                            setHomeAdapter(result,1);
//                            ceshi=1;
//                        }else if (ceshi==1){
//                            setHomeAdapter(result,2);
//                            ceshi=0;
//                        }

                    }
                });
    }

    private void updataAlipaytwo() {
        Map<String, Object> allParam = new HashMap<>();
//        allParam.put("id", 104);
        allParam.put("obtype", 2);
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag( Qurl.tmallSupermarket)
                .getApiService(ModuleApi.class)
                .getTmallSupermarket(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TmallSupermarketBean> (TmallSupermarketActivity.this,true,true) {

                    @Override
                    public void onSuccess(final TmallSupermarketBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true,0 );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false,0 );
                        setHomeAdapter(result,2);
//                        if (ceshi==0){
//                            setHomeAdapter(result,1);
//                            ceshi=1;
//                        }else if (ceshi==1){
//                            setHomeAdapter(result,2);
//                            ceshi=0;
//                        }

                    }
                });
    }
    private void updataAlipayThree() {
        Map<String, Object> allParam = new HashMap<>();
//        allParam.put("id", 105);
        allParam.put("obtype", 3);
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag( Qurl.tmallSupermarket)
                .getApiService(ModuleApi.class)
                .getTmallSupermarket(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TmallSupermarketBean> (TmallSupermarketActivity.this,true,true) {

                    @Override
                    public void onSuccess(final TmallSupermarketBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true,0 );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false ,0);
                        setHomeAdapter(result,3);
//                        if (ceshi==0){
//                            setHomeAdapter(result,1);
//                            ceshi=1;
//                        }else if (ceshi==1){
//                            setHomeAdapter(result,2);
//                            ceshi=0;
//                        }

                    }
                });
    }
    /**
     * 设置adapter数据
     *
     * @param result
     */
    private List<TmallSupermarketEntity> mHomeAllEntities;
    private TmallSupermarketAdapter mHomeRvAdapter;
    private void setHomeAdapter (TmallSupermarketBean result,int count) {
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        if (page==1){
            if (count==1){
                mHomeAllEntities.clear();
                if (result.bannerList != null && result.bannerList.size() > 0){
                    TmallSupermarketEntity TianMaoEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_HEADER);
                    //更新使用者数据
                    TianMaoEntity.mBanners = result;
                    mHomeAllEntities.add(TianMaoEntity);
                }
                for (int i =0;i<result.list.size();i++){
                    TmallSupermarketEntity homeAllEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_PRODUCT);
                    homeAllEntity.TianMao = result.list.get( i );
//                    homeAllEntity.id = result.list.get( i ).id;
//                    homeAllEntity.title = result.list.get( i ).title;
//                    homeAllEntity.img = result.list.get( i ).img;
//                    homeAllEntity.descriptionWords = result.list.get( i ).descriptionWords;//文字说明
                    mHomeAllEntities.add(homeAllEntity);
                }
                num=1;

            }else if (count==2){
                mHomeAllEntities.clear();
                if (result.list != null && result.list.size() > 0){
                    TmallSupermarketEntity homeAllEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_HEADER);
                    //更新使用者数据
                    homeAllEntity.mBanners = result;
                    mHomeAllEntities.add(homeAllEntity);
                }
                mHomeRvAdapter.notifyDataSetChanged();
                for (int i =0;i<result.list.size();i++){
                    TmallSupermarketEntity homeAllEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_PRODUCTTWO);
                    homeAllEntity.loopholeList = result.list.get( i );
                    mHomeAllEntities.add(homeAllEntity);
                }
                num=2;

            }else if (count==3){
                mHomeAllEntities.clear();
                if (result.bannerList != null && result.bannerList.size() > 0){
                    TmallSupermarketEntity homeAllEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_HEADER);
                    //更新使用者数据
                    homeAllEntity.mBanners = result;
                    mHomeAllEntities.add(homeAllEntity);
                }
                mHomeRvAdapter.notifyDataSetChanged();
                //添加
                for (int i =0;i<result.list.size();i++){
                    TmallSupermarketEntity haoJuanEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_PRODUCTTHREE);
//                        homeAllEntity.haojuan = result.list.get( i );
                    haoJuanEntity.ids = result.list.get( i ).ids;
                    haoJuanEntity.itemName = result.list.get( i ).name;
                    haoJuanEntity.img = result.list.get( i ).img;
                    if (result.list.get( i ).secondList!=null&&result.list.get( i ).secondList.size()>0){
//                        haoJuanEntity.secondListBean = result.list.get( i ).secondList;
                        for (int n=0;n<result.list.get( i ).secondList.size();n++){
                            if (n==0){
                                haoJuanEntity.idOne=result.list.get( i ).secondList.get( n ).id;
                                haoJuanEntity.imgOne=result.list.get( i ).secondList.get( n ).img;
                                haoJuanEntity.nameOne=result.list.get( i ).secondList.get( n ).name;
                                haoJuanEntity.statusOne=result.list.get( i ).secondList.get( n ).status;
                                haoJuanEntity.superIdOne=result.list.get( i ).secondList.get( n ).superId;
                            }
                            if (n==1){
                                haoJuanEntity.idTwo=result.list.get( i ).secondList.get( n ).id;
                                haoJuanEntity.imgTwo=result.list.get( i ).secondList.get( n ).img;
                                haoJuanEntity.nameTwo=result.list.get( i ).secondList.get( n ).name;
                                haoJuanEntity.statusTwo=result.list.get( i ).secondList.get( n ).status;
                                haoJuanEntity.superIdTwo=result.list.get( i ).secondList.get( n ).superId;
                            }
                            if (n==2){
                                haoJuanEntity.idThree=result.list.get( i ).secondList.get( n ).id;
                                haoJuanEntity.imgThree=result.list.get( i ).secondList.get( n ).img;
                                haoJuanEntity.nameThree=result.list.get( i ).secondList.get( n ).name;
                                haoJuanEntity.statusThree=result.list.get( i ).secondList.get( n ).status;
                                haoJuanEntity.superIdThree=result.list.get( i ).secondList.get( n ).superId;
                            }
                            if (n==3){
                                haoJuanEntity.idFour=result.list.get( i ).secondList.get( n ).id;
                                haoJuanEntity.imgFour=result.list.get( i ).secondList.get( n ).img;
                                haoJuanEntity.nameFour=result.list.get( i ).secondList.get( n ).name;
                                haoJuanEntity.statusFour=result.list.get( i ).secondList.get( n ).status;
                                haoJuanEntity.superIdFour=result.list.get( i ).secondList.get( n ).superId;
                            }
                        }
                    }
                    haoJuanEntity.itemNum = itemNum;
                    showOne.put( ""+itemNum,View.GONE );
                    showTwo.put( ""+itemNum,View.GONE );
                    showThree.put( ""+itemNum,View.GONE );
                    showFour.put( ""+itemNum,View.GONE );
                    showFive.put( ""+itemNum,View.VISIBLE );
                    itemNum++;
                    mHomeAllEntities.add(haoJuanEntity);
                }
                num=3;
            }
        }else {
            if (count==1){
                if (result.list == null || result.list.size() <= 0) {
                    setRefreshLoadMoreState(true, true,0);
                } else {
                    mSmartRefreshLayout.setNoMoreData(false);
                }

                for (int i = 0; result.list != null && i < result.list.size(); i++) {
                    TmallSupermarketEntity homeAllProduct = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_PRODUCT);
                    homeAllProduct.TianMao = result.list.get( i );
//                    homeAllProduct.ids = result.list.get( i ).ids;
//                    homeAllProduct.title = result.list.get( i ).title;
//                    homeAllProduct.img = result.list.get( i ).img;
//                    homeAllProduct.descriptionWords = result.list.get( i ).descriptionWords;//文字说明
                    mHomeAllEntities.add(homeAllProduct);
                }
            }else if (count==2){
                if (result.list == null || result.list.size() <= 0) {
                    setRefreshLoadMoreState(true, true,0);
                } else {
                    mSmartRefreshLayout.setNoMoreData(false);
                }
                for (int i = 0; result.list != null && i < result.list.size(); i++) {
                    TmallSupermarketEntity homeAllProduct = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_PRODUCTTWO);
                    homeAllProduct.loopholeList = result.list.get( i );
                    mHomeAllEntities.add(homeAllProduct);
                }
            }else if (count==3){
                if (result.list == null || result.list.size() <= 0) {
                    setRefreshLoadMoreState(true, true,0);
                } else {
                    mSmartRefreshLayout.setNoMoreData(false);
                }
                for (int i =0;i<result.list.size();i++){
                    TmallSupermarketEntity homeAllEntity = new TmallSupermarketEntity(TmallSupermarketEntity.TYPE_PRODUCTTHREE);
                    homeAllEntity.ids = result.list.get( i ).ids;
                    homeAllEntity.itemName = result.list.get( i ).name;
                    homeAllEntity.img = result.list.get( i ).img;
                    if (result.list.get( i ).secondList!=null&&result.list.get( i ).secondList.size()>0){
//                        homeAllEntity.secondListBean = result.list.get( i ).secondList;
                        for (int n=0;n<result.list.get( i ).secondList.size();n++){
                            if (n==0){
                                homeAllEntity.idOne=result.list.get( i ).secondList.get( n ).id;
                                homeAllEntity.imgOne=result.list.get( i ).secondList.get( n ).img;
                                homeAllEntity.nameOne=result.list.get( i ).secondList.get( n ).name;
                                homeAllEntity.statusOne=result.list.get( i ).secondList.get( n ).status;
                                homeAllEntity.superIdOne=result.list.get( i ).secondList.get( n ).superId;
                            }
                            if (n==1){
                                homeAllEntity.idTwo=result.list.get( i ).secondList.get( n ).id;
                                homeAllEntity.imgTwo=result.list.get( i ).secondList.get( n ).img;
                                homeAllEntity.nameTwo=result.list.get( i ).secondList.get( n ).name;
                                homeAllEntity.statusTwo=result.list.get( i ).secondList.get( n ).status;
                                homeAllEntity.superIdTwo=result.list.get( i ).secondList.get( n ).superId;
                            }
                            if (n==2){
                                homeAllEntity.idThree=result.list.get( i ).secondList.get( n ).id;
                                homeAllEntity.imgThree=result.list.get( i ).secondList.get( n ).img;
                                homeAllEntity.nameThree=result.list.get( i ).secondList.get( n ).name;
                                homeAllEntity.statusThree=result.list.get( i ).secondList.get( n ).status;
                                homeAllEntity.superIdThree=result.list.get( i ).secondList.get( n ).superId;
                            }
                            if (n==3){
                                homeAllEntity.idFour=result.list.get( i ).secondList.get( n ).id;
                                homeAllEntity.imgFour=result.list.get( i ).secondList.get( n ).img;
                                homeAllEntity.nameFour=result.list.get( i ).secondList.get( n ).name;
                                homeAllEntity.statusFour=result.list.get( i ).secondList.get( n ).status;
                                homeAllEntity.superIdFour=result.list.get( i ).secondList.get( n ).superId;
                            }
                        }
                    }
                    homeAllEntity.itemNum = itemNum;
                    showOne.put( ""+itemNum,View.GONE );
                    showTwo.put( ""+itemNum,View.GONE );
                    showThree.put( ""+itemNum,View.GONE );
                    showFour.put( ""+itemNum,View.GONE );
                    showFour.put( ""+itemNum,View.VISIBLE );
                    itemNum++;
                    mHomeAllEntities.add(homeAllEntity);
                }
            }
        }

        if (mHomeRvAdapter ==null){
            mHomeRvAdapter = new TmallSupermarketAdapter<TmallSupermarketEntity, BaseViewHolder>(mHomeAllEntities){
                @Override
                protected void addItemTypeView() {

                    //条目布局
                    addItemType(TmallSupermarketEntity.TYPE_HEADER, R.layout.activity_tmall_supermarket_title);
                    addItemType(TmallSupermarketEntity.TYPE_PRODUCT, R.layout.item_tmallsupermarket_one_list);
                    addItemType(TmallSupermarketEntity.TYPE_PRODUCTTWO, R.layout.item_tmallsupermarket_two_list);
                    addItemType(TmallSupermarketEntity.TYPE_PRODUCTTHREE, R.layout.item_tmallsupermarket_three_list);
                }
                @Override
                protected void convert(final BaseViewHolder helper, final TmallSupermarketEntity item) {

                    if (helper.getItemViewType() == TmallSupermarketEntity.TYPE_HEADER) {
                        setBannerData(helper,item);

                    } else if (helper.getItemViewType() == TmallSupermarketEntity.TYPE_PRODUCT) {

                        try {
                            ImageView squareImageView = helper.getView( R.id.iv_item_all_product_tupian );
                            Glide.with(BaseApplication.getApplication()).load(item.TianMao.img).placeholder(R.drawable.bg_trydetails_null).into(squareImageView);
                            //标题  标注
                            TextView biaoti=helper.getView( R.id.tv_item_tmallsupermarket_biaoti );
                            TextView biaozhu=helper.getView( R.id.tv_item_tmallsupermarket_biaozhu );
                            biaoti.setText( item.TianMao.title );
                            biaozhu.setText( item.TianMao.descriptionWords );
                            //点击查看详情
                            LinearLayout chakan = helper.getView( R.id.ll_item_all );
                            chakan.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    getMinute(item.TianMao.title,item.TianMao.ids);
//                                    ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_IMAGE).withString( "title",item.title ).withInt( "id",item.id ).navigation();
                                }
                            } );

                        } catch (Exception e) {
                            Logger.e("---->", e.toString());
                        }
                    }else if (helper.getItemViewType() == TmallSupermarketEntity.TYPE_PRODUCTTWO){
                        //图片 iv_item_all_product_tupian 标题 tv_item_tmallsupermarket_biaoti 标注 tv_item_tmallsupermarket_biaozhu
                        //劵金额 tv_item_all_product_quan 购买人 tv_goumairen 现价 tv_item_xianjia 原价 tv_item_yuanjia
                        //图片
                        ImageView squareImageView = helper.getView( R.id.iv_item_all_product_tupian );
                        Glide.with(BaseApplication.getApplication()).load(item.loopholeList.img).placeholder(R.drawable.bg_trydetails_null).into(squareImageView);
                        //标题标注
                        TextView biaoti=helper.getView( R.id.tv_item_tmallsupermarket_biaoti );
                        TextView biaozhu=helper.getView( R.id.tv_item_tmallsupermarket_biaozhu );
                        biaoti.setText( item.loopholeList.productName );
                        biaozhu.setText( item.loopholeList.productText );
                        //分享赚
                        helper.setText( R.id.tv_fenxiangzhuan,"分享赚￥"+item.loopholeList.zhuanMoney );
                        //购买人数tv_goumairen
                        helper.setText( R.id.tv_goumairen,""+item.loopholeList.nowNumber+"人购买" );
                        //劵金额
                        TextView tvJuan =helper.getView( R.id.tv_item_all_product_quan );
                        TextView tvJuanHead =helper.getView( R.id.tv_item_all_product_quan_txt );
                        if (item.loopholeList.couponMoney==0){
                            tvJuan.setVisibility( View.INVISIBLE );
                            tvJuanHead.setVisibility( View.INVISIBLE );
                        }else{
                            tvJuan.setVisibility( View.VISIBLE );
                            tvJuanHead.setVisibility( View.VISIBLE );
                        }
                        if ((int)item.loopholeList.couponMoney==item.loopholeList.couponMoney){
                            helper.setText( R.id.tv_item_all_product_quan,"￥ "+(int)item.loopholeList.couponMoney );
                        }else {
                            helper.setText( R.id.tv_item_all_product_quan,"￥ "+item.loopholeList.couponMoney );
                        }
                        //现价
                        helper.setText( R.id.tv_item_xianjia,""+item.loopholeList.preferentialPrice );
                        //原价
                        TextView yuanjia=helper.getView( R.id.tv_item_yuanjia );
                        yuanjia.setText( ""+item.loopholeList.price );
                        //添加删除线
                        yuanjia.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        //查看
                        LinearLayout chakan=helper.getView( R.id.ll_item_all );
                        chakan.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                try {
                                    RouterBean routerBean = new RouterBean();
                                    routerBean.type = 9;
                                    routerBean.tbCouponId = item.loopholeList.tbCouponId;
                                    routerBean.mustParam = "type=6"
                                            + "&id=" + (TextUtils.isEmpty(""+item.loopholeList.id) ? "" : item.loopholeList.id)
                                            + "&tbItemId=" + item.loopholeList.tbItemId;
                                    LinkRouterUtils.getInstance().setRouterBean(TmallSupermarketActivity.this, routerBean);
                                } catch (Exception e) {
                                    toast(e.toString());
                                }
                            }
                        } );

                    }else if (helper.getItemViewType() == TmallSupermarketEntity.TYPE_PRODUCTTHREE){
                        ImageView imageView=helper.getView( R.id.iv_item_all_product_tupian );
                        Glide.with(BaseApplication.getApplication())
                                .load(item.img)
                                .placeholder(R.drawable.ic_common_user_def)
                                .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                .into(imageView);
                        TextView title=helper.getView( R.id.tv_item_fans_username );
//                        helper.setText( R.id.tv_item_fans_username,item.name );
                        title.setText( item.itemName );
                        RelativeLayout rlall=helper.getView( R.id.rl_all );
                        final RelativeLayout one=helper.getView( R.id.ll_one );
                        final RelativeLayout two=helper.getView( R.id.ll_two );
                        final RelativeLayout three=helper.getView( R.id.ll_three );
                        final RelativeLayout four=helper.getView( R.id.ll_four );
                        final ImageView imageView3=helper.getView( R.id.iv_jiantou1 );
                        final ImageView imageView2=helper.getView( R.id.iv_jiantou2 );
//                        showOne.put( ""+item.itemNum,View.GONE );
//                        showTwo.put( ""+item.itemNum,View.GONE );
//                        showThree.put( ""+item.itemNum,View.GONE );
//                        showFour.put( ""+item.itemNum,View.GONE );
                        one.setVisibility( showOne.get( ""+item.itemNum ) );
                        two.setVisibility( showTwo.get( ""+item.itemNum ) );
                        three.setVisibility( showThree.get( ""+item.itemNum ) );
                        four.setVisibility( showFour.get( ""+item.itemNum ) );
                        imageView3.setVisibility( showFive.get( ""+item.itemNum ) );
                        imageView2.setVisibility( showOne.get( ""+item.itemNum ) );
//                        if (showFive.get( ""+item.itemNum )==1){
//                            imageView2.setBackground( getResources().getDrawable( R.drawable.img_tmall_youjiantou ) );//向右
//                        }else if (showFive.get( ""+item.itemNum )==0){
//                            imageView2.setBackground( getResources().getDrawable( R.drawable.img_tmall_xiajiantou ) );//向下
//                        }
                        rlall.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //判断列表是否返回
                                if (item.nameFour!=null){
                                    if (two.getVisibility()==View.VISIBLE){
                                        one.setVisibility( View.GONE );
                                        two.setVisibility( View.GONE );
                                        three.setVisibility( View.GONE );
                                        four.setVisibility( View.GONE );
                                        showOne.put( ""+item.itemNum,View.GONE );
                                        showTwo.put( ""+item.itemNum,View.GONE );
                                        showThree.put( ""+item.itemNum,View.GONE );
                                        showFour.put( ""+item.itemNum,View.GONE );
                                        imageView3.setVisibility( View.VISIBLE );
                                        imageView2.setVisibility( View.GONE );
                                        showFive.put( ""+item.itemNum,View.VISIBLE );
                                    }else {
                                        one.setVisibility( View.VISIBLE );
                                        two.setVisibility( View.VISIBLE );
                                        three.setVisibility( View.VISIBLE );
                                        four.setVisibility( View.VISIBLE );
                                        showOne.put( ""+item.itemNum,View.VISIBLE );
                                        showTwo.put( ""+item.itemNum,View.VISIBLE );
                                        showThree.put( ""+item.itemNum,View.VISIBLE );
                                        showFour.put( ""+item.itemNum,View.VISIBLE );
                                        imageView3.setVisibility( View.GONE );
                                        imageView2.setVisibility( View.VISIBLE );
                                        showFive.put( ""+item.itemNum,View.GONE );
                                    }
                                }else
                                if (item.nameThree!=null){
                                    if (two.getVisibility()==View.VISIBLE){
                                        one.setVisibility( View.GONE );
                                        two.setVisibility( View.GONE );
                                        three.setVisibility( View.GONE );
                                        showOne.put( ""+item.itemNum,View.GONE );
                                        showTwo.put( ""+item.itemNum,View.GONE );
                                        showThree.put( ""+item.itemNum,View.GONE );
                                        imageView3.setVisibility( View.VISIBLE );
                                        imageView2.setVisibility( View.GONE );
                                        showFive.put( ""+item.itemNum,View.VISIBLE );
                                    }else {
                                        one.setVisibility( View.VISIBLE );
                                        two.setVisibility( View.VISIBLE );
                                        three.setVisibility( View.VISIBLE );
                                        showOne.put( ""+item.itemNum,View.VISIBLE );
                                        showTwo.put( ""+item.itemNum,View.VISIBLE );
                                        showThree.put( ""+item.itemNum,View.VISIBLE );
                                        imageView3.setVisibility( View.GONE );
                                        imageView2.setVisibility( View.VISIBLE );
                                        showFive.put( ""+item.itemNum,View.GONE );
                                    }
                                }else
                                if (item.nameTwo!=null){
                                    if (two.getVisibility()==View.VISIBLE){
                                        one.setVisibility( View.GONE );
                                        two.setVisibility( View.GONE );
                                        showOne.put( ""+item.itemNum,View.GONE );
                                        showTwo.put( ""+item.itemNum,View.GONE );
                                        imageView3.setVisibility( View.VISIBLE );
                                        imageView2.setVisibility( View.GONE );
                                        showFive.put( ""+item.itemNum,View.VISIBLE );
                                    }else {
                                        one.setVisibility( View.VISIBLE );
                                        two.setVisibility( View.VISIBLE );
                                        showOne.put( ""+item.itemNum,View.VISIBLE );
                                        showTwo.put( ""+item.itemNum,View.VISIBLE );
                                        imageView3.setVisibility( View.GONE );
                                        imageView2.setVisibility( View.VISIBLE );
                                        showFive.put( ""+item.itemNum,View.GONE );
                                    }
                                }else
                                if (item.nameOne!=null){
                                    if (one.getVisibility()==View.VISIBLE){
                                        one.setVisibility( View.GONE );
                                        showOne.put( ""+item.itemNum,View.GONE );
                                        imageView3.setVisibility( View.VISIBLE );
                                        imageView2.setVisibility( View.GONE );
                                        showFive.put( ""+item.itemNum,View.VISIBLE );
                                    }else {
                                        one.setVisibility( View.VISIBLE );
                                        showOne.put( ""+item.itemNum,View.VISIBLE );
                                        imageView3.setVisibility( View.GONE );
                                        imageView2.setVisibility( View.VISIBLE );
                                        showFive.put( ""+item.itemNum,View.GONE );
                                    }
                                }else {
                                    //直接跳转
                                    ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_YHJ).withString( "name","美团" )
                                            .withInt( "couponTypeId", item.ids )
                                            .withInt( "appTypeId",0 ).navigation();
                                }
                            }
                        } );
                        if (item.nameOne!=null){
                            //判断列表数量大于1
                            if (item.nameOne!=null){
                                //图片
                                ImageView imageView1=helper.getView( R.id.iv_photo_one );
                                if (item.imgOne!=null){
                                    Glide.with(BaseApplication.getApplication())
                                            .load(item.imgOne)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }else {
                                    Glide.with(BaseApplication.getApplication())
                                            .load(R.drawable.ic_common_user_def)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }

                                //名称
                                helper.setText( R.id.tv_title_one,item.nameOne );
                                one.setOnClickListener( new OnClickEvent() {
                                    @Override
                                    public void singleClick(View v) {
                                        ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_YHJ).withString( "name",item.nameOne )
                                                .withInt( "couponTypeId",item.superIdOne )
                                                .withInt( "appTypeId",item.idOne )
                                                .navigation();
                                    }
                                } );
                            }
                            //判断列表数量大于2
                            if (item.nameTwo!=null){
                                //图片
                                ImageView imageView1=helper.getView( R.id.iv_photo_two );
                                if (item.imgTwo!=null){
                                    Glide.with(BaseApplication.getApplication())
                                            .load(item.imgTwo)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }else {
                                    Glide.with(BaseApplication.getApplication())
                                            .load(R.drawable.ic_common_user_def)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }

                                //名称
                                helper.setText( R.id.tv_title_two,item.nameTwo );
                                two.setOnClickListener( new OnClickEvent() {
                                    @Override
                                    public void singleClick(View v) {
                                        ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_YHJ).withString( "name",item.nameTwo )
                                                .withInt( "couponTypeId",item.superIdTwo )
                                                .withInt( "appTypeId",item.idTwo )
                                                .navigation();
                                    }
                                } );
                            }


                            //判断列表数量大于3
                            if (item.nameThree!=null){
                                //图片
                                ImageView imageView1=helper.getView( R.id.iv_photo_three );
                                if (item.imgThree!=null){
                                    Glide.with(BaseApplication.getApplication())
                                            .load(item.imgThree)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }else {
                                    Glide.with(BaseApplication.getApplication())
                                            .load(R.drawable.ic_common_user_def)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }
                                //名称
                                helper.setText( R.id.tv_title_three,item.nameThree );
                                three.setOnClickListener( new OnClickEvent() {
                                    @Override
                                    public void singleClick(View v) {
                                        ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_YHJ).withString( "name",item.nameThree )
                                                .withInt( "couponTypeId",item.superIdThree )
                                                .withInt( "appTypeId",item.idThree )
                                                .navigation();
                                    }
                                } );
                            }

                            //判断列表数量大于4
                            if (item.nameFour!=null){
                                //图片
                                ImageView imageView1=helper.getView( R.id.iv_photo_four );
                                if (item.imgFour!=null){
                                    Glide.with(BaseApplication.getApplication())
                                            .load(item.imgFour)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }else {
                                    Glide.with(BaseApplication.getApplication())
                                            .load(R.drawable.ic_common_user_def)
                                            .placeholder(R.drawable.ic_common_user_def)
                                            .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                            .into(imageView1);
                                }

                                //名称
                                helper.setText( R.id.tv_title_four,item.nameFour );
                                four.setOnClickListener( new OnClickEvent() {
                                    @Override
                                    public void singleClick(View v) {
                                        ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET_YHJ).withString( "name",item.nameFour )
                                                .withInt( "couponTypeId",item.superIdFour )
                                                .withInt( "appTypeId",item.idFour )
                                                .navigation();
                                    }
                                } );
                            }

                        }
                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( TmallSupermarketActivity.this, 1 );
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
    private void getMinute(final String title, int id) {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id);
        allParam.put("isEncode", 1);
        NetWork.getInstance()
                .setTag( Qurl.tmallSuperMarketTmallDetail)
                .getApiService(ModuleApi.class)
                .tmallSuperMarketTmallDetail(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TryRuleBean> (TmallSupermarketActivity.this,true,false) {

                    @Override
                    public void onSuccess(TryRuleBean result) {
                        if (result==null){
                            return;
                        }
                        try{
                            String html= URLDecoder.decode( result.content,"UTF-8" );
                            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                    .withString("minute", html)
                                    .withString( Constant.TITLE, title)
//                                    .withInt(Constant.TYPE, 4)
                                    .navigation();
                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }

                    }
                });
    }

    //设置Banner
    private List<String> mBannderData = new ArrayList<>();
    private void setBannerData(final BaseViewHolder helper, final TmallSupermarketEntity list){
        MZBannerView bannerView = helper.getView(R.id.banner_all);
        //正事
        if (list.mBanners == null || list.mBanners.bannerList.size() <= 0) {

        }else {
            BannerBgContainer bannerBgContainer = mBannerBgContainer;
            if (mBannderData == null) {
                mBannderData = new ArrayList<>();
            }
            mBannderData.clear();
            for (int i = 0; i < list.mBanners.bannerList.size(); i++) {
                mBannderData.add(list.mBanners.bannerList.get(i).img);
            }
            bannerView.setBannerBgContainer(bannerBgContainer);
            bannerView.setPages(mBannderData, new MZHolderCreator<BannerViewHolder>() {
                @Override
                public BannerViewHolder createViewHolder() {
                    return new BannerViewHolder();
                }
            });
            bannerView.setIndicatorVisible( true );
            bannerView.setIndicatorNumVisible( false );
            bannerView.start();
        }

        final TextView name=helper.getView( R.id.tv_change );
        //点击事件
        helper.getView( R.id.ll_tianmao ).setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                name.setText( "天猫超市");
                mTvTitle.setText("淘宝福利专区");
                mSmartRefreshLayout.setEnableLoadMore ( true );
                if (num==1){

                }else {
                    page=1;
                    itemNum=0;
                    updataAlipay();
                    setRefreshLoadMoreState ( true, false,1 );
                }
            }
        } );
        helper.getView( R.id.ll_loudong ).setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                name.setText( "漏洞单");
                mTvTitle.setText("淘宝福利专区");
                mSmartRefreshLayout.setEnableLoadMore ( true );
                if (num==2){

                }else {
                    page=1;
                    itemNum=0;
                    updataAlipaytwo();
                    setRefreshLoadMoreState ( true, false,1 );
                }
            }
        } );
        helper.getView( R.id.ll_haojuan ).setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                name.setText( "好劵专区");
                mTvTitle.setText("淘宝福利专区");
//                mSmartRefreshLayout.setEnableLoadMore ( false );
                if (num==3){

                }else {
                    page=1;
                    itemNum=0;
                    updataAlipayThree();
                    setRefreshLoadMoreState ( true, false,1 );
                }
            }
        } );

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
////        getAllData();
//        if (refresh){
//            mSmartRefreshLayout.autoRefresh(500);
//            refresh=false;
//        }
//    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (mHomeRvAdapter != null) {
            page++;
            if (num==1){
                updataAlipay ();
            }else if (num==2){
                updataAlipaytwo();
            }else if (num==3){
                updataAlipayThree();
            }
        } else {
            page = 1;
            mSmartRefreshLayout.finishLoadMore(100);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        itemNum=0;
        if (num==1||num==0){
            updataAlipay ();
        }else if (num==2){
            updataAlipaytwo();
        }else if (num==3){
            updataAlipayThree();
        }
    }
    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData,int num) {
        if (num==1){
            mSmartRefreshLayout.setNoMoreData( false );
        }else if (page == 1) {
            mSmartRefreshLayout.finishRefresh ( state );
        } else if (noMoreData) {
            mSmartRefreshLayout.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartRefreshLayout.finishLoadMore ( state );
        }

//        mSmartRefreshLayout.finishRefresh ( state );

    }
}
