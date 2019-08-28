package com.common.app.jumprouter;

import android.app.Activity;
import android.content.Context;

/**
 * @author: zhengjr
 * @since: 2018/9/4
 * @describe:
 */

public interface IBuilder {

    /**
     * 跳转到外部h5界面
     */
    void startOutsideH5Builder(Context activity, String url);

    /**
     * 跳转到内部h5界面
     */
    void startInsideH5Builder(String url,String title,RouterBean routerBean);

    /**
     * 跳转到内部APP（携带参数）
     */
    void startAppParmaBuilder();

    /**
     * 跳转到内部APP（不携带参数）
     */
    void startAppNoParmaBuilder();

    /**
     * 跳转到商品详情
     */
    void startProductInfo(String i,String id,String t,String zbjId );
    /**
     * 回到顶部
     */
    void gotoTop();

}
