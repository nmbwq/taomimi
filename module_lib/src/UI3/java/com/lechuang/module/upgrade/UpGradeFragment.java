package java.com.lechuang.module.upgrade;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LinearGradientFontSpan;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.Utils;
import com.common.app.view.CircularProgressView;
import com.common.app.view.GlideRoundTransform;
import com.common.app.view.GridItemDecoration;
import com.common.app.view.NoShouYiTiaoJian;
import com.common.app.view.TiaoJianView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.lechuang.module.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.UpGradeBean;
import java.com.lechuang.module.upgrade.adapter.MZBannerViewHolder;
import java.com.lechuang.module.upgrade.adapter.OtherRvAdapter;
import java.com.lechuang.module.upgrade.bean.UpGradeEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * @author: zhengjr
 * @since: 2018/8/20
 * @describe:
 */

@Route(path = ARouters.PATH_UP_GRADE_F)
public class UpGradeFragment extends LazyBaseFragment implements OnRefreshLoadMoreListener, View.OnClickListener {

    private SmartRefreshLayout mSmartHomeOther;
    private int page = 1;
    private RecyclerView mRvHomeOtherProduct;
    private int mPosition = 0;//表示筛选条件，0为综合，1为价格，2为销量，3为收益
    private boolean mSort = true;//表示价格的排列，true为箭头向上
    private boolean mIsSingleLine = true;//单行和多行切换
    private LinearLayout mNetError, mLlPopweixin, mLlPoppengyou, mLlPophaoyou, mLlPopkongjian;
    private int shuaxin = 10;
    private boolean mNeedNotifyList = false;//点击头部的筛选，是否需要更新列表显示头部
    private View mVsCommonWevError;
    private boolean mLoadError = true;
    private RelativeLayout mShaiXuanHomeTop;
    private ImageView IvFenXiangTitle, mIvPopFinish, mIvPopWeiXin;
    private MZBannerView mBannerProduct;
    private PopupWindow mPopupWindow;
    private TextView tvPopTitle, tvPopContent, mTvPercentage;
    private PopupWindow mPopupWindow1;
    private ImageView tvPopKnow, tvPopImage;

    boolean isFromPayAfter = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_up_grade;
    }

    @Override
    protected void findViews() {
        mSmartHomeOther = mInflate.findViewById( R.id.smart_home_other );
        mRvHomeOtherProduct = mInflate.findViewById( R.id.rv_home_other_product );
        mShaiXuanHomeTop = mInflate.findViewById( R.id.rl_title );
        IvFenXiangTitle = mInflate.findViewById( R.id.iv_title_fenxiang_one );//分享图片

        mRvHomeOtherProduct.addItemDecoration( new GridItemDecoration(
                new GridItemDecoration.Builder( getActivity() )
                        .size( 0 )
        ) );
        IvFenXiangTitle.setOnClickListener( this );
        $( R.id.tv_common_hint ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getProductList(false);
            }
        } );
    }

    @Override
    protected void initView() {

        EventBus.getDefault().register( this );
        Bundle arguments = getArguments();
        mSmartHomeOther.setOnRefreshLoadMoreListener( this );
        mSmartHomeOther.setEnableLoadMore( false );
    }

    @Override
    protected void getData() {
        //smart设置属性，设置自动刷新，调用刷新方法
        mSmartHomeOther.autoRefresh( 100 );
    }


    private UpGradeEntity otherAllEntityHeader;
    //    分享的信息
    UpGradeBean.ShareBean share;

    private void getNextTbClass() {
        Map<String, Object> allParam = new HashMap<>();
        if (UserHelper.getInstence().isLogin()) {
            allParam.put( "userId", UserHelper.getInstence().getUserInfo().getId() );
        }

        NetWork.getInstance()
                .setTag( Qurl.getAppraise2 )
                .getApiService( ModuleApi.class )
                .getAppraise2( allParam )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<UpGradeBean>( getActivity(), false, false ) {
                    @Override
                    public void onSuccess(UpGradeBean result) {
                        if (result == null || result == null) {
                            setRefreshLoadMoreState( true, true );
                            return;
                        }
                        setRefreshLoadMoreState( true, false );
                        if (mProductListEntity == null) {
                            mProductListEntity = new ArrayList<>();
                        }

//                        if (result.agencyLevel > 0) {
//                            IvFenXiangTitle.setVisibility( View.GONE );
//                        } else {
//                            IvFenXiangTitle.setVisibility( View.VISIBLE );
//                        }
                        if (isFromPayAfter == true && result.agencyLevel == 3) {
                            isFromPayAfter = false;
                            showPopupWindow1();
                        }

                        share = result.share;
                        setOtherProductData1( result );
                    }
                } );

    }

