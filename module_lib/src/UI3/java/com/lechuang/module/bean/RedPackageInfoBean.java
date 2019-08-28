package java.com.lechuang.module.bean;

import java.util.List;

public class RedPackageInfoBean {


    /**
     * list : [{"createTime":"03-01 17:18:45","expiredTime":"04-30 17:18:45","id":"mhaa","intId":2,"money":0.17,"nickName":"若飞💭","phone":"18882321051","photo":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTL3xC3su7xUBbPzMqkLRyGIDAKt9dEwuWcN2icOoo3AdglOevVpuQiajhyyuoia5DQmmcaBHPCYG5ibGw/132","type":1,"upUserId":1,"userId":86675},{"createTime":"03-02 12:35:06","expiredTime":"04-30 12:35:06","id":"18ydg","intId":4,"money":0.25,"nickName":"木棉","phone":"18356112325","photo":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJqibb3ibNYvm41iaPgm028Ja02DKvXBR0wzbIZXNeMdsse7ejrRbsjzomI8XkxWg7fmYWf2W5m2tgVw/132","type":1,"upUserId":1,"userId":86677},{"createTime":"03-01 15:14:32","expiredTime":"04-30 15:14:32","id":"b8qp","intId":1,"money":0.21,"nickName":"田园在心","phone":"15090387077","photo":"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqE2ib7iaGACNGiaYT0Z0UzDpYjaIZFuPh2ia9Kg3ILANf99gIGn50QXjLYgFVHIL9ToEFfyMvQM5pMTg/132","type":1,"upUserId":1,"userId":86674},{"createTime":"03-02 10:13:34","expiredTime":"04-30 10:13:34","id":"xptv","intId":3,"money":0.31,"nickName":"🍒丫头🍒","phone":"13053879983","photo":"http://thirdwx.qlogo.cn/mmopen/vi_32/R9SIQmREdQ6IhcC4MAljtfRib7KMicLVZ08Y09uuEeEoXfECtyCw36AEuPwm1UpHY65y2c8QrmcbO026hEl9TaMA/132","type":1,"upUserId":1,"userId":86676},{"createTime":"03-03 11:23:29","expiredTime":"04-30 11:23:29","id":"1k6x1","intId":5,"money":0.14,"nickName":"秀芝","phone":"13546393498","photo":"http://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEIYCBROUPYEjiaK4zJ2VTx9LpsafBkJ9H8XzqjsvVeRFHicRH33svgpWnN68h2GhpaRE2xmgxtm8tXg/132","type":1,"upUserId":1,"userId":86678}]
     * provide : 1.08
     * restore : 0.38
     */

    public double provide;
    public double restore;
    public List<ListBean> list;


    public static class ListBean {
        /**
         * createTime : 03-01 17:18:45
         * expiredTime : 04-30 17:18:45
         * id : mhaa
         * intId : 2
         * money : 0.17
         * nickName : 若飞💭
         * phone : 18882321051
         * photo : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTL3xC3su7xUBbPzMqkLRyGIDAKt9dEwuWcN2icOoo3AdglOevVpuQiajhyyuoia5DQmmcaBHPCYG5ibGw/132
         * type : 1
         * upUserId : 1
         * userId : 86675
         */

        public String createTime;
        public String expiredTime;
        public String id;
        public int intId;
        public double money;
        public int isStatus;
        public String nickName;
        public String phone;
        public String photo;
        public int type;
        public int upUserId;
        public int userId;

    }
}
