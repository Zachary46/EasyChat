package com.itheima.leon.qqdemo.ui.fragment;

import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.itheima.leon.qqdemo.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Author: Zachary
 * Time: 2017/10/13 下午 2:20
 * Desc:
 */

public class MyChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper{

    private String avatar;

    @Override
    public void onSetMessageAttributes(EMMessage message) {

       /* String userName=getActivity().getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);

        getBombUsers(userName,message);
        //设置要发送扩展消息用户昵称
        message.setAttribute("name", userName);*/

    }

    private void getBombUsers(final String name,final EMMessage message) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereNotEqualTo("username", EMClient.getInstance().getCurrentUser());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                for (int i = 0; i <list.size() ; i++) {
                    if (name.equals(list.get(i).getUsername())){
                        avatar=list.get(i).getAvatar();
                        //设置要发送扩展消息用户头像
                        message.setAttribute("avatar", avatar);
                    }
                }
            }
        });
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }
}
