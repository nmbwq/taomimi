package java.com.lechuang.module.shoucang;


import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.StringUtils;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.SpannelTextViewSinge;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.ShouCangBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SHOUCANG)
public class ShouCangActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {


    private boolean mIsSuccess = false;//默认进来是管理
    private LinearLayout mLlShouCangBottom;
    private SmartRefreshLayout mSmartShouCang;
    private RecyclerView mRvShouCang;
    private LinearLayout mNetError;
    private int page = 1;
    private TextView mTvShouCangQuanXuan,mTvShouCangDelete;
    private TextView mTvCommonRight;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shou_cang;
    }

    @Override
    protected void findViews() {


        mSmartShouCang = $(R.id.smart_shoucang);
        mRvShouCang = $(R.id.rv_shoucang);
        mTvShouCangQuanXuan = $(R.id.tv_shoucang_quanxuan);
        mTvShouCangDelete = $(R.id.tv_shoucang_delete);

        mLlShouCangBottom = $(R.id.ll_shoucang_bottom);
        mTvCommonRight = $(R.id.tv_common_right);
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        mTvShouCangQuanXuan.setOnClickListener(this);
        mTvShouCangDelete.setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_shoucang ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "还没有关注的内容哦，先去逛逛吧~" );

    }

    @Override
    protected void initView() {
        mTvCommonRight.setTextColor(getResources().getColor(R.color.c_161616));
        mTvCommonRight.setTextSize(13);
        ((TextView) $(R.id.iv_common_title)).setText("我的收藏");
        ((TextView) $(R.id.tv_common_right)).setText(mIsSuccess ? "完成" : "管理");
        $(R.id.tv_common_right).setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.bg_shoucang_quanxuan_selecter);
        drawable.setBounds(0, 0, 80, 80);
        mTvShouCangQuanXuan.setCompoundDrawables(drawable, null, null, null);
        mLlShouCangBottom.setVisibility(mIsSuccess ? View.VISIBLE : View.GONE);
        mSmartShouCang.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void getData() {
        mSmartShouCang.autoRefresh(100);
    }

    /**
     * 获取数据
     */
    private void getShouCangData() {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("page", page + "");

        NetWork.getInstance()
                .setTag(Qurl.shoucang)
                .getApiService(ModuleApi.class)
                .shouCang(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShouCangBean>(this, true, false) {

                    @Override
                    public void onSuccess(ShouCangBean result) {

                        if (result == null || result.productList == null || result.productList.size() <= 0) {
                            if (page == 1 && mShouCangBeans != null && mBaseQuickAdapter != null) {
                                mShouCangBeans.clear();
                                mBaseQuickAdapter.notifyDataSetChanged();
                            }
                            if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            setRefreshLoadMoreState(true, true);
                            updataAllSelectState();
                            return;
                        }
                        setRefreshLoadMoreState(true, false);

                        //设置底部商品数据
                        setShouCangData(result.productList);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    /**
     * 设置收藏的数据
     * isUpdata  强制更新
     */
    private List<ShouCangBean.ShouCangBeans> mShouCangBeans;
    private BaseQuickAdapter<ShouCangBean.ShouCangBeans, BaseViewHolder> mBaseQuickAdapter;

    private void setShouCangData(List<ShouCangBean.ShouCangBeans> shouCangBeans) {

        if (mShouCangBeans == null) {
            mShouCangBeans = new ArrayList<>();
        }
        mNetError.setVisibility(View.INVISIBLE);
        if (page == 1) {
            mShouCangBeans.clear();
        }

        mShouCangBeans.addAll(shouCangBeans);


        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<ShouCangBean.ShouCangBeans, BaseViewHolder>(R.layout.item_shoucang_rv, mShouCangBeans) {
                @Override
                protected void convert(BaseViewHolder helper, final ShouCangBean.ShouCangBeans item) {
                    try {

                        helper.getView(R.id.iv_shoucang_select).setVisibility(mIsSuccess ? View.VISIBLE : View.GONE);
                        helper.getView(R.id.iv_shoucang_select).setSelected(item.isSelect);
                        helper.getView(R.id.iv_shoucang_select).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                item.isSelect = !item.isSelect;
                                updataAllSelectState();
                                mBaseQuickAdapter.notifyDataSetChanged();
                            }
                        });
                        //是否显示视频图片
//                        helper.getView( R.id.iv_video ).setVisibility( item.isVideo.equals( "1" )? View.VISIBLE:View.GONE);
                        if (item.isStatus == 1){//失效
                            helper.getView(R.id.iv_item_shoucang_sx).setVisibility(View.VISIBLE);
                            helper.getView(R.id.ll_item_shoucang_product_parent).setVisibility(View.INVISIBLE);
                            helper.getView(R.id.ll_item_shoucang_product_parent1).setVisibility(View.INVISIBLE);
                            ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_shoucang_tupian);
                            Glide.with(BaseApplication.getApplication()).load(R.drawable.ic_shoucang_sxleft).placeholder ( R.drawable.bg_common_img_null ).into(ivItemAllFenLei);

                        }else {
                            helper.getView(R.id.iv_item_shoucang_sx).setVisibility(View.INVISIBLE);
                            helper.getView(R.id.ll_item_shoucang_product_parent).setVisibility(View.VISIBLE);
                            helper.getView(R.id.ll_item_shoucang_product_parent1).setVisibility(View.VISIBLE);
                            //商品图片
                            ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_shoucang_tupian);
                            Glide.with(BaseApplication.getApplication()).load(item.img).placeholder ( R.drawable.bg_common_img_null ).into(ivItemAllFenLei);

                            //设置来源和标题
                            SpannelTextViewSinge spannelTextViewSinge = helper.getView(R.id.tv_item_shoucang_biaoti);
                            spannelTextViewSinge.setDrawText(item.name);
                            spannelTextViewSinge.setShopType(item.shopType);

                            //补贴佣金
                            helper.setText(R.id.tv_item_shoucang_butie_yongjin, "补贴佣金  ¥暂无显示");

                            //预估佣金
//                        helper.getView(R.id.tv_item_shoucang_yugu_yongjin).setVisibility(TextUtils.isEmpty(item.zhuanMoney) ? View.GONE : View.VISIBLE);
                            boolean isAllShow = true;
                            if (UserHelper.getInstence().getUserInfo().getAgencyLevel() == 3 ||
                                    UserHelper.getInstence().getUserInfo().getAgencyLevel() == 4) {
                                isAllShow = false;
                            }
//                        helper.getView(R.id.tv_item_shoucang_yugu_yongjin).setVisibility(isAllShow ? View.GONE : View.VISIBLE);
//                        helper.setText(R.id.tv_item_shoucang_yugu_yongjin, "预估佣金  ¥" + StringUtils.stringToStringDeleteZero(item.zhuanMoney));

                            //券金额
//                            helper.getView(R.id.tv_item_shoucang_quan).setVisibility(item.couponMoney == 0 ? View.GONE : View.VISIBLE);
//                            helper.setText(R.id.tv_item_shoucang_quan, "" + StringUtils.doubleToStringDeleteZero(item.couponMoney) + "元券");

                            //现价(券后价)
//                            helper.setText(R.id.tv_item_shoucang_xianjia, "¥" + StringUtils.stringToStringDeleteZero(item.preferentialPrice) + "");

                            //原价
                            helper.setText(R.id.tv_item_shoucang_yuanjia, "¥" + StringUtils.doubleToStringDeleteZero(item.price) + "");
//                            ((TextView) helper.getView(R.id.tv_item_shoucang_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                            //销量
                            helper.setText(R.id.tv_item_shoucang_xiaoliang, "月销" + StringUtils.intToStringUnit(item.nowNumber) + "件");
                        }



                    } catch (Exception e) {

                    }

                }
            };

            mRvShouCang.setHasFixedSize(true);
            mRvShouCang.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            mRvShouCang.setLayoutManager(gridLayoutManager);
            mRvShouCang.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(ShouCangActivity.this)
                            .size(5)
            ));
            mRvShouCang.setAdapter(mBaseQuickAdapter);
            mBaseQuickAdapter.setOnItemClickListener(this);

        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
            updataAllSelectState();
        }
    }

    /**
     * 更新全选的状态
     */
    private void updataAllSelectState() {
        if (mShouCangBeans == null || mShouCangBeans.size() <= 0) {
            mTvShouCangQuanXuan.setSelected(false);
            return;
        }
        int count = 0;
        for (ShouCangBean.ShouCangBeans beans : mShouCangBeans) {
            if (beans.isSelect) {
                count++;
            }
        }
        if (count == mShouCangBeans.size()) {
            mTvShouCangQuanXuan.setSelected(true);
        } else {
            mTvShouCangQuanXuan.setSelected(false);
        }

    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartShouCang.finishRefresh(state);
        } else if (noMoreData) {
            mSmartShouCang.finishLoadMoreWithNoMoreData();
        } else {
            mSmartShouCang.finishLoadMore(state);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.tv_common_right) {
            mIsSuccess = !mIsSuccess;

            ((TextView) $(R.id.tv_common_right)).setText(mIsSuccess ? "完成" : "管理");
            if (mLlShouCangBottom != null) {
                mLlShouCangBottom.setVisibility(mIsSuccess ? View.VISIBLE : View.GONE);
            }

            if (mBaseQuickAdapter != null) {
                if (!mIsSuccess && mShouCangBeans != null) {
                    for (ShouCangBean.ShouCangBeans beans : mShouCangBeans) {
                        beans.isSelect = false;
                    }
                }
                mBaseQuickAdapter.notifyDataSetChanged();
            }
            updataAllSelectState();
        } else if (id == R.id.tv_shoucang_quanxuan) {
            if (mShouCangBeans != null) {
                for (ShouCangBean.ShouCangBeans beans : mShouCangBeans) {
                    beans.isSelect = !mTvShouCangQuanXuan.isSelected();
                }
            }
            if (mBaseQuickAdapter != null) {
                mBaseQuickAdapter.notifyDataSetChanged();
            }

            updataAllSelectState();
        } else if (id == R.id.tv_shoucang_delete) {
            //删除
            deleteShouCang();
        }
    }

    /**
     * 删除收藏选中
     */
    private void deleteShouCang() {
        Map<String, String> allParam = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        if (mShouCangBeans.size() <= 0){
            toast("暂无数据操作！");
            return;
        }

        for (ShouCangBean.ShouCangBeans shouCangBeans : mShouCangBeans) {
            if (shouCangBeans.isSelect){
                stringBuilder.append(shouCangBeans.id + ",");
            }

        }
        if (TextUtils.isEmpty(stringBuilder.toString())){
            toast("请选择删除条目！");
            return;
        }
        allParam.put("ids", stringBuilder.toString());
        NetWork.getInstance()
                .setTag(Qurl.deleteShoucang)
                .getApiService(ModuleApi.class)
                .deleteShouCang(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShouCangBean>(this, true, true) {
                    @Override
                    public void onSuccess(ShouCangBean result) {
                        page = 1;
                        if (result == null || result.productList == null || result.productList.size() <= 0) {
                            if (mShouCangBeans != null && mBaseQuickAdapter != null) {
                                mShouCangBeans.clear();
                                mBaseQuickAdapter.notifyDataSetChanged();
                            }
                            if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            setRefreshLoadMoreState(true, true);
                            updataAllSelectState();
                            return;
                        }
                        if (!TextUtils.isEmpty(result.infoData)) {

                            setRefreshLoadMoreState(true, false);
                        }
                        //设置底部商品数据
                        setShouCangData(result.productList);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                            mNetError.setVisibility(View.VISIBLE);
                        }
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        page++;
        getShouCangData();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mSmartShouCang.setNoMoreData(false);
        page = 1;
        getShouCangData();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RouterBean routerBean = new RouterBean();
        routerBean.type = 9;
        routerBean.tbCouponId = mShouCangBeans.get(position).tbCouponId;
        routerBean.mustParam = "type=3"
                + "&id=" + mShouCangBeans.get(position).productId
                + "&tbItemId=" + mShouCangBeans.get(position).tbItemId;
//        routerBean.t = "1";
//        routerBean.id = mShouCangBeans.get(position).id;
//        routerBean.i = mShouCangBeans.get(position).tbItemId;

        LinkRouterUtils.getInstance().setRouterBean(ShouCangActivity.this, routerBean);

    }
}
