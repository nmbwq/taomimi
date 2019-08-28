package java.com.lechuang.login.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/30
 * @describe:
 */

public class UsersBean {


    /**
     * user : {"createTime":1535614527000,"createTimeStr":"2018-08-30 15:35:27","firstLoginFlag":1,"id":"oune","invitationCode":"553548","isAgencyStatus":1,"isInvitation":0,"openImPassword":"f86e660245dc743ac28fb944839fcd8c","password":"f97869be787aa2710b8d545138ba8c7d2c857cf58228ab02b708c78a8822ab9f7b8f1fdab2c1bd42","phone":"13221035103","rmUserId":"84","safeToken":"5C9EB594D910876578D4FF3A32BDEA8D","status":1,"superiorId":1322,"verifiCode":267873}
     */

    public UserBean user;

    public static class UserBean {
        /**
         * createTime : 1535614527000
         * createTimeStr : 2018-08-30 15:35:27
         * firstLoginFlag : 1
         * id : oune
         * invitationCode : 553548
         * isAgencyStatus : 1
         * isInvitation : 0
         * openImPassword : f86e660245dc743ac28fb944839fcd8c
         * password : f97869be787aa2710b8d545138ba8c7d2c857cf58228ab02b708c78a8822ab9f7b8f1fdab2c1bd42
         * phone : 13221035103
         * rmUserId : 84
         * safeToken : 5C9EB594D910876578D4FF3A32BDEA8D
         * status : 1
         * superiorId : 1322
         * verifiCode : 267873
         */

        public long createTime;
        public String createTimeStr;
        public int firstLoginFlag;
        public String id;
        public String invitationCode;
        public int isAgencyStatus;
        public int isInvitation;
        public String openImPassword;
        public String password;
        public String phone;
        public String photo;
        public String weixinName;
        public String weixinPhoto;
        public String rmUserId;
        public String safeToken;
        public int status;
        public int superiorId;
        public int verifiCode;
        public int agencyLevel;
    }
}
