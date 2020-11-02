 package com.myapp.appbase.main;

import android.app.Activity;
import android.app.Application;

import com.myapp.appbase.config.Config;
import com.myapp.appbase.entity.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

 public class TheApplication extends Application {

     private List<Activity> activities = new ArrayList<Activity>();

     public void addActivity(Activity activity){
         activities.add(activity);
     }
     public void removeActivity(Activity activity){
         activities.remove(activity);
     }

     Config config;
     User user;

     //远程数据库连接
     Connection dbConnUserDatabase;
     Connection dbConnReportDatabase;

     @Override
     public void onTerminate() {
         super.onTerminate();

         for(Activity activity : activities){
             activity.finish();
         }

         System.exit(0);

     }

 }
