package java.com.lechuang.module.bean;

import java.io.Serializable;

/**
 * @author: zhengjr
 * @since: 2018/8/20
 * @describe:
 */

public class SuperTabBean implements Serializable{

    public int programaId;
    public String className;

    public SuperTabBean(int programaId, String className) {
        this.programaId = programaId;
        this.className = className;
    }
}
