package java.com.lechuang.module.upgrade.bean;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.com.lechuang.module.mytry.bean.MyTryAllEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public abstract class MyOrderAdapter<T extends MyOrderEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<MyOrderEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MyOrderAdapter(List<MyOrderEntity> data) {
        super(data);
        addItemTypeView();
    }

    protected abstract void addItemTypeView();

}
