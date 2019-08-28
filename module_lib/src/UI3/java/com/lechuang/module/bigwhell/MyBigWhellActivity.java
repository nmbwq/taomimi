package java.com.lechuang.module.bigwhell;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LoadImage;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.Utils;
import com.common.app.utils.ZeroBuyLoadImage;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sunfusheng.marqueeview.MarqueeView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.BigWhellRunBean;
import java.com.lechuang.module.bean.BigWhellShowBean;
import java.com.lechuang.module.bean.ZeroBuyShareAppBean;
import java.com.lechuang.module.zerobuy.MyZeroBuyActivity;
import java.com.lechuang.module.zerobuy.ZeroBuyDetailsActivity;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import tech.oom.luckpan.LuckBean;
import tech.oom.luckpan.LuckItemInfo;
import tech.oom.luckpan.NewLuckView;

@Route(path = ARouters.PATH_MY_BIG_WHELL)
public class MyBigWhellActivity extends BaseActivity implements View.OnClickListener, OnRefreshLoadMoreListener {
    private NewLuckView mBigWhell;
    private PopupWindow mPopupWindow;
    private RelativeLayout mTiShi,mPopBtn;
    private LinearLayout mLlFriend,mTvPopJingGao,mLlFenxiang,mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private MarqueeView mMvTiShi;
    private TextView mPopTitle,mPopWeixin,mPopKnow,mPopFriend,mTvRedCard,mTvBigWhellLook;
    private ImageView mPopTupian,mPopFinish,mIvPopFinish,mTvGuiZe,mTitleBack;
    private View mVbtn;
    private SmartRefreshLayout mSmartEarning;
    private ClassicsHeader mSmartEarningHeader;
    private String activityUnique=null;
    private String rule=null;

