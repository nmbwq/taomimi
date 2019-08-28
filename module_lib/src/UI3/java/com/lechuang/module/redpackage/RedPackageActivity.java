package java.com.lechuang.module.redpackage;

import android.text.*;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseOtherActivity;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.AlipayEntity;
import com.common.app.utils.DeviceUtils;
import com.common.app.utils.PayUtils;
import com.common.app.utils.SPUtils;
import com.common.app.utils.StringUtils;
import com.common.app.view.CommonDialog;
import com.lechuang.module.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.MyCardNotifyBean;
import java.com.lechuang.module.bean.MyCardStratPayBean;
import java.com.lechuang.module.bean.RechargeInfoBean;
import java.com.lechuang.module.bean.RedPackageBean;
import java.com.lechuang.module.signin.SignInActivity;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Route(path = ARouters.PATH_REDPACKAGE)
public class RedPackageActivity extends BaseOtherActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, PayUtils.PayResultListener {

    private Switch mSwitchRedPack;
    private boolean mIsRedPackageSwitch = false;//红包的开关状态
    private TextView mRvRedPackageSuiJi,mRvRedPackageGuDing,mTvRedPackageWanFa,mTvRedPackageJiQiao,
            mTvRedPackageShuoMing,mTvRedPackageYue,mTvRedPackageSet,mTvCommonRight;
    private ImageView mIvRedPackage;
    private double mYueMoney;//余额

    @Override
    protected int getLayoutId() {
        return R.layout.activity_red_package;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("新人注册红包");
        mSwitchRedPack = $(R.id.switch_red_pack);
        mTvCommonRight = $(R.id.tv_common_right);
        mTvCommonRight.setTextColor(getResources().getColor(R.color.c_2F2F2F));
        mTvCommonRight.setText("红包明细");
        mTvCommonRight.setVisibility(View.VISIBLE);
        $(R.id.tv_red_package_chongzhi).setOnClickListener(this);
        $(R.id.tv_red_package_tixian).setOnClickListener(this);
        $(R.id.iv_common_back).setOnClickListener(this);
        mRvRedPackageSuiJi = $(R.id.tv_red_package_suiji);
        mRvRedPackageGuDing = $(R.id.tv_red_package_guding);
        mTvRedPackageWanFa = $(R.id.tv_red_package_wanfa);
        mTvRedPackageJiQiao = $(R.id.tv_red_package_jiqiao);
        mTvRedPackageShuoMing = $(R.id.tv_red_package_shuoming);
        mIvRedPackage = $(R.id.iv_red_package);
        mTvRedPackageYue = $(R.id.tv_red_package_yue);
        mTvRedPackageSet = $(R.id.tv_red_package_set);

        mTvCommonRight.setOnClickListener(this);
        mRvRedPackageSuiJi.setOnClickListener(this);
        mRvRedPackageGuDing.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mIsRedPackageSwitch = SPUtils.getInstance().getBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",false);
        mSwitchRedPack.setChecked(mIsRedPackageSwitch);
    }

    @Override
    protected void getData() {
        getRedpackageInfo();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        if (isChecked){

            if (!mResultRedPackState){
                showPopuwindYue();
                mIsRedPackageSwitch = mResultRedPackState;
                SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",mIsRedPackageSwitch);
                mSwitchRedPack.setChecked(mIsRedPackageSwitch);
                return;
            }

//            mIsRedPackageSwitch = isChecked;
//            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",mIsRedPackageSwitch);
//            mSwitchRedPack.setChecked(mIsRedPackageSwitch);

            setRedOpen(1);//开启
        }else {
            setRedOpen(0);//关闭
//            mIsRedPackageSwitch = isChecked;
//            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",mIsRedPackageSwitch);
//            mSwitchRedPack.setChecked(mIsRedPackageSwitch);
        }
    }

