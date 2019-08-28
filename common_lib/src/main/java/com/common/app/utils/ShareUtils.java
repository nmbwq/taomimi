package com.common.app.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.common.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: LGH
 * @since: 2018/3/29
 * @describe:
 */

public class ShareUtils {


    public static final String QQPACKAGENAME = "com.tencent.mobileqq";//QQ包名
    public static final String WXPACKAGENAME = "com.tencent.mm";//微信包名
    public static final String QQZPACKAGENAME = "com.qzone";//QQ空间包名
    public static final String SINAPACKAGENAME = "com.sina.weibo";//新浪包名
    public static final String WX = "com.tencent.mm.ui.tools.ShareImgUI";//分享到微信好友
    public static final String WXF = "com.tencent.mm.ui.tools.ShareToTimeLineUI";//分享到微信朋友圈
    public static final String QQ = "com.tencent.mobileqq.activity.JumpActivity";//分享到QQ好友
    public static final String QQZ = "com.qzonex.module.operation.ui.QZonePublishMoodActivity";//分享到QQ空间
    public static final String SINA = "com.sina.weibo.composerinde.ComposerDispatchActivity";//分享到新浪微博


    /**
     * @param context
     * @param files   图片路径
     * @param comp    分享的类型
     */
    private static void shareSingleOrMoreImage(Context context, ComponentName comp, String packName, String imgUrl, File... files) {

        if (!isAvilible(context, packName)) {
            Utils.toast("请安装要分享渠道APP！");
            return;
        }

        ArrayList<Uri> arrayImageUris = new ArrayList<>();
        Intent shareIntent = new Intent();
        shareIntent.setComponent(comp);

        if (imgUrl != null && !imgUrl.equalsIgnoreCase("")) {

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, imgUrl);
            context.startActivity(Intent.createChooser(shareIntent, ""));

        } else if (files.length > 0) {
            for (File pic : files) {
                /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    arrayImageUris.add(Uri.fromFile(pic));
                } else {
                    //修复微信在7.0崩溃的问题
                    Uri uri = null;
                    try {
                        uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), pic.getAbsolutePath(), pic.getName(), null));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    arrayImageUris.add(uri);
                }*/
                //修复微信在7.0崩溃的问题
                Uri uri = null;
                try {
                    uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), pic.getAbsolutePath(), pic.getName(), null));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                arrayImageUris.add(uri);
            }

            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayImageUris);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, arrayImageUris);
//            context.startActivity(Intent.createChooser(shareIntent, "分享到"));
            shareIntent.setType("image/*");
            context.startActivity(shareIntent);
        }


    }

    /**
     * @param context
     * @param files   图片路径
     * @param comp    分享的类型
     */
    private static void shareToWchartFs(Context context, ComponentName comp, String packName, String imgUrl, File... files) {

        if (!isAvilible(context, packName)) {
            Utils.toast("请安装要分享渠道APP！");
            return;
        }

        ArrayList<Uri> arrayImageUris = new ArrayList<>();
        Intent shareIntent = new Intent();
        shareIntent.setComponent(comp);

        for (File pic : files) {
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                arrayImageUris.add(Uri.fromFile(pic));
            } else {
                //修复微信在7.0崩溃的问题
                Uri uri = null;
                try {
                    uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), pic.getAbsolutePath(), pic.getName(), null));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                arrayImageUris.add(uri);
            }*/
            Uri uri = null;
            try {
                uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), pic.getAbsolutePath(), pic.getName(), null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            arrayImageUris.add(uri);
        }

        if (imgUrl != null && !imgUrl.equalsIgnoreCase("")) {
            shareIntent.putExtra("Kdescription", imgUrl);
        }
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("image/*");
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayImageUris);
        shareIntent.putExtra(Intent.EXTRA_STREAM, arrayImageUris);
        context.startActivity(shareIntent);


    }

    public static void shareToWChart(Context context, File[] imageUris, String imgUrl) {
        ComponentName comp = new ComponentName(WXPACKAGENAME, WX);
        shareSingleOrMoreImage(context, comp, WXPACKAGENAME, imgUrl, imageUris);
    }

    public static void shareToWChartFs(Context context, File[] imageUris, String imgUrl) {
        ComponentName comp = new ComponentName(WXPACKAGENAME, WXF);
        shareToWchartFs(context, comp, WXPACKAGENAME, imgUrl, imageUris);
    }

    public static void shareToQQ(Context context, File[] imageUris, String imgUrl) {
        ComponentName comp = new ComponentName(QQPACKAGENAME, QQ);
        shareSingleOrMoreImage(context, comp, QQPACKAGENAME, imgUrl, imageUris);
    }

    public static void shareToQQZ(Context context, File[] imageUris, String imgUrl) {
        ComponentName comp = new ComponentName(QQZPACKAGENAME, QQZ);
        shareSingleOrMoreImage(context, comp, QQZPACKAGENAME, imgUrl, imageUris);
    }

    public static void shareToSinaWeiBo(Context context, File[] imageUris, String imgUrl) {
        ComponentName comp = new ComponentName(SINAPACKAGENAME, SINA);
        shareSingleOrMoreImage(context, comp, SINAPACKAGENAME, imgUrl, imageUris);
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if ((pinfo.get(i)).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }

        }
        return false;
    }

    public static void umShare(Activity activity, SHARE_MEDIA var1, ArrayList<File> shareListFile, UMShareListener umShareListener){
        UMImage[] umImages = new UMImage[shareListFile.size()];
        for (int i = 0; i < shareListFile.size(); i++){
            UMImage image = new UMImage(activity, shareListFile.get(i));
            image.compressStyle = UMImage.CompressStyle.SCALE;
            umImages[i] = image;
        }

        if (umShareListener != null){
            new ShareAction(activity)
                    .withText("")
                    .withMedias(umImages)
                    .setPlatform(var1)
                    .share();
        }
    }
    /**
     * 友盟分享
     * 上下文activity、分享的链接、标题、内容、类型
     * 若是要分享视频、音乐可看官方文档
     */
    public static void shareWeb(final Activity activity, String WebUrl, String title, String description, String Imageurl, SHARE_MEDIA platform) {
        UMImage thumb = new UMImage( activity, Imageurl);
        UMWeb web = new UMWeb( WebUrl );//连接地址(注意链接开头必须包含http)
        web.setTitle( title );//标题
        web.setDescription( description );//描述
        web.setThumb( thumb );//缩略图
        new ShareAction( activity ) //分享的平台
                .setPlatform( platform ).withMedia( web ).setCallback( new UMShareListener() {

            //            分享开始的回调
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            //            分享成功的回调
            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            //            分享失败的回调
            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            //            分享取消的回调
            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        } ).share();
    }


}
