package java.com.lechuang.brand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.common.app.base.BaseApplication;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.brand.R;
import com.zhouwei.mzbanner.holder.MZViewHolder;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public class MZBannerViewHolder implements MZViewHolder<String> {
    private ImageView mImageView;
    @Override
    public View createView(Context context) {
        // 返回页面布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_brand_mz_banner,null);
        mImageView = (ImageView) view.findViewById(R.id.banner_all_item_image);
        return view;
    }

    @Override
    public void onBind(Context context, int position, String data) {
        // 数据绑定
//        Glide.with(context).load(data).transform(new GlideRoundTransform(BaseApplication.getApplication(), 20)).into(mImageView);
        Glide.with(context).load(data).into(mImageView);
//        mImageView.setImageResource(data);
    }
}
