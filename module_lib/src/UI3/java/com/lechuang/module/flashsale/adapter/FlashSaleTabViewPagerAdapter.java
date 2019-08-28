package java.com.lechuang.module.flashsale.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.com.lechuang.module.flashsale.bean.FlashSaleTabBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/7/12
 * @describe:
 */

public class FlashSaleTabViewPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;
    private String[] title;
    private List<Fragment> mTabFragment;
    public FlashSaleTabViewPagerAdapter(Context context, FragmentManager fm,String[] title, List<Fragment> tabFragment) {
        super(fm);
        this.mContext = context;
        this.title = title;
        this.mTabFragment = tabFragment;
    }

    @Override
    public Fragment getItem(int position) {
        //https://www.cnblogs.com/android-blogs/p/5524172.html  缓存网址
        Bundle bundle = new Bundle();
        bundle.putSerializable("TopTab",title[position]);
        Fragment tabViewPagerFragment = mTabFragment.get(position);
        tabViewPagerFragment.setArguments(bundle);
        return tabViewPagerFragment;
    }

    @Override
    public int getCount() {
        return mTabFragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
