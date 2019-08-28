package java.com.lechuang.module.fensi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FenSiBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */
@Route(path = ARouters.PATH_FENSI_F)
public class FenSiFragment extends LazyBaseFragment implements OnRefreshLoadMoreListener {

    private SmartRefreshLayout mSmartFenSi;
    public int page = 1;//页数
    private RecyclerView mRvFenSi;
    private boolean mload = true,mloadt=true;
    private TextView mTvVip,mTvCommon;
//    @Autowired(name = "type")
//    public String ll;
    @Autowired
    public String type;
    public String variety="2";
    private LinearLayout mNetError;
    private boolean onShow=false;


    @Override
    protected int getLayoutId() {

        return R.layout.fragment_fensi;
    }

    @Override
    protected void findViews() {
        EventBus.getDefault().register(this);
        mSmartFenSi = mInflate.findViewById ( R.id.smart_fensi );
        mRvFenSi = mInflate.findViewById ( R.id.rv_fensi );
        mTvVip = mInflate.findViewById ( R.id.tv_vip_number );
        mTvCommon = mInflate.findViewById ( R.id.tv_common_number );
        mNetError = $ ( R.id.ll_net_error );
        ((ImageView)$( R.id.iv_common_image )).setImageDrawable (getResources ().getDrawable ( R.drawable.iv_common_fans ) );
        ((TextView)$(R.id.tv_common_click_try)).setText ( "还没有粉丝呢,继续加油咯!" );
    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        mSmartFenSi.setOnRefreshLoadMoreListener ( this );
//        int i = fenSiActivity;
    }

