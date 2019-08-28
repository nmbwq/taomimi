package java.com.lechuang.module.order;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.androidkun.xtablayout.XTabLayout;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.Utils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.OrderBean;
import java.com.lechuang.module.order.widget.CustomDatePicker;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_ORDER)
public class OrderActivity extends BaseActivity implements OnRefreshLoadMoreListener, View.OnClickListener {
    private SmartRefreshLayout mSmartOrder;
    private CustomDatePicker mCustomDatePicker;

    private ImageView mIvOrderBack, mIvJianTou, mIvSearch, mIvDate;
    private TextView mTvTitle, mTvTaoBao, mTvJingDong, mTvPinDuoDuo, mTvSearch, mTvYifukuan, mTvYishouhuo, mTvYijiesuan,mTvState;
    private LinearLayout mLlTitle, mLlOption, mLlHint, mLlSearch;
    private RelativeLayout mRlYouxiaokuang,mRlAllClose;
    private View mVLine, mVBackground;
    private EditText mEdSearch;
    private XTabLayout mXTabLayoutOrder;

    private RecyclerView mRvOrderList;
    private boolean SearchBotton=false;

    //返回键是否点击了搜索框,是否进行了搜索
    private boolean search = false, goSearch = false, mload = true;

    private int page = 1;//页数
    //淘宝，京东，拼多多
    private int variety = 1;
    //订单参数
    private int status = 0;
    //时间参数
    private String time = "";
    //当前时间
    private String nowTime = "";
    //搜索
    private String orderNum = "";
    private boolean mloadt=true;
    private LinearLayout mNetError;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    protected void findViews() {
        mXTabLayoutOrder = $ ( R.id.xTablayout_order );
        mSmartOrder = $ ( R.id.smart_order );
        mVBackground = $ ( R.id.v_order_background );
        mRlAllClose = $ ( R.id.rl_all_close );

        mIvOrderBack = $ ( R.id.iv_order_back );
        mIvJianTou = $ ( R.id.iv_order_jiantou );
        mIvSearch = $ ( R.id.iv_order_search );
        mIvDate = $ ( R.id.iv_order_date );
        mTvTitle = $ ( R.id.iv_order_title );
        mLlTitle = $ ( R.id.ll_order_title );
        mLlOption = $ ( R.id.ll_order_option );
        mLlHint = $ ( R.id.ll_order_hint );
        mVLine = $ ( R.id.v_order_line );

        mTvTaoBao = $ ( R.id.tv_order_taobao );
        mTvJingDong = $ ( R.id.tv_order_jingdong );
        mTvPinDuoDuo = $ ( R.id.tv_order_pinduoduo );

        mEdSearch = $ ( R.id.et_order_search );
        mLlSearch = $ ( R.id.ll_order_search );
        mTvSearch = $ ( R.id.tv_order_search );

        mTvYifukuan = $ ( R.id.tv_order_yifukuan );
        mTvYishouhuo = $ ( R.id.tv_order_yishouhuo );
        mTvYijiesuan = $ ( R.id.tv_order_yijiesuan );
        mRlYouxiaokuang = $ ( R.id.rl_order_youxiaokuang );

        mTvState = $ ( R.id.tv_order_state);
        mNetError = $ ( R.id.ll_net_error );

        mRvOrderList = $ ( R.id.rv_order_list );//底部产品列表

    }

