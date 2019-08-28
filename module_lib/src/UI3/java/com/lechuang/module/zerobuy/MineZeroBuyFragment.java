package java.com.lechuang.module.zerobuy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.androidkun.xtablayout.XTabLayout;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.LazyBaseFragment;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.FileUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.lechuang.module.R;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.ShowInMyCardBean;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_Mine_ZERO_BUY)
public class MineZeroBuyFragment extends LazyBaseFragment implements View.OnClickListener {
    private PopupWindow mPopupWindow;
    private TextView mTvCommonTitle, mTvYellowCard, mTvMoreGetCard;
    private XTabLayout mXTabMineTry;
    private ViewPager mVpMineTry;
    private String[] mTabs = new String[]{"全部", "参与中", "待开奖", "已开奖"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private LinearLayout mLlPopweixin,mLlPoppengyou,mLlPophaoyou,mLlPopkongjian;
    private ImageView mIvPopFinish;
    private Bitmap  shareBitmap;//设置分享图片

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_minezerobuy;
    }

    @Override
    protected void findViews() {

        $(R.id.iv_common_back).setOnClickListener(this);
        mTvCommonTitle = $(R.id.iv_common_title);
        mTvCommonTitle.setText("我的抢购");
        mTvCommonTitle.setTextColor(getResources().getColor(R.color.c_161616));
        mXTabMineTry = $(R.id.xtab_mine_try);
        mVpMineTry = $(R.id.vp_mine_try);
        mTvYellowCard = $(R.id.tv_yellow_card);
        mTvMoreGetCard = $(R.id.tv_more_get_card);
        mTvMoreGetCard.setOnClickListener(this);

    }

    @Override
    protected void initView() {
        shareBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bg_shareimg);
        for (int i = 1; i <= 4; i++) {
            Fragment tryContentFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_Mine_ZERO_BUY_CONTENT).navigation();
            Bundle bundle = new Bundle();
            bundle.putSerializable("obtype", i);
            tryContentFragment.setArguments(bundle);
            mFragments.add(tryContentFragment);
        }

        for (int i = 0; i < mTabs.length; i++) {
            mXTabMineTry.addTab(mXTabMineTry.newTab().setText(mTabs[i]));
        }

        mVpMineTry.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabs[position];
            }
        });

        mVpMineTry.setOffscreenPageLimit(mTabs.length);
        mXTabMineTry.setupWithViewPager(mVpMineTry);
    }

    @Override
    protected void getData() {
        getProduct();
    }

    private void getProduct() {

        NetWork.getInstance()
                .setTag(Qurl.zeroBuyHaveCardsNums)
                .getApiService(ModuleApi.class)
                .zeroBuyHaveCardsNums()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ShowInMyCardBean>(getActivity(), false, false) {
                    @Override
                    public void onSuccess(ShowInMyCardBean result) {
                        if (result == null || TextUtils.isEmpty(result.number)) {
                            mTvYellowCard.setText("绿卡剩余0张");
                            return;
                        }
                        mTvYellowCard.setText("绿卡剩余" + result.number + "张");
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        mTvYellowCard.setText("绿卡剩余0张");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mTvYellowCard.setText("绿卡剩余0张");
                    }
                });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_common_back) {
            getActivity().finish();
//            测试数据
//            JoinSuccessBean joinSuccessBean = new JoinSuccessBean();
//            joinSuccessBean.uaCodeNum = 8;
//            joinSuccessBean.num = 5;
//            joinSuccessBean.keyNumSt = "344";
//            joinSuccessBean.proList = new ArrayList<>();
//
//            for (int i = 0; i < 10; i++) {
//                JoinSuccessBean.ProListBean proListBean = new JoinSuccessBean.ProListBean();
//                proListBean.showImg = "";
//                proListBean.countDown = 1413241;
//                proListBean.id = "askdf";
//                proListBean.name = "nameasknjfkjkasdf";
//                proListBean.needNum = 150;
//                proListBean.realNum = 50;
//                joinSuccessBean.proList.add(proListBean);
//            }
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("joinSuccess", joinSuccessBean);
//            ARouter.getInstance().build(ARouters.PATH_JOIN_SUCCESS_A).with(bundle).navigation();

        } else if (id == R.id.tv_more_get_card) {//获取更多黄卡，跳转到分享界面
            //分享弹框
            View contentView = LayoutInflater.from ( getContext() ).inflate ( R.layout.popupwind_share_goods, null );
            mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
            mLlPopweixin = (LinearLayout) contentView.findViewById ( R.id.ll_shareweixin );
            mLlPoppengyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharepengyou );
            mLlPophaoyou = (LinearLayout) contentView.findViewById ( R.id.ll_sharehaoyou );
            mLlPopkongjian = (LinearLayout) contentView.findViewById ( R.id.ll_sharekongjian );
            mIvPopFinish = (ImageView) contentView.findViewById ( R.id.iv_finish );
            mLlPopweixin.setOnClickListener( this );
            mLlPoppengyou.setOnClickListener( this );
            mLlPophaoyou.setOnClickListener( this );
            mLlPopkongjian.setOnClickListener( this );
            mIvPopFinish.setOnClickListener( this );

            mPopupWindow.setContentView ( contentView );
            mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
        }else if (id==R.id.iv_finish){
            mPopupWindow.dismiss();
        }else if (id==R.id.ll_shareweixin){
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.WEIXIN, MyZeroBuyActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.QQ, MyZeroBuyActivity.uriListUm, new UMShareListener() {
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
            ShareUtils.umShare(getActivity(), SHARE_MEDIA.QZONE, MyZeroBuyActivity.uriListUm, new UMShareListener() {
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

    private ArrayList<File> getArrayList(){
        ArrayList<File> uriListUm = new ArrayList<>();
        File[] shareFile = getShareFile();
        for (File file : shareFile) {
            uriListUm.add(file);
        }
        return uriListUm;
    }
    private File[] getShareFile() {
        final File[][] files = {{}};

        AndPermission.with(this)
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //这里需要读写的权限
                        //用来存放拍照之后的图片存储路径文件夹
                        File newFile = new File( Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }

                        File shareAppFile = new File(newFile, FileUtils.getNameFromDate() + ".png");
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(shareAppFile);
                            shareBitmap.compress( Bitmap.CompressFormat.JPEG, 100,
                                    fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            files[0] = new File[]{shareAppFile};
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                    }
                })
                .start();


        return files[0];
    }
}
