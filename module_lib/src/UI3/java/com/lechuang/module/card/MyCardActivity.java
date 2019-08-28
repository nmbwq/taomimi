package java.com.lechuang.module.card;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.base.FragmentActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.Utils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.EarningsRecordBean;
import java.com.lechuang.module.bean.MyCardBean;
import java.com.lechuang.module.earnings.EarningsRecordActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MY_CARD)
public class MyCardActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener{
    private SmartRefreshLayout mSmartRecord;
    private RecyclerView mRvRecord;
    private int page = 1;//页数
    private boolean mload = true;
    private PopupWindow mPopupWindow;
    private ImageView mIvDiss;
    private TextView mTvContentOne,mTvContentTwo;
    private LinearLayout mNetError;
    private View vNull;
    public static boolean refresh=false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mycard;
    }

    @Override
    protected void findViews() {
        mRvRecord=$(R.id.rv_recyclerView);
        mSmartRecord=$(R.id.smart_record);
        mNetError = $ ( R.id.ll_net_error );
        vNull = $ ( R.id.v_null );
        $(R.id.iv_common_back).setOnClickListener(this);
//        mEtAlipayNumber = $(R.id.et_bind_alipayNumber);
//        mEtAlipayRealName = $(R.id.et_bind_alipayRealName);
//        mTvSave = $(R.id.et_bind_zfb_save);
//        mTvSave.setOnClickListener ( this );
//        ((TextView) $(R.id.tv_common_right)).setVisibility( View.VISIBLE );
        ((TextView) $(R.id.tv_common_right)).setOnClickListener ( this );
        ((TextView) $(R.id.tv_mycard_exchange)).setOnClickListener ( this );
    }

    @Override
    protected void initView() {
        mSmartRecord.setOnRefreshLoadMoreListener(this);
        mSmartRecord.setEnableLoadMore ( false );
    }

    @Override
    protected void getData() {
        mSmartRecord.autoRefresh(500);
//        getAllData();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_bind_zfb_save){
        }else if (id== R.id.iv_common_back){
            finish();
        }else if (id==R.id.tv_common_right){
            ARouter.getInstance().build(ARouters.PATH_MY_CARD_RECORD).navigation();
        }else if (id==R.id.iv_popwindow_diss){
            mPopupWindow.dismiss();
        }else if (id == R.id.tv_mycard_exchange){
            ARouter.getInstance().build(ARouters.PATH_MY_CARD_EXCHANGE).navigation();
        }
    }

    private void getAllData() {

        /*Map<String, String> allParam = new HashMap<>();
        String pageOne = page + "";
        allParam.put("page", pageOne);*/
        NetWork.getInstance()
                .setTag(Qurl.myCard)
                .getApiService(ModuleApi.class)
                .myCard()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardBean>(MyCardActivity.this, true, false) {

                    @Override
                    public void onSuccess(MyCardBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        setProductData((result.getList()));
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
//                        setRefreshLoadMoreState(false, false);
                        mNetError.setVisibility(View.VISIBLE);
                        mRvRecord.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        setRefreshLoadMoreState(false, false);
                        mNetError.setVisibility(View.VISIBLE);
                        mRvRecord.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<MyCardBean.ListBean> mProductList;
    private BaseQuickAdapter<MyCardBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setProductData(List<MyCardBean.ListBean> productList) {
        if (productList == null || productList.size() == 0) {
            if (mload) {
                mNetError.setVisibility(View.VISIBLE);
                mRvRecord.setVisibility(View.GONE);
            }
            mload = true;
            return;
        } else {
            mNetError.setVisibility(View.GONE);
            mRvRecord.setVisibility(View.VISIBLE);
        }
        if (mProductList == null) {
            mProductList = new ArrayList<>();
        }
        if (page == 1) {
            mProductList.clear();
        }
//        mNetError.setVisibility(View.INVISIBLE);
        mProductList.addAll(productList);

        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<MyCardBean.ListBean, BaseViewHolder>
                    (R.layout.item_mycard_list, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, final MyCardBean.ListBean item) {
                    try {

                        //背景色rl_all_colour
                        ImageView imageView = helper.getView(R.id.iv_macard_bg);
                        Glide.with( BaseApplication.getApplication()).load(item.img).centerCrop().placeholder(R.drawable.bg_mycard_img_null).into(imageView);
                        //卡名 tv_card_name
                        helper.setText(R.id.tv_card_name,  item.name);

                        //数量 tv_card_num
                        helper.setText(R.id.tv_card_num,  "剩余"+item.count+"张");

                        //使用说明 tv_card_directions
                        helper.getView(R.id.tv_card_directions).setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPopupWindow( item.getRemark() );
                            }
                        } );
                        //查看更多
                        helper.getView(R.id.tv_touse).setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                if (item.isActive==0){
                                    toast( item.useMessage );
                                }else if (item.isActive==1){
                                    //activeType活动类型 1 免费试用; 2 签到活动; 3 早起打卡; 4 拉新榜单; 5 幸运转盘; 6 神奇宝箱; 7 0元抢购
                                    if (item.activeType==1){//免费试用
                                        ARouter.getInstance().build(ARouters.PATH_MY_TRY).navigation();
                                    }else if (item.activeType==2){//签到活动
                                        Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_WEEK_SIGNIN).navigation();
                                        FragmentActivity.start(MyCardActivity.this, mineTryFragment.getClass());
                                    }else if (item.activeType==3){//早起打卡
                                        ARouter.getInstance().build(ARouters.PATH_SIGN_IN).navigation();
                                    }else if (item.activeType==7){//拉新榜单
                                        Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_LAXIN).navigation();
                                        FragmentActivity.start(MyCardActivity.this, mineTryFragment.getClass());
                                    }else if (item.activeType==4){//幸运转盘
                                        ARouter.getInstance().build(ARouters.PATH_MY_BIG_WHELL).navigation();
                                    }else if (item.activeType==5){//神奇宝箱
                                        ARouter.getInstance().build(ARouters.PATH_MY_TREASURE_BOX).navigation();
                                    }else if (item.activeType==6){//0元抢购
                                        ARouter.getInstance().build(ARouters.PATH_MY_ZERO_BUY).navigation();
                                    }else if (item.activeType==8){//红包活动
                                        ARouter.getInstance().build(ARouters.PATH_REDPACKAGE).navigation();
                                    }else if (item.activeType==9){//淘宝福利
                                        ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET).navigation();
                                    }
                                }
                            }
                        } );
                        RelativeLayout relativeLayout = helper.getView( R.id.tv_card_more );
                        relativeLayout.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build(ARouters.PATH_MY_CARD_MORE)
                                        .withString( "typeId",item.typeId ).withString( "name",item.name ).navigation();
                            }
                        } );
                    } catch (Exception e) {

                    }
                }
            };

            mRvRecord.setHasFixedSize(true);
            mRvRecord.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MyCardActivity.this, 1);
            mRvRecord.setLayoutManager(gridLayoutManager);
            /*mBaseProductQuickAdapter.setOnItemClickListener( new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    LogUtils.w( "tag1","我進來了!!!!" );
                }
            } );*/
