package java.com.lechuang.home.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2019/1/9
 * @describe:
 */

public class OtherAllChildBean {

    public List<ClassTypeListBean> classTypeList;

    public static class ClassTypeListBean {
        /**
         * classTypeId : 8
         * img : http://img.taoyouji666.com/697fad6eedb73e8a3e197b7d911e9d70?imageView2/2/w/480/q/90
         * keyword : 童车,母婴,玩具
         * name : 玩具
         * rootId : 2
         */

        public int classTypeId;
        public String img;
        public String keyword;
        public String name;
        public int rootId;
    }
}
