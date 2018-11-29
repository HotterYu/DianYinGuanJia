package com.znt.vodbox.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;

/**
 * Created by prize on 2018/11/14.
 */

public class TextInputBottomDialog
{
    private Activity mContext = null;
    public TextInputBottomDialog(Activity mContext)
    {
        this.mContext = mContext;
    }

    private boolean isConfirm = false;

    public interface OnDismissResultListener
    {
        public void onConfirmDismiss(String content);
    }

    public void show(String titleName, String oldContent, final OnDismissResultListener mOnDismissResultListener)
    {
        View view = View.inflate(mContext, R.layout.dialog_bottom_text_input, null);

        BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(mContext, R.style.BottomSheetEdit);
        bottomInterPasswordDialog.setContentView(view);
        bottomInterPasswordDialog.show();

        TextView title = (TextView) view.findViewById(R.id.tibd_title);
        EditText content = (EditText) view.findViewById(R.id.tibd_content);
        Button btnCancel = (Button) view.findViewById(R.id.tibd_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.tibd_confirm);

        if(titleName != null)
            title.setText(titleName);

        if(oldContent != null)
            content.setText(oldContent);

        bottomInterPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(isConfirm)
                {
                    if(mOnDismissResultListener != null)
                        mOnDismissResultListener.onConfirmDismiss(content.getText().toString());
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
                    Toast.makeText(mContext,"请输入内容",Toast.LENGTH_LONG).show();
                    return;
                }

                if(oldContent != null && oldContent.equals(newContent))
                {
                    Toast.makeText(mContext,"内容无变化",Toast.LENGTH_LONG).show();
                    return;
                }

                isConfirm = true;
                bottomInterPasswordDialog.dismiss();
            }
        });

    }
}
