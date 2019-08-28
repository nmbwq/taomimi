package java.com.lechuang.module.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.utils.FileUtils;
import com.common.app.utils.Utils;
import com.github.chrisbanes.photoview.PhotoView;
import com.lechuang.module.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.productinfo.PreShareActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(path = ARouters.PATH_PREIMG)
public class PreviewImgActivity extends BaseActivity {

    private HackyViewPager mVpPreviewImage;
    @Autowired
    public boolean withDelete;//是展示删除按钮
    @Autowired
    public boolean showLoad;//是展示下载按钮

    @Override
    public int getLayoutId() {
        return R.layout.activity_preview_img;
    }

    @Override
    protected void findViews() {
        mVpPreviewImage = $(R.id.vp_preview_img);

    }

    @Override
    public void initView() {
        ARouter.getInstance().inject(this);

    }

    @Override
    public void initData() {
        mVpPreviewImage.setAdapter(new SamplePagerAdapter(getIntent().getStringArrayListExtra("urls"),this));
        mVpPreviewImage.setOffscreenPageLimit(1);
        mVpPreviewImage.setCurrentItem(getIntent().getIntExtra("position", 0), false);
    }

    @Override
    protected void getData() {

    }

    class SamplePagerAdapter extends PagerAdapter {

        private List<String> urls;
        //        private List<Bitmap> mLoadBitMap;
        private Activity mActivity;

        public SamplePagerAdapter(List<String> urls,Activity activity) {
            this.urls = urls;
            mActivity = activity;

//            mLoadBitMap = new ArrayList<>();
//            mLoadBitMap.clear();
//            for (int i = 0; i < urls.size();i++){
//                mLoadBitMap.add(null);
//            }
        }

        @Override
        public int getCount() {
            int size = urls.size();
            return size;
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_pre_image, null);
            final PhotoView photoView = inflate.findViewById(R.id.photoview_pre_img);
            TextView tvDeleteImg = inflate.findViewById(R.id.tv_delete_img);
            ImageView ivPreImgLoad = inflate.findViewById(R.id.iv_preimg_load);
            ivPreImgLoad.setVisibility(showLoad ? View.VISIBLE : View.GONE);

            if (withDelete) {
                tvDeleteImg.setVisibility(View.VISIBLE);
            } else {
                tvDeleteImg.setVisibility(View.GONE);
            }

            /*
            *
            * new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    photoView.setImageDrawable(resource);
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                }
            }
            * */
            final Bitmap[] bitmap = {null};
            Glide.with(BaseApplication.getApplication()).load(urls.get(position)).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    photoView.setImageBitmap(resource);
//                    mLoadBitMap.add(position,resource);
                    bitmap[0] = resource;
                }
            });
            inflate.findViewById(R.id.iv_preimg_load).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Bitmap bitmap = mLoadBitMap.get(position);
                    if (bitmap[0] != null){
                        AndPermission.with(mActivity)
                                .permission(Permission.Group.STORAGE)
                                .onGranted(new Action() {
                                    @Override
                                    public void onAction(List<String> permissions) {

                                        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                                        if (!newFile.exists()) {
                                            newFile.mkdir();
                                        }
                                        File kefu = new File(newFile, FileUtils.getNameFromDate() + ".png");
                                        FileOutputStream outputStream = null;     //构建输出流
                                        try {
                                            outputStream = new FileOutputStream(kefu);
                                            bitmap[0].compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                            try {
                                                String name = kefu.getName();
                                                MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), kefu.getAbsolutePath(),name , null);
                                                // 发送广播，通知刷新图库的显示
                                                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + name)));
                                                toast("图片保存至" + kefu.getAbsolutePath());
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }

                                            outputStream.flush();
                                            outputStream.close();
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
                    }else {
                        toast("图片加载失败！");
                    }


                }
            });
            inflate.findViewById(R.id.tv_delete_img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    setResult(200, intent);
                    finish();
                }
            });


            // Now just add PhotoView to ViewPager and return it
            container.addView(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
