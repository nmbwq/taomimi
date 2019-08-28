package com.common.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author: zhengjr
 * @since: 2018/7/5
 * @describe:
 */

public class SPUtils {

    //设置表明为APP的名称
    public final String PREFERENCE_NAME = "lingquanbao";

    private static class LazyUtil {
        private static SPUtils instance = new SPUtils();
    }

    public static SPUtils getInstance() {
        return LazyUtil.instance;
    }

    /**
     * 存储字符串
     */
    public boolean putString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 读取字符串
     */
    public String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * 读取字符串（带默认值的）
     */
    public String getString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return preferences.getString(key, defaultValue);
    }

    /**
     * 存储整型数字
     */
    public boolean putInt(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 读取整型数字
     */
    public int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 读取整型数字（带默认值的）
     */
    public int getInt(Context context, String key, int defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return preferences.getInt(key, defaultValue);
    }

    /**
     * 存储长整型数字
     */
    public boolean putLong(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * 读取长整型数字
     */
    public long getLong(Context context, String key) {
        return getLong(context, key, 0xffffffff);
    }

    /**
     * 读取长整型数字（带默认值的）
     */
    public long getLong(Context context, String key, long defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return preferences.getLong(key, defaultValue);
    }

    /**
     * 存储Float数字
     */
    public boolean putFloat(Context context, String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * 读取Float数字
     */
    public float getFloat(Context context, String key) {
        return getFloat(context, key, -1.0f);
    }

    /**
     * 读取Float数字（带默认值的）
     */
    public float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return preferences.getFloat(key, defaultValue);
    }

    /**
     * 存储boolean类型数据
     */
    public boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * 读取boolean类型数据
     */
    public boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * 读取boolean类型数据（带默认值的）
     */
    public boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * 清除数据
     */
    public boolean clearPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        return editor.commit();
    }
}
