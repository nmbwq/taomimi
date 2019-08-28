package java.com.lechuang.module.adress;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.adress.SelectCityUtils.utils.VerticalImageSpan;
import java.com.lechuang.module.bean.AddListBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_ADRESSLIST)
public class AdressListActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {


    private boolean mIsSuccess = false;//默认进来是管理
    private SmartRefreshLayout smartAddressList;
    private RecyclerView mRvShouCang;
    private LinearLayout mNetError;
    private int page = 1;
    private TextView tvPopKnowFinish, tvPopKnowSure;
    private PopupWindow mPopupWindow;
    private TextView tv_addadress, tv_common_click_trys;
    //true来自我的页面    点击item不消失
    boolean isfromMe = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_addresslist;
    }

    @Override
    protected void findViews() {
        isfromMe = getIntent().getBooleanExtra( "isfromMe", false );
        smartAddressList = $( R.id.smart_addresslist );
        mRvShouCang = $( R.id.rv_shoucang );
        tv_addadress = $( R.id.tv_addadress );
        tv_common_click_trys = $( R.id.tv_common_click_trys );

        mNetError = $( R.id.ll_net_error );
        ((ImageView) $( R.id.iv_common_image )).setImageDrawable( getResources().getDrawable( R.drawable.rc_modul_kong ) );
        ((TextView) $( R.id.tv_common_click_try )).setText( "你还没有收货地址哦~添加一个吧!" );

        $( R.id.iv_common_back ).setOnClickListener( this );
        tv_addadress.setOnClickListener( this );
        tv_common_click_trys.setOnClickListener( this );
    }

    @Override
    protected void initView() {
        ((TextView) $( R.id.iv_common_title )).setText( "选择收货地址" );
        smartAddressList.setOnRefreshLoadMoreListener( this );
        smartAddressList.setEnableLoadMore( false );
    }

    @Override
    protected void getData() {
        smartAddressList.autoRefresh( 100 );
    }

    /**
     * 获取数据
     */
    private void getShouCangData() {
        Map<String, Object> allParam = new HashMap<>();
        NetWork.getInstance()
                .setTag( Qurl.adressList )
                .getApiService( ModuleApi.class )
                .adressList( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<AddListBean>( this, true, false ) {
                    @Override
                    public void onSuccess(AddListBean result) {
                        if (result == null || result.getAddressList() == null || result.getAddressList().size() <= 0) {
                            mNetError.setVisibility( View.VISIBLE );
                            if (page == 1 && mShouCangBeans != null && mBaseQuickAdapter != null) {
                                mShouCangBeans.clear();
                                mBaseQuickAdapter.notifyDataSetChanged();
                            }
                            if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                                mNetError.setVisibility( View.VISIBLE );
                            }
                            setRefreshLoadMoreState( true, true );
                            return;
                        }
                        if (result.getAddressList().size() > 0) {
                            mNetError.setVisibility( View.GONE );
                        } else {
                            mNetError.setVisibility( View.VISIBLE );
                        }
                        setRefreshLoadMoreState( true, false );
//                        设置适配器
                        setShouCangData( result.getAddressList() );
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed( errorCode, moreInfo );
                        if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                            mNetError.setVisibility( View.VISIBLE );
                        }
                        setRefreshLoadMoreState( false, false );

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError( e );
                        if (mShouCangBeans == null || mShouCangBeans.size() <= 0 || mShouCangBeans == null) {
                            mNetError.setVisibility( View.VISIBLE );
                        }
                        setRefreshLoadMoreState( false, false );
                    }
                } );
    }

    /**
     * 设置收藏的数据
     * isUpdata  强制更新
     */
    private List<AddListBean.AddressListBean> mShouCangBeans;
    private BaseQuickAdapter<AddListBean.AddressListBean, BaseViewHolder> mBaseQuickAdapter;

    private void setShouCangData(List<AddListBean.AddressListBean> addressList) {
        Log.d( "Debug", "传过来的数量个数为" + addressList.size() );
        mBaseQuickAdapter = new BaseQuickAdapter<AddListBean.AddressListBean, BaseViewHolder>( R.layout.item_adress, addressList ) {
            @Override
            protected void convert(BaseViewHolder helper, final AddListBean.AddressListBean item) {
                helper.setText( R.id.tv_name, item.getReceiverName() + "" );
                helper.setText( R.id.tv_phone, item.getReceiverPhone() + "" );
                TextView tv_adress = (TextView)helper.getView( R.id.tv_adress );

//                    是否默认地址 0 否 1 默认
                if (item.getIsDefault() == 0) {
                    tv_adress.setText(  item.getAreaAddress() + item.getDetailAddress() + "");
                } else {
                    //第一个空白是 放图片的   第二个空白是 文字与图片之间的距离
                    SpannableString spannableString = new SpannableString(" " + "  "+item.getAreaAddress() + item.getDetailAddress());
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.group);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    spannableString.setSpan(new VerticalImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_adress.setText(spannableString);
                }
                TextView tv_update = (TextView) helper.getView( R.id.tv_update );
                //点击编辑按钮跳转操作
                tv_update.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent( AdressListActivity.this, AddandUpdateActivity.class );
                        intent.putExtra( "isAdd", false );
                        intent.putExtra( "adressBean", item );
                        startActivity( intent );
                    }
                } );
                helper.getConvertView().setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isfromMe == false) {
                            //数据是使用Intent返回
                            Intent intent = new Intent();
                            //把返回数据存入Intent
                            intent.putExtra( "resultBean", item );
                            //设置返回数据
                            AdressListActivity.this.setResult( 10001, intent );
                            //关闭Activity
                            AdressListActivity.this.finish();
                        }

                    }
                } );
            }
        };
        mRvShouCang.setHasFixedSize( true );
        mRvShouCang.setNestedScrollingEnabled( false );
        GridLayoutManager gridLayoutManager = new GridLayoutManager( this, 1 );
        mRvShouCang.setLayoutManager( gridLayoutManager );
        mRvShouCang.setAdapter( mBaseQuickAdapter );
    }


    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            smartAddressList.finishRefresh( state );
        } else if (noMoreData) {
            smartAddressList.finishLoadMoreWithNoMoreData();
        } else {
            smartAddressList.finishLoadMore( state );
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.tv_addadress) {
            Intent intent = new Intent( AdressListActivity.this, AddandUpdateActivity.class );
            intent.putExtra( "isAdd", true );
            startActivity( intent );
        } else if (id == R.id.tv_common_click_trys) {

            Intent intent = new Intent( AdressListActivity.this, AddandUpdateActivity.class );
            intent.putExtra( "isAdd", true );
            startActivity( intent );
        }
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        page++;
        getShouCangData();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        smartAddressList.setNoMoreData( false );
        page = 1;
        getShouCangData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        smartAddressList.autoRefresh( 100 );
    }
}
