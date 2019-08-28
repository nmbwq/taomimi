package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class MyCardExchangeRecordBean {


    /**
     * data : {"count":20,"list":[{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:26:09","secretKey":"ULWB787RM-39-mhaa"},{"aquiredTime":"2019-01-11 13:26:06","secretKey":"ETU96GJ8H-39-b8qp"}]}
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
         * count : 20
         * list : [{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:31:41","secretKey":"WQXSRQNDA-39-18ydg"},{"aquiredTime":"2019-01-11 13:26:09","secretKey":"ULWB787RM-39-mhaa"},{"aquiredTime":"2019-01-11 13:26:06","secretKey":"ETU96GJ8H-39-b8qp"}]
         */

        public int count;
        public List<ListBean> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * aquiredTime : 2019-01-11 13:31:41
             * secretKey : WQXSRQNDA-39-18ydg
             */

            public String aquiredTime;
            public String secretKey;
            public String miCardDetail;

            public String getMiCardDetail() {
                return miCardDetail;
            }

            public void setMiCardDetail(String miCardDetail) {
                this.miCardDetail = miCardDetail;
            }



            public String getAquiredTime() {
                return aquiredTime;
            }

            public void setAquiredTime(String aquiredTime) {
                this.aquiredTime = aquiredTime;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }
        }

}
