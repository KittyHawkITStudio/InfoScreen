package com.myapp.appbase.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.appbase.R;
import com.myapp.appbase.config.Config;
import com.myapp.appbase.config.DT;
import com.myapp.appbase.database.QueryRecordFilter;
import com.myapp.appbase.database.RecordManager;
import com.myapp.appbase.entity.Record;
import com.myapp.appbase.func.FnConfig;
import com.myapp.appbase.func.FnDate;
import com.myapp.appbase.func.FnFile;
import com.myapp.appbase.func.FnString;
import com.myapp.appbase.func.FnWidget;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@SuppressWarnings("ResourceType")
/** 检测项目列表 */
public class ActRecord extends Activity {

    Context context;

    //Data
    Config config;
    private String dateTemp, monthTemp;
    private ArrayList<Record> records;

    //UI
    private TextView tvDateStart, tvDateEnd, tvTemp, tvMonth;
    Button btnQueryRecord, btnClear, btnExportRecord, btnBack;
    ListView lv;
    CheckBox cbx0,cbx1,cbx2,cbx3,cbx4,cbx5,cbx6,cbx7,cbx8,cbx9,cbx10,cbx11,
            cbx12,cbx13,cbx14,cbx15,cbx16,cbx17,cbx18,cbx19,cbx20,cbx21,cbx22,cbx23;
    CheckBox[] cbxs;

    /* 日期选择框元素小组 */
    private AlertDialog dlgDatePicker;
    private View vDatePicker;
    private DatePicker datepicker;
    private Button btnDatePickerOK;
    DatePicker.OnDateChangedListener listenerDate;

    /* 月份选择框元素小组 */
    private AlertDialog dlgMonthPicker;
    private View vMonthPicker;
    private DatePicker monthpicker;
    private Button btnMonthPickerOK;
    DatePicker.OnDateChangedListener listenerMonth;

    /* 确认框元素小组 */
    private AlertDialog dlgConfirm;
    private View vConfirm;
    private TextView tvConfirm;
    private Button btnYes;
    private Button btnNo;

    //Control
    private Handler handler;
    private final int QUERY_SUCCESS = 0;
    private final int EXPORT_WIFI_SUCCESS = 1;
    private final int EXPORT_WIFI_ERROR = 2;

