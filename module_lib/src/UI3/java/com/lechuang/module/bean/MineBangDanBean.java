package java.com.lechuang.module.bean;

import java.util.List;

public class MineBangDanBean {

    public List<ListBean> list;

    public static class ListBean{

        public int followerCount;
        public int position;
        public String positionStr;
        public int status;
        public int isAward;
        public String startTime;
        public String endTime;
        public String caption;
        public String id;
    }
}
