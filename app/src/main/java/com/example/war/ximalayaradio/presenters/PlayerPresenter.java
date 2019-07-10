package com.example.war.ximalayaradio.presenters;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.war.ximalayaradio.data.HimilayaApi;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.data.HistoryDao;
import com.example.war.ximalayaradio.interfaces.IPlayerCallback;
import com.example.war.ximalayaradio.interfaces.IPlayerPresenter;
import com.example.war.ximalayaradio.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mXmPlayerManager;
    private List<IPlayerCallback> mIPlayerCallbacksList = new ArrayList<>();
    private Track mTrack;
    private int mCurrentIndex = 0;
    private static final int DEFAULT_PLAY_INDEX = 0;
    private final SharedPreferences mPlayMode;
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //sp - key and name
    public static final String PLAY_MODEL_SP_NAME = "PlayMode";
    public static final String PLAY_MODEL_SP_KEY = "currentPlayMode";
    private Track mMCurrentTrack;
    private boolean mIsReverse = false;
    private int mProgressDuration = 0;
    private int mCurrentProgressPsoition = 0;


    private PlayerPresenter() {
        mXmPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关接口
        mXmPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关
        mXmPlayerManager.addPlayerStatusListener(this);
        //记录当前播放模式
        mPlayMode = BaseApplication.getAppContext().getSharedPreferences("PlayMode", Context.MODE_PRIVATE);


    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    /**
     * 是否设置播放列表
     */
    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        LogUtil.i(TAG, "setPlayList: ");
        if (mXmPlayerManager != null) {
            mXmPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            Track track = list.get(playIndex);
            mTrack = track;
            mCurrentIndex = playIndex;
        } else {
            LogUtil.d(TAG, "mXmPlayerManager null ");
        }
    }

    @Override
    public void play() {
        Log.i(TAG, "play_press: ");
        if (isPlayListSet) {
            mXmPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        Log.i(TAG, "pause: ");
        if (mXmPlayerManager != null) {
            mXmPlayerManager.pause();
        }
    }

    @Override
    public void stop() {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.stop();
        }
    }

    @Override
    public void playPre() {
        //播放上一个
        if (mXmPlayerManager != null) {
            mXmPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放前一个
        if (mXmPlayerManager != null) {
            mXmPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.setPlayMode(mode);
            mCurrentPlayMode = mode;
            //通知UI更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到sp里
            SharedPreferences.Editor edit = mPlayMode.edit();
            edit.putInt(PLAY_MODEL_SP_KEY, getIntByPlayMode(mode));
            edit.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getIntByPlayInt(int index) {
        switch (index) {
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mXmPlayerManager != null) {
            List<Track> playList = mXmPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {

        if (mXmPlayerManager != null) {
            mCurrentIndex = index;
            mXmPlayerManager.play(mCurrentIndex);
            Log.i(TAG, "playByIndex: -- > mCurrentIndex" + mCurrentIndex);
        }
    }

    @Override
    public void seekTo(int progress) {
        mXmPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正正在播放
        return mXmPlayerManager.isPlaying();
    }

    @Override
    public void revesePlayList() {
        List<Track> playList = mXmPlayerManager.getPlayList();
        Collections.reverse(playList);

        mIsReverse = !mIsReverse;

        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        setPlayList(playList, mCurrentIndex);
        mMCurrentTrack = (Track) mXmPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mMCurrentTrack, mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //获取专辑内容
        HimilayaApi himilayaApi = HimilayaApi.getHimilayaApi();
        himilayaApi.getAlumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                //设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && !tracks.isEmpty()) {
                    mXmPlayerManager.setPlayList(tracks, 0);
                    Log.i(TAG, "onSuccess: -- > 播放第一个 ");
                    isPlayListSet = true;
                    mMCurrentTrack = tracks.get(0);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                    for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
                        iPlayerCallback.onPlayByAlbumId(tracks);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(), "请求数据失败。。。", Toast.LENGTH_SHORT).show();
            }
        }, (int) id, 1);

        //开始播放


    }


    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdate(mTrack, mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgressPsoition, mProgressDuration);
        //从sp里拿播放模式
        //更新状态

        int modeInt = mPlayMode.getInt(PLAY_MODEL_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getIntByPlayInt(modeInt);
        //int转换成mode
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
        if (!mIPlayerCallbacksList.contains(iPlayerCallback)) {
            mIPlayerCallbacksList.add(iPlayerCallback);
        }
    }

    @Override
    public void unregisterViewCallback(IPlayerCallback iPlayerCallback) {
        mIPlayerCallbacksList.remove(iPlayerCallback);
    }

    //==============================广告相关的回调方法 start====================

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo ...");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo...");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering...");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering...");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds...");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds...");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG, "onError...what " + what + "... extra" + extra);
    }
    //==============================广告相关的回调方法 end ====================

    //====================================播放器相关回调方法 start =====================
    @Override
    public void onPlayStart() {
        Log.i(TAG, "onPlayStart: ");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.i(TAG, "onPlayPause: ");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.i(TAG, "onPlayStop: ");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        Log.i(TAG, "onSoundPlayComplete: ");
    }

    @Override
    public void onSoundPrepared() {
        Log.i(TAG, "onSoundPrepared: ");
        Log.i(TAG, "onSoundPrepared: current status -- > " + mXmPlayerManager.getPlayerStatus());
        mXmPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mXmPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mXmPlayerManager.play();
        }
        Log.i(TAG, "onSoundPrepared: -- >getCurrentIndex() " + mXmPlayerManager.getCurrentIndex());
        Log.i(TAG, "onSoundPrepared: -- >mCurrentIndex " + mCurrentIndex);
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        Log.i(TAG, "onSoundSwitch: ");
        if (lastModel != null) {
            Log.i(TAG, "onSoundSwitch: lastModel getKind" + lastModel.getKind());
        }
        if (curModel != null) {
            Log.i(TAG, "onSoundSwitch: ---- > curmodel "+curModel.getKind());
        }
        Log.i(TAG, "onSoundSwitch: curModel getKind" + curModel.getKind());
        //curModel 代表的是当前播放的内容
        //通过getKind()方法来获取他是什么类型
        //track表示是track类型
        //第一种写法,但是不推荐
        //if ("track".endsWith(curModel.getKind())) {
        //   Track currentTrack = (Track) curModel;
        // LogUtil.d(TAG, "title -- > " + currentTrack.getTrackTitle());
        //}
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentIndex = mXmPlayerManager.getCurrentIndex();

            //添加进历史
            HistoryPresenter historyPresenter = HistoryPresenter.getInstance();
            if (currentTrack!= null) {
                historyPresenter.addHistory(currentTrack);
            }

            LogUtil.d(TAG, "title -- > " + currentTrack.getTrackTitle());
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
                iPlayerCallback.onTrackUpdate(currentTrack, mCurrentIndex);
                //todo
                iPlayerCallback.upDatePlayIndexForUi(mCurrentIndex);
            }
        }
        //当前界面改变以后要修改界面图片

    }

    @Override
    public void onBufferingStart() {
        Log.i(TAG, "onBufferingStart: ");
    }

    @Override
    public void onBufferingStop() {
        Log.i(TAG, "onBufferingStop: ");
    }

    @Override
    public void onBufferProgress(int i) {
        Log.i(TAG, "onBufferProgress:  缓冲进度 -- >" + i);
    }


    @Override
    public void onPlayProgress(int current, int duration) {
        this.mCurrentProgressPsoition = current;
        this.mProgressDuration = duration;
        //当我毫秒
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacksList) {
            iPlayerCallback.onProgressChange(current, duration);
        }
        Log.i(TAG, "onPlayProgress: duration -- > " + duration
                + "  current" + current);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.i(TAG, "onError: " + e);
        return false;
    }

    public boolean haslayList() {
        return isPlayListSet;
    }

    public int getCurrentPlayIndex() {
        return mXmPlayerManager.getCurrentIndex();
    }


    //====================================播放器相关回调方法 start =====================

    /**
     * 判断是否播放当前列表的歌曲
     *
     * @return
     */
    public boolean isPlayCurrentTrackList() {
        if (mXmPlayerManager != null) {
            return mXmPlayerManager.getPlayList().contains(mXmPlayerManager.getTrack(mCurrentIndex));
        }
        return false;
    }

    public String getCurrentPlayTitle() {
        if (mXmPlayerManager != null && mXmPlayerManager.getTrack(mCurrentIndex) != null) {
            return mXmPlayerManager.getTrack(mCurrentIndex).getTrackTitle();
        }
        return "";
    }

    public Track getCurrentTrack(){
        return mXmPlayerManager.getTrack(mCurrentIndex);
    }
}
