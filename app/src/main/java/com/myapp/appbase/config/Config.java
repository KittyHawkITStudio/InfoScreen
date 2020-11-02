package com.myapp.appbase.config;

public class Config {

	public final String pCopName = "copName";
	public final String pCityCode = "cityCode";
	public final String pVoiceSet = "voiceSet";
	public final String pRecordSet = "recordSet";

	public final String pReportTitle = "reportTitle";

	String copName; //单位名称
	String cityCode; //城市代码
	String voiceSet; //语音播报设置 格式：0-0-0-0-0-0-1-1-1-1-1-1-1-1-1-0-0-0  0：不播   1：播
	String recordSet;  //温度记录设置 格式：0-0-0-0-0-0-1-1-1-1-1-1-1-1-1-0-0-0  0：不记录   1：记录
	String reportTitle; //导出报告的标题

	public String getCopName() {
		return copName;
	}

	public void setCopName(String copName) {
		this.copName = copName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getVoiceSet() {
		return voiceSet;
	}

	public void setVoiceSet(String voiceSet) {
		this.voiceSet = voiceSet;
	}

	public String getRecordSet() {
		return recordSet;
	}

	public void setRecordSet(String recordSet) {
		this.recordSet = recordSet;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

}
