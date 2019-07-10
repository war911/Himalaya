package com.example.war.ximalayaradio.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao {
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private final HimalayaDHHelper mHimalayaDHHelper;
    private static final String TAG = "SubscriptionDao";
    private ISubDaoCallback mCallback = null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mHimalayaDHHelper = new HimalayaDHHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isAssSuccess = false;
        try {
            db = mHimalayaDHHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            //封装数据
            contentValues.put(Constants.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACK_COUNT,album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID,album.getId());

            //插入数据


            db.insert(Constants.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isAssSuccess = true;


        }catch (Exception e){
            if (mCallback != null) {
                isAssSuccess = false;
            }
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                //db.close();
            }
            if (mCallback != null) {
                mCallback.onAddResult(isAssSuccess,album);
            }
        }



    }

    @Override
    public void deAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDelSuccess = false;
        try {
            db = mHimalayaDHHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});

            Log.i(TAG, "deAlbum: --- > 删除的个数"+delete);
            db.setTransactionSuccessful();
            isDelSuccess = true;

        }catch (Exception e){
            if (mCallback != null) {
                isDelSuccess = false;
            }
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                //db.close();
            }

            if (mCallback != null) {
                mCallback.onDelReslt(isDelSuccess,album);
            }
        }
    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mHimalayaDHHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_TB_NAME, null, null, null, null, null, "_id desc");
            while (query.moveToNext()) {
                Album album = new Album();
                //图片封面
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);

                String titie = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                Log.i(TAG, "listAlbums: --- > war title -- > "+titie);
                album.setAlbumTitle(titie);

                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);



                long albumId = query.getLong(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(albumId);

                long playCount = query.getLong(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);

                long trackCount = query.getLong(query.getColumnIndex(Constants.SUB_TRACK_COUNT));
                album.setIncludeTrackCount(trackCount);

                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);

                album.setAnnouncer(announcer);

                result.add(album);
            }


            query.close();
            db.setTransactionSuccessful();



        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                //db.close();
            }
            if (mCallback != null) {
                mCallback.onSubListLoaded(result);
            }
        }
    }
}
