package com.example.war.ximalayaradio.base;

import com.example.war.ximalayaradio.interfaces.IAlbumDetalViewCallback;

public interface IBasePrester<T> {

    /**
     * 注册UI停止接口
     * @param
     */
    void registerViewCallback(T t);

    /**
     * 删除UI通知接口
     * @param
     */
    void unregisterViewCallback(T t);


}
