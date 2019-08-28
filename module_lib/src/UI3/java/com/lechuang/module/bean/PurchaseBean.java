package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/6/28
 * @describe:
 */

public class PurchaseBean {


        /**
         * returnMap : {"paymentNo":"201903251318271575300065813","sign":"alipay_sdk=alipay-sdk-java-3.4.49.ALL&app_id=2018090761244670&biz_content=%7B%22out_trade_no%22%3A%22201903251318271575300065813%22%2C%22subject%22%3A%22398%E5%A5%97%E9%A4%90%E8%B4%AD%E4%B9%B0%22%2C%22total_amount%22%3A%229.99%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2F47.101.54.66%3A8080%2Fmake_money%2Fnotify_url&sign=dgha%2FJrQlloAXbMfh36dGl6hJI4I%2F1o4aoPoiShuljp8RDSX5veJK%2FMiiPpCu9%2FOV4I04M%2BNfyz3rAOxkD%2BQ%2BHT6kgAe7Kh9AFYx7u1q5ZQALkr%2BB3CuE%2Fu3a87s2HilfsPSOA1H4v4Sjt8S6MiKbl%2FMOvrW8a7jW9%2B5jUTukyKReoRX0ZCczUXP3pdOLwGg3Cqf430KSPNS2ZKFPgbrMNxdhyIqcSVum%2FusWrvQeQleUsUSOjxji7vIg0N55MI5Yl9HMVw7%2BqW0Vo2xZV7OCitBIMoEPBSaGKDT9I43hEplvH0kWap11fFQczqNYvUZCAP7TFq0vzoD7BTQbJcSDg%3D%3D&sign_type=RSA2×tamp=2019-03-25+13%3A18%3A53&version=1.0"}
         * status : 200
         */

        public ReturnMapBean returnMap;
        public int status;
    public String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ReturnMapBean getReturnMap() {
            return returnMap;
        }

        public void setReturnMap(ReturnMapBean returnMap) {
            this.returnMap = returnMap;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public static class ReturnMapBean {
            /**
             * paymentNo : 201903251318271575300065813
             * sign : alipay_sdk=alipay-sdk-java-3.4.49.ALL&app_id=2018090761244670&biz_content=%7B%22out_trade_no%22%3A%22201903251318271575300065813%22%2C%22subject%22%3A%22398%E5%A5%97%E9%A4%90%E8%B4%AD%E4%B9%B0%22%2C%22total_amount%22%3A%229.99%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2F47.101.54.66%3A8080%2Fmake_money%2Fnotify_url&sign=dgha%2FJrQlloAXbMfh36dGl6hJI4I%2F1o4aoPoiShuljp8RDSX5veJK%2FMiiPpCu9%2FOV4I04M%2BNfyz3rAOxkD%2BQ%2BHT6kgAe7Kh9AFYx7u1q5ZQALkr%2BB3CuE%2Fu3a87s2HilfsPSOA1H4v4Sjt8S6MiKbl%2FMOvrW8a7jW9%2B5jUTukyKReoRX0ZCczUXP3pdOLwGg3Cqf430KSPNS2ZKFPgbrMNxdhyIqcSVum%2FusWrvQeQleUsUSOjxji7vIg0N55MI5Yl9HMVw7%2BqW0Vo2xZV7OCitBIMoEPBSaGKDT9I43hEplvH0kWap11fFQczqNYvUZCAP7TFq0vzoD7BTQbJcSDg%3D%3D&sign_type=RSA2×tamp=2019-03-25+13%3A18%3A53&version=1.0
             */

            private String paymentNo;
            private String sign;

            public String getPaymentNo() {
                return paymentNo;
            }

            public void setPaymentNo(String paymentNo) {
                this.paymentNo = paymentNo;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }
        }

}
