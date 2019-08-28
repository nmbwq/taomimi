package java.com.lechuang.module.bean;

import java.io.Serializable;

public class ZeroBuyShareAppBean implements Serializable {

    /**
     * data : {"shareImg":"http://img.taoyouji666.com/73a0528c7b4840ec9db28c5bf4ed6a9c","shareUrl":"www.lingyuangou.com"}
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
         * shareImg : http://img.taoyouji666.com/73a0528c7b4840ec9db28c5bf4ed6a9c
         * shareUrl : www.lingyuangou.com
         */

        public String shareImg;
    public String shareUrl;
    public int fontSize;
    public String id;
    public int intId;
    public String inviteCode;
    public int inviteCodeX;
    public int inviteCodeY;
    public String qrCodeLink;
    public int qrCodeSize;
    public int qrCodeX;
    public int qrCodeY;
    public String shareImage;

        public String getShareImg() {
            return shareImg;
        }

        public void setShareImg(String shareImg) {
            this.shareImg = shareImg;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

}
