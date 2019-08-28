package java.com.lechuang.module.zuji;


import android.graphics.drawable.Drawable;
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
import com.common.app.utils.LogUtils;
import com.common.app.utils.StringUtils;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.SpannelTextViewSinge;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.ShouCangBean;
import java.com.lechuang.module.bean.ZuJiBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_ZUJI)
public class ZuJiActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {


    private boolean mIsSuccess = false;//默认进来是管理
    private LinearLayout mLlShouCangBottom;
    private SmartRefreshLayout mSmartShouCang;
    private RecyclerView mRvShouCang;
    private LinearLayout mNetError;
    private int page = 1;
    private TextView mTvShouCangQuanXuan,mTvShouCangDelete,mTvZuJi,tvPopKnowFinish,tvPopKnowSure;
    private PopupWindow mPopupWindow;
    private ImageView mIvZuJi;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zuji;
    }

    @Override
    protected void findViews() {


        mSmartShouCang = $(R.id.smart_shoucang);
        mRvShouCang = $(R.id.rv_shoucang);
        mTvShouCangQuanXuan = $(R.id.tv_shoucang_quanxuan);
        mTvShouCangDelete = $(R.id.tv_shoucang_delete);
        mTvZuJi = $(R.id.tv_zuji);
        mIvZuJi = $(R.id.iv_common_right);

        mLlShouCangBottom = $(R.id.ll_shoucang_bottom);
        $(R.id.iv_common_back).setOnClickListener(this);
        mIvZuJi.setImageDrawable( getResources ().getDrawable ( R.drawable.iv_zuji_lajitong ) );
        mIvZuJi.setOnClickListener(this);
        mTvShouCangQuanXuan.setOnClickListener(this);
        mTvShouCangDelete.setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_zuji ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "你近30天没有浏览足迹哦" );

    }

    @Override
    protected void initView() {
        ((TextView) $(R.id.iv_common_title)).setText("我的足迹");
        ((TextView) $(R.id.tv_common_right)).setText(mIsSuccess ? "完成" : "清除");
        $(R.id.tv_common_right).setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.bg_shoucang_quanxuan_selecter);
        drawable.setBounds(0, 0, 80, 80);
        mTvShouCangQuanXuan.setCompoundDrawables(drawable, null, null, null);