//    private List<HomeOtherBean.ProductListBean> mRroductList;


    private List<UpGradeEntity> mProductListEntity;
    private OtherRvAdapter<UpGradeEntity, BaseViewHolder> mOtherRvAdapter;
    private BaseQuickAdapter<UpGradeEntity, BaseViewHolder> mHeaderBaseQuickAdapter;
    private int distance;

    private void setOtherProductData1(final UpGradeBean result) {
        if (mProductListEntity == null) {
            mProductListEntity = new ArrayList<>();
        }
        mProductListEntity.clear();
        distance = 0;
        if (result.agencyLevel == 2 | result.agencyLevel == 0) {
            if (page == 1) {
                UpGradeEntity otherAllEntity = new UpGradeEntity( UpGradeEntity.TYPE_HEADER );
                if (result.boardList != null && result.boardList.size() > 0) {
                    otherAllEntity.boardList = result.boardList;
                }
                if (result.proImgList != null && result.proImgList.size() > 0) {
                    otherAllEntity.proImgList = result.proImgList;
                }
                if (result.rightsImgList != null && result.rightsImgList.size() > 0) {
                    otherAllEntity.rightsImgList = result.rightsImgList;
                }
                if (result.topImg != null) {
                    otherAllEntity.topImg = result.topImg;
                }
                otherAllEntity.status = result.status;//是否满足升级条件
                otherAllEntity.isMaxLevel = result.isMaxLevel;//是否可以继续升级
                otherAllEntity.giftPrice = result.giftPrice;//是否可以继续升级
                otherAllEntity.mProductListBean = result;
                mProductListEntity.add( otherAllEntity );
            }
            for (int i = 0; i < result.rightsImgList.size(); i++) {
                UpGradeEntity otherMoreEntity = new UpGradeEntity( UpGradeEntity.TYPE_PRODUCT );
                otherMoreEntity.img = result.rightsImgList.get( i ).img;
                mProductListEntity.add( otherMoreEntity );
            }
        }
        if (result.agencyLevel != 2 && result.agencyLevel != 0) {
            UpGradeEntity otherAllEntity = new UpGradeEntity( UpGradeEntity.TYPE_SHAIXUAN );
            otherAllEntity.scale = result.scale;//百分比
            otherAllEntity.mProductListBean = result;
            if (result.boardList != null && result.boardList.size() > 0) {
                otherAllEntity.boardList = result.boardList;
            }

            mProductListEntity.add( otherAllEntity );
        }


        if (mOtherRvAdapter == null) {
            mOtherRvAdapter = new OtherRvAdapter<UpGradeEntity, BaseViewHolder>( mProductListEntity ) {
                @Override
                protected void addItemTypeView() {
                    addItemType( UpGradeEntity.TYPE_HEADER, R.layout.layout_up_grade_title );
                    addItemType( UpGradeEntity.TYPE_SHAIXUAN, R.layout.layout_up_grade_other_title );
                    addItemType( UpGradeEntity.TYPE_PRODUCT, R.layout.layout_up_grade_dibu );
                }

                @Override
                protected void convert(BaseViewHolder helper, final UpGradeEntity item) {

                    if (item.getItemType() == UpGradeEntity.TYPE_HEADER) {
                        ImageView mIvTitle = helper.getView( R.id.iv_title_down );
                        Glide.with( BaseApplication.getApplication() ).load( item.topImg ).fitCenter().into( mIvTitle );
                        TextView mTvOne = helper.getView( R.id.tv_center_title );
                        mTvOne.setText( getRadiusGradientSpan( "特约店主升级进度", 0xFF26272B, 0xFF444549 ) );

                        //礼包价格
                        helper.setText( R.id.tv_three, "" + item.giftPrice );

                        //直邀
                        CircularProgressView circularProgressViewOne = helper.getView( R.id.progress_bar );
                        circularProgressViewOne.setProgress( item.mProductListBean.directCount * 100 / item.mProductListBean.upDirectCount );
                        TextView mTvZhiYaoNum = helper.getView( R.id.tv_zhiyao_num );//已直邀人数
                        TextView mTvZhiYaoNeedNum = helper.getView( R.id.tv_zhiyao_need_num );//直邀需要人数
                        TextView mTvNeedNum = helper.getView( R.id.tv_need_num );//需要多少人升级
                        mTvZhiYaoNum.setText( "" + item.mProductListBean.directCount );
                        mTvZhiYaoNeedNum.setText( "/" + item.mProductListBean.upDirectCount );
                        mTvNeedNum.setText( "共需邀请" + item.mProductListBean.upDirectCount + "人" );

                        //间接邀请
                        CircularProgressView circularProgressViewOtherOne = helper.getView( R.id.progress_bar_other );
                        circularProgressViewOtherOne.setProgress( item.mProductListBean.indirectCount * 100 / item.mProductListBean.upIndirectCount );
                        TextView mTvZhiYaoOtherNum = helper.getView( R.id.tv_zhiyao_other_num );//已间接邀请人数
                        TextView mTvZhiYaoOtherNeedNum = helper.getView( R.id.tv_zhiyao_other_need_num );//间接邀请需要人数
                        TextView mTvOtherNeedNum = helper.getView( R.id.tv_jianjie_num );//需要多少人升级
                        mTvZhiYaoOtherNum.setText( "" + item.mProductListBean.indirectCount );
                        mTvZhiYaoOtherNeedNum.setText( "/" + item.mProductListBean.upIndirectCount );
                        mTvOtherNeedNum.setText( "共需邀请" + item.mProductListBean.upIndirectCount + "人" );

                        //分享
                        ImageView IvShare = helper.getView( R.id.iv_title_fenxiang );
                        IvShare.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                showPopupWindowShare();
                            }
                        } );

                        ImageView IvFreeUp = helper.getView( R.id.iv_free_up );
                        IvFreeUp.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                showPopupWindow( item.mProductListBean.agencyLevel - 1, item.mProductListBean.status, item.mProductListBean.isMaxLevel, item.mProductListBean.wechat_number );
                            }
                        } );
                        //轮播图
                        setBannerData( helper, item.proImgList );
                        //运营商收益信息
                        setBoardList( helper, item.boardList );
                        //购买礼包按钮
                        ImageView imageView = helper.getView( R.id.iv_up_grade_buy );
                        imageView.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                if (!UserHelper.getInstence().isLogin()) {
                                    ARouter.getInstance().build( ARouters.PATH_LOGIN ).navigation();
                                    return;
                                }
                                ARouter.getInstance().build( ARouters.PATH_UP_GRADE_DETAILS ).withInt( "intId", 1000000000 ).navigation();
                            }
                        } );

                    } else if (item.getItemType() == UpGradeEntity.TYPE_SHAIXUAN) {
                        //iv_title_fenxiang
                        ImageView fenxaingImg = helper.getView( R.id.iv_title_fenxiang );
                        fenxaingImg.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {
                                showPopupWindowShare();
                            }
                        } );
                        IvFenXiangTitle.setVisibility(View.GONE);
                        //sb_bar
