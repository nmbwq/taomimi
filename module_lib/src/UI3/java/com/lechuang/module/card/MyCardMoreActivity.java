package java.com.lechuang.module.card;


import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
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
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.LogUtils;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.TimeTools;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.w3c.dom.Text;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.EarningsRecordBean;
import java.com.lechuang.module.bean.MyCardMoreBean;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

@Route(path = ARouters.PATH_MY_CARD_MORE)
public class MyCardMoreActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener{
    private SmartRefreshLayout mSmartRecord;
    private RecyclerView mRvRecord;
    private int page = 1;//页数
    private boolean mload = true;
    private PopupWindow mPopupWindow;
    private ImageView mIvColour;
    private TextView mTvContentOne,mTvContentTwo;
    private LinearLayout mNetError;
    @Autowired
    public String typeId;//是啥？
    @Autowired
    public String name;//是啥？


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mycard;
    }

    @Override
    protected void findViews() {
        mRvRecord=$(R.id.rv_recyclerView);
        mSmartRecord=$(R.id.smart_record);
        mNetError=$(R.id.ll_net_error);
        ((TextView) $(R.id.tv_common_right)).setVisibility( View.GONE );
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_mycard_exchange).setVisibility( View.GONE );
//        mEtAlipayNumber = $(R.id.et_bind_alipayNumber);
//        mEtAlipayRealName = $(R.id.et_bind_alipayRealName);
//        mTvSave = $(R.id.et_bind_zfb_save);
//        mTvSave.setOnClickListener ( this );
//        ((TextView) $(R.id.tv_common_right)).setVisibility( View.VISIBLE );
        ((TextView) $(R.id.tv_common_right)).setOnClickListener ( this );
//        ((ImageView) $(R.id.iv_common_image)).setBackground( getResources().getDrawable( R.drawable. ) );

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mSmartRecord.setOnRefreshLoadMoreListener(this);
        mSmartRecord.setEnableLoadMore ( true );
        ((TextView) $(R.id.iv_common_title)).setText(name);
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
        }
    }

    private void getAllData() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("typeId", typeId);
        allParam.put("page", page);

        NetWork.getInstance()
                .setTag(Qurl.myCardMore)
                .getApiService(ModuleApi.class)
                .myCardMore(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardMoreBean>(MyCardMoreActivity.this, true, false) {

                    @Override
                    public void onSuccess(MyCardMoreBean result) {
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
//                        mNetError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        setRefreshLoadMoreState(false, false);
//                        mNetError.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 设置底部商品数据
     *
     * @param productList
     */
    private List<MyCardMoreBean.ListBean> mProductList;
    private BaseQuickAdapter<MyCardMoreBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;
    private void setProductData(List<MyCardMoreBean.ListBean> productList) {
        if (productList == null || productList.size() == 0) {
            if (mload) {
//                mNetError.setVisibility(View.VISIBLE);
//                mRvRecord.setVisibility(View.GONE);
            }
            if (page == 1) {
                mProductList.clear();
                mBaseProductQuickAdapter.notifyDataSetChanged();
//            mNetError.setVisibility(View.GONE);
//            mRvRecord.setVisibility(View.VISIBLE);
            }
            mload = true;
            return;
        } else {
//            mNetError.setVisibility(View.GONE);
//            mRvRecord.setVisibility(View.VISIBLE);
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
            mBaseProductQuickAdapter = new BaseQuickAdapter<MyCardMoreBean.ListBean, BaseViewHolder>
                    (R.layout.item_mycard_more_list, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, final MyCardMoreBean.ListBean item) {
                    try {
                        //背景色rl_all_colour
                        ImageView relativeLayout = helper.getView(R.id.rl_all_colour);
                        Glide.with( BaseApplication.getApplication()).load(item.img).centerCrop().placeholder(R.drawable.bg_mycard_img_null).into(relativeLayout);

                        //卡名 tv_card_name
                        if (item.name!=null){
                            helper.setText(R.id.tv_card_name,  item.name);
                        }

                        //时间 tv_card_num
                        if (item.startTime!=null&&item.endTime!=null){
                            helper.setText(R.id.tv_card_num,item.startTime+ "-" + item.endTime);
                        }

                        //是否快到时间
                        final TextView toGoUse=helper.getView( R.id.tv_used );
                        if (item.closeEnd==null){
                        }else if (item.closeEnd.equals( "1" )){
                            //倒计时 tv_card_directions
                            final CountDownTextView countDownTextView=helper.getView(R.id.tv_card_time);
                            countDownTextView.setNormalText( "仅剩 23:59:59" ).setShowFormatTime( true ).setCountDownText( "仅剩","" )
                                    .setCloseKeepCountDown( false ).setOnCountDownFinishListener( new CountDownTextView.OnCountDownFinishListener() {
                                @Override
                                public void onFinish() {
                                countDownTextView.setText( "仅剩 00:00:00" );
                                mSmartRecord.autoRefresh(500);
//                                mSmartRecord.setNoMoreData(false);
//                                getAllData();
//                                toGoUse.setOnClickListener( new OnClickEvent() {
//                                    @Override
//                                    public void singleClick(View v) {
//                                        toast( "已过期" );
//                                    }
//                                } );
                                }
                            } );
                            countDownTextView.setVisibility( View.VISIBLE );
                            countDownTextView.startCountDown( Long.parseLong( item.endTimestamp )/1000 );
                        }

                        toGoUse.setOnClickListener( new OnClickEvent() {
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
                                        FragmentActivity.start(MyCardMoreActivity.this, mineTryFragment.getClass());
                                    }else if (item.activeType==3){//早起打卡
                                        ARouter.getInstance().build(ARouters.PATH_SIGN_IN).navigation();
                                    }else if (item.activeType==7){//拉新榜单
                                        Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_LAXIN).navigation();
                                        FragmentActivity.start(MyCardMoreActivity.this, mineTryFragment.getClass());
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

                        //是否显示快过期图片iv_mycard_expiring
                        if (item.closeEnd!=null){
                            ImageView imageView=helper.getView( R.id.iv_mycard_expiring );
                            if (item.closeEnd.equals( "0" )){
                                imageView.setVisibility( View.GONE );
                            }else if (item.closeEnd.equals( "1" )){
                                imageView.setVisibility( View.VISIBLE );
                            }
//                            helper.getView( R.id.iv_mycard_expiring ).setVisibility( item.closeEnd.equals( "1" )?View.VISIBLE:View.GONE );
                        }
                    } catch (Exception e) {

                    }
                }
            };

            mRvRecord.setHasFixedSize(true);
            mRvRecord.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MyCardMoreActivity.this, 1);
            mRvRecord.setLayoutManager(gridLayoutManager);

            mRvRecord.setNestedScrollingEnabled(false);
            mRvRecord.setAdapter(mBaseProductQuickAdapter);
            mRvRecord.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(MyCardMoreActivity.this)
                            .margin(5, 5)
                            .size(10)
            ));

        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }


    //弹出提示
    private void showPopupWindow(String title,String colour) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mycard, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
//        iv_card_img
//        mIvColour = (ImageView) contentView.findViewById( R.id.iv_card_img );
        mTvContentOne = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
        mTvContentTwo= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
//        tvPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_title );
//        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
//        tvPopContent2= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopContent3 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content3 );
//        tvPopTitle.setText ( title );
//        tvPopContent1.setText ( content1 );
//        tvPopContent2.setText ( content2 );
//        tvPopContent3.setText ( content3 );
//        tvPopKnow.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
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
