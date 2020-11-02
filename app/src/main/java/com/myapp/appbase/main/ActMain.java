package com.myapp.appbase.main;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.appbase.R;
import com.myapp.appbase.config.Config;
import com.myapp.appbase.config.DT;
import com.myapp.appbase.database.RecordManager;
import com.myapp.appbase.entity.Record;
import com.myapp.appbase.entity.WeatherForecast;
import com.myapp.appbase.entity.WeatherInfo;
import com.myapp.appbase.func.ChinaDate;
import com.myapp.appbase.func.FnApp;
import com.myapp.appbase.func.FnConfig;
import com.myapp.appbase.func.FnDate;
import com.myapp.appbase.func.FnHttp;
import com.myapp.appbase.func.FnSerialPort;
import com.myapp.appbase.func.FnString;
import com.myapp.appbase.func.FnTask;
import com.myapp.appbase.func.FnWidget;
import com.myapp.appbase.widget.SpinnerOption;

import org.json.JSONObject;

import java.io.Console;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActMain extends AppCompatActivity implements  TextToSpeech.OnInitListener {

    Context context;

    //Data
    Config config;
    int[] voiceSet;
    int[] recordSet;
    int counter;
    String currDate;
    String time = "", timeSecond = "";
    String dateGL = ""; //公历
    String dateNL = ""; //农历
    String dayOfWeek = ""; //星期几
    WeatherInfo wi;
    ArrayList<WeatherForecast> fcs;
    double wd, sd; //温度，湿度
    DecimalFormat df;

    // TTS对象
    private TextToSpeech mTextToSpeech;

    //UI
    TextView etGLYear, etGLMonth, etGLDate, tvDayOfWeek,
            etNLYear, etNLMonth, etNLDate;
    TextView tvCopName, tvDateGL, tvTime, tvTimeSecond, tvDateNL, tvWD, tvSD,
             tvDay1, tvDay2, tvDay3,
             tvWeatherD1, tvWeatherD2, tvWeatherD3;

    //Control
    final int MSG_REFRESH = 0;
    final int MSG_REFRESH_ERROR = 1;

    final int MSG_GETWDSD_OK = 2;
    final int MSG_GETWDSD_ERROR = 3;

    final int MSG_WEATHER_OK = 4;
    final int MSG_WEATHER_ERROR = 5;

    final int MSG_CHECK_VALID_FAIL = 7;

    final int MSG_REFRESH_UI = 8;

    FnSerialPort fnSerialPort;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TheApplication) getApplication()).addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        context = this;
        setContentView(R.layout.act_main);

        new CheckValidThread().start();

        //初始化文件


        initData();
        initUI();
        bindEvent();

        initTextToSpeech();

        // 准备好Message Hander
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CHECK_VALID_FAIL:
                        Toast.makeText(context, "试用期已结束，请联系供应商", Toast.LENGTH_LONG).show();
                        finish();
                        break;

                    case MSG_REFRESH:
                        //分解dateGL   2020-07-27
                        handleAndShowDateGL();

                        tvTime.setText(time);
                        tvTimeSecond.setText(timeSecond);
                        break;
                    case MSG_REFRESH_ERROR: break;

                    case MSG_REFRESH_UI:
                        refreshUI();
                        break;

                    case MSG_GETWDSD_OK:
                        tvWD.setText(df.format(wd) + "℃");
                        tvSD.setText(df.format(sd) + "%");
                        break;
                    case MSG_GETWDSD_ERROR: break;

                    case MSG_WEATHER_OK:
                        //实况
                        //tvWeather.setText(wi.getWeather());

                        if(fcs==null || fcs.size() < 3){
                            return;
                        }

                        //预报
                        WeatherForecast fc1 = fcs.get(0); //今天
                        WeatherForecast fc2 = fcs.get(1); //明天
                        WeatherForecast fc3 = fcs.get(2); //后天

                        tvWeatherD1.setText(FnApp.handleWeatherForcastForShow(fc1));
                        tvWeatherD2.setText(FnApp.handleWeatherForcastForShow(fc2));
                        tvWeatherD3.setText(FnApp.handleWeatherForcastForShow(fc3));

                        break;
                    case MSG_WEATHER_ERROR:
                        //tvWeather.setText("-");
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        new ThreadRefreshTime().start();
        //...

        new ThreadGetWDSD().start();

        new ThreadGetWeather().start();

        //for test 暂时关闭
        setVoiceSchedule();

        setRecordSchedule();

        //for test 如果是没有ttyS2是设备，会报错的
        try{
            fnSerialPort = new FnSerialPort(context);
            fnSerialPort.startListenWDSD();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private void initData() {

        FnTask.initDatabase(this);

        config = FnConfig.getConfig(context);
        currDate = FnDate.getCurrentTime("yyyy-M-d");
        dateGL = FnDate.getCurrentTime("yyyy-M-d");

        dayOfWeek = FnDate.getDayOfWeek(new Date());
        dayOfWeek = dayOfWeek.substring(2);

        ChinaDate lc = new ChinaDate();
        dateNL = lc.toString();
        dateNL = dateNL.replace("年", "-");
        dateNL = dateNL.replace("月", "-");  //庚子-六-十一
        System.out.println("农历日期：" + dateNL);

        df = new DecimalFormat(".0"); //保留1位

        //整点播报设置
        voiceSet = new int[24];
        String voiceSetStr = config.getVoiceSet();
        String[] array = voiceSetStr.split("-");
        for(int i = 0; i < array.length; i++){
            try{ voiceSet[i] = Integer.parseInt(array[i]);
            }catch(Exception e){ e.printStackTrace(); }
        }

        //温度记录设置
        recordSet = new int[24];
        //每个整点都记录温度
        for(int i = 0; i < recordSet.length; i++){
            recordSet[i] = 1;
        }

        /*
        String recordSetStr = config.getVoiceSet();
        array = recordSetStr.split("-");
        for(int i = 0; i < array.length; i++){
            try{ recordSet[i] = Integer.parseInt(array[i]);
            }catch(Exception e){ e.printStackTrace(); }
        }
        */

    }

    private void initUI() {
        //new FnWidget().hideBar(context);

        //hideBar(); //让用户手动隐藏s

        tvCopName = (TextView) findViewById(R.id.tvCopName);
        tvCopName.setText(config.getCopName());

        etGLYear = (TextView) findViewById(R.id.etGLYear);
        etGLMonth = (TextView) findViewById(R.id.etGLMonth);
        etGLDate = (TextView) findViewById(R.id.etGLDate);
        tvDayOfWeek = (TextView) findViewById(R.id.etDayOfWeek);

        etNLYear = (TextView) findViewById(R.id.etNLYear);
        etNLMonth = (TextView) findViewById(R.id.etNLMonth);
        etNLDate = (TextView) findViewById(R.id.etNLDate);

        //********以下两个可以不要了

        //tvDateGL = (TextView) findViewById(R.id.tvDateGL);
        //tvDateGL.setText(currDate);

        tvDateNL = (TextView) findViewById(R.id.tvDateNL);
        tvDateNL.setText(dateNL);

        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTimeSecond = (TextView) findViewById(R.id.tvTimeSecond);

        tvWD = (TextView) findViewById(R.id.tvWD);
        tvSD = (TextView) findViewById(R.id.tvSD);

        tvDay1 = (TextView) findViewById(R.id.tvDay1);
        tvDay2 = (TextView) findViewById(R.id.tvDay2);
        tvDay3 = (TextView) findViewById(R.id.tvDay3);

        tvWeatherD1 = (TextView) findViewById(R.id.tvWeatherD1);
        tvWeatherD2 = (TextView) findViewById(R.id.tvWeatherD2);
        tvWeatherD3 = (TextView) findViewById(R.id.tvWeatherD3);

        refreshUI();

    }

    private void bindEvent() {
        tvCopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                counter++;
                if(counter == 10){
                    startActivity(new Intent(context, ActConfig.class));
                    counter = 0;
                }
            }
        });

    }

    private void refreshUI(){
        Date day2 = FnDate.getOneDateFromDay(1);
        Date day3 = FnDate.getOneDateFromDay(2);
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");

        System.out.println("明天：" + sdf.format(day2));
        System.out.println("后天：" + sdf.format(day3));

        tvDay2.setText(sdf.format(day2));
        tvDay3.setText(sdf.format(day3));
    }

    private void handleAndShowDateGL(){

        //yyyy-M-d   2020-7-31
        String[] strs = dateGL.split("-");
        etGLYear.setText(strs[0]);
        etGLMonth.setText(strs[1]);
        etGLDate.setText(strs[2]);
        //etDayOfWeek.setText(dayOfWeek.substring(2)); //星期一

        tvDayOfWeek.setText(dayOfWeek);

        ////庚子-六-十一
        strs = dateNL.split("-");
        etNLYear.setText(strs[0]);
        etNLMonth.setText(strs[1]);
        etNLDate.setText(strs[2]);
    }

    /** 设置定制播报任务（24小时） */
    private void setVoiceSchedule(){
        /*
        int hour = 0;
        for(int i = 0; i < recordSet.length; i++){
            if(recordSet[i]==DT.V_YES){ //如果设置了，就记录
                setRecordTask(hour);
            }
            hour ++;
        }
        */

        int hour = 0;
        for(int i = 0; i < voiceSet.length; i++){
            if(voiceSet[i]==DT.V_YES){ //如果设置了，就播报
                setHourVoice(hour);
            }
            hour++;
        }

    }

    /** 设定某个整点的播报任务 */
    private void setHourVoice(int hour) {
        final int  hourF = hour;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //播报>>
                //...
                submit(hourF);
                System.out.println("时间=" + new Date() + "  每日定时播报执行"); // 1次
            }
        };

        String timeStr = FnString.fillZero(hour, 2);
        timeStr += ":00:00";//20:00:00

        /*
         * 参数一:command：执行线程
         * 参数二:initialDelay：初始化延时
         * 参数三:period：两次开始执行最小间隔时间
         * 参数四:unit：计时单位
         */

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        long oneDay = 1;
        long initDelay  = FnDate.getTimeMillis(timeStr) - System.currentTimeMillis();

        //(24 * 60 * 60 * 1000)

        initDelay = initDelay > 0 ? initDelay : ((24 * 60 * 60 * 1000) + initDelay);

        System.out.println("已安排任务，延时：" + initDelay);

        //for test 暂时就先用这种方式
        java.util.Timer timer = new java.util.Timer(true);
        long oneDayMilli = (24 * 60 * 60 * 1000);
        timer.schedule(task, initDelay, oneDayMilli);

        /*
        executor.scheduleAtFixedRate(
                task,
                initDelay,
                oneDay,
                TimeUnit.DAYS);
        */

    }

    /** 设定某个整点的播报任务 */
    private void setHourVoiceOld(int hour) {
        final int  hourF = hour;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //播报>>
                //...
                submit(hourF);
                System.out.println("时间=" + new Date() + "  每日定时播报执行"); // 1次
            }
        };

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, hour, 00, 00);
        //第一次执行的时间,
        Timer timer = new Timer();
        timer.schedule(task,  FnDate.getNextHour());
        //....

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 2);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

