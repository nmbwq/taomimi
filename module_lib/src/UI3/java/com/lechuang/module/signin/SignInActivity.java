package java.com.lechuang.module.signin;


import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.AlipayEntity;
import com.common.app.utils.CountDownTextView;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.PayUtils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.MyCardExchangeBean;
import java.com.lechuang.module.bean.MyCardMessageBeab;
import java.com.lechuang.module.bean.MyCardNotifyBean;
import java.com.lechuang.module.bean.MyCardStratPayBean;
import java.com.lechuang.module.bean.MyParticipateStateBean;
import java.com.lechuang.module.bean.MySigninTodayBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SIGN_IN)
public class SignInActivity extends BaseActivity implements View.OnClickListener, PayUtils.PayResultListener, OnRefreshLoadMoreListener {
    private PopupWindow mPopupWindow, mPopupWindowTips, mPopupWindowNo;

    private TextView mTvRight, mTvContentOne, mTvToPay, mTvDaKa, mTvFinish, mTvKuaiName, mTvKuaiTime, mTvFailure, mTvSuccessful, mTvTodayMoney
            , mTvTodayPrompt, mTvTodayPeople, mTvChixuName, mTvChixuTime;
    private ImageView mIvDiss, mIvPhotoOne, mIvPhotoTwo, mIvPhotoThree, mIvPhotoFour, mIvPhotoFive, mIvPhotoKuai, mIvPhotoChixu;
    private CardView mCdOne,mCdTwo,mCdThree,mCdFour;
    private TabLayout mXTabLayoutSignIn;
    private List<String> tabS = new ArrayList<>();
    private TextView mTvSignOne, mTvSignTwo, mTvSignThree, mTvSignFour, mTvSignSure, mTvSignCancle,
            mTvSignPayTips, mTvSignPayTipsyeValue, mTvSignPayTipsSurePay, mTvSignPayTipsPayMoney, mTvSignRemainMoney;
    private ImageView mIvSignZfZFB, mIvSignZfYE, mIvSignPayTipsClose, mIvSignPayTips;
    private CountDownTextView countDownTextView;
    private SmartRefreshLayout mSmartRecord;
    private int state = 1;
    private LinearLayout mLlKuaiShou,mLlChixu;

//    private RelativeLayout mRlChallenge;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in_one;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("早起打卡赢现金");
        mTvRight = $(R.id.tv_common_right);
        mIvPhotoOne = $(R.id.iv_photo_one);
        mIvPhotoTwo = $(R.id.iv_photo_two);
        mIvPhotoThree = $(R.id.iv_photo_three);
        mIvPhotoFour = $(R.id.iv_photo_four);
        mIvPhotoFive = $(R.id.iv_photo_five);
        countDownTextView = $(R.id.tv_signin_time);
        mTvDaKa = $(R.id.tv_signin_daka);
        mSmartRecord = $(R.id.smart_signin);
        mIvPhotoKuai = $(R.id.iv_signin_photo);
        mTvKuaiName = $(R.id.tv_kuaishou_name);
        mTvKuaiTime = $(R.id.tv_kuaishou_time);
        mTvSuccessful = $(R.id.tv_signin_successful);
        mTvFailure = $(R.id.tv_signin_failure);
        mTvTodayPrompt = $(R.id.tv_signin_today_prompt);
        mTvTodayMoney = $(R.id.tv_signin_today_money);
        mTvTodayPeople = $(R.id.tv_signin_now_people);
        mLlKuaiShou = $(R.id.ll_kuaishou_all);
        mTvChixuName = $(R.id.tv_chixu_name);
        mTvChixuTime = $(R.id.tv_chixu_time);
        mIvPhotoChixu = $(R.id.iv_signin_photo_chixu);
        mLlChixu = $(R.id.ll_chixu);
        mCdOne = $(R.id.cv_item_one);
        mCdTwo = $(R.id.cv_item_two);
        mCdThree = $(R.id.cv_item_three);
        mCdFour = $(R.id.cv_item_four);
        tabS.add("1份");
        tabS.add("2份");
        tabS.add("5份");
        tabS.add("10份");

        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText("打卡记录");

        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.rl_signin_challenge).setOnClickListener(this);
//        mTvDaKa.setOnClickListener(this);
        $(R.id.iv_common_title).setOnClickListener(this);
        $(R.id.rl_signin_clockstate).setOnClickListener(this);
