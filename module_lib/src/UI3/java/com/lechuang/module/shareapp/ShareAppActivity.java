package java.com.lechuang.module.shareapp;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.ShareAppBean;
import com.common.app.utils.*;
import com.common.app.view.coverflowviewpager.CoverFlowViewPager;
import com.common.app.view.coverflowviewpager.OnPageSelectListener;
import com.lechuang.module.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.RedPackageStateBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SHARE_APP)
public class ShareAppActivity extends BaseActivity implements View.OnClickListener {

    public static boolean startShow = true;
    private CoverFlowViewPager mBannerShareApp;
    private int mCurrenPostion = 0;//当前选中的图片下标
    private Map<Integer, Bitmap> mCurrenBitmapMap;//当前选中的图片
    private TextView mTvCommonRight, mTvShareAppRedStateTxt, mTvShareAppRedTip;
    private FrameLayout mFlShareAppRedState;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_app;
    }

    @Override
    protected void findViews() {


        ((TextView) $( R.id.iv_common_title )).setText( "分享" );
        mTvCommonRight = $( R.id.tv_common_right );
        mFlShareAppRedState = $( R.id.fl_share_app_red_state );
        mTvCommonRight.setText( "注册红包" );
        mTvCommonRight.setTextColor( getResources().getColor( R.color.c_2F2F2F ) );
        mTvCommonRight.setVisibility( View.INVISIBLE );
        mFlShareAppRedState.setVisibility( View.GONE );

        mBannerShareApp = $( R.id.banner_share_app );
        mTvShareAppRedStateTxt = $( R.id.tv_share_app_red_state_txt );
        mTvShareAppRedTip = $( R.id.tv_share_app_tip );
        mTvShareAppRedStateTxt.setOnClickListener( this );
        mTvShareAppRedTip.setOnClickListener( this );
        mTvCommonRight.setOnClickListener( this );
        $( R.id.iv_common_back ).setOnClickListener( this );
        $( R.id.share_weixin ).setOnClickListener( this );
        $( R.id.share_friends ).setOnClickListener( this );
        $( R.id.share_qq ).setOnClickListener( this );
        $( R.id.share_qq_kongjian ).setOnClickListener( this );
        $( R.id.save_local ).setOnClickListener( this );
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void getData() {
        getRedpackageState();

        AndPermission.with( this )
                .permission( Permission.Group.STORAGE )
                .onGranted( new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //这里需要读写的权限
                        getBannerDataNew();
                    }
                } )
                .onDenied( new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                    }
                } )
                .start();
    }

    private void getRedpackageState() {
        NetWork.getInstance()
                .setTag( Qurl.getActiveStatus )
                .getApiService( ModuleApi.class )
                .getActiveStatus()
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<RedPackageStateBean>( this, false ) {
                    @Override
                    public void onSuccess(RedPackageStateBean result) {
                        if (result == null) {
                            return;
                        }
                        if (result.isStatus == 0) {
                            mFlShareAppRedState.setVisibility( View.GONE );
                            mTvCommonRight.setVisibility( View.INVISIBLE );
                        } else if (result.isStatus == 1) {
                            mFlShareAppRedState.setVisibility( View.VISIBLE );
                            mTvCommonRight.setVisibility( View.VISIBLE );
                        }

                        if (result.isStatus == 1 && startShow) {
                            mFlShareAppRedState.setVisibility( View.VISIBLE );
                            startShow = false;
                        } else {
                            mFlShareAppRedState.setVisibility( View.GONE );
                        }
                    }
                } );
    }

    private void getBannerDataNew() {
        NetWork.getInstance()
                .setTag( Qurl.getShareImagesCode )
                .getApiService( ModuleApi.class )
                .getShareImagesCode()
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new RxObserver<ShareAppBean>( ShareAppActivity.this, true, true ) {

                    @Override
                    public void onSuccess(ShareAppBean result) {
                        if (result == null || result.imageList == null || result.imageList.size() <= 0) {
                            return;
                        }
                        DisplayMetrics mDisplayMetrics = new DisplayMetrics();//屏幕分辨率容器
                        getWindowManager().getDefaultDisplay().getMetrics( mDisplayMetrics );
                        int width = mDisplayMetrics.widthPixels;
                        if (width <= 720) {
                            LoadImage.AddTagBean.small = true;
                        }
                        LoadImage.AddTagBean addTagBean = new LoadImage.AddTagBean();
                        addTagBean.codeHttp = TextUtils.isEmpty( result.qrCodeLink ) ? "" : result.qrCodeLink;
                        addTagBean.codeNum = TextUtils.isEmpty( result.inviteCode ) ? "" : result.inviteCode;
                        addTagBean.xCodeNum = result.inviteCodeX;
                        addTagBean.yCodeNum = result.inviteCodeY;
                        addTagBean.xWidth = result.qrCodeSize + 5;//二维码的宽高
                        addTagBean.xBitmap = result.qrCodeX + 5;
                        addTagBean.yBitmap = result.qrCodeY + 5;
                        addTagBean.size = result.fontSize;
                        createBitmap( result.imageList, addTagBean );
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                } );
    }

    private void createBitmap(List<String> loadHttp, LoadImage.AddTagBean addTagBean) {

        LoadImage.getInstance().startLoadImages( this, true, loadHttp, addTagBean, new LoadImage.OnLoadImageLisenter() {
            @Override
            public void onSuccess(List<File> path, int failNum, boolean isCancle) {
                if (isCancle) {
                    toast( "已取消！" );
                    return;
                }
                List<File> pathCopy = new ArrayList<>();

                //非空过滤
                for (int i = 0; i < path.size(); i++) {
                    if (path.get( i ) != null) {
                        pathCopy.add( path.get( i ) );
                    }
                }


                List<String> shareFile = new ArrayList<>();
                for (int i = 0; i < pathCopy.size(); i++) {
                    shareFile.add( pathCopy.get( i ).getAbsolutePath() );
                }

                if (shareFile == null || shareFile.size() <= 0) {
                    toast( "图片路径出错！" );
                    return;
                }
                setBannerData( shareFile );
            }
        } );
    }

    private List<String> mBannderData;

    private void setBannerData(List<String> images) {
        if (mBannderData == null) {
            mBannderData = new ArrayList<>();
        }

        mBannderData.clear();
        mBannderData.addAll( images );
        mBannerShareApp.setViewList( mBannderData, R.layout.vp_item );
        mBannerShareApp.setOnPageSelectListener( new OnPageSelectListener() {
            @Override
            public void select(int position) {
                mCurrenPostion = position;
            }
        } );
    }

    private boolean savepic = true;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.share_weixin) {
            File[] shareFile = getShareFile();
            if (shareFile == null) {
                toast( "图片路径出错！" );
                return;
            }
            ShareUtils.shareToWChart( this, shareFile, "" );
        } else if (id == R.id.share_friends) {
            File[] shareFile = getShareFile();
            if (shareFile == null) {
                toast( "图片路径出错！" );
                return;
            }

            ArrayList<File> uriListUm = new ArrayList<>();
            for (File file : shareFile) {
                uriListUm.add( file );
            }
            ShareUtils.umShare( ShareAppActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e( "-----", "onStart" );

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e( "-----", "onResult" );
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e( "-----", "onError" );
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e( "-----", "onCancel" );
                }
            } );
        } else if (id == R.id.share_qq) {
            File[] shareFile = getShareFile();
            if (shareFile == null) {
                toast( "图片路径出错！" );
                return;
            }
            ShareUtils.shareToQQ( this, shareFile, "" );
        } else if (id == R.id.share_qq_kongjian) {
            File[] shareFile = getShareFile();
            if (shareFile == null) {
                toast( "图片路径出错！" );
                return;
            }
//            ShareUtils.shareToQQZ(this, shareFile, "");
            ArrayList<File> uriListUm = new ArrayList<>();
            for (File file : shareFile) {
                uriListUm.add( file );
            }
            ShareUtils.umShare( ShareAppActivity.this, SHARE_MEDIA.QZONE, uriListUm, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e( "-----", "onStart" );

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e( "-----", "onResult" );
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e( "-----", "onError" );
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e( "-----", "onCancel" );
                }
            } );
        } else if (id == R.id.save_local && savepic) {
            savepic = false;
            try {
                File[] shareFile = getShareFile();
                if (shareFile == null) {
                    toast( "图片路径出错！" );
                    return;
                }
                toast( "以保存至" + shareFile[0].getAbsolutePath() );

                String name = shareFile[0].getName();
                MediaStore.Images.Media.insertImage( this.getContentResolver(), shareFile[0].getAbsolutePath(), name, null );
                // 发送广播，通知刷新图库的显示
                this.sendBroadcast( new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse( "file://" + name ) ) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            savepic = true;

        } else if (id == R.id.tv_share_app_red_state_txt) {
            mFlShareAppRedState.setVisibility( View.GONE );
        } else if (id == R.id.tv_share_app_tip) {
            mFlShareAppRedState.setVisibility( View.GONE );
        } else if (id == R.id.tv_common_right) {//新人注册红包
            ARouter.getInstance().build( ARouters.PATH_REDPACKAGE ).navigation();
        }
    }

    private File[] getShareFile() {
        final File[][] files = {{}};

        AndPermission.with( this )
                .permission( Permission.Group.STORAGE )
                .onGranted( new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //这里需要读写的权限
                        //用来存放拍照之后的图片存储路径文件夹
                        File newFile = new File( Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH );
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }

                        File shareAppFile = new File( newFile, FileUtils.getNameFromDate() + ".png" );
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream( shareAppFile );
                            mBannerShareApp.mBitmapMap.get( mCurrenPostion ).compress( Bitmap.CompressFormat.JPEG, 100,
                                    fileOutputStream );
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            files[0] = new File[]{shareAppFile};
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } )
                .onDenied( new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                    }
                } )
                .start();


        return files[0];
    }


}
