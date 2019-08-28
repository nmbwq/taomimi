package com.common.app.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.common.app.http.bean.AlipayEntity;
import com.common.app.http.bean.PayResult;
import com.common.app.http.bean.PayZfbResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
/**
 * @author: zhengjr
 * @since: 2019/1/15
 * @describe:
 */

public class PayUtils {
    //开发者的appid,拿到正式的appid，就要替换
    public static final String WECHAT_APP_ID = "*************";
    //商户id
    public static final String PARENT_ID = "*************";//支付宝APP_id 2018090761244670

    private static final int SDK_PAY_FLAG = 1;
    private Activity activity;

    public PayUtils(Activity activity) {
        this.activity = activity;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG:// 支付宝
                    PayZfbResultEntity payZfbResultEntity = (PayZfbResultEntity) msg.obj;
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    PayResult payResult = payZfbResultEntity.payResult;
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (null != resultListener) {
                            resultListener.aliPayCallBack(payZfbResultEntity.orderid);
                        }
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        if (null != resultListener) {
                            resultListener.aliPayCancle(resultInfo);
                        }
                    } else {
                        if (null != resultListener) {
                            resultListener.aliPayFailOther(resultStatus,resultInfo);
                        }
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(activity, "支付失败或未安装支付宝", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * 支付宝支付
     *
     * @param alipayEntity 服务器传回来的支付数据
     */
    public void payByAliPay(final AlipayEntity alipayEntity) {
        //需要处理后台服务器返回的数据信息
        // 后台返回的zfb 信息
        if (TextUtils.isEmpty(alipayEntity.getOrderid())) return;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(alipayEntity.getOrderstring(), true);

                PayZfbResultEntity payZfbResultEntity = new PayZfbResultEntity();
                payZfbResultEntity.payResult = new PayResult(result);
                payZfbResultEntity.orderid = alipayEntity.getOrderid();
                try {
                    JSONObject jsonObject = new JSONObject(payZfbResultEntity.payResult.getResult());
                    //   LogUtils.LOG_D(PayUtils.class, "支付宝返回结果" + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = payZfbResultEntity;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public interface PayResultListener {
        /**
         * 阿里支付完成回掉
         */
        void aliPayCallBack(String orderId);

        /**
         * 取消支付
         */
        void aliPayCancle(String errorInfo);

        /**
         * 支付失败的其他原因
         */
        void aliPayFailOther(String errorCode,String errorInfo);
    }

    private PayResultListener resultListener;

    public void setResultListener(PayResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void release() {
        mHandler.removeCallbacksAndMessages(null);
    }

}
