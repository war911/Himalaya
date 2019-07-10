package com.example.war.ximalayaradio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.war.ximalayaradio.utils.Constants;

public class HimalayaDHHelper extends SQLiteOpenHelper {

    private static final String TAG = "HimalayaDHHelper";

    public HimalayaDHHelper(Context context) {
        //name 数据库名字，factory游标工厂，version版本号
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: -- > ");
        //创建数据表
        //订阅相关的字段
        //图片，title，描述，播放量,作者名称(详情界面)，专辑id
        String subTbSql = "create table " + Constants.SUB_TB_NAME + "(" +
                Constants.SUB_ID + " integer primary key autoincrement, " +
                Constants.SUB_COVER_URL + " varchar, " +
                Constants.SUB_TITLE + " varchar, " +
                Constants.SUB_DESCRIPTION + " varchar, " +
                Constants.SUB_PLAY_COUNT + " integer, " +
                Constants.SUB_TRACK_COUNT + " integer, " +
                Constants.SUB_AUTHOR_NAME + " varchar, " +
                Constants.SUB_ALBUM_ID + " integer" +
                ")";

        db.execSQL(subTbSql);

        //创建历史记录表
        String histoTbsql = "create table " + Constants.HISTORY_TB_NAME + "(" +
                Constants.HISTORY_ID + " integer primary key autoincrement, " +
                Constants.HISTORY_TRACK_ID + " integer, " +
                Constants.HISTORY_TITLE + " varchar, " +
                Constants.HISTORY_COVER + " integer, " +
                Constants.HISTORY_PLAY_COUNT + " varchar, " +
                Constants.HISTORY_DRUATION + " varchar, " +
                Constants.HISTORY_UPDATE_TIME + " integer" +
                ")";

        db.execSQL(histoTbsql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
