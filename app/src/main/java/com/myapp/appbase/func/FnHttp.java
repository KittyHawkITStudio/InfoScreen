package com.myapp.appbase.func;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FnHttp {

    /** Get 慧康默认 */
    public static String getDFT(String target)  {

        try {
            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            //获得结果码
            int responseCode = connection.getResponseCode();
            String resp = "";
            if(responseCode ==200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
                String str = "";
                while ((str = reader.readLine()) != null) {
                    resp += str;
                }

                return resp;
            }else {
                //请求失败
                return null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }

    }

    /** Get okhttp */
    public static String get(String target)  {

        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(target)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            //e.printStackTrace();
            return e.getMessage();
        }

    }


}
