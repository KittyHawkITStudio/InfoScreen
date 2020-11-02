package com.myapp.appbase.config;

public interface DT {

	String SOFTNAME = "AppBase";

	String COP_ID = "COP_ID";

	/* 数据库 */
	String DB_NAME = "data.db";
	String DB_PATH = "/data/data/com.myapp.appbase/databases/";

	/* 默认初始化的配置文件*/
	String FILE_PATH = "/data/data/com.myapp.appbase/files/";
	String DEFAULT_CONFIG_NAME = "config.json";

	//szctfly
	String CTFLY_USB1_PATH = "/storage/usbhost01"; //创腾翔

	String STORAGE_DOWNLOAD_PATH = "/Internal shared storage/DCIM"; //创腾翔

	//导出模板文件名称
	String EXPORT_TEMPLATE_NAME = "InfoScreenReport.docx";

	int V_YES = 1; //播报
	int V_NO = 0; //不播报

	String WEATHER_APP_KEY = "ab2ce3b557b55c29a4e2b54dfb77f0f6&extensions"; //zhouyingge的账号申请的高德天气key

}
