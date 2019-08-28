package java.com.lechuang.module.mytry;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CountDownTextView;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.commonsdk.debug.E;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.ShowInMyBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Route(path = ARouters.PATH_Mine_TRY_CONTENT)
public class TryContentFragment extends LazyBaseFragment implements View.OnClickListener, OnRefreshLoadMoreListener {


    private SmartRefreshLayout mSmartTryContent;
    private RecyclerView mRvTryContent;
    private int page = 1;
    private int mObtype;
    private LinearLayout mLlNetError;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_try_content;
    }

    @Override
    protected void findViews() {
        mSmartTryContent = $(R.id.smart_try_content);
        mRvTryContent = $(R.id.rv_try_content);

        mLlNetError = $(R.id.ll_net_error);
        $(R.id.tv_common_to_tryuser).setOnClickListener(this);
        mSmartTryContent.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initView() {
        mObtype = getArguments().getInt("obtype");
    }

    @Override
    protected void getData() {
        mSmartTryContent.autoRefresh(100);
    }

    private void getProduct() {

        ApiCancleManager.getInstance().removeAll();
        Map<String, Object> allParam = new HashMap<>();

        allParam.put("page", page);
        allParam.put("obtype", mObtype);

        NetWork.getInstance()
                .setTag(Qurl.showIngMy)
                .getApiService(ModuleApi.class)
                .showIngMy(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShowInMyBean>(getActivity(), false, false) {
                    @Override
                    public void onSuccess(ShowInMyBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            setRefreshLoadMoreState(true, true);
                            if (page == 1){
                                mLlNetError.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        mLlNetError.setVisibility(View.GONE);
                        setRefreshLoadMoreState(true, false);
                        setProductAdapter(result.list);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                        if (page == 1){
                            mLlNetError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        if (page == 1){
                            mLlNetError.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private ArrayList<ShowInMyBean.ListBean> mListProcuct = new ArrayList<>();
    private BaseQuickAdapter<ShowInMyBean.ListBean, BaseViewHolder> mBaseQuickAdapter;

    private void setProductAdapter(ArrayList<ShowInMyBean.ListBean> list) {

        if (page == 1) {
            mListProcuct.clear();
        }
        mListProcuct.addAll(list);
        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<ShowInMyBean.ListBean, BaseViewHolder>(R.layout.layout_item_try_content, mListProcuct) {
                @Override
                protected void convert(BaseViewHolder helper, ShowInMyBean.ListBean item) {

                    try {
                        //商品图片
                        ImageView ivItemTryContent = helper.getView(R.id.iv_item_try_content);
                        Glide.with(BaseApplication.getApplication()).load(item.showImgList.get(0)).placeholder(R.drawable.bg_common_img_null).into(ivItemTryContent);

                        //设置商品标题
                        helper.setText(R.id.tv_item_try_content_name, item.name);
                        //进度条
                        NumSeekBar numSeekItemTryContent = helper.getView(R.id.numSeek_item_try_content);
                        numSeekItemTryContent.setProgress(divide(item.realNum, item.needNum));
                        numSeekItemTryContent.setSeekBarStyle(R.drawable.pro_seekbar_12);
                        if (item.obtype == 4 || item.obtype == 5){
                            numSeekItemTryContent.setVisibility(View.GONE);
                        }else {
                            numSeekItemTryContent.setVisibility(View.VISIBLE);
                        }

                        //开奖时间
                        TextView tvItemTryContentStartReward = helper.getView(R.id.tv_item_try_content_start_reward);
                        //需要多少人数
                        TextView tvItemTryContentNeedPersion = helper.getView(R.id.tv_item_try_content_need_persion);
                        //已中奖
                        TextView tvItemTryContentRewardSelect = helper.getView(R.id.tv_item_try_content_reward_select);
                        //剩余多少时间
                        CountDownTextView tvItemTryContentRestTime = helper.getView(R.id.tv_item_try_content_rest_time);
                        //预计开奖时间
                        TextView tvItemTryContentWillStartReward = helper.getView(R.id.tv_item_try_content_will_start_reward);
                        TextView tvItemTryContentWillStartRewardTime = helper.getView(R.id.tv_item_try_content_will_start_reward_time);
                        //查看详情
                        TextView tvItemTryContentRewardInfo = helper.getView(R.id.tv_item_try_content_reward_info_or_result);
                        helper.addOnClickListener(R.id.tv_item_try_content_reward_info_or_result);
                        //抽奖状态
                        TextView tvItemTryContentJionState = helper.getView(R.id.tv_item_try_content_join_state);
                        //试用码
                        TextView tvItemTryContentTryCode = helper.getView(R.id.tv_item_try_content_trycode);
                        helper.addOnClickListener(R.id.tv_item_try_content_trycode);
                        //需要人数
                        if (item.obtype == 4 || item.obtype == 5) {
                            tvItemTryContentNeedPersion.setVisibility(View.GONE);
                            tvItemTryContentNeedPersion.setText("");
                        } else {
                            tvItemTryContentNeedPersion.setVisibility(View.VISIBLE);
                            tvItemTryContentNeedPersion.setText("需" + item.needNum + "人次");
                        }

                        //开奖时间
                        if (!TextUtils.isEmpty(item.realEndTime) && (item.obtype == 4 || item.obtype == 5)) {
                            tvItemTryContentStartReward.setVisibility(View.VISIBLE);
                            tvItemTryContentStartReward.setText("开奖时间 " + item.realEndTime + "");
                        } else {
                            tvItemTryContentStartReward.setVisibility(View.GONE);
                            tvItemTryContentStartReward.setText("");
                        }


                        //已中奖
                        if (item.obtype == 5) {
                            tvItemTryContentRewardSelect.setText("已中奖");
                            tvItemTryContentRewardSelect.setVisibility(View.VISIBLE);
                        } else if (item.obtype == 4) {
                            tvItemTryContentRewardSelect.setText("未中奖");
                            tvItemTryContentRewardSelect.setVisibility(View.VISIBLE);
                        } else {
                            tvItemTryContentRewardSelect.setText("");
                            tvItemTryContentRewardSelect.setVisibility(View.GONE);
                        }

                        //剩余多少时间 全部和参与中显示，其他不显示
                        if (item.obtype == 2 && item.countDown > 0) {
                            tvItemTryContentRestTime.setVisibility(View.VISIBLE);//显示剩余时间   todo
                            tvItemTryContentRestTime.setNormalText( "仅剩 23:59:59" ).setShowFormatTime( true ).setCountDownText( "仅剩 ","" )
                                    .setCloseKeepCountDown( false ).setShowFormatTimeOther(3).setOnCountDownFinishListener( new CountDownTextView.OnCountDownFinishListener() {
                                @Override
                                public void onFinish() {
//                                countDownTextView.setText( "过期了" );
                                }
                            } );
                            tvItemTryContentRestTime.setVisibility( View.VISIBLE );
                            tvItemTryContentRestTime.startCountDown( item.countDown /1000 );
                        } else {
                            tvItemTryContentRestTime.setVisibility(View.GONE);
                            tvItemTryContentRestTime.setText("");
                        }

                        //预计开奖时间，待开奖显示
                        if (item.obtype == 3 && !TextUtils.isEmpty(item.preWinTime)) {
                            tvItemTryContentWillStartReward.setVisibility(View.VISIBLE);
                            tvItemTryContentWillStartRewardTime.setVisibility(View.VISIBLE);
                            tvItemTryContentWillStartRewardTime.setText(item.preWinTime);
                        } else {
                            tvItemTryContentWillStartReward.setVisibility(View.GONE);
                            tvItemTryContentWillStartRewardTime.setVisibility(View.GONE);
                            tvItemTryContentWillStartReward.setText("");
                            tvItemTryContentWillStartRewardTime.setText("");
                        }

                        /*//obyType = 2，3为查看详情；obyType = 4，5中奖结果
                        if (item.obtype == 2 || item.obtype == 3) {
                            tvItemTryContentRewardInfo.setVisibility(View.VISIBLE);
                            tvItemTryContentRewardInfo.setText("查看详情");
                        } else if (item.obtype == 4 || item.obtype == 5) {
                            tvItemTryContentRewardInfo.setVisibility(View.VISIBLE);
                            tvItemTryContentRewardInfo.setText("中奖结果");
                        } else {
                            tvItemTryContentRewardInfo.setVisibility(View.GONE);
                            tvItemTryContentRewardInfo.setText("");
                        }*/

                        if (item.obtype == 2) {
                            tvItemTryContentJionState.setText("参与成功,待开奖");
                            tvItemTryContentJionState.setVisibility(View.VISIBLE);
                        } else if (item.obtype == 3) {
                            tvItemTryContentJionState.setText("已结束,待开奖");
                            tvItemTryContentJionState.setVisibility(View.VISIBLE);
                        } else if (item.obtype == 4) {
                            tvItemTryContentJionState.setText("很遗憾,未中奖");
                            tvItemTryContentJionState.setVisibility(View.VISIBLE);
                        } else if (item.obtype == 5) {
                            tvItemTryContentJionState.setText("恭喜您,已中奖,中奖码为" + (TextUtils.isEmpty(item.winNum) ? "" : item.winNum));
                            tvItemTryContentJionState.setVisibility(View.VISIBLE);
                        } else {
                            tvItemTryContentJionState.setText("");
                            tvItemTryContentJionState.setVisibility(View.GONE);
                        }

                        //试用码
                        tvItemTryContentTryCode.setText("试用码:" + item.sum + "个 > ");


                    } catch (Exception e) {

                    }
                }
            };
            mRvTryContent.setHasFixedSize(true);
            mRvTryContent.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            mRvTryContent.setLayoutManager(gridLayoutManager);
            mRvTryContent.setAdapter(mBaseQuickAdapter);

            mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                }
            });

            //设置内部item的点击事件
            mBaseQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    try {
                        int id = view.getId();
                        if (id == R.id.tv_item_try_content_trycode) {//试用码
                            ShowInMyBean.ListBean listBean = mListProcuct.get(position);
                            String keyNum = listBean.keyNumStr;
                            String[] split = keyNum.split(",");
                            ArrayList<String> stringList = new ArrayList<>();
                            for (int i = 0; i < split.length; i++) {
                                stringList.add(split[i]);
                            }

                            showTryCodePopuwind(stringList);
                        } else if (id == R.id.tv_item_try_content_reward_info_or_result) {//查看详情 和 中奖结果
                            ShowInMyBean.ListBean listBean = (ShowInMyBean.ListBean) adapter.getData().get(position);
                            int obtype = listBean.obtype;
                            int idProduct = listBean.id;
                            String winNum = listBean.winNum;
                            //PATH_TRY_DETAILS
                            ARouter.getInstance().build(ARouters.PATH_TRY_DETAILS)
                                    .withInt("obtype",obtype)
                                    .withInt("id",idProduct)
                                    .withString("winNum",winNum)
                                    .navigation();

                        }
                    }catch (Exception e){

                    }
                }
            });
        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }


    }

    /**
     * @param x
     * @param y
     * @return
     */
    private int divide(int x, int y) {
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String result = df.format((float) x / y);//返回的是String类型
        Float f = Float.valueOf(result);

        int progress;
        if (f >= 1f) {
            progress = 100;
        } else {
            progress = (int) (f * 100);
        }
        return progress;
    }


    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartTryContent.finishRefresh(state);
            if (!state || noMoreData) {
                //第一次加载失败时，再次显示时可以重新加载;page=1，第一次加载没数据时也再次从新显示
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartTryContent.finishLoadMoreWithNoMoreData();
        } else {
            mSmartTryContent.finishLoadMore(state);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_common_to_tryuser) {//立即前往试用
            getActivity().finish();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

        if (mBaseQuickAdapter == null) {
            page = 1;
            mSmartTryContent.finishLoadMore(1000);
        } else {
            page++;
        }
        getProduct();

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        getProduct();
    }

    private PopupWindow mPopupWindow;
    private ArrayList<String> mListString = new ArrayList<>();
    private String mNumTryCode = "0";

    /**
     * 弹出试用码弹框
     *
     * @param showImgList
     */
    private void showTryCodePopuwind(ArrayList<String> showImgList) {
        if (showImgList == null || showImgList.size() < 1) {
            return;
        }
        if (mListString == null) {
            mListString = new ArrayList<>();
        }
        mListString.clear();
        mListString.addAll(showImgList);
        mNumTryCode = showImgList.size() + "";

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popu_mine_try_code, null);

        TextView tvMineTryCode = contentView.findViewById(R.id.tv_mine_try_code);
        SpannableString ss1 = new SpannableString("我的试用码(" + mNumTryCode + "个)");
        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_CFCFCF)), 5, mNumTryCode.length() + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMineTryCode.setText(ss1);
        RecyclerView rvMineTryCode = contentView.findViewById(R.id.rv_mine_try_code);
        rvMineTryCode.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvMineTryCode.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_item_mine_try_code, mListString) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.iv_item_try_content, item);
            }
        });
        contentView.findViewById(R.id.iv_mine_try_code_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }
}
