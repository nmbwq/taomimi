package java.com.lechuang.module.bean;

public class RedPackageBean {


    /**
     * activeInfo : {"activeImg":"http://img.taoyouji666.com/690b748824ec45aea301980c8789cbf6?imageView2/2/w/480/q/90","activeName":"新人注册红包","deposit":160,"effectiveTimemillis":259200000,"endTime":"2019-05-01 10:00:00","fixedAmount":0.01,"floatAmount":0.05,"id":"b8qp","imgHeight":153,"imgWidth":224,"intId":1,"isStatus":1,"kindlyReminder":"1.红包建议小于1元，因为提现最低限制为1元才能提现，保证该新用户为有效用户；","regulation":"1.利用红包引导新人扫码注册；\n\n2.新用户注册后需下载APP才能领取此红包；\n\n3.若该用户两个月内不出单，且余额未体现，则该红包金额将返还给您；","remark":"如果开启了新人红包功能，微信（未注册淘觅觅）扫码会出现下图领取红包的过程，提高新用户注册和下载淘觅觅的积极性","startTime":"2019-03-03 08:00:00","type":1}
     */

    public ActiveInfoBean activeInfo;


    public static class ActiveInfoBean {
        /**
         * activeImg : http://img.taoyouji666.com/690b748824ec45aea301980c8789cbf6?imageView2/2/w/480/q/90
         * activeName : 新人注册红包
         * deposit : 160.0
         * effectiveTimemillis : 259200000
         * endTime : 2019-05-01 10:00:00
         * fixedAmount : 0.01
         * floatAmount : 0.05
         * id : b8qp
         * imgHeight : 153
         * imgWidth : 224
         * intId : 1
         * isStatus : 1
         * kindlyReminder : 1.红包建议小于1元，因为提现最低限制为1元才能提现，保证该新用户为有效用户；
         * regulation : 1.利用红包引导新人扫码注册；

         2.新用户注册后需下载APP才能领取此红包；

         3.若该用户两个月内不出单，且余额未体现，则该红包金额将返还给您；
         * remark : 如果开启了新人红包功能，微信（未注册淘觅觅）扫码会出现下图领取红包的过程，提高新用户注册和下载淘觅觅的积极性
         * startTime : 2019-03-03 08:00:00
         * type : 1
         */

        public String activeImg;
        public String activeName;
        public double deposit;
        public int effectiveTimemillis;
        public String endTime;
        public double fixedAmount;
        public double floatAmount;
        public String id;
        public int imgHeight;
        public int imgWidth;
        public int intId;
        public int isStatus;
        public String kindlyReminder;
        public String regulation;
        public String remark;
        public String startTime;
        public int type;

    }
}
