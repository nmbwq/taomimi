package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class TryRuleBean {

    /**
     * data : {"regular":"1、用户支付不等进入参与早起打卡，放入打卡奖励金奖池。\r\n\r\n2、次日早晨06：00~08：00，用户未在期间打卡，视为打卡失败，挑战金不予退回。\r\n\r\n3、次日早晨06：00~08：00，用户在期间打卡，视为打卡成功，可平均分当日资金池内出本金外全部奖金，奖励金不低于参与挑战是支付的金额。\r\n\r\n4、奖励金在每日8：00打卡时间结束后开始结算，预计当日9：00存入可提现余额，可自行提现。\r\n\r\n5、本活动是倡导养成早起习惯的营销活动，平台不收取任何费用，仅供娱乐参与"}
     * error : Success
     * errorCode : 200
     * status : SUCESS
     */

    public String error;
    public int errorCode;
    public String status;


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

        /**
         * regular : 1、用户支付不等进入参与早起打卡，放入打卡奖励金奖池。

         2、次日早晨06：00~08：00，用户未在期间打卡，视为打卡失败，挑战金不予退回。

         3、次日早晨06：00~08：00，用户在期间打卡，视为打卡成功，可平均分当日资金池内出本金外全部奖金，奖励金不低于参与挑战是支付的金额。

         4、奖励金在每日8：00打卡时间结束后开始结算，预计当日9：00存入可提现余额，可自行提现。

         5、本活动是倡导养成早起习惯的营销活动，平台不收取任何费用，仅供娱乐参与
         */

        public String regular;
        public String content;
        public String explain;

        public String getRegular() {
            return regular;
        }

        public void setRegular(String regular) {
            this.regular = regular;
        }

}
