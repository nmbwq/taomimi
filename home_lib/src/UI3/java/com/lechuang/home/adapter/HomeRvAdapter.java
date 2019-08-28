package java.com.lechuang.home.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.com.lechuang.home.bean.HomeAllEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public abstract class HomeRvAdapter<T extends HomeAllEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<HomeAllEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public HomeRvAdapter(List<HomeAllEntity> data) {
        super(data);
        addItemTypeView();
    }

    protected abstract void addItemTypeView();

}
