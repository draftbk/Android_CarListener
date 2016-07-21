package com.example.slf.stone_car.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by slf on 16/6/9.
 */
public class Data extends BmobObject {
    private Integer temp;
    private Integer level;

    public Integer getTemp() {
        return temp;
    }

    public void setTemp(Integer temp) {
        this.temp = temp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
