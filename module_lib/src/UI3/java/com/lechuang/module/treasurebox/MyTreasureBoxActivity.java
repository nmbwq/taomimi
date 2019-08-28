package java.com.lechuang.module.treasurebox;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.BigWhellRunBean;
import java.com.lechuang.module.bean.BigWhellShowBean;
import java.com.lechuang.module.bean.LotRunBean;
import java.com.lechuang.module.bean.LottoyShowBean;
import java.com.lechuang.module.bean.ZeroBuyShareAppBean;
import java.com.lechuang.module.bigwhell.MyBigWhellActivity;
import java.com.lechuang.module.weeksignin.WeekSigninRuleActivity;
import java.com.lechuang.module.zerobuy.MyZeroBuyActivity;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import tech.oom.luckpan.LuckBean;
import tech.oom.luckpan.LuckItemInfo;
import tech.oom.luckpan.NewLuckView;

@Route(path = ARouters.PATH_MY_TREASURE_BOX)
public class MyTreasureBoxActivity extends BaseActivity implements View.OnClickListener{

    private TextView mTvValueNick,mTvValueInfo,mTvValueTime;
    private RecyclerView mArlValueFirst;
    private LinearLayout mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mIvPopFinish;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_treasure_box;
    }

    @Override
    protected void findViews() {
        $(R.id.iv_common_back).setOnClickListener( this );
        $(R.id.iv_value_try_hand).setOnClickListener( this );
        $(R.id.tv_find_mine_value).setOnClickListener( this );
        $(R.id.iv_value_rule).setOnClickListener( this );
        mTvValueNick = $(R.id.tv_value_nick);
        mTvValueInfo = $(R.id.tv_value_info);
        mTvValueTime = $(R.id.tv_value_time);
        mArlValueFirst = $(R.id.arv_value_first);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void getData() {
        getAllData();
        getShareImager();
    }

    private long mActivityUnique;
    private String mRule;
    private int mNumber;
    private void getAllData() {

        NetWork.getInstance ()
                .setTag(Qurl.lottoyShow)
                .getApiService(ModuleApi.class)
                .lottoyShow()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LottoyShowBean>(MyTreasureBoxActivity.this,true,true){

                    @Override
                    public void onSuccess(LottoyShowBean result) {
                        if (result == null || result.winTopShuf == null){
                            return;
                        }
                        mActivityUnique = result.activityUnique;
                        mRule = result.rule;
                        mNumber = result.number;
                        setAllData(result.winTopShuf);
                    }
                });
    }

    //请求获取邀请好友图片以及二维码
    private void getShareImager(){
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("type", 5);
        NetWork.getInstance()
                .setTag(Qurl.zeroBuyShareApp)
                .getApiService(ModuleApi.class)
                .zeroBuyShareApp(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ZeroBuyShareAppBean>(MyTreasureBoxActivity.this, true, true) {

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

    private void setAllData(LottoyShowBean.WinTopShufBean winTopShuf) {
        try {
            if (winTopShuf.winShufTop != null){
                mTvValueNick.setText(winTopShuf.winShufTop.nickName);
                mTvValueInfo.setText(winTopShuf.winShufTop.awardProduct + winTopShuf.winShufTop.winCount);
                mTvValueTime.setText(winTopShuf.winShufTop.acquiredTimeDelYearStr);
            }

            if (winTopShuf.winShufList != null && winTopShuf.winShufList.size() > 0){
                setInfoAdapter(winTopShuf.winShufList);
            }
        }catch (Exception e){

        }
    }

    private RecyclerView.Adapter<ViewHolder> mBaseDataAdapter;
    private List<LottoyShowBean.WinTopShufBean.WinShufListBean> mWinShufList;
    private void setInfoAdapter(List<LottoyShowBean.WinTopShufBean.WinShufListBean> winShufList) {
        if (mWinShufList == null){
            mWinShufList = new ArrayList<>();
        }
        mWinShufList.clear();
        mWinShufList.addAll(winShufList);

        if (mBaseDataAdapter == null){
            mBaseDataAdapter = new RecyclerView.Adapter<ViewHolder>(){

                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new ViewHolder(LayoutInflater.from(MyTreasureBoxActivity.this).inflate(R.layout.item_value_info, parent, false));
                }

                @Override
                public void onBindViewHolder(ViewHolder holder, int position) {
                    int index = position % mWinShufList.size();
                    LottoyShowBean.WinTopShufBean.WinShufListBean winShufListBean = mWinShufList.get(index);
                    holder.nickName.setText(winShufListBean.nickName);
                    holder.info.setText(winShufListBean.awardProduct + winShufListBean.winCount);
                    holder.time.setText(winShufListBean.acquiredTimeDelYearStr);
                }

                @Override
                public int getItemCount() {
                    return Integer.MAX_VALUE;
                }
            };
            mArlValueFirst.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 如果自动滑动到最后一个位置，则此处状态为SCROLL_STATE_IDLE
                        AutoScrollLayoutManager lm = (AutoScrollLayoutManager) recyclerView
                                .getLayoutManager();

                        int position = lm.findLastCompletelyVisibleItemPosition();
                        int count = lm.getItemCount();
                        if(position == count-1){
                            lm.scrollToPosition(0);
                            mArlValueFirst.smoothScrollToPosition(mBaseDataAdapter.getItemCount());
                        }
                    }
                }
            });
            mArlValueFirst.setHasFixedSize(true);
            mArlValueFirst.setNestedScrollingEnabled(false);
            AutoScrollLayoutManager autoScrollLayoutManager = new AutoScrollLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            mArlValueFirst.setLayoutManager(autoScrollLayoutManager);
            mArlValueFirst.setAdapter(mBaseDataAdapter);
            mArlValueFirst.smoothScrollToPosition(mBaseDataAdapter.getItemCount());


        }else {
            mBaseDataAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId ();
        if (id == R.id.iv_common_back) {//关闭
            finish ();
        }else if (id == R.id.iv_value_try_hand){//试试手气
            try {

                if (mNumber <= 0){
                    showSuccessPopuwind(true,"","");
                }else {
                    openValue();
                }

            }catch (Exception e){

            }

        }else if (id == R.id.tv_find_mine_value){//我的奖品
            ARouter.getInstance().build(ARouters.PATH_VALUE_BANG_DAN).withLong("mActivityUnique",mActivityUnique).navigation();
        }else if (id == R.id.iv_value_rule){
            Intent intent = new Intent(this, ValueRuleActivity.class);
            intent.putExtra("rule",mRule);
            startActivity(intent);
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN, MyTreasureBoxActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, MyTreasureBoxActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.QQ,MyTreasureBoxActivity.uriListUm , new UMShareListener() {
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
            ShareUtils.umShare(this, SHARE_MEDIA.QZONE, MyTreasureBoxActivity.uriListUm, new UMShareListener() {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nickName;
        TextView info;
        TextView time;
        public ViewHolder(View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.tv_item_value_nick);
            info = itemView.findViewById(R.id.tv_item_value_info);
            time = itemView.findViewById(R.id.tv_item_value_time);
        }

    }

    private void openValue() {

        if (mActivityUnique == 0){
            toast("获取活动信息出错");
            return;
        }

        Map<String,Object> param = new HashMap<>();
        param.put("activityUnique",mActivityUnique);
        param.put("page",0);

        NetWork.getInstance ()
                .setTag(Qurl.lotRun)
                .getApiService(ModuleApi.class)
                .lotRun(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LotRunBean>(MyTreasureBoxActivity.this,false,true){

                    @Override
                    public void onSuccess(LotRunBean result) {
                        if (result == null ){
                            return;
                        }
                        if (result.type == 1){//觅卡
                            showCardPopuwind(result.img,result.prize);
                        }else if (result.type == 2){//商品
                            showSuccessPopuwind(false,result.prize,result.wechatCare);
                        }
                    }

                    @Override
                    public void onFailed_11004(LotRunBean result) {
                        super.onFailed_11004(result);
                        showSuccessPopuwind(true,"","");
                    }
                });
    }

    private PopupWindow mPopupWindow;
    private void showSuccessPopuwind(boolean isSuccess, String info, final String wechatNum) {
        View contentView = LayoutInflater.from(this).inflate(isSuccess ? R.layout.layout_popu_value_success1 : R.layout.layout_popu_value_success2, null);

        if (isSuccess){
            contentView.findViewById(R.id.tv_popu_week_signin_join).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //去参与
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                        sharePopWindow();
                    }
                }
            });
            contentView.findViewById(R.id.iv_popu_value_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //去参与
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                }
            });
        }else {
            TextView tvPupoWeekSigninSuccessContent = contentView.findViewById(R.id.tv_popu_week_signin_success_content);
            TextView tvPupoWeekSigninSuccessCopy = contentView.findViewById(R.id.tv_popu_week_signin_wechat_num);
            tvPupoWeekSigninSuccessContent.setText(info);
            tvPupoWeekSigninSuccessCopy.setText(wechatNum);
            contentView.findViewById(R.id.ll_popu_week_signin_success_copy_wechat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                    try {
                        if (!TextUtils.isEmpty(wechatNum.trim())){
                            // 获取系统剪贴板
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                            ClipData clipData = ClipData.newPlainText("app_kefu", TextUtils.isEmpty(wechatNum) ? "" : wechatNum);

                            // 把数据集设置（复制）到剪贴板
                            clipboard.setPrimaryClip(clipData);

                            getWechatApi();

                        }else {
                            toast("复制失败！");
                        }
                    }catch (Exception e){

                    }
                }
            });
        }

        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 跳转到微信
     */
    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast( "检查到您手机没有安装微信，请安装后使用该功能" );
        }
    }

    private void showCardPopuwind(String imgUrl, String info) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popu_value_success3,null);


        TextView viewById = contentView.findViewById(R.id.tv_popu_value_jiangpin);
        viewById.setText(info);
        ImageView ivPopuValueCard = contentView.findViewById(R.id.iv_popu_value_card);
        Glide.with(BaseApplication.getApplication()).load(imgUrl).into(ivPopuValueCard);
        contentView.findViewById(R.id.tv_popu_week_signin_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去参与
                if (mPopupWindow != null){
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        contentView.findViewById(R.id.iv_popu_value_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去参与
                if (mPopupWindow != null){
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });

        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
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

}
