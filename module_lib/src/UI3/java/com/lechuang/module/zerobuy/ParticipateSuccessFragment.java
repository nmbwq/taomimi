package java.com.lechuang.module.zerobuy;

import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.base.bean.BaseItemEntity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.DeviceUtils;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.JoinSuccessBean;
import java.com.lechuang.module.bean.JoinSuccessProductEntity;
import java.com.lechuang.module.bean.JoinSuccessTryCodeEntity;
import java.com.lechuang.module.bean.ZeroBuyJoinSuccessBean;
import java.com.lechuang.module.bean.ZeroBuyJoinSuccessProductEntity;
import java.com.lechuang.module.bean.ZeroBuyJoinSuccessTryCodeEntity;
import java.com.lechuang.module.mytry.MyTryActivity;
import java.com.lechuang.module.zerobuy.bean.MyZeroBuyEntity;
import java.com.lechuang.module.zerobuy.bean.MyZeroBuyOtherEntity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_PARTICIPATE_SUCCESS_F)
public class ParticipateSuccessFragment extends LazyBaseFragment implements View.OnClickListener, OnRefreshLoadMoreListener {

    private TextView mTvCommonTitle;
    private SmartRefreshLayout mSmartJoinSuccess;
    private RecyclerView mRvJoinSuccess;
    private int page = 1;
    private LinearLayout mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mIvPopFinish;
    @Autowired
    public int type;//1为更多，0为参与
    private String id;//活动id

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_join_success;
    }

    @Override
    protected void findViews() {
        $(R.id.iv_common_back).setOnClickListener(this);
        mTvCommonTitle = $(R.id.iv_common_title);
        mSmartJoinSuccess = $(R.id.smart_join_success);
        mTvCommonTitle.setTextColor(getResources().getColor(R.color.c_161616));

        mRvJoinSuccess = $(R.id.rv_join_success);
        mSmartJoinSuccess.setEnableRefresh(false);
        mSmartJoinSuccess.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void getData() {
        //获取传值，设置数据
        ZeroBuyJoinSuccessBean joinSuccessBean = (ZeroBuyJoinSuccessBean) getArguments().getSerializable("joinSuccess");
        type=getArguments().getInt( "type" );
        id=getArguments().getString( "id" );
        if (type==0){
            mTvCommonTitle.setText("参与成功");
        }else {
            mTvCommonTitle.setText("兑换成功");
        }
//        获取页面传值，直接设置数据
        setProductAdapter(joinSuccessBean);
    }

    private void getProduct(final String id) {

        ApiCancleManager.getInstance().removeAll();
        final Map<String, Object> allParam = new HashMap<>();

        allParam.put("num", "1");
        allParam.put("page", page + "");
        allParam.put("type", "0");//参与活动
        if (!TextUtils.isEmpty(id)) {
            allParam.put("id", id);
        }

        NetWork.getInstance()
                .setTag(Qurl.zeroBuyJoinSuccess)
                .getApiService(ModuleApi.class)
                .zeroBuyJoinSuccess(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZeroBuyJoinSuccessBean>(getActivity(), false, false) {
                    @Override
                    public void onSuccess(ZeroBuyJoinSuccessBean result) {
                        if (result == null) {
                            page --;
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        if (!TextUtils.isEmpty(id)) {//id != null 是点击参与试用，需要跳转页面
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("joinSuccess", result);
//
//                            ARouter.getInstance().build(ARouters.PATH_JOIN_SUCCESS_F).with(bundle).navigation();
                             } else {//否则是加载更多，设置adapter数据
                            setProductAdapter(result);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        page --;
                        setRefreshLoadMoreState(false, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        page --;
                    }
                });

    }

    private ArrayList<BaseItemEntity> mListProcuctEntity = new ArrayList<>();
    private BaseAdapter<BaseItemEntity, BaseViewHolder> mBaseAdapter;

    private void setProductAdapter(ZeroBuyJoinSuccessBean result) {
        if (mListProcuctEntity == null) {
            mListProcuctEntity = new ArrayList<>();
        }

        if (page == 1) {
            mListProcuctEntity.clear();
        }
        //合成实体类  todo

        if (page == 1){
            ZeroBuyJoinSuccessTryCodeEntity joinSuccessTryCodeEntity = new ZeroBuyJoinSuccessTryCodeEntity(ZeroBuyJoinSuccessTryCodeEntity.TYPE_TRYCODE_STATE);
            joinSuccessTryCodeEntity.num = result.num;
            joinSuccessTryCodeEntity.uaCodeNum = result.uaCodeNum;
            joinSuccessTryCodeEntity.code = result.code;
            joinSuccessTryCodeEntity.keyNumSt = result.keyNumSt;
            mListProcuctEntity.add(joinSuccessTryCodeEntity);
        }

        if (result.proList != null && result.proList.size() > 0) {
            for (int i = 0; i < result.proList.size(); i++) {
                ZeroBuyJoinSuccessProductEntity joinSuccessProductEntity = new ZeroBuyJoinSuccessProductEntity(ZeroBuyJoinSuccessProductEntity.TYPE_PRODUCT);
                joinSuccessProductEntity.mProListBean = result.proList.get(i);
                mListProcuctEntity.add(joinSuccessProductEntity);
            }
        }

        if (mBaseAdapter == null) {
            mBaseAdapter = new BaseAdapter<BaseItemEntity, BaseViewHolder>(mListProcuctEntity) {
                @Override
                protected void addItemTypeView() {
                    addItemType(ZeroBuyJoinSuccessTryCodeEntity.TYPE_TRYCODE_STATE, R.layout.layout_item_zero_buy_join_success_trycode_state);
//                    addItemType(ZeroBuyJoinSuccessProductEntity.TYPE_PRODUCT, R.layout.layout_item_join_success_product);
                    addItemType(ZeroBuyJoinSuccessProductEntity.TYPE_PRODUCT, R.layout.item_my_zero_buy_list_two);
                }

                @Override
                protected void convert(BaseViewHolder helper, BaseItemEntity item) {
                    int itemType = item.getItemType();
                    if (itemType == ZeroBuyJoinSuccessTryCodeEntity.TYPE_TRYCODE_STATE) {
                        try {
                            ZeroBuyJoinSuccessTryCodeEntity joinSuccessTryCodeEntity = (ZeroBuyJoinSuccessTryCodeEntity) item;
                            helper.setText(R.id.tv_item_join_success_trycode_state_num, "恭喜您获得" + joinSuccessTryCodeEntity.num + "个抽奖码");
                            TextView trycode=helper.getView( R.id.tv_item_join_successt_current_trycode );
                            trycode.setText( "当前拥有抽奖码" + joinSuccessTryCodeEntity.uaCodeNum + "个 >");
                            if (type==0){
                                trycode.setVisibility( View.GONE );
                            }else {
                                trycode.setVisibility( View.VISIBLE );
                            }
                            LinearLayout llItemJoinSuccessTryCodeState = helper.getView(R.id.ll_item_join_success_trycode_state);
                            llItemJoinSuccessTryCodeState.removeAllViews();
                            View view = helper.getView(R.id.tv_item_join_success_more_trycode);
                            helper.addOnClickListener(R.id.tv_item_join_success_more_trycode);
                            helper.addOnClickListener(R.id.tv_item_join_successt_more_card);
                            helper.addOnClickListener(R.id.tv_item_join_successt_current_trycode);
                            if (!TextUtils.isEmpty(joinSuccessTryCodeEntity.code)) {
                                if (joinSuccessTryCodeEntity.code.contains(",")) {
                                    String[] split = joinSuccessTryCodeEntity.code.split(",");
                                    if (split.length > 5) {
                                        view.setVisibility(View.VISIBLE);
                                    } else {
                                        view.setVisibility(View.GONE);
                                    }
                                    for (int i = 0; i < (split.length > 5 ? 5 : split.length); i++) {
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                        TextView tv = new TextView(getActivity());
                                        tv.setText(split[i]);
                                        tv.setTextColor(getResources().getColor(R.color.c_FF4040));
                                        tv.setTextSize(DeviceUtils.px2dp(40));
                                        tv.setGravity(Gravity.CENTER);
                                        tv.setLayoutParams(params);
                                        llItemJoinSuccessTryCodeState.addView(tv);
                                    }
                                } else {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                    TextView tv = new TextView(getActivity());
//                                    SpannableString ss1 = new SpannableString(joinSuccessTryCodeEntity.code);
//                                    ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_2F2F2F)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                    ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_FF4040)), 4, joinSuccessTryCodeEntity.code.length() + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv.setText(joinSuccessTryCodeEntity.code);
                                    tv.setTextColor(getResources().getColor(R.color.c_FF4040));
                                    tv.setTextSize(DeviceUtils.px2dp(40));
                                    tv.setGravity(Gravity.CENTER);
                                    tv.setLayoutParams(params);
                                    llItemJoinSuccessTryCodeState.addView(tv);
                                }
                            }
                        } catch (Exception e) {

                        }
                    } else if (itemType == ZeroBuyJoinSuccessProductEntity.TYPE_PRODUCT) {

                        try {
                            helper.addOnClickListener(R.id.tv_item_join_success_product_reward_info);
                            ZeroBuyJoinSuccessProductEntity joinSuccessProductEntity = (ZeroBuyJoinSuccessProductEntity) item;
                            //商品图片
                            ImageView ivItemJoinSuccess = helper.getView(R.id.iv_item_all_product_tupian);
                            Glide.with(BaseApplication.getApplication()).load(joinSuccessProductEntity.mProListBean.showImgList.get(0)).placeholder(R.drawable.bg_common_img_null).into(ivItemJoinSuccess);

                            //设置商品标题
                            helper.setText(R.id.tv_item_all_product_biaoti, joinSuccessProductEntity.mProListBean.name);
                            //进度条
                            NumSeekBar numSeekItemJoinSuccess = helper.getView(R.id.numSeekBar);
                            int num=joinSuccessProductEntity.mProListBean.realNum*100/joinSuccessProductEntity.mProListBean.needNum;
                            numSeekItemJoinSuccess.setProgress( num );
//                            numSeekItemJoinSuccess.setProgress(divide(joinSuccessProductEntity.mProListBean.realNum, joinSuccessProductEntity.mProListBean.needNum));
                            //需要多少人数
                            TextView tvItemJoinSuccessNeedPersion = helper.getView(R.id.item_mytry_person);
                            //剩余多少时间
                            CountDownTextView tvItemJoinSuccessRestTime = helper.getView(R.id.tv_card_time);
                            //参与试用
                            helper.addOnClickListener(R.id.tv_item_canjia);

                            tvItemJoinSuccessNeedPersion.setText("需" + joinSuccessProductEntity.mProListBean.needNum + "人次");
                            tvItemJoinSuccessRestTime.setText(joinSuccessProductEntity.mProListBean.countDown + "");

                            tvItemJoinSuccessRestTime.setNormalText( "仅剩 23:59:59" ).setShowFormatTime( true ).setCountDownText( "仅剩","" )
                                    .setCloseKeepCountDown( false ).setShowFormatTimeOther(3).setOnCountDownFinishListener( new CountDownTextView.OnCountDownFinishListener() {
                                @Override
                                public void onFinish() {
//                                countDownTextView.setText( "过期了" );
                                }
                            } );
                            tvItemJoinSuccessRestTime.startCountDown( joinSuccessProductEntity.mProListBean.countDown /1000 );

                        } catch (Exception e) {

                        }
                    }

                }
                @Override
                protected int onAttachedToRv(int position) {
                    if (position == ZeroBuyJoinSuccessTryCodeEntity.TYPE_TRYCODE_STATE){
                        return 2;

                    }else if (position == ZeroBuyJoinSuccessProductEntity.TYPE_PRODUCT){
                        return 1;
                    }
                    return super.onAttachedToRv(position);

                }
            };

            mRvJoinSuccess.setHasFixedSize(true);
            mRvJoinSuccess.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRvJoinSuccess.setLayoutManager(gridLayoutManager);
            mRvJoinSuccess.setAdapter(mBaseAdapter);

            mBaseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                }
            });

            //设置内部item的点击事件
            mBaseAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    int id = view.getId();
                    if (id == R.id.tv_item_join_success_more_trycode){
                        ZeroBuyJoinSuccessTryCodeEntity joinSuccessTryCodeEntity = (ZeroBuyJoinSuccessTryCodeEntity) adapter.getData().get(position);

                        if (!TextUtils.isEmpty(joinSuccessTryCodeEntity.code) && joinSuccessTryCodeEntity.code.contains(",")) {

                            String[] split = joinSuccessTryCodeEntity.code.split(",");
                            ArrayList<String> stringList = new ArrayList<>();
                            for (int i = 0; i < split.length; i++) {
                                stringList.add(split[i]);
                            }
                            showTryCodePopuwind(stringList);
                        }else if (!TextUtils.isEmpty(joinSuccessTryCodeEntity.code)){
                            ArrayList<String> stringList = new ArrayList<>();
                            stringList.add(joinSuccessTryCodeEntity.code);
                            showTryCodePopuwind(stringList);
                        }
                    }else if (id == R.id.tv_item_join_successt_current_trycode) {//查看更多
                        ZeroBuyJoinSuccessTryCodeEntity joinSuccessTryCodeEntity = (ZeroBuyJoinSuccessTryCodeEntity) adapter.getData().get(position);

                        if (!TextUtils.isEmpty(joinSuccessTryCodeEntity.keyNumSt) && joinSuccessTryCodeEntity.keyNumSt.contains(",")) {

                            String[] split = joinSuccessTryCodeEntity.keyNumSt.split(",");
                            ArrayList<String> stringList = new ArrayList<>();
                            for (int i = 0; i < split.length; i++) {
                                stringList.add(split[i]);
                            }
                            showTryCodePopuwind(stringList);
                        }else if (!TextUtils.isEmpty(joinSuccessTryCodeEntity.keyNumSt)){
                            ArrayList<String> stringList = new ArrayList<>();
                            stringList.add(joinSuccessTryCodeEntity.keyNumSt);
                            showTryCodePopuwind(stringList);
                        }
                    } else if (id == R.id.tv_item_join_successt_more_card) {
                        getSharePop();
//                        AndPermission.with(getActivity())
//                                .permission(Permission.Group.STORAGE)
//                                .onGranted(new Action() {
//                                    @Override
//                                    public void onAction(List<String> permissions) {
//                                        //这里需要读写的权限
//                                        ARouter.getInstance().build(ARouters.PATH_SHARE_APP).navigation();
//                                    }
//                                })
//                                .onDenied(new Action() {
//                                    @Override
//                                    public void onAction(@NonNull List<String> permissions) {
//                                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
//                                            //这个里面提示的是一直不过的权限
//                                        }
//                                    }
//                                })
//                                .start();
                    } else if (id == R.id.tv_item_canjia) {//参与试用
                        ZeroBuyJoinSuccessProductEntity joinSuccessProductEntity = (ZeroBuyJoinSuccessProductEntity) adapter.getData().get(position);
//                        getProduct(joinSuccessProductEntity.mProListBean.id);
                        ARouter.getInstance().build(ARouters.PATH_ZERO_BUY_DETAILS).withInt( "id",Integer.valueOf(joinSuccessProductEntity.mProListBean.id) ).withInt( "obtype",1 ).navigation();

                    }
                }
            });
        } else {
            mBaseAdapter.notifyDataSetChanged();
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
    private void getSharePop(){
        View contentView = LayoutInflater.from ( getActivity() ).inflate ( R.layout.popupwind_share_goods, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian
        mLlPopweixin = (LinearLayout) contentView.findViewById ( R.id.ll_shareweixin );
        mLlPoppengyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharepengyou );
        mLlPophaoyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharehaoyou );
        mLlPopkongjian = (LinearLayout) contentView.findViewById ( R.id.ll_sharekongjian );
        mIvPopFinish = (ImageView) contentView.findViewById ( R.id.iv_finish );
        mLlPopweixin.setOnClickListener( this );
        mLlPoppengyou.setOnClickListener( this );
        mLlPophaoyou.setOnClickListener( this );
        mLlPopkongjian.setOnClickListener( this );
        mIvPopFinish.setOnClickListener( this );

        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back) {
            getActivity().finish();
        }else if (id==R.id.iv_finish){
            mPopupWindow.dismiss();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.WEIXIN, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }else if (id==R.id.ll_sharepengyou){
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
//            addShare(image,SHARE_MEDIA.WEIXIN_CIRCLE);
        }else if (id==R.id.ll_sharehaoyou){
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.QQ, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }else if (id==R.id.ll_sharekongjian){
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.QZONE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (mBaseAdapter == null) {
            page = 2;
            mSmartJoinSuccess.finishLoadMore(1000);
        } else {
            page++;
        }
        getProduct(id);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {//无刷新功能
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartJoinSuccess.finishRefresh(state);
            if (!state || noMoreData) {
                //第一次加载失败时，再次显示时可以重新加载;page=1，第一次加载没数据时也再次从新显示
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartJoinSuccess.finishLoadMoreWithNoMoreData();
        } else {
            mSmartJoinSuccess.finishLoadMore(state);
        }
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
        mNumTryCode = mListString.size() + "";

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popu_mine_try_code, null);

        TextView tvMineTryCode = contentView.findViewById(R.id.tv_mine_try_code);
        SpannableString ss1 = new SpannableString("我的抽奖码(" + mNumTryCode + "个)");
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

