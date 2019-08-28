package java.com.lechuang.module.laxin;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseOtherActivity;
import com.common.app.base.bean.BaseItemEntity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.OnClickEvent;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.InviteUserListBean;
import java.com.lechuang.module.bean.MineBangDanBean;
import java.com.lechuang.module.bean.MineBangDanEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = ARouters.PATH_MINE_BANG_DAN)
public class MineBangDanActivity extends BaseOtherActivity implements View.OnClickListener, OnRefreshLoadMoreListener {

    private RecyclerView mRvMineBangDan;
    private TextView mTvCommonTitle,mTvCommonClickTry;
    private BaseAdapter<BaseItemEntity, BaseViewHolder> mBaseAdapter;
    private int page = 1;
    private SmartRefreshLayout mSmartMineBangDan;
    private View mLlNetError;
    private ImageView mIvCommonImage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_bang_dan;
    }

    @Override
    protected void findViews() {

        mRvMineBangDan = $(R.id.rv_mine_bangdan);
        mTvCommonTitle = $(R.id.iv_common_title);
        mSmartMineBangDan = $(R.id.smart_mine_bangdan);
        mLlNetError = $(R.id.ll_net_error);
        mIvCommonImage = $(R.id.iv_common_image);
        mTvCommonClickTry = $(R.id.tv_common_click_try);
        mTvCommonClickTry.setText("");
        mIvCommonImage.setImageResource(R.drawable.ic_mine_bangdan_error);
        $(R.id.iv_common_back).setOnClickListener(this);
        mTvCommonTitle.setText("我的榜单");
        mSmartMineBangDan.setOnRefreshLoadMoreListener(this);
        mSmartMineBangDan.setEnableLoadMoreWhenContentNotFull(false);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void getData() {
        mSmartMineBangDan.autoRefresh(100);
    }

    private void getMineBangDanData(){
         Map<String, Object> allParam = new HashMap<>();
        allParam.put("num", "1");
        allParam.put("page", page + "");

        NetWork.getInstance()
                .setTag(Qurl.userRankingList)
                .getApiService(ModuleApi.class)
                .userRankingList(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MineBangDanBean>(this, false, false) {
                    @Override
                    public void onSuccess(MineBangDanBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            setRefreshLoadMoreState(true, true);
                            page --;
                            if (mBangDanEntityList == null || mBangDanEntityList.size() < 0){
                                mLlNetError.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        mLlNetError.setVisibility(View.INVISIBLE);
                        setAdapter(result.list);
                        setRefreshLoadMoreState(true, false);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                        page --;
                        if (mBangDanEntityList == null || mBangDanEntityList.size() < 0){
                            mLlNetError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        page --;
                        if (mBangDanEntityList == null || mBangDanEntityList.size() < 0){
                            mLlNetError.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }



    private ArrayList<BaseItemEntity> mBangDanEntityList;
    private void setAdapter(List<MineBangDanBean.ListBean> list){
        if (mBangDanEntityList == null){
            mBangDanEntityList = new ArrayList<>();
        }
        if (page == 1){
            mBangDanEntityList.clear();
        }

            for (int i = 0;i < list.size(); i++){
                MineBangDanBean.ListBean listBean = list.get(i);
                MineBangDanEntity mineBangDanEntity = new MineBangDanEntity(BaseItemEntity.TYPE_DEFITEM);
                mineBangDanEntity.followerCount = listBean.followerCount;
                mineBangDanEntity.position = listBean.position;
                mineBangDanEntity.positionStr = listBean.positionStr;
                mineBangDanEntity.status = listBean.status;
                mineBangDanEntity.isAward = listBean.isAward;
                mineBangDanEntity.startTime = listBean.startTime;
                mineBangDanEntity.endTime = listBean.endTime;
                mineBangDanEntity.caption = listBean.caption;
                mineBangDanEntity.id = listBean.id;

                mBangDanEntityList.add(mineBangDanEntity);
            }


            if (mBaseAdapter == null){
                mBaseAdapter = new BaseAdapter<BaseItemEntity, BaseViewHolder>(mBangDanEntityList){

                    @Override
                    protected void convert(BaseViewHolder helper, BaseItemEntity item1) {
                        int itemType = item1.getItemType();
                        if (itemType == BaseItemEntity.TYPE_DEFITEM){
                            try{
                                final MineBangDanEntity item = (MineBangDanEntity) item1;
                                helper.setText(R.id.tv_item_mine_dang_dan_paiming,"排名"+item.positionStr);
                                helper.setText(R.id.tv_item_mine_dang_dan_time,item.startTime +"~" + item.endTime
                                        + (TextUtils.isEmpty(item.caption) ? "" : ("(" + item.caption + ")")));
                                TextView dangdanNum=helper.getView( R.id.tv_item_mine_dang_dan_num );
                                dangdanNum.setOnClickListener( new OnClickEvent() {
                                    @Override
                                    public void singleClick(View v) {
                                        ARouter.getInstance()
                                                .build(ARouters.PATH_MINE_BANG_DAN_INVITE)
                                                .withTransition(R.anim.mine_bang_dan_into,R.anim.mine_bang_dan_out)
                                                .withString("startTime",item.startTime)
                                                .withString("endTime",item.endTime)
                                                .withString("numPersion",item.followerCount + "")
                                                .navigation(MineBangDanActivity.this);
                                    }
                                } );
                                dangdanNum.setText( "邀请" + item.followerCount + "人 >" );
//                                helper.setText(R.id.tv_item_mine_dang_dan_num,"邀请" + item.followerCount + "人 >");

                                TextView tvItemMineBandDanJiangLiState = helper.getView(R.id.tv_item_mine_dang_dan_jiangli_state);

                                if (item.isAward == 0){//待领取
                                    tvItemMineBandDanJiangLiState.setVisibility(View.VISIBLE);
                                    tvItemMineBandDanJiangLiState.setText("待领取");
                                    tvItemMineBandDanJiangLiState.setBackgroundResource(R.drawable.bg_mine_bang_dan_dailingqu);
                                }else if (item.isAward == 1){//已领取
                                    tvItemMineBandDanJiangLiState.setVisibility(View.VISIBLE);
                                    tvItemMineBandDanJiangLiState.setText("已获得奖励");
                                    tvItemMineBandDanJiangLiState.setBackgroundResource(R.drawable.bg_mine_bang_dan_state_huode);
                                } else if (item.isAward == 2){//奖励失效
                                    tvItemMineBandDanJiangLiState.setVisibility(View.VISIBLE);
                                    tvItemMineBandDanJiangLiState.setText("奖励失效");
                                    tvItemMineBandDanJiangLiState.setBackgroundResource(R.drawable.bg_mine_bang_dan_state_shixiao);
                                }else {
                                    tvItemMineBandDanJiangLiState.setVisibility(View.INVISIBLE);
                                }

                            }catch (Exception e){

                            }
                        }


                    }

                    @Override
                    protected void addItemTypeView() {
                        addItemType(BaseItemEntity.TYPE_DEFITEM,R.layout.item_mine_bang_dan);
                    }

                };
                mRvMineBangDan.setHasFixedSize(true);
                mRvMineBangDan.setNestedScrollingEnabled(false);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
                mRvMineBangDan.setLayoutManager(gridLayoutManager);
                mRvMineBangDan.setAdapter(mBaseAdapter);
                mBaseAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        int id = view.getId();
                    }
                });
                mBaseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                        MineBangDanEntity mineBangDanEntity = (MineBangDanEntity) adapter.getData().get(position);
//                        ARouter.getInstance()
//                                .build(ARouters.PATH_MINE_BANG_DAN_INVITE)
//                                .withTransition(R.anim.mine_bang_dan_into,R.anim.mine_bang_dan_out)
//                                .withString("startTime",mineBangDanEntity.startTime)
//                                .withString("endTime",mineBangDanEntity.endTime)
//                                .withString("numPersion",mineBangDanEntity.followerCount + "")
//                                .navigation(MineBangDanActivity.this);
                    }
                });
            }else {
                mBaseAdapter.notifyDataSetChanged();
            }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
            finish();
        }
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            if (noMoreData){
                mSmartMineBangDan.finishLoadMoreWithNoMoreData();
            }
            mSmartMineBangDan.finishRefresh(state);
        } else if (noMoreData) {
            mSmartMineBangDan.finishLoadMoreWithNoMoreData();
        } else {
            mSmartMineBangDan.finishLoadMore(state);
        }

    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (mBaseAdapter == null) {
            page = 1;
            mSmartMineBangDan.finishLoadMore(100);
        } else {
            page++;
        }
        getMineBangDanData();

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        getMineBangDanData();
    }

}
