package java.com.lechuang.module.upgrade.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.com.lechuang.module.upgrade.bean.UpGradeEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public abstract class OtherRvAdapter<T extends UpGradeEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<UpGradeEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public OtherRvAdapter(List<UpGradeEntity> data) {
        super(data);
        addItemTypeView();
    }

    protected abstract void addItemTypeView();

}
