package com.znt.vodbox.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.model.Shopinfo;

/**
 * Created by prize on 2018/11/14.
 */

public class VolumeSetBottomDialog
{
    private Activity mContext = null;

    private TextView textTitle = null;
    private TextView textInfor = null;
    private SeekBar seekBar = null;
    private Button btnLeft = null;
    private Button btnRight = null;

    private Shopinfo deviceInfor = null;

    public VolumeSetBottomDialog(Activity mContext)
    {
        this.mContext = mContext;
    }

    private boolean isConfirm = false;

    public interface OnVolumeSetDismissResultListener
    {
        public void onConfirmDismiss(int volume);
    }

    public void show(String titleName, Shopinfo deviceInfor, final OnVolumeSetDismissResultListener mOnDismissResultListener)
    {

        this.deviceInfor = deviceInfor;

        View view = View.inflate(mContext, R.layout.dialog_volume_set, null);

        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(mContext);
        bottomInterPasswordDialog.setContentView(view);
        bottomInterPasswordDialog.show();

        seekBar = (SeekBar) view.findViewById(R.id.sb_dialog_volume_set_progress);
        btnLeft = (Button) view.findViewById(R.id.btn_dialog_volume_set_left);
        btnRight = (Button) view.findViewById(R.id.btn_dialog_volume_set_right);
        textTitle = (TextView) view.findViewById(R.id.tv_dialog_volume_set_title);
        textInfor = (TextView) view.findViewById(R.id.tv_dialog_volume_set_progress);



        bottomInterPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(isConfirm)
                {
                    if(mOnDismissResultListener != null)
                        mOnDismissResultListener.onConfirmDismiss(seekBar.getProgress());
                }
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConfirm = false;
                bottomInterPasswordDialog.dismiss();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*String newContent = content.getText().toString();
                if(TextUtils.isEmpty(newContent))
                {
                    Toast.makeText(mContext,"请输入插播间隔数字",Toast.LENGTH_LONG).show();
                    return;
                }*/


                isConfirm = true;
                bottomInterPasswordDialog.dismiss();
            }
        });

        int cur = 0;
        if(!TextUtils.isEmpty(deviceInfor.getTmlRunStatus().get(0).getVolume()))
            cur = Integer.parseInt(deviceInfor.getTmlRunStatus().get(0).getVolume());
        seekBar.setMax(15);
        seekBar.setProgress(cur);

        textInfor.setText(cur + " / " + 15);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar sb)
            {
                // TODO Auto-generated method stub
                /*isStop = false;
                updateSpeakerInfor(sb.getProgress());*/
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

                    //isStop = true;
                    textInfor.setText(position + " / " + 15);
                }
            }
        });
    }

}
