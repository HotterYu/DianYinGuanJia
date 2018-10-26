
package com.znt.vodbox.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.znt.vodbox.R;

/** 
 * @ClassName: MusicPlayDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-20 涓嬪崍3:30:43  
 */
public class CountEditDialog extends Dialog
{

	private TextView textTitle = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private EditText etInput = null;
	private View.OnClickListener listener = null;
	
	private Activity context = null;
	private boolean isDismissed = false;
	private String nameOld = "";
	
	private final int PREPARE_FINISH = 0;
	private final int PREPARE_FAIL = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == PREPARE_FINISH)
			{
				
			}
			else if(msg.what == PREPARE_FAIL)
			{
				textTitle.setText("鎾斁澶辫触锛岃閲嶈瘯");
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public CountEditDialog(Activity context)
	{
		super(context, R.style.CustomDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

    /** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param themeCustomdialog 
	*/
	public CountEditDialog(Activity context, int themeCustomdialog)
	{
		super(context, themeCustomdialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public String getContent()
	{
		String content = etInput.getText().toString().trim();
		if(content == null)
			content = "";
		return content;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_count_edit);
	    setScreenBrightness();
	    
	    
		btnRight = (Button) CountEditDialog.this.findViewById(R.id.btn_dialog_count_edit_right);
    	btnLeft = (Button) CountEditDialog.this.findViewById(R.id.btn_dialog_count_edit_left);
    	textTitle = (TextView) CountEditDialog.this.findViewById(R.id.tv_dialog_count_edit_title);
    	etInput = (EditText) CountEditDialog.this.findViewById(R.id.et_count_edit);
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
				
			}
		});
	    
	}
	
    private void initViews()
    {
        
        setCanceledOnTouchOutside(false);
        
        textTitle.setText("请输入插播的间隔歌曲数");
        etInput.setText(nameOld);
        if(listener != null)
        	btnRight.setOnClickListener(listener);
        
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
        
    }
    
    public boolean isDismissed()
    {
    	return isDismissed;
    }
    
    public void setOnClickListener(View.OnClickListener listener)
    {
    	this.listener  = listener;
    }
    
    public void setInfor(String nameOld)
    {
    	if(nameOld == null)
    		nameOld = "";
    	this.nameOld = nameOld;
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
        /**
        *  姝ゅ璁剧疆浜害鍊笺�俤imAmount浠ｈ〃榛戞殫鏁伴噺锛屼篃灏辨槸鏄忔殫鐨勫灏戯紝璁剧疆涓�0鍒欎唬琛ㄥ畬鍏ㄦ槑浜��
        *  鑼冨洿鏄�0.0鍒�1.0
        */
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
}