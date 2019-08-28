package java.com.lechuang.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lechuang.main.R;
import com.zhouwei.mzbanner.holder.MZViewHolder;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public class BannerViewHolder implements MZViewHolder<Integer> {
    private ImageView mImageView;
    @Override
    public View createView(Context context) {
        // 返回页面布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_guide_banner,null);
        mImageView = (ImageView) view.findViewById(R.id.banner_guide_item_image);
        return view;
    }

    @Override
    public void onBind(Context context, int position, Integer data) {
        // 数据绑定
        Glide.with(context).load(data).into(mImageView);
//        mImageView.setImageResource(data);
    }
}