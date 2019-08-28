package java.com.lechuang.module.set;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
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
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.NewsBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_NEWS)
public class NewsActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {

    @Autowired
    public boolean mSetDefState;
    private TextView mTvNewsShouYi,mTvNewsOther,mTvNewsShouYiLine,mTvNewsOtherLine;
    private RecyclerView mRvNews;
    private SmartRefreshLayout mSmartNews;
    private LinearLayout mNetError;
    private int page = 1;//页数
    private String type = "1";
    private boolean mload = true;
    private boolean mNeedNotifyList = false;//推送消息重新进入刷新

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news;
    }

    @Override
    protected void findViews() {

        mTvNewsShouYi = $(R.id.tv_news_shouyi);
        mTvNewsOther = $(R.id.tv_news_other);
        mTvNewsShouYiLine = $(R.id.tv_news_shouyi_line);
        mTvNewsOtherLine = $(R.id.tv_news_other_line);
        mRvNews = $(R.id.rv_news);
        mSmartNews = $(R.id.smart_news);
        mNetError = $(R.id.ll_net_error);

        mTvNewsShouYi.setOnClickListener(this);
        mTvNewsOther.setOnClickListener(this);

        $(R.id.iv_common_back).setOnClickListener(this);
        ((TextView) $(R.id.iv_common_title)).setText("消息");

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);


        mTvNewsShouYi.setSelected(!mSetDefState);
        mTvNewsOther.setSelected(mSetDefState);
        mTvNewsShouYiLine.setSelected(!mSetDefState);
        mTvNewsOtherLine.setSelected(mSetDefState);
        type = mSetDefState ? "2" : "1";
        changeTabStyle(!mSetDefState);
        mSmartNews.setOnRefreshLoadMoreListener(this);

    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取焦点是自动刷新数据
        mSmartNews.autoRefresh(500);
    }


    private boolean mOldSelectIsSY = true;//用户判断当前选中是否是收益消息，避免重复请求和区别请求参数

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {//关闭
            finish();
        } else if (id == R.id.tv_news_shouyi) {//收益消息
            type = "1";
            page = 1;
            mSmartNews.setNoMoreData(false);
            changeTabStyle(true);
        } else if (id == R.id.tv_news_other) {//其他消息
            type = "2";
            page = 1;
            mSmartNews.setNoMoreData(false);
            changeTabStyle(false);
        }
    }

    private void changeTabStyle(boolean currentSelectIsSY) {

        if (mOldSelectIsSY != currentSelectIsSY) {

            mTvNewsShouYi.setSelected(currentSelectIsSY);
            mTvNewsOther.setSelected(!currentSelectIsSY);
            mTvNewsShouYiLine.setSelected(currentSelectIsSY);
            mTvNewsOtherLine.setSelected(!currentSelectIsSY);
            mOldSelectIsSY = currentSelectIsSY;
            //无延时刷新
//            mSmartNews.autoRefresh();
            getAllData();
        }
    }

    /**
     * 获取刷新数据
     */
    private void getAllData() {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("page", page + "");
        allParam.put("type", type);
        NetWork.getInstance()
                .setTag(Qurl.allNews)
                .getApiService(ModuleApi.class)
                .allNews(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<NewsBean>(NewsActivity.this, true, true) {

                    @Override
                    public void onSuccess(NewsBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            if (mNewsListBeans == null || mNewsListBeans.size() <= 0 || mBaseQuickAdapter == null) {
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            if (mload) {
                                mRvNews.setVisibility(View.GONE);
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            mload = true;
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        String phone = UserHelper.getInstence().getUserInfo().getPhone();
                        UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
                        UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
                        if (unique != null) {
                            unique.setMsgState(false);
                            unique.setMsgSize("0");
                            userInfoDao.update(unique);
                        }
                        setRefreshLoadMoreState(true, false);
                        if (page == 1){
                            mNeedNotifyList = true;
                        }
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
    private List<NewsBean.NewsListBean> mNewsListBeans;
    private BaseQuickAdapter<NewsBean.NewsListBean, BaseViewHolder> mBaseQuickAdapter;

    private void setNewsData(List<NewsBean.NewsListBean> list) {
        if (mNewsListBeans == null) {
            mNewsListBeans = new ArrayList<>();
        }
        if (page == 1) {
            mNewsListBeans.clear();
        }
        mRvNews.setVisibility(View.VISIBLE);
        mNetError.setVisibility(View.INVISIBLE);

        mNewsListBeans.addAll(list);
        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<NewsBean.NewsListBean, BaseViewHolder>(R.layout.item_news_rv, mNewsListBeans) {
                @Override
                protected void convert(BaseViewHolder helper, NewsBean.NewsListBean item) {
                    try {
                        helper.setText(R.id.tv_news_title, item.title);
                        helper.setText(R.id.tv_news_time, item.createTimeStr);
                    } catch (Exception e) {

                    }
                }
            };
            mRvNews.setHasFixedSize(true);
            mRvNews.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            mRvNews.setLayoutManager(gridLayoutManager);
            mRvNews.setAdapter(mBaseQuickAdapter);
            mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ARouter.getInstance().build(ARouters.PATH_NEWS_CONTENT)
                            .withString("title", mNewsListBeans.get(position).title)
                            .withString("timeStr", mNewsListBeans.get(position).createTimeStr)
                            .withString("content", mNewsListBeans.get(position).content)
                            .navigation();
                }
            });
        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }
        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvNews.scrollToPosition(0);
        }


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
        mload = true;
        mSmartNews.setNoMoreData(false);
        getAllData();
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
