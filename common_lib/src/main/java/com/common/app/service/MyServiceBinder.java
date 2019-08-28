package com.common.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.common.BuildConfig;
import com.common.R;
import com.common.app.http.DownNetWork;
import com.common.app.http.DownProgressRxObserver;
import com.common.app.utils.FileUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.NotificationUtils;
import com.common.app.utils.Utils;

import java.io.File;

import okhttp3.ResponseBody;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public class MyServiceBinder extends Binder {
    private Context mContext;
    private NotificationUtils mNotificationUtils;

    public MyServiceBinder(Context context, NotificationUtils notificationUtils) {
        this.mContext = context;
        this.mNotificationUtils = notificationUtils;
    }

    public void startDownload(String loadUrl) {
        if (TextUtils.isEmpty(loadUrl) || !loadUrl.endsWith(".apk") || !loadUrl.startsWith("http")){
            Utils.toast("下载地址有误！");
            return;
        }
        //判断是否可用于存放数据文件
        if (FileUtils.isExternalStorageReadable()) {
            //用来存放拍照之后的图片存储路径文件夹
            File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
            if (!newFile.exists()) {
                newFile.mkdir();
            }

            File appDownFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH + "/" + FileUtils.getNameFromDate() + ".apk");
            DownNetWork.getInstance()
                    .setLoadUrl(loadUrl)
                    .setFile(appDownFile)
                    .callBack(new DownProgressRxObserver<ResponseBody>(mContext) {
                        @Override
                        public void onStart() {
                            if (mNotificationUtils != null && mNotificationUtils.isNotificationEnabled(mContext)) {
                                mNotificationUtils.sendAppUpdataNotification(0 + "");
                            }
                        }

                        @Override
                        public void onProgress(int progress, int count) {
                            if (mNotificationUtils != null && mNotificationUtils.isNotificationEnabled(mContext)) {
                                mNotificationUtils.sendAppUpdataNotification(progress + "");
                            }
                        }

                        @Override
                        public void onPause() {

                            //暂无实现暂停下载功能
                        }

                        @Override
                        public void onSuccess(String path) {
                            //下载完成
                            if (mNotificationUtils != null){
                                mNotificationUtils.removeAppUpdataNotification();
                            }
                            installApk(new File(path));


                        }

                        @Override
                        public void onFailed(String errorInfo) {
                            //下载失败
                            if (mNotificationUtils != null){
                                mNotificationUtils.removeAppUpdataNotification();
                            }
                        }
                    });
        }
    }

    /**
     * 安装apk
     */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.FILE_PATH + ".fileProvider", file);

            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file.toString()),
                    "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
    }
    /*final String CHANNEL_ID = "APP更新_1";
    final String CHANNEL_NAME = "APP更新";

    private Context mContext;
    private final NotificationManager mManager;
    private RemoteViews view;

    public MyServiceBinder(Context context) {
        this.mContext = context;
        mManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
    }

    public void startDownload(String loadUrl) {
        //判断是否可用于存放数据文件
        if (FileUtils.isExternalStorageReadable()) {
            //用来存放拍照之后的图片存储路径文件夹
            File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
            if (!newFile.exists()) {
                newFile.mkdir();
            }

            File appDownFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH + "/" + FileUtils.getNameFromDate() + ".apk");
            DownNetWork.getInstance()
                    .setLoadUrl(loadUrl)
                    .setFile(appDownFile)
                    .callBack(new DownProgressRxObserver<ResponseBody>(mContext) {
                        @Override
                        public void onStart() {
                            getNotification("正在更新，请稍后。。。",0, 0);
                        }

                        @Override
                        public void onProgress(final int progress, int count) {
                            getNotification("正在更新，请稍后。。。",progress, count);
                            Logger.e("---", progress + "");
                            //更新一个通知
                        }

                        @Override
                        public void onPause() {

                            //暂无实现暂停下载功能
                        }

                        @Override
                        public void onSuccess(String path) {
                            //下载完成
                            mManager.cancel(1);

                        }

                        @Override
                        public void onFailed(String errorInfo) {
                            //下载失败
                            mManager.cancel(1);
                        }
                    });
        }
    }

    private void getNotification(String title,int progress, int count) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false); //是否在桌面icon右上角展示小红点
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            channel.enableVibration(false);
            channel.setSound(null, null);
            mManager.createNotificationChannel(channel);
        }

        //设置下载进度条
        if (view == null) {
            view = new RemoteViews(mContext.getPackageName(), R.layout.layout_notify_appupdata);
        }

        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_common_back)
                .setAutoCancel(false)
//                .setContent(view)
                .build();

        mManager.notify(1, notification);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, R.string.app_name, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;// 滑动或者clear都不会清空



    }*/
}
