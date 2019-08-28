package java.com.lechuang.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
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
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.StringUtils;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.SpannelTextViewMore;
import com.lechuang.home.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sunfusheng.marqueeview.MarqueeView;
import com.zhouwei.mzbanner.BannerBgContainer;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.home.adapter.BannerViewHolder;
import java.com.lechuang.home.adapter.HomeRvAdapter;
import java.com.lechuang.home.bean.ContainerView;
import java.com.lechuang.home.bean.FlashSaleIdBean;
import java.com.lechuang.home.bean.HomeAllBean;
import java.com.lechuang.home.bean.HomeAllEntity;
import java.com.lechuang.home.bean.HomeTabBean;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

@Route(path = ARouters.PATH_ALL)
public class AllFragment extends LazyBaseFragment implements OnRefreshLoadMoreListener, View.OnClickListener {

    private HomeTabBean mHomeTabBean;
    private ContainerView mContainerView;
    public static BannerBgContainer mBannerBgContainer;
    private SmartRefreshLayout mSmartHomeAll;
    private int page = 0;//页数
    private ClassicsHeader mSmartHomeHeaderAll;
    private ClassicsFooter mSmartHomeFooterAll;
    private RecyclerView mRvHomeParentView;
    private ImageView mIvHomeAllTop;
    private View mVsCommonWevError;
    private boolean mLoadError = true;
    private int shuaxin = 11;
    private boolean mNeedNotifyList = false;//用户变更，是否需要更新列表显示头部
    private CountDownTimer mCountDownTimer;
    private FrameLayout mFlAllBottomLogin;
    private String mDeviceId=null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_all;
    }

    public INotifyScrollProgress mINotifyScrollProgress;

    public interface INotifyScrollProgress {
        void onNotifyProgress(int progress);
    }

    private int mProgressScroll;
    @Override
    protected void findViews() {
        mSmartHomeAll = mInflate.findViewById(R.id.smart_home_all);
        mIvHomeAllTop = mInflate.findViewById(R.id.iv_home_all_top);
        mSmartHomeHeaderAll = mInflate.findViewById(R.id.smart_home_header_all);
        mSmartHomeFooterAll = mInflate.findViewById(R.id.smart_home_footer_all);
        mRvHomeParentView = mInflate.findViewById(R.id.rv_home_all_product);//底部产品列表
        mVsCommonWevError = $(R.id.vs_common_web_error);
        mFlAllBottomLogin = $(R.id.fl_all_bottom_login);
        mIvHomeAllTop.setOnClickListener(this);
        $(R.id.tv_all_bottom_login).setOnClickListener(this);
        mRvHomeParentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRvHomeParentView, mToPosition);
                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition == 0) {
                        mIvHomeAllTop.setVisibility(View.INVISIBLE);
                    } else {
                        mIvHomeAllTop.setVisibility(View.VISIBLE);
                    }

                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    if (lastItemPosition > shuaxin) {
                        if (mHomeRvAdapter != null) {
                            page++;
                            getProductList();
                            shuaxin = shuaxin + 20;
                        }
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mProgressScroll += dy;
                if (mINotifyScrollProgress != null) {
                    mINotifyScrollProgress.onNotifyProgress(mProgressScroll);
                }
            }
        });

        $(R.id.tv_common_hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllData();
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
        mSmartHomeAll.setOnRefreshLoadMoreListener(this);
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
    @Override
    protected void getData() {
        //smart设置属性，设置自动刷新，调用刷新方法
        mSmartHomeAll.autoRefresh(100);
        if (UserHelper.getInstence().isLogin()){
            mFlAllBottomLogin.setVisibility(View.GONE);
        }else {
            mFlAllBottomLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新数据
     */
    private void getAllData() {
        ApiCancleManager.getInstance().removeAll();
        Map<String, String> allParam = new HashMap<>();

        NetWork.getInstance()
                .setTag(Qurl.homePageShowAll)
                .getApiService(HomeApi.class)
                .homePageShowAll(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<HomeAllBean>(getActivity(), false, false) {

                    @Override
                    public void onSuccess(HomeAllBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        isNeedRefreshSpike = true;
                        setRefreshLoadMoreState(true, false);
                        //set data
                        setHomeAdapter(result);
                        page++;
                        getProductList();
                        mLoadError = false;
                        mVsCommonWevError.setVisibility(View.GONE);
                        mRvHomeParentView.setVisibility(View.VISIBLE);
                        /*//设置banner数据
                        setBannerData(result.indexBannerList);

                        //设置导航数据
                        setNavData(result.guideBannerList);

                        //设置红包集合
                        setRedPackList(result.activityImgList);

                        //设置textView滚动
                        setMarqueeView(result.broadcastList);

//                        mTvHomeAllBrand.setVisibility(TextUtils.isEmpty(result.advertName) ? View.GONE : View.VISIBLE);
//                        mTvHomeAllBrand.setText(TextUtils.isEmpty(result.advertName) ? "" : result.advertName);
                        //品牌热卖
                        setBrandHot(result.advertProductList);

                        //设置中间栏目
                        setMiddleLanMu(result.programaClass, result.programaImgList);

                        //抢购时间列表
                        setQiangGouData(result.rushTimeList);

                        //设置底部商品数据
                        setProductData(result.productList);*/
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                        if (mLoadError && moreInfo.indexOf("无网络") != -1) {
                            mVsCommonWevError.setVisibility(View.VISIBLE);
                            mRvHomeParentView.setVisibility(View.GONE);
                        } else {
                            mVsCommonWevError.setVisibility(View.GONE);
                            mRvHomeParentView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    /**
     * 设置adapter数据
     *
     * @param result
     */
    private List<HomeAllEntity> mHomeAllEntities;
    private HomeRvAdapter mHomeRvAdapter;

    private void setHomeAdapter(HomeAllBean result) {
        //注意这里需要组装数据，
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        if (page == 0) {
            mHomeAllEntities.clear();
            HomeAllEntity homeAllEntity = new HomeAllEntity(HomeAllEntity.TYPE_HEADER);
            //更新首页banner数据
            homeAllEntity.indexBannerList = result.indexBannerList;

            //更新首页导航数据
            homeAllEntity.guideBannerList = result.guideBannerList;

            //海报图
            homeAllEntity.placardBanner = result.placardBanner;

            //活动数据
            homeAllEntity.activityImgList = result.activityImgList;

            //设置textView滚动
            homeAllEntity.broadcastList = result.broadcastList;

            //品牌热卖
            homeAllEntity.advertProductList = result.advertProductList;

            //设置中间栏目
            homeAllEntity.programaImgList = result.programaImgList;

            //抢购时间列表
            homeAllEntity.rushTimeList = result.rushTimeList;

            //24小时热搜
            homeAllEntity.hotSaleProductList = result.hotSaleProductList;

            //大牌专场
            homeAllEntity.boutiqueProductList = result.boutiqueProductList;

            //今日必买
            homeAllEntity.todayProductList = result.todayProductList;

            //限时秒杀
            homeAllEntity.rushProductList = result.rushProductList;
            //新栏目
            homeAllEntity.moduleColumnHeadList = result.moduleColumnHeadList;
            //滑动
            homeAllEntity.middleBannerList = result.middleBannerList;
            //新增五个格子
            homeAllEntity.moduleColumnBelowList = result.moduleColumnBelowList;
            //0元夺宝
            homeAllEntity.activeCover = result.activeCover;
            //爆款必抢
            homeAllEntity.hotSaleProduct = result.hotSaleProduct;


            homeAllEntity.countDown = result.countDown;
            homeAllEntity.advertName = result.advertName;
            homeAllEntity.programaClass = result.moduleColumnHeadList.size()-2;
            homeAllEntity.advertImg = result.advertImg;
            mHomeAllEntities.add(homeAllEntity);

        } else {
            if (result.productList == null || result.productList.size() <= 0) {
                setRefreshLoadMoreState(true, true);
            } else {
                mSmartHomeAll.setNoMoreData(false);
            }
            for (int i = 0; result.productList != null && i < result.productList.size(); i++) {
                HomeAllEntity homeAllProduct = new HomeAllEntity(HomeAllEntity.TYPE_PRODUCT);
                homeAllProduct.mProductListBean = result.productList.get(i);
                mHomeAllEntities.add(homeAllProduct);
            }
        }

        if (mHomeRvAdapter == null) {
            mHomeRvAdapter = new HomeRvAdapter<HomeAllEntity, BaseViewHolder>(mHomeAllEntities) {


                @Override
                protected void addItemTypeView() {
                    addItemType(HomeAllEntity.TYPE_HEADER, R.layout.home_item_head);
                    addItemType(HomeAllEntity.TYPE_PRODUCT, R.layout.item_all_product);
                }

                @Override
                protected void convert(BaseViewHolder helper, HomeAllEntity item) {

                    if (helper.getItemViewType() == HomeAllEntity.TYPE_HEADER) {

                        helper.setText(R.id.tv_home_all_brand, item.advertName);

                        setBannerData(helper, item.indexBannerList);
                        //滑动
                        setHuaDong(helper, item.middleBannerList);

                        setNavData(helper, item.guideBannerList);

                        setPlacebanner(helper, item.placardBanner);

                        //品牌闪购图片
//                        setRedPackList(helper, item.activityImgList);

                        setMarqueeView(helper, item.broadcastList);

                        setBrandHot(helper, item.advertProductList,item.advertImg);

                        setSpike(helper, item.rushProductList, item.countDown);

//                        setQiangGouData(helper, item.rushTimeList);

//                        setMiddleLanMu(helper, item.programaClass, item.programaImgList);
                        //新栏目
//                        setMiddleLanMu(helper, item.programaClass, item.moduleColumnHeadList);
                        setNewLanMu(helper, item.moduleColumnHeadList);
                        //新格子
                        setNewFourLanMu(helper, item.moduleColumnBelowList);
//                        //0元抢购
//                        setActiveCover(helper, item.activeCover);
//                        //爆款必抢
//                        setBaoKuan(helper, item.hotSaleProduct);

//                        setNewMiddleLanMu(helper,item.moduleColumnHeadList);

                    } else if (helper.getItemViewType() == HomeAllEntity.TYPE_PRODUCT) {

                        try {
                            //商品图片
                            ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_all_product_tupian);
                            Glide.with(BaseApplication.getApplication()).load(item.mProductListBean.img).placeholder(R.drawable.bg_common_img_null).into(ivItemAllFenLei);

                            helper.getView(R.id.iv_video).setVisibility(item.mProductListBean.isVideo.equals("1") ? View.VISIBLE : View.GONE);
                            //设置来源和标题
                            SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_item_all_product_biaoti);
                            spannelTextViewMore.setDrawText(item.mProductListBean.productName);
                            spannelTextViewMore.setShopType(item.mProductListBean.shopType);

                            //补贴佣金
                            helper.setText(R.id.tv_item_all_product_butie_yongjin, "补贴佣金  ¥暂无显示");

                            //根据后台返的数据做判断，改成一下的自己做判断
                            /*if (BaseApplication.getApplication().mQueryShowHide) {
                                //预估佣金和升级赚
                                if (TextUtils.isEmpty(item.mProductListBean.zhuanMoney) || TextUtils.isEmpty(item.mProductListBean.upZhuanMoney)) {
                                    helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.mProductListBean.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                    helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.mProductListBean.zhuanMoney) ? View.GONE : View.VISIBLE);
                                } else {
                                    helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(View.VISIBLE);
                                    helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(View.GONE);
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
                                TextView yujizhuan=helper.getView( R.id.tv_item_all_product_single_yugu_yongjin );
                                if (UserHelper.getInstence().isLogin()){
                                    if (UserHelper.getInstence().getUserInfo().getAgencyLevel()==4){
                                        yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom4 ) );
                                    }else {
                                        yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom ) );
                                    }
                                }else {
                                    yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom ) );
                                }
                                helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(isAllShow ? View.GONE : View.GONE);

//                                helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.INVISIBLE);
//                                helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.INVISIBLE);
//                                if (!isAllShow) {
//                                    helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.INVISIBLE);
//                                }

                            }


                            helper.setText(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin, "升级赚 ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney)+"  ");
                            helper.setText(R.id.tv_item_all_product_single_yugu_yongjin, "  预计赚 ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));
//                            helper.setText(R.id.tv_all_product_sheng_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney));
//                            helper.setText(R.id.tv_item_all_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));

                            //券金额
                            helper.getView(R.id.tv_item_all_product_quan).setVisibility(item.mProductListBean.couponMoney == 0 ? View.GONE : View.VISIBLE);
                            helper.setText(R.id.tv_item_all_product_quan, "¥" + StringUtils.doubleToStringDeleteZero(item.mProductListBean.couponMoney) + "劵");
//                            helper.getView(R.id.tv_item_all_product_quan_txt).setVisibility(item.mProductListBean.couponMoney == 0 ? View.GONE : View.VISIBLE);


                            //现价(券后价)
                            helper.setText(R.id.tv_item_all_product_xianjia, "" + StringUtils.stringToStringDeleteZero(item.mProductListBean.preferentialPrice));

                            //渠道
//                            helper.setText(R.id.tv_item_all_product_yuanjia_type, item.mProductListBean.shopType == 1 ? "淘宝价 " : "天猫价 ");
                            //原价
                            helper.setText(R.id.tv_item_all_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.mProductListBean.price));
                            //去除中间横线
                            ((TextView) helper.getView(R.id.tv_item_all_product_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                            //销量
                            helper.setText(R.id.tv_item_all_product_xiaoliang, "" + StringUtils.intToStringUnit(item.mProductListBean.nowNumber) + "人已买");


                        } catch (Exception e) {
                            Logger.e("---->", e.toString());
                        }
                    }
                }
            };

            mHomeRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
            });
            mRvHomeParentView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            mRvHomeParentView.setAdapter(mHomeRvAdapter);
        } else {
            mHomeRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
            });
            mHomeRvAdapter.notifyDataSetChanged();
        }
        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvHomeParentView.scrollToPosition(0);
        }

    }

    private void setPlacebanner(BaseViewHolder helper, final HomeAllBean.PlacardBannerBean placardBanner) {
        ImageView ivHomeAllPlaceBanner = helper.getView(R.id.iv_home_all_placebanner);

        if (placardBanner == null){
            ivHomeAllPlaceBanner.setVisibility(View.GONE);
            return;
        }
        ivHomeAllPlaceBanner.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(placardBanner.img).into(ivHomeAllPlaceBanner);
        ivHomeAllPlaceBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placardBanner.type == 7) {
                    smoothMoveToPosition(mRvHomeParentView, 1);
                    return;
                }
                RouterBean routerBean = new RouterBean();
                routerBean.img = placardBanner.img;
                routerBean.link = placardBanner.link;
                routerBean.type = placardBanner.type;
                routerBean.mustParam = placardBanner.mustParam;
                routerBean.attachParam = placardBanner.attachParam;
                routerBean.rootName = placardBanner.rootName;
                routerBean.obJump = placardBanner.obJump;
                routerBean.linkAllows = placardBanner.linkAllows;
                routerBean.commandCopy = placardBanner.commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
            }
        });
    }


    /**
     * 设置banner数据
     */
    private List<String> mBannderData = new ArrayList<>();
    private List<Object> bgList = new ArrayList<>();

    private void setBannerData(BaseViewHolder helper, final List<HomeAllBean.IndexBannerListBean> indexBannerList) {
        if (indexBannerList == null || indexBannerList.size() <= 0) {
            return;
        }
        MZBannerView mBannerHomeAll = helper.getView(R.id.banner_home_all);

        if (mBannderData == null) {
            mBannderData = new ArrayList<>();
        }
        mBannderData.clear();

        for (int i = 0; i < indexBannerList.size(); i++) {
            mBannderData.add(indexBannerList.get(i).img);
        }

        if (bgList == null) {
            bgList = new ArrayList<>();
        }
        bgList.clear();

        for (int i = 0; i < indexBannerList.size(); i++) {
            if (TextUtils.isEmpty(indexBannerList.get(i).slipImg)){
                bgList.add(R.drawable.bg_all_banner);
            }else {
                bgList.add(indexBannerList.get(i).slipImg);
            }

        }
        if (mBannerBgContainer != null){
            mBannerBgContainer.setBannerBackBg(getActivity(), bgList);
            mBannerHomeAll.setBannerBgContainer(mBannerBgContainer);
        }
        mBannerHomeAll.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                HomeAllBean.IndexBannerListBean indexBannerListBean = indexBannerList.get(i);
                if (indexBannerListBean.type == 7) {
                    smoothMoveToPosition(mRvHomeParentView, 1);
                    return;
                }
//                if (indexBannerListBean.type == 36){//跳转活动
//                    RouterBean routerBean = new RouterBean();
////                    routerBean.img = indexBannerListBean.img;
////                    routerBean.link = indexBannerListBean.link;
//                    routerBean.type = indexBannerListBean.type;
//                    routerBean.attachParam = indexBannerListBean.attachParam;
//                    routerBean.thisActivity = getActivity();
////                    routerBean.mustParam = indexBannerListBean.mustParam;
////                    routerBean.attachParam = indexBannerListBean.attachParam;
////                    routerBean.rootName = indexBannerListBean.rootName;
////                    routerBean.obJump = indexBannerListBean.obJump;
////                    routerBean.linkAllows = indexBannerListBean.linkAllows;
////                    routerBean.commandCopy = indexBannerListBean.commandCopy;
//                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
//                    return;
//                }
                RouterBean routerBean = new RouterBean();
                routerBean.img = indexBannerListBean.img;
                routerBean.link = indexBannerListBean.link;
                routerBean.type = indexBannerListBean.type;
                routerBean.mustParam = indexBannerListBean.mustParam;
                routerBean.attachParam = indexBannerListBean.attachParam;
                routerBean.rootName = indexBannerListBean.rootName;
                routerBean.obJump = indexBannerListBean.obJump;
                routerBean.linkAllows = indexBannerListBean.linkAllows;
                routerBean.commandCopy = indexBannerListBean.commandCopy;
                routerBean.customParam = indexBannerListBean.customParam;
                routerBean.thisActivity = getActivity();
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
            }
        });
        mBannerHomeAll.setIndicatorVisible(true);
//        mBannerHomeAll.setIndicatorOtherVisible(true);
        mBannerHomeAll.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);
//        mBannerHomeAll.setIndicatorRes( com.zhouwei.mzbanner.R.drawable.indicator_other_normal, com.zhouwei.mzbanner.R.drawable.indicator_other_selected );
        mBannerHomeAll.setPages(mBannderData, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        mBannerHomeAll.start();
    }
    private RecyclerView mHeadRecyclerView;
    private List<HomeAllBean.MiddleBannerList> mProductHuoreList;
    private BaseQuickAdapter<HomeAllBean.MiddleBannerList, BaseViewHolder> mBaseProductQuickAdapter;
    private RelativeLayout mHuaDong;
    private void setHuaDong(BaseViewHolder helper, final List<HomeAllBean.MiddleBannerList> middleBannerList) {
        mHuaDong=helper.getView( R.id.huadong );
        if (middleBannerList == null || middleBannerList.size() <= 0) {
            mHuaDong.setVisibility( View.GONE );
            return;
        }
        mHuaDong.setVisibility( View.VISIBLE );
        mHeadRecyclerView=helper.getView( R.id.rl_zhuanti_jingxuan_h );
        if (mProductHuoreList == null) {
            mProductHuoreList = new ArrayList<> ();
        }
        if (page == 1) {
            mProductHuoreList.clear ();
        }
        for (int i =0;i<middleBannerList.size();i++){
            mProductHuoreList.add( middleBannerList.get( i ) );
        }
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<HomeAllBean.MiddleBannerList, BaseViewHolder>(R.layout.item_huadong, mProductHuoreList) {
                @Override
                protected void convert(BaseViewHolder helper, final HomeAllBean.MiddleBannerList item) {
                    try {
                        //会员头像
                        ImageView ivItemAllFenLei = helper.getView(R.id.img_item);
                        Glide.with(BaseApplication.getApplication())
                                .load(item.img)
//                                .placeholder(R.drawable.ic_common_user_def)
                                .transform(new GlideRoundTransform (BaseApplication.getApplication(), 0))
                                .into(ivItemAllFenLei);


                        ivItemAllFenLei.setOnClickListener ( new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
//                                if (item.type == 36){//跳转活动
//                                    RouterBean routerBean = new RouterBean();
////                    routerBean.img = indexBannerListBean.img;
////                    routerBean.link = indexBannerListBean.link;
//                                    routerBean.type = item.type;
//                                    routerBean.attachParam = item.attachParam;
//                                    routerBean.thisActivity = getActivity();
////                    routerBean.mustParam = indexBannerListBean.mustParam;
////                    routerBean.attachParam = indexBannerListBean.attachParam;
////                    routerBean.rootName = indexBannerListBean.rootName;
////                    routerBean.obJump = indexBannerListBean.obJump;
////                    routerBean.linkAllows = indexBannerListBean.linkAllows;
////                    routerBean.commandCopy = indexBannerListBean.commandCopy;
//                                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
//                                    return;
//                                }
                                RouterBean routerBean = new RouterBean();
                                routerBean.img = item.img;
                                routerBean.link = item.link;
                                routerBean.type = item.type;
                                routerBean.mustParam = item.mustParam;
                                routerBean.attachParam = item.attachParam;
                                routerBean.rootName = item.rootName;
                                routerBean.commandCopy = item.commandCopy;
                                routerBean.attachParam = item.attachParam;
                                routerBean.rootName = item.rootName;
                                routerBean.commandCopy = item.commandCopy;
                                routerBean.id = item.id;
                                routerBean.thisActivity = getActivity();
                                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);




//                                if (item.link!=null||!item.equals( "" )){
//                                    ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
//                                            .withString("loadUrl", Qurl.HOST + item.link+item.mustParam)
//                                            .withString(Constant.TITLE, TextUtils.isEmpty(item.rootName) ? "" : item.rootName)
//                                            .navigation();
//                                }
//                                RouterBean routerBean = new RouterBean();
//                                routerBean.img = item.img;
//                                routerBean.link = item.link;
//                                routerBean.type = item.type;
//                                routerBean.mustParam = item.mustParam;
//                                routerBean.attachParam = item.attachParam;
//                                routerBean.rootName = item.rootName;
//                                routerBean.commandCopy = item.commandCopy;
//                                routerBean.attachParam = item.attachParam;
//                                routerBean.rootName = item.rootName;
//                                routerBean.commandCopy = item.commandCopy;
//                                routerBean.id = item.id;
//                                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                                //跳转新页面
//                                ARouter.getInstance().build(ARouters.PATH_MYFENSI).withString ( "userId",userId+"" ).navigation();
                            }
                        } );


                    } catch (Exception e) {

                    }
                }
            };
            mHeadRecyclerView.setHasFixedSize(true);
            mHeadRecyclerView.setNestedScrollingEnabled(false);
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
//            mHeadRecyclerView.setLayoutManager(gridLayoutManager);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
            linearLayoutManager.setOrientation( LinearLayoutManager.HORIZONTAL );
            mHeadRecyclerView.setLayoutManager( linearLayoutManager );
            mHeadRecyclerView.setAdapter(mBaseProductQuickAdapter);
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * 设置九宫格导航数据
     *
     * @param guideBannerList
     */
    private List<HomeAllBean.GuideBannerListBean> mGuideBannerList;
    private BaseQuickAdapter<HomeAllBean.GuideBannerListBean, BaseViewHolder> mBaseFenLeiQuickAdapter;

    private void setNavData(BaseViewHolder helper, final List<HomeAllBean.GuideBannerListBean> guideBannerList) {
        if (guideBannerList == null || guideBannerList.size() <= 0) {
            return;
        }
        RecyclerView rvHomeAllFenLei = helper.getView(R.id.rv_home_all_fenlei);
        if (mGuideBannerList == null) {
            mGuideBannerList = new ArrayList<>();
        }

        mGuideBannerList.clear();
        mGuideBannerList.addAll(guideBannerList);
        int spanCount = mGuideBannerList.size();

        if (mBaseFenLeiQuickAdapter == null) {
            mBaseFenLeiQuickAdapter = new BaseQuickAdapter<HomeAllBean.GuideBannerListBean, BaseViewHolder>(R.layout.item_all_fenlei, mGuideBannerList) {
                @Override
                protected void convert(BaseViewHolder helper, HomeAllBean.GuideBannerListBean item) {
                    helper.setText(R.id.tv_item_all_fenlei, item.rootName);
                    ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_all_fenlei);
                    Glide.with(BaseApplication.getApplication()).load(item.img).placeholder(R.drawable.ic_common_jiugong_img_null).into(ivItemAllFenLei);
                }
            };
            rvHomeAllFenLei.setHasFixedSize(true);
            rvHomeAllFenLei.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount > 5 ? 5 : spanCount);
            rvHomeAllFenLei.setLayoutManager(gridLayoutManager);

            rvHomeAllFenLei.setAdapter(mBaseFenLeiQuickAdapter);
//九宫格点击事件 todo
            mBaseFenLeiQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

//                    ARouter.getInstance().build(ARouters.PATH_PRODUCT)
//                            .withString(Constant.PROGRAMAID, guideBannerList.get(position).rootId)
//                            .withString(Constant.TITLE, guideBannerList.get(position).rootName)
//                            .navigation();

                    HomeAllBean.GuideBannerListBean guideBannerListBean = guideBannerList.get(position);
                    if (guideBannerListBean.type == 7) {
                        smoothMoveToPosition(mRvHomeParentView, 1);
                        return;
                    }

                    RouterBean routerBean = new RouterBean();
                    routerBean.img = guideBannerListBean.img;
                    routerBean.link = guideBannerListBean.link;
                    routerBean.type = guideBannerListBean.type;
                    routerBean.mustParam = guideBannerListBean.mustParam;
                    routerBean.attachParam = guideBannerListBean.attachParam;
                    routerBean.rootName = guideBannerListBean.rootName;
                    routerBean.obJump = guideBannerListBean.obJump;
                    routerBean.linkAllows = guideBannerListBean.linkAllows;
                    routerBean.commandCopy = guideBannerListBean.commandCopy;
                    routerBean.thisActivity = getActivity();
                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);

                }
            });


        } else {
            //九宫格点击事件 todo
            mBaseFenLeiQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

//                    ARouter.getInstance().build(ARouters.PATH_PRODUCT)
//                            .withString(Constant.PROGRAMAID, guideBannerList.get(position).rootId)
//                            .withString(Constant.TITLE, guideBannerList.get(position).rootName)
//                            .navigation();

                    HomeAllBean.GuideBannerListBean guideBannerListBean = guideBannerList.get(position);
                    if (guideBannerListBean.type == 7) {
                        smoothMoveToPosition(mRvHomeParentView, 1);
                        return;
                    }

                    RouterBean routerBean = new RouterBean();
                    routerBean.img = guideBannerListBean.img;
                    routerBean.link = guideBannerListBean.link;
                    routerBean.type = guideBannerListBean.type;
                    routerBean.mustParam = guideBannerListBean.mustParam;
                    routerBean.attachParam = guideBannerListBean.attachParam;
                    routerBean.rootName = guideBannerListBean.rootName;
                    routerBean.obJump = guideBannerListBean.obJump;
                    routerBean.linkAllows = guideBannerListBean.linkAllows;
                    routerBean.commandCopy = guideBannerListBean.commandCopy;
                    routerBean.thisActivity = getActivity();
                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);

                }
            });
            mBaseFenLeiQuickAdapter.notifyDataSetChanged();
        }


    }


    /**
     * 设置红包集合
     */
    private List<String> mBannderRedPackData = new ArrayList<>();

    private void setRedPackList(BaseViewHolder helper, final List<HomeAllBean.ActivityImgListBean> activityImgList) {
        MZBannerView bannerHomeAllRedpage = helper.getView(R.id.banner_home_all_redpage);
        if (activityImgList == null || activityImgList.size() <= 0) {
            bannerHomeAllRedpage.setVisibility(View.GONE);
            return;
        }
        bannerHomeAllRedpage.setVisibility(View.VISIBLE);
        if (mBannderRedPackData == null) {
            mBannderRedPackData = new ArrayList<>();
        }
        mBannderRedPackData.clear();
        for (int i = 0; i < activityImgList.size(); i++) {
            mBannderRedPackData.add(activityImgList.get(i).img);
        }
        bannerHomeAllRedpage.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {

                HomeAllBean.ActivityImgListBean indexBannerListBean = activityImgList.get(i);
//                if (indexBannerListBean.type == 7) {
//                    smoothMoveToPosition(mRvHomeParentView, 1);
//                    return;
//                }
//                RouterBean routerBean = new RouterBean();
//                routerBean.img = indexBannerListBean.img;
//                routerBean.link = indexBannerListBean.link;
//                routerBean.type = indexBannerListBean.type;
//                routerBean.mustParam = indexBannerListBean.mustParam;
//                routerBean.attachParam = indexBannerListBean.attachParam;
//                routerBean.rootName = indexBannerListBean.rootName;
//                routerBean.obJump = indexBannerListBean.obJump;
//                routerBean.linkAllows = indexBannerListBean.linkAllows;
//                routerBean.commandCopy = indexBannerListBean.commandCopy;
//                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                //活动banner跳转品牌闪购
                ARouter.getInstance().build(ARouters.PATH_BRAND_ACTIVITY).navigation();
                /*ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", Qurl.HOST + activityImgList.get(i).activeUrl)
                        .withString(Constant.TITLE, TextUtils.isEmpty(activityImgList.get(i).activeName) ? "" : activityImgList.get(i).activeName)
                        .navigation();*/
            }
        });
        if (mBannderRedPackData.size() > 1) {
            bannerHomeAllRedpage.setCanLoop(true);
        } else {
            bannerHomeAllRedpage.setCanLoop(false);
        }
        bannerHomeAllRedpage.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);
        bannerHomeAllRedpage.setPages(mBannderRedPackData, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        bannerHomeAllRedpage.start();
    }


    /**
     * 设置跑马灯滚动数据
     *
     * @param broadcastList
     */
    private List<CharSequence> mMarqueeData = new ArrayList<>();

    private void setMarqueeView(BaseViewHolder helper, List<HomeAllBean.BroadcastListBean> broadcastList) {
        LinearLayout llHomeKuaiBao = helper.getView(R.id.ll_home_kuaibao);
        MarqueeView tvScrollHomeAll = helper.getView(R.id.tv_scroll_home_all);
        if (broadcastList == null || broadcastList.size() <= 0) {
            llHomeKuaiBao.setVisibility(View.GONE);
            return;
        }
        llHomeKuaiBao.setVisibility(View.VISIBLE);
        if (mMarqueeData == null) {
            mMarqueeData = new ArrayList<>();
        }
        mMarqueeData.clear();
        for (int i = 0; i < broadcastList.size(); i++) {
            String nickName = broadcastList.get(i).nickName;
            //后台处理
//            if (nickName.length() >= 4) {
//                nickName = nickName.substring(0, 1) + "**" + nickName.substring(nickName.length() - 2, nickName.length() - 1);
//            }
            String event = broadcastList.get(i).event;
            String award = broadcastList.get(i).award;

            SpannableString ss1 = new SpannableString(nickName + event + award);
//            ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_F13B3A)), 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int before = nickName.length() + event.length();
            ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_FF6100)), before, before + award.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    /**
     * 限时秒杀
     *
     * @param helper
     * @param rushProductList
     */
    private BaseQuickAdapter<HomeAllBean.RushProductList, BaseViewHolder> mRvHomeAllSpikeProductAdapter;

    private void setSpike(final BaseViewHolder helper, List<HomeAllBean.RushProductList> rushProductList, long countDown) {
        RelativeLayout rlSpikeParent = helper.getView(R.id.rl_spike_parent);
        RelativeLayout rlGoodsBuy = helper.getView(R.id.rl_goods_buy);
        ImageView ivGoodsBuyImg = helper.getView(R.id.iv_goods_buy_img);
        TextView tvGoodsBuyName = helper.getView(R.id.tv_goods_buy_name);
        RecyclerView rvHomeAllSpikeProduct = helper.getView(R.id.rv_home_all_spike_product);

        if (rushProductList == null || rushProductList.size() <= 0) {
            rlSpikeParent.setVisibility(View.GONE);
            return;
        }
        rlSpikeParent.setVisibility(View.VISIBLE);
        final HomeAllBean.RushProductList rushProductList0 = rushProductList.get(0);
        Glide.with(BaseApplication.getApplication()).load(rushProductList0.pic).placeholder(R.drawable.bg_common_img_null).into(ivGoodsBuyImg);
        tvGoodsBuyName.setText(rushProductList0.d_title);
        helper.setText(R.id.tv_goods_buy_xianjia_xiaoliang, "" + StringUtils.stringToStringDeleteZero(rushProductList0.xiaoliang));
        helper.setText(R.id.tv_goods_buy_xianjia, StringUtils.stringToStringDeleteZero(rushProductList0.jiage));
        helper.setText(R.id.tv_goods_buy_yuanjia, "¥" + StringUtils.stringToStringDeleteZero(rushProductList0.yuanjia));
        TextView view = helper.getView(R.id.tv_goods_buy_yuanjia);
        view.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
        helper.setText(R.id.tv_goods_buy_xianjia_quan, "下单立减" + StringUtils.doubleToStringDeleteZero(rushProductList0.quan_jine) + "元");

        helper.getView(R.id.tv_spike_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_FLASH_SALE).navigation();
            }
        });
        //马上抢
        rlGoodsBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTbItemId(rushProductList0.id);

            }
        });
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(countDown, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    long day = millisUntilFinished / (1000 * 60 * 60 * 24);
                    long hour = (millisUntilFinished - day * millisUntilFinished / (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                    long minute = (millisUntilFinished - day * millisUntilFinished / (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);
                    long second = (millisUntilFinished - day * millisUntilFinished / (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;


                    helper.setText(R.id.tv_spike_hour, hour < 10 ? ("0" + hour) : (hour + ""));
                    helper.setText(R.id.tv_spike_minute, minute < 10 ? ("0" + minute) : (minute + ""));
                    helper.setText(R.id.tv_spike_second, second < 10 ? ("0" + second) : (second + ""));

                }

                @Override
                public void onFinish() {

                }
            };
            mCountDownTimer.start();
        } else if (isNeedRefreshSpike){
            mCountDownTimer.onTick(countDown);
//            mCountDownTimer.start();
        }
        isNeedRefreshSpike = false;


        final List<HomeAllBean.RushProductList> rushProductListSub = rushProductList.subList(1, rushProductList.size());
        if (rushProductListSub == null || rushProductListSub.size() <= 0) {
            rvHomeAllSpikeProduct.setVisibility(View.GONE);
            return;
        }
        rvHomeAllSpikeProduct.setVisibility(View.VISIBLE);
        if (mRvHomeAllSpikeProductAdapter == null) {
            mRvHomeAllSpikeProductAdapter = new BaseQuickAdapter<HomeAllBean.RushProductList, BaseViewHolder>(R.layout.layout_all_goods_item, rushProductListSub) {

                @Override
                protected void convert(BaseViewHolder helper, HomeAllBean.RushProductList item) {
                    ImageView ivGoodsItemBuyImg = helper.getView(R.id.iv_goods_item_buy_img);
                    Glide.with(BaseApplication.getApplication()).load(item.pic).placeholder(R.drawable.bg_common_img_null).into(ivGoodsItemBuyImg);

                    helper.setText(R.id.tv_goods_buy_name, item.d_title);
                    helper.setText(R.id.tv_goods_buy_item_xiaoliang, "" + StringUtils.stringToStringDeleteZero(item.xiaoliang));
                    helper.setText(R.id.tv_goods_buy_item_xianjia, "¥" + StringUtils.stringToStringDeleteZero(item.jiage));
                    TextView tvGoodsBuyYuanJia = helper.getView(R.id.tv_goods_buy_yuanjia);
                    tvGoodsBuyYuanJia.setText("¥" + StringUtils.stringToStringDeleteZero(item.yuanjia));
                    tvGoodsBuyYuanJia.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
                }
            };
            rvHomeAllSpikeProduct.setHasFixedSize(true);
            rvHomeAllSpikeProduct.setNestedScrollingEnabled(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rvHomeAllSpikeProduct.setLayoutManager(linearLayoutManager);
            rvHomeAllSpikeProduct.setAdapter(mRvHomeAllSpikeProductAdapter);
            mRvHomeAllSpikeProductAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    getTbItemId(rushProductListSub.get(position).id);
                }
            });
        } else {
            mRvHomeAllSpikeProductAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    getTbItemId(rushProductListSub.get(position).id);
                }
            });
            mRvHomeAllSpikeProductAdapter.notifyDataSetChanged();
        }


    }

    public void getTbItemId(final String id){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put( "id",id );
        NetWork.getInstance()
                .setTag( Qurl.flashSaleId )
                .getApiService( HomeApi.class )
                .getFlashSaleId( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<FlashSaleIdBean>( getActivity(), true, true ) {
                    @Override
                    public void onSuccess(FlashSaleIdBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState( true, true );
                            return;
                        }
                        setRefreshLoadMoreState( true, false );

                        RouterBean routerBean = new RouterBean();
                        routerBean.type = 9;
//                                    routerBean.tbCouponId = mHomeAllEntities.get(position).mProductListBean.tbCouponId;
                                    /*routerBean.mustParam = "type=5"
                                            + "&id=" + id
                                            + "&tbItemId=" + result.prod.goodsID;*/
                        routerBean.mustParam = "type=5"
                                + "&tbItemId=" + result.prod.goodsID;
                        LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed( errorCode, moreInfo );
                        setRefreshLoadMoreState( false, false );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError( e );
                        setRefreshLoadMoreState( false, false );
                    }
                } );
    }


    /**
     * 设置品牌热卖
     *
     * @param advertProductList
     */
    private List<HomeAllBean.AdvertProductListBean> mAdvertProductList;
    private BaseQuickAdapter<HomeAllBean.AdvertProductListBean, BaseViewHolder> mBaseBrandHotQuickAdapter;

    private void setBrandHot(BaseViewHolder helper, List<HomeAllBean.AdvertProductListBean> advertProductList, String advertImg) {

        RecyclerView rvHomeAllHotProduct = helper.getView(R.id.rv_home_all_hot_product);
        View cvHomeAllBrand = helper.getView(R.id.cv_home_all_brand);
        ImageView ivHotImg = helper.getView(R.id.iv_hot_img);
        if ((advertProductList == null || advertProductList.size() <= 0 ) && TextUtils.isEmpty(advertImg)) {
            cvHomeAllBrand.setVisibility(View.GONE);
            return;
        }
        cvHomeAllBrand.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(advertImg)){
            ivHotImg.setVisibility(View.VISIBLE);
            ivHotImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build(ARouters.PATH_BRAND_ACTIVITY)
                            .navigation();
                }
            });
            Glide.with(BaseApplication.getApplication()).load(advertImg).into(ivHotImg);
        }else {
            ivHotImg.setVisibility(View.GONE);
        }



        if (advertProductList != null && advertProductList.size() > 0){
            rvHomeAllHotProduct.setVisibility(View.VISIBLE);

            if (mAdvertProductList == null) {
                mAdvertProductList = new ArrayList<>();
            }
            mAdvertProductList.clear();
            mAdvertProductList.addAll(advertProductList);
            mBaseBrandHotQuickAdapter = new BaseQuickAdapter<HomeAllBean.AdvertProductListBean, BaseViewHolder>(R.layout.item_all_brand_hot, mAdvertProductList) {
                @Override
                protected void convert(BaseViewHolder helper, HomeAllBean.AdvertProductListBean item) {
                    try {

                        //商品图片
                        ImageView ivItemAllBrandHot = helper.getView(R.id.tv_item_all_brand_hot_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.imgs).placeholder(R.drawable.bg_common_img_null).into(ivItemAllBrandHot);
                        //标题
                        helper.setText(R.id.tv_item_all_brand_hot_biaoti, item.productName);

                        //券金额

                        helper.getView(R.id.ll_all_product_brand_quan_parent).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
                        helper.setText(R.id.tv_item_all_brand_hot_quan, "" + StringUtils.doubleToStringDeleteZero(item.couponMoney) + "元券");

                        //券后价（现价）
                        helper.setText(R.id.tv_item_all_brand_hot_quanhoujia, "" + StringUtils.stringToStringDeleteZero(item.preferentialPrice) + "");


                    } catch (Exception e) {

                    }


                }
            };
            rvHomeAllHotProduct.setHasFixedSize(true);
            rvHomeAllHotProduct.setNestedScrollingEnabled(false);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            rvHomeAllHotProduct.setLayoutManager(gridLayoutManager);

            rvHomeAllHotProduct.setAdapter(mBaseBrandHotQuickAdapter);
            //今日爆款点击事件 todo
            mBaseBrandHotQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    ARouter.getInstance().build(ARouters.PATH_PRODUCT)
//                            .withString(Constant.CHASS_TYPE_ID, mAdvertProductList.get(position).name)
//                            .withString(Constant.TITLE, mAdvertProductList.get(position).rootName)
//                            .navigation();
                    RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.tbCouponId = mAdvertProductList.get(position).tbCouponId;
                    routerBean.mustParam = "type=1"
                            + "&id=" + mAdvertProductList.get(position).id
                            + "&tbItemId=" + mAdvertProductList.get(position).tbItemId;
//                    routerBean.t = "1";
//                    routerBean.id = mAdvertProductList.get(position).id;
//                    routerBean.i = mAdvertProductList.get(position).tbItemId;

                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                }
            });
        }else {
            rvHomeAllHotProduct.setVisibility(View.GONE);
        }

    }

    private void setNewLanMu(BaseViewHolder helper, final List<HomeAllBean.ModuleColumnHeadList> moduleColumnBelowList){
        mLanMu=helper.getView( R.id.ll_new_lanmu );
        if (moduleColumnBelowList==null||moduleColumnBelowList.size()<=0){
            mLanMu.setVisibility( View.GONE );
            return;
        }

        mLanMu.setVisibility( View.VISIBLE );
        mIvLanMu1=helper.getView( R.id.iv_item_all_lanmu_1 );
        mIvLanMu2=helper.getView( R.id.iv_item_all_lanmu_2 );
        mIvLanMu3=helper.getView( R.id.iv_item_all_lanmu_3 );
        mIvLanMu4=helper.getView( R.id.iv_item_all_lanmu_4 );
        mIvLanMu5=helper.getView( R.id.iv_item_all_lanmu_5 );
        mIvLists.add( mIvLanMu1 );
        mIvLists.add( mIvLanMu2 );
        mIvLists.add( mIvLanMu3 );
        mIvLists.add( mIvLanMu4 );
        mIvLists.add( mIvLanMu5 );
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(0).img).placeholder(R.drawable.bg_all_one).into(mIvLists.get(0));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(1).img).placeholder(R.drawable.bg_all_two).into(mIvLists.get(1));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(2).img).placeholder(R.drawable.bg_all_two).into(mIvLists.get(2));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(3).img).placeholder(R.drawable.bg_all_three).into(mIvLists.get(3));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(4).img).placeholder(R.drawable.bg_all_two).into(mIvLists.get(4));
        for (int i=0;i<moduleColumnBelowList.size();i++){
//            Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(i).img).placeholder(R.drawable.bg_common_img_null).into(mIvLists.get(i));
            final int finalI = i;
            mIvLists.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RouterBean routerBean = new RouterBean();
                    if (moduleColumnBelowList.get( finalI ).module==1){
                        routerBean.type = 5;
                        routerBean.programaId = moduleColumnBelowList.get(finalI).programaId;
                        routerBean.pname = moduleColumnBelowList.get(finalI).programaId;
                        routerBean.rootName = moduleColumnBelowList.get(finalI).pname;
                        routerBean.link = Qurl.programaShowAll;
                        routerBean.mustParam = "programaId=" + moduleColumnBelowList.get(finalI).programaId;
                        LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                    }else {
                        if (moduleColumnBelowList.get( finalI ).type==1){//跳转内部
                            routerBean.type = 36;
                            routerBean.attachParam = moduleColumnBelowList.get(finalI).attachParam;
                            routerBean.thisActivity = getActivity();
                            LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                        }else if (moduleColumnBelowList.get( finalI ).type==2){//跳转外部
                            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                    .withString("loadUrl", Qurl.HOST+moduleColumnBelowList.get( finalI ).activeUrl)
                                    .withString(Constant.TITLE, moduleColumnBelowList.get( finalI ).programaId)
                                    .withInt(Constant.TYPE, 4)
                                    .navigation();
                        }
                    }

                }
            });
        }


    }

    /**
     * 设置栏目数据
     *
     * @param programaCount   栏目数量
     * @param programaClass   栏目类别
     * @param programaImgList 栏目数据
     */
    private List<ImageView> mIvLists = new ArrayList<>();
    private ImageView mIvLanMu1, mIvLanMu2, mIvLanMu3, mIvLanMu4, mIvLanMu5, mIvLanMu6, mIvLanMu7, mIvLanMu8;
    private LinearLayout mLanMu;

    private void setMiddleLanMu(BaseViewHolder helper, int programaClass, final List<HomeAllBean.ModuleColumnHeadList> moduleColumnHeadList) {
        FrameLayout flHomeAllLanmu = helper.getView(R.id.fl_home_all_lanmu);
        if (moduleColumnHeadList == null || moduleColumnHeadList.size() <= 0) {
            flHomeAllLanmu.setVisibility(View.GONE);
            return;
        }
        flHomeAllLanmu.setVisibility(View.VISIBLE);
        try {
            mIvLanMu1 = null;
            mIvLanMu2 = null;
            mIvLanMu3 = null;
            mIvLanMu4 = null;
            mIvLanMu5 = null;
            mIvLanMu6 = null;
            mIvLanMu7 = null;
            mIvLanMu8 = null;
            if (mIvLists == null) {
                mIvLists = new ArrayList<>();
            }
            mIvLists.clear();

            View inflate = null;
            if (programaClass == 1) {//3
                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_3_0, flHomeAllLanmu);
            } else if (programaClass == 2) {//4
                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_4_0, flHomeAllLanmu);
            } else if (programaClass == 3) {//5
                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_5_1, flHomeAllLanmu);
