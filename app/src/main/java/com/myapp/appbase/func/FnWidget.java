package com.myapp.appbase.func;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.myapp.appbase.widget.SpinnerOption;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/** 界面元素相关配置 */
public class FnWidget {

	/** 隐藏导航栏 */
	public void hideBar(Context context){
		/*
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		*/

	}

	/** 展示导航栏 */
	public void showBar(Context context){
		/*
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
		decorView.setSystemUiVisibility(uiOptions);
		*/
	}

	/** 加载Spinner列表 */
	public static void initSpinner(Spinner sp, ArrayList<SpinnerOption> options,
                            Context context) {
		ArrayAdapter<SpinnerOption> adapter = new ArrayAdapter<SpinnerOption>(
				context, android.R.layout.simple_spinner_item, options);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) { };
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}

	/** 根据value, 设置spinner默认选中 */
	public static void matchSpinner(Spinner spinner, String str, int matchMode) {
		SpinnerAdapter adapter = spinner.getAdapter();
		int k = adapter.getCount();
		for (int i = 0; i < k; i++) {
			boolean isMatch = false;
			switch (matchMode) {
				case SpinnerOption.MODE_VALUE:
					isMatch = ((SpinnerOption) adapter.getItem(i)).getValue().equals(str);
					break;
				case SpinnerOption.MODE_TEXT:
					isMatch = ((SpinnerOption) adapter.getItem(i)).getText().equals(str);
					break;
			}
			Log.d(((SpinnerOption) adapter.getItem(i)).getValue(), isMatch + "");
			if (isMatch) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	public static void clearSpinner(Spinner sp) {
		sp.setAdapter(null);
	}

	/** 初始化日期选择框 */
	public static void initDatePicker(DatePicker datepicker, DatePicker.OnDateChangedListener listener) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		datepicker.init(year, month, day, listener);
	}

	/** 设置日期选择框的日期 */
	public static void setDatePicker(DatePicker datepicker, Date mDate, DatePicker.OnDateChangedListener listener){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		datepicker.init(year, month, day, listener);
	}

}