//                        TextView TvBaiFen=helper.getView(R.id.tv_baifen);
//                        SeekBar SbBar=helper.getView(R.id.sb_bar);
//                        SbBar.setProgress();
                        if (item.mProductListBean.agencyLevel != 2) {
                            String scale = item.scale.substring( 0, item.scale.length() - 1 );
                            int num = Integer.parseInt( scale );
                            //hp_bar
//                            HorizontalProgressBar horizontalProgressBar=helper.getView(R.id.hp_bar);
//                            horizontalProgressBar.setProgressWithAnimation(num);
//                            horizontalProgressBar.setCurrentProgress(num);

//                            num=98;
                            TextView textView = helper.getView( R.id.tv_percentage );
                            int pro1 = (int) (285 * (Double.valueOf( num ) / 100));
                            if (pro1 < 22) {
                            } else if (pro1 > 263) {
                                pro1 = 263;
                                int pro = dp2px( getContext(), pro1 );
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                                layoutParams.setMargins( pro, 0, 0, 0 );//4个参数按顺序分别是左上右下
                                textView.setLayoutParams( layoutParams );
                            } else {
                                int pro = dp2px( getContext(), pro1 - 22 );
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                                layoutParams.setMargins( pro, 0, 0, 0 );//4个参数按顺序分别是左上右下
                                textView.setLayoutParams( layoutParams );
                            }
                            textView.setText( num + "%" );
                            ProgressBar verticalProgressBar = helper.getView( R.id.verticalProgressBar );
                            verticalProgressBar.setProgress( num );

                            //需要人数
//                        TextView needPeople=helper.getView(R.id.tv_need_people);
//                        needPeople.setText("直属特约店主人数(共需"+"人)");

                            //运营商收益信息
                            setBoardList( helper, item.boardList );

                            //升级
                            ImageView imageViewUp = helper.getView( R.id.iv_up_grade_up );
                            imageViewUp.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    showPopupWindow( item.mProductListBean.agencyLevel - 1, item.mProductListBean.status, item.mProductListBean.isMaxLevel, item.mProductListBean.wechat_number );
                                }
                            } );
                        }

                    } else if (item.getItemType() == UpGradeEntity.TYPE_PRODUCT) {
                        //iv_title_up
                        ImageView ivItemOther = helper.getView( R.id.iv_title_up );
                        Glide.with( BaseApplication.getApplication() ).load( item.img ).into( ivItemOther );
                    }
                }
            };
            mRvHomeOtherProduct.setHasFixedSize( true );
            mRvHomeOtherProduct.setNestedScrollingEnabled( false );
            GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), this.mIsSingleLine ? 1 : 2 );
            mRvHomeOtherProduct.setLayoutManager( gridLayoutManager );


            mRvHomeOtherProduct.setAdapter( mOtherRvAdapter );

            //底部推荐产品点击事件 todo
