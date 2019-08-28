package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class MyTryBean {
    /**
     * data : {"pro_list":[{"countDown":359176869,"detailImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941","endTime":"2019-02-18 15:05:31","id":7,"name":"进行2","needNum":4,"price":20,"realNum":1,"showImg":"http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941,"},{"countDown":186376866,"detailImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941","endTime":"2019-02-16 15:05:31","id":8,"name":"进行2","needNum":11,"price":20,"realNum":6,"showImg":"http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941,"},{"countDown":272776863,"detailImg":"qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941","endTime":"2019-02-17 15:05:31","id":9,"name":"正在进行商品","needNum":11,"price":20.01,"realNum":3,"showImg":"http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941,http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941,"}],"use_list":[{"nickName":"陈子恨","proName":"正在进行商品"},{"nickName":"总公司","proName":"待开奖"},{"nickName":"热带鱼的尾巴","proName":"已开奖1"},{"nickName":"八神","proName":"已开奖1"},{"proName":"已开奖1"},{"nickName":"乐创","proName":"进行2"},{"nickName":"总公司","proName":"正在进行商品"},{"nickName":"总公司","proName":"进行2"},{"nickName":"木有昵称00","proName":"进行2"}]}
     * error : Success
     * errorCode : 200
     * status : SUCESS
     */

    public String error;
    public int errorCode;
    public String status;
    public String imgUrl;

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

    public List<ProListBean> getProList() {
        return proList;
    }

    public void setProList(List<ProListBean> proList) {
        this.proList = proList;
    }

    public List<UseListBean> getUseList() {
        return useList;
    }

    public void setUseList(List<UseListBean> useList) {
        this.useList = useList;
    }

    public List<ProListBean> proList;
    public List<UseListBean> useList;



        public static class ProListBean {
            /**
             * countDown : 359176869
             * detailImg : qn|taoyouji2|2493feb037409c7b3a48d2315590c941,qn|taoyouji2|2493feb037409c7b3a48d2315590c941
             * endTime : 2019-02-18 15:05:31
             * id : 7
             * name : 进行2
             * needNum : 4
             * price : 20.0
             * realNum : 1
             * showImg : http://img.taoyouji666.com/2493feb037409c7b3a48d2315590c941,
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
            public List<String> showImgList;

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
        }

        public static class UseListBean {
            /**
             * nickName : 陈子恨
             * proName : 正在进行商品
             */

            public String nickName;
            public String proName;

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getProName() {
                return proName;
            }

            public void setProName(String proName) {
                this.proName = proName;
            }

    }
}
