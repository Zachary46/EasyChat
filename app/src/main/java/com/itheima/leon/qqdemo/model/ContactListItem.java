package com.itheima.leon.qqdemo.model;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/18 12:10
 * 描述：    TODO
 */
public class ContactListItem {
    public static final String TAG = "ContactListItem";

    public String avatar;

    public String userName;

    public boolean showFirstLetter = true;

    public char getFirstLetter() {
        return userName.charAt(0);
    }

    public String getFirstLetterString() {
        return String.valueOf(getFirstLetter()).toUpperCase();
    }
}
