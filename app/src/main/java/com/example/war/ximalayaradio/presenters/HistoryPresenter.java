package com.example.war.ximalayaradio.presenters;

import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.data.HistoryDao;
import com.example.war.ximalayaradio.data.IHistoryDaoCallback;
import com.example.war.ximalayaradio.interfaces.IHistoryCallback;
import com.example.war.ximalayaradio.interfaces.IHistoryPresenter;
import com.example.war.ximalayaradio.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private final HistoryDao mHistoryDao;
    private List<IHistoryCallback> mCallback = new ArrayList<>();
    private List<Track> mCurrentHistrackList = null;
    private Track mCurrentTrack = null;

    private HistoryPresenter() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        getListHistories();
    }

    public static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getInstance() {

        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class) {
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void getListHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.getListHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean doDelAsOutSize = false;

    @Override
    public void addHistory(final Track track) {
        //最多一百条，如果超多一百条，就删除前面的。添加新的历史记录
        if (mCurrentHistrackList != null && mCurrentHistrackList.size() >= Constants.MAX_HIS_COUNT) {
            //先删除，最前的一条再添加
            doDelAsOutSize = true;
            this.mCurrentTrack = track;
            delHistory(mCurrentHistrackList.get(mCurrentHistrackList.size() - 1));
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistoryTrack(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistoryTrack(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.cleanHistoryTrack();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //添加Ui注册的回调
        if (!mCallback.contains(iHistoryCallback)) {
            mCallback.add(iHistoryCallback);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryCallback iHistoryCallback) {
        //删除Ui注册的回调
        if (mCallback.contains(iHistoryCallback)) {
            mCallback.remove(iHistoryCallback);
        }
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        getListHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if (doDelAsOutSize && mCurrentTrack != null) {
            doDelAsOutSize = false;
            addHistory(mCurrentTrack);
        } else {
            getListHistories();
        }
    }

    @Override
    public void onClearHistory() {
        getListHistories();
    }

    @Override
    public void onHistoryLoaded(final List<Track> list) {
        this.mCurrentHistrackList = list;
        //通知Ui更新数据 切换到UI 线程
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    for (IHistoryCallback iHistoryCallback : mCallback) {
                        iHistoryCallback.onHistoriesLoaded(list);
                    }
                }
            }
        });

    }
}
