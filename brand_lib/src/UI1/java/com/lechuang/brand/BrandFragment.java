package java.com.lechuang.brand;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.StringUtils;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.SquareImageView;
import com.lechuang.brand.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.com.lechuang.brand.adapter.BannerViewHolder;
import java.com.lechuang.brand.adapter.MZBannerViewHolder;
import java.com.lechuang.brand.bean.BrandBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_BRAND)
public class BrandFragment extends BaseFragment implements OnRefreshLoadMoreListener {

    @Autowired(name = Constant.TITLE)
    public String title;//头部
    private ImageView mTvBrandGo;
//    private TextView mIvCommonTitle;
    private MZBannerView mBannerBrand, mBannerRenQi, mBannerDaPai;
    private RecyclerView mRvBrandProduct, mRvBrandReMai;
    private SmartRefreshLayout mSmartBrand;
    private int page = 1;//页数
    private TextView mTvRenQiTuiJian, mTvBrandReMai,mTvBrandName,mTvBrandSlogan;
    private LinearLayout mLlBrandJingxuan, mTvDaMai;
    private RelativeLayout mRlBrandDibu;
    private int shuaxin = 11;
    private LinearLayout mNetError;
    private boolean mload = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_brand;
    }

    @Override
    protected void findViews() {

//        mIvCommonBack = $(R.id.iv_common_back);
//        mIvCommonBack.setVisibility(View.GONE);
//        mIvCommonTitle = $(R.id.iv_common_title);
        mBannerBrand = $(R.id.banner_brand);
        mBannerRenQi = $(R.id.banner_brand_renqi);
        mBannerDaPai = $(R.id.banner_brand_dapai);
        mTvRenQiTuiJian = $(R.id.tv_renqi_tuijian);
        mTvBrandReMai = $(R.id.tv_brand_hot);
        mTvDaMai = $(R.id.tv_dapai);
        mRvBrandProduct = $(R.id.rv_brand_product);
        mRvBrandReMai = $(R.id.rv_brand_hot);
        mSmartBrand = $(R.id.smart_brand);
        mTvBrandName = $(R.id.tv_brand_mingcheng);
        mTvBrandSlogan = $(R.id.tv_brand_slogan);
        mTvBrandGo = $(R.id.iv_brand_go);
        mLlBrandJingxuan = $(R.id.ll_brand_weini_jingxuan);
        mRlBrandDibu = $(R.id.rl_brand_dibu);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_fans ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "还没有粉丝呢,继续加油咯!" );

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
//        mIvCommonTitle.setText("品牌闪购");

        mSmartBrand.setOnRefreshLoadMoreListener(this);
        mRvBrandProduct.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    /*if (firstVisibleItemPosition == 0) {
                        mIvHomeAllTop.setVisibility(View.INVISIBLE);
                    } else {
                        mIvHomeAllTop.setVisibility(View.VISIBLE);
                    }*/

                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    if (lastItemPosition > shuaxin) {
                        if (mBaseBrandSellerQuickAdapter != null) {
                            page++;
                            getBrandJingXuanData();
                            shuaxin = shuaxin + 20;
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void getData() {
        mSmartBrand.autoRefresh(100);

    }

    /**
     * 底部刷新数据
     */
    private void getBrandJingXuanData(){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag(Qurl.brandShowAll)
                .getApiService(BrandApi.class)
                .brandShowAll(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<BrandBean>(getActivity(), true, false) {

                    @Override
                    public void onSuccess(BrandBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        //设置为你精选数据
                        setBottomDianPu(result.brandSellerList);
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

    /**
     * 首次获取数据接口
     */
    private void getBrandData() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag(Qurl.brandListShow)
                .getApiService(BrandApi.class)
                .brandListShow(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<BrandBean>(getActivity(), true, false) {

                    @Override
                    public void onSuccess(BrandBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);

                        //设置banner
                        setBannerData(result.brandBannerList);
                        //设置人气banner
//                        setRenQiData(result.productBannerList);

                        //设置品牌热卖banner
                        setBrandData(result.advertName,result.advertProductList);

                        //设置大牌banner
                        setDaPaiData(result.todayHotSale);
                        if (result.brandSellerList.size()>0){
                            mLlBrandJingxuan.setVisibility( View.VISIBLE );
                            mRlBrandDibu.setVisibility( View.VISIBLE );
                        }else {
                            mLlBrandJingxuan.setVisibility( View.GONE );
                            mRlBrandDibu.setVisibility( View.GONE );
                        }
                        //设置为你精选数据
                        setBottomDianPu(result.brandSellerList);

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }
                });
    }

    /**
     * 设置顶部的banner图
     *
     * @param brandBannerList
     */
    private List<String> mBannderData = new ArrayList<>();

    private void setBannerData(final List<BrandBean.BrandBannerListBean> brandBannerList) {

        if (brandBannerList == null || brandBannerList.size() <= 0) {
            mBannerBrand.setVisibility(View.GONE);
            mNetError.setVisibility ( View.VISIBLE );
            return;
        }
        mBannerBrand.setVisibility(View.VISIBLE);
        mNetError.setVisibility ( View.GONE );

        if (mBannderData == null) {
            mBannderData = new ArrayList<>();
        }
        mBannderData.clear();
        for (BrandBean.BrandBannerListBean bannerListBean : brandBannerList) {
            mBannderData.add(bannerListBean.img);
        }
        mBannerBrand.setIndicatorVisible(false);

        mBannerBrand.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                BrandBean.BrandBannerListBean indexBannerListBean = brandBannerList.get(i);
                RouterBean routerBean = new RouterBean();
                routerBean.img = indexBannerListBean.img;
                routerBean.link = indexBannerListBean.link;
                routerBean.type = indexBannerListBean.type;
                routerBean.programaId = indexBannerListBean.programaId + "";
                routerBean.mustParam = indexBannerListBean.mustParam;
                routerBean.attachParam = indexBannerListBean.attachParam;
                routerBean.rootName = indexBannerListBean.rootName;
                routerBean.obJump = indexBannerListBean.obJump;
                routerBean.linkAllows = indexBannerListBean.linkAllows;
                routerBean.commandCopy = indexBannerListBean.commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
            }
        });
//        mBannerBrand.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);
        mBannerBrand.setPages(mBannderData, new MZHolderCreator<MZBannerViewHolder>() {
            @Override
            public MZBannerViewHolder createViewHolder() {
                return new MZBannerViewHolder();
            }
        });

        mBannerBrand.start();

    }

    /**
     * 设置人气banner图
     *
     * @param productBannerList
     */
    private List<String> mProductBannderData = new ArrayList<>();

    private void setRenQiData(final List<BrandBean.ProductBannerListBean> productBannerList) {
        if (productBannerList == null || productBannerList.size() <= 0) {
            mBannerRenQi.setVisibility(View.GONE);
            mTvRenQiTuiJian.setVisibility(View.GONE);
            return;
        }
        mBannerRenQi.setVisibility(View.VISIBLE);
        mTvRenQiTuiJian.setVisibility(View.VISIBLE);

        if (mProductBannderData == null) {
            mProductBannderData = new ArrayList<>();
        }
        mProductBannderData.clear();
        for (BrandBean.ProductBannerListBean bannerListBean : productBannerList) {
            mProductBannderData.add(bannerListBean.img);
        }

        mBannerRenQi.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                BrandBean.ProductBannerListBean indexBannerListBean = productBannerList.get(i);
                RouterBean routerBean = new RouterBean();
                routerBean.img = indexBannerListBean.img;
                routerBean.link = indexBannerListBean.link;
                routerBean.type = indexBannerListBean.type;
                routerBean.programaId = indexBannerListBean.programaId + "";
                routerBean.mustParam = indexBannerListBean.mustParam;
                routerBean.attachParam = indexBannerListBean.attachParam;
                routerBean.rootName = indexBannerListBean.rootName;
                routerBean.obJump = indexBannerListBean.obJump;
                routerBean.linkAllows = indexBannerListBean.linkAllows;
                routerBean.commandCopy = indexBannerListBean.commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
            }
        });
        mBannerRenQi.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);
        mBannerRenQi.setPages(mProductBannderData, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        mBannerRenQi.start();
    }

    /**
     * 设置品牌热卖的banner图
     *
     * @param advertProductList
     */
    private List<BrandBean.AdvertProductListBean> mAdvertProductList;
    private BaseQuickAdapter<BrandBean.AdvertProductListBean, BaseViewHolder> mBaseAdvertProductQuickAdapter;

    private void setBrandData(String advertName,List<BrandBean.AdvertProductListBean> advertProductList) {
        if (advertProductList == null || advertProductList.size() <= 0) {
            mTvBrandReMai.setVisibility(View.GONE);
            mRvBrandReMai.setVisibility(View.GONE);
            return;
        }
        mRvBrandReMai.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(advertName)){
            mTvBrandReMai.setText(advertName);
            mTvBrandReMai.setVisibility(View.VISIBLE);
        }else {
            mTvBrandReMai.setVisibility(View.GONE);
        }

        if (mAdvertProductList == null) {
            mAdvertProductList = new ArrayList<>();
        }
        mAdvertProductList.clear();
        mAdvertProductList.addAll(advertProductList);
        if (mBaseAdvertProductQuickAdapter == null) {
            mBaseAdvertProductQuickAdapter = new BaseQuickAdapter<BrandBean.AdvertProductListBean, BaseViewHolder>(R.layout.item_brand_hot, mAdvertProductList) {
                @Override
                protected void convert(BaseViewHolder helper, BrandBean.AdvertProductListBean item) {
                    try {

                        //商品图片
                        ImageView ivItemAllBrandHot = helper.getView(R.id.tv_item_brand_hot_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.imgs).into(ivItemAllBrandHot);
                        //标题
                        helper.setText(R.id.tv_item_brand_hot_biaoti, item.name);

                        //券金额

                        helper.getView(R.id.ll_brand_quan_parent).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_brand_hot_quan, "¥" + StringUtils.doubleToStringDeleteZero(item.couponMoney) + "");

                        //券后价（现价）
                        helper.setText(R.id.tv_item_brand_hot_quanhoujia, "券后价¥ " + StringUtils.stringToStringDeleteZero(item.preferentialPrice) + "");


                    } catch (Exception e) {

                    }


                }
            };
            mRvBrandReMai.setHasFixedSize(true);
            mRvBrandReMai.setNestedScrollingEnabled(false);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            mRvBrandReMai.setLayoutManager(gridLayoutManager);

            mRvBrandReMai.setAdapter(mBaseAdvertProductQuickAdapter);
            //今日爆款点击事件 todo
            mBaseAdvertProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    ARouter.getInstance().build(ARouters.PATH_PRODUCT)