//                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_5_2, flHomeAllLanmu);
//            }else if(programaClass == 3){//5
//                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_5_2, flHomeAllLanmu);
            } else if (programaClass == 4) {//6
                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_6_0, flHomeAllLanmu);
            } else if (programaClass == 5) {//7
                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_7_0, flHomeAllLanmu);
            } else if (programaClass == 6) {//8
                inflate = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.item_all_lanmu_8_0, flHomeAllLanmu);
            }
            mIvLanMu1 = inflate.findViewById(R.id.iv_item_all_lanmu_1);
            mIvLanMu2 = inflate.findViewById(R.id.iv_item_all_lanmu_2);
            mIvLanMu3 = inflate.findViewById(R.id.iv_item_all_lanmu_3);
            mIvLists.add(mIvLanMu1);
            mIvLists.add(mIvLanMu2);
            mIvLists.add(mIvLanMu3);
            if (programaClass > 1) {
                mIvLanMu4 = inflate.findViewById(R.id.iv_item_all_lanmu_4);
                mIvLists.add(mIvLanMu4);
            }
            if (programaClass > 2) {
                mIvLanMu5 = inflate.findViewById(R.id.iv_item_all_lanmu_5);
                mIvLists.add(mIvLanMu5);
            }
            if (programaClass > 3) {
                mIvLanMu6 = inflate.findViewById(R.id.iv_item_all_lanmu_6);
                mIvLists.add(mIvLanMu6);
            }
            if (programaClass > 4) {
                mIvLanMu7 = inflate.findViewById(R.id.iv_item_all_lanmu_7);
                mIvLists.add(mIvLanMu7);
            }
            if (programaClass > 5) {
                mIvLanMu8 = inflate.findViewById(R.id.iv_item_all_lanmu_8);
                mIvLists.add(mIvLanMu8);
            }

            if (mIvLists == null) {
                mIvLists = new ArrayList<>();
                mIvLists.clear();
            }

            for (int i = 0; i < moduleColumnHeadList.size(); i++) {
                Glide.with(BaseApplication.getApplication()).load(moduleColumnHeadList.get(i).img).into(mIvLists.get(i));
                final int finalI = i;
                mIvLists.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RouterBean routerBean = new RouterBean();
                        if (moduleColumnHeadList.get( finalI ).module==1){
                            routerBean.type = 5;
                            routerBean.programaId = moduleColumnHeadList.get(finalI).programaId;
                            routerBean.pname = moduleColumnHeadList.get(finalI).pname;
                            routerBean.rootName = moduleColumnHeadList.get(finalI).pname;
                            routerBean.link = Qurl.programaShowAll;
                            routerBean.mustParam = "programaId=" + moduleColumnHeadList.get(finalI).programaId;
                            LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                        }else {
                            if (moduleColumnHeadList.get( finalI ).type==1){//跳转内部
                                routerBean.type = 36;
                                routerBean.attachParam = moduleColumnHeadList.get(finalI).attachParam;
                                routerBean.thisActivity = getActivity();
                                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                            }else if (moduleColumnHeadList.get( finalI ).type==2){//跳转外部
                                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                        .withString("loadUrl", Qurl.HOST+moduleColumnHeadList.get( finalI ).activeUrl)
                                        .withString(Constant.TITLE, moduleColumnHeadList.get( finalI ).pname)
                                        .withInt(Constant.TYPE, 4)
                                        .navigation();
                            }
                        }

                    }
                });
            }


        } catch (Exception e) {

        }


    }
    private List<ImageView> mIvNewLists = new ArrayList<>();
    private ImageView mIvNewLanMu1, mIvNewLanMu2, mIvNewLanMu3, mIvNewLanMu4;
    private LinearLayout mFourGezi;
    private void setNewFourLanMu(BaseViewHolder helper, final List<HomeAllBean.ModuleColumnBelowList> moduleColumnBelowList){
        mFourGezi=helper.getView( R.id.ll_fourgezi );
        if (moduleColumnBelowList==null||moduleColumnBelowList.size()<=0){
            mFourGezi.setVisibility( View.GONE );
            return;
        }
        mFourGezi.setVisibility( View.VISIBLE );
        mIvNewLanMu1=helper.getView( R.id.cv_lanmu_one );
        mIvNewLanMu2=helper.getView( R.id.cv_lanmu_two );
        mIvNewLanMu3=helper.getView( R.id.cv_lanmu_three );
        mIvNewLanMu4=helper.getView( R.id.cv_lanmu_four );
        mIvNewLists.add( mIvNewLanMu1 );
        mIvNewLists.add( mIvNewLanMu2 );
        mIvNewLists.add( mIvNewLanMu3 );
        mIvNewLists.add( mIvNewLanMu4 );
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(0).img).placeholder(R.drawable.bg_all_four).into(mIvNewLists.get(0));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(1).img).placeholder(R.drawable.bg_all_five).into(mIvNewLists.get(1));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(2).img).placeholder(R.drawable.bg_all_six).into(mIvNewLists.get(2));
        Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(3).img).placeholder(R.drawable.bg_all_six).into(mIvNewLists.get(3));
        for (int i=0;i<moduleColumnBelowList.size();i++){
//            Glide.with(BaseApplication.getApplication()).load(moduleColumnBelowList.get(i).img).into(mIvNewLists.get(i));
            final int finalI = i;
            mIvNewLists.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RouterBean routerBean = new RouterBean();
                    if (moduleColumnBelowList.get( finalI ).module==1){
                        routerBean.type = 5;
                        routerBean.programaId = moduleColumnBelowList.get(finalI).programaId;
                        routerBean.pname = moduleColumnBelowList.get(finalI).programaId;
//                        routerBean.rootName = moduleColumnBelowList.get(finalI).pname;
                        routerBean.link = Qurl.programaShowAll;
                        routerBean.mustParam = "programaId=" + moduleColumnBelowList.get(finalI).programaId;
                        LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                    }else {
                        if (moduleColumnBelowList.get( finalI ).type==1){//跳转内部
                            routerBean.type = 36;
                            routerBean.attachParam = moduleColumnBelowList.get(finalI).attachParam;
                            routerBean.thisActivity = getActivity();
                            LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                        }else if (moduleColumnBelowList.get( finalI ).type==2){//跳转外部
                            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                    .withString("loadUrl", Qurl.HOST+moduleColumnBelowList.get( finalI ).activeUrl)
                                    .withString(Constant.TITLE, moduleColumnBelowList.get( finalI ).programaId)
                                    .withInt(Constant.TYPE, 4)
                                    .navigation();
                        }
                    }

                }
            });
        }


    }

    private void setActiveCover(BaseViewHolder helper, final HomeAllBean.ActiveCover moduleColumnBelowList){
        if (moduleColumnBelowList==null){
            RelativeLayout relativeLayout=helper.getView( R.id.tl_zerobuy_lanmu );
            relativeLayout.setVisibility( View.GONE );
            return;
        }
            LinearLayout mLlAll=helper.getView( R.id.ll_togo_zerobuy );
            ImageView mIvImage=helper.getView( R.id.iv_item_all_product_tupian );
            TextView mTvBiaozhu=helper.getView( R.id.tv_zuire_biaozhu );
            TextView mTvYuanjia=helper.getView( R.id.tv_zuire_yuanjia );
            TextView mTvPeopleNum=helper.getView( R.id.tv_zuire_peopleNum );
            Glide.with(BaseApplication.getApplication())
                    .load(moduleColumnBelowList.coverImg)
    //                                .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform (BaseApplication.getApplication(), 4))
                    .into(mIvImage);
        mTvBiaozhu.setText( moduleColumnBelowList.coverName );
        mTvYuanjia.setText( "原价￥"+moduleColumnBelowList.coverPrice );
        mTvPeopleNum.setText( ""+StringUtils.intToStringUnit(moduleColumnBelowList.coverActiveNumber)+"人正在夺宝" );
        mLlAll.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_MY_ZERO_BUY).navigation();
            }
        } );

