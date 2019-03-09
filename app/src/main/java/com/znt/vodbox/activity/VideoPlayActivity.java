package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.znt.vodbox.R;
import com.znt.vodbox.entity.Config;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.video.VideoPlayer;
import com.znt.vodbox.video.VideoPlayerItemInfo;

public class VideoPlayActivity extends BaseActivity {

    @Bind(R.id.videoPlayer)
    private VideoPlayer videoPlayer;
    @Bind(R.id.iv_image_preview)
    private ImageView imageView;

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;

    private String videoName = "";
    private String videoUrl = "";
    private String videoId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        //videoPlayer = (VideoPlayer)findViewById(R.id.videoPlayer);

        videoName = getIntent().getStringExtra(Config.VIDEO_NAME);
        videoUrl = getIntent().getStringExtra(Config.VIDEO_URL);
        videoId = getIntent().getStringExtra(Config.VIDEO_ID);

        tvTopTitle.setText(videoName);
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        tvConfirm.setText("插播");

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                Bundle b = new Bundle();
                b.putString("MEDIA_NAME",videoName);
                b.putString("MEDIA_ID",videoId);
                b.putString("MEDIA_URL",videoUrl);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        if(FileUtils.isVideo(videoUrl))
        {
            imageView.setVisibility(View.GONE);
            videoPlayer.setVisibility(View.VISIBLE);

            //视频
            VideoPlayerItemInfo mVideoPlayerItemInfo = new VideoPlayerItemInfo(videoName,videoUrl);
            //传递给条目里面的MyVideoPlayer
            videoPlayer.setPlayData(mVideoPlayerItemInfo);

            //设置为初始化状态
            videoPlayer.initViewDisplay();
        }
        else
        {
            //图片
            imageView.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.GONE);

            Glide.with(this)
                    .load(videoUrl)
                    .placeholder(R.drawable.icon_album_sys)
                    .error(R.drawable.icon_album_sys)
                    .into(imageView);

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null)
        {
            videoPlayer.onDestroy();

        }
    }
}
