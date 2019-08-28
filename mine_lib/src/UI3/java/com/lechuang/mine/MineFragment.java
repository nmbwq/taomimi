package java.com.lechuang.mine;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.database.UserInfoBeanDao;
import com.common.app.database.bean.UserInfoBean;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.FileUtils;
import com.common.app.utils.LogUtils;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.Utils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.mine.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.mine.adapter.MZBannerViewHolder;
import java.com.lechuang.mine.bean.MineHuoDongBean;
import java.com.lechuang.mine.bean.MineUserBean;
import java.com.lechuang.module.adress.AdressListActivity;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_MINE)
public class MineFragment extends LazyBaseFragment implements View.OnClickListener, OnRefreshLoadMoreListener {
    private ImageView  ivSet,mIvHuiyuan, ivNews, ivPhono,ivDong1,ivDong2;
    private TextView tvMoney, tvSettlement, tvEarnings,tvMoneyZhuan,guanLiMoney
            , tvForecast, tvInviteCode, tvFensi, tvName,tvShangyueyugu;

    private LinearLayout myOrder, myFans, keFu, shouChang, yiJian
            , tvInviteCodeCopy;
    private RelativeLayout tvWithdrawal,mRlup2,mRlup1,mRlKefu, xinShou, wenTi, gongGao, about, guanLi,mJinTieTiXian,rl_my_address,mLlFindOrder,mIvMiKa,mineMyEarnings;
    private View mVxian;
//    private View gongLue;

    private String mCacheSize = "";//获取缓存的大小
    private MZBannerView mBannerMineHuodong;
    private SmartRefreshLayout mSmartEarning;
    private ClassicsHeader mSmartEarningHeader;
    private View mVsCommonWevError;
    private boolean mLoadError = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_mine;
    }

    @Override
    protected void findViews() {
        ivSet = $(R.id.iv_set);
        mSmartEarning = $ ( R.id.smart_earnings );
        mSmartEarningHeader = $ ( R.id.smart_earnings_header );
        ivNews = $(R.id.iv_news);
        ivPhono = $(R.id.iv_photo);
        tvName = $(R.id.tv_name);
        tvFensi = $(R.id.tv_fensi);
        tvInviteCode = $(R.id.tv_InviteCode);
        tvInviteCodeCopy = $(R.id.ll_yaoqingma_parent);
        tvForecast = $(R.id.tv_forecast);
        tvEarnings = $(R.id.tv_earnings);
        tvSettlement = $(R.id.tv_settlement);
        tvMoney = $(R.id.tv_money);
        tvMoneyZhuan = $(R.id.tv_zhuan);
        tvWithdrawal = $(R.id.tv_withdrawal);
        mRlup1 = $(R.id.up_1);
        mRlup2 = $(R.id.up_2);
        mIvHuiyuan = $(R.id.iv_mine_huiyuan);
        tvShangyueyugu = $(R.id.tv_shangyueyugu);
        mRlKefu = $(R.id.rl_mine_weikefu);
        mIvMiKa = $(R.id.iv_mika);
        mJinTieTiXian = $(R.id.rl_mine_jintie);
        mVxian = $(R.id.v_xian);
        rl_my_address = $(R.id.rl_my_address);
        mLlFindOrder = $(R.id.rl_find_order);
//        ivDong1 = $(R.id.iv_hot_photo1);
//        ivDong2 = $(R.id.iv_hot_photo2);




        mineMyEarnings = $(R.id.shouyi);
        myOrder = $(R.id.ll_myOrder);
        myFans = $(R.id.ll_myFans);

//        gongLue = $(R.id.v_gonglue);

        xinShou = $(R.id.tv_xinshou);
        wenTi = $(R.id.tv_wenti);
        keFu = $(R.id.tv_kefu);
        gongGao = $(R.id.tv_gonggao);
        shouChang = $(R.id.tv_shouchang);
        yiJian = $(R.id.tv_yijian);
        about = $(R.id.tv_about);
        guanLi = $(R.id.tv_guanli);
        guanLiMoney = $(R.id.tv_guanli_money);

//        mBannerMineHuodong = $(R.id.banner_mine_huodong);
        mVsCommonWevError = $ ( R.id.vs_common_web_error );


        $(R.id.rl_yaoqinghaoyou).setOnClickListener(this);
        $( R.id.ll_taobao_gouwu ).setOnClickListener( this );

        rl_my_address.setOnClickListener(this);
        ivSet.setOnClickListener(this);
        ivNews.setOnClickListener(this);
        mineMyEarnings.setOnClickListener(this);
        myOrder.setOnClickListener(this);
        myFans.setOnClickListener(this);
        tvWithdrawal.setOnClickListener(this);
//        gongLue.setOnClickListener(this);
        xinShou.setOnClickListener(this);
        wenTi.setOnClickListener(this);
        keFu.setOnClickListener(this);
        gongGao.setOnClickListener(this);
        shouChang.setOnClickListener(this);
        yiJian.setOnClickListener(this);
        about.setOnClickListener(this);
        guanLi.setOnClickListener(this);
        tvInviteCodeCopy.setOnClickListener(this);
        mRlKefu.setOnClickListener(this);
        mIvMiKa.setOnClickListener(this);
        mLlFindOrder.setOnClickListener(this);
        $ ( R.id.tv_common_hint ).setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
//                mLoadError = false;
//                mVsCommonWevError.setVisibility ( View.GONE );
                getUserInfo();
//                getHuoDongData();
                getCache();
            }
        } );
        $ ( R.id.rl_mine_weikefu_cha ).setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                mRlKefu.setVisibility( View.GONE );
            }
        } );
        $(R.id.iv_photo).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mSmartEarning.setOnRefreshLoadMoreListener ( this );
        mSmartEarning.setEnableLoadMore ( false );
        ivNews.setVisibility(View.GONE);
        if (!BaseApplication.getApplication().mQueryShowHide) {
            ivNews.setVisibility(View.GONE);
            $(R.id.ll_yaoqingma_parent).setVisibility(View.INVISIBLE);
        }
