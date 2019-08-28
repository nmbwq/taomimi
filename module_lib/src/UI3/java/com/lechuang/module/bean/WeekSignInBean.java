package java.com.lechuang.module.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeekSignInBean implements Serializable {
    /**
     * day : 0
     * list : [{"day":10,"endTime":1549779813000,"id":"b8qp","inStatus":2,"intId":1,"isDel":0,"isShow":0,"nper":1,"number":2,"opStatus":4,"price":999.99,"prizeName":"婴儿车","prizePic":"1","show":false,"signNumber":1,"startTime":1549002209000,"winNumber":0,"winNumberSt":0},{"day":5,"endTime":1550211813000,"id":"mhaa","inStatus":2,"intId":2,"isDel":0,"isShow":0,"nper":2,"number":2,"opStatus":4,"price":999.99,"prizeName":"婴儿车","prizePic":"2","show":false,"startTime":1549779809000,"winNumber":0,"winNumberSt":0},{"day":2,"endTime":1550419199000,"id":"xptv","inStatus":2,"intId":3,"isDel":0,"isShow":0,"nper":3,"number":2,"opStatus":4,"price":999.99,"prizeName":"婴儿车","prizePic":"3","show":false,"signNumber":1,"startTime":1550246400000,"winNumber":0,"winNumberSt":0},{"day":20,"endTime":1550643813000,"id":"18ydg","inStatus":0,"intId":4,"isDel":0,"isShow":0,"nper":4,"number":2,"opStatus":0,"price":999.99,"prizeName":"婴儿车","prizePic":"4","show":false,"startTime":1551421409000,"winNumber":0,"winNumberSt":0}]
     * opStatus : 0
     * signNumber : 0
     */

    private int day;
    private int opStatus;
    private int signNumber;
    public int showSort;
    public List<DayStrListBean> dayStr;
    public String regular;
    public List<WeekSignListBean> list;

    public static class DayStrListBean{
        public String timeStr;
        public String status;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getOpStatus() {
        return opStatus;
    }

    public void setOpStatus(int opStatus) {
        this.opStatus = opStatus;
    }

    public int getSignNumber() {
        return signNumber;
    }

    public void setSignNumber(int signNumber) {
        this.signNumber = signNumber;
    }

    public List<WeekSignListBean> getList() {
        return list;
    }

    public void setList(List<WeekSignListBean> list) {
        this.list = list;
    }

    public static class WeekSignListBean implements Serializable {
        /**
         * day : 10
         * endTime : 1549779813000
         * id : b8qp
         * inStatus : 2
         * intId : 1
         * isDel : 0
         * isShow : 0
         * nper : 1
         * number : 2
         * opStatus : 4
         * price : 999.99
         * prizeName : 婴儿车
         * prizePic : 1
         * show : false
         * signNumber : 1
         * startTime : 1549002209000
         * winNumber : 0
         * winNumberSt : 0
         */

        private int day;
        private long endTime;
        private String id;
        private int inStatus;
        private int intId;
        private int isDel;
        private int isShow;
        private int nper;
        private int number;
        private int opStatus;
        private double price;
        private String prizeName;
        private String prizePic;
        private boolean show;
        private int signNumber;
        private long startTime;
        private int winNumber;
        private int winNumberSt;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getInStatus() {
            return inStatus;
        }

        public void setInStatus(int inStatus) {
            this.inStatus = inStatus;
        }

        public int getIntId() {
            return intId;
        }

        public void setIntId(int intId) {
            this.intId = intId;
        }

        public int getIsDel() {
            return isDel;
        }

        public void setIsDel(int isDel) {
            this.isDel = isDel;
        }

        public int getIsShow() {
            return isShow;
        }

        public void setIsShow(int isShow) {
            this.isShow = isShow;
        }

        public int getNper() {
            return nper;
        }

        public void setNper(int nper) {
            this.nper = nper;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getOpStatus() {
            return opStatus;
        }

        public void setOpStatus(int opStatus) {
            this.opStatus = opStatus;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getPrizeName() {
            return prizeName;
        }

        public void setPrizeName(String prizeName) {
            this.prizeName = prizeName;
        }

        public String getPrizePic() {
            return prizePic;
        }

        public void setPrizePic(String prizePic) {
            this.prizePic = prizePic;
        }

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public int getSignNumber() {
            return signNumber;
        }

        public void setSignNumber(int signNumber) {
            this.signNumber = signNumber;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getWinNumber() {
            return winNumber;
        }

        public void setWinNumber(int winNumber) {
            this.winNumber = winNumber;
        }

        public int getWinNumberSt() {
            return winNumberSt;
        }

        public void setWinNumberSt(int winNumberSt) {
            this.winNumberSt = winNumberSt;
        }
    }

}
