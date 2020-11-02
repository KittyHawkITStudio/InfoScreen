package com.myapp.appbase.func;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.myapp.appbase.config.DT;
import com.myapp.appbase.database.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 一些任务性质的操作，比如备份，单独拿出来，供其他模块调用 */
public class FnTask {
	public void updateDatabase(Context context){
		new DBHelper(context, DBHelper.VERSION).getWritableDatabase();
	}

	/** 初始化数据库 */
	public static void initDatabase(Context context) {

		String DB_PATH = DT.DB_PATH;
		String DB_NAME = DT.DB_NAME;

		if ((new File(DB_PATH + DB_NAME)).exists() == false) {
			File f = new File(DB_PATH);
			if (!f.exists()) {
				f.mkdir();
			}

			try {
				InputStream is = context.getAssets().open(DB_NAME);
				OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);

				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}

				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
