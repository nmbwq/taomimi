package com.common.app.arouter;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.database.manger.UserHelper;

/**
 * @author: zhengjr
 * @since: 2018/8/30
 * @describe:
 */
@Interceptor(priority = 4)
public class ArouterLoginInterceper implements IInterceptor{
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
    }

    @Override
    public void init(Context context) {
    }
}
