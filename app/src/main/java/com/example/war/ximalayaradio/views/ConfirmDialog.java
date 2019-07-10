package com.example.war.ximalayaradio.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.war.ximalayaradio.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public class ConfirmDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private onDialogActionClickLinstener mClickLinstener = null;
    private Album mAlbum = null;

    public ConfirmDialog(Context context) {
        this(context,0);
    }

    public ConfirmDialog(Context context, int themeResId) {
        this(context, true,null);
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
        mCancelSub = this.findViewById(R.id.tv_cancal_sub);
        mGiveUp = this.findViewById(R.id.tv_give_up);
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
