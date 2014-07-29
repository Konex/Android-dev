package com.yini.daos;

import java.sql.SQLException;
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yini.R;
import com.yini.models.licensingcentre.Pod;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DbHelper extends OrmLiteSqliteOpenHelper {
	// TODO: replace with your own database name
    private static final String DATABASE_NAME = "yini.db";
    private static final int DATABASE_VERSION = 1;
    @SuppressWarnings("rawtypes")
	private HashMap maps;
    
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DbHelper.class.getName(), "onCreate");
            
            TableUtils.createTable(connectionSource, Pod.class);
            
        } catch (Exception e) {
	    Log.e(DbHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }   
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        try {
            Log.i(DbHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Pod.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DbHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close() {
        super.close();
        maps = null;
    }
    
    @SuppressWarnings("unchecked")
    public Object getRuntimeDao(Class<?> type) {
        if (maps == null) maps = new HashMap<String, Object>();
		
	if (!maps.containsKey(type.getSimpleName())) {
		Object obj = getRuntimeExceptionDao(type);
		maps.put(type.getSimpleName(), obj);
	}
		
	return maps.get(type.getSimpleName());
    }
}
