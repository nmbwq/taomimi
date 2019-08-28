package java.com.lechuang.module.search;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
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
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.DeviceIdUtil;
import com.common.app.utils.Logger;
import com.common.app.utils.StringUtils;
import com.common.app.view.CommonPopupwind;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.SpannelTextViewMore;
import com.common.app.view.SpannelTextViewSinge;
import com.common.app.view.TiaoJianView;
import com.common.app.view.WiperSwitch;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.SearchResultBean;
import java.com.lechuang.module.bean.SearchResultEntity;
import java.com.lechuang.module.search.adapter.SearchProductAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SEARCH_RESULT)
public class SearchResultActivity extends BaseActivity implements OnRefreshLoadMoreListener, View.OnClickListener, TiaoJianView.OnSelecterLisenter, BaseQuickAdapter.OnItemClickListener {

    @Autowired(name = Constant.CHANNEL)
    public String mStringExtra;
    @Autowired(name = Constant.SEARCHTEXT)
    public String mSearchTxt;
    @Autowired(name = Constant.KEYWORD)
    public String isKeyword;
    @Autowired(name = Constant.KEYWORDText)
    public String keywordText;
    private TextView mTvSearchChannel, mTvSearchResultText;
    private CommonPopupwind mCommonPopupwind;
    private TextView mTvSearchTB;
    private TextView mTvSearchJD;
    private TextView mTvSearchPDD;
    private TextView mTvSearchApp;
    private RecyclerView mRvSearchResultProduct;
    private SmartRefreshLayout mSmartSearchResult;
    private TiaoJianView mShaiXuanSearchResult;
    private WiperSwitch mWiperSearchResult;
    private LinearLayout mNetError;
    private int shuaxin = 10;

