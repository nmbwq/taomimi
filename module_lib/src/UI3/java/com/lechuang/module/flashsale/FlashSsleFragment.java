package java.com.lechuang.module.flashsale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.Utils;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.SpannelTextViewSinge;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FenSiBean;
import java.com.lechuang.module.bean.FlashSaleDetailsBean;
import java.com.lechuang.module.bean.FlashSaleIdBean;
import java.com.lechuang.module.bean.OrderBean;
import java.com.lechuang.module.order.OrderActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */
@Route(path = ARouters.PATH_FLASH_SALE_FRAGMENT)
public class FlashSsleFragment extends LazyBaseFragment implements OnRefreshLoadMoreListener {

    private SmartRefreshLayout mSmartFenSi;
    public int page = 1;//页数
    private RecyclerView mRvFenSi;
    private boolean mload = true, mloadt = true;
    private TextView mTvVip, mTvCommon;
    private RelativeLayout tvPopKnow;
    //    @Autowired(name = "type")
//    public String ll;
    @Autowired
    public String type;
    @Autowired
    public String xtabs;
    @Autowired
    public int getNum;
    @Autowired
    public int num;
    public String variety = "2";
    private LinearLayout mNetError;
    private boolean onShow = false;
    private PopupWindow mPopupWindow;


    @Override
    protected int getLayoutId() {

        return R.layout.fragment_flash_sale;
    }

    @Override
    protected void findViews() {
        EventBus.getDefault().register( this );
        mSmartFenSi = mInflate.findViewById( R.id.smart_fensi );
        mRvFenSi = mInflate.findViewById( R.id.rv_fensi );
        mTvVip = mInflate.findViewById( R.id.tv_vip_number );
        mTvCommon = mInflate.findViewById( R.id.tv_common_number );
        mNetError = $( R.id.ll_net_error );
        ((ImageView) $( R.id.iv_common_image )).setImageDrawable( getResources().getDrawable( R.drawable.iv_common_flashsale ) );
        ((TextView) $( R.id.tv_common_click_try )).setText( "都被抢光了,下次早点来哦~" );
        ((TextView) $( R.id.tv_flash_sale )).setVisibility( View.VISIBLE );
        ((TextView) $( R.id.tv_flash_sale )).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlashSaleActivity.flashSaleActivity.finish(  );
            }
        } );
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject( this );
        mSmartFenSi.setOnRefreshLoadMoreListener( this );
//        int i = fenSiActivity;
    }

    @Override
    protected void getData() {
        //设置自动加载数据
        /*if (onShow){
            mSmartFenSi.autoRefresh ( 500 );
            onShow=false;
        }*/
        mSmartFenSi.autoRefresh( 500 );
    }

    private Context context;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
        this.context = getActivity();
    }


    @Override
    public void onAttach(Activity Activity) {
        super.onAttach( Activity );
        this.context = Activity;
    }

    //普通请求
    private void getAllData() {
        ApiCancleManager.getInstance().removeAll();
        //需要獲得orderNum订单号，选中时间，type等
        Map<String, Object> allParam = new HashMap<>();
        allParam.put( "page",page );
        allParam.put( "time",xtabs );
        NetWork.getInstance()
                .setTag( Qurl.flashSaleDetails )
                .getApiService( ModuleApi.class )
                .flashSaleDetailsBean( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<FlashSaleDetailsBean>( getActivity(), false, false ) {
                    @Override
                    public void onSuccess(FlashSaleDetailsBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState( true, true );
                            mNetError.setVisibility( View.VISIBLE );
                            mRvFenSi.setVisibility( View.GONE );
                            return;
                        }
                        setRefreshLoadMoreState( true, false );
                        if (result.count==0){
                            mNetError.setVisibility( View.VISIBLE );
                            mRvFenSi.setVisibility( View.GONE );
                        }else
                        //设置底部商品数据
                        setProductData( result.getList() );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed( errorCode, moreInfo );
                        setRefreshLoadMoreState( false, false );
//                        mNetError.setVisibility( View.VISIBLE );
//                        mRvFenSi.setVisibility( View.GONE );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError( e );
                        setRefreshLoadMoreState( false, false );
//                        mNetError.setVisibility( View.VISIBLE );
//                        mRvFenSi.setVisibility( View.GONE );
                    }
                } );
    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<FlashSaleDetailsBean.ListBean> mProductList;
    private BaseQuickAdapter<FlashSaleDetailsBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setProductData(List<FlashSaleDetailsBean.ListBean> productList) {
        if (productList == null || productList.size () <= 0) {
            if (mload){
                mRvFenSi.setVisibility ( View.GONE );
//                showNull();
            }
            mload = true;
            mloadt = true;
            return;
        } else {
            mRvFenSi.setVisibility ( View.VISIBLE );
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
            mBaseProductQuickAdapter = new BaseQuickAdapter<FlashSaleDetailsBean.ListBean, BaseViewHolder> ( R.layout.item_flashsale_list, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, final FlashSaleDetailsBean.ListBean item) {
                    try {
                        //商品图片
                        ImageView ivItemAllFenLei = helper.getView ( R.id.iv_item_flash_sale_product_tupian );
                        Glide.with ( BaseApplication.getApplication () ).load ( item.pic ).placeholder ( R.drawable.bg_common_img_null ).into ( ivItemAllFenLei );

                        //商品淘宝ID
                        helper.setText ( R.id.tv_item_search_result_product_biaoti, item.d_title );
                        /*SpannelTextViewSinge spannelTextViewSinge = helper.getView ( R.id.tv_item_search_result_product_biaoti );
                        spannelTextViewSinge.setDrawText ( item.title );
                        spannelTextViewSinge.setShopType ( 1 );*/

                        //劵金额
                        helper.setText ( R.id.tv_item_search_result_product_quan, "¥" + item.quan_jine );

                        //关键字
                        helper.setText ( R.id.tv_two_price,  item.new_words );

                        //销量
                        helper.setText ( R.id.tv_item_search_result_product_xiaoliang,  ""+item.xiaoliang+"" );

                        //劵后价
                        helper.setText ( R.id.tv_item_search_result_product_xianjia, "¥" + item.jiage );
                        TextView textView =helper.getView ( R.id.tv_item_mashangqiang );
                        if (getNum<num){
                            textView.setText( "即将开抢" );
                            textView.setBackground( getResources ().getDrawable ( R.drawable.bg_flashasle_unqiang ) );
                            helper.getView ( R.id.ll_search_single_product ).setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showPopupWindow();
                                }
                            } );
                        }else {
                            textView.setText( "马上抢" );
                            textView.setBackground( getResources ().getDrawable ( R.drawable.bg_flashasle_qiang ) );
                            helper.getView ( R.id.ll_search_single_product ).setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        getTbItemId(item.id);
                                    } catch (Exception e) {
                                        toast(e.toString());
                                    }
                                }
                            } );
                        }




                    } catch (Exception e) {

                    }
                }
            };
