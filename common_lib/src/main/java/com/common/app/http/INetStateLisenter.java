package com.common.app.http;


/**
 * @author: LGH
 * @since: 2018/5/3
 * @describe:
 */

public interface INetStateLisenter {
    //网络的连接状态
    //网络连接的类型
    void netStateLisenter(String connType,boolean netState);
}
