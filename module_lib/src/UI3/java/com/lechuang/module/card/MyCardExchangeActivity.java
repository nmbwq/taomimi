package java.com.lechuang.module.card;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.EarningsRecordBean;
import java.com.lechuang.module.bean.MyCardExchangeBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MY_CARD_EXCHANGE)
public class MyCardExchangeActivity extends BaseActivity implements View.OnClickListener{

    private PopupWindow mPopupWindow;
    private ImageView mIvDiss;
    private TextView mTvContentOne,tvPopKnow,tvPopFinish;
    private EditText mEtKey;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mycard_exchange;
    }

    @Override
    protected void findViews() {
        mEtKey=$(R.id.et_mycard_cdkey);
//        ((TextView) $(R.id.iv_common_title)).setText("觅卡兑换");
//        ((TextView) $(R.id.tv_common_right)).setText("兑换记录");
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        $(R.id.tv_macard_exchange).setOnClickListener(this);
//        mEtAlipayNumber = $(R.id.et_bind_alipayNumber);
//        mEtAlipayRealName = $(R.id.et_bind_alipayRealName);
//        mTvSave = $(R.id.et_bind_zfb_save);
//        mTvSave.setOnClickListener ( this );
//        ((TextView) $(R.id.tv_common_right)).setVisibility( View.VISIBLE );
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void getData() {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_bind_zfb_save){
        }else if (id== R.id.iv_common_back){
            finish();
        }else if (id==R.id.tv_common_right){
            ARouter.getInstance().build(ARouters.PATH_MY_CARD_EXCHANGE_RECORD).navigation();
        }else if (id==R.id.tv_popwindow_know){
            mPopupWindow.dismiss();
        }else if (id==R.id.tv_macard_exchange){
            getAllData();
        }else if (id==R.id.tv_popwindow_finish){
            MyCardActivity.refresh=true;
            mPopupWindow.dismiss();
            finish();
        }
    }

    private void getAllData() {
        String key = mEtKey.getText().toString().trim();
        if (TextUtils.isEmpty(key)){
            hintKeyBoard();
            showPopupWindow("请输入密钥");
            return;
        }
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("secretKey", key);

        NetWork.getInstance()
                .setTag(Qurl.myCardExchange)
                .getApiService(ModuleApi.class)
                .myCardExchange(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardExchangeBean>(MyCardExchangeActivity.this, true, false) {

                    @Override
                    public void onSuccess(MyCardExchangeBean result) {
                        showPopupWindowOK("兑换成功, 快去我的觅卡查看吧!");
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        toast( moreInfo );
//                        showPopupWindow("密钥错误, 请输入正确的CDKEY");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        setRefreshLoadMoreState(false, false);
//                        mNetError.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void hintKeyBoard(){
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if(imm.isActive()&&getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken()!=null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //弹出错误提示
    private void showPopupWindow(String title) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mycard_exchange_know, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
//        iv_card_img
        mIvDiss = (ImageView) contentView.findViewById( R.id.iv_popwindow_diss );
        mTvContentOne = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
//        tvPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_title );
//        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
//        tvPopContent2= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopContent3 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content3 );
//        tvPopTitle.setText ( title );
        mTvContentOne.setText ( title );
//        tvPopContent2.setText ( content2 );
//        tvPopContent3.setText ( content3 );
        tvPopKnow.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
    //弹出成功提示
    private void showPopupWindowOK(String title) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mycard_exchange_knowok, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
//        iv_card_img
        mIvDiss = (ImageView) contentView.findViewById( R.id.iv_popwindow_diss );
        mTvContentOne = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
        tvPopFinish = (TextView) contentView.findViewById ( R.id.tv_popwindow_finish );
//        tvPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_title );
//        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content1 );
//        tvPopContent2= (TextView) contentView.findViewById ( R.id.tv_popwindow_content2 );
//        tvPopContent3 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content3 );
//        tvPopTitle.setText ( title );
        mTvContentOne.setText ( title );
//        tvPopContent2.setText ( content2 );
//        tvPopContent3.setText ( content3 );
        tvPopKnow.setOnClickListener ( this );
        tvPopFinish.setOnClickListener ( this );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
}
