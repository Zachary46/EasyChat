package com.itheima.leon.qqdemo.model;

import cn.bmob.v3.BmobUser;

/**
 *Author:Zachary
 *Time:2018/1/29 0029 下午 5:06
 *Dec:Bomb云端数据实体类
 *Call:0592-3278796
 *Email:developer@baogukeji.com
 *Web:www.baogukeji.com
 */
public class User extends BmobUser {

    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



}
