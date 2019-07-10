package com.example.war.ximalayaradio.interfaces;

import com.example.war.ximalayaradio.base.IBasePrester;

public interface IAlbumDetalPresenter extends IBasePrester<IAlbumDetalViewCallback> {
    /**
     * 先下啦刷新
     */
    void pullToRefeeshMore();

    /**
     * 上啦更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param album
     * @param page
     */
    void getAlumDetail(int album, int page);
}
