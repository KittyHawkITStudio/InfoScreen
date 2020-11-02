package com.myapp.appbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.appbase.config.DT;

public class DBHelper extends SQLiteOpenHelper {
	public static final int VERSION = 1;
	public static final String DB = DT.DB_NAME;
	private Context context;
	private int Nversion;

	public DBHelper(Context context, int version) {
		super(context, DB, null, version);
		this.context = context;
		this.Nversion = version;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		onUpgrade(db, 1, Nversion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

}
