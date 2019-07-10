package com.example.war.ximalayaradio.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.war.ximalayaradio.data.HimilayaApi;
import com.example.war.ximalayaradio.interfaces.IAlbumDetalPresenter;
import com.example.war.ximalayaradio.interfaces.IAlbumDetalViewCallback;
import com.example.war.ximalayaradio.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetalPresenter implements IAlbumDetalPresenter {
    private static final String TAG = "AlbumDetalPresenter";
    private List<IAlbumDetalViewCallback> mCallbacks = new ArrayList<>();

    private List<Track> mTrackList = new ArrayList<>();
    private Album mTargetAlbum = null;
    //当前专辑ID
    private int mCurrentAlbumId = -1;
    //当前页码
    private int mCurrentPageIndex = -1;

    private AlbumDetalPresenter() {
    }

    private static AlbumDetalPresenter sInstance = null;

    public static AlbumDetalPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetalPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetalPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pullToRefeeshMore() {
        mCurrentPageIndex = 1;
        doLoaded(false);
    }

    @Override
    public void loadMore() {
        //去加载更多内容
        mCurrentPageIndex++;
        doLoaded(true);

    }

    private void doLoaded(final boolean isLoadedMore) {
        HimilayaApi himilayaApi = HimilayaApi.getHimilayaApi();
        himilayaApi.getAlumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks.size --- > " + tracks.size());
                    if (isLoadedMore) {
                        //上啦加载结果放到很后面去
                        mTrackList.addAll(tracks);
                        int size = tracks.size();

                        handlerLoaderMoreResult(size);
                    } else {
                        //下拉加载结果放到前面
                        mTrackList.clear();
                        mTrackList.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTrackList);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoadedMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG, "errorCode--> " + errorCode);
                LogUtil.d(TAG, "errorMsg--> " + errorMsg);
                handlerError(errorCode, errorMsg);
            }
        }, mCurrentAlbumId, mCurrentPageIndex);

    }

    /**
     * 处理加载更多的结果
     *
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetalViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlumDetail(int albumId, int page) {
        mTrackList.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        //根据页码和alum获取专辑
        doLoaded(false);
    }

    /**
     * 网络错误通知UI
     *
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetalViewCallback callback : mCallbacks) {
            callback.onNetWorkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetalViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetalViewCallback detalViewCallback) {
        if (!mCallbacks.contains(detalViewCallback)) {
            mCallbacks.add(detalViewCallback);
            if (mCallbacks != null) {
                detalViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetalViewCallback detalViewCallback) {
        mCallbacks.remove(detalViewCallback);
    }

    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }

    public boolean isPlayCurrentAlbumList() {
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        if (mTrackList!=null) {
            Log.i(TAG, "isPlayCurrentAlbumList: -- > mTrackList.size"+mTrackList.size());
        }else {
            Log.i(TAG, "isPlayCurrentAlbumList: --- > mTrackList null");
        }
        
        return mTrackList.contains(playerPresenter.getCurrentTrack());

    }
}
