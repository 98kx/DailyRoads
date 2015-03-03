package com.konka.dialyroads.dao;

import com.konka.dialyroads.util.Assist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDBHelper extends SQLiteOpenHelper {

	private static final CursorFactory factory = null;

	private static final int version = 1;

	public BaseDBHelper(Context context) {
		super(context, "konka_dailyroads.db", factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Assist.TABLENAME_VIDEO + " (_id integer primary key ,starttime,endtime,path,name,thumbpath,size,showName,resolution_ratio,onuploadsuccess,oncrash,foreversave)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Assist.TABLENAME_IMAGE + " (_id integer primary key ,photo_date,path,name,thumbpath,size,showName,resolution_ratio,onuploadsuccess)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void dellAll() {
		// this.getWritableDatabase().delete(tablename, null, null);
		this.getWritableDatabase().close();
	}

}
