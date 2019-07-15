package com.example.war.ximalayaradio.presenters;

import android.support.annotation.Nullable;

import com.example.war.ximalayaradio.data.HimilayaApi;
import com.example.war.ximalayaradio.interfaces.IRecommendViewCallback;
import com.example.war.ximalayaradio.interfaces.IRecommendPresenter;
import com.example.war.ximalayaradio.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter() {
    }

    private static RecommendPresenter sInstance = null;
    private List<Album> mRecommendList;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 这个借口3.10.6获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //如果内存不为空，就直接使用内容
        if (mRecommendList != null && mRecommendList.size() > 0) {

            handlerRecommendResult(mRecommendList);
            return;
        }
        updateLoading();
        HimilayaApi himilayaApi = HimilayaApi.getHimilayaApi();
        himilayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {


            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    mRecommendList = gussLikeAlbumList.getAlbumList();
                    LogUtil.d(TAG, "albumList -- >" + mRecommendList);
                    //更新UI
//                    upRecommandUI(albumList);
                    handlerRecommendResult(mRecommendList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取出错
                LogUtil.d(TAG, "error --- > " + i);
                LogUtil.d(TAG, "errorMsg -- >" + s);
                HandlerError();
            }
        });
    }

    private void HandlerError() {
        //通知UI更新
        if (mCallbacks != null) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback mCallback : mCallbacks) {
                    mCallback.onEmpty();
                }
            } else {
                for (IRecommendViewCallback mCallback : mCallbacks) {
                    mCallback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    /**
     * 获取当前推荐列表
     *
     * @return 使用之前要判空
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }

    private void updateLoading() {
        for (IRecommendViewCallback mCallback : mCallbacks) {
            mCallback.onLoading();
        }
    }

    @Override
    public void pullToRefeeshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
