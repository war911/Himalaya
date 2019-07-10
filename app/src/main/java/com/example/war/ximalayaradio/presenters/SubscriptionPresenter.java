package com.example.war.ximalayaradio.presenters;

import android.util.Log;

import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.data.ISubDaoCallback;
import com.example.war.ximalayaradio.data.SubscriptionDao;
import com.example.war.ximalayaradio.interfaces.ISubscriptionCallback;
import com.example.war.ximalayaradio.interfaces.ISubscriptionPresenter;
import com.example.war.ximalayaradio.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.Callback;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private static final String TAG = "SubscriptionPresenter";
    private final SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);

    }

    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }

            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private static SubscriptionPresenter sInstance = null;

    public static SubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                sInstance = new SubscriptionPresenter();
            }
        }

        return sInstance;
    }

    @Override
    public void addSubscription(final Album album) {
        //判断当前的订阅数量，不能超过100个
        if (mData.size() >= Constants.MAX_SUB_COUNT) {
            //给出提示

            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubFull();
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //
                if (mSubscriptionDao != null) {
                    Log.i(TAG, "subscribe: --- > war album title "+album.getAlbumTitle());
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.deAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());

        Log.i(TAG, "isSub:  --- > "+mData);
        //不为空表示已经订阅
        return result != null;

    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unregisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.remove(iSubscriptionCallback);
        }

    }

    @Override
    public void onAddResult(final boolean isSuccess, final Album album) {
        listSubscriptions();
        //添加结果的回调

        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
//                if (isSuccess) {
//                    mData.put(album.getId(), album);
//                }
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelReslt(final boolean isSuccess, final Album album) {
        listSubscriptions();
        //删除订阅的回调

        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
//                if (isSuccess) {
//                    mData.remove(album.getId());
//                }
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        mData.clear();
        for (Album album : result) {
            Log.i(TAG, "  war onSubListLoaded: --- > id"+album.getId() + "-- >名称"+album.getAlbumTitle());

        }

        //加载回调
        for (Album album : result) {
            mData.put(album.getId(), album);
        }

        Set<Long> longs = mData.keySet();
        for (Long aLong : longs) {
            Log.i(TAG, "onSubListLoaded: --- 名字 "+mData.get(aLong));

        }
        //通知UI更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
