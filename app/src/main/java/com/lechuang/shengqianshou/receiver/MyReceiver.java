package com.lechuang.shengqianshou.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.Logger;
import com.common.app.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.ProductInfoBean;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));


            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//                String model = openNotification(bundle, "model");
                String type = openNotification(bundle, "type");
                if (!UserHelper.getInstence().isLogin() && !TextUtils.isEmpty(type) && (TextUtils.equals("3", type) || TextUtils.equals("4", type))) {
                    JPushInterface.clearAllNotifications(context);
                }

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");

                String img = openNotification(bundle, "img");
                String link = openNotification(bundle, "link");
                String type = openNotification(bundle, "type");
                String mustParam = openNotification(bundle, "mustParam");
                String attachParam = openNotification(bundle, "attachParam");
                String rootName = openNotification(bundle, "rootName");
                String obJump = openNotification(bundle, "obJump");
                String linkAllows = openNotification(bundle, "linkAllows");
                String commandCopy = openNotification(bundle, "commandCopy");


                RouterBean routerBean = new RouterBean();
                routerBean.img = img;
                routerBean.link = link;
                routerBean.type = Integer.valueOf(TextUtils.isEmpty(type) ? "0" : type);
                routerBean.mustParam = mustParam;
                routerBean.attachParam = attachParam;
                routerBean.rootName = rootName;
                routerBean.obJump = Integer.valueOf(TextUtils.isEmpty(obJump) ? "0" : obJump);
                routerBean.linkAllows = Integer.valueOf(TextUtils.isEmpty(linkAllows) ? "0" : linkAllows);
                routerBean.commandCopy = commandCopy;
                LinkRouterUtils.getInstance().setRouterBean(context, routerBean);


                /*String model = openNotification(bundle, "model");

                if (!TextUtils.isEmpty(model) && TextUtils.equals("0", model)) {//仅显示文字信息

                } else if (!TextUtils.isEmpty(model) && TextUtils.equals("1", model)) {//推送商品
                    String id = openNotification(bundle, "id");
                    String productId = openNotification(bundle, "productId");
                    if (id != null && !id.equalsIgnoreCase("")
                            && productId != null && !productId.equalsIgnoreCase("")) {
                        getProgramData(context, id, productId);
                    }
                } else if (!TextUtils.isEmpty(model) && TextUtils.equals("2", model)) {//推送活动
                    String activeUrl = openNotification(bundle, "activeUrl");
                    if (activeUrl != null && !activeUrl.equalsIgnoreCase("")) {
                        ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                .withString("loadUrl", Qurl.HOST + activeUrl)
                                .navigation();
                    }
                } else if (!TextUtils.isEmpty(model) && TextUtils.equals("3", model)) {//消息的订单收益
                    ARouter.getInstance().build(ARouters.PATH_NEWS)
                            .withBoolean("mSetDefState", false)
                            .navigation();
                } else if (!TextUtils.isEmpty(model) && TextUtils.equals("4", model)) {//消息的其他消息
                    ARouter.getInstance().build(ARouters.PATH_NEWS)
                            .withBoolean("mSetDefState", true)
                            .navigation();
                }*/


                //打开自定义的Activity
//                Intent i = new Intent(context, MainActivity.class);
//                i.putExtras(bundle);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                context.startActivity(i);

//                String id = openNotification(bundle, "id");
//                String productId = openNotification(bundle, "productId");
//                String activeUrl = openNotification(bundle, "activeUrl");


            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            Logger.e("e----",e.toString());
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private String openNotification(Bundle bundle, String key) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.d(TAG, "extras : " + extras);
        String value = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            return extrasJson.optString(key);
        } catch (Exception e) {
            Logger.d(TAG, "Unexpected: extras is not a valid json" + e.toString());
        }
        return value;
    }

    private void getProgramData(Context context, String id, String productId) {
        Map<String, Object> allParam = new HashMap<>();
        if (!TextUtils.isEmpty(id)) {
            allParam.put("id", id);
        }
        allParam.put("tbItemId", productId);
        allParam.put("type", 1);
        allParam.put("deviceType", 1);
        String userId = UserHelper.getInstence().getUserInfo().getId();
        if (!TextUtils.isEmpty(userId)) {
            allParam.put("userId", userId);
        }

        NetWork.getInstance()
                .setTag(Qurl.zhuanProductDetail1)
                .getApiService(ModuleApi.class)
                .zhuanProductDetail(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ProductInfoBean>(context, false, false) {

                    @Override
                    public void onSuccess(ProductInfoBean result) {
                        if (result == null) {
                            return;
                        }
                        //请求成功，直接跳转到分享
//						ARouter.getInstance().build(ARouters.PATH_PRE_SHARE)
//								.withString("all", JSON.toJSONString(result))
//								.navigation();
                        RouterBean routerBean = new RouterBean();
                        routerBean.type = 9;
                        routerBean.t = "1";
                        routerBean.mustParam = "type=1"
                                + "&id=" + result.productWithBLOBs.id
                                + "&tbItemId=" + result.productWithBLOBs.tbItemId;


                        String id = StringUtils.getUrlString(routerBean.mustParam, "id");
                        String tbItemId = StringUtils.getUrlString(routerBean.mustParam, "tbItemId");
                        String tbCouponId = StringUtils.getUrlString(routerBean.mustParam, "tbCouponId");
                        String type_info = StringUtils.getUrlString(routerBean.mustParam, "type");
                        String uuid = StringUtils.getUrlString(routerBean.mustParam, "uuid");
                        String sort = StringUtils.getUrlString(routerBean.mustParam, "sort");

                        ARouter.getInstance()
                                .build(ARouters.PATH_PRODUCT_INFO)
                                .withString(Constant.id, id)
                                .withString(Constant.TBITEMID, tbItemId)
                                .withString(Constant.TBCOUPINID, tbCouponId)
                                .withString(Constant.TYPE, type_info)
                                .withString(Constant.UUID, uuid)
                                .withString(Constant.SORT, sort)
                                .navigation();
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    //send msg to MainActivity
    /*private void processCustomMessage(Context context, Bundle bundle) {
        if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}

			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}
	}*/
}
