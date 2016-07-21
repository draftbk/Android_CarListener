package com.example.slf.stone_car.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by slf on 16/6/9.
 */
public class _User extends BmobObject{
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