//        mEtAlipayNumber = $(R.id.et_bind_alipayNumber);
//        mEtAlipayRealName = $(R.id.et_bind_alipayRealName);
//        mTvSave = $(R.id.et_bind_zfb_save);
        mTvRight.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mSmartRecord.setOnRefreshLoadMoreListener(this);
        mSmartRecord.setEnableLoadMore(false);
    }

    @Override
    protected void getData() {
        //今日打卡状态
        getTodayState();
        //参与打卡状态
        getParticipateState();
        //打卡按钮的状态
//        OnClockState();
        //给最快手速添加照片
        /*Glide.with( BaseApplication.getApplication())
                        .load( UserHelper.getInstence().getUserInfo().getPhoto())
                        .placeholder(R.drawable.ic_common_user_def)
                        .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                        .into(mIvPhotoKuai);
        for (int i=0;i<5;i++){
            if (i==4){
                mIvPhotoFive.setVisibility( View.VISIBLE );
                *//*Glide.with( BaseApplication.getApplication())
                        .load( UserHelper.getInstence().getUserInfo().getPhoto())
                        .placeholder(R.drawable.ic_common_user_def)
                        .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                        .into(mIvPhotoFive);*//*
                return;
            }else {
                putPhoto(i,UserHelper.getInstence().getUserInfo().getPhoto());
            }
        }*/

    }

    private int num = 0;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        //if (id == R.id.tv_common_right) {
        if (id == R.id.tv_common_right) {
            ARouter.getInstance().build(ARouters.PATH_SIGN_IN_RECORD).navigation();
//            updataAlipay();
        } else if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.rl_signin_challenge) {
            ARouter.getInstance().build(ARouters.PATH_SIGN_IN_CHALLENGE).navigation();
        } else if (id == R.id.rl_signin_clockstate) {
            //state=1 参与打卡 state=2 倒计时 state=3 打卡
//            toast("我的状态是 " + state);
            if (state == 1) {
                getPayInfo();
                //state = 2;
                //刷新数据
//                getData();
//                showPopupWindowOK("18540");
//                num = 1;
            } else if (state == 2) {
//                showPopupWindowNO();
                //state=3;
                //刷新数据
//                getData();
//                num = 0;
            } else if (state == 3) {
                gotoDaKa();
                //state=1;
                //刷新数据
//                getData();
            }
//        } else if (id == R.id.iv_popwindow_diss) {
//            mPopupWindow.dismiss();
        } else if (id == R.id.tv_topay) {

        } else if (id == R.id.iv_sign_zf_ye) {
            changePayChannel(id);
        } else if (id == R.id.iv_sign_zf_zfb) {
            changePayChannel(id);
        } else if (id == R.id.tv_sign_one) {
            changePayNum(id);
        } else if (id == R.id.tv_sign_two) {
            changePayNum(id);
        } else if (id == R.id.tv_sign_three) {
            changePayNum(id);
        } else if (id == R.id.tv_sign_four) {
            changePayNum(id);
        } else if (id == R.id.tv_sign_sure) {
            showPopupWindowPayTips();
        } else if (id == R.id.iv_sign_pay_tips_close) {
            if (mPopupWindowTips != null) {
                mPopupWindowTips.dismiss();
                mPopupWindowTips = null;
            }
        } else if (id == R.id.tv_sign_cancle) {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        } else if (id == R.id.tv_sign_pay_tips_sure_pay) {
            startPayInfo();
        }
    }

    private void gotoDaKa() {
        NetWork.getInstance()
                .setTag(Qurl.userPunchIn)
                .getApiService(ModuleApi.class)
                .myPunchIn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardExchangeBean>(SignInActivity.this) {

                    @Override
                    public void onSuccess(MyCardExchangeBean result) {
                        if (result.status == 200) {
                            showPopupWindowOK();
                        } else {
                            toast(result.errMsg);
//                            showPopupWindowNO();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                        toast(moreInfo);
//                        showPopupWindowNO();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                        toast(e.getMessage());
//                        showPopupWindowNO();
                    }
                });
    }

    private void getTodayState() {

//        Map<String, String> allParam = new HashMap<>();
//        allParam.put("alipayNumber",alipayNumber);
//        allParam.put("alipayRealName",alipayRealName);

        NetWork.getInstance()
                .setTag(Qurl.myTodayPunchInSituation)
                .getApiService(ModuleApi.class)
                .myTodayPunchInSituation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MySigninTodayBean>(SignInActivity.this) {

                    @Override
                    public void onSuccess(MySigninTodayBean result) {
                        if (TextUtils.isEmpty(result.punchInStr)) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        if (result.firstPunchInUser == null) {
                            mLlKuaiShou.setVisibility( View.GONE );
                            //给快手添加图片
                            Glide.with(BaseApplication.getApplication())
                                    .load(R.drawable.ic_common_user_def)
                                    .placeholder(R.drawable.ic_common_user_def)
                                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 0))
                                    .into(mIvPhotoKuai);
                            //给快手添加名字
                            mTvKuaiName.setText("还没有人哦~");
                            //给快手添加时间
                            mTvKuaiTime.setText("");
                        } else {
                            mLlKuaiShou.setVisibility( View.VISIBLE );
                            //给快手添加图片
                            Glide.with(BaseApplication.getApplication())
                                    .load(result.firstPunchInUser.img)
                                    .placeholder(R.drawable.ic_common_user_def)
                                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 0))
                                    .into(mIvPhotoKuai);
                            //给快手添加名字
                            mTvKuaiName.setText(result.firstPunchInUser.nickName);
                            //给快手添加时间
                            mTvKuaiTime.setText(result.firstPunchInUser.punchInTime + "打卡");
                        }
                        //添加持续打卡 mTvChixuName mTvChixuTime mIvPhotoChixu
                        if (result.staminaUser == null) {
                            mLlChixu.setVisibility( View.GONE );
                            //给持续打卡添加图片
                            Glide.with(BaseApplication.getApplication())
                                    .load(R.drawable.ic_common_user_def)
                                    .placeholder(R.drawable.ic_common_user_def)
                                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 0))
                                    .into(mIvPhotoChixu);
                            //给持续打卡添加名字
                            mTvChixuName.setText("还没有人哦~");
                            //给持续打卡添加时间
                            mTvChixuTime.setText("");
                        } else {
                            mLlChixu.setVisibility( View.VISIBLE );
                            //给持续打卡添加图片
                            Glide.with(BaseApplication.getApplication())
                                    .load(result.staminaUser.img)
                                    .placeholder(R.drawable.ic_common_user_def)
                                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 0))
                                    .into(mIvPhotoChixu);
                            //给持续打卡添加名字
                            mTvChixuName.setText(result.staminaUser.nickName);
                            //给持续打卡添加时间
                            mTvChixuTime.setText("连续"+result.staminaUser.durationDay + "次");
                        }
                        //state=1 参与打卡 state=2 倒计时 state=3 打卡
                        //6-8点判断打卡状态 1可以打卡  0未打卡
                        if (result.isAllowClock == 1&&result.isPunchIn==0) {
                            state = 3;
                            //打卡按钮的状态
                            OnClockState(result.punchInStr, "0");
                        } else if (result.isAllowClock==0&&result.isJoin == 1 && result.isPunchIn == 2) {
                            //0-6点打卡倒计时 0不可以打卡  1参加今天打卡  0未打卡
                            state = 2;
                            //打卡按钮的状态
                            OnClockState(result.punchInStr, result.endTimestamp);
                        } else if ( result.isJoinTomorrow == 1) {
                            //8-24点打卡倒计时 1 参与明天打卡
                            state = 2;
                            //打卡按钮的状态
                            OnClockState(result.punchInStr, result.endTimestamp);
                        } else if (result.isJoinTomorrow == 0) {
                            //参与打卡
                            state = 1;
                            //打卡按钮的状态
                            OnClockState(result.punchInStr, "0");
                        }
                        /*LogUtils.w("okhttp", "result.isAllowClock=" + result.isAllowClock + "   result.isJoin==" + result.isJoin
                                + "   result.isJoinTomorrow==" + result.isJoinTomorrow + "    result.punchInStr=" + result.punchInStr + "    isPunchIn=" + result.isPunchIn +
                                "    endTimestamp= " + result.endTimestamp + "    show=" + result.show + "    state=" + state);*/
                        //打卡按钮的状态