//            mRvRecord.setNestedScrollingEnabled(false);
            mRvRecord.setAdapter(mBaseProductQuickAdapter);
            mRvRecord.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(MyCardActivity.this)
                            .margin(5, 5)
                            .size(10)
            ));
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    //弹出提示
    private void showPopupWindow(String title) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mycard, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
//        iv_card_img
//        mIvColour = (ImageView) contentView.findViewById( R.id.iv_card_img );
        mIvDiss = (ImageView) contentView.findViewById( R.id.iv_popwindow_diss );
        mTvContentOne = (TextView) contentView.findViewById ( R.id.tv_popwindow_content );
//        mTvContentTwo= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        mTvContentTwo= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
//        tvPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_title );
//        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
//        tvPopContent2= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopContent3 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content3 );
//        tvPopTitle.setText ( title );
//        tvPopContent1.setText ( content1 );
//        tvPopContent2.setText ( content2 );
//        tvPopContent3.setText ( content3 );
        mTvContentOne.setText( title );
        mIvDiss.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getAllData();
        if (refresh){
        mSmartRecord.autoRefresh(500);
        refresh=false;
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        /*page++;
        getAllData();*/
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mSmartRecord.setNoMoreData(false);
        getAllData();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartRecord.finishRefresh(state);
        } else if (noMoreData) {
            mSmartRecord.finishLoadMoreWithNoMoreData();
        } else {
            mSmartRecord.finishLoadMore(state);
        }
    }
}
