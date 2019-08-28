package java.com.lechuang.module.mytry.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.com.lechuang.module.mytry.bean.MyTryAllEntity;
import java.com.lechuang.module.mytry.bean.TryDetailsEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public abstract class TryDetailsAdapter<T extends TryDetailsEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<TryDetailsEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TryDetailsAdapter(List<TryDetailsEntity> data) {
        super(data);
        addItemTypeView();
    }

    protected abstract void addItemTypeView();

}
