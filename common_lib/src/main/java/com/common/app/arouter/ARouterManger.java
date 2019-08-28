package com.common.app.arouter;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

public class ARouterManger {

    private static class SingleHolder {
        private static ARouterManger INSTANCE = new ARouterManger();
    }

    private ARouter mArouter;

    private ARouterManger (){
        mArouter = ARouter.getInstance();
    }

    public static ARouterManger getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void build(String path){
        mArouter.build(path).navigation();
    }
}
