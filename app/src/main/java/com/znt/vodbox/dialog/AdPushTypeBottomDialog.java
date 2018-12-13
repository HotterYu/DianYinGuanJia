package com.znt.vodbox.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;

/**
 * Created by prize on 2018/11/14.
 */

public class AdPushTypeBottomDialog
{
    private Activity mContext = null;

    TextView title = null;
    TextView tvHint = null;
    EditText content = null;
    Button btnCancel = null;
    Button btnConfirm = null;
    RadioButton rbCount = null;
    RadioButton rbTime = null;

    public AdPushTypeBottomDialog(Activity mContext)
    {
        this.mContext = mContext;
    }

    private boolean isConfirm = false;

    public interface OnAdPushDismissResultListener
    {
        public void onConfirmDismiss(String content,String type);
    }

    public void show(String titleName, String oldContent, String mode, final OnAdPushDismissResultListener mOnDismissResultListener)
    {
        View view = View.inflate(mContext, R.layout.dialog_bottom_ad_push_type, null);

        BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(mContext, R.style.BottomSheetEdit);
        bottomInterPasswordDialog.setContentView(view);
        bottomInterPasswordDialog.show();

        title = (TextView) view.findViewById(R.id.tibd_title);
        tvHint = (TextView) view.findViewById(R.id.tv_push_type_hint);
        content = (EditText) view.findViewById(R.id.tibd_content);
        btnCancel = (Button) view.findViewById(R.id.tibd_cancel);
        btnConfirm = (Button) view.findViewById(R.id.tibd_confirm);
        rbCount = (RadioButton) view.findViewById(R.id.rb_ad_push_type_count);
        rbTime = (RadioButton) view.findViewById(R.id.rb_ad_push_type_time);

        if(mode.equals("0"))
            rbCount.setChecked(true);
        else
            rbTime.setChecked(true);

        rbCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    tvHint.setText("首歌");
            }
        });
        rbTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    tvHint.setText("分钟");
            }
        });

        if(titleName != null)
            title.setText(titleName);

        if(oldContent != null)
        {
            if(oldContent.startsWith("0"))
                content.setText(oldContent.substring(1));
            else
                content.setText(oldContent);
        }

        bottomInterPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(isConfirm)
                {
                    if(mOnDismissResultListener != null)
                    {
                        String type = rbCount.isChecked()?"0":"1";
                        String musicNum = content.getText().toString().trim();
                        if(musicNum.startsWith("0"))
                            musicNum = musicNum.substring(1);

                        if(rbTime.isChecked())
                            musicNum = "0"+musicNum;
                        mOnDismissResultListener.onConfirmDismiss(musicNum,type);
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConfirm = false;
                bottomInterPasswordDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newContent = content.getText().toString();
                if(TextUtils.isEmpty(newContent))
                {
                    Toast.makeText(mContext,"请输入插播间隔数字",Toast.LENGTH_LONG).show();
                    return;
                }

                String curType = rbCount.isChecked()?"0":"1";

                if(oldContent != null && oldContent.equals(newContent) && mode.equals(curType))
                {
                    Toast.makeText(mContext,"内容无变化",Toast.LENGTH_LONG).show();
                    return;
                }

                if(Integer.parseInt(newContent) <= 0)
                {
                    Toast.makeText(mContext,"请输入正整数",Toast.LENGTH_LONG).show();
                    return;
                }

                isConfirm = true;
                bottomInterPasswordDialog.dismiss();
            }
        });
    }

}
