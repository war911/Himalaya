package com.example.war.ximalayaradio.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.war.ximalayaradio.data.HimilayaApi;
import com.example.war.ximalayaradio.interfaces.ISearchCallBack;
import com.example.war.ximalayaradio.interfaces.ISearchPresenter;
import com.example.war.ximalayaradio.utils.Constants;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private HimilayaApi mHimilayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurentPage = 1;

    private List<Album> mSearChResult = new ArrayList<>();

    private SearchPresenter() {
        mHimilayaApi = HimilayaApi.getHimilayaApi();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallBack> mCallBacks = new ArrayList<>();

    @Override
    public void doSearch(String keyword) {
        mCurentPage = DEFAULT_PAGE;
        mSearChResult.clear();
        //用于搜索
        //网络不好时用户会点击重新搜索
        this.mCurrentKeyword = keyword;

        search(keyword);
    }

    private void search(String keyword) {
        mHimilayaApi.doSearchByKeyWord(keyword, mCurentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearChResult.addAll(albums);
                if (albums != null) {
                    Log.i(TAG, "onSuccess: albums size---- >" + albums.size());
                    if (mIsloadmore) {
                        for (ISearchCallBack callBack : mCallBacks) {

                            callBack.onLoadMoreResult(mSearChResult, albums.size() != 0);


                        }
                        mIsloadmore = false;
                    } else {
                        for (ISearchCallBack callBack : mCallBacks) {
                            callBack.onSearchResultLoaded(mSearChResult);
                        }
                    }
                } else {
                    Log.i(TAG, "onSuccess: albums null");

                }

            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.i(TAG, "onError:  -- > errorCode-- " + errorMsg
                        + "errorMsg -- " + errorMsg);

                for (ISearchCallBack callBack : mCallBacks) {
                    if (mIsloadmore) {
                        callBack.onLoadMoreResult(mSearChResult, false);
                        mCurentPage--;
                        mIsloadmore = false;
                    } else {
                        callBack.onError(errorCode, errorMsg);
                    }


                }
            }
        });
    }


    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    private boolean mIsloadmore = false;

    @Override
    public void loadMore() {
        //判断有没有比较加载更多
        if (mSearChResult.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallBack callBack : mCallBacks) {
                callBack.onLoadMoreResult(mSearChResult, false);
            }
        } else {
            mIsloadmore = true;
            mCurentPage++;
            search(mCurrentKeyword);

        }

    }

    @Override
    public void getHotWord() {
        //todo 做一个热词缓存


        mHimilayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWordLists = hotWordList.getHotWordList();
                    Log.i(TAG, "getHotWord onSuccess: -- > hotWordList1 size" + hotWordLists.size());
                    for (ISearchCallBack callBack : mCallBacks) {
                        callBack.onHotWordLoaded(hotWordLists);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.i(TAG, "getHotWord onError: -- >errorCode " + errorCode +
                        "-- > errorMsg-- >" + errorMsg);

            }
        });
    }

    @Override
    public void getRecommedKeyWord(String keyWord) {
        mHimilayaApi.getSuggestWord(keyWord, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    Log.i(TAG, "onSuccess: -- > suggestList size" + keyWordList.size());

                    if (mCallBacks != null) {
                        for (ISearchCallBack callBack : mCallBacks) {
                            callBack.onRecommendWordLoaded(keyWordList);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.i(TAG, "getRecommedKeyWord onError: -- >errorCode " + errorCode +
                        "-- > errorMsg-- >" + errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallBack iSearchCallBack) {
        if (!mCallBacks.contains(iSearchCallBack)) {
            mCallBacks.add(iSearchCallBack);
        }
    }

    @Override
    public void unregisterViewCallback(ISearchCallBack iSearchCallBack) {
        if (mCallBacks.contains(iSearchCallBack)) {
            mCallBacks.remove(iSearchCallBack);
        }
    }
}
