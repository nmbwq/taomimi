package java.com.lechuang.module.signin;


import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.UserInfoBeanDao;
import com.common.app.database.bean.UserInfoBean;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.MyParticipateStateBean;
import java.com.lechuang.module.bean.MySigninUserPunchInHistoryBean;
import java.com.lechuang.module.bean.MyUserPunchInSituationBean;
import java.com.lechuang.module.bean.NewsBean;
import java.com.lechuang.module.set.NewsActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SIGN_IN_RECORD)
public class SignInRecordActivity extends BaseActivity implements View.OnClickListener,OnRefreshLoadMoreListener {

    private TextView mTvIncome,mTvEarn,mTvToParticipate,mTvSuccessful;
    private RecyclerView mRvRecord;
    private SmartRefreshLayout mSmartNews;
    private LinearLayout mNetError;
    private int page = 1;//页数
    private boolean mload = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in_record;
    }

    @Override
    protected void findViews() {
        mRvRecord = $(R.id.rl_recyclerView);
        mSmartNews = $(R.id.smart_signin);
        mNetError = $(R.id.ll_net_error);
        mTvIncome = $(R.id.tv_income);
        mTvEarn = $(R.id.tv_earn);
        mTvToParticipate = $(R.id.tv_to_participate);
        mTvSuccessful = $(R.id.tv_successful);

        $(R.id.iv_common_back).setOnClickListener(this);
        ((TextView) $(R.id.iv_common_title)).setText("打卡记录");
//        mSmartNews.setOnRefreshLoadMoreListener(this);

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mSmartNews.setOnRefreshLoadMoreListener(this);

    }

    @Override
    protected void getData() {
        getUserPunchInSituation();
        getUserPunchInHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {//关闭
            finish();
        }
    }


    /**
     * 获取刷新数据
     */
    private void getUserPunchInHistory() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("page", page + "");
        NetWork.getInstance()
                .setTag(Qurl.userPunchInHistory)
                .getApiService(ModuleApi.class)
                .userPunchInHistory(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MySigninUserPunchInHistoryBean>(SignInRecordActivity.this, true, true) {

                    @Override
                    public void onSuccess(MySigninUserPunchInHistoryBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            if (mNewsListBeans == null || mNewsListBeans.size() <= 0 || mBaseQuickAdapter == null) {
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            if (mload) {
                                mRvRecord.setVisibility(View.GONE);
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            mload = true;
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        //收入 tv_income mTvIncome mTvEarn mTvToParticipate mTvSuccessful

                        //赚取 tv_earn
                        //参与 tv_to_participate
                        //成功 tv_successful

                        setNewsData(result.list);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (mNewsListBeans == null || mNewsListBeans.size() <= 0 || mBaseQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mNewsListBeans == null || mNewsListBeans.size() <= 0 || mBaseQuickAdapter == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    /**
     * 设置消息数据
     *
     * @param list
     */
    private List<MySigninUserPunchInHistoryBean.ListBean> mNewsListBeans;
    private BaseQuickAdapter<MySigninUserPunchInHistoryBean.ListBean, BaseViewHolder> mBaseQuickAdapter;

    private void setNewsData(List<MySigninUserPunchInHistoryBean.ListBean> list) {
        if (mNewsListBeans == null) {
            mNewsListBeans = new ArrayList<>();
        }
        if (page == 1) {
            mNewsListBeans.clear();
        }
        mRvRecord.setVisibility(View.VISIBLE);
        mNetError.setVisibility(View.INVISIBLE);

        mNewsListBeans.addAll(list);
        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<MySigninUserPunchInHistoryBean.ListBean, BaseViewHolder>(R.layout.item_signinrecord_item, mNewsListBeans) {
                @Override
                protected void convert(BaseViewHolder helper, MySigninUserPunchInHistoryBean.ListBean item) {
                    try {
                        //日期
                        helper.setText(R.id.tv_signin_date, item.activityTime);
                        //投入
                        helper.setText(R.id.tv_signin_input, item.payment+"");
                        //状态
                        helper.setText(R.id.tv_signin_state, item.statusStr);
                    } catch (Exception e) {

                    }
                }
            };
            mRvRecord.setHasFixedSize(true);
            mRvRecord.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            mRvRecord.setLayoutManager(gridLayoutManager);
            mRvRecord.setAdapter(mBaseQuickAdapter);
            /*mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ARouter.getInstance().build(ARouters.PATH_NEWS_CONTENT)
                            .withString("title", mNewsListBeans.get(position).title)
                            .withString("timeStr", mNewsListBeans.get(position).createTimeStr)
                            .withString("content", mNewsListBeans.get(position).content)
                            .navigation();
                }
            });*/
        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }
    }

    private void getUserPunchInSituation() {

//        Map<String, String> allParam = new HashMap<>();
//        allParam.put("alipayNumber",alipayNumber);
//        allParam.put("alipayRealName",alipayRealName);

        NetWork.getInstance()
                .setTag(Qurl.userPunchInSituation)
                .getApiService(ModuleApi.class)
                .userPunchInSituation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyUserPunchInSituationBean>(SignInRecordActivity.this) {

                    @Override
                    public void onSuccess(MyUserPunchInSituationBean result) {
                        if (TextUtils.isEmpty(result.income+"")) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //mTvIncome,mTvEarn,mTvToParticipate,mTvSuccessful
                        //提示语
                        mTvEarn.setText( result.income+"" );
                        mTvToParticipate.setText( result.joinDays+"" );
                        mTvIncome.setText( result.payment+"" );
                        mTvSuccessful.setText( result.punchInDays+"" );
//                        toast(result);

                        /*if (result.indexOf(R.string.s_set_succes) != 1) {
//                            UserHelper.getInstence ().getUserInfo ().setZhifubaoNum ( alipayNumber );
                            finish();
                        }*/
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
                });
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        page++;
        getUserPunchInHistory();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mload = true;
        mSmartNews.setNoMoreData(false);
        getUserPunchInHistory();
        getUserPunchInSituation();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartNews.finishRefresh(state);
        } else if (noMoreData) {
            mSmartNews.finishLoadMoreWithNoMoreData();
        } else {
            mSmartNews.finishLoadMore(state);
        }
    }
}
