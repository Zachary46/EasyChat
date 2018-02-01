package com.itheima.leon.qqdemo.view;

import com.itheima.leon.qqdemo.model.ContactListItem;

import java.util.List;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/18 15:27
 * 描述：    TODO
 */
public interface ContactView {

    void onGetContactListSuccess(List<ContactListItem> list);

    void onGetContactListFailed();

    void onDeleteFriendSuccess();

    void onDeleteFriendFailed();
}
