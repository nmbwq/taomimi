package java.com.lechuang.main;


import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseOtherActivity;
import com.lechuang.main.R;

import java.util.ArrayList;

//banner  github   https://github.com/youth5201314/banner?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io
@Route(path = ARouters.PATH_GUILD)
public class GuideActivity extends BaseOtherActivity {

    private ViewPager mBannerGuide;
    private ArrayList<View> mMGuide;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void findViews() {
        mBannerGuide = findViewById(R.id.banner_guide);
        //进入过引导页之后设置为false


    }

    @Override
    protected void initView() {
//        final ArrayList<String> mGuide = getIntent().getStringArrayListExtra("mGuide");
        if (mMGuide == null) {
            mMGuide = new ArrayList<>();
        }
        LayoutInflater from = LayoutInflater.from(this);
        View inflate1 = from.inflate(R.layout.layout_guide1, null);
        View inflate2 = from.inflate(R.layout.layout_guide2, null);
        View inflate3 = from.inflate(R.layout.layout_guide3, null);
        View inflate4 = from.inflate(R.layout.layout_guide4, null);


        mMGuide.add(inflate1);
        mMGuide.add(inflate2);
        mMGuide.add(inflate3);
        mMGuide.add(inflate4);
        inflate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mBannerGuide.setOffscreenPageLimit(mMGuide.size());
        mBannerGuide.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mMGuide.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mMGuide.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });


        /*mBannerGuide.setCanLoop(false);
        mBannerGuide.setIndicatorVisible(false);
        mBannerGuide.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                if (i == mMGuide.size() - 1) {
                    Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
        mBannerGuide.setIndicatorRes(R.drawable.line_banner_unselecter, R.drawable.line_banner_selecter);
        mBannerGuide.setPages(mMGuide, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        mBannerGuide.start();*/

//        mBannerGuide.setDelayTime(2000);//设置轮播时间
//        mBannerGuide.setBannerStyle(BannerConfig.NOT_INDICATOR);//设置没有指示器
//        mBannerGuide.setImageLoader(new ImageLoader() {
//            @Override
//            public void displayImage(Context context, Object path, ImageView imageView) {
//                //Glide 加载图片简单用法
//                Glide.with(context).load(path).into(imageView);
//            }
//        });
//        mBannerGuide.setImages(mGuide);//设置图片源
//        mBannerGuide.setOnBannerListener(new OnBannerListener() {
//            @Override
//            public void OnBannerClick(int position) {
//                if (position >= mGuide.size() - 1){
//                    Intent intent = new Intent(GuideActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });
//        mBannerGuide.start();
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBannerGuide.start();
//        mBannerGuide.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mBannerGuide.pause();
//        mBannerGuide.stopAutoPlay();
    }
}
