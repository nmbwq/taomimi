package com.common.app.base.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Author：CHENHAO
 * date：2018/5/3
 * desc：
 */

public class BaseResponseBean<T> implements Serializable {
    public int errorCode = 0;
    public String errorMsg = "";
    public T data;
    public String moreInfo = "";

    public List<T> urls;

    /*public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }*/
}
