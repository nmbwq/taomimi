package java.com.lechuang.module.upgrade;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.LogUtils;
import com.common.app.utils.OnClickEvent;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FlashSaleDetailsBean;
import java.com.lechuang.module.bean.FlashSaleIdBean;
import java.com.lechuang.module.flashsale.FlashSaleActivity;
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
@Route(path = ARouters.PATH_UP_GRADE_DETAILS_F)
public class UpGradeDetailsFragment extends LazyBaseFragment{

    private TextView mTvPrice;
    private RelativeLayout tvPopKnow,mRlBuy;
    private ImageView mIvShort;
//    private ImageView mIvLong;
    private WebView mIvLong;
    //    @Autowired(name = "type")
//    public String ll;
    @Autowired
    public String img;//带名字图片
    @Autowired
    public String detailImg;//详情图片
    @Autowired
    public String price;//价格
    @Autowired
    public String id;//标签
    public String variety = "2";
    private boolean onShow = false;
    private PopupWindow mPopupWindow;


    @Override
    protected int getLayoutId() {

        return R.layout.fragment_up_grade_details;
    }

    @Override
    protected void findViews() {
        ARouter.getInstance().inject(this);
//        if (!EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().register( this );
//        }
        mTvPrice = mInflate.findViewById( R.id.tv_price );
        mIvShort = mInflate.findViewById( R.id.iv_shore_img );
        mIvLong = mInflate.findViewById( R.id.iv_long_img );
        mRlBuy = mInflate.findViewById( R.id.rl_lijigoiumai );
        mRlBuy.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                ARouter.getInstance().build(ARouters.PATH_MY_ORDER).withString("id", id).navigation();
            }
        });
//        ((ImageView) $( R.id.iv_common_image )).setImageDrawable( getResources().getDrawable( R.drawable.iv_common_flashsale ) );
//        ((TextView) $( R.id.tv_common_click_try )).setText( "都被抢光了,下次早点来哦~" );
//        ((TextView) $( R.id.tv_flash_sale )).setVisibility( View.VISIBLE );
//        ((TextView) $( R.id.tv_flash_sale )).setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpGradeDetailsActivity.flashSaleActivity.finish(  );
//            }
//        } );
    }

    @Override
    protected void initView() {
        mTvPrice.setText(""+price);
        Glide.with(BaseApplication.getApplication()).load(img).into(mIvShort);
//        Glide.with(BaseApplication.getApplication()).load(detailImg).into(mIvLong);
        mIvLong.getSettings().setSupportZoom(true);//缩放
        mIvLong.getSettings().setBuiltInZoomControls(true);
        mIvLong.getSettings().setDisplayZoomControls(false);//不显示控制器
        mIvLong.getSettings().setUseWideViewPort(true);
        mIvLong.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mIvLong.getSettings().setLoadWithOverviewMode(true);
        mIvLong.loadUrl(detailImg);


//        Glide.with(BaseApplication.getApplication()).load("http://img.taoyouji666.com/cc5eeff020f948ea864c2428b572131c").into(mIvLong);
//        int i = fenSiActivity;
    }

    @Override
    protected void getData() {
    }

    private Context context;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
        this.context = getActivity();
    }


    @Override
    public void onAttach(Activity Activity) {
        super.onAttach( Activity );
        this.context = Activity;
    }

    //弹出提示
    private void showPopupWindow(){
        View contentView = LayoutInflater.from ( getContext() ).inflate ( R.layout.popupwind_flash_sale_know, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnow = (RelativeLayout) contentView.findViewById ( R.id.tv_popwindow_know );
        tvPopKnow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint( isVisibleToUser );
//        if (isVisibleToUser) {
//            if (onShow) {
//            }
//            onShow = true;
//            if (variety.equals( "0" )) {
//                EventBus.getDefault().post( Constant.SENDDATAO );
//            } else if (variety.equals( "1" )) {
//                EventBus.getDefault().post( Constant.SENDDATAONE );
//            } else {
//                EventBus.getDefault().post( Constant.SENDDATA );
//            }
//        }
//    }



    /*@Override
    public void onPause() {
        super.onPause ();
        EventBus.getDefault().unregister(this);
    }*/

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void changeNav(String message) {
//        if (!onShow) {
//            if (message.equalsIgnoreCase( Constant.SENDDATA2 )) {
//                //当前界面如果显示，就立即刷新，否则滑动显示是刷新
//                if (getUserVisibleHint()) {
//                    variety = "";
//                } else {
//                    this.mIsFirstVisible = true;
//                }
//
//            } else if (message.equalsIgnoreCase( Constant.SENDDATAO0 )) {
//                //当前界面如果显示，就立即刷新，否则滑动显示是刷新
//                if (getUserVisibleHint()) {
//                    variety = "0";
//                } else {
//                    this.mIsFirstVisible = true;
//                }
//
//            } else if (message.equalsIgnoreCase( Constant.SENDDATAONE1 )) {
//                //当前界面如果显示，就立即刷新，否则滑动显示是刷新
//                if (getUserVisibleHint()) {
//                    variety = "1";
//                } else {
//                    this.mIsFirstVisible = true;
//                }
//
//            }
//        }
//    }
}