//                        OnClockState(result.punchInStr,result.endTimestamp);
                        //成功多少人 mTvFailure , mTvSuccessful
                        mTvSuccessful.setText(result.punchCardCount + "");
                        mTvFailure.setText(result.unPunchCardCount + "");

                        //判断是否弹打卡失败
//                        toast("isPunchIn= " + result.isPunchIn+"    show="+result.show);
                        if (result.isPunchIn == 3 && result.show == 0) {
                            showPopupWindowNO(result.failedMoney);
                        }
                        /*if (result.isPunchIn==0){
                            //未打卡
                        }else if (result.isPunchIn==1){
                            //已打卡
                        }else if (result.isPunchIn==2){
                            //未开始打卡
                        }else if (result.isPunchIn==3){
                            //打卡过期，不允许打卡
                        }else if (result.isPunchIn==4){
                            //没有今天的参加记录
                        }*/
//                        toast(result);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                    }
                });

    }
    //参与打卡
    private void getParticipateState() {

//        Map<String, String> allParam = new HashMap<>();
//        allParam.put("alipayNumber",alipayNumber);
//        allParam.put("alipayRealName",alipayRealName);

        NetWork.getInstance()
                .setTag(Qurl.myParticipateState)
                .getApiService(ModuleApi.class)
                .myParticipateState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyParticipateStateBean>(SignInActivity.this) {

                    @Override
                    public void onSuccess(MyParticipateStateBean result) {
                        if (TextUtils.isEmpty(result.allAvgMonetExplain)) {
                            setRefreshLoadMoreState(true, true);
                            return;
                        }
                        setRefreshLoadMoreState(true, false);
                        //提示语
                        mTvTodayPrompt.setText(result.allAvgMonetExplain);
                        mTvTodayMoney.setText(result.allAvgMoney + "");
                        mTvTodayPeople.setText(result.joinCount + "");
//                        toast(result);
                        //首页参与人头像
                        mIvPhotoOne.setVisibility(View.GONE);
                        mIvPhotoTwo.setVisibility(View.GONE);
                        mIvPhotoThree.setVisibility(View.GONE);
                        mIvPhotoFour.setVisibility(View.GONE);
                        mIvPhotoFive.setVisibility(View.GONE);
                        mCdOne.setVisibility(View.GONE);
                        mCdTwo.setVisibility(View.GONE);
                        mCdThree.setVisibility(View.GONE);
                        mCdFour.setVisibility(View.GONE);
                        for (int i = 0; i < result.userHeaders.size(); i++) {
                            if (i == 4) {
                                mIvPhotoFive.setVisibility(View.VISIBLE);
                                return;
                            } else {
                                putPhoto(i, result.userHeaders.get(i).img);
                            }
                        }

                        /*if (result.indexOf(R.string.s_set_succes) != 1) {
//                            UserHelper.getInstence ().getUserInfo ().setZhifubaoNum ( alipayNumber );
                            finish();
                        }*/
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState(false, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState(false, false);
                    }
                });
    }

    /*private void putPhoto(int i, String photo) {
        if (i == 0) {
            mIvPhotoOne.setVisibility(View.VISIBLE);
        } else if (i == 1) {
            mIvPhotoTwo.setVisibility(View.VISIBLE);
        } else if (i == 2) {
            mIvPhotoThree.setVisibility(View.VISIBLE);
        } else if (i == 3) {
            mIvPhotoFour.setVisibility(View.VISIBLE);
        }
        Glide.with(BaseApplication.getApplication())
                .load(photo)
                .placeholder(R.drawable.ic_common_user_def)
                .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                .into(mIvPhotoOne);*/
    private void putPhoto(int i, String photo) {
        if (i == 0) {
            mIvPhotoOne.setVisibility(View.VISIBLE);
            mCdOne.setVisibility(View.VISIBLE);
            Glide.with(BaseApplication.getApplication())
                    .load(photo)
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 200))
                    .into(mIvPhotoOne);
        } else if (i == 1) {
            mIvPhotoTwo.setVisibility(View.VISIBLE);
            mCdTwo.setVisibility(View.VISIBLE);
            Glide.with(BaseApplication.getApplication())
                    .load(photo)
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 200))
                    .into(mIvPhotoTwo);
        } else if (i == 2) {
            mIvPhotoThree.setVisibility(View.VISIBLE);
            mCdThree.setVisibility(View.VISIBLE);
            Glide.with(BaseApplication.getApplication())
                    .load(photo)
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 200))
                    .into(mIvPhotoThree);
        } else if (i == 3) {
            mIvPhotoFour.setVisibility(View.VISIBLE);
            mCdFour.setVisibility(View.VISIBLE);
            Glide.with(BaseApplication.getApplication())
                    .load(photo)
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 200))
                    .into(mIvPhotoFour);
        }

    }

    private void toTime(int time) {
        countDownTextView.setNormalText("打卡倒计时23小时59分59秒").setShowFormatTime(true).setCountDownText("打卡倒计时", "")
                .setCloseKeepCountDown(false).setShowFormatTimeOther(0).setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
            @Override
            public void onFinish() {
                countDownTextView.setText("打卡");
                state = 3;
//                toast("我打卡的时间是" + state);
            }
        });
