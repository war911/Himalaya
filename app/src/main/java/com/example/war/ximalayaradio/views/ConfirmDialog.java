package com.example.war.ximalayaradio.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.war.ximalayaradio.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public class ConfirmDialog extends Dialog {
    private static final String TAG = "ConfirmDialog";
    private TextView mCancelSub;
    private TextView mGiveUp;
    private onDialogActionClickLinstener mClickLinstener = null;
    private Album mAlbum = null;

    public ConfirmDialog(Context context) {
        this(context,R.style.ConfirmDialog);
    }

    public ConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfirmDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickLinstener.onCancelSubClick(mAlbum);
            }
        });

        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickLinstener.onGiveUpClick();
            }
        });
    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_check_box_cancel);
        mGiveUp = this.findViewById(R.id.dialog_check_box_comfrim);

    }

    public void setOnDialogActionClickLinstener(onDialogActionClickLinstener linstener, Album album){
        this.mClickLinstener = linstener;
        this.mAlbum = album;
    }

    public interface onDialogActionClickLinstener{
        void onCancelSubClick( Album album);

        void onGiveUpClick();
    }
}
