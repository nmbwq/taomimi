package com.common.app.http.bean;

import com.google.gson.annotations.Expose;

/**
 * @author: zhengjr
 * @since: 2019/1/15
 * @describe:
 */

public class AlipayEntity {
    @Expose
    private String orderstring;
    @Expose
    private String orderid;
    public String getOrderstring() {
        return orderstring;
    }
    public AlipayEntity setOrderstring(String orderstring) {
        this.orderstring = orderstring;
        return this;
    }
    public String getOrderid() {
        return orderid;
    }
    public AlipayEntity setOrderid(String orderid) {
        this.orderid = orderid;
        return this;
    }

}
