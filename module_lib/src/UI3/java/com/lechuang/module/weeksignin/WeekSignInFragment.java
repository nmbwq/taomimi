package java.com.lechuang.module.weeksignin;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.DeviceUtils;
import com.common.app.view.NumSeekBar;
import com.lechuang.module.R;
import com.umeng.commonsdk.debug.E;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.WeekSiginRunBean;
import java.com.lechuang.module.bean.WeekSignInBean;
import java.util.ArrayList;
import java.util.List;

@Route(path = ARouters.PATH_WEEK_SIGNIN)
public class WeekSignInFragment extends LazyBaseFragment implements View.OnClickListener {
    private ViewPager mVpWeekSignIn;
    private List<Fragment> mFragments;
    private LinearLayout mLlWeekSigninSignParentIv;
    private LinearLayout mLlWeekSigninSignParentTv;
    private List<ImageView> mSignImage;
    private List<TextView> mSignTv;
    private TextView mTvWeekSigninDay,mTvWeekSigninRule,mTvWeekSigninSign,mTvWeekSigninSignDayTip;
    private ImageView mTvWeekQuickSigninTip;
    private FragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_week_signin;
    }

    @Override
    protected void findViews() {
        mVpWeekSignIn = $(R.id.vp_week_signin);
        mTvWeekSigninDay = $(R.id.tv_week_signin_day);
        mTvWeekSigninRule = $(R.id.tv_week_signin_rule);
        mTvWeekQuickSigninTip = $(R.id.tv_week_quick_sign_tip);
        mTvWeekSigninSign = $(R.id.tv_week_signin_sign);
        mTvWeekSigninSignDayTip = $(R.id.tv_week_signin_day_tip);
        mLlWeekSigninSignParentIv = $(R.id.ll_week_signin_sign_parent);
        mLlWeekSigninSignParentTv = $(R.id.ll_week_signin_weekday);
        $(R.id.tv_week_signin_sign).setOnClickListener(this);
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_week_signin_rule).setOnClickListener(this);
        mTvWeekSigninRule.setText("活" +"\n" + "动" + "\n" + "规" + "\n" + "则");

    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void getData() {
        getProduct();
    }

    private int mOpStatus;//签到状态
    private void getProduct() {
        NetWork.getInstance()
                .setTag(Qurl.signShowsInfo)
                .getApiService(ModuleApi.class)
                .signShowsInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<WeekSignInBean>(getActivity(), false, true) {

                    @Override
                    public void onSuccess(WeekSignInBean result) {
                        if (result != null && result.list == null && result.list.size() < 0) {
                            return;
                        }
                        if (result.getDay() <= 0){
                            mTvWeekSigninSignDayTip.setText("连续签到就可抽取丰厚大礼哦~");
                        }else {
                            mTvWeekSigninSignDayTip.setText("连续签到"+ result.getDay() + "天就可抽取丰厚大礼哦~");
                        }

                        SpannableString ss1 = new SpannableString("已连续签到" + result.getSignNumber() + "天");
                        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_ED4C4A)), 5, (result.getSignNumber() + "").length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss1.setSpan(new RelativeSizeSpan(1.7f), 5, (result.getSignNumber() + "").length() + 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        mTvWeekSigninDay.setText(ss1);

                        if (mSignImage == null){
                            mSignImage = new ArrayList<>();
                        }
                        mSignImage.clear();
                        if (mSignTv == null){
                            mSignTv = new ArrayList<>();
                        }
                        mSignTv.clear();
                        mLlWeekSigninSignParentIv.removeAllViews();
                        mLlWeekSigninSignParentTv.removeAllViews();

                        int signDay = result.dayStr.size();

                        int measuredWidthIv = mLlWeekSigninSignParentIv.getMeasuredWidth();
                        int measuredWidthTv = mLlWeekSigninSignParentTv.getMeasuredWidth();

                        int dimensionIv = (int) getResources().getDimension(R.dimen.dp_27);
                        int leftMarginIv = (measuredWidthIv - signDay * dimensionIv) / (signDay - 1);


                        int dimensionTv = (int) getResources().getDimension(R.dimen.dp_40);
                        int leftMarginTv = (measuredWidthTv - signDay * dimensionTv) / (signDay - 1);

                        for (int i = 0; i < signDay;i++ ){
                            ImageView iv = new ImageView(getActivity());
                            LinearLayout.LayoutParams paramsIv = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.dp_27),(int)getResources().getDimension(R.dimen.dp_27));
                            if ( i > 0){
                                paramsIv.setMargins(leftMarginIv,0,0,0);
                            }
                            iv.setLayoutParams(paramsIv);
                            iv.setImageResource(R.drawable.week_signin_sign_selecter);
                            mSignImage.add(iv);
                            mLlWeekSigninSignParentIv.addView(iv);

                            TextView tv = new TextView(getActivity());
                            LinearLayout.LayoutParams paramsTv = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.dp_40),ViewPager.LayoutParams.WRAP_CONTENT);
                            if ( i > 0){
                                paramsTv.setMargins(leftMarginTv,0,0,0);
                            }
                            tv.setLayoutParams(paramsTv);
                            tv.setTextSize(DeviceUtils.px2dp(35));
                            tv.setTextColor(getResources().getColor(R.color.c_CFCFCF));
                            tv.setGravity(Gravity.CENTER);
                            tv.setText(result.dayStr.get(i).timeStr);
                            mSignTv.add(tv);
                            mLlWeekSigninSignParentTv.addView(tv);

                            String status = result.dayStr.get(i).status;
                            if (i == 0 && TextUtils.equals(status,"0")){
                                mTvWeekQuickSigninTip.setVisibility(View.VISIBLE);
                            }else if (i ==0 ){
                                mTvWeekQuickSigninTip.setVisibility(View.INVISIBLE);
                            }
                            if (TextUtils.equals(status,"0")){
                                mSignImage.get(i).setSelected(false);
                            }else if (TextUtils.equals(status,"1")){
                                mSignImage.get(i).setSelected(true);
                                mSignTv.get(i).setTextColor(getResources().getColor(R.color.c_ED4C4A));
                            }

                        }


                        int opStatus = result.getOpStatus();//签到状态
                        mOpStatus = opStatus;
                        if (opStatus == 0){//未参与 -> 立即签到
                            mTvWeekSigninSign.setText("立即签到");
                        }else if (opStatus == 1){//立即签到
                            mTvWeekSigninSign.setText("立即签到");
                        }else if (opStatus == 2){//已签到
                            mTvWeekSigninSign.setText("已签到");
                        }else if (opStatus == 3){//领取奖励
                            mTvWeekSigninSign.setText("领取奖励");
                        }else if (opStatus == 4){//已结束 -> 领取奖励
                            mTvWeekSigninSign.setText("领取奖励");
                        }
                        if (mFragmentPagerAdapter != null){
                            return;
                        }
                        setVpContent(result,result.showSort);

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

    private String mRule = "";
    private void setVpContent(WeekSignInBean result, int showSort) {
        try {
            mRule = result.regular;
            if (mFragments == null){
                mFragments = new ArrayList<>();
            }

            mFragments.clear();

            for (int i = 0; i < result.list.size(); i++) {

                Fragment signinContent = (Fragment) ARouter.getInstance().build(ARouters.PATH_WEEK_SIGNIN_CONTENT).navigation();
//                WeekSignInVpContentFragment signinContent = new WeekSignInVpContentFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("signinContent", result.list.get(i));
                bundle.putInt("position", i);
                signinContent.setArguments(bundle);
                mFragments.add(signinContent);

            }

            mVpWeekSignIn.setOffscreenPageLimit(mFragments.size());
            float dimension = getResources().getDimension(R.dimen.dp_10);
            mVpWeekSignIn.setPageMargin((int) dimension);

            if (mFragmentPagerAdapter == null){
                mFragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
                    @Override
                    public Fragment getItem(int position) {
                        return mFragments.get(position);
                    }

                    @Override
                    public int getCount() {
                        return mFragments.size();
                    }
                };
                mVpWeekSignIn.setAdapter(mFragmentPagerAdapter);
            }
            mVpWeekSignIn.setCurrentItem(showSort);





        }catch (Exception e){
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back){
            getActivity().finish();
        }else if (id == R.id.tv_week_signin_sign){//立即签到，领取奖励

            if (mOpStatus == 1){
                //去签到
                goRunSign();
            }else if (mOpStatus == 3){
                //领取奖励
                getReward();
            }else if (mOpStatus == 0 || mOpStatus == 4){//未参与和已结束
                //弹出框
                showPopuwind();
            }
        }else if (id == R.id.tv_week_signin_rule){//活动规则
            Intent intent = new Intent(getActivity(),WeekSigninRuleActivity.class);
            intent.putExtra("rule",mRule);
            startActivity(intent);
        }
    }

    /**
     * 去签到
     */
    private void goRunSign() {
        NetWork.getInstance()
                .setTag(Qurl.signRun)
                .getApiService(ModuleApi.class)
                .signRun()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<WeekSiginRunBean>(getActivity(), false, true) {

                    @Override
                    public void onSuccess(WeekSiginRunBean result) {
                        if (result == null) {
                            return;
                        }
                        toast(result.day);
                        //刷新界面数据。
                        getProduct();
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

    /**
     * 领取奖励
     */
    private void getReward() {
        NetWork.getInstance()
                .setTag(Qurl.getReward)
                .getApiService(ModuleApi.class)
                .getReward()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<WeekSiginRunBean>(getActivity(), false, true) {

                    @Override
                    public void onSuccess(WeekSiginRunBean result) {
                        if (result == null) {
                            return;
                        }
                        showSuccessPopuwind(false,result.winInfo,result.weChatID);
                        //刷新界面数据
                        getProduct();
                    }

                    @Override
                    public void onFailed_11003(WeekSiginRunBean result) {
                        super.onFailed_11003(result);
                        showSuccessPopuwind(true,result.Msg,"");
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

    /**
     * 手慢无，去参与
     * 恭喜您抽中奖品
     *
     */
    private PopupWindow mPopupWindow;
    private void showSuccessPopuwind(boolean isSuccess, String info, final String wechatNum) {
        View contentView = LayoutInflater.from(getActivity()).inflate(isSuccess ? R.layout.layout_popu_week_signin_success1 : R.layout.layout_popu_week_signin_success2, null);

        if (isSuccess){
            TextView viewById = contentView.findViewById(R.id.tv_week_sign_success1);
            viewById.setText(info);
            contentView.findViewById(R.id.tv_popu_week_signin_join).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //去参与
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                }
            });
        }else {
            TextView tvPupoWeekSigninSuccessContent = contentView.findViewById(R.id.tv_popu_week_signin_success_content);
            TextView tvPupoWeekSigninSuccessCopy = contentView.findViewById(R.id.tv_popu_week_signin_wechat_num);
            tvPupoWeekSigninSuccessContent.setText(info);
            tvPupoWeekSigninSuccessCopy.setText(wechatNum);
            contentView.findViewById(R.id.ll_popu_week_signin_success_copy_wechat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                    try {
                        if (!TextUtils.isEmpty(wechatNum.trim())){
                            // 获取系统剪贴板
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                            ClipData clipData = ClipData.newPlainText("app_kefu", TextUtils.isEmpty(wechatNum) ? "" : wechatNum);

                            // 把数据集设置（复制）到剪贴板
                            clipboard.setPrimaryClip(clipData);
                            getWechatApi();
                        }else {
                            toast("复制失败！");
                        }
                    }catch (Exception e){

                    }
                }
            });


        }

        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    private void showPopuwind() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popu_week_signin_go, null);
        contentView.findViewById(R.id.tv_popu_week_signin_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去参与
                if (mPopupWindow != null){
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.WEEK_SIGN_REFRESH)) {
            getProduct();
        }
    }

    /**
     * 跳转到微信
     */
    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast( "检查到您手机没有安装微信，请安装后使用该功能" );
        }
    }
}
