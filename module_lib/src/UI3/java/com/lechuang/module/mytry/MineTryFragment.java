package java.com.lechuang.module.mytry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.androidkun.xtablayout.XTabLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.FragmentActivity;
import com.common.app.base.LazyBaseFragment;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.lechuang.module.R;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.JoinSuccessBean;
import java.com.lechuang.module.bean.ShowInMyBean;
import java.com.lechuang.module.bean.ShowInMyCardBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = ARouters.PATH_Mine_TRY)
public class MineTryFragment extends LazyBaseFragment implements View.OnClickListener {

    private TextView mTvCommonTitle, mTvYellowCard, mTvMoreGetCard;
    private XTabLayout mXTabMineTry;
    private ViewPager mVpMineTry;
    private String[] mTabs = new String[]{"全部", "参与中", "待开奖", "已开奖"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private PopupWindow mPopupWindow;
    private LinearLayout mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mIvPopFinish;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_minetry;
    }

    @Override
    protected void findViews() {

        $(R.id.iv_common_back).setOnClickListener(this);
        mTvCommonTitle = $(R.id.iv_common_title);
        mTvCommonTitle.setText("我的试用");
        mTvCommonTitle.setTextColor(getResources().getColor(R.color.c_161616));
        mXTabMineTry = $(R.id.xtab_mine_try);
        mVpMineTry = $(R.id.vp_mine_try);
        mTvYellowCard = $(R.id.tv_yellow_card);
        mTvMoreGetCard = $(R.id.tv_more_get_card);
        mTvMoreGetCard.setOnClickListener(this);

    }

    @Override
    protected void initView() {

        for (int i = 1; i <= 4; i++) {
            Fragment tryContentFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_Mine_TRY_CONTENT).navigation();
            Bundle bundle = new Bundle();
            bundle.putSerializable("obtype", i);
            tryContentFragment.setArguments(bundle);
            mFragments.add(tryContentFragment);
        }

        for (int i = 0; i < mTabs.length; i++) {
            mXTabMineTry.addTab(mXTabMineTry.newTab().setText(mTabs[i]));
        }

        mVpMineTry.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabs[position];
            }
        });

        mVpMineTry.setOffscreenPageLimit(mTabs.length);
        mXTabMineTry.setupWithViewPager(mVpMineTry);
    }

    @Override
    protected void getData() {
        getProduct();
    }

    private void getProduct() {

        NetWork.getInstance()
                .setTag(Qurl.haveCardsNums)
                .getApiService(ModuleApi.class)
                .haveCardsNums()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShowInMyCardBean>(getActivity(), false, false) {
                    @Override
                    public void onSuccess(ShowInMyCardBean result) {
                        if (result == null || TextUtils.isEmpty(result.number)) {
                            mTvYellowCard.setText("黄卡剩余0张");
                            return;
                        }
                        mTvYellowCard.setText("黄卡剩余" + result.number + "张");
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        mTvYellowCard.setText("黄卡剩余0张");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mTvYellowCard.setText("黄卡剩余0张");
                    }
                });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back) {
            getActivity().finish();
//            测试数据
//            JoinSuccessBean joinSuccessBean = new JoinSuccessBean();
//            joinSuccessBean.uaCodeNum = 8;
//            joinSuccessBean.num = 5;
//            joinSuccessBean.keyNumSt = "344";
//            joinSuccessBean.proList = new ArrayList<>();
//
//            for (int i = 0; i < 10; i++) {
//                JoinSuccessBean.ProListBean proListBean = new JoinSuccessBean.ProListBean();
//                proListBean.showImg = "";
//                proListBean.countDown = 1413241;
//                proListBean.id = "askdf";
//                proListBean.name = "nameasknjfkjkasdf";
//                proListBean.needNum = 150;
//                proListBean.realNum = 50;
//                joinSuccessBean.proList.add(proListBean);
//            }
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("joinSuccess", joinSuccessBean);
//            ARouter.getInstance().build(ARouters.PATH_JOIN_SUCCESS_A).with(bundle).navigation();

        } else if (id == R.id.tv_more_get_card) {//获取更多黄卡，跳转到分享界面
            View contentView = LayoutInflater.from ( getActivity() ).inflate ( R.layout.popupwind_share_goods, null );
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
        }else if (id==R.id.iv_finish){
            mPopupWindow.dismiss();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.WEIXIN, MyTryActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, MyTryActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.QQ, MyTryActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.QZONE, MyTryActivity.uriListUm, new UMShareListener() {
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
}
