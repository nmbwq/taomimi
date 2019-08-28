package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class MyCardUsedRecordBean {


    /**
     * data : {"count":10,"list":[{"count":2,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 2","useTime":"2019-01-10 16:02:48"},{"count":2,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 2","useTime":"2019-01-10 16:02:48"},{"count":3,"name":"绿卡","remark":"试用活动","useMiCardDetail":"绿卡 * 3","useTime":"2019-01-10 18:31:13"},{"count":4,"name":"红卡","remark":"试用活动","useMiCardDetail":"红卡 * 4","useTime":"2019-01-10 18:31:14"},{"count":5,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 5","useTime":"2019-01-10 18:30:46"},{"count":2,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 2","useTime":"2019-01-10 16:02:48"},{"count":7,"name":"绿卡","remark":"试用活动","useMiCardDetail":"绿卡 * 7","useTime":"2019-01-10 18:31:15"},{"count":4,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 4","useTime":"2019-01-10 18:30:48"},{"count":2,"name":"觅卡1","remark":"试用活动","useMiCardDetail":"觅卡1 * 2","useTime":"2019-01-10 18:31:18"},{"count":3,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 3","useTime":"2019-01-10 18:30:49"}]}
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
         * count : 10
         * list : [{"count":2,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 2","useTime":"2019-01-10 16:02:48"},{"count":2,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 2","useTime":"2019-01-10 16:02:48"},{"count":3,"name":"绿卡","remark":"试用活动","useMiCardDetail":"绿卡 * 3","useTime":"2019-01-10 18:31:13"},{"count":4,"name":"红卡","remark":"试用活动","useMiCardDetail":"红卡 * 4","useTime":"2019-01-10 18:31:14"},{"count":5,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 5","useTime":"2019-01-10 18:30:46"},{"count":2,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 2","useTime":"2019-01-10 16:02:48"},{"count":7,"name":"绿卡","remark":"试用活动","useMiCardDetail":"绿卡 * 7","useTime":"2019-01-10 18:31:15"},{"count":4,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 4","useTime":"2019-01-10 18:30:48"},{"count":2,"name":"觅卡1","remark":"试用活动","useMiCardDetail":"觅卡1 * 2","useTime":"2019-01-10 18:31:18"},{"count":3,"name":"黄卡","remark":"试用活动","useMiCardDetail":"黄卡 * 3","useTime":"2019-01-10 18:30:49"}]
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
             * count : 2
             * name : 黄卡
             * remark : 试用活动
             * useMiCardDetail : 黄卡 * 2
             * useTime : 2019-01-10 16:02:48
             */

            public int count;
            public String name;
            public String remark;
            public String useMiCardDetail;
            public String useTime;

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getUseMiCardDetail() {
                return useMiCardDetail;
            }

            public void setUseMiCardDetail(String useMiCardDetail) {
                this.useMiCardDetail = useMiCardDetail;
            }

            public String getUseTime() {
                return useTime;
            }

            public void setUseTime(String useTime) {
                this.useTime = useTime;
            }
        }

}
