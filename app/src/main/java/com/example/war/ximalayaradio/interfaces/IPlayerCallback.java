package com.example.war.ximalayaradio.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {
    /**
     * 播放
     */
    void onPlayStart();

    /**
     * 暂停
     */
    void onPlayPause();

    /**
     * 停止
     */
    void onPlayStop();

    /**
     * 进度
     * @param current
     * @param duration
     * @return
     */
    void onProgressChange(int current, int duration);

    /**
     * 上一首
     */
    void  playPre();

    /**
     * 下一首
     */
    void playNext();

    /**
     * 切换播放模式
     */

    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     *获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行 播放
     * @param index index
     */
    void playByIndex(int index);

    /**
     * 设置进度
     * @param prigress
     */
    void seekTo(int prigress);

    /**
     * 更新当前节目的标题
     * @param track 节目
     */
    void onTrackUpdate(Track track,int playIndex);

    void onListLoaded(List<Track> list);

    /**
     * 播放模式改变
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 通知UI更新播放列表
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);

    /**
     * 更新播放position
     * @param position position
     */
    void upDatePlayIndexForUi(int position);

    void onPlayByAlbumId(List<Track> tracks);
    
}
