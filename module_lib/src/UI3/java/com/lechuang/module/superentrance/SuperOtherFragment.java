package java.com.lechuang.module.superentrance;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.com.lechuang.module.bean.SuperTabBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SUPEROTHER)
public class SuperOtherFragment extends BaseFragment {
    private SuperTabBean mSuperTabBean;
    private RecyclerView mRvSuper;

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
        Bundle arguments = getArguments ();
        if (arguments != null) {
            mSuperTabBean = (SuperTabBean) arguments.getSerializable ( "TopTab" );
        }
    }

    @Override
    protected void getData() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint ( isVisibleToUser );
        if (isVisibleToUser) {
            if (((SuperEntranceFragment)(SuperOtherFragment.this.getParentFragment ())).isListContext ()) {
                setFristData ( ((SuperEntranceFragment)(SuperOtherFragment.this.getParentFragment ())).getMss ().get ( mSuperTabBean.programaId ).getItemsData () );
            } else {
                getAllData ();
            }
        }
    }

    /**
     * 刷新数据
     */
    private void getAllData() {
        ApiCancleManager.getInstance ().removeAll ();
        Map<String, String> allParam = new HashMap<> ();

        NetWork.getInstance ()
                .setTag ( Qurl.superEntrance )
                .getApiService ( ModuleApi.class )
                .superEntrance ()
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<SuperEntranceBean> ( getActivity (), false, false ) {

                    @Override
                    public void onSuccess(SuperEntranceBean result) {
                        if (result == null) {
                            return;
                        }

                        result.getAllData ().get ( 0 ).getItemsData ();
                        //设置底部商品数据
                        setFristData ( result.getAllData ().get ( mSuperTabBean.programaId ).getItemsData () );
                        ((SuperEntranceFragment)(SuperOtherFragment.this.getParentFragment ())).setListContext (true);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                    }
                } );
    }

    /**
     * 设置数据
     *
     * @param productList
     */
    private List<SuperEntranceBean.AllDataBean.ItemsDataBean> mProductList;
    private BaseQuickAdapter<SuperEntranceBean.AllDataBean.ItemsDataBean, BaseViewHolder> mBaseProductQuickAdapter;

    private void setFristData(List<SuperEntranceBean.AllDataBean.ItemsDataBean> productList) {
        if (productList == null || productList.size () <= 0) {
            return;
        }
        if (mProductList == null) {
            mProductList = new ArrayList<> ();
        }
        mProductList.clear ();
        mProductList.addAll ( productList );
        if (mBaseProductQuickAdapter == null) {
            mBaseProductQuickAdapter = new BaseQuickAdapter<SuperEntranceBean.AllDataBean.ItemsDataBean, BaseViewHolder> ( R.layout.item_super_product, mProductList ) {
                @Override
                protected void convert(BaseViewHolder helper, final SuperEntranceBean.AllDataBean.ItemsDataBean item) {
                    try {
                        //图片
                        ImageView ivItemAllFenLei = helper.getView ( R.id.iv_super_icon );
                        Glide.with ( BaseApplication.getApplication () ).load ( item.getSeIcon () ).into ( ivItemAllFenLei );

                        //名字
                        helper.setText ( R.id.tv_super_name, item.getSeName () );
                        //平均佣金
                        helper.setText ( R.id.tv_super_info, item.getSeInfo () );
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
                    new GridItemDecoration.Builder ( getActivity () )
                            .margin ( 5, 5 )
                            .size ( 10 )
            ) );
            //底部推荐产品点击事件 todo
            mBaseProductQuickAdapter.setOnItemClickListener ( new BaseQuickAdapter.OnItemClickListener () {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ARouter.getInstance ().build ( ARouters.PATH_SUPER_WEB )
                            .withString ( "loadUrl", mProductList.get ( position ).getSeUrl () )
                            .withString ( Constant.TITLE, mProductList.get ( position ).getSeName () )
                            .navigation ();
                }
            } );


        } else {
            mBaseProductQuickAdapter.notifyDataSetChanged ();
        }

    }
}