    @Override
    protected void initView() {
        //时间弹出框
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm", Locale.CHINA );
        nowTime = sdf.format ( new Date () );
        mCustomDatePicker = new CustomDatePicker ( this, new CustomDatePicker.ResultHandler () {
            @Override
            public void handle(String returnTime) {
                time = returnTime;
                nowTime = returnTime;
                page = 1;
                getAllData ();
//                time = "";
            }
        }, "2017-01-01 00:00", nowTime );
        mCustomDatePicker.setIsLoop ( false );

        mSmartOrder.setOnRefreshLoadMoreListener ( this );
        mEdSearch.setOnClickListener ( this );
        mRlAllClose.setOnClickListener ( this );
        mIvOrderBack.setOnClickListener ( this );
        mIvSearch.setOnClickListener ( this );
        mIvDate.setOnClickListener ( this );
        mTvTaoBao.setOnClickListener ( this );
        mTvJingDong.setOnClickListener ( this );
        mTvPinDuoDuo.setOnClickListener ( this );
        mTvSearch.setOnClickListener ( this );
        mTvYifukuan.setOnClickListener ( this );
        mTvYishouhuo.setOnClickListener ( this );
        mTvYijiesuan.setOnClickListener ( this );
        mRlYouxiaokuang.setOnClickListener ( this );
        variety = 1;
        //添加三个框
        mXTabLayoutOrder.removeAllTabs ();
        mXTabLayoutOrder.addTab ( mXTabLayoutOrder.newTab ().setText ( "所有订单" ) );
        mXTabLayoutOrder.addTab ( mXTabLayoutOrder.newTab ().setText ( "有效订单" ) );
        mXTabLayoutOrder.addTab ( mXTabLayoutOrder.newTab ().setText ( "失效订单" ) );
        mXTabLayoutOrder.setOnTabSelectedListener ( new XTabLayout.OnTabSelectedListener () {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                if (tab.getText () == "所有订单") {
                    status = 0;
                    mRlYouxiaokuang.setVisibility ( View.GONE );
                    page = 1;
                    getAllData ();
                } else if (tab.getText () == "有效订单") {
                    mRlYouxiaokuang.setVisibility ( View.VISIBLE );
                    status = 1;
                    page = 1;
                    OrderButtonChange(status);
                    getAllData ();
                } else if (tab.getText () == "失效订单") {
                    status = 5;
                    mRlYouxiaokuang.setVisibility ( View.GONE );
                    page = 1;
                    getAllData ();
                }
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
            }
        } );
    }

    @Override
    protected void getData() {
        //smart设置属性，设置自动刷新，调用刷新方法
        mSmartOrder.autoRefresh ( 100 );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        //表头点击，暂时去掉
        if (id == R.id.ll_order_title) {
            if (mLlOption.getVisibility () != View.VISIBLE) {
                mIvJianTou.setBackground ( getResources ().getDrawable ( R.drawable.ic_order_down ) );
                mLlHint.setVisibility ( View.GONE );
                mLlSearch.setVisibility ( View.GONE );
                mVLine.setVisibility ( View.VISIBLE );
                mVBackground.setVisibility ( View.VISIBLE );
            }
            //返回
        } else if (id == R.id.iv_order_back) {
            if (search && !goSearch) {
                mIvSearch.setVisibility ( View.VISIBLE );
                mIvDate.setVisibility ( View.VISIBLE );
                mLlSearch.setVisibility ( View.GONE );
                mLlHint.setVisibility ( View.VISIBLE );
                mVLine.setVisibility ( View.VISIBLE );
                mVBackground.setVisibility ( View.GONE );
                search = false;
                return;
            } else if (goSearch && search) {
                mIvSearch.setVisibility ( View.VISIBLE );
                mIvDate.setVisibility ( View.VISIBLE );
                mLlSearch.setVisibility ( View.GONE );
                mLlHint.setVisibility ( View.VISIBLE );
                mVLine.setVisibility ( View.VISIBLE );
                mVBackground.setVisibility ( View.GONE );
                search = false;
                goSearch = false;
                getAllData ();
                /*finish ();
                ARouter.getInstance ().build ( ARouters.PATH_ORDER ).navigation ();*/
                return;
            }
            hintKbTwo();
            finish ();
        } else if (id == R.id.rl_all_close) {

        } else if (id == R.id.tv_order_search) {//文字搜索按钮
            if (TextUtils.isEmpty (mEdSearch.getText ().toString ()  )){
                Utils.toast ( "请输入想要查询的订单号！" );
                return;
            }

            goSearch = true;
            orderNum = mEdSearch.getText ().toString ();
            mVBackground.setVisibility ( View.GONE );
            mVBackground.setBackgroundColor ( getResources ().getColor ( R.color.c_32000000 ) );
//            mProductList.clear ();
            page = 1;
            getSearchData ();
            //图片搜索按钮
        } else if (id == R.id.iv_order_search) {
            if (mLlSearch.getVisibility () != View.VISIBLE) {
                search = true;
                mLlSearch.setVisibility ( View.VISIBLE );
                mLlHint.setVisibility ( View.GONE );
                mVLine.setVisibility ( View.GONE );
                mLlOption.setVisibility ( View.GONE );
                mVBackground.setVisibility ( View.VISIBLE );
                mIvSearch.setVisibility ( View.GONE );
                mIvDate.setVisibility ( View.GONE );
                mVBackground.setBackgroundColor ( getResources ().getColor ( R.color.c_5A5A5A ) );
            }
            //日历按钮
        } else if (id == R.id.iv_order_date) {
            mCustomDatePicker.show ( nowTime );

            //选择淘宝，暂时屏蔽
        } else if (id == R.id.tv_order_taobao) {
            if (variety != 1) {
                variety = 1;
                mIvJianTou.setBackground ( getResources ().getDrawable ( R.drawable.ic_order_up ) );
                ButtonChange ( variety );
                ControlSwitch ();
            }
            //选择京东，暂时屏蔽
        } else if (id == R.id.tv_order_jingdong) {
            if (variety != 2) {
                variety = 2;
                mIvJianTou.setBackground ( getResources ().getDrawable ( R.drawable.ic_order_up ) );
                ButtonChange ( variety );
                ControlSwitch ();
            }
            //选择拼多多，暂时屏蔽
        } else if (id == R.id.tv_order_pinduoduo) {
            if (variety != 3) {
                variety = 3;
                mIvJianTou.setBackground ( getResources ().getDrawable ( R.drawable.ic_order_up ) );
                ButtonChange ( variety );
                ControlSwitch ();
            }
        } else if (id == R.id.tv_order_yifukuan) {
            if (status != 2) {
                status = 2;
                OrderButtonChange ( status );
                page = 1;
                getAllData ();
            }

        } else if (id == R.id.tv_order_yishouhuo) {
            if (status != 3) {
                status = 3;
                OrderButtonChange ( status );
                page = 1;
                getAllData ();
            }

        } else if (id == R.id.tv_order_yijiesuan) {
            if (status != 4) {
                status = 4;
                OrderButtonChange ( status );
                page = 1;
                getAllData ();
            }
        }
    }

    //改变背景
    private void ControlSwitch() {
        mLlHint.setVisibility ( View.VISIBLE );
        mVLine.setVisibility ( View.GONE );
        mLlOption.setVisibility ( View.GONE );
        mVBackground.setVisibility ( View.GONE );
    }

    //按钮改变状态
    private void ButtonChange(int variety) {
        if (variety == 1) {
            mTvTaoBao.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btn16f13 ) );
            mTvJingDong.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf6 ) );
            mTvPinDuoDuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf6 ) );
        }
        if (variety == 2) {
            mTvJingDong.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btn16f13 ) );
            mTvTaoBao.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf6 ) );
            mTvPinDuoDuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf6 ) );
        }
        if (variety == 3) {
            mTvPinDuoDuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btn16f13 ) );
            mTvJingDong.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf6 ) );
            mTvTaoBao.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf6 ) );
        }
    }

    //改变有效订单下层按钮状态
    private void OrderButtonChange(int status) {
        if (status == 1) {
            mTvYifukuan.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYifukuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
            mTvYishouhuo.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYishouhuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
            mTvYijiesuan.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYijiesuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
        }
        if (status == 2) {
            mTvYifukuan.setTextColor ( getResources ().getColor ( R.color.white ) );
            mTvYifukuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnf94a ) );
            mTvYishouhuo.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYishouhuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
            mTvYijiesuan.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYijiesuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
        }
        if (status == 3) {
            mTvYishouhuo.setTextColor ( getResources ().getColor ( R.color.white ) );
            mTvYishouhuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btn65abfb ) );
            mTvYifukuan.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYifukuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
            mTvYijiesuan.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYijiesuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
        }
        if (status == 4) {
            mTvYijiesuan.setTextColor ( getResources ().getColor ( R.color.white ) );
            mTvYijiesuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btn7cdb00 ) );
            mTvYishouhuo.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYishouhuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
            mTvYifukuan.setTextColor ( getResources ().getColor ( R.color.c_444444 ) );
            mTvYifukuan.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnff ) );
        }
    }
    //普通请求
    private void getAllData() {
        ApiCancleManager.getInstance ().removeAll ();
        //需要獲得orderNum订单号，选中时间，type等
        NetWork.getInstance ()
                .setTag ( Qurl.myOrder )
                .getApiService ( ModuleApi.class )
                .MyOrderShowAll (  status, page, time, 1 )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<OrderBean> ( OrderActivity.this, false, true ) {
                    @Override
                    public void onSuccess(OrderBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        SearchBotton=false;
                        //设置底部商品数据
                        setProductData ( result.getList () );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        showNull();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        showNull();
                    }
                } );
    }
    //搜索请求
    private void getSearchData() {
        ApiCancleManager.getInstance ().removeAll ();
        NetWork.getInstance ()
                .setTag ( Qurl.myOrder )
                .getApiService ( ModuleApi.class )
                .MyOrderShowSearch(1,orderNum)
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<OrderBean> ( OrderActivity.this, true, true ) {
                    @Override
                    public void onSuccess(OrderBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //设置底部商品数据
                        SearchBotton=true;
                        setProductData ( result.getList () );
                    }
                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        showNull();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        showNull();
                    }
                } );
    }
    //为null提示图
    private void showNull(){
        mNetError.setVisibility ( View.VISIBLE );
        if (goSearch){
            ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_dingdan ) );
            ((TextView)$(R.id.tv_common_click_try)).setText ( "呀！没有搜到该订单的相关信息" );
            ((TextView)$(R.id.tv_common_click_trys)).setText ( "换个订单号试试吧" );
        }else {
            ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_order ) );
            ((TextView)$(R.id.tv_common_click_try)).setText ( "暂无相关订单" );
        }
    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<OrderBean.ListBean> mProductList;
    private BaseQuickAdapter<OrderBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setProductData(List<OrderBean.ListBean> productList) {
        if (productList == null || productList.size () <= 0) {
            if (mload){
                mRvOrderList.setVisibility ( View.GONE );
                showNull();
            }
            mload = true;
            mloadt = true;
            return;
        } else {
            mRvOrderList.setVisibility ( View.VISIBLE );
            mNetError.setVisibility ( View.GONE );
        }
        mload = true;

        if (mProductList == null) {
            mProductList = new ArrayList<> ();
        }

        if (page == 1) {
            mProductList.clear ();
        }

        mProductList.addAll ( productList );

        if (mloadt&&mBaseProductQuickAdapter == null) {
                mBaseProductQuickAdapter = new BaseQuickAdapter<OrderBean.ListBean, BaseViewHolder> ( R.layout.item_order_list, mProductList ) {
                    @Override
                    protected void convert(BaseViewHolder helper, final OrderBean.ListBean item) {
                        try {
                            //商品图片
                            ImageView ivItemAllFenLei = helper.getView ( R.id.iv_item_all_product_tupian );
                            Glide.with ( BaseApplication.getApplication () ).load ( item.img ).placeholder ( R.drawable.bg_common_img_null ).into ( ivItemAllFenLei );

                            //商品淘宝ID
                            helper.setText ( R.id.tv_item_all_product_biaoti, item.goodsMsg );

                            //订单状态
                            TextView orderState = helper.getView ( R.id.tv_order_state );
                            final TextView itempayPrice = helper.getView ( R.id.tv_item_all_product_yuanjia );
                            final TextView itemawardDetails = helper.getView ( R.id.tv_item_all_product_xiaoliang );
                            if (item.orderStatus.equals ( "订单付款" )){
                                orderState.setText ( "   已付款   " );
                                orderState.setTextColor ( getResources ().getColor ( R.color.c_FF0000 ) );
                                orderState.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnyfk ) );
                            }else if (item.orderStatus.equals ( "订单结算" )){
                                orderState.setText ( "   已结算   " );
                                orderState.setTextColor ( getResources ().getColor ( R.color.c_14A51E ) );
                                orderState.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnyjs ) );
                            }else if (item.orderStatus.equals ( "订单失效" )){
                                orderState.setText ( "   已失效   " );
                                orderState.setTextColor ( getResources ().getColor ( R.color.c_6E6E6E ) );
                                orderState.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnysx ) );
                            }

                            //创建日期
                            helper.setText ( R.id.tv_item_date, "创建日 " + item.createTime );

                            //结算日期
                            final TextView jsData = helper.getView ( R.id.tv_item_jiesuandate );
                            if (status==3||status==4||item.orderStatus.equals ( "订单结算" )){
                                jsData.setVisibility ( View.VISIBLE );
                                helper.setText ( R.id.tv_item_jiesuandate, "结算日  "+item.jsTime );
                            }else {
                                jsData.setVisibility ( View.GONE );
                            }


                            //订单号
                            final TextView itemOrderNum = helper.getView ( R.id.tv_item_order_number );
                            itemOrderNum.setText ( "订单号："+item.orderNum );

                            //复制订单号
                            TextView orderCopy = helper.getView ( R.id.tv_order_copy );
                            orderCopy.setOnClickListener ( new View.OnClickListener () {
                                @Override
                                public void onClick(View view) {
                                    ClipboardManager cm = (ClipboardManager)getSystemService ( Context.CLIPBOARD_SERVICE ) ;
                                    cm.setText ( item.orderNum );
                                    Utils.toast ( "复制成功" );
                                }
                            } );

