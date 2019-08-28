package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class FenSiBean {

    /**
     * data : {"agentCount":7,"followerList":[{"agencyLevel":1,"agencyLevelName":"超级会员","createTime":1519886364000,"createTimeStr":"2018年03月01日","nextAgentCount":12,"nickName":"我是一只猫","phone":"15060101857","photo":"http://img.taoyouji666.com/666A65BE2CA09DC794C06ECB04D53893?imageView2/2/w/150/q/90","userId":1234},{"agencyLevel":1,"agencyLevelName":"超级会员","createTime":1519899563000,"createTimeStr":"2018年03月01日","nextAgentCount":11,"nickName":"兔兔侠^\u2022^","phone":"18538573281","userId":1236},{"agencyLevel":1,"agencyLevelName":"超级会员","createTime":1520326631000,"createTimeStr":"2018年03月06日","nextAgentCount":3,"nickName":"如沐买东西专用","phone":"18668191846","userId":1246},{"agencyLevel":1,"agencyLevelName":"超级会员","createTime":1520326656000,"createTimeStr":"2018年03月06日","nextAgentCount":2,"nickName":"大头爸爸","phone":"18668191841","photo":"http://img.taoyouji666.com/5935B6D4727BD66E1089941AD90FB374?imageView2/2/w/150/q/90","userId":1247},{"agencyLevel":1,"agencyLevelName":"超级会员","createTime":1521107302000,"createTimeStr":"2018年03月15日","nextAgentCount":1,"nickName":"Adriana","phone":"15678395076","photo":"http://img.taoyouji666.com/DA6256100D18EB3825DF629A1D7CA7AD?imageView2/2/w/150/q/90","userId":1313},{"agencyLevel":1,"agencyLevelName":"超级会员","createTime":1521428412000,"createTimeStr":"2018年03月19日","nextAgentCount":0,"nickName":"猪猪侠士兵","phone":"19912345678","photo":"http://img.taoyouji666.com/5C67AB7CC33BC67138E34E70667F91C5?imageView2/2/w/150/q/90","userId":1319},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535427603000,"createTimeStr":"2018年08月28日","nextAgentCount":6,"phone":"15666666666","photo":"http://img.taoyouji666.com/0CF86F8702E3DB944EF0709FD1780C0D?imageView2/2/w/150/q/90","userId":1379},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535440530000,"createTimeStr":"2018年08月28日","nextAgentCount":0,"nickName":"小黑哈哈哈","phone":"17099917063","photo":"http://img.taoyouji666.com/E28DC510F15CD1AA59AADEA8C42E3535?imageView2/2/w/150/q/90","userId":1324},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535440644000,"createTimeStr":"2018年08月28日","nextAgentCount":3,"nickName":"0","phone":"15623145243","photo":"http://img.taoyouji666.com/182E581897F30EF93DF996F4A7C844E5?imageView2/2/w/150/q/90","userId":1322},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535509388000,"createTimeStr":"2018年08月29日","nextAgentCount":1,"nickName":"cqwmcn","phone":"15650151872","photo":"http://img.taoyouji666.com/0DB917E7FE66966348233B0198D5E281?imageView2/2/w/150/q/90","userId":1339},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535509529000,"createTimeStr":"2018年08月29日","nextAgentCount":0,"nickName":"大卫·梅森","phone":"17638831868","photo":"http://img.taoyouji666.com/9242D501916E041ADE35D86415C3E518?imageView2/2/w/150/q/90","userId":1490},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535510149000,"createTimeStr":"2018年08月29日","nextAgentCount":0,"nickName":"做好自己","phone":"13663806014","photo":"http://img.taoyouji666.com/3631F77D95104ED6AFE50E95BD96B912?imageView2/2/w/150/q/90","userId":1565},{"agencyLevel":0,"agencyLevelName":"普通会员","createTime":1535708862000,"createTimeStr":"2018年08月31日","nextAgentCount":0,"phone":"13221035103","photo":"http://img.taoyouji666.com/E58A28A0928E556D6DFAA2BB9A47AC0C?imageView2/2/w/150/q/90","userId":6982}],"superAgentCount":6}
     * error : Success
     * errorCode : 200
     */

    private String error;
    private int errorCode;
    public List<FollowerListBean> followerList;
    public List<FollowerListBean> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<FollowerListBean> followerList) {
        this.followerList = followerList;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }



    public int agentCount;
        public int superAgentCount;


        public int getAgentCount() {
            return agentCount;
        }

        public void setAgentCount(int agentCount) {
            this.agentCount = agentCount;
        }

        public int getSuperAgentCount() {
            return superAgentCount;
        }

        public void setSuperAgentCount(int superAgentCount) {
            this.superAgentCount = superAgentCount;
        }



        public static class FollowerListBean {
            /**
             * agencyLevel : 1
             * agencyLevelName : 超级会员
             * createTime : 1519886364000
             * createTimeStr : 2018年03月01日
             * nextAgentCount : 12
             * nickName : 我是一只猫
             * phone : 15060101857
             * photo : http://img.taoyouji666.com/666A65BE2CA09DC794C06ECB04D53893?imageView2/2/w/150/q/90
             * userId : 1234
             */

            public int agencyLevel;
            public String agencyLevelName;
            public long createTime;
            public String createTimeStr;
            public int nextAgentCount;
            public String nickName;
            public String phone;
            public String photo;
            public int userId;

            public int getAgencyLevel() {
                return agencyLevel;
            }

            public void setAgencyLevel(int agencyLevel) {
                this.agencyLevel = agencyLevel;
            }

            public String getAgencyLevelName() {
                return agencyLevelName;
            }

            public void setAgencyLevelName(String agencyLevelName) {
                this.agencyLevelName = agencyLevelName;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getCreateTimeStr() {
                return createTimeStr;
            }

            public void setCreateTimeStr(String createTimeStr) {
                this.createTimeStr = createTimeStr;
            }

            public int getNextAgentCount() {
                return nextAgentCount;
            }

            public void setNextAgentCount(int nextAgentCount) {
                this.nextAgentCount = nextAgentCount;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }
        }

}
