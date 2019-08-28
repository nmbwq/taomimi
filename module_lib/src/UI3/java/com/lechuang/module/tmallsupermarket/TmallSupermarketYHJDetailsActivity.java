package java.com.lechuang.module.tmallsupermarket;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.OnClickEvent;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FenSiBean;
import java.com.lechuang.module.bean.TmallSupermarketListBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_TMALL_SUPERMARKET_YHJ)
public class TmallSupermarketYHJDetailsActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartFenSi;
    private int page = 1;//页数
    private RecyclerView mRvFenSi;
    private boolean mload = true;
    @Autowired()
    public int couponTypeId;
    @Autowired()
    public int appTypeId;
    @Autowired()
    public String name;
    private LinearLayout mNetError;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tmallsupermarket_yhj;
    }

    @Override
    protected void findViews() {
        mSmartFenSi = $ ( R.id.smart_fensi );
        mRvFenSi = $ ( R.id.rv_fensi );
        ((TextView) $(R.id.iv_common_title)).setText("好券专区");
        $(R.id.iv_common_back).setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_tmallsupermarket_details ) );
        ((LinearLayout)$( R.id.ll_net_error )).setBackgroundColor (getResources ().getColor ( R.color.c_F6F6F6 ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "暂无优惠券, 去别处看看吧" );

    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        ((TextView) $(R.id.tv_name)).setText(name);
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
        allParam.put ( "couponTypeId", couponTypeId );
        allParam.put ( "appTypeId", appTypeId );
        allParam.put ( "page", page+"" );
        NetWork.getInstance ()
                .setTag ( Qurl.tmallSuperMarketTmallList )
                .getApiService ( ModuleApi.class )
                .tmallSuperMarketTmallList ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<TmallSupermarketListBean> ( TmallSupermarketYHJDetailsActivity.this, true, true ) {

                    @Override
                    public void onSuccess(TmallSupermarketListBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setFansData ( result.list );
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

    private List<TmallSupermarketListBean.ListBean> mProductList;
    private BaseQuickAdapter<TmallSupermarketListBean.ListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setFansData(List<TmallSupermarketListBean.ListBean> fansList) {
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
            mBaseProductQuickAdapter = new BaseQuickAdapter<TmallSupermarketListBean.ListBean, BaseViewHolder> ( R.layout.item_tmallsupermarket_yhj_list, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, final TmallSupermarketListBean.ListBean item) {
                    try {
                        //图片 iv_item_all_product_tupian 标题 tv_item_tmallsupermarket_biaoti 标注 tv_item_tmallsupermarket_biaozhu
                        //劵 tv_item_all_product_quan_txt 劵金额 tv_item_all_product_quan
                        //屏蔽不显示 旗舰店 tv_qijiandian 外卖是否显示 ll_maiwai_all 外卖图标 iv_tubiao 外卖名字 tv_name
                        // 立即领取/购买 tv_item_canjia
                        //会员头像
                        ImageView ivItemAllFenLei = helper.getView ( R.id.iv_item_all_product_tupian );
                        Glide.with(BaseApplication.getApplication())
                                .load(item.img)
                                .placeholder(R.drawable.ic_common_user_def)
                                .transform(new GlideRoundTransform (BaseApplication.getApplication(), 0))
                                .into(ivItemAllFenLei);

                        //标题
                        helper.setText( R.id.tv_item_tmallsupermarket_biaoti,item.title );
                        //文字说明
                        helper.setText( R.id.tv_item_tmallsupermarket_biaozhu,item.explains );
                        //劵金额 不在显示
//                        helper.setText( R.id.tv_item_all_product_quan,"￥"+item.couponMoney );
                        //立即领取
                        TextView lingqu=helper.getView( R.id.tv_item_canjia );
                        lingqu.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                        .withString("loadUrl", item.link)
                                        .withString( Constant.TITLE, item.title)
//                                        .withInt(Constant.TYPE, 4)
                                        .navigation();
                            }
                        } );
                    } catch (Exception e) {

                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( TmallSupermarketYHJDetailsActivity.this, 1 );
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
