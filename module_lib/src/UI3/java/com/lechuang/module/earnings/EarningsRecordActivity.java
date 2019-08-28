package java.com.lechuang.module.earnings;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.Utils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.EarningsRecordBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


@Route(path = ARouters.PATH_EARNINGSRECORD)
public class EarningsRecordActivity extends BaseActivity implements OnRefreshLoadMoreListener {
    private SmartRefreshLayout mSmartRecord;
    private ClassicsHeader mSmartRecordHeader;

    private ImageView mIvBack;
    private LinearLayout mNetError;

    private int page = 1;//页数
    private boolean mload = true;

    private RecyclerView mRvRecord;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_earnings_record;
    }

    @Override
    protected void findViews() {
        mSmartRecord = $(R.id.smart_record);
        mSmartRecordHeader = $(R.id.smart_record_header);
        mRvRecord = $(R.id.rv_record_list);
        mIvBack = $(R.id.iv_common_back);
        mNetError = $(R.id.ll_net_error);
        ((ImageView) $(R.id.iv_common_image)).setImageDrawable(getResources().getDrawable(R.drawable.iv_common_tixian));
        ((TextView) $(R.id.tv_common_click_try)).setText("暂无提现的记录");
        ((TextView) $(R.id.iv_common_title)).setText("提现记录");

    }

    @Override
    protected void initView() {
        mSmartRecord.setOnRefreshLoadMoreListener(this);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void getData() {
        mSmartRecord.autoRefresh(500);
    }

    /**
     * 获取刷新数据
     */
    private void getAllData() {
        Map<String, String> allParam = new HashMap<>();
        String pageOne = page + "";
        allParam.put("page", pageOne);

        NetWork.getInstance()
                .setTag(Qurl.earningsRecord)
                .getApiService(ModuleApi.class)
                .EarningsRecord(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<EarningsRecordBean>(EarningsRecordActivity.this, true, false) {

                    @Override
                    public void onSuccess(EarningsRecordBean result) {
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
                        setRefreshLoadMoreState(false, false);
                        mNetError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        mNetError.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<EarningsRecordBean.ListBean> mProductList;
    private BaseQuickAdapter<EarningsRecordBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setProductData(List<EarningsRecordBean.ListBean> productList) {
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
            mBaseProductQuickAdapter = new BaseQuickAdapter<EarningsRecordBean.ListBean, BaseViewHolder>
                    (R.layout.item_earningsrecord_list, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, EarningsRecordBean.ListBean item) {
                    try {
                        //金额
                        helper.setText(R.id.tv_item_jine, "+ ￥" + item.cashMoney);
                        //状态
                        ImageView iv = helper.getView(R.id.iv_item_dui);

                        if (item.status == 0) {
                            helper.setText(R.id.tv_record_zhuangtai, "待处理");
                            iv.setBackground(getResources().getDrawable(R.drawable.ic_record_daichuli));
                        } else if (item.status == 1) {
                            helper.setText(R.id.tv_record_zhuangtai, "成功");
                            iv.setBackground(getResources().getDrawable(R.drawable.ic_record_chenggong));
                        } else if (item.status == 2) {
                            helper.setText(R.id.tv_record_zhuangtai, "已拒绝");
                            iv.setBackground(getResources().getDrawable(R.drawable.ic_record_jujue));
                        }


                        //订单号
                        final TextView itemOrderNum = helper.getView(R.id.tv_item_record_number);
                        //复制订单号
                        TextView orderCopy = helper.getView(R.id.tv_record_copy);

                        if (TextUtils.isEmpty(item.payNum)) {
                            itemOrderNum.setVisibility(View.INVISIBLE);
                            orderCopy.setVisibility(View.INVISIBLE);
                        } else {
                            itemOrderNum.setText("订单号：" + item.payNum);
                            itemOrderNum.setVisibility(View.VISIBLE);
                            orderCopy.setVisibility(View.VISIBLE);
                        }

                        orderCopy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setText(itemOrderNum.getText().toString());
                                Utils.toast("复制成功");
                            }
                        });

                        //時間
                        helper.setText(R.id.tv_item_date, stampToDate(item.createTime));


                    } catch (Exception e) {

                    }
                }
            };

            mRvRecord.setHasFixedSize(true);
            mRvRecord.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(EarningsRecordActivity.this, 1);
            mRvRecord.setLayoutManager(gridLayoutManager);


            mRvRecord.setAdapter(mBaseProductQuickAdapter);
            mRvRecord.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(EarningsRecordActivity.this)
                            .margin(5, 5)
                            .size(10)
            ));
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    public String stampToDate(long gmt) {
//        long lt = gmt + 8 * 3600;
        String res = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
//            res = simpleDateFormat.format(lt * 1000);
//            res = res.substring(res.length() - 14);
//            res = res.substring(0, res.length() - 3);
            res = simpleDateFormat.format(new Date(Long.valueOf(gmt)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
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
