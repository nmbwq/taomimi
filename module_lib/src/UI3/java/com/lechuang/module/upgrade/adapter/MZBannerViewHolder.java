package java.com.lechuang.module.upgrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lechuang.module.R;
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_brand_upgrade_mz_banner,null);
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