    private void setRedOpen(final int isStatus){
        Map<String,Object> param = new HashMap<>();
        param.put("isStatus",isStatus + "");
        NetWork.getInstance()
                .setTag(Qurl.operateSwitch)
                .getApiService(ModuleApi.class)
                .operateSwitch(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(this,false,true) {
                    @Override
                    public void onSuccess(String result) {
                        if (isStatus == 0){
                            mIsRedPackageSwitch = false;
                            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",mIsRedPackageSwitch);
                            mSwitchRedPack.setChecked(mIsRedPackageSwitch);
                        }else if (isStatus == 1){
                            mIsRedPackageSwitch = true;
                            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",mIsRedPackageSwitch);
                            mSwitchRedPack.setChecked(mIsRedPackageSwitch);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id == R.id.tv_red_package_chongzhi){//充值
            getChongzhi();
        }else if (id == R.id.tv_red_package_tixian){//转至可提现余额
            showPopuwindPayTrans();
        }else if (id == R.id.tv_red_package_suiji){//随机红包
            showPopuwindSuiJiSet();
        }else if (id == R.id.tv_red_package_guding){//固定红包
            showPopuwindGuDingSet();
        }else if (id == R.id.tv_common_right){
            ARouter.getInstance().build(ARouters.PATH_REDPACKAGE_INFO).navigation();
        }
    }

    private boolean mResultRedPackState;//点击红包开关是时的判断状态
    private void getRedpackageInfo(){
        NetWork.getInstance()
                .setTag(Qurl.redPackageInfo)
                .getApiService(ModuleApi.class)
                .redPackage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<RedPackageBean>(this, false, true) {
                    @Override
                    public void onSuccess(RedPackageBean result) {
                        if (result == null || result.activeInfo == null){
                            return;
                        }

                        mYueMoney = result.activeInfo.deposit;

                        mTvRedPackageYue.setText(String.format("%.2f", result.activeInfo.deposit));
                        if (result.activeInfo.type == 1){
                            mTvRedPackageSet.setText(String.format("%.2f",result.activeInfo.fixedAmount) + "~" +String.format("%.2f",result.activeInfo.floatAmount));
                        }else if (result.activeInfo.type == 2){
                            mTvRedPackageSet.setText(String.format("%.2f",result.activeInfo.fixedAmount) + "");
                        }

                        //点击红包开关是时的判断状态
                        if (result.activeInfo.type == 1 && result.activeInfo.deposit >= result.activeInfo.floatAmount){//随机红包
                            mResultRedPackState = true;
                        }else if (result.activeInfo.type == 2 && result.activeInfo.deposit >= result.activeInfo.fixedAmount){//固定红包
                            mResultRedPackState = true;
                        }else {//关闭红包
                            mResultRedPackState = false;
                        }

                        if (result.activeInfo.type == 1){
                            mRvRedPackageSuiJi.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_selecter));
                            mRvRedPackageGuDing.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_unselecter));
                            mRvRedPackageSuiJi.setTextColor(getResources().getColor(R.color.white));
                            mRvRedPackageGuDing.setTextColor(getResources().getColor(R.color.c_222222));
                        }else if (result.activeInfo.type == 2){
                            mRvRedPackageSuiJi.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_unselecter));
                            mRvRedPackageGuDing.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_selecter));
                            mRvRedPackageGuDing.setTextColor(getResources().getColor(R.color.white));
                            mRvRedPackageSuiJi.setTextColor(getResources().getColor(R.color.c_222222));

                        }



                        //红包开关的默认状态
                        if (result.activeInfo.isStatus == 0 || !mResultRedPackState){
                            mIsRedPackageSwitch = false;
                        }else {
                            mIsRedPackageSwitch = true;
                        }
                        SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",mIsRedPackageSwitch);
                        mSwitchRedPack.setChecked(mIsRedPackageSwitch);


                        mTvRedPackageWanFa.setText(result.activeInfo.regulation);
                        mTvRedPackageJiQiao.setText(result.activeInfo.kindlyReminder);
                        mTvRedPackageShuoMing.setText(result.activeInfo.remark);

                        Glide.with(BaseApplication.getApplication()).load(result.activeInfo.activeImg).into(mIvRedPackage);


                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mSwitchRedPack.setOnCheckedChangeListener(RedPackageActivity.this);
                    }

                    @Override
                    public void onFailed(int errorCode, String result) {
                        super.onFailed(errorCode, result);
                        mSwitchRedPack.setOnCheckedChangeListener(RedPackageActivity.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSwitchRedPack.setOnCheckedChangeListener(RedPackageActivity.this);
                    }
                });
    }


    /**
     * 余额不足弹窗
     */
    private PopupWindow mPopupWindowYue;
    private void showPopuwindYue(){
        releasePopuwind(mPopupWindowYue);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popu_red_package_yue,null);
        contentView.findViewById(R.id.tv_popu_red_package_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去参与
                releasePopuwind(mPopupWindowYue);
            }
        });

        mPopupWindowYue = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindowYue.setContentView(contentView);
        mPopupWindowYue.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 充值弹窗
     */
    private PopupWindow mPopupWindowPay;
    private boolean mPayYue = true;//默认余额
    private void showPopuwindPay(final double yueMoney){


        releasePopuwind(mPopupWindowPay);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popu_red_package_pay,null);
        final EditText etPopuRedPackagePayStartValue = contentView.findViewById(R.id.et_popu_red_package_pay_start_value);
        final ImageView ivPayZfYue = contentView.findViewById(R.id.iv_pay_zf_ye);
        final ImageView ivPayZfZFB = contentView.findViewById(R.id.iv_pay_zf_zfb);
        TextView tvReMainMoney = contentView.findViewById(R.id.tv_iv_pay_remain_money);
        etPopuRedPackagePayStartValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tvReMainMoney.setText("淘觅觅可用余额 " + String.format("%.2f", yueMoney));
        if (!TextUtils.isEmpty(yueMoney + "") && Double.valueOf(yueMoney) < 50){
            mPayYue = false;
            changePayChannel(mPayYue,ivPayZfYue,ivPayZfZFB);
        }

        contentView.findViewById(R.id.tv_pay_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                releasePopuwind(mPopupWindowPay);
            }
        });
        contentView.findViewById(R.id.tv_pay_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                String payValue = etPopuRedPackagePayStartValue.getText().toString();
                if (!TextUtils.isEmpty(payValue)){
                    double payValueF = Double.valueOf(payValue);
                    if (payValueF >= 50){//充值

                        if (mPayYue){//走余额接口
                            startPayInfo(2,payValueF);
                        }else {//走支付宝接口
                            startPayInfo(1,payValueF);
                        }
                    }else {
                        toast("一次充值最低50元");
                    }
                }else {
                    toast("一次充值最低50元");
                }

            }

        });

        ivPayZfYue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//余额图片

                if (yueMoney >= 50){
                    mPayYue = true;
                    changePayChannel(mPayYue,ivPayZfYue,ivPayZfZFB);
                }else {
                    toast("可用余额不足");
                }


            }
        });
        ivPayZfZFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//支付宝图片
                mPayYue = false;
                changePayChannel(mPayYue,ivPayZfYue,ivPayZfZFB);

            }
        });
        mPopupWindowPay = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindowPay.setContentView(contentView);
        mPopupWindowPay.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 转至可提现余额弹窗
     */
