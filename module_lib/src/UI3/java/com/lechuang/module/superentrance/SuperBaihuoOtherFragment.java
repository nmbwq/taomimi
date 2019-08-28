package java.com.lechuang.module.superentrance;

import android.os.Bundle;
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
import java.com.lechuang.module.bean.SuperBaihuoTabBean;
import java.com.lechuang.module.bean.SuperEntranceBean;
import java.com.lechuang.module.bean.SuperTabBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SUPERBAIHUOOTHER)
public class SuperBaihuoOtherFragment extends BaseFragment {
    private SuperBaihuoTabBean mSuperTabBean;
    private RecyclerView mRvSuper;
    private int[] mYazhouImage = {R.drawable.ic_super_oumei_lhlh,R.drawable.ic_super_oumei_bj,R.drawable.ic_super_oumei_mdl,R.drawable.ic_super_oumei_cw
            ,R.drawable.ic_super_oumei_hf,R.drawable.ic_super_oumei_dm,R.drawable.ic_super_oumei_bls,R.drawable.ic_super_oumei_unichen
            ,R.drawable.ic_super_oumei_aldi,R.drawable.ic_super_oumei_kirindo,R.drawable.ic_super_oumei_he,R.drawable.ic_super_oumei_nm
            ,R.drawable.ic_super_oumei_f,R.drawable.ic_super_oumei_w,R.drawable.ic_super_oumei_v,R.drawable.ic_super_oumei_d};
    //名称
    private String[] mYazhouName = {"联合利华", "宝洁", "麦德龙", "奥洲CW", "HouseofFra...", "DM", "Paraseller", "Unichen"
            , "ALDI", "woolworths", "healthelem...", "NihaoMarket", "farmacity", "Windeln", "Ventalis", "Dio"};
    private String[] mYazhouRebate = {"日用品快销巨头", "日用品快销巨头", "德国最大零售贸易...", "著名美容保健品商城",
            "英国知名百货公司", "德国药妆连锁", "法国连锁药妆超市", "新西兰联合大药房", "国际零售品牌"
            , "澳洲第一大超市", "新西兰连锁大药房", "意大利著名连锁超市", "荷兰第一药妆超市", "windeln"
            , "德国连锁大药房", "荷兰第一药妆超市"};
    private String[] mYazhouUrl={"https://lianhelihua.tmall.com","https://pg.tmall.com","https://metro.tmall.hk","https://chemistwarehouse.tmall.hk"
            ,"http://houseoffraser.tmall.com","https://dm-deguo.tmall.hk","https://paraseller.tmall.hk","https://unichem.tmall.hk"
            ,"https://shopsearch.taobao.com","https://woolworths.tmall.hk","https://healthelement.tmall.hk","https://nihao.tmall.hk"
            ,"https://farmacity.tmall.hk","https://windelnde.tmall.hk","https://ventalis.tmall.hk","https://dio2016.tmall.hk"};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_super_first;
    }

    @Override
    protected void findViews() {
        mRvSuper = $ ( R.id.rv_super_first_product );
    }

    @Override
    protected void initView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mSuperTabBean = (SuperBaihuoTabBean) arguments.getSerializable("TopTab");
        }
    }

    @Override
    protected void getData() {
        setOtherData();
    }





    /**
     * 设置数据
     *
     * @param productList
     */
    private List<SuperEntranceOuzhou> mProductList;
    private BaseQuickAdapter<SuperEntranceOuzhou, BaseViewHolder> mBaseProductQuickAdapter;

    private void setOtherData() {
        if (mProductList==null){
            mProductList = new ArrayList<> ();
        }
        for (int i = 0; i < mYazhouImage.length; i++) {
            mProductList.add ( new SuperEntranceOuzhou ( mYazhouImage[i], mYazhouName[i], mYazhouRebate[i], mYazhouUrl[i] ) );
        }
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<SuperEntranceOuzhou, BaseViewHolder> ( R.layout.item_super_product, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, final SuperEntranceOuzhou item) {
                    try {
                        //图片
                        ImageView ivItemAllFenLei = helper.getView ( R.id.iv_super_icon );
                        Glide.with ( BaseApplication.getApplication () ).load ( item.seIcon ).into ( ivItemAllFenLei );

                        //名字
                        helper.setText ( R.id.tv_super_name, item.seName );
                        //平均佣金
                        helper.setText ( R.id.tv_super_info, item.seInfo );
                    } catch (Exception e) {

                    }


                }
            };
            mRvSuper.setHasFixedSize ( true );
            mRvSuper.setNestedScrollingEnabled ( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager ( getActivity (), 4 );
            mRvSuper.setLayoutManager ( gridLayoutManager );


            mRvSuper.setAdapter ( mBaseProductQuickAdapter );
            mRvSuper.addItemDecoration ( new GridItemDecoration (
                    new GridItemDecoration.Builder(getActivity())
                            .margin(0, 0)
                            .size(0)
            ) );
            //底部推荐产品点击事件 todo
            mBaseProductQuickAdapter.setOnItemClickListener ( new BaseQuickAdapter.OnItemClickListener () {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ARouter.getInstance ().build ( ARouters.PATH_SUPER_WEB )
                            .withString ( "loadUrl", mYazhouUrl[position] )
                            .withString ( Constant.TITLE, mYazhouName[position] )
                            .navigation ();
                }
            } );


        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }

    }
    private class SuperEntranceOuzhou implements Serializable {
        public int seIcon;
        public String seInfo;
        public String seName;
        public String seUrl;

        public SuperEntranceOuzhou() {
        }

        public SuperEntranceOuzhou(int seIcon, String seName, String seInfo, String seUrl) {
            this.seIcon = seIcon;
            this.seInfo = seInfo;
            this.seName = seName;
            this.seUrl = seUrl;
        }
    }
}