//                            if (status==5){
//                                itempayPrice.setVisibility ( View.GONE );
//                                itemawardDetails.setVisibility ( View.GONE );
//                            }else {
                                itempayPrice.setVisibility ( View.VISIBLE );
                                itemawardDetails.setVisibility ( View.VISIBLE );

                                DecimalFormat df = new DecimalFormat("#####0.00");
                                //消费金额
                                itempayPrice.setText ( "消费金额：¥ " + item.payPrice);
//                                itempayPrice.setText ( "消费金额：¥" + df.format ( Double.parseDouble ( item.payPrice)));

                                //预估佣金

                                itemawardDetails.setText ( "预估佣金：¥ " + item.awardDetails );
//                            }
                            RelativeLayout rlAll=helper.getView(R.id.rl_all);
                            rlAll.setOnClickListener(new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    try {
                                        RouterBean routerBean = new RouterBean();
                                        routerBean.type = 9;
//                                        routerBean.tbCouponId = mHomeAllEntities.get(position).mProductListBean.tbCouponId;
                                        routerBean.mustParam = "type=5"
                                                + "&id=" + (TextUtils.isEmpty(item.id) ? "" : item.id)
                                                + "&tbItemId=" + item.itemId;
                                        LinkRouterUtils.getInstance().setRouterBean(OrderActivity.this, routerBean);

                                    } catch (Exception e) {
                                        toast(e.toString());
                                    }
                                }
                            });
                        } catch (Exception e) {

                        }
                    }
                };