//    private PopupWindow mPopupWindowPayTrans;
    private CommonDialog mPopupWindowPayTrans;
    private void showPopuwindPayTrans(){
        if (mPopupWindowPayTrans != null) {
            mPopupWindowPayTrans.dismiss();
            mPopupWindowPayTrans = null;
        }
        String trim = mTvRedPackageYue.getText().toString().trim();
        final double aDouble = Double.valueOf(trim);

        if (mPopupWindowPayTrans == null) {
            mPopupWindowPayTrans = new CommonDialog(RedPackageActivity.this, R.layout.layout_popu_red_package_pay_trans).setGravity(Gravity.BOTTOM);
        }

        final EditText etPopuRedPackagePayTransValue = (EditText) mPopupWindowPayTrans.getViewId(R.id.et_popu_red_package_pay_trans_value);
        etPopuRedPackagePayTransValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) return;
                if (aDouble == 0 && !TextUtils.equals("0",editable.toString())){
                    editable.replace(0,editable.length(),"0");
                }
            }
        });
        mPopupWindowPayTrans.getViewId(R.id.tv_pay_trans_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                showKeyboard(false);
                if (mPopupWindowPayTrans != null) {
                    mPopupWindowPayTrans.dismiss();
                    mPopupWindowPayTrans = null;
                }

            }
        });
        mPopupWindowPayTrans.getViewId(R.id.tv_pay_trans_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定

                String payValue = etPopuRedPackagePayTransValue.getText().toString();
                if (TextUtils.isEmpty(payValue)){
                    return;
                }

                double payValueF = Double.valueOf(payValue);

                 if (aDouble < payValueF){
                     toast("输入金额超过红包余额");
                 }else {
                     tixian(payValueF);
                     showKeyboard(false);
                 }


            }
        });
        mPopupWindowPayTrans.show();

    }

    /**
     * 余额提现
     */
    private void tixian(double amount){
        Map<String,Object> param = new HashMap<>();
        param.put("amount",amount);
        NetWork.getInstance()
                .setTag(Qurl.transferAccounts)
                .getApiService(ModuleApi.class)
                .transferAccounts(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(this,false,true) {
                    @Override
                    public void onSuccess(String result) {
                        if (mPopupWindowPayTrans != null) {
                        mPopupWindowPayTrans.dismiss();
                        mPopupWindowPayTrans = null;
                    }

                        getData();
                    }
                });
    }

    /**
     * 随机红包设置弹窗
     */
    private PopupWindow mPopupWindowSuiJiSet;
    private void showPopuwindSuiJiSet(){
        releasePopuwind(mPopupWindowSuiJiSet);

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popu_red_package_suiji_set,null);
        final EditText etPopuRedPackageSuiJiStartValue = contentView.findViewById(R.id.et_popu_red_package_suiji_start_value);
        final EditText etPopuRedPackageSuiJiEndValue = contentView.findViewById(R.id.et_popu_red_package_suiji_end_value);
        etPopuRedPackageSuiJiStartValue.setFilters(new InputFilter[]{new CusInputFilter(etPopuRedPackageSuiJiStartValue,0.01,100)});
        etPopuRedPackageSuiJiStartValue.addTextChangedListener(new CusInputFilter(etPopuRedPackageSuiJiStartValue,0.01,100));

        etPopuRedPackageSuiJiEndValue.setFilters(new InputFilter[]{new CusInputFilter(etPopuRedPackageSuiJiEndValue,0.01,100)});
        etPopuRedPackageSuiJiEndValue.addTextChangedListener(new CusInputFilter(etPopuRedPackageSuiJiEndValue,0.01,100));

        contentView.findViewById(R.id.tv_popu_red_package_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                releasePopuwind(mPopupWindowSuiJiSet);
            }
        });
        contentView.findViewById(R.id.tv_popu_red_package_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                String payStartValue = etPopuRedPackageSuiJiStartValue.getText().toString();
                String payEndValue = etPopuRedPackageSuiJiEndValue.getText().toString();

                if (!TextUtils.isEmpty(payStartValue) && !TextUtils.isEmpty(payEndValue)){
                    double payStartValueF = Double.valueOf(payStartValue);
                    double payEndValueF = Double.valueOf(payEndValue);
                    if (payStartValueF > mYueMoney || payEndValueF > mYueMoney){
                        toast("红包余额不足");
                        return;
                    }
                    if (payStartValueF == payEndValueF){
                        toast("金额范围错误");
                        return;
                    }
                    if (payStartValueF >= 0.01 && payEndValueF <= 100 &&
                            payEndValueF >= 0.01 && payEndValueF <= 100 &&
                            payStartValueF < payEndValueF){
                        setRedSize(payStartValueF,payEndValueF,1);

                    }else {
                        toast("金额范围错误");
                    }
                }else {
                    toast("金额范围错误");
                }
            }
        });
        mPopupWindowSuiJiSet = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindowSuiJiSet.setContentView(contentView);
        mPopupWindowSuiJiSet.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 固定红包弹窗
     */
    private PopupWindow mPopupWindowGuDingSet;
    private void showPopuwindGuDingSet(){

        releasePopuwind(mPopupWindowGuDingSet);

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popu_red_package_guding_set,null);
        final EditText etPopuRedPackageGuDingValue = contentView.findViewById(R.id.et_popu_red_package_gudingset_value);
        etPopuRedPackageGuDingValue.setFilters(new InputFilter[]{new CusInputFilter(etPopuRedPackageGuDingValue,0.01,100)});
        etPopuRedPackageGuDingValue.addTextChangedListener(new CusInputFilter(etPopuRedPackageGuDingValue,0.01,100));

        contentView.findViewById(R.id.tv_popu_red_package_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                releasePopuwind(mPopupWindowGuDingSet);
            }
        });
        contentView.findViewById(R.id.tv_popu_red_package_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                String value = etPopuRedPackageGuDingValue.getText().toString();
                if (!TextUtils.isEmpty(value)){
                    double valueF = Double.valueOf(value);

                    if (valueF > mYueMoney){
                        toast("红包余额不足");
                        return;
                    }

                    if (valueF >= 0.01 && valueF <= 100){
                        setRedSize(valueF,0,2);
                    }else {
                        toast("金额范围错误");
                    }
                }else {
                    toast("金额范围错误");
                }

            }
        });
        mPopupWindowGuDingSet = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindowGuDingSet.setContentView(contentView);
        mPopupWindowGuDingSet.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 设置发放金额
     */
    private void setRedSize(final double fixedA, final double floatA, final int type){

        Map<String,Object> param = new HashMap<>();
        param.put("type",type);
        param.put("fixedAmount",fixedA);
        if (type == 1){
            param.put("floatAmount",floatA);
        }

        NetWork.getInstance()
                .setTag(Qurl.redPacketFit)
                .getApiService(ModuleApi.class)
                .redPacketFit(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(this,false,true) {
                    @Override
                    public void onSuccess(String result) {
                        if (type == 1){
                            releasePopuwind(mPopupWindowSuiJiSet);
                        }else if (type == 2){
                            releasePopuwind(mPopupWindowGuDingSet);
                        }

                        getData();

//                        if (type == 2){
//                            mTvRedPackageSet.setText(fixedA + "");
//                        }else if (type == 1){
//                            mTvRedPackageSet.setText(fixedA + "~" + floatA);
//                        }
//
//                        if (type == 1){
//                            mRvRedPackageSuiJi.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_selecter));
//                            mRvRedPackageGuDing.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_unselecter));
//                        }else if (type == 2){
//                            mRvRedPackageSuiJi.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_selecter));
//                            mRvRedPackageGuDing.setBackground(getResources().getDrawable(R.drawable.bg_red_package_btn_unselecter));
//                        }
//
//                        //红包开关的默认状态
//                        if (type == 1 && floatA <= mYueMoney){
//                            mIsRedPackageSwitch = true;
//                        }else if (type == 2 &&fixedA <= mYueMoney ){
//                            mIsRedPackageSwitch = true;
//                        }else {
//                            mIsRedPackageSwitch = false;
//                        }

                    }
                });
    }

    /**
     * 更改支付方式
     *
     */
    private void changePayChannel(boolean isYue,ImageView yueImg,ImageView zfbImg) {
        yueImg.setImageResource(isYue ? R.drawable.ic_sign_selected : R.drawable.ic_sign_unselected );
        zfbImg.setImageResource(isYue ? R.drawable.ic_sign_unselected : R.drawable.ic_sign_selected);
    }

    private void releasePopuwind(PopupWindow popupWindow){
        if (popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void aliPayCallBack(String orderId) {
        notyfyInfoUpdata(orderId);
        releasePopuwind(mPopupWindowPay);
    }

    @Override
    public void aliPayCancle(String errorInfo) {

    }

    @Override
    public void aliPayFailOther(String errorCode, String errorInfo) {

    }

    private void notyfyInfoUpdata(String paymentNo) {

        Map<String, Object> param = new HashMap<>();
        param.put("paymentNo", paymentNo);
        NetWork.getInstance()
                .setTag(Qurl.queryAlipayBill)
                .getApiService(ModuleApi.class)
                .queryAlipayBill(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardNotifyBean>(RedPackageActivity.this, false, true) {

                    @Override
                    public void onSuccess(MyCardNotifyBean result) {
                        if (result == null) {
                            toast("获取支付数据异常！");
                            return;
                        }
                        if (result.status == 200) {
                            toast(TextUtils.isEmpty(result.errMsg) ? "支付成功！" : result.errMsg);
                            getData();
                        } else {
                            toast(TextUtils.isEmpty(result.errMsg) ? "支付失败！" : result.errMsg);
                        }

                    }

                });
    }

    class CusInputFilter implements TextWatcher,InputFilter{

        private static final int POINTER_LENGTH = 2;
        private static final String POINTER = ".";
        private EditText mEditText;
        Pattern mPattern;
        private double mMinValue = 0.01f;
        private int mMaxValue = 100;
        private String mChangeBefore = "";

        public CusInputFilter(EditText editText, double minValue, int maxValue) {
            mEditText = editText;
            mMinValue = minValue;
            mMaxValue = maxValue;
            mPattern = Pattern.compile("([0-9]|\\.)*");
        }


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mChangeBefore = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Editable editable = mEditText.getText();
            String text = s.toString();
            if (TextUtils.isEmpty(text)) return;
            if ((s.length() > 1) && (s.charAt(0) == '0') && s.charAt(1) != '.') {   //删除首位无效的“0”
                editable.delete(0, 1);
                return;
            }

            if (text.equals(".")) {                                    //首位是“.”自动补“0”
                editable.insert(0, "0");
                return;
            }

            if (mPattern != null && !mPattern.matcher(text).matches() && editable.length() > 0) {
                editable.delete(editable.length() - 1, editable.length());
                return;
            }

            if (TextUtils.equals("0",text) || TextUtils.equals("0.",text) || TextUtils.equals("0.0",text)){
                return;
            }

            Double aDouble = Double.valueOf(text);
            if (aDouble < mMinValue){
                mEditText.setText( mMinValue + "");
                mEditText.setSelection((mMinValue + "").toString().length());
                toast("金额范围错误");
                return;
            }

            if (aDouble > 100){
                mEditText.setText(mMaxValue + "");
                mEditText.setSelection((mMaxValue + "").toString().length());
                toast("金额范围错误");
                return;
            }


        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String sourceText = source.toString();
            String destText = dest.toString();
            //验证删除等按键
            if (TextUtils.isEmpty(sourceText)) {
                return "";
            }
            Matcher matcher = mPattern.matcher(source);
            if (destText.startsWith(".")) return null;
            //已经输入小数点的情况下，只能输入数字
            if(destText.contains(POINTER)) {
                if (!matcher.matches()) {
                    return "";
                } else {
                    if (POINTER.equals(source)) { //只能输入一个小数点
                        return "";
                    }
                }
                //验证小数点精度，保证小数点后只能输入两位
                int index = destText.indexOf(POINTER);
                int length = dend - index;
                if (length > POINTER_LENGTH) {
                    return "";
                }
            }
            return null;
        }
    }

    private void getChongzhi(){

        NetWork.getInstance()
                .setTag(Qurl.rechargeInfo)
                .getApiService(ModuleApi.class)
                .rechargeInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<RechargeInfoBean>(this,false,true) {
                    @Override
                    public void onSuccess(RechargeInfoBean result) {

                        if (result == null){
                            return;
                        }
                        showPopuwindPay(result.balance);
                    }
                });
    }

    private void startPayInfo(final int type, double payment) {

        Map<String, Object> param = new HashMap<>();
        param.put("payment", payment);
        param.put("type", type);
        NetWork.getInstance()
                .setTag(Qurl.operateRecharge)
                .getApiService(ModuleApi.class)
                .operateRecharge(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardStratPayBean>(this, false, true) {

                    @Override
                    public void onSuccess(MyCardStratPayBean result) {
                        if (result == null) {
                            toast("获取支付数据异常！");
                            return;
                        }

                        if (result.status == 200) {
                            if (type == 1) {//支付宝支付，获取支付宝订单签名去支付宝支付
                                if (result.returnMap == null || TextUtils.isEmpty(result.returnMap.sign) || TextUtils.isEmpty(result.returnMap.paymentNo)) {
                                    toast("获取支付数据异常！");
                                    return;
                                }
                                PayUtils payUtils = new PayUtils(RedPackageActivity.this);
                                AlipayEntity alipayEntity = new AlipayEntity();
                                alipayEntity.setOrderid(result.returnMap.paymentNo);
                                alipayEntity.setOrderstring(result.returnMap.sign);
                                payUtils.payByAliPay(alipayEntity);
                                payUtils.setResultListener(RedPackageActivity.this);
                            } else {//余额支付，关闭弹窗
                                getData();
                                releasePopuwind(mPopupWindowPay);
                            }
                        } else if (result.status == 500) {
                            toast(result.errMsg);
                        }
                    }
                });
    }

}
