package com.itheima.leon.qqdemo.presenter;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.itheima.leon.qqdemo.BuildConfig;
import com.itheima.leon.qqdemo.R;
import com.itheima.leon.qqdemo.adpater.EMMessageListenerAdapter;
import com.itheima.leon.qqdemo.app.Constant;
import com.itheima.leon.qqdemo.model.MyUser;
import com.itheima.leon.qqdemo.ui.activity.ChatActivity;

import org.litepal.crud.DataSupport;

import java.util.Iterator;
import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 *Author:Zachary
 *Time:2018/1/29 0029 下午 4:22
 *Dec:环信初始化管理类
 *Call:0592-3278796
 *Email:developer@baogukeji.com
 *Web:www.baogukeji.com
 */
public class HuanXinPresenter {
    private Context mContext;
    private int mDuanSound;
    private int mYuluSound;
    private SoundPool mSoundPool;
    public static final String TAG = "HuanXinPresenter";
    private static HuanXinPresenter sInstance;

    public static HuanXinPresenter getInstance() {
        if (sInstance == null) {
            synchronized (HuanXinPresenter.class) {
                if (sInstance == null) {
                    sInstance = new HuanXinPresenter();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext=context;
        initHuanXin();
        initSoundPool();
    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        mDuanSound = mSoundPool.load(mContext, R.raw.duan, 1);
        mYuluSound = mSoundPool.load(mContext, R.raw.yulu, 1);
    }

    private void initHuanXin() {

        EMOptions options = new EMOptions();
        //默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null ||!processAppName.equalsIgnoreCase(mContext.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化
        EMClient.getInstance().init(getApplicationContext(), options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        if (BuildConfig.DEBUG) {
            EMClient.getInstance().setDebugMode(true);
        }

        EaseUI.getInstance().init(mContext, null);

        //用户信息配置
        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //设置聊天消息监听
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListenerAdapter);
    }

    //进入聊天室会调用这个方法
    private EaseUser getUserInfo(String username){
        EaseUser user = null;
        List<MyUser> myUsers = DataSupport.findAll(MyUser.class);
        Log.i("66666",username+"==========myUsers.size()============="+myUsers.size());
        if (!TextUtils.isEmpty(username))
            for (int i = 0; i <myUsers.size() ; i++) {
                if (!TextUtils.isEmpty(username)){
                    if (username.equals(myUsers.get(i).username)){
                        user=new EaseUser(username);
                        user.setAvatar(myUsers.get(i).avatar);
                        return user;
                    }
                }
            }

        //如果用户不是你的联系人，则进行初始化
        if(user == null){
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }else {
            //如果名字为空，则显示环信号码
            if (TextUtils.isEmpty(user.getAvatar())){
                user.setNick(user.getUsername());
            }
        }
        return user;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = mContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {

            }
        }
        return processName;
    }

    private EMMessageListenerAdapter mEMMessageListenerAdapter = new EMMessageListenerAdapter() {

        @Override
        public void onMessageReceived(List<EMMessage> list) {
            if (isForeground()) {
                mSoundPool.play(mDuanSound, 1, 1, 0, 0, 1);
            } else {
                mSoundPool.play(mYuluSound, 1, 1, 0, 0, 1);
                showNotification(list.get(0));
            }

            for (EMMessage message : list) {
                EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());

            }
        }
    };


    private void showNotification(EMMessage emMessage) {
        String contentText = "";
        if (emMessage.getBody() instanceof EMTextMessageBody) {
            contentText = ((EMTextMessageBody) emMessage.getBody()).getMessage();
        }

        Intent chat = new Intent(mContext, ChatActivity.class);
        chat.putExtra(Constant.Extra.USER_NAME, emMessage.getUserName());
        PendingIntent pendingIntent = TaskStackBuilder.create(mContext)
                .addParentStack(ChatActivity.class)
                .addNextIntent(chat)
                .getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(mContext)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.avatar1))
                .setSmallIcon(R.mipmap.ic_contact_selected_2)
                .setContentTitle(mContext.getString(R.string.receive_new_message))
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }

    public boolean isForeground() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo info :runningAppProcesses) {
            if (info.processName.equals(mContext.getPackageName()) && info.importance == IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}

