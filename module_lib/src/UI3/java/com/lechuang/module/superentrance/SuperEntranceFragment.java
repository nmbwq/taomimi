package java.com.lechuang.module.superentrance;


import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.androidkun.xtablayout.XTabLayout;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseFragment;
import com.common.app.constants.Constant;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.TransChangeNesScrollView;
import com.lechuang.module.R;

import java.com.lechuang.module.bean.SuperBaihuoTabBean;
import java.com.lechuang.module.bean.SuperEntranceBean;
import java.com.lechuang.module.bean.SuperTabBean;
import java.com.lechuang.module.superentrance.adapter.TabViewPagerAdapter;
import java.com.lechuang.module.superentrance.adapter.TabViewPagerbaiduAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_SUPERENTRANCE)
public class SuperEntranceFragment extends BaseFragment implements View.OnClickListener {
    private XTabLayout mXTabLayoutSuperBig, mXTabLayoutSuperBaihuo;
    private List<SuperTabBean> mClassType;
    private List<SuperBaihuoTabBean> mClassTypeBaihuo;
    private List<Fragment> mTabFragment = new ArrayList<>();//存储首页的fragment
    private List<Fragment> mTabBaihuoFragment = new ArrayList<>();//存储首页的fragment
    private ViewPager mVpSuper, mVpBaihuo;
    //图片
    private int[] mTianmaoImage = {R.drawable.ic_super_tianmao_otls, R.drawable.ic_super_tianmao_my
            , R.drawable.ic_super_tianmao_mz, R.drawable.ic_super_tianmao_gj, R.drawable.ic_super_tianmao_bh
            , R.drawable.ic_super_tianmao_sx, R.drawable.ic_super_tianmao_yy, R.drawable.ic_super_tianmao_tb};

    private int[] mDianshangImage = {
            R.drawable.ic_super_dianshang_ymx, R.drawable.ic_super_dianshang_ml, R.drawable.ic_super_dianshang_dd, R.drawable.ic_super_dianshang_ss,
            R.drawable.ic_super_dianshang_wy, R.drawable.ic_super_dianshang_mz,R.drawable.ic_super_dianshang_qcs,  R.drawable.ic_super_dianshang_zl,
            R.drawable.ic_super_dianshang_cq, R.drawable.ic_super_dianshang_tc, R.drawable.ic_super_dianshang_lmm, R.drawable.ic_super_dianshang_tn};
    //名称
    private String[] mTianmaoName = {"天猫电器", "天猫母婴", "天猫美妆", "天猫国际", "天猫百货", "天猫生鲜", "天猫医药", "淘宝心选"};

    private String[] mDianshangName = {"凡客旗舰店", "魅力惠", "当当", "莎莎",
            "网易严选", "魅族手机", "屈臣氏", "中青旅",
            "春秋旅游", "同程旅游", "驴妈妈", "途牛"};
    //平均返利
    private String[] mDianshangRebate = {"平均返佣5%", "平均返佣3.82%", "平均返佣5%","平均返佣3.34%",
            "平均返佣4.92%", "平均返佣0.93%", "平均返佣2.25%", "平均返佣2.5%",
            "平均返佣0.51%", "平均返佣0.9%", "平均返佣1.18%", "平均返佣8.25%"};
    //点击跳转url
    private String[] mTianmaoUrl = {"https://3c.tmall.com", "https://baby.tmall.com/"
            , "https://meizhuang.tmall.com/", "https://www.tmall.hk/", "https://chaoshi.tmall.com", "https://miao.tmall.com/", "http://yao.tmall.com/"
            , "https://good.tmall.com/"};

    private String[] mDianshangUrl = {"https://vancl.tmall.com/shop/view_shop.htm?spm=a230r.7195193.1997079397.2.5d786e08OWeigQ", "https://www.tmall.com", "https://dangdang.tmall.com","https://sasahk.tmall.hk",
            "https://wangyiyanxuan.tmall.com", "https://meizu.tmall.com", "https://watsons.tmall.com", "https://zhongqinglv.fliggy.com",
            "https://china-sss.fliggy.com", "https://17u.fliggy.com", "https://lvmama.fliggy.com", "https://tuniufw.tmall.com"};

    private RecyclerView mRvTianmao, mRvDianshang;

    private View mTvJiaocheng, mTvXian, mTvSearchTitle, mTvJiaochengy;
    private RelativeLayout mRlsearch;
    private TransChangeNesScrollView transChangeNesScrollView;
    private LinearLayout mLlTitle;
//    public static List<SuperEntranceBean.AllDataBean> mString;

    public List<SuperEntranceBean.AllDataBean> mss = null;

    public boolean isListContext() {
        return listContext;
    }

    public void setListContext(boolean listContext) {
        this.listContext = listContext;
    }

    public boolean listContext = false;

    public List<SuperEntranceBean.AllDataBean> getMss() {
        return mss;
    }

