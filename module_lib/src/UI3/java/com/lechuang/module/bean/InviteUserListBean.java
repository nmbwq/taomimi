package java.com.lechuang.module.bean;

import java.util.List;

public class InviteUserListBean {

    public List<ListBean> list;

    public static class ListBean{

        public String createTime;
        public String registerTime;
        public String nickName;
        public String phone;
        public String photo;

    }
}
