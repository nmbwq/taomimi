package java.com.lechuang.module.withdrawdeposit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.Utils;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.WithdrawDepositBean;
import java.com.lechuang.module.set.UpdataNickActivity;
import java.com.lechuang.module.withdrawdeposit.utils.ClearEditText;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_WITHDRAW_DEPOSI)
public class WithdrawDepositActivity extends BaseActivity implements View.OnClickListener{
    private ClearEditText mCetWithdrawPrice;
//    private TextView mCetWithdrawPrice;
    private TextView mTvAlipayNumber,mTvWithdrawPrice,mTvWithdrawMinPrice,mTvCashDeclaration;
    private Button mBtnTx;
    private LinearLayout mLlBindZfb;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw_deposit;
    }

    @Override
    protected void findViews() {
        mCetWithdrawPrice = $(R.id.cet_wd_withdrawPrice);
        mTvAlipayNumber = $(R.id.tv_wd_alipayNumber);
        mTvWithdrawPrice = $(R.id.tv_wd_withdrawPrice);
        mTvWithdrawMinPrice = $(R.id.tv_wd_withdrawMinPrice);
        mBtnTx = $(R.id.btn_wd_tx);
        mTvCashDeclaration = $(R.id.tv_wd_cashDeclaration);
        mLlBindZfb = $(R.id.ll_wd_alipay);
        ((TextView) $(R.id.iv_common_title)).setText("提现");
        $(R.id.iv_common_back).setOnClickListener(this);
        mLlBindZfb.setOnClickListener ( this );
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

    private void userWithdraw(){

        NetWork.getInstance()
                .setTag( Qurl.userWithdraw)
                .getApiService(ModuleApi.class)
                .getUserWithdraw()
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<WithdrawDepositBean> (WithdrawDepositActivity.this,false,true) {

                    @Override
                    public void onSuccess(final WithdrawDepositBean result) {
                        DecimalFormat decimalFormat=new DecimalFormat("#0.00");
                        mTvWithdrawPrice.setText ( decimalFormat.format(result.withdrawPrice)+"元" );
                        mTvWithdrawMinPrice.setText ( result.withdrawMinPrice+"元" );
                        mTvCashDeclaration.setText ( result.cashDeclaration );
                        mCetWithdrawPrice.setText ( result.withdrawPrice+"" );
                        final double money = result.withdrawPrice;
                        if (!TextUtils.isEmpty ( result.alipayNumber )){
                            mTvAlipayNumber.setText ( "支付账号("+result.alipayNumber+")" );
                            UserHelper.getInstence ().getUserInfo ().setZhifubaoNum (result.alipayNumber+"");
                            mBtnTx.setBackground ( getResources ().getDrawable ( R.drawable.bg_withdrawdeposit_button ) );
                            mBtnTx.setOnClickListener ( new View.OnClickListener () {
                                @Override
                                public void onClick(View view) {
                                    if (TextUtils.isEmpty ( mCetWithdrawPrice.getText ().toString () )||money==0 ){
                                        Utils.toast ( "当前无可提现额，无法提现" );
                                        return;
                                    }
                                    sendWithdraw();
                                }
                            } );
                        }else {
                            mTvAlipayNumber.setText ( "支付账号(去授权)" );
                            mBtnTx.setBackground ( getResources ().getDrawable ( R.drawable.bg_withdrawdeposit_buttonhui ) );
                            mBtnTx.setOnClickListener ( new View.OnClickListener () {
                                @Override
                                public void onClick(View view) {
                                    Utils.toast ( "请先授权支付账号！" );
                                }
                            } );
                        }
                    }
                });
    }

    private void sendWithdraw(){
        ApiCancleManager.getInstance().removeAll();
        Map<String, String> allParam = new HashMap<> ();
        allParam.put ( "withdrawPrice",mCetWithdrawPrice.getText ().toString () );
        NetWork.getInstance()
                .setTag( Qurl.sendWithdraw)
                .getApiService(ModuleApi.class)
                .getSendWithdraw(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String> (WithdrawDepositActivity.this,false,true) {

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)){
                            return;
                        }
                        Utils.toast ( result );
                        userWithdraw();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume ();
        userWithdraw();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
            finish ();
        }else if (id == R.id.ll_wd_alipay){
            if (TextUtils.isEmpty ( UserHelper.getInstence ().getUserInfo ().getZhifubaoNum ())){
                ARouter.getInstance().build(ARouters.PATH_VERIFY).navigation();
            }else {
                ARouter.getInstance().build(ARouters.PATH_COMPLETE_BIND).navigation();
            }
        }
    }
}
