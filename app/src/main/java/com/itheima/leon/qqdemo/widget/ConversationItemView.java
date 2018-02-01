package com.itheima.leon.qqdemo.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.util.DateUtils;
import com.itheima.leon.qqdemo.R;
import com.itheima.leon.qqdemo.model.User;
import com.itheima.leon.qqdemo.ui.activity.EasyChatActivity;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * 创建者:   Leon
 * 创建时间:  2016/10/21 10:20
 * 描述：    TODO
 */
public class ConversationItemView extends RelativeLayout {
    public static final String TAG = "ConversationItemView";
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.last_message)
    TextView mLastMessage;
    @BindView(R.id.timestamp)
    TextView mTimestamp;
    @BindView(R.id.unread_count)
    TextView mUnreadCount;
    @BindView(R.id.conversation_item_container)
    RelativeLayout mConversationItemContainer;

    public ConversationItemView(Context context) {
        this(context, null);
    }

    public ConversationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_conversation_item, this);
        ButterKnife.bind(this, this);

    }

    private void getBombUsers(final String name) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereNotEqualTo("username", EMClient.getInstance().getCurrentUser());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                for (int i = 0; i <list.size() ; i++) {
                    if (name.equals(list.get(i).getUsername())){
                        Glide.with(getContext()).load(list.get(i).getAvatar()).into(mAvatar);
                    }
                }
            }
        });
    }

    public void bindView(final EMConversation emConversation) {
        mUserName.setText(emConversation.conversationId());

        /**
         * 环信好友列表与服务器数据对比获取头像
         * */
        getBombUsers(emConversation.conversationId());

        updateLastMessage(emConversation);
        updateUnreadCount(emConversation);
        mConversationItemContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), ChatActivity.class);
                //intent.putExtra(Constant.Extra.USER_NAME, emConversation.conversationId());
                Intent intent = new Intent(getContext(), EasyChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emConversation.conversationId());
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat);

                getContext().startActivity(intent);
            }
        });
    }

    private void updateLastMessage(EMConversation emConversation) {
        EMMessage emMessage = emConversation.getLastMessage();
        if (emMessage.getBody() instanceof EMTextMessageBody) {
            mLastMessage.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
        } else {
            mLastMessage.setText(getContext().getString(R.string.no_text_message));
        }
        mTimestamp.setText(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
    }

    private void updateUnreadCount(EMConversation emConversation) {
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        if (unreadMsgCount > 0) {
            mUnreadCount.setVisibility(VISIBLE);
            mUnreadCount.setText(String.valueOf(unreadMsgCount));
        } else {
            mUnreadCount.setVisibility(GONE);
        }
    }
}