//            }

            mRvOrderList.setHasFixedSize ( true );
            mRvOrderList.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( OrderActivity.this, 1 );
            mRvOrderList.setLayoutManager ( gridLayoutManager );


            mRvOrderList.setAdapter ( mBaseProductQuickAdapter );
            mRvOrderList.addItemDecoration ( new GridItemDecoration (
                    new GridItemDecoration.Builder ( OrderActivity.this )
                            .margin ( 5, 5 )
                            .size ( 10 )
            ) );
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }
        mloadt=true;
    }


    /**
     * 获取底部的商品
     */
    private void getProductList() {

        NetWork.getInstance ()
                .setTag ( Qurl.myOrder )
                .getApiService ( ModuleApi.class )
                .MyOrderShowAll (  status, page, time, 1 )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<OrderBean> ( OrderActivity.this, true, false ) {

                    @Override
                    public void onSuccess(OrderBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //设置底部商品数据（下拉加载更多）
                        setProductData ( result.getList () );

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                    }
                } );
    }

    //返回键的操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !search && !goSearch) {
            finish ();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && search && !goSearch) {
            mIvSearch.setVisibility ( View.VISIBLE );
            mIvDate.setVisibility ( View.VISIBLE );
            mLlSearch.setVisibility ( View.GONE );
            mLlHint.setVisibility ( View.VISIBLE );
            mVLine.setVisibility ( View.VISIBLE );
            mVBackground.setVisibility ( View.GONE );
            search = false;
            getAllData ();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && search && goSearch) {
            mIvSearch.setVisibility ( View.VISIBLE );
            mIvDate.setVisibility ( View.VISIBLE );
            mLlSearch.setVisibility ( View.GONE );
            mLlHint.setVisibility ( View.VISIBLE );
            mVLine.setVisibility ( View.VISIBLE );
            mVBackground.setVisibility ( View.GONE );
            search = false;
            goSearch = false;
            getAllData ();
            /*finish ();
            ARouter.getInstance ().build ( ARouters.PATH_ORDER ).navigation ();*/
            return false;
        }

        return super.onKeyDown ( keyCode, event );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        mloadt = false;
        //判断用于首次没有加载出来数据时，刷新整体数据
            page++;
//            getProductList ();
        if (SearchBotton){
//            getSearchData();
            setRefreshLoadMoreState ( true, true );
        }else {
            getAllData ();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mSmartOrder.setNoMoreData(false);
        if (SearchBotton){
            getSearchData();
        }else {
            getAllData ();
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */




    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartOrder.finishRefresh ( state );
        } else if (noMoreData) {
            mSmartOrder.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartOrder.finishLoadMore ( state );
        }
    }

}