    private int type = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_bigwhell;
    }

    @Override
    protected void findViews() {
        mSmartEarning = $ ( R.id.smart_earnings );
        mSmartEarningHeader = $ ( R.id.smart_earnings_header );
        mBigWhell = $ ( R.id.nlv_big_whell );
        mTiShi = $ ( R.id.ll_tishi );
        mMvTiShi = $ ( R.id.tv_tishi_all );
        mVbtn = $ ( R.id.v_btn );
        mTvRedCard = $ ( R.id.tv_redcard_num );
        mTvGuiZe = $ ( R.id.tv_bigwhell_guize );
        mTitleBack = $ ( R.id.iv_common_back );
        mTvBigWhellLook = $ ( R.id.tv_bigwhell_look );
        $(R.id.iv_common_back).setOnClickListener( this );
        $(R.id.tv_bigwhell_look).setOnClickListener( this );
//        mRlYesterdayEarnings.setOnClickListener ( this );
//        ((TextView)$ ( R.id.rl_myearnings_orderdetail )).setOnClickListener( this );
    }

    @Override
    protected void initView() {
        mSmartEarning.setOnRefreshLoadMoreListener ( this );
        mSmartEarning.setEnableLoadMore ( false );
    }

    @Override
    protected void getData() {
//smart设置属性，设置自动刷新，调用刷新方法
        mSmartEarning.autoRefresh ( 100 );
        getShareImager();
    }

    private void getAllData() {

        NetWork.getInstance ()
                .setTag ( Qurl.dialShow )
                .getApiService ( ModuleApi.class )
                .getDialShow (  )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<BigWhellShowBean> ( MyBigWhellActivity.this, true, true ) {

                    @Override
                    public void onSuccess(final BigWhellShowBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //设置滚动条
                        setMarqueeView(result.shuffling);
                        mTvRedCard.setText( ""+result.number );
                        activityUnique=result.activityUnique;
                        setBigWhellLuckDraw(result.list,result.number);
                        if (result.rule!=null){
                            rule=result.rule;
                            mTvGuiZe.setOnClickListener( new OnClickEvent() {
                                @Override
                                public void singleClick(View v) {
                                    ARouter.getInstance().build(ARouters.PATH_BIG_WHELL_RULE).withString( "rule",result.rule ).navigation();
                                }
                            } );
                        }
//                        setBigWhell(result.list);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                    }

                    @Override
                    public void onFailed_11004(BigWhellShowBean moreInfo) {
                        super.onFailed_11004( moreInfo );
                        //弹出失败框
                        showPopupWindowLoser();
                    }
                } );
    }
    private void getAllDataLuckDraw() {
        Map<String, Object> allParam = new HashMap<> ();
        allParam.put ( "activityUnique",activityUnique );

        NetWork.getInstance ()
                .setTag ( Qurl.dialRun )
                .getApiService ( ModuleApi.class )
                .getDialRun ( allParam )
                .subscribeOn ( Schedulers.io () )
                .observeOn ( AndroidSchedulers.mainThread () )
                .subscribe ( new RxObserver<BigWhellRunBean> ( MyBigWhellActivity.this, true, false ) {

                    @Override
                    public void onSuccess(BigWhellRunBean result) {
                        if (result == null) {
                            setRefreshLoadMoreState ( true, true );
                            //模拟网络请求获取抽奖结果，然后设置选中项的index值
                            mBigWhell.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBigWhell.setStop( 0);
                                }
                            }, 1);
                            toast( "请刷新重试" );
                            return;
                        }
                        setRefreshLoadMoreState ( true, false );
                        //设置滚动条
//                        setMarqueeView(result.shuffling);
                        //转盘数据
//                        for (int i =0;i<result.list.size();i++){
//                            LogUtils.w( "tag1","name="+result.list.get( i ).name );
//                            LogUtils.w( "tag1","name="+result.list.get( i ).img );
//                        }
                        mTvRedCard.setText( ""+result.number );
                        mLuckNumber=getNum( result.list );
                        mLuckName=result.prize;
                        mLuckPicture=result.img;
                        mLuckWeixin=result.wechatCare;
                        //大转盘结果
                        setBigWhellResult(result);
                        //抽奖并刷新大转盘
//                        setBigWhellShow(result);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed ( errorCode, moreInfo );
                        setRefreshLoadMoreState ( false, false );
                        mBigWhell.setEnable(true);
                        //模拟网络请求获取抽奖结果，然后设置选中项的index值
                        mBigWhell.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBigWhell.setStop( 0);
                            }
                        }, 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError ( e );
                        setRefreshLoadMoreState ( false, false );
                        mBigWhell.setEnable(true);
                        //模拟网络请求获取抽奖结果，然后设置选中项的index值
                        mBigWhell.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBigWhell.setStop( 0);
                            }
                        }, 1);
                    }
                    @Override
                    public void onFailed_11004(BigWhellRunBean moreInfo) {
                        super.onFailed_11004( moreInfo );
                        mBigWhell.setEnable(true);
                        //模拟网络请求获取抽奖结果，然后设置选中项的index值
                        mBigWhell.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBigWhell.setStop( 0);
                            }
                        }, 1);
                        //弹出失败框
                        showPopupWindowLoser();
                    }
                } );
    }

    //请求获取邀请好友图片以及二维码
    private void getShareImager(){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("type", 4);
        NetWork.getInstance()
                .setTag(Qurl.zeroBuyShareApp)
                .getApiService(ModuleApi.class)
                .zeroBuyShareApp(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZeroBuyShareAppBean>(MyBigWhellActivity.this, true, true) {

                    @Override
                    public void onSuccess(ZeroBuyShareAppBean result) {
                        if (result == null ) {
                            return;
                        }
                        DisplayMetrics mDisplayMetrics = new DisplayMetrics();//屏幕分辨率容器
                        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
                        int width = mDisplayMetrics.widthPixels;
                        if (width <= 720) {
                            LoadImage.AddTagBean.small = true;
                        }
                        ZeroBuyLoadImage.AddTagBean addTagBean = new ZeroBuyLoadImage.AddTagBean();
                        addTagBean.codeHttp = TextUtils.isEmpty(result.qrCodeLink) ? "" : result.qrCodeLink;
                        addTagBean.codeNum = TextUtils.isEmpty(result.inviteCode) ? "" : result.inviteCode;
                        addTagBean.xCodeNum = 0;
                        addTagBean.yCodeNum = R.dimen.dp_60;
                        addTagBean.xWidth = 180 + 5;//二维码的宽高
                        addTagBean.xBitmap = 0;
                        addTagBean.yBitmap = R.dimen.dp_35;
                        addTagBean.size = 12;
                        List<String> shareImage = new ArrayList<>();
                        shareImage.add( result.shareImage );
                        createBitmap(shareImage, addTagBean);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }
    public static ArrayList<File> uriListUm = new ArrayList<>();
    private void createBitmap(List<String> loadHttp, ZeroBuyLoadImage.AddTagBean addTagBean) {

        ZeroBuyLoadImage.getInstance().startLoadImages(this, true, loadHttp, addTagBean, new ZeroBuyLoadImage.OnLoadImageLisenter() {
            @Override
            public void onSuccess(List<File> path, int failNum, boolean isCancle) {
                if (isCancle) {
                    toast("已取消！");
                    return;
                }
                List<File> pathCopy = new ArrayList<>();

                //非空过滤
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i) != null) {
                        pathCopy.add(path.get(i));
                    }
                }


                List<String> shareFile = new ArrayList<>();
                for (int i = 0; i < pathCopy.size(); i++) {
                    shareFile.add(pathCopy.get(i).getAbsolutePath());
                }

                if (shareFile == null || shareFile.size() <= 0) {
                    toast("图片路径出错！");
                    return;
                }

                File shareFiles=new File( shareFile.get( 0 ) );
                uriListUm.add(shareFiles);
//                getArrayList(shareFile);
            }
        });
    }
    //跑马灯
    private List<CharSequence> mMarqueeData = new ArrayList<>();
    public void setMarqueeView(List<String> str){
        if (str==null||str.size()==0){
            mTiShi.setVisibility( View.GONE );
            return;
        }
        mTiShi.setVisibility( View.VISIBLE );
        if (mMarqueeData == null) {
            mMarqueeData = new ArrayList<>();
        }
        mMarqueeData.clear();
        for (int i = 0; i < str.size(); i++) {
            String nickName = str.get( i );
            //后台处理
//            if (nickName.length() >= 4) {
//                nickName = nickName.substring(0, 1) + "**" + nickName.substring(nickName.length() - 2, nickName.length() - 1);
//            }

            SpannableString ss1 = new SpannableString(nickName);
//            ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_F13B3A)), 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mMarqueeData.add(ss1);
        }
        mMvTiShi.startWithList(mMarqueeData, R.anim.anim_bottom_in, R.anim.anim_top_out);
        mMvTiShi.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                //上下滑动的红包
//                toast(position + textView.getText().toString());

            }
        });
        mMvTiShi.startFlipping();
    }
    //转盘数据
    public void setBigWhellLuckDraw(final List<BigWhellShowBean.ListBean> list, final int number){
        mBigWhell.setIndicatorResourceId(R.drawable.ic_bigwhell_node);
        final ArrayList<LuckItemInfo> items = new ArrayList<>();
        final ArrayList<Bitmap> bitmaps = new ArrayList<>();
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i=0;i<list.size();i++){
                            LuckItemInfo luckItem = new LuckItemInfo();
                            luckItem.prize_name = list.get( i ).name;
                            Bitmap bitmap;
                            URL url = new URL(list.get( i ).img);
                            URLConnection conn = url.openConnection();
                            conn.connect();
                            InputStream in=conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(in);
                            bitmaps.add(bitmap);
                            items.add(luckItem);

                        }