//            }

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( getActivity(), 1 );
            mRvFenSi.setLayoutManager ( gridLayoutManager );


            mRvFenSi.setAdapter ( mBaseProductQuickAdapter );
            /*mRvFenSi.addItemDecoration ( new GridItemDecoration (
                    new GridItemDecoration.Builder ( getActivity() )
                            .margin ( 5, 5 )
                            .size ( 10 )
            ) );*/
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }
        mloadt=true;
    }

    public void getTbItemId(final String id){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put( "id",id );
        NetWork.getInstance()
                .setTag( Qurl.flashSaleId )
                .getApiService( ModuleApi.class )
                .getFlashSaleId( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<FlashSaleIdBean>( getActivity(), false, false ) {
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

    //弹出提示
    private void showPopupWindow(){
        View contentView = LayoutInflater.from ( getContext() ).inflate ( R.layout.popupwind_flash_sale_know, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnow = (RelativeLayout) contentView.findViewById ( R.id.tv_popwindow_know );
        tvPopKnow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        mloadt = false;
        //判断用于首次没有加载出来数据时，刷新整体数据
        if (mBaseProductQuickAdapter != null) {
            page++;
//            getProductList();
            getAllData();
        } else {
            page = 1;
            getAllData();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mSmartFenSi.setNoMoreData( false );
        getAllData();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartFenSi.finishRefresh( state );
            if (!state) {
                //第一次加载失败时，再次显示时可以重新加载
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartFenSi.finishLoadMoreWithNoMoreData();
        } else {
            mSmartFenSi.finishLoadMore( state );
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint( isVisibleToUser );
        if (isVisibleToUser) {
            if (onShow) {
                mSmartFenSi.autoRefresh( 500 );
            }
            onShow = true;
            if (variety.equals( "0" )) {
                EventBus.getDefault().post( Constant.SENDDATAO );
            } else if (variety.equals( "1" )) {
                EventBus.getDefault().post( Constant.SENDDATAONE );
            } else {
                EventBus.getDefault().post( Constant.SENDDATA );
            }
        }
    }



    /*@Override
    public void onPause() {
        super.onPause ();
        EventBus.getDefault().unregister(this);
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (!onShow) {
            if (message.equalsIgnoreCase( Constant.SENDDATA2 )) {
                //当前界面如果显示，就立即刷新，否则滑动显示是刷新
                if (getUserVisibleHint()) {
                    variety = "";
                    mSmartFenSi.autoRefresh( 100 );
                } else {
                    this.mIsFirstVisible = true;
                }

            } else if (message.equalsIgnoreCase( Constant.SENDDATAO0 )) {
                //当前界面如果显示，就立即刷新，否则滑动显示是刷新
                if (getUserVisibleHint()) {
                    variety = "0";
                    mSmartFenSi.autoRefresh( 100 );
                } else {
                    this.mIsFirstVisible = true;
                }

            } else if (message.equalsIgnoreCase( Constant.SENDDATAONE1 )) {
                //当前界面如果显示，就立即刷新，否则滑动显示是刷新
                if (getUserVisibleHint()) {
                    variety = "1";
                    mSmartFenSi.autoRefresh( 100 );
                } else {
                    this.mIsFirstVisible = true;
                }

            }
        }
    }
}
