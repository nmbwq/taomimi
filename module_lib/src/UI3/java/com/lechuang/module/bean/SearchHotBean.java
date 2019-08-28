package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/30
 * @describe:
 */

public class SearchHotBean {


    public List<HswListBean> hswList;

    public static class HswListBean {
        /**
         * count : 40
         * id : mhaa
         * insertDate : 1535385600000
         * searchWord : 书包
         */

        public int count;
        public String id;
        public long insertDate;
        public String searchWord;
    }
}
