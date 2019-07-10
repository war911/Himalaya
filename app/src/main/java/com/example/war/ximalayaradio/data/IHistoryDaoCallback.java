package com.example.war.ximalayaradio.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    /**
     * 添加历史的结果
     *
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 清除历史结果
     */
    void onClearHistory();

    /**
     * 通知历史刷新
     * @param list
     */
    void onHistoryLoaded(List<Track> list);
}
