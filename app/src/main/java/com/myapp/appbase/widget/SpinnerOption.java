package com.myapp.appbase.widget;

public class SpinnerOption {

	public static final int MODE_VALUE = 0; //比较模式之比较value
	public static final int MODE_TEXT = 1; //比较模式之比较text

	private String value = "";
	private String text = "";

	public SpinnerOption() {
		value = "";
		text = "";
	}

	public SpinnerOption(String value, String text) {
		this.value = value;
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

}