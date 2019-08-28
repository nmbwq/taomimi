package com.common.app.database.manger;


import android.support.annotation.NonNull;

import com.common.app.base.BaseApplication;
import com.common.app.database.UserInfoBeanDao;
import com.common.app.database.bean.UserInfoBean;
import com.common.app.utils.SPUtils;

import java.util.List;

/**
 * 作者：尹松涛
 * 邮箱：yinsongtaoshmily@outlook.com
 * 日期：2017/10/21
 * 时间：15:01
 */

public class UserHelper {

    private static class LazyUserHelper {
        private static UserHelper userHelper = new UserHelper();
    }

    public static UserHelper getInstence() {
        return LazyUserHelper.userHelper;
    }

    private UserHelper() {
    }


    /**
     * 保存用户信息
     *
     * @param userInfoBean
     */
    public void saveUserInfo(@NonNull UserInfoBean userInfoBean) {

        if (queryUserInfo(userInfoBean.phone)) {//如果有用户信息，将更新用户信息
            updataUserInfo(userInfoBean);
        } else {//否则的话插入用户信息
            insertUserInfo(userInfoBean);
        }
    }

    /**
     * 删除用户信息
     *
     * @param userInfoBean
     */
    public void deleteUserInfo(@NonNull UserInfoBean userInfoBean) {
        if (queryUserInfo(userInfoBean.phone)) {//如果有用户信息，将删除用户信息
            UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
            userInfoBeanDao.delete(userInfoBean);
        }
    }

    /**
     * 清空用户表
     */
    public void deleteAllUserInfo() {
        UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
        List<UserInfoBean> list = userInfoBeanDao.queryBuilder().list();
        if (list.size() > 0) {
            //删除所有用户
            for (int i = 0; i < list.size(); i++) {
                userInfoBeanDao.delete(list.get(i));
            }
        }
        //删除红包的默认值
        SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isRedPackageSwitch",false);
    }

    /**
     * app 的设计是用户表存只存在一个用户，没有用户则表示没任何登录，大于一个用户则表示数据表存放混乱，需要清空检查
     *
     * @return
     */
    public boolean isLogin() {
        UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
        List<UserInfoBean> list = userInfoBeanDao.queryBuilder().list();
        if (list.size() <= 0 || list.size() > 1) {
            //数据错乱，清空用户数据表
            deleteAllUserInfo();
            return false;
        }
        return list.get(0).isLogin;
    }

    /**
     * app 的设计是用户表存只存在一个用户，没有用户则表示没任何登录，大于一个用户则表示数据表存放混乱，需要清空检查
     *
     * @return
     */
    public UserInfoBean getUserInfo() {
        UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
        List<UserInfoBean> list = userInfoBeanDao.queryBuilder().list();
        if (list.size() <= 0 || list.size() > 1) {

            //用户表错乱，删除所有用户，待检测
            for (int i = 0; i < list.size(); i++) {
                userInfoBeanDao.delete(list.get(i));
            }
            return new UserInfoBean();
        }
        return list.get(0);
    }


    /**
     * 根据手机号字段查询是否存在改用户
     *
     * @param phone
     * @return
     */
    private boolean queryUserInfo(@NonNull String phone) {
        UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
        UserInfoBean unique = userInfoBeanDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).unique();
        return unique != null;//返回查询结果
    }

    /**
     * 插入一个用户
     *
     * @param userInfoBean
     */
    private void insertUserInfo(@NonNull UserInfoBean userInfoBean) {
        UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
        userInfoBeanDao.insert(userInfoBean);
    }

    /**
     * 更新一个用户
     *
     * @param userInfoBean
     */
    private void updataUserInfo(@NonNull UserInfoBean userInfoBean) {
        UserInfoBeanDao userInfoBeanDao = EntityDaoManager.getInstance().getUserInfoBeanDao();
        userInfoBeanDao.update(userInfoBean);
    }

    public UserInfoBeanDao getUserInfoDao(){
        return EntityDaoManager.getInstance().getUserInfoBeanDao();
    }



    /*public static UserInfo getUserInfo(Context context) {
        return userInfo;
    }


    public static void clearUserToken(Context context) {
    }

    public static boolean isLogin() {
    }

    public static String getToken() {
        if (token == null)
            return "";
        return token;
    }*/
}
