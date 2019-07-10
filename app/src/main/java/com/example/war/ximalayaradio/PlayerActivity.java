package com.example.war.ximalayaradio;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.war.ximalayaradio.adapters.PlayerTrackPagerAdapter;
import com.example.war.ximalayaradio.base.BaseActivity;
import com.example.war.ximalayaradio.interfaces.IPlayerCallback;
import com.example.war.ximalayaradio.presenters.PlayerPresenter;
import com.example.war.ximalayaradio.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {
    private TextView tvDruction, tvCurrentTime;
    private SeekBar mSeekBar;
    private PlayerPresenter mPlayerPresenter;
    private ImageView mBtnPlayOrPause;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private int mCurrentPrgress = 0;
    private boolean isUserTouchProgress = false;
    private static final String TAG = "PlayerActivity";
    private ImageView mPlayPreImg;
    private ImageView mPlayNextImg;
    private TextView mTrackTitle;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsSelectFromUser = false;
    private ImageView mPlayModelSwitchBtn;

    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    private XmPlayListControl.PlayMode mCurrentModel = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private ImageView mPlayListBtn;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnamtor;
    private ValueAnimator mOutBgAnimator;
    private final int BG_ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        Log.i(TAG, "onCreate: -- >注册");
        mPlayerPresenter.registerViewCallback(this);


        //在界面初始化以后获取数据
        mPlayerPresenter.getPlayList();
        boolean playing = mPlayerPresenter.isPlaying();
        upDatePlayState(playing);
        initEvent();
        initBgAnimation();
        mTrackTitleText = mPlayerPresenter.getCurrentPlayTitle();
        Log.i(TAG, "onCreate: --- > "+mTrackTitleText);
        mTrackTitle.setText(mTrackTitleText != null?mTrackTitleText:"");
        //        startPlay();

    }


    private void initBgAnimation() {
        mEnterBgAnamtor = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnamtor.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnamtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });

        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });

    }

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    /**
     * 给控件设置相关的事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mBtnPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果正在播放那么暂停
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {
                    //如果非播放，那么就播放
                    mPlayerPresenter.play();
                }


            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentPrgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserTouchProgress = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开拖动进度条更新进度
                isUserTouchProgress = false;
                mPlayerPresenter.seekTo(mCurrentPrgress);
            }
        });

        mPlayPreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放上一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }

            }
        });

        mPlayNextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mTrackPageView.addOnPageChangeListener(this);
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsSelectFromUser = true;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mIsSelectFromUser = false;
                        break;
                }
                return false;
            }
        });

        mPlayModelSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();

            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                mSobPopWindow.ScrollToCurrentPosition(mPlayerPresenter.getCurrentPlayIndex());
                //处理一下背景的透明度

                //修改背景的透明渐变过程
                mEnterBgAnamtor.start();

            }
        });

        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop窗体消失以后，恢复透明度
                mOutBgAnimator.start();
            }
        });

        mSobPopWindow.setplayListItemOnclickListener(new SobPopWindow.playListItemOnclickListener() {
            @Override
            public void onclick(int postion) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(postion);
                }
            }
        });

        mSobPopWindow.setPlayListActionClickLinstener(new SobPopWindow.playListActionClickLinstener() {
            @Override
            public void onPlayModeClick() {
                switchPlayMode();
            }

            @Override
            public void onPalyOrderClick() {
                //切换列表顺序
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.revesePlayList();
                }

            }
        });
    }


    private void switchPlayMode() {
        //根据档期啊model获取下一个mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentModel);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
            mCurrentModel = playMode;
            updatePlayModeBtnImg(mCurrentModel);

        }
    }

    public void updateBgAlpha(float aplha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = aplha;
        window.setAttributes(attributes);
    }

    /**
     * 根据当前的模式，更新播放模式
     * //1.默认模式 PLAY_MODEL_LIST
     * //2.列表循环 PLAY_MODEL_LIST_LOOP
     * //3.随机播放 PLAY_MODEL_RANDOM
     * //4.单曲循环 PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode mCurrentModel) {
        switch (mCurrentModel) {
            case PLAY_MODEL_LIST:
                if (mPlayerPresenter != null) {
                    mPlayModelSwitchBtn.setImageResource(R.drawable.select_model_list_normal);
                }
                if (mSobPopWindow != null) {
                    mSobPopWindow.upDatePlayListPlayModeState(R.drawable.select_model_list_normal, getString(R.string.play_mode_order_text));
                }
                break;
            case PLAY_MODEL_RANDOM:
                if (mPlayerPresenter != null) {
                    mPlayModelSwitchBtn.setImageResource(R.drawable.select_mode_random);
                }
                if (mSobPopWindow != null) {
                    mSobPopWindow.upDatePlayListPlayModeState(R.drawable.select_mode_random, getString(R.string.play_mode_random_text));
                }
                break;
            case PLAY_MODEL_LIST_LOOP:
                if (mPlayModelSwitchBtn != null) {
                    mPlayModelSwitchBtn.setImageResource(R.drawable.select_play_mode_list_loop);
                }
                if (mSobPopWindow != null) {
                    mSobPopWindow.upDatePlayListPlayModeState(R.drawable.select_play_mode_list_loop, getString(R.string.play_mode_list_loop_text));
                }
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                if (mPlayModelSwitchBtn != null) {
                    mPlayModelSwitchBtn.setImageResource(R.drawable.select_mode_one_loop);
                }
                if (mSobPopWindow != null) {
                    mSobPopWindow.upDatePlayListPlayModeState(R.drawable.select_mode_one_loop, getString(R.string.play_mode_single_loop_text));
                }
                break;

        }
    }

    /**
     * 找到各自控件
     */
    private void initView() {
        mBtnPlayOrPause = findViewById(R.id.img_play_or_ause);
        tvDruction = findViewById(R.id.track_duration);
        tvCurrentTime = findViewById(R.id.current_position);
        mSeekBar = findViewById(R.id.track_seek_bar);
        mPlayNextImg = findViewById(R.id.player_next);
        mPlayPreImg = findViewById(R.id.player_pre);
        mTrackTitle = findViewById(R.id.track_title);


        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitle.setText(mTrackTitleText);
        }

        mTrackPageView = findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);
        //切换播放模式的按钮
        mPlayModelSwitchBtn = findViewById(R.id.play_mode_switch_btn);

        //播放列表
        mPlayListBtn = findViewById(R.id.play_list);

        //初始化
        mSobPopWindow = new SobPopWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }
    }

    /**
     * 更新播放状态
     * @param isPlaying
     */
    private void upDatePlayState(boolean isPlaying) {
        mBtnPlayOrPause.setImageResource(isPlaying ? R.drawable.selecter_player_pause : R.drawable.selecter_player_play);
    }

    /**
     * 修改UI暂停按钮
     */
    @Override
    public void onPlayStart() {
        if (mBtnPlayOrPause != null) {
            upDatePlayState(true);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void onPlayPause() {
        if (mBtnPlayOrPause != null) {
            upDatePlayState(false);
        }
    }

    /**
     * 停止
     */
    @Override
    public void onPlayStop() {
        if (mBtnPlayOrPause != null) {
            upDatePlayState(false);
        }
    }

    @Override
    public void onProgressChange(int current, int duration) {
        mSeekBar.setMax(duration);
        //更新播放进度
        String totalDuration;
        String currentPostion;
        if (duration > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(duration);
            currentPostion = mHourFormat.format(current);
        } else {
            totalDuration = mMinFormat.format(duration);
            currentPostion = mMinFormat.format(current);
        }
        if (tvDruction != null) {
            tvDruction.setText(totalDuration);
        }

        //当前时间
        if (tvCurrentTime != null) {
            tvCurrentTime.setText(currentPostion);
        }
        // 更新进度条
        if (!isUserTouchProgress) {
            mSeekBar.setProgress(current);
        }
    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int prigress) {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track == null) {
            Log.i(TAG, "onTrackUpdate: ---- track null ");
            return;
        }
        this.mTrackTitleText = track.getTrackTitle();
        Log.i(TAG, "onTrackUpdate: -- -> "+ track.getTrackTitle());
        if (mTrackTitle != null) {
            mTrackTitle.setText(mTrackTitleText);
        }
        //当节目改变的时候，我就获取当前播放中的位置
        //当前界面改变时，修改图片
        if (mTrackPageView != null) {
            Log.i(TAG, "onTrackUpdate: -- > 当前界面改变时，修改图片");
            mTrackPageView.setCurrentItem(playIndex, true);
        }
        //设置播放列表里的index
        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentPlayIndex(playIndex);
        }
    }

    @Override
    public void onListLoaded(List<Track> list) {
        if (list == null) {
            Log.i(TAG, "onListLoaded: -- > list null");
        }else {
            Log.i(TAG, "onListLoaded: list.size"+list.size());
        }
        //把数据设置的适配器里
        if (mTrackPagerAdapter != null) {
            
            mTrackPagerAdapter.setData(list);
            Log.i(TAG, "onListLoaded: 给viewPager设置图片 ");
        }
        //获取到播放列表，也要给到PopWindow

        if (mSobPopWindow != null) {
            mSobPopWindow.setList(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        mCurrentModel = playMode;
        updatePlayModeBtnImg(playMode);
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.upDatePlayListPlayOrder(isReverse);
    }

    @Override
    public void upDatePlayIndexForUi(int postion) {

    }

    @Override
    public void onPlayByAlbumId(List<Track> list) {

        if (list == null) {
            Log.i(TAG, "onPlayByAlbumId: -- > list null");
        }else {
            Log.i(TAG, "onPlayByAlbumId: list.size"+list.size());
        }
        //把数据设置的适配器里
        if (mTrackPagerAdapter != null) {

            mTrackPagerAdapter.setData(list);
            Log.i(TAG, "onPlayByAlbumId: 给viewPager设置图片 ");
        }
        //获取到播放列表，也要给到PopWindow

        if (mSobPopWindow != null) {
            mSobPopWindow.setList(list);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int postion) {
        //当页面选中的时候，就去切换播放内容
        if (mIsSelectFromUser && mPlayerPresenter != null) {
            mPlayerPresenter.playByIndex(postion);
        }
        mIsSelectFromUser = false;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
