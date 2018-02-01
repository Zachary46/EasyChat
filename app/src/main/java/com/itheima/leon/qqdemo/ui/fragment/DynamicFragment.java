package com.itheima.leon.qqdemo.ui.fragment;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.itheima.leon.qqdemo.R;
import com.itheima.leon.qqdemo.adpater.EMCallBackAdapter;
import com.itheima.leon.qqdemo.model.MyUser;
import com.itheima.leon.qqdemo.ui.activity.LoginActivity;
import com.itheima.leon.qqdemo.utils.ThreadUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/17 22:39
 * 描述：    TODO
 */
public class DynamicFragment extends BaseFragment{
    public static final String TAG = "DynamicFragment";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.logout)
    Button mLogout;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.avatar)
    ImageView mAvatar;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_dynamic;
    }

    @Override
    protected void init() {
        super.init();
        mName.setText(EMClient.getInstance().getCurrentUser());
        mTitle.setText(getString(R.string.dynamic));

        List<MyUser> myUsers= DataSupport.findAll(MyUser.class);
        for (int i = 0; i <myUsers.size() ; i++) {
            if (EMClient.getInstance().getCurrentUser().equals(myUsers.get(i).username)){
                Glide.with(this).load(myUsers.get(i).avatar).into(mAvatar);
            }
        }
    }

    @OnClick(R.id.logout)
    public void onClick() {
        showProgress(getString(R.string.logouting));
        EMClient.getInstance().logout(true, mEMCallBackAdapter);
    }


    private EMCallBackAdapter mEMCallBackAdapter = new EMCallBackAdapter() {

        @Override
        public void onSuccess() {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    toast(getString(R.string.logout_success));
                    startActivity(LoginActivity.class, true);
                }
            });
        }

        @Override
        public void onError(int i, String s) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    toast(getString(R.string.logout_failed));
                }
            });
        }
    };
}
