package java.com.lechuang.module.bean;

import android.graphics.Bitmap;

import java.io.File;

/**
 * @author: zhengjr
 * @since: 2018/9/3
 * @describe:
 */

public class PreShareImageBean {
    public boolean isSelect;//表示是否选中
    public int position;//图片下标
    public String imgUrl;//
    public File fileLocal;//图片保存在本地的路径
    public File fileBigLocal;//图片保存在本地的路径
    public Bitmap mHttpBitmap;
    public Bitmap mLocalBitmap;


    //show大图的时候展示
    public String localImageCache;
    public String localCodeCache;
    public String localCodeCacheShowLogo;
    public String qnyCodeUrl;
    public String qnyCodeUrlShowLogo;
    public String productName;
    public String quanhoujia;
    public String yuanjia;
    public String quan;
    public int shopType;
    public boolean isSelecter;

    public PreShareImageBean() {
    }

    public PreShareImageBean(boolean isSelect, int position, String imgUrl) {
        this.isSelect = isSelect;
        this.position = position;
        this.imgUrl = imgUrl;
    }
}
