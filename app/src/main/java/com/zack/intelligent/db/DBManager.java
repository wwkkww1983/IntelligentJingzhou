package com.zack.intelligent.db;

import android.database.sqlite.SQLiteDatabase;

import com.zack.intelligent.App;
import com.zack.intelligent.bean.AlarmLog;
import com.zack.intelligent.db.gen.AlarmLogDao;
import com.zack.intelligent.db.gen.DaoMaster;
import com.zack.intelligent.db.gen.DaoSession;
import com.zack.intelligent.db.gen.NormalOperLogDao;
import com.zack.intelligent.db.gen.OperGunsLogDao;

/**
 *
 */

public class DBManager {

    private static final String TAG = "DBManager";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private static final String DB_NAME = "intelligent";
    private AlarmLogDao alarmLogDao;
    private OperGunsLogDao operGunsLogDao;
    private NormalOperLogDao normalOperLogDao;

    //    private GunsBeanDao gunsBeanDao;
    private DBManager() {
    }

    public static DBManager getInstance() {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(App.getContext(), DB_NAME, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(App.getContext(), DB_NAME, null);
        }
        return openHelper.getWritableDatabase();
    }

    private DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            daoMaster = new DaoMaster(getWritableDatabase());
        }
        return daoMaster;
    }

    private DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public AlarmLogDao getAlarmLogDao() {
        if (alarmLogDao == null) {
            alarmLogDao = getDaoSession().getAlarmLogDao();
        }
        return alarmLogDao;
    }

    public int insertAlarmLog(AlarmLog alarmLog){
      return(int) getAlarmLogDao().insert(alarmLog);
    }

    public OperGunsLogDao getOperGunsLogDao() {
        if (operGunsLogDao == null) {
            operGunsLogDao = getDaoSession().getOperGunsLogDao();
        }
        return operGunsLogDao;
    }

    public NormalOperLogDao getNormalOperLogDao() {
        if (normalOperLogDao == null) {
            if (daoSession == null) {
                daoSession = getDaoSession();
            }
            normalOperLogDao = getDaoSession().getNormalOperLogDao();
        }
        return normalOperLogDao;
    }

}
