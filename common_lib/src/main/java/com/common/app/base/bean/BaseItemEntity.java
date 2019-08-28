package com.common.app.base.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author: zhengjr
 * @since: 2018/11/29
 * @describe:
 */

public class BaseItemEntity implements MultiItemEntity {

    public static final int TYPE_DEFITEM = -1;//默认的布局类型

    public int itemType;

    public BaseItemEntity(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
