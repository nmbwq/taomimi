package java.com.lechuang.module.adress.SelectCityUtils.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.common.app.view.coverflowviewpager.OnPageSelectListener;
import com.lechuang.module.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchen on 2017/8/15.
 * <p>
 * 代码中有几个position需要注意一些
 * 1. setCurrentItem的position参数，是指当前页的第一个Item的所在position
 * 2. OnPageChangeListener的position参数，同样也是指当前页的第一个Item的所在position
 * 3. instantiateItem的position参数，是指每一个Item的所在position
 * 4. PageTransformer的position参数，是指每一个Item相对于Page所占的比例，当前页的第一个Item是0，左边的依次减去Item的宽度，右边的依次加上Item的宽度
 * 比如一页只有一个Item。那么当前页的position为0。左边的position分别为-1。右边的position分别为1；
 * 比如一页有三个Item。那么当前页的Item的position分别为0、1/3、2/3。左边的position分别为-1、-2/3、-1/3。右边的position分别为1、4/3、5/3。
 */
public class OnePageThreeItemAdapter extends PagerAdapter {
    //一屏最多3个item，不能随便改变这个值，改这个数量必须要改动很多逻辑
    private static final int ONE_PAGE_ITEM_COUNT = 5;

    private Context context;
    private ViewPager viewPager;
    // 居中的Item的最大放大值
    private float itemMaxScale;
    //在原始的数据前后各加了3项数据
    private List<String> repairData = new ArrayList<String>();
    //原始的数据，用于数据检索等
    private List<String> sourceData = new ArrayList<String>();

    public OnePageThreeItemAdapter(ViewPager viewPager, Context context, float itemMaxScale) {
        this.viewPager = viewPager;
        this.context = context;
        this.itemMaxScale = itemMaxScale;
        initData();
        repairDataForLoop();
        handleLoop();
    }

    /**
     * 调用之前保证initData方法已被调用
     */
    @Override
    public void notifyDataSetChanged() {
        repairDataForLoop();
        super.notifyDataSetChanged();
    }

    /**
     * 初始化原始数据
     */
    public void initData() {
        sourceData.clear();
        for (int i = 0; i < 5; i++) {
            sourceData.add( i + "" );
        }
    }

    /**
     * 为了能无限滚动，将末三项复制加入列表开头，将前三项复制加入到列表末尾
     */
    private void repairDataForLoop() {
        repairData.clear();
        List<String> beforeTemp = new ArrayList<String>();
        List<String> afterTemp = new ArrayList<String>();
        for (int i = 0; i < ONE_PAGE_ITEM_COUNT; i++) {
            beforeTemp.add( 0, sourceData.get( sourceData.size() - 1 - i ) );
            afterTemp.add( sourceData.get( i ) );
        }
        repairData.addAll( beforeTemp );
        repairData.addAll( sourceData );
        repairData.addAll( afterTemp );
    }