//            mOtherRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    int itemViewType = adapter.getItemViewType(position);
//                    if (itemViewType == OtherAllEntity.TYPE_PRODUCT){
//                        RouterBean routerBean = new RouterBean();
//                        routerBean.type = 9;
//                        routerBean.tbCouponId = mProductListEntity.get(position).mProductListBean.tbCouponId;
//                        routerBean.mustParam = "type=1"
//                                + "&id=" + mProductListEntity.get(position).mProductListBean.id
//                                + "&tbItemId=" + mProductListEntity.get(position).mProductListBean.tbItemId;
//
//                        LinkRouterUtils.getInstance().setRouterBean(getActivity(), routerBean);
//                    }
//
//                }
//            });

            mRvHomeOtherProduct.addOnScrollListener( new RecyclerView.OnScrollListener() {


                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged( recyclerView, newState );

                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;

                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        if (lastItemPosition > shuaxin) {
                            if (mOtherRvAdapter != null) {
                                page++;
//                                getProductList(false);
                                shuaxin = shuaxin + 20;
                            }
                        }

                        int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled( recyclerView, dx, dy );
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        View firstVisibItem = recyclerView.getChildAt( 0 );
                        int height = firstVisibItem.getHeight();

                        distance += dy;
                        if (otherAllEntityHeader == null) {
                            if (distance == 0) {
                                mShaiXuanHomeTop.setVisibility( View.INVISIBLE );
                            } else {
                                mShaiXuanHomeTop.setVisibility( View.VISIBLE );
                            }
                        } else {
                            if (distance >= height) {
                                mShaiXuanHomeTop.setVisibility( View.VISIBLE );
                            } else {
                                mShaiXuanHomeTop.setVisibility( View.INVISIBLE );
                            }
                        }
                    }
                }
            } );
        } else {
            mOtherRvAdapter.notifyDataSetChanged();
        }

        if (mNeedNotifyList) {
            mNeedNotifyList = false;
            mRvHomeOtherProduct.scrollToPosition( 0 );
        }
    }

    /**
     * dp转换成px
     */
    private int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //字体渐变色
    public static SpannableStringBuilder getRadiusGradientSpan(String string, int startColor, int endColor) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder( string );
        LinearGradientFontSpan span = new LinearGradientFontSpan( startColor, endColor );
        spannableStringBuilder.setSpan( span, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        return spannableStringBuilder;

    }

    /**
     * 设置banner数据
     *
     * @param indexBannerList
     */
    private List<String> mBannderData = new ArrayList<>();

    private void setBannerData(BaseViewHolder helper, List<UpGradeBean.ProImgListBean> indexBannerList) {
        if (indexBannerList == null) {
            indexBannerList = new ArrayList<>();
        }
        mBannerProduct = helper.getView( R.id.banner_product );

        if (indexBannerList == null || indexBannerList.size() <= 0) {
            mBannerProduct.setVisibility( View.GONE );
            return;
        }
        mBannerProduct.setVisibility( View.VISIBLE );
        mBannderData.clear();
        for (int i = 0; i < indexBannerList.size(); i++) {
            mBannderData.add( indexBannerList.get( i ).img );
        }
        mBannerProduct.setIndicatorVisible( false );
        mBannerProduct.setPadding( 10, 0, 10, 0 );

        final List<UpGradeBean.ProImgListBean> finalIndexBannerList = indexBannerList;
        mBannerProduct.setBannerPageClickListener( new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                ARouter.getInstance().build( ARouters.PATH_UP_GRADE_DETAILS ).withInt( "intId", finalIndexBannerList.get( position ).integerId ).navigation();
            }
        } );
        mBannerProduct.setPages( mBannderData, new MZHolderCreator<MZBannerViewHolder>() {
            @Override
            public MZBannerViewHolder createViewHolder() {
                return new MZBannerViewHolder();
            }
        } );
        mBannerProduct.start();

    }

    private List<String> mBoardList = new ArrayList<>();
    private List<ImageView> mBoardViewList = new ArrayList<>();

    //设置运营商收益信息
    private void setBoardList(BaseViewHolder helper, List<UpGradeBean.BoardListBean> indexBoardList) {
        if (indexBoardList == null) {
            mBoardList = new ArrayList<>();
        }
//        ImageView mIvOne=helper.getView(R.id.iv_user_photo_one);
//        ImageView mIvTwo=helper.getView(R.id.iv_user_photo_two);
//        ImageView mIvThree=helper.getView(R.id.iv_user_photo_three);
//        ImageView mIvFour=helper.getView(R.id.iv_user_photo_four);
//        ImageView mIvFive=helper.getView(R.id.iv_user_photo_five);
//        mBoardViewList.add(mIvOne);
//        mBoardViewList.add(mIvTwo);
//        mBoardViewList.add(mIvThree);
//        mBoardViewList.add(mIvFour);
//        mBoardViewList.add(mIvFive);
//        for (int i=0;i<indexBoardList.size();i++){
//            Glide.with(BaseApplication.getApplication()).load(indexBoardList.get(i).photo).transform(new GlideRoundTransform(BaseApplication.getApplication(), 200)).into(mBoardViewList.get(i));
//        }
        ViewPager viewPager = helper.getView( R.id.viewPager );
        OnePageThreeItemAdapter adapter = new OnePageThreeItemAdapter( viewPager, getActivity(), (float) 1.5, indexBoardList, helper );
        viewPager.setAdapter( adapter );
        adapter.configViewPager();
        //默认是取中间的位置  5 个数据  取第三个   下标为2
        TextView money = helper.getView( R.id.tv_money );
        money.setText( indexBoardList.get( 2 ).revenue + "元" );
        TextView name = helper.getView( R.id.tv_name );
        name.setText( indexBoardList.get( 2 ).nickName );

    }

    private void showPopupWindowShare() {
        //分享弹框
        View contentView = LayoutInflater.from( getContext() ).inflate( R.layout.popupwind_share_goods, null );
        mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        mLlPopweixin = (LinearLayout) contentView.findViewById( R.id.ll_shareweixin );
        mLlPoppengyou = (LinearLayout) contentView.findViewById( R.id.ll_sharepengyou );
        mLlPophaoyou = (LinearLayout) contentView.findViewById( R.id.ll_sharehaoyou );
        mLlPopkongjian = (LinearLayout) contentView.findViewById( R.id.ll_sharekongjian );
        mIvPopFinish = (ImageView) contentView.findViewById( R.id.iv_finish );

        mLlPopweixin.setOnClickListener( this );
        mLlPoppengyou.setOnClickListener( this );
        mLlPophaoyou.setOnClickListener( this );
        mLlPopkongjian.setOnClickListener( this );
        mIvPopFinish.setOnClickListener( this );

        mPopupWindow.setContentView( contentView );
        mPopupWindow.showAtLocation( contentView, Gravity.BOTTOM, 0, 0 );
    }

    //弹出提示

    /**
     * @param agencyLevel 当前等级
     * @param status      是否满足条件升级
     * @param isMaxLevel  继续升级
     */
    private void showPopupWindow(int agencyLevel, String status, int isMaxLevel, final String wechat_number) {
        View contentView = LayoutInflater.from( getActivity() ).inflate( R.layout.popupwind_up_grade_up, null );
        mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnow = (ImageView) contentView.findViewById( R.id.iv_popwindow_btn );
        tvPopTitle = (TextView) contentView.findViewById( R.id.tv_popwindow_tishi );
        tvPopContent = (TextView) contentView.findViewById( R.id.tv_popwindow_content );
        tvPopImage = (ImageView) contentView.findViewById( R.id.iv_popwindow_img );
        if (isMaxLevel == 0 && agencyLevel == 4) {//不可升级
            tvPopTitle.setTextColor( getResources().getColor( R.color.c_2B2B2B ) );
            tvPopImage.setBackground( getResources().getDrawable( R.drawable.popwindow_up_grade_up_one ) );
            tvPopContent.setText( "您已完成所有升级" );
            tvPopKnow.setBackground( getResources().getDrawable( R.drawable.bg_popwindow_btn_know ) );
            tvPopKnow.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    mPopupWindow.dismiss();
                }
            } );
        } else {//可升级
            if (status.equals( "0" )) {//不满足升级条件
                if (agencyLevel == 1) {//超级会员
                    tvPopTitle.setTextColor( getResources().getColor( R.color.c_A5A5A5 ) );
                    tvPopImage.setBackground( getResources().getDrawable( R.drawable.popwindow_up_grade_up_two ) );
                    tvPopContent.setText( "您还不符合申请条件\n请继续邀请好友注册淘觅觅哦！" );
                    tvPopKnow.setBackground( getResources().getDrawable( R.drawable.bg_popwindow_btn_huiknow ) );
                    tvPopKnow.setOnClickListener( new OnClickEvent() {
                        @Override
                        public void singleClick(View v) {
                            mPopupWindow.dismiss();
                        }
                    } );
                } else {//其他会员
                    tvPopTitle.setTextColor( getResources().getColor( R.color.c_A5A5A5 ) );
                    tvPopImage.setBackground( getResources().getDrawable( R.drawable.popwindow_up_grade_up_three ) );
                    tvPopContent.setText( "对不起您不满足升级条件" );
                    tvPopKnow.setBackground( getResources().getDrawable( R.drawable.bg_popwindow_btn_huiknow ) );
                    tvPopKnow.setOnClickListener( new OnClickEvent() {
                        @Override
                        public void singleClick(View v) {
                            mPopupWindow.dismiss();
                        }
                    } );
                }
            } else {//满足升级条件
                if (agencyLevel < 2) {//超级会员
                    tvPopTitle.setTextColor( getResources().getColor( R.color.c_2B2B2B ) );
                    tvPopImage.setBackground( getResources().getDrawable( R.drawable.popwindow_up_grade_up_four ) );
                    tvPopContent.setText( "恭喜您已经满足特约店主\n升级条件，请联系上级进行升级" );
                    tvPopKnow.setBackground( getResources().getDrawable( R.drawable.bg_popwindow_btn_copy ) );
                    tvPopKnow.setOnClickListener( new OnClickEvent() {
                        @Override
                        public void singleClick(View v) {
                            if (TextUtils.isEmpty( wechat_number )) {
                                toast( "激活码为空！" );
                                return;
                            }
                            ClipData clipData = ClipData.newPlainText( "app_inviteCode", wechat_number );
                            ((ClipboardManager) getActivity().getSystemService( CLIPBOARD_SERVICE )).setPrimaryClip( clipData );
                            Utils.toast( "复制成功" );
                        }
                    } );
                } else {//其他会员
                    tvPopTitle.setTextColor( getResources().getColor( R.color.c_2B2B2B ) );
                    tvPopImage.setBackground( getResources().getDrawable( R.drawable.popwindow_up_grade_up_four ) );
                    tvPopContent.setText( "恭喜您已经满足高级店主\n升级条件，请联系上级进行升级" );
                    tvPopKnow.setBackground( getResources().getDrawable( R.drawable.bg_popwindow_btn_copy ) );
                    tvPopKnow.setOnClickListener( new OnClickEvent() {
                        @Override
                        public void singleClick(View v) {
                            if (TextUtils.isEmpty( wechat_number )) {
                                toast( "激活码为空！" );
                                return;
                            }
                            ClipData clipData = ClipData.newPlainText( "app_inviteCode", wechat_number );
                            ((ClipboardManager) getActivity().getSystemService( CLIPBOARD_SERVICE )).setPrimaryClip( clipData );
                            Utils.toast( "复制成功" );
                        }
                    } );
                }
            }
        }

