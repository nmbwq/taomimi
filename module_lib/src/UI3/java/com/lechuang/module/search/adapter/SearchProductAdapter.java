package java.com.lechuang.module.search.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.com.lechuang.module.bean.SearchResultEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/11/11
 * @describe:
 */

abstract public class SearchProductAdapter <T extends SearchResultEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<SearchResultEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SearchProductAdapter(List<SearchResultEntity> data) {
        super(data);
        addItemTypeView();
    }

    protected abstract void addItemTypeView();

}