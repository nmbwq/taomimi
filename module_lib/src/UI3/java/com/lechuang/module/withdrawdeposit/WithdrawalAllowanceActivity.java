package java.com.lechuang.module.withdrawdeposit;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.LogUtils;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.Utils;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.IncomeOverviewBean;
import java.com.lechuang.module.bean.WithdrawDepositBean;
import java.com.lechuang.module.withdrawdeposit.utils.ClearEditText;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_WITHDRAWL_ALLOWANCE)
public class WithdrawalAllowanceActivity extends BaseActivity implements View.OnClickListener{
    private ClearEditText mCetWithdrawPrice;
//    private TextView mCetWithdrawPrice;
    private TextView mTvKezhuanchu,mTvDaijiesuan,mTvYijiesuan,mTvTixian;
    private Button mBtnTx;
    private double aMoney;
    private double eMoney;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw_allowance;
    }

    @Override
    protected void findViews() {
        mCetWithdrawPrice = $(R.id.cet_wd_withdrawPrice);
        mTvKezhuanchu = $(R.id.tv_kezhuanchu);
        mTvDaijiesuan = $(R.id.tv_daijiesuan_num);
        mTvYijiesuan = $(R.id.tv_yijiesuan_num);
        mTvTixian = $(R.id.tv_tixain);
        mBtnTx = $(R.id.btn_wd_tx);
        ((TextView) $(R.id.iv_common_title)).setText("转至可提现");
        ((TextView) $(R.id.tv_common_right)).setText("转出记录");
        ((TextView) $(R.id.tv_common_right)).setVisibility(View.VISIBLE);
        ((RelativeLayout) $(R.id.rl_common_background)).setBackgroundColor(getResources().getColor(R.color.c_F4F4F4));
        ((TextView) $(R.id.tv_status_bar)).setBackgroundColor(getResources().getColor(R.color.c_F4F4F4));
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        mTvTixian.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void getData() {
        getUserAllowance();
    }

    /**
     * 获取管理津贴信息
     */
    private void getUserAllowance() {
        NetWork.getInstance()
                .setTag(Qurl.incomeOverview)
                .getApiService(ModuleApi.class)
                .userAllowance()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<IncomeOverviewBean>(this, false, false) {

                    @Override
                    public void onSuccess(final IncomeOverviewBean result) {
                        if (result == null) {
                            return;
                        }
//                        totalIncome
                        DecimalFormat df = new DecimalFormat("#####0.00");
                        //管理津贴 guanLiMoney
                        aMoney=Double.parseDouble(result.allowanceMoney);
                        eMoney=Double.parseDouble(result.estimateMoney);
                        mTvYijiesuan.setText( "" + df.format(aMoney) );//可提现管理津贴
                        mTvKezhuanchu.setText( df.format(aMoney)+"元" );//可提现管理津贴
                        mTvDaijiesuan.setText( "" + df.format(eMoney) );//预估管理津贴
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void userWithdraw(double withdrawPrice){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("withdrawPrice", withdrawPrice);
        NetWork.getInstance()
                .setTag( Qurl.withdrawAllowance)
                .getApiService(ModuleApi.class)
                .withdrawAllowance(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String> (WithdrawalAllowanceActivity.this,false,true) {

                    @Override
                    public void onSuccess(final String result) {
                        toast("转至可提现余额成功");
                        mCetWithdrawPrice.setText("");
                        getUserAllowance();
                    }
                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
            finish ();
        }else if (id ==R.id.tv_common_right){
            ARouter.getInstance().build(ARouters.PATH_WITHDRAWL_ALLOWANCE_RECORD).navigation();
        }else if (id==R.id.tv_tixain){
            if (aMoney==0){
                toast("没有可转出金额");
                return;
            }
            if (mCetWithdrawPrice.getText ().toString ()==null||mCetWithdrawPrice.getText ().toString ().equals("")){
                toast("请输入金额");
                return;
            }
            Double price=Double.parseDouble(mCetWithdrawPrice.getText ().toString ());
            if (price>0){
                if (price>aMoney){
                    toast("转出金额大于可提现金额");
                }else {
                    userWithdraw(price);
                }
            }

        }
    }
}
