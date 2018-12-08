package com.znt.vodbox.executor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.AboutActivity;
import com.znt.vodbox.activity.AdListActivity;
import com.znt.vodbox.activity.AllPlanListActivity;
import com.znt.vodbox.activity.GroupListActivity;
import com.znt.vodbox.activity.MusicActivity;
import com.znt.vodbox.activity.MyAlbumActivity;
import com.znt.vodbox.activity.SystemAlbumActivity;
import com.znt.vodbox.service.QuitTimer;
import com.znt.vodbox.storage.preference.Preferences;
import com.znt.vodbox.utils.ToastUtils;


/**
 * 导航菜单执行器
 * Created by hzwangchenyan on 2016/1/14.
 */
public class NaviMenuExecutor {
    private Activity activity;

    public NaviMenuExecutor(Activity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_all_plan:
                startActivity(AllPlanListActivity.class);
                return true;
            case R.id.action_my_zones:
                startActivity(GroupListActivity.class);
                return true;
            case R.id.action_my_albums:
                //startActivity(SettingActivity.class);
                startActivity(MyAlbumActivity.class);
                return true;
            case R.id.action_sys_albums:
                //startActivity(SettingActivity.class);
                startActivity(SystemAlbumActivity.class);
                return true;
            case R.id.action_setting:
                //startActivity(SettingActivity.class);
                startActivity(AdListActivity.class);
                return true;
            case R.id.action_night:
                nightMode();
                break;
            case R.id.action_timer:
                timerDialog();
                return true;
            case R.id.action_exit:
                /*activity.finish();
                PlayService.startCommand(activity, Actions.ACTION_STOP);*/

                return true;
            case R.id.action_about:
                startActivity(AboutActivity.class);
                return true;
        }
        return false;
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    private void nightMode() {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.recreate();
    }

    private void timerDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_timer)
                .setItems(activity.getResources().getStringArray(R.array.timer_text), (dialog, which) -> {
                    int[] times = activity.getResources().getIntArray(R.array.timer_int);
                    startTimer(times[which]);
                })
                .show();
    }

    private void startTimer(int minute) {
        QuitTimer.get().start(minute * 60 * 1000);
        if (minute > 0) {
            ToastUtils.show(activity.getString(R.string.timer_set, String.valueOf(minute)));
        } else {
            ToastUtils.show(R.string.timer_cancel);
        }
    }
}
