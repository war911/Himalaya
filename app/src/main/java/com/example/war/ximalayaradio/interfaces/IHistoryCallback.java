package com.example.war.ximalayaradio.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {

    /**
     * 历史内容加载
     * @param list
     */
    void onHistoriesLoaded(List<Track> list);
}
