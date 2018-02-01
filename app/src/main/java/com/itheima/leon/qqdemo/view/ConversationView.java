package com.itheima.leon.qqdemo.view;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/21 9:53
 * 描述：    TODO
 */

public interface ConversationView {

    void onAllConversationsLoaded(List<EMConversation> list);
}
