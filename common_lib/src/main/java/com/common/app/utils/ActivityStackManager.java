package com.common.app.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.taobao.sophix.SophixManager;

import java.util.Iterator;
import java.util.Stack;

/**
 * @author: zhengjr
 * @since: 2018/9/30
 * @describe:
 */

public class ActivityStackManager implements Application.ActivityLifecycleCallbacks{
    private final String TAG = "ActivityStack";

    //打开的Activity数量统计
    private int mActivityCount = 0;

    private Stack<Activity> stack;

    private ActivityStackManager() {
        stack = new Stack<>();
    }

    private static class Instance{
        public static ActivityStackManager INSTANCE = new ActivityStackManager();
    }

    public static ActivityStackManager getInstance() {
        return Instance.INSTANCE;
    }

    public void register(Application app){
        app.registerActivityLifecycleCallbacks(this);
    }

    public void unRegister(Application app) {
        SophixManager.getInstance().killProcessSafely();//杀死进程保证
        app.unregisterActivityLifecycleCallbacks(this);
    }

    /**
     * @param activity  需要添加进栈管理的activity
     */
    public void addActivity(Activity activity) {
        stack.add(activity);
        Logger.i(TAG,"add-->" + activity.getComponentName().getClassName().toString());
    }

    /**
     * @param activity 需要从栈管理中删除的activity
     * @return
     */
    public boolean removeActivity(Activity activity) {
        Logger.i(TAG,"remove-->" + activity.getComponentName().getClassName().toString());
        return stack.remove(activity);
    }

    /**
     * @param activity 查询指定activity在栈中的位置，从栈顶开始
     * @return
     */
    public int searchActivity(Activity activity) {
        return stack.search(activity);
    }

    /**
     * @param activity 将指定的activity从栈中删除然后finish()掉
     */
    public void finishActivity(Activity activity) {
        stack.pop().finish();
        Logger.i(TAG,"pop-finish-->" + activity.getComponentName().getClassName().toString());
    }

    /**
     * @param activity 将指定类名的activity从栈中删除并finish()掉
     */
    public void finishActivityClass(Class<Activity> activity) {
        if (activity != null) {
            Iterator<Activity> iterator = stack.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();
                if (next.getClass().equals(activity)) {
                    iterator.remove();
                    finishActivity(next);
                }
            }
        }
    }

    /**
     * 销毁activity直到栈的最后一个，保留栈最后一个activity，用于token失效回到首页
     */
    public void finishActivityToLast(){
        while (!stack.isEmpty() && stack.size() > 1) {
            stack.pop().finish();
        }
    }

    /**
     * 销毁所有的activity
     */
    public void finishAllActivity() {
        while (!stack.isEmpty()) {
            stack.pop().finish();
        }
        SophixManager.getInstance().killProcessSafely();//杀死进程保证
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityCount ++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityCount --;
        //当热修复需要重新加载时，切APP处于杀死和后台状态时，都需要杀死进程进行热修复加载
        boolean reload = SPUtils.getInstance().getBoolean(activity, "reload", false);
        if (mActivityCount == 0 && reload){
            //从前台切到后台
            SPUtils.getInstance().putBoolean(activity, "reload", false);
            SophixManager.getInstance().killProcessSafely();//杀死进程
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeActivity(activity);
    }

}
