package com.example.war.ximalayaradio.interfaces;

import com.example.war.ximalayaradio.base.IBasePrester;

public interface IRecommendPresenter extends IBasePrester<IRecommendViewCallback> {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 先下啦刷新
     */
    void pullToRefeeshMore();

    /**
     * 上啦更多
     */
    void loadMore();
}
