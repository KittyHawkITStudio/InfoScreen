package com.myapp.appbase.func;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.storage.StorageManager;
import android.widget.Spinner;

import com.myapp.appbase.config.Config;
import com.myapp.appbase.config.DT;
import com.myapp.appbase.entity.User;
import com.myapp.appbase.entity.WeatherForecast;
import com.myapp.appbase.entity.WeatherInfo;
import com.myapp.appbase.main.TheApplication;
import com.myapp.appbase.widget.SpinnerOption;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/** 操作配置 */
public class FnApp {

    /** 获取天气实况(高德接口) */
    public static WeatherInfo getWeatheInfo(Config config){
        //高德天气接口
        //String url = "https://restapi.amap.com/v3/weather/weatherInfo?city=530627&key=ab2ce3b557b55c29a4e2b54dfb77f0f6&extensions=all";
        String url = "https://restapi.amap.com/v3/weather/weatherInfo?city="+config.getCityCode()+"&key="+DT.WEATHER_APP_KEY;
        String resp = FnHttp.get(url);
        //System.out.println("天气实况返回：" + resp);
        return  parseWeatherInfo(resp);
    }

    /** 获取天气预报 */
    public static ArrayList<WeatherForecast> getWeatheForecast(Config config){
        //高德天气接口
        String url = "https://restapi.amap.com/v3/weather/weatherInfo?city="+config.getCityCode()+"&key="+DT.WEATHER_APP_KEY+"=all";
        String resp = FnHttp.get(url);
        //System.out.println("天气预报返回：" + resp);
        return  parseWeatherForecast(resp);
    }

    /// <summary>
    /// 解析天气结果（高德接口）
    /// </summary>
    public static WeatherInfo parseWeatherInfo(String resp)
    {
        try{
            JSONObject obj = new JSONObject(resp);
            JSONArray lives = obj.getJSONArray("lives");
            JSONObject info = lives.getJSONObject(0);

            WeatherInfo wi = new WeatherInfo();
            wi.weather = info.getString("weather");;
            wi.temperature = info.getString("temperature");;
            wi.winddirection = info.getString("winddirection");;
            wi.windpower = info.getString("windpower");;

            return wi;
        }catch(Exception ex){
            //ex.printStackTrace();
            return null;
        }

    }

    /// <summary>
    /// 解析天气结果（高德接口）
    /// </summary>
    public static ArrayList<WeatherForecast> parseWeatherForecast(String resp)
    {
        try{
            JSONObject obj = new JSONObject(resp);
            JSONArray array = obj.getJSONArray("forecasts");
            JSONObject info = array.getJSONObject(0);
            array = info.getJSONArray("casts");

            ArrayList<WeatherForecast> fcs = new ArrayList<WeatherForecast>();

            for(int i = 0; i < array.length(); i++){
                JSONObject json = array.getJSONObject(i);
                WeatherForecast fc = new WeatherForecast(json);
                fcs.add(fc);
            }

            return fcs;
        }catch(Exception ex){
            //ex.printStackTrace();
            return null;
        }

    }

    /** 处理预报，组织成要显示的格式 */
    public static String  handleWeatherForcastForShow(WeatherForecast fc){
        String fcStr = "";
        if(fc.getDayweather().equals(fc.getNightweather())){
            fcStr += fc.getDayweather();
        }else{
            fcStr += fc.getDayweather() + "转" + fc.getNightweather();
        }
        fcStr += "\r\n";

        fcStr += fc.getNighttemp() + " ~ " + fc.getDaytemp() + "℃";
        //System.out.println("显示的预报：" + fcStr);
        return fcStr;
    }

    /** 处理预报，组织成要语言播报的格式 */
    public static String  handleWeatherForcastForVoice(WeatherForecast fc){
        String fcStr = "";
        if(fc.getDayweather().equals(fc.getNightweather())){
            fcStr += fc.getDayweather();
        }else{
            fcStr += fc.getDayweather() + "转" + fc.getNightweather();
        }
        fcStr += "  最高温度" + fc.getDaytemp() + "度， 最低温度" + fc.getNighttemp() + "度";
        //System.out.println("语音的预报：" + fcStr);
        return fcStr;
    }

    /// <summary>
    /// 解析天气结果
    /// </summary>
    /*
    public static WeatherInfo parseWeather(String resp)
    {

        try{
            JSONObject obj = new JSONObject(resp);
            JSONObject data = (JSONObject)obj.getJSONObject("data");
            JSONArray forecast = (JSONArray)data.getJSONArray("forecast");
            JSONObject info = (JSONObject)forecast.get(0);
            //{ "date":"9日星期天","high":"高温 8℃","fengli":"<![CDATA[4-5级]]>","low":"低温 1℃","fengxiang":"北风","type":"阴"},
            String high = info.getString("high");
            String low = info.getString("low");
            String fengli = info.getString("fengli");
            fengli = fengli.replace("<![CDATA[", "").replace("]]>", ""); ;
            String fengxiang = (String)info.getString("fengxiang");
            String type = (String)info.getString("type");

            high = high.replace("高温", "");
            low = low.replace("低温", "");
            //fengli += ;

            WeatherInfo wi = new WeatherInfo();
            wi.type = type;
            wi.high = high;
            wi.low = low;
            wi.fengli = fengli;

            return wi;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

    }
    */

    public static String getStoragePath(Context mContext,String keyword) throws Exception  {

        String targetpath = "";

        StorageManager mStorageManager = (StorageManager)   mContext

                .getSystemService(Context.STORAGE_SERVICE);

        Class<?> storageVolumeClazz =   null;

        try {

            storageVolumeClazz =   Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList =   mStorageManager.getClass().getMethod("getVolumeList");

            Method getPath =   storageVolumeClazz.getMethod("getPath");

            Object result =   getVolumeList.invoke(mStorageManager);

            final int length =   Array.getLength(result);



            Method getUserLabel =   storageVolumeClazz.getMethod("getUserLabel");

            for (int   i = 0; i < length; i++) {

                Object storageVolumeElement =   Array.get(result, i);

                String userLabel = (String)   getUserLabel.invoke(storageVolumeElement);

                String path = (String)   getPath.invoke(storageVolumeElement);

                if(userLabel.contains(keyword)){

                    targetpath = path;

                }

            }

        } catch   (ClassNotFoundException e)   {

            e.printStackTrace();

        } catch (InvocationTargetException e)   {

            e.printStackTrace();

        } catch (NoSuchMethodException e) {

            e.printStackTrace();

        } catch (IllegalAccessException   e) {

            e.printStackTrace();

        }

        return targetpath ;

    }


    /** 初始化温湿度导出模块 */
    public static void initReportTemplate(Context context) {

        String FILE_PATH = DT.FILE_PATH;
        String FILE_NAME = DT.EXPORT_TEMPLATE_NAME;

        if ((new File(FILE_PATH + FILE_NAME)).exists() == false) {
            File f = new File(FILE_PATH);
            if (!f.exists()) {
                f.mkdir();
            }

            try {
                InputStream is = context.getAssets().open(FILE_NAME);
                OutputStream os = new FileOutputStream(FILE_PATH + FILE_NAME);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                // 用日志的形式记录
                e.printStackTrace();
            }

        }
    }


}
