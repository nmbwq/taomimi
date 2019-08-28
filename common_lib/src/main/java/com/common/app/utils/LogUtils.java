package com.common.app.utils;

import android.util.Log;

import com.common.BuildConfig;


/**
 * @ProjectName: AiParkToC
 * @Package: com.zhtc.ipark.baselibrary.utils
 * @ClassName: LogUtils
 * @Description: Log日志类
 * @Author: 王鹏
 * @CreateDate: 18/10/30 11:16
 * @UpdateUser: 更新者
 * @UpdateDate: 18/10/30 11:16
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LogUtils {

    public static boolean isDebug = BuildConfig.DEBUG;

    private static String TAG = "tmm";

    private static String getFunctionLocation() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LogUtils.class.getName())) {
                    continue;
                }
                return "(" + st.getFileName() + ":" + st.getLineNumber() + ")";
            }
        }
        return null;
    }

    public static void w(String msg) {
        if (isDebug) {
            if (msg == null || msg.length() == 0)
                return;
            int segmentSize = 3 * 1024;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                Log.w(TAG + getFunctionLocation(), msg);
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    Log.w(TAG + getFunctionLocation(), logContent);
                }
                Log.w(TAG + getFunctionLocation(), msg);// 打印剩余日志
            }

        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            if (msg == null || msg.length() == 0)
                return;
            int segmentSize = 3 * 1024;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                Log.w(tag + getFunctionLocation(), msg);
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    Log.w(tag + getFunctionLocation(), logContent);
                }
                Log.w(tag + getFunctionLocation(), msg);
            }

        }
    }


}
