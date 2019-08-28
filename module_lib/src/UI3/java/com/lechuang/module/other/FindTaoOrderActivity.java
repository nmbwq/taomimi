package java.com.lechuang.module.other;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.Utils;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FindTaoOrderBean;
import java.com.lechuang.module.other.bean.FindTaoOrderAdapter;
import java.com.lechuang.module.other.bean.FindTaoOrderEntity;
import java.com.lechuang.module.withdrawdeposit.utils.ClearEditText;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_FIND_TAO_ORDER)
public class FindTaoOrderActivity extends BaseActivity implements View.OnClickListener{
    private ClearEditText mCetWithdrawPrice;
//    private TextView mCetWithdrawPrice;
    private TextView mTvKezhuanchu,mTvDaijiesuan,mTvYijiesuan;
    private Button mBtnTx;
    private double aMoney;
    private double eMoney;
//    private TextView mTvFind;
    private EditText mEtOrderNum;
    private RecyclerView mRvFindTaoOrder;
    private LinearLayout mLlPage,mLlPageNull;
    private String MyOrderNum=null;
    private boolean showFirst=false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_tao_order;
    }

    @Override
    protected void findViews() {
        mCetWithdrawPrice = $(R.id.cet_wd_withdrawPrice);
        mTvKezhuanchu = $(R.id.tv_kezhuanchu);
        mTvDaijiesuan = $(R.id.tv_daijiesuan_num);
        mTvYijiesuan = $(R.id.tv_yijiesuan_num);
        mBtnTx = $(R.id.btn_wd_tx);
        mEtOrderNum = $(R.id.et_order_num);
        mRvFindTaoOrder = $(R.id.rv_find_tao_order);
        mLlPage = $(R.id.ll_find_tao_order_page);
        mLlPageNull = $(R.id.ll_find_tao_order_page_null);
        ((TextView) $(R.id.iv_common_title)).setText("订单找回");
//        ((ImageView) $(R.id.iv_common_right)).setVisibility(View.VISIBLE);
        ((RelativeLayout) $(R.id.rl_common_background)).setBackgroundColor(getResources().getColor(R.color.c_F4F4F4));
        ((TextView) $(R.id.tv_status_bar)).setBackgroundColor(getResources().getColor(R.color.c_F4F4F4));
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.iv_common_right).setOnClickListener(this);
        $(R.id.tv_find).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void getData() {
    }

    /**
     * 获取淘宝订单信息
     */
    private void getTaoOrder(String orderNum) {
        Map<String, Object> allParam = new HashMap<> ();
        allParam.put ( "tradeId", orderNum );
        NetWork.getInstance()
                .setTag(Qurl.findOrder)
                .getApiService(ModuleApi.class)
                .findOrder(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<FindTaoOrderBean>(this, false, false) {

                    @Override
                    public void onSuccess(final FindTaoOrderBean result) {
                        showFirst=true;
                        if (result == null||result.list==null) {
                            mLlPage.setVisibility(View.GONE);
                            mEtOrderNum.setText("");
                            mLlPageNull.setVisibility(View.VISIBLE);
                            mRvFindTaoOrder.setVisibility(View.GONE);
                            return;
                        }
                        mLlPage.setVisibility(View.GONE);
                        mEtOrderNum.setText("");
                        mLlPageNull.setVisibility(View.GONE);
                        mRvFindTaoOrder.setVisibility(View.VISIBLE);
                        setFindTaoOrderAdapter(result.list);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        showFirst=true;
                        mLlPage.setVisibility(View.GONE);
                        mEtOrderNum.setText("");
                        mLlPageNull.setVisibility(View.VISIBLE);
                        mRvFindTaoOrder.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showFirst=true;
                        mLlPage.setVisibility(View.GONE);
                        mEtOrderNum.setText("");
                        mLlPageNull.setVisibility(View.VISIBLE);
                        mRvFindTaoOrder.setVisibility(View.GONE);
                    }
                });
    }
    /**
     * 设置adapter数据
     *
     * @param result
     */
    private List<FindTaoOrderEntity> mHomeAllEntities;
    private FindTaoOrderAdapter mHomeRvAdapter;
    private void setFindTaoOrderAdapter(List<FindTaoOrderBean.ListBean> result) {
        if (mHomeAllEntities == null) {
            mHomeAllEntities = new ArrayList<>();
        }
        mHomeAllEntities.clear();

        if (result != null && result.size() > 0){
            FindTaoOrderEntity homeAllEntity = new FindTaoOrderEntity(FindTaoOrderEntity.TYPE_HEADER);
            //更新使用者数据
            homeAllEntity.head="some";
            mHomeAllEntities.add(homeAllEntity);
        }
        for (int i=0;i<result.size();i++){
            FindTaoOrderEntity homeAllEntity = new FindTaoOrderEntity(FindTaoOrderEntity.TYPE_PRODUCT);
            homeAllEntity.listBean=result.get(i);
            mHomeAllEntities.add(homeAllEntity);
        }
        if (mHomeRvAdapter ==null){
            mHomeRvAdapter = new FindTaoOrderAdapter<FindTaoOrderEntity, BaseViewHolder>(mHomeAllEntities){
                @Override
                protected void addItemTypeView() {
                    addItemType(FindTaoOrderEntity.TYPE_HEADER, R.layout.activity_find_tao_order_title);
                    addItemType(FindTaoOrderEntity.TYPE_PRODUCT, R.layout.item_find_tao_order_list);
                }
                @Override
                protected void convert(BaseViewHolder helper, final FindTaoOrderEntity item) {

                    if (helper.getItemViewType() == FindTaoOrderEntity.TYPE_HEADER) {

                    } else if (helper.getItemViewType() == FindTaoOrderEntity.TYPE_PRODUCT) {
                        try {
                            //商品图片
                            ImageView ivItemAllFenLei = helper.getView ( R.id.iv_item_all_product_tupian );
                            Glide.with ( BaseApplication.getApplication () ).load ( item.listBean.img ).placeholder ( R.drawable.bg_common_img_null ).into ( ivItemAllFenLei );

                            //商品标题
                            helper.setText ( R.id.tv_item_all_product_biaoti, item.listBean.goodsMsg );

                            //订单状态
                            TextView orderState = helper.getView ( R.id.tv_order_state );
                            final TextView itempayPrice = helper.getView ( R.id.tv_item_all_product_yuanjia );
                            final TextView itemawardDetails = helper.getView ( R.id.tv_item_all_product_xiaoliang );
                            if (item.listBean.orderStatus.equals ( "订单付款" )){
                                orderState.setText ( "   已付款   " );
                                orderState.setTextColor ( getResources ().getColor ( R.color.c_FF0000 ) );
                                orderState.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnyfk ) );
                            }else if (item.listBean.orderStatus.equals ( "订单结算" )){
                                orderState.setText ( "   已结算   " );
                                orderState.setTextColor ( getResources ().getColor ( R.color.c_14A51E ) );
                                orderState.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnyjs ) );
                            }else if (item.listBean.orderStatus.equals ( "订单失效" )){
                                orderState.setText ( "   已失效   " );
                                orderState.setTextColor ( getResources ().getColor ( R.color.c_6E6E6E ) );
                                orderState.setBackground ( getResources ().getDrawable ( R.drawable.bg_order_btnysx ) );
                            }

                            //创建日期
                            helper.setText ( R.id.tv_item_date, "创建日 " + item.listBean.createTime );

                            //结算日期
                            final TextView jsData = helper.getView ( R.id.tv_item_jiesuandate );
                                jsData.setVisibility ( View.VISIBLE );
                                helper.setText ( R.id.tv_item_jiesuandate, "结算日  "+item.listBean.jsTime );


                            //订单号
                            final TextView itemOrderNum = helper.getView ( R.id.tv_item_order_number );
                            itemOrderNum.setText ( "订单号："+item.listBean.orderNum );

                            //复制订单号
                            TextView orderCopy = helper.getView ( R.id.tv_order_copy );
                            orderCopy.setOnClickListener ( new View.OnClickListener () {
                                @Override
                                public void onClick(View view) {
                                    ClipboardManager cm = (ClipboardManager)getSystemService ( Context.CLIPBOARD_SERVICE ) ;
                                    cm.setText ( item.listBean.orderNum );
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
                            itempayPrice.setText ( "消费金额：¥ " + item.listBean.payPrice);
//                                itempayPrice.setText ( "消费金额：¥" + df.format ( Double.parseDouble ( item.payPrice)));

                            //预估佣金

                            itemawardDetails.setText ( "预估佣金：¥ " + item.listBean.awardDetails );
//                            }

                        } catch (Exception e) {
                            Logger.e("---->", e.toString());
                        }
                    }
                }
            };

            mRvFindTaoOrder.setHasFixedSize ( true );
            mRvFindTaoOrder.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( FindTaoOrderActivity.this, 1 );
            mRvFindTaoOrder.setLayoutManager ( gridLayoutManager );


            mRvFindTaoOrder.setAdapter ( mHomeRvAdapter );

        } else {
            mHomeRvAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
//            finish ();
            if (showFirst){
            mLlPage.setVisibility(View.VISIBLE);
            mEtOrderNum.setText("");
            mLlPageNull.setVisibility(View.GONE);
            mRvFindTaoOrder.setVisibility(View.GONE);
            showFirst=false;
            }else {
                finish ();
            }
        }else if (id ==R.id.iv_common_right){
            if (MyOrderNum!=null){
                getTaoOrder(MyOrderNum);
            }
//            mLlPage.setVisibility(View.VISIBLE);
//            mEtOrderNum.setText("");
//            mLlPageNull.setVisibility(View.GONE);
//            mRvFindTaoOrder.setVisibility(View.GONE);
        }else if (id==R.id.tv_find){
            String orderNum=mEtOrderNum.getText().toString().trim();
            if (orderNum.equals("")||orderNum==null){
                toast("订单号不能为空");
            }else {
                MyOrderNum=orderNum;
                getTaoOrder(orderNum);
            }
        }
    }
}