//                        runOnUi(bitmaps,items);
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                setBigWhell(bitmaps,items,number);
                            }
                        } );



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } ).start();
    }
    //获取中奖的位置以及信息
    private int getNum(List<BigWhellShowBean.ListBean> list){
        int num=0;
        for (int i=0;i<list.size();i++){
            if (list.get( i ).win==1){
                num=i;
            }
        }
        return num;
    }

    private void setBigWhellResult(final BigWhellRunBean result){
        mBigWhell.setEnable(true);
        //模拟网络请求获取抽奖结果，然后设置选中项的index值
        mBigWhell.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBigWhell.setStop(getNum( result.list ));
                mBigWhell.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        if (result.type==2){//设置中奖类型true为实体，false为虚拟
                            showPopupWindow(result.prize,result.wechatCare);
                        }else {
                            showPopupWindowCard(result.prize,result.img);
                        }
                        getOther(result.number);
//                                showPopupWindowCard("haha","haha");
                    }
                },700 );
            }
        }, 2000);
        //放开按钮  mTvBigWhellLook mTitleBack mTvGuiZe
        mTvBigWhellLook.setOnClickListener( this );
        mTitleBack.setOnClickListener( this );
        mTvGuiZe.setOnClickListener( this );
    }

    private int mLuckNumber;//中奖位置
    private String mLuckName;//奖品
    private String mLuckPicture;//图片
    private String mLuckWeixin;//客服

    //抽奖数据
    public void setBigWhellShow(final BigWhellRunBean result){
        mBigWhell.setIndicatorResourceId(R.drawable.ic_bigwhell_node);
        final ArrayList<LuckItemInfo> items = new ArrayList<>();
        final ArrayList<Bitmap> bitmaps = new ArrayList<>();
//        final int num=getNum(list);
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i=0;i<result.list.size();i++){
                        LuckItemInfo luckItem = new LuckItemInfo();
                        luckItem.prize_name = result.list.get( i ).name;
                        Bitmap bitmap;
                        URL url = new URL(result.list.get( i ).img);
                        URLConnection conn = url.openConnection();
                        conn.connect();
                        InputStream in=conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(in);
                        bitmaps.add(bitmap);
                        items.add(luckItem);

                    }
