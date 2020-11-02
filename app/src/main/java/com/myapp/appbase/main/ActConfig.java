package com.myapp.appbase.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.appbase.R;
import com.myapp.appbase.config.Config;
import com.myapp.appbase.config.DT;
import com.myapp.appbase.func.FnApp;
import com.myapp.appbase.func.FnConfig;
import com.myapp.appbase.func.FnDate;
import com.myapp.appbase.func.FnWidget;
import com.myapp.appbase.widget.SpinnerOption;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("ResourceType")
/** 后台设置，供用户自主设置信息
 *  1. 单位名称
 *  2. 需要语音播报的时间点（0点~23点）
 * */
public class ActConfig extends AppCompatActivity {

    Context context;

    //Data
    Config config;
    int[] voiceSet, recordSet;

    //UI
    EditText etCopName, etCityCode;
    CheckBox cbx0,cbx1,cbx2,cbx3,cbx4,cbx5,cbx6,cbx7,cbx8,cbx9,cbx10,cbx11,
             cbx12,cbx13,cbx14,cbx15,cbx16,cbx17,cbx18,cbx19,cbx20,cbx21,cbx22,cbx23;
    CheckBox[] cbxs;

    CheckBox cbxR0,cbxR1,cbxR2,cbxR3,cbxR4,cbxR5,cbxR6,cbxR7,cbxR8,cbxR9,cbxR10,cbxR11,
            cbxR12,cbxR13,cbxR14,cbxR15,cbxR16,cbxR17,cbxR18,cbxR19,cbxR20,cbxR21,cbxR22,cbxR23;
    CheckBox[] cbxRs;

