package java.com.lechuang.module.laxin;

import android.content.*;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.DeviceUtils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.umeng.commonsdk.debug.E;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.AcceptAwardBean;
import java.com.lechuang.module.bean.LaXinMainWeekBean;
import java.com.lechuang.module.bean.LaiXinMainBean;
import java.com.lechuang.module.bean.MineBangDanBean;
import java.com.lechuang.module.weeksignin.WeekSigninRuleActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = ARouters.PATH_LAXIN)
public class LaXinFragment extends LazyBaseFragment implements View.OnClickListener {

    private ImageView mIvLaXinProduct,mIvLaXinWeekLocal1,mIvLaXinWeekLocal2,mIvLaXinWeekNull;
    private LinearLayout mLlLaXinRuleParent,mLlLaXinPaiMine;
    private TextView mTvLaXinMinePaiMine,mTvLaXinMineFriends,mTvLaXinMineYaqingNum,
            mTvLaXinWeekLocal1,mTvLaXinWeekLocal2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_laxin;
    }

    @Override
    protected void findViews() {

        mIvLaXinProduct = $(R.id.iv_laxin_product);
        mLlLaXinRuleParent = $(R.id.ll_laxin_rule_parent);
        mLlLaXinPaiMine = $(R.id.ll_laxin_paiming);
        mTvLaXinMinePaiMine = $(R.id.tv_laxin_mine_paiming);
        mTvLaXinMineFriends = $(R.id.tv_laxin_yaoqing_friends);
        mTvLaXinMineYaqingNum = $(R.id.tv_laxin_yaoqing_num);
        mTvLaXinWeekLocal1 = $(R.id.tv_laxin_week_local);
        mTvLaXinWeekLocal2 = $(R.id.tv_laxin_week_local2);
        mIvLaXinWeekLocal1 = $(R.id.iv_laxin_week_local);
        mIvLaXinWeekLocal2 = $(R.id.iv_laxin_week_local2);
        mIvLaXinWeekNull = $(R.id.iv_bangdan_zanwu);
        $(R.id.iv_laxin_yaoqing_num).setOnClickListener(this);
        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_common_right).setOnClickListener(this);
        $(R.id.tv_laxin_rule).setOnClickListener(this);
        $(R.id.tv_laxin_info).setOnClickListener(this);
        mTvLaXinMineYaqingNum.setOnClickListener(this);
        mTvLaXinWeekLocal1.setOnClickListener(this);
        mTvLaXinWeekLocal2.setOnClickListener(this);
        mTvLaXinMineFriends.setOnClickListener(this);




    }

    private void changeStyle(int position){

        if (currentWeekState == position){
            return;
        }
        currentWeekState = position;
        if (currentWeekState == 0){
            mTvLaXinWeekLocal1.setTextColor(getResources().getColor(R.color.c_F64841));
            mTvLaXinWeekLocal2.setTextColor(getResources().getColor(R.color.black));
            mIvLaXinWeekLocal1.setVisibility(View.VISIBLE);
            mIvLaXinWeekLocal2.setVisibility(View.INVISIBLE);
        }else if (currentWeekState == 1){
            mTvLaXinWeekLocal1.setTextColor(getResources().getColor(R.color.black));
            mTvLaXinWeekLocal2.setTextColor(getResources().getColor(R.color.c_F64841));
            mIvLaXinWeekLocal1.setVisibility(View.INVISIBLE);
            mIvLaXinWeekLocal2.setVisibility(View.VISIBLE);
        }
        isWeek = currentWeekState;
        getActivityPaiMine();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {
        getActivityInfo();
        getActivityPaiMine();
    }

    private void getActivityInfo(){
        NetWork.getInstance()
                .setTag(Qurl.getActiveInfo)
                .getApiService(ModuleApi.class)
                .getActiveInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LaiXinMainBean>(getActivity(), false, true) {
                    @Override
                    public void onSuccess(LaiXinMainBean result) {
                        if (result == null || result.getActiveInfo() == null) {
                            return;
                        }
                        setActivityInfo(result.getActiveInfo());
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

    private int isWeek = 0;
    private int currentWeekState = 0;
    private void getActivityPaiMine(){
        Map<String,Object> param = new HashMap<>();
        param.put("isWeek",isWeek);

        NetWork.getInstance()
                .setTag(Qurl.rankingList)
                .getApiService(ModuleApi.class)
                .rankingList(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LaXinMainWeekBean>(getActivity(), false, true) {
                    @Override
                    public void onSuccess(LaXinMainWeekBean result) {
                        if (result == null) {
                            return;
                        }
                        setPaiMingInfo(result);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        clearData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        clearData();
                    }
                });
    }

    private String mRule;
    private void setActivityInfo(LaiXinMainBean.ActiveInfoBean activeInfo){
        try {
            mRule =  activeInfo.getDetailedRules();
            Glide.with(BaseApplication.getApplication()).load(activeInfo.getAwardPoolImg()).into(mIvLaXinProduct);

            List<String> regulationArr = activeInfo.getRegulationArr();
            if (regulationArr != null && regulationArr.size() > 0){
                for (int i = 0;i < regulationArr.size();i++){
                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_laxin_rule, null);
                    TextView tvLaXinRuleNum = inflate.findViewById(R.id.tv_laxin_rule_num);
                    TextView tvLaXinRule = inflate.findViewById(R.id.tv_laxin_rule);
                    tvLaXinRuleNum.setText(i + 1 + "");
                    tvLaXinRule.setText(regulationArr.get(i));
                    mLlLaXinRuleParent.addView(inflate);
                }
            }
        }catch (Exception e){

        }
    }

    private int btnType = -1;
    private String startTime = "";
    private String endTime = "";
    private String numPersion = "";
    private void setPaiMingInfo(LaXinMainWeekBean laXinMainWeekBean){
        try {

            if (TextUtils.equals(laXinMainWeekBean.position,"0")){
                mTvLaXinMinePaiMine.setText("我的排名 0");
                mTvLaXinMinePaiMine.setVisibility(View.GONE);
            }else {
                mTvLaXinMinePaiMine.setText("我的排名 " +laXinMainWeekBean.position);
                mTvLaXinMinePaiMine.setVisibility(View.VISIBLE);
            }

            numPersion = laXinMainWeekBean.count + "";
            mTvLaXinMineYaqingNum.setText("已邀请" + laXinMainWeekBean.count + "人");
            if (laXinMainWeekBean.btnType == 0){
                mTvLaXinMineFriends.setText("已邀请" + laXinMainWeekBean.count + "人");
            }else {
                mTvLaXinMineFriends.setText("领取奖励");
            }
            btnType = laXinMainWeekBean.btnType;
            startTime = laXinMainWeekBean.startTime;
            endTime = laXinMainWeekBean.endTime;

            List<LaXinMainWeekBean.RankingListBean> rankingList = laXinMainWeekBean.rankingList;
            mLlLaXinPaiMine.removeAllViews();
            if (rankingList != null && rankingList.size() > 0){
                mIvLaXinWeekNull.setVisibility(View.GONE);
                for (int i = 0;i < rankingList.size();i++){
                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_laxin_paiming,null);

                    TextView tvItemLaXinPaiMine = inflate.findViewById(R.id.tv_item_laxin_paiming);//排名
                    TextView tvItemLaXinPaiMineName = inflate.findViewById(R.id.tv_item_laxin_paiming_name);//姓名
                    TextView tvItemLaXinPaiMinePhone = inflate.findViewById(R.id.tv_item_laxin_paiming_phone);//手机号
                    TextView tvItemLaXinPaiMineNum = inflate.findViewById(R.id.tv_item_laxin_paiming_num);//邀请人数
                    ImageView ivItemLaXinPaiMinePhoto = inflate.findViewById(R.id.iv_item_laxin_paiming_photo);//头像

                    LaXinMainWeekBean.RankingListBean rankingListBean = rankingList.get(i);

                    tvItemLaXinPaiMineName.setText(rankingListBean.nickName);
                    tvItemLaXinPaiMinePhone.setText(rankingListBean.phone.substring ( 0,3 )+"****"+rankingListBean.phone.substring ( 7,11 ));
                    tvItemLaXinPaiMineNum.setText("邀请" + rankingListBean.inviteCount + "人");

                    Glide.with(BaseApplication.getApplication()).load(rankingListBean.photo).placeholder(R.drawable.ic_common_user_def).transform(new GlideRoundTransform(getContext(), (int)DeviceUtils.px2dp(114))).into(ivItemLaXinPaiMinePhoto);
                    if (rankingListBean.sort == 1){
                        tvItemLaXinPaiMine.setBackground(getResources().getDrawable(R.drawable.ic_laxin_paiming_one));
                    }else if (rankingListBean.sort == 2){
                        tvItemLaXinPaiMine.setBackground(getResources().getDrawable(R.drawable.ic_laxin_paiming_two));
                    }else if (rankingListBean.sort == 3){
                        tvItemLaXinPaiMine.setBackground(getResources().getDrawable(R.drawable.ic_laxin_paiming_three));
                    }else if (rankingListBean.sort < 10){
                        tvItemLaXinPaiMine.setText("0" + rankingListBean.sort);
                    }else {
                        tvItemLaXinPaiMine.setText(rankingListBean.sort + "");
                    }
                    mLlLaXinPaiMine.addView(inflate);

                }
            }else {
                mIvLaXinWeekNull.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_laxin_yaoqing_num){//邀请好友
            AndPermission.with(getActivity())
                    .permission(Permission.Group.STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            //这里需要读写的权限
                            ARouter.getInstance().build(ARouters.PATH_SHARE_APP).navigation();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(@NonNull List<String> permissions) {
                            if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                                //这个里面提示的是一直不过的权限
                            }
                        }
                    })
                    .start();
        }else if (id == R.id.iv_common_back){
            getActivity().finish();
        }else if (id == R.id.tv_common_right){//我的榜单
            ARouter.getInstance().build(ARouters.PATH_MINE_BANG_DAN).navigation();
        }else if (id == R.id.tv_laxin_week_local){
            changeStyle(0);
        }else if (id == R.id.tv_laxin_week_local2){
            changeStyle(1);
        }else if (id == R.id.tv_laxin_yaoqing_friends){
            if (btnType == 0 && !TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)){
                if (Integer.valueOf( numPersion )<=0){
                    return;
                }
                ARouter.getInstance()
                        .build(ARouters.PATH_MINE_BANG_DAN_INVITE)
                        .withTransition(R.anim.mine_bang_dan_into,R.anim.mine_bang_dan_out)
                        .withString("startTime",startTime)
                        .withString("endTime",endTime)
                        .withString("numPersion",numPersion)
                        .navigation(getActivity());
            }else  if (btnType == 1){
                acceptAward();
            }else {
                toast("参数异常！");
            }
        }else if (id == R.id.tv_laxin_info){//查看详情
            Intent intent = new Intent(getActivity(), WeekSigninRuleActivity.class);
            intent.putExtra("rule",mRule);
            startActivity(intent);
        }

    }

    private void clearData(){
        try {
            mTvLaXinMinePaiMine.setText("我的排名 0" );
            mTvLaXinMineYaqingNum.setText("已邀请0人");
            mTvLaXinMineFriends.setText("已邀请0人");
            mLlLaXinPaiMine.removeAllViews();
            btnType = -1;
            startTime = "";
            endTime = "";
        }catch (Exception e){

        }

    }

    private void acceptAward(){
        NetWork.getInstance()
                .setTag(Qurl.acceptAward)
                .getApiService(ModuleApi.class)
                .acceptAward()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<AcceptAwardBean>(getActivity(), false, true) {
                    @Override
                    public void onSuccess(AcceptAwardBean result) {
                        if (result == null) {
                            return;
                        }
                        showSuccessPopuwind(result.rewardInfo,result.WechatNumber,result.position + "");

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
    private void showSuccessPopuwind(String info, final String wechatNum,String num) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popu_laxin_lingqujiangli, null);

        TextView tvPupoWeekSigninSuccessContent = contentView.findViewById(R.id.tv_popu_week_signin_success_content);
        TextView tvPupoWeekSigninSuccessCopy = contentView.findViewById(R.id.tv_popu_week_signin_wechat_num);
        TextView tvPupoWeekSigninSuccessPaiMine = contentView.findViewById(R.id.tv_popu_week_signin_success_paiming);

        SpannableString ss1 = new SpannableString("本周排名第" + num + "名");
        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_FFF73C)), 5, num.length() + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(new TypefaceSpan("default"), 5, num.length() + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPupoWeekSigninSuccessPaiMine.setText(ss1);

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

        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
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
