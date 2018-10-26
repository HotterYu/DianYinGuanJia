
package com.znt.vodbox.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.ShopSelectActivity;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.player.PlayerHelper;
import com.znt.vodbox.player.PlayerHelper.OnMusicPrepareListener;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.ViewUtils;

public class MusicPlayDialog extends Dialog implements OnMusicPrepareListener
{

	private TextView textTitle = null;
	private TextView textInfor = null;
	private SeekBar seekBar = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private View.OnClickListener listener = null;
	
	private PlayerHelper playerHelper = null;


	private Activity context = null;
	private boolean isDismissed = false;
	private boolean isStop = false;
	private int duration = 0;
	private String terminalId = null;
	private String mediaName = null;
	private String mediaUrl = null;
	private String mediaId = null;

	private final int PREPARE_FINISH = 0;
	private final int PREPARE_FAIL = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == PREPARE_FINISH)
			{
				seekBar.setMax(duration);
				playerHelper.startPlay();
				
				startPlayPosition();
			}
			else if(msg.what == PREPARE_FAIL)
			{
				textTitle.setText("加载失败");
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public MusicPlayDialog(Activity context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}


	public MusicPlayDialog(Activity context, int themeCustomdialog)
	{
		super(context, themeCustomdialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_music_play);
	    setScreenBrightness();
	    
	    
		seekBar = (SeekBar) MusicPlayDialog.this.findViewById(R.id.sb_dialog_music_play_progress);
		btnRight = (Button) MusicPlayDialog.this.findViewById(R.id.btn_dialog_music_play_right);
    	btnLeft = (Button) MusicPlayDialog.this.findViewById(R.id.btn_dialog_music_play_left);
    	textTitle = (TextView) MusicPlayDialog.this.findViewById(R.id.tv_dialog_music_play_title);
        textInfor = (TextView) MusicPlayDialog.this.findViewById(R.id.tv_dialog_music_play_progress);
		
        playerHelper = new PlayerHelper();
        playerHelper.setOnPrepareListener(this);

		btnRight.setText("插播");
        
	    this.setOnShowListener(new OnShowListener()
	    {
            @Override
            public void onShow(DialogInterface dialog)
            {
            	initViews();
            }
        });
	    this.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(playerHelper != null)
					playerHelper.closeMediaPlayer();
				stopPlayPosition();
			}
		});
	    
	    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar sb)
			{
				// TODO Auto-generated method stub
				if(playerHelper != null && playerHelper.getMediaPlayer() != null)
				{
					int temp = (int) ((duration * 1000) * ((float)sb.getProgress() / sb.getMax()));
					playerHelper.getMediaPlayer().seekTo(temp);
					isStop = false;
					//MyLog.e("onProgressChanged temp-->"+temp);
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int position, boolean fromUser)
			{
				// TODO Auto-generated method stub
				if(fromUser)
				{
					//MyLog.e("onProgressChanged position-->"+position);
					isStop = true;
				}
			}
		});
	}
	
    private void initViews()
    {
        setCanceledOnTouchOutside(false);
        
        textTitle.setText(mediaName);
        
        btnLeft.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				isDismissed = true;
				dismiss();
			}
		});
        btnRight.setOnClickListener(new View.OnClickListener()
        {
        	@Override
        	public void onClick(View arg0)
        	{
        		// TODO Auto-generated method stub
        		//checkUrl();
				Intent intent = new Intent(context, ShopSelectActivity.class);
				Bundle b = new Bundle();
				b.putString("MEDIA_NAME",mediaName);
				b.putString("MEDIA_ID",mediaId);
				b.putString("MEDIA_URL",mediaUrl);
				intent.putExtras(b);
				context.startActivity(intent);
				dismiss();
        	}
        });

		startPlayMusic(mediaUrl);
    }
    
    public boolean isDismissed()
    {
    	return isDismissed;
    }
    
    public void setOnClickListener(View.OnClickListener listener)
    {
    	this.listener  = listener;
    }
    
    public void setInfor(String mediaName, String mediaUrl, String mediaId)
    {
    	this.mediaName = mediaName;
    	this.mediaUrl = mediaUrl;
    	this.mediaId = mediaId;
    }
    
    public void updateProgress(String text)
    {
    	this.textInfor.setText(text);
    }
    
    public Button getLeftButton()
    {
    	return btnLeft;
    }
    public Button getRightButton()
    {
    	return btnRight;
    }
    
    private void setScreenBrightness() 
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
    
    Runnable tast = new Runnable()
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			
			if(!isDismissed && playerHelper != null && playerHelper.getMediaPlayer() != null && !isStop)
			{
				int progress = playerHelper.getMediaPlayer().getCurrentPosition() / 1000;
				seekBar.setProgress(progress);
				textInfor.setText(StringUtils.secToTime(progress) + " / " + StringUtils.secToTime(duration));
			}
			handler.postDelayed(tast, 800);
		}
	};
    private void startPlayPosition()
    {
    	handler.postDelayed(tast, 800);
    }
    private void stopPlayPosition()
    {
    	handler.removeCallbacks(tast);
    }
    

    private void startPlayMusic(String url)
    {
    	if(!isDismissed)
		{
    		if(Constant.isInnerVersion)
    		{
    			StringUtils.copy(url, context);
    		}
			playerHelper.startInitPlayer(url);
		}
    }
    


	/**
	*callbacks
	*/
	@Override
	public void onPrepareFinish(MediaPlayer mp)
	{
		// TODO Auto-generated method stub
		duration = (int) Math.ceil((float)(mp.getDuration() / 1000));
		ViewUtils.sendMessage(handler, PREPARE_FINISH);
	}

	/**
	*callbacks
	*/
	@Override
	public void onPrepareFail(String error)
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, PREPARE_FAIL);
	}
}