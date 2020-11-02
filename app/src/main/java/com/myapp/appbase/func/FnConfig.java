package com.myapp.appbase.func;

import android.content.Context;
import android.content.SharedPreferences;

import com.myapp.appbase.config.Config;

/** 操作配置 */
public class FnConfig {

	/** 加载设置信息 */
	public static Config getConfig(Context context){
		Config config = new Config();
		SharedPreferences localSP = context.getSharedPreferences("config", 0);
		config.setCopName(localSP.getString(config.pCopName, ""));
		config.setCityCode(localSP.getString(config.pCityCode, "101190402"));
		String voictSetDFT = "0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0"; //都不播报
		config.setVoiceSet(localSP.getString(config.pVoiceSet, voictSetDFT));
        String recordDFT = "0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0"; //都不记录
        config.setRecordSet(localSP.getString(config.pRecordSet, recordDFT));
        String titleDFT = "温湿度记录表";
        config.setReportTitle(localSP.getString(config.pReportTitle, titleDFT));
		return config;
	}

	/** 保存设置信息 */
	public static void saveConfig(Context context, Config config){
		SharedPreferences.Editor localEt = context.getSharedPreferences("config", 0).edit();
		localEt.putString(config.pCopName, config.getCopName());
		localEt.putString(config.pCityCode, config.getCityCode());
		localEt.putString(config.pVoiceSet, config.getVoiceSet());
        localEt.putString(config.pRecordSet, config.getRecordSet());
		localEt.putString(config.pReportTitle, config.getReportTitle());
		localEt.commit();
	}

}
