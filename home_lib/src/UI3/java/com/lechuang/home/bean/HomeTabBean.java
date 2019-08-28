package java.com.lechuang.home.bean;

import java.io.Serializable;

/**
 * @author: zhengjr
 * @since: 2018/8/20
 * @describe:
 */

public class HomeTabBean implements Serializable{

    public int programaId;
    public String className;
    public boolean isSelecter;

    public HomeTabBean(int programaId, String className,boolean isSelecter) {
        this.programaId = programaId;
        this.className = className;
        this.isSelecter = isSelecter;
    }
}