//        helper.setText( R.id.tv_zuire_biaozhu ,moduleColumnBelowList.name);
//        helper.setText( R.id.tv_zuire_biaozhu ,moduleColumnBelowList.name);
    }

    private RelativeLayout mRlBaoKuan;
    private void setBaoKuan(BaseViewHolder helper, final HomeAllBean.HotSaleProduct hotSaleProduct){
        mRlBaoKuan=helper.getView( R.id.rl_baokuanbiqiang );
        if (hotSaleProduct==null){
            mRlBaoKuan.setVisibility( View.GONE );
        }
        mRlBaoKuan.setVisibility( View.VISIBLE );
        RelativeLayout relativeLayout=helper.getView( R.id.rl_baokuan );
        relativeLayout.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                try {
                    RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.tbCouponId = hotSaleProduct.tbCouponId;
                    routerBean.mustParam = "type=1"
                            + "&id=" + (TextUtils.isEmpty(hotSaleProduct.id) ? "" : hotSaleProduct.id)
                            + "&tbItemId=" + hotSaleProduct.tbItemId;
                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                } catch (Exception e) {
                    toast(e.toString());
                }
            }
        } );
        //商品图片
        ImageView ivItemAllFenLei = helper.getView(R.id.iv_baokuan_tupian);
        Glide.with(BaseApplication.getApplication()).load(hotSaleProduct.img).placeholder(R.drawable.bg_common_img_null).into(ivItemAllFenLei);

        //设置来源和标题
