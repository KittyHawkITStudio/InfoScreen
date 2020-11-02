package com.myapp.appbase.func;

import android.content.Context;

import com.myapp.appbase.database.RecordManager;
import com.myapp.appbase.entity.Record;

/** 操作数据库 */
public class FnDatabase {

	public static Object getAll(Context c, Class type) {
		if(type == Record.class)			{ return RecordManager.get(c).getAll(); }
		/* 其他类，依此类推
		if(type == Operator.class)		{ return OperatorManager.get(c).getAll(); }
		*/
		return null;
	}

	public static Object get(Context c, Class type, long id) {
		if(type == Record.class)			{ return RecordManager.get(c).get(id); }
		/* 其他类，依此类推
		if(type == Operator.class)		{ return OperatorManager.get(c).get(id); }
		*/
		return null;
	}

	public static void insert(Context c, Class type, Object obj) {
		if(type == Record.class)			{ RecordManager.get(c).insert((Record)obj); }
		/* 其他类，依此类推
		if(type == Operator.class)		{ OperatorManager.get(c).insert((Operator)obj); }
		*/
	}

	public static void delete(Context c, Class type, Object obj) {
		if(type == Record.class)			{ RecordManager.get(c).delete((Record)obj); }
		/* 其他类，依此类推
		if(type == Operator.class)		{ OperatorManager.get(c).delete((Operator)obj); }
		*/
	}

	public static void update(Context c, Class type, Object obj) {
		if(type == Record.class)			{ RecordManager.get(c).update((Record)obj); }
		/* 其他类，依此类推
		if(type == Operator.class)		{ OperatorManager.get(c).update((Operator)obj); }
		*/
	}

}
