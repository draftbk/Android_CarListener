package com.example.slf.stone_car.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slf.stone_car.Bean.Data;
import com.example.slf.stone_car.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment2 extends Fragment implements View.OnClickListener {
    private List<Data> dataArrayList;
    private TextView textRefresh;

    public MyFragment2() {
    }

    private LineChart mLineChart,mLineChart2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2,container,false);
        mLineChart= (LineChart) view.findViewById(R.id.spread_line_chart);
        mLineChart2= (LineChart) view.findViewById(R.id.spread_line_chart2);
        textRefresh= (TextView) view.findViewById(R.id.text_refresh);
        textRefresh.setOnClickListener(this);
        dataArrayList=new ArrayList<Data>();
        getList();
        return view;
    }

    private void getList() {
        //初始化bmob
        Bmob.initialize(getContext(), "e9b3f89db94b9b215c686e690ffe7bb7");
        BmobQuery<Data> query = new BmobQuery<Data>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(10);
        //这...竟然是负的
        query.order("-updatedAt");
//执行查询方法
        query.findObjects(getContext(),new FindListener<Data>() {
            @Override
            public void onSuccess(List<Data> list) {
                dataArrayList=list;
                for (Data date : list) {
                    Log.d("test","date.getLevel().."+date.getLevel());
                    Log.d("test","date.getTemp().."+date.getTemp());
                    Log.d("test","date.getCreatedAt().."+date.getCreatedAt());
                }
                LineData mLineData=getLineData(10,50,"温度变化图");
                showChart(mLineChart, mLineData, Color.rgb(114, 188, 223));
                LineData mLineData2=getLineData(10,50,"水量变化图");
                showChart(mLineChart2, mLineData2, Color.rgb(114, 188, 223));
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(),"获取数据失败",Toast.LENGTH_SHORT).show();
            }


        });
    }

    /**
     * 生成一个数据
     * @param count 表示图表中有多少个坐标点
     * @param range 用来生成range以内的随机数
     * @return
     */
    private LineData getLineData(int count, float range,String name) {
        ArrayList<String> xValues = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add("" + i);
        }

        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float value=0.0f;
            if (name.equals("温度变化图")){
                value= (float)dataArrayList.get(i).getTemp() ;
            }else if (name.equals("水量变化图")){
                value= 130-(float)dataArrayList.get(i).getLevel() ;
            }
            yValues.add(new Entry(value, i));
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, name /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        return lineData;
    }

    // 设置显示的样式
    private void showChart(LineChart lineChart, LineData lineData, int color) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框

        // no description text
        lineChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);//

        lineChart.setBackgroundColor(color);// 设置背景

        // add data
        lineChart.setData(lineData); // 设置数据

        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体

        mLegend.setTextColor(Color.WHITE);// 颜色
//      mLegend.setTypeface(mTf);// 字体

        lineChart.animateX(2500); // 立即执行的动画,x轴
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_refresh:
                getList();
                break;
        }
    }
}