//        SpannelTextViewMore spannelTextViewMore = helper.getView(R.id.tv_baokuan_biaoti);
//        spannelTextViewMore.setDrawText(hotSaleProduct.productName);
//        spannelTextViewMore.setShopType(hotSaleProduct.shopType);
        ImageView imageView =helper.getView( R.id.iv_baokuan_tubiao );
        TextView textView = helper.getView( R.id.tv_dianpu );
        if (hotSaleProduct.shopType==1){
            imageView.setBackground( getResources().getDrawable( R.drawable.ic_tb_singe ) );
        }else if (hotSaleProduct.shopType==2){
            imageView.setBackground( getResources().getDrawable( R.drawable.ic_tm_singe ) );
        }
        textView.setText( hotSaleProduct.productName );

        //补贴佣金
//        helper.setText(R.id.tv_item_all_product_butie_yongjin, "补贴佣金  ¥暂无显示");

        //根据后台返的数据做判断，改成一下的自己做判断
                            /*if (BaseApplication.getApplication().mQueryShowHide) {
                                //预估佣金和升级赚
                                if (TextUtils.isEmpty(item.mProductListBean.zhuanMoney) || TextUtils.isEmpty(item.mProductListBean.upZhuanMoney)) {
                                    helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.mProductListBean.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                    helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.mProductListBean.zhuanMoney) ? View.GONE : View.VISIBLE);
                                } else {
                                    helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(View.VISIBLE);
                                    helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(View.GONE);
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

            helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(isAllShow ? View.VISIBLE : View.INVISIBLE);

//                                helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.INVISIBLE);
//                                helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.INVISIBLE);
//                                if (!isAllShow) {
//                                    helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.INVISIBLE);
//                                }

        }
        TextView yujizhuan=helper.getView( R.id.tv_item_all_product_single_yugu_yongjin );
        if (UserHelper.getInstence().isLogin()){
            if (UserHelper.getInstence().getUserInfo().getAgencyLevel()==4){
                yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom4 ) );
            }else {
                yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom ) );
            }
        }else {
            yujizhuan.setBackground( getResources().getDrawable( R.drawable.ic_product_zhuan_single_bottom ) );
        }

        helper.setText(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin, "升级赚 ¥" + StringUtils.stringToStringDeleteZero(hotSaleProduct.upZhuanMoney)+"  ");
        helper.setText(R.id.tv_item_all_product_single_yugu_yongjin, "  预计赚 ¥" + StringUtils.stringToStringDeleteZero(hotSaleProduct.zhuanMoney));
//                            helper.setText(R.id.tv_all_product_sheng_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney));
//                            helper.setText(R.id.tv_item_all_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));

        //券金额
        helper.getView(R.id.tv_baokuan_quan).setVisibility(hotSaleProduct.couponMoney == 0 ? View.INVISIBLE : View.VISIBLE);
        helper.setText(R.id.tv_baokuan_quan, "¥" + StringUtils.doubleToStringDeleteZero(hotSaleProduct.couponMoney) + "劵");
//                            helper.getView(R.id.tv_item_all_product_quan_txt).setVisibility(item.mProductListBean.couponMoney == 0 ? View.GONE : View.VISIBLE);


        //现价(券后价)
        helper.setText(R.id.tv_baokuan_xianjia, "" + StringUtils.stringToStringDeleteZero(hotSaleProduct.preferentialPrice));

        //渠道
//                            helper.setText(R.id.tv_item_all_product_yuanjia_type, item.mProductListBean.shopType == 1 ? "淘宝价 " : "天猫价 ");
        //原价
        helper.setText(R.id.tv_item_all_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(hotSaleProduct.price));
        //去除中间横线
        ((TextView) helper.getView(R.id.tv_item_all_product_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        //销量
        helper.setText(R.id.tv_baokuan_xiaoliang, "" + StringUtils.intToStringUnit(hotSaleProduct.nowNumber) + "人已买");
    }

    /**
     * 设置栏目数据
     *
     * @param programaCount   栏目数量
     * @param programaClass   栏目类别
     * @param programaImgList 栏目数据
     */
