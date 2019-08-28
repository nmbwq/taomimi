package java.com.lechuang.module.superentrance;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.Utils;
import com.common.app.view.GridItemDecoration;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.SuperEntranceBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SUPERBAIHUOFIRST)
public class SuperBaihuoFirstFragment extends BaseFragment {
    private RecyclerView mRvSuper;
    private int[] mYazhouImage = {R.drawable.ic_super_yazhou_qcs, R.drawable.ic_super_yazhou_wn, R.drawable.ic_super_yazhou_ss
            , R.drawable.ic_super_yazhou_zy, R.drawable.ic_super_yazhou_cosme, R.drawable.ic_super_yazhou_emart, R.drawable.ic_super_yazhou_aeon
            , R.drawable.ic_super_yazhou_sbq, R.drawable.ic_super_yazhou_wwms, R.drawable.ic_super_yazhou_kirindo, R.drawable.ic_super_yazhou_syysd
            , R.drawable.ic_super_yazhou_olive, R.drawable.ic_super_yazhou_fresta, R.drawable.ic_super_yazhou_zst, R.drawable.ic_super_yazhou_bl
            , R.drawable.ic_super_yazhou_ecolite};
    //名称
    private String[] mYazhouName = {"屈臣氏", "万宁", "莎莎", "香港卓悦", "cosme", "emart", "AEON", "松本清"
            , "丸屋免税店", "kirindo", "三越伊势丹", "OLIVEYOUNG", "日本Fresta", "资生堂集团", "宾利之选", "Ecolite"};
    private String[] mYazhouRebate = {"著名美妆保健零售商", "亚洲最大零售集团", "著名美容保健品商城", "著名美容保健品商城",
            "日本最权威美妆网站", "韩国大型连锁超市", "日本最大连锁超市", "日本最大的药妆连锁", "日本综合免税商店"
            , "日本上市药妆企业", "日本最知名的百货..", "韩国知名美妆连锁", "广岛第一连锁超市", "日本著名化妆品集团"
            , "澳门大型连锁超市", "健康美容产品商城"};
    private String[] mYazhouUrl = {"https://watsons.tmall.com","https://mannings.tmall.com","https://sasa.tmall.com","https://bonjour.tmall.hk"
            ,"https://atcosme.tmall.hk","https://atcosme.tmall.hk","https://aeonretail.tmall.hk","https://mk.tmall.hk"
            ,"https://maruya.tmall.hk","https://kirindo.tmall.hk","https://mitsukoshiisetan.tmall.hk","https://oliveyoung.tmall.hk"
            ,"https://fresta.tmall.hk","https://shiseidojp.tmall.hk","https://bannychoice.tmall.hk","https://ecolite.tmall.hk"};
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_super_first;
    }

    @Override
    protected void findViews() {
        mRvSuper = $(R.id.rv_super_first_product);

    }

    @Override
    protected void initView() {
    }

    @Override
    protected void getData() {
        setFristData();
    }


    /**
     * 设置数据
     *
     * @param productList
     */
    private List<SuperEntranceYazhou> mProductList;
    private BaseQuickAdapter<SuperEntranceYazhou, BaseViewHolder> mBaseProductQuickAdapter;
    private void setFristData(){
        if (mProductList==null){
            mProductList = new ArrayList<> ();
        }
        for (int i = 0; i < mYazhouImage.length; i++) {
            mProductList.add ( new SuperEntranceYazhou ( mYazhouImage[i], mYazhouName[i], mYazhouRebate[i], mYazhouUrl[i] ) );
        }

        if (mBaseProductQuickAdapter == null){
            mBaseProductQuickAdapter = new BaseQuickAdapter<SuperEntranceYazhou, BaseViewHolder> (R.layout.item_super_product,mProductList) {
                @Override
                protected void convert(BaseViewHolder helper, final SuperEntranceYazhou item) {
                    try{
                        //图片
                        ImageView ivItemAllFenLei = helper.getView ( R.id.iv_super_icon );
                        Glide.with ( BaseApplication.getApplication () ).load ( item.seIcon ).into ( ivItemAllFenLei );

                        //名字
                        helper.setText ( R.id.tv_super_name, item.seName );
                        //平均佣金
                        helper.setText ( R.id.tv_super_info, item.seInfo );
                    }catch (Exception e){
                    }
                }
            };
            mRvSuper.setHasFixedSize(true);
            mRvSuper.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            mRvSuper.setLayoutManager(gridLayoutManager);


            mRvSuper.setAdapter(mBaseProductQuickAdapter);
            mRvSuper.addItemDecoration(new GridItemDecoration (
                    new GridItemDecoration.Builder(getActivity())
                            .margin(0, 0)
                            .size(0)
            ));
            //底部推荐产品点击事件 todo
            mBaseProductQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ARouter.getInstance ().build ( ARouters.PATH_SUPER_WEB )
                            .withString ( "loadUrl", mYazhouUrl[position] )
                            .withString ( Constant.TITLE, mYazhouName[position] )
                            .navigation ();

                }
            });


        }else {
            mBaseProductQuickAdapter.notifyDataSetChanged();
        }

    }
    private class SuperEntranceYazhou implements Serializable {
        public int seIcon;
        public String seInfo;
        public String seName;
        public String seUrl;

        public SuperEntranceYazhou() {
        }

        public SuperEntranceYazhou(int seIcon, String seName, String seInfo, String seUrl) {
            this.seIcon = seIcon;
            this.seInfo = seInfo;
            this.seName = seName;
            this.seUrl = seUrl;
        }
    }

}
