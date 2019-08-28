package java.com.lechuang.module.bigwhell;


import android.content.*;
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

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.Utils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.BigWhellLuckInventoryBean;
import java.com.lechuang.module.bean.FenSiBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_BIG_WHELL_RECORD)
public class BigWhellRecordActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartFenSi;
    private int page = 1;//页数
    private RecyclerView mRvFenSi;
    private boolean mload = true;
    private PopupWindow mPopupWindow;
    private TextView mPopTitle,mPopWeixin,mPopOther;
    private RelativeLayout mPopBtn;
    @Autowired()
    public String userId;
    private LinearLayout mNetError;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bigwhell_record;
    }

    @Override
    protected void findViews() {
        mSmartFenSi = $ ( R.id.smart_fensi );
        mRvFenSi = $ ( R.id.rv_fensi );
        ((TextView) $(R.id.iv_common_title)).setText("奖品列表");
        $(R.id.iv_common_back).setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_bigwhell_record_null ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "无更多奖品" );

    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        mSmartFenSi.setOnRefreshLoadMoreListener ( this );
    }

    @Override
    protected void getData() {
//设置自动加载数据
        mSmartFenSi.autoRefresh ( 500 );
    }

    /**
     * 获取刷新数据
     */
    private void getAllData() {
        Map<String, Object> allParam = new HashMap<> ();
        allParam.put ( "page", page+"" );
        NetWork.getInstance ()
                .setTag ( Qurl.bigWhellLuckInventory )
                .getApiService ( ModuleApi.class )
                .getBigWhellInventory ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<BigWhellLuckInventoryBean> ( BigWhellRecordActivity.this, true, true ) {

                    @Override
                    public void onSuccess(BigWhellLuckInventoryBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setInventory ( result.getLottoWin() );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        if (mload){
                            mNetError.setVisibility ( View.VISIBLE );
                        }
                    }
                } );
    }

    private List<BigWhellLuckInventoryBean.LottoWinBean> mProductList;
    private BaseQuickAdapter<BigWhellLuckInventoryBean.LottoWinBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setInventory(List<BigWhellLuckInventoryBean.LottoWinBean> fansList) {
        if (fansList == null || fansList.size () <= 0) {
            if (mload) {
                mRvFenSi.setVisibility ( View.GONE );
                mNetError.setVisibility ( View.VISIBLE );
            }
            mload = true;
            return;
        } else {
            mRvFenSi.setVisibility ( View.VISIBLE );
            mNetError.setVisibility ( View.GONE );
        }
        mload = true;
        if (mProductList == null) {
            mProductList = new ArrayList<> ();
        }
        if (page == 1) {
            mProductList.clear ();
        }

        mProductList.addAll ( fansList );
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<BigWhellLuckInventoryBean.LottoWinBean, BaseViewHolder> ( R.layout.item_bigwhell_list, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, final BigWhellLuckInventoryBean.LottoWinBean item) {
                    try {
                        //tv_title tv_time tv_btn
                        if (item.winCount>1){
                            helper.setText ( R.id.tv_title, item.awardProduct+"X"+item.winCount);
                        }else {
                            helper.setText ( R.id.tv_title, item.awardProduct+"X1");
                        }
                        helper.setText ( R.id.tv_time, item.acquiredTime  );

                        TextView btn = helper.getView( R.id.tv_btn );
                        if (item.winType==1){
                            btn.setVisibility( View.GONE );
                        }else {
                            btn.setVisibility( View.VISIBLE );
                        }
                        if (item.isChange==0){//未兑换
                            btn.setText( "领取奖励" );
                            btn.setBackground( getResources ().getDrawable ( R.drawable.bg_bigwhell_record_btn_one ) );
                            btn.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    showPopupWindow("恭喜您抽中"+item.awardProduct+"X"+item.winCount,item.wechatNumber);
                                }
                            } );
                        }else if (item.isChange==1){//已兑换
                            btn.setText( "已领取" );
                            btn.setBackground( getResources ().getDrawable ( R.drawable.bg_bigwhell_record_btn_two ) );
                            btn.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    toast( "您已经领取奖励了哦, 不可重复领取" );
                                }
                            } );
                        }else if (item.isChange==2){//已失效
                            btn.setText( "已失效" );
                            btn.setBackground( getResources ().getDrawable ( R.drawable.bg_bigwhell_record_btn_three ) );
                            btn.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    toast( "您的奖品已失效" );
                                }
                            } );
                        }
                    } catch (Exception e) {

                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( BigWhellRecordActivity.this, 1 );
            mRvFenSi.setLayoutManager ( gridLayoutManager );


            mRvFenSi.setAdapter ( mBaseProductQuickAdapter );
            /*mRvFenSi.addItemDecoration ( new GridItemDecoration (
                    new GridItemDecoration.Builder ( MyFansActivity.this )
                            .margin ( 5, 5 )
                            .size ( 10 )
            ) );*/
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }
    }

    //弹出提示
    private void showPopupWindow(String title, final String content) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mybigwhell_zhongjiang, null );
        mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        mPopTitle = (TextView) contentView.findViewById ( R.id.tv_title );
        mPopBtn = (RelativeLayout) contentView.findViewById ( R.id.rl_bigwhell_btn );
        mPopWeixin = (TextView) contentView.findViewById ( R.id.tv_bigwhell_weixin );
        mPopOther = (TextView) contentView.findViewById ( R.id.tv_other_content );
        mPopTitle.setText( title );
        mPopWeixin.setText( content );
        mPopOther.setText( "快去添加客服微信领取奖品吧\n超过3天未领取视为放弃哦~" );
        mPopBtn.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss ();
                ClipData clipData = ClipData.newPlainText("app_inviteCode", content);
                ((ClipboardManager) BigWhellRecordActivity.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
                try {
                    Intent intent = new Intent();
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // TODO: handle exception
                    toast( "检查到您手机没有安装微信，请安装后使用该功能" );
                }
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        if (mBaseProductQuickAdapter != null) {
            page++;
            getAllData ();
        } else {
            page = 1;
            getAllData ();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        mSmartFenSi.setNoMoreData(false);
        getAllData ();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartFenSi.finishRefresh ( state );
        } else if (noMoreData) {
            mSmartFenSi.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartFenSi.finishLoadMore ( state );
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id ==R.id.iv_common_back){
            finish ();
        }
    }
}
