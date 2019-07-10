package com.example.war.ximalayaradio.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.example.war.ximalayaradio.R;

@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {

    private static final String TAG = "LoadingView";
    //旋转的角度
    private int rotateDegree = 0;

    private boolean nNeedRoate = true;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图标
        setImageResource(R.drawable.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //绑定到window的时候
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree += 30;
                //是否继续旋转
                rotateDegree = rotateDegree <= 360 ? rotateDegree:rotateDegree-360;
                invalidate();
                if (nNeedRoate) {
                    postDelayed(this,100);
                }
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //从window中解绑
        nNeedRoate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第一个数旋转角度，
         * 第二个参数是x坐标
         * 第三个参数是y坐标
         */
        canvas.rotate(rotateDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
