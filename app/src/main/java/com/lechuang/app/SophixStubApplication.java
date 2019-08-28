package com.lechuang.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;

import com.common.app.base.BaseApplication;
import com.common.app.utils.DeviceUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.SPUtils;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * @author: zhengjr
 * @since: 2018/10/8
 * @describe:
 */

public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(MyApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //如果需要使用MultiDex，需要在此处调用。
        //突破65535的限制
        MultiDex.install(base);
        initSophix();
    }

    private void initSophix() {
        String appVersion = "1.0.0";
        try {
            appVersion = getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData("25101050-1", "b8b96b8c5e3da544fbe4ed5762b74669", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCqqi0ZQh0nkbVJmMbOCoSlGL5xDKQQTM0rxwHRFrAGKHtgRqFSCS4k5Y3DYy82si/4x1iKDXTEl16/xTOc2nrrbW9AG7JpJ9cO+E7EW/fOCXpVN8zGUwICgCbpV2+I04dl4waF0o41fCdQetLWq3zNUCNxrmKb3CY11LznZo6U1zB/s/6zMYYfbf1bC/68O9b9kIrt7t6wmNCABb9kiYL444M/ZsFUkD4juSoHma6Y9UoTWFFCR5c3u5IzgcmSdJafQuQHZ1RyX+la4GCLhOmBRxMualFopZ9HCajHltKiN85b6cS5uhxwPNPJ7hG3mCjyOZ3QHjscs+hO/0kkaKL7AgMBAAECggEBAKh+MN5SDrSlP4V0xzpe2gyhfEvifgv1t050QM/shUbfKsenk9eJZrxYwnhX8SIgFAqazUgm9tSs2Yedq1I9xcOvnHl/nsoA4mtOLX3hiqspYlTGLW7UPxS27zPK9jvxEyk9VZBDi9E0TSBA0u3MCwAtEYFH+OUGGddlvEb9UuGQXQpCg72qDEps9U8lP/RnAphkcKU+gqJ7BXx/lgQpjUGNJH6MwZNnHpFC89x5ahMt3mJDzdYEbr6T7AAtETwEZw2UWxkx9cHvyyz+R/0SQ2M3u7+P2I7ijfPNu38Irv6AZLSQfrRoOiz280ALjszeuR2+cu7P2XozjWzrRL+JffkCgYEA9AePV2QaSqUJbMQnKw3mWL/ZR4ojNPc1lsZEWlaSjlFOFvTNujvWZGlLVtlnsT9AxxPaIT0xrtYDbZEOEdrX1nekgIzeh9dEj2LU/b5s7t2LyJvDfu2aYpIFcjQyk7l7ZdbE6fcz7PnjKnBcUzC4BiMlejSuFVDce7LM9i9AQy0CgYEAswlTgscKwOsfH+ceT6D/ITml/ifUYji78XAEYoT3nF40QRFCM3HfOmGMF4s3ZWSoNGOSpoDML7zlJ4S89qRHM7eqsttFg7cHqMezoEG2MP0Y9WQvBo3BAVNwBA48y4MPLKBMAePzBAC+3G3Hsz/ALdZ/t/v7gvwEJoSnBQG098cCgYB97DDBbbxLbGIyp/12MBP/E/BxBA0q1a3ngaPf4fB6U+Yx3l0SjrIQ2mywAjuJsRQiJlJW+Jbcmz7lmQZjnEPoPZC7bPWfryuHuf4iedMIZ3YsLIRyyPTzjFoXFmxK4lmUsYCnIpK+5CueyKA7pVYwhI5gQzLZeQJtKlStbLCCeQKBgCRscE352ok3DGT8KyF+GomS5d9YERBOhIXxCSNV894fGDhon3RB5W1GLS8ZBpMdME9ANrSjHWdU4bXxflQbRdUSt6qdi33pfahrwHKJC9zZkPtTf3Gw4yQ26mIY12t75Vlp3yy/SFB3Kl1EBE3GWX251tACdr/GL736XNRa7Ys/AoGAI2pRqephoyG6dHnplR/cilC11VA8H+UKjKQnVAzqak2UomTXB3fuw1HzDBnl63mz74wrqULh+OMl9zrY7Mw4v72XvQp6blYQg2qKMM6FD3f/7yUOFfx37jddPT5UkUK7FEhcjqTxDMHxu45Vc8m8WuLKaIVBCfqrI1duyRnCVfM=")
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {

                            //尽量别用自己写的api，修复的时候会失败
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            SharedPreferences preferences = getSharedPreferences("lingquanbao", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("reload", false);
                            editor.commit();

                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            //尽量别用自己写的api，修复的时候会失败
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。

                            SharedPreferences preferences = getSharedPreferences("lingquanbao", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("reload", true);
                            editor.commit();

                        }
                    }
                }).initialize();
    }
}
