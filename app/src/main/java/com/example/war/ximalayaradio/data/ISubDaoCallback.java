package com.example.war.ximalayaradio.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {


    /**
     * 添加的结果回调方法
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess,Album album);

    /**
     * 删除是否成功
     * @param isSuccess
     */
    void onDelReslt(boolean isSuccess,Album album);

    /**
     * 加载的结果
     * @param result
     */
    void onSubListLoaded(List<Album> result);

}
