package java.com.lechuang.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.DeviceIdUtil;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.StringUtils;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.NoShouYiTiaoJian;
import com.common.app.view.SpannelTextViewMore;
import com.common.app.view.TiaoJianView;
import com.lechuang.home.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.home.adapter.OtherRvAdapter;
import java.com.lechuang.home.bean.HomeOtherBean;
import java.com.lechuang.home.bean.HomeTabBean;
import java.com.lechuang.home.bean.OtherAllChildBean;
import java.com.lechuang.home.bean.OtherAllEntity;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: zhengjr
 * @since: 2018/8/20
 * @describe:
 */

@Route(path = ARouters.PATH_OTHER)
public class OtherFragment extends LazyBaseFragment implements OnRefreshLoadMoreListener {

    private HomeTabBean mHomeTabBean;
    private SmartRefreshLayout mSmartHomeOther;
    private int page = 1;
    private RecyclerView mRvHomeOtherProduct;
    private NoShouYiTiaoJian mShaiXuanHomeTop;
    private int mPosition = 0;//表示筛选条件，0为综合，1为价格，2为销量，3为收益
    private boolean mSort = true;//表示价格的排列，true为箭头向上
    private boolean mIsSingleLine = true;//单行和多行切换
    private LinearLayout mNetError;
    private int shuaxin = 10;
    private ImageView mIvHomeAllTop;
    private boolean mNeedNotifyList = false;//点击头部的筛选，是否需要更新列表显示头部
    private View mVsCommonWevError;
    private boolean mLoadError = true;
    private String mDeviceId=null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other;
    }

    @Override
    protected void findViews() {
        mSmartHomeOther = mInflate.findViewById(R.id.smart_home_other);
        mRvHomeOtherProduct = mInflate.findViewById(R.id.rv_home_other_product);
        mShaiXuanHomeTop = mInflate.findViewById(R.id.shaixuan_home_other_top);
        mIvHomeAllTop = mInflate.findViewById(R.id.iv_home_all_top);
        mNetError = mInflate.findViewById(R.id.ll_net_error);
        mVsCommonWevError = $(R.id.vs_common_web_error);
        mIvHomeAllTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance=0;
                mRvHomeOtherProduct.scrollToPosition(0);
                mIvHomeAllTop.setVisibility(View.INVISIBLE);
            }
        });

        mRvHomeOtherProduct.addItemDecoration(new GridItemDecoration(
                new GridItemDecoration.Builder(getActivity())
                        .size(5)
        ));
        $(R.id.tv_common_hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductList(false);
            }
        });
    }

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);
        mDeviceId=getDeviceId(getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            mHomeTabBean = (HomeTabBean) arguments.getSerializable("TopTab");
        }
        mSmartHomeOther.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void getData() {
        //smart设置属性，设置自动刷新，调用刷新方法
        mSmartHomeOther.autoRefresh(100);
    }
    public String getDeviceId(Context context) {
        String IMEI=null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
            return IMEI;
        }
        IMEI= DeviceIdUtil.getDeviceId(context);
        return IMEI;
    }


    private OtherAllEntity otherAllEntityHeader;

    private void getNextTbClass() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("rootId", mHomeTabBean.programaId);

        NetWork.getInstance()
                .setTag(Qurl.getNextTbClass)
                .getApiService(HomeApi.class)
                .getNextTbClass(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<OtherAllChildBean>(getActivity(), false, false) {
                    @Override
                    public void onSuccess(OtherAllChildBean result) {
                        if (result == null || result.classTypeList == null || result.classTypeList.size() <= 0) {
                            return;
                        }
                        if (mProductListEntity == null) {
                            mProductListEntity = new ArrayList<>();
                        }
                        otherAllEntityHeader = new OtherAllEntity(OtherAllEntity.TYPE_HEADER);
                        otherAllEntityHeader.classTypeList = result.classTypeList;
                        setOtherProductData1();
                    }
                });

    }

    private List<HomeOtherBean.ProductListBean> mRroductList;

    private void getProductList(boolean isShowProgress) {
        ApiCancleManager.getInstance().removeAll();
        Map<String, String> allParam = new HashMap<>();
        allParam.put("classTypeId", mHomeTabBean.programaId + "");
        allParam.put("page", page + "");

        if (page == 1) {
            mIvHomeAllTop.setVisibility(View.INVISIBLE);
        }
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
        if (mDeviceId!=null){
            allParam.put("deviceId", mDeviceId);
        }

        NetWork.getInstance()
                .setTag(Qurl.productShowAll)
                .getApiService(HomeApi.class)
                .productShowAll(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<HomeOtherBean>(getActivity(), true, isShowProgress) {

                    @Override
                    public void onSuccess(HomeOtherBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);

                        //设置底部商品数据
//                        setOtherProductData(result.productList);
                        mRroductList = result.productList;
                        setOtherProductData1();
                        mLoadError = false;
                        mVsCommonWevError.setVisibility(View.GONE);
                        mRvHomeOtherProduct.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (mProductList == null || mProductList.size() <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                        if (mLoadError && moreInfo.indexOf("无网络") != -1) {
                            mVsCommonWevError.setVisibility(View.VISIBLE);
                            mRvHomeOtherProduct.setVisibility(View.GONE);
                            mNetError.setVisibility(View.GONE);
                        } else {
                            mVsCommonWevError.setVisibility(View.GONE);
                            mRvHomeOtherProduct.setVisibility(View.VISIBLE);
                            mNetError.setVisibility(View.GONE);
                        }
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

    private List<OtherAllEntity> mProductListEntity;
    private OtherRvAdapter<OtherAllEntity, BaseViewHolder> mOtherRvAdapter;
    private BaseQuickAdapter<OtherAllChildBean.ClassTypeListBean,BaseViewHolder> mHeaderBaseQuickAdapter;
    private int distance;

    private void setOtherProductData1() {
        if (mProductListEntity == null) {
            mProductListEntity = new ArrayList<>();
        }
        if (page == 1) {
            mProductListEntity.clear();
            distance = 0;
        }

        if (page == 1 && otherAllEntityHeader != null && otherAllEntityHeader.classTypeList != null && otherAllEntityHeader.classTypeList.size() > 0) {
            mProductListEntity.add(otherAllEntityHeader);
        }

        if (page == 1) {
            OtherAllEntity otherAllEntity = new OtherAllEntity(OtherAllEntity.TYPE_SHAIXUAN);
            mProductListEntity.add(otherAllEntity);
        }

        if (mRroductList != null && mRroductList.size() > 0) {
            for (HomeOtherBean.ProductListBean productListBean : mRroductList) {
                OtherAllEntity otherAllEntity = new OtherAllEntity(OtherAllEntity.TYPE_PRODUCT);
                otherAllEntity.mProductListBean = productListBean;
                mProductListEntity.add(otherAllEntity);
            }
        }


        if (mOtherRvAdapter == null) {
            mOtherRvAdapter = new OtherRvAdapter<OtherAllEntity, BaseViewHolder>(mProductListEntity) {
                @Override
                protected void addItemTypeView() {
                    addItemType(OtherAllEntity.TYPE_HEADER, R.layout.layout_item_other_header);
                    addItemType(OtherAllEntity.TYPE_SHAIXUAN, R.layout.layout_item_other_shaixuan);
                    addItemType(OtherAllEntity.TYPE_PRODUCT, R.layout.item_other_product1);
                }

                @Override
                protected void convert(BaseViewHolder helper, final OtherAllEntity item) {

                    if (item.getItemType() == OtherAllEntity.TYPE_HEADER) {
                        RecyclerView rvHomeOtherHeader = helper.getView(R.id.rv_home_other_header);
                            mHeaderBaseQuickAdapter = new BaseQuickAdapter<OtherAllChildBean.ClassTypeListBean, BaseViewHolder>(R.layout.layout_item_rv_other_header,item.classTypeList) {
                                @Override
                                protected void convert(BaseViewHolder helper, OtherAllChildBean.ClassTypeListBean classTypeListBean) {
                                    helper.setText(R.id.tv_item_other_child, classTypeListBean.name);
                                    ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_other_child);
                                    Glide.with(BaseApplication.getApplication()).load(classTypeListBean.img).placeholder(R.drawable.ic_common_jiugong_img_null).into(ivItemAllFenLei);
                                }
                            };
                            rvHomeOtherHeader.setAdapter(mHeaderBaseQuickAdapter);
                            mHeaderBaseQuickAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    OtherAllChildBean.ClassTypeListBean classTypeListBean = item.classTypeList.get(position);
                                    ARouter.getInstance()
                                            .build(ARouters.PATH_OTHER_PRODUCT)
                                            .withString(Constant.KEYWORD,classTypeListBean.keyword)
                                            .withInt(Constant.CLASSTYPEID,mHomeTabBean.programaId)
                                            .withString(Constant.TITLE,classTypeListBean.name)
                                            .navigation();
                                }
                            });
                        rvHomeOtherHeader.setHasFixedSize(true);
                        rvHomeOtherHeader.setNestedScrollingEnabled(false);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), item.classTypeList.size() > 5 ? 5 : item.classTypeList.size());
                        rvHomeOtherHeader.setLayoutManager(gridLayoutManager);

                    }else if (item.getItemType() == OtherAllEntity.TYPE_SHAIXUAN){
                        NoShouYiTiaoJian noShouYiTiaoJian = helper.getView(R.id.shaixuan_item_other_top);
                        noShouYiTiaoJian.setSelectLisenter(new PublicSelecter(mShaiXuanHomeTop));
                        mShaiXuanHomeTop.setSelectLisenter(new PublicSelecter(noShouYiTiaoJian));

                    }else if (item.getItemType() == OtherAllEntity.TYPE_PRODUCT) {
                        //商品图片
                        ImageView ivItemOtherFenLei = helper.getView(R.id.iv_item_other_product_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.mProductListBean.img).placeholder(R.drawable.bg_common_img_null).into(ivItemOtherFenLei);
                        helper.getView(R.id.iv_video).setVisibility(item.mProductListBean.isVideo.equals("1") ? View.VISIBLE : View.GONE);
                        //设置来源和标题
                        if (mIsSingleLine) {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_other_product_biaoti);
                            spannelTextViewMore.setDrawText(item.mProductListBean.productName);
                            spannelTextViewMore.setShopType(item.mProductListBean.shopType);
                        } else {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_other_product_biaoti);
                            spannelTextViewMore.setDrawText(item.mProductListBean.productName);
                            spannelTextViewMore.setShopType(item.mProductListBean.shopType);
                        }


                        //补贴佣金
                        helper.setText(R.id.tv_item_other_product_butie_yongjin, "补贴佣金  ¥暂无显示");
                        if (BaseApplication.getApplication().mQueryShowHide) {
                            //true 显示为不登录&普通会员&超级会员 { 预计赚 + 升级赚 }，
                            //false为显示运营商 { 预计赚  }，高级运营商 { 预计赚 }
                            boolean isAllShow = true;
                            if (UserHelper.getInstence().getUserInfo().getAgencyLevel() == 3 ||
                                    UserHelper.getInstence().getUserInfo().getAgencyLevel() == 4) {
                                isAllShow = false;
                            }

                            if (mIsSingleLine) {
                                helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(isAllShow ? View.VISIBLE : View.INVISIBLE);
                                helper.setText(R.id.tv_item_other_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney));
                                helper.setText(R.id.tv_item_other_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));
                            } else {
                                if (isAllShow) {
                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(View.VISIBLE);
                                    helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(View.VISIBLE);
                                } else {
                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(View.INVISIBLE);
                                    helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(View.VISIBLE);
                                }

                                helper.setText(R.id.tv_item_other_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney));
                                helper.setText(R.id.tv_item_other_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));
                            }

                        }
                        //预计赚背景图片
                        TextView yujizhuan=helper.getView( R.id.tv_item_other_single_yugu_yongjin );
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
                        helper.getView(R.id.tv_item_other_product_quan).setVisibility(item.mProductListBean.couponMoney == 0 ? View.GONE : View.VISIBLE);