// every night at 2am you run your task
       // Timer timer = new Timer();
       // timer.schedule(task, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
    }

    /** 设置温湿度记录任务（定时记录) */
    private void setRecordSchedule(){

        int hour = 0;
        for(int i = 0; i < recordSet.length; i++){
            if(recordSet[i]==DT.V_YES){ //如果设置了，就记录
                setRecordTask(hour);
            }
            hour ++;
        }

    }

    /** 设定某个整点的记录温湿度任务 */
    private void setRecordTask(int hour) {

        final int  hourF = hour;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                record(hourF);
                //for test
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("时间=" + sdf.format(new Date()) + "  记录温度功能执行"); // 1次
            }
        };

        /*
        Runnable task = new Runnable() {
            @Override
            public void run() {
                record(hourF);
                //for test
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("时间=" + sdf.format(new Date()) + "  记录温度功能执行"); // 1次
            }
        };
        */

        String timeStr = FnString.fillZero(hour, 2);
        timeStr += ":00:00";//20:00:00

        /*
         * 参数一:command：执行线程
         * 参数二:initialDelay：初始化延时
         * 参数三:period：两次开始执行最小间隔时间
         * 参数四:unit：计时单位
         */

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        long oneDay = 1;
        long initDelay  = FnDate.getTimeMillis(timeStr) - System.currentTimeMillis();

        //(24 * 60 * 60 * 1000)

        initDelay = initDelay > 0 ? initDelay : ((24 * 60 * 60 * 1000) + initDelay);

        System.out.println("已安排记录任务，" + hour + "点，延时：" + initDelay);

        //for test 暂时就先用这种方式
        java.util.Timer timer = new java.util.Timer(true);
        long oneDayMilli = (24 * 60 * 60 * 1000);
        timer.schedule(task, initDelay, oneDayMilli);

        //for test
        /*
        executor.scheduleAtFixedRate(
                new Runnable() {

                    @Override
                    public void run() {
                        record(hourF);
                        //for test
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        System.out.println("Runnable 时间=" + sdf.format(new Date()) + "  记录温度功能执行"); // 1次
                    }
                },
                1000, //delay
                10,  //period
                TimeUnit.SECONDS);
        */
        /*
        executor.scheduleAtFixedRate(
                task,
                initDelay,
                oneDay,
                TimeUnit.DAYS);
        */

    }

    /** 设定某个整点的记录温湿度任务 */
    private void setRecordTaskOld(int hour) {
        final int  hourF = hour;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("时间=" + new Date() + "  记录温度功能执行"); // 1次
                record(hourF);
            }
        };

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, hour, 00, 00);

        //第一次执行的时间,
        Timer timer = new Timer();
        timer.schedule(task, FnDate.getNextHour());

    }

    /** 记录 */
    private void record(int hour){
        //System.out.println("温湿度记入数据库，时间点：" + hour);
        Record record = new Record();

        String date = FnDate.getCurrentTime("yyyy-MM-dd");
        record.setTime(date + " " + FnString.fillZero(hour, 2) + ":00"); //5点 》》  05:00:00 //报告上每天显示5个时间段
        record.setWd(wd);
        record.setSd(sd);
        RecordManager.get(context).insert(record);
    }

    /** 刷新时间 */
    private class ThreadRefreshTime extends Thread {
        @Override
        public void run() {

            try{

                //显示时间
                java.util.Timer timer = new java.util.Timer(true);

                TimerTask task = new TimerTask() {
                    public void run() {

                        time = FnDate.getCurrentTime("HH:mm");
                        timeSecond = FnDate.getCurrentTime("ss");
                        //每次需要执行的代码放到这里面。
                        dateGL =  FnDate.getCurrentTime("yyyy-M-d");

                        if(!dateGL.equals(currDate)){ //进入了新的一天
                            System.out.println("新的一天");
                            //公历
                            dateGL =  FnDate.getCurrentTime("yyyy-M-d");;
                            currDate = dateGL;
                            dayOfWeek = FnDate.getDayOfWeek(new Date());
                            dayOfWeek = dayOfWeek.substring(2);
                            //农历
                            ChinaDate lc = new ChinaDate();
                            System.out.println("农历：" + lc.toString());
                            dateNL = lc.toString(); // //庚子年六月十一
                            dateNL = dateNL.replace("年", "-");
                            dateNL = dateNL.replace("月", "-");  //庚子-六-十一

                            final Message msg = handler.obtainMessage();
                            msg.what = MSG_REFRESH_UI;
                            handler.sendMessage(msg);

                        }else{
                            //System.out.println("还在当天");
                        }
                        final Message msg = handler.obtainMessage();
                        msg.what = MSG_REFRESH;
                        handler.sendMessage(msg);
                    }
                };

                timer.schedule(task, 0, 1000); //task, delay, 1000


            }catch(Exception e){
                //msg.what = MSG_REFRESH_ERROR;
                e.printStackTrace();
            }


        }

    }

    /** 获取温湿度（串口） */
    private class ThreadGetWDSD extends Thread {
        @Override
        public void run() {

            while(true){
                Message msg = handler.obtainMessage();
                try{
                    //Temp = 30.180   Humi = 64.870
                    //Temp = 30.180   Humi = 64.870
                    //Temp = 30.180   Humi = 64.870
                    //Temp = 30.180   Humi = 64.870

                    //>>>>>  30.180  30.180

                    /*
                    String info = fnSerialPort.info;
                    info = info.replace("Temp = ", "");
                    info = info.replace("Humi = ", "");
                    String[] wdsd = info.split("   ");
                    wd = Double.parseDouble(wdsd[0]);
                    sd = Double.parseDouble(wdsd[1]);
                    */
                    wd = fnSerialPort.wd;
                    sd = fnSerialPort.sd;

                    msg.what = MSG_GETWDSD_OK;

                }catch(Exception e){
                    msg.what = MSG_GETWDSD_ERROR;
                    //e.printStackTrace();
                }
                handler.sendMessage(msg);

                //等待3秒
                try{
                    Thread.sleep(3000);
                }catch(Exception e){ e.printStackTrace(); }

            }

        }

    }

    /** 获取天气信息 */
    private void reqWeather(){
        final Message msg = handler.obtainMessage();
        try{
            wi = FnApp.getWeatheInfo(config);
            fcs = FnApp.getWeatheForecast(config);

            if(fcs!=null){
                msg.what = MSG_WEATHER_OK;
            }else{
                msg.what = MSG_WEATHER_ERROR;
            }

        }catch(Exception e){
            msg.what = MSG_WEATHER_ERROR;
            //e.printStackTrace();
        }
        handler.sendMessage(msg);
    }

    /** 获取天气预报 */
    private class ThreadGetWeather extends Thread {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            try{
                //显示时间
                java.util.Timer timer = new java.util.Timer(true);

                TimerTask task = new TimerTask() {
                    public void run() {
                        reqWeather();
                    }
                };
                timer.schedule(task, 0, 1000 * 3600); //task, delay, 1000 //一小时请求一次
                //先请求10次
                for(int i = 0; i < 10; i++){
                    reqWeather();
                    try{
                        Thread.sleep(3000);
                    }catch(Exception e){
                        //...
                    }
                }

            }catch(Exception e){
                msg.what = MSG_WEATHER_ERROR;
                //e.printStackTrace();
            }

            handler.sendMessage(msg);
        }

    }

    /** 定时播报 */
    private class ThreadVoice extends Thread {

        Date dateTime;

        public ThreadVoice(Date dateTime){
            this.dateTime = dateTime;
        }

        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            try{

                msg.what = MSG_GETWDSD_OK;
            }catch(Exception e){
                msg.what = MSG_GETWDSD_ERROR;
                e.printStackTrace();
            }

            handler.sendMessage(msg);
        }

    }

    /** 隐藏导航栏 */
    public void hideBar(){

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


    }

    private void initTextToSpeech() {

        try{
            // 参数Context,TextToSpeech.OnInitListener
            mTextToSpeech = new TextToSpeech(context, this);
            // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            mTextToSpeech.setPitch(1.0f);
            // 设置语速
            mTextToSpeech.setSpeechRate(0.5f);
        }catch(Exception ex){
            ex.printStackTrace();
        }


    }



    private void submit(int hour) {
        // validate
        String type = "";
        try{
            WeatherForecast fc = fcs.get(0);

            type = FnApp.handleWeatherForcastForVoice(fc);
        }catch(Exception ex){
            //...
        }

        df = new DecimalFormat(".0"); //保留1位

        String text = "现在是北京时间"+hour+"点整，今天的天气是" + type + "  " +
                "当前室内温度为" + df.format(wd) + "度，湿度为百分之" + df.format(sd) + "";

        // TODO validate success, do something
        if (mTextToSpeech != null && !mTextToSpeech.isSpeaking()) {
            /*
                TextToSpeech的speak方法有两个重载。
                // 执行朗读的方法
                speak(CharSequence text,int queueMode,Bundle params,String utteranceId);
                // 将朗读的的声音记录成音频文件
                synthesizeToFile(CharSequence text,Bundle params,File file,String utteranceId);
                第二个参数queueMode用于指定发音队列模式，两种模式选择
                （1）TextToSpeech.QUEUE_FLUSH：该模式下在有新任务时候会清除当前语音任务，执行新的语音任务
                （2）TextToSpeech.QUEUE_ADD：该模式下会把新的语音任务放到语音任务之后，
                等前面的语音任务执行完了才会执行新的语音任务
             */
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }


    /**
     * 用来初始化TextToSpeech引擎
     *
     * @param status SUCCESS或ERROR这2个值
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            /*
                使用的是小米手机进行测试，打开设置，在系统和设备列表项中找到更多设置，
            点击进入更多设置，在点击进入语言和输入法，见语言项列表，点击文字转语音（TTS）输出，
            首选引擎项有三项为Pico TTs，科大讯飞语音引擎3.0，度秘语音引擎3.0。其中Pico TTS不支持
            中文语言状态。其他两项支持中文。选择科大讯飞语音引擎3.0。进行测试。

                如果自己的测试机里面没有可以读取中文的引擎，
            那么不要紧，我在该Module包中放了一个科大讯飞语音引擎3.0.apk，将该引擎进行安装后，进入到
            系统设置中，找到文字转语音（TTS）输出，将引擎修改为科大讯飞语音引擎3.0即可。重新启动测试
            Demo即可体验到文字转中文语言。
             */
            // setLanguage设置语言
            int result = mTextToSpeech.setLanguage(Locale.CHINESE);

            System.out.println("result:" + result);
            System.out.println("LANG_MISSING_DATA:" +  TextToSpeech.LANG_MISSING_DATA);
            System.out.println("LANG_NOT_SUPPORTED:" + TextToSpeech.LANG_NOT_SUPPORTED);

            // TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失
            // TextToSpeech.LANG_NOT_SUPPORTED：不支持
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CheckValidThread extends Thread {

        @Override
        public void run() {

            try{
                String resp = FnHttp.getDFT("http://support.victtec.com/web2_service/zyg/if_infoscreen_valid");

                //System.out.println("验证返回：" + resp);
                if(resp.equals("no")){
                    Message msg = handler.obtainMessage();
                    msg.what = MSG_CHECK_VALID_FAIL;
                    handler.sendMessage(msg);
                }
            }catch(Exception ex){
                //ex.printStackTrace();
                //System.out.println("checkValid请求失败");
            }

            try{
                Thread.sleep(1000 * 10);
                new CheckValidThread().start();
            }catch(Exception ex){
                //...
            }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果
        new ThreadGetWDSD().start();
        new ThreadGetWeather().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
