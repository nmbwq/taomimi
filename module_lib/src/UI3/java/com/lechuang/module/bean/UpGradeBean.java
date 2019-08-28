package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/6/28
 * @describe:
 */

public class UpGradeBean {





        /**
         * agencyLevel : 0
         * boardList : [{"agencyLevel":2,"id":"7k","intId":16,"nickName":"多米诺","photo":"http://img.taoyouji666.com/16d0836252f5443c94e8aa7b7bb3c7ba","revenue":22154},{"agencyLevel":2,"id":"b8r5","intId":17,"nickName":"橘淮北","photo":"http://img.taoyouji666.com/48528454611e434bba8830ea4edd5723","revenue":9821},{"agencyLevel":2,"id":"mhaq","intId":18,"nickName":"也許明天","photo":"http://img.taoyouji666.com/4fc8ad036c8f47449bcecc5ea6fc2cf8","revenue":13554},{"agencyLevel":2,"id":"xpub","intId":19,"nickName":"﹏solitude。","photo":"http://img.taoyouji666.com/c7b5970bb8d6401ba7893609bdb56c91","revenue":18894},{"agencyLevel":2,"id":"18ydw","intId":20,"nickName":"chen陈","photo":"http://img.taoyouji666.com/1787089c454e4b7f84858543609f5621","revenue":17124}]
         * directCount : 0
         * indirectCount : 0
         * isMaxLevel : 0
         * proImgList : [{"id":"19b70","img":"http://img.taoyouji666.com/90bc2477fbb248aa8f6c4493b54cf6ce","intId":1260,"linkAllows":1,"slipImg":""}]
         * rightsImgList : [{"id":"blk1","img":"http://img.taoyouji666.com/b255be1495692537f9dd943ef4b2b5a5","intId":1249,"linkAllows":1,"slipImg":""},{"id":"y2n7","img":"http://img.taoyouji666.com/bde2627b8596051bd4182fb3ff774667","intId":1251,"linkAllows":1,"slipImg":""},{"id":"19b6s","img":"http://img.taoyouji666.com/90bc2477fbb248aa8f6c4493b54cf6ce","intId":1252,"linkAllows":1,"slipImg":""},{"id":"mu3u","img":"http://img.taoyouji666.com/64206d9b31134929a71e626ebcc12184","intId":1258,"linkAllows":1,"slipImg":""}]
         * status : 0
         * topImg : http://img.taoyouji666.com/b0c994841bfc47a4a12150ad519174d0
         * upDirectCount : 60
         * upIndirectCount : 120
         * wechat_number : Tao-Vivien111
         */

    public int agencyLevel;
    public int directCount;
    public int indirectCount;
    public int isMaxLevel;
    public String status;
    public String topImg;
    public int upDirectCount;
    public int upIndirectCount;
    public String wechat_number;
    public Number giftPrice;//礼包价格
    public List<BoardListBean> boardList;//运营商收益信息
    public List<ProImgListBean> proImgList;//轮播图
    public List<RightsImgListBean> rightsImgList;//运营商权益图片

    public ShareBean share;//分享的信息

    public String scale;
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    public static class ShareBean {
//        分享图片
        public String shareImg;
//        分享链接
        public String shareLink;
//        分享标题
        public String shareTitle;
//        分享文字
        public String shareWords;

        public ShareBean() {
        }

        public String getShareImg() {
            return shareImg;
        }

        public void setShareImg(String shareImg) {
            this.shareImg = shareImg;
        }

        public String getShareLink() {
            return shareLink;
        }

        public void setShareLink(String shareLink) {
            this.shareLink = shareLink;
        }

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getShareWords() {
            return shareWords;
        }

        public void setShareWords(String shareWords) {
            this.shareWords = shareWords;
        }
    }


        public static class BoardListBean {
            /**
             * agencyLevel : 2
             * id : 7k
             * intId : 16
             * nickName : 多米诺
             * photo : http://img.taoyouji666.com/16d0836252f5443c94e8aa7b7bb3c7ba
             * revenue : 22154.0
             */

            public int agencyLevel;
            public String id;
            public int intId;
            public String nickName;
            public String photo;
            public double revenue;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getIntId() {
                return intId;
            }

            public void setIntId(int intId) {
                this.intId = intId;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

        }

        public static class ProImgListBean {
            /**
             * id : 19b70
             * img : http://img.taoyouji666.com/90bc2477fbb248aa8f6c4493b54cf6ce
             * intId : 1260
             * linkAllows : 1
             * slipImg :
             */

            public String id;
            public String img;
            public int integerId;
            public int linkAllows;
            public String slipImg;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public int getLinkAllows() {
                return linkAllows;
            }

            public void setLinkAllows(int linkAllows) {
                this.linkAllows = linkAllows;
            }

            public String getSlipImg() {
                return slipImg;
            }

            public void setSlipImg(String slipImg) {
                this.slipImg = slipImg;
            }
        }

        public static class RightsImgListBean {
            /**
             * id : blk1
             * img : http://img.taoyouji666.com/b255be1495692537f9dd943ef4b2b5a5
             * intId : 1249
             * linkAllows : 1
             * slipImg :
             */

            public String id;
            public String img;
            public int intId;
            public int linkAllows;
            public String slipImg;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public int getIntId() {
                return intId;
            }

            public void setIntId(int intId) {
                this.intId = intId;
            }

            public int getLinkAllows() {
                return linkAllows;
            }

            public void setLinkAllows(int linkAllows) {
                this.linkAllows = linkAllows;
            }

            public String getSlipImg() {
                return slipImg;
            }

            public void setSlipImg(String slipImg) {
                this.slipImg = slipImg;
            }
        }
}
