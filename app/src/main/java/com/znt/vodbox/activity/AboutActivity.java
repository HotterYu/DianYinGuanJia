package com.znt.vodbox.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.znt.vodbox.BuildConfig;
import com.znt.vodbox.R;
import com.znt.vodbox.config.Config;
import com.znt.vodbox.constants.HttpApi;
import com.znt.vodbox.utils.ViewUtils;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container, new AboutFragment()).commit();
    }

    public static class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private Preference mVersion;
        private Preference mStar;
        private Preference mWeibo;
        private Preference mJianshu;
        private Preference mGithub;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_about);

            mVersion = findPreference("version");
            mStar = findPreference("star");
            mWeibo = findPreference("weibo");
            mJianshu = findPreference("jianshu");
            mGithub = findPreference("github");

            if(!HttpApi.API.contains("zhunit.com"))
                mVersion.setSummary("v " + BuildConfig.VERSION_NAME + " " + getResources().getString(R.string.version_hint_debug));
            else
                mVersion.setSummary("v " + BuildConfig.VERSION_NAME + " " + getResources().getString(R.string.version_hint_release));
            setListener();
        }

        private void setListener() {
            mStar.setOnPreferenceClickListener(this);
            mWeibo.setOnPreferenceClickListener(this);
            mJianshu.setOnPreferenceClickListener(this);
            mGithub.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mStar) {
                openUrl("申请试用",getString(R.string.about_project_url));
                return true;
            } else if (preference == mWeibo || preference == mJianshu || preference == mGithub) {
                openUrl("店音-StoreSound",preference.getSummary().toString());
                return true;
            }
            return false;
        }

        private void openUrl(String title,String url) {

            Bundle b = new Bundle();
            b.putString(Config.Key.ACTIVITY_TITLE,title);
            b.putString(Config.Key.ACTIVITY_WEB_URL,url);
            ViewUtils.startActivity(getActivity(), WebViewActivity.class,b);
        }
    }
}
