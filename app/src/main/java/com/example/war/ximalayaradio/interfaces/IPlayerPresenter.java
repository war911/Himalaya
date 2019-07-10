package com.example.war.ximalayaradio.interfaces;

import com.example.war.ximalayaradio.base.IBasePrester;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePrester<IPlayerCallback> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

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
     * 判断是否是正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 吧播放器列表内容翻转
     */
    void revesePlayList();

    /**
     * 播放专辑第一个界面
     * @param id
     */
    void playByAlbumId(long id);
}
