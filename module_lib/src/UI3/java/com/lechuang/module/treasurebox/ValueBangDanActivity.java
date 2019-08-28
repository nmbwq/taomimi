package java.com.lechuang.module.treasurebox;

import android.content.*;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseOtherActivity;
import com.common.app.base.bean.BaseItemEntity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = ARouters.PATH_VALUE_BANG_DAN)
public class ValueBangDanActivity extends BaseOtherActivity implements View.OnClickListener, OnRefreshLoadMoreListener {

    private RecyclerView mRvMineBangDan;
    private TextView mTvCommonTitle,mTvCommonClickTry;
    private BaseAdapter<BaseItemEntity, BaseViewHolder> mBaseAdapter;
    private int page = 1;
    private SmartRefreshLayout mSmartMineBangDan;
    private View mLlNetError;
    private ImageView mIvCommonImage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_value_bang_dan;
    }

    @Override
    protected void findViews() {
        mRvMineBangDan = $(R.id.rv_value_bangdan);
        mTvCommonTitle = $(R.id.iv_common_title);
        mSmartMineBangDan = $(R.id.smart_value_bangdan);
        mLlNetError = $(R.id.ll_net_error);
        mIvCommonImage = $(R.id.iv_common_image);
        mTvCommonClickTry = $(R.id.tv_common_click_try);
        mTvCommonClickTry.setText("无更多奖品");
        mIvCommonImage.setImageResource(R.drawable.ic_value_bangdan);
        $(R.id.iv_common_back).setOnClickListener(this);
        mTvCommonTitle.setText("我的奖品");
        mSmartMineBangDan.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initView() {
        mActivityUnique = getIntent().getLongExtra("mActivityUnique",0);
    }

    @Override
    protected void getData() {
        mSmartMineBangDan.autoRefresh(100);
    }

    private void getMineBangDanData(){
         Map<String, Object> allParam = new HashMap<>();

        allParam.put("page", page + "");

        NetWork.getInstance()
                .setTag(Qurl.showWinInfo)
                .getApiService(ModuleApi.class)
                .showWinInfo(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShowWinInfoBean>(this, false, false) {
                    @Override
                    public void onSuccess(ShowWinInfoBean result) {
                        if (result == null || result.LottoWin == null || result.LottoWin.size() <= 0) {
                            setRefreshLoadMoreState(true, true);
                            page --;
                            if (mBangDanEntityList == null || mBangDanEntityList.size() < 0){
                                mLlNetError.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        mLlNetError.setVisibility(View.INVISIBLE);
                        setAdapter(result.LottoWin);

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
    private void setAdapter(List<ShowWinInfoBean.LottoWinBean> list){
        if (mBangDanEntityList == null){
            mBangDanEntityList = new ArrayList<>();
        }
        if (page == 1){
            mBangDanEntityList.clear();
        }

            for (int i = 0;i < list.size(); i++){
                ShowWinInfoBean.LottoWinBean listBean = list.get(i);
                ShowWinInfoEntity valueBangDanEntity = new ShowWinInfoEntity(ShowWinInfoEntity.TYPE_NONE);
                valueBangDanEntity.acquiredTime = listBean.acquiredTime;
                valueBangDanEntity.awardProduct = listBean.awardProduct;
                valueBangDanEntity.id = listBean.id;
                valueBangDanEntity.isChange = listBean.isChange;
                valueBangDanEntity.nickName = listBean.nickName;
                valueBangDanEntity.wechatNumber = listBean.wechatNumber;
                valueBangDanEntity.winCount = listBean.winCount;
                valueBangDanEntity.winType = listBean.winType;

                mBangDanEntityList.add(valueBangDanEntity);
            }


            if (mBaseAdapter == null){
                mBaseAdapter = new BaseAdapter<BaseItemEntity, BaseViewHolder>(mBangDanEntityList){

                    @Override
                    protected void convert(BaseViewHolder helper, BaseItemEntity item1) {
                        int itemType = item1.getItemType();
                        if (itemType == ShowWinInfoEntity.TYPE_NONE){
                            try{
                                ShowWinInfoEntity item = (ShowWinInfoEntity) item1;
                                helper.setText(R.id.tv_item_value_dang_dan_paiming,item.awardProduct + "X" + item.winCount);
                                helper.setText(R.id.tv_item_value_dang_dan_time,item.acquiredTime);

                                TextView tvItemMineBandDanJiangLiState = helper.getView(R.id.tv_item_value_dang_dan_jiangli_state);
                                helper.addOnClickListener(R.id.tv_item_value_dang_dan_jiangli_state);

                                if (TextUtils.equals("2",item.winType) && item.isChange == 0){//领取奖励
                                    tvItemMineBandDanJiangLiState.setVisibility(View.VISIBLE);
                                    tvItemMineBandDanJiangLiState.setText("领取奖励");
                                    tvItemMineBandDanJiangLiState.setBackgroundResource(R.drawable.bg_value_bang_dan_dailingqu);
                                }else if (TextUtils.equals("2",item.winType) && item.isChange == 1){//已领取
                                    tvItemMineBandDanJiangLiState.setVisibility(View.VISIBLE);
                                    tvItemMineBandDanJiangLiState.setText("已领取");
                                    tvItemMineBandDanJiangLiState.setBackgroundResource(R.drawable.bg_value_bang_dan_state_huode);
                                } else if (TextUtils.equals("2",item.winType) && item.isChange == 2){//已失效
                                    tvItemMineBandDanJiangLiState.setVisibility(View.VISIBLE);
                                    tvItemMineBandDanJiangLiState.setText("已失效");
                                    tvItemMineBandDanJiangLiState.setBackgroundResource(R.drawable.bg_value_bang_dan_state_shixiao);
                                }else {
                                    tvItemMineBandDanJiangLiState.setVisibility(View.INVISIBLE);
                                }

                            }catch (Exception e){

                            }
                        }


                    }

                    @Override
                    protected void addItemTypeView() {
                        addItemType(ShowWinInfoEntity.TYPE_NONE,R.layout.item_value_bang_dan);
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
                        if (id == R.id.tv_item_value_dang_dan_jiangli_state){
                            ShowWinInfoEntity showWinInfoEntity = (ShowWinInfoEntity) adapter.getData().get(position);
                            if (showWinInfoEntity.isChange == 0){
//                                openValue();
                                String test = "恭喜您抽中" + showWinInfoEntity.awardProduct + "X" + showWinInfoEntity.winCount;
                                showSuccessPopuwind(false,test,showWinInfoEntity.wechatNumber);
                            }else if (showWinInfoEntity.isChange == 1){
                                toast("您已经领取奖励了哦, 不可重复领取");
                            }else if (showWinInfoEntity.isChange == 2){
                                toast("您的奖品已失效");
                            }

                        }

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

    private long mActivityUnique;

    private void openValue() {

        if (mActivityUnique == 0){
            toast("获取活动信息出错");
            return;
        }

        Map<String,Object> param = new HashMap<>();
        param.put("activityUnique",mActivityUnique);
        param.put("page",0);

        NetWork.getInstance ()
                .setTag(Qurl.lotRun)
                .getApiService(ModuleApi.class)
                .lotRun(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LotRunBean>(ValueBangDanActivity.this,false,true){

                    @Override
                    public void onSuccess(LotRunBean result) {
                        if (result == null ){
                            return;
                        }
                        if (result.type == 1){//觅卡
                            showCardPopuwind(result.img,result.prize);
                        }else if (result.type == 2){//商品
                            showSuccessPopuwind(false,result.prize,result.wechatCare);
                        }
                    }

                    @Override
                    public void onFailed_11004(LotRunBean result) {
                        super.onFailed_11004(result);
                        showSuccessPopuwind(true,"","");
                    }
                });
    }

    private PopupWindow mPopupWindow;
    private void showSuccessPopuwind(boolean isSuccess, String info, final String wechatNum) {
        View contentView = LayoutInflater.from(this).inflate(isSuccess ? R.layout.layout_popu_value_success1 : R.layout.layout_popu_value_success2, null);

        if (isSuccess){
            contentView.findViewById(R.id.tv_popu_week_signin_join).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //去参与
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                    AndPermission.with(ValueBangDanActivity.this)
                            .permission(Permission.Group.STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    //这里需要读写的权限
                                    ARouter.getInstance().build(ARouters.PATH_SHARE_APP).navigation();
                                }
                            })
                            .onDenied(new Action() {
                                @Override
                                public void onAction(@NonNull List<String> permissions) {
                                    if (AndPermission.hasAlwaysDeniedPermission(ValueBangDanActivity.this, permissions)) {
                                        //这个里面提示的是一直不过的权限
                                    }
                                }
                            })
                            .start();
                }
            });
            contentView.findViewById(R.id.iv_popu_value_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //去参与
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                }
            });
        }else {
            TextView tvPupoWeekSigninSuccessContent = contentView.findViewById(R.id.tv_popu_week_signin_success_content);
            TextView tvPupoWeekSigninSuccessCopy = contentView.findViewById(R.id.tv_popu_week_signin_wechat_num);
            tvPupoWeekSigninSuccessContent.setText(info);
            tvPupoWeekSigninSuccessCopy.setText(wechatNum);
            contentView.findViewById(R.id.ll_popu_week_signin_success_copy_wechat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                    try {
                        if (!TextUtils.isEmpty(wechatNum.trim())){
                            // 获取系统剪贴板
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                            ClipData clipData = ClipData.newPlainText("app_kefu", TextUtils.isEmpty(wechatNum) ? "" : wechatNum);

                            // 把数据集设置（复制）到剪贴板
                            clipboard.setPrimaryClip(clipData);

                            getWechatApi();
                        }else {
                            toast("复制失败！");
                        }
                    }catch (Exception e){

                    }
                }
            });
        }

        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 跳转到微信
     */
    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast( "检查到您手机没有安装微信，请安装后使用该功能" );
        }
    }

    private void showCardPopuwind(String imgUrl, String info) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popu_value_success3,null);


        TextView viewById = contentView.findViewById(R.id.tv_popu_value_jiangpin);
        viewById.setText(info);
        ImageView ivPopuValueCard = contentView.findViewById(R.id.iv_popu_value_card);
        Glide.with(BaseApplication.getApplication()).load(imgUrl).into(ivPopuValueCard);
        contentView.findViewById(R.id.tv_popu_week_signin_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去参与
                if (mPopupWindow != null){
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        contentView.findViewById(R.id.iv_popu_value_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去参与
                if (mPopupWindow != null){
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
