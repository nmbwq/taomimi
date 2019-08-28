package java.com.lechuang.module.upgrade.bean;


import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.UpGradeBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

        public class UpGradeEntity implements MultiItemEntity {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_SHAIXUAN = 1;
        public static final int TYPE_PRODUCT = 2;
        public int itemType;

        public List<UpGradeBean> classTypeList;
        public List<UpGradeBean.ProImgListBean> proImgList;//轮播图
        public List<UpGradeBean.BoardListBean> boardList;//运营商收益信息
        public List<UpGradeBean.RightsImgListBean> rightsImgList;//运营商权益图片
        public String topImg;//顶部图片
        public String img;//底部图片
        public Number giftPrice;//礼包价格

        public UpGradeBean mProductListBean;


        //高级
        public String scale;
        public String status;
        public int isMaxLevel;

        public UpGradeEntity(int itemType) {
        this.itemType = itemType;
        }

        @Override
        public int getItemType() {
        return this.itemType;
        }
        }