    Button btnQueryRecord, btnSave, btnBack, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TheApplication) getApplication()).addActivity(this);
        context = this;
        setContentView(R.layout.act_config);

        initData();
        initUI();
        bindEvent();

    }

    private void initData() {
        config = FnConfig.getConfig(context);
        voiceSet = new int[24];
        String voiceSetStr = config.getVoiceSet();
        System.out.println("播报设置：" + voiceSetStr);
        String[] array = voiceSetStr.split("-");
        for(int i = 0; i < array.length; i++){
            try{ voiceSet[i] = Integer.parseInt(array[i]);
            }catch(Exception e){ e.printStackTrace(); }
        }

        recordSet = new int[24];
        String recordSetStr = config.getRecordSet();
        System.out.println("记录设置：" + recordSet);
        array = recordSetStr.split("-");
        for(int i = 0; i < array.length; i++){
            try{ recordSet[i] = Integer.parseInt(array[i]);
            }catch(Exception e){ e.printStackTrace(); }
        }
    }

    private void initUI() {
        etCopName = (EditText) findViewById(R.id.etCopName);
        etCopName.setText(config.getCopName());

        etCityCode = (EditText) findViewById(R.id.etCityCode);
        etCityCode.setText(config.getCityCode());

        cbx0 = (CheckBox) findViewById(R.id.cbx0); if(voiceSet[0] == DT.V_YES){ cbx0.setChecked(true);}
        cbx1 = (CheckBox) findViewById(R.id.cbx1); if(voiceSet[1] == DT.V_YES){ cbx1.setChecked(true);}
        cbx2 = (CheckBox) findViewById(R.id.cbx2); if(voiceSet[2] == DT.V_YES){ cbx2.setChecked(true);}
        cbx3 = (CheckBox) findViewById(R.id.cbx3); if(voiceSet[3] == DT.V_YES){ cbx3.setChecked(true);}
        cbx4 = (CheckBox) findViewById(R.id.cbx4); if(voiceSet[4] == DT.V_YES){ cbx4.setChecked(true);}
        cbx5 = (CheckBox) findViewById(R.id.cbx5); if(voiceSet[5] == DT.V_YES){ cbx5.setChecked(true);}
        cbx6 = (CheckBox) findViewById(R.id.cbx6); if(voiceSet[6] == DT.V_YES){ cbx6.setChecked(true);}
        cbx7 = (CheckBox) findViewById(R.id.cbx7); if(voiceSet[7] == DT.V_YES){ cbx7.setChecked(true);}
        cbx8 = (CheckBox) findViewById(R.id.cbx8); if(voiceSet[8] == DT.V_YES){ cbx8.setChecked(true);}
        cbx9 = (CheckBox) findViewById(R.id.cbx9); if(voiceSet[9] == DT.V_YES){ cbx9.setChecked(true);}
        cbx10 = (CheckBox) findViewById(R.id.cbx10); if(voiceSet[10] == DT.V_YES){ cbx10.setChecked(true);}
        cbx11 = (CheckBox) findViewById(R.id.cbx11); if(voiceSet[11] == DT.V_YES){ cbx11.setChecked(true);}
        cbx12 = (CheckBox) findViewById(R.id.cbx12); if(voiceSet[12] == DT.V_YES){ cbx12.setChecked(true);}
        cbx13 = (CheckBox) findViewById(R.id.cbx13); if(voiceSet[13] == DT.V_YES){ cbx13.setChecked(true);}
        cbx14 = (CheckBox) findViewById(R.id.cbx14); if(voiceSet[14] == DT.V_YES){ cbx14.setChecked(true);}
        cbx15 = (CheckBox) findViewById(R.id.cbx15); if(voiceSet[15] == DT.V_YES){ cbx15.setChecked(true);}
        cbx16 = (CheckBox) findViewById(R.id.cbx16); if(voiceSet[16] == DT.V_YES){ cbx16.setChecked(true);}
        cbx17 = (CheckBox) findViewById(R.id.cbx17); if(voiceSet[17] == DT.V_YES){ cbx17.setChecked(true);}
        cbx18 = (CheckBox) findViewById(R.id.cbx18); if(voiceSet[18] == DT.V_YES){ cbx18.setChecked(true);}
        cbx19 = (CheckBox) findViewById(R.id.cbx19); if(voiceSet[19] == DT.V_YES){ cbx19.setChecked(true);}
        cbx20 = (CheckBox) findViewById(R.id.cbx20); if(voiceSet[20] == DT.V_YES){ cbx20.setChecked(true);}
        cbx21 = (CheckBox) findViewById(R.id.cbx21); if(voiceSet[21] == DT.V_YES){ cbx21.setChecked(true);}
        cbx22 = (CheckBox) findViewById(R.id.cbx22); if(voiceSet[22] == DT.V_YES){ cbx22.setChecked(true);}
        cbx23 = (CheckBox) findViewById(R.id.cbx23); if(voiceSet[23] == DT.V_YES){ cbx23.setChecked(true);}

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

        cbxR0 = (CheckBox) findViewById(R.id.cbxR0); if(recordSet[0] == DT.V_YES){ cbxR0.setChecked(true);}
        cbxR1 = (CheckBox) findViewById(R.id.cbxR1); if(recordSet[1] == DT.V_YES){ cbxR1.setChecked(true);}
        cbxR2 = (CheckBox) findViewById(R.id.cbxR2); if(recordSet[2] == DT.V_YES){ cbxR2.setChecked(true);}
        cbxR3 = (CheckBox) findViewById(R.id.cbxR3); if(recordSet[3] == DT.V_YES){ cbxR3.setChecked(true);}
        cbxR4 = (CheckBox) findViewById(R.id.cbxR4); if(recordSet[4] == DT.V_YES){ cbxR4.setChecked(true);}
        cbxR5 = (CheckBox) findViewById(R.id.cbxR5); if(recordSet[5] == DT.V_YES){ cbxR5.setChecked(true);}
        cbxR6 = (CheckBox) findViewById(R.id.cbxR6); if(recordSet[6] == DT.V_YES){ cbxR6.setChecked(true);}
        cbxR7 = (CheckBox) findViewById(R.id.cbxR7); if(recordSet[7] == DT.V_YES){ cbxR7.setChecked(true);}
        cbxR8 = (CheckBox) findViewById(R.id.cbxR8); if(recordSet[8] == DT.V_YES){ cbxR8.setChecked(true);}
        cbxR9 = (CheckBox) findViewById(R.id.cbxR9); if(recordSet[9] == DT.V_YES){ cbxR9.setChecked(true);}
        cbxR10 = (CheckBox) findViewById(R.id.cbxR10); if(recordSet[10] == DT.V_YES){ cbxR10.setChecked(true);}
        cbxR11 = (CheckBox) findViewById(R.id.cbxR11); if(recordSet[11] == DT.V_YES){ cbxR11.setChecked(true);}
        cbxR12 = (CheckBox) findViewById(R.id.cbxR12); if(recordSet[12] == DT.V_YES){ cbxR12.setChecked(true);}
        cbxR13 = (CheckBox) findViewById(R.id.cbxR13); if(recordSet[13] == DT.V_YES){ cbxR13.setChecked(true);}
        cbxR14 = (CheckBox) findViewById(R.id.cbxR14); if(recordSet[14] == DT.V_YES){ cbxR14.setChecked(true);}
        cbxR15 = (CheckBox) findViewById(R.id.cbxR15); if(recordSet[15] == DT.V_YES){ cbxR15.setChecked(true);}
        cbxR16 = (CheckBox) findViewById(R.id.cbxR16); if(recordSet[16] == DT.V_YES){ cbxR16.setChecked(true);}
        cbxR17 = (CheckBox) findViewById(R.id.cbxR17); if(recordSet[17] == DT.V_YES){ cbxR17.setChecked(true);}
        cbxR18 = (CheckBox) findViewById(R.id.cbxR18); if(recordSet[18] == DT.V_YES){ cbxR18.setChecked(true);}
        cbxR19 = (CheckBox) findViewById(R.id.cbxR19); if(recordSet[19] == DT.V_YES){ cbxR19.setChecked(true);}
        cbxR20 = (CheckBox) findViewById(R.id.cbxR20); if(recordSet[20] == DT.V_YES){ cbxR20.setChecked(true);}
        cbxR21 = (CheckBox) findViewById(R.id.cbxR21); if(recordSet[21] == DT.V_YES){ cbxR21.setChecked(true);}
        cbxR22 = (CheckBox) findViewById(R.id.cbxR22); if(recordSet[22] == DT.V_YES){ cbxR22.setChecked(true);}
        cbxR23 = (CheckBox) findViewById(R.id.cbxR23); if(recordSet[23] == DT.V_YES){ cbxR23.setChecked(true);}

        cbxRs = new CheckBox[24];
        cbxRs[0] = cbxR0;
        cbxRs[1] = cbxR1;
        cbxRs[2] = cbxR2;
        cbxRs[3] = cbxR3;
        cbxRs[4] = cbxR4;
        cbxRs[5] = cbxR5;
        cbxRs[6] = cbxR6;
        cbxRs[7] = cbxR7;
        cbxRs[8] = cbxR8;
        cbxRs[9] = cbxR9;
        cbxRs[10] = cbxR10;
        cbxRs[11] = cbxR11;
        cbxRs[12] = cbxR12;
        cbxRs[13] = cbxR13;
        cbxRs[14] = cbxR14;
        cbxRs[15] = cbxR15;
        cbxRs[16] = cbxR16;
        cbxRs[17] = cbxR17;
        cbxRs[18] = cbxR18;
        cbxRs[19] = cbxR19;
        cbxRs[20] = cbxR20;
        cbxRs[21] = cbxR21;
        cbxRs[22] = cbxR22;
        cbxRs[23] = cbxR23;

        btnQueryRecord = (Button) findViewById(R.id.btnQueryRecord);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnExit = (Button) findViewById(R.id.btnExit);
    }

    private void bindEvent() {

        btnQueryRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(context, ActRecord.class));

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                config.setCopName(etCopName.getText().toString());
                config.setCityCode(etCityCode.getText().toString());
                //"0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0"
                String voiceSetStr = "";
                for(int i = 0; i < 24; i++){
                    int set = cbxs[i].isChecked() ? DT.V_YES : DT.V_NO;
                    voiceSetStr += "-" + set;
                }
                voiceSetStr = voiceSetStr.substring(1);
                System.out.println("播报设置：" + voiceSetStr);
                config.setVoiceSet(voiceSetStr);

                String recordStr = "";
                for(int i = 0; i < 24; i++){
                    int set = cbxRs[i].isChecked() ? DT.V_YES : DT.V_NO;
                    recordStr += "-" + set;
                }
                recordStr = recordStr .substring(1);
                System.out.println("记录设置：" + recordStr);
                config.setRecordSet(recordStr);


                FnConfig.saveConfig(context, config);
                Toast.makeText(context, "已保存", Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               showBar();
                ((TheApplication)getApplication()).onTerminate();
            }
        });
    }

    /** 展示导航栏 */
    public void showBar(){

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

}