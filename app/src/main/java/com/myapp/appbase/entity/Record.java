package com.myapp.appbase.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class Record {

    Long id;
    String time;
    double wd;
    double sd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getWd() {
        return wd;
    }

    public void setWd(double wd) {
        this.wd = wd;
    }

    public double getSd() {
        return sd;
    }

    public void setSd(double sd) {
        this.sd = sd;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        DecimalFormat df = new DecimalFormat("0.00");

        try{  json.put("time",this.time); }catch(Exception ex){ ex.printStackTrace(); }

        try{  json.put("wd", df.format(this.wd)); }catch(Exception ex)
        {
            ex.printStackTrace();
            try{json.put("wd", ""); }catch(Exception e){}
        }
        try{  json.put("sd",df.format(this.sd)); }catch(Exception ex)
        {
            ex.printStackTrace();
            try{json.put("sd", ""); }catch(Exception e){}
        }

        return json;
    }

    public static JSONObject newJson(String timeStr){
        JSONObject json = new JSONObject();
        DecimalFormat df = new DecimalFormat("0.00");

        try{
            json.put("time", timeStr);
            json.put("wd", "");
            json.put("sd", "");
        }catch(Exception ex){ ex.printStackTrace(); }


        return json;
    }

}
