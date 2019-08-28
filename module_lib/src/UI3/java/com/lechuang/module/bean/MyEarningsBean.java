package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class MyEarningsBean {


        public ListBean list;

        public ListBean getList() {
            return list;
        }

        public void setList(ListBean list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * Estimatedlastmonth : 8.6500
             * EstimatedlastmonthNum : 5
             * Paymenttoday : 0
             * PaymenttodayNum : 0
             * Paymentyesterday : 0
             * PaymentyesterdayNum : 0
             * Settlementlastmonth : 0
             * SettlementlastmonthNum : 0
             * monthlyestimates : 0
             * monthlyestimatesNum : 0
             * total : 8.6500
             * totalNum : 5
             */

            public String Estimatedlastmonth;
            public String EstimatedlastmonthNum;
            public String Paymenttoday;
            public String PaymenttodayNum;
            public String Paymentyesterday;
            public String PaymentyesterdayNum;
            public String Settlementlastmonth;
            public String SettlementlastmonthNum;
            public String monthlyestimates;
            public String monthlyestimatesNum;
            public String total;
            public String totalNum;

            public String getEstimatedlastmonth() {
                return Estimatedlastmonth;
            }

            public void setEstimatedlastmonth(String Estimatedlastmonth) {
                this.Estimatedlastmonth = Estimatedlastmonth;
            }

            public String getEstimatedlastmonthNum() {
                return EstimatedlastmonthNum;
            }

            public void setEstimatedlastmonthNum(String EstimatedlastmonthNum) {
                this.EstimatedlastmonthNum = EstimatedlastmonthNum;
            }

            public String getPaymenttoday() {
                return Paymenttoday;
            }

            public void setPaymenttoday(String Paymenttoday) {
                this.Paymenttoday = Paymenttoday;
            }

            public String getPaymenttodayNum() {
                return PaymenttodayNum;
            }

            public void setPaymenttodayNum(String PaymenttodayNum) {
                this.PaymenttodayNum = PaymenttodayNum;
            }

            public String getPaymentyesterday() {
                return Paymentyesterday;
            }

            public void setPaymentyesterday(String Paymentyesterday) {
                this.Paymentyesterday = Paymentyesterday;
            }

            public String getPaymentyesterdayNum() {
                return PaymentyesterdayNum;
            }

            public void setPaymentyesterdayNum(String PaymentyesterdayNum) {
                this.PaymentyesterdayNum = PaymentyesterdayNum;
            }

            public String getSettlementlastmonth() {
                return Settlementlastmonth;
            }

            public void setSettlementlastmonth(String Settlementlastmonth) {
                this.Settlementlastmonth = Settlementlastmonth;
            }

            public String getSettlementlastmonthNum() {
                return SettlementlastmonthNum;
            }

            public void setSettlementlastmonthNum(String SettlementlastmonthNum) {
                this.SettlementlastmonthNum = SettlementlastmonthNum;
            }

            public String getMonthlyestimates() {
                return monthlyestimates;
            }

            public void setMonthlyestimates(String monthlyestimates) {
                this.monthlyestimates = monthlyestimates;
            }

            public String getMonthlyestimatesNum() {
                return monthlyestimatesNum;
            }

            public void setMonthlyestimatesNum(String monthlyestimatesNum) {
                this.monthlyestimatesNum = monthlyestimatesNum;
            }

            public String getTotal() {
                return total;
            }

            public void setTotal(String total) {
                this.total = total;
            }

            public String getTotalNum() {
                return totalNum;
            }

            public void setTotalNum(String totalNum) {
                this.totalNum = totalNum;
            }
        }

}
