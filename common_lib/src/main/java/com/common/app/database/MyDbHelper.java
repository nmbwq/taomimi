package com.common.app.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.common.BuildConfig;
import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

/**
 * @author: zhengjr
 * @since: 2018/6/21
 * @describe:
 */

public class MyDbHelper extends DaoMaster.OpenHelper {

    private static final String DBNAME = BuildConfig.DBNAME;

    public MyDbHelper(Context context) {
        super(context, DBNAME);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
            //注意此处的参数StudentDao.class，很重要（一开始没注意，给坑了一下），它就是需要升级的table的Dao,
            //不填的话数据丢失，
            // 这里可以放多个Dao.class，也就是可以做到很多table的安全升级，Good~
        }, UserInfoBeanDao.class);
    }
}
