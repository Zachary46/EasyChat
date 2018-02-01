package com.itheima.leon.qqdemo.presenter.impl;

import android.content.ContentValues;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.leon.qqdemo.model.MyUser;
import com.itheima.leon.qqdemo.model.ContactListItem;
import com.itheima.leon.qqdemo.model.User;
import com.itheima.leon.qqdemo.presenter.ContactPresenter;
import com.itheima.leon.qqdemo.utils.ThreadUtils;
import com.itheima.leon.qqdemo.view.ContactView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/18 15:34
 * 描述：    TODO
 */
public class ContactPresenterImpl implements ContactPresenter {
    private static final String TAG = "ContactPresenterImpl";

    private ContactView mContactView;



    public ContactPresenterImpl(ContactView contactView) {
        mContactView = contactView;


    }


    @Override
    public void refreshContactList() {
        getContactsFromServer();
    }

    @Override
    public void deleteFriend(final String name) {
        ThreadUtils.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(name);
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onDeleteFriendSuccess();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onDeleteFriendFailed();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getContactsFromServer() {

        ThreadUtils.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                try {
                    startGetContactList();

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    notifyGetContactListFailed();
                }
            }
        });

    }

    private void notifyGetContactListFailed() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContactView.onGetContactListFailed();
            }
        });
    }

    /**
     * 获取联系人列表数据
     * @throws HyphenateException
     */
    private void startGetContactList() throws HyphenateException {
        List<String> contacts = EMClient.getInstance().contactManager().getAllContactsFromServer();
        Collections.sort(contacts, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.charAt(0) - o2.charAt(0);//ascending order
            }
        });
        //DatabaseManager.getInstance().deleteAllContacts();

        //--------------------------
        getBombUsers(contacts);


    }

    private void comliper(List<String> contacts,List<User> userList) {
        Log.d("====66====", "====contacts.size()====="+contacts.size());
        final List<ContactListItem> mContactListItems=new ArrayList<>();
        if (!contacts.isEmpty()) {
            for (int i = 0; i < contacts.size(); i++) {
                ContactListItem item = new ContactListItem();
                for (int j = 0; j <userList.size() ; j++) {
                    if (contacts.get(i).equals(userList.get(j).getUsername())){
                        item.avatar=userList.get(j).getAvatar();
                        Log.d("====66====", "====avatar====="+userList.get(j).getAvatar());
                    }
                }
                item.userName = contacts.get(i);
                if (itemInSameGroup(i, item, mContactListItems)) {
                    item.showFirstLetter = false;
                }
                mContactListItems.add(item);

                //保存到db,添加好友列表用于区分是否已添加好友
                saveContactToDatabase(item.userName);

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactView.onGetContactListSuccess(mContactListItems);
                    }
                });
            }
        }
    }

    private void getBombUsers(final List<String> contacts) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereNotEqualTo("username", EMClient.getInstance().getCurrentUser());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                comliper(contacts,list);
            }
        });
    }

    private void saveContactToDatabase(String userName) {
        ContentValues values = new ContentValues();
        values.put("isContact", true);
        DataSupport.updateAll(MyUser.class, values, "username = ?",userName);
        //DatabaseManager.getInstance().saveContact(userName);
    }

    /**
     * 当前联系人跟上个联系人比较，如果首字符相同则返回true
     * @param i 当前联系人下标
     * @param item 当前联系人数据模型
     * @return true 表示当前联系人和上一联系人在同一组
     */
    private boolean itemInSameGroup(int i, ContactListItem item, List<ContactListItem> contactListItems) {
        return i > 0 && (item.getFirstLetter() == contactListItems.get(i - 1).getFirstLetter());
    }


}
