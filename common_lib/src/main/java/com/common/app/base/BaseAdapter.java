package com.common.app.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.common.app.base.bean.BaseItemEntity;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/11/29
 * @describe:
 */

public abstract class BaseAdapter<T extends BaseItemEntity,K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<T, K> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BaseAdapter(List<T> data) {
        super(data);
        addItemTypeView();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    return onAttachedToRv(position);
                }

            });
        }
    }

    /**
     * 设置item的排列方式
     *
     * @param position
     * @describe://默认显示一列
     */
    protected int onAttachedToRv(int position) {
        return 1;//默认显示一列
    }

    /**
     * 添加item布局
     */
    protected abstract void addItemTypeView();

}
