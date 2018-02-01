package com.itheima.leon.qqdemo.model;

import org.litepal.crud.DataSupport;

/**
 * Author: Zachary
 * Time: 2018/01/29 下午 2:15
 * Desc:Litepal表实体类
 */

public class MyUser extends DataSupport{
    public String username;
    public String avatar;
    public boolean isContact;//是否是好友
}
