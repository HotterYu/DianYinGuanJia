package com.znt.vodbox.executor;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.AboutActivity;
import com.znt.vodbox.activity.AdListActivity;
import com.znt.vodbox.activity.AllPlanListActivity;
import com.znt.vodbox.activity.ExportShopActivity;
import com.znt.vodbox.activity.GroupListActivity;
import com.znt.vodbox.activity.MyAlbumActivity;
import com.znt.vodbox.activity.SystemAlbumActivity;
import com.znt.vodbox.storage.preference.Preferences;


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
            /*case R.id.action_night:
                nightMode();
                break;*/
            case R.id.action_export:
                startActivity(ExportShopActivity.class);
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


}
