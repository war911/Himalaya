package com.example.war.ximalayaradio.data;

import com.example.war.ximalayaradio.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class HimilayaApi {

    private HimilayaApi() {

    }

    public static HimilayaApi sHimilayaApi;

    public static HimilayaApi getHimilayaApi() {

        if (sHimilayaApi == null) {
            synchronized (HimilayaApi.class) {
                if (sHimilayaApi == null) {
                    sHimilayaApi = new HimilayaApi();
                }
            }
        }
        return sHimilayaApi;
    }

    /**
     * 获取推荐内容
     *
     * @param callBack 请求结果回调
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMAND + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }


    /**
     * 根据专辑的ID获取到专辑的内容
     *
     * @param callBack 专辑详情的回调
     * @param album    专辑的ID
     * @param page     页码
     */
    public void getAlumDetail(IDataCallBack<TrackList> callBack, long album,
                              int page) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, album + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }


    /**
     * 根据关键字，进行搜索
     * @param keyword
     */
    public void doSearchByKeyWord(String keyword, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取推荐的热词
     * @param callBack
     */
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(Constants.COUNT_HOT_WORDS));
        CommonRequest.getHotWords(map, callBack);
    }

    /**
     * 根据关键字获取联想词
     * @param keyWord 关键字
     * @param callback 回调
     */
    public void getSuggestWord(String keyWord, IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        CommonRequest.getSuggestWord(map, callback);
    }
}
