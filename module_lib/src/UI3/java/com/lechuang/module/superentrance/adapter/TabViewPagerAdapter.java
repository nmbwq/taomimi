package java.com.lechuang.module.superentrance.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.com.lechuang.module.bean.SuperTabBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/7/12
 * @describe:
 */

public class TabViewPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;
    private List<SuperTabBean> mTabBeans;
    private List<Fragment> mTabFragment;
    public TabViewPagerAdapter(Context context, FragmentManager fm, List<SuperTabBean> homeTabBeans, List<Fragment> tabFragment) {
        super(fm);
        this.mContext = context;
        this.mTabBeans = homeTabBeans;
        this.mTabFragment = tabFragment;
    }

    @Override
    public Fragment getItem(int position) {
        //https://www.cnblogs.com/android-blogs/p/5524172.html  缓存网址
        Bundle bundle = new Bundle();
        bundle.putSerializable("TopTab",mTabBeans.get(position));
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
        return mTabBeans.get(position).className;
    }
}
