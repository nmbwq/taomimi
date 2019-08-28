package java.com.lechuang.module.upgrade.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/7/12
 * @describe:
 */

public class UpGradeDetailsTabsViewPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> mTabFragment;
    public UpGradeDetailsTabsViewPagerAdapter(FragmentManager fm, List<Fragment> tabFragment) {
        super(fm);
        this.mTabFragment = tabFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabFragment.get( position );
    }

    @Override
    public int getCount() {
        return mTabFragment.size();
    }

}