//        tvPopTitle.setText ( title );
//        tvPopContent.setText ( content1 );
//        tvPopImage.setText ( content2 );
//        tvPopKnow.setOnClickListener ( this );
        mPopupWindow.setContentView( contentView );
        mPopupWindow.showAtLocation( contentView, Gravity.BOTTOM, 0, 0 );
    }


    /**
     * 支付成功成为特约店主 弹窗
     */
    private void showPopupWindow1() {
        View contentView = LayoutInflater.from( getActivity() ).inflate( R.layout.popupwind_up_grade_up1, null );
        mPopupWindow1 = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnow = (ImageView
                ) contentView.findViewById( R.id.iv_popwindow_btn );
        tvPopContent = (TextView) contentView.findViewById( R.id.tv_popwindow_content );
        tvPopContent.setText( "您购买的礼包我们会尽快安排发货哦~\n请继续加油" );
        tvPopKnow.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow1.dismiss();
            }
        } );
        mPopupWindow1.setContentView( contentView );
        mPopupWindow1.showAtLocation( contentView, Gravity.BOTTOM, 0, 0 );
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mBannerProduct != null) {
            mBannerProduct.start();//开始轮播
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBannerProduct != null) {
            mBannerProduct.pause();//暂停轮播
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        page++;
//        getProductList(false);
        getNextTbClass();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        page = 1;
        shuaxin = 10;
        mSmartHomeOther.setNoMoreData( false );
//        getProductList(false);
        getNextTbClass();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        if (page == 1) {
            mSmartHomeOther.finishRefresh( state );
            if (!state) {
                //第一次加载失败时，再次显示时可以重新加载
                this.mIsFirstVisible = true;
            }
        } else if (noMoreData) {
            mSmartHomeOther.finishLoadMoreWithNoMoreData();
        } else {
            mSmartHomeOther.finishLoadMore( state );
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_finish) {
            mPopupWindow.dismiss();
        } else if (id == R.id.ll_shareweixin) {
            Log.d( "Debug", "返回的分享信息" + share.getShareLink() );
            onShare( SHARE_MEDIA.WEIXIN );
        } else if (id == R.id.ll_sharepengyou) {
            onShare( SHARE_MEDIA.WEIXIN_CIRCLE );
        } else if (id == R.id.ll_sharehaoyou) {
            onShare( SHARE_MEDIA.QQ );
        } else if (id == R.id.ll_sharekongjian) {
            onShare( SHARE_MEDIA.QZONE );
        } else if (id == R.id.iv_title_fenxiang_one) {
            showPopupWindowShare();
        }

    }

    /**
     * 分享操作
     *
     * @param platform
     */
    public void onShare(SHARE_MEDIA platform) {
        ShareUtils.shareWeb( getActivity(), share.getShareLink(), share.getShareTitle(), share.getShareWords(), share.getShareImg(), platform );
        mPopupWindow.dismiss();
    }