//        mTvDaKa.setVisibility( View.GONE );
//        countDownTextView.setVisibility( View.VISIBLE );
        countDownTextView.startCountDown(time);

    }

    private void OnClockState(String str, String time) {
        //state=1 参与打卡 state=2 倒计时 state=3 打卡
        if (state == 1) {
            //隐藏倒计时
            countDownTextView.setVisibility(View.GONE);
            //开放打卡
            mTvDaKa.setVisibility(View.VISIBLE);
            mTvDaKa.setText(str);
        } else if (state == 2) {
            //显示倒计时
            countDownTextView.setVisibility(View.VISIBLE);
            toTime(Integer.parseInt(time) / 1000);
//            toTime(24*60*60+5);
            //隐藏打卡
            mTvDaKa.setVisibility(View.GONE);
        } else if (state == 3) {
            //隐藏倒计时
            countDownTextView.setVisibility(View.GONE);
            //开放打卡
            mTvDaKa.setVisibility(View.VISIBLE);
            mTvDaKa.setText(str);
        }
    }

    //弹出提示
    private void showPopupWindowOK() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwind_signin_challenge, null);
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_popwindow_diss);
//        mTvContentOne = (TextView) contentView.findViewById(R.id.tv_popwindow_content);
        mTvToPay = (TextView) contentView.findViewById(R.id.tv_topay);
