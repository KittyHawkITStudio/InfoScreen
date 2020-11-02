package com.myapp.appbase.func;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**操作串口读写 用于获取温湿度传感器数据 SHT20*/
public class FnSerialPort {

    static byte[] ORDER_REQ = {0x01, 0x03, 0x00, 0x02, 0x00, 0x02, 0x65, (byte)0xCB};

    String ENTER = "\r\n";
    int EXP_RESP_LENGTH = 31;

     SerialPort mSerialPort; // 奇智类
     OutputStream outputStream;
     InputStream inputStream;

     //老传感器
     public String info; //当前收到的信息
     //新传感器
     public double wd, sd;


    int size = 0;
    /**
     * 判断线程是否开启
     **/
    public  boolean isPortOpen = false;

    public FnSerialPort( Context context) {

        info = "";

        {
            // 创建串口对象
            try {
                // mSerialPort = new SerialPort(new File("/dev/ttyS0"), 19200,
                // 0);
                //mSerialPort = new SerialPort(new File("/dev/ttyS2"), 115200, 0);
                //新传感器，固定ttyS3
                //mSerialPort = new SerialPort(new File("/dev/ttyS3"), 9600, 0); //测试用S3
                mSerialPort = new SerialPort(new File("/dev/ttyS4"), 9600, 0); //实际用S4
                // 获取输出流、向外设输出数据
                if (outputStream == null) {
                    outputStream = mSerialPort.getOutputStream();
                }
                // 串口对象获取输入流、接收到的数据
                if (inputStream == null) {
                    inputStream = mSerialPort.getInputStream();
                }
                //
            } catch (NoClassDefFoundError e) {
                Toast.makeText(context, "配置错误！请检查ADM", Toast.LENGTH_LONG)
                        .show();
            } catch (ExceptionInInitializerError e) {
                Toast.makeText(context, "配置错误！请检查ADM", Toast.LENGTH_LONG)
                        .show();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 开启线程
         */
        isPortOpen = true;

    }

    /** 开始监听温度湿度串口数据 */
    public  void startListenWDSD(){
        System.out.println("开始监听温湿度信息>>");
            new WDSDThread().start();
            new ReqThread().start();
    }

    /** 请求传感器数据*/
    public void reqWDSD(){
        try {
            if (mSerialPort!=null){ mSerialPort.getOutputStream().write(ORDER_REQ); };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  class ReqThread extends Thread {
        public synchronized void run() {

            reqWDSD();
            try {
                Thread.sleep(2000);
                new ReqThread().start(); //等待2秒之后，继续请求
            } catch (Exception e) { e.printStackTrace(); }
        }


    }


    /** 新版本传感器，主动请求，进行串口通信的类, 以ASCII方式处理 */
    private  class WDSDThread extends Thread {
        public synchronized void run() {
            if (!isInterrupted()) {
                byte[] buf = new byte[1000];
                while (isPortOpen) {

                    try {
                        if (isPortOpen == true && mSerialPort.getInputStream().available() == 0) { continue; }
                        int size = mSerialPort.getInputStream().read(buf);
                         System.out.println("收到数据位数：" + size);

                        //01 03 04，温度H，温度L，湿度H，湿度L，CRC_L，CRC_H
                        //01 03 04  01 39       01 80        28 F2
                        //1. 判断数据格式是否正确
                        if(size!=9 || buf[0]!=01 || buf[1]!=03 || buf[2]!=04){
                            continue;
                        }

                        //for test
                        /*
                        String str = "";
                        for(int i = 0; i < 9; i++){
                            str += Integer.toHexString(buf[i]&0xff) + " ";
                        }
                        System.out.println("buf: " + str);
                        */

                        //2. 取温湿度
                        wd = twoInt2OneInt(buf[3]&0xff, buf[4]&0xff)/10.0;
                        sd = twoInt2OneInt(buf[5]&0xff, buf[6]&0xff)/10.0;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isPortOpen ==false){ return; }

                }

            }
        }


    }

    /** 老版本传感器，被动监听，进行串口通信的类, 以ASCII方式处理 */
    /*
    private  class WDSDThread extends Thread {
        public synchronized void run() {
            if (!isInterrupted()) {
                byte[] buf = new byte[1000];
                byte[] bufTemp = new byte[1000];
                while (isPortOpen) {

                    try {
                        if (isPortOpen == true && mSerialPort.getInputStream().available() == 0) { continue; }
                        int size = mSerialPort.getInputStream().read(bufTemp);
                        byte[] bytesForStr = new byte[size];
                        for(int i = 0; i < size; i++){
                            bytesForStr[i] = bufTemp[i];
                            //System.out.print(new Integer(bufTemp[i]).toString);
                        }

                        String str = new String(bytesForStr, "UTF-8");

                        //Temp = 30.180   Humi = 64.870
                        //Temp = 30.180   Humi = 64.870
                        //Temp = 30.180   Humi = 64.870 //长度31

                        //>>>>>  30.180  30.180
                        //System.out.println("收到数据"+str.length()+"位：" + str);

                        if(str.length() == EXP_RESP_LENGTH){
                            info = str;
                        }

                        //System.out.println("收到数据 位数：" + size + "  内容" + str);
                        if(info.contains(ENTER)){
                            //System.out.print(" >> info: " + info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isPortOpen ==false){ return; }

                }

            }
        }

    }
    */

    /** 两个十六进制字符串拼接转换成一个数字 */
    private int twoInt2OneInt(int a, int b) {

        String strA = Integer.toHexString(a); if(strA.length() < 2) {strA = "0" + strA;}
        String strB = Integer.toHexString(b); if(strB.length() < 2) {strB = "0" + strB;}

        String str = strA + strB;

        return Integer.parseInt(str, 16);
    }

}
