package com.common.app.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.common.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.support.v4.app.NotificationCompat.FLAG_NO_CLEAR;

/**
 * @author: zhengjr
 * @since: 2018/9/12
 * @describe:
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public final String channelId_app_updata = "App_Updata";
    private final String channelNameAppUpdata = "应用更新";
    private final int tagUpdata = 0;

    public final String channelId_shouyi = "Profit_Notify";
    private final String channelNameShouYi = "收益通知";
    private final int tagShouYi = 1;

    public final String channelId_new_fans = "New_Fans_Notify";
    private final String channelNameNewFans = "新粉丝通知";
    public RemoteViews mRemoteViews;
    private final int tagFans = 2;

    public NotificationUtils(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //APP更新通知
            createNotificationChannel(channelId_app_updata, channelNameAppUpdata, importance);
            //收益通知
            createNotificationChannel(channelId_shouyi, channelNameShouYi, importance);
            //新粉丝通知
            createNotificationChannel(channelId_new_fans, channelNameNewFans, importance);
        }
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        getManager().createNotificationChannel(channel);
    }

    /**
     * 发送更新APP通知
     */
    public void sendAppUpdataNotification(String progress) {
        Notification notification = new NotificationCompat.Builder(this, channelId_app_updata)
//                .setContentTitle("")
//                .setContentText("！")
                .setTicker("APP更新下载")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon( R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setAutoCancel(true)
                .setContent(getRemoteView(progress))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        getManager().notify(tagUpdata, notification);
    }

    public void removeAppUpdataNotification() {
        getManager().cancel(tagUpdata);
    }

    /**
     * 发送收益通知 todo
     */
    public void sendShouYiNotification() {
        Notification notification = new NotificationCompat.Builder(this, channelId_app_updata)
//                .setContentTitle("")
//                .setContentText("！")
                .setTicker("APP更新下载")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon( R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setAutoCancel(true)
                .setProgress(100, 50, false)
                .build();
        notification.flags |= FLAG_NO_CLEAR;
        getManager().notify(tagShouYi, notification);
    }

    /**
     * 发送新粉丝通知 todo
     */
    public void sendNewFansNotification() {
        Notification notification = new NotificationCompat.Builder(this, channelId_app_updata)
//                .setContentTitle("")
//                .setContentText("！")
                .setTicker("APP更新下载")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon( R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setAutoCancel(true)
                .setProgress(100, 50, false)
                .build();
        notification.flags |= FLAG_NO_CLEAR;
        getManager().notify(tagFans, notification);
    }


    /**
     * 创建RemoteViews,3.0之后版本使用
     *
     * @return
     */
    private RemoteViews getRemoteView(String progress) {
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(getPackageName(), R.layout.notify_app_updata);
        }
        mRemoteViews.setTextViewText( R.id.tv_load_progress, "下载进度" + progress + "%");
        mRemoteViews.setProgressBar( R.id.progress_loading, 100, Integer.valueOf(progress), false);
        return mRemoteViews;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationChannel getNotificationChannel(String channelid){
        return getManager().getNotificationChannel(channelid);
    }

    @SuppressLint("NewApi")
    public boolean isNotificationEnabled(Context context) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
                sServiceField.setAccessible(true);
                Object sService = sServiceField.invoke(notificationManager);

                ApplicationInfo appInfo = context.getApplicationInfo();
                String pkg = context.getApplicationContext().getPackageName();
                int uid = appInfo.uid;

                Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                        , String.class, Integer.TYPE);
                method.setAccessible(true);
                return (boolean) method.invoke(sService, pkg, uid);
            } else {
                String CHECK_OP_NO_THROW = "checkOpNoThrow";
                String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
                Class appOpsClass = null;
                AppOpsManager mAppOps = null;
                mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

                ApplicationInfo appInfo = context.getApplicationInfo();
                String pkg = context.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                        AppOpsManager.MODE_ALLOWED);
            }
        } catch (Exception e) {
            return true;
        }

    }

    public void openSystemNotify() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //android 8.0引导
        if (Build.VERSION.SDK_INT >= 26) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        }
        //android 5.0-7.0
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 26) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        }
        //其他
        if (Build.VERSION.SDK_INT < 21) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }

        startActivity(intent);
    }
}