//        Glide.with(BaseApplication.getApplication())
//                .load(getResources().getDrawable(R.drawable.bg_mine_dongtu))
//                .into(ivDong1);
//        Glide.with(BaseApplication.getApplication())
//                .load(getResources().getDrawable(R.drawable.bg_mine_dongtu))
//                .into(ivDong2);
    }

    @Override
    protected void getData() {
        //todo 不要再mine处理跳转到登录的界面。没登录的时候也能显示我的界面，说明程序有bug，要解决！！！！！
        if (UserHelper.getInstence().isLogin()) {
            getUserInfo();//用户信息
            getIncomeOverview();//收益信息
            getUserAllowance();//管理津贴
//            getHuoDongData();
            getCache();

        } else {
            //如果程序有bug，需要清空当前界面的数据
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        NetWork.getInstance()
                .setTag(Qurl.userInfo2)
                .getApiService(MineApi.class)
                .userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MineUserBean>(getActivity(), false, false) {

                    @Override
                    public void onSuccess(MineUserBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //设置我信息
                        setMineData(result);
                        mLoadError=false;
                        mVsCommonWevError.setVisibility ( View.GONE );
                        mSmartEarning.setVisibility ( View.VISIBLE );
                        mRlup1.setVisibility ( View.VISIBLE );
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mIsFirstVisible = false;
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState ( false, false );
                        mIsFirstVisible = false;
                        if (mLoadError&&moreInfo.indexOf( "无网络" )!=-1) {
                            mVsCommonWevError.setVisibility ( View.VISIBLE );
                            mSmartEarning.setVisibility ( View.GONE );
                            mRlup1.setVisibility ( View.GONE );
                        } else {
                            mVsCommonWevError.setVisibility ( View.GONE );
                            mSmartEarning.setVisibility ( View.VISIBLE );
                            mRlup1.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState ( false, false );
                        mIsFirstVisible = false;
                    }
                });
    }

    /**
     * 获取收益信息
     */
    private void getIncomeOverview() {
        NetWork.getInstance()
                .setTag(Qurl.incomeOverview)
                .getApiService(MineApi.class)
                .incomeOverview()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MineUserBean>(getActivity(), false, false) {

                    @Override
                    public void onSuccess(MineUserBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
//                        totalIncome
                        DecimalFormat df = new DecimalFormat("#####0.00");
                        //上月预估 lastMonthIncome
                        tvShangyueyugu.setText( "¥ " + df.format(Double.parseDouble(result.lastMonthIncome)) );//上月预估
                        tvForecast.setText("¥ " + df.format(Double.parseDouble(result.estimatedIncome)));//本月预估
                        tvEarnings.setText("¥ " + df.format(Double.parseDouble(result.todayIncome)));//今日收益
                        tvSettlement.setText("¥ " + df.format(Double.parseDouble(result.completeIncome)));//上月结算
                        if (result.withdrawMoney.equals ( "0" )){
                            tvMoney.setText("" + result.withdrawMoney);
                        }else {
                            tvMoney.setText("" + df.format(Double.parseDouble(result.withdrawMoney)));//可提现金额
                        }
                        //总收益
                        tvMoneyZhuan.setText(""+df.format(Double.parseDouble(result.totalIncome)));
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mIsFirstVisible = false;
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState ( false, false );
                        mIsFirstVisible = false;
                        if (mLoadError&&moreInfo.indexOf( "无网络" )!=-1) {
                            mVsCommonWevError.setVisibility ( View.VISIBLE );
                            mSmartEarning.setVisibility ( View.GONE );
                            mRlup1.setVisibility ( View.GONE );
                        } else {
                            mVsCommonWevError.setVisibility ( View.GONE );
                            mSmartEarning.setVisibility ( View.VISIBLE );
                            mRlup1.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState ( false, false );
                        mIsFirstVisible = false;
                    }
                });
    }

    /**
     * 获取管理津贴信息
     */
    private void getUserAllowance() {
        NetWork.getInstance()
                .setTag(Qurl.incomeOverview)
                .getApiService(MineApi.class)
                .userAllowance()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MineUserBean>(getActivity(), false, false) {

                    @Override
                    public void onSuccess(final MineUserBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
//                        totalIncome
                        DecimalFormat df = new DecimalFormat("#####0.00");
                        //管理津贴 guanLiMoney
                        double money=Double.parseDouble(result.estimateMoney)+Double.parseDouble(result.allowanceMoney);
                        guanLiMoney.setText( "" + df.format(money) );//管理津贴
                        mJinTieTiXian.setOnClickListener(new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                ARouter.getInstance().build(ARouters.PATH_WITHDRAWL_ALLOWANCE).navigation();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mIsFirstVisible = false;
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        setRefreshLoadMoreState ( false, false );
                        mIsFirstVisible = false;
                        if (mLoadError&&moreInfo.indexOf( "无网络" )!=-1) {
                            mVsCommonWevError.setVisibility ( View.VISIBLE );
                            mSmartEarning.setVisibility ( View.GONE );
                            mRlup1.setVisibility ( View.GONE );
                        } else {
                            mVsCommonWevError.setVisibility ( View.GONE );
                            mSmartEarning.setVisibility ( View.VISIBLE );
                            mRlup1.setVisibility ( View.VISIBLE );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshLoadMoreState ( false, false );
                        mIsFirstVisible = false;
                    }
                });
    }

    /**
     * 获取活动信息
     */
    private boolean mIsLoadingShareImg = false;
    private void getHuoDongData() {
        if (mIsLoadingShareImg){
            return;
        }
        mIsLoadingShareImg = true;
        NetWork.getInstance()
                .setTag(Qurl.active)
                .getApiService(MineApi.class)
                .getHuoDong()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<MineHuoDongBean>(getActivity(), false, false) {

                    @Override
                    public void onSuccess(MineHuoDongBean result) {
                        /*if (result == null || result.activeList_top == null || result.activeList_top.size() <= 0 || result.activeList_top.get(0) == null) {
                            mBannerMineHuodong.setVisibility(View.GONE);
                        } else {
                            mBannerMineHuodong.setVisibility(View.VISIBLE);
                            setBannerHuoDongData(result.activeList_top);
                        }*/
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mIsLoadingShareImg = false;
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        mIsLoadingShareImg = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mIsLoadingShareImg = false;
                    }
                });
    }

    @Override
    protected void updataRequest() {
        super.updataRequest();
        //非第一次显示的时候，，获取焦点
        getData();
        ivSet.setEnabled(true);
    }

    private void getCache() {
        //存储路径文件夹
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        String glideFile = getActivity().getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        String localFile = newFile.getAbsolutePath();
        mCacheSize = FileUtils.getAutoFileOrFilesSize(glideFile, localFile);
    }

    /**
     * 处理mine界面的数据
     *
     * @param result
     */
    private void setMineData(MineUserBean result) {
        try {

            //更新数据
            if (!TextUtils.isEmpty(result.photo)) {
                String phone = UserHelper.getInstence().getUserInfo().getPhone();
                UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
                UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
                if (unique != null) {
                    unique.setPhoto(result.photo);
                    unique.setMsgState(TextUtils.equals("1", result.noticeNum));
                    unique.setAgencyLevel(result.agencyLevel);
                    unique.setMsgSize("0");
                    userInfoDao.update(unique);
                }
            }

            //用户头像
            //用户头像
            Glide.with(BaseApplication.getApplication())
                    .load(UserHelper.getInstence().getUserInfo().getPhoto())
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                    .into(ivPhono);

            tvName.setText(TextUtils.isEmpty(result.nickName) ? result.phone : result.nickName);//用户昵称(昵称为空，显示手机号)
            tvFensi.setText("粉丝数:" + result.agencyNum+"人");//粉丝数
            UserHelper.getInstence().getUserInfo().setAgencyLevel( result.agencyLevel );

            if (result.agencyLevel == 0) {
                mRlup2.setBackground(getResources().getDrawable(R.drawable.bg_mine_up2));
                mIvHuiyuan.setBackground(getResources().getDrawable(R.drawable.iv_mine_putong));
                tvFensi.setVisibility(View.INVISIBLE);//普通用户隐藏粉丝数量

                tvWithdrawal.setVisibility(View.GONE);
                guanLi.setVisibility(View.GONE);
                mVxian.setVisibility(View.GONE);
            } else if (result.agencyLevel == 1) {
//                mRlup2.setBackground(getResources().getDrawable(R.drawable.bg_mine_up1));
                mIvHuiyuan.setBackground(getResources().getDrawable(R.drawable.iv_mine_vip));
                tvFensi.setVisibility(View.VISIBLE);

                tvWithdrawal.setVisibility(View.VISIBLE);
                guanLi.setVisibility(View.GONE);
                mVxian.setVisibility(View.GONE);
            } else if (result.agencyLevel == 2) {
//                mRlup2.setBackground(getResources().getDrawable(R.drawable.bg_mine_up1));
                mIvHuiyuan.setBackground(getResources().getDrawable(R.drawable.iv_mine_yunying));
                tvFensi.setVisibility(View.VISIBLE);

                tvWithdrawal.setVisibility(View.VISIBLE);
                guanLi.setVisibility(View.VISIBLE);
                mVxian.setVisibility(View.VISIBLE);
            } else if (result.agencyLevel == 3) {
//                mRlup2.setBackground(getResources().getDrawable(R.drawable.bg_mine_up1));
                mIvHuiyuan.setBackground(getResources().getDrawable(R.drawable.iv_mine_juntuanzhang));
                tvFensi.setVisibility(View.VISIBLE);

                tvWithdrawal.setVisibility(View.VISIBLE);
                guanLi.setVisibility(View.VISIBLE);
                mVxian.setVisibility(View.VISIBLE);
            } else if (result.agencyLevel == 4) {
//                mRlup2.setBackground(getResources().getDrawable(R.drawable.bg_mine_up1));
                mIvHuiyuan.setBackground(getResources().getDrawable(R.drawable.iv_mine_fengongsi));
                tvFensi.setVisibility(View.VISIBLE);

                tvWithdrawal.setVisibility(View.VISIBLE);
                guanLi.setVisibility(View.VISIBLE);
            }


            tvInviteCode.setText(TextUtils.isEmpty(result.invitationCode) ? "" : result.invitationCode);//邀请码
            if (!TextUtils.isEmpty(result.invitationCode)){
                UserHelper.getInstence().getUserInfo().setInvitationCode( result.invitationCode );
            }

//            UserHelper.getInstence ().getUserInfo ().setZhifubaoNum ( result.alipayNumber );

        } catch (Exception e) {
            toast(e.toString());
        }

    }

    /**
     * 设置banner数据
     */
    private List<String> mBannderHuoDongData;

    private void setBannerHuoDongData(final List<MineHuoDongBean.ActiveListTopBean> activeListTop) {

        if (mBannderHuoDongData == null) {
            mBannderHuoDongData = new ArrayList<>();
        }
        mBannderHuoDongData.clear();

        for (MineHuoDongBean.ActiveListTopBean activeListTopBean : activeListTop) {
            mBannderHuoDongData.add(activeListTopBean.img);
        }

        mBannerMineHuodong.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                MineHuoDongBean.ActiveListTopBean indexBannerListBean = activeListTop.get(i);
                RouterBean routerBean = new RouterBean();
                routerBean.img = indexBannerListBean.img;
                routerBean.link = indexBannerListBean.link;
                routerBean.type = indexBannerListBean.type;
                routerBean.mustParam = indexBannerListBean.mustParam;
                routerBean.attachParam = indexBannerListBean.attachParam;
                routerBean.rootName = indexBannerListBean.rootName;
                routerBean.obJump = indexBannerListBean.obJump;
                routerBean.linkAllows = indexBannerListBean.linkAllows;
                routerBean.commandCopy = indexBannerListBean.commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
//                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
//                        .withString("loadUrl", Qurl.HOST + activeListTop.get(i).activeUrl)
//                        .withString(Constant.TITLE, TextUtils.isEmpty(activeListTop.get(i).activeName) ? "" : activeListTop.get(i).activeName)
//                        .navigation();
            }
        });
        mBannerMineHuodong.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);
        mBannerMineHuodong.setPages(mBannderHuoDongData, new MZHolderCreator<MZBannerViewHolder>() {
            @Override
            public MZBannerViewHolder createViewHolder() {
                return new MZBannerViewHolder();
            }
        });

        mBannerMineHuodong.start();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_photo){
//            ARouter.getInstance().build(ARouters.PATH_UP_GRADE_DETAILS).navigation();
//            startActivity(new Intent(getActivity(),ActivityTestActivity.class));
        }else
        if (i == R.id.iv_set) {
            AndPermission.with(getActivity())
                    .permission(Permission.Group.STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            //这里需要读写的权限
                            ARouter.getInstance().build(ARouters.PATH_SET).withString("mCacheSize", mCacheSize).navigation();
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


        } else if (i == R.id.iv_news) {//消息
//            ARouter.getInstance ().build ( ARouters.PATH_VIDEO ).navigation ();
            //跳消息
            ARouter.getInstance().build(ARouters.PATH_NEWS).navigation();
            //跳测试
//            ARouter.getInstance().build(ARouters.PATH_MY_CARD).navigation();
        } else if (i == R.id.tv_withdrawal) {//进入提现页面
            ARouter.getInstance().build(ARouters.PATH_WITHDRAW_DEPOSI).navigation();
        } else if (i == R.id.shouyi) {//进入我的收益页面
            ARouter.getInstance().build(ARouters.PATH_MYEARNINGS).navigation();
        } else if (i == R.id.ll_myOrder) {//进入我的订单页面
            ARouter.getInstance().build(ARouters.PATH_ORDER).navigation();
//            ARouter.getInstance().build(ARouters.PATH_MY_CARD).navigation();
        } else if (i == R.id.ll_myFans) {//粉丝
            ARouter.getInstance().build(ARouters.PATH_FENSI_A).navigation();
        } else if (i == R.id.rl_yaoqinghaoyou) {//分享APP
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

        } else if (i == R.id.tv_xinshou) {//进入新手页面
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.newpeople)
                    .withString(Constant.TITLE, "新手指引")
                    .withInt(Constant.TYPE, 4)
                    .navigation();

        } else if (i == R.id.tv_wenti) {//进入常见问题页面


            AndPermission.with(getActivity())
                    .permission(Permission.Group.STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            //这里需要读写的权限
                            ARouter.getInstance().build(ARouters.PATH_SET).withString("mCacheSize", mCacheSize).navigation();
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
//
//            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
//                    .withString("loadUrl", Qurl.problem)
//                    .withString(Constant.TITLE, "常见问题")
//                    .withInt(Constant.TYPE, 4)
//                    .navigation();

        } else if (i == R.id.rl_my_address){ //跳转到 地址列表界面
            startActivity( new Intent( getActivity(), AdressListActivity.class).putExtra( "isfromMe",true));
        }else if (i == R.id.tv_kefu ||i == R.id.rl_mine_weikefu) {//进入客服页面
            AndPermission.with(getActivity())
                    .permission(Permission.Group.STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            //这里需要读写的权限
                            ARouter.getInstance().build(ARouters.PATH_WX_KEFU).navigation();
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


        } else if (i == R.id.tv_gonggao) {//进入公告页面
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.gonggao)
                    .withString(Constant.TITLE, "官方公告")
                    .withInt(Constant.TYPE, 4)
                    .navigation();

        } else if (i == R.id.tv_shouchang) {//收藏
            ARouter.getInstance().build(ARouters.PATH_SHOUCANG).navigation();
        } else if (i == R.id.tv_yijian) {//反馈//足迹
            //跳转意见反馈
//            ARouter.getInstance().build(ARouters.PATH_FANKUI).navigation();
            //跳转足迹
            ARouter.getInstance().build(ARouters.PATH_ZUJI).navigation();

        } else if (i == R.id.tv_about) {//进入关于我们页面
            ARouter.getInstance().build(ARouters.PATH_ABOUT_US).navigation();

        } else if (i == R.id.tv_guanli) {
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.guanli1 + "?phone=" + UserHelper.getInstence().getUserInfo().getPhone())
                    .withString(Constant.TITLE, "管理中心")
                    .withInt(Constant.TYPE, 4)
                    .navigation();

//        } else if (i == R.id.v_gonglue) {//攻略

        } else if (i == R.id.ll_yaoqingma_parent) {
            String inviteCode = tvInviteCode.getText().toString();
            if (TextUtils.isEmpty(inviteCode)) {
                toast("激活码为空！");
                return;
            }
            ClipData clipData = ClipData.newPlainText("app_inviteCode", inviteCode);
            ((ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
            Utils.toast("复制成功");
        /*} else if (i == R.id.tv_withdrawal2) {//||i==R.id.tv_withdrawal2
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.shengji + "?userId=" + UserHelper.getInstence().getUserInfo().getId())
                    .withString(Constant.TITLE, "升级会员")
                    .withInt(Constant.TYPE, 4)
                    .navigation();*/
        }else if (i==R.id.iv_mika){
            ARouter.getInstance().build(ARouters.PATH_MY_CARD).navigation();
        }else if (i==R.id.rl_find_order){
            ARouter.getInstance().build(ARouters.PATH_FIND_TAO_ORDER).navigation();
        }else if (i == R.id.ll_taobao_gouwu) {
            showMyCartsPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.UPDATA_USERINFO)) {
            //用户头像
            Glide.with(BaseApplication.getApplication())
                    .load(UserHelper.getInstence().getUserInfo().getPhoto())
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                    .into(ivPhono);
            getCache();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mSmartEarning.finishLoadMore ();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData();
    }
    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        mSmartEarning.finishRefresh ( state );

    }

    /**
     * 跳转到淘宝购物车
     */
    public void showMyCartsPage() {
        AlibcShowParams alibcShowParams = new AlibcShowParams( OpenType.Native, false );
        AlibcTaokeParams taoke = new AlibcTaokeParams();
        Map exParams = new HashMap<>();
        exParams.put( "isv_code", "appisvcode" );
        exParams.put( "alibaba", "阿里巴巴" );//自定义参数部分，可任意增删改
        taoke.setPid( BuildConfig.ALI_PID );
        AlibcMyCartsPage alibcBasePage = new AlibcMyCartsPage();//实例化URL打开page(打开淘宝购物车界面)
        AlibcTrade.show( getActivity(), alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
            @Override
            public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
            }

            @Override
            public void onFailure(int i, String s) {
                toast( s );
            }
        } );

    }
}
