package com.example.war.ximalayaradio.views;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.adapters.PlayListAdapter;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private static final String TAG = "SobPopWindow";
    private final View mPopView;
    private Button mBtnClose;
    private RecyclerView mRecyclerView;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeImg;
    private View mPlayModeContainer;
    private playListActionClickLinstener mPlayListActionClickLinstener = null;
    private LinearLayout mMPlayListOrderContainer;
    private ImageView mOrderIcon;
    private TextView mOrderTv;

    public SobPopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //这里注意设置这个属性前，先要设置setBackgroundDrawable
        //否则点击外部无法关闭

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        //把view 载入
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(
                R.layout.pop_play_list, null);
        setContentView(mPopView);

        //设置窗体进入和退出的动画

        setAnimationStyle(R.style.pop_animation);

        initView();
        initEvent();


    }

    private void initEvent() {
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: -- > 关闭popWindow");
                SobPopWindow.this.dismiss();
            }
        });

        //切换模式
        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                if (mPlayListActionClickLinstener != null) {
                    mPlayListActionClickLinstener.onPlayModeClick();
                }
            }
        });

        mMPlayListOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayListActionClickLinstener.onPalyOrderClick();
            }
        });
    }

    private void initView() {
        mBtnClose = mPopView.findViewById(R.id.btn_close);
        mRecyclerView = mPopView.findViewById(R.id.play_list_recyclerview);

        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());

        mRecyclerView.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();

//        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                outRect.top = UIUtil.dip2px(view.getContext(), 2);
//                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
//                outRect.left = UIUtil.dip2px(view.getContext(), 5);
//                outRect.right = UIUtil.dip2px(view.getContext(), 5);
//            }
//        });
        mRecyclerView.setAdapter(mPlayListAdapter);

        //播放模式
        mPlayModeTv = mPopView.findViewById(R.id.play_list_mode_tv);
        mPlayModeImg = mPopView.findViewById(R.id.play_list_mode_img);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_mode_contain);
        //播放顺序
        mMPlayListOrderContainer = mPopView.findViewById(R.id.play_list_order_container);

        mOrderIcon = mPopView.findViewById(R.id.play_list_order_img);
        mOrderTv = mPopView.findViewById(R.id.play_list_order_tv);
    }

    public void setList(List<Track> list) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(list);
        }
    }

    public void setCurrentPlayIndex(int index) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setPlayIndex(index);
        }
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(index);
        }

    }

    public void setplayListItemOnclickListener(playListItemOnclickListener mlistener) {
        mPlayListAdapter.setOnItemClickLinstener(mlistener);
    }

    public interface playListItemOnclickListener {
        void onclick(int postion);

    }

    public void setPlayListActionClickLinstener(playListActionClickLinstener mlinstener) {
        mPlayListActionClickLinstener = mlinstener;
    }

    public interface playListActionClickLinstener {
        //播放模式切换换
        void onPlayModeClick();

        //播放顺序或者逆序被点击了
        void onPalyOrderClick();
    }

    public void upDatePlayListPlayModeState(int draw, String str) {
        mPlayModeImg.setImageResource(draw);
        mPlayModeTv.setText(str);
    }

    public void upDatePlayListPlayOrder(boolean isOrder) {
        //更新顺序
        mOrderIcon.setImageResource(isOrder ? R.drawable.select_model_list_reover : R.drawable.select_model_list_order);
        mPlayModeTv.setText(BaseApplication.getAppContext().getString
                (isOrder ? R.string.play_order : R.string.play_reover));
    }

    public void ScrollToCurrentPosition(int position){
        mRecyclerView.smoothScrollToPosition(position);
    }
}