//                            .withString(Constant.CHASS_TYPE_ID, mAdvertProductList.get(position).name)
//                            .withString(Constant.TITLE, mAdvertProductList.get(position).rootName)
//                            .navigation();
                    RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.t = "1";
                    routerBean.tbCouponId = mAdvertProductList.get(position).tbCouponId;
                    routerBean.id = mAdvertProductList.get(position).id;
                    routerBean.i = mAdvertProductList.get(position).tbItemId;

                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                }
            });
        } else {
            mBaseAdvertProductQuickAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置大牌
     *
     * @param todayHotSale
     */
    private List<String> mDaPaiBannderData = new ArrayList<>();

    private void setDaPaiData(final BrandBean.TodayHotSaleBean todayHotSale) {
        if (todayHotSale == null) {
            mBannerDaPai.setVisibility(View.GONE);
            mTvDaMai.setVisibility(View.GONE);
            return;
        }
        mBannerDaPai.setVisibility(View.VISIBLE);
        mTvDaMai.setVisibility(View.VISIBLE);

        if (mDaPaiBannderData == null) {
            mDaPaiBannderData = new ArrayList<>();
        }
        mDaPaiBannderData.clear();
        mDaPaiBannderData.add(todayHotSale.img);
        mTvBrandSlogan.setText( todayHotSale.slogan );
        mTvBrandName.setText( todayHotSale.brandName );
        mRlBrandDibu.setVisibility( View.VISIBLE );
        mTvBrandGo.setOnClickListener(
                new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                RouterBean routerBean = new RouterBean();
                routerBean.img = todayHotSale.img;
                routerBean.link = todayHotSale.link;
                routerBean.type = todayHotSale.type;
                routerBean.programaId = todayHotSale.programaId + "";
                routerBean.mustParam = todayHotSale.mustParam;
                routerBean.attachParam = todayHotSale.attachParam;
                routerBean.rootName = todayHotSale.rootName;
                routerBean.obJump = todayHotSale.obJump;
                routerBean.linkAllows = todayHotSale.linkAllows;
                routerBean.commandCopy = todayHotSale.commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
            }
        } );


        mBannerDaPai.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                RouterBean routerBean = new RouterBean();
                routerBean.img = todayHotSale.img;
                routerBean.link = todayHotSale.link;
                routerBean.type = todayHotSale.type;
                routerBean.programaId = todayHotSale.programaId + "";
                routerBean.mustParam = todayHotSale.mustParam;
                routerBean.attachParam = todayHotSale.attachParam;
                routerBean.rootName = todayHotSale.rootName;
                routerBean.obJump = todayHotSale.obJump;
                routerBean.linkAllows = todayHotSale.linkAllows;
                routerBean.commandCopy = todayHotSale.commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
            }
        });
        mBannerDaPai.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);

        mBannerDaPai.setIndicatorVisible(mDaPaiBannderData.size() > 1 ? true : false);

        mBannerDaPai.setPages(mDaPaiBannderData, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        mBannerDaPai.start();
    }

    /**
     * 设置店铺数据
     *
     * @param brandSellerList
     */
    private List<BrandBean.BrandSellerListBean> mBrandSellerList;
    private BaseQuickAdapter<BrandBean.BrandSellerListBean, BaseViewHolder> mBaseBrandSellerQuickAdapter;
    private void setBottomDianPu(List<BrandBean.BrandSellerListBean> brandSellerList) {
        //||brandSellerList.get( 0 ).brandProduct==null
        if (brandSellerList == null || brandSellerList.size() <= 0) {
            if (mload){
                mLlBrandJingxuan.setVisibility( View.GONE );
                mRvBrandProduct.setVisibility( View.GONE );
            }
            return;
        }
        if (mBrandSellerList == null) {
            mBrandSellerList = new ArrayList<>();
        }
        if (page == 1){
            mBrandSellerList.clear();
        }
        for (int i=0;i<brandSellerList.size();i++ ){
            if (brandSellerList.get( i ).brandProduct!=null){
                mBrandSellerList.add( brandSellerList.get( i ) );
            }
        }
        mRvBrandProduct.setVisibility( View.VISIBLE );
//        mBrandSellerList.addAll(brandSellerList);
        if (mBaseBrandSellerQuickAdapter == null) {
            mBaseBrandSellerQuickAdapter = new BaseQuickAdapter<BrandBean.BrandSellerListBean, BaseViewHolder>(R.layout.item_brand_dianpu, mBrandSellerList) {
                @Override
                protected void convert(BaseViewHolder helper, final BrandBean.BrandSellerListBean item) {
                    try {
                        /*if (item.brandProduct==null){
                            helper.getView( R.id.ll_brand_item_all ).setVisibility( View.GONE );
                            return;
                        }else if (item.brandProduct.size()==1){
                            helper.getView( R.id.tv_item_brand_dianpu_2 ).setVisibility( View.VISIBLE );
                            helper.getView( R.id.tv_item_brand_dianpu_3 ).setVisibility( View.VISIBLE );
                        }else if (item.brandProduct.size()==2){
                            helper.getView( R.id.tv_item_brand_dianpu_3 ).setVisibility( View.VISIBLE );
                        }*/
                        //店铺名
                        helper.setText(R.id.tv_item_brand_dianpu_name, item.title);
                        //给标题添加背景图片
                        ImageView imageView = helper.getView( R.id.img_bg );
                        Glide.with ( BaseApplication.getApplication () ).load ( item.outImg ).into ( imageView );
//                        Glide.with ( BaseApplication.getApplication () ).load ( item.brandProduct.get(0).img ).into ( imageView );
                        //店铺宣传提示
                        helper.setText(R.id.tv_item_brand_dianpu_hint, item.slogan);
//                        ImageView imageViewTitle = helper.getView ( R.id.tv_item_brand_dianpu_tupian );
//                        Glide.with ( BaseApplication.getApplication () ).load ( item.brandProduct.get(0).img ).placeholder ( R.drawable.bg_common_img_null ).into ( imageViewTitle );
                        //店铺商品图片
                        /*SquareImageView ivItemDianPuTuPian1 = helper.getView(R.id.tv_item_brand_dianpu_tupian1);
                        Glide.with(BaseApplication.getApplication()).load(item.brandProduct.get(0).img)
                                .transform(new GlideRoundTransform(BaseApplication.getApplication(), 5)).into(ivItemDianPuTuPian1);
                        //商品标题
                        helper.setText(R.id.tv_item_brand_dianpu_biaoti1, item.brandProduct.get(0).name);
                        //券后价（现价）
                        helper.setText(R.id.tv_item_brand_dianpu_quanhoujia1, "券后价¥ " + StringUtils.stringToStringDeleteZero(item.brandProduct.get(0).preferentialPrice) + "");

                        //店铺商品图片
                        SquareImageView ivItemDianPuTuPian2 = helper.getView(R.id.tv_item_brand_dianpu_tupian2);
                        Glide.with(BaseApplication.getApplication()).load(item.brandProduct.get(1).img)
                                .transform(new GlideRoundTransform(BaseApplication.getApplication(), 5)).into(ivItemDianPuTuPian2);
                        //商品标题
                        helper.setText(R.id.tv_item_brand_dianpu_biaoti2, item.brandProduct.get(1).name);
                        //券后价（现价）
                        helper.setText(R.id.tv_item_brand_dianpu_quanhoujia2, "券后价¥ " + StringUtils.stringToStringDeleteZero(item.brandProduct.get(1).preferentialPrice) + "");

                        //店铺商品图片
                        SquareImageView ivItemDianPuTuPian3 = helper.getView(R.id.tv_item_brand_dianpu_tupian3);
                        Glide.with(BaseApplication.getApplication()).load(item.brandProduct.get(2).img)
                                .transform(new GlideRoundTransform(BaseApplication.getApplication(), 5)).into(ivItemDianPuTuPian3);
                        //商品标题
                        helper.setText(R.id.tv_item_brand_dianpu_biaoti3, item.brandProduct.get(2).name);
                        //券后价（现价）
                        helper.setText(R.id.tv_item_brand_dianpu_quanhoujia3, "券后价¥ " + StringUtils.stringToStringDeleteZero(item.brandProduct.get(2).preferentialPrice) + "");*/

//                        ivItemDianPuTuPian1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                RouterBean routerBean = new RouterBean();
//                                routerBean.type = 9;
//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(0).id;
//                                routerBean.i = item.brandProduct.get(0).tbItemId;
//
//                                LinkRouterUtils.getInstance().setRouterBean(getActivity(),routerBean);
//                            }
//                        });
//
//                        ivItemDianPuTuPian2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                RouterBean routerBean = new RouterBean();
//                                routerBean.type = 9;
//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(1).id;
//                                routerBean.i = item.brandProduct.get(1).tbItemId;
//
//                                LinkRouterUtils.getInstance().setRouterBean(getActivity(),routerBean);
//                            }
//                        });
//
//                        ivItemDianPuTuPian3.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                RouterBean routerBean = new RouterBean();
//                                routerBean.type = 9;
//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(2).id;
//                                routerBean.i = item.brandProduct.get(2).tbItemId;
//
//                                LinkRouterUtils.getInstance().setRouterBean(getActivity(),routerBean);
//                            }
//                        });
//
                        helper.getView(R.id.tv_item_brand_dianpu_more).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //自定义跳转类型，非后台传值
                                RouterBean routerBean = new RouterBean();
                                routerBean.type = 24;//品牌详情
                                routerBean.typeBrand = item.type;//这个参数不是后台用作路由参数，该参数需要转递到下个界面
                                routerBean.mustParam = "id=" + item.id;//店铺id，非商品id
                                routerBean.pname = item.title;

                                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                            }
                        });
                        helper.getView(R.id.ll_brand_item_title).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //自定义跳转类型，非后台传值
                                RouterBean routerBean = new RouterBean();
                                routerBean.type = 24;//品牌详情
                                routerBean.typeBrand = item.type;//这个参数不是后台用作路由参数，该参数需要转递到下个界面
                                routerBean.mustParam = "id=" + item.id;//店铺id，非商品id
                                routerBean.pname = item.title;

                                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                            }
                        });
                        //店铺商品图片
                        SquareImageView ivItemDianPuTuPian1 = helper.getView(R.id.tv_item_brand_dianpu_tupian1);
                        Glide.with(BaseApplication.getApplication()).load(item.brandProduct.get(0).img)
                                /*.transform(new GlideRoundTransform(BaseApplication.getApplication(), 5))*/.into(ivItemDianPuTuPian1);
                        //商品标题
                        helper.setText(R.id.tv_item_brand_dianpu_biaoti1, item.brandProduct.get(0).productName);
                        //券后价（现价）
                        helper.setText(R.id.tv_item_brand_dianpu_quanhoujia1, "" + StringUtils.stringToStringDeleteZero(item.brandProduct.get(0).preferentialPrice) + "");
                        //图片一的点击事件
                        ivItemDianPuTuPian1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BrandBean.BrandSellerListBean.BrandProductBean brandProductBean = item.brandProduct.get(0);
                                if (brandProductBean == null){
                                    return;
                                }
                                RouterBean routerBean = new RouterBean();
                                routerBean.type = 9;
                                routerBean.mustParam = "type=1"
                                        + "&id=" + item.brandProduct.get(0).id
                                        + "&tbItemId=" + item.brandProduct.get(0).tbItemId;