//    @Override
//    public void onSelecter(int position, boolean sort) {
//
//        if (mPosition == position && position != 1) {
//            return;
//        }
//        page = 1;
//        mPosition = position;
//        mSort = sort;
//        shuaxin = 10;
//        mNeedNotifyList = true;
////        getProductList(true);
////        mSmartHomeOther.autoRefresh();
//
//    }

//    @Override
//    public void onChangeStyle(boolean isSingleLine) {
//        mIsSingleLine = isSingleLine;
//        mBaseProductQuickAdapter = null;
//        mNeedNotifyList = true;
//        setOtherProductData1();
//
//    }

    class PublicSelecter implements TiaoJianView.OnSelecterLisenter {

        private NoShouYiTiaoJian mNoShouYiTiaoJian;

        public PublicSelecter(NoShouYiTiaoJian noShouYiTiaoJian) {
            this.mNoShouYiTiaoJian = noShouYiTiaoJian;
        }

        @Override
        public void onSelecter(int position, boolean sort) {
            if (mPosition == position && position != 1) {
                return;
            }
            Logger.e( "----", position + "***" + sort );
            page = 1;
            mPosition = position;
            mSort = sort;
            shuaxin = 10;
//            mSmartProduct.autoRefresh();
            mNoShouYiTiaoJian.updataStyles( position );
            mNeedNotifyList = true;
//            getProductList(true);

        }

        @Override
        public void onChangeStyle(boolean isSingleLine) {
            mIsSingleLine = isSingleLine;
//            mBaseProductQuickAdapter = null;
            mNeedNotifyList = true;
//            setOtherProductData1();
            mNoShouYiTiaoJian.updataShowStyle();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase( Constant.LOGIN_SUCCESS ) || message.equalsIgnoreCase( Constant.LOGOUT_SUCCESS )) {
            mSmartHomeOther.finishRefresh();
            mSmartHomeOther.finishLoadMore();
            mRvHomeOtherProduct.smoothScrollToPosition( 0 );
            //当前界面如果显示，就立即刷新，否则滑动显示是刷新
            if (getUserVisibleHint()) {
                mNeedNotifyList = true;
                mSmartHomeOther.autoRefresh( 100 );
            } else {
                this.mIsFirstVisible = true;
            }
        } else if (message.equalsIgnoreCase( Constant.PAY_UPDATE )) {
            isFromPayAfter = true;
            mSmartHomeOther.autoRefresh( 100 );
        }
    }


