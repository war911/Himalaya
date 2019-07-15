package com.example.war.ximalayaradio.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.example.war.ximalayaradio.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public class ConfirmCheckBoxDialog extends Dialog {

    private View mCancel;
    private View mConfirm;
    private onDialogActionClickLinstener mClickLinstener = null;
    private Album mAlbum = null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_checkbox_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickLinstener.onCancelSubClick(mAlbum);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mCheckBox.isChecked();

                mClickLinstener.onConfirmClick(checked);
            }
        });
    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        mConfirm = this.findViewById(R.id.dialog_check_box_comfrim);
        mCheckBox = this.findViewById(R.id.checkBox);
    }

    public void setOnDialogActionClickLinstener(onDialogActionClickLinstener linstener){
        this.mClickLinstener = linstener;
    }



    public interface onDialogActionClickLinstener{
        void onCancelSubClick(Album album);

        void onConfirmClick(boolean checked);
    }
}
