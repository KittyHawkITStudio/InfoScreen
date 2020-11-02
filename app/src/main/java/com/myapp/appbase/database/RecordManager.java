package com.myapp.appbase.database;

import android.content.Context;

import com.myapp.appbase.entity.Record;

import java.util.ArrayList;

public class RecordManager {
	private static RecordManager manager;
	private Context context;
	private RecordHelper helper;
	
	private RecordManager(Context appContext) {
		context = appContext;
		helper = new RecordHelper(context);
	}
	
	public static RecordManager get(Context c) {
		if (manager == null) {
			manager = new RecordManager(c.getApplicationContext());
		}
		return manager;
	}

	public ArrayList<Record> getAll() {
		ArrayList<Record> objs = new ArrayList<Record>();
		RecordHelper.RecordCursor cursor = helper.queryAll();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			objs.add(cursor.getRecord());
			cursor.moveToNext();
		}
		cursor.close();
		return objs;
	}

	public ArrayList<Record> getSome(QueryRecordFilter filter) {
		ArrayList<Record> objs = new ArrayList<Record>();
		RecordHelper.RecordCursor cursor = helper.querySome(filter);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			objs.add(cursor.getRecord());
			cursor.moveToNext();
		}
		cursor.close();
		return objs;
	}

	public ArrayList<Record> getByMonth(String month) {
		ArrayList<Record> objs = new ArrayList<Record>();
		RecordHelper.RecordCursor cursor = helper.queryByMonth(month);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			objs.add(cursor.getRecord());
			cursor.moveToNext();
		}
		cursor.close();
		return objs;
	}

	public Record getByTime(String time) {
		Record record = null;
		RecordHelper.RecordCursor cursor = helper.queryByTime(time);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			record = cursor.getRecord();
		}
		cursor.close();
		return record;
	}

	public Record get(long id) {
		Record record = null;
		RecordHelper.RecordCursor cursor = helper.query(id);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			record = cursor.getRecord();
		}
		cursor.close();
		return record;
	}
	
	public Record get(String name) {
		Record record = null;
		RecordHelper.RecordCursor cursor = helper.query(name);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			record = cursor.getRecord();
		}
		cursor.close();
		return record;
	}
	
	public long insert(Record record){
		return helper.insert(record);
	}
	
	public void update(Record record){
		helper.update(record);
	}
	
	public void delete(Record record) {
		helper.delete(record.getId());
	}

	public void deleteAll() {
		helper.deleteAll();
	}
}
