package com.common.app.database.manger;


import com.common.app.database.UserInfoBeanDao;

/**
 * @author: zhengjr
 * @since: 2018/6/21
 * @describe:
 */

public class EntityDaoManager {

    private static class LazyHolder{
        private static EntityDaoManager INSTANCE = new EntityDaoManager();
    }

    private EntityDaoManager(){}

    public static EntityDaoManager getInstance(){
        return LazyHolder.INSTANCE;
    }


    /**
     * 得到UserInfoBean的数据库操作
     * @return
     */
    public UserInfoBeanDao getUserInfoBeanDao(){
        return GreenDaoManger.getInstance().getDaoSession().getUserInfoBeanDao();
    }


}
