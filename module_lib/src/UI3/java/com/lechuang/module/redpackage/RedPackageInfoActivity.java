package java.com.lechuang.module.redpackage;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseOtherActivity;
import com.common.app.base.bean.BaseItemEntity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.DeviceUtils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.RedPackInfoEntity;
import java.com.lechuang.module.bean.RedPackageInfoBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = ARouters.PATH_REDPACKAGE_INFO)
public class RedPackageInfoActivity extends BaseOtherActivity implements OnRefreshLoadMoreListener {

    private int page = 1;
    private RecyclerView mRvRedPackageInfoPaiming;
    private TextView mTvRedPackageInfoFaChu,mTvRedPackageInfoUse,mTvRedPackageInfoUnUse,mTvRedPackageInfoResult,mTvCommonTitle;
    private SmartRefreshLayout mSmartRedPackageInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_red_package_info;
    }

    @Override
    protected void findViews() {

        mTvCommonTitle = $(R.id.iv_common_title);
        mTvCommonTitle.setText("红包明细");

        mRvRedPackageInfoPaiming = $(R.id.rv_red_package_paiming);
        mTvRedPackageInfoFaChu = $(R.id.tv_red_package_info_fachu);
        mTvRedPackageInfoUse = $(R.id.tv_red_package_info_use);
        mTvRedPackageInfoUnUse = $(R.id.tv_red_package_info_unuse);
        mTvRedPackageInfoResult = $(R.id.tv_red_package_info_result);
        mSmartRedPackageInfo = $(R.id.smart_red_package_info);
        $(R.id.iv_common_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        mSmartRedPackageInfo.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void getData() {
        getInfoData();
    }

    private void getInfoData(){

        Map<String,Object> param = new HashMap<>();
        param.put("page",page);

        NetWork.getInstance()
                .setTag(Qurl.redPacketInfo)
                .getApiService(ModuleApi.class)
                .redPacketInfo(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<RedPackageInfoBean>(this, false, true) {

                    @Override
                    public void onSuccess(RedPackageInfoBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            setRefreshLoadMoreState(true, true);
                            page --;
                            return;
                        }
                        setAdapter(result.provide,result.restore,result.list);
                        setRefreshLoadMoreState(true, false);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                        page --;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        page --;
                    }
                });
    }

    private List<BaseItemEntity> mRedPackInfoEntities;
    private BaseAdapter<BaseItemEntity, BaseViewHolder> mBaseAdapter;
    private void setAdapter(double provide, double restore, List<RedPackageInfoBean.ListBean> list){

        if (mRedPackInfoEntities == null){
            mRedPackInfoEntities = new ArrayList<>();
        }

        if (page == 1){
            mRedPackInfoEntities.clear();
            RedPackInfoEntity redPackInfoEntityHeader = new RedPackInfoEntity(RedPackInfoEntity.TYPE_HEADER);
            redPackInfoEntityHeader.provide = provide;
            redPackInfoEntityHeader.restore = restore;
            mRedPackInfoEntities.add(redPackInfoEntityHeader);
        }


        for (int i = 0; i < list.size(); i++){
            RedPackageInfoBean.ListBean listBean = list.get(i);

            RedPackInfoEntity redPackInfoEntity = new RedPackInfoEntity(BaseItemEntity.TYPE_DEFITEM);
            redPackInfoEntity.createTime = listBean.createTime;
            redPackInfoEntity.expiredTime = listBean.expiredTime;
            redPackInfoEntity.id = listBean.id;
            redPackInfoEntity.intId = listBean.intId;
            redPackInfoEntity.money = listBean.money;
            redPackInfoEntity.isStatus = listBean.isStatus;
            redPackInfoEntity.nickName = listBean.nickName;
            redPackInfoEntity.phone = listBean.phone;
            redPackInfoEntity.photo = listBean.photo;
            redPackInfoEntity.type = listBean.type;
            redPackInfoEntity.upUserId = listBean.upUserId;
            redPackInfoEntity.userId = listBean.userId;
            mRedPackInfoEntities.add(redPackInfoEntity);
        }

        if (mBaseAdapter == null){
            mBaseAdapter = new BaseAdapter<BaseItemEntity, BaseViewHolder>(mRedPackInfoEntities) {
                @Override
                protected void addItemTypeView() {
                    addItemType(RedPackInfoEntity.TYPE_HEADER,R.layout.item_red_package_info_header);
                    addItemType(BaseItemEntity.TYPE_DEFITEM,R.layout.item_red_package_info);
                }

                @Override
                protected void convert(BaseViewHolder helper, BaseItemEntity item) {
                    try {
                        int itemType = item.getItemType();

                        if (itemType == RedPackInfoEntity.TYPE_HEADER){
                            RedPackInfoEntity redPackInfoEntity = (RedPackInfoEntity) item;
                            helper.setText(R.id.tv_red_package_info_fachu,redPackInfoEntity.provide + "");
                            helper.setText(R.id.tv_red_package_info_result,redPackInfoEntity.restore + "");
                        }else if (itemType ==  BaseItemEntity.TYPE_DEFITEM){
                            RedPackInfoEntity redPackInfoEntity = (RedPackInfoEntity) item;

                            ImageView photo = helper.getView(R.id.iv_item_red_package_info_photo);
                            Glide.with(BaseApplication.getApplication()).load(redPackInfoEntity.photo).placeholder(R.drawable.ic_common_user_def).transform(new GlideRoundTransform(BaseApplication.getApplication(),20)).into(photo);

                            if (redPackInfoEntity.nickName!=null){
                                if (redPackInfoEntity.nickName.length()>8){
                                    helper.setText(R.id.tv_item_red_package_info_name,redPackInfoEntity.nickName.substring( 0,8 )+"...");
                                }else {
                                    helper.setText(R.id.tv_item_red_package_info_name,redPackInfoEntity.nickName);
                                }
                            }else {
                                helper.setText(R.id.tv_item_red_package_info_name,"木有昵称");
                            }
                            if (redPackInfoEntity.phone!=null){
                                helper.setText(R.id.tv_item_red_package_info_phone,redPackInfoEntity.phone.substring ( 0,3 )+"****"+redPackInfoEntity.phone.substring ( 7,11 ));
                            }
                            if (""+redPackInfoEntity.money!=null){
                                helper.setText(R.id.tv_item_red_package_info_price,redPackInfoEntity.money + "");
                            }

                            TextView state = helper.getView(R.id.tv_item_red_package_info_state);
                            if (redPackInfoEntity.isStatus == 0){//已领取
                                state.setText("已领取");
                                state.setBackground(getResources().getDrawable(R.drawable.bg_red_packinfo_state_unuse));
                                helper.setText(R.id.tv_item_red_package_info_data,redPackInfoEntity.createTime + "");
                            }else if (redPackInfoEntity.isStatus == 1){//已返还
                                state.setText("已返还");
                                state.setBackground(getResources().getDrawable(R.drawable.bg_red_packinfo_state_use));
                                helper.setText(R.id.tv_item_red_package_info_data,redPackInfoEntity.expiredTime + "");
                            }
                        }

                    }catch (Exception e){

                    }

                }
            };

            mRvRedPackageInfoPaiming.setHasFixedSize(true);
            mRvRedPackageInfoPaiming.setNestedScrollingEnabled(false);
            mRvRedPackageInfoPaiming.setLayoutManager(new GridLayoutManager(this, 1));
            mRvRedPackageInfoPaiming.setAdapter(mBaseAdapter);

        }else {
            mBaseAdapter.notifyDataSetChanged();
        }

    }
    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            if (noMoreData){
                mSmartRedPackageInfo.finishLoadMoreWithNoMoreData();
            }
            mSmartRedPackageInfo.finishRefresh(state);
        } else if (noMoreData) {
            mSmartRedPackageInfo.finishLoadMoreWithNoMoreData();
        } else {
            mSmartRedPackageInfo.finishLoadMore(state);
        }

    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (mBaseAdapter == null || mRedPackInfoEntities== null || mRedPackInfoEntities.size() <= 0){
            page = 1;
            getInfoData();
            mSmartRedPackageInfo.finishLoadMore();
            return;
        }
        page++;
        getInfoData();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        getInfoData();
        mSmartRedPackageInfo.setNoMoreData(false);
    }
}
