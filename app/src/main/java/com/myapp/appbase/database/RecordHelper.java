package com.myapp.appbase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.appbase.config.DT;
import com.myapp.appbase.entity.Record;

public class RecordHelper extends SQLiteOpenHelper {
	
	private static final int VERSION = DBHelper.VERSION;
	private static final String DB = DT.DB_NAME;
	private static final String TABLE = "t_record";
		
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_TIME = "time";
	private static final String COLUMN_WD = "wd";
	private static final String COLUMN_SD = "sd";

	public RecordCursor queryAll() {
		Cursor wrapped = getReadableDatabase().query(
				TABLE,
				null, // All columns
				null, // Look for an Type
				null, // with this value
				null, // group by
				null, // having
				null // order by
				); 
		return new RecordCursor(wrapped);
	}
	
	public RecordCursor query(long id) {
		Cursor wrapped = getReadableDatabase().query(
				TABLE,
				null, // All columns
				COLUMN_ID + " = ?", // Look for an Type
				new String[]{ String.valueOf(id) }, // with this value
				null, // group by
				null, // having
				null, // order by
				"1" //limit 1
				); 
		return new RecordCursor(wrapped);
	}
	
	public RecordCursor query(String time) {
		Cursor wrapped = getReadableDatabase().query(
				TABLE,
				null, // All columns
				COLUMN_TIME + " like %?%", // Look for an Type
				new String[]{ time }, // with this value
				null, // group by
				null, // having
				null, // order by
				"1" //limit 1
				); 
		return new RecordCursor(wrapped);
	}

	public RecordCursor querySome(QueryRecordFilter filter) {

		String filters = "";
		String[] values;
		filters = COLUMN_TIME + " >= ? and " + COLUMN_TIME + " <= ?";
		values = new String[]{ filter.getStartDate(), filter.getEndDate() };

		Cursor wrapped = getReadableDatabase().query(
				TABLE,
				null, // All columns
				filters, // Look for an Type
				values, // with this value
				null, // group by
				null, // having
				COLUMN_TIME + " desc "// order by
		);
		return new RecordCursor(wrapped);
	}

	public RecordCursor queryByMonth(String month) {

		Cursor wrapped = getReadableDatabase().query(
				TABLE,
				null, // All columns
				//COLUMN_TIME + " like '%?%'", // Look for an Type
                COLUMN_TIME + " like ?", // Look for an Type
				 new String[]{ "%"+month+"%" }, // with this value
				null, // group by
				null, // having
				COLUMN_TIME + " desc "// order by
		);
		return new RecordCursor(wrapped);
	}

	public RecordCursor queryByTime(String time) {

		Cursor wrapped = getReadableDatabase().query(
				TABLE,
				null, // All columns
				//COLUMN_TIME + " like '%?%'", // Look for an Type
				COLUMN_TIME + " like ?", // Look for an Type
				new String[]{ "%"+time+"%" }, // with this value
				null, // group by
				null, // having
				COLUMN_TIME + " desc ",// order by
				"1" //limit 1
		);
		return new RecordCursor(wrapped);
	}
	
	public static class RecordCursor extends CursorWrapper {
		public RecordCursor(Cursor c) {
			super(c);
		}
		
		public Record getRecord() {
			if (isBeforeFirst() || isAfterLast()) { return null; }
			Record item = new Record();
			item.setId(getLong(getColumnIndex(COLUMN_ID)));
			item.setTime(getString(getColumnIndex(COLUMN_TIME)));
			item.setWd(getDouble(getColumnIndex(COLUMN_WD)));
			item.setSd(getDouble(getColumnIndex(COLUMN_SD)));
			return item;
		}
		
	}
	
	public RecordHelper(Context context) {
		super(context, DB, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	public long insert(Record item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TIME, item.getTime());
		cv.put(COLUMN_WD, item.getWd());
		cv.put(COLUMN_SD, item.getSd());
		return getWritableDatabase().insert(TABLE, null, cv);
	}
	
	public void update(Record item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TIME, item.getTime());
		cv.put(COLUMN_WD, item.getWd());
		cv.put(COLUMN_SD, item.getSd());
		getWritableDatabase().update(TABLE, cv, COLUMN_ID + " = ?", new String[]{ String.valueOf(item.getId()) });
	}
	
	public void delete(long id) {
		getWritableDatabase().delete(TABLE, COLUMN_ID + " = ?", new String[]{ String.valueOf(id) });
	}

	public void deleteAll() {
		String sql = "delete from " + TABLE;
		getWritableDatabase().execSQL(sql);
	}
	
}
