package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class WithdrawDepositBean {

    /**
     * data : {"cashDeclaration":"提现日为每月25号，满10元可提现，申请提现后会在24小时内转账到您的支付宝里，有任何疑问请联系在线客服。\n客服微信：自定义","integralRate":1,"integralTime":1533052800000,"teamWithdrawMinPrice":10,"withdrawDate":1,"withdrawIntegral":"0","withdrawMinPrice":10,"withdrawPrice":0}
     * error : Success
     * errorCode : 200
     */


    private String error;
    private int errorCode;


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


    public String cashDeclaration;
    public int integralRate;
    public long integralTime;
    public double teamWithdrawMinPrice;
    public int withdrawDate;
    public String withdrawIntegral;
    public double withdrawMinPrice;
    public double withdrawPrice;
    public String alipayNumber;

    public String getAlipayNumber() {
        return alipayNumber;
    }

    public void setAlipayNumber(String alipayNumber) {
        this.alipayNumber = alipayNumber;
    }

    public String getCashDeclaration() {
        return cashDeclaration;
    }

    public void setCashDeclaration(String cashDeclaration) {
        this.cashDeclaration = cashDeclaration;
    }

    public int getIntegralRate() {
        return integralRate;
    }

    public void setIntegralRate(int integralRate) {
        this.integralRate = integralRate;
    }

    public long getIntegralTime() {
        return integralTime;
    }

    public void setIntegralTime(long integralTime) {
        this.integralTime = integralTime;
    }

    public double getTeamWithdrawMinPrice() {
        return teamWithdrawMinPrice;
    }

    public void setTeamWithdrawMinPrice(int teamWithdrawMinPrice) {
        this.teamWithdrawMinPrice = teamWithdrawMinPrice;
    }

    public int getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(int withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public String getWithdrawIntegral() {
        return withdrawIntegral;
    }

    public void setWithdrawIntegral(String withdrawIntegral) {
        this.withdrawIntegral = withdrawIntegral;
    }

    public double getWithdrawMinPrice() {
        return withdrawMinPrice;
    }

    public void setWithdrawMinPrice(int withdrawMinPrice) {
        this.withdrawMinPrice = withdrawMinPrice;
    }

    public double getWithdrawPrice() {
        return withdrawPrice;
    }

    public void setWithdrawPrice(int withdrawPrice) {
        this.withdrawPrice = withdrawPrice;
    }


}