    private int page = 1;
    private int mPosition = 0;//表示筛选条件，0为综合，1为价格，2为销量，3为收益
    private int range = 0;//表示查询范围：0 查APP(数据库)， 1 查淘宝 ，2 拼多多， 3 京东
    private boolean mSort = true;//表示价格的排列，true为箭头向上
    private boolean mIsSingleLine = true;//单行和多行切换
    private int flag = 0;//记录是否有人工筛选
    private LinearLayout mLLSwiperParent;
    private boolean mNeedNotifyList = false;//点击头部的筛选，是否需要更新列表显示头部
    private ImageView mIvHomeAllTop;
    private boolean mRefresh = true;
    private String mDeviceId=null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void findViews() {

        mStringExtra = getIntent ().getStringExtra ( Constant.CHANNEL );
        mSearchTxt = getIntent ().getStringExtra ( Constant.SEARCHTEXT );

        mTvSearchChannel = $ ( R.id.tv_search_result_channel );
        mTvSearchResultText = $ ( R.id.tv_search_result_text );
        mSmartSearchResult = $ ( R.id.smart_search_result );
        mRvSearchResultProduct = $ ( R.id.rv_search_result_product );
        mShaiXuanSearchResult = $ ( R.id.shaixuan_search_result );
        mWiperSearchResult = $ ( R.id.wiper_search_result );
        mLLSwiperParent = $ ( R.id.ll_swiper_parent );
        mNetError = $ ( R.id.ll_net_error );
        mIvHomeAllTop = $ ( R.id.iv_home_all_top );
        mNetError.setVisibility ( View.GONE );
        mShaiXuanSearchResult.setSelectLisenter ( this );
        mIvHomeAllTop.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                mRvSearchResultProduct.scrollToPosition ( 0 );
                mIvHomeAllTop.setVisibility ( View.INVISIBLE );
            }
        } );

        mTvSearchChannel.setOnClickListener ( this );
        $ ( R.id.iv_common_back ).setOnClickListener ( this );
        $ ( R.id.tv_search_result_text ).setOnClickListener ( this );
        ((TextView)$ ( R.id.tv_common_click_try )).setTextColor( getResources().getColor( R.color.c_313131 ) );
    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        mDeviceId=getDeviceId(this);
        if (!TextUtils.isEmpty ( mStringExtra )) {
            mTvSearchChannel.setText ( mStringExtra );
        }
        if (!TextUtils.isEmpty ( mSearchTxt )) {
            mTvSearchResultText.setText ( mSearchTxt );
        }
        mCommonPopupwind = new CommonPopupwind
                .Builder ()
                .showAsDropDown ( mTvSearchChannel )
                .build ( SearchResultActivity.this, R.layout.popupwind_search_channel );
        mTvSearchApp = (TextView) mCommonPopupwind.findViewById ( R.id.tv_search_app );
        mTvSearchTB = (TextView) mCommonPopupwind.findViewById ( R.id.tv_search_taobao );
        mTvSearchJD = (TextView) mCommonPopupwind.findViewById ( R.id.tv_search_jingdong );
        mTvSearchPDD = (TextView) mCommonPopupwind.findViewById ( R.id.tv_search_pinduoduo );

        mTvSearchApp.setOnClickListener ( this );
        mTvSearchTB.setOnClickListener ( this );
        mTvSearchJD.setOnClickListener ( this );
        mTvSearchPDD.setOnClickListener ( this );
        mSmartSearchResult.setOnRefreshLoadMoreListener ( this );

        String search = mTvSearchResultText.getText ().toString ();
       /* if (search.length() > 0){
            stringTransInt(getString(R.string.s_tb));
        }else {
            stringTransInt(mStringExtra);
        }*/
        mStringExtra = "APP";//默认的
        stringTransInt ( mStringExtra );

        mWiperSearchResult.setChecked ( false );//默认不选中，关闭人工筛选(查全部)
        mWiperSearchResult.setOnChangedListener ( new WiperSwitch.OnChangedListener () {
            @Override
            public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
                flag = checkState ? 1 : 0;
                mSmartSearchResult.autoRefresh ();

            }
        } );
        mRvSearchResultProduct.addItemDecoration ( new GridItemDecoration (
                new GridItemDecoration.Builder ( this )
                        .size ( 5 )
        ) );
    }

    @Override
    protected void getData() {
        mSmartSearchResult.autoRefresh ( 100 );
    }
    public String getDeviceId(Context context) {
        String IMEI=null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
            return IMEI;
        }
        IMEI= DeviceIdUtil.getDeviceId(context);
        return IMEI;
    }

    private void getProductList(boolean isShowProgress) {
        Map<String, Object> allParam = new HashMap<> ();
        allParam.put ( "page", page + "" );
        if (TextUtils.equals ( isKeyword, "1" )) {//使用关键字
            allParam.put ( "name", keywordText );
            allParam.put ( "isKeyword", "1" );
        } else {
            allParam.put ( "name", mTvSearchResultText.getText ().toString ().trim () + "" );
            allParam.put ( "isKeyword", "0" );
        }
        allParam.put ( "range", range + "" );
        if (mLLSwiperParent.getVisibility () == View.VISIBLE) {
            allParam.put ( "flag", flag + "" );
        }
        if (range == 0 && page == 1) {
            mIvHomeAllTop.setVisibility ( View.INVISIBLE );
        }


        switch (mPosition) {
            case 0://综合
                allParam.put ( "isAppraise", "1" );
                break;
            case 1://价格
                allParam.put ( "isPrice", mSort ? 2 + "" : 1 + "" );
                break;
            case 2://销量
                allParam.put ( "isVolume", "1" );
                break;
            case 3://收益
                allParam.put ( "isZhuan", "1" );
                break;
        }
        if (mDeviceId!=null){
            allParam.put("deviceId", mDeviceId);
        }

        NetWork.getInstance ()
                .setTag ( Qurl.productShowAll )
                .getApiService ( ModuleApi.class )
                .productShowAll_search_result ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<SearchResultBean> ( this, true, isShowProgress ) {

                    @Override
                    public void onSuccess(SearchResultBean result) {
                        if (result == null) {

                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );

                        //设置底部商品数据
                        setOtherProductData ( result.productList );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        if (mProductList == null || mProductList.size () <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                        setRefreshLoadMoreState ( false, false );

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        if (mProductList == null || mProductList.size () <= 0 || mBaseProductQuickAdapter == null) {
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                        setRefreshLoadMoreState ( false, false );

                    }
                } );
    }

    /**
     * 设置数据
     *
     * @param productList
     */
    private List<SearchResultBean.ProductListBean> mProductList;
    private BaseQuickAdapter<SearchResultBean.ProductListBean, BaseViewHolder> mBaseProductQuickAdapter;
    int number = 0;

    private void setOtherProductData(List<SearchResultBean.ProductListBean> productList) {
        if (productList == null || productList.size () <= 0) {
            if (page == 1 && range == 0 && mProductList != null && mBaseProductQuickAdapter != null) {
                mProductList.clear ();
                mBaseProductQuickAdapter.notifyDataSetChanged ();
            }
            if (mProductList == null || mProductList.size () <= 0 || mBaseProductQuickAdapter == null) {
                if (!mRefresh) {
                    mNetError.setVisibility ( View.VISIBLE );
                }
                mRefresh = false;
            }
            if (range == 0) {
                range = 1;
                page = 1;
                getProductList ( false );
            }
            return;
        }
        if (mProductList == null) {
            mProductList = new ArrayList<> ();
        }

        mNetError.setVisibility ( View.GONE );

        //这里添加判断，用于区分时候已经初始化过。初始化过就刷新数据，没
        if (page == 1 && range == 0) {
            mProductList.clear ();
        }

        if (page == 1 && range == 1) {
            SearchResultBean.ProductListBean tuijian = new SearchResultBean.ProductListBean ();
            tuijian.tuijian = true;
            productList.add ( 0, tuijian );
        }

        if (productList.size () < 20 && range == 0) {
            range = 1;
            page = 1;
            getProductList ( false );
        }
        mProductList.addAll ( productList );
        setAdapter ();
    }

    private List<SearchResultEntity> mProductListEntitys;
    private SearchProductAdapter<SearchResultEntity, BaseViewHolder> mSearchProductAdapter;

    private void setAdapter_new() {

        //注意这里需要组装数据，
        if (mProductListEntitys == null) {
            mProductListEntitys = new ArrayList<> ();
        }

        if (page == 1) {
            mProductListEntitys.clear ();
            SearchResultEntity searchResultEntity1 = new SearchResultEntity ( SearchResultEntity.TYPE1 );
            SearchResultEntity searchResultEntity2 = new SearchResultEntity ( SearchResultEntity.TYPE2 );
            SearchResultEntity searchResultEntityProduct = new SearchResultEntity ( SearchResultEntity.TYPE_PRODUCT );
            mProductListEntitys.add ( searchResultEntity1 );

            for (int i = 0; mProductList != null && i < mProductList.size (); i++) {
                SearchResultEntity searchResultEntity = new SearchResultEntity ( SearchResultEntity.TYPE_PRODUCT );
                searchResultEntity.mProductListBean = mProductList.get ( i );
                mProductListEntitys.add ( searchResultEntity );
            }


            mProductListEntitys.add ( searchResultEntity2 );
            mProductListEntitys.add ( searchResultEntityProduct );

        } else {
            for (int i = 0; mProductList != null && i < mProductList.size (); i++) {
                SearchResultEntity searchResultEntity = new SearchResultEntity ( SearchResultEntity.TYPE_PRODUCT );
                searchResultEntity.mProductListBean = mProductList.get ( i );
                mProductListEntitys.add ( searchResultEntity );
            }
        }

        if (mSearchProductAdapter == null) {

            mSearchProductAdapter = new SearchProductAdapter<SearchResultEntity, BaseViewHolder> ( mProductListEntitys ) {
                @Override
                public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                    super.onAttachedToRecyclerView ( recyclerView );
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager ();
                    if (manager instanceof GridLayoutManager) {
                        final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;

                        gridLayoutManager.setSpanSizeLookup ( new GridLayoutManager.SpanSizeLookup () {

                            @Override
                            public int getSpanSize(int position) {
                                int type = getItemViewType ( position );
                                switch (type) {
                                    case SearchResultEntity.TYPE1:
                                    case SearchResultEntity.TYPE2:
                                    case SearchResultEntity.TYPE_PRODUCT:
                                        return mIsSingleLine ? 2 : 1;
                                    default:
                                        return mIsSingleLine ? 2 : 1;
                                }
                            }

                        } );
                    }
                }

                @Override
                protected void addItemTypeView() {
                    addItemType ( SearchResultEntity.TYPE1, R.layout.layout_seach_type1 );
                    addItemType ( SearchResultEntity.TYPE2, R.layout.layout_seach_type1 );
                    addItemType ( SearchResultEntity.TYPE_PRODUCT, R.layout.layout_search_type_product );
                }

                @Override
                protected void convert(BaseViewHolder helper, SearchResultEntity item) {
                    if (helper.getItemViewType () == SearchResultEntity.TYPE1) {

                    } else if (helper.getItemViewType () == SearchResultEntity.TYPE2) {

                    } else if (helper.getItemViewType () == SearchResultEntity.TYPE_PRODUCT) {

                        ViewStub vtItemAllSingle = helper.getView ( R.id.vs_search_single );
                        ViewStub vtItemAllMore = helper.getView ( R.id.vs_search_more );
                        vtItemAllSingle.setVisibility ( mIsSingleLine ? View.VISIBLE : View.GONE );
                        vtItemAllMore.setVisibility ( mIsSingleLine ? View.GONE : View.VISIBLE );

                        try {
                            //商品图片
                            ImageView ivItemOtherFenLei = helper.getView ( R.id.iv_item_search_result_product_tupian );
                            Glide.with ( BaseApplication.getApplication () ).load ( item.mProductListBean.img ).placeholder ( R.drawable.bg_common_img_null ).into ( ivItemOtherFenLei );

                            //设置来源和标题
                            if (mIsSingleLine) {
                                SpannelTextViewSinge spannelTextViewSinge = helper.getView ( R.id.tv_item_search_result_product_biaoti );
                                spannelTextViewSinge.setDrawText ( item.mProductListBean.productName );
                                spannelTextViewSinge.setShopType ( item.mProductListBean.shopType );
                            } else {
                                SpannelTextViewMore spannelTextViewMore = helper.getView ( R.id.tv_item_search_result_product_biaoti );
                                spannelTextViewMore.setDrawText ( item.mProductListBean.productName );
                                spannelTextViewMore.setShopType ( item.mProductListBean.shopType );
                            }
                            TextView shopname = helper.getView ( R.id.tv_dianpu );
                            if (!TextUtils.isEmpty ( item.mProductListBean.shopName )) {
                                shopname.setText ( item.mProductListBean.shopName );
                                shopname.setVisibility ( View.VISIBLE );
                            } else {
                                shopname.setVisibility ( View.INVISIBLE );
                            }

                            //补贴佣金
                            helper.setText ( R.id.tv_item_search_result_product_butie_yongjin, "补贴佣金  ¥暂无显示" );

                            //根据后台返的数据做判断，改成一下的自己做判断
                            if (BaseApplication.getApplication ().mQueryShowHide) {
                                //预估佣金和升级赚
                                if (mIsSingleLine) {

                                    if (TextUtils.isEmpty ( item.mProductListBean.zhuanMoney ) || TextUtils.isEmpty ( item.mProductListBean.upZhuanMoney )) {
//                                        helper.getView(R.id.tv_item_search_result_product_zhuan_double_parent).setVisibility(View.GONE);
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( TextUtils.isEmpty ( item.mProductListBean.upZhuanMoney ) ? View.GONE : View.VISIBLE );
                                        helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( TextUtils.isEmpty ( item.mProductListBean.zhuanMoney ) ? View.GONE : View.VISIBLE );
                                        helper.setText ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.upZhuanMoney ) );
                                        helper.setText ( R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.zhuanMoney ) );
                                    } else {
//                                        helper.getView(R.id.tv_item_search_result_product_zhuan_double_parent).setVisibility(View.VISIBLE);
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( View.GONE );
                                        helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( View.GONE );
//                                        helper.setText(R.id.tv_item_search_result_product_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney));
//                                        helper.setText(R.id.tv_item_search_result_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));
                                    }
                                } else {
                                    helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( TextUtils.isEmpty ( item.mProductListBean.upZhuanMoney ) ? View.GONE : View.VISIBLE );
                                    helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( TextUtils.isEmpty ( item.mProductListBean.zhuanMoney ) ? View.GONE : View.VISIBLE );
                                    helper.setText ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.upZhuanMoney ) );
                                    helper.setText ( R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.zhuanMoney ) );
                                }

                            }
                            //自己判断
                            if (BaseApplication.getApplication ().mQueryShowHide) {
                                //true 显示为不登录&普通会员&超级会员 { 预计赚 + 升级赚 }，
                                //false为显示运营商 { 预计赚  }，高级运营商 { 预计赚 }
                                boolean isAllShow = true;
                                if (UserHelper.getInstence ().getUserInfo ().getAgencyLevel () == 3 ||
                                        UserHelper.getInstence ().getUserInfo ().getAgencyLevel () == 4) {
                                    isAllShow = false;
                                }


                                if (mIsSingleLine) {
//                                    helper.getView(R.id.tv_item_search_result_product_zhuan_double_parent).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
                                    helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( !isAllShow ? View.VISIBLE : View.GONE );
                                    helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( !isAllShow ? View.VISIBLE : View.GONE );
                                    if (!isAllShow) {
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( View.GONE );
                                    }

                                    helper.setText ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.upZhuanMoney ) );
                                    helper.setText ( R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.zhuanMoney ) );
