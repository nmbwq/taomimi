package java.com.lechuang.module.other.bean;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.com.lechuang.module.zerobuy.bean.ZeroBuyDetailsEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public abstract class FindTaoOrderAdapter<T extends FindTaoOrderEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<FindTaoOrderEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public FindTaoOrderAdapter(List<FindTaoOrderEntity> data) {
        super(data);
        addItemTypeView();
    }

    protected abstract void addItemTypeView();

}
