package com.znt.vodbox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.znt.vodbox.R;

public class VideoDirectionDialog extends Dialog
{
	private Context context;
	private View parentView = null;
	private TextView tvCancel = null;
	private TextView tvOne = null;
	private TextView tvTwo = null;
	private TextView tvThree = null;
	private TextView tvFour = null;
	
	private TextView tvFive = null;
	private TextView tvSix = null;
	private TextView tvSeven = null;
	private TextView tvEight = null;
	private TextView tvNine = null;
	private TextView tvTen = null;
	private TextView tvEleven = null;
	private TextView tvTw = null;
	
	private String screenOri = "";
	private String devId = "";
	
	public VideoDirectionDialog(Context context)
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = inflater.inflate(R.layout.dialog_video_direction, null);
		
	    setScreenBrightness();
	    /*Window window = getWindow();
		window.setWindowAnimations(R.style.MMTheme_DataSheet);
		requestWindowFeature(Window.FEATURE_NO_TITLE);*/
		
		parentView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	public void showDialog(String screenOri, String devId)
	{
		this.screenOri = screenOri;
		this.devId = devId;
		
        tvCancel = (TextView)parentView. findViewById(R.id.tv_dialog_wifi_cancel);
        tvOne = (TextView)parentView. findViewById(R.id.tv_video_direc_one);
        tvTwo = (TextView)parentView. findViewById(R.id.tv_video_direc_two);
        tvThree = (TextView)parentView. findViewById(R.id.tv_video_direc_three);
        tvFour = (TextView)parentView. findViewById(R.id.tv_video_direc_four);
        
        tvFive = (TextView)parentView. findViewById(R.id.tv_video_direc_5);
        tvSix = (TextView)parentView. findViewById(R.id.tv_video_direc_6);
        tvSeven = (TextView)parentView. findViewById(R.id.tv_video_direc_7);
        tvEight = (TextView)parentView. findViewById(R.id.tv_video_direc_8);
        tvNine = (TextView)parentView. findViewById(R.id.tv_video_direc_9);
        tvTen = (TextView)parentView. findViewById(R.id.tv_video_direc_10);
        tvEleven = (TextView)parentView. findViewById(R.id.tv_video_direc_11);
        tvTw = (TextView)parentView. findViewById(R.id.tv_video_direc_12);
        
        Window w = getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		
		final int cFullFillWidth = 10000;
		parentView.setMinimumWidth(cFullFillWidth);
		
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		onWindowAttributesChanged(lp);
		setCanceledOnTouchOutside(true);
		setContentView(parentView);
		show();
		
		tvCancel.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		tvOne.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("0");
			}
		});
		tvTwo.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("1");
			}
		});
		tvThree.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("2");
			}
		});
		tvFour.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("3");
			}
		});
		tvFive.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("4");
			}
		});
		tvSix.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("5");
			}
		});
		tvSeven.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("6");
			}
		});
		tvEight.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("7");
			}
		});
		tvNine.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("8");
			}
		});
		tvTen.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("9");
			}
		});
		tvEleven.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("10");
			}
		});
		tvTw.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("11");
			}
		});
	}
	
	private void updateVideoDirection(String direct)
	{
		if(screenOri != null && screenOri.equals(direct))
		{
			screenOri = null;
			dismiss();
			return;
		}
		screenOri = direct;
	}
	
	public String getCurDerection()
	{
		
		return screenOri ;
	}
	
	private void setScreenBrightness() 
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        /**
        *  此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。
        *  范围是0.0到1.0
        */
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
}
