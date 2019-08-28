package java.com.lechuang.module.card;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.EarningsRecordBean;
import java.com.lechuang.module.bean.MyCardExchangeBean;
import java.com.lechuang.module.bean.MyCardExchangeRecordBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MY_CARD_EXCHANGE_RECORD)
public class MyCardExchangeRecordActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener{
    private SmartRefreshLayout mSmartRecord;
    private RecyclerView mRvRecord;
    private int page = 1;//页数
    private int num = 0;//页数
    private boolean mload = true;
    private PopupWindow mPopupWindow;
    private ImageView mIvDiss;
    private LinearLayout mNetError;
    private TextView mTvContentOne,mTvContentTwo;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mycard_exchange_record;
    }

    @Override
    protected void findViews() {
        mRvRecord=$(R.id.rv_record_list);
        mSmartRecord=$(R.id.smart_record);
        $(R.id.iv_common_back).setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
//        mEtAlipayNumber = $(R.id.et_bind_alipayNumber);
//        mEtAlipayRealName = $(R.id.et_bind_alipayRealName);
//        mTvSave = $(R.id.et_bind_zfb_save);
//        mTvSave.setOnClickListener ( this );
//        ((TextView) $(R.id.tv_common_right)).setVisibility( View.VISIBLE );
        ((TextView) $(R.id.tv_common_right)).setOnClickListener ( this );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_mycardrecord_null ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "暂无记录" );
    }

    @Override
    protected void initView() {
        mSmartRecord.setOnRefreshLoadMoreListener(this);
        mSmartRecord.setEnableLoadMore ( true );
    }

    @Override
    protected void getData() {
        mSmartRecord.autoRefresh(500);
//        getAllData();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_bind_zfb_save){
        }else if (id== R.id.iv_common_back){
            finish();
        }else if (id==R.id.tv_common_right){
            ARouter.getInstance().build(ARouters.PATH_MY_CARD_RECORD).navigation();
        }else if (id==R.id.iv_popwindow_diss){
            mPopupWindow.dismiss();
        }
    }

    private void getAllData() {

        Map<String, Object> allParam = new HashMap<>();
        String pageOne = page + "";
        allParam.put("page", pageOne);

        NetWork.getInstance()
                .setTag(Qurl.myCardExchangeRecord)
                .getApiService(ModuleApi.class)
                .myCardExchangeRecord(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardExchangeRecordBean>(MyCardExchangeRecordActivity.this, true, false) {

                    @Override
                    public void onSuccess(MyCardExchangeRecordBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        setProductData((result.getList()));
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
//                        setRefreshLoadMoreState(false, false);
//                        mNetError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        setRefreshLoadMoreState(false, false);
//                        mNetError.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<MyCardExchangeRecordBean.ListBean> mProductList;
    private BaseQuickAdapter<MyCardExchangeRecordBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setProductData(List<MyCardExchangeRecordBean.ListBean> productList) {
        if (productList == null || productList.size() == 0) {
            if (mload) {
                mNetError.setVisibility(View.VISIBLE);
                mRvRecord.setVisibility(View.GONE);
            }
            mload = true;
            return;
        } else {
            mNetError.setVisibility(View.GONE);
            mRvRecord.setVisibility(View.VISIBLE);
        }
        if (mProductList == null) {
            mProductList = new ArrayList<>();
        }
        if (page == 1) {
            mProductList.clear();
        }
        mNetError.setVisibility(View.INVISIBLE);
        mProductList.addAll(productList);

        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<MyCardExchangeRecordBean.ListBean, BaseViewHolder>
                    (R.layout.item_mycardexchangerecord_list, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, MyCardExchangeRecordBean.ListBean item) {
                    try {
                        //使用专区 tv_name 什么卡 tv_card 时间 tv_time
                        helper.setText(R.id.tv_time, item.aquiredTime);
                        helper.setText(R.id.tv_name, "CDKEY:"+item.secretKey);
                        num=0;
                        String statusString = "";
                        for (int i=0;i<item.miCardDetail.length();i++){
                            if (item.miCardDetail.substring( i,i+1 ).equals( "，" )||item.miCardDetail.substring( i,i+1 ).equals( "," )){
                                //添加3个逗号一个换行符
                                /*num++;
                                if (num!=3){
                                    statusString=statusString+"  ";
                                }else {
                                    statusString=statusString+"\n";
                                }*/
                                //不添加换行符
                                statusString=statusString+"  ";
                            }else {
                                statusString=statusString+item.miCardDetail.substring( i,i+1 );
                            }
                        }
                        helper.setText(R.id.tv_card, statusString);
                    } catch (Exception e) {

                    }
                }
            };

            mRvRecord.setHasFixedSize(true);
            mRvRecord.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MyCardExchangeRecordActivity.this, 1);
            mRvRecord.setLayoutManager(gridLayoutManager);

            mRvRecord.setNestedScrollingEnabled(false);
            mRvRecord.setAdapter(mBaseProductQuickAdapter);
            mRvRecord.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(MyCardExchangeRecordActivity.this)
                            .margin(5, 5)
                            .size(10)
            ));
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    //弹出提示
    private void showPopupWindow(String title,String colour) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mycard, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
//        iv_card_img
//        mIvColour = (ImageView) contentView.findViewById( R.id.iv_card_img );
        mIvDiss = (ImageView) contentView.findViewById( R.id.iv_popwindow_diss );
        mTvContentOne = (TextView) contentView.findViewById ( R.id.tv_popwindow_content );
//        mTvContentTwo= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        mTvContentTwo= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
//        tvPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_title );
//        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
//        tvPopContent2= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopContent3 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content3 );
//        tvPopTitle.setText ( title );
//        tvPopContent1.setText ( content1 );
//        tvPopContent2.setText ( content2 );
//        tvPopContent3.setText ( content3 );
        mIvDiss.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        page++;
        getAllData();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mSmartRecord.setNoMoreData(false);
        getAllData();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartRecord.finishRefresh(state);
        } else if (noMoreData) {
            mSmartRecord.finishLoadMoreWithNoMoreData();
        } else {
            mSmartRecord.finishLoadMore(state);
        }
    }
}