    private final int EXPORT_USB_SUCCESS = 3;
    private final int EXPORT_USB_ERROR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TheApplication) getApplication()).addActivity(this);
        setContentView(R.layout.act_record);

        context = this;

        initData();
        initUI();
        bindEvent();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {

                    case QUERY_SUCCESS:
                        System.out.println("查询成功");
                        RecordAdapter adapter = new RecordAdapter(context, records);
                        lv.setAdapter(adapter);
                        break;
                    case EXPORT_WIFI_SUCCESS:
                        System.out.println("WIFI导出成功");
                        Toast.makeText(context, "WIFI导出成功" , Toast.LENGTH_LONG).show();
                        break;

                    case EXPORT_WIFI_ERROR:
                        System.out.println("WIFI导出失败");
                        Toast.makeText(context, "WIFI导出失败\r\n" + (String)msg.obj , Toast.LENGTH_LONG).show();
                        break;

                    case EXPORT_USB_SUCCESS:
                        System.out.println("USB导出成功");
                        Toast.makeText(context, "USB导出成功" , Toast.LENGTH_LONG).show();
                        break;

                    case EXPORT_USB_ERROR:
                        System.out.println("USB导出失败");
                        Toast.makeText(context, "USB导出失败\r\n" + (String)msg.obj , Toast.LENGTH_LONG).show();
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };

        query();

        //FnFile.verifyStoragePermissions(this);

        //reqExtStoragePermission();
        verifyStoragePermissions(this);
    }

    private void initData() {
        config = FnConfig.getConfig(context);
    }

    private void initUI() {

        lv = (ListView) findViewById(R.id.lvDetects);

        tvDateStart = (TextView) findViewById(R.id.tvDateStart);
        tvDateEnd = (TextView) findViewById(R.id.tvDateEnd);

        tvDateStart.setText(FnDate.getDayAway(new Date(), -7));
        tvDateEnd.setText(FnDate.getCurrentTime("yyyy-MM-dd"));

        tvMonth = (TextView) findViewById(R.id.tvMonth);
        tvMonth.setText(FnDate.getCurrentTime("yyyy-MM"));

        btnQueryRecord = (Button) findViewById(R.id.btnQueryRecord);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnExportRecord = (Button) findViewById(R.id.btnExportRecord);

        btnBack = (Button) findViewById(R.id.btnBack);

        LayoutInflater factory = LayoutInflater.from(this);
        //Date Picker
        vDatePicker = factory.inflate(R.layout.dlg_datepicker, null);
        dlgDatePicker = new AlertDialog.Builder(this).setView(vDatePicker).create();
        btnDatePickerOK = (Button) vDatePicker.findViewById(R.id.btn_datepicker_ok);
        datepicker = (DatePicker) vDatePicker.findViewById(R.id.datepicker);
        listenerDate = new DatePicker.OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                Date date = new GregorianCalendar(year, month, day).getTime();
                dateTemp = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(date);
            }
        };
        new FnWidget().initDatePicker(datepicker, listenerDate);
        dateTemp = FnDate.getCurrentTime("yyyy-MM-dd");

        //Month Picker
        vMonthPicker = factory.inflate(R.layout.dlg_monthpicker, null);
        dlgMonthPicker = new AlertDialog.Builder(this).setView(vMonthPicker).create();
        btnMonthPickerOK = (Button) vMonthPicker.findViewById(R.id.btnMonthPickerOk);
        monthpicker = (DatePicker) vMonthPicker.findViewById(R.id.monthpicker);
        listenerMonth = new DatePicker.OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int month,
                                      int day) {
                Date date = new GregorianCalendar(year, month, day).getTime();
                monthTemp = new SimpleDateFormat("yyyy-MM", Locale.CHINA)
                        .format(date);
            }
        };
        new FnWidget().initDatePicker(monthpicker, listenerMonth);

        cbx0 = (CheckBox) findViewById(R.id.cbx0);
        cbx1 = (CheckBox) findViewById(R.id.cbx1);
        cbx2 = (CheckBox) findViewById(R.id.cbx2);
        cbx3 = (CheckBox) findViewById(R.id.cbx3);
        cbx4 = (CheckBox) findViewById(R.id.cbx4);
        cbx5 = (CheckBox) findViewById(R.id.cbx5);
        cbx6 = (CheckBox) findViewById(R.id.cbx6);
        cbx7 = (CheckBox) findViewById(R.id.cbx7);
        cbx8 = (CheckBox) findViewById(R.id.cbx8);
        cbx9 = (CheckBox) findViewById(R.id.cbx9);
        cbx10 = (CheckBox) findViewById(R.id.cbx10);
        cbx11 = (CheckBox) findViewById(R.id.cbx11);
        cbx12 = (CheckBox) findViewById(R.id.cbx12);
        cbx13 = (CheckBox) findViewById(R.id.cbx13);
        cbx14 = (CheckBox) findViewById(R.id.cbx14);
        cbx15 = (CheckBox) findViewById(R.id.cbx15);
        cbx16 = (CheckBox) findViewById(R.id.cbx16);
        cbx17 = (CheckBox) findViewById(R.id.cbx17);
        cbx18 = (CheckBox) findViewById(R.id.cbx18);
        cbx19 = (CheckBox) findViewById(R.id.cbx19);
        cbx20 = (CheckBox) findViewById(R.id.cbx20);
        cbx21 = (CheckBox) findViewById(R.id.cbx21);
        cbx22 = (CheckBox) findViewById(R.id.cbx22);
        cbx23 = (CheckBox) findViewById(R.id.cbx23);

        cbxs = new CheckBox[24];
        cbxs[0] = cbx0;
        cbxs[1] = cbx1;
        cbxs[2] = cbx2;
        cbxs[3] = cbx3;
        cbxs[4] = cbx4;
        cbxs[5] = cbx5;
        cbxs[6] = cbx6;
        cbxs[7] = cbx7;
        cbxs[8] = cbx8;
        cbxs[9] = cbx9;
        cbxs[10] = cbx10;
        cbxs[11] = cbx11;
        cbxs[12] = cbx12;
        cbxs[13] = cbx13;
        cbxs[14] = cbx14;
        cbxs[15] = cbx15;
        cbxs[16] = cbx16;
        cbxs[17] = cbx17;
        cbxs[18] = cbx18;
        cbxs[19] = cbx19;
        cbxs[20] = cbx20;
        cbxs[21] = cbx21;
        cbxs[22] = cbx22;
        cbxs[23] = cbx23;

        vConfirm = factory.inflate(R.layout.dlg_confirm, null);
        tvConfirm = (TextView) vConfirm.findViewById(R.id.tvConfirm);
        dlgConfirm = new AlertDialog.Builder(this).setView(vConfirm).create();
        btnYes = (Button) vConfirm.findViewById(R.id.btnConfirmYes);
        btnNo = (Button) vConfirm.findViewById(R.id.btnConfirmNo);



    }

    private void bindEvent() {

        tvDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTemp = tvDateStart;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",
                        Locale.CHINA);
                try {
                    new FnWidget().setDatePicker(datepicker,
                            df.parse(tvDateStart.getText().toString()),
                            listenerDate);
                    dlgDatePicker.show();
                } catch (Exception e) {
                    // log记录
                    e.printStackTrace();
                }

            }
        });

        tvDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTemp = tvDateEnd;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",
                        Locale.CHINA);
                try {
                    new FnWidget().setDatePicker(datepicker,
                            df.parse(tvDateEnd.getText().toString()), listenerDate);
                    dlgDatePicker.show();
                } catch (Exception e) {
                    // log记录
                    e.printStackTrace();
                }
            }
        });

        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM",
                        Locale.CHINA);
                try {
                    new FnWidget().setDatePicker(monthpicker,
                            df.parse(tvMonth.getText().toString()),
                            listenerMonth);
                    dlgMonthPicker.show();
                } catch (Exception e) {
                    // log记录
                    e.printStackTrace();
                }

            }
        });

        btnDatePickerOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgDatePicker.hide();

                if (tvTemp == tvDateStart) {
                    tvDateStart.setText(dateTemp);
                }
                if (tvTemp == tvDateEnd) {
                    tvDateEnd.setText(dateTemp);
                }

            }
        });

        btnMonthPickerOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgMonthPicker.hide();
                tvMonth.setText(monthTemp);
            }
        });

        btnQueryRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                query();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tvConfirm.setText("确定要清空吗？");
                dlgConfirm.show();
            }
        });

        btnExportRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //检查选的是否是5个时段
                int checkedNum = 0;
                for(CheckBox cbx :cbxs){
                    if(cbx.isChecked()){ checkedNum++; }
                }
                if(checkedNum != 5){
                    Toast.makeText(context, "请选择5个时间点", Toast.LENGTH_LONG).show();
                    return;
                }

                //1.组织数据
                JSONArray jMonth = makeExportData();
                System.out.println("jMonth数据>>：" + jMonth.toString());

                //UDP广播导出
                new ExportWIFIThread(jMonth).start();

                //导出至设备的USB
                new ExportUSBThread(jMonth).start();
           }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgConfirm.hide();
                RecordManager.get(context).deleteAll();
                query();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgConfirm.hide();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }



    class RecordAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<Record> items;

        public RecordAdapter(Context context, ArrayList<Record> items) {
            super();
            this.inflater = LayoutInflater.from(context);
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {

            v = inflater.inflate(R.layout.list_item_record, null);

            final Record obj = items.get(position);

            TextView tv = (TextView) v.findViewById(R.id.colTime);
            tv.setText(obj.getTime());

            tv = (TextView) v.findViewById(R.id.colWd);
            tv.setText(obj.getWd() + "");

            tv = (TextView) v.findViewById(R.id.colSd);
            tv.setText(obj.getSd() + "");

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return v;
        }
    }

    /** 查询 */
    private void query() {
        QueryRecordFilter filter = getQueryFilter();
        records = RecordManager.get(this).getSome(filter);
        System.out.println("查询数量：" + records.size());
        handler.sendEmptyMessage(QUERY_SUCCESS);
    }

    /** 获取查询条件 */
    private QueryRecordFilter getQueryFilter(){
        QueryRecordFilter filter = new QueryRecordFilter();
        filter.setStartDate(tvDateStart.getText().toString() + " 00:00:00");
        filter.setEndDate(tvDateEnd.getText().toString() + " 23:59:59");

        return filter;
    }

    private JSONArray makeExportData(){
        //查询选定月份的数据
        //ArrayList<Record> records =  RecordManager.get(context).getByMonth(tvMonth.getText().toString());
        //做30次查询，每次查1天，每天查5个时段（必须是5个，如果该时段没有数据，则返回空白对象）
        //2020-07-29 9:00
        //组织5个时间点
        String[] times = new String[5];
        int idx = 0;
        for(CheckBox cbx :cbxs){
            if(cbx.isChecked()){
                String timeStr = cbx.getText().toString(); //15时
                timeStr = timeStr.replace("时", ":00");
                times[idx] = timeStr;
                idx++;
            }
        }

        JSONArray jMonth = new JSONArray();
        //第一个元素是年度、月份
        JSONArray yearMonth = new JSONArray();
        String monthStr = tvMonth.getText().toString();
        if(monthStr == null){
            monthStr = FnDate.getCurrentTime("yyyy-MM");
        }

        String[] strs = monthStr.split("-");
        yearMonth.put(strs[0]);
        yearMonth.put(strs[1]);
        jMonth.put(yearMonth);

        //第二个元素是5个时间点
        JSONArray fiveTimes = new JSONArray();
        for(int i = 0; i < times.length; i++){
            fiveTimes.put(times[i]);
        }
        jMonth.put(fiveTimes);

        String month = tvMonth.getText().toString();

        //获取一个月有几天
        Date dateM = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
        try{
            dateM = simpleDateFormat.parse(month+ "-01"); //把1号加上去，否则没有具体日期会报错
        }catch(Exception e){
            System.out.println();
            e.printStackTrace();
            dateM = new Date();
        }

        int dayNums = FnDate.getDaysOfMonth(dateM);
        System.out.println(month + "的天数：" + dayNums);

        for(int i = 1; i < (dayNums+1); i++){ //31天的
            String date = FnString.fillZero(i, 2); //7 -> 07

            JSONArray jDay = new JSONArray();

            for(int k = 0; k < times.length; k++){

                String time = FnString.fillZero(times[k],5);//1:00 -> 01:00

                String timeStr = month  + "-" + date + " " +  time;//2020-08 -29 9:00
                //做一次查询
                Record record = RecordManager.get(this).getByTime(timeStr);
                System.out.println("查询 " + timeStr + " 结果：" + record);
                if(record!=null){
                    jDay.put(record.toJson());
                }else{
                    jDay.put(Record.newJson(times[k]));
                }

            }

            jMonth.put(jDay);

        }

        return jMonth;
    }

    /**数据导出发送局域网广播的线程；*/
    private class ExportWIFIThread extends Thread {
        JSONArray jMonth;
        public  ExportWIFIThread(JSONArray jMonth){ this.jMonth = jMonth; }

        @Override
        public void run() {
            exportWIFI(jMonth);
        }
    }

    /**通过广播把数据发送到局域网电脑上*/
    private void exportWIFI(JSONArray jMonth) {

        try
        {
            byte[] bytes = jMonth.toString().getBytes();
            String ip = "255.255.255.255";
            int pcPort = 9999;
            InetAddress broadcastAddr = InetAddress.getByName(ip);

            DatagramSocket udpSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, broadcastAddr, pcPort);
            udpSocket.send(packet);

            System.out.println("数据发送完毕->\r\n");
            System.out.println(jMonth.toString());
            handler.sendEmptyMessage(EXPORT_WIFI_SUCCESS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Message msg = Message.obtain();
            msg.what = EXPORT_WIFI_ERROR;
            msg.obj = e.getMessage();
            handler.sendEmptyMessage(EXPORT_WIFI_ERROR);
        }
    }

    /**数据导出发送局域网广播的线程；*/
    private class ExportUSBThread extends Thread {
        JSONArray jMonth;
        public  ExportUSBThread(JSONArray jMonth){ this.jMonth = jMonth; }

        @Override
        public void run() {
            /*
            File template = readTemplate();
            File report = makeReportFile(template, jMonth);

            String reportFileName = "InfoScreen_" +  FnDate.getCurrentTime("yyyyMMddHHmmss")+ ".docx";

            UsbFile cFolder = FnUSB.getUSB(context);

            FnUSB.saveFile2OTG(cFolder, report, reportFileName);
            Toast.makeText(context, "已导出", Toast.LENGTH_LONG).show();
            */

            File template = readTemplate();
            makeReportFile(template, jMonth);


        }
    }

    /**读取word模板*/
    /*
    private File readTemplate(){

        try {

            File template = new File(DT.FILE_PATH + DT.EXPORT_TEMPLATE_NAME);
            if(template.exists()){
                return template;
            }else{
                return null;
            }

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

    }
    */

    /**读取word模板*/
    private File readTemplate(){


        //判断USB目录是否存在
        File usbPath = new File(DT.CTFLY_USB1_PATH);

        if(!usbPath.exists()){
            System.out.println("U盘路径不存在》》");
            return null;
        }

        String path = "";
        try {

            InputStream is = context.getAssets().open(DT.EXPORT_TEMPLATE_NAME);
            String reportFileName = "InfoScreen_" +  FnDate.getCurrentTime("yyyyMMddHHmmss")+ ".docx";

            path = DT.CTFLY_USB1_PATH + "/" + reportFileName;

            System.out.println("文件路径：" + path);
            OutputStream os = new FileOutputStream(path);

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
            Message msg = Message.obtain();
            msg.what = EXPORT_USB_ERROR;
            msg.obj = "生成报告失败 " + e.getMessage() ;
            handler.sendMessage(msg);
        }

        File file = new File(path); //

        if(file.exists()){
            return file;
        }else{
            return null;
        }
    }


    private File makeReportFile(File template, JSONArray objs){
        //String[][] data = ...//初始化数据（m行 * nlie)的表格
        //String fileName = "XXX.docx";
        //String exportPath = "D:\\export"

        try{
            FileInputStream fis = new FileInputStream(template.getAbsolutePath());

            XWPFDocument document = new XWPFDocument(fis);
            XWPFTable table = document.getTables().get(0); //实测，只认表格第一行的列数，因此要再放一个表格

            //Part1 固定内容的替换
            JSONArray yearMonth = objs.getJSONArray(0); //第一个元素是年度和月份
            JSONArray times = objs.getJSONArray(1); //第二个元素是五个时间段

            FnFile.wordTextReplace(document, "<title>", config.getReportTitle());

            FnFile.wordTextReplace(document, "<year>", yearMonth.getString(0));
            FnFile.wordTextReplace(document, "<month>", yearMonth.getString(1));

            FnFile.wordTextReplace(document, "<time1>", times.getString(0));
            FnFile.wordTextReplace(document, "<time2>", times.getString(1));
            FnFile.wordTextReplace(document, "<time3>", times.getString(2));
            FnFile.wordTextReplace(document, "<time4>", times.getString(3));
            FnFile.wordTextReplace(document, "<time5>", times.getString(4));

            //Part2: 循环31天的内容,
            int date = 1;
            for (int i = 2; i < objs.length(); i++)
            {
                try
                {
                    XWPFTableRow row = table.createRow();
                    //手动多加一列（自动添加只认第一行的列数，比第二行少6列，所以手动补上）
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();

                    JSONArray jDay = objs.getJSONArray(i);
                    JSONObject data1 = jDay.getJSONObject(0);
                    JSONObject data2 = jDay.getJSONObject(1);
                    JSONObject data3 = jDay.getJSONObject(2);
                    JSONObject data4 = jDay.getJSONObject(3);
                    JSONObject data5 = jDay.getJSONObject(4);

                    row.getCell(0).setText(date+"");

                    String wd = data1.getString("wd");
                    String sd = data1.getString("sd");
                    row.getCell(1).setText(wd);
                    row.getCell(2).setText(sd);

                    wd = data2.getString("wd");
                    sd = data2.getString("sd");
                    row.getCell(3).setText(wd);
                    row.getCell(4).setText(sd);

                    wd = data3.getString("wd");
                    sd = data3.getString("sd");
                    row.getCell(5).setText(wd);
                    row.getCell(6).setText(sd);

                    wd = data4.getString("wd");
                    sd = data4.getString("sd");
                    row.getCell(7).setText(wd);
                    row.getCell(8).setText(sd);

                    wd = data5.getString("wd");
                    sd = data5.getString("sd");
                    row.getCell(9).setText(wd);
                    row.getCell(10).setText(sd);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = EXPORT_USB_ERROR;
                    msg.obj = "生成报告失败 " + ex.getMessage() ;
                    handler.sendMessage(msg);
                    return null;
                }

                date++;
            }

            //删掉第一个空行
            //table.removeRow(0);

            FileOutputStream fos = new FileOutputStream(template.getAbsolutePath());
            document.write(fos);
            fos.close();

            Message msg = Message.obtain();
            msg.what = EXPORT_USB_SUCCESS;
            handler.sendMessage(msg);

        }catch(Exception ex){
            ex.printStackTrace();
            Message msg = Message.obtain();
            msg.what = EXPORT_USB_ERROR;
            msg.obj = "生成报告失败 " + ex.getMessage();
            handler.sendMessage(msg);
            return null;
        }

        return template;

    }

    private JSONArray toJsonArray(ArrayList<Record> objs){
        JSONArray array = new JSONArray();  DecimalFormat df = new DecimalFormat("0.0");

        try{
            for(Record obj : objs){
                JSONObject json = new JSONObject();
                json.put("time",obj.getTime());
                json.put("wd",obj.getWd());
                json.put("sd",obj.getSd());

                array.put(json);
            }
            return array;
        }catch(Exception e){
            Toast.makeText(context, "导出转换失败：\r\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**6.0以后申请外部存储访问权限*/
    private void reqExtStoragePermission(){

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_PERMISSION_STORAGE = 100;
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                    return;
                }
            }
        }

    }

    private  final int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public void verifyStoragePermissions(Activity activity)
    { // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permission,
                                           int[] grantResults) {
        //requestCode is the third parameter of requestPermissions()
        //permission is the second parameter of requestPermissions()
        //grantResults is the result, 0 is debugged, and -1 is rejected.
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop(){
        super.onStop();
        dlgConfirm.dismiss();
    }

}