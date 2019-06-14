package com.zack.intelligent.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.zack.intelligent.bean.NormalOperLog;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NORMAL_OPER_LOG".
*/
public class NormalOperLogDao extends AbstractDao<NormalOperLog, Long> {

    public static final String TABLENAME = "NORMAL_OPER_LOG";

    /**
     * Properties of entity NormalOperLog.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Id = new Property(1, String.class, "id", false, "ID");
        public final static Property RoomId = new Property(2, String.class, "roomId", false, "ROOM_ID");
        public final static Property RoomName = new Property(3, String.class, "roomName", false, "ROOM_NAME");
        public final static Property CabId = new Property(4, String.class, "cabId", false, "CAB_ID");
        public final static Property CabNo = new Property(5, String.class, "cabNo", false, "CAB_NO");
        public final static Property PoliceId = new Property(6, String.class, "policeId", false, "POLICE_ID");
        public final static Property PoliceName = new Property(7, String.class, "policeName", false, "POLICE_NAME");
        public final static Property LogType = new Property(8, int.class, "logType", false, "LOG_TYPE");
        public final static Property PoliceType = new Property(9, int.class, "policeType", false, "POLICE_TYPE");
        public final static Property OperTaskType = new Property(10, int.class, "operTaskType", false, "OPER_TASK_TYPE");
        public final static Property LogSubType = new Property(11, int.class, "logSubType", false, "LOG_SUB_TYPE");
        public final static Property ManageId = new Property(12, String.class, "manageId", false, "MANAGE_ID");
        public final static Property LeadId = new Property(13, String.class, "leadId", false, "LEAD_ID");
        public final static Property LogContent = new Property(14, String.class, "logContent", false, "LOG_CONTENT");
        public final static Property AddTime = new Property(15, long.class, "addTime", false, "ADD_TIME");
        public final static Property IsSync = new Property(16, boolean.class, "isSync", false, "IS_SYNC");
    }


    public NormalOperLogDao(DaoConfig config) {
        super(config);
    }
    
    public NormalOperLogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NORMAL_OPER_LOG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"ID\" TEXT," + // 1: id
                "\"ROOM_ID\" TEXT," + // 2: roomId
                "\"ROOM_NAME\" TEXT," + // 3: roomName
                "\"CAB_ID\" TEXT," + // 4: cabId
                "\"CAB_NO\" TEXT," + // 5: cabNo
                "\"POLICE_ID\" TEXT," + // 6: policeId
                "\"POLICE_NAME\" TEXT," + // 7: policeName
                "\"LOG_TYPE\" INTEGER NOT NULL ," + // 8: logType
                "\"POLICE_TYPE\" INTEGER NOT NULL ," + // 9: policeType
                "\"OPER_TASK_TYPE\" INTEGER NOT NULL ," + // 10: operTaskType
                "\"LOG_SUB_TYPE\" INTEGER NOT NULL ," + // 11: logSubType
                "\"MANAGE_ID\" TEXT," + // 12: manageId
                "\"LEAD_ID\" TEXT," + // 13: leadId
                "\"LOG_CONTENT\" TEXT," + // 14: logContent
                "\"ADD_TIME\" INTEGER NOT NULL ," + // 15: addTime
                "\"IS_SYNC\" INTEGER NOT NULL );"); // 16: isSync
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NORMAL_OPER_LOG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NormalOperLog entity) {
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
 
        String policeId = entity.getPoliceId();
        if (policeId != null) {
            stmt.bindString(7, policeId);
        }
 
        String policeName = entity.getPoliceName();
        if (policeName != null) {
            stmt.bindString(8, policeName);
        }
        stmt.bindLong(9, entity.getLogType());
        stmt.bindLong(10, entity.getPoliceType());
        stmt.bindLong(11, entity.getOperTaskType());
        stmt.bindLong(12, entity.getLogSubType());
 
        String manageId = entity.getManageId();
        if (manageId != null) {
            stmt.bindString(13, manageId);
        }
 
        String leadId = entity.getLeadId();
        if (leadId != null) {
            stmt.bindString(14, leadId);
        }
 
        String logContent = entity.getLogContent();
        if (logContent != null) {
            stmt.bindString(15, logContent);
        }
        stmt.bindLong(16, entity.getAddTime());
        stmt.bindLong(17, entity.getIsSync() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NormalOperLog entity) {
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
 
        String policeId = entity.getPoliceId();
        if (policeId != null) {
            stmt.bindString(7, policeId);
        }
 
        String policeName = entity.getPoliceName();
        if (policeName != null) {
            stmt.bindString(8, policeName);
        }
        stmt.bindLong(9, entity.getLogType());
        stmt.bindLong(10, entity.getPoliceType());
        stmt.bindLong(11, entity.getOperTaskType());
        stmt.bindLong(12, entity.getLogSubType());
 
        String manageId = entity.getManageId();
        if (manageId != null) {
            stmt.bindString(13, manageId);
        }
 
        String leadId = entity.getLeadId();
        if (leadId != null) {
            stmt.bindString(14, leadId);
        }
 
        String logContent = entity.getLogContent();
        if (logContent != null) {
            stmt.bindString(15, logContent);
        }
        stmt.bindLong(16, entity.getAddTime());
        stmt.bindLong(17, entity.getIsSync() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NormalOperLog readEntity(Cursor cursor, int offset) {
        NormalOperLog entity = new NormalOperLog( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // roomId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // roomName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // cabId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // cabNo
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // policeId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // policeName
            cursor.getInt(offset + 8), // logType
            cursor.getInt(offset + 9), // policeType
            cursor.getInt(offset + 10), // operTaskType
            cursor.getInt(offset + 11), // logSubType
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // manageId
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // leadId
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // logContent
            cursor.getLong(offset + 15), // addTime
            cursor.getShort(offset + 16) != 0 // isSync
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NormalOperLog entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRoomId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRoomName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCabId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCabNo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPoliceId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPoliceName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLogType(cursor.getInt(offset + 8));
        entity.setPoliceType(cursor.getInt(offset + 9));
        entity.setOperTaskType(cursor.getInt(offset + 10));
        entity.setLogSubType(cursor.getInt(offset + 11));
        entity.setManageId(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setLeadId(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setLogContent(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setAddTime(cursor.getLong(offset + 15));
        entity.setIsSync(cursor.getShort(offset + 16) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NormalOperLog entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NormalOperLog entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NormalOperLog entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