    @Override
    protected void getData() {
        //设置自动加载数据
        /*if (onShow){
            mSmartFenSi.autoRefresh ( 500 );
            onShow=false;
        }*/
        mSmartFenSi.autoRefresh ( 500 );
    }
    private Context context;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated ( savedInstanceState );
        this.context = getActivity ();
    }


    @Override
    public void onAttach(Activity Activity) {
        super.onAttach ( Activity );
        this.context=Activity;
    }

    /**
     * 获取刷新数据
     */
    public void getAllData() {
        Map<String, String> allParam = new HashMap<> ();
        String pages = page+"";
        allParam.put ( "page",pages );
        /*if (!TextUtils.isEmpty ( type )){
            allParam.put ( "type",type );
        }*/
        if (!variety.equals ( "2" )){
            allParam.put ( "variety",variety );
        }

        NetWork.getInstance ()
                .setTag ( Qurl.MyTeam )
                .getApiService ( ModuleApi.class )
                .myTeam ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<FenSiBean> ( context==null?getActivity ():context
                        , false, true ) {

                    @Override
                    public void onSuccess(FenSiBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        result.getFollowerList ();
                        setFansData(result.getFollowerList ());
                        //超级会员数量
                        if (result.getSuperAgentCount ()>0){
                            mTvVip.setText ( "超级会员："+result.getSuperAgentCount ()+"位" );
                        }else {
                            mTvVip.setText ( "超级会员：0位" );
                        }

                        //普通会员数量
                        if (result.getAgentCount ()>0){
                            mTvCommon.setText ( "普通会员："+result.getAgentCount ()+"位" );
                        }else{
                            mTvCommon.setText ( "普通会员：0位" );
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
        if (fansList == null || fansList.size () <= 0){
        if (mload){
            mRvFenSi.setVisibility ( View.GONE );
            mNetError.setVisibility ( View.VISIBLE );
            }
            mload = true;
            mloadt = true;
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
            mBaseProductQuickAdapter = new BaseQuickAdapter<FenSiBean.FollowerListBean, BaseViewHolder>(R.layout.item_fans_list, mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, FenSiBean.FollowerListBean item) {
                    try {
                        //会员头像
                        ImageView ivItemAllFenLei = helper.getView(R.id.iv_item_all_product_tupian);
                        Glide.with(BaseApplication.getApplication())
                                .load(item.photo)
                                .placeholder(R.drawable.ic_common_user_def)
                                .transform(new GlideRoundTransform (BaseApplication.getApplication(), 100))
                                .into(ivItemAllFenLei);

                        //会员名字
                        TextView name = helper.getView(R.id.tv_item_fans_username);
                        int numb=item.nickName.length ();
                        if (numb>9){
                            name.setText(TextUtils.isEmpty(item.nickName) ? item.phone : item.nickName.substring ( 0,10 )+"...");//用户昵称(昵称为空，显示手机号)
                        }else{
                            name.setText(TextUtils.isEmpty(item.nickName) ? item.phone : item.nickName);//用户昵称(昵称为空，显示手机号)
                        }

                        //会员vip
                        ImageView ivFansVip = helper.getView(R.id.iv_item_fans_vip);
                        if (item.agencyLevel==1){
                            ivFansVip.setBackground ( getResources ().getDrawable ( R.drawable.iv_mine_vip ) );
                        }else{
                            ivFansVip.setBackground ( getResources ().getDrawable ( R.drawable.img_fans_common ) );
                        }

                        //手机号
                        helper.setText(R.id.tv_item_fans_phone, item.phone.substring ( 0,3 )+"****"+item.phone.substring ( 7,11 ));

                        //日期
                        helper.setText(R.id.tv_item_data,  item.createTimeStr + "");

                        final int userId= item.userId;

                        //推荐人数
                        final TextView fansPerson = helper.getView(R.id.item_fans_person);
                        LinearLayout fansCenterPerson = helper.getView ( R.id.ll_fans_person );
                        if (item.nextAgentCount>0){
                            fansCenterPerson.setVisibility ( View.VISIBLE );
                        }else{
                            fansCenterPerson.setVisibility ( View.GONE );
                        }
                        fansPerson.setText ( "推荐"+item.nextAgentCount+"人" );
                        fansCenterPerson.setOnClickListener ( new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                //跳转新页面
                                ARouter.getInstance().build(ARouters.PATH_MYFENSI).withString ( "userId",userId+"" ).navigation();
                            }
                        } );


                    } catch (Exception e) {

                    }
                }
            };

            mRvFenSi.setHasFixedSize(true);
            mRvFenSi.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            mRvFenSi.setLayoutManager(gridLayoutManager);


            mRvFenSi.setAdapter(mBaseProductQuickAdapter);
            /*mRvFenSi.addItemDecoration(new GridItemDecoration (
                    new GridItemDecoration.Builder(getActivity())
                            .margin(5, 5)
                            .size(10)
            ));*/
        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取底部的商品
     */
    private void getProductList() {
        Bundle bundle = getArguments();
        Map<String, String> allParam = new HashMap<> ();
        String pages = page+"";
        allParam.put ( "page",pages );
        if (!TextUtils.isEmpty ( type )){
            allParam.put ( "type",type );
        }
        if (!variety.equals ( "2" )){
            allParam.put ( "variety",variety );
        }

        NetWork.getInstance ()
                .setTag ( Qurl.MyTeam )
                .getApiService ( ModuleApi.class )
                .myTeam ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<FenSiBean> ( getActivity (), false, true ) {

                    @Override
                    public void onSuccess(FenSiBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        setFansData(result.getFollowerList ());
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                    }
                } );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mload = false;
        mloadt = false;
        //判断用于首次没有加载出来数据时，刷新整体数据
        if (mBaseProductQuickAdapter != null) {
            page++;
            getProductList ();
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
            if (!state) {
                //第一次加载失败时，再次显示时可以重新加载
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartFenSi.finishLoadMoreWithNoMoreData ();
        } else {
            mSmartFenSi.finishLoadMore ( state );
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint ( isVisibleToUser );
        if (isVisibleToUser){
            if (onShow){
                mSmartFenSi.autoRefresh ( 500 );
            }
            onShow=true;
            if (variety.equals ( "0" )){
                EventBus.getDefault ().post ( Constant.SENDDATAO );
            }else if (variety.equals ( "1" )){
                EventBus.getDefault ().post ( Constant.SENDDATAONE );
            }else{
                EventBus.getDefault ().post ( Constant.SENDDATA );
            }
        }
    }



    @Override
    public void onPause() {
        super.onPause ();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (!onShow){
        if (message.equalsIgnoreCase( Constant.SENDDATA2)) {
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            if (getUserVisibleHint()) {
                variety = "";
                mSmartFenSi.autoRefresh(100);
            } else {
                this.mIsFirstVisible = true;
            }

        }else if (message.equalsIgnoreCase( Constant.SENDDATAO0)) {
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            if (getUserVisibleHint()) {
                variety = "0";
                mSmartFenSi.autoRefresh(100);
            } else {
                this.mIsFirstVisible = true;
            }

        }else if (message.equalsIgnoreCase( Constant.SENDDATAONE1)) {
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            if (getUserVisibleHint()) {
                variety = "1";
                mSmartFenSi.autoRefresh(100);
            } else {
                this.mIsFirstVisible = true;
            }

        }
        }
    }
}
