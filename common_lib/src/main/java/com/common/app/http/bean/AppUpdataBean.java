package com.common.app.http.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public class AppUpdataBean {

    public MaxApp maxApp;

    public static class MaxApp{
        public String downloadQnyUrl;//七牛云
        public String downloadUrl;//app下载
        public String versionDescribe;
        public String versionNumber;

    }
}
