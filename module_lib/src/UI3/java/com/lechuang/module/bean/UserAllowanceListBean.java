package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class UserAllowanceListBean {
        public List<AllowanceListBean> allowanceList;

        public static class AllowanceListBean {
            /**
             * awardDetails : 500.0
             * awardMoney : 5.00
             * createTime : 1553597886000
             * createTimeStr : 2019-03-26 18:58:06
             * id : 59wlwjrn
             * intId : 8342291
             * status : 1
             * type : 21
             * typeStr : 管理津贴提现
             * userId : 1
             */

            public double awardDetails;
            public String awardMoney;
            public long createTime;
            public String createTimeStr;
            public String id;
            public int intId;
            public int status;
            public int type;
            public String typeStr;
            public int userId;
        }
}
