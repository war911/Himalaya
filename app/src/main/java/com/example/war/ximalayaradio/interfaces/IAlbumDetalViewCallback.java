package com.example.war.ximalayaradio.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetalViewCallback {
    /**
     * 专辑详情内容加载出来了
     *
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 网络错误
     */
    void onNetWorkError(int errorCde,String errorMsg);
    /**
     * 把Album传给ui
     * @param album
     */
    void onAlbumLoaded(Album album);


    /**
     * 上啦加载更多
     * @param size > 0 表示加载成功 否则 表示失败
     */
    void onLoaderMoreFinished(int size);

    /**
     * 拉下刷新
     * @param size > 0 表示加载成功 否则 表示失败
     */
    void onRefreshFinished(int size);
}
