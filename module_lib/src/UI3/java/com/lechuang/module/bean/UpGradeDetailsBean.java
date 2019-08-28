package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/6/28
 * @describe:
 */

public class UpGradeDetailsBean {
        public List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * bannerId : 1260
             * detailImg : http://img.taoyouji666.com/64206d9b31134929a71e626ebcc12184
             * detailImgHeight : 360
             * detailImgWidth : 750
             * id : 2
             * img : http://img.taoyouji666.com/90bc2477fbb248aa8f6c4493b54cf6ce
             * price : 88.0
             * showImg : http://img.taoyouji666.com/64206d9b31134929a71e626ebcc12184
             */

            public int bannerId;
            public String detailImg;
            public int detailImgHeight;
            public int detailImgWidth;
            public int id;
            public String img;
            public double price;
            public String showImg;

            public int getBannerId() {
                return bannerId;
            }

            public void setBannerId(int bannerId) {
                this.bannerId = bannerId;
            }

            public String getDetailImg() {
                return detailImg;
            }

            public void setDetailImg(String detailImg) {
                this.detailImg = detailImg;
            }

            public int getDetailImgHeight() {
                return detailImgHeight;
            }

            public void setDetailImgHeight(int detailImgHeight) {
                this.detailImgHeight = detailImgHeight;
            }

            public int getDetailImgWidth() {
                return detailImgWidth;
            }

            public void setDetailImgWidth(int detailImgWidth) {
                this.detailImgWidth = detailImgWidth;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public String getShowImg() {
                return showImg;
            }

            public void setShowImg(String showImg) {
                this.showImg = showImg;
            }
        }
}
