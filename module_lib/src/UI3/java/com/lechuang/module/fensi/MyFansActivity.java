package java.com.lechuang.module.fensi;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.Utils;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FenSiBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MYFENSI)
public class MyFansActivity extends BaseActivity implements OnRefreshLoadMoreListener,View.OnClickListener {
    private SmartRefreshLayout mSmartFenSi;
    private int page = 1;//页数
    private RecyclerView mRvFenSi;
    private boolean mload = true;
    private TextView mTvVipNumber,mTvCommonNumber;
    @Autowired()
    public String userId;
    private LinearLayout mNetError;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_fans;
    }

    @Override
    protected void findViews() {
        mSmartFenSi = $ ( R.id.smart_fensi );
        mRvFenSi = $ ( R.id.rv_fensi );
        mTvVipNumber = $ ( R.id.tv_vip_number );
        mTvCommonNumber = $ ( R.id.tv_common_number );
        ((TextView) $(R.id.iv_common_title)).setText("我的粉丝");
        $(R.id.iv_common_back).setOnClickListener(this);
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_fans ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "还没有粉丝呢,继续加油咯!" );

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
        Map<String, String> allParam = new HashMap<> ();
        allParam.put ( "userId", userId );
        allParam.put ( "page", page+"" );
        NetWork.getInstance ()
                .setTag ( Qurl.MyFansTeam )
                .getApiService ( ModuleApi.class )
                .myFansTeam ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<FenSiBean> ( MyFansActivity.this, true, true ) {

                    @Override
                    public void onSuccess(FenSiBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setFansData ( result.getFollowerList () );
                        //超级会员数量
                        if (result.getSuperAgentCount ()>0){
                            mTvVipNumber.setText ( "超级会员："+result.getSuperAgentCount ()+"位" );
                        }else {
                            mTvVipNumber.setText ( "超级会员：0位" );
                        }

                        //普通会员数量
                        if (result.getAgentCount ()>0){
                            mTvCommonNumber.setText ( "普通会员："+result.getAgentCount ()+"位" );
                        }else{
                            mTvCommonNumber.setText ( "普通会员：0位" );
                        }
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

    private List<FenSiBean.FollowerListBean> mProductList;
    private BaseQuickAdapter<FenSiBean.FollowerListBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setFansData(List<FenSiBean.FollowerListBean> fansList) {
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
            mBaseProductQuickAdapter = new BaseQuickAdapter<FenSiBean.FollowerListBean, BaseViewHolder> ( R.layout.item_fans_list, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, FenSiBean.FollowerListBean item) {
                    try {
                        //会员头像
                        ImageView ivItemAllFenLei = helper.getView ( R.id.iv_item_all_product_tupian );
                        Glide.with(BaseApplication.getApplication())
                                .load(item.photo)
                                .placeholder(R.drawable.ic_common_user_def)
                                .transform(new GlideRoundTransform (BaseApplication.getApplication(), 100))
                                .into(ivItemAllFenLei);

                        //会员名字
                        TextView name = helper.getView(R.id.tv_item_fans_username);
                        int numb=item.nickName.length ();
                        if (numb>9){
                            name.setText( TextUtils.isEmpty(item.nickName) ? item.phone : item.nickName.substring ( 0,10 )+"...");//用户昵称(昵称为空，显示手机号)
                        }else{
                            name.setText(TextUtils.isEmpty(item.nickName) ? item.phone : item.nickName);//用户昵称(昵称为空，显示手机号)
                        }

                        //会员vip
                        ImageView ivFansVip = helper.getView ( R.id.iv_item_fans_vip );
                        if (item.agencyLevel == 1) {
                            ivFansVip.setBackground ( getResources ().getDrawable ( R.drawable.iv_mine_vip ) );
                        } else {
                            ivFansVip.setBackground ( getResources ().getDrawable ( R.drawable.img_fans_common ) );
                        }

                        //手机号
                        helper.setText(R.id.tv_item_fans_phone, item.phone.substring ( 0,3 )+"****"+item.phone.substring ( 7,11 ));

                        //日期
                        helper.setText ( R.id.tv_item_data, item.createTimeStr + "" );

                        final int userId = item.userId;
                    } catch (Exception e) {

                    }
                }
            };

            mRvFenSi.setHasFixedSize ( true );
            mRvFenSi.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( MyFansActivity.this, 1 );
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
