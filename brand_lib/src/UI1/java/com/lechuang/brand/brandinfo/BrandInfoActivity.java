package java.com.lechuang.brand.brandinfo;

import android.graphics.Paint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.Logger;
import com.common.app.utils.StringUtils;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.NoShouYiTiaoJian;
import com.common.app.view.TiaoJianView;
import com.common.app.view.TransChangeNesScrollView;
import com.lechuang.brand.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.com.lechuang.brand.BrandApi;
import java.com.lechuang.brand.adapter.BannerViewHolder;
import java.com.lechuang.brand.bean.BrandInfoBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_BRAND_INFO)
public class BrandInfoActivity extends BaseActivity implements OnRefreshLoadMoreListener, View.OnClickListener {

    @Autowired(name = Constant.id)
    public String id;//界面传递的值，用于请求数据的参数
    @Autowired(name = Constant.TYPE)
    public String type;//界面传递的值，用于请求数据的参数
    @Autowired(name = Constant.TITLE)
    public String title = "";//界面传递的值标题，
    private ImageView mIvCommonBack;
    private TextView mIvCommonTitle;
    private MZBannerView mBannerProduct;
    private NoShouYiTiaoJian mShaiXuanProTop, mShaiXuanProBottom;
    private RecyclerView mRvProduct;
    private TransChangeNesScrollView mNesProduct;
    private SmartRefreshLayout mSmartProduct;
    private LinearLayout mNetError;
    private int page = 1;
    private int mPosition = 0;//表示筛选条件，0为综合，1为价格，2为销量，3为收益
    private boolean mSort = true;//表示价格的排列，true为箭头向上
    private boolean mIsSingleLine = true;//单行和多行切换
    private int shuaxin = 11;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_brand_info;
    }

    @Override
    protected void findViews() {

        mIvCommonBack = $(R.id.iv_common_back);
        mIvCommonTitle = $(R.id.iv_common_title);
        mNesProduct = $(R.id.nes_product);

        mSmartProduct = $(R.id.smart_product);
        mBannerProduct = $(R.id.banner_product);
        mShaiXuanProTop = $(R.id.shaixuan_product_top);
        mShaiXuanProBottom = $(R.id.shaixuan_product_bottom);
        mRvProduct = $(R.id.rv_product);
        mNetError = $(R.id.ll_net_error);

        //排序隐藏
//        mNesProduct.setTopTabLayout(mShaiXuanProBottom,mShaiXuanProTop);

        mIvCommonBack.setOnClickListener(this);


    }

    @Override
    protected void initView() {

        ARouter.getInstance().inject(this);

        mShaiXuanProBottom.setSelectLisenter(new PublicSelecter(mShaiXuanProTop));
        mShaiXuanProTop.setSelectLisenter(new PublicSelecter(mShaiXuanProBottom));
        mSmartProduct.setOnRefreshLoadMoreListener(this);

        mRvProduct.addItemDecoration(new GridItemDecoration(
                new GridItemDecoration.Builder(this)
                        .isExistHead(false)
                        .showHeadDivider(false)
                        .size(5)
        ));
        mRvProduct.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        if (mBaseProductQuickAdapter != null) {
                            page++;
                            getBrandFreshData();
                            shuaxin = shuaxin + 20;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void getData() {
        mSmartProduct.autoRefresh(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBannerProduct != null) {
            mBannerProduct.start();//开始轮播
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBannerProduct != null) {
            mBannerProduct.pause();//暂停轮播
        }
    }

    private void getBrandFreshData() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id + "");
        allParam.put("type", type + "");
        allParam.put("page", page + "");

        /*排序隐藏*/
        /*switch (mPosition) {
            case 0://综合
                allParam.put("isAppraise", "1");
                break;
            case 1://价格
                allParam.put("isPrice", mSort ? 2 + "" : 1 + "");
                break;
            case 2://销量
                allParam.put("isVolume", "1");
                break;
            case 3://收益
                allParam.put("isZhuan", "1");
                break;

        }*/

        NetWork.getInstance()
                .setTag(Qurl.brandProductShow)
                .getApiService(BrandApi.class)
                .brandProductShow(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<BrandInfoBean>(this, true, false) {

                    @Override
                    public void onSuccess(BrandInfoBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        //设置底部商品数据
                        setOtherProductData(result.productList);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (mProductList == null || mProductList.size() <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mProductList == null || mProductList.size() <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    private void getProductList(boolean isShowProgress) {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id + "");
        allParam.put("type", type + "");
        allParam.put("page", page + "");

        /*排序隐藏*/
        /*switch (mPosition) {
            case 0://综合
                allParam.put("isAppraise", "1");
                break;
            case 1://价格
                allParam.put("isPrice", mSort ? 2 + "" : 1 + "");
                break;
            case 2://销量
                allParam.put("isVolume", "1");
                break;
            case 3://收益
                allParam.put("isZhuan", "1");
                break;

        }*/

        NetWork.getInstance()
                .setTag(Qurl.brandDetail)
                .getApiService(BrandApi.class)
                .brandDetail(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<BrandInfoBean>(this, true, isShowProgress) {

                    @Override
                    public void onSuccess(BrandInfoBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);


                        mIvCommonTitle.setText(TextUtils.isEmpty(result.brandTitle) ? title : result.brandTitle);


                        setBannerData(result.brandImg,result.indexBannerList);
//                        setBannerData(result.brandImg,result.indexBannerList);


                        //设置底部商品数据
                        setOtherProductData(result.productList);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (mProductList == null || mProductList.size() <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mProductList == null || mProductList.size() <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    /**
     * 设置banner数据
     *
     * @param indexBannerList
     */
    private List<String> mBannderData = new ArrayList<>();

    private void setBannerData(String brandImg, List<BrandInfoBean.IndexBannerListBean> indexBannerList) {
        if (indexBannerList == null){
            indexBannerList = new ArrayList<>();
        }
        if (!TextUtils.isEmpty(brandImg)){
            BrandInfoBean.IndexBannerListBean bannerListBean = new BrandInfoBean.IndexBannerListBean();
            bannerListBean.img = brandImg;
            indexBannerList.add(bannerListBean);
        }

        if (indexBannerList == null || indexBannerList.size() <= 0) {
            mBannerProduct.setVisibility(View.GONE);
            return;
        }
        mBannerProduct.setVisibility(View.VISIBLE);
        mBannderData.clear();
        for (int i = 0; i < indexBannerList.size(); i++) {
            mBannderData.add(indexBannerList.get(i).img);
        }
        mBannerProduct.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);

        mBannerProduct.setIndicatorVisible(mBannderData.size() > 1 ? true : false);

        mBannerProduct.setPages(mBannderData, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        mBannerProduct.start();

    }

    /**
     * 设置数据
     *
     * @param productList
     */
    private List<BrandInfoBean.ProductListBean> mProductList;
    private BaseQuickAdapter<BrandInfoBean.ProductListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setOtherProductData(List<BrandInfoBean.ProductListBean> productList) {
        if (productList == null || productList.size() <= 0) {
            if (mProductList == null || mProductList.size() <= 0 || mBaseProductQuickAdapter == null) {
                mNetError.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (mProductList == null) {
            mProductList = new ArrayList<>();
        }

        mNetError.setVisibility(View.INVISIBLE);

        //这里添加判断，用于区分时候已经初始化过。初始化过就刷新数据，没
        if (page == 1) {
            mProductList.clear();
        }

        mProductList.addAll(productList);
        setAdapter();
    }


    /**
     * 适配切换布局
     */
    private void setAdapter() {
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<BrandInfoBean.ProductListBean, BaseViewHolder>(!this.mIsSingleLine ? R.layout.item_brand_product1 : R.layout.item_brand_product2, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, BrandInfoBean.ProductListBean item) {
                    try {
                        //商品图片
                        ImageView ivItemOtherFenLei = helper.getView(R.id.iv_item_product_product_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.img).into(ivItemOtherFenLei);


                        //设置来源和标题
                        helper.setText(R.id.tv_item_product_product_biaoti, item.productName);
                        /*if (mIsSingleLine){
                            SpannelTextViewSinge spannelTextViewSinge = helper.getView(R.id.tv_item_product_product_biaoti);
                            spannelTextViewSinge.setDrawText(item.productName);
                            spannelTextViewSinge.setShopType(item.shopType);
                        }else {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_product_product_biaoti);
                            spannelTextViewMore.setDrawText(item.productName);
                            spannelTextViewMore.setShopType(item.shopType);
                        }*/

                        //补贴佣金
                        /*helper.setText(R.id.tv_item_product_product_butie_yongjin, "补贴佣金  ¥暂无显示");

                        //预估佣金和升级赚
                        if (mIsSingleLine){
                            helper.getView(R.id.tv_item_product_product_zhuan_double_parent).setVisibility(TextUtils.isEmpty(item.zhuanMoney) || TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                            helper.setText(R.id.tv_item_product_product_double_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                            helper.setText(R.id.tv_item_product_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                        }
                        helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                        helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_product_product_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                        helper.setText(R.id.tv_item__product_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));

                        //预估佣金和升级赚
                        if (mIsSingleLine){

                            if (TextUtils.isEmpty(item.zhuanMoney) || TextUtils.isEmpty(item.upZhuanMoney)){
                                helper.getView(R.id.tv_item_product_product_zhuan_double_parent).setVisibility( View.GONE );
                                helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.setText(R.id.tv_item_product_product_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item__product_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                            }else {
                                helper.getView(R.id.tv_item_product_product_zhuan_double_parent).setVisibility( View.VISIBLE );
                                helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(View.GONE);
                                helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(View.GONE);
                                helper.setText(R.id.tv_item_product_product_double_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item_product_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));

                            }
                        }else {
                            helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                            helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                            helper.setText(R.id.tv_item_product_product_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                            helper.setText(R.id.tv_item__product_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                        }*/
                        //券金额
                        helper.getView(R.id.tv_item_product_product_quan).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_product_product_quan, "¥" + StringUtils.doubleToStringDeleteZero(item.couponMoney));

                        //现价(券后价)
                        helper.setText(R.id.tv_item_product_product_xianjia, "" + StringUtils.stringToStringDeleteZero(item.preferentialPrice) + "");

                        /*if (mIsSingleLine){
                            //渠道
                            helper.setText(R.id.tv_item_product_product_yuanjia_type,item.shopType == 1 ? "淘宝价 " : "天猫价 ");
                        }*/

                        //原价
                        helper.setText(R.id.tv_item_product_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.price) + "");
                        ((TextView)helper.getView(R.id.tv_item_product_product_yuanjia)).getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
                        //销量
                        helper.setText(R.id.tv_item_product_product_xiaoliang, StringUtils.intToStringUnit(item.nowNumber) + " 人已买");


                    } catch (Exception e) {

                    }
                }
            };
            mRvProduct.setHasFixedSize(true);
            mRvProduct.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, !this.mIsSingleLine ? 1 : 2);
            mRvProduct.setLayoutManager(gridLayoutManager);

            mRvProduct.setAdapter(mBaseProductQuickAdapter);
            mBaseProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                    /*RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.t = "1";
                    routerBean.id = mProductList.get(position).id;
                    routerBean.i = mProductList.get(position).tbItemId;

                    LinkRouterUtils.getInstance().setRouterBean(BrandInfoActivity.this, routerBean);*/
                    RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.mustParam = "type=1"
                            + "&id=" + mProductList.get(position).id
                            + "&tbItemId=" + mProductList.get(position).tbItemId;

//                                routerBean.t = "1";
//                                routerBean.id = item.brandProduct.get(0).id;
//                                routerBean.i = item.brandProduct.get(0).tbItemId;

                    LinkRouterUtils.getInstance().setRouterBean(BrandInfoActivity.this, routerBean);
                }
            });
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        //判断用于首次没有加载出来数据时，刷新整体数据
        if (mBaseProductQuickAdapter != null) {
            page++;
            getBrandFreshData();
        } else {
            page = 1;
            shuaxin = 11;
            getProductList(false);
            mSmartProduct.finishLoadMore(1000);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        shuaxin = 11;
        getProductList(false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartProduct.finishRefresh(state);
        } else if (noMoreData) {
            mSmartProduct.finishLoadMoreWithNoMoreData();
        } else {
            mSmartProduct.finishLoadMore(state);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        }
    }

    class PublicSelecter implements TiaoJianView.OnSelecterLisenter {

        private NoShouYiTiaoJian mNoShouYiTiaoJian;

        public PublicSelecter(NoShouYiTiaoJian noShouYiTiaoJian) {
            this.mNoShouYiTiaoJian = noShouYiTiaoJian;
        }

        @Override
        public void onSelecter(int position, boolean sort) {
            if (mPosition == position && position != 1) {
                return;
            }
            Logger.e("----", position + "***" + sort);
            page = 1;
            mPosition = position;
            mSort = sort;
//            mSmartProduct.autoRefresh();
            mNoShouYiTiaoJian.updataStyles(position);
            getProductList(true);

        }

        @Override
        public void onChangeStyle(boolean isSingleLine) {
            mIsSingleLine = isSingleLine;
            mBaseProductQuickAdapter = null;
            setAdapter();
            mNoShouYiTiaoJian.updataShowStyle();
        }
    }
}
