package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class MyCardBean {


    /**
     * data : {"list":[{"closeEnd":0,"color":"yellow","count":303,"img":"qn|taoyouji2|705d144ebedf405c919a74bf4ddd8196","name":"黄卡","typeId":"b8qp","remark":"11112222"},{"closeEnd":0,"color":"blue","count":3,"img":"qn|taoyouji2|705d144ebedf405c919a74bf4ddd8196","name":"蓝卡","remark":"11112222","typeId":"mhaa"}]}
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

        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * closeEnd : 0
             * color : yellow
             * count : 303
             * img : qn|taoyouji2|705d144ebedf405c919a74bf4ddd8196
             * name : 黄卡
             * typeId : b8qp
             * remark : 11112222
             */

            public int closeEnd;
            public String color;
            public String useMessage;
            public int count;
            public int isActive;
            public int activeType;

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String img;
            public String name;
            public String typeId;
            public String remark;

            public int getCloseEnd() {
                return closeEnd;
            }

            public void setCloseEnd(int closeEnd) {
                this.closeEnd = closeEnd;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

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

            public String getTypeId() {
                return typeId;
            }

            public void setTypeId(String typeId) {
                this.typeId = typeId;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }

}
