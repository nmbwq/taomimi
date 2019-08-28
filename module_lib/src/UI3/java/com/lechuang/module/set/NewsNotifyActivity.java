package java.com.lechuang.module.set;

import android.app.NotificationManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.utils.NotificationUtils;
import com.lechuang.module.R;

@Route(path = ARouters.PATH_NEW_NOTIFY)
public class NewsNotifyActivity extends BaseActivity implements View.OnClickListener {

    private Switch mSwitchNotifyShouYi,mSwitchNotifyFans;
    private NotificationUtils mNotificationUtils;
    private TextView mTvNotifyState;
    private LinearLayout mLLNotifyFans,mLLNotifyShouYi;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_notify;
    }

    @Override
    protected void findViews() {
        ((TextView)$( R.id.iv_common_title)).setText("消息提醒");
        mSwitchNotifyShouYi = $( R.id.switch_notify_shouyi);
        mSwitchNotifyFans = $( R.id.switch_notify_fensi);
        mTvNotifyState = $( R.id.tv_notify_state);
        mLLNotifyFans = $( R.id.ll_notify_fans);
        mLLNotifyShouYi = $( R.id.ll_notify_shouyi);
        $( R.id.iv_common_back).setOnClickListener(this);
        mTvNotifyState.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mNotificationUtils = new NotificationUtils (this);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//8.0显示
            mLLNotifyFans.setVisibility(View.VISIBLE);
            mLLNotifyShouYi.setVisibility(View.VISIBLE);
        }else {//非8.0隐藏
            mLLNotifyFans.setVisibility(View.GONE);
            mLLNotifyShouYi.setVisibility(View.GONE);
        }

        if (mNotificationUtils == null){
            mNotificationUtils = new NotificationUtils (this);
        }

        if (!mNotificationUtils.isNotificationEnabled(this)){
            mTvNotifyState.setText("点击开启");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//8.0设置
                mSwitchNotifyShouYi.setChecked(false);
                mSwitchNotifyFans.setChecked(false);
            }

        }else {
            mTvNotifyState.setText("已开启");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = mNotificationUtils.getNotificationChannel(mNotificationUtils.channelId_shouyi).getImportance();
                if (importance == NotificationManager.IMPORTANCE_NONE){
                    mSwitchNotifyShouYi.setChecked(false);
                }else {
                    mSwitchNotifyShouYi.setChecked(true);
                }

                importance = mNotificationUtils.getNotificationChannel(mNotificationUtils.channelId_new_fans).getImportance();
                if (importance == NotificationManager.IMPORTANCE_NONE){
                    mSwitchNotifyFans.setChecked(false);
                }else {
                    mSwitchNotifyFans.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int  id = v.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id == R.id.tv_notify_state){
            if (TextUtils.equals(mTvNotifyState.getText(),"点击开启")){
                //打开系统通知界面，弹出对话框
                mNotificationUtils.openSystemNotify();
            }else {
                toast("已开启");
            }
        }
    }
}
