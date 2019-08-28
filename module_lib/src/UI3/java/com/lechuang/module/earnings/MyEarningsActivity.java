package java.com.lechuang.module.earnings;

import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.view.CommonPopupwind;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.MyEarningsBean;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MYEARNINGS)
public class MyEarningsActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {
    private RelativeLayout mRlCommonBackground, mRlTodayCount, mRlTodayEarnings, mRlYesterdayCount, mRlYesterdayEarnings;
    private LinearLayout mLlLastMonthAccount, mLlLastMonthForecast, mLlThisMonthForecast;
    private ImageView mIvCommonBack;
    private TextView mTvStatusBar, mTvCommonTitle, mTvCommonRight, tvPopKnow, tvPopTitle, tvPopContent1, tvPopContent2, tvPopContent3
            , mTvTaoBao, mTvJingDong, mTvPinDuoDuo, mTvLastMonthAccount, mTvLastMonthForecast, mTvThisMonthForecast, mTvTodayCount
            , mTvTodayEarnings, mTvYesterdayCount, mTvYesterdayEarnings, mTvLeiJiJieSuanShouYi;
    private PopupWindow mPopupWindow;

    private SmartRefreshLayout mSmartEarning;
    private ClassicsHeader mSmartEarningHeader;

    private int type = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_earnings_one;
    }

    @Override
    protected void findViews() {
        mSmartEarning = $ ( R.id.smart_earnings );
        mSmartEarningHeader = $ ( R.id.smart_earnings_header );
        mRlCommonBackground = $ ( R.id.rl_common_background );
//        mTvStatusBar = $ ( R.id.tv_status_bar );
        mIvCommonBack = $ ( R.id.iv_common_back );
        mTvCommonTitle = $ ( R.id.iv_common_title );
        mTvCommonRight = $ ( R.id.tv_common_right );
        mTvTaoBao = $ ( R.id.tv_myearnings_taobao );
        mTvJingDong = $ ( R.id.tv_myearnings_jingdong );
        mTvPinDuoDuo = $ ( R.id.tv_myearnings_pinduoduo );
        //刷新改变值
        mTvLeiJiJieSuanShouYi = $ ( R.id.tv_myearnings_leijijiesuanshouyi );
        mTvLastMonthAccount = $ ( R.id.tv_myearnings_lastmonthaccount );
        mTvLastMonthForecast = $ ( R.id.tv_myearnings_lastmonthforecast );
        mTvThisMonthForecast = $ ( R.id.tv_myearnings_thismonthforecast );
        mTvTodayCount = $ ( R.id.tv_myearnings_todaycount );
        mTvTodayEarnings = $ ( R.id.tv_myearnings_todayearnings );
        mTvYesterdayCount = $ ( R.id.tv_myearnings_yesterdaycount );
        mTvYesterdayEarnings = $ ( R.id.tv_myearnings_yesterdayrarnings );


        mLlLastMonthAccount = $ ( R.id.ll_myearnings_lastmonthaccount );
        mLlLastMonthForecast = $ ( R.id.ll_myearnings_lastmonthforecast );
        mLlThisMonthForecast = $ ( R.id.ll_myearnings_thismonthforecast );

        mRlTodayCount = $ ( R.id.rl_myearnings_todaycount );
        mRlTodayEarnings = $ ( R.id.rl_myearnings_todayearnings );
        mRlYesterdayCount = $ ( R.id.rl_myearnings_yesterdaycount );
        mRlYesterdayEarnings = $ ( R.id.rl_myearnings_yesterdayrearnings );
//        mTvStatusBar.setBackgroundColor ( ContextCompat.getColor ( this, R.color.white ) );
        mIvCommonBack.setOnClickListener ( this );
        mTvCommonRight.setOnClickListener ( this );
        mTvTaoBao.setOnClickListener ( this );
        mTvJingDong.setOnClickListener ( this );
        mTvPinDuoDuo.setOnClickListener ( this );

        mLlLastMonthAccount.setOnClickListener ( this );
        mLlLastMonthForecast.setOnClickListener ( this );
        mLlThisMonthForecast.setOnClickListener ( this );

        mRlTodayCount.setOnClickListener ( this );
        mRlTodayEarnings.setOnClickListener ( this );
        mRlYesterdayCount.setOnClickListener ( this );
        mRlYesterdayEarnings.setOnClickListener ( this );
        ((TextView)$ ( R.id.rl_myearnings_orderdetail )).setOnClickListener( this );
    }

    @Override
    protected void initView() {
        mSmartEarning.setOnRefreshLoadMoreListener ( this );
        mSmartEarning.setEnableLoadMore ( false );
    }

    @Override
    protected void getData() {
//smart设置属性，设置自动刷新，调用刷新方法
        mSmartEarning.autoRefresh ( 100 );
    }

    private void getAllData() {
        Map<String, String> allParam = new HashMap<> ();
        allParam.put ( "type",type+"" );

        NetWork.getInstance ()
                .setTag ( Qurl.myEarnings )
                .getApiService ( ModuleApi.class )
                .MyEarnings ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<MyEarningsBean> ( MyEarningsActivity.this, true, true ) {

                    @Override
                    public void onSuccess(MyEarningsBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //设置底部商品数据
                        if (result == null){
                            return;
                        }
                        changeState(result.getList ().getTotal (),result.getList ().Settlementlastmonth,result.getList ().Estimatedlastmonth,result.getList ().monthlyestimates,result.getList ().PaymenttodayNum,result.getList ().Paymenttoday,result.getList ().PaymentyesterdayNum,result.getList ().Paymentyesterday);
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

    /**
     * @param leijijiesuanshouyi 累计结算收益
     * @param lastmonthaccount   上月结算
     * @param lastmonthforecast  上月预估
     * @param thismonthforecast  本月预估
     * @param todaycount         今日预付笔数
     * @param todayearnings      今日预估收益
     * @param yesterdaycount     昨日预付笔数
     * @param yesterdayrarnings  昨日预估收益
     */
    private void changeState(String leijijiesuanshouyi, String lastmonthaccount, String lastmonthforecast, String thismonthforecast
            , String todaycount, String todayearnings, String yesterdaycount, String yesterdayrarnings) {
        DecimalFormat df = new DecimalFormat("#####0.00");
        lastmonthaccount = df.format ( Double.parseDouble ( lastmonthaccount ) );
        lastmonthforecast = df.format ( Double.parseDouble ( lastmonthforecast ) );
        thismonthforecast = df.format ( Double.parseDouble ( thismonthforecast ) );
        todayearnings = df.format ( Double.parseDouble ( todayearnings ) );
        yesterdayrarnings = df.format ( Double.parseDouble ( yesterdayrarnings ) );
        mTvLeiJiJieSuanShouYi.setText ( "￥"+leijijiesuanshouyi );
        mTvLastMonthAccount.setText ( "￥"+lastmonthaccount );
        mTvLastMonthForecast.setText ( "￥"+lastmonthforecast );
        mTvThisMonthForecast.setText ( "￥"+thismonthforecast );
        mTvTodayCount.setText ( todaycount );
        mTvTodayEarnings.setText ( ""+todayearnings );
        mTvYesterdayCount.setText ( yesterdaycount );
        mTvYesterdayEarnings.setText ( ""+yesterdayrarnings );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id == R.id.iv_common_back) {//关闭
            finish ();
        } else if (id == R.id.tv_common_right) {
            ARouter.getInstance().build(ARouters.PATH_EARNINGSRECORD).navigation();
        } else if (id == R.id.tv_popwindow_know) {
            mPopupWindow.dismiss ();
        } else if (id == R.id.tv_myearnings_taobao) {
            if (type != 1) {
                type = 1;
                ButtonChange ( type );//按钮改变状态
            }
        } else if (id == R.id.tv_myearnings_jingdong) {
            if (type != 2) {
                type = 2;
                ButtonChange ( type );//按钮改变状态
            }
        } else if (id == R.id.tv_myearnings_pinduoduo) {
            if (type != 3) {
                type = 3;
                ButtonChange ( type );//按钮改变状态
            }
        } else if (id == R.id.ll_myearnings_lastmonthaccount) {
            showPopupWindow ( "上月结算", "上个月内确认收货的订单收","益，每月25日结算后，将转","入到余额中" );
        } else if (id == R.id.ll_myearnings_lastmonthforecast) {
            showPopupWindow ( "上月预估", "上月内创建的所有订单预估","收益","" );
        } else if (id == R.id.ll_myearnings_thismonthforecast) {
            showPopupWindow ( "本月预估", "本月内创建的所有订单预估","收益","" );
        } else if (id == R.id.rl_myearnings_todaycount) {
            showPopupWindow ( "今日付款笔数", "所有付款的订单数量,包含","有效订单和失效订单","" );
        } else if (id == R.id.rl_myearnings_todayearnings) {
            showPopupWindow ( "今日预估收益", "今天内创建的有效订单","预估收益","" );
        } else if (id == R.id.rl_myearnings_yesterdaycount) {
            showPopupWindow ( "昨日付款笔数", "所有付款的订单数量,包含","有效订单和失效订单","" );
        } else if (id == R.id.rl_myearnings_yesterdayrearnings) {
            showPopupWindow ( "昨日预估收益", "昨日创建的有效订单","预估收益","" );
        } else if (id == R.id.rl_myearnings_orderdetail) {
            ARouter.getInstance().build(ARouters.PATH_ORDER).navigation();
        }
    }

    //按钮改变状态
    private void ButtonChange(int type) {
        if (type == 1) {
            mTvTaoBao.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btnred ) );
            mTvJingDong.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btn00 ) );
            mTvPinDuoDuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btn00 ) );
            mTvTaoBao.setTextColor ( getResources ().getColor ( R.color.white ) );
            mTvJingDong.setTextColor ( getResources ().getColor ( R.color.c_353535 ) );
            mTvPinDuoDuo.setTextColor ( getResources ().getColor ( R.color.c_353535 ) );
        }
        if (type == 2) {
            mTvJingDong.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btnred ) );
            mTvTaoBao.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btn00 ) );
            mTvPinDuoDuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btn00 ) );
            mTvJingDong.setTextColor ( getResources ().getColor ( R.color.white ) );
            mTvTaoBao.setTextColor ( getResources ().getColor ( R.color.c_353535 ) );
            mTvPinDuoDuo.setTextColor ( getResources ().getColor ( R.color.c_353535 ) );
        }
        if (type == 3) {
            mTvPinDuoDuo.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btnred ) );
            mTvJingDong.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btn00 ) );
            mTvTaoBao.setBackground ( getResources ().getDrawable ( R.drawable.bg_myearnings_btn00 ) );
            mTvPinDuoDuo.setTextColor ( getResources ().getColor ( R.color.white ) );
            mTvTaoBao.setTextColor ( getResources ().getColor ( R.color.c_353535 ) );
            mTvJingDong.setTextColor ( getResources ().getColor ( R.color.c_353535 ) );
        }
        getAllData ();
    }

    //弹出提示
    private void showPopupWindow(String title, String content1, String content2, String content3) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_myearnings_know, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
        tvPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_title );
        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
        tvPopContent2= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
        tvPopContent3 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content3 );
        tvPopTitle.setText ( title );
        tvPopContent1.setText ( content1 );
        tvPopContent2.setText ( content2 );
        tvPopContent3.setText ( content3 );
        tvPopKnow.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mSmartEarning.finishLoadMore ();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getAllData ();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        mSmartEarning.finishRefresh ( state );

    }
}
