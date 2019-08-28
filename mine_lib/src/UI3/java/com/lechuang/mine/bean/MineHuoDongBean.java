package java.com.lechuang.mine.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/9/28
 * @describe:
 */

public class MineHuoDongBean {
    public List<ActiveListFootBean> activeList_foot;
    public List<ActiveListTopBean> activeList_top;

    public static class ActiveListFootBean {
        /**
         * activeImage : qn|taoyouji2|7d328df8a90abac5f87741a8205fd2b3
         * activeName : 赚钱攻略
         * activeUrl :
         * id : mhaa
         * img : http://img.taoyouji666.com/7d328df8a90abac5f87741a8205fd2b3
         * orderNum : 1
         * status : 1
         * type : 2
         */

        public String activeImage;
        public String activeName;
        public String activeUrl;
        public String id;
        public String img;
        public int orderNum;
        public int status;
        public int type;
    }

    public static class ActiveListTopBean {
        /**
         * activeImage : qn|taoyouji2|69951610ef2132b7171d8dd16b905416
         * activeName : 邀请活动
         * id : xptv
         * img : http://img.taoyouji666.com/69951610ef2132b7171d8dd16b905416
         * orderNum : 1
         * status : 1
         * type : 1
         * activeUrl :
         */

        public String activeImage;
        public String activeName;
        public String id;
        public String img;
        public int orderNum;
        public int status;
        public int type;
        public String activeUrl;


        public String rootId;
        public String rootName;
        public String link;
        public String mustParam;
        public String attachParam;
        public int linkAllows;
        public int obJump;
        public String commandCopy;//跳转的路径



    }



    /*public List<ActiveListTopBean> activeList_top;
    public List<ActiveListFootBean> activeList_foot;

    public static class ActiveListTopBean{

        public String activeImage;
        public String activeName;
        public String activeUrl;
        public String img;
        public String id;
        public int orderNum;
        public int type;

    }

    public static class ActiveListFootBean{
        public String activeImage;
        public String activeName;
        public String activeUrl;
        public String img;
        public String id;
        public int orderNum;
        public int type;
    }*/
}
