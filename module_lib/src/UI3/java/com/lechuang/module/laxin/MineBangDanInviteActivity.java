package java.com.lechuang.module.laxin;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseOtherActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.InviteUserListBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = ARouters.PATH_MINE_BANG_DAN_INVITE)
public class MineBangDanInviteActivity extends BaseOtherActivity implements View.OnClickListener, OnLoadMoreListener {

    private int page = 1;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRvMineBandDingInvateNum;


    @Autowired
    public String startTime;
    @Autowired
    public String endTime;
    @Autowired
    public String numPersion;
    private FrameLayout mFlMineBangDanInvate;
    private BaseQuickAdapter mBaseQuickAdapter;
    private TextView mTvMineBangDinInvateNum;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_popu_mine_bangdan_invate_num;
    }

    @Override
    protected void findViews() {
        mSmartRefreshLayout = $(R.id.smar_popu_mine_bangdan);
        mRvMineBandDingInvateNum = $(R.id.rv_mine_bangding_invate_num);
        mFlMineBangDanInvate = $(R.id.fl_popu_mine_bangdan_invate);
        mTvMineBangDinInvateNum = $(R.id.tv_mine_bangding_invate_num);
        $(R.id.iv_layout_popu_banddan_close).setOnClickListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        mSmartRefreshLayout.setEnableRefresh(false);

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mTvMineBangDinInvateNum.setText("已邀请人数" + numPersion + "人");
    }

    @Override
    protected void getData() {
        mSmartRefreshLayout.autoLoadMore();
    }


    private void getYaoQingInfo(){
        ApiCancleManager.getInstance().removeAll();
        final Map<String, Object> allParam = new HashMap<>();

        allParam.put("startTime", startTime);
        allParam.put("endTime", endTime);
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag(Qurl.inviteUserList)
                .getApiService(ModuleApi.class)
                .inviteUserList(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<InviteUserListBean>(this, true, true) {
                    @Override
                    public void onSuccess(InviteUserListBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            if (page > 1){
                                page --;
                            }
                            setRefreshLoadMoreState(true,true);
                            return;
                        }
                        mFlMineBangDanInvate.setVisibility(View.VISIBLE);
                        setAdpager(result.list);
                        setRefreshLoadMoreState(true,false);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (page > 1){
                            page --;
                        }
                        setRefreshLoadMoreState(false,false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (page > 1){
                            page --;
                        }
                        setRefreshLoadMoreState(false,false);
                    }
                });
    }

    private List<InviteUserListBean.ListBean> mListBeans;
    private void setAdpager(List<InviteUserListBean.ListBean> list) {

        if (mListBeans == null){
            mListBeans = new ArrayList<>();
        }
        mListBeans.addAll(list);

        if (mBaseQuickAdapter == null){
            mBaseQuickAdapter = new BaseQuickAdapter<InviteUserListBean.ListBean, BaseViewHolder>(R.layout.layout_item_popu_mine_bangdan, mListBeans) {
                @Override
                protected void convert(BaseViewHolder helper, InviteUserListBean.ListBean item) {
                    helper.setText(R.id.tv_item_popu_mine_bangdan,item.registerTime);
                    helper.setText(R.id.tv_item_popu_mine_bangdan_phone,item.phone.substring(0, 3) + "****" + item.phone.substring(7, 11));
                }
            };
            mRvMineBandDingInvateNum.setHasFixedSize(true);
            mRvMineBandDingInvateNum.setNestedScrollingEnabled(false);
            mRvMineBandDingInvateNum.setLayoutManager(new GridLayoutManager(this, 1));
            mRvMineBandDingInvateNum.setAdapter(mBaseQuickAdapter);
        }else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_layout_popu_banddan_close){
            finish();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        getYaoQingInfo();
        page ++;
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {
        if ((page == 1 && noMoreData) || (page ==1 && !state)){
            finish();
        }else if (noMoreData) {
            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadMore(state);
        }

    }
}
