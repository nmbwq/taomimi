package java.com.lechuang.module.bean;

import java.util.List;

public class LaiXinMainBean {


    /**
     * activeInfo : {"activityName":"拉新榜单","awardPoolImg":"http://img.taoyouji666.com/839d2a2915bb4f7aa2289103fde24b3a?imageView2/2/w/480/q/90","awardTitle":"邀请好友领大礼！","detailedRules":"1. 一张黄卡可参与一场商品试用活动; \n2. 参与试用获得1个试用码，每使用1张黄卡增加1个试用码; 3. 邀请新用户注册获得1张黄卡; \n4. 每人可参与多个商品试用，且每件试用商品使用黄卡数量无 上限; \n5. 当参与人次达到要求时，活动结束，活动结束在21:15之前则 当日开奖，活动结束在21:15之后则第二日开奖，若参与时间 截止后仍未达到开奖人次，活动结束，次日开奖。中奖码将 于每日22点左右公布。计算公式为：A÷B =商加余数，\u201c余 数+1\u201d为中奖码。 答：开奖当日福彩3D开奖号，试机号组合数列; 如：当日福彩3D开奖号为538，试机号为301，则甲为 538301; B：对应试用商品所需参与人次（非实际试用人次）; 如：某试用商品所需参与人次为1012，当日18:00活动时间结 束时，实际参与人次为800，则开奖规则符合当日开奖，取对 应试用商品所需人次，则B为1012;那么A÷ B = 538301÷1012 = 531余929;那么中奖码为930 \n6. 偷偷告诉你多拿试用码会提高中奖概率哦！ \n7. 本活动最终解释权归淘觅觅所有。 \n8. 本次活动所有奖励商品，都由淘觅觅官方全权处理，和相关 平台（如：苹果官方）没有关系！","endTime":1554048000000,"firstAward":"iPhoneXR","id":"18ydg","intId":4,"regulation":"1. 一张黄卡可参与一场商品试用活动; \n2. 参与试用获得1个试用码，每使用1张黄卡增加1个试用码; \n3. 邀请新用户注册获得1张黄卡; \n4. 每人可参与多个商品试用，且每件试用商品使用黄卡数量无上限;","regulationArr":["一张黄卡可参与一场商品试用活动; ","参与试用获得1个试用码，每使用1张黄卡增加1个试用码; ","邀请新用户注册获得1张黄卡; ","每人可参与多个商品试用，且每件试用商品使用黄卡数量无上限;"],"secondAward":"8克小金猪","startTime":1548604800000,"status":"0","thirdAward":"智能保温杯12"}
     */

    private ActiveInfoBean activeInfo;

    public ActiveInfoBean getActiveInfo() {
        return activeInfo;
    }

    public void setActiveInfo(ActiveInfoBean activeInfo) {
        this.activeInfo = activeInfo;
    }

    public static class ActiveInfoBean {
        /**
         * activityName : 拉新榜单
         * awardPoolImg : http://img.taoyouji666.com/839d2a2915bb4f7aa2289103fde24b3a?imageView2/2/w/480/q/90
         * awardTitle : 邀请好友领大礼！
         * detailedRules : 1. 一张黄卡可参与一场商品试用活动;
         2. 参与试用获得1个试用码，每使用1张黄卡增加1个试用码; 3. 邀请新用户注册获得1张黄卡;
         4. 每人可参与多个商品试用，且每件试用商品使用黄卡数量无 上限;
         5. 当参与人次达到要求时，活动结束，活动结束在21:15之前则 当日开奖，活动结束在21:15之后则第二日开奖，若参与时间 截止后仍未达到开奖人次，活动结束，次日开奖。中奖码将 于每日22点左右公布。计算公式为：A÷B =商加余数，“余 数+1”为中奖码。 答：开奖当日福彩3D开奖号，试机号组合数列; 如：当日福彩3D开奖号为538，试机号为301，则甲为 538301; B：对应试用商品所需参与人次（非实际试用人次）; 如：某试用商品所需参与人次为1012，当日18:00活动时间结 束时，实际参与人次为800，则开奖规则符合当日开奖，取对 应试用商品所需人次，则B为1012;那么A÷ B = 538301÷1012 = 531余929;那么中奖码为930
         6. 偷偷告诉你多拿试用码会提高中奖概率哦！
         7. 本活动最终解释权归淘觅觅所有。
         8. 本次活动所有奖励商品，都由淘觅觅官方全权处理，和相关 平台（如：苹果官方）没有关系！
         * endTime : 1554048000000
         * firstAward : iPhoneXR
         * id : 18ydg
         * intId : 4
         * regulation : 1. 一张黄卡可参与一场商品试用活动;
         2. 参与试用获得1个试用码，每使用1张黄卡增加1个试用码;
         3. 邀请新用户注册获得1张黄卡;
         4. 每人可参与多个商品试用，且每件试用商品使用黄卡数量无上限;
         * regulationArr : ["一张黄卡可参与一场商品试用活动; ","参与试用获得1个试用码，每使用1张黄卡增加1个试用码; ","邀请新用户注册获得1张黄卡; ","每人可参与多个商品试用，且每件试用商品使用黄卡数量无上限;"]
         * secondAward : 8克小金猪
         * startTime : 1548604800000
         * status : 0
         * thirdAward : 智能保温杯12
         */

        private String activityName;
        private String awardPoolImg;
        private String awardTitle;
        private String detailedRules;
        private long endTime;
        private String firstAward;
        private String id;
        private int intId;
        private String regulation;
        private String secondAward;
        private long startTime;
        private String status;
        private String thirdAward;
        private List<String> regulationArr;

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getAwardPoolImg() {
            return awardPoolImg;
        }

        public void setAwardPoolImg(String awardPoolImg) {
            this.awardPoolImg = awardPoolImg;
        }

        public String getAwardTitle() {
            return awardTitle;
        }

        public void setAwardTitle(String awardTitle) {
            this.awardTitle = awardTitle;
        }

        public String getDetailedRules() {
            return detailedRules;
        }

        public void setDetailedRules(String detailedRules) {
            this.detailedRules = detailedRules;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getFirstAward() {
            return firstAward;
        }

        public void setFirstAward(String firstAward) {
            this.firstAward = firstAward;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIntId() {
            return intId;
        }

        public void setIntId(int intId) {
            this.intId = intId;
        }

        public String getRegulation() {
            return regulation;
        }

        public void setRegulation(String regulation) {
            this.regulation = regulation;
        }

        public String getSecondAward() {
            return secondAward;
        }

        public void setSecondAward(String secondAward) {
            this.secondAward = secondAward;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getThirdAward() {
            return thirdAward;
        }

        public void setThirdAward(String thirdAward) {
            this.thirdAward = thirdAward;
        }

        public List<String> getRegulationArr() {
            return regulationArr;
        }

        public void setRegulationArr(List<String> regulationArr) {
            this.regulationArr = regulationArr;
        }
    }
}
