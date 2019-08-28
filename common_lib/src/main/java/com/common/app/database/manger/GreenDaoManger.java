package com.common.app.database.manger;

import com.common.app.base.BaseApplication;
import com.common.app.database.DaoMaster;
import com.common.app.database.DaoSession;
import com.common.app.database.MyDbHelper;

/**
 * @author: zhengjr
 * @since: 2018/6/21
 * @describe:
 */

public class GreenDaoManger {

    private DaoSession mDaoSession;

    private static class LazyHolder{
        private static GreenDaoManger INSTANCE = new GreenDaoManger();

    }

    private GreenDaoManger (){
        //数据库的初始化
        MyDbHelper myDbHelper = new MyDbHelper(BaseApplication.getApplication());
        DaoMaster daoMaster = new DaoMaster(myDbHelper.getWritableDb());
        mDaoSession = daoMaster.newSession();
    }


    public static GreenDaoManger getInstance(){
        return LazyHolder.INSTANCE;
    }

    public DaoSession getDaoSession(){
        if (mDaoSession != null){
            getInstance();
        }
        return mDaoSession;
    }



}