//                        runOnUi(bitmaps,items);
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            runOnUi(bitmaps,items,getNum( result.list ),result);
                        }
                    } );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } ).start();
    }



    private void setBigWhell(ArrayList<Bitmap> bitmaps,ArrayList<LuckItemInfo> items,int number){
        LuckBean luck = new LuckBean();
        luck.details = items;
        //        load数据
        mBigWhell.loadData(luck, bitmaps);
        getOther(number);

    }
    private void getOther(int number){
        if (number==0){
            mBigWhell.setEnable(false);
            mVbtn.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    //没有红卡提示
                    showPopupWindowLoser();
//                    getAllDataLuckDraw();
                }
            } );
        }else {
            mBigWhell.setEnable(true);
            mBigWhell.setLuckViewListener(new NewLuckView.LuckViewListener() {
                @Override
                public void onStart() {
                    if (activityUnique!=null){
                        //添加取消按钮操作
                        mTvBigWhellLook.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {

                            }
                        } );
                        mTitleBack.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {

                            }
                        } );
                        mTvGuiZe.setOnClickListener( new OnClickEvent() {
                            @Override
                            public void singleClick(View v) {

                            }
                        } );
                        getAllDataLuckDraw();
                    }
                }
                @Override
                public void onStop(int index) {

                }
            });
        }
    }

    private void runOnUi(ArrayList<Bitmap> bitmaps, ArrayList<LuckItemInfo> items, final int num, final BigWhellRunBean result){
        LuckBean luck = new LuckBean();
        luck.details = items;
        //        load数据
        //屏蔽掉加载，还用原来的数据
        mBigWhell.loadData(luck, bitmaps);
        mBigWhell.setEnable(true);
        //模拟网络请求获取抽奖结果，然后设置选中项的index值
        mBigWhell.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBigWhell.setStop(mLuckNumber);
                mBigWhell.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        if (result.type==2){//设置中奖类型true为实体，false为虚拟
                            showPopupWindow(result.prize,result.wechatCare);
                        }else {
                            showPopupWindowCard(result.prize,result.img);
                        }
//                                showPopupWindowCard("haha","haha");
                    }
                },700 );
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id == R.id.iv_common_back) {//关闭
            finish ();
        } else if (id == R.id.tv_bigwhell_guize) {//活动规则
            if (rule!=null){
                ARouter.getInstance().build(ARouters.PATH_BIG_WHELL_RULE).withString( "rule",rule ).navigation();
            }
        } else if (id == R.id.tv_bigwhell_look) {//查看我抽到的奖品
            ARouter.getInstance().build(ARouters.PATH_BIG_WHELL_RECORD).navigation();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN, MyBigWhellActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, MyBigWhellActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.QQ,MyBigWhellActivity.uriListUm , new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.QZONE, MyBigWhellActivity.uriListUm, new UMShareListener() {
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


    //弹出中奖（物品）提示
    private void showPopupWindow(String title, final String content) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mybigwhell_zhongjiang, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        mPopTitle = (TextView) contentView.findViewById ( R.id.tv_title );
        mPopBtn = (RelativeLayout) contentView.findViewById ( R.id.rl_bigwhell_btn );
        mPopWeixin = (TextView) contentView.findViewById ( R.id.tv_bigwhell_weixin );
        mPopTitle.setText( title );
        mPopWeixin.setText( content );
        mPopBtn.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss ();
                ClipData clipData = ClipData.newPlainText("app_inviteCode", content);
                ((ClipboardManager) MyBigWhellActivity.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
                Utils.toast("复制成功");
                getWechatApi();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
    //弹出中奖（觅卡）提示
    private void showPopupWindowCard(String title, String picture) {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_mybigwhell_card, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        mPopTitle = (TextView) contentView.findViewById ( R.id.tv_popwindow_card );
        mPopBtn = (RelativeLayout) contentView.findViewById ( R.id.rl_bigwhell_btn );
        mPopTupian = (ImageView) contentView.findViewById ( R.id.iv_popwindow_tupian );
        mPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
        mPopTitle.setText( title );
        Glide.with( BaseApplication.getApplication()).load(picture).centerCrop().into(mPopTupian);
        mPopKnow.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
    //弹出数量不足提示
    private void showPopupWindowLoser() {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popwindow_bigwhell_loser, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        //mPopFriend,mPopFinish
        mPopFriend = (TextView) contentView.findViewById ( R.id.tv_friend );
        mPopFinish = (ImageView) contentView.findViewById ( R.id.iv_finish );
        mPopFriend.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
                sharePopWindow();
            }
        } );
        mPopFinish.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
    //分享弹框
    private void sharePopWindow(){
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_share_goods, null );
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
        mIvPopFinish.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                mPopupWindow.dismiss();
            }
        } );

        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }
    /**
     * 跳转到微信
     */
    private void getWechatApi(){
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            toast( "检查到您手机没有安装微信，请安装后使用该功能" );
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mSmartEarning.finishLoadMore ();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        activityUnique=null;
        getAllData ();
    }

    /**
     * @param state      刷新加载的状态，
     * @param noMoreData 加载没有更多的数据
     */
    private void setRefreshLoadMoreState(boolean state, boolean noMoreData) {

        mSmartEarning.finishRefresh ( state );

    }
}