//    private void setNewMiddleLanMu(BaseViewHolder helper, List<HomeAllBean.ModuleColumnHeadList> moduleColumnHeadList){
//        LinearLayout newLanMu=helper.getView( R.id.ll_new_lanmu );
//        ImageView newLanMuOne=helper.getView( R.id.iv_item_all_lanmu_1 );
//        ImageView newLanMuTwo=helper.getView( R.id.iv_item_all_lanmu_2 );
//        ImageView newLanMuThree=helper.getView( R.id.iv_item_all_lanmu_3 );
//        ImageView newLanMuFour=helper.getView( R.id.iv_item_all_lanmu_4 );
//        ImageView newLanMuFive=helper.getView( R.id.iv_item_all_lanmu_5 );
//        List<ImageView> newLanMuImage=new ArrayList<>(  );
//        newLanMuImage.add( newLanMuOne );
//        newLanMuImage.add( newLanMuTwo );
//        newLanMuImage.add( newLanMuThree );
//        newLanMuImage.add( newLanMuFour );
//        newLanMuImage.add( newLanMuFive );
//        newLanMu.setVisibility( View.VISIBLE );
//        for (int i=0;i<newLanMuImage.size();i++){
//            Glide.with(BaseApplication.getApplication()).load(moduleColumnHeadList.get(i).img).into(newLanMuImage.get( i ));
//            newLanMuImage.get( i ).setOnClickListener( new OnClickEvent() {
//                @Override
//                public void singleClick(View v) {
//
//                }
//            } );
//        }
//
//
//
//    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<HomeAllBean.RushTimeListBean> mQiangGouList;
    private BaseQuickAdapter<HomeAllBean.RushTimeListBean, BaseViewHolder> mBaseQiangGouQuickAdapter;
    private LinearLayoutManager mQGLinearLayoutManager;

    private void setQiangGouData(BaseViewHolder helper, List<HomeAllBean.RushTimeListBean> rushTimeListBeanList) {
        FrameLayout flHomeAllQiangGou = helper.getView(R.id.fl_all_qianggou);
        RecyclerView rvHomeAllQiangGou = helper.getView(R.id.rv_home_all_qianggou);
        ImageView ivAllWeiNiJingXuan = helper.getView(R.id.iv_all_weini_jingxuan);
        LinearLayout llHomeAllQiangGouTxt = helper.getView(R.id.ll_all_qianggou_txt);
        helper.getView(R.id.tv_all_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳测试闪购
                ARouter.getInstance().build(ARouters.PATH_FLASH_SALE).navigation();
            }
        });
        if (rushTimeListBeanList == null || rushTimeListBeanList.size() <= 0) {
            flHomeAllQiangGou.setVisibility(View.GONE);
            llHomeAllQiangGouTxt.setVisibility(View.GONE);
            ivAllWeiNiJingXuan.setVisibility(View.GONE);
            return;
        }
        flHomeAllQiangGou.setVisibility(View.VISIBLE);
        llHomeAllQiangGouTxt.setVisibility(View.VISIBLE);
        ivAllWeiNiJingXuan.setVisibility(View.GONE);

        if (mQiangGouList == null) {
            mQiangGouList = new ArrayList<>();
        }

        mQiangGouList.clear();

        mQiangGouList.addAll(rushTimeListBeanList);

        if (mBaseQiangGouQuickAdapter == null) {
            mBaseQiangGouQuickAdapter = new BaseQuickAdapter<HomeAllBean.RushTimeListBean, BaseViewHolder>(R.layout.item_all_qianggou, mQiangGouList) {
                @Override
                protected void convert(BaseViewHolder helper, HomeAllBean.RushTimeListBean item) {
                    try {
                        LinearLayout itemAllQiangGouParent = helper.getView(R.id.ll_item_all_qianggou_parent);
                        if (item.status == 1) {
                            itemAllQiangGouParent.setBackgroundResource(R.drawable.bg_home_qianggou_item);
                        } else {
                            itemAllQiangGouParent.setBackground(null);
                        }
                        helper.setText(R.id.tv_item_qianggou_time, item.time);
                        helper.setText(R.id.tv_item_qianggou_txt, item.statusStr);
                    } catch (Exception e) {

                    }
                }
            };

            rvHomeAllQiangGou.setHasFixedSize(true);
            rvHomeAllQiangGou.setNestedScrollingEnabled(false);
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1,GridLayoutManager.HORIZONTAL,false);
            if (mQGLinearLayoutManager == null) {
                mQGLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            }
            rvHomeAllQiangGou.setLayoutManager(mQGLinearLayoutManager);

            rvHomeAllQiangGou.setAdapter(mBaseQiangGouQuickAdapter);
            //底部推荐产品点击事件 todo
            mBaseQiangGouQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    //跳转到web页
                    /*ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                            .withString("loadUrl", Qurl.ddq)
                            .withString(Constant.TITLE, "限时抢购")
                            .withInt(Constant.TYPE, 4)
                            .navigation();*/
                    //跳测试闪购
                    ARouter.getInstance().build(ARouters.PATH_FLASH_SALE).navigation();
                }
            });
            /*llHomeAllQiangGouTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                            .withString("loadUrl", Qurl.ddq)
                            .withString(Constant.TITLE, "限时抢购")
                            .withInt(Constant.TYPE, 4)
                            .navigation();
                }
            });
            flHomeAllQiangGou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                            .withString("loadUrl", Qurl.ddq)
                            .withString(Constant.TITLE, "限时抢购")
                            .withInt(Constant.TYPE, 4)
                            .navigation();
                }
            });*/
        } else {
            mBaseQiangGouQuickAdapter.notifyDataSetChanged();
        }

        for (int i = 0; i < mQiangGouList.size(); i++) {
            if (mQiangGouList.get(i).status == 1) {
                mQGLinearLayoutManager.scrollToPositionWithOffset(i, 430);
                return;
            }
        }
    }


    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    /*private List<HomeAllBean.ProductListBean> mProductList;
    private BaseQuickAdapter<HomeAllBean.ProductListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setProductData(List<HomeAllBean.ProductListBean> productList) {
        if (productList == null || productList.size() <= 0) {
            return;
        }

        if (mProductList == null) {
            mProductList = new ArrayList<>();
        }

        if (page == 1) {
            mProductList.clear();
        }

        mProductList.addAll(productList);

        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<HomeAllBean.ProductListBean, BaseViewHolder>(R.layout.item_all_product, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, HomeAllBean.ProductListBean item) {
                    try {
                        //商品图片
                        ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_all_product_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.img).into(ivItemAllFenLei);

                        //设置来源和标题
                        SpannelTextViewSinge spannelTextViewSinge = helper.getView(R.id.tv_item_all_product_biaoti);
                        spannelTextViewSinge.setDrawText(item.productName);
                        spannelTextViewSinge.setShopType(item.shopType);

                        //补贴佣金
                        helper.setText(R.id.tv_item_all_product_butie_yongjin, "补贴佣金  ¥暂无显示");

                        if (BaseApplication.getApplication().mQueryShowHide) {
                            //预估佣金和升级赚
                            if (TextUtils.isEmpty(item.zhuanMoney) || TextUtils.isEmpty(item.upZhuanMoney)) {
                                helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(View.GONE);
                                helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                            } else {
                                helper.getView(R.id.tv_all_product_zhuan_double_parent).setVisibility(View.VISIBLE);
                                helper.getView(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.GONE);
                                helper.getView(R.id.tv_item_all_product_single_yugu_yongjin).setVisibility(View.GONE);
                            }
                        }

                        helper.setText(R.id.tv_all_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                        helper.setText(R.id.tv_item_all_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                        helper.setText(R.id.tv_all_product_sheng_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                        helper.setText(R.id.tv_item_all_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));

                        //券金额
                        helper.getView(R.id.tv_item_all_product_quan).setVisibility(item.couponMoney == 0 ? View.INVISIBLE : View.VISIBLE);
                        helper.setText(R.id.tv_item_all_product_quan, "" + StringUtils.doubleToStringDeleteZero(item.couponMoney) + "元券");

                        //现价(券后价)
                        helper.setText(R.id.tv_item_all_product_xianjia, "¥" + StringUtils.stringToStringDeleteZero(item.preferentialPrice));

                        //渠道
                        helper.setText(R.id.tv_item_all_product_yuanjia_type, item.shopType == 1 ? "淘宝价 " : "天猫价 ");
                        //原价
                        helper.setText(R.id.tv_item_all_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.price));
                        //去除中间横线
                        ((TextView) helper.getView(R.id.tv_item_all_product_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                        //销量
                        helper.setText(R.id.tv_item_all_product_xiaoliang, "月销" + StringUtils.intToStringUnit(item.nowNumber) + "件");


                    } catch (Exception e) {

                    }
                }
            };

            mRvHomeAllProduct.setHasFixedSize(true);
            mRvHomeAllProduct.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            mRvHomeAllProduct.setLayoutManager(gridLayoutManager);


            mRvHomeAllProduct.setAdapter(mBaseProductQuickAdapter);
            mRvHomeAllProduct.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(getActivity())
                            .margin(5, 5)
                            .size(10)
            ));
            //底部推荐产品点击事件 todo
            mBaseProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    RouterBean routerBean = new RouterBean();
                    routerBean.type = 9;
                    routerBean.mustParam = "type=1"
                            + "&id=" + mProductList.get(position).id
                            + "&tbItemId=" + mProductList.get(position).tbItemId;
//                    routerBean.t = "1";
//                    routerBean.id = mProductList.get(position).id;
//                    routerBean.i = mProductList.get(position).tbItemId;

                    LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
                }
            });
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }*/

    /**
     * 获取底部的商品
     */
    private void getProductList() {
        ApiCancleManager.getInstance().removeAll();
        Map<String, String> allParam = new HashMap<>();
        allParam.put("page", page + "");
        allParam.put("isAppraise", "1");
        allParam.put("programaId", "12");
        if (mDeviceId!=null){
            allParam.put("deviceId", mDeviceId);
        }

        NetWork.getInstance()
                .setTag(Qurl.productShowAll)
                .getApiService(HomeApi.class)
                .homeProgramaShowAll(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<HomeAllBean>(getActivity(), true, false) {
                    @Override
                    public void onSuccess(HomeAllBean result) {
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

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        isNeedRefreshSpike = false;

        //判断用于首次没有加载出来数据时，刷新整体数据
        if (mHomeRvAdapter != null) {
            page++;
            getProductList();
        } else {
            page = 0;
            getAllData();
            mSmartHomeAll.finishLoadMore(100);
        }

    }

    private boolean isNeedRefreshSpike = false;//解决每次限时抢购item刷新问题
    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 0;
        shuaxin = 11;
        isNeedRefreshSpike = false;
        //保险起见
        mProgressScroll = 0;
        if (mINotifyScrollProgress != null) {
            mINotifyScrollProgress.onNotifyProgress(mProgressScroll);
        }
        getAllData();
    }

    @Override
    public void onResume() {
        super.onResume();

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

        if (page == 0) {
            mSmartHomeAll.finishRefresh(state);
            if (!state) {
                //第一次加载失败时，再次显示时可以重新加载
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartHomeAll.finishLoadMoreWithNoMoreData();
        } else {
            mSmartHomeAll.finishLoadMore(state);
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
            mSmartHomeAll.finishRefresh();
            mSmartHomeAll.finishLoadMore();
            smoothMoveToPosition(mRvHomeParentView, 0);
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            if (getUserVisibleHint()) {
                mNeedNotifyList = true;
                mSmartHomeAll.autoRefresh(100);
            } else {
                this.mIsFirstVisible = true;
            }
            if (message.equalsIgnoreCase(Constant.LOGOUT_SUCCESS)){
                mFlAllBottomLogin.setVisibility(View.VISIBLE);
            }else {
                mFlAllBottomLogin.setVisibility(View.GONE);
            }

        }else if (message.equalsIgnoreCase(Constant.HOME_ALL_BOTTOM)){
            mFlAllBottomLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_home_all_top) {
            mRvHomeParentView.scrollToPosition(0);
            mIvHomeAllTop.setVisibility(View.INVISIBLE);
//            mRvHomeParentView.smoothScrollToPosition(0);
            //滚到顶部
        }else if (id == R.id.tv_all_bottom_login){

            if (UserHelper.getInstence().isLogin()){
                mFlAllBottomLogin.setVisibility(View.GONE);
            }else {
                mFlAllBottomLogin.setVisibility(View.VISIBLE);
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }
        }
    }
}
