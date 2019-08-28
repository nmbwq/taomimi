package com.common.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.common.app.utils.Logger;

import java.util.HashMap;
import java.util.Map;

import static android.R.id.widget_frame;

/**
 * Fragment 容器页面
 */

public class FragmentActivity extends BaseActivity {

    protected static final String TAG = "FragmentActivity";
    protected static final String EXTRA_FRAGMENT = "EXTRA_FRAGMENT";//用户保存当前fragment的类名
    private Fragment mFragment;

    /**
     * 跳转封装
     *
     * @param context
     * @param clazz
     */
    private static void start(Context context, Class<?> clazz) {
        if (context != null && Fragment.class.isAssignableFrom(clazz)) {
            Logger.i(TAG,EXTRA_FRAGMENT);
            context.startActivity(newIntent(clazz, context));
        }
    }

    public static void start(Fragment fragment, Class<?> clazz) {
        if (fragment != null) {
            start(fragment.getActivity(), clazz);
        }
    }

    public static void start(Activity activity, Class<?> clazz) {
        if (activity != null && Fragment.class.isAssignableFrom(clazz)) {
            Logger.i(TAG,EXTRA_FRAGMENT);
            activity.startActivity(newIntent(clazz, activity));
        }
    }

    private static Intent newIntent(Class<?> clazz, Context context) {
        Intent intent = new Intent(context, FragmentActivity.class);
        intent.putExtra(EXTRA_FRAGMENT, clazz.getName());//用户保存当前fragment的类名
        return intent;
    }

    /**
     * Fragment类名
     */
    protected String mFragmentClazz = null;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        super.getLayoutView();

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(widget_frame);
        return frameLayout;
    }

    @Override
    protected void findViews() {
    }

    @Override
    protected void initView() {
        //获取传值
        mFragmentClazz = getIntent().getStringExtra(EXTRA_FRAGMENT);
        replaceFragment();
    }

    @Override
    protected void getData() {

    }

    protected void replaceFragment() {
        try {
            mFragment = (Fragment) getFragmentClass().newInstance();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(widget_frame, mFragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
//            AfExceptionHandler.handle(e, "AfFragmentActivity Fragment 类型错误：" + mFragmentClazz);
        }
    }

    /**
     * 反射缓存
     */
    private static Map<String, Class> typeCache = new HashMap<>();

    private Class<?> getFragmentClass() throws ClassNotFoundException {
        Class type = typeCache.get(mFragmentClazz);
        if (type == null) {
            typeCache.put(mFragmentClazz, type = Class.forName(mFragmentClazz));
        }
        return type;
    }

    public Fragment getFragment() {
        return mFragment;
    }

}
