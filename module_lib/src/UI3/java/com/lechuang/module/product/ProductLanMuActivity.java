package java.com.lechuang.module.product;

import android.graphics.Paint;
import android.support.v4.view.ViewPager;
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
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.Logger;
import com.common.app.utils.StringUtils;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.NoShouYiTiaoJian;
import com.common.app.view.SpannelTextViewMore;
import com.common.app.view.SpannelTextViewSinge;
import com.common.app.view.TiaoJianView;
import com.common.app.view.TransChangeNesScrollView;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.ProductBean;
import java.com.lechuang.module.shareapp.adapter.BannerViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_PRODUCT)
public class ProductLanMuActivity extends BaseActivity implements OnRefreshLoadMoreListener, View.OnClickListener {

    @Autowired(name = Constant.PROGRAMAID)
    public String programaId;//界面传递的值，用于请求数据的参数
    @Autowired(name = Constant.TITLE)
    public String title = "";//界面传递的值标题，
    @Autowired()
    public String link = "";
    @Autowired(name = Constant.CUSTOM)
    public String custom = "";
    @Autowired(name = Constant.GATHERID)
    public String gatherId = "";
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
    private boolean mNeedNotifyList = false;//点击头部的筛选，是否需要更新列表显示头部
    private long shuaxin = 10;
    private ImageView mIvHomeAllTop;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_luman;
    }

    @Override
    protected void findViews() {

        mIvCommonBack = $(R.id.iv_common_back);
        mIvCommonTitle = $(R.id.iv_common_title);
//        mNesProduct = $(R.id.nes_product);

        mSmartProduct = $(R.id.smart_product);
        mBannerProduct = $(R.id.banner_product);
        mShaiXuanProTop = $(R.id.shaixuan_product_top);
        mShaiXuanProBottom = $(R.id.shaixuan_product_bottom);
        mRvProduct = $(R.id.rv_product);
        mNetError = $(R.id.ll_net_error);
        mIvHomeAllTop = $(R.id.iv_home_all_top);
        mIvHomeAllTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRvProduct.scrollToPosition(0);
                mIvHomeAllTop.setVisibility(View.INVISIBLE);
            }
        });

//        mNesProduct.setTopTabLayout(mShaiXuanProBottom,mShaiXuanProTop);

        mIvCommonBack.setOnClickListener(this);


    }

    @Override
    protected void initView() {

        ARouter.getInstance().inject(this);
        mIvCommonTitle.setText(title);

        mShaiXuanProBottom.setSelectLisenter(new PublicSelecter(mShaiXuanProTop));
        mShaiXuanProTop.setSelectLisenter(new PublicSelecter(mShaiXuanProBottom));
        mSmartProduct.setOnRefreshLoadMoreListener(this);

        mRvProduct.addItemDecoration(new GridItemDecoration(
                new GridItemDecoration.Builder(this)
                        .size(5)
        ));
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

    private void getProductList(boolean isShowProgress) {
        Map<String, String> allParam = new HashMap<>();
        if (!TextUtils.isEmpty(programaId)) {
            allParam.put("programaId", programaId + "");
        }
        allParam.put("page", page + "");

        switch (mPosition) {
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

        }

        if (!TextUtils.isEmpty(custom)) {
            allParam.put("custom", custom);
        }
        if (!TextUtils.isEmpty(gatherId)) {
            allParam.put("gatherId", gatherId);
        }

        NetWork.getInstance()
                .setTag(link)
                .getApiService(ModuleApi.class)
                .programaShowAllUrl(link, allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ProductBean>(this, true, isShowProgress) {

                    @Override
                    public void onSuccess(ProductBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);

                        if (page == 1){
                            mIvHomeAllTop.setVisibility(View.INVISIBLE);
                        }

                        //设置banner数据
                        setBannerData(result.indexBannerList);

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

    private void setBannerData(List<ProductBean.IndexBannerListBean> indexBannerList) {
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
        mBannerProduct.addPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBannerProduct.start();

    }

    /**
     * 设置数据
     *
     * @param productList
     */
    private List<ProductBean.ProductListBean> mProductList;
    private BaseQuickAdapter<ProductBean.ProductListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setOtherProductData(List<ProductBean.ProductListBean> productList) {
        if (productList == null || productList.size() <= 0) {

            if (page == 1 && mProductList != null && mBaseProductQuickAdapter != null) {
                mProductList.clear();
                mBaseProductQuickAdapter.notifyDataSetChanged();
            }

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
            mBaseProductQuickAdapter = new BaseQuickAdapter<ProductBean.ProductListBean, BaseViewHolder>(this.mIsSingleLine ? R.layout.item_product_product1 : R.layout.item_product_product2, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, ProductBean.ProductListBean item) {
                    try {
                        //商品图片
                        ImageView ivItemOtherFenLei = helper.getView(R.id.iv_item_product_product_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.img).placeholder(R.drawable.bg_common_img_null).into(ivItemOtherFenLei);

                        //是否显示视频图片
                        helper.getView( R.id.iv_video ).setVisibility( item.isVideo.equals( "1" )? View.VISIBLE:View.GONE);

                        //设置来源和标题
                        if (mIsSingleLine) {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_product_product_biaoti);
                            spannelTextViewMore.setDrawText(item.productName);
                            spannelTextViewMore.setShopType(item.shopType);
                        } else {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_product_product_biaoti);
                            spannelTextViewMore.setDrawText(item.productName);
                            spannelTextViewMore.setShopType(item.shopType);
                        }

                        //补贴佣金
                        helper.setText(R.id.tv_item_product_product_butie_yongjin, "补贴佣金  ¥暂无显示");

                        //根据后台返的数据做判断，改成一下的自己做判断
                        /*if (BaseApplication.getApplication().mQueryShowHide){
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
                            }
                        }*/
                        //自己判断
                        if (BaseApplication.getApplication().mQueryShowHide) {
                            //true 显示为不登录&普通会员&超级会员 { 预计赚 + 升级赚 }，
                            //false为显示运营商 { 预计赚  }，高级运营商 { 预计赚 }
                            boolean isAllShow = true;
                            if (UserHelper.getInstence().getUserInfo().getAgencyLevel() == 3 ||
                                    UserHelper.getInstence().getUserInfo().getAgencyLevel() == 4) {
                                isAllShow = false;
                            }


                            if (mIsSingleLine) {
                                helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_item_product_product_zhuan_double_parent).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.GONE);
//                                if (!isAllShow) {
//                                    helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(View.GONE);
//                                }

                                helper.setText(R.id.tv_item_product_product_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item__product_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
//                                helper.setText(R.id.tv_item_product_product_double_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
//                                helper.setText(R.id.tv_item_product_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                            } else {
                                if (isAllShow) {
                                    helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(View.VISIBLE);
                                    helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(View.VISIBLE);
                                } else {
                                    helper.getView(R.id.tv_item_product_product_single_sheng_yongjin).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_item__product_product_single_yugu_yongjin).setVisibility(View.VISIBLE);
                                }

                                helper.setText(R.id.tv_item_product_product_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item__product_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                            }
                        }
                        TextView yujizhuan=helper.getView( R.id.tv_item__product_product_single_yugu_yongjin );
                        if (UserHelper.getInstence().isLogin()){
                            if (UserHelper.getInstence().getUserInfo().getAgencyLevel()==4){
                                yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom4 ) );
                            }else {
                                yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom ) );
                            }
                        }else {
                            yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom ) );
                        }

                        //券金额
                        helper.getView(R.id.tv_item_product_product_quan).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
