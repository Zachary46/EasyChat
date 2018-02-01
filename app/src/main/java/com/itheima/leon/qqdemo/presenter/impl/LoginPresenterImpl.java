package com.itheima.leon.qqdemo.presenter.impl;

import android.content.ContentValues;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.itheima.leon.qqdemo.adpater.EMCallBackAdapter;
import com.itheima.leon.qqdemo.model.MyUser;
import com.itheima.leon.qqdemo.model.User;
import com.itheima.leon.qqdemo.presenter.LoginPresenter;
import com.itheima.leon.qqdemo.utils.StringUtils;
import com.itheima.leon.qqdemo.utils.ThreadUtils;
import com.itheima.leon.qqdemo.view.LoginView;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/16 21:17
 * 描述：    TODO
 */
public class LoginPresenterImpl implements LoginPresenter {
    public static final String TAG = "LoginPresenterImpl";


    public LoginView mLoginView;

    public LoginPresenterImpl(LoginView loginView) {
        mLoginView = loginView;
    }

    @Override
    public void login(String userName, String pwd) {
        if (StringUtils.checkUserName(userName)) {
            if (StringUtils.checkPassword(pwd)) {
                mLoginView.onStartLogin();
                startLogin(userName, pwd);
            } else {
                mLoginView.onPasswordError();
            }
        } else {
            mLoginView.onUserNameError();
        }
    }

    private void startLogin(String userName, String pwd) {
        EMClient.getInstance().login(userName, pwd, mEMCallBack);
    }

    private EMCallBackAdapter mEMCallBack = new EMCallBackAdapter() {

        @Override
        public void onSuccess() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveBombUserToDB();
                }
            }).start();

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoginView.onLoginSuccess();

                }
            });



        }

        @Override
        public void onError(int i, String s) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoginView.onLoginFailed();
                }
            });
        }
    };

    private void saveBombUserToDB() {
        BmobQuery<User> query = new BmobQuery<User>();
        final List<MyUser> myUsers = DataSupport.findAll(MyUser.class);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                for (int i = 0; i <list.size() ; i++) {
                    //有数据才能有更新的操作
                    if (myUsers.size()>0){
                        Log.i("66666","==========》0=============");
                        for (int j = 0; j <myUsers.size() ; j++) {
                            //存在就更改，不存在就创建，防止重复添加
                            if (list.get(i).getUsername().equals(myUsers.get(j).username)){
                                ContentValues values = new ContentValues();
                                values.put("avatar", list.get(i).getAvatar());
                                DataSupport.updateAll(MyUser.class,values,"username = ?",myUsers.get(j).username);
                            }else {
                                MyUser myUser=new MyUser();
                                myUser.username=list.get(i).getUsername();
                                myUser.avatar=list.get(i).getAvatar();
                                myUser.save();
                            }
                        }
                    }else {
                        Log.i("66666","==========《0=============");
                        MyUser myUser=new MyUser();
                        myUser.username=list.get(i).getUsername();
                        myUser.avatar=list.get(i).getAvatar();
                        myUser.save();
                    }

                }

            }
        });
    }

}
