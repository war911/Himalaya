package com.example.war.ximalayaradio.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {

    /**
     * 设置回调接口
     * @param callback 回调
     */
    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     * @param track track
     */
    void addHistoryTrack(Track track);


    /**
     * 删除历史
     * @param track
     */
    void delHistoryTrack(Track track);


    /**
     * 清除历史
     * @param
     */
    void cleanHistoryTrack();

    /**
     * 获取历史
     */
    void getListHistory();
}
