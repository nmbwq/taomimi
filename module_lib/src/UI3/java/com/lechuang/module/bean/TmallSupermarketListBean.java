package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class TmallSupermarketListBean {
    public List<ListBean> list;
    public static class ListBean {
        public int id;
        public String img;
        public String title;
        public String explains;
        public String couponTypeName;
        public String appTypeName;
        public String couponMoney;
        public String shopName;
        public String link;
    }
}
