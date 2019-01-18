package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.znt.vodbox.R;
import com.znt.vodbox.config.Config;
import com.znt.vodbox.utils.binding.Bind;

public class WebViewActivity extends  BaseActivity {

    private String TAG = "WebViewActivity";
    private LinearLayout mWebContainer = null;

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvTopMore = null;

    private AgentWeb mAgentWeb = null;

    private String title = "";
    private String webUrl = "";
    private String PARAMS = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivTopMore.setVisibility(View.GONE);
        tvTopMore.setVisibility(View.VISIBLE);
        mWebContainer = findViewById(R.id.web_container);

        tvTopMore.setText("复制");

        tvTopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(webUrl);
                showToast("复制地址成功");
            }
        });

        webUrl = getIntent().getStringExtra(Config.Key.ACTIVITY_WEB_URL);
        title = getIntent().getStringExtra(Config.Key.ACTIVITY_TITLE);
        PARAMS = getIntent().getStringExtra(Config.Key.PARAMS);

        if(!TextUtils.isEmpty(title))
            tvTopTitle.setText(title);

        setwebview();
    }

    private void setwebview() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mWebContainer, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .additionalHttpHeader("PARAMS",PARAMS)
                //                .setReceivedTitleCallback(mCallback)
                //                .setSecutityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(webUrl);
        mAgentWeb.getAgentWebSettings().getWebSettings().setAppCacheEnabled(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setDisplayZoomControls(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setJavaScriptEnabled(true); // 设置支持javascript脚本
        mAgentWeb.getAgentWebSettings().getWebSettings().setAllowFileAccess(true); // 允许访问文件
        mAgentWeb.getAgentWebSettings().getWebSettings().setBuiltInZoomControls(true); // 设置显示缩放按钮
        mAgentWeb.getAgentWebSettings().getWebSettings().setSupportZoom(true); // 支持缩放
        mAgentWeb.getAgentWebSettings().getWebSettings().setLoadWithOverviewMode(true);
    }

    //WebViewClient 用于监听界面的开始和结束
    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //界面开始
            Log.d(TAG, "onPageStarted!!!");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //界面结束
            Log.d(TAG, "onPageFinished!!!");
        }

    };

    //WebChromeClient 监听界面的改变
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //界面改变
            Log.d(TAG, "onProgressChanged====" + newProgress);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, " onKeyDown keyCode=" + keyCode + "  event=" + event.getAction());
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跟随生命周期释放资源更省电
     */
    @Override
    protected void onPause() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onPause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onResume();

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy=");
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
