package com.zack.intelligent.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.zack.intelligent.bean.AlarmLog;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ALARM_LOG".
*/
public class AlarmLogDao extends AbstractDao<AlarmLog, Long> {

    public static final String TABLENAME = "ALARM_LOG";

    /**
     * Properties of entity AlarmLog.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Id = new Property(1, String.class, "id", false, "ID");
        public final static Property RoomId = new Property(2, String.class, "roomId", false, "ROOM_ID");
        public final static Property RoomName = new Property(3, String.class, "roomName", false, "ROOM_NAME");
        public final static Property CabId = new Property(4, String.class, "cabId", false, "CAB_ID");
        public final static Property CabNo = new Property(5, String.class, "cabNo", false, "CAB_NO");
        public final static Property LogSubType = new Property(6, int.class, "logSubType", false, "LOG_SUB_TYPE");
        public final static Property ManageId = new Property(7, String.class, "manageId", false, "MANAGE_ID");
        public final static Property ManageId2 = new Property(8, String.class, "manageId2", false, "MANAGE_ID2");
        public final static Property LeadId = new Property(9, String.class, "leadId", false, "LEAD_ID");
        public final static Property LogTime = new Property(10, long.class, "logTime", false, "LOG_TIME");
        public final static Property LogStatus = new Property(11, int.class, "logStatus", false, "LOG_STATUS");
        public final static Property DisPoliceId = new Property(12, String.class, "disPoliceId", false, "DIS_POLICE_ID");
        public final static Property DisPoliceName = new Property(13, String.class, "disPoliceName", false, "DIS_POLICE_NAME");
        public final static Property RelieveTime = new Property(14, String.class, "relieveTime", false, "RELIEVE_TIME");
        public final static Property LogContent = new Property(15, String.class, "logContent", false, "LOG_CONTENT");
        public final static Property AddTime = new Property(16, long.class, "addTime", false, "ADD_TIME");
        public final static Property IsSync = new Property(17, boolean.class, "isSync", false, "IS_SYNC");
        public final static Property Tag = new Property(18, String.class, "tag", false, "TAG");
    }


    public AlarmLogDao(DaoConfig config) {
        super(config);
    }
    
    public AlarmLogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ALARM_LOG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"ID\" TEXT," + // 1: id
                "\"ROOM_ID\" TEXT," + // 2: roomId
                "\"ROOM_NAME\" TEXT," + // 3: roomName
                "\"CAB_ID\" TEXT," + // 4: cabId
                "\"CAB_NO\" TEXT," + // 5: cabNo
                "\"LOG_SUB_TYPE\" INTEGER NOT NULL ," + // 6: logSubType
                "\"MANAGE_ID\" TEXT," + // 7: manageId
                "\"MANAGE_ID2\" TEXT," + // 8: manageId2
                "\"LEAD_ID\" TEXT," + // 9: leadId
                "\"LOG_TIME\" INTEGER NOT NULL ," + // 10: logTime
                "\"LOG_STATUS\" INTEGER NOT NULL ," + // 11: logStatus
                "\"DIS_POLICE_ID\" TEXT," + // 12: disPoliceId
                "\"DIS_POLICE_NAME\" TEXT," + // 13: disPoliceName
                "\"RELIEVE_TIME\" TEXT," + // 14: relieveTime
                "\"LOG_CONTENT\" TEXT," + // 15: logContent
                "\"ADD_TIME\" INTEGER NOT NULL ," + // 16: addTime
                "\"IS_SYNC\" INTEGER NOT NULL ," + // 17: isSync
                "\"TAG\" TEXT);"); // 18: tag
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ALARM_LOG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AlarmLog entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(2, id);
        }
 
        String roomId = entity.getRoomId();
        if (roomId != null) {
            stmt.bindString(3, roomId);
        }
 
        String roomName = entity.getRoomName();
        if (roomName != null) {
            stmt.bindString(4, roomName);
        }
 
        String cabId = entity.getCabId();
        if (cabId != null) {
            stmt.bindString(5, cabId);
        }
 
        String cabNo = entity.getCabNo();
        if (cabNo != null) {
            stmt.bindString(6, cabNo);
        }
        stmt.bindLong(7, entity.getLogSubType());
 
        String manageId = entity.getManageId();
        if (manageId != null) {
            stmt.bindString(8, manageId);
        }
 
        String manageId2 = entity.getManageId2();
        if (manageId2 != null) {
            stmt.bindString(9, manageId2);
        }
 
        String leadId = entity.getLeadId();
        if (leadId != null) {
            stmt.bindString(10, leadId);
        }
        stmt.bindLong(11, entity.getLogTime());
        stmt.bindLong(12, entity.getLogStatus());
 
        String disPoliceId = entity.getDisPoliceId();
        if (disPoliceId != null) {
            stmt.bindString(13, disPoliceId);
        }
 
        String disPoliceName = entity.getDisPoliceName();
        if (disPoliceName != null) {
            stmt.bindString(14, disPoliceName);
        }
 
        String relieveTime = entity.getRelieveTime();
        if (relieveTime != null) {
            stmt.bindString(15, relieveTime);
        }
 
        String logContent = entity.getLogContent();
        if (logContent != null) {
            stmt.bindString(16, logContent);
        }
        stmt.bindLong(17, entity.getAddTime());
        stmt.bindLong(18, entity.getIsSync() ? 1L: 0L);
 
        String tag = entity.getTag();
        if (tag != null) {
            stmt.bindString(19, tag);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AlarmLog entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(2, id);
        }
 
        String roomId = entity.getRoomId();
        if (roomId != null) {
            stmt.bindString(3, roomId);
        }
 
        String roomName = entity.getRoomName();
        if (roomName != null) {
            stmt.bindString(4, roomName);
        }
 
        String cabId = entity.getCabId();
        if (cabId != null) {
            stmt.bindString(5, cabId);
        }
 
        String cabNo = entity.getCabNo();
        if (cabNo != null) {
            stmt.bindString(6, cabNo);
        }
        stmt.bindLong(7, entity.getLogSubType());
 
        String manageId = entity.getManageId();
        if (manageId != null) {
            stmt.bindString(8, manageId);
        }
 
        String manageId2 = entity.getManageId2();
        if (manageId2 != null) {
            stmt.bindString(9, manageId2);
        }
 
        String leadId = entity.getLeadId();
        if (leadId != null) {
            stmt.bindString(10, leadId);
        }
        stmt.bindLong(11, entity.getLogTime());
        stmt.bindLong(12, entity.getLogStatus());
 
        String disPoliceId = entity.getDisPoliceId();
        if (disPoliceId != null) {
            stmt.bindString(13, disPoliceId);
        }
 
        String disPoliceName = entity.getDisPoliceName();
        if (disPoliceName != null) {
            stmt.bindString(14, disPoliceName);
        }
 
        String relieveTime = entity.getRelieveTime();
        if (relieveTime != null) {
            stmt.bindString(15, relieveTime);
        }
 
        String logContent = entity.getLogContent();
        if (logContent != null) {
            stmt.bindString(16, logContent);
        }
        stmt.bindLong(17, entity.getAddTime());
        stmt.bindLong(18, entity.getIsSync() ? 1L: 0L);
 
        String tag = entity.getTag();
        if (tag != null) {
            stmt.bindString(19, tag);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AlarmLog readEntity(Cursor cursor, int offset) {
        AlarmLog entity = new AlarmLog( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // roomId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // roomName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // cabId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // cabNo
            cursor.getInt(offset + 6), // logSubType
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // manageId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // manageId2
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // leadId
            cursor.getLong(offset + 10), // logTime
            cursor.getInt(offset + 11), // logStatus
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // disPoliceId
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // disPoliceName
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // relieveTime
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // logContent
            cursor.getLong(offset + 16), // addTime
            cursor.getShort(offset + 17) != 0, // isSync
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18) // tag
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AlarmLog entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRoomId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRoomName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCabId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCabNo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLogSubType(cursor.getInt(offset + 6));
        entity.setManageId(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setManageId2(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLeadId(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLogTime(cursor.getLong(offset + 10));
        entity.setLogStatus(cursor.getInt(offset + 11));
        entity.setDisPoliceId(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setDisPoliceName(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setRelieveTime(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setLogContent(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setAddTime(cursor.getLong(offset + 16));
        entity.setIsSync(cursor.getShort(offset + 17) != 0);
        entity.setTag(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AlarmLog entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AlarmLog entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AlarmLog entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
