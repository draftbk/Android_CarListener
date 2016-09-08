package com.example.slf.stone_car.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slf.stone_car.R;
import com.example.slf.stone_car.activity.MainActivity;
import com.example.slf.stone_car.tools.Setting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;


/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment1 extends Fragment implements View.OnClickListener {

    private Context context;
    private Activity activity;
    private View view;
    private TextView textTemp,textLevel,textTime,textState,textYuZhi;
    private String temp,level;
    private Handler han;

    public MyFragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1, container, false);
        context=getActivity();
        activity=getActivity();
        init();
        changeListener();
        return view;
    }

    private void changeListener() {
        //开线程监听
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {

                        Message mess=new Message();
                        mess.what=1;
                        han.sendMessage(mess);
                        Thread.sleep(Setting.Intervals);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        }).start();
        //主线程变换UI

    }

    private void init() {
        textTemp= (TextView) view.findViewById(R.id.temp);
        textLevel= (TextView) view.findViewById(R.id.level);
        textTime= (TextView) view.findViewById(R.id.time);
        textState= (TextView) view.findViewById(R.id.state);
        textYuZhi=(TextView) view.findViewById(R.id.yuzhi);
        //用handler来监听
        han = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (msg.what==1){
                    SharedPreferences pref_part=context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences border=context.getSharedPreferences("border", Context.MODE_PRIVATE);
                    temp=pref_part.getString("temp", "0");
                    level=pref_part.getString("level", "0");
                    textTemp.setText(temp+"℃");
                    int level_true= 130-Integer.parseInt(level);
                    int temp_true=Integer.parseInt(temp);
                    String yuString="水位阈值: "+border.getInt("level",110)+" cm\\n温度阈值: "+border.getInt("temp",70)+" ℃";
                    textYuZhi.setText(yuString);
                    if (level_true<border.getInt("level",110)){
                        textState.setText("水位过低");
                        textLevel.setText(textLevel.getText().toString()+"(过低)");
                    }
                    if (temp_true>border.getInt("temp",70)){
                        textState.setText("温度过高");
                    }
                    if (temp_true<=border.getInt("temp",70)&&level_true>=border.getInt("level",110)){
                        textState.setText("正常");
                        textLevel.setText(level_true+"CM");
                    }
                    textLevel.setText(level_true+"CM");
                    int time= Integer.valueOf(level_true)/7;
                    String t= String.valueOf(time);
                    textTime.setText(t+" H");
                }
            }


        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }



}
