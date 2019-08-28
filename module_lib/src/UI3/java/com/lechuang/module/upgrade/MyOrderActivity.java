package java.com.lechuang.module.upgrade;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.base.FragmentActivity;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.AlipayEntity;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.LoadImage;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.PayUtils;
import com.common.app.utils.ZeroBuyLoadImage;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sunfusheng.marqueeview.MarqueeView;

import org.greenrobot.eventbus.EventBus;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.adress.AdressListActivity;
import java.com.lechuang.module.bean.AddListBean;
import java.com.lechuang.module.bean.MyCardNotifyBean;
import java.com.lechuang.module.bean.MyCardStratPayBean;
import java.com.lechuang.module.bean.MyOrderBean;
import java.com.lechuang.module.bean.MyTryBean;
import java.com.lechuang.module.bean.PurchaseBean;
import java.com.lechuang.module.bean.QueryAlipayBean;
import java.com.lechuang.module.bean.ZeroBuyShareAppBean;
import java.com.lechuang.module.mytry.adapter.MyTryRvAdapter;
import java.com.lechuang.module.mytry.bean.MyTryAllEntity;
import java.com.lechuang.module.upgrade.bean.MyOrderAdapter;
import java.com.lechuang.module.upgrade.bean.MyOrderEntity;
import java.io.Console;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MY_ORDER)
public class MyOrderActivity extends BaseActivity implements PayUtils.PayResultListener, OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartFenSi;
    private ImageView imageView,mIvImage;
    private int page = 1;//页数
//    private LRecyclerView mRvFenSi;
    private RecyclerView mRvFenSi;
    private boolean mload = true;
    private TextView mTvName,mTvPhone,mTvAddress,mTvTitle,mTvProWords
            ,mTvPrice,mTvSmallNum,mTvCommodityNum;
    private LinearLayout userAddressNull,userAddress;
    private TextView mTvMyOrderPrice,getmTvMyOrderPriceSmall,tv_submit_order;
    //订单id
    @Autowired()
    public String id="";
    //主要支付的钱
    String money="";
    //地址id
    String adresssId="";
//    private LinearLayout mNetError;
    private boolean show=false;
    private boolean loadMore=true;//true没有上拉加载更多，false上拉加载更多

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_order;
    }

    @Override
    protected void findViews() {
        mSmartFenSi = $ ( R.id.smart_fensi );
        mRvFenSi = $ ( R.id.rv_mytry );

        tv_submit_order = $ ( R.id.tv_submit_order );
        mTvMyOrderPrice = $ ( R.id.tv_my_order_price );
        imageView = $ ( R.id.iv_macard_bg );
        userAddressNull=$(R.id.ll_address_null);
        userAddress=$(R.id.ll_address);
        mTvName=$(R.id.tv_name);
        mTvPhone=$(R.id.tv_phone);
        mTvAddress=$(R.id.tv_address);
        mIvImage=$(R.id.iv_item_all_product_tupian);
        mTvTitle=$(R.id.tv_my_order_biaoti);
        mTvProWords=$(R.id.item_my_order_explain);
        mTvPrice=$(R.id.tv_price);
        mTvSmallNum=$(R.id.tv_small_num);
        mTvCommodityNum=$(R.id.tv_commodity_num);
        getmTvMyOrderPriceSmall = $ ( R.id.tv_my_order_price_small );
        ((TextView) $(R.id.iv_common_title)).setText("确认下单");
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        tv_submit_order.setOnClickListener(this);

    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        mSmartFenSi.setOnRefreshLoadMoreListener ( this );
    }

    @Override
    protected void getData() {
//设置自动加载数据
        mSmartFenSi.autoRefresh ( 500 );
    }

    /**
     * 获取刷新数据
     */
    private void getAllData() {
        Map<String, Object> allParam = new HashMap<> ();
        allParam.put ( "id", id );
        NetWork.getInstance ()
                .setTag ( Qurl.getMyOrder )
                .getApiService ( ModuleApi.class )
                .getMyOrder ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<MyOrderBean> ( MyOrderActivity.this, true, false ) {

                    @Override
                    public void onSuccess(MyOrderBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );

                        DecimalFormat decimalFormat=new DecimalFormat("#0.00");
                        String price=decimalFormat.format(result.totalMoney)+"";
                        //支付包需要支付的总钱数
                        money=result.totalMoney+"";
                        String priceLong;
                        String priceShort;
                        if (price.contains(".")){
                            priceLong=price.substring(0,price.indexOf("."));
                            priceShort=price.substring(price.indexOf("."),price.length());
                            mTvMyOrderPrice.setText(priceLong );
                            getmTvMyOrderPriceSmall.setText( priceShort );
                        }else {
                            mTvMyOrderPrice.setText( price );
                        }
                        //地址

                        if (result.userAddress==null){
                            userAddressNull.setVisibility(View.VISIBLE);
                            userAddress.setVisibility(View.GONE);
                            userAddressNull.setOnClickListener(new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    startActivityForResult(new Intent(MyOrderActivity.this, AdressListActivity.class), 10000);
                                }
                            });
                        }else {
                            adresssId=result.userAddress.id+"";
                            userAddressNull.setVisibility(View.GONE);
                            userAddress.setVisibility(View.VISIBLE);
                            mTvName.setText(result.userAddress.receiverName);
                            mTvPhone.setText(result.userAddress.receiverPhone);
                            mTvAddress.setText(result.userAddress.areaAddress+result.userAddress.detailAddress+"");
                            userAddress.setOnClickListener(new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    startActivityForResult(new Intent(MyOrderActivity.this, AdressListActivity.class), 10000);
                                }
                            });
                        }
                        //商品图片
                        Glide.with(BaseApplication.getApplication()).load(result.product.showImg).placeholder(R.drawable.bg_common_img_null).into(mIvImage);
                        //标题
                        mTvTitle.setText( result.product.name );
                        //文字说明
                        mTvProWords.setText( result.product.proWords );

                        String prices=decimalFormat.format(result.product.price)+"";
                        String priceLongs;
                        String priceShorts;
                        if (price.contains(".")){
                            priceLongs=price.substring(0,price.indexOf("."));
                            priceShorts=price.substring(price.indexOf("."),price.length());
                            mTvPrice.setText( priceLongs );
                            mTvSmallNum.setText( priceShorts );
                        }else {
                            mTvPrice.setText( prices );
                        }
                        //数量
                        mTvCommodityNum.setText("X"+result.product.num);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