    public void setMss(List<SuperEntranceBean.AllDataBean> mss) {
        this.mss = mss;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_superentrance;
    }


    @Override
    protected void findViews() {
        transChangeNesScrollView = $(R.id.tcnsv_super);
        mXTabLayoutSuperBig = $(R.id.xTablayout_super_big);
        mXTabLayoutSuperBaihuo = $(R.id.xTablayout_super_baihuo);
        mVpSuper = $(R.id.vp_super_big);
        mVpBaihuo = $(R.id.vp_super_baihuo);
        mRvTianmao = $(R.id.rv_super_tianmao_product);
        mRvDianshang = $(R.id.rv_super_dianshang_product);
        mTvJiaocheng = $(R.id.tv_super_jiaocheng);
        mRlsearch = $(R.id.rl_super_search);
        mLlTitle = $(R.id.ll_super_titleyinchang);
        mTvXian = $(R.id.tv_status_bar);
        mTvSearchTitle = $(R.id.tv_super_searchtitle);
        mTvJiaochengy = $(R.id.tv_super_jiaochengtitle);
    }

    @Override
    protected void initView() {
        /*transChangeNesScrollView = new TransChangeNesScrollView (getActivity ()  );
        transChangeNesScrollView.setTransparentChange(mRlsearch);*/
//        mTvXian.setBackgroundColor(getResources().getColor(R.color.c_33000000));
        transChangeNesScrollView.setTransparentChange(mLlTitle);
        mTvJiaocheng.setOnClickListener(this);
        mTvJiaochengy.setOnClickListener(this);
        mTvSearchTitle.setOnClickListener(this);
        mRlsearch.setOnClickListener(this);


        mClassType = new ArrayList<>();
        String[] tabS = getResources().getStringArray(R.array.s_super_tab_bean);

        mXTabLayoutSuperBig.removeAllTabs();
        mClassType.add(new SuperTabBean(99, "精选"));
        mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_SUPERFIRST).navigation());
        mXTabLayoutSuperBig.addTab(mXTabLayoutSuperBig.newTab().setText(mClassType.get(0).className));

        for (int i = 0; i < tabS.length; i++) {
            mClassType.add(new SuperTabBean(i + 1, tabS[i]));
            mTabFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_SUPEROTHER).navigation());
            mXTabLayoutSuperBig.addTab(mXTabLayoutSuperBig.newTab().setText(tabS[i]));
        }

        mVpSuper.setOffscreenPageLimit(mClassType.size() + 1);
        float dimension = getResources().getDimension(R.dimen.dp_15);
        mVpSuper.setPageMargin((int) dimension);
        mVpSuper.setAdapter(new TabViewPagerAdapter(BaseApplication.getApplication(), getChildFragmentManager(), mClassType, mTabFragment));
        mXTabLayoutSuperBig.setupWithViewPager(mVpSuper);


        mClassTypeBaihuo = new ArrayList<>();
        mXTabLayoutSuperBaihuo.removeAllTabs();
        mClassTypeBaihuo.add(new SuperBaihuoTabBean(99, "亚洲"));
        mTabBaihuoFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_SUPERBAIHUOFIRST).navigation());
        mXTabLayoutSuperBaihuo.addTab(mXTabLayoutSuperBaihuo.newTab().setText(mClassTypeBaihuo.get(0).className));

        mClassTypeBaihuo.add(new SuperBaihuoTabBean(100, "欧美"));
        mTabBaihuoFragment.add((Fragment) ARouter.getInstance().build(ARouters.PATH_SUPERBAIHUOOTHER).navigation());
        mXTabLayoutSuperBaihuo.addTab(mXTabLayoutSuperBaihuo.newTab().setText(mClassTypeBaihuo.get(1).className));

        mVpBaihuo.setOffscreenPageLimit(mClassTypeBaihuo.size() + 1);
        float dimension1 = getResources().getDimension(R.dimen.dp_15);
        mVpBaihuo.setPageMargin((int) dimension1);
        mVpBaihuo.setAdapter(new TabViewPagerbaiduAdapter(BaseApplication.getApplication(), getChildFragmentManager(), mClassTypeBaihuo, mTabBaihuoFragment));
        mXTabLayoutSuperBaihuo.setupWithViewPager(mVpBaihuo);

        setTianmaoProduct();
        setDianshangProduct();


    }

    @Override
    protected void getData() {
    }

    //天猫
    private List<SuperEntranceTianmao> mTianmaoProductList;
    private BaseQuickAdapter<SuperEntranceTianmao, BaseViewHolder> mTianmaoBaseProductQuickAdapter;

    private void setTianmaoProduct() {
        if (mTianmaoProductList == null) {
            mTianmaoProductList = new ArrayList<>();
        }

        for (int i = 0; i < mTianmaoImage.length; i++) {
            mTianmaoProductList.add(new SuperEntranceTianmao(mTianmaoImage[i], mTianmaoName[i], "平均返利5%", mTianmaoUrl[i]));
        }

        if (mTianmaoBaseProductQuickAdapter == null) {

            mTianmaoBaseProductQuickAdapter = new BaseQuickAdapter<SuperEntranceTianmao, BaseViewHolder>(R.layout.item_super_product, mTianmaoProductList) {
                @Override
                protected void convert(BaseViewHolder helper, final SuperEntranceTianmao item) {
                    //图片
                    ImageView ivItemAllFenLei = helper.getView(R.id.iv_super_icon);
                    Glide.with(BaseApplication.getApplication()).load(item.seIcon).into(ivItemAllFenLei);

                    //名字
                    helper.setText(R.id.tv_super_name, item.seName);
                    //平均佣金
                    helper.setText(R.id.tv_super_info, item.seInfo);
                    RelativeLayout rl = helper.getView(R.id.ll_super_item);
                    rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ARouter.getInstance().build(ARouters.PATH_SUPER_WEB)
                                    .withString("loadUrl", item.seUrl)
                                    .withString(Constant.TITLE, item.seName)
                                    .navigation();
                        }
                    });
                }
            };
            mRvTianmao.setHasFixedSize(true);
            mRvTianmao.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            mRvTianmao.setLayoutManager(gridLayoutManager);
            mRvTianmao.setAdapter(mTianmaoBaseProductQuickAdapter);
            mRvTianmao.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(getActivity())
                            .margin(0, 0)
                            .size(0)
            ));

            //底部推荐产品点击事件 todo
            mTianmaoBaseProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                }
            });
        } else {
            mTianmaoBaseProductQuickAdapter.notifyDataSetChanged();
        }
    }

    //电商
    private List<SuperEntranceDianshang> mDianshangProductList;
    private BaseQuickAdapter<SuperEntranceDianshang, BaseViewHolder> mDianshangBaseProductQuickAdapter;

    private void setDianshangProduct() {
        if (mDianshangProductList == null) {
            mDianshangProductList = new ArrayList<>();
        }


        for (int i = 0; i < mDianshangImage.length; i++) {
            mDianshangProductList.add(new SuperEntranceDianshang(mDianshangImage[i], mDianshangName[i], mDianshangRebate[i], mDianshangUrl[i]));
        }


        if (mDianshangBaseProductQuickAdapter == null) {

            mDianshangBaseProductQuickAdapter = new BaseQuickAdapter<SuperEntranceDianshang, BaseViewHolder>(R.layout.item_super_product, mDianshangProductList) {
                @Override
                protected void convert(BaseViewHolder helper, SuperEntranceDianshang item) {
                    //图片
                    ImageView ivItemAllFenLei = helper.getView(R.id.iv_super_icon);
                    Glide.with(BaseApplication.getApplication()).load(item.seIcon).into(ivItemAllFenLei);

                    //名字
                    helper.setText(R.id.tv_super_name, item.seName);
                    //平均佣金
                    helper.setText(R.id.tv_super_info, item.seInfo);
                }
            };
            mRvDianshang.setHasFixedSize(true);
            mRvDianshang.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            mRvDianshang.setLayoutManager(gridLayoutManager);
            mRvDianshang.setAdapter(mDianshangBaseProductQuickAdapter);
            mRvDianshang.addItemDecoration(new GridItemDecoration(
                    new GridItemDecoration.Builder(getActivity())
                            .margin(0, 0)
                            .size(0)
            ));


            //底部推荐产品点击事件 todo
            mDianshangBaseProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ARouter.getInstance().build(ARouters.PATH_SUPER_WEB)
                            .withString("loadUrl", mDianshangUrl[position])
                            .withString(Constant.TITLE, mDianshangName[position])
                            .navigation();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_super_jiaocheng || id == R.id.tv_super_jiaochengtitle) {
            ARouter.getInstance().build(ARouters.PATH_SUPERTUTORIAL).navigation();
        } else if (id == R.id.tv_super_searchtitle || id == R.id.rl_super_search) {
            ARouter.getInstance().build(ARouters.PATH_SEARCH).withString(Constant.CHANNEL, "APP").navigation();//默认搜索APP
        }
    }


    private class SuperEntranceTianmao implements Serializable {
        public int seIcon;
        public String seInfo;
        public String seName;
        public String seUrl;

        public SuperEntranceTianmao() {
        }

        public SuperEntranceTianmao(int seIcon, String seName, String seInfo, String seUrl) {
            this.seIcon = seIcon;
            this.seInfo = seInfo;
            this.seName = seName;
            this.seUrl = seUrl;
        }
    }

    private class SuperEntranceDianshang implements Serializable {
        public int seIcon;
        public String seInfo;
        public String seName;
        public String seUrl;

        public SuperEntranceDianshang() {
        }

        public SuperEntranceDianshang(int seIcon, String seName, String seInfo, String seUrl) {
            this.seIcon = seIcon;
            this.seInfo = seInfo;
            this.seName = seName;
            this.seUrl = seUrl;
        }
    }
}
