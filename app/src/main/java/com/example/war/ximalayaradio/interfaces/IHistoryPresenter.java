package com.example.war.ximalayaradio.interfaces;

import com.example.war.ximalayaradio.base.IBasePrester;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasePrester<IHistoryCallback> {

    /**
     * 获取历史
     */
    void getListHistories();

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史
     * @param
     */
    void clearHistories();


}
