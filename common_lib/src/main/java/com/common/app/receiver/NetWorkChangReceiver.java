package com.common.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.common.app.http.INetStateLisenter;
import com.common.app.utils.Logger;
import com.common.app.utils.Utils;

/**
 * @author: LGH
 * @since: 2018/5/2
 * @describe:
 */

public class NetWorkChangReceiver extends BroadcastReceiver{
    private final String TAG = "TAG_NetWorkChangReceiver";

    private INetStateLisenter mINetStateLisenter;

    public NetWorkChangReceiver() {
    }

    public NetWorkChangReceiver(INetStateLisenter mINetStateLisenter) {
        this.mINetStateLisenter = mINetStateLisenter;
    }

    /**
     * 获取连接类型
     *
     * @param type
     * @return
     */
    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "移动网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络数据";
        } else if(type == ConnectivityManager.TYPE_BLUETOOTH){
            connType = "蓝牙共享数据";
        }
        return connType;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Logger.e(TAG, "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
//            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo info = getNetInfo(context);

            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                String type = getConnectionType(info.getType());
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE) {

                        if(mINetStateLisenter != null){
                            mINetStateLisenter.netStateLisenter(type,true);
                        }
//                        EventBusUtils.getInstance().post(netStateEvent);
                        Logger.i(TAG, type + "连接！");
                    }
                } else {
                    if(mINetStateLisenter != null){
                        mINetStateLisenter.netStateLisenter("",false);
                    }
//                    EventBusUtils.getInstance().post(netStateEvent);
                    Logger.i(TAG, type + "断开!");
                }
            }else {
                Logger.i(TAG, "检测网络失败!");
                Utils.toast("检测网络失败!");
            }
        }
    }

    private NetworkInfo getNetInfo(Context context){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiNetworkInfo.isConnected()) {
                return wifiNetworkInfo;
            } else if (dataNetworkInfo.isConnected()) {
                return dataNetworkInfo;
            }
            return null;
        }else {

            Network[] allNetworks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (int i=0; i < allNetworks.length; i++){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(allNetworks[i]);
                if (networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)){
                    return networkInfo;
                }
            }
        }
        return null;
    }
}