//                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
//                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }
                } );
    }
    /**
     * 设置adapter数据
     *
     * @param result
     */
    private List<MyOrderEntity> mHomeAllEntities;
    private MyOrderAdapter mHomeRvAdapter;
    private void setHomeAdapter(MyOrderBean result) {
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        mHomeAllEntities.clear();
            MyOrderEntity homeAllEntity = new MyOrderEntity(MyOrderEntity.TYPE_HEADER);
            homeAllEntity.userAddress = result.userAddress;
            homeAllEntity.product = result.product;
            mHomeAllEntities.add(homeAllEntity);
        if (mHomeRvAdapter ==null){
            mHomeRvAdapter = new MyOrderAdapter<MyOrderEntity, BaseViewHolder>(mHomeAllEntities){
                @Override
                protected void addItemTypeView() {
                    addItemType(MyOrderEntity.TYPE_HEADER, R.layout.activity_my_order_title);
                    addItemType(MyOrderEntity.TYPE_PRODUCT, R.layout.item_my_order_list);
                }
                @Override
                protected void convert(BaseViewHolder helper, final MyOrderEntity item) {

                    if (helper.getItemViewType() == MyOrderEntity.TYPE_HEADER) {
                        //商品图片
                        ImageView imageView=helper.getView(R.id.iv_item_all_product_tupian);
                        Glide.with(BaseApplication.getApplication()).load(item.product.showImg).placeholder(R.drawable.bg_common_img_null).into(imageView);
                        //标题
                        helper.setText( R.id.tv_my_order_biaoti,item.product.name );
                        //文字说明
                        helper.setText( R.id.item_my_order_explain,item.product.proWords );

                        DecimalFormat decimalFormat=new DecimalFormat("#0.00");
                        String price=decimalFormat.format(item.product.price)+"";
                        String priceLong;
                        String priceShort;
                        if (price.contains(".")){
                            priceLong=price.substring(0,price.indexOf("."));
                            priceShort=price.substring(price.indexOf("."),price.length());
                            helper.setText( R.id.tv_price,priceLong );
                            helper.setText( R.id.tv_small_num,priceShort );
                        }else {
                            helper.setText( R.id.tv_price,price );
                        }
                        //数量
                        helper.setText(R.id.tv_commodity_num,"X"+item.product.num);

                    } else if (helper.getItemViewType() == MyOrderEntity.TYPE_PRODUCT) {

                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( MyOrderActivity.this, 1 );
            mRvFenSi.setLayoutManager ( gridLayoutManager );


            mRvFenSi.setAdapter ( mHomeRvAdapter );

        } else {
            mHomeRvAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        show=false;
        loadMore=true;
        page = 1;
        getAllData ();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartFenSi.finishRefresh ( state );
        } else if (noMoreData) {
            mSmartFenSi.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartFenSi.finishLoadMore ( state );
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id ==R.id.iv_common_back){
            finish ();
        }else if (id == R.id.tv_common_right){
            Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_Mine_TRY).navigation();
            FragmentActivity.start(MyOrderActivity.this, mineTryFragment.getClass());
        }else if (id == R.id.tv_submit_order){
            startPayInfo();
        }
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     *
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==10000&&resultCode==10001){
            AddListBean.AddressListBean result = (AddListBean.AddressListBean)data.getExtras().getSerializable("resultBean");//得到新Activity 关闭后返回的数据
            adresssId=result.getId()+"";
            userAddressNull.setVisibility(View.GONE);
            userAddress.setVisibility(View.VISIBLE);
            mTvName.setText(result.getReceiverName()+"");
            mTvPhone.setText(result.getReceiverPhone()+"");
            mTvAddress.setText(result.getAreaAddress()+""+result.getDetailAddress()+"");
        }
    }

//    ----------------------------------下面是支付的代碼-------------------------------------------------
    /**
     * 订单支付
     */
    private void startPayInfo() {
        if (money.length() == 0) {
            toast("参数错误");
            return;
        }
        if (adresssId.length() == 0) {
            toast("请选择收获地址");
            return;
        }
        Log.d( "Debug","请求的参数钱为"+ money+"订单id"+id+"地址id"+adresssId);
        Map<String, Object> param = new HashMap<>();
        param.put("payment",   money+"");
        param.put("productId",   id+"");
        param.put("addressId",   adresssId+"");
        NetWork.getInstance()
                .setTag(Qurl.purchase)
                .getApiService(ModuleApi.class)
                .purchase( param )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<PurchaseBean>(MyOrderActivity.this, false, true) {

                    @Override
                    public void onSuccess(PurchaseBean result) {
                        if (result == null) {
                            toast("获取支付数据异常！");
                            return;
                        }
                        if (result.status == 200) {
                                if (result.returnMap == null || TextUtils.isEmpty(result.returnMap.getSign()) || TextUtils.isEmpty(result.returnMap.getPaymentNo())) {
                                    toast("获取支付数据异常！");
                                    return;
                                }
                                PayUtils payUtils = new PayUtils(MyOrderActivity.this);
                                AlipayEntity alipayEntity = new AlipayEntity();
                                alipayEntity.setOrderid(result.returnMap.getPaymentNo());
                                alipayEntity.setOrderstring(result.returnMap.getSign());
                                payUtils.payByAliPay(alipayEntity);
                                payUtils.setResultListener(MyOrderActivity.this);
                           
                        } else if (result.status == 500) {
                            toast(result.errorMsg);
                        }
                    }
                });
    }

    /**
     * 订单查看
     * @param paymentNo
     */
    private void notyfyInfoUpdata(String paymentNo) {

        Map<String, Object> param = new HashMap<>();
        param.put("paymentNo", paymentNo);
        NetWork.getInstance()
                .setTag(Qurl.queryAlipay)
                .getApiService(ModuleApi.class)
                .queryAlipay(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<QueryAlipayBean>(MyOrderActivity.this, false, true) {

                    @Override
                    public void onSuccess(QueryAlipayBean result) {
                        if (result == null) {
                            toast("获取支付数据异常！");
                            return;
                        }
                        if (result.status == 200) {
                            toast(TextUtils.isEmpty(result.errorMsg) ? "支付成功！" : result.errorMsg);
                            //弹窗操作  跳转到赚钱的界面
                            EventBus.getDefault().post(Constant.THREEKEY_HOME);
                            //赚钱界面弹窗
                            EventBus.getDefault().post(Constant.PAY_UPDATE);
                        } else {
                            toast(TextUtils.isEmpty(result.errorMsg) ? "支付失败！" : result.errorMsg);
                        }
                    }

                });
    }



    @Override
    public void aliPayCallBack(String orderId) {
        notyfyInfoUpdata(orderId);
    }
    //支付失败
    @Override
    public void aliPayCancle(String errorInfo) {

    }
    //
    @Override
    public void aliPayFailOther(String errorCode, String errorInfo) {

    }

}
