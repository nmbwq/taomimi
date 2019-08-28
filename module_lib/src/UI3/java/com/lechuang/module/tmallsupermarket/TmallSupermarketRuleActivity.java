package java.com.lechuang.module.tmallsupermarket;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.TryRuleBean;
import java.com.lechuang.module.zerobuy.MyZeroBuyActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_TMALL_SUPERMARKET_RULE)
public class TmallSupermarketRuleActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {
    private TextView mTvContent;
    private SmartRefreshLayout mSmartRefreshLayout;
    private PopupWindow mPopupWindow;
    private LinearLayout mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mIvPopFinish;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tmallsupermarket_rule;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("说明");
        $(R.id.iv_common_back).setOnClickListener(this);
//        $(R.id.tv_friends).setOnClickListener(this);
        mTvContent = $(R.id.tv_content);
        mSmartRefreshLayout = $(R.id.mSmartRefreshLayout);
    }

    @Override
    protected void initView() {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener ( this );
        mSmartRefreshLayout.setEnableLoadMore ( false );
    }

    @Override
    protected void getData() {
        updataAlipay();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_bind_zfb_save){
            updataAlipay();
        }else if (id== R.id.iv_common_back){
            finish();
        }else if (id==R.id.tv_friends){
            sharePopWindow();
        }else if (id==R.id.iv_finish){
            mPopupWindow.dismiss();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }else if (id==R.id.ll_sharepengyou){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
//            addShare(image,SHARE_MEDIA.WEIXIN_CIRCLE);
        }else if (id==R.id.ll_sharehaoyou){
            ShareUtils.umShare(this, SHARE_MEDIA.QQ, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }else if (id==R.id.ll_sharekongjian){
            ShareUtils.umShare(this, SHARE_MEDIA.QZONE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });
        }
    }

    private void updataAlipay() {
        NetWork.getInstance()
                .setTag( Qurl.tmallSuperMarketRule)
                .getApiService(ModuleApi.class)
                .tmallSuperMarketRule()
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TryRuleBean> (TmallSupermarketRuleActivity.this,true,false) {

                    @Override
                    public void onSuccess(TryRuleBean result) {
                        if (result==null){
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        mTvContent.setText( result.explain );
                    }
                });
    }
    //分享弹框
    private void sharePopWindow(){
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_share_goods, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian
        mLlPopweixin = (LinearLayout) contentView.findViewById ( R.id.ll_shareweixin );
        mLlPoppengyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharepengyou );
        mLlPophaoyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharehaoyou );
        mLlPopkongjian = (LinearLayout) contentView.findViewById ( R.id.ll_sharekongjian );
        mIvPopFinish = (ImageView) contentView.findViewById ( R.id.iv_finish );
        mLlPopweixin.setOnClickListener( this );
        mLlPoppengyou.setOnClickListener( this );
        mLlPophaoyou.setOnClickListener( this );
        mLlPopkongjian.setOnClickListener( this );
        mIvPopFinish.setOnClickListener( this );

        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        updataAlipay();
    }
    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        mSmartRefreshLayout.finishRefresh ( state );

    }
}