//                        helper.getView(R.id.tv_item_product_product_quan_txt).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_product_product_quan, "¥" + StringUtils.doubleToStringDeleteZero(item.couponMoney) + "劵");

                        if (mIsSingleLine) {
                            //渠道
//                            helper.setText(R.id.tv_item_product_product_yuanjia_type, item.shopType == 1 ? "淘宝价 " : "天猫价 ");
                        }
                        //现价(券后价)
                        helper.setText(R.id.tv_item_product_product_xianjia, "" + StringUtils.stringToStringDeleteZero(item.preferentialPrice) + "");

                        //原价
                        helper.setText(R.id.tv_item_product_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.price) + "");
                        ((TextView) helper.getView(R.id.tv_item_product_product_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


                        //销量
                        helper.setText(R.id.tv_item_product_product_xiaoliang, "" + StringUtils.intToStringUnit(item.nowNumber) + "人购买");


                    } catch (Exception e) {

                    }
                }
            };
            mRvProduct.setHasFixedSize(true);
            mRvProduct.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, this.mIsSingleLine ? 1 : 2);
            mRvProduct.setLayoutManager(gridLayoutManager);


            mRvProduct.setAdapter(mBaseProductQuickAdapter);
            mBaseProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.tbCouponId = mProductList.get(position).tbCouponId;
                    routerBean.mustParam = "type=1"
                            + "&id=" + mProductList.get(position).id
                            + "&tbItemId=" + mProductList.get(position).tbItemId;
//                    routerBean.t = "1";
//                    routerBean.id = mProductList.get(position).id;
//                    routerBean.i = mProductList.get(position).tbItemId;

                    LinkRouterUtils.getInstance().setRouterBean(ProductLanMuActivity.this, routerBean);
                }
            });
            mRvProduct.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        if (lastItemPosition > shuaxin) {
                            if (mBaseProductQuickAdapter != null) {
                                page++;
                                getProductList(false);
                                shuaxin = shuaxin + 20;
                            }
                        }
                        int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                        if (firstVisibleItemPosition <= 15) {
                            mIvHomeAllTop.setVisibility(View.INVISIBLE);
                        } else {
                            mIvHomeAllTop.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvProduct.scrollToPosition(0);
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        //判断用于首次没有加载出来数据时，刷新整体数据
        if (mBaseProductQuickAdapter != null) {
            page++;
            getProductList(false);
        } else {
            page = 1;
            shuaxin = 10;
            getProductList(false);
            mSmartProduct.finishLoadMore(1000);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        shuaxin = 10;
        mSmartProduct.setNoMoreData(false);
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
            shuaxin = 10;
//            mSmartProduct.autoRefresh();
            mNoShouYiTiaoJian.updataStyles(position);
            mNeedNotifyList = true;
            getProductList(true);

        }

        @Override
        public void onChangeStyle(boolean isSingleLine) {
            mIsSingleLine = isSingleLine;
            mBaseProductQuickAdapter = null;
            mNeedNotifyList = true;
            setAdapter();
            mNoShouYiTiaoJian.updataShowStyle();
        }
    }
}