//---------------------------为了测试方便放在这里（设计到两个界面之间的传值）后期封装成工具类-------------------------------------------

    /**
     * Created by chenchen on 2017/8/15.
     * <p>
     * 代码中有几个position需要注意一些
     * 1. setCurrentItem的position参数，是指当前页的第一个Item的所在position
     * 2. OnPageChangeListener的position参数，同样也是指当前页的第一个Item的所在position
     * 3. instantiateItem的position参数，是指每一个Item的所在position
     * 4. PageTransformer的position参数，是指每一个Item相对于Page所占的比例，当前页的第一个Item是0，左边的依次减去Item的宽度，右边的依次加上Item的宽度
     * 比如一页只有一个Item。那么当前页的position为0。左边的position分别为-1。右边的position分别为1；
     * 比如一页有三个Item。那么当前页的Item的position分别为0、1/3、2/3。左边的position分别为-1、-2/3、-1/3。右边的position分别为1、4/3、5/3。
     */
    class OnePageThreeItemAdapter extends PagerAdapter {
        //一屏最多3个item，不能随便改变这个值，改这个数量必须要改动很多逻辑
        private static final int ONE_PAGE_ITEM_COUNT = 5;

        private Context context;
        private ViewPager viewPager;
        // 居中的Item的最大放大值
        private float itemMaxScale;
        //在原始的数据前后各加了3项数据
        private List<String> repairData = new ArrayList<String>();
        //原始的数据，用于数据检索等
        private List<String> sourceData = new ArrayList<String>();
        List<UpGradeBean.BoardListBean> indexBoardList = new ArrayList<>();

        BaseViewHolder helper;

        public OnePageThreeItemAdapter(ViewPager viewPager, Context context, float itemMaxScale, List<UpGradeBean.BoardListBean> indexBoardList, BaseViewHolder helper) {
            this.viewPager = viewPager;
            this.context = context;
            this.itemMaxScale = itemMaxScale;
            this.indexBoardList = indexBoardList;
            this.helper = helper;
            initData();
            repairDataForLoop();
            handleLoop();
        }

        /**
         * 调用之前保证initData方法已被调用
         */
        @Override
        public void notifyDataSetChanged() {
            repairDataForLoop();
            super.notifyDataSetChanged();
        }

        /**
         * 初始化原始数据
         */
        public void initData() {
            sourceData.clear();
            for (int i = 0; i < indexBoardList.size(); i++) {
                sourceData.add( i + "" );
            }
        }

        /**
         * 为了能无限滚动，将末三项复制加入列表开头，将前三项复制加入到列表末尾
         */
        private void repairDataForLoop() {
            repairData.clear();
            List<String> beforeTemp = new ArrayList<String>();
            List<String> afterTemp = new ArrayList<String>();
            for (int i = 0; i < ONE_PAGE_ITEM_COUNT; i++) {
                beforeTemp.add( 0, sourceData.get( sourceData.size() - 1 - i ) );
                afterTemp.add( sourceData.get( i ) );
            }
            repairData.addAll( beforeTemp );
            repairData.addAll( sourceData );
            repairData.addAll( afterTemp );
        }

        /**
         * 真正处理无限滚动的逻辑，在滑动到边界时，立马跳转到对应的位置
         */
        private void handleLoop() {
            viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                // 是否应该强制跳转到首页
                boolean shouldToBefore = false;
                // 是否应该强制跳转到末页
                boolean shouldToAfter = false;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //这里的position是指每一页的第一个Item的position
                    if (position == 0) {
                        shouldToAfter = true;
                    } else if (position == getCount() - ONE_PAGE_ITEM_COUNT) {
                        shouldToBefore = true;
                    }

                    if (position <= 2) {
                        //012
                        TextView money = helper.getView( R.id.tv_money );
                        money.setText( indexBoardList.get( position + 2 ).revenue + "元" );
                        TextView name = helper.getView( R.id.tv_name );
                        name.setText( indexBoardList.get( position +2 ).nickName );
                    } else if (2 < position && position <= 7) {
                        // 34567
                        TextView money = helper.getView( R.id.tv_money );
                        money.setText( indexBoardList.get( position -3 ).revenue + "元" );
                        TextView name = helper.getView( R.id.tv_name );
                        name.setText( indexBoardList.get( position -3).nickName );

                    } else if (7 < position && position <= 12) {
                        //89 10
                        TextView money = helper.getView( R.id.tv_money );
                        money.setText( indexBoardList.get( position -3-5 ).revenue + "元" );
                        TextView name = helper.getView( R.id.tv_name );
                        name.setText( indexBoardList.get( position -3-5).nickName );
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    //onPageScrollStateChanged的SCROLL_STATE_IDLE比onPageSelected晚调用，如果在onPageSelected中处理方法，则不会有滑动动画效果
//                    Log.d( "Debug", "1-----------------------" );
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        if (shouldToAfter) {
                            //这里的position是指每一页的第一个Item的position
                            viewPager.setCurrentItem( getCount() - ONE_PAGE_ITEM_COUNT * 2, false );
                            handleScale( getCount() - ONE_PAGE_ITEM_COUNT * 2 + 1 );
                            shouldToAfter = false;
//                            Log.d( "Debug", "2-----------------------" );
                        }
                        if (shouldToBefore) {
                            //这里的position是指每一页的第一个Item的position
                            viewPager.setCurrentItem( ONE_PAGE_ITEM_COUNT, false );
                            handleScale( ONE_PAGE_ITEM_COUNT + 1 );
                            shouldToBefore = false;
//                            Log.d( "Debug", "3----------------------" );
                        }
                    }
                }
            } );
        }

        /**
         * 调用了setCurrentItem(position, false)后，设置的PageTransformer不会生效，
         * 也就是说中间需要放大的项不会放大，所以手动将这一项放大
         */
        private void handleScale(int position) {
//            Log.d( "Debug", "4----------------------位置" + position );

            View view = findCenterView( position );
            if (view == null) {
                return;
            }
            view.setScaleX( itemMaxScale );
            view.setScaleY( itemMaxScale );
        }

        /**
         * 通过在instantiateItem方法给每个View设置的Tag来标记，找出内存中的View
         */
        private View findCenterView(int position) {
            for (int i = 0; i < viewPager.getChildCount(); i++) {
                View view = viewPager.getChildAt( i );
                if (String.valueOf( position ).equals( view.getTag() )) {
                    return view;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return repairData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
//            Log.d( "Debug222","适配器返回的位置信息是"+ position);
            View view = View.inflate( context, R.layout.activity_text_item, null );
            view.setTag( String.valueOf( position ) );
            ImageView img = (ImageView) view.findViewById( R.id.img );
            Glide.with( BaseApplication.getApplication() ).load( indexBoardList.get( Integer.parseInt( repairData.get( position ) ) ).photo ).transform( new GlideRoundTransform( BaseApplication.getApplication(), 200 ) ).into( img );
            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里的position是指每一页的第一个Item的position
                    viewPager.setCurrentItem( position == 0 ? 0 : position - 2, true );
//                    Toast.makeText( context, "点击了" + repairData.get( position ), Toast.LENGTH_SHORT ).show();
                    TextView money = helper.getView( R.id.tv_money );
                    money.setText( indexBoardList.get( Integer.parseInt( repairData.get( position ) ) ).revenue + "元" );
                    TextView name = helper.getView( R.id.tv_name );
                    name.setText( indexBoardList.get( Integer.parseInt( repairData.get( position ) ) ).nickName );

                }
            } );
            container.addView( view );
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView( (View) object );
        }

        @Override
        public float getPageWidth(int position) {
            return 1.0f / ONE_PAGE_ITEM_COUNT;
        }


        public class OnePageThreeItemTransformer implements ViewPager.PageTransformer {

            @Override
            public void transformPage(View page, float position) {
                //这里的position是指每一个Item相对于Page所占的比例，当前页的第一个Item是0，左边的依次减去Item的宽度，右边的依次加上Item的宽度
                float centerPosition = 2.0f / ONE_PAGE_ITEM_COUNT;
//                Log.d( "Debug", "当前的position为" + position + "当前的" + centerPosition );
                if (position <= 0) {
                    page.setScaleX( 1 );
                    page.setScaleY( 1 );
                } else if (position <= centerPosition) {
                    page.setScaleX( 1 + (itemMaxScale - 1) * position / centerPosition );
                    page.setScaleY( 1 + (itemMaxScale - 1) * position / centerPosition );
//                    Log.d( "Debug", "position <= centerPosition-----------------------" );
                } else if (position <= centerPosition * 2) {
                    page.setScaleX( 1 + (itemMaxScale - 1) * (2 * centerPosition - position) / centerPosition );
                    page.setScaleY( 1 + (itemMaxScale - 1) * (2 * centerPosition - position) / centerPosition );
//                    Log.d( "Debug", "position <= centerPosition2222-----------------------" );
                }
                else {
                    page.setScaleX( 1 );
                    page.setScaleY( 1 );
                }
            }
        }

        /**
         * 配置ViewPager一些其他的属性
         */
        public void configViewPager() {
            viewPager.setOffscreenPageLimit( ONE_PAGE_ITEM_COUNT );
            viewPager.setPageTransformer( true, new OnePageThreeItemTransformer() );
            viewPager.setCurrentItem( ONE_PAGE_ITEM_COUNT );
        }
    }


}
