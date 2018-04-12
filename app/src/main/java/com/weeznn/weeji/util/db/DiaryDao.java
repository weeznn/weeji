package com.weeznn.weeji.util.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.weeznn.weeji.util.db.entry.Diary;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DIARY".
*/
public class DiaryDao extends AbstractDao<Diary, Long> {

    public static final String TABLENAME = "DIARY";

    /**
     * Properties of entity Diary.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _DAIID = new Property(0, long.class, "_DAIID", true, "_id");
        public final static Property Date = new Property(1, String.class, "date", false, "DATE");
        public final static Property Address = new Property(2, String.class, "address", false, "ADDRESS");
        public final static Property Mood = new Property(3, int.class, "mood", false, "MOOD");
        public final static Property Image = new Property(4, String.class, "image", false, "IMAGE");
    }


    public DiaryDao(DaoConfig config) {
        super(config);
    }
    
    public DiaryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DIARY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: _DAIID
                "\"DATE\" TEXT," + // 1: date
                "\"ADDRESS\" TEXT," + // 2: address
                "\"MOOD\" INTEGER NOT NULL ," + // 3: mood
                "\"IMAGE\" TEXT);"); // 4: image
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DIARY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Diary entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.get_DAIID());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
        stmt.bindLong(4, entity.getMood());
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(5, image);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Diary entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.get_DAIID());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
        stmt.bindLong(4, entity.getMood());
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(5, image);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Diary readEntity(Cursor cursor, int offset) {
        Diary entity = new Diary( //
            cursor.getLong(offset + 0), // _DAIID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // date
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // address
            cursor.getInt(offset + 3), // mood
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // image
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Diary entity, int offset) {
        entity.set_DAIID(cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMood(cursor.getInt(offset + 3));
        entity.setImage(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Diary entity, long rowId) {
        entity.set_DAIID(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Diary entity) {
        if(entity != null) {
            return entity.get_DAIID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Diary entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
