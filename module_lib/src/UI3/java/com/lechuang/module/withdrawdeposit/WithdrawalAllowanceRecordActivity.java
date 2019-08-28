package java.com.lechuang.module.withdrawdeposit;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
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

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.OnClickEvent;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.BigWhellLuckInventoryBean;
import java.com.lechuang.module.bean.UserAllowanceListBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_WITHDRAWL_ALLOWANCE_RECORD)
public class WithdrawalAllowanceRecordActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartFenSi;
    private int page = 1;//页数
    private RecyclerView mRvFenSi;
    private boolean mload = true;
    private PopupWindow mPopupWindow;
    private TextView mPopTitle,mPopWeixin,mPopOther;
    private RelativeLayout mPopBtn;
    @Autowired()
    public String userId;
    private LinearLayout mNetError;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bigwhell_record;
    }

    @Override
    protected void findViews() {
        mSmartFenSi = $ ( R.id.smart_fensi );
        mRvFenSi = $ ( R.id.rv_fensi );
        ((TextView) $(R.id.iv_common_title)).setText("转出记录");
        $(R.id.iv_common_back).setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_bigwhell_record_null ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "无转出记录" );

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
        allParam.put ( "page", page+"" );
        NetWork.getInstance ()
                .setTag ( Qurl.userAllowanceList )
                .getApiService ( ModuleApi.class )
                .userAllowanceList ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<UserAllowanceListBean> ( WithdrawalAllowanceRecordActivity.this, true, true ) {

                    @Override
                    public void onSuccess(UserAllowanceListBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setInventory ( result.allowanceList );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }
                } );
    }

    private List<UserAllowanceListBean.AllowanceListBean> mProductList;
    private BaseQuickAdapter<UserAllowanceListBean.AllowanceListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setInventory(List<UserAllowanceListBean.AllowanceListBean> fansList) {
        if (fansList == null || fansList.size () <= 0) {
            if (mload) {
                mRvFenSi.setVisibility ( View.GONE );
                mNetError.setVisibility ( View.VISIBLE );
            }
            mload = true;
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

        mProductList.addAll ( fansList );
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<UserAllowanceListBean.AllowanceListBean, BaseViewHolder> ( R.layout.item_withdrawal_allowance_record_list, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, final UserAllowanceListBean.AllowanceListBean item) {
                    try {
                        //tv_title tv_time tv_money
                        helper.setText ( R.id.tv_title, item.typeStr);
                        helper.setText ( R.id.tv_time, item.createTimeStr  );
                        DecimalFormat df = new DecimalFormat("#####0.00");
                        helper.setText ( R.id.tv_money, "￥" + df.format(Double.parseDouble(item.awardMoney))  );

                    } catch (Exception e) {

                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( WithdrawalAllowanceRecordActivity.this, 1 );
            mRvFenSi.setLayoutManager ( gridLayoutManager );


            mRvFenSi.setAdapter ( mBaseProductQuickAdapter );
            /*mRvFenSi.addItemDecoration ( new GridItemDecoration (
                    new GridItemDecoration.Builder ( MyFansActivity.this )
                            .margin ( 5, 5 )
                            .size ( 10 )
            ) );*/
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        if (mBaseProductQuickAdapter != null) {
            page++;
            getAllData ();
        } else {
            page = 1;
            getAllData ();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mSmartFenSi.setNoMoreData(false);
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
        }
    }
}
