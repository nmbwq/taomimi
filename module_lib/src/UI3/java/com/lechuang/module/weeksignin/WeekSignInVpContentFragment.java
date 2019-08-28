package java.com.lechuang.module.weeksignin;

import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.DeviceUtils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.WeekSiginRunBean;
import java.com.lechuang.module.bean.WeekSignInBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Route(path = ARouters.PATH_WEEK_SIGNIN_CONTENT)
public class WeekSignInVpContentFragment extends LazyBaseFragment implements View.OnClickListener {

    private WeekSignInBean.WeekSignListBean mWeekSignListBean;
    private TextView mTvWeekSigninVpContentNper;
    private TextView mTvWeekSigninVpContentTime,mTvWeekSigninVpContentPrice,mTvWeekSigninVpContentVpPriceNumunit,
            mTvWeekSigninVpContentVpPriceName,mTvWeekSigninVpContentVpJoinSign;
    private ImageView mIvWeekSigninVpContentVpContent,mIvWeekSigninVpContentVpJoinFinish;
    private int mPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_week_signin_vpcontent;
    }

    @Override
    protected void findViews() {

        mTvWeekSigninVpContentTime = $(R.id.tv_week_signin_vpcontent_time);
        mTvWeekSigninVpContentNper = $(R.id.tv_week_signin_vpcontent_nper);
        mTvWeekSigninVpContentPrice = $(R.id.tv_week_signin_vpcontent_price);
        mIvWeekSigninVpContentVpContent = $(R.id.iv_week_signin_vpcontent);
        mTvWeekSigninVpContentVpPriceNumunit = $(R.id.tv_week_signin_vpcontent_price_numunit);
        mTvWeekSigninVpContentVpPriceName = $(R.id.tv_week_signin_vpcontent_name);
        mTvWeekSigninVpContentVpJoinSign = $(R.id.tv_week_signin_vpcontent_join_sign);
        mIvWeekSigninVpContentVpJoinFinish = $(R.id.iv_week_signin_vpcontent_finish);
        $(R.id.tv_week_signin_vpcontent_join_sign).setOnClickListener(this);

    }

    @Override
    protected void initView() {
        try {
            mPosition = getArguments().getInt("position");
            mWeekSignListBean = (WeekSignInBean.WeekSignListBean) getArguments().getSerializable("signinContent");
            mTvWeekSigninVpContentNper.setText("第" + mWeekSignListBean.getNper() + "期");
            mTvWeekSigninVpContentTime.setText(timeStamp2Date(mWeekSignListBean.getStartTime()+"","") +"~" + timeStamp2Date(mWeekSignListBean.getEndTime()+"",""));
            mTvWeekSigninVpContentPrice.setText(mWeekSignListBean.getPrice() + "");
            mTvWeekSigninVpContentVpPriceNumunit.setText("共" + mWeekSignListBean.getNumber() + "个");
            mTvWeekSigninVpContentVpPriceName.setText(mWeekSignListBean.getPrizeName());
            mWeekSignListBean.setPrizePic(mWeekSignListBean.getPrizePic());
            Glide.with(BaseApplication.getApplication()).load(mWeekSignListBean.getPrizePic()).into(mIvWeekSigninVpContentVpContent);

            if (mWeekSignListBean.getWinNumberSt() == 0){
                mIvWeekSigninVpContentVpJoinFinish.setVisibility(View.GONE);
            }else {
                mIvWeekSigninVpContentVpJoinFinish.setVisibility(View.VISIBLE);
            }



            int inStatus = mWeekSignListBean.getInStatus();
            if (inStatus == 0){//参与此期签到，亮色
                mTvWeekSigninVpContentVpJoinSign.setText("参与此期签到");
                mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.c_864B19));
                mTvWeekSigninVpContentVpJoinSign.setClickable(true);
                mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_sign));
            }else if (inStatus == 1){//已参与，暗色
                mTvWeekSigninVpContentVpJoinSign.setText("已参与");
                mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.white));
                mTvWeekSigninVpContentVpJoinSign.setClickable(false);
                mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_unsign));
            }else if (inStatus == 2){//已结束，暗色
                mTvWeekSigninVpContentVpJoinSign.setText("已结束");
                mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.white));
                mTvWeekSigninVpContentVpJoinSign.setClickable(false);
                mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_unsign));
            }

        }catch (Exception e){

        }

    }

    @Override
    protected void getData() {

    }

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
                        mWeekSignListBean = result.list.get(mPosition);
                        mTvWeekSigninVpContentNper.setText("第" + mWeekSignListBean.getNper() + "期");
                        mTvWeekSigninVpContentTime.setText(timeStamp2Date(mWeekSignListBean.getStartTime()+"","") +"~" + timeStamp2Date(mWeekSignListBean.getEndTime()+"",""));
                        mTvWeekSigninVpContentPrice.setText(mWeekSignListBean.getPrice() + "");
                        mTvWeekSigninVpContentVpPriceNumunit.setText("共" + mWeekSignListBean.getNumber() + "个");
                        mTvWeekSigninVpContentVpPriceName.setText(mWeekSignListBean.getPrizeName());
                        mWeekSignListBean.setPrizePic(mWeekSignListBean.getPrizePic());
                        Glide.with(BaseApplication.getApplication()).load(mWeekSignListBean.getPrizePic()).into(mIvWeekSigninVpContentVpContent);

                        if (mWeekSignListBean.getWinNumberSt() == 0){
                            mIvWeekSigninVpContentVpJoinFinish.setVisibility(View.GONE);
                        }else {
                            mIvWeekSigninVpContentVpJoinFinish.setVisibility(View.VISIBLE);
                        }



                        int inStatus = mWeekSignListBean.getInStatus();
                        if (inStatus == 0){//参与此期签到，亮色
                            mTvWeekSigninVpContentVpJoinSign.setText("参与此期签到");
                            mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.c_864B19));
                            mTvWeekSigninVpContentVpJoinSign.setClickable(true);
                            mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_sign));
                        }else if (inStatus == 1){//已参与，暗色
                            mTvWeekSigninVpContentVpJoinSign.setText("已参与");
                            mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.white));
                            mTvWeekSigninVpContentVpJoinSign.setClickable(false);
                            mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_unsign));
                        }else if (inStatus == 2){//已结束，暗色
                            mTvWeekSigninVpContentVpJoinSign.setText("已结束");
                            mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.white));
                            mTvWeekSigninVpContentVpJoinSign.setClickable(false);
                            mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_unsign));
                        }
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
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format    yyyy-MM-dd HH:mm:ss
     * @return
     */
     private String timeStamp2Date(String seconds,String format) {
         if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
             return "";
         }
         if(format == null || format.isEmpty()){
             format = "yyyy-MM-dd";
         }
         SimpleDateFormat sdf = new SimpleDateFormat(format);
         return sdf.format(new Date(Long.valueOf(seconds)));
     }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_week_signin_vpcontent_join_sign){
            participate();
        }
    }

    /**
     * 参与此期奖励
     */
    private void participate() {
        Map<String,Object> parma = new HashMap<>();
        if (mWeekSignListBean != null){
            parma.put("id",mWeekSignListBean.getId());
        }else {
            parma.put("id","0");
        }
        NetWork.getInstance()
                .setTag(Qurl.getReward)
                .getApiService(ModuleApi.class)
                .participate(parma)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(getActivity(), false, true) {

                    @Override
                    public void onSuccess(String result) {
//                        if (result != null) {
//                            return;
//                        }
                        toast(result);
//                        mTvWeekSigninVpContentVpJoinSign.setText("已参与");
//                        mTvWeekSigninVpContentVpJoinSign.setTextColor(getResources().getColor(R.color.white));
//                        mTvWeekSigninVpContentVpJoinSign.setClickable(false);
//                        mTvWeekSigninVpContentVpJoinSign.setBackground(getResources().getDrawable(R.drawable.bg_week_signin_unsign));

                        getProduct();
                        //通知刷新界面
                        EventBus.getDefault().post(Constant.WEEK_SIGN_REFRESH);
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

}