//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(0).id;
//                                routerBean.i = item.brandProduct.get(0).tbItemId;

                                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                            }
                        });
                        /*
                        if (item.brandProduct.size()==1){
                            helper.getView( R.id.tv_item_brand_dianpu_3 ).setVisibility( View.INVISIBLE );
                            helper.getView( R.id.tv_item_brand_dianpu_2 ).setVisibility( View.INVISIBLE );
                        }else if (item.brandProduct.size()==2){
                            helper.getView( R.id.tv_item_brand_dianpu_3 ).setVisibility( View.INVISIBLE );
                        }*/

                        if (item.brandProduct.size()>1&&!TextUtils.isEmpty( item.brandProduct.get(1).productName )){
                            helper.getView( R.id.tv_item_brand_dianpu_2 ).setVisibility( View.VISIBLE );
                            //店铺商品图片
                            SquareImageView ivItemDianPuTuPian2 = helper.getView(R.id.tv_item_brand_dianpu_tupian2);
                            Glide.with(BaseApplication.getApplication()).load(item.brandProduct.get(1).img)
                                    /*.transform(new GlideRoundTransform(BaseApplication.getApplication(), 5))*/.into(ivItemDianPuTuPian2);
//                        helper.getView(R.id.ll_item_brand_juanhoujia2).setVisibility(TextUtils.isEmpty(item.brandProduct.get(1).preferentialPrice) ? View.GONE : View.VISIBLE);
                            //商品标题
                            helper.setText(R.id.tv_item_brand_dianpu_biaoti2, item.brandProduct.get(1).productName);
                            //券后价（现价）
                            helper.setText(R.id.tv_item_brand_dianpu_quanhoujia2, "" + StringUtils.stringToStringDeleteZero(item.brandProduct.get(1).preferentialPrice) + "");
                            //图片二的点击事件
                            ivItemDianPuTuPian2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BrandBean.BrandSellerListBean.BrandProductBean brandProductBean = item.brandProduct.get(1);
                                    if (brandProductBean == null){
                                        return;
                                    }
                                    RouterBean routerBean = new RouterBean();
                                    routerBean.type = 9;
                                    routerBean.mustParam = "type=1"
                                            + "&id=" + item.brandProduct.get(1).id
                                            + "&tbItemId=" + item.brandProduct.get(1).tbItemId;
//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(1).id;
//                                routerBean.i = item.brandProduct.get(1).tbItemId;

                                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                                }
                            });
                        }else {
                            helper.getView( R.id.tv_item_brand_dianpu_2 ).setVisibility( View.INVISIBLE );
                        }



                        if (item.brandProduct.size()>2&&!TextUtils.isEmpty( item.brandProduct.get( 2 ).productName )){
                            helper.getView( R.id.tv_item_brand_dianpu_3 ).setVisibility( View.VISIBLE );
                            //店铺商品图片
                            SquareImageView ivItemDianPuTuPian3 = helper.getView(R.id.tv_item_brand_dianpu_tupian3);
                            Glide.with(BaseApplication.getApplication()).load(item.brandProduct.get(2).img)
                                    /*.transform(new GlideRoundTransform(BaseApplication.getApplication(), 5))*/.into(ivItemDianPuTuPian3);
//                        helper.getView(R.id.ll_item_brand_juanhoujia3).setVisibility(TextUtils.isEmpty(item.brandProduct.get(2).preferentialPrice) ? View.GONE : View.VISIBLE);
                            //商品标题
                            helper.setText(R.id.tv_item_brand_dianpu_biaoti3, item.brandProduct.get(2).productName);
                            //券后价（现价）
                            helper.setText(R.id.tv_item_brand_dianpu_quanhoujia3, "" + StringUtils.stringToStringDeleteZero(item.brandProduct.get(2).preferentialPrice) + "");
                            //图片三的点击事件
                            ivItemDianPuTuPian3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BrandBean.BrandSellerListBean.BrandProductBean brandProductBean = item.brandProduct.get(2);
                                    if (brandProductBean == null){
                                        return;
                                    }
                                    RouterBean routerBean = new RouterBean();
                                    routerBean.type = 9;
                                    routerBean.mustParam = "type=1"
                                            + "&id=" + item.brandProduct.get(2).id
                                            + "&tbItemId=" + item.brandProduct.get(2).tbItemId;
//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(2).id;
//                                routerBean.i = item.brandProduct.get(2).tbItemId;

                                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                                }
                            });
                        }else {
                            helper.getView( R.id.tv_item_brand_dianpu_3 ).setVisibility( View.INVISIBLE );
                        }

                    } catch (Exception e) {

                    }
                }
            };
            mRvBrandProduct.setHasFixedSize(true);
            mRvBrandProduct.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            mRvBrandProduct.setLayoutManager(gridLayoutManager);

            mRvBrandProduct.setAdapter(mBaseBrandSellerQuickAdapter);
            mBaseBrandSellerQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    /*RouterBean routerBean = new RouterBean();
                    routerBean.type = 23;//品牌详情
                    routerBean.typeBrand = mBrandSellerList.get(position).type;
                    routerBean.id = mBrandSellerList.get(position).id;
                    routerBean.pname = mBrandSellerList.get(position).title;

                    LinkRouterUtils.getInstance().setRouterBean(getActivity(),routerBean);*/
                }
            });
        } else {
            mBaseBrandSellerQuickAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        page++;
        getBrandJingXuanData();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        shuaxin = 11;
        getBrandData();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartBrand.finishRefresh(state);
        } else if (noMoreData) {
            mSmartBrand.finishLoadMoreWithNoMoreData();
        } else {
            mSmartBrand.finishLoadMore(state);
        }
    }

}
