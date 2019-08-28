package com.lechuang.module.friends;


import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseFragment;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.api.Qurl;
import com.lechuang.module.R;

import java.com.lechuang.module.friends.adapter.FriendsPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_FRIENDS)
public class FriendsFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTvFriendsHeadLeft,mTvFriendsHeadLeftLine;
    private TextView mTvFriendsHeadRight,mTvFriendsHeadRightLine;
    private TextView mTvFriendsHeadOther,mTvFriendsHeadOtherLine;
    private ViewPager mVpFriends;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friends;
    }

    @Override
    protected void findViews() {

        mTvFriendsHeadLeft = $( R.id.tv_friends_head_left);
        mTvFriendsHeadRight = $( R.id.tv_friends_head_right);
        mTvFriendsHeadOther = $( R.id.tv_friends_head_other);

        mTvFriendsHeadLeftLine = $( R.id.tv_friends_head_left_line);
        mTvFriendsHeadRightLine = $( R.id.tv_friends_head_right_line);
        mTvFriendsHeadOtherLine = $( R.id.tv_friends_head_other_line);
        mVpFriends = $( R.id.vp_friends_content);
        $( R.id.tv_status_bar).setBackgroundColor(getResources().getColor(R.color.c_FAFAFA));


        mTvFriendsHeadLeft.setOnClickListener(this);
        mTvFriendsHeadRight.setOnClickListener(this);
        mTvFriendsHeadOther.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mTvFriendsHeadLeft.setSelected(true);
        mTvFriendsHeadRight.setSelected(false);

        mTvFriendsHeadLeftLine.setSelected(true);
        mTvFriendsHeadRightLine.setSelected(false);
        List<Fragment> fragments = new ArrayList<>();
        //ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
//                                            .withString("loadUrl", Qurl.HOST + item.link+item.mustParam)
//                                            .withString(Constant.TITLE, TextUtils.isEmpty(item.rootName) ? "" : item.rootName)
//                                            .navigation();
        Fragment pageLeft = (Fragment) ARouter.getInstance().build( ARouters.PATH_FRAGMENT_CONTENT).withString( Constant.TYPE,"1").navigation();
        Fragment pageRight = (Fragment) ARouter.getInstance().build( ARouters.PATH_FRAGMENT_CONTENT).withString( Constant.TYPE,"2").navigation();
//        Fragment pageOther = (Fragment) ARouter.getInstance().build( ARouters.PATH_FRAGMENT_CONTENT).withString( Constant.TYPE,"3").navigation();
//        Fragment pageOther = (Fragment) ARouter.getInstance().build( ARouters.PATH_FRAGMENT_CONTENT_OTHER).withString("loadUrl", Qurl.HOST + "/appH/html/business_conduct.html").navigation();
//        Fragment pageOther = (Fragment) ARouter.getInstance()
//                .build(ARouters.PATH_SUPER_VIP)
//                .withString(Constant.TITLE, "会员")
////                .withString("loadUrl", Qurl.shengji + "?userId=" + UserHelper.getInstence().getUserInfo().getId())
//                .withString("loadUrl", Qurl.HOST + "/appH/html/business_conduct.html")
//                .withInt(Constant.TYPE, 4)
//                .navigation();
//        Fragment pageOther = (Fragment) ARouter.getInstance().build( ARouters.PATH_FRAGMENT_CONTENT_OTHER).withString("loadUrl", "https://www.baidu.com/").withString( Constant.TYPE,"3").navigation();
        fragments.add(pageLeft);
        fragments.add(pageRight);
//        fragments.add(pageOther);
//        mVpFriends.setOffscreenPageLimit( 3 );
        mVpFriends.setAdapter(new FriendsPagerAdapter(getChildFragmentManager(),fragments));
        mVpFriends.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0){
                    mTvFriendsHeadLeft.setSelected(true);
                    mTvFriendsHeadRight.setSelected(false);
                    mTvFriendsHeadOther.setSelected(false);
                    mTvFriendsHeadLeftLine.setSelected(false);
                    mTvFriendsHeadRightLine.setSelected(false);
                    mTvFriendsHeadOtherLine.setSelected(false);
                }else if (position == 1){
                    mTvFriendsHeadLeft.setSelected(false);
                    mTvFriendsHeadRight.setSelected(true);
                    mTvFriendsHeadOther.setSelected(false);
                    mTvFriendsHeadLeftLine.setSelected(false);
                    mTvFriendsHeadRightLine.setSelected(false);
                    mTvFriendsHeadOtherLine.setSelected(false);
//                }else if (position ==2){
//                    mTvFriendsHeadLeft.setSelected(false);
//                    mTvFriendsHeadRight.setSelected(false);
//                    mTvFriendsHeadOther.setSelected(true);
//                    mTvFriendsHeadLeftLine.setSelected(false);
//                    mTvFriendsHeadRightLine.setSelected(false);
//                    mTvFriendsHeadOtherLine.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.tv_friends_head_left){
            mTvFriendsHeadLeft.setSelected(true);
            mTvFriendsHeadRight.setSelected(false);
            mTvFriendsHeadOther.setSelected(false);
            mTvFriendsHeadLeftLine.setSelected(true);
            mTvFriendsHeadRightLine.setSelected(false);
            mTvFriendsHeadOtherLine.setSelected(false);
            mVpFriends.setCurrentItem(0,false);

        }else if (id == R.id.tv_friends_head_right){
            mTvFriendsHeadLeft.setSelected(false);
            mTvFriendsHeadRight.setSelected(true);
            mTvFriendsHeadOther.setSelected(false);
            mTvFriendsHeadLeftLine.setSelected(false);
            mTvFriendsHeadRightLine.setSelected(false);
            mTvFriendsHeadOtherLine.setSelected(false);
            mVpFriends.setCurrentItem(1,false);
        }else if (id == R.id.tv_friends_head_other){
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", Qurl.HOST + "/appH/html/business_conduct.html")
                    .withString(Constant.TITLE, "觅觅学院")
                    .withInt(Constant.TYPE, 4)
                    .navigation();
//            mTvFriendsHeadLeft.setSelected(false);
//            mTvFriendsHeadRight.setSelected(false);
//            mTvFriendsHeadOther.setSelected(true);
//            mTvFriendsHeadLeftLine.setSelected(false);
//            mTvFriendsHeadRightLine.setSelected(false);
//            mTvFriendsHeadOtherLine.setSelected(true);
//            mVpFriends.setCurrentItem(2,true);
        }
    }
}
