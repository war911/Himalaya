package com.example.war.ximalayaradio.interfaces;

import com.example.war.ximalayaradio.base.IBasePrester;

public interface ISearchPresenter extends IBasePrester<ISearchCallBack> {

    /**
     * 进行搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载更多的搜索结结果
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 获取推荐的内容关键字（跟用户输入相似）
     * @param keyWord
     */
    void getRecommedKeyWord(String keyWord);

}
