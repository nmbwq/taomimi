package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class ZeroBuyDetailsBean {

    /**
     * data : {"product":{"countDown":0,"detailImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941","detailImgList":["http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941","http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941"],"endTime":"2019-02-16 15:05:31","id":8,"name":"进行2","needNum":10,"price":20,"realNum":6,"showImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941","showImgList":["http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941"],"startTime":"2019-01-16 15:05:36"},"retype":1,"shortRegular":"短规则"}
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
         * product : {"countDown":0,"detailImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941","detailImgList":["http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941","http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941"],"endTime":"2019-02-16 15:05:31","id":8,"name":"进行2","needNum":10,"price":20,"realNum":6,"showImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941","showImgList":["http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941"],"startTime":"2019-01-16 15:05:36"}
         * retype : 1
         * shortRegular : 短规则
         */

        public ProductBean product;
    public int retype;
    public String shortRegular;
    public LuckUserBean luckUser;
    public MustWinUserBean mustWinUser;

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getRetype() {
            return retype;
        }

        public void setRetype(int retype) {
            this.retype = retype;
        }

        public String getShortRegular() {
            return shortRegular;
        }

        public void setShortRegular(String shortRegular) {
            this.shortRegular = shortRegular;
        }

        public static class ProductBean {
            /**
             * countDown : 0
             * detailImg : qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941
             * detailImgList : ["http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941","http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941"]
             * endTime : 2019-02-16 15:05:31
             * id : 8
             * name : 进行2
             * needNum : 10
             * price : 20.0
             * realNum : 6
             * showImg : qn|taoyouji2|2493feb037409c7b3a48d2315590c941
             * showImgList : ["http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941"]
             * startTime : 2019-01-16 15:05:36
             */

            public int countDown;
            public String detailImg;
            public String endTime;
            public int id;
            public String name;
            public int needNum;
            public double price;
            public int realNum;
            public String showImg;
            public String startTime;
            public String preWinTime;
            public List<String> detailImgList;
            public List<String> showImgList;
            public String winNum;
            public String weChatNum;
            public String sum;
            public String keyNumStr;
            public String realEndTime;

            public int getCountDown() {
                return countDown;
            }

            public void setCountDown(int countDown) {
                this.countDown = countDown;
            }

            public String getDetailImg() {
                return detailImg;
            }

            public void setDetailImg(String detailImg) {
                this.detailImg = detailImg;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getNeedNum() {
                return needNum;
            }

            public void setNeedNum(int needNum) {
                this.needNum = needNum;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public int getRealNum() {
                return realNum;
            }

            public void setRealNum(int realNum) {
                this.realNum = realNum;
            }

            public String getShowImg() {
                return showImg;
            }

            public void setShowImg(String showImg) {
                this.showImg = showImg;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public List<String> getDetailImgList() {
                return detailImgList;
            }

            public void setDetailImgList(List<String> detailImgList) {
                this.detailImgList = detailImgList;
            }

            public List<String> getShowImgList() {
                return showImgList;
            }

            public void setShowImgList(List<String> showImgList) {
                this.showImgList = showImgList;
            }
        }
    public static class LuckUserBean {
        /**
         * appUserId : 7151
         * keyNum : 4
         * nickName : 陈子恨
         * phone : 17376562650
         * photo : http://img.taoyouji666.com/BEDF7B752A8A4794BFA754AA150224AC
         * ranking : 4
         * sum : 1
         */

        public int appUserId;
        public int keyNum;
        public String nickName;
        public String phone;
        public String photo;
        public int ranking;
        public int sum;
    }

    public static class MustWinUserBean {
        /**
         * appUserId : 1
         * nickName : 总公司
         * phone : 18888888888
         * photo : http://img.taoyouji666.com/8B5F022D9BC03C49257FD3A325294244
         * ranking : 5
         * sum : 6
         */

        public int appUserId;
        public String nickName;
        public String phone;
        public String photo;
        public int ranking;
        public int sum;
    }
}
