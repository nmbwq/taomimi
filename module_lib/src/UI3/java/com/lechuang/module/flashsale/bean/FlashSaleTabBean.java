package java.com.lechuang.module.flashsale.bean;

import java.io.Serializable;

/**
 * @author: zhengjr
 * @since: 2018/8/20
 * @describe:
 */

public class FlashSaleTabBean implements Serializable{

    public int programaId;
    public String className;

    public FlashSaleTabBean(int programaId, String className) {
        this.programaId = programaId;
        this.className = className;
    }
}
