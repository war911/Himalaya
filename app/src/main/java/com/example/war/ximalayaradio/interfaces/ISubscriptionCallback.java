package com.example.war.ximalayaradio.interfaces;

import com.example.war.ximalayaradio.base.IBasePrester;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 订阅一般会有上线，比如说不能超过一百个
 * @author war
 */
public interface ISubscriptionCallback {

    /**
     * 调用添加的时候，去通知UI结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);


    /**
     * 删除订阅的方法
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);


    /**
     * 订阅专辑加载的结果回调方法
     * @param albums
     */
    void onSubscriptionsLoaded(List<Album> albums);

    /**
     * 订阅数量大于100
     */
    void onSubFull();
}