//                        helper.getView(R.id.tv_item_other_product_quan_txt).setVisibility(item.mProductListBean.couponMoney == 0 ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_other_product_quan, "¥" + StringUtils.doubleToStringDeleteZero(item.mProductListBean.couponMoney) + "劵");

                        //现价(券后价)
                        helper.setText(R.id.tv_item_other_product_xianjia, "" + StringUtils.stringToStringDeleteZero(item.mProductListBean.preferentialPrice) + "");

                        //原价
                        helper.setText(R.id.tv_item_other_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.mProductListBean.price) + "");
                        ((TextView) helper.getView(R.id.tv_item_other_product_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                        //销量
                        helper.setText(R.id.tv_item_other_product_xiaoliang, "" + StringUtils.intToStringUnit(item.mProductListBean.nowNumber) + "人购买");
                    }
                }
            };
            mRvHomeOtherProduct.setHasFixedSize(true);
            mRvHomeOtherProduct.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), this.mIsSingleLine ? 1 : 2);
            mRvHomeOtherProduct.setLayoutManager(gridLayoutManager);


            mRvHomeOtherProduct.setAdapter(mOtherRvAdapter);

            //底部推荐产品点击事件 todo
            mOtherRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    int itemViewType = adapter.getItemViewType(position);
                    if (itemViewType == OtherAllEntity.TYPE_PRODUCT){
                        RouterBean routerBean = new RouterBean();
                        routerBean.type = 9;
                        routerBean.tbCouponId = mProductListEntity.get(position).mProductListBean.tbCouponId;
                        routerBean.mustParam = "type=1"
                                + "&id=" + mProductListEntity.get(position).mProductListBean.id
                                + "&tbItemId=" + mProductListEntity.get(position).mProductListBean.tbItemId;

                        LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                    }

                }
            });

            mRvHomeOtherProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {


                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;

                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        if (lastItemPosition > shuaxin) {
                            if (mOtherRvAdapter != null) {
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

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        View firstVisibItem = recyclerView.getChildAt(0);
                        int height = firstVisibItem.getHeight();

                        distance += dy;
                        if (otherAllEntityHeader == null) {
                            if (distance == 0) {
                                mShaiXuanHomeTop.setVisibility(View.INVISIBLE);
                            } else {
                                mShaiXuanHomeTop.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (distance >= height) {
                                mShaiXuanHomeTop.setVisibility(View.VISIBLE);
                            } else {
                                mShaiXuanHomeTop.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            });
        } else {
            mOtherRvAdapter.notifyDataSetChanged();
        }

        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvHomeOtherProduct.scrollToPosition(0);
        }
    }

    /**
     * 设置数据
     *
     * @param productList
     */
    private List<HomeOtherBean.ProductListBean> mProductList;
    private BaseQuickAdapter<HomeOtherBean.ProductListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setOtherProductData(List<HomeOtherBean.ProductListBean> productList) {
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
        setOtherAdapter();
    }

    /**
     * 适配切换布局
     */
    private void setOtherAdapter() {
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<HomeOtherBean.ProductListBean, BaseViewHolder>(this.mIsSingleLine ? R.layout.item_other_product1 : R.layout.item_other_product2, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, HomeOtherBean.ProductListBean item) {
                    try {
                        //商品图片
                        ImageView ivItemOtherFenLei = helper.getView(R.id.iv_item_other_product_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.img).placeholder(R.drawable.bg_common_img_null).into(ivItemOtherFenLei);
                        helper.getView(R.id.iv_video).setVisibility(item.isVideo.equals("1") ? View.VISIBLE : View.GONE);
                        //设置来源和标题
                        if (mIsSingleLine) {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_other_product_biaoti);
                            spannelTextViewMore.setDrawText(item.productName);
                            spannelTextViewMore.setShopType(item.shopType);
                        } else {
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_other_product_biaoti);
                            spannelTextViewMore.setDrawText(item.productName);
                            spannelTextViewMore.setShopType(item.shopType);
                        }


                        //补贴佣金
                        helper.setText(R.id.tv_item_other_product_butie_yongjin, "补贴佣金  ¥暂无显示");

                        //根据后台返的数据做判断，改成一下的自己做判断
                        /*if (BaseApplication.getApplication().mQueryShowHide){
                            //预估佣金和升级赚
                            if (mIsSingleLine){

                                if (TextUtils.isEmpty(item.zhuanMoney) || TextUtils.isEmpty(item.upZhuanMoney)){
                                    helper.getView(R.id.tv_item_other_zhuan_double_parent).setVisibility( View.GONE );
                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                    helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                                    helper.setText(R.id.tv_item_other_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                    helper.setText(R.id.tv_item_other_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                                }else {
                                    helper.getView(R.id.tv_item_other_zhuan_double_parent).setVisibility( View.VISIBLE );
                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(View.GONE);
                                    helper.setText(R.id.tv_item_other_double_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                    helper.setText(R.id.tv_item_other_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                                }
                            }else {
                                helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.setText(R.id.tv_item_other_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item_other_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
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
//                                helper.getView(R.id.tv_item_other_zhuan_double_parent).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
                                helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(isAllShow ? View.VISIBLE : View.INVISIBLE);
//                                helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.INVISIBLE);
//                                if (!isAllShow) {
//                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(View.INVISIBLE);
//                                }

//                                helper.setText(R.id.tv_item_other_double_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
//                                helper.setText(R.id.tv_item_other_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                                helper.setText(R.id.tv_item_other_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item_other_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                            } else {
                                if (isAllShow) {
                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(View.VISIBLE);
                                    helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(View.GONE);
                                } else {
                                    helper.getView(R.id.tv_item_other_single_sheng_yongjin).setVisibility(View.INVISIBLE);
                                    helper.getView(R.id.tv_item_other_single_yugu_yongjin).setVisibility(View.GONE);
                                }

                                helper.setText(R.id.tv_item_other_single_sheng_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item_other_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                            }

                        }
                        //预计赚背景图片
                        TextView yujizhuan=helper.getView( R.id.tv_item_other_single_yugu_yongjin );
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
                        helper.getView(R.id.tv_item_other_product_quan).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
//                        helper.getView(R.id.tv_item_other_product_quan_txt).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_other_product_quan, "¥" + StringUtils.doubleToStringDeleteZero(item.couponMoney) + "劵");

                        //现价(券后价)
                        helper.setText(R.id.tv_item_other_product_xianjia, "" + StringUtils.stringToStringDeleteZero(item.preferentialPrice) + "");

                        if (mIsSingleLine) {
                            //渠道
//                            helper.setText(R.id.tv_item_other_product_yuanjia_type,item.shopType == 1 ? "淘宝价 " : "天猫价 ");
                        }

                        //原价
                        helper.setText(R.id.tv_item_other_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.price) + "");
                        ((TextView) helper.getView(R.id.tv_item_other_product_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                        //销量
                        helper.setText(R.id.tv_item_other_product_xiaoliang, "" + StringUtils.intToStringUnit(item.nowNumber) + "人购买");


                    } catch (Exception e) {

                    }
                }
            };
            mRvHomeOtherProduct.setHasFixedSize(true);
            mRvHomeOtherProduct.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), this.mIsSingleLine ? 1 : 2);
            mRvHomeOtherProduct.setLayoutManager(gridLayoutManager);


            mRvHomeOtherProduct.setAdapter(mBaseProductQuickAdapter);

            //底部推荐产品点击事件 todo
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

                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                }
            });
            mRvHomeOtherProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }

        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvHomeOtherProduct.scrollToPosition(0);
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        page++;
        getProductList(false);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        shuaxin = 10;
        mSmartHomeOther.setNoMoreData(false);
        getProductList(false);
        getNextTbClass();
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
            mSmartHomeOther.finishRefresh(state);
            if (!state) {
                //第一次加载失败时，再次显示时可以重新加载
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartHomeOther.finishLoadMoreWithNoMoreData();
        } else {
            mSmartHomeOther.finishLoadMore(state);
        }
    }

    /*@Override
    public void onSelecter(int position, boolean sort) {

        if (mPosition == position && position != 1) {
            return;
        }
        page = 1;
        mPosition = position;
        mSort = sort;
        shuaxin = 10;
        mNeedNotifyList = true;
        getProductList(true);
//        mSmartHomeOther.autoRefresh();

    }

    @Override
    public void onChangeStyle(boolean isSingleLine) {
        mIsSingleLine = isSingleLine;
        mBaseProductQuickAdapter = null;
        mNeedNotifyList = true;
        setOtherProductData1();

    }*/

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
            setOtherProductData1();
            mNoShouYiTiaoJian.updataShowStyle();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS) || message.equalsIgnoreCase(Constant.LOGOUT_SUCCESS)) {
            mSmartHomeOther.finishRefresh();
            mSmartHomeOther.finishLoadMore();
            mRvHomeOtherProduct.smoothScrollToPosition(0);
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            if (getUserVisibleHint()) {
                mNeedNotifyList = true;
                mSmartHomeOther.autoRefresh(100);
            } else {
                this.mIsFirstVisible = true;
            }
        }
    }
}
