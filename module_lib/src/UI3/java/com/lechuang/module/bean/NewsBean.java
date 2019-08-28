package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/24
 * @describe:
 */

public class NewsBean {

    public List<NewsListBean> list;

    public static class NewsListBean {

        public String content;

        public long createTime;

        public String createTimeStr;

        public int status;

        public String orderNum;

        public String integralStatus;

        public String haveIntegral;

        public String type;

        public String title;
        public String id;
        public String sort;

    }
}
