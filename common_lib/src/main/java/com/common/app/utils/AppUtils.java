package com.common.app.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.common.app.base.BaseApplication;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/8
 * @describe:
 */

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    /**
     * App 的运行状态
     */
    /* App 运行在前台 */
    public static final int APP_FORE = 1;
    /* App 运行在后台 */
    public static final int APP_BACK = 2;
    /* App 已经被杀死 */
    public static final int APP_DEAD = 3;

    private AppUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断 App 是否存活
     *
     * @param packageName 应用程序包名
     * @return {@code true}: 依然存活<br>{@code false}: 已被杀死
     */
    public static boolean isAppAlive(@NonNull String packageName) {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                Logger.i(TAG, String.format("AppAliveInfo ========> App %s is running", packageName));
                return true;
            }
        }
        Logger.i(TAG, String.format("AppAliveInfo ========> App %s has been killed", packageName));
        return false;
    }

    /**
     * 获取 App 的状态
     *
     * @param packageName 应用程序包名
     * @return {@link #APP_FORE}: 运行在前台<br>{@link #APP_BACK}: 运行在后台<br>{@link #APP_DEAD}: 已被杀死
     */
    public static int getAppStatus(@NonNull String packageName) {
        ActivityManager am = (ActivityManager) BaseApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(20);
        // 判断 App 是否在栈顶
        if (taskInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            Logger.i(TAG, String.format("AppStatusInfo ========> App %s is running onForeground", packageName));
            return APP_FORE;
        } else {
            // 判断 App 是否在堆栈中
            for (ActivityManager.RunningTaskInfo info : taskInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    Logger.i(TAG, String.format("AppStatusInfo ========> App %s is running onBackground", packageName));
                    return APP_BACK;
                }
            }
            Logger.i(TAG, String.format("AppStatusInfo ========> App %s has been killed", packageName));
            return APP_DEAD;
        }
    }
}