//        mTvContentOne.setText(title);
        imageView.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                getData();
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        mTvToPay.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
                getData();
                getPayInfo();
            }
        } );
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    //弹出提示
    private void showPopupWindowNO(String failedMoney) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwind_signin_challengeno, null);
        mPopupWindowNo = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        RelativeLayout relativeLayout = (RelativeLayout) contentView.findViewById(R.id.iv_popwindow_diss);
        mTvContentOne = (TextView) contentView.findViewById(R.id.tv_popwindow_content);
        mTvToPay = (TextView) contentView.findViewById(R.id.tv_topay);
        mTvContentOne.setText( "您的"+failedMoney+"元参与金将被打卡成功瓜分" );
//        mTvContentOne.setText( title );
        mTvToPay.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
                getData();
                getPayInfo();
            }
        } );
        relativeLayout.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                getData();
                if (mPopupWindowNo != null)
                    mPopupWindowNo.dismiss();
            }
        });
        mPopupWindowNo.setContentView(contentView);
        mPopupWindowNo.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }


    private void getPayInfo() {
        balance = 0;
        joinNumber = new ArrayList<>();
        NetWork.getInstance()
                .setTag(Qurl.message)
                .getApiService(ModuleApi.class)
                .message()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardMessageBeab>(SignInActivity.this, false, true) {

                    @Override
                    public void onSuccess(MyCardMessageBeab result) {
                        if (result == null) {
                            toast("获取数据异常！");
                            return;
                        }
                        joinNumber = result.joinNumber;
                        balance = result.balance;
                        showPopupWindowPay(result.balance, result.joinNumber);
                    }
                });
    }

    /**
     * @param remainMoney 剩余余额
     * @param weight      购买多少份
     */
    private List<Integer> joinNumber;
    private double balance;

    private void showPopupWindowPay(double remainMoney, List<Integer> weight) {
        mPayChannel = 2;
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwind_signin_pay, null);
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mTvFinish = (TextView) contentView.findViewById(R.id.tv_sign_cancle);
        mTvFinish.setOnClickListener(this);
        mPopupWindow.setContentView(contentView);

        mTvSignOne = contentView.findViewById(R.id.tv_sign_one);
        mTvSignTwo = contentView.findViewById(R.id.tv_sign_two);
        mTvSignThree = contentView.findViewById(R.id.tv_sign_three);
        mTvSignFour = contentView.findViewById(R.id.tv_sign_four);
        mTvSignRemainMoney = contentView.findViewById(R.id.tv_iv_sign_remain_money);
        mTvSignCancle = contentView.findViewById(R.id.tv_sign_cancle);
        mTvSignSure = contentView.findViewById(R.id.tv_sign_sure);
        mIvSignZfYE = contentView.findViewById(R.id.iv_sign_zf_ye);
        mIvSignZfZFB = contentView.findViewById(R.id.iv_sign_zf_zfb);

        mTvSignOne.setOnClickListener(this);
        mTvSignTwo.setOnClickListener(this);
        mTvSignThree.setOnClickListener(this);
        mTvSignFour.setOnClickListener(this);
        mTvSignCancle.setOnClickListener(this);
        mTvSignSure.setOnClickListener(this);
        mIvSignZfYE.setOnClickListener(this);
        mIvSignZfZFB.setOnClickListener(this);
        try {
            mTvSignOne.setText(weight.get(0) + "份");
            mPayNum = weight.get(0);
            mTvSignTwo.setText(weight.get(1) + "份");
            mTvSignThree.setText(weight.get(2) + "份");
            mTvSignFour.setText(weight.get(3) + "份");
            mTvSignRemainMoney.setText("淘觅觅可用余额(" + remainMoney + "元)");

        } catch (Exception e) {
            mTvSignOne.setVisibility(View.INVISIBLE);
            mTvSignTwo.setVisibility(View.INVISIBLE);
            mTvSignThree.setVisibility(View.INVISIBLE);
            mTvSignFour.setVisibility(View.INVISIBLE);
            mTvSignRemainMoney.setText("淘觅觅可用余额(0元)");
        }
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    private int mPayChannel = 2;//1是支付宝，2是余额；默认余额

    /**
     * 更改支付方式
     *
     * @param viewId
     */
    private void changePayChannel(int viewId) {
        if (viewId == R.id.iv_sign_zf_ye) {
            mPayChannel = 2;
            mIvSignZfYE.setImageResource(R.drawable.ic_sign_selected);
            mIvSignZfZFB.setImageResource(R.drawable.ic_sign_unselected);
        } else if (viewId == R.id.iv_sign_zf_zfb) {
            mPayChannel = 1;
            mIvSignZfYE.setImageResource(R.drawable.ic_sign_unselected);
            mIvSignZfZFB.setImageResource(R.drawable.ic_sign_selected);
        }
    }


    private int mPayNum = 0;//默认支付一份

    /**
     * 更改支付数量
     *
     * @param viewId
     */
    private void changePayNum(int viewId) {
        if (viewId == R.id.tv_sign_one) {
            mTvSignOne.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_selected));
            mTvSignTwo.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignThree.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignFour.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignOne.setTextColor(getResources().getColor(R.color.white));
            mTvSignTwo.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignThree.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignFour.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            if (joinNumber != null && joinNumber.size() >= 1 && !TextUtils.isEmpty(joinNumber.get(0) + "")) {
                mPayNum = joinNumber.get(0);
            } else {
                mPayNum = 0;
            }

        } else if (viewId == R.id.tv_sign_two) {
            mTvSignOne.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignTwo.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_selected));
            mTvSignThree.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignFour.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignOne.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignTwo.setTextColor(getResources().getColor(R.color.white));
            mTvSignThree.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignFour.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            if (joinNumber != null && joinNumber.size() >= 2 && !TextUtils.isEmpty(joinNumber.get(1) + "")) {
                mPayNum = joinNumber.get(1);
            } else {
                mPayNum = 0;
            }
        } else if (viewId == R.id.tv_sign_three) {
            mTvSignOne.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignTwo.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignThree.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_selected));
            mTvSignFour.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignOne.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignTwo.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignThree.setTextColor(getResources().getColor(R.color.white));
            mTvSignFour.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            if (joinNumber != null && joinNumber.size() >= 3 && !TextUtils.isEmpty(joinNumber.get(2) + "")) {
                mPayNum = joinNumber.get(2);
            } else {
                mPayNum = 0;
            }
        } else if (viewId == R.id.tv_sign_four) {
            mTvSignOne.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignTwo.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignThree.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_unselected));
            mTvSignFour.setBackground(getResources().getDrawable(R.drawable.bg_sign_num_selected));
            mTvSignOne.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignTwo.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignThree.setTextColor(getResources().getColor(R.color.c_2F2F2F));
            mTvSignFour.setTextColor(getResources().getColor(R.color.white));
            if (joinNumber != null && joinNumber.size() >= 4 && !TextUtils.isEmpty(joinNumber.get(3) + "")) {
                mPayNum = joinNumber.get(3);
            } else {
                mPayNum = 0;
            }
        }
    }

    //弹出确定支付的提示
    private void showPopupWindowPayTips() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwind_signin_pay_tips, null);
        mPopupWindowTips = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindowTips.setContentView(contentView);

        mIvSignPayTipsClose = contentView.findViewById(R.id.iv_sign_pay_tips_close);
        mIvSignPayTips = contentView.findViewById(R.id.iv_sign_pay_tips);
        mTvSignPayTips = contentView.findViewById(R.id.tv_sign_pay_tips);
        mTvSignPayTipsyeValue = contentView.findViewById(R.id.tv_sign_pay_tips_ye_value);
        mTvSignPayTipsSurePay = contentView.findViewById(R.id.tv_sign_pay_tips_sure_pay);
        mTvSignPayTipsPayMoney = contentView.findViewById(R.id.iv_sign_pay_tips_pay_money);


        mIvSignPayTips.setImageResource(mPayChannel == 2 ? R.drawable.ic_sign_tmm : R.drawable.ic_sign_zfb);
        mTvSignPayTips.setText(mPayChannel == 2 ? "淘觅觅可用余额" : "支付宝支付");
        mTvSignPayTipsyeValue.setText(mPayChannel == 2 ? "¥ " + balance : "");

        if (mPayNum > 0) {
            mTvSignPayTipsPayMoney.setText(mPayNum + "");
        }
        if (mPayChannel == 2 && mPayNum > balance) {
            toast("淘觅觅可提现余额不足！");
            return;
        }

        mIvSignPayTipsClose.setOnClickListener(this);
        mIvSignPayTips.setOnClickListener(this);
        mTvSignPayTips.setOnClickListener(this);
        mTvSignPayTipsyeValue.setOnClickListener(this);
        mTvSignPayTipsSurePay.setOnClickListener(this);
        mTvSignPayTipsPayMoney.setOnClickListener(this);

        mPopupWindowTips.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    private void startPayInfo() {
        if (mPayNum == 0) {
            toast("参数错误");
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("joinNumber", mPayNum + "");
        param.put("type", mPayChannel + "");
        NetWork.getInstance()
                .setTag(Qurl.startPay)
                .getApiService(ModuleApi.class)
                .startPay(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardStratPayBean>(SignInActivity.this, false, true) {

                    @Override
                    public void onSuccess(MyCardStratPayBean result) {
                        if (result == null) {
                            toast("获取支付数据异常！");
                            return;
                        }
                        if (result.status == 200) {
                            if (mPayChannel == 1) {//支付宝支付，获取支付宝订单签名去支付宝支付
                                if (result.returnMap == null || TextUtils.isEmpty(result.returnMap.sign) || TextUtils.isEmpty(result.returnMap.tradNo)) {
                                    toast("获取支付数据异常！");
                                    return;
                                }
                                PayUtils payUtils = new PayUtils(SignInActivity.this);
                                AlipayEntity alipayEntity = new AlipayEntity();
                                alipayEntity.setOrderid(result.returnMap.tradNo);
                                alipayEntity.setOrderstring(result.returnMap.sign);
                                payUtils.payByAliPay(alipayEntity);
                                payUtils.setResultListener(SignInActivity.this);
                            } else {//余额支付，关闭弹窗
                                getData();
                                release();
                            }
                        } else if (result.status == 500) {
                            toast(result.errMsg);
                        }
                    }
                });
    }

    private void notyfyInfoUpdata(String tradNo) {

        Map<String, Object> param = new HashMap<>();
        param.put("tradNo", tradNo);
        NetWork.getInstance()
                .setTag(Qurl.notify)
                .getApiService(ModuleApi.class)
                .notify(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MyCardNotifyBean>(SignInActivity.this, false, true) {

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


    @Override
    public void aliPayCallBack(String orderId) {//需要回调给后台通知，
        release();
        notyfyInfoUpdata(orderId);
    }

    @Override
    public void aliPayCancle(String errorInfo) {//支付被取消
    }

    @Override
    public void aliPayFailOther(String errorCode, String errorInfo) {//支付失败
    }

    private void release() {
        if (mPopupWindowTips != null) {
            mPopupWindowTips.dismiss();
            mPopupWindowTips = null;
        }

        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        //今日打卡状态
        getTodayState();
        //参与打卡状态
        getParticipateState();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {
        mSmartRecord.finishRefresh(state);
    }
}
