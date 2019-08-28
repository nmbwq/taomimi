package com.common.app.http;

import android.text.TextUtils;
import android.util.Log;

import com.common.BuildConfig;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.cancle.ApiCancleManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import okhttp3.*;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author：CHENHAO
 */

public class NetWork {
    private static NetWork manager;
    private Retrofit retrofit;

    /**
     * 单例模式
     */
    public static NetWork getInstance() {
        if (manager == null) {
            synchronized (NetWork.class) {
                if (manager == null) {
                    manager = new NetWork();
                }
            }
        }
        return manager;
    }

    /**
     * 私有构造方法
     */
    private NetWork() {

        /** OKHttp默认三个超时时间是10s，有些请求时间比较长，需要重新设置下 **/
        X509TrustManager trustManager = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            InputStream open = BaseApplication.getApplication().getResources().getAssets().open("taomimi.pem");
            trustManager = trustManagerForCertificates(open);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constant.OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constant.OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constant.OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(getHttpLoggingIntercept())
//                .addNetworkInterceptor(getHttpLoggingInterceptor())
                .addInterceptor(getUserAgentIntercepter())
//                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
                        return new Converter<ResponseBody,Object>() {
                            @Override
                            public Object convert(ResponseBody body) throws IOException {
                                if (body.contentLength() == 0) return null;
                                return delegate.convert(body);
                            }
                        };
                    }
                })
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, ‘null‘ creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "214904204270262".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }



    /**
     * 返回一个log拦截器,添加之后下载会出现异常，这里没有添加
     * @return
     */

    private Interceptor getHttpLoggingIntercept(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //Chain 里包含了request和response
                Request request = chain.request();
                long t1 = System.nanoTime();//请求发起的时间
                LogUtils.w(String.format("发送请求 %s on %s%n%s",request.url(),chain.connection(),request.headers()));
                Response response = chain.proceed(request);
                long t2 = System.nanoTime();//收到响应的时间
                //不能直接使用response.body（）.string()的方式输出日志
                //因为response.body().string()之后，response中的流会被关闭，程序会报错，
                // 我们需要创建出一个新的response给应用层处理
                ResponseBody responseBody = response.peekBody(1024 * 1024);
                LogUtils.w(String.format("接收响应：[%s] %n返回json:%s  %.1fms%n%s",
                        response.request().url(),
                        responseBody.string(),
                        (t2-t1) /1e6d,
                        response.headers()
                ));
                return response;

            }
        };
    }
    @Deprecated
    private HttpLoggingInterceptor getHttpLoggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    String msg = URLDecoder.decode(message, "utf-8");
                    Log.i("OkHttpInterceptor----->", msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.i("OkHttpInterceptor----->", message);
                }
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    /**
     * 返回一个请求的代理统一配置
     * @return
     */
    private Interceptor getUserAgentIntercepter(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String safeToken = UserHelper.getInstence().getUserInfo().getSafeToken();
                Request request = chain.request().newBuilder()
                        .addHeader("safeToken", TextUtils.isEmpty(safeToken) ? "" : safeToken)
                        .addHeader("client", "android")
                        .addHeader("version", BuildConfig.VERSION_NAME)
                        .build();
                return chain.proceed(request);
            }
        };
    }

    public <T> T getApiService(Class<T> apiServer) {
        return retrofit.create(apiServer);
    }

    //设置tag取消请求标签
    public NetWork setTag(String tag) {
        ApiCancleManager.getInstance().setTagValue(tag);
        return manager;
    }
}
