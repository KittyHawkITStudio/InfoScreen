package com.myapp.appbase.func;

import android.content.Context;
import android.widget.Spinner;

import com.myapp.appbase.config.DT;
import com.myapp.appbase.entity.User;
import com.myapp.appbase.widget.SpinnerOption;

import java.util.ArrayList;

/** 操作配置 */
public class FnString {

	public static String fillZero(int number, int digits) {
		String numberS = String.valueOf(number);
		int length = numberS.length();
		if (numberS.length() < digits) {
			for (int i = 0; i < digits - length; i++) {
				numberS = "0" + number;
			}
		}
		return numberS;
	}

	public static String fillZero(String numberS, int digits) {
		int length = numberS.length();
		if (numberS.length() < digits) {
			for (int i = 0; i < digits - length; i++) {
				numberS = "0" + numberS;
			}
		}
		return numberS;
	}

}
