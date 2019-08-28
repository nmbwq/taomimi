package java.com.lechuang.module.adress;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.SPUtils;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.adress.SelectCityUtils.ChangeAddressPopwindow;
import java.com.lechuang.module.adress.SelectCityUtils.utils.KeyBoardUtil;
import java.com.lechuang.module.bean.AddListBean;
import java.com.lechuang.module.redpackage.RedPackageActivity;
import java.com.lechuang.module.view.CommonSuperWebViewActivity;
import java.com.lechuang.module.view.CommonWebViewActivity;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_ADDANDUPDATE)
public class AddandUpdateActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public AddListBean.AddressListBean adressBean;
    public boolean isAdd;

    private PopupWindow mPopupWindow;
//    private TextView tv_addadress,tv_common_click_trys;

    private ImageView iv_common_back;
    private TextView tv_addadress, tv_adress, tv_delete;
    private EditText et_name, et_phone, et_detail_adress;

    Switch switch_red_pack;
    //选择地区的省市区
    private String Province = "";
    private String City = "";
    private String County = "";
    //是否默认地址
    Boolean isDefault = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_address;
    }

    @Override
    protected void findViews() {
        iv_common_back = ((ImageView) $( R.id.iv_common_back ));
        tv_addadress = ((TextView) $( R.id.tv_addadress ));
        tv_adress = ((TextView) $( R.id.tv_adress ));
        tv_delete = ((TextView) $( R.id.tv_delete ));
        switch_red_pack = $( R.id.switch_red_pack );
        et_name = ((EditText) $( R.id.et_name ));
        et_phone = ((EditText) $( R.id.et_phone ));
        et_detail_adress = ((EditText) $( R.id.et_detail_adress ));

        iv_common_back.setOnClickListener( this );
        tv_addadress.setOnClickListener( this );
        tv_delete.setOnClickListener( this );
        tv_adress.setOnClickListener( this );

        switch_red_pack.setOnCheckedChangeListener( AddandUpdateActivity.this );

    }

    @Override
    protected void initView() {
        adressBean = (AddListBean.AddressListBean) getIntent().getSerializableExtra( "adressBean" );
        isAdd = getIntent().getBooleanExtra( "isAdd", false );
        if (isAdd) {
            ((TextView) $( R.id.iv_common_title )).setText( "添加收货地址" );
            tv_delete.setVisibility( View.GONE );
            isDefault = false;
        } else {
            ((TextView) $( R.id.iv_common_title )).setText( "编辑收货地址" );
            tv_delete.setVisibility( View.VISIBLE );
            et_name.setText( adressBean.getReceiverName() + "" );
            //光标在字的后面
            et_name.setSelection( adressBean.getReceiverName().toString().length() );
            et_phone.setText( adressBean.getReceiverPhone() + "" );
            tv_adress.setText( adressBean.getAreaAddress() + "" );
            et_detail_adress.setText( adressBean.getDetailAddress() + "" );
            Province = adressBean.getProvince();
            City = adressBean.getCity();
            County = adressBean.getCounty();
//            是否默认地址 0 否 1 默认
            if (adressBean.getIsDefault() == 1) {
                isDefault = true;
            } else {
                isDefault = false;
            }
            switch_red_pack.setChecked( isDefault );
        }


    }

    @Override
    protected void getData() {
    }

    /**
     * 添加以及修改地址
     */
    private void editAddress() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put( "receiverName", et_name.getText().toString() + "" );
        allParam.put( "receiverPhone", et_phone.getText().toString() + "" );
        allParam.put( "province", Province + "" );
        allParam.put( "city", City + "" );
        allParam.put( "county", County + "" );
        allParam.put( "detailAddress", et_detail_adress.getText().toString() + "" );
        if (isDefault) {
            allParam.put( "isDefault", "1" );
        } else {
            allParam.put( "isDefault", "0" );
        }
        if (!isAdd) {
            allParam.put( "id", adressBean.getId() + "" );
        }
        NetWork.getInstance()
                .setTag( Qurl.editAddress )
                .getApiService( ModuleApi.class )
                .editAddress( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<String>( this, true, true ) {
                    @Override
                    public void onSuccess(String result) {
                        if (isAdd) {
                            toast( "添加地址成功" );
                        } else {
                            toast( "修改地址成功" );
                        }
                        finish();
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed( errorCode, moreInfo );

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError( e );
                    }
                } );
    }


    /**
     * 刪除地址
     */
    private void delAddress() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put( "id", adressBean.getId() + "" );
        NetWork.getInstance()
                .setTag( Qurl.delAddress )
                .getApiService( ModuleApi.class )
                .delAddress( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<String>( this, true, true ) {
                    @Override
                    public void onSuccess(String result) {
                        toast( "删除地址成功" );
                        finish();
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed( errorCode, moreInfo );

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError( e );
                    }
                } );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.tv_addadress) {
            if (et_name.getText().toString().length() == 0) {
                toast( "请输入收货名称" );
                return;
            }
            if (et_phone.getText().toString().length() == 0) {
                toast( "请输入手机号码" );
                return;
            }
            if (tv_adress.getText().toString().length()==0){
                toast("请选择所在地区");
                return;
            }
            if (et_detail_adress.getText().toString().length() == 0) {
                toast( "请输入详细地址" );
                return;
            }
            editAddress();
        } else if (id == R.id.tv_delete) {
            showPopupWindow();
        } else if (id == R.id.tv_adress) {
            KeyBoardUtil.KeyBoard( this, "close" );
            ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow( AddandUpdateActivity.this );
            mChangeAddressPopwindow.setAddress( "浙江", "杭州", "上城区" );
            mChangeAddressPopwindow.showAtLocation( tv_adress, Gravity.BOTTOM, 0, 0 );
            mChangeAddressPopwindow
                    .setAddresskListener( new ChangeAddressPopwindow.OnAddressCListener() {

                        @Override
                        public void onClick(String province, String city, String area) {
                            // TODO Auto-generated method stub
                            Log.d( "Debug", "返回的地址是" + province + "-" + city + "-" + area );
                            Province = province;
                            City = city;
                            County = area;
                            tv_adress.setText( province + "" + city + "" + area );
                        }
                    } );
        }

    }

    //弹出提示
    private void showPopupWindow() {
        View contentView = LayoutInflater.from( this ).inflate( R.layout.popupwind_zujiactivity_clear1, null );
        mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        TextView tvPopKnowFinish = (TextView) contentView.findViewById( R.id.tv_popwindow_finish );
        TextView tvPopKnowSure = (TextView) contentView.findViewById( R.id.tv_popwindow_sure );
        tvPopKnowFinish.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        } );

        tvPopKnowSure.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAddress();
                mPopupWindow.dismiss();
            }
        } );
        mPopupWindow.setContentView( contentView );
        mPopupWindow.showAtLocation( contentView, Gravity.BOTTOM, 0, 0 );
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Log.d( "Debug", "kaiqi" );
            isDefault = true;
        } else {
            Log.d( "Debug", "guanbi" );
            isDefault = false;
        }
    }

}
