package java.com.lechuang.module.tmallsupermarket.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.utils.OnClickEvent;

import java.com.lechuang.module.mytry.bean.TryDetailsEntity;
import java.com.lechuang.module.tmallsupermarket.bean.TmallSupermarketEntity;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public abstract class TmallSupermarketAdapter<T extends TmallSupermarketEntity, K extends BaseViewHolder> extends BaseMultiItemQuickAdapter<TmallSupermarketEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TmallSupermarketAdapter(List<TmallSupermarketEntity> data) {
        super(data);
        addItemTypeView();
    }

//    @Override
//    protected void convert(final BaseViewHolder helper, TmallSupermarketEntity item) {
//        if (helper.getItemViewType()==TmallSupermarketEntity.TYPE_PRODUCTTHREE){
//            helper.itemView.setOnClickListener( new OnClickEvent() {
//                @Override
//                public void singleClick(View v) {
//                    int pos = helper.getAdapterPosition();
//                    collapse( pos );
//                }
//            } );
//        }
//    }

    protected abstract void addItemTypeView();

}
