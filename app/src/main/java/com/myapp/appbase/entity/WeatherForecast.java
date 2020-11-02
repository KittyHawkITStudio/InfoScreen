package com.myapp.appbase.entity;

import org.json.JSONObject;

/** 天气预报（高德接口格式） */
public class WeatherForecast {

    public String date;
    public String week;
    public String dayweather;
    public String nightweather;
    public String daytemp;
    public String nighttemp;
    public String daywind;
    public String nightwind;
    public String daypower;
    public String nightpower;

    public WeatherForecast(JSONObject json){
        try{
            this.date = json.getString("date");
            this.week = json.getString("week");
            this.dayweather = json.getString("dayweather");
            this.nightweather = json.getString("nightweather");
            this.daytemp = json.getString("daytemp");
            this.nighttemp = json.getString("nighttemp");
            this.daywind = json.getString("daywind");
            this.nightwind = json.getString("nightwind");
            this.daypower = json.getString("daypower");
            this.nightpower = json.getString("nightpower");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDayweather() {
        return dayweather;
    }

    public void setDayweather(String dayweather) {
        this.dayweather = dayweather;
    }

    public String getNightweather() {
        return nightweather;
    }

    public void setNightweather(String nightweather) {
        this.nightweather = nightweather;
    }

    public String getDaytemp() {
        return daytemp;
    }

    public void setDaytemp(String daytemp) {
        this.daytemp = daytemp;
    }

    public String getNighttemp() {
        return nighttemp;
    }

    public void setNighttemp(String nighttemp) {
        this.nighttemp = nighttemp;
    }

    public String getDaywind() {
        return daywind;
    }

    public void setDaywind(String daywind) {
        this.daywind = daywind;
    }

    public String getNightwind() {
        return nightwind;
    }

    public void setNightwind(String nightwind) {
        this.nightwind = nightwind;
    }

    public String getDaypower() {
        return daypower;
    }

    public void setDaypower(String daypower) {
        this.daypower = daypower;
    }

    public String getNightpower() {
        return nightpower;
    }

    public void setNightpower(String nightpower) {
        this.nightpower = nightpower;
    }
}
