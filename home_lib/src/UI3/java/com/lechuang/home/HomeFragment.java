package java.com.lechuang.home;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.androidkun.xtablayout.XTabLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.FragmentActivity;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.lechuang.home.R;
import com.zhouwei.mzbanner.BannerBgContainer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.home.adapter.TabViewPagerAdapter;
import java.com.lechuang.home.bean.HomeTabBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_HOME)
public class HomeFragment extends LazyBaseFragment implements View.OnClickListener {

    private XTabLayout mXTabLayoutHome;
    private ViewPager mVpHome;
    private List<HomeTabBean> mClassType;
    private List<Fragment> mTabFragment = new ArrayList<>();//存储首页的fragment
    private RelativeLayout mTvHomeSearch;
    private TextView mTvHomeXiaoXi;
    private LinearLayout mLlHomeHeaderParent;
    public static BannerBgContainer mBannerBgContainer;
    private ImageView mIvHomeOtherAll;
    private RelativeLayout mRlHomeOtherAll;
    private RecyclerView mRvHomeOtherAll;
    private TextView mTvHomeOtherAll;
    private BaseQuickAdapter<HomeTabBean, BaseViewHolder> mBaseQuickAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home1;
    }

    @Override
    protected void findViews() {
        mXTabLayoutHome = mInflate.findViewById(R.id.xTablayout_home);
        mVpHome = mInflate.findViewById(R.id.vp_home);
        mTvHomeXiaoXi = mInflate.findViewById(R.id.tv_home_xiaoxi);
        mTvHomeSearch = mInflate.findViewById(R.id.tv_home_search);
        mLlHomeHeaderParent = mInflate.findViewById(R.id.ll_home_header_parent);
        mBannerBgContainer = mInflate.findViewById(R.id.banner_bg_container);
        mIvHomeOtherAll = mInflate.findViewById(R.id.iv_home_other_all);
        mRlHomeOtherAll = mInflate.findViewById(R.id.rl_home_other_all);
        mRvHomeOtherAll = mInflate.findViewById(R.id.rv_home_other_all);
        mTvHomeOtherAll = mInflate.findViewById(R.id.tv_home_other_all);
        mTvHomeSearch.setOnClickListener(this);
        mIvHomeOtherAll.setOnClickListener(this);
        $(R.id.rl_home_news_parent).setOnClickListener(this);
        mTvHomeOtherAll.setOnClickListener(this);

    }

    private int mProgressScroll = 0;
    private int mCurrentSelected = 0;

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);

        mClassType = new ArrayList<>();
        String[] tabS = getResources().getStringArray(R.array.s_home_tab_bean);

        mXTabLayoutHome.removeAllTabs();

        mClassType.add(new HomeTabBean(99, "精选", true));
        AllFragment allFragment = (AllFragment) ARouter.getInstance().build(ARouters.PATH_ALL).navigation();
        allFragment.mBannerBgContainer = mBannerBgContainer;

        mTabFragment.add(allFragment);
        mXTabLayoutHome.addTab(mXTabLayoutHome.newTab().setText(mClassType.get(0).className));

        for (int i = 0; i < tabS.length; i++) {
            mClassType.add(new HomeTabBean(i + 1, tabS[i], false));
            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_OTHER).navigation());
            mXTabLayoutHome.addTab(mXTabLayoutHome.newTab().setText(tabS[i]));
        }

        mVpHome.setOffscreenPageLimit(mClassType.size() + 1);
        mVpHome.setAdapter(new TabViewPagerAdapter(BaseApplication.getApplication(), getChildFragmentManager(), mClassType, mTabFragment));
        mVpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0 && positionOffset > 0 || position > 0) {
                    mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_change_color));
                } else {
                    if (mProgressScroll > 200) {
                        mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_change_color));
                    } else {
                        mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_trans_color));
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (mBaseQuickAdapter != null && mCurrentSelected != position) {
                    boolean isSelecter = mClassType.get(position).isSelecter;
                    mClassType.get(position).isSelecter = !isSelecter;
                    mClassType.get(mCurrentSelected).isSelecter = false;
                    mBaseQuickAdapter.notifyDataSetChanged();
                }

                mRlHomeOtherAll.setVisibility(View.GONE);
                mIsVisibility = false;
                mCurrentSelected = position;

                if (mIsVisibility) {
                    mIvHomeOtherAll.setImageDrawable(getResources().getDrawable(R.drawable.bg_other_all_up));
                } else {
                    mIvHomeOtherAll.setImageDrawable(getResources().getDrawable(R.drawable.bg_other_all_down));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        allFragment.mINotifyScrollProgress = new AllFragment.INotifyScrollProgress() {
            @Override
            public void onNotifyProgress(int progress) {
                mProgressScroll = progress;
                if (mProgressScroll > 200) {
                    mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_change_color));
                } else {
                    mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_trans_color));
                }
            }
        };
        mBaseQuickAdapter = new BaseQuickAdapter<HomeTabBean, BaseViewHolder>(R.layout.layout_item_other_all, mClassType) {
            @Override
            protected void convert(BaseViewHolder helper, HomeTabBean item) {
                helper.setText(R.id.tv_item_other_all, item.className);
                helper.getView(R.id.tv_item_other_all).setSelected(item.isSelecter);
            }
        };
        mRvHomeOtherAll.setAdapter(mBaseQuickAdapter);
        mRvHomeOtherAll.setHasFixedSize(true);
        mRvHomeOtherAll.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mRvHomeOtherAll.setLayoutManager(gridLayoutManager);
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mCurrentSelected != position) {
                    boolean isSelecter = mClassType.get(position).isSelecter;
                    mClassType.get(position).isSelecter = !isSelecter;
                    mClassType.get(mCurrentSelected).isSelecter = false;
                    mBaseQuickAdapter.notifyDataSetChanged();
                    mCurrentSelected = position;
                    mVpHome.setCurrentItem(position, true);
                }
                mRlHomeOtherAll.setVisibility(View.GONE);
                mIsVisibility = false;
            }
        });

        mXTabLayoutHome.setupWithViewPager(mVpHome);

    }

    /**
     * 根据百分比改变颜色透明度
     *
     * @param color
     * @param fraction
     * @return
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void getData() {
        mTvHomeXiaoXi.setVisibility(UserHelper.getInstence().getUserInfo().getMsgState() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void updataRequest() {
        super.updataRequest();
        mTvHomeXiaoXi.setVisibility(UserHelper.getInstence().getUserInfo().getMsgState() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTvHomeXiaoXi != null) {
            mTvHomeXiaoXi.setVisibility(UserHelper.getInstence().getUserInfo().getMsgState() ? View.VISIBLE : View.INVISIBLE);
        }

    }

    private boolean mIsVisibility = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_home_search) {
            ARouter.getInstance().build(ARouters.PATH_SEARCH).withString(Constant.CHANNEL, "APP").navigation();


//            AlibcLogin alibcLogin = AlibcLogin.getInstance();
//            Log.e("----",AlibcLogin.getInstance().getSession().toString());
//            if (alibcLogin.isLogin()){
//                String url = "https://oauth.taobao.com/authorize?response_type=code&client_id=25264288&redirect_uri=http://yxee5b.natappfree.cc/taobao_author/author&state=1212&view=wap";
//                url = "https://oauth.taobao.com/authorize?response_type=code&client_id=25264288&redirect_uri=http://435wns.natappfree.cc/taobao_author/author?ckToken=789456&state=1212&view=wap";
//
//                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
//                        .withString("loadUrl", url)
//                        .withString(Constant.TITLE, "授权登陆")
////                .withString("userId", TextUtils.isEmpty(UserHelper.getInstence().getUserInfo().getId()) ? "" : UserHelper.getInstence().getUserInfo().getId())
//                        .navigation();
//            }else {
//                alibcLogin.showLogin(new AlibcLoginCallback() {
//                    @Override
//                    public void onSuccess(int i) {
//
//
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//
//                    }
//                });
//            }




//            ARouter.getInstance().build(ARouters.PATH_MY_TREASURE_BOX).navigation();
//            ARouter.getInstance().build(ARouters.PATH_MINE_BANG_DAN).navigation();
//            ARouter.getInstance().build(ARouters.PATH_MY_TRY).navigation();
//            ARouter.getInstance().build(ARouters.PATH_SIGN_IN).navigation();
//            Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_WEEK_SIGNIN).navigation();
//            FragmentActivity.start(this, mineTryFragment.getClass());
//            ARouter.getInstance().build(ARouters.PATH_SIGN_IN).navigation();

//            ARouter.getInstance().build(ARouters.PATH_PRODUCT)
//                    .withInt("classTypeId",1)
//                    .navigation();//测试跳转到首页二级界面
//            showKaiFaDialog();
        } else if (id == R.id.iv_home_other_all) {

            if (mIsVisibility) {
                mRlHomeOtherAll.setVisibility(View.GONE);
                mIsVisibility = false;
                mIvHomeOtherAll.setImageDrawable(getResources().getDrawable(R.drawable.bg_other_all_down));
            } else {
                mRlHomeOtherAll.setVisibility(View.VISIBLE);
                mIsVisibility = true;
                mIvHomeOtherAll.setImageDrawable(getResources().getDrawable(R.drawable.bg_other_all_up));
            }

        } else if (id == R.id.tv_home_other_all) {
            mRlHomeOtherAll.setVisibility(View.GONE);
            mIsVisibility = false;
        } else if (id == R.id.rl_home_news_parent) {
            if (UserHelper.getInstence().getUserInfo().isLogin) {
                ARouter.getInstance().build(ARouters.PATH_NEWS).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_GUIDE_LOGIN).navigation();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGOUT_SUCCESS)) {
            mXTabLayoutHome.setScrollPosition(0, 0, true);
            mVpHome.setCurrentItem(0, true);
            mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_trans_color));
            mProgressScroll = 0;
        }/*else if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS)){
            int currentItem = mVpHome.getCurrentItem();
            if (currentItem == 0) {
                mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_trans_color));
                mProgressScroll = 0;
            } else {
                mLlHomeHeaderParent.setBackground(getResources().getDrawable(R.drawable.bg_home_change_color));
            }

        }*/
    }
}
