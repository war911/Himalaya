package com.example.war.ximalayaradio.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallBack {

    /**
     * 搜索结果
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的结果回调
     * @param hotWords
     */
    void onHotWordLoaded(List<HotWord> hotWords);

    /**
     * 加载更多结果返回
     * @param result 结果
     * @param isOkay true 成功 false 失败
     */
    void onLoadMoreResult(List<Album> result,boolean isOkay);

    /**
     * 推荐关键字回调结果
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 错误通知
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode,String errorMsg);


}
