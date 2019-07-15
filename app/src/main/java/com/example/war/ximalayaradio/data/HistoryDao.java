package com.example.war.ximalayaradio.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final HimalayaDHHelper mDbHelper;
    private IHistoryDaoCallback mCallback = null;
    private Object mLock = new Object();

    public HistoryDao() {
        mDbHelper = new HimalayaDHHelper(BaseApplication.getAppContext());

    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistoryTrack(Track track) {
        synchronized (mLock) {


            SQLiteDatabase db = null;
            boolean isSUccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();

                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?"
                        , new String[]{track.getDataId() + ""});

                ContentValues valuses = new ContentValues();
                //封装数据
                valuses.put(Constants.HISTORY_TITLE, track.getTrackTitle());
                valuses.put(Constants.HISTORY_PLAY_COUNT, track.getPlayCount());
                valuses.put(Constants.HISTORY_DRUATION, track.getDuration());
                valuses.put(Constants.HISTORY_TRACK_ID, track.getDataId());
                valuses.put(Constants.HISTORY_UPDATE_TIME, track.getUpdatedAt());
                valuses.put(Constants.HISTORY_COVER, track.getCoverUrlMiddle());


                //插入数据
                db.insert(Constants.HISTORY_TB_NAME, null, valuses);


                db.setTransactionSuccessful();
                isSUccess = true;
            } catch (Exception e) {
                isSUccess = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    //db.close();
                }
                mCallback.onHistoryAdd(isSUccess);
            }
        }
    }

    @Override
    public void delHistoryTrack(Track track) {
        synchronized (mLock) {

            SQLiteDatabase db = null;
            boolean isSUccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();

                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?"
                        , new String[]{track.getDataId() + ""});
                Log.i(TAG, "delHistoryTrack: -- > delete " + db);

                db.setTransactionSuccessful();
                isSUccess = true;
            } catch (Exception e) {
                isSUccess = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    //db.close();
                }
                mCallback.onHistoryDel(isSUccess);
            }
        }

    }

    @Override
    public void cleanHistoryTrack() {
        synchronized (mLock) {

            SQLiteDatabase db = null;
            boolean isSUccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();

                int delete = db.delete(Constants.HISTORY_TB_NAME, null, null);
                Log.i(TAG, "delHistoryTrack: -- > delete " + db);

                db.setTransactionSuccessful();
                isSUccess = true;

            } catch (Exception e) {
                isSUccess = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    //db.close();
                }
                mCallback.onClearHistory();

            }
        }

    }

    @Override
    public void getListHistory() {
        synchronized (mLock) {

            SQLiteDatabase db = null;
            List<Track> historyList = new ArrayList<>();
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, null);

                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String trackTitle = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(trackTitle);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int dration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DRUATION));
                    track.setDuration(dration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String corver = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlMiddle(corver);

                    historyList.add(track);
                }


                db.setTransactionSuccessful();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (db != null) {
                    db.endTransaction();
                    //db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryLoaded(historyList);
                }
            }
        }
    }
}