//        mLlShouCangBottom.setVisibility(mIsSuccess ? View.VISIBLE : View.GONE);
        mLlShouCangBottom.setVisibility( View.GONE);
        mSmartShouCang.setOnRefreshLoadMoreListener(this);
        if (mIsSuccess){
            mTvZuJi.setVisibility( View.GONE );
        }else {
            mTvZuJi.setVisibility( View.VISIBLE );
        }
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
                .setTag(Qurl.getZuji)
                .getApiService(ModuleApi.class)
                .getZuji(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZuJiBean>(this, true, false) {

                    @Override
                    public void onSuccess(ZuJiBean result) {

                        if (result == null || result.productList == null || result.productList.size() <= 0) {
                            if (page == 1 && mShouCangBeans != null && mBaseQuickAdapter != null) {
                                mShouCangBeans.clear();
                                mBaseQuickAdapter.notifyDataSetChanged();
                            }
                            if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            setRefreshLoadMoreState(true, true);
//                            updataAllSelectState();
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

    private void deleteZuJi(){
        NetWork.getInstance()
                .setTag(Qurl.deleteZuji)
                .getApiService(ModuleApi.class)
                .deleteZuji()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZuJiBean>(this, true, false) {

                    @Override
                    public void onSuccess(ZuJiBean result) {

                        if (result == null || result.productList == null || result.productList.size() <= 0) {
                            if (page == 1 && mShouCangBeans != null && mBaseQuickAdapter != null) {
                                mShouCangBeans.clear();
                                mBaseQuickAdapter.notifyDataSetChanged();
                            }
                            if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                                mNetError.setVisibility(View.VISIBLE);
                            }
                            setRefreshLoadMoreState(true, true);
//                            updataAllSelectState();
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        getShouCangData();

                        //设置底部商品数据
//                        setShouCangData(result.productList);
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
    private List<ZuJiBean.ShouCangBeans> mShouCangBeans;
    private BaseQuickAdapter<ZuJiBean.ShouCangBeans, BaseViewHolder> mBaseQuickAdapter;

    private void setShouCangData(List<ZuJiBean.ShouCangBeans> shouCangBeans) {

        if (mShouCangBeans == null) {
            mShouCangBeans = new ArrayList<>();
        }
        mNetError.setVisibility(View.INVISIBLE);
        if (page == 1) {
            mShouCangBeans.clear();
        }

        mShouCangBeans.addAll(shouCangBeans);
        mIsSuccess=true;
        mIvZuJi.setVisibility( View.VISIBLE );

        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<ZuJiBean.ShouCangBeans, BaseViewHolder>(R.layout.item_shoucang_rv, mShouCangBeans) {
                @Override
                protected void convert(BaseViewHolder helper, final ZuJiBean.ShouCangBeans item) {
                    try {

                        helper.getView(R.id.iv_shoucang_select).setVisibility( View.GONE);
                        helper.getView(R.id.iv_shoucang_select).setSelected(item.isSelect);
                        helper.getView(R.id.iv_shoucang_select).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                item.isSelect = !item.isSelect;
//                                updataAllSelectState();
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
                    new GridItemDecoration.Builder(ZuJiActivity.this)
                            .size(5)
            ));
            mRvShouCang.setAdapter(mBaseQuickAdapter);
            mBaseQuickAdapter.setOnItemClickListener(this);

        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
//            updataAllSelectState();
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
        } else if (id == R.id.tv_zuji_right) {
            if (mIsSuccess){
                mTvZuJi.setVisibility( View.GONE );
            }else {
                mTvZuJi.setVisibility( View.VISIBLE );
            }
            /*if (mLlShouCangBottom != null) {
                mLlShouCangBottom.setVisibility(mIsSuccess ? View.VISIBLE : View.GONE);
            }*/

            if (mBaseQuickAdapter != null) {
                //点击弹出可选删除框
                /*if (!mIsSuccess && mShouCangBeans != null) {
                    for (ShouCangBean.ShouCangBeans beans : mShouCangBeans) {
                        beans.isSelect = false;
                    }
                }
                mBaseQuickAdapter.notifyDataSetChanged();*/
                //点击弹出popwindown
                showPopupWindow();
            }
//            updataAllSelectState();
        } else if (id == R.id.tv_shoucang_quanxuan) {
            /*if (mShouCangBeans != null) {
                for (ShouCangBean.ShouCangBeans beans : mShouCangBeans) {
                    beans.isSelect = !mTvShouCangQuanXuan.isSelected();
                }
            }
            if (mBaseQuickAdapter != null) {
                mBaseQuickAdapter.notifyDataSetChanged();
            }

            updataAllSelectState();*/
        } else if (id == R.id.tv_shoucang_delete) {
            //删除
//            deleteShouCang();
        } else if (id == R.id.tv_popwindow_finish){
            mPopupWindow.dismiss();
        }else if (id == R.id.tv_popwindow_sure){
            //执行清除足迹
            deleteZuJi();
            mIsSuccess = false;
            mPopupWindow.dismiss();
            ((TextView) $(R.id.tv_common_right)).setText(mIsSuccess ? "完成" : "清除");
            mIvZuJi.setVisibility( View.GONE );
        }else if (id == R.id.iv_common_right){
            showPopupWindow();
        }
    }

    //弹出提示
    private void showPopupWindow() {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_zujiactivity_clear, null );
        mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnowFinish = (TextView) contentView.findViewById ( R.id.tv_popwindow_finish );
        tvPopKnowSure = (TextView) contentView.findViewById ( R.id.tv_popwindow_sure );
        tvPopKnowFinish.setOnClickListener ( this );
        tvPopKnowSure.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
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

        LinkRouterUtils.getInstance().setRouterBean(ZuJiActivity.this, routerBean);

    }
}
