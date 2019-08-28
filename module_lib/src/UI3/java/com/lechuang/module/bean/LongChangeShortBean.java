package java.com.lechuang.module.bean;

import java.util.List;

/**
 * Created by lianzun on 2018/12/27.
 */

public class LongChangeShortBean {

//    public List<UrlsBean> urls;
//
//    public List<UrlsBean> getUrls() {
//        return urls;
//    }
//
//    public void setUrls(List<UrlsBean> urls) {
//        this.urls = urls;
//    }
//
//    public static class UrlsBean {
        /**
         * result : true
         * url_short : http://t.cn/E4K5LYD
         * url_long : 原网址
         * object_type : product
         * type : 39
         * object_id : 1042122:shop_sc_weibo_218015454727270006180125
         */

        public boolean result;
        public String url_short;
        public String url_long;
        public String object_type;
        public int type;
        public String object_id;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getUrl_short() {
            return url_short;
        }

        public void setUrl_short(String url_short) {
            this.url_short = url_short;
        }

        public String getUrl_long() {
            return url_long;
        }

        public void setUrl_long(String url_long) {
            this.url_long = url_long;
        }

        public String getObject_type() {
            return object_type;
        }

        public void setObject_type(String object_type) {
            this.object_type = object_type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getObject_id() {
            return object_id;
        }

        public void setObject_id(String object_id) {
            this.object_id = object_id;
        }
//    }
}