    /**
     * 真正处理无限滚动的逻辑，在滑动到边界时，立马跳转到对应的位置
     */
    private void handleLoop() {
        viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            // 是否应该强制跳转到首页
            boolean shouldToBefore = false;
            // 是否应该强制跳转到末页
            boolean shouldToAfter = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //这里的position是指每一页的第一个Item的position
                if (position == 0) {
                    shouldToAfter = true;
                } else if (position == getCount() - ONE_PAGE_ITEM_COUNT) {
                    shouldToBefore = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //onPageScrollStateChanged的SCROLL_STATE_IDLE比onPageSelected晚调用，如果在onPageSelected中处理方法，则不会有滑动动画效果
                Log.d( "Debug", "1-----------------------" );
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (shouldToAfter) {
                        //这里的position是指每一页的第一个Item的position
                        viewPager.setCurrentItem( getCount() - ONE_PAGE_ITEM_COUNT * 2, false );
//                        handleScale(getCount() - ONE_PAGE_ITEM_COUNT * 2 + 1);
                        shouldToAfter = false;
                        Log.d( "Debug", "2-----------------------" );
                    }
                    if (shouldToBefore) {
                        //这里的position是指每一页的第一个Item的position
                        viewPager.setCurrentItem( ONE_PAGE_ITEM_COUNT, false );
//                        handleScale(ONE_PAGE_ITEM_COUNT + 1);
                        shouldToBefore = false;
                        Log.d( "Debug", "3----------------------" );
                    }
                }
            }
        } );
    }

    /**
     * 调用了setCurrentItem(position, false)后，设置的PageTransformer不会生效，
     * 也就是说中间需要放大的项不会放大，所以手动将这一项放大
     */
    private void handleScale(int position) {
        Log.d( "Debug", "4----------------------位置" + position );

        View view = findCenterView( position );
        if (view == null) {
            return;
        }
        view.setScaleX( itemMaxScale );
        view.setScaleY( itemMaxScale );
    }

    /**
     * 通过在instantiateItem方法给每个View设置的Tag来标记，找出内存中的View
     */
    private View findCenterView(int position) {
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            View view = viewPager.getChildAt( i );
            if (String.valueOf( position ).equals( view.getTag() )) {
                return view;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return repairData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate( context, R.layout.activity_text_item, null );
        view.setTag( String.valueOf( position ) );
        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里的position是指每一页的第一个Item的position
                viewPager.setCurrentItem( position == 0 ? 0 : position - 1, true );
//                Toast.makeText( context, "点击了" + repairData.get( position ), Toast.LENGTH_SHORT ).show();
            }
        } );
        container.addView( view );
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView( (View) object );
    }

    @Override
    public float getPageWidth(int position) {
        return 1.0f / ONE_PAGE_ITEM_COUNT;
    }
    public class OnePageThreeItemTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            //这里的position是指每一个Item相对于Page所占的比例，当前页的第一个Item是0，左边的依次减去Item的宽度，右边的依次加上Item的宽度
            float centerPosition = 1.0f / ONE_PAGE_ITEM_COUNT;
            Log.d( "Debug", "当前的position为" + position + "当前的" + centerPosition );
            if (position <= 0) {
                page.setScaleX( 1 );
                page.setScaleY( 1 );
            } else if (position <= centerPosition) {
                page.setScaleX( 1 + (itemMaxScale - 1) * position / centerPosition );
                page.setScaleY( 1 + (itemMaxScale - 1) * position / centerPosition );
//                page.setScaleX(1);
//                page.setScaleY(1);
//                Log.d( "Debug", "position <= centerPosition-----------------------" );
            } else if (position <= centerPosition * 2) {
                page.setScaleX( 1 + (itemMaxScale - 1) * (2 * centerPosition - position) / centerPosition );
                page.setScaleY( 1 + (itemMaxScale - 1) * (2 * centerPosition - position) / centerPosition );
//                Log.d( "Debug", "position <= centerPosition2222-----------------------" );
            }
//            else if (position > centerPosition*2&&position <= centerPosition * 3) {
//                page.setScaleX(1 + (itemMaxScale - 1) * (3 * centerPosition - position) / centerPosition);
//                page.setScaleY(1 + (itemMaxScale - 1) * (3 * centerPosition - position) / centerPosition);
//            }
//            else if (position > centerPosition*3&&position <= centerPosition * 4) {
//                page.setScaleX(1 + (itemMaxScale - 1) * (4 * centerPosition - position) / centerPosition);
//                page.setScaleY(1 + (itemMaxScale - 1) * (4 * centerPosition - position) / centerPosition);
//            }
//             else if (position <= centerPosition *2.2) {
//                page.setScaleX( (float) (1 + (itemMaxScale - 1) * (2.2 * centerPosition - position) / centerPosition) );
//                page.setScaleY( (float) (1 + (itemMaxScale - 1) * (2.2* centerPosition - position) / centerPosition) );
//            }
//            else if (position <= centerPosition * 4) {
////                page.setScaleX(1 + (itemMaxScale - 1) * (4 * centerPosition - position) / centerPosition);
////                page.setScaleY(1 + (itemMaxScale - 1) * (4 * centerPosition - position) / centerPosition);
//                page.setScaleX(1);
//                page.setScaleY(1);
//            }
            else {
                page.setScaleX( 1 );
                page.setScaleY( 1 );
            }
        }
    }

    /**
     * 配置ViewPager一些其他的属性
     */
    public void configViewPager() {
        viewPager.setOffscreenPageLimit( ONE_PAGE_ITEM_COUNT + 2 );
        viewPager.setPageTransformer( true, new OnePageThreeItemTransformer() );
        viewPager.setCurrentItem( ONE_PAGE_ITEM_COUNT );
    }
}