package com.example.war.ximalayaradio.utils;

public class Constants {
    //获取推荐列表的专辑数量
    public static int COUNT_RECOMMAND = 50;

    //默认请求数量
    public static int COUNT_DEFAULT = 50;

    //热词的数量
    public static int COUNT_HOT_WORDS = 10;

    //数据库相关的常量
    public static final String DB_NAME = "himalaya.db";

    //数据库版本号
    public static final int DB_VERSION_CODE = 1;

    //订阅的表名
    public static final String SUB_TB_NAME = "tb_subscription";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "coverUrl";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_PLAY_COUNT = "playCount";
    public static final String SUB_TRACK_COUNT = "trackCount";
    public static final String SUB_AUTHOR_NAME = "authorName";
    public static final String SUB_ALBUM_ID = "albumId";

    //历史记录表名
    public static final String HISTORY_TB_NAME = "tb_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TITLE = "history_title";
    public static final String HISTORY_PLAY_COUNT = "history_playCount";
    public static final String HISTORY_DRUATION= "history_druation";
    public static final String HISTORY_TRACK_ID = "trackId";
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";
    public static final String HISTORY_COVER = "historyCover";

    //订阅最多个数
    public static final int MAX_SUB_COUNT = 100;
    //最大历史记录数
    public static final int MAX_HIS_COUNT = 100;


}