//                                    helper.setText(R.id.tv_item_search_result_product_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.upZhuanMoney));
//                                    helper.setText(R.id.tv_item_search_result_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.mProductListBean.zhuanMoney));
                                } else {

                                    if (isAllShow) {
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( View.VISIBLE );
                                        helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( View.VISIBLE );
                                    } else {
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( View.GONE );
                                        helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( View.VISIBLE );
                                    }

                                    helper.setText ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.upZhuanMoney ) );
                                    helper.setText ( R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.zhuanMoney ) );
                                }
                            }


                            //券金额
                            helper.getView ( R.id.tv_item_search_result_product_quan ).setVisibility ( item.mProductListBean.couponMoney == 0 ? View.INVISIBLE : View.VISIBLE );
//                            helper.getView ( R.id.tv_item_search_product_quan_txt ).setVisibility ( item.mProductListBean.couponMoney == 0 ? View.INVISIBLE : View.VISIBLE );
                            helper.setText ( R.id.tv_item_search_result_product_quan, "" + StringUtils.doubleToStringDeleteZero ( item.mProductListBean.couponMoney ) + "元券" );

                            //现价(券后价)
                            helper.setText ( R.id.tv_item_search_result_product_xianjia, "" + StringUtils.stringToStringDeleteZero ( item.mProductListBean.preferentialPrice ) + "" );

                            if (mIsSingleLine) {
                                //渠道
//                                helper.setText(R.id.tv_item_search_result_product_yuanjia_type, item.mProductListBean.shopType == 1 ? "淘宝价 " : "天猫价 ");
                            }
                            //原价
                            helper.setText ( R.id.tv_item_search_result_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero ( item.mProductListBean.price ) + "" );
                            ((TextView) helper.getView ( R.id.tv_item_search_result_product_yuanjia )).getPaint ().setFlags ( Paint.STRIKE_THRU_TEXT_FLAG );

                            //销量
                            helper.setText ( R.id.tv_item_search_result_product_xiaoliang, "" + StringUtils.intToStringUnit ( item.mProductListBean.nowNumber ) + "人购买" );
                        } catch (Exception e) {

                        }


                    }
                }
            };


        } else {
            mSearchProductAdapter.notifyDataSetChanged ();
        }
        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvSearchResultProduct.scrollToPosition ( 0 );
        }
    }

    /**
     * 适配切换布局
     */
    private void setAdapter() {
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<SearchResultBean.ProductListBean, BaseViewHolder> ( this.mIsSingleLine ? R.layout.item_search_result_product1 : R.layout.item_search_result_product2, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, SearchResultBean.ProductListBean item) {
                    try {


                        if (mIsSingleLine) {
                            helper.getView ( R.id.ll_search_single_product ).setVisibility ( item.tuijian ? View.GONE : View.VISIBLE );
                            helper.getView ( R.id.ll_search_single_tuijian ).setVisibility ( !item.tuijian ? View.GONE : View.VISIBLE );
                        } else {
                            helper.getView ( R.id.ll_search_more_product ).setVisibility ( item.tuijian ? View.GONE : View.VISIBLE );
                            helper.getView ( R.id.ll_search_more_tuijian ).setVisibility ( !item.tuijian ? View.GONE : View.VISIBLE );
                        }
                        helper.getView( R.id.iv_video ).setVisibility( item.isVideo.equals( "1" )? View.VISIBLE:View.GONE);
                        Logger.e ( "------------", item.tuijian + "" );
                        if (!item.tuijian) {

                            //设置来源和标题
                            if (mIsSingleLine) {
                                SpannelTextViewMore spannelTextViewMore = helper.getView ( R.id.tv_item_search_result_product_biaoti );
                                spannelTextViewMore.setDrawText ( item.productName );
                                spannelTextViewMore.setShopType ( item.shopType );
                            } else {
                                SpannelTextViewMore spannelTextViewMore = helper.getView ( R.id.tv_item_search_result_product_biaoti );
                                spannelTextViewMore.setDrawText ( item.productName );
                                spannelTextViewMore.setShopType ( item.shopType );

                            }

                            //商品图片
                            ImageView ivItemOtherFenLei = helper.getView ( R.id.iv_item_search_result_product_tupian );
                            Glide.with ( BaseApplication.getApplication () ).load ( item.img ).placeholder ( R.drawable.bg_common_img_null ).into ( ivItemOtherFenLei );

                            TextView shopname = helper.getView ( R.id.tv_dianpu );
                            if (!TextUtils.isEmpty ( item.shopName )) {
                                shopname.setText ( item.shopName );
                                shopname.setVisibility ( View.VISIBLE );
                            } else {
                                shopname.setVisibility ( View.INVISIBLE );
                            }

                            //补贴佣金
                            helper.setText ( R.id.tv_item_search_result_product_butie_yongjin, "补贴佣金  ¥暂无显示" );

                            //根据后台返的数据做判断，改成一下的自己做判断
                       /* if (BaseApplication.getApplication().mQueryShowHide){
                            //预估佣金和升级赚
                            if (mIsSingleLine){

                                if (TextUtils.isEmpty(item.zhuanMoney) || TextUtils.isEmpty(item.upZhuanMoney)){
                                    helper.getView(R.id.tv_item_search_result_product_zhuan_double_parent).setVisibility( View.GONE );
                                    helper.getView(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                    helper.getView(R.id.tv_item_search_result_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                                    helper.setText(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                    helper.setText(R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                                }else {
                                    helper.getView(R.id.tv_item_search_result_product_zhuan_double_parent).setVisibility( View.VISIBLE );
                                    helper.getView(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.GONE);
                                    helper.getView(R.id.tv_item_search_result_product_single_yugu_yongjin).setVisibility(View.GONE);
                                    helper.setText(R.id.tv_item_search_result_product_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                    helper.setText(R.id.tv_item_search_result_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                                }
                            }else {
                                helper.getView(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.upZhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.getView(R.id.tv_item_search_result_product_single_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                                helper.setText(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
                                helper.setText(R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                            }

                        }*/
                            //自己判断
                            if (BaseApplication.getApplication ().mQueryShowHide) {
                                //true 显示为不登录&普通会员&超级会员 { 预计赚 + 升级赚 }，
                                //false为显示运营商 { 预计赚  }，高级运营商 { 预计赚 }
                                boolean isAllShow = true;
                                if (UserHelper.getInstence ().getUserInfo ().getAgencyLevel () == 3 ||
                                        UserHelper.getInstence ().getUserInfo ().getAgencyLevel () == 4) {
                                    isAllShow = false;
                                }


                                if (mIsSingleLine) {
                                    helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( isAllShow ? View.VISIBLE : View.GONE );
//                                helper.getView(R.id.tv_item_search_result_product_zhuan_double_parent).setVisibility(isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.GONE);
//                                helper.getView(R.id.tv_item_search_result_product_single_yugu_yongjin).setVisibility(!isAllShow ? View.VISIBLE : View.GONE);
//                                if (!isAllShow) {
//                                    helper.getView(R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin).setVisibility(View.GONE);
//                                }

                                    helper.setText ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero ( item.upZhuanMoney ) );
                                    helper.setText ( R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero ( item.zhuanMoney ) );
//                                helper.setText(R.id.tv_item_search_result_product_double_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero(item.upZhuanMoney));
//                                helper.setText(R.id.tv_item_search_result_product_double_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));
                                } else {

                                    if (isAllShow) {
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( View.VISIBLE );
                                        helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( View.VISIBLE );
                                    } else {
                                        helper.getView ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin ).setVisibility ( View.GONE );
                                        helper.getView ( R.id.tv_item_search_result_product_single_yugu_yongjin ).setVisibility ( View.VISIBLE );
                                    }

                                    helper.setText ( R.id.tv_item_search_result_product_sheng_single_sheng_yugu_yongjin, "升级赚  ¥" + StringUtils.stringToStringDeleteZero ( item.upZhuanMoney ) );
                                    helper.setText ( R.id.tv_item_search_result_product_single_yugu_yongjin, "预计赚  ¥" + StringUtils.stringToStringDeleteZero ( item.zhuanMoney ) );
                                }
                            }
                            //预计赚背景图片
                            TextView yujizhuan=helper.getView( R.id.tv_item_search_result_product_single_yugu_yongjin );
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
                            helper.getView ( R.id.tv_item_search_result_product_quan ).setVisibility ( item.couponMoney == 0 ? View.GONE : View.VISIBLE );
//                            helper.getView ( R.id.tv_item_search_product_quan_txt ).setVisibility ( item.couponMoney == 0 ? View.GONE : View.VISIBLE );
                            helper.setText ( R.id.tv_item_search_result_product_quan, "¥" + StringUtils.doubleToStringDeleteZero ( item.couponMoney ) + "劵" );

                            //现价(券后价)
                            helper.setText ( R.id.tv_item_search_result_product_xianjia, "" + StringUtils.stringToStringDeleteZero ( item.preferentialPrice ) + "" );

                            if (mIsSingleLine) {
                                //渠道
//                            helper.setText(R.id.tv_item_search_result_product_yuanjia_type, item.shopType == 1 ? "淘宝价 " : "天猫价 ");
                            }
                            //原价
                            helper.setText ( R.id.tv_item_search_result_product_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero ( item.price ) + "" );
                            ((TextView) helper.getView ( R.id.tv_item_search_result_product_yuanjia )).getPaint ().setFlags ( Paint.STRIKE_THRU_TEXT_FLAG );

                            //销量
                            helper.setText ( R.id.tv_item_search_result_product_xiaoliang, "" + StringUtils.intToStringUnit ( item.nowNumber ) + "人购买" );

                        }


                    } catch (Exception e) {

                    }
                }
            };
            mRvSearchResultProduct.setHasFixedSize ( true );
            mRvSearchResultProduct.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( this, this.mIsSingleLine ? 1 : 2 );
            mRvSearchResultProduct.setLayoutManager ( gridLayoutManager );


            mRvSearchResultProduct.setAdapter ( mBaseProductQuickAdapter );
            mBaseProductQuickAdapter.setOnItemClickListener ( this );
            mRvSearchResultProduct.setOnScrollListener ( new RecyclerView.OnScrollListener () {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged ( recyclerView, newState );
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager ();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置, todo 暂时不能加自动加载
                        /*int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        if (lastItemPosition > shuaxin) {
                            if (mBaseProductQuickAdapter != null) {
                                page++;
                                getProductList(false);
                                shuaxin = shuaxin + 20;
                            }
                        }*/

                        int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition ();
                        if (firstVisibleItemPosition <= 15) {
                            mIvHomeAllTop.setVisibility ( View.INVISIBLE );
                        } else {
                            mIvHomeAllTop.setVisibility ( View.VISIBLE );
                        }
                    }
                }
            } );

        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }
        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvSearchResultProduct.scrollToPosition ( 0 );
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        //判断用于首次没有加载出来数据时，刷新整体数据
        if (mBaseProductQuickAdapter != null) {
            page++;
            getProductList ( false );
        } else {
            page = 1;
            getProductList ( false );
            mSmartSearchResult.finishLoadMore ( 1000 );
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        shuaxin = 10;
        range = 0;//恢复默认搜索APP
        mRefresh = true;
        mSmartSearchResult.setNoMoreData ( false );
        getProductList ( false );
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartSearchResult.finishRefresh ( state );
        } else if (noMoreData) {
            mSmartSearchResult.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartSearchResult.finishLoadMore ( state );
        }
    }

    @Override
    public void onSelecter(int position, boolean sort) {

        if (mPosition == position && position != 1) {
            return;
        }
        Logger.e ( "----", position + "***" + sort );
        page = 1;
        mPosition = position;
        mSort = sort;
        shuaxin = 10;
        range = 0;
        mNeedNotifyList = true;
        getProductList ( true );
//        mSmartSearchResult.autoRefresh();

    }

    @Override
    public void onChangeStyle(boolean isSingleLine) {
        mIsSingleLine = isSingleLine;
        mBaseProductQuickAdapter = null;
        mNeedNotifyList = true;
        setAdapter ();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        if (id == R.id.iv_common_back) {//返回
            finish ();
        } else if (id == R.id.tv_search_result_text) {
            String txt = mTvSearchResultText.getText ().toString ();
            txt = txt.equalsIgnoreCase ( getResources ().getString ( R.string.tv_common_search_hint ) ) ? "" : txt;

            ARouter.getInstance ().build ( ARouters.PATH_SEARCH )
                    .withString ( Constant.CHANNEL, mTvSearchChannel.getText ().toString () )
                    .withString ( Constant.SEARCHTEXT, txt )
                    .withString ( Constant.t, "1" )
                    .navigation ( this, new NavigationCallback () {
                        @Override
                        public void onFound(Postcard postcard) {

                        }

                        @Override
                        public void onLost(Postcard postcard) {

                        }

                        @Override
                        public void onArrival(Postcard postcard) {
                            finish ();

                        }

                        @Override
                        public void onInterrupt(Postcard postcard) {

                        }
                    } );
        } else if (id == R.id.tv_search_result_channel) {//展示选框
            mCommonPopupwind.showPopupwind ( mCommonPopupwind );
        } else if (id == R.id.tv_search_app) {//选择APP
            changePopupwindTvStyle ( R.string.s_app );
            mSmartSearchResult.autoRefresh ();
        } else if (id == R.id.tv_search_taobao) {//选择淘宝
            changePopupwindTvStyle ( R.string.s_tb );
            mSmartSearchResult.autoRefresh ();
        } else if (id == R.id.tv_search_jingdong) {//选择京东
            changePopupwindTvStyle ( R.string.s_jd );
            mSmartSearchResult.autoRefresh ();
        } else if (id == R.id.tv_search_pinduoduo) {//选择拼多多
            changePopupwindTvStyle ( R.string.s_pdd );
            mSmartSearchResult.autoRefresh ();
        }
    }


    /**
     * 更改弹出框样式
     *
     * @param tvId
     */
    private void changePopupwindTvStyle(@StringRes int tvId) {
        mTvSearchChannel.setText ( tvId );
        if (tvId == R.string.s_app) {
            range = 0;
            mTvSearchApp.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvSearchTB.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchJD.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchPDD.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
//            mLLSwiperParent.setVisibility(View.GONE);
        } else if (tvId == R.string.s_tb) {
            range = 1;
            mTvSearchApp.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchTB.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvSearchJD.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchPDD.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
//            mLLSwiperParent.setVisibility(View.VISIBLE);

        } else if (tvId == R.string.s_jd) {
            range = 3;
            mTvSearchApp.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchTB.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchJD.setTextColor ( getResources ().getColor ( R.color.c_main ) );
            mTvSearchPDD.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
        } else if (tvId == R.string.s_pdd) {
            range = 2;
            mTvSearchApp.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchTB.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchJD.setTextColor ( getResources ().getColor ( R.color.c_343434 ) );
            mTvSearchPDD.setTextColor ( getResources ().getColor ( R.color.c_main ) );
        }
        if (mCommonPopupwind != null) {
            mCommonPopupwind.dismiss ();
        }
    }

    private void stringTransInt(String channel) {
        if (TextUtils.isEmpty ( channel )) {
            return;
        }
        int tvId = 0;
        if (channel.equalsIgnoreCase ( getResources ().getString ( R.string.s_app ) )) {
            tvId = R.string.s_app;
        } else if (channel.equalsIgnoreCase ( getResources ().getString ( R.string.s_tb ) )) {
            tvId = R.string.s_tb;
        } else if (channel.equalsIgnoreCase ( getResources ().getString ( R.string.s_jd ) )) {
            tvId = R.string.s_jd;
        } else if (channel.equalsIgnoreCase ( getResources ().getString ( R.string.s_pdd ) )) {
            tvId = R.string.s_pdd;
        }

        changePopupwindTvStyle ( tvId );
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mProductList.get ( position ).tuijian) {
            return;
        }
        //跳转到商品详情
        RouterBean routerBean = new RouterBean ();
        routerBean.type = 9;
        routerBean.tbCouponId = mProductList.get ( position ).tbCouponId;
        String uuid = mProductList.get(position).uuid;
        String t = "1";
        if (!TextUtils.isEmpty(uuid)){
            t = "2";
        }
        routerBean.mustParam = "type=" + t
                + "&id=" + mProductList.get ( position ).id
                + "&uuid=" + mProductList.get ( position ).uuid
                + "&sort=" + mProductList.get ( position ).sort
                + "&tbItemId=" + mProductList.get ( position ).tbItemId;

        LinkRouterUtils.getInstance ().setRouterBean ( this, routerBean );
    }
}
