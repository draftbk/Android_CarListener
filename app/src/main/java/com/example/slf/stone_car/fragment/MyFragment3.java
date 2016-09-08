package com.example.slf.stone_car.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.slf.stone_car.Bean.Data;
import com.example.slf.stone_car.R;
import com.example.slf.stone_car.activity.LoginActivity;
import com.example.slf.stone_car.tools.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment3 extends Fragment implements View.OnClickListener {

    private Context context;
    private Activity activity;
    private View view;
    private BluetoothAdapter m_BtAdapter;
    private Button btnOut;
    private final int REQUEST_ENABLE = 1;
    private LinearLayout item1,item2;
    private ArrayList<String> mListStr ;
    private ArrayList<String> mListAddress ;
    private BluetoothDevice device1;
    private BluetoothSocket socket;
    ListView mListView ;
    public MyFragment3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment3,container,false);
        context=getActivity();
        activity=getActivity();
        init();


        return view;
    }

    private void init() {
        item1= (LinearLayout) view.findViewById(R.id.item_1);
        item1.setOnClickListener(this);
        item2= (LinearLayout) view.findViewById(R.id.item_2);
        item2.setOnClickListener(this);
        btnOut= (Button) view.findViewById(R.id.login_out);
        btnOut.setOnClickListener(this);
        mListStr=new ArrayList<String>();
        mListAddress=new ArrayList<String>();
        Bmob.initialize(context, "e9b3f89db94b9b215c686e690ffe7bb7");
    }

    //接收消息的一个监听
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //如果发现了一个蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //我们拿到这个蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //打印出蓝牙的名称和蓝牙地址
                Log.i("info", "devie 蓝牙名称:" + device.getName() + ",蓝牙地址：" + device.getAddress());
                mListStr.add(device.getName() + ":   " + device.getAddress());
                mListAddress.add(device.getAddress());
                mListView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mListStr));
            }
            //搜索完成后
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                //打印搜索完成
                Log.i("info", "devie : 搜索结束");
            }


        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_1:
                m_BtAdapter = BluetoothAdapter.getDefaultAdapter();
                if(m_BtAdapter==null)                 //如果为空可能不存在蓝牙，退出；
                {
                    Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                }

                if(!m_BtAdapter.isEnabled()){
                    //弹出对话框提示用户是后打开
                    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enabler,REQUEST_ENABLE);
                    //不做提示，强行打开
                    // mAdapter.enable();
                }else {
                    showThisDialog();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                context.registerReceiver(mReceiver, filter);
                filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                context.registerReceiver(mReceiver, filter);
                //如果当前本地蓝牙适配器处于搜索设备中
                if(m_BtAdapter.isDiscovering()){
                    //那么取消搜索
                    m_BtAdapter.cancelDiscovery();
                }
                //开始搜索蓝牙设备
                m_BtAdapter.startDiscovery();

                break;
            case R.id.item_2:
                showSettleDialog();
                break;
            case R.id.login_out:
                Intent intent=new Intent(activity, LoginActivity.class);
                startActivity(intent);
                activity.finish();

                break;
        }
    }

    private void showThisDialog() {
        Log.d("test","这里");
        //弹出框
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        mListView= (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, mListStr));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position,
                                    long id) {
                Toast.makeText(context, "您选择了" + mListStr.get(position), Toast.LENGTH_LONG).show();
                m_BtAdapter.cancelDiscovery();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            device1 = m_BtAdapter.getRemoteDevice(mListAddress.get(position));

                            Log.d("test1", 1 + "");
                            byte[] buffer = new byte[1024];
                            int bytes;
                            socket = device1.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                            // 通过socket连接服务器，这是一个阻塞过程，直到连接建立或者连接失效
                            socket.connect();
                            Log.d("test1", socket.isConnected() + ".....socket.isConnected()");
                            Log.d("test1", 2 + "");
                            InputStream is = null;
                            try {

                                while (true) {
                                    Log.d("test1", socket.isConnected() + ".....socket.isConnected()");
                                    is = socket.getInputStream();
                                    Log.d("test1", 3 + "");
                                    String data;
                                    if ((bytes = is.read(buffer)) > 7) {
                                        byte[] buf_data = new byte[bytes];
                                        Log.d("test1", "bytes..." + bytes);
                                        Log.d("test1", "is.available()..." + is.available());
                                        for (int i = 0; i < 8; i++) {
                                            buf_data[i] = buffer[i];
                                            Log.d("test", "buffer..." + i + "..." + buffer[i]);
                                            SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                                            if (i == 7 && buffer[0] == 16 && buffer[7] == 32) {
                                                editor.putString("temp", buffer[1] + "");
                                                editor.putString("level", buffer[2] + "");
                                                editor.commit();
                                                Data data1 = new Data();
                                                data1.setTemp(Integer.valueOf(buffer[1]));
                                                data1.setLevel(Integer.valueOf(buffer[2]));
                                                data1.save(context, new SaveListener() {

                                                    @Override
                                                    public void onSuccess() {
                                                        Log.d("test3", "success!!!");
                                                    }

                                                    @Override
                                                    public void onFailure(int code, String arg0) {
                                                        // 添加失败
                                                    }
                                                });
                                            }

                                        }
                                    }
                                    try {
                                        Thread.sleep(Setting.Intervals);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mListStr.clear();
                mListAddress.clear();
            }
        });
        builder.show();
    }

    private void showSettleDialog() {
        //弹出框
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_settle, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText editTemp,editLevel,editState;
        editTemp= (EditText) view.findViewById(R.id.edit_temp);
        editLevel= (EditText) view.findViewById(R.id.edit_level);
        editState= (EditText) view.findViewById(R.id.edit_state);


        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int temp=70;
                int level=20;
                int state=0;
                if (!editTemp.equals("")){
                    temp = Integer.parseInt(editTemp.getText().toString());
                }
                if (!editLevel.equals("")){
                    level = Integer.parseInt(editLevel.getText().toString());
                }
                if (!editState.equals("")){
                    state = Integer.parseInt(editState.getText().toString());
                }
                if (temp >= 15 && temp <= 99 && level >= 20 && level <= 99 && state >= 0 && state <= 2) {
                    try {
                        OutputStream os = socket.getOutputStream();
                        byte[] up = new byte[7];
                        //char up[] ={0xAA,0xA2,0x01,0,0xA3,1,0xFF};
                        up[0] = (byte) 0xAA;
                        String t1 = Integer.toHexString(temp);
                        String l1 = Integer.toHexString(level);
                        up[1] = (byte) Integer.parseInt(t1, 16);
                        up[2] = (byte) Integer.parseInt(l1, 16);
                        up[3] = (byte) (((0x00+up[1] + up[2]) & 0xff00) >> 8);
                        up[4] = (byte) ((0x00+up[1] + up[2] )&0x00ff);
                        //  up[4]= Byte.parseByte(Integer.toHexString(level+temp));
                        up[5] = Byte.parseByte(Integer.toHexString(state));
                        up[6] = (byte) 0xFF;
                        Log.d("test2", "up[1]" + up[1]);
                        Log.d("test2", "up[2]" + up[2]);
                        Log.d("test2", "up[3]" + up[3]);
                        Log.d("test2", "up[4]" + up[4]);
                        Log.d("test2", "up[5]" + up[5]);
                        os.write(up);
                        Log.d("test2", "eeeeee");
                        SharedPreferences.Editor editor = context.getSharedPreferences("border", Context.MODE_PRIVATE).edit();
                        editor.putInt("level",level);
                        editor.putInt("temp",temp);
                        editor.commit();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "请输入正确值", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }


}
