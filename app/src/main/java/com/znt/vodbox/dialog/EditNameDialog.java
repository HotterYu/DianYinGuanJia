
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
public class EditNameDialog extends Dialog
{

	private TextView textTitle = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private EditText etInput = null;
	private View.OnClickListener listener = null;
	
	private Activity context = null;
	private boolean isDismissed = false;
	private String nameOld = "";
	private String title = "";
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
				textTitle.setText("操作失败");
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public EditNameDialog(Activity context, String title)
	{
		super(context, R.style.CustomDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.title = title;
	}

    /** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param themeCustomdialog 
	*/
	public EditNameDialog(Activity context, int themeCustomdialog)
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
	    setContentView(R.layout.dialog_name_edit);
	    setScreenBrightness();

		btnRight = (Button) EditNameDialog.this.findViewById(R.id.btn_dialog_name_edit_right);
    	btnLeft = (Button) EditNameDialog.this.findViewById(R.id.btn_dialog_name_edit_left);
    	textTitle = (TextView) EditNameDialog.this.findViewById(R.id.tv_dialog_name_edit_title);
    	etInput = (EditText) EditNameDialog.this.findViewById(R.id.et_name_edit);
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
        if(TextUtils.isEmpty(title))
        	title = "请输入内容";
        textTitle.setText(title);
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