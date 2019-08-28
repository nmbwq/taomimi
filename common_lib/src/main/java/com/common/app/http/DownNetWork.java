package com.common.app.http;

import android.text.TextUtils;

import com.common.app.http.api.RetrofitServer;
import com.common.app.http.down.DownProgressLisenter;
import com.common.app.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author: zhengjr
 * @since: 2018/8/15
 * @describe:
 */

public class DownNetWork {

    private static DownNetWork mDownNetWork;

    private String mLoadUrl;
    private File mFile;

    private DownNetWork() {
    }

    /**
     * 单例模式
     */
    public static DownNetWork getInstance() {
        if (mDownNetWork == null) {
            synchronized (NetWork.class) {
                if (mDownNetWork == null) {
                    mDownNetWork = new DownNetWork();
                }
            }
        }
        return mDownNetWork;
    }

    public DownNetWork setLoadUrl(String loadUrl) {
        this.mLoadUrl = loadUrl;
        return this;
    }

    public DownNetWork setFile(File file) {
        this.mFile = file;
        return this;
    }


    public void callBack(final DownProgressRxObserver observer) {
        if (TextUtils.isEmpty(this.mLoadUrl) || this.mFile == null) {

            return;
        }
        NetWork.getInstance()
                .getApiService(RetrofitServer.class)
                .downloadFile(this.mLoadUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation()) // 用于计算任务
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        writeFile(responseBody, mFile, observer);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 将输入流写入文件
     *
     * @param appDownFile
     */
    private void writeFile(ResponseBody responseBody, File appDownFile, DownProgressLisenter downProgressLisenter) throws IOException {
        String absolutePath = appDownFile.getAbsolutePath();
        long currentLength = 0;
        OutputStream os = null;
        InputStream inputStream = responseBody.byteStream();
        long to = responseBody.contentLength();
        try {
            os = new FileOutputStream(appDownFile); //输出流
            int len;
            byte[] buff = new byte[1024];
            while ((len = inputStream.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                if (downProgressLisenter != null) {
                    downProgressLisenter.onUpdata((int) (100 * currentLength / to), (int) to,absolutePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            downProgressLisenter.onFailed(e.toString());
        } finally {
            if (os != null) {
                try {
                    os.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close(); //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
